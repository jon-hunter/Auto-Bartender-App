package com.example.autobartender.utils;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.autobartender.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.util.Date;
import java.util.UUID;

public class PostDrinkRequest {
    private final String TAG = "PostDrinkRequest";

    private static PostDrinkRequest instance = null;
    private PostDrinkRequest() { }
    private PostDrinkRequest(Context ctx) {
        this.recipeDBManager = RecipeDBManager.getInstance();

        try {
            URL_BASE = new URI(ctx.getString(R.string.URL_BASE));
        } catch (URISyntaxException e) {
            Log.d(TAG, "onCreate: R.string.URLBASE did not make a valid URI. check that out");
        }
    }
    public static PostDrinkRequest getInstance(Context ctx) {
        if (instance == null)
            instance = new PostDrinkRequest(ctx);

        return instance;
    }

    private RecipeDBManager recipeDBManager;
    //TODO centralize URLBASE variable idk
    public URI URL_BASE;  // the directory all the files are in.

    // Livedata getters
//    private MutableLiveData<String> resultInfo = new MutableLiveData<String>();
//    public MutableLiveData<String> getResultInfo() {
//        if (resultInfo == null)
//            resultInfo = new MutableLiveData<String>();
//        return resultInfo;
//    }


    public DrinkRequestThread requestDrink(Context ctx) {
        // Add appropriate path to end of URL and start thread
        try {
            URL url = URL_BASE.resolve(ctx.getString(R.string.URL_PATH_DRINK_REQUEST)).toURL();

            DrinkRequestThread sendJSONThread = new DrinkRequestThread(
                    url,
                    createDrinkRequest(recipeDBManager.getSelectedRecipeID())
            );
            sendJSONThread.start();
            return sendJSONThread;
        } catch (MalformedURLException e) {
            Log.d(TAG, "requestDrink: MALFORMED URL. This is hardcoded so should not happen");
            Log.d(TAG, String.format(
                    "requestDrink: URLBASE = %s, path = %s",
                    URL_BASE,
                    ctx.getString(R.string.URL_PATH_INVENTORY)
            ));
        }
        return null;
    }

    /**
     * takes recipe ID and builds a post request body. request contains UUID, timestamp, recipe. in JSON format
     *
     * @param recipeID which recipe to request
     * @return JSON object
     */
    private String createDrinkRequest(String recipeID) {
        JSONObject rawRecipe = recipeDBManager.getRecipe(recipeID);
        JSONObject requestBody = new JSONObject();

        try {
            //TODO centralize JSON tags
            requestBody.put("DRINK_ID", UUID.randomUUID());
            requestBody.put("USER_ID", "bennettH");
            //TODO UID system
            requestBody.put("TIMESTAMP", DateFormat.getDateTimeInstance().format(new Date()));
            requestBody.put("INGREDIENTS", recipeDBManager.getSelectedRecipe().getValue().getJSONArray(recipeDBManager.INGREDIENTS));

            return requestBody.toString();
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d(TAG, "createDrinkRequest: failed to build drink request body");
        }

        return null;
    }


    public class DrinkRequestThread extends Thread {
        private static final String TAG = "SendRecipeJSON";
        private static final int DEFAULTBUFFERSIZE = 8096;
        private static final int TIMEOUT = 1000; // 1 second
        protected int statusCode = 0;
        private final URL URL;
        private final String requestBody;

        private MutableLiveData<JSONObject> returnData;
        public MutableLiveData<JSONObject> getReturnData() {
            if (returnData == null)
                returnData = new MutableLiveData<JSONObject>();
            return returnData;
        }

        public DrinkRequestThread(URL url, String reqBody) {
            Log.d(TAG, "constructor: Initialized new Thread to send drink recipe: " + url);
            this.URL = url;
            this.requestBody = reqBody;
            this.returnData = new MutableLiveData<JSONObject>();
        }

        public void run() {
            HttpURLConnection connection = null;
            try {
                Log.d(TAG, "run: sending drink recipe to URL: " + URL);

                // Setup connection
                connection = (HttpURLConnection) this.URL.openConnection();
                connection.setDoOutput(true);
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json; utf-8");
                connection.setRequestProperty("Connection", "close");
                connection.setRequestProperty("Accept-Charset", "UTF-8");
                connection.setRequestProperty("Accept", "application/json");
                connection.setRequestProperty("Content-Length", Integer.toString(requestBody.length()));
                connection.setReadTimeout(TIMEOUT);
                connection.setConnectTimeout(TIMEOUT);

                // Write request body
                try (OutputStream os = connection.getOutputStream()) {
                    os.write(requestBody.getBytes(StandardCharsets.UTF_8));
                }

                // ensure response code is right
                statusCode = connection.getResponseCode();
                Log.d(TAG, "run: connection statuscode=" + statusCode);
                if (statusCode / 100 != 2) {
                    Log.d(TAG, "run: error. May not updating data");
//                    resultInfo.postValue("request error, code " + statusCode);
                }
                Log.d(TAG, "run: made it here");


                // build response string
                StringBuilder responseBuilder = new StringBuilder();
                Log.d(TAG, "run: stringbuilder init'd");
                try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                    Log.d(TAG, "run: buffereader initd");
                    String responseLine = null;
                    while ((responseLine = br.readLine()) != null) {
                        Log.d(TAG, "run: in the loop");
                        responseBuilder.append(responseLine.trim());
                    }
                    Log.d(TAG, "run: response: " + responseBuilder.toString());
                    returnData.postValue(new JSONObject(responseBuilder.toString()));

                }

            } catch (IOException | JSONException e) {
                Log.d(TAG, "run: error: " + e.toString());
//                resultInfo.postValue(e.toString());
            } finally {
                if (connection != null)
                    connection.disconnect();
            }
            Log.d(TAG, "run: networking thread finished");
        }
    }
}