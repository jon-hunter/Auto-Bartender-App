package com.example.autobartender.utils;

import android.content.SharedPreferences;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;

public class PrefsManager {
    private static final String TAG = PrefsManager.class.getName();


    public static void initPrefsManager(SharedPreferences settings) {
        setUserID(settings.getString(Constants.PREFS_USERID, null));
        setKnownMachines(settings.getString(Constants.PREFS_KNOWN_MACHINES, null));
        setFavoriteMachineId(settings.getString(Constants.PREFS_FAVORITE_MACHINE_ID, null));
        setUrlBase(settings.getString(Constants.PREFS_SERVER_URL, Constants.URLBASE_DEFAULT));
        setRecipeDBSource(settings.getString(Constants.PREFS_RECIPE_DB_SOURCE, Constants.RECIPE_DB_KEY_DEFAULT));
        setMaxInventoryAge(settings.getInt(Constants.PREFS_INV_REFRESH_TM, Constants.DEFAULT_MAX_AGE_INVENTORY));
        setMaxDrinkQueueAge(settings.getInt(Constants.PREFS_DQ_REFRESH_TM, Constants.DEFAULT_MAX_AGE_DRINK_QUEUE));
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


    // Preference Favorite_machine_id
    private static String favoriteMachineId;
    public static String getFavoriteMachineId() {
        return favoriteMachineId;
    }
    public static void setFavoriteMachineId(String favoriteMachineId) {
        PrefsManager.favoriteMachineId = favoriteMachineId;
    }


    // Preference Current selected machine
    private static final MutableLiveData<String> currentSelectedMachineID = new MutableLiveData<String>();
    public static MutableLiveData<String> getCurrentSelectedMachineID() {
        return currentSelectedMachineID;
    }
    public static void setCurrentSelectedMachineID(String id) {
        currentSelectedMachineID.setValue(id);
    }


    // Preference Known machines
    private static final MutableLiveData<JSONArray> knownMachines = new MutableLiveData<JSONArray>();
    public static MutableLiveData<JSONArray> getKnownMachines() {
        return knownMachines;
    }
    public static void setKnownMachines(String machines) {
        if (machines == null) {
            knownMachines.setValue(new JSONArray());
            return;
        }

        try {
            knownMachines.setValue(new JSONArray(machines));
        } catch (JSONException e) {
            Log.d(TAG, "setKnownMachines: error parsing JSOn string: " + e.getLocalizedMessage() + " - " + machines);
        }
    }
    public static void addMachine(String machine, SharedPreferences prefs) {
        if (machine == null)
            return;

        if (knownMachines.getValue() == null)
            setKnownMachines(null);

        try {
            JSONArray obj = knownMachines.getValue();
            obj.put(new JSONObject(machine));
            knownMachines.setValue(obj);
            commitMachineList(prefs);
        } catch (JSONException e) {
            Log.d(TAG, "setKnownMachines: error parsing JSOn string: " + e.getLocalizedMessage() + " - " + machine);
        }
    }
    public static void deleteMachine(String machineID, SharedPreferences prefs) {
        Log.d(TAG, "deleteMachine: deleting " + machineID);
        //TODO should probably check for if im deleting the fav or current selected one
        for (int i = 0; i < knownMachines.getValue().length(); i++) {
            try {
                JSONObject machine = knownMachines.getValue().getJSONObject(i);
                if (machine.getString(Constants.MACHINE_ID).equals(machineID)) {
                    JSONArray obj = knownMachines.getValue();
                    obj.remove(i);
                    knownMachines.setValue(obj);
                    commitMachineList(prefs);
                    return;
                }
            } catch (JSONException e) {
                Log.d(TAG, "getMachineIDFromName: json eror idk: " + e.getLocalizedMessage());
            }
        }
    }
    private static void commitMachineList(SharedPreferences prefs) {
        if (knownMachines.getValue() == null)
            return;

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Constants.PREFS_KNOWN_MACHINES, knownMachines.getValue().toString());
        editor.apply();
    }

    /**
     * Gets a printable string displaying attributes of all known machines.
     * fills in format with name, ID, addr respectively. Format for users locale
     * @param format string format for each machine. must have 3 str fields
     * @param knowNone string to return if there are no machines known
     * @return repr of all machines
     */
    public static String getMachineListPrintable(String format, String knowNone) {
        if (knownMachines.getValue() == null || knownMachines.getValue().length() == 0)
            return knowNone;

        StringBuilder out = new StringBuilder();
        try {
            for (int i = 0; i < knownMachines.getValue().length(); i++) {
                JSONObject machine = knownMachines.getValue().getJSONObject(i);
                out.append(String.format(
                        format,
                        machine.getString(Constants.MACHINE_NAME),
                        machine.getString(Constants.MACHINE_ID),
                        machine.getString(Constants.MACHINE_ADDR)
                ));
            }
            return out.toString().trim();
        } catch (JSONException e) {
            Log.d(TAG, "getMachineList: fuckup iterating json array: " + e.getLocalizedMessage());
            return "could not read machine list. this is a bug";
        }
    }

    /**
     * get list of machine attrs, NAME or ID for example.
     * key is parameterized so i didnt have to copy paste this code lol
     * @param key the attr to get. should be in [Constants.MACHINE_ID, Constants.MACHINE_ADDR, Constants.MACHINE_NAME]
     * @return array of the attribute strings
     */
    public static String[] getKnownMachineAttrs(String key) {
        if (knownMachines.getValue() == null)
            return null;

        String[] out = new String[knownMachines.getValue().length()];
        for (int i = 0; i < knownMachines.getValue().length(); i++) {
            try {
                out[i] = knownMachines.getValue().getJSONObject(i).getString(key);
            } catch (JSONException e) {
                Log.d(TAG, "getKnownMachineAttrs: json error oopsie: " + e.getLocalizedMessage());
            }
        }
        return out;
    }

    public static String getMachineIDFromName(String name) {
        for (int i = 0; i < knownMachines.getValue().length(); i++) {
            try {
                JSONObject machine = knownMachines.getValue().getJSONObject(i);
                if (machine.getString(Constants.MACHINE_NAME).equals(name))
                    return machine.getString(Constants.MACHINE_ID);
            } catch (JSONException e) {
                Log.d(TAG, "getMachineIDFromName: json eror idk: " + e.getLocalizedMessage());
            }
        }
        return null;
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
