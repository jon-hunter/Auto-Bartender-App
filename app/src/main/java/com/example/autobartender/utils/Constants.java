package com.example.autobartender.utils;

import android.util.Log;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.util.Date;

/**
 * In this project there are a large number of String constants that are language/locale independent.
 * These Strings are stored here instead of the String resource file for simplicity.
 */
public class Constants {
    private static final String TAG = "Constants";

    // JSON Tag Strings. Used in recipe storage and networking
    public static final String
            CUSTOM = "CUSTOM",
            DESCRIPTION = "DESCRIPTION",
            DRINK_ID = "DRINK_ID",
            ID = "ID",
            INDEX = "INDEX",
            INGREDIENT = "INGREDIENT",
            INGREDIENTS = "INGREDIENTS",
            MAX_QUANTITY = "MAX_QUANTITY",
            MODIFIED = "MODIFIED",
            NAME = "NAME",
            NUM_SLOTS = "NUM_SLOTS",
            PROGRESS = "PROGRESS",
            QUANTITY = "QUANTITY",
            QUEUE = "QUEUE",
            QUEUE_LEN = "QUEUE_LENGTH",
            RECIPE = "RECIPE",
            REQUEST_TIME = "REQUEST_TIME",
            SLOTS = "SLOTS",
            STARTED = "STARTED",
            TIMESTAMP = "TIMESTAMP",
            TYPE = "TYPE",
            UNMODIFIED = "UNMODIFIED",
            USER_ID = "USER_ID";

    // Networking params
    public static final int TIMEOUT = 1000;  // ms
    public static final int DEFAULTBUFFERSIZE = 8096;

    // Networking URLs and paths
    public static final String
            ON_EMULATOR_URL = "http://10.0.2.2:8080/",
            URL_PATH_INVENTORY = "./inventory/",
            URL_PATH_DRINK = "./drink/";

    // SharedPrefs identifiers
    public static final String
            LOCAL_RECIPE_DB = "LOCAL_RECIPE_DB_SHAREDPREFS";

    /**
     * Get appropriate Base URL (emulator, production build, etc)
     * @return base url
     * TODO do this fr not just emulator base url
     */
    public static URI getURLBase() {
        try {
            return new URI(ON_EMULATOR_URL);
        } catch (URISyntaxException e) {
            Log.d(TAG, "getURLBase: Constants.getURLBase() did not make a valid URI. check that out");
        }
        return null;
    }


    /**
     * Helper to get current time (UTC) in standard format
     * @return timestamp
     */
    public static String getCurrentTimestamp() {
        //TODO fix UTC formatting
        return DateFormat.getDateTimeInstance().format(new Date());
    }

    public static Date getDateFromTimestamp(String timestamp) {
        //TODO implement
        return new Date();
    }
}
