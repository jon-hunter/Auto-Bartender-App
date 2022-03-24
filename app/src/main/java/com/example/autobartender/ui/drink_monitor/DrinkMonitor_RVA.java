package com.example.autobartender.ui.drink_monitor;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.autobartender.R;

public class DrinkMonitor_RVA extends RecyclerView.Adapter<DrinkMonitor_RVA.RowViewHolder> {
    public static final String TAG = "DrinkMonitor_RVA";

    private final LayoutInflater li;

    public DrinkMonitor_RVA(Context ctx) {
        this.li = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @NonNull
    @Override
    public RowViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RowViewHolder(li.inflate(R.layout.row_drink_queue_info, parent, false));
    }


    @Override
    public void onBindViewHolder(@NonNull RowViewHolder holder, int position) {
        holder.tvQueuePos.setText(Integer.toString(position + 1));

        holder.pbProgress.setMax(100);
        holder.pbProgress.setProgress(69);

        //TODO figure this shit out lol. copy paste from recipeView rva
    }


    @Override
    public int getItemCount() {
        return 6; //TODO implement
    }


    class RowViewHolder extends RecyclerView.ViewHolder {
        TextView tvQueuePos;
        TextView tvRecipeName;
        TextView tvUserID;
        ProgressBar pbProgress;

        public RowViewHolder(@NonNull View itemView) {
            super(itemView);
            Log.d(TAG, "RowViewHolder: init rowviewholder");
            tvQueuePos = itemView.findViewById(R.id.tv_queue_position);
            tvRecipeName = itemView.findViewById(R.id.tv_recipe_name);
            tvUserID = itemView.findViewById(R.id.tv_user_ID);
            pbProgress = itemView.findViewById(R.id.progressBar);
        }
    }
}
