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
    private MainDataSingleton vm;


    public RecyclerView_Adapter(Context ctx){
        this.ctx=ctx;
        this.li=(LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.vm = MainDataSingleton.getInstance();
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
            JSONObject recipe = vm.getRecipe(position, MainDataSingleton.RecipeSortOrder.DEFAULT);
            //TODO: make the order parameterized so this list can be populated differently
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

//    public interface RecyclerViewClickListener {
//        public void recyclerViewListClicked(View v, int position);
//    }


    class RowViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvTitle;
        TextView tvDescription;
        ImageView ivThumbnail;

        public RowViewHolder(@NonNull View itemView) {
            super(itemView);
            this.tvTitle = (TextView)itemView.findViewById(R.id.title);
            this.tvDescription = (TextView)itemView.findViewById(R.id.description);
            this.ivThumbnail = (ImageView) itemView.findViewById(R.id.thumbnail);
        }

        @Override
        public void onClick(View v) {
            vm.launch_order_info(this.getLayoutPosition());
        }
    }
}