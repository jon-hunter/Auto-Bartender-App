package com.example.autobartender.utils;

import androidx.annotation.NonNull;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

public class SimpleObserverManager {


    private ArrayList<SimpleObserver> observers;
    private Date lastUpdateTm;

    public SimpleObserverManager() {
        this.observers = new ArrayList<SimpleObserver>();
        this.lastUpdateTm = new Date();
    }


    public long timeSinceUpdate() {
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
        this.lastUpdateTm = new Date();
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

