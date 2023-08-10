package com.app.threads.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.balysv.materialripple.MaterialRippleLayout;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.app.threads.R;
import com.app.threads.model.Profile;

import java.util.List;

public class LikeListAdapter extends RecyclerView.Adapter<LikeListAdapter.MyViewHolder> {

    private final List<Profile> items;
    private final Context mContext;
    private PeopleListAdapter.OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, Profile obj, int position);
    }

    public void setOnItemClickListener(final PeopleListAdapter.OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    public LikeListAdapter(Context context, List<Profile> items) {
        mContext = context;
        this.items = items;
    }

    @NonNull
    @Override
    public LikeListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_like_thumbnail, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull LikeListAdapter.MyViewHolder holder, int position) {
        final Profile item = items.get(position);

        holder.mProgressBar.setVisibility(View.VISIBLE);
        holder.mProfilePhoto.setVisibility(View.VISIBLE);

        holder.tvDistance.setText(item.getDistance() +"km");

        if (item.getNormalPhotoUrl() != null && item.getNormalPhotoUrl().length() > 0) {

            final ImageView img = holder.mProfilePhoto;
            final ProgressBar progressView = holder.mProgressBar;

            Glide.with(mContext)
                    .load(item.getNormalPhotoUrl())
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {

                            progressView.setVisibility(View.GONE);
                            img.setImageResource(R.drawable.profile_default_photo);
                            img.setVisibility(View.VISIBLE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            progressView.setVisibility(View.GONE);
                            img.setVisibility(View.VISIBLE);
                            return false;
                        }
                    }).into(holder.mProfilePhoto);

        } else {

            holder.mProgressBar.setVisibility(View.GONE);
            holder.mProfilePhoto.setVisibility(View.VISIBLE);
            holder.mProfilePhoto.setImageResource(R.drawable.profile_default_photo);
        }

        holder.mProfileFullname.setText(item.getFullname());

        if (item.getDistance() > 0.0) {
            holder.mProfileUsername.setText(String.format("%.1f km @%s", item.getDistance(), item.getUsername()));
        } else {
            holder.mProfileUsername.setText(String.format("@%s", item.getUsername()));
        }

        if (item.isVerify()) {
            holder.mProfileIcon.setVisibility(View.VISIBLE);

        } else {
            holder.mProfileIcon.setVisibility(View.GONE);
        }

        holder.mParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(item);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView mProfileFullname, mProfileUsername,tvDistance;
        public ImageView mProfileOnlineIcon, mProfileIcon;
        public ImageView mProfilePhoto;
        public MaterialRippleLayout mParent;
        public ProgressBar mProgressBar;

        public MyViewHolder(View view) {
            super(view);
            mParent = (MaterialRippleLayout) view.findViewById(R.id.parent);
            mProfilePhoto = (ImageView) view.findViewById(R.id.profileImg);
            mProfileFullname = (TextView) view.findViewById(R.id.profileFullname);
            mProfileUsername = (TextView) view.findViewById(R.id.profileUsername);
            mProfileOnlineIcon = (ImageView) view.findViewById(R.id.profileOnlineIcon);
            mProfileIcon = (ImageView) view.findViewById(R.id.profileIcon);
            mProgressBar = (ProgressBar) view.findViewById(R.id.progressBar);
            tvDistance = (TextView) view.findViewById(R.id.tvDistance);
        }
    }
}
