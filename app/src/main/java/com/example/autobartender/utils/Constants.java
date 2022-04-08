package com.example.autobartender.utils;

import android.content.Context;
import android.util.Log;

import com.example.autobartender.R;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.Locale;

/**
 * In this project there are a large number of String constants that are language/locale independent.
 * These Strings are stored here instead of the String resource file for simplicity.
 */
public class Constants {
    public static final String TAG = Constants.class.getName();

    // JSON Tag Strings. Used in recipe storage and networking
    public static final String
            ADDED_TO_QUEUE = "ADDED_TO_QUEUE",
            ALC_PCT = "ALC_PCT",
            DESCRIPTION = "DESCRIPTION",
            DRINK_ID = "DRINK_ID",
            ID = "ID",
            IMAGE_FILENAME = "IMAGE",
            INDEX = "INDEX",
            INGREDIENT = "INGREDIENT",
            INGREDIENTS = "INGREDIENTS",
            MAX_QUANTITY = "MAX_QUANTITY",
            NAME = "NAME",
            NUM_SLOTS = "NUM_SLOTS",
            PROGRESS = "PROGRESS",
            QUANTITY = "QUANTITY",
            QUEUE = "QUEUE",
            QUEUE_POS = "QUEUE_POSITION",
            READY = "READY",
            RECIPE = "RECIPE",
            RECIPES = "RECIPES",
            REQUEST_TIME = "REQUEST_TIME",
            SLOTS = "SLOTS",
            STARTED = "STARTED",
            TIMESTAMP = "TIMESTAMP",
            TYPE = "TYPE",
            USER_ID = "USER_ID";

    // Networking params
    public static final int
            TIMEOUT = 1000,  // ms
            DEFAULTBUFFERSIZE = 8096,
            DEFAULT_MAX_AGE_INVENTORY = 2000,  // ms
            MIN_MAX_AGE_INVENTORY = 1000,
            MAX_MAX_AGE_INVENTORY = 30000,
            DEFAULT_MAX_AGE_DRINK_QUEUE = 2000,
            MIN_MAX_AGE_DRINK_QUEUE = 1000,
            MAX_MAX_AGE_DRINK_QUEUE = 30000;

    // Networking URLs and paths
    public static final String
            URLBASE_EMULATOR_SIMULATED = "http://10.0.2.2:8080/",
            URLBASE_EMULATOR_HARDCODED = "http://10.0.2.2:8000/",
            URLBASE_DEFAULT = URLBASE_EMULATOR_HARDCODED,
            URL_PATH_INVENTORY = "./inventory",
            URL_PATH_DRINK = "./drink";

    // Recipe database keys, URLs, etc,
    public static final String
            RECIPE_DB_KEY_HARDCODED = "RECIPE_DB_HARDCODED",
            RECIPE_DB_KEY_FTP = "RECIPE_DB_FTP",
            RECIPE_DB_KEY_DEFAULT = RECIPE_DB_KEY_HARDCODED,
            URL_RECIPE_DB_HARDCODED = "http://10.0.2.2:8000/recipe_db.json",
            URL_PATH_IMG = "img/";

    // Default Ingredient ID
    public static final String UNKNOWN = "UNKNOWN";

    // Arrays of values for preferences. Must not be changed without corresponding change to arrays in string resource
    public static final String[] NETWORK_URLS = new String[] {
            URLBASE_EMULATOR_SIMULATED,
            URLBASE_EMULATOR_HARDCODED
    };
    public static final String[] RECIPE_DB_OPTIONS = new String[] {
            RECIPE_DB_KEY_FTP,
            RECIPE_DB_KEY_HARDCODED
    };


    // SharedPrefs identifiers
    public static final String
            PREFS_USERID = "USER_ID",
            PREFS_SERVER_URL = "SERVER_URL",
            PREFS_RECIPE_DB_SOURCE = "RECIPE_DB_SOURCE",
            PREFS_INV_REFRESH_TM = "INV_REFRESH_TM",
            PREFS_DQ_REFRESH_TM = "DQ_REFRESH_TM",
            LOCAL_RECIPE_DB_SHAREDPREFS = "LOCAL_RECIPE_DB_SHAREDPREFS";


    // Ingredient locale-dependent name lookup key (see R.string.ing_name_VODKA for example)
    public static final String ING_NAME_LOOKUP_KEY = "ING_NAME_%s";

    // Recipe sort-order lookup key (see r.string.SORT_ORDER_ALPHABETICAL for example)
    public static final String SORT_ORDER_LOOKUP_KEY = "SORT_ORDER_%s";

    // DATETIME formatting functions
    /**
     * Helper to get current time (UTC) in ISO 8601 format. uses "Z" timezone designator instd of "+00:00"
     * see https://en.wikipedia.org/wiki/ISO_8601
     * ex "2022-04-01T04:23:30.723+00:00[UTC]"
     * @return timestamp
     */
    public static String getTimestamp(OffsetDateTime date) {
        return date.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }

    public static OffsetDateTime getDateTime(String timestamp) {
        try {
            return OffsetDateTime.parse(timestamp);
        } catch (DateTimeParseException e) {
            Log.d(TAG, "getDateFromTimestamp: error parsing " + timestamp + " - " + e.getLocalizedMessage());
        }
        return getCurrentTime();
    }

    public static OffsetDateTime getCurrentTime() {
        return Instant.now().atOffset(ZoneOffset.UTC);
    }

    /**
     * gets time difference in format "2m ago"
     * needs context bc its gotta be locale dependent >:(
     */
    public static String formatTimeDifference(OffsetDateTime datetime, Context ctx) {
        Duration diff = Duration.between(datetime, getCurrentTime());

        if (diff.compareTo(Duration.ofSeconds(5)) < 0)
            return ctx.getString(R.string.time_ago_now);

        String subRet;
        if (diff.compareTo(Duration.ofDays(1)) >= 0)
            subRet = String.format(ctx.getString(R.string.time_ago_d), diff.toDays());
        else if (diff.compareTo(Duration.ofHours(1)) >= 0)
            subRet = String.format(ctx.getString(R.string.time_ago_h), diff.toHours());
        else if (diff.compareTo(Duration.ofMinutes(1)) >= 0)
            subRet = String.format(ctx.getString(R.string.time_ago_m), diff.toMinutes());
        else // know its btwn 1m and 5s
            subRet = String.format(ctx.getString(R.string.time_ago_s), diff.getSeconds());

        return String.format(ctx.getString(R.string.time_ago_main), subRet);
    }
}
