package com.example.autobartender.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.example.autobartender.R;
import com.example.autobartender.utils.networking.HTTPGETBase.RequestStatus;
import com.example.autobartender.utils.networking.HTTPGETJSONObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;

public class RecipeManager {
    private static final String TAG = RecipeManager.class.getSimpleName();


    public enum RecipeSortOrder {
        RECENT,  // recipes in order by recency
        ALC_CONTENT_HL,  // highest to lowest alc %
        ALC_CONTENT_LH,  // lowest to highest alc %
        ALPHABETICAL,  // recipes in alphabetical order
        DEFAULT,  // whatever order theyre in the list in
    }

    /**
     * builds list of locale-appropriate sort-order names from string resources.
     * builds lookup key using a format from Constants "SORT_ORDER_%s" where %s is the sort_order ID
     * @param ctx to access string resources
     * @return list of locale-appropriate names
     */
    public static String[] getSortOrderNames(Context ctx) {
        String[] values = new String[RecipeSortOrder.values().length];

        for (int i = 0; i < values.length; i++) {
            String lookup_id = String.format(Constants.SORT_ORDER_LOOKUP_KEY, RecipeSortOrder.values()[i]);
            try {
                values[i] = ctx.getString(ctx.getResources().getIdentifier(lookup_id, "string", ctx.getPackageName()));
            } catch (Resources.NotFoundException e) {
                Log.d(TAG, "getSortOrderNames: fuckup. these dont change this shouldnt happen: " + e.getLocalizedMessage());
                values[i] = ctx.getString(R.string.SORT_ORDER_DEFAULT);
            }
        }
        return values;
    }


    private static class RecipeSorter implements Comparator<Recipe> {

        @Override
        public int compare(Recipe o1, Recipe o2) {
            //giant switch by sort order
            switch (selectedSortOrder) {
                case ALPHABETICAL:
                    return o1.getName().compareTo(o2.getName());
                case RECENT:
                    //TODO implement some kinda recency check idek
                    Log.d(TAG, "compare: RECENT sortOrder not implemented");
                    // dont break so it just carries down to alc content
                case ALC_CONTENT_HL:  // default int sorting is low-hi so negative to flip it
                    return -Integer.compare(o1.getAlcPct(), o2.getAlcPct());
                case ALC_CONTENT_LH:
                    return Integer.compare(o1.getAlcPct(), o2.getAlcPct());
                case DEFAULT:
                default:
                    // do nothing
                    return 0;
            }
        }
    }


    enum RecipeType {
        UNMODIFIED, // Indicates this is an unmodified recipe from the database
        MODIFIED, // Indicates this is a database recipe with modified quantities
        CUSTOM // Indicates custom recipe
    }


    /**
     * basically a struct that represents an ingredient in a recipe.
     * has an ID and a quantity.
     * Separate reference DB has locale specific names, alc%, etc.
     */
    public static class RecipeIngredient {
        private final ReferenceIngredient ID;
        private final int quantity_ml;
        private final int index;

        public RecipeIngredient(String id, int quant, int ind) {
            this.ID = getReferenceIngredient(id);
            this.quantity_ml = quant;
            this.index = ind;
        }

        public ReferenceIngredient getReference() {
            return this.ID;
        }

        public String getID() {
            return this.ID.ID;
        }

        public int getQuantity_ml() {
            return this.quantity_ml;
        }

        public int getIndex() {
            return this.index;
        }
    }


    public static class Recipe implements Iterable<RecipeIngredient> {
        private String id;
        private String name;
        private String description;
        private String imgFileName;
        RecipeType type;
        private ArrayList<RecipeIngredient> ingredients;

        public Recipe(JSONObject recipeObj) {
            try {
                this.id = recipeObj.getString(Constants.ID);
                this.name = recipeObj.getString(Constants.NAME);
                this.description = recipeObj.getString(Constants.DESCRIPTION);
                this.type = RecipeType.valueOf(RecipeType.class, recipeObj.getString(Constants.TYPE));

                if (recipeObj.has(Constants.IMAGE_FILENAME))
                    this.imgFileName = recipeObj.getString(Constants.IMAGE_FILENAME);
                else
                    this.imgFileName = null;

                this.ingredients = new ArrayList<RecipeIngredient>();
                for (int i = 0; i < recipeObj.getJSONArray(Constants.INGREDIENTS).length(); i++) {
                    JSONObject ingObj = recipeObj.getJSONArray(Constants.INGREDIENTS).getJSONObject(i);
                    RecipeIngredient ing = new RecipeIngredient(
                            ingObj.getString(Constants.ID),
                            ingObj.getInt(Constants.QUANTITY),
                            ingObj.getInt(Constants.INDEX)
                    );
                    this.ingredients.add(ing);
                }
            } catch (JSONException e) {
                Log.d(TAG, "Recipe: bad JSON recipe: " + e.getLocalizedMessage() + ". json= "+ recipeObj);
            }
        }


        public String getID() {
            return this.id;
        }

        public String getName() {
            return this.name;
        }

        public String getDescription() {
            return this.description;
        }

        public String getImgFileName() {
            return this.imgFileName;
        }

        public RecipeType getType() {
            return this.type;
        }

        /**
         * calculates alcohol content
         * @return as an integer percentage
         */
        public int getAlcPct() {
            int totalVolumeMl = 0;
            float volumeAlcMl = 0;
            for (RecipeIngredient ing: this) {
                totalVolumeMl += ing.quantity_ml;
                volumeAlcMl += ing.quantity_ml * ing.getReference().alcPct;
            }

            return Math.round(volumeAlcMl / totalVolumeMl);
        }


        @NonNull
        @Override
        public Iterator<RecipeIngredient> iterator() {
            return this.ingredients.iterator();
        }
    }


    /**
     * Used to keep track of reference data for known ingredients.
     * So this data which doesnt change isnt repeated for each recipe
     */
    public static class ReferenceIngredient {
        public final String ID;
        public final int alcPct;

        public ReferenceIngredient(String ID, int alcPct) {
            this.ID = ID;
            this.alcPct = alcPct;
        }

        public String toString() {
            return this.ID + ": " + this.alcPct + "%";
        }
    }


    // Recipe Database variables
    private static final ArrayList<Recipe> recipes;
    public static final ArrayList<ReferenceIngredient> ingredientReference;
    private static RecipeSortOrder selectedSortOrder;
    private static HTTPGETJSONObject requestThread;


    // Initializer
    static {
        recipes = new ArrayList<Recipe>();
        ingredientReference = new ArrayList<ReferenceIngredient>();
        selectedSortOrder = RecipeSortOrder.DEFAULT;
        requestThread = null;
    }


    /**
     * request load recipes and ingredients from remote database
     * two source options are file server,
     * or the local testing server. See Constants
     */
    public static void loadRemote() {
        switch (PrefsManager.getRecipeDBSource()) {
            case Constants.RECIPE_DB_KEY_FTP:
                Log.d(TAG, "loadRemoteRecipes: attempting to load from file server. This is not implemented");
                break;
            case Constants.RECIPE_DB_KEY_HARDCODED:
                Log.d(TAG, "loadRemoteRecipes: loading recipes from hardcoded server");
                try {
                    MutableLiveData<RequestStatus> requestStatus = new MutableLiveData<RequestStatus>();
                    requestStatus.observeForever(new Observer<RequestStatus>() {
                        @Override
                        public void onChanged(RequestStatus s) { onRemoteLoad(s); }
                    });

                    requestThread = new HTTPGETJSONObject(new URL(Constants.URL_RECIPE_DB_HARDCODED), requestStatus);
                    requestThread.start();
                } catch (MalformedURLException e) {
                    Log.d(TAG, "loadRemoteRecipes: Constants.URL_RECIPE_DB_HARDCODED invalid URL: " + e.getLocalizedMessage());
                }
        }
    }

    /**
     * request recipe load from local storage -
     */
    public static void loadLocalRecipes(SharedPreferences prefs) {
        if (!prefs.contains(Constants.LOCAL_RECIPE_DB_SHAREDPREFS)) {
            Log.d(TAG, "loadLocalRecipes: no local recipes stored");
            return;
        }


        try {
            JSONArray rawRecipeArr = new JSONArray(prefs.getString(Constants.LOCAL_RECIPE_DB_SHAREDPREFS, null));

            // Now that we know we are loading new recipes, we can remove old recipes from local storage (if necessary)
            recipes.removeIf(recipe -> recipe.getType() != RecipeType.UNMODIFIED);

            for (int i = 0; i < rawRecipeArr.length(); i++) {
                JSONObject recipeObj = rawRecipeArr.getJSONObject(i);
                recipes.add(new Recipe(recipeObj));
            }
            Log.d(TAG, "loadLocalRecipes: loaded " + recipes.size() + " custom recipes from sharedprefs");
        } catch (JSONException e) {
            Log.d(TAG, "loadLocalRecipes: recipe Array stored in sharedprefs could not be loaded, json error: " + e.getLocalizedMessage());
        }
    }


    private static void onRemoteLoad(RequestStatus s) {
        if (s != RequestStatus.DONE_SUCCESS) {
            Log.d(TAG, "onRecipeLoad: request status is fail. oops");
            nullThread();
            return;
        }

        Log.d(TAG, "onRecipeLoad: successful request. have to load them now");

        JSONArray recipeArr;
        JSONArray ingredientArr;
        try {
            // load ingredients from DB
            ingredientArr = requestThread.getJsonObject().getJSONArray(Constants.INGREDIENTS);
            ingredientReference.clear();
            for (int i = 0; i < ingredientArr.length(); i++) {
                JSONObject ingredientObj = ingredientArr.getJSONObject(i);
                ReferenceIngredient newIng = new ReferenceIngredient(
                        ingredientObj.getString(Constants.ID),
                        ingredientObj.getInt(Constants.ALC_PCT)
                );
                ingredientReference.add(newIng);
            }
            Log.d(TAG, "onRemoteLoad: loaded " + ingredientReference.size() + " ingredients");
            for (ReferenceIngredient ing: ingredientReference)
                Log.d(TAG, "onRemoteLoad: " + ing);

            // load recipes from DB
            recipeArr = requestThread.getJsonObject().getJSONArray(Constants.RECIPES);
            for (int i = 0; i < recipeArr.length(); i++) {
                Recipe newRecipe = new Recipe(recipeArr.getJSONObject(i));
                Recipe oldRecipe = getRecipe(newRecipe.getID());  // determine if recipe is already in the database. If so, replace it with new copy.
                if (oldRecipe != null)
                    recipes.remove(oldRecipe);
                recipes.add(newRecipe);
            }
        } catch (JSONException e) {
            Log.d(TAG, "onRecipeLoad: bad json: " + e.getLocalizedMessage());
        }
        finally {
            nullThread();
        }
    }

    /**
     * joins thread then sets it to null. hopefully its finished already
     */
    private static void nullThread() {
        if (requestThread == null)
            return;

        try {
            Log.d(TAG, "nullThread: calling thread.join, thread should already be done");
            requestThread.join();
        } catch (InterruptedException e) {
            Log.d(TAG, "nullThread: " + e.getLocalizedMessage());
        } finally {
            requestThread = null;
        }
    }


    public static void setSortOrder(RecipeSortOrder order) {
        selectedSortOrder = order;
        recipes.sort(new RecipeSorter());  // sorts based on selectedSortOrder
    }



    /**
     * calculates the nuymber of recipes found in the DB
     * @return int num of recipes
     */
    public static int getNumRecipes() {
        return recipes.size();
    }


    public static ReferenceIngredient getReferenceIngredient(String ingID) {
        for (ReferenceIngredient ing: ingredientReference)
            if (ing.ID.equals(ingID))
                return ing;
        return new ReferenceIngredient(Constants.UNKNOWN, 0);
    }

    /**
     * Lookup a recipe by ID. will only return valid recipes
     * @param recipeID the id of the recipe to find
     * @return recipe obj
     */
    public static Recipe getRecipe(String recipeID) {
        for (Recipe recipe: recipes)
            if (recipe.getID().equals(recipeID))
                return recipe;
        return null;
    }


    /**
     * Gets recipe by index in the list.
     * SortOrder should be set before this is ever called, so that the list is sorted to user preference
     * @param index index in the list
     * @return recipe object
     */
    public static Recipe getRecipe(int index) {
        return recipes.get(index);
    }


    public static String makeIngredientList(Recipe recipe, String rowFormat, Context ctx) {
        StringBuilder sb = new StringBuilder();
        for (RecipeIngredient ing: recipe) {
            sb.append(String.format(rowFormat, ing.quantity_ml, getIngredientName(ing.getReference(), ctx)));
            //TODO unit conversion as appropriate between mL, oz, shots.
            //TODO id -> name lookup
        }

        String ret = sb.toString();
        return ret.trim();
    }


    /**
     * Finds locale-appropriate ingredient name from string resources.
     * builds lookup key using a format from Constants "ing_name_%s" where %s is the ingredient ID
     * @param ing a reference ingredient (ID)
     * @param ctx to access string resources
     * @return locale-appropriate ingredient name
     */
    public static String getIngredientName(ReferenceIngredient ing, Context ctx) {
        String lookup_id = String.format(Constants.ING_NAME_LOOKUP_KEY, ing.ID);
        try {
            return ctx.getString(ctx.getResources().getIdentifier(lookup_id, "string", ctx.getPackageName()));
        } catch (Resources.NotFoundException e) {
            Log.d(TAG, "getIngredientName: ingredient " + ing.ID + " unknown: " + e.getLocalizedMessage());
            return ctx.getString(R.string.ING_NAME_UNKNOWN) + ": " + ing.ID;
        }
    }


    public static JSONObject jsonifyRecipe(Recipe recipe) {
        try {
            JSONObject obj = new JSONObject();
            obj.put(Constants.ID, recipe.getID());
            obj.put(Constants.NAME, recipe.getName());
            obj.put(Constants.DESCRIPTION, recipe.getDescription());
            obj.put(Constants.TYPE, recipe.getType().toString());

            ArrayList<JSONObject> arr = new ArrayList<JSONObject>();
            for (RecipeIngredient ing: recipe) {
                JSONObject ingObj = new JSONObject();
                ingObj.put(Constants.ID, ing.ID);
                ingObj.put(Constants.QUANTITY, ing.quantity_ml);
                ingObj.put(Constants.INDEX, ing.index);
                arr.add(ingObj);
            }
            obj.put(Constants.INGREDIENTS, new JSONArray(arr));

            return obj;

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

}
