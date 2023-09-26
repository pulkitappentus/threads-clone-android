package com.app.threads_clone.adapter;

import android.app.Activity;
import android.content.Context;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;

import java.util.List;

import com.app.threads_clone.R;
import com.app.threads_clone.model.MediaItem;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class MediaViewerAdapter extends PagerAdapter {

    private final Activity act;
    private final List<MediaItem> items;

    public MediaViewerAdapter(Activity activity, List<MediaItem> items) {

        this.act = activity;
        this.items = items;
    }

    @Override
    public int getCount() {

        return this.items.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {

        return view == ((RelativeLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        final MediaItem item = items.get(position);
        ImageView mImage;
        LayoutInflater inflater = (LayoutInflater) act.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View viewLayout = inflater.inflate(R.layout.media_viewer_item, container, false);

        mImage = (ImageView) viewLayout.findViewById(R.id.image);

        try {
            Glide.with(act).load(item.getImageUrl())
                    .transition(withCrossFade())
                    .into(mImage);

        } catch (Exception e) {
            Log.e("MediaViewerAdapter", e.toString());
        }

        ((ViewPager) container).addView(viewLayout);

        return viewLayout;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {

        ((ViewPager) container).removeView((RelativeLayout) object);

    }
}
