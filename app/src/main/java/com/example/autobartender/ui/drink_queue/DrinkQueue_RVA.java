package com.example.autobartender.ui.drink_queue;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.autobartender.R;
import com.example.autobartender.ui.layout_helpers_lol.RecipeInfo;
import com.example.autobartender.utils.Constants;
import com.example.autobartender.utils.DrinkQueueManager;
import com.example.autobartender.utils.DrinkQueueManager.Drink;

public class DrinkQueue_RVA extends RecyclerView.Adapter<DrinkQueue_RVA.RowViewHolder> {
    public static final String TAG = "DrinkQueue_RVA";

    private final LayoutInflater li;

    public DrinkQueue_RVA(Context ctx) {
        this.li = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    static class RowViewHolder extends RecyclerView.ViewHolder {

        // thumbnail card UI elements
        TextView tvQueuePos;
        TextView tvRecipeNameUID;
        TextView tvServerStatus;
        TextView tvReqTM;
        ProgressBar pbProgress;
        ImageView ivDropdownBtn;

        // helper for full info card UI elements
        RecipeInfo recipeInfoFull;



        public RowViewHolder(@NonNull View itemView) {
            super(itemView);
            Log.d(TAG, "RowViewHolder: init rowviewholder");

            // Initialize everything (bruh)
            this.tvQueuePos = itemView.findViewById(R.id.tv_queue_position);
            this.tvRecipeNameUID = itemView.findViewById(R.id.tv_recipe_name_user_id);
            this.tvServerStatus = itemView.findViewById(R.id.tv_server_status);
            this.tvReqTM = itemView.findViewById(R.id.tv_request_time);
            this.pbProgress = itemView.findViewById(R.id.progressBar);
            this.ivDropdownBtn = itemView.findViewById(R.id.btn_expand);

            this.recipeInfoFull = new RecipeInfo(itemView.findViewById(R.id.recipe_info_full));

            // hide full info view, its image, and redundent title
            this.collapseInfo();
            this.recipeInfoFull.ivRecipeImg.setVisibility(View.GONE);
            this.recipeInfoFull.tvRecipeName.setVisibility(View.GONE);
        }

        public void toggleVisibility() {
            if (this.recipeInfoFull.rootView.getVisibility() != View.VISIBLE)
                expandInfo();
            else
                collapseInfo();
        }

        public void expandInfo() {
            this.recipeInfoFull.rootView.setVisibility(View.VISIBLE);
            this.ivDropdownBtn.setRotation(180);
        }

        public void collapseInfo() {
            this.recipeInfoFull.rootView.setVisibility(View.GONE);
            this.ivDropdownBtn.setRotation(0);
        }
    }


    @NonNull
    @Override
    public RowViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RowViewHolder(li.inflate(R.layout.row_drink_queue_info, parent, false));
    }


    @Override
    public void onBindViewHolder(@NonNull RowViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: binding viewholder index " + position);

        holder.tvQueuePos.setText(String.format("%d", position + 1));

        Drink drink = DrinkQueueManager.getDrink(position);

        // Set recipeName & userId textview
        String recipeNm;
        if (drink.recipe.getName() != null)
            recipeNm = drink.recipe.getName();
        else
            recipeNm = li.getContext().getString(R.string.default_recipe_name);

        String uId;
        if (drink.userID != null)
            uId = drink.userID;
        else
            uId = li.getContext().getString(R.string.default_user);

        holder.tvRecipeNameUID.setText(String.format(
                li.getContext().getString(R.string.recipe_name_user_id),
                recipeNm,
                uId
        ));

        // set requestTime textview
        holder.tvReqTM.setText(Constants.formatTimeDifference(drink.requestTime, li.getContext()));

        // Set server status textview
        if (drink.inMachineQueue) {
            holder.tvServerStatus.setText(R.string.server_sync_yes);
            holder.tvServerStatus.setBackgroundResource(R.drawable.status_good);
        }
        else {
            holder.tvServerStatus.setText(R.string.server_sync_no);
            holder.tvServerStatus.setBackgroundResource(R.drawable.status_bad);
        }

        // Setup progressbar
        holder.pbProgress.setMax(100);
        holder.pbProgress.setProgress(drink.getProgressPct());

        // Setup full info view
        holder.recipeInfoFull.init(drink.recipe, li.getContext());

        // Setup click handler
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { holder.toggleVisibility(); }
        });
    }


    @Override
    public int getItemCount() {
        return DrinkQueueManager.getQueueLength();
    }

}
