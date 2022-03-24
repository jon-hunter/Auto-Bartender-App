package com.example.autobartender.utils;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RecipeDBManager {
    private static final String TAG = "MainDataVM";  // For log.

    private static RecipeDBManager instance = null;
    private RecipeDBManager() {  }
    public static RecipeDBManager getInstance() {
        if (instance == null)
            instance = new RecipeDBManager();

        return instance;
    }

    // Variables go here
    private MutableLiveData<JSONObject> selectedRecipe = new MutableLiveData<JSONObject>();
    public MutableLiveData<JSONObject> getSelectedRecipe() {
        return selectedRecipe;
    };
    public String getSelectedRecipeID() {
        try {
            return selectedRecipe.getValue().getString("id");
        } catch (JSONException e) {
            return "null";
        }
    }

    // Recipe DB variables - string keys.
    public JSONArray recipeDB;
    public final String ID = "ID";
    public final String NAME = "NAME";
    public final String DESCRIPTION = "DESCRIPTION";
    public final String INGREDIENTS = "INGREDIENTS";
    public final String INDEX = "INDEX";
    public final String QUANTITY = "QUANTITY";

    public enum RecipeSortOrder {
        DEFAULT, RECENT
    }


    /**
     * calculates the nuymber of recipes found in the DB
     *
     * @return int num of recipes
     */
    public int getNumRecipes() {
        if (this.recipeDB == null) {
            Log.d(TAG, "getNumRecipes: recipeDB is null, returning 0");
            return 0;
        }

        Log.d(TAG, "getNumRecipes: recipeDB has recipes: " + recipeDB.length());
        return recipeDB.length();
    }


    public JSONObject getRecipe(String recipeID) {
        try {
            for (int i = 0; i < this.recipeDB.length(); i++) {
                if (this.recipeDB.getJSONObject(i).getString(ID).equals(recipeID)) {
                    return this.recipeDB.getJSONObject(i);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public JSONObject getRecipe(int index, RecipeSortOrder order) {
        try {
            return recipeDB.getJSONObject(index);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * onclick handler for recipe_list items
     * @param recipeID: which recipe selected
     */
    public void launch_recipe_order_info(String recipeID) {
        // post value, observer in mainActivity launches activity on livedata update.
        this.selectedRecipe.postValue(this.getRecipe(recipeID));
    }

}
