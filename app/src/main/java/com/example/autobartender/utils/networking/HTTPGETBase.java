package com.example.autobartender.utils.networking;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.autobartender.utils.Constants;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * Abstract class for getting data via HTTP GET request.
 * Fully implemented function for getting data as a byte[].
 * Fully implemented infrastrcture for notifying observers of success/fail on completion.
 *
 * Subclasses must implement parseReturnData, which parses the data and returns a RequestStatus to indicate whether parsing was successful
 * Subclasses should also store the parsed result so that users can access it.
 */
public abstract class HTTPGETBase extends Thread {
    private static final String TAG = HTTPGETBase.class.getSimpleName();

    public enum RequestStatus {
        IN_PROGRESS,
        DONE_SUCCESS,
        DONE_FAIL
    }


    private final URL url;
    public int statusCode;
    private final MutableLiveData<RequestStatus> requestStatus;
    protected byte[] returnData;


    protected HTTPGETBase(URL url, MutableLiveData<RequestStatus> requestStatus) {
        this.url = url;
        this.requestStatus = requestStatus;
    }


    /**
     * Parses return data to desired format, returns a requestStatus for handling by base class
     * @return whether parsing was successful or not
     */
    protected abstract RequestStatus parseReturnData();


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
            try {
                connection.connect();

                // ensure code is right
                statusCode = connection.getResponseCode();
                if (statusCode / 100 != 2) {
                    Log.d(TAG, "run: failed. Not updating data");
                    requestStatus.postValue(RequestStatus.DONE_FAIL);
                    return;
                }

                // read requested data, save to byte[], and
                try (BufferedInputStream bis = new BufferedInputStream(connection.getInputStream())) {
                    ByteArrayOutputStream baf = new ByteArrayOutputStream(Constants.DEFAULTBUFFERSIZE);
                    int current = 0;
                    while ((current = bis.read()) != -1)
                        baf.write((byte) current);

                    this.returnData = baf.toByteArray();
                } catch (IOException e) {
                    Log.d(TAG, "run: IOException reading bufferedInputStream: " + e.getLocalizedMessage());
                }

                // We have the data successfully as a byte array. let the subclass parse it and determine success
                Log.d(TAG, "run: Successfully got network data, posted DONE value");
                this.requestStatus.postValue(this.parseReturnData());
            } finally {
                connection.disconnect();
            }
        } catch (IOException e) {
            Log.d(TAG, "run: IOException for GET request " + this.url + " - " + e.getLocalizedMessage());
            this.requestStatus.postValue(RequestStatus.DONE_FAIL);
        }
    }
}