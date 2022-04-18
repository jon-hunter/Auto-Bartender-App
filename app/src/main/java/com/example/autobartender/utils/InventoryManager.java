package com.example.autobartender.utils;

import android.util.Log;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;

import com.example.autobartender.utils.RecipeManager.Recipe;
import com.example.autobartender.utils.RecipeManager.RecipeIngredient;
import com.example.autobartender.utils.networking.HTTPGETBase.RequestStatus;
import com.example.autobartender.utils.networking.HTTPGETJSONObject;


public class InventoryManager {
    private static final String TAG = InventoryManager.class.getName();

    private static JSONObject inventory;
    public static SimpleObserverManager observers;
    private static HTTPGETJSONObject requestThread;
    private static JSONObject inventory() {
        if (observers.timeSinceUpdate() > PrefsManager.getMaxInventoryAge())
            updateInventory();

        return inventory;
    }


    // Initialize everything
    static {
        Log.d(TAG, "static initializer: initializing");

        observers = new SimpleObserverManager();
        inventory = new JSONObject();
        requestThread = null;

        // Observe machine selection - if it changes we need to update
        PrefsManager.getCurrentSelectedMachineID().observeForever(s -> updateInventory());
    }


    /**
     * Begin network request to update inventory.
     */
    public static void updateInventory() {
        // Ensure dont already have active thread
        if (requestThread != null)
            return;

        Log.d(TAG, "updateInventory: updating");

        // Setup URL
        URL url = null;
        try {
            url = PrefsManager.getURLBase().resolve(Constants.URL_PATH_INVENTORY).toURL();
        } catch (MalformedURLException e) {
            Log.d(TAG, "updateInventory: MALFORMED URL. This is hardcoded so should not happen");
            Log.d(TAG, String.format(
                    "updateInventory: URLBASE = %s, path = %s",
                    PrefsManager.getURLBase(),
                    Constants.URL_PATH_INVENTORY
            ));
        }

        // Setup return data and status observer
        MutableLiveData<RequestStatus> requestStatus = new MutableLiveData<RequestStatus>();
        requestStatus.observeForever(new Observer<RequestStatus>() {
            @Override
            public void onChanged(RequestStatus s) { onInventoryUpdate(s); }
        });

        // Start thread, passing return data observer
        requestThread = new HTTPGETJSONObject(url, requestStatus);
        requestThread.start();
    }


    private static void onInventoryUpdate(RequestStatus s) {
        if (s != RequestStatus.DONE_SUCCESS) {
            Log.d(TAG, "onInventoryUpdate: status is fail. oops");
            nullThread();
            return;
        }

        inventory = requestThread.getJsonObject();
        observers.update();
        nullThread();
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


    public static int getMaxCapacity() {
        try {
            return inventory().getInt(Constants.MAX_QUANTITY);
        } catch (JSONException e) {
            Log.d(TAG, "getMaxCapacity: inventory missing MAX_QUANTITY int item");
            return 1;
        }
    }

    public static int getNumSlots() {
        try {
            return inventory().getInt(Constants.NUM_SLOTS);
        } catch (JSONException e) {
            Log.d(TAG, "getNumSlots: inventory missing NUM_SLOTS int item");
            return 1;
        }
    }

    public static String getIngredientID(int slotIndex) {
        try {
            return inventory().getJSONArray(Constants.SLOTS).getJSONObject(slotIndex).getString(Constants.INGREDIENT);
        } catch (JSONException e) {
            Log.d(TAG, "getIngredientID: inventory json issue getting inv[SLOTS][int][INGREDIENT] - " + e.getLocalizedMessage());
            //TODO better default return
            return "";
        }
    }

    public static int getIngredientQuantity(int slotIndex) {
        try {
            return inventory().getJSONArray(Constants.SLOTS).getJSONObject(slotIndex).getInt(Constants.QUANTITY);
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
        for (RecipeIngredient ing: recipe)
            if (!hasQuantityOfIngredient(ing.getID(), ing.getQuantity_ml()))
                return false;

        return true;
    }
}
