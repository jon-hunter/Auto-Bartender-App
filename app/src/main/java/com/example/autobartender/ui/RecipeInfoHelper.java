package com.example.autobartender.ui;

import android.content.Context;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.autobartender.R;
import com.example.autobartender.utils.InventoryManager;
import com.example.autobartender.utils.RecipeManager;
import com.example.autobartender.utils.RecipeManager.Recipe;

/**
 * not an activity or a fragment. used as an attribute of a viewholder
 */
public class RecipeInfoHelper {

    // All the UI elements
    public ConstraintLayout rootView;
    public ImageView ivRecipeImg;
    public TextView tvRecipeName;
    public TextView canMakeRecipe;
    public TextView tvDescription;
    public TextView tvIngList;
    public Button btnOrder;
    public Button btnEdit;
    public ImageButton btnFav;

    public RecipeInfoHelper(ConstraintLayout rootView) {
        this.rootView = rootView;
        this.ivRecipeImg = rootView.findViewById(R.id.iv_recipe_main);
        this.tvRecipeName = rootView.findViewById(R.id.tv_recipe_name_full);
        this.canMakeRecipe = rootView.findViewById(R.id.can_make_drink);
        this.tvDescription = rootView.findViewById(R.id.tv_description_full);
        this.tvIngList = rootView.findViewById(R.id.tv_ingredient_list);
        this.btnOrder = rootView.findViewById(R.id.btn_order);
        this.btnEdit = rootView.findViewById(R.id.btn_edit);
        this.btnFav = rootView.findViewById(R.id.btn_favorite);
    }

    /**
     * Helper to set text values from a recipe. Sets main IV, Title, Description, can make drink, ingredient list TVs
     * @param recipe the recipe to use
     * @param ctx Context to access a String resource
     */
    public void init(Recipe recipe, Context ctx) {

        //TODO set image

        this.tvRecipeName.setText(recipe.getName());

        if (InventoryManager.canMakeRecipe(recipe)) {
            canMakeRecipe.setText(R.string.can_make_drink);
            canMakeRecipe.getBackground().setState(new int[]{R.attr.state_can_make_drink});
        }
        else {
            canMakeRecipe.setText(R.string.cant_make_drink);
            canMakeRecipe.getBackground().setState(new int[]{-R.attr.state_can_make_drink});
        }

        this.tvDescription.setText(recipe.getDescription());

        // Ingredient list
        this.tvIngList.setText(
                RecipeManager.makeIngredientList(
                        recipe,
                        ctx.getString(R.string.ingredient_list_row_mL)
                )
        );
    }


}
