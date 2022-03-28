package com.example.autobartender.utils;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class NetworkGETRequest extends Thread {
    private static final String TAG = "NetworkGETRequest";

    private final URL url;
    private final MutableLiveData<String> returnData;
    public int statusCode;


    public NetworkGETRequest(URL url, MutableLiveData<String> returnData) {
        this.url = url;
        this.returnData = returnData;
    }


    public void run() {
        Log.d(TAG, "run: making get request, URL: " + this.url);

        try {
            // Setup connection
            HttpURLConnection connection = (HttpURLConnection) this.url.openConnection();
            connection.setRequestMethod("GET");
            connection.setReadTimeout(Constants.TIMEOUT);
            connection.setConnectTimeout(Constants.TIMEOUT);
            connection.setRequestProperty("Accept-Charset", "UTF-8");

            // Do network IO
            BufferedReader in = null;
            try {
                connection.connect();

                // ensure code is right
                statusCode = connection.getResponseCode();
                if (statusCode / 100 != 2) {
                    Log.d(TAG, "run: failed. Not updating data");
                    return;
                }

                // get output data
                in = new BufferedReader(new InputStreamReader(connection.getInputStream()), Constants.DEFAULTBUFFERSIZE);
                String line;
                StringBuilder sb = new StringBuilder();
                while ((line = in.readLine()) != null) {
                    sb.append(line);
                }

                // Post requested data
                this.returnData.postValue(sb.toString());
                Log.d(TAG, "run: Successfully posted JSOn value");
            } finally {
                // close resource no matter what exception occurs
                if(in != null)
                    in.close();
                connection.disconnect();
            }
        } catch (IOException e) {
            Log.d(TAG, "run: IOException for GET request " + this.url + " - " + e.getLocalizedMessage());
        }
    }
}