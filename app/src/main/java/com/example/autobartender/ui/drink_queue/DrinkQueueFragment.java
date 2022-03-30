package com.example.autobartender.ui.drink_queue;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.autobartender.R;
import com.example.autobartender.utils.DrinkQueueManager;
import com.example.autobartender.utils.SimpleObserverManager;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DrinkQueueFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DrinkQueueFragment extends Fragment implements SimpleObserverManager.SimpleObserver {
    private static final String TAG  = "DrinkMonitorFragment";

    private RecyclerView rv;
    private DrinkQueue_RVA rva;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_drink_monitor, container, false);
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        Log.d(TAG, "onViewCreated: created");

        rv = getActivity().findViewById(R.id.rv_drink_queue);
        rv.setLayoutManager(new LinearLayoutManager(view.getContext()));
        rva = new DrinkQueue_RVA(view.getContext());
        rv.setAdapter(rva);

        // Make network call to populate data
        DrinkQueueManager.observers.register(this);
        DrinkQueueManager.updateDrinkQueue();
    }

    public void onDestroy() {
        super.onDestroy();
        DrinkQueueManager.observers.deregister(this);
    }


    public void onUpdate() {
        Log.d(TAG, "onUpdate: drink queue update callback'd");

        // Populate spinner by telling it to repopulate itself lol
        rva.notifyDataSetChanged();
    }




}