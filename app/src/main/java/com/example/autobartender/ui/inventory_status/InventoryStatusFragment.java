package com.example.autobartender.ui.inventory_status;

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
import android.widget.LinearLayout;

import com.example.autobartender.R;
import com.example.autobartender.ui.helpers.MachineInfo;
import com.example.autobartender.utils.InventoryManager;
import com.example.autobartender.utils.SimpleObserverManager;

public class InventoryStatusFragment extends Fragment implements SimpleObserverManager.SimpleObserver {
    private final String TAG = InventoryStatusFragment.class.getName();

    private MachineInfo mi;
    private RecyclerView rv;
    private InventoryStatus_RVA rva;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_inventory_status, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        Log.d(TAG, "onViewCreated: created");

        // Register as inventory observer
        InventoryManager.observers.register(this);

        // init machine info
        LinearLayout temp = view.findViewById(R.id.include_machine_info);
        mi = new MachineInfo(temp);
        mi.init(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO handle refresh btn click
            }
        }, getContext());

        // init the recyclerview
        rv = view.findViewById(R.id.rv_ingredients);
        rv.setLayoutManager(new LinearLayoutManager(view.getContext()));
        rva = new InventoryStatus_RVA(view.getContext());
        rv.setAdapter(rva);
    }

    public void onDestroy() {
        super.onDestroy();
        InventoryManager.observers.deregister(this);
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