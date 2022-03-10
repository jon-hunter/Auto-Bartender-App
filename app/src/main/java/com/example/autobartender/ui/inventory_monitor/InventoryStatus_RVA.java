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

public class InventoryStatus_RVA extends RecyclerView.Adapter{
    private final LayoutInflater li;
    private InventoryStatusVM vm;
    final String TAG = "RecyclerView_Adapter";

    //two arg constructor in case user wants to define their own maxrows
    public InventoryStatus_RVA(Context ctx, InventoryStatusVM vm) {
        Log.d(TAG, "RecyclerView_Adapter: initing rva");
        this.li = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.vm  = vm;
    }

    class RowViewHolder extends RecyclerView.ViewHolder {
        View view;
        TextView tvSlot;
        TextView tvIngName;
        TextView tvIngQuantity;
        ProgressBar percent;

        public RowViewHolder(@NonNull View itemView) {
            super(itemView);
            Log.d(TAG, "RowViewHolder: init rowviewholder");
            view = itemView;
            tvSlot = itemView.findViewById(R.id.slot_number);
            tvIngName = itemView.findViewById(R.id.ingredient_title);
            tvIngQuantity = itemView.findViewById(R.id.ingredient_amount);
            percent = itemView.findViewById(R.id.progressBar);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RowViewHolder(li.inflate(R.layout.row_ingredient_status, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: binding viewholder, index " + position);

        RowViewHolder viewHolder = (RowViewHolder) holder;

        // Fill in fields using JSON stored in VM
        viewHolder.tvSlot.setText(Integer.toString(position + 1));

        String id = vm.getInventory().getIngredientID(position);
        if (id.equals(""))
            id = viewHolder.view.getContext().getString(R.string.no_ingredient);

        int amt = vm.getInventory().getIngredientQuantity(position);

        Log.d(TAG, "onBindViewHolder: " + id + " " + amt);
        viewHolder.tvIngName.setText(id);
        // TODO implement some id -> name function. somehow.
        viewHolder.tvIngQuantity.setText(String.format(viewHolder.view.getContext().getString(R.string.suffix_ml), amt));
        viewHolder.percent.setMax(vm.getInventory().getMaxCapacity());
        viewHolder.percent.setProgress(amt);
    }

    @Override
    public int getItemCount() {
        return vm.getInventory().getNumSlots();
    }
}
