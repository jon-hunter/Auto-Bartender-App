package com.example.autobartender.ui.helpers;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.lifecycle.Observer;

import com.example.autobartender.R;
import com.example.autobartender.utils.Constants;
import com.example.autobartender.utils.PrefsManager;

import org.json.JSONArray;

/**
 * manages the cards at the top of drink queue and inventory manager. connects to machine.
 */
public class MachineInfo {
    public static final String TAG = MachineInfo.class.getSimpleName();

    // All the UI elements
    public LinearLayout rootView;
    public Spinner spinnerMachineName;
    public TextView tvConnectionStatus;
    public Button btnRefresh;

    public MachineInfo(LinearLayout rootView) {
        this.rootView = rootView;
        this.spinnerMachineName = rootView.findViewById(R.id.spinner_machine);
        this.tvConnectionStatus = rootView.findViewById(R.id.tv_connection_status);
        this.btnRefresh = rootView.findViewById(R.id.btn_refresh);
    }

    public void init(View.OnClickListener refreshHandler, Context ctx) {
        Log.d(TAG, "init: init ing machine info");

        initSpinner(ctx);
        PrefsManager.getKnownMachines().observeForever(new Observer<JSONArray>() {
            @Override
            public void onChanged(JSONArray jsonArray) {
                initSpinner(rootView.getContext());
            }
        });

        this.btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRefreshBtn();
                refreshHandler.onClick(v);
            }
        });
    }

    private void initSpinner(Context ctx) {// Populate machine spinner
        this.spinnerMachineName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "onItemSelected: new machine selected: " + spinnerMachineName.getSelectedItem().toString());
                PrefsManager.setCurrentSelectedMachineID(
                        PrefsManager.getMachineIDFromName(spinnerMachineName.getSelectedItem().toString())
                );  // This posts a value and triggers updates to whoever listens.
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                ctx,
                R.layout.spinner_item,
                PrefsManager.getKnownMachineAttrs(Constants.MACHINE_NAME)
        );
        this.spinnerMachineName.setAdapter(adapter);

    }

    private void onRefreshBtn() {

    }
}
