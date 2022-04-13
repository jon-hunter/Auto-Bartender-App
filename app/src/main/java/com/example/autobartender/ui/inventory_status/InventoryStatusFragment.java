package com.example.autobartender.ui.inventory_status;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.autobartender.R;
import com.example.autobartender.ui.layout_helpers_lol.NoNetworkInfo;
import com.example.autobartender.utils.InventoryManager;
import com.example.autobartender.utils.SimpleObserverManager;
import com.example.autobartender.utils.networking.NetworkStatusManager;
import com.example.autobartender.utils.networking.NetworkStatusManager.Status;

public class InventoryStatusFragment extends Fragment implements SimpleObserverManager.SimpleObserver {
    private final String TAG = InventoryStatusFragment.class.getName();

    private RecyclerView rv;
    private InventoryStatus_RVA rva;
    private NoNetworkInfo networkInfo;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_inventory_status, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        Log.d(TAG, "onViewCreated: created");

        // Register as inventory observer
        InventoryManager.observers.register(this);

        // Initialize network status helper
        this.networkInfo = new NoNetworkInfo(getActivity().findViewById(R.id.include_no_network));
        this.networkInfo.hide();

        // init the recyclerview
        rv = getActivity().findViewById(R.id.rv_ingredients);
        rv.setLayoutManager(new LinearLayoutManager(view.getContext()));
        rva = new InventoryStatus_RVA(view.getContext());
        rv.setAdapter(rva);

        NetworkStatusManager.getMachineConnectedStatus().observeForever(new Observer<Status>() {
            @Override
            public void onChanged(Status status) {
                if (status == Status.ALL_GOOD) {
                    networkInfo.hide();
                    rv.setVisibility(View.VISIBLE);
                    return;
                }

                networkInfo.init(getMsg(status), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        NetworkStatusManager.updateMachine(getContext());
                    }
                });
                networkInfo.show();
                rv.setVisibility(View.GONE);
            }
        });
        NetworkStatusManager.updateMachine(getContext());
    }

    public void onDestroy() {
        super.onDestroy();
        InventoryManager.observers.deregister(this);
    }

    private String getMsg(Status status) {
        switch (status) {
            case TIMEOUT:
                return getString(R.string.network_error_machine_timeout);
            case FOUR_OH_FOUR:
                return getString(R.string.network_error_machine_404);
            case NO_NETWORK:
                return getString(R.string.network_error_no_network);
            case AIRPLANE_MODE:
                return getString(R.string.network_error_airplane_mode);
            case UNKNOWN_ERROR:
            case UNKNOWN:
            default:
                return getString(R.string.network_error_machine_unknown);
        }
    }


    /**
     * Called when the thread to get the pet json finishes successfully.
     * This is called manually instead of using an observer, to give me more control
     * over handling of good and bad results.
     */
    public void onUpdate() {
        Log.d(TAG, "onUpdate: inventory update callback'd");

        // Populate spinner by telling it to repopulate itself lol
        rva.notifyDataSetChanged();
    }

}