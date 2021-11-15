package com.example.autobartender;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

import com.google.android.material.navigation.NavigationView;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.example.autobartender.databinding.ActivityMainBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    private MainDataSingleton vm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        // Get the ViewModel
        vm = MainDataSingleton.getInstance();
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

    private void launchOrderInfoActivity() {
        Intent intent = new Intent(this, OrderInfoActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }


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