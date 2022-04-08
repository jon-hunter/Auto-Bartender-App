package com.example.autobartender.utils;

import android.content.SharedPreferences;
import android.util.Log;

import java.net.URI;
import java.net.URISyntaxException;

public class PrefsManager {
    private static final String TAG = PrefsManager.class.getName();


    public static void initPrefsManager(SharedPreferences settings) {
        setUserID(settings.getString(Constants.PREFS_USERID, null));
        setUrlBase(settings.getString(Constants.PREFS_SERVER_URL, Constants.URLBASE_DEFAULT));
        setRecipeDBSource(settings.getString(Constants.PREFS_RECIPE_DB_SOURCE, Constants.RECIPE_DB_KEY_DEFAULT));
        setMaxInventoryAge(settings.getInt(Constants.PREFS_INV_REFRESH_TM, Constants.DEFAULT_MAX_AGE_INVENTORY));
        setMaxDrinkQueueAge(settings.getInt(Constants.PREFS_DQ_REFRESH_TM, Constants.DEFAULT_MAX_AGE_DRINK_QUEUE));
    }


    // Preference URLBASE
    private static URI urlBase;
    public static URI getURLBase() {
        if (urlBase == null) {
            try {
                urlBase = new URI(Constants.URLBASE_DEFAULT);
            } catch (URISyntaxException e) {
                Log.d(TAG, "getURLBase: Constants.URLBASE_DEFAULT did not make a valid URI. check that out");
            }
        }

        return urlBase;
    }
    public static void setUrlBase(String url) {
        try {
            urlBase = new URI(url);
        } catch (URISyntaxException e) {
            Log.d(TAG, "setUrlBase: failed. url= " + url + " - " + e.getLocalizedMessage());
            urlBase = null;
        }
    }


    // Preference RecipeDB location
    private static String recipeDBSource;
    public static String getRecipeDBSource() {
        return recipeDBSource;
    }
    public static String getRecipeDBSourceURI() {
        if (recipeDBSource == null)
            recipeDBSource = Constants.RECIPE_DB_KEY_DEFAULT;

        switch (recipeDBSource) {
            case Constants.RECIPE_DB_KEY_FTP:
                Log.d(TAG, "getRecipeDBSourceURI: not yet implemented oops");
                //TODO implement
                return null;
            case Constants.RECIPE_DB_KEY_HARDCODED:
            default:
                return Constants.URL_RECIPE_DB_HARDCODED;
        }
    }
    public static void setRecipeDBSource(String key) {
        recipeDBSource = key;
    }

    // Preference UserID
    private static String userID;
    public static String getUserID() {
        return userID;
    }
    public static void setUserID(String userID) {
        if (userID != null)
            PrefsManager.userID = userID;
    }


    // Preference Inventory timeout
    private static long maxInventoryAge;
    public static long getMaxInventoryAge() {
        return maxInventoryAge;
    }
    public static void setMaxInventoryAge(long maxInventoryAge) {
        PrefsManager.maxInventoryAge = maxInventoryAge;
    }


    // Preference Drink Queue timeout
    private static long maxDrinkQueueAge;
    public static long getMaxDrinkQueueAge() {
        return maxDrinkQueueAge;
    }
    public static void setMaxDrinkQueueAge(long maxDrinkQueueAge) {
        PrefsManager.maxDrinkQueueAge = maxDrinkQueueAge;
    }
}
