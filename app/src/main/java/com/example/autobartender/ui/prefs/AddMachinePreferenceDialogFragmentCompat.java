package com.example.autobartender.ui.prefs;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.preference.DialogPreference;
import androidx.preference.PreferenceDialogFragmentCompat;

import com.example.autobartender.R;
import com.example.autobartender.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

public class AddMachinePreferenceDialogFragmentCompat extends PreferenceDialogFragmentCompat {
    private static final String TAG = AddMachinePreferenceDialogFragmentCompat.class.getSimpleName();

    private EditText etMachineID;
    private EditText etMachineAddr;
    private EditText etMachineName;

    public static AddMachinePreferenceDialogFragmentCompat newInstance(String key) {
        final AddMachinePreferenceDialogFragmentCompat fragment = new AddMachinePreferenceDialogFragmentCompat();
        final Bundle b = new Bundle(1);
        b.putString(ARG_KEY, key);
        fragment.setArguments(b);

        return fragment;
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);

        etMachineID = view.findViewById(R.id.et_machine_id);
        etMachineAddr = view.findViewById(R.id.et_machine_addr);
        etMachineName = view.findViewById(R.id.et_machine_name);

    }

    @Override
    public void onDialogClosed(boolean positiveResult) {
        DialogPreference pref = getPreference();
        if (pref instanceof AddMachinePreference) {
            AddMachinePreference preference = (AddMachinePreference) pref;

            JSONObject machine = new JSONObject();
            try {
                machine.put(Constants.MACHINE_ID, etMachineID.getText().toString());
                machine.put(Constants.MACHINE_ADDR, etMachineAddr.getText().toString());
                machine.put(Constants.MACHINE_NAME, etMachineName.getText().toString());
            } catch (JSONException e) {
                Log.d(TAG, "onDialogClosed: error putting value in json idk lol" + e.getLocalizedMessage());
            }
            preference.setValues(machine);
        }
    }
}
