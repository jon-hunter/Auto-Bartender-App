package com.example.autobartender.ui.recipe_list;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.autobartender.R;
import com.example.autobartender.utils.Constants;
import com.example.autobartender.utils.RecipeManager;
import com.example.autobartender.utils.RecipeManager.Recipe;

import org.json.JSONException;
import org.json.JSONObject;

public class RecipeView_RVA extends RecyclerView.Adapter<RecipeView_RVA.RowViewHolder> {
    private static final String TAG = "RecipeView_RVA";

    private final LayoutInflater li;

    public RecipeView_RVA(Context ctx) {
        this.li=(LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @NonNull
    @Override
    public RowViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = li.inflate(R.layout.row_recipe_info, parent, false);
        return new RowViewHolder(view); //the new one
    }


    @Override
    public void onBindViewHolder(@NonNull  RowViewHolder holder, int position) {
        Recipe recipe = RecipeManager.getRecipe(position);
        //TODO: make the order parameterized so this list can be populated differently

        holder.recipeID = recipe.getID();
        holder.tvTitle.setText(recipe.getName());
        holder.tvDescription.setText(recipe.getDescription());

        // Setup click handler
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: rowviewholder clicked: " + holder.recipeID);
//                if (!RecipeManager.setSelectedRecipe(holder.recipeID)) {
//                    Log.d(TAG, "onClick: setting selected recipe failed: id " + holder.recipeID);
//                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return RecipeManager.getNumRecipes();
    }


    static class RowViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;
        TextView tvDescription;
        ImageView ivThumbnail;
        String recipeID;

        public RowViewHolder(@NonNull View itemView) {
            super(itemView);
            this.tvTitle = (TextView)itemView.findViewById(R.id.title);
            this.tvDescription = (TextView)itemView.findViewById(R.id.description);
            this.ivThumbnail = (ImageView) itemView.findViewById(R.id.thumbnail);
        }
    }
}