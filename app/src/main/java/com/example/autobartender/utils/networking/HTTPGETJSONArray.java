package com.example.autobartender.utils.networking;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import org.json.JSONArray;
import org.json.JSONException;

import java.net.URL;
import java.nio.charset.StandardCharsets;

public class HTTPGETJSONArray extends HTTPGETBase {
    private static final String TAG = HTTPGETJSONArray.class.getSimpleName();

    private JSONArray jsonArray;

    public HTTPGETJSONArray(URL url, MutableLiveData<RequestStatus> requestStatus) {
        super(url, requestStatus);
        this.jsonArray = null;
    }

    @Override
    protected RequestStatus parseReturnData() {
        try {
            this.jsonArray = new JSONArray(new String(this.returnData, StandardCharsets.UTF_8));
            return RequestStatus.DONE_SUCCESS;
        } catch (JSONException e) {
            Log.d(TAG, "parseReturnData: json could not be parsed: " + e.getLocalizedMessage());
            return RequestStatus.DONE_FAIL;
        } catch (NullPointerException e) {
            Log.d(TAG, "parseReturnData: nullpointerexception: " + e.getLocalizedMessage());
            return RequestStatus.DONE_FAIL;
        }
    }


    public JSONArray getJsonArray() {
        return jsonArray;
    }
}
