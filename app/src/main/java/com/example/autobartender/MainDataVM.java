package com.example.autobartender;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.ViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MainDataVM extends ViewModel {
    private static final String TAG = "MainDataVM";  // For log.

    // Variables go here

    // Recipe DB variables - string keys.
    public JSONArray recipeDB;
    public final String ID = "id";
    public final String NAME = "name";
    public final String DESCRIPTION = "desc";
    public final String INGREDIENTS = "ingr";
    public final String INDEX = "index";
    public final String QUANTITY = "qty";


    /**
     * calculates the nuymber of recipes found in the DB
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
}
