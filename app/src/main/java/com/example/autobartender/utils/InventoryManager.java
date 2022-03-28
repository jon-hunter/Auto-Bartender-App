package com.example.autobartender.utils;

import android.util.Log;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;

import com.example.autobartender.utils.RecipeManager.Recipe;


public class InventoryManager {
    private static final String TAG = "InventoryManager";

    private static JSONObject inventory;
    public static SimpleObserverManager observers;


    // Initialize everything
    static {
        Log.d(TAG, "static initializer: initializing");

        observers = new SimpleObserverManager();
        inventory = new JSONObject();
    }


    /**
     * Begin network request to update inventory.
     */
    public static void updateInventory() {
        Log.d(TAG, "updateInventory: updating");

        // Setup URL
        URL url = null;
        try {
            url = Constants.getURLBase().resolve(Constants.URL_PATH_INVENTORY).toURL();
        } catch (MalformedURLException e) {
            Log.d(TAG, "updateInventory: MALFORMED URL. This is hardcoded so should not happen");
            Log.d(TAG, String.format(
                    "updateInventory: URLBASE = %s, path = %s",
                    Constants.getURLBase(),
                    Constants.URL_PATH_INVENTORY
            ));
        }

        // Setup return data observer
        MutableLiveData<String> rawInventory = new MutableLiveData<String>();
        rawInventory.observeForever(new Observer<String>() {
            @Override
            public void onChanged(String s) {
                onInventoryUpdate(s);
            }
        });

        // Start thread, passing return data observer
        NetworkGETRequest inventoryRequest = new NetworkGETRequest(url, rawInventory);
        inventoryRequest.start();
    }


    private static void onInventoryUpdate(String s) {
        try {
            inventory = new JSONObject(s);
        } catch (JSONException e) {
            Log.d(TAG, "onInventoryUpdate: JSON error loading data from server: " + e.getLocalizedMessage());
        }

        observers.update();
    }




    public static int getMaxCapacity() {
        try {
            return inventory.getInt(Constants.MAX_QUANTITY);
        } catch (JSONException e) {
            Log.d(TAG, "getMaxCapacity: inventory missing MAX_QUANTITY int item");
            return 1;
        }
    }

    public static int getNumSlots() {
        try {
            return inventory.getInt(Constants.NUM_SLOTS);
        } catch (JSONException e) {
            Log.d(TAG, "getNumSlots: inventory missing NUM_SLOTS int item");
            return 1;
        }
    }

    public static String getIngredientID(int slotIndex) {
        try {
            return inventory.getJSONArray(Constants.SLOTS).getJSONObject(slotIndex).getString(Constants.INGREDIENT);
        } catch (JSONException e) {
            Log.d(TAG, "getIngredientID: inventory json issue getting inv[SLOTS][int][INGREDIENT]");
            //TODO better default return
            return "";
        }
    }

    public static int getIngredientQuantity(int slotIndex) {
        try {
            return inventory.getJSONArray(Constants.SLOTS).getJSONObject(slotIndex).getInt(Constants.QUANTITY);
        } catch (JSONException e) {
            Log.d(TAG, "getIngredientQuantity: inventory json issue getting inv[SLOTS][int][QUANTITY]");
            return 0;
        }
    }

    public static int getIngredientQuantity(String ingID) {
        for (int i = 0; i < getNumSlots(); i++) {
            if (getIngredientID(i).equals(ingID)) {
                return getIngredientQuantity(i);
            }
        }
        return 0;
    }

    public static boolean hasQuantityOfIngredient(String ingID, int quantity) {
        return getIngredientQuantity(ingID) >= quantity;
    }

    public static boolean canMakeRecipe(Recipe recipe) {
        for (int i = 0; i < recipe.getNumIngredients(); i++) {
            if (!hasQuantityOfIngredient(
                    recipe.getIngredient(i).id,
                    recipe.getIngredient(i).quantity_ml
            ))
                return false;
        }

        return true;
    }
}
