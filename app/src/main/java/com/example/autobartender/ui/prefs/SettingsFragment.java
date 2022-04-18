package com.example.autobartender.ui.prefs;

import android.os.Bundle;
import android.util.Log;

import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;
import androidx.preference.SeekBarPreference;

import com.example.autobartender.R;
import com.example.autobartender.utils.Constants;
import com.example.autobartender.utils.PrefsManager;

import org.json.JSONArray;
import org.json.JSONException;

public class SettingsFragment extends PreferenceFragmentCompat {
    private static final String TAG = SettingsFragment.class.getName();

    /*
     * Setup a bunch of Preferences objects.
     * Done here instead of in xml so that I can pull values from Constants
     */
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        PreferenceScreen preferenceScreen = getPreferenceManager().createPreferenceScreen(getContext());

        //TODO observe knownmachines list, on update, set values as appropriate

        // User Prefs
        PreferenceCategory userPrefsCategory = new PreferenceCategory(getContext());
        userPrefsCategory.setTitle(R.string.pref_category_user_title);
        preferenceScreen.addPreference(userPrefsCategory);

        EditTextPreference userIDPref = new EditTextPreference(getContext());
        userIDPref.setKey(Constants.PREFS_USERID);
        userIDPref.setTitle(R.string.pref_userID_title);
        userIDPref.setSummary(R.string.pref_userID_summary);
        userPrefsCategory.addPreference(userIDPref);

        // Machine Prefs
        PreferenceCategory machinePrefsCategory = new PreferenceCategory(getContext());
        machinePrefsCategory.setTitle(R.string.pref_category_machine_title);
        preferenceScreen.addPreference(machinePrefsCategory);

        AddMachinePreference addMachinePref = new AddMachinePreference(getContext());
        addMachinePref.setKey(Constants.PREFS_ADD_MACHINE);
        addMachinePref.setTitle(R.string.pref_add_machine_title);
        addMachinePref.setSummary(R.string.pref_add_machine_summary);
        machinePrefsCategory.addPreference(addMachinePref);

        Preference machineIDListerPref = new Preference(getContext());
        machineIDListerPref.setSelectable(false);
        machineIDListerPref.setPersistent(false);
        machineIDListerPref.setTitle(R.string.pref_known_machines_title);
        machineIDListerPref.setSummary(PrefsManager.getMachineListPrintable(
                getString(R.string.pref_known_machines_row_format),
                getString(R.string.pref_known_machines_none)
        ));
        machinePrefsCategory.addPreference(machineIDListerPref);

        ListPreference faveMachinePref = new ListPreference(getContext());
        faveMachinePref.setKey(Constants.PREFS_FAVORITE_MACHINE_ID);
        faveMachinePref.setTitle(R.string.pref_fav_machine_title);
        faveMachinePref.setSummary(R.string.pref_fav_machine_summary);
        faveMachinePref.setEntryValues(PrefsManager.getKnownMachineAttrs(Constants.MACHINE_ID));
        faveMachinePref.setEntries(PrefsManager.getKnownMachineAttrs(Constants.MACHINE_NAME));
        machinePrefsCategory.addPreference(faveMachinePref);

        ListPreference deleteMachinePref = new ListPreference(getContext());
        deleteMachinePref.setKey(Constants.PREFS_DELETE_MACHINE);
        deleteMachinePref.setTitle(R.string.pref_delete_machine_title);
        deleteMachinePref.setSummary(R.string.pref_delete_machine_summary);
        deleteMachinePref.setEntryValues(PrefsManager.getKnownMachineAttrs(Constants.MACHINE_ID));
        deleteMachinePref.setEntries(PrefsManager.getKnownMachineAttrs(Constants.MACHINE_NAME));
        machinePrefsCategory.addPreference(deleteMachinePref);

        // Handling machine list changes: update things that depend on that list
        PrefsManager.getKnownMachines().observeForever(new Observer<JSONArray>() {
            @Override
            public void onChanged(JSONArray jsonArray) {
                machineIDListerPref.setSummary(PrefsManager.getMachineListPrintable(
                        getString(R.string.pref_known_machines_row_format),
                        getString(R.string.pref_known_machines_none)
                ));

                faveMachinePref.setEntryValues(PrefsManager.getKnownMachineAttrs(Constants.MACHINE_ID));
                faveMachinePref.setEntries(PrefsManager.getKnownMachineAttrs(Constants.MACHINE_NAME));

                deleteMachinePref.setEntryValues(PrefsManager.getKnownMachineAttrs(Constants.MACHINE_ID));
                deleteMachinePref.setEntries(PrefsManager.getKnownMachineAttrs(Constants.MACHINE_NAME));
            }
        });

        // Networking Prefs
        PreferenceCategory networkPrefsCategory = new PreferenceCategory(getContext());
        networkPrefsCategory.setTitle(R.string.pref_category_network_title);
        preferenceScreen.addPreference(networkPrefsCategory);

        ListPreference serverURLPref = new ListPreference(getContext());
        serverURLPref.setKey(Constants.PREFS_SERVER_URL);
        serverURLPref.setTitle(R.string.pref_serverURL_title);
        serverURLPref.setSummary(R.string.pref_serverURL_summary);
        serverURLPref.setEntryValues(Constants.NETWORK_URLS);
        serverURLPref.setEntries(R.array.pref_serverURL_entries);
        serverURLPref.setDefaultValue(Constants.URLBASE_DEFAULT);
        networkPrefsCategory.addPreference(serverURLPref);

        ListPreference recipeDBPref = new ListPreference(getContext());
        recipeDBPref.setKey(Constants.PREFS_RECIPE_DB_SOURCE);
        recipeDBPref.setTitle(R.string.pref_recipeDB_title);
        recipeDBPref.setSummary(R.string.pref_recipeDB_summary);
        recipeDBPref.setEntryValues(Constants.RECIPE_DB_OPTIONS);
        recipeDBPref.setEntries(R.array.pref_recipeDB_entries);
        networkPrefsCategory.addPreference(recipeDBPref);

        SeekBarPreference invRefreshTMPref = new SeekBarPreference(getContext());
        invRefreshTMPref.setKey(Constants.PREFS_INV_REFRESH_TM);
        invRefreshTMPref.setTitle(R.string.pref_inv_refresh_tm_title);
        invRefreshTMPref.setSummary(R.string.pref_inv_refresh_tm_summary);
        invRefreshTMPref.setMin(Constants.MIN_MAX_AGE_INVENTORY);
        invRefreshTMPref.setMax(Constants.MAX_MAX_AGE_INVENTORY);
        invRefreshTMPref.setDefaultValue(Constants.DEFAULT_MAX_AGE_INVENTORY);
        networkPrefsCategory.addPreference(invRefreshTMPref);

        SeekBarPreference dqRefreshTMPref = new SeekBarPreference(getContext());
        dqRefreshTMPref.setKey(Constants.PREFS_DQ_REFRESH_TM);
        dqRefreshTMPref.setTitle(R.string.pref_dq_refresh_tm_title);
        dqRefreshTMPref.setSummary(R.string.pref_dq_refresh_tm_summary);
        dqRefreshTMPref.setMin(Constants.MIN_MAX_AGE_DRINK_QUEUE);
        dqRefreshTMPref.setMax(Constants.MAX_MAX_AGE_DRINK_QUEUE);
        dqRefreshTMPref.setDefaultValue(Constants.DEFAULT_MAX_AGE_DRINK_QUEUE);
        networkPrefsCategory.addPreference(dqRefreshTMPref);


        setPreferenceScreen(preferenceScreen);
    }

    @Override
    public void onDisplayPreferenceDialog(Preference preference) {
        if (preference instanceof AddMachinePreference) {
            Log.d(TAG, "onDisplayPreferenceDialog: preference.getKey" + preference.getKey());
            DialogFragment df = AddMachinePreferenceDialogFragmentCompat.newInstance(preference.getKey());
            df.setTargetFragment(this, 0);
            df.show(this.getFragmentManager(), "android.support.v7.preference.PreferenceFragment.DIALOG");
        }
        else {
            super.onDisplayPreferenceDialog(preference);
        }
    }

}