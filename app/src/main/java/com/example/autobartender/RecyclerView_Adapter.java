package com.example.autobartender;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerView_Adapter extends RecyclerView.Adapter {

    private final LayoutInflater li;
    private final Context ctx;
    private final int numRows;

    public RecyclerView_Adapter(Context ctx){
        this.ctx=ctx;
        this.li=(LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.numRows = 3;
        //TODO figure out how many recipes there are and put that in
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
        vh.tvTitle.setText("[specific drink title]");
        vh.tvDescription.setText("[if you are reading this then the code works]");
    }

    @Override
    public int getItemCount() {
        return this.numRows;
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
