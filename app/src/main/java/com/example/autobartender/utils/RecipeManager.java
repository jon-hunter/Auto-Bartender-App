package com.example.autobartender.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.example.autobartender.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.UUID;

public class RecipeManager {
    private static final String TAG = "RecipeManager";  // For log.


    public enum RecipeSortOrder {
        DEFAULT, RECENT
    }


    enum RecipeType {
        UNMODIFIED, // Indicates this is an unmodified recipe from the database
        MODIFIED, // Indicates this is a database recipe with modified quantities
        CUSTOM // Indicates custom recipe
    }
    public static RecipeType getRecipeType(String type) {
        switch (type) {
            case Constants.UNMODIFIED:
                return RecipeType.UNMODIFIED;
            case Constants.MODIFIED:
                return RecipeType.MODIFIED;
            case Constants.CUSTOM:
            default:
                return RecipeType.CUSTOM;
        }
    }


    public static class Ingredient {
        public final String id;
        public final int quantity_ml;
        public final int index;

        public Ingredient(String id, int quant, int ind) {
            this.id = id;
            this.quantity_ml = quant;
            this.index = ind;
        }
    }


    public static class Recipe implements Iterable<Ingredient> {
        private boolean valid;  // Whether the JSON parsed correctly
        private ArrayList<Ingredient> ingredients;
        private String name;
        private String description;
        private String id;
        RecipeType type;
        //TODO image attribute

        public Recipe(JSONObject recipe) {
            try {
                this.name = recipe.getString(Constants.NAME);
                this.description = recipe.getString(Constants.DESCRIPTION);
                this.id = recipe.getString(Constants.ID);
                this.type = getRecipeType(recipe.getString(Constants.TYPE));

                this.ingredients = new ArrayList<Ingredient>();
                for (int i = 0; i < recipe.getJSONArray(Constants.INGREDIENTS).length(); i++) {
                    JSONObject ingObj = recipe.getJSONArray(Constants.INGREDIENTS).getJSONObject(i);
                    Ingredient ing = new Ingredient(
                            ingObj.getString(Constants.ID),
                            ingObj.getInt(Constants.QUANTITY),
                            ingObj.getInt(Constants.INDEX)
                    );
                    this.ingredients.add(ing);
                }
                this.valid = true;
            } catch (JSONException e) {
                Log.d(TAG, "Recipe: bad JSON recipe: " + e.getLocalizedMessage() + ". json= "+ recipe);
                this.valid = false;
            }
        }


        public boolean isValid() {
            return this.valid;
        }

        public String getName() {
            return this.name;
        }

        public String getDescription() {
            return description;
        }

        public String getID() {
            return this.id;
        }

        public RecipeType getType() {
            return this.type;
        }

        public Ingredient getIngredient(int index) {
            return this.ingredients.get(index);
        }

        public int getNumIngredients() {
            return this.ingredients.size();
        }


        @NonNull
        @Override
        public Iterator<Ingredient> iterator() {
            return this.ingredients.iterator();
        }
    }


    // Recipe Database variables
    private static ArrayList<Recipe> recipeDB;
    public static RecipeSortOrder selectedSortOrder;
    private static boolean dbInitialized;


    // Initializer
    static {
        recipeDB = new ArrayList<Recipe>();
        selectedSortOrder = RecipeSortOrder.DEFAULT;
        dbInitialized = false;
    }


    /**
     * request recipe load from remote database - file server
     */
    public static void loadRemoteRecipes() {
        //TODO implement CORRECTLY - THIS is a temporary workaround
        try {
            MutableLiveData<String> returnData = new MutableLiveData<String>();
            returnData.observeForever(new Observer<String>() {
                @Override
                public void onChanged(String s) {
                    onRecipeLoad(s);
                }
            });

            NetworkGETRequest request = new NetworkGETRequest(
                    new URL("http://10.0.2.2:8000/RECIPE_DB.JSON"),
                    returnData
            );
            request.start();
        } catch (MalformedURLException ignored) { }

        //TODO observe and catch the result when it comes out the other end

        // Go through all the recipes, check Image files and request them

    }

    /**
     * request recipe load from local storage -
     */
    public static void loadLocalRecipes(SharedPreferences prefs) {
        //TODO implement
    }


    public static void onRecipeLoad(String rawArray) {
        JSONArray recipeArr;
        try {
            recipeArr = new JSONArray(rawArray);
            for (int i = 0; i < recipeArr.length(); i++) {
                recipeDB.add(new Recipe(recipeArr.getJSONObject(i)));
            }
            dbInitialized = true;

        } catch (JSONException e) {
            Log.d(TAG, "onRecipeLoad: bad json: " + e.getLocalizedMessage());
        }
    }

//    /**
//     * loads the recipe DB and saves it as a variable.
//     */
//    public void loadRecipeDb() {
//        Log.d(TAG, "loadRecipeDb: loading recipe DB");
//        try {
//            // Do file input stream stuff
//            InputStream is = getResources().openRawResource(R.raw.recipe_db);
//            BufferedReader br = new BufferedReader(new InputStreamReader(is));
//            String line = br.readLine();
//            StringBuilder json_str = new StringBuilder();
//            while (line != null) {
//                json_str.append(line);
//                line = br.readLine();
//            }
//
//            // Read the JSON
//            vm.recipeDB = new JSONArray(json_str.toString());
//        }
//        catch (IOException e) {
//            Log.d(TAG, "loadRecipeDb: IOexception oops");
//            e.printStackTrace();
//        } catch (JSONException e) {
//            e.printStackTrace();
//            Log.d(TAG, "loadRecipeDb: Bro the fuckin db threw a json error. its literally not a file that ever changes so you should check that");
//        }
//    }

    public static boolean isDbInitialized() {
        return dbInitialized;
    }


    /**
     * calculates the nuymber of recipes found in the DB
     * @return int num of recipes
     */
    public static int getNumRecipes() {
        if (recipeDB == null) {
            Log.d(TAG, "getNumRecipes: recipeDB is null, returning 0");
            return 0;
        }

        Log.d(TAG, "getNumRecipes: recipeDB has recipes: " + recipeDB.size());
        return recipeDB.size();
    }


    /**
     * Lookup a recipe by ID. will only return valid recipes
     * @param recipeID the id of the recipe to find
     * @return recipe obj
     */
    public static Recipe getRecipe(String recipeID) {
        for (Recipe recipe: recipeDB) {
            if (recipe.isValid() && recipe.getID().equals(recipeID))
                return recipe;
        }
        return null;
    }

    public static Recipe getRecipe(int index) {
        //TODO implement more sortorders. just going to use default for now
        switch (selectedSortOrder) {
            case DEFAULT:
            default:
                return recipeDB.get(index);
        }
    }


    @Deprecated
    public static Recipe getSelectedRecipe() {
        //TODO remove
        Random r = new Random();
        return getRecipe(r.nextInt(getNumRecipes()));
    }


    /**
     * takes recipe and builds a post request body. request contains UUID, timestamp, recipe. in JSON format
     *
     * @param recipe the recipe to make into a request
     * @param userID ID of user making request
     * @return JSON object
     */
    public static String createDrinkRequestBody(Recipe recipe, String userID) {
        JSONObject requestBody = new JSONObject();

        try {
            requestBody.put(Constants.DRINK_ID, UUID.randomUUID());
            requestBody.put(Constants.USER_ID, userID);
            requestBody.put(Constants.TIMESTAMP, Constants.getCurrentTimestamp());
            requestBody.put(Constants.DESCRIPTION, recipe.getDescription());

            ArrayList<JSONObject> ingrs = new ArrayList<JSONObject>();
            for (Ingredient ing: recipe) {
                JSONObject ingObj = new JSONObject();
                ingObj.put(Constants.ID, ing.id);
                ingObj.put(Constants.QUANTITY, ing.quantity_ml);
                ingObj.put(Constants.INDEX, ing.index);
                ingrs.add(ingObj);
            }
            requestBody.put(Constants.INGREDIENTS, new JSONArray(ingrs));

            return requestBody.toString(2);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d(TAG, "createDrinkRequest: failed to build drink request body");
        }

        return null;
    }


    public static String makeIngredientList(Recipe recipe, String rowFormat) {
        StringBuilder sb = new StringBuilder();
        for (Ingredient ing: recipe) {
            sb.append(String.format(rowFormat, ing.quantity_ml, ing.id));
            //TODO unit conversion as sppropriate between mL, oz, shots.
            //TODO id -> name lookup
        }

        String ret = sb.toString();
        return ret.trim();
    }

}
