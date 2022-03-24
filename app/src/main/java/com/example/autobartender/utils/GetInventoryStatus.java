package com.example.autobartender.utils;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.autobartender.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class GetInventoryStatus {
    private final String TAG = "InventoryStatusVM";

    private static GetInventoryStatus instance = null;
    private GetInventoryStatus() { }
    private GetInventoryStatus(Context ctx) {
        try {
            URL_BASE = new URI(ctx.getString(R.string.URL_BASE));
        } catch (URISyntaxException e) {
            Log.d(TAG, "onCreate: R.string.URLBASE did not make a valid URI. check that out");
        }
    }
    public static GetInventoryStatus getInstance(Context ctx) {
        if (instance == null)
            instance = new GetInventoryStatus(ctx);

        return instance;
    }

    public URI URL_BASE;  // the directory all the files are in.

    private InventoryJSONManager inventory;
    public InventoryJSONManager getInventory() {
        if (inventory == null)
            inventory = new InventoryJSONManager();
        return inventory;
    }

    /**
     * Stores inventory jsonobj and has methods to parse and get data from it.
     * Nicely abstracts actual json format away from all other code
     */
    public class InventoryJSONManager {
        // JSON tag names
        private static final String MAX_QUANTITY = "MAX_QUANTITY";
        private static final String NUM_SLOTS = "NUM_SLOTS";
        private static final String SLOTS = "SLOTS";
        private static final String INGREDIENT = "INGREDIENT";
        private static final String QUANTITY = "QUANTITY";

        private final MutableLiveData<JSONObject> ingStats;

        public InventoryJSONManager() {
            ingStats = new MutableLiveData<JSONObject>();
        }

        /**
         * private helper to get inventory. deals with null value stuff that mutableLiveData has
         * @return new JSONObj if null
         */
        private JSONObject inv() {
            if (ingStats.getValue() == null)
                return new JSONObject();
            return ingStats.getValue();
        }

        public int getMaxCapacity() {
            try {
                return inv().getInt(MAX_QUANTITY);
            } catch (JSONException e) {
                Log.d(TAG, "getMaxCapacity: inventory missing MAX_QUANTITY int item");
                return 1;
            }
        }

        public int getNumSlots() {
            try {
                return inv().getInt(NUM_SLOTS);
            } catch (JSONException e) {
                Log.d(TAG, "getNumSlots: inventory missing NUM_SLOTS int item");
                return 1;
            }
        }

        public String getIngredientID(int slotIndex) {
            try {
                return inv().getJSONArray(SLOTS).getJSONObject(slotIndex).getString(INGREDIENT);
            } catch (JSONException e) {
                Log.d(TAG, "getIngredientID: inventory json issue getting inv[SLOTS][int][INGREDIENT]");
                //TODO better default return
                return "";
            }
        }

        public int getIngredientQuantity(int slotIndex) {
            try {
                return inv().getJSONArray(SLOTS).getJSONObject(slotIndex).getInt(QUANTITY);
            } catch (JSONException e) {
                Log.d(TAG, "getIngredientQuantity: inventory json issue getting inv[SLOTS][int][QUANTITY]");
                return 0;
            }
        }

        public int getIngredientQuantity(String ingID) {
            for (int i = 0; i < getNumSlots(); i++) {
                if (getIngredientID(i).equals(ingID)) {
                    return getIngredientQuantity(i);
                }
            }
            return 0;
        }

        public boolean hasQuantityOfIngredient(String ingID, int quantity) {
            return getIngredientQuantity(ingID) >= quantity;
        }
    }

    // Livedata getters
    public MutableLiveData<JSONObject> getIngStatsJson() {
        return getInventory().ingStats;
    }
    private MutableLiveData<String> resultInfo = new MutableLiveData<String>();
    public MutableLiveData<String> getResultInfo() {
        if (resultInfo == null)
            resultInfo = new MutableLiveData<String>();
        return resultInfo;
    }


    public void requestFetchFullInventory(Context ctx) {
        // Add appropriate path to end of URL and start thread
        try {
            URL url = URL_BASE.resolve(ctx.getString(R.string.URL_PATH_INVENTORY)).toURL();
            IngStatsRequestThread getJSONThread = new IngStatsRequestThread(url);
            getJSONThread.start();
        } catch (MalformedURLException e) {
            Log.d(TAG, "requestFetchFullInventory: MALFORMED URL. This is hardcoded so should not happen");
            Log.d(TAG, String.format(
                    "requestFetchFullInventory: URLBASE = %s, path = %s",
                    URL_BASE,
                    ctx.getString(R.string.URL_PATH_INVENTORY)
            ));
        }
    }


    public class IngStatsRequestThread extends Thread{
        private static final String TAG = "GetIngredientStatsJSON";
        private static final int DEFAULTBUFFERSIZE = 8096;
        private static final int TIMEOUT = 1000; // 1 second
        protected int statusCode = 0;
        private final URL URL;
        private MutableLiveData<String> output_dest;

        public IngStatsRequestThread(URL url) {
            Log.d(TAG, "constructor: Initialized new Thread to get json from url " + url);
            this.URL=url;
        }

        public void run() {
            Log.d(TAG, "run: getting ingredient stats json from URL: " + URL);
            try {
                // Setup connection
                HttpURLConnection connection = (HttpURLConnection) this.URL.openConnection();
                connection.setRequestMethod("GET");
                connection.setReadTimeout(TIMEOUT);
                connection.setConnectTimeout(TIMEOUT);
                connection.setRequestProperty("Accept-Charset", "UTF-8");

                // Do network IO
                BufferedReader in = null;
                try {
                    Log.d(TAG, "run: trying connection...");
                    connection.connect();

                    // ensure code is right
                    statusCode = connection.getResponseCode();
                    Log.d(TAG, "run: connection statuscode=" + statusCode);
                    if (statusCode / 100 != 2) {
                        Log.d(TAG, "run: failed. Not updating data");
                        resultInfo.postValue("failed, code " + statusCode);
                        return;
                    }

                    // get output data
                    in = new BufferedReader(new InputStreamReader(connection.getInputStream()), DEFAULTBUFFERSIZE);
                    // the following buffer will grow as needed
                    String myData;
                    StringBuilder sb = new StringBuilder();
                    while ((myData = in.readLine()) != null) {
                        sb.append(myData);
                    }
                    inventory.ingStats.postValue(new JSONObject(sb.toString()));
                    Log.d(TAG, "run: Successfully posted JSOn value");
                } finally {
                    // close resource no matter what exception occurs
                    if(in != null)
                        in.close();
                    connection.disconnect();
                }
            } catch (Exception exc) {
                Log.d(TAG, "run: " + exc.toString());
                resultInfo.postValue(exc.toString());
            }
        }
    }


}