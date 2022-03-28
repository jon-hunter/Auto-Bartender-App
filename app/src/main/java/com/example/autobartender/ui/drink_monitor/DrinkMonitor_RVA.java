package com.example.autobartender.ui.drink_monitor;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.RecyclerView;

import com.example.autobartender.R;
import com.example.autobartender.utils.DrinkQueueManager;
import com.example.autobartender.utils.DrinkQueueManager.Drink;
import com.example.autobartender.utils.RecipeManager;

public class DrinkMonitor_RVA extends RecyclerView.Adapter<DrinkMonitor_RVA.RowViewHolder> {
    public static final String TAG = "DrinkMonitor_RVA";

    private final LayoutInflater li;

    public DrinkMonitor_RVA(Context ctx) {
        this.li = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    class RowViewHolder extends RecyclerView.ViewHolder {
        TextView tvQueuePos;
        TextView tvRecipeName;
        TextView tvUserID;
        ProgressBar pbProgress;
        LinearLayout extraInfoLL;
        ImageView dropdownBtn;

        public RowViewHolder(@NonNull View itemView) {
            super(itemView);
            Log.d(TAG, "RowViewHolder: init rowviewholder");
            tvQueuePos = itemView.findViewById(R.id.tv_queue_position);
            tvRecipeName = itemView.findViewById(R.id.tv_recipe_name);
            tvUserID = itemView.findViewById(R.id.tv_user_ID);
            pbProgress = itemView.findViewById(R.id.progressBar);
            extraInfoLL = itemView.findViewById(R.id.secondaryInfoLayout);
            extraInfoLL.setDividerDrawable(AppCompatResources.getDrawable(itemView.getContext(), R.drawable.divider));
            dropdownBtn = itemView.findViewById(R.id.imageButton);
        }

        public void toggleInfoVisibility() {
            if (extraInfoLL.getVisibility() == View.VISIBLE ) {
                extraInfoLL.setVisibility(View.GONE);
                dropdownBtn.setRotation(0);
            }
            else {
                extraInfoLL.setVisibility(View.VISIBLE);
                dropdownBtn.setRotation(180);
            }
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

        // Set main textviews
        holder.tvQueuePos.setText(Integer.toString(position + 1));
        Drink drink = DrinkQueueManager.getDrink(position);

        if (drink.recipe.getName() != null)
            holder.tvRecipeName.setText(drink.recipe.getName());
        else
            holder.tvRecipeName.setText(R.string.default_recipe_name);

        if (drink.userID != null)
            holder.tvUserID.setText(drink.userID);
        else
            holder.tvUserID.setText(R.string.default_user);

        // Setup progressbar
        holder.pbProgress.setMax(100);
        holder.pbProgress.setProgress(drink.getProgressPct());

        // Setup extra info, initialy hidden
        holder.extraInfoLL.setVisibility(View.GONE);
        holder.extraInfoLL.addView(RecipeManager.buildRecipeLayout(li.getContext(), drink.recipe));
        //TODO fill in LL with a row_ingredient_single for each ingredient in the recipe for the drink in question

        // Setup click handler
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { holder.toggleInfoVisibility(); }
        });
    }


    @Override
    public int getItemCount() {
        return DrinkQueueManager.getQueueLength();
    }

}
