package com.example.autobartender;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

public class RecyclerView_Adapter extends RecyclerView.Adapter {
    private static final String TAG = "RecyclerView_Adapter";

    private final LayoutInflater li;
    private final Context ctx;
    private MainDataVM vm;

    public RecyclerView_Adapter(Context ctx, MainDataVM vm){
        this.ctx=ctx;
        this.li=(LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.vm = vm;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = li.inflate(R.layout.drink_list_item, parent, false);
        return new RowViewHolder(view); //the new one
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        //TODO fill in specific data (find info from somewhere lol idk) and image
        RowViewHolder vh = (RowViewHolder) holder;
        try {
            JSONObject recipe = vm.recipeDB.getJSONObject(position);
            vh.tvTitle.setText(recipe.getString(vm.NAME));
            vh.tvDescription.setText(recipe.getString(vm.DESCRIPTION));

        } catch (JSONException e) {
            e.printStackTrace();
            Log.d(TAG, "onBindViewHolder: tried to make more rows than there are drink recipes. shouldnt be possible");
        }
    }

    @Override
    public int getItemCount() {
        return this.vm.getNumRecipes();
    }

    class RowViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;
        TextView tvDescription;
        ImageView ivThumbnail;

        public RowViewHolder(@NonNull View itemView) {
            super(itemView);
            this.tvTitle = (TextView)itemView.findViewById(R.id.title);
            this.tvDescription = (TextView)itemView.findViewById(R.id.description);
            this.ivThumbnail = (ImageView) itemView.findViewById(R.id.thumbnail);
        }
    }
}