package com.example.autobartender.utils.networking;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import java.net.URL;


/**
 * subclass to parse output as an image
 */
public class HTTPGETImage extends HTTPGETBase {
    private static final String TAG = HTTPGETImage.class.getSimpleName();

    private Bitmap image;

    public HTTPGETImage(URL url, MutableLiveData<RequestStatus> requestStatus) {
        super(url, requestStatus);
        this.image = null;
    }


    @Override
    protected RequestStatus parseReturnData() {
        try {
            Log.d(TAG, "parseReturnData: parsing...");
            this.image = BitmapFactory.decodeByteArray(this.returnData, 0, this.returnData.length);
            Log.d(TAG, "parseReturnData: not failed?? returning success");
            return RequestStatus.DONE_SUCCESS;
        } catch (NullPointerException e) {
            Log.d(TAG, "parseReturnData: nullPointerexception decoding image. returning fail. " + e.getLocalizedMessage());
            return RequestStatus.DONE_FAIL;
        }
    }


    public Bitmap getImage() {
        return image;
    }
}
