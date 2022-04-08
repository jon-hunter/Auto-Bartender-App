package com.example.autobartender.ui.prefs;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.autobartender.R;
import com.example.autobartender.databinding.ActivitySettingsBinding;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(ActivitySettingsBinding.inflate(getLayoutInflater()).getRoot());

        getSupportFragmentManager().beginTransaction().replace(R.id.settings_container, new SettingsFragment()).commit();
    }
}