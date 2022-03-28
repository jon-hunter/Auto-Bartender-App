package com.example.autobartender.ui.order_info;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.autobartender.R;
import com.example.autobartender.utils.Constants;
import com.example.autobartender.utils.InventoryManager;
import com.example.autobartender.utils.NetworkGETRequest;
import com.example.autobartender.utils.NetworkPOSTRequest;
import com.example.autobartender.utils.RecipeManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;

public class OrderInfoActivity extends AppCompatActivity {
    private static final String TAG = "OrderInfoActivity";

    TextView canMakeDrinkTV;
    TextView titleTV;
    TextView descTV;
    LinearLayout ingredientListTV;
    TextView requestStatusTV;
    RecipeManager.Recipe recipe;
    MutableLiveData<JSONObject> returnData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_info);

        canMakeDrinkTV = (TextView) findViewById(R.id.can_make_drink_tv);
        titleTV = (TextView) findViewById(R.id.title_textView);
        descTV = (TextView) findViewById(R.id.desc_textView);
        ingredientListTV = (LinearLayout) findViewById(R.id.ingredient_list);
        requestStatusTV = (TextView) findViewById(R.id.request_status_tv);
        this.recipe = RecipeManager.getSelectedRecipe();

        // Set "can make drink" box
        if (InventoryManager.canMakeRecipe(this.recipe))
            canMakeDrinkTV.setText(R.string.can_make_drink);
        else
            canMakeDrinkTV.setText(R.string.cant_make_drink);

        // Set title/desc boxes
        titleTV.setText(this.recipe.getName());
        descTV.setText(this.recipe.getDescription());

        // Set recipe ingredient view
        ingredientListTV.addView(RecipeManager.buildRecipeLayout(this, this.recipe));

    }


    public void startRecipeOrder(View v) {
        Log.d(TAG, "startRecipeOrder: order button clicked.");

        // Prepare parameters for request: URL, request body, return data
        URL url = null;
        try {
            url = Constants.getURLBase().resolve(Constants.URL_PATH_DRINK).toURL();
        } catch (MalformedURLException e) {
            Log.d(TAG, "requestDrink: MALFORMED URL. This is hardcoded so should not happen");
            Log.d(TAG, String.format(
                    "requestDrink: URLBASE = %s, path = %s",
                    Constants.getURLBase(),
                    Constants.URL_PATH_DRINK
            ));
        }

        String requestBody = RecipeManager.createDrinkRequestBody(this.recipe, "bennetth");
        this.returnData = new MutableLiveData<JSONObject>();
        this.returnData.observe(this, new Observer<JSONObject>() {
            @Override
            public void onChanged(JSONObject jsonObject) {
                onOrderComplete();
            }
        });

        NetworkPOSTRequest thread = new NetworkPOSTRequest(url, requestBody, this.returnData);
        thread.start();
    }

    public void onOrderComplete() {
        // Called when post function completes and gets data back.

        //TODO make pretty
        try {
            if (this.returnData.getValue() == null)
                this.requestStatusTV.setText(R.string.order_status_fail);
            this.requestStatusTV.setText(this.returnData.getValue().toString(2));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}