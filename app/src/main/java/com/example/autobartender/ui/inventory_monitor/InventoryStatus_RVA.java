package com.example.autobartender.ui.inventory_monitor;

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
import com.example.autobartender.utils.GetInventoryStatus;

public class InventoryStatus_RVA extends RecyclerView.Adapter<InventoryStatus_RVA.RowViewHolder> {
    private final LayoutInflater li;
    private GetInventoryStatus vm;
    final String TAG = "RecyclerView_Adapter";

    //two arg constructor in case user wants to define their own maxrows
    public InventoryStatus_RVA(Context ctx) {
        Log.d(TAG, "RecyclerView_Adapter: initing rva");
        this.li = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.vm  = GetInventoryStatus.getInstance(ctx);
    }

    class RowViewHolder extends RecyclerView.ViewHolder {
        TextView tvSlot;
        TextView tvIngName;
        TextView tvIngQuantity;
        ProgressBar pbPercent;

        public RowViewHolder(@NonNull View itemView) {
            super(itemView);
            Log.d(TAG, "RowViewHolder: init rowviewholder");
            tvSlot = itemView.findViewById(R.id.slot_number);
            tvIngName = itemView.findViewById(R.id.ingredient_title);
            tvIngQuantity = itemView.findViewById(R.id.ingredient_amount);
            pbPercent = itemView.findViewById(R.id.progressBar);
        }
    }

    @NonNull
    @Override
    public RowViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RowViewHolder(li.inflate(R.layout.row_ingredient_status, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RowViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: binding viewholder, index " + position);

        // Fill in fields using JSON stored in VM
        holder.tvSlot.setText(Integer.toString(position + 1));

        String id = vm.getInventory().getIngredientID(position);
        if (id.equals(""))
            id = holder.itemView.getContext().getString(R.string.no_ingredient);

        int amt = vm.getInventory().getIngredientQuantity(position);

        Log.d(TAG, "onBindViewHolder: " + id + " " + amt);
        holder.tvIngName.setText(id);
        // TODO implement some id -> name function. somehow.
        holder.tvIngQuantity.setText(String.format(holder.itemView.getContext().getString(R.string.suffix_ml), amt));
        holder.pbPercent.setMax(vm.getInventory().getMaxCapacity());
        holder.pbPercent.setProgress(amt);
    }

    @Override
    public int getItemCount() {
        return vm.getInventory().getNumSlots();
    }
}
