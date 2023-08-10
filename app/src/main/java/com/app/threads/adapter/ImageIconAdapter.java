package com.app.threads.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.app.threads.R;
import com.app.threads.model.Group;

import java.util.List;

public class ImageIconAdapter extends RecyclerView.Adapter<ImageIconAdapter.ViewHolder> {

    private final Context ctx;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView  iconImage;

        public ViewHolder(View view) {
            super(view);

            iconImage = (ImageView) view.findViewById(R.id.iconImage);

        }
    }

    public ImageIconAdapter(Context mContext) {
        this.ctx = mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image_icon, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

    }


    @Override
    public int getItemCount() {
        return 1;
    }

}