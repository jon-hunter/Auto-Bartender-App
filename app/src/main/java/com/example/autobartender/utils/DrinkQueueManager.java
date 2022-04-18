package com.example.autobartender.utils;

import android.util.Log;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.UUID;

import com.example.autobartender.utils.RecipeManager.Recipe;
import com.example.autobartender.utils.networking.HTTPGETBase.RequestStatus;
import com.example.autobartender.utils.networking.HTTPGETJSONObject;
import com.example.autobartender.utils.networking.NetworkPOSTRequest;


/**
 * Class to keep track of drink objects in the queue.
 * All recipes for these drinks are stored with the drink. Because each recipe can be different
 */
public class DrinkQueueManager {
    private static final String TAG = DrinkQueueManager.class.getSimpleName();

    /**
     * represents a drink request within the app. contains all drink+recipe attributes, and data to track request's state
     */
    public static class Drink {
        public String drinkID;
        public String userID;
        public OffsetDateTime requestTime;
        public Recipe recipe;

        public double progress;
        public boolean isStarted;
        public boolean inMachineQueue;  // Whether confirmed in machine's queue
        public boolean machineReady;

        public Drink() {}
        public Drink(JSONObject drink) {
            try {
                this.drinkID = drink.getString(Constants.DRINK_ID);
                this.userID = drink.getString(Constants.USER_ID);
                this.requestTime = Constants.getDateTime(drink.getString(Constants.REQUEST_TIME));
                this.progress = drink.getDouble(Constants.PROGRESS);
                this.isStarted = drink.getBoolean(Constants.STARTED);
                this.recipe = new Recipe(drink.getJSONObject(Constants.RECIPE));
                this.inMachineQueue = false;
                this.machineReady = false;
            }
            catch (JSONException e) {
                Log.d(TAG, "Drink: bad json drink: " + e.getLocalizedMessage() + ". json= " + drink);
            }
        }


        public int getProgressPct() {
            return (int)(this.progress * 100);
        }
    }


    private static ArrayList<Drink> drinkQueue;
    public static SimpleObserverManager observers;
    private static HTTPGETJSONObject requestThread;

    private static ArrayList<Drink> drinkQueue() {
        if (observers.timeSinceUpdate() > PrefsManager.getMaxDrinkQueueAge())
            updateDrinkQueue();

        return drinkQueue;
    }


    // Initializer
    static {
        Log.d(TAG, "static initializer: init ing");

        observers = new SimpleObserverManager();
        drinkQueue = new ArrayList<Drink>();
        requestThread = null;

        // Observe machine selection - if it changes we need to update
        PrefsManager.getCurrentSelectedMachineID().observeForever(s -> updateDrinkQueue());
    }


    /**
     * Begin network request to update drink queue
     */
    public static void updateDrinkQueue() {
        // Ensure dont already have active thread
        if (requestThread != null)
            return;

        Log.d(TAG, "updateDrinkQueue: updating");

        // Setup URL
        URL url = null;
        try {
            url = PrefsManager.getURLBase().resolve(Constants.URL_PATH_DRINK).toURL();
        } catch (MalformedURLException e) {
            Log.d(TAG, "updateDrinkQueue: MALFORMED URL. This is hardcoded so should not happen");
            Log.d(TAG, String.format(
                    "updateDrinkQueue: URLBASE = %s, path = %s",
                    PrefsManager.getURLBase(),
                    Constants.URL_PATH_DRINK
            ));
        }

        // Setup return data and status observer
        MutableLiveData<RequestStatus> requestStatus = new MutableLiveData<RequestStatus>();
        requestStatus.observeForever(new Observer<RequestStatus>() {
            @Override
            public void onChanged(RequestStatus s) { onDrinkQueueUpdate(s); }
        });

        // Start thread, passing return data observer
        requestThread = new HTTPGETJSONObject(url, requestStatus);
        requestThread.start();
    }

    /**
     * Called when drink queue request completes.
     * must reconcile data received with local copy of drink queue
     * remove completed drinks, add new drinks, update existing ones
     */
    private static void onDrinkQueueUpdate(RequestStatus s) {
        Log.d(TAG, "onDrinkQueueUpdate: update received: " + s);

        if (s != RequestStatus.DONE_SUCCESS) {
            Log.d(TAG, "onDrinkQueueUpdate: request failed. doing nothing");
            nullThread();
            return;
        }

        try {
            ArrayList<Drink> newQueue = new ArrayList<Drink>();
            JSONObject dqObj = requestThread.getJsonObject();

            int qlen = dqObj.getJSONArray(Constants.QUEUE).length();
            for (int i = 0; i < qlen; i++) {
                // Add each drink from the server to the new Queue
                Drink drink = new Drink(dqObj.getJSONArray(Constants.QUEUE).getJSONObject(i));
                drink.inMachineQueue = true;  // We got it from the server so it must be
                newQueue.add(drink);

                // remove each drink from the old Queue
                //TODO dont dlete old drinks until we navigate away from this page
                int indx = drinkQueue.indexOf(getDrink(drink.drinkID));
                if (indx != -1)
                    drinkQueue.remove(indx);
            }

            // Add any un-synced drinks in old queue to end of new queue
            for (Drink drink: drinkQueue) {
                if (!drink.inMachineQueue)
                    newQueue.add(drink);
            }

            drinkQueue = newQueue;

            Log.d(TAG, "onDrinkQueueUpdate: drink queue json loaded successfully");
        } catch (JSONException e) {
            Log.d(TAG, "onDrinkQueueUpdate: JSON error loading data from server: " + e.getLocalizedMessage());
        }
        finally {
            nullThread();
        }

        observers.update();
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


    /**
     * Post a drink request. Adds drink to local queue and attempts to post to server.
     */
    public static void postDrink(Recipe recipe, String userID) {
        Log.d(TAG, "postDrink: posting drink for " + userID);

        // Make the drink object
        Drink drink = new Drink();
        drink.drinkID = UUID.randomUUID().toString();
        drink.userID = userID;
        drink.requestTime = Constants.getCurrentTime();
        drink.recipe = recipe;

        drink.progress = 0;
        drink.isStarted = false;
        drink.inMachineQueue = false;
        drink.machineReady = false;

        // Add drink to local Queue
        drinkQueue.add(drink);
        observers.update();

        // Make request body and post it
        String reqBody = makeDrinkRequestBody(drink);
        MutableLiveData<String> returnData = new MutableLiveData<String>();
        returnData.observeForever(DrinkQueueManager::onDrinkPostCallback);

        try {
            NetworkPOSTRequest thread = new NetworkPOSTRequest(
                    PrefsManager.getURLBase().resolve(Constants.URL_PATH_DRINK).toURL(),
                    reqBody,
                    returnData
            );
            thread.start();
        } catch (MalformedURLException e) {
            Log.d(TAG, "postDrink: MALFORMED URL. This is hardcoded so should not happen");
        }
    }


    /**
     * called when drink post request calls back. updates local queue/drink data. hopefully all is well
     * @param s the data returned from the server
     */
    private static void onDrinkPostCallback(String s) {
        try {
            JSONObject returnData = new JSONObject(s);
            Drink drink = getDrink(returnData.getString(Constants.DRINK_ID));
            if (drink == null) {
                Log.d(TAG, "onDrinkPostCallback: received callback for drink we dont have. Check ur shit out");
                return;
            }

            drink.inMachineQueue = returnData.getBoolean(Constants.ADDED_TO_QUEUE);
            drink.machineReady = returnData.getBoolean(Constants.READY);
            Log.d(TAG, "onDrinkPostCallback: drink posted, back from machine: in queue: " + drink.inMachineQueue + ", can make: " + drink.machineReady);

            if (returnData.has(Constants.QUEUE_POS)) {
                drinkQueue.remove(drink);
                drinkQueue.add(
                        returnData.getInt(Constants.QUEUE_POS),
                        drink
                );
            }

            observers.update();

        } catch (JSONException e) {
            Log.d(TAG, "onDrinkPostCallback: json error parsing data from server: " + e.getLocalizedMessage());
        }
    }


    /**
     * takes recipe and builds a post request body. request contains UUID, timestamp, recipe. in JSON format
     *
     * @param drink the drink object
     * @return JSON object
     */
    public static String makeDrinkRequestBody(Drink drink) {
        JSONObject requestBody = new JSONObject();

        try {
            requestBody.put(Constants.DRINK_ID, drink.drinkID);
            requestBody.put(Constants.USER_ID, drink.userID);
            requestBody.put(Constants.TIMESTAMP, Constants.getTimestamp(drink.requestTime));
            requestBody.put(Constants.RECIPE, RecipeManager.jsonifyRecipe(drink.recipe));

            return requestBody.toString(2);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d(TAG, "createDrinkRequest: failed to build drink request body: " + e.getLocalizedMessage());
        }

        return null;
    }


    public static int getQueueLength() {
        return drinkQueue().size();
    }


    public static Drink getDrink(int index) {
        return drinkQueue().get(index);
    }

    public static Drink getDrink(String id) {
        for (Drink drink: drinkQueue()) {
            if (drink.drinkID.equals(id))
                return drink;
        }

        return null;
    }
}
