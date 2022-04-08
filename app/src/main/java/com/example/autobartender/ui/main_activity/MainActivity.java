package com.example.autobartender.ui.main_activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;


import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;

import com.example.autobartender.ui.drink_queue.DrinkQueueFragment;
import com.example.autobartender.R;
import com.example.autobartender.databinding.ActivityMainBinding;
import com.example.autobartender.ui.inventory_status.InventoryStatusFragment;
import com.example.autobartender.ui.prefs.SettingsActivity;
import com.example.autobartender.ui.recipe_editor.RecipeEditorActivity;
import com.example.autobartender.ui.recipe_list.RecipeListFragment;
import com.example.autobartender.utils.Constants;
import com.example.autobartender.utils.DrinkQueueManager;
import com.example.autobartender.utils.InventoryManager;
import com.example.autobartender.utils.PrefsManager;
import com.example.autobartender.utils.RecipeManager;

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener{
    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        com.example.autobartender.databinding.ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Setup fragments
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, new InventoryStatusFragment());
        ft.commit();

        // Setup preferences stuff
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        settings.registerOnSharedPreferenceChangeListener(this);
        PrefsManager.initPrefsManager(settings);

        // Set settings button
        findViewById(R.id.btn_settings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { launchSettingsActivity(); }
        });

        // Setup the Recipe Manager
        RecipeManager.loadRemote();
        RecipeManager.loadLocalRecipes(getSharedPreferences(Constants.LOCAL_RECIPE_DB_SHAREDPREFS, MODE_PRIVATE));
    }


    //Navigation functions. These are event handlers (click or otherwise), either launching an activity or swapping out the main fragment view
    public void launchRecipeListFragment(View v) {
        Log.d(TAG, "launchRecipeListFragment: launching");
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, new RecipeListFragment());
        ft.commit();
    }

    public void launchInventoryStatsFragment(View v) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, new InventoryStatusFragment());
        ft.commit();
    }

    public void launchDrinkMonitorFragment(View v) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, new DrinkQueueFragment());
        ft.commit();
    }

    public void launchSettingsActivity() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Log.d(TAG, "onSharedPreferenceChanged: handling pref change. key=" + key);

        switch (key) {
            case Constants.PREFS_USERID:
                PrefsManager.setUserID(sharedPreferences.getString(Constants.PREFS_USERID, null));
                break;
            case Constants.PREFS_SERVER_URL:
                PrefsManager.setUrlBase(sharedPreferences.getString(Constants.PREFS_SERVER_URL, Constants.URLBASE_DEFAULT));
                InventoryManager.updateInventory();
                DrinkQueueManager.updateDrinkQueue();
                break;
            case Constants.PREFS_RECIPE_DB_SOURCE:
                PrefsManager.setRecipeDBSource(sharedPreferences.getString(Constants.PREFS_RECIPE_DB_SOURCE, Constants.RECIPE_DB_KEY_DEFAULT));
                RecipeManager.loadRemote();
        }
        PrefsManager.setMaxInventoryAge(sharedPreferences.getInt(Constants.PREFS_INV_REFRESH_TM, Constants.DEFAULT_MAX_AGE_INVENTORY));
        PrefsManager.setMaxDrinkQueueAge(sharedPreferences.getInt(Constants.PREFS_DQ_REFRESH_TM, Constants.DEFAULT_MAX_AGE_DRINK_QUEUE));

    }
}