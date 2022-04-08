package com.example.autobartender.utils.networking;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.autobartender.utils.Constants;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class NetworkPOSTRequest extends Thread{
    private static final String TAG = NetworkPOSTRequest.class.getName();

    private final URL url;
    private final String requestBody;
    private final MutableLiveData<String> returnData;
    public int statusCode;

    public MutableLiveData<String> getReturnData() {
        return returnData;
    }


    public NetworkPOSTRequest(URL url, String reqBody, MutableLiveData<String> returnData) {
        this.url = url;
        this.requestBody = reqBody;
        this.returnData = returnData;
    }


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
            }

            // build response string
            StringBuilder responseBuilder = new StringBuilder();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                String responseLine = null;
                while ((responseLine = br.readLine()) != null) {
                    responseBuilder.append(responseLine.trim());
                }
                Log.d(TAG, "run: response: " + responseBuilder.toString());
                returnData.postValue(responseBuilder.toString());
            }

        } catch (IOException e) {
            Log.d(TAG, "run: error: " + e.getLocalizedMessage());
        } finally {
            if (connection != null)
                connection.disconnect();
        }
        Log.d(TAG, "run: networking thread finished");
    }
}