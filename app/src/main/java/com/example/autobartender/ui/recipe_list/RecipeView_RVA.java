package com.example.autobartender.ui.recipe_list;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.autobartender.R;
import com.example.autobartender.ui.RecipeInfoHelper;
import com.example.autobartender.utils.RecipeManager;
import com.example.autobartender.utils.RecipeManager.Recipe;

public class RecipeView_RVA extends RecyclerView.Adapter<RecipeView_RVA.RowViewHolder> {
    private static final String TAG = "RecipeView_RVA";

    private final LayoutInflater li;

    public RecipeView_RVA(Context ctx) {
        this.li=(LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    static class RowViewHolder extends RecyclerView.ViewHolder {
        Recipe recipe;  // Save reference to recipe to pass along onClick

        // Thumbnail card UI elements
        ConstraintLayout rootViewThumb;
        TextView tvTitle;
        TextView tvDescription;
        ImageView ivThumbnail;

        // helper for full info card
        RecipeInfoHelper recipeInfoFull;

        public RowViewHolder(@NonNull View itemView) {
            super(itemView);
            this.rootViewThumb = itemView.findViewById(R.id.recipe_info_thumb);
            this.tvTitle = (TextView)itemView.findViewById(R.id.tv_title_thumb);
            this.tvDescription = (TextView)itemView.findViewById(R.id.tv_description_thumb);
            this.ivThumbnail = (ImageView) itemView.findViewById(R.id.iv_thumbnail);

            this.recipeInfoFull = new RecipeInfoHelper(itemView.findViewById(R.id.recipe_info_full));

            // hide full info view
            this.collapseInfo();
        }

        public void toggleVisibility() {
            if (this.recipeInfoFull.rootView.getVisibility() != View.VISIBLE)
                expandInfo();
            else
                collapseInfo();
        }

        public void expandInfo() {
            this.recipeInfoFull.rootView.setVisibility(View.VISIBLE);
            this.rootViewThumb.setVisibility(View.GONE);
        }

        public void collapseInfo() {
            this.recipeInfoFull.rootView.setVisibility(View.GONE);
            this.rootViewThumb.setVisibility(View.VISIBLE);
        }
    }


    @NonNull
    @Override
    public RowViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RowViewHolder(li.inflate(R.layout.row_recipe_info, parent, false));
    }


    @Override
    public void onBindViewHolder(@NonNull  RowViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: binding viewholder index " + position);

        Recipe recipe = RecipeManager.getRecipe(position);
        //TODO: make the order parameterized so this list can be populated differently

        holder.recipe = recipe;
        holder.tvTitle.setText(recipe.getName());
        holder.tvDescription.setText(recipe.getDescription());
        //TODO set recipe thumbnail image

        // Setup full info
        holder.recipeInfoFull.init(recipe, li.getContext());


        // Setup click handler
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: rowviewholder clicked: " + holder.recipe.getID());
                holder.toggleVisibility();
            }
        });
    }


    @Override
    public int getItemCount() {
        return RecipeManager.getNumRecipes();
    }
}