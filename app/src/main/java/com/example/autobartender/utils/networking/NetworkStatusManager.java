package com.example.autobartender.utils.networking;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.example.autobartender.utils.Constants;
import com.example.autobartender.utils.PrefsManager;
import com.example.autobartender.utils.networking.HTTPGETBase.RequestStatus;

import org.json.JSONException;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Holds some data on the status of various network connections
 * maintains livedata so that activities and such can have UI updated on network status changes
 */
public class NetworkStatusManager {
    private static final String TAG = NetworkStatusManager.class.getSimpleName();

    public enum Status {
        ALL_GOOD,  // can connect, send and receive
        TIMEOUT,  // connection timed out - indicates server side issue
        FOUR_OH_FOUR,  // got 404 - indicates client side issue
        NO_NETWORK,  // cannot connect to network
        AIRPLANE_MODE,  // user does not have wifi on
        UNKNOWN_ERROR,
        UNKNOWN
    }

    private static final MutableLiveData<Status> machineConnectedStatus;
    public static MutableLiveData<Status> getMachineConnectedStatus() {
        return machineConnectedStatus;
    }
    private static HTTPGETJSONObject machinePing;

    private static final MutableLiveData<Status> remoteResourceStatus;
    public static MutableLiveData<Status> getRemoteResourceStatus() {
        return remoteResourceStatus;
    }

    static {
        machineConnectedStatus = new MutableLiveData<Status>(Status.UNKNOWN);
        remoteResourceStatus = new MutableLiveData<Status>(Status.UNKNOWN);
    }


    /**
     * Pings machine, posts value indicating connection status
     */
    public static void updateMachine(Context ctx) {
        if (update(ctx))
            return;

        URL url = null;
        try {
            url = PrefsManager.getURLBase().resolve(Constants.URL_PATH_PING).toURL();
        } catch (MalformedURLException e) {
            Log.d(TAG, "updateMachine: hardcoded url broek dumbass " + e.getLocalizedMessage());
        }
        MutableLiveData<RequestStatus> status = new MutableLiveData<RequestStatus>();
        status.observeForever(new Observer<RequestStatus>() {
            @Override
            public void onChanged(RequestStatus requestStatus) {
                switch (requestStatus) {
                    case TIMEOUT:
                        machineConnectedStatus.setValue(Status.TIMEOUT);
                        break;
                    case FOUR_O_FOUR:
                        machineConnectedStatus.setValue(Status.FOUR_OH_FOUR);
                        break;
                    case DONE_FAIL:
                        machineConnectedStatus.setValue(Status.UNKNOWN_ERROR);
                        break;
                    case DONE_SUCCESS:
                        try {
                            machinePing.getJsonObject().getBoolean(Constants.PING);  // lazy check if json is valid. means that machine works lol
                            machineConnectedStatus.setValue(Status.ALL_GOOD);
                        } catch (JSONException e) {
                            machineConnectedStatus.setValue(Status.FOUR_OH_FOUR);
                        }
                }
                machinePing = null;
            }
        });

        machinePing = new HTTPGETJSONObject(url, status);
        machinePing.start();
    }


    public static void updateRemoteResources(Context ctx) {
        if (update(ctx))
            return;

        //TODO implement lol
    }


    /**
     * checks if network is available.
     * @param ctx context
     * @return true if status was updated. false otherwise
     */
    private static boolean update(Context ctx) {
        WifiManager wifi = (WifiManager) ctx.getSystemService(Context.WIFI_SERVICE);
        if (!wifi.isWifiEnabled()) {
            machineConnectedStatus.setValue(Status.AIRPLANE_MODE);
            remoteResourceStatus.setValue(Status.AIRPLANE_MODE);
            return true;
        }
        NetworkInfo info = ((ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (info == null || !info.isConnected()) {
            machineConnectedStatus.setValue(Status.NO_NETWORK);
            machineConnectedStatus.setValue(Status.NO_NETWORK);
            return true;
        }

        return false;
    }
}
