package com.example.autobartender.ui.prefs;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.preference.DialogPreference;

import com.example.autobartender.R;
import com.example.autobartender.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

public class AddMachinePreference extends DialogPreference {
    private static final String TAG = AddMachinePreference.class.getSimpleName();

    JSONObject machine;

    public AddMachinePreference(Context context) {
        super(context);
    }

    public String getValues() {
        return machine.toString();
    }

    public void setValues(JSONObject machine) {
        this.machine = machine;
        persistString(machine.toString());
    }

    @Override
    public int getDialogLayoutResource() {
        return R.layout.pref_dialog_add_machine;
    }
}
