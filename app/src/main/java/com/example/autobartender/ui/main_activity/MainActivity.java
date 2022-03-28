package com.example.autobartender.ui.main_activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;


import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.appcompat.app.AppCompatActivity;

import com.example.autobartender.ui.drink_monitor.DrinkMonitorFragment;
import com.example.autobartender.ui.order_info.OrderInfoActivity;
import com.example.autobartender.R;
import com.example.autobartender.databinding.ActivityMainBinding;
import com.example.autobartender.ui.inventory_monitor.InventoryStatusFragment;
import com.example.autobartender.ui.recipe_list.RecipeListFragment;
import com.example.autobartender.utils.Constants;
import com.example.autobartender.utils.RecipeManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        com.example.autobartender.databinding.ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Setup fragments
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, new InventoryStatusFragment());
        ft.commit();


        // Setup the ViewModel
        RecipeManager.loadRemoteRecipes();
        RecipeManager.loadLocalRecipes(getSharedPreferences(Constants.LOCAL_RECIPE_DB, MODE_PRIVATE));
    }

    //Navigation functions. These are event handlers (click or otherwise), either launching an activity or swapping out the main fragment view
    public void launchOrderInfoActivity() {
        Intent intent = new Intent(this, OrderInfoActivity.class);
        startActivity(intent);
    }

    public void launchRecipeListFragment(View v) {
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
        ft.replace(R.id.fragment_container, new DrinkMonitorFragment());
        ft.commit();
    }




}