package com.example.autobartender;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MainDataSingleton {
    private static MainDataSingleton instance = null;

    private static final String TAG = "MainDataVM";  // For log.

    // Variables go here
    private MutableLiveData<JSONObject> recipeChoice = new MutableLiveData<JSONObject>();
    public MutableLiveData<JSONObject> getRecipeChoice() {
        return recipeChoice;
    };

    // Recipe DB variables - string keys.
    public JSONArray recipeDB;
    public final String ID = "id";
    public final String NAME = "name";
    public final String DESCRIPTION = "desc";
    public final String INGREDIENTS = "ingr";
    public final String INDEX = "index";
    public final String QUANTITY = "qty";

    public enum RecipeSortOrder {
        DEFAULT, RECENT
    }

    private MainDataSingleton() {

    }

    public static MainDataSingleton getInstance() {
        if (instance != null)
            return instance;

        instance = new MainDataSingleton();
        return instance;
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

    public JSONObject getRecipe(int index, RecipeSortOrder order) {
        try {
            return recipeDB.getJSONObject(index);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d(TAG, "getRecipeDefaultOrder: index out of bounds.");
        }
        return null;
    }


    /**
     * onclick handler for recipe_list items
     * @param index the index in the list
     */
    public void launch_order_info(int index) {
        Log.d(TAG, "launch_order_info: index clicked: " + index);

        // post value, observer handles launching activity.
        this.recipeChoice.postValue(this.getRecipe(index, RecipeSortOrder.DEFAULT));
    }


}
