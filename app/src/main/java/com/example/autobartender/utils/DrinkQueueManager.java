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


/**
 * Class to keep track of drink objects in the queue.
 * All recipes for these drinks are stored with the drink. Because each recipe can be different
 */
public class DrinkQueueManager {
    private static final String TAG = "DrinkQueueManager";


    public static class Drink {
        public String drinkID;
        public String userID;
        public Date requestTime;
        public double progress;
        public boolean isStarted;
        public Recipe recipe;

        public Drink(JSONObject drink) {
            try {
                this.drinkID = drink.getString(Constants.DRINK_ID);
                this.userID = drink.getString(Constants.USER_ID);
                this.requestTime = Constants.getDateFromTimestamp(drink.getString(Constants.REQUEST_TIME));
                this.progress = drink.getDouble(Constants.PROGRESS);
                this.isStarted = drink.getBoolean(Constants.STARTED);
                this.recipe = new Recipe(drink.getJSONObject(Constants.RECIPE));
            }
            catch (JSONException e) {
                Log.d(TAG, "Drink: bad json drink: " + e.getLocalizedMessage() + ". json= " + drink);
            }
        }


        public int getProgressPct() {
            return (int)( this.progress * 100);
        }
    }


    private static ArrayList<Drink> drinkQueue;
    private static boolean queueInitialized;
    public static SimpleObserverManager observers;


    // Initializer
    static {
        Log.d(TAG, "static initializer: init ing");

        observers = new SimpleObserverManager();
        drinkQueue = new ArrayList<Drink>();
        queueInitialized = false;
    }


    /**
     * Begin network request to update drink queue
     */
    public static void updateDrinkQueue() {
        Log.d(TAG, "updateDrinkQueue: updating");

        // Setup URL
        URL url = null;
        try {
//            url = Constants.getURLBase().resolve(Constants.URL_PATH_DRINK).toURL();
            url = new URL("http://10.0.2.2:8000/DRINK_QUEUE.JSON");
            //TODO use real URL
        } catch (MalformedURLException e) {
            Log.d(TAG, "updateDrinkQueue: MALFORMED URL. This is hardcoded so should not happen");
            Log.d(TAG, String.format(
                    "updateDrinkQueue: URLBASE = %s, path = %s",
                    Constants.getURLBase(),
                    Constants.URL_PATH_DRINK
            ));
        }

        // Setup return data observer
        MutableLiveData<String> rawDrinkQueue = new MutableLiveData<String>();
        rawDrinkQueue.observeForever(new Observer<String>() {
            @Override
            public void onChanged(String s) {
                onDrinkQueueUpdate(s);
            }
        });

        // Start thread, passing return data observer
        NetworkGETRequest drinkQueueRequest = new NetworkGETRequest(url, rawDrinkQueue);
        drinkQueueRequest.start();
    }

    /**
     * Called when drink queue request completes
     */
    private static void onDrinkQueueUpdate(String s) {
        Log.d(TAG, "onDrinkQueueUpdate: update received: " + s);

        try {
            JSONObject dqObj = new JSONObject(s);
            drinkQueue.clear();
            int qlen = dqObj.getInt(Constants.QUEUE_LEN);
            for (int i = 0; i < qlen; i++) {
                drinkQueue.add(new Drink(dqObj.getJSONArray(Constants.QUEUE).getJSONObject(i)));
            }
            Log.d(TAG, "onDrinkQueueUpdate: drink queue json loaded successfully");
        } catch (JSONException e) {
            Log.d(TAG, "onDrinkQueueUpdate: JSON error loading data from server: " + e.getLocalizedMessage());
        }

        observers.update();
    }


    public static int getQueueLength() {
        return drinkQueue.size();
    }


    public static Drink getDrink(int index) {
        return drinkQueue.get(index);
    }
}
