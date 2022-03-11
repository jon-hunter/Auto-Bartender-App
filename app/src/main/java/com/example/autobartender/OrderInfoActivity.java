package com.example.autobartender;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.autobartender.ui.main_activity.MainVM;

import org.json.JSONException;

import java.net.URI;
import java.net.URISyntaxException;

public class OrderInfoActivity extends AppCompatActivity {
    private static final String TAG = "OrderInfoActivity";

    MainVM mainVM;
    DrinkRequestVM drinkReqVM;
    TextView titleTV;
    TextView descTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_info);

        mainVM = MainVM.getInstance();
        drinkReqVM = new ViewModelProvider(this).get(DrinkRequestVM.class);
        drinkReqVM.mainVM = mainVM;
        try {
            drinkReqVM.URL_BASE = new URI(getString(R.string.URL_BASE));
        } catch (URISyntaxException e) {
            Log.d(TAG, "onCreate: R.string.URLBASE did not make a valid URI. check that out");
        }

        titleTV = (TextView) findViewById(R.id.title_textView);
        descTV = (TextView) findViewById(R.id.desc_textView);

        try {
            titleTV.setText(mainVM.getSelectedRecipe().getValue().getString(mainVM.NAME));
            descTV.setText(mainVM.getSelectedRecipe().getValue().getString(mainVM.DESCRIPTION));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public void startRecipeOrder(View v) {
        Log.d(TAG, "startRecipeOrder: order button clicked.");

        // the vm already knows what recipe is selected so parameter is not needed
        drinkReqVM.requestDrink(this);
    }
}