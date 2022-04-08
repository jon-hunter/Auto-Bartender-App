package com.example.autobartender.utils;

import android.util.Log;

import androidx.annotation.NonNull;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

public class SimpleObserverManager {
    private static final String TAG = SimpleObserverManager.class.getName();

    private ArrayList<SimpleObserver> observers;
    private Date lastUpdateTm;
    private boolean hasInitUpdate;

    public SimpleObserverManager() {
        this.observers = new ArrayList<SimpleObserver>();
        this.lastUpdateTm = new Date();
        this.hasInitUpdate = false;
    }


    public long timeSinceUpdate() {
        if (!hasInitUpdate)
            return 100000000;  // Shut up
        return new Date().getTime() - this.lastUpdateTm.getTime();
    }


    // Inventory observer stuff
    public void register(SimpleObserver observer) {
        this.observers.add(observer);
    }

    public void deregister(SimpleObserver observer) {
        this.observers.remove(observer);
    }

    public void update() {
        Log.d(TAG, "update: triggering observer update");
        this.lastUpdateTm = new Date();
        this.hasInitUpdate = true;
        for (SimpleObserver obs: this.observers) {
            obs.onUpdate();
        }
    }


    /**
     * Simple interface for managing callbacks on inventory/drink queue update
     */
    public interface SimpleObserver {
        void onUpdate();
    }
}

