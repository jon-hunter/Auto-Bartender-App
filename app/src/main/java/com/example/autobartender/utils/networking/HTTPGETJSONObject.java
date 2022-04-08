package com.example.autobartender.utils.networking;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.nio.charset.StandardCharsets;

public class HTTPGETJSONObject extends HTTPGETBase {
    private static final String TAG = HTTPGETJSONObject.class.getSimpleName();

    private JSONObject jsonObject;

    public HTTPGETJSONObject(URL url, MutableLiveData<RequestStatus> requestStatus) {
        super(url, requestStatus);
        this.jsonObject = null;
    }

    @Override
    protected RequestStatus parseReturnData() {
        try {
            this.jsonObject = new JSONObject(new String(this.returnData, StandardCharsets.UTF_8));
            return RequestStatus.DONE_SUCCESS;
        } catch (JSONException e) {
            Log.d(TAG, "parseReturnData: json could not be parsed: " + e.getLocalizedMessage());
            return RequestStatus.DONE_FAIL;
        } catch (NullPointerException e) {
            Log.d(TAG, "parseReturnData: nullpointerexception: " + e.getLocalizedMessage());
            return RequestStatus.DONE_FAIL;
        }
    }


    public JSONObject getJsonObject() {
        return jsonObject;
    }
}
