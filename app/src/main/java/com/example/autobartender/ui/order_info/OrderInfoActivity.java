package com.example.autobartender.ui.order_info;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.autobartender.R;
import com.example.autobartender.utils.GetInventoryStatus;
import com.example.autobartender.utils.PostDrinkRequest;
import com.example.autobartender.utils.RecipeDBManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;

public class OrderInfoActivity extends AppCompatActivity {
    private static final String TAG = "OrderInfoActivity";

    GetInventoryStatus inventoryStatusManager;
    RecipeDBManager recipeDBManager;
    PostDrinkRequest drinkReqVM;
    TextView canMakeDrinkTV;
    TextView titleTV;
    TextView descTV;
    TextView ingredientListTV;   // TODO make some  fancy linearlayout for this
    TextView requestStatusTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_info);

        inventoryStatusManager = GetInventoryStatus.getInstance(this);
        recipeDBManager = RecipeDBManager.getInstance();
        drinkReqVM = PostDrinkRequest.getInstance(this);

        canMakeDrinkTV = (TextView) findViewById(R.id.can_make_drink_tv);
        titleTV = (TextView) findViewById(R.id.title_textView);
        descTV = (TextView) findViewById(R.id.desc_textView);
        ingredientListTV = (TextView) findViewById(R.id.ingredient_list_tv);
        requestStatusTV = (TextView) findViewById(R.id.request_status_tv);

        try {
            canMakeDrinkTV.setText(R.string.can_make_drink);
            for (int i = 0; i < recipeDBManager.getSelectedRecipe().getValue().getJSONArray(recipeDBManager.INGREDIENTS).length(); i++) {
                String ingID = recipeDBManager.getSelectedRecipe().getValue().getJSONArray(recipeDBManager.INGREDIENTS).getJSONObject(i).getString(recipeDBManager.ID);
                int quant = recipeDBManager.getSelectedRecipe().getValue().getJSONArray(recipeDBManager.INGREDIENTS).getJSONObject(i).getInt(recipeDBManager.QUANTITY);
                if (!inventoryStatusManager.getInventory().hasQuantityOfIngredient(ingID, quant)) {
                    canMakeDrinkTV.setText(R.string.cant_make_drink);
                    break;
                }
            }
            titleTV.setText(recipeDBManager.getSelectedRecipe().getValue().getString(recipeDBManager.NAME));
            descTV.setText(recipeDBManager.getSelectedRecipe().getValue().getString(recipeDBManager.DESCRIPTION));
            StringBuilder ingredientList = new StringBuilder();
            for (int i = 0; i < recipeDBManager.getSelectedRecipe().getValue().getJSONArray(recipeDBManager.INGREDIENTS).length(); i++)  {
                ingredientList.append(recipeDBManager.getSelectedRecipe().getValue().getJSONArray(recipeDBManager.INGREDIENTS).getJSONObject(i).getString(recipeDBManager.ID));
                ingredientList.append(": ");
                ingredientList.append(recipeDBManager.getSelectedRecipe().getValue().getJSONArray(recipeDBManager.INGREDIENTS).getJSONObject(i).getInt(recipeDBManager.QUANTITY));
                ingredientList.append("\n");
            }
            ingredientListTV.setText(ingredientList.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public void startRecipeOrder(View v) {
        Log.d(TAG, "startRecipeOrder: order button clicked.");

        // the vm already knows what recipe is selected so parameter is not needed
        PostDrinkRequest.DrinkRequestThread thread = drinkReqVM.requestDrink(this);
        final Observer<JSONObject> returnDataObserver = new Observer<JSONObject>() {
            @Override
            public void onChanged(JSONObject jsonObject) { onOrderComplete(thread); }
        };
        thread.getReturnData().observe(this, returnDataObserver);
    }

    public void onOrderComplete(PostDrinkRequest.DrinkRequestThread thread) {
        // Called when post function completes and gets data back.

        //TODO make pretty
        try {
            this.requestStatusTV.setText(thread.getReturnData().getValue().toString(2));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}