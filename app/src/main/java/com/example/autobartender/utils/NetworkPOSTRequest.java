package com.example.autobartender.utils;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

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

public class NetworkPOSTRequest extends Thread{
    private static final String TAG = "NetworkPOSTRequest";

    private final URL url;
    private final String requestBody;
    private final MutableLiveData<JSONObject> returnData;
    public int statusCode;

    public MutableLiveData<JSONObject> getReturnData() {
        return returnData;
    }


    public NetworkPOSTRequest(URL url, String reqBody, MutableLiveData<JSONObject> returnData) {
        this.url = url;
        this.requestBody = reqBody;
        this.returnData = returnData;
    }


//    public static NetworkPOSTRequest requestDrink() {
//        // Add appropriate path to end of URL and start thread
//        try {
//            URL url = Constants.getURLBase().resolve(Constants.URL_PATH_DRINK).toURL();
//
//            DrinkRequestThread sendJSONThread = new DrinkRequestThread(
//                    url,
//                    createDrinkRequest(recipeManager.getSelectedRecipeID())
//            );
//            sendJSONThread.start();
//            return sendJSONThread;
//        } catch (MalformedURLException e) {
//            Log.d(TAG, "requestDrink: MALFORMED URL. This is hardcoded so should not happen");
//            Log.d(TAG, String.format(
//                    "requestDrink: URLBASE = %s, path = %s",
//                    Constants.getURLBase(),
//                    Constants.URL_PATH_DRINK
//            ));
//        }
//        return null;
//    }


    public void run() {
        HttpURLConnection connection = null;
        try {
            Log.d(TAG, "run: sending drink recipe to URL: " + url);

            // Setup connection
            connection = (HttpURLConnection) this.url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json; utf-8");
            connection.setRequestProperty("Connection", "close");
            connection.setRequestProperty("Accept-Charset", "UTF-8");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("Content-Length", Integer.toString(requestBody.length()));
            connection.setReadTimeout(Constants.TIMEOUT);
            connection.setConnectTimeout(Constants.TIMEOUT);

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