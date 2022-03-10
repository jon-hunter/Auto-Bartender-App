package com.example.autobartender.ui.main_activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;


import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.appcompat.app.AppCompatActivity;

import com.example.autobartender.OrderInfoActivity;
import com.example.autobartender.R;
import com.example.autobartender.databinding.ActivityMainBinding;
import com.example.autobartender.ui.inventory_monitor.InventoryStatusFragment;
import com.example.autobartender.ui.recipe_list.RecipeListFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private MainVM vm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        com.example.autobartender.databinding.ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Setup fragments
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, new InventoryStatusFragment());
        ft.commit();



        // Get the ViewModel
        vm = MainVM.getInstance();
        if (vm.recipeDB == null) {
            loadRecipeDb();
        }

        //LiveData observer
        final Observer<JSONObject> recipeChoiceObserver = new Observer<JSONObject>() {
            @Override
            public void onChanged(JSONObject jsonObject) { launchOrderInfoActivity(); }
        };
        vm.getRecipeChoice().observe(this, recipeChoiceObserver);
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



    //TODO probably doent belong here
    /**
     * loads the recipe DB and saves it as a variable.
     */
    public void loadRecipeDb() {
        Log.d(TAG, "loadRecipeDb: loading recipe DB");
        try {
            // Do file input stream stuff
            InputStream is = getResources().openRawResource(R.raw.recipe_db);
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line = br.readLine();
            StringBuilder json_str = new StringBuilder();
            while (line != null) {
                json_str.append(line);
                line = br.readLine();
            }

            // Read the JSON
            vm.recipeDB = new JSONArray(json_str.toString());
        }
        catch (IOException e) {
            Log.d(TAG, "loadRecipeDb: IOexception oops");
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d(TAG, "loadRecipeDb: Bro the fuckin db threw a json error. its literally not a file that ever changes so you should check that");
        }
    }

}