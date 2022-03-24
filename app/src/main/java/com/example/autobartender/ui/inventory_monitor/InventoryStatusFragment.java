package com.example.autobartender.ui.inventory_monitor;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.autobartender.R;
import com.example.autobartender.utils.GetInventoryStatus;

import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;

public class InventoryStatusFragment extends Fragment implements View.OnClickListener {
    private final String TAG = "InventoryStatusFragment";

    private GetInventoryStatus vm;
    private RecyclerView rv;
    private InventoryStatus_RVA rva;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_inventory_status, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        Log.d(TAG, "onViewCreated: created");

        vm = GetInventoryStatus.getInstance(this.getContext());

        // init the recyclerview
        rv = getActivity().findViewById(R.id.rvIngredients);
        rv.setLayoutManager(new LinearLayoutManager(view.getContext()));
        rva = new InventoryStatus_RVA(view.getContext());
        rv.setAdapter(rva);

        // Configure livedata Observers
        final Observer<JSONObject> ingStatsJsonObserver = new Observer<JSONObject>() {
            public void onChanged(JSONObject s) { IngStatsFinishedCallback(); }
        };
        vm.getIngStatsJson().observe(getViewLifecycleOwner(), ingStatsJsonObserver);

        final Observer<String> resultInfoObserver = new Observer<String>() {
            public void onChanged(String s) { displayBadResult(s); }  // ResultInfo only holds bad things. hope it doesnt get updated
        };
        vm.getResultInfo().observe(getViewLifecycleOwner(), resultInfoObserver);

        // Make network call to populate data
        getFullInventory();
    }

    /**
     * Called when the thread to get the pet json finishes successfully.
     * This is called manually instead of using an observer, to give me more control
     * over handling of good and bad results.
     */
    public void IngStatsFinishedCallback() {
        Log.d(TAG, "IngStatsFinishedCallback: thread to get pet json has completed and called back");
        Log.d(TAG, "IngStatsFinishedCallback: " + vm.getIngStatsJson().getValue());

        // Populate spinner by telling it to repopulate itself lol
        rva.notifyDataSetChanged();
    }

    private void displayBadResult(String s) {
        // Called when bad things have happened. show user the info.
        Log.d(TAG, "displayBadResult: bad result: " + s);
        Toast.makeText(this.getContext(), s, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onClick(View v) {
        Log.d(TAG, "onClick: Click handling...");
        switch (v.getId()) {
            case R.id.refresh_btn:
                getFullInventory();
                break;
        }
    }


    /**
     * Checks network connection. If good, makes call to request inventory
     */
    private void getFullInventory() {
        Log.d(TAG, "getFullInventory: checking network connection");
//        vm.hasNetworkConnection = hasInternetConnection();

        if (true) {//(vm.hasNetworkConnection) {
            // have internet, start thread to get pets json
            Log.d(TAG, "getFullInventory: have network. calling requestFetchFullInventory");
            vm.requestFetchFullInventory(this.getContext());
        }
        else {
            // Advise user
            Log.d(TAG, "getFullInventory: oops no network");
//            displayBadResult(getString(R.string.NO_INTERNET_MSG));
        }
    }

}