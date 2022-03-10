package com.example.autobartender.ui.inventory_monitor;

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
import java.net.URL;

public class InventoryStatusVM extends ViewModel {
    private final String TAG = "InventoryStatusVM";

    public URI URL_BASE;  // the directory all the files are in.

    private InventoryJSON inventory;
    public InventoryJSON getInventory() {
        if (inventory == null)
            inventory = new InventoryJSON();
        return inventory;
    }

    /**
     * Stores inventory jsonobj and has methods to parse and get data from it.
     * Nicely abstracts actual json format away from all other code
     */
    public class InventoryJSON {
        // JSON tag names
        private final String MAX_QUANTITY = "MAX_QUANTITY";
        private final String NUM_SLOTS = "NUM_SLOTS";
        private final String SLOTS = "SLOTS";
        private final String INGREDIENT = "INGREDIENT";
        private final String QUANTITY = "QUANTITY";

        private final MutableLiveData<JSONObject> ingStats;

        public InventoryJSON() {
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
            GetIngredientStatsJSON getJSONThread = new GetIngredientStatsJSON(url);
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


    public class GetIngredientStatsJSON extends Thread{
        private static final String TAG = "GetIngredientStatsJSON";
        private static final int DEFAULTBUFFERSIZE = 8096;
        private static final int TIMEOUT = 1000; // 1 second
        protected int statusCode = 0;
        private final URL URL;
        private MutableLiveData<String> output_dest;

        public GetIngredientStatsJSON(URL url) {
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