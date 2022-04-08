package com.example.autobartender.ui.prefs;

import android.os.Bundle;

import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;
import androidx.preference.SeekBarPreference;

import com.example.autobartender.R;
import com.example.autobartender.utils.Constants;

public class SettingsFragment extends PreferenceFragmentCompat {
    private static final String TAG = SettingsFragment.class.getName();

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        PreferenceScreen preferenceScreen = getPreferenceManager().createPreferenceScreen(getContext());

        // Setup a bunch of Preferences objects.
        // Done here instead of in xml so that I can pull values from Constants
        PreferenceCategory userPrefsCategory = new PreferenceCategory(getContext());
        userPrefsCategory.setTitle(R.string.pref_category_user_title);
        preferenceScreen.addPreference(userPrefsCategory);

        EditTextPreference userIDPref = new EditTextPreference(getContext());
        userIDPref.setKey(Constants.PREFS_USERID);
        userIDPref.setTitle(R.string.pref_userID_title);
        userIDPref.setSummary(R.string.pref_userID_summary);
        userPrefsCategory.addPreference(userIDPref);


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

}