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
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.RecyclerView;

import com.example.autobartender.R;
import com.example.autobartender.ui.helpers.RecipeInfo;
import com.example.autobartender.utils.Constants;
import com.example.autobartender.utils.networking.HTTPGETBase.RequestStatus;
import com.example.autobartender.utils.PrefsManager;
import com.example.autobartender.utils.RecipeManager;
import com.example.autobartender.utils.RecipeManager.Recipe;
import com.example.autobartender.utils.networking.HTTPGETImage;

import java.net.MalformedURLException;
import java.net.URL;

public class RecipeView_RVA extends RecyclerView.Adapter<RecipeView_RVA.RowViewHolder> {
    private static final String TAG = RecipeView_RVA.class.getSimpleName();

    private final LayoutInflater li;

    public RecipeView_RVA(Context ctx) {
        Log.d(TAG, "RecipeView_RVA: initializing. " + RecipeManager.getNumRecipes());
        this.li=(LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    static class RowViewHolder extends RecyclerView.ViewHolder {
        Recipe recipe;  // Save reference to recipe to pass along onClick
        HTTPGETImage requestThread;  // Save reference to image decoder so can pull that also

        // Thumbnail card UI elements
        ConstraintLayout rootViewThumb;
        TextView tvTitle;
        TextView tvDescription;
        ImageView ivThumbnail;

        // helper for full info card
        RecipeInfo recipeInfoFull;

        public RowViewHolder(@NonNull View itemView) {
            super(itemView);
            Log.d(TAG, "RowViewHolder: initializing");

            this.rootViewThumb = itemView.findViewById(R.id.recipe_info_thumb);
            this.tvTitle = (TextView)itemView.findViewById(R.id.tv_title_thumb);
            this.tvDescription = (TextView)itemView.findViewById(R.id.tv_description_thumb);
            this.ivThumbnail = (ImageView) itemView.findViewById(R.id.iv_thumbnail);

            this.recipeInfoFull = new RecipeInfo(itemView.findViewById(R.id.recipe_info_full));

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

        // get recipe image
        //TODO make this work with the file server also oops its probably gonna have to be in recipemanager
        if (recipe.getImgFileName() != null) {
            URL url = null;
            try {
                //TODO find better way to put the URL together idk
                url = new URL(Constants.URLBASE_EMULATOR_HARDCODED + Constants.URL_PATH_IMG + recipe.getImgFileName());
                Log.d(TAG, "onBindViewHolder: made url + " + url);
            } catch (MalformedURLException e) {
                Log.d(TAG, "onBindViewHolder: malformed url exception: " + e.getLocalizedMessage());
            } catch (IllegalArgumentException e) {
                Log.d(TAG, "onBindViewHolder: ill argument exc: " + PrefsManager.getRecipeDBSourceURI() + ", " + Constants.URL_PATH_IMG + ", " + recipe.getImgFileName());
            }
            MutableLiveData<RequestStatus> requestStatus = new MutableLiveData<RequestStatus>();
            requestStatus.observeForever(requestStatus1 -> {
                Log.d(TAG, "onChanged: get Image: got image: " + requestStatus1);

                if (requestStatus1 != RequestStatus.DONE_SUCCESS)
                    return;

                holder.ivThumbnail.setImageBitmap(holder.requestThread.getImage());
                holder.recipeInfoFull.ivRecipeImg.setImageBitmap(holder.requestThread.getImage());
            });
            holder.requestThread = new HTTPGETImage(url, requestStatus);
            holder.requestThread.start();
        }

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