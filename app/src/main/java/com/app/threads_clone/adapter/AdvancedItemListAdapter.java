package com.app.threads_clone.adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.ImageLoader;
import com.app.threads_clone.util.DoubleClickListener;
import com.balysv.materialripple.MaterialRippleLayout;
//import com.google.android.gms.ads.AdRequest;
//import com.google.android.gms.ads.AdView;
import com.makeramen.roundedimageview.RoundedImageView;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import github.ankushsachdeva.emojicon.EmojiconEditText;
import github.ankushsachdeva.emojicon.EmojiconTextView;
import com.app.threads_clone.GroupActivity;
import com.app.threads_clone.HashtagsActivity;
import com.app.threads_clone.LikersActivity;
import com.app.threads_clone.MediaViewerActivity;
import com.app.threads_clone.ProfileActivity;
import com.app.threads_clone.R;
import com.app.threads_clone.VideoViewActivity;
import com.app.threads_clone.ViewItemActivity;
import com.app.threads_clone.ViewYouTubeVideoActivity;
import com.app.threads_clone.app.App;
import com.app.threads_clone.constants.Constants;
import com.app.threads_clone.model.Comment;
import com.app.threads_clone.model.Item;
import com.app.threads_clone.model.MediaItem;
import com.app.threads_clone.util.Api;
import com.app.threads_clone.util.CustomRequest;
import com.app.threads_clone.util.TagClick;
import com.app.threads_clone.util.TagSelectingTextview;

public class AdvancedItemListAdapter extends RecyclerView.Adapter<AdvancedItemListAdapter.ViewHolder>
        implements Constants, TagClick {

    //
    private Boolean isOne = true;

    private long replyToUserId = 0;

    private List<Item> items = new ArrayList<>();

    private final Context context;

    TagSelectingTextview mTagSelectingTextview;

    public static int hashTagHyperLinkDisabled = 0;

    public static final String HASHTAGS_COLOR = "#5BCFF2";

    ImageLoader imageLoader = App.getInstance().getImageLoader();

    private OnItemMenuButtonClickListener onItemMenuButtonClickListener;

    public interface OnItemMenuButtonClickListener {

        void onItemClick(Item obj, int actionId, int position);
    }

    public void setOnMoreButtonClickListener(final OnItemMenuButtonClickListener onItemMenuButtonClickListener) {
        this.onItemMenuButtonClickListener = onItemMenuButtonClickListener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public CircularImageView mItemAuthorPhoto, mItemAuthorIcon;
        //mItemFeelingIcon;
        public TextView mItemAuthor;
        //mItemFeelingTitle;
        public ImageView mItemPlayVideo;
        //mItemAuthorOnlineIcon
        public ImageView mItemMenuButton;
        public RoundedImageView mItemImg;
        public RoundedImageView mVideoImg;
        public RelativeLayout mVideoLayout, mImageLayout;
        public LinearLayout mImagesCounterLayout;
        public TextView mImagesCounterLabel;
        public ImageView mItemLikeImg, mItemCommentImg, mItemRepostImg;
        //public TextView mItemRepostsCount;
        public EmojiconTextView mItemDescription;
        public TextView mItemTimeAgo;
        public ProgressBar mImageProgressBar, mVideoProgressBar;

        public LinearLayout cardContainer;
        public MaterialRippleLayout mItemLikeButton, mItemCommentButton, mItemRepostButton,mItemLikeListButton;

       // public LinearLayout mLocationLayout;
        //mAccessModeLayout;
        //public TextView mLocationLabel;
        //mAccessModeLabel;
        //public ImageView mAccessModeImage;

        public LinearLayout mLinkContainer;
        public ImageView mLinkImage;
        public TextView mLinkTitle;
        public TextView mLinkDescription;

        public CardView mAdCard;
       // public AdView mAdView;


        public LinearLayout mCardRepostContainer;

        public ProgressBar mReImageProgressBar, mReVideoProgressBar;
        public RelativeLayout mReVideoLayout, mReImageLayout;
        public LinearLayout mReImagesCounterLayout;
        public TextView mReImagesCounterLabel;
        public RoundedImageView mReItemImg;
        public ImageView videoImage;
        public RoundedImageView mReVideoImg;

        public CircularImageView mReAuthorPhoto, mReAuthorIcon;
        public TextView mReAuthor, mReAuthorUsername;
        public ImageView mRePlayVideo;
        public EmojiconTextView mReDescription;
        public TextView mReTimeAgo;

        public LinearLayout mReLinkContainer, mReMessageContainer, mReBodyContainer;
        ConstraintLayout mReHeaderContainer;
        public ImageView mReLinkImage;
        public TextView mReLinkTitle;
        public TextView mReLinkDescription;

        public LinearLayout mItemCountersContainer;
        public MaterialRippleLayout mItemCountersContainerButton;
      //  public ImageView mItemLikesCountImage, mItemCommentsCountImage;
        public TextView mItemLikesCountText, mItemCommentsCountText;

        //new commented views
        public View view3;
        public ConstraintLayout clCommentedImage;
        public CircularImageView cvUserOne,cvUserTwo;


       // public DoubleTapLikeView doubleTap;

        public ViewHolder(View v, int itemType) {
            super(v);
            if (itemType == 0) {
                mItemAuthorPhoto = (CircularImageView) v.findViewById(R.id.itemAuthorPhoto);
                mItemAuthorIcon = (CircularImageView) v.findViewById(R.id.itemAuthorIcon);

               // mItemFeelingIcon = (CircularImageView) v.findViewById(R.id.itemFeelingIcon);

                mItemAuthor = (TextView) v.findViewById(R.id.itemAuthor);
                //mItemAuthorOnlineIcon = (ImageView) v.findViewById(R.id.itemAuthorOnlineIcon);

                //mAccessModeLayout = (LinearLayout) v.findViewById(R.id.access_mode_layout);
                //mLocationLayout = (LinearLayout) v.findViewById(R.id.location_layout);

                //mLocationLabel = (TextView) v.findViewById(R.id.location_label);
                //mAccessModeLabel = (TextView) v.findViewById(R.id.access_mode_label);
                //mAccessModeImage = (ImageView) v.findViewById(R.id.access_mode_image);

                //mItemFeelingTitle = (TextView) v.findViewById(R.id.itemFeelingTitle);

                mVideoLayout = (RelativeLayout) v.findViewById(R.id.video_layout);
                mImageLayout = (RelativeLayout) v.findViewById(R.id.image_layout);
                mImagesCounterLayout = (LinearLayout) v.findViewById(R.id.images_counter_layout);

                mImagesCounterLabel = (TextView) v.findViewById(R.id.images_counter_label);

                mItemImg = (RoundedImageView) v.findViewById(R.id.item_image);

                mVideoImg = (RoundedImageView) v.findViewById(R.id.video_image);
                mItemPlayVideo = (ImageView) v.findViewById(R.id.video_play_image);

                mImageProgressBar = (ProgressBar) v.findViewById(R.id.image_progress_bar);
                mVideoProgressBar = (ProgressBar) v.findViewById(R.id.video_progress_bar);

                mItemDescription = (EmojiconTextView) v.findViewById(R.id.itemDescription);


                mItemMenuButton = (ImageView) v.findViewById(R.id.itemMenuButton);
                mItemLikeImg = (ImageView) v.findViewById(R.id.itemLikeImg);
                mItemCommentImg = (ImageView) v.findViewById(R.id.itemCommentImg);
                mItemRepostImg = (ImageView) v.findViewById(R.id.itemRepostImg);
                mItemTimeAgo = (TextView) v.findViewById(R.id.itemTimeAgo);

                //mItemRepostsCount = (TextView) v.findViewById(R.id.itemRepostsCount);

                cardContainer = (LinearLayout) v.findViewById(R.id.cardContainer);
                mItemLikeButton = (MaterialRippleLayout) v.findViewById(R.id.itemLikeButton);
                mItemCommentButton = (MaterialRippleLayout) v.findViewById(R.id.itemCommentButton);
                mItemRepostButton = (MaterialRippleLayout) v.findViewById(R.id.itemRepostButton);

                mLinkContainer = (LinearLayout) v.findViewById(R.id.linkContainer);
                mLinkTitle = (TextView) v.findViewById(R.id.linkTitle);
                mLinkDescription = (TextView) v.findViewById(R.id.linkDescription);
                mLinkImage = (ImageView) v.findViewById(R.id.linkImage);

                // Repost

                mReHeaderContainer = (ConstraintLayout) v.findViewById(R.id.reHeaderContainer);
                mReMessageContainer = (LinearLayout) v.findViewById(R.id.reMessageContainer);
                mReBodyContainer = (LinearLayout) v.findViewById(R.id.reBodyContainer);
                mCardRepostContainer = (LinearLayout) v.findViewById(R.id.cardRepostContainer);

                mReAuthorPhoto = (CircularImageView) v.findViewById(R.id.reAuthorPhoto);
                mReAuthorIcon = (CircularImageView) v.findViewById(R.id.reAuthorIcon);

                mReAuthor = (TextView) v.findViewById(R.id.reAuthor);
                mReAuthorUsername = (TextView) v.findViewById(R.id.reAuthorUsername);

                mReImageProgressBar = (ProgressBar) v.findViewById(R.id.repost_image_progress_bar);
                mReItemImg = (RoundedImageView) v.findViewById(R.id.repost_item_image);
                videoImage = (ImageView) v.findViewById(R.id.video_image);
                mReImageLayout = (RelativeLayout) v.findViewById(R.id.repost_image_layout);

                mReImagesCounterLayout = (LinearLayout) v.findViewById(R.id.repost_images_counter_layout);
                mReImagesCounterLabel = (TextView) v.findViewById(R.id.repost_images_counter_label);

                mReVideoProgressBar = (ProgressBar) v.findViewById(R.id.repost_video_progress_bar);
                mReVideoLayout = (RelativeLayout) v.findViewById(R.id.repost_video_layout);
                mReVideoImg = (RoundedImageView) v.findViewById(R.id.repost_video_image);
                mRePlayVideo = (ImageView) v.findViewById(R.id.repost_video_play_image);

                mReDescription = (EmojiconTextView) v.findViewById(R.id.reDescription);
                mReTimeAgo = (TextView) v.findViewById(R.id.reTimeAgo);

                mReLinkContainer = (LinearLayout) v.findViewById(R.id.reLinkContainer);
                mReLinkTitle = (TextView) v.findViewById(R.id.reLinkTitle);
                mReLinkDescription = (TextView) v.findViewById(R.id.reLinkDescription);
                mReLinkImage = (ImageView) v.findViewById(R.id.reLinkImage);

                // Counters

                mItemCountersContainer = (LinearLayout) v.findViewById(R.id.item_counters_container);

                mItemCountersContainerButton = (MaterialRippleLayout) v.findViewById(R.id.item_counters_container_button);
                mItemLikeListButton = (MaterialRippleLayout) v.findViewById(R.id.item_counter_like);

                //mItemLikesCountImage = (ImageView) v.findViewById(R.id.item_likes_icon);
                //mItemCommentsCountImage = (ImageView) v.findViewById(R.id.item_comments_icon);

                mItemLikesCountText = (TextView) v.findViewById(R.id.item_likes_count);
                mItemCommentsCountText = (TextView) v.findViewById(R.id.item_comments_count);

                view3 = (View) v.findViewById(R.id.view3);

                clCommentedImage = (ConstraintLayout) v.findViewById(R.id.clCommentedImage);
                cvUserOne = (CircularImageView) v.findViewById(R.id.cvUserOne);
                cvUserTwo = (CircularImageView) v.findViewById(R.id.cvUserTwo);

                //doubleTap = (DoubleTapLikeView) v.findViewById(R.id.layout_double_tap_like);

            } else if (itemType == 1) {

                mAdCard = (CardView) v.findViewById(R.id.adCard);

               // mAdView = (AdView) v.findViewById(R.id.adView);
            }

        }

    }

    public AdvancedItemListAdapter(Context ctx, List<Item> items) {

        this.context = ctx;
        this.items = items;

        if (imageLoader == null) {

            imageLoader = App.getInstance().getImageLoader();
        }

        mTagSelectingTextview = new TagSelectingTextview();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == 0) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_row, parent, false);
            return new ViewHolder(v, viewType);

        } else if (viewType == 1) {

            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.ad_item, parent, false);
            return new ViewHolder(v, viewType);

        }

        return null;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        final Item p = items.get(position);

        if (p.getAd() == 0) {

            onBindItem(holder, position);

        } else {

          //  AdRequest adRequest = new AdRequest.Builder().build();
          //  holder.mAdView.loadAd(adRequest);

            holder.mAdCard.setVisibility(View.VISIBLE);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    public void onBindItem(ViewHolder holder, final int position) {

        final Item p = items.get(position);

        holder.mItemCountersContainer.setVisibility(View.GONE);

        holder.mLinkContainer.setVisibility(View.GONE);

        holder.mItemPlayVideo.setVisibility(View.GONE);
        holder.mImageProgressBar.setVisibility(View.GONE);
        holder.mVideoProgressBar.setVisibility(View.GONE);

        holder.mImageLayout.setVisibility(View.GONE);
        holder.mImagesCounterLayout.setVisibility(View.GONE);
        holder.mVideoLayout.setVisibility(View.GONE);

       // holder.mAccessModeLayout.setVisibility(View.GONE);
       // holder.mLocationLayout.setVisibility(View.GONE);

        holder.mItemAuthorPhoto.setVisibility(View.VISIBLE);

        //double tap clicks
        /*holder.doubleTap.setOnTapListener(new DoubleTapLikeView.OnTapListener() {
            @Override
            public void onDoubleTap(View view) {
                // If you want to customize animation, use method below and start your Animation here.
                // mDoubleTapLikeView.disableAnimation(false);
            }

            @Override
            public void onTap() {
                // This method will be called if user didn't tap again after PRESS_TIME_TERM (default is 200)
                // So keep PRESS_TIME_GAP short ( 200~400 )^.
                // Due to Thread for single Tap, if you want to change UI through "onTap()", you should use Activity.runOnUiThread()
            }
        });*/

        //ViewUtils.setOnDoubleClickListener();

        holder.cardContainer.setOnClickListener(new DoubleClickListener() {
            @Override
            public void onSingleClick(View view) {
                // Single click action
                Log.d("DOUBLE_CLICKED","noo");
            }

            @Override
            public void onDoubleClick(View view) {
                // Double click action
                Log.d("DOUBLE_CLICKED","yess");

                if (p.getRemoveAt() != 0) {

                    return;
                }

                notifyDataSetChanged();

                if (p.isMyLike()) {

                    p.setMyLike(false);
                    p.setLikesCount(p.getLikesCount() - 1);

                } else {

                    p.setMyLike(true);
                    p.setLikesCount(p.getLikesCount() + 1);

                    holder.mItemLikeImg.setImageResource(R.drawable.ic_like_active);
                }

                animateIcon(holder.mItemLikeImg);

                CustomRequest jsonReq = new CustomRequest(Request.Method.POST, METHOD_LIKES_LIKE, null,
                        response -> {
                            try {
                                if (!response.getBoolean("error")) {
                                    p.setLikesCount(response.getInt("likesCount"));
                                    p.setMyLike(response.getBoolean("myLike"));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } finally {
                                Log.e("Item.Like", response.toString());
                            }
                        }, error -> Log.e("Item.Like", error.toString())) {

                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("accountId", Long.toString(App.getInstance().getId()));
                        params.put("accessToken", App.getInstance().getAccessToken());
                        params.put("itemType", Integer.toString(ITEM_TYPE_POST));
                        params.put("itemId", Long.toString(p.getId()));

                        return params;
                    }
                };

                App.getInstance().addToRequestQueue(jsonReq);
            }
        });
        holder.mItemDescription.setOnClickListener(new DoubleClickListener() {
            @Override
            public void onSingleClick(View view) {
                // Single click action
                Log.d("DOUBLE_CLICKED","noo");
            }

            @Override
            public void onDoubleClick(View view) {
                // Double click action
                Log.d("DOUBLE_CLICKED","yess");

                if (p.getRemoveAt() != 0) {

                    return;
                }

                notifyDataSetChanged();

                if (p.isMyLike()) {

                    p.setMyLike(false);
                    p.setLikesCount(p.getLikesCount() - 1);

                } else {

                    p.setMyLike(true);
                    p.setLikesCount(p.getLikesCount() + 1);

                    holder.mItemLikeImg.setImageResource(R.drawable.ic_like_active);
                }

                animateIcon(holder.mItemLikeImg);

                CustomRequest jsonReq = new CustomRequest(Request.Method.POST, METHOD_LIKES_LIKE, null,
                        response -> {

                            try {
                                if (!response.getBoolean("error")) {
                                    p.setLikesCount(response.getInt("likesCount"));
                                    p.setMyLike(response.getBoolean("myLike"));
                                }

                            } catch (JSONException e) {

                                e.printStackTrace();

                            } finally {

                                Log.e("Item.Like", response.toString());
                            }
                        }, error -> Log.e("Item.Like", error.toString())) {

                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("accountId", Long.toString(App.getInstance().getId()));
                        params.put("accessToken", App.getInstance().getAccessToken());
                        params.put("itemType", Integer.toString(ITEM_TYPE_POST));
                        params.put("itemId", Long.toString(p.getId()));

                        return params;
                    }
                };

                App.getInstance().addToRequestQueue(jsonReq);
            }
        });

        holder.mItemAuthorPhoto.setOnClickListener(v -> {

            if (p.getGroupId() == 0) {

                Intent intent = new Intent(context, ProfileActivity.class);
                intent.putExtra("profileId", p.getFromUserId());
                context.startActivity(intent);

            } else {

                Intent intent = new Intent(context, GroupActivity.class);
                intent.putExtra("groupId", p.getGroupId());
                context.startActivity(intent);
            }
        });

        if (p.getFromUserPhotoUrl().length() != 0) {

            imageLoader.get(p.getFromUserPhotoUrl(), ImageLoader.getImageListener(holder.mItemAuthorPhoto, R.drawable.profile_default_photo, R.drawable.profile_default_photo));

        } else {

            holder.mItemAuthorPhoto.setVisibility(View.VISIBLE);
            holder.mItemAuthorPhoto.setImageResource(R.drawable.profile_default_photo);
        }

        if (p.getFromUserVerify() == 1) {

            holder.mItemAuthorIcon.setVisibility(View.VISIBLE);

        } else {

            holder.mItemAuthorIcon.setVisibility(View.GONE);
        }

        holder.mItemAuthor.setVisibility(View.VISIBLE);
        holder.mItemAuthor.setText(p.getFromUserFullname());

        holder.mItemAuthor.setOnClickListener(v -> {

            if (p.getGroupId() == 0) {

                Intent intent = new Intent(context, ProfileActivity.class);
                intent.putExtra("profileId", p.getFromUserId());
                context.startActivity(intent);

            } else {

                Intent intent = new Intent(context, GroupActivity.class);
                intent.putExtra("groupId", p.getGroupId());
                context.startActivity(intent);
            }
        });

//        if (p.getFeeling() == 0) {
//
//            holder.mItemFeelingIcon.setVisibility(View.GONE);
//            holder.mItemFeelingTitle.setVisibility(View.GONE);
//
//        } else {
//
//            holder.mItemFeelingIcon.setVisibility(View.VISIBLE);
//            holder.mItemFeelingTitle.setVisibility(View.VISIBLE);
//
//            ImageLoader imageLoader = App.getInstance().getImageLoader();
//
//            imageLoader.get(Constants.WEB_SITE + "feelings/" + p.getFeeling() + ".png", ImageLoader.getImageListener(holder.mItemFeelingIcon, R.drawable.mood, R.drawable.mood));
//        }

       // holder.mItemAuthorOnlineIcon.setVisibility(View.GONE);

        if (getLocation(p).length() > 0) {

      //      holder.mLocationLayout.setVisibility(View.VISIBLE);
           // holder.mLocationLabel.setText(getLocation(p));
        }

//        if (p.getGroupId() == 0) {
//
//            holder.mAccessModeLayout.setVisibility(View.VISIBLE);
//            if (p.getAccessMode() == 0) {
//                holder.mAccessModeLabel.setText(context.getString(R.string.label_post_to_public));
//                holder.mAccessModeImage.setImageResource(R.drawable.ic_public);
//
//            } else {
//                holder.mAccessModeLabel.setText(context.getString(R.string.label_post_to_friends));
//                holder.mAccessModeImage.setImageResource(R.drawable.ic_share_friend);
//            }
//        }

        if (p.getImgUrl().length() != 0){

            holder.mImageLayout.setVisibility(View.VISIBLE);
            holder.mItemImg.setVisibility(View.VISIBLE);
            holder.mImageProgressBar.setVisibility(View.VISIBLE);

            final ProgressBar progressView = holder.mImageProgressBar;
            final ImageView imageView = holder.mItemImg;

            Picasso.with(context)
                    .load(p.getImgUrl())
                    .into(holder.mItemImg, new Callback() {

                        @Override
                        public void onSuccess() {

                            progressView.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError() {

                            progressView.setVisibility(View.GONE);
                            imageView.setImageResource(R.drawable.img_loading_error);
                        }
                    });
        }

        if (p.getImagesCount() != 0) {

            holder.mImagesCounterLayout.setVisibility(View.VISIBLE);
            holder.mImagesCounterLabel.setText(" +" + p.getImagesCount());
        }

        if (p.getVideoUrl() != null && p.getVideoUrl().length() != 0) {

            holder.mVideoLayout.setVisibility(View.VISIBLE);
            holder.mVideoImg.setVisibility(View.VISIBLE);
            holder.mVideoProgressBar.setVisibility(View.VISIBLE);

            if (p.getPreviewVideoImgUrl().length() != 0) {

                final ImageView imageView = holder.mVideoImg;
                final ProgressBar progressView = holder.mVideoProgressBar;
                final ImageView playButtonView = holder.mItemPlayVideo;

                Picasso.with(context)
                        .load(p.getPreviewVideoImgUrl())
                        .into(holder.mVideoImg, new Callback() {

                            @Override
                            public void onSuccess() {

                                progressView.setVisibility(View.GONE);
                                playButtonView.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onError() {

                                progressView.setVisibility(View.GONE);
                                playButtonView.setVisibility(View.GONE);
                                imageView.setImageResource(R.drawable.ic_video_preview);
                            }
                        });

            } else {

                holder.mVideoProgressBar.setVisibility(View.GONE);
                holder.mVideoImg.setVisibility(View.VISIBLE);
                holder.mItemPlayVideo.setVisibility(View.GONE);
                holder.mVideoImg.setImageResource(R.drawable.ic_video_preview);
            }

        }
        else if (p.getYouTubeVideoUrl() != null && p.getYouTubeVideoUrl().length() != 0) {

            holder.mVideoLayout.setVisibility(View.VISIBLE);
            holder.mVideoImg.setVisibility(View.VISIBLE);
            holder.mVideoProgressBar.setVisibility(View.VISIBLE);

            final ProgressBar progressView = holder.mVideoProgressBar;
            final ImageView playButtonView = holder.mItemPlayVideo;

            Picasso.with(context)
                    .load(p.getYouTubeVideoImg())
                    .into(holder.mVideoImg, new Callback() {

                        @Override
                        public void onSuccess() {

                            progressView.setVisibility(View.GONE);
                            playButtonView.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onError() {
                            // TODO Auto-generated method stub

                        }
                    });

        } else {

            holder.mVideoImg.setVisibility(View.GONE);
        }

        holder.mItemImg.setOnClickListener(new DoubleClickListener() {
            @Override
            public void onSingleClick(View view) {
                // Single click action
                Log.d("DOUBLE_CLICKED","noo");
                ArrayList<MediaItem> images = new ArrayList<>();
                images.add(new MediaItem("", "", p.getImgUrl(), "", 0));

                Intent i = new Intent(context, MediaViewerActivity.class);
                i.putExtra("position", 0);
                i.putExtra("itemId", p.getId());
                i.putExtra("count", p.getImagesCount());
                i.putParcelableArrayListExtra("images", images);
                context.startActivity(i);
            }

            @Override
            public void onDoubleClick(View view) {
                // Double click action
                Log.d("DOUBLE_CLICKED","yess");

                if (p.getRemoveAt() != 0) {

                    return;
                }

                notifyDataSetChanged();

                if (p.isMyLike()) {

                    p.setMyLike(false);
                    p.setLikesCount(p.getLikesCount() - 1);

                } else {

                    p.setMyLike(true);
                    p.setLikesCount(p.getLikesCount() + 1);

                    holder.mItemLikeImg.setImageResource(R.drawable.ic_like_active);
                }

                animateIcon(holder.mItemLikeImg);

                CustomRequest jsonReq = new CustomRequest(Request.Method.POST, METHOD_LIKES_LIKE, null,
                        response -> {

                            try {

                                if (!response.getBoolean("error")) {

                                    p.setLikesCount(response.getInt("likesCount"));
                                    p.setMyLike(response.getBoolean("myLike"));
                                }

                            } catch (JSONException e) {

                                e.printStackTrace();

                            } finally {

                                Log.e("Item.Like", response.toString());
                            }
                        }, error -> Log.e("Item.Like", error.toString())) {

                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("accountId", Long.toString(App.getInstance().getId()));
                        params.put("accessToken", App.getInstance().getAccessToken());
                        params.put("itemType", Integer.toString(ITEM_TYPE_POST));
                        params.put("itemId", Long.toString(p.getId()));

                        return params;
                    }
                };

                App.getInstance().addToRequestQueue(jsonReq);
            }
        });

//        holder.mItemImg.setOnClickListener(v -> {
//            ArrayList<MediaItem> images = new ArrayList<>();
//            images.add(new MediaItem("", "", p.getImgUrl(), "", 0));
//
//            Intent i = new Intent(context, MediaViewerActivity.class);
//            i.putExtra("position", 0);
//            i.putExtra("itemId", p.getId());
//            i.putExtra("count", p.getImagesCount());
//            i.putParcelableArrayListExtra("images", images);
//            context.startActivity(i);
//        });

//        holder.mVideoImg.setOnClickListener(v -> {
//
//            if (p.getVideoUrl().length() != 0) {
//
//                watchVideo(p.getVideoUrl());
//
//            } else {
//                watchYoutubeVideo(p.getYouTubeVideoCode());
//            }
//        });

        holder.mItemPlayVideo.setOnClickListener(v -> {

            if (p.getVideoUrl().length() != 0) {

                watchVideo(p.getVideoUrl());

            } else {

                watchYoutubeVideo(p.getYouTubeVideoCode());
            }
        });

        if (p.getPostType() == POST_TYPE_DEFAULT) {

            if (p.getPost().length() != 0) {

                holder.mItemDescription.setVisibility(View.VISIBLE);
                holder.mItemDescription.setText(p.getPost().replaceAll("<br>", "\n"));

                holder.mItemDescription.setMovementMethod(LinkMovementMethod.getInstance());

                String textHtml = p.getPost();

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {

                    holder.mItemDescription.setText(mTagSelectingTextview.addClickablePart(Html.fromHtml(textHtml, Html.FROM_HTML_MODE_LEGACY).toString(), this, hashTagHyperLinkDisabled, HASHTAGS_COLOR), TextView.BufferType.SPANNABLE);

                } else {

                    holder.mItemDescription.setText(mTagSelectingTextview.addClickablePart(Html.fromHtml(textHtml).toString(), this, hashTagHyperLinkDisabled, HASHTAGS_COLOR), TextView.BufferType.SPANNABLE);
                }

                holder.mItemDescription.setOnLongClickListener(v -> {

                    ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("msg", p.getPost().replaceAll("<br>", "\n"));
                    clipboard.setPrimaryClip(clip);

                    Toast.makeText(context, context.getString(R.string.msg_copied_to_clipboard), Toast.LENGTH_SHORT).show();

                    return false;
                });

            } else {
                holder.mItemDescription.setVisibility(View.GONE);
            }

        } else if (p.getPostType() == POST_TYPE_PHOTO_UPDATE) {

            holder.mItemDescription.setVisibility(View.VISIBLE);
            holder.mItemDescription.setText(p.getFromUserFullname() + " " + context.getString(R.string.label_updated_profile_photo));

        } else {

            // POST_TYPE_COVER_UPDATE

            holder.mItemDescription.setVisibility(View.VISIBLE);
            holder.mItemDescription.setText(p.getFromUserFullname() + " " + context.getString(R.string.label_updated_cover_photo));
        }

        holder.mItemTimeAgo.setVisibility(View.VISIBLE);
        holder.mItemTimeAgo.setText(p.getTimeAgo());


        holder.mItemMenuButton.setVisibility(View.VISIBLE);

        holder.mItemMenuButton.setOnClickListener(view -> onItemMenuButtonClick(view, p, position));

        final ImageView mItemMenuButton = holder.mItemMenuButton;

        holder.mItemMenuButton.setOnTouchListener((view, motionEvent) -> {

            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {

                animateIcon(mItemMenuButton);
            }
            return false;
        });
        //p.getCommentsCount()
        //p.getLikesCount()

        if (p.getCommentsCount() == 0){
            holder.view3.setVisibility(View.GONE);
            holder.clCommentedImage.setVisibility(View.GONE);
        }
        else if(p.getCommentsCount() == 1){
            holder.view3.setVisibility(View.VISIBLE);
            holder.clCommentedImage.setVisibility(View.VISIBLE);
            holder.cvUserOne.setVisibility(View.VISIBLE);
            holder.cvUserTwo.setVisibility(View.GONE);
        }
        else if(p.getCommentsCount() == 2){
            holder.view3.setVisibility(View.VISIBLE);
            holder.clCommentedImage.setVisibility(View.VISIBLE);
            holder.cvUserOne.setVisibility(View.VISIBLE);
            holder.cvUserTwo.setVisibility(View.VISIBLE);
        }


        if (p.getCommentsCount() > 0 || p.getLikesCount() > 0) {
          //  holder.mItemCommentsCountImage.setVisibility(View.GONE);
            holder.mItemCommentsCountText.setVisibility(View.GONE);

           // holder.mItemLikesCountImage.setVisibility(View.GONE);
            holder.mItemLikesCountText.setVisibility(View.GONE);

            holder.mItemCountersContainer.setVisibility(View.VISIBLE);

            if (p.getCommentsCount() > 0) {

                //holder.mItemCommentsCountImage.setVisibility(View.VISIBLE);
                holder.mItemCommentsCountText.setVisibility(View.VISIBLE);

                holder.mItemCommentsCountText.setText(p.getCommentsCount() +" replies");
            }

            if (p.getLikesCount() > 0) {

               // holder.mItemLikesCountImage.setVisibility(View.VISIBLE);
                holder.mItemLikesCountText.setVisibility(View.VISIBLE);

                holder.mItemLikesCountText.setText(p.getLikesCount() + " likes");
            }

        } else {

            holder.mItemCountersContainer.setVisibility(View.GONE);
        }

        holder.mItemCountersContainerButton.setOnClickListener(view -> {

            showCommentsDialog(p, position);

            // viewItem(p);
        });

        holder.mItemLikeListButton.setOnClickListener(view -> {

            Intent intent = new Intent(context, LikersActivity.class);
            intent.putExtra("itemId", p.getId());
            intent.putExtra("itemType", ITEM_TYPE_POST);
            context.startActivity(intent);
        });

        if (p.getRePostsCount() > 0) {

           // holder.mItemRepostsCount.setVisibility(View.VISIBLE);
           // holder.mItemRepostsCount.setText(Integer.toString(p.getRePostsCount()));

        } else {

            //holder.mItemRepostsCount.setVisibility(View.GONE);
        }

        if (p.isMyLike()) {

            holder.mItemLikeImg.setImageResource(R.drawable.ic_liked);

        }
        else {

            holder.mItemLikeImg.setImageResource(R.drawable.ic_like_unselect);
        }

        final ImageView imgLike = holder.mItemLikeImg;

        holder.mItemLikeButton.setOnClickListener(view -> {
            if (p.getRemoveAt() != 0) {
                return;
            }
            notifyDataSetChanged();
            if (p.isMyLike()) {
                p.setMyLike(false);
                p.setLikesCount(p.getLikesCount() - 1);
            }
            else {
                p.setMyLike(true);
                p.setLikesCount(p.getLikesCount() + 1);
                imgLike.setImageResource(R.drawable.ic_like_active);
            }
            animateIcon(imgLike);
            CustomRequest jsonReq = new CustomRequest(Request.Method.POST, METHOD_LIKES_LIKE, null,
                    response -> {
                        try {
                            if (!response.getBoolean("error")) {
                                p.setLikesCount(response.getInt("likesCount"));
                                p.setMyLike(response.getBoolean("myLike"));
                            }
                        } catch (JSONException e) {

                            e.printStackTrace();

                        } finally {

                            Log.e("Item.Like", response.toString());
                        }
                    }, error -> Log.e("Item.Like", error.toString())) {

                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("accountId", Long.toString(App.getInstance().getId()));
                    params.put("accessToken", App.getInstance().getAccessToken());
                    params.put("itemType", Integer.toString(ITEM_TYPE_POST));
                    params.put("itemId", Long.toString(p.getId()));

                    return params;
                }
            };

            App.getInstance().addToRequestQueue(jsonReq);
        });

        holder.mItemCommentButton.setOnClickListener(view -> showCommentsDialog(p, position));


        holder.mItemRepostButton.setOnClickListener(view ->
                onItemMenuButtonClickListener.onItemClick(p,  ITEM_ACTION_REPOST, position));

        if (p.getUrlPreviewLink() != null && p.getUrlPreviewLink().length() > 0) {

            holder.mLinkContainer.setVisibility(View.VISIBLE);

            holder.mLinkContainer.setOnClickListener(v -> {

                if (!p.getUrlPreviewLink().startsWith("https://") && !p.getUrlPreviewLink().startsWith("http://")){

                    p.setUrlPreviewLink("http://" + p.getUrlPreviewLink());
                }

                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(p.getUrlPreviewLink()));
                context.startActivity(i);
            });

            if (p.getUrlPreviewImage() != null && p.getUrlPreviewImage().length() != 0) {

                imageLoader.get(p.getUrlPreviewImage(), ImageLoader.getImageListener(holder.mLinkImage, R.drawable.img_link, R.drawable.img_link));

            } else {

                holder.mLinkImage.setImageResource(R.drawable.img_link);
            }

            if (p.getUrlPreviewTitle() != null && p.getUrlPreviewTitle().length() != 0) {

                holder.mLinkTitle.setText(p.getUrlPreviewTitle());

            } else {

                holder.mLinkTitle.setText("Link");
            }

            if (p.getUrlPreviewDescription() != null && p.getUrlPreviewDescription().length() != 0) {

                holder.mLinkDescription.setText(p.getUrlPreviewDescription());

            } else {

                holder.mLinkDescription.setText("Link");
            }
        }

        // Repost

        if (p.getRePostId() != 0) {

            holder.mCardRepostContainer.setVisibility(View.VISIBLE);

            holder.mReImageLayout.setVisibility(View.GONE);
            holder.mReVideoLayout.setVisibility(View.GONE);

            if (p.getRePostRemoveAt() == 0) {

                // original post available

                holder.mReMessageContainer.setVisibility(View.GONE);
                holder.mReLinkContainer.setVisibility(View.GONE);

                holder.mReAuthorPhoto.setVisibility(View.VISIBLE);

                holder.mReAuthorPhoto.setOnClickListener(v -> {

                    Intent intent = new Intent(context, ViewItemActivity.class);
                    intent.putExtra("itemId", p.getRePostId());
                    context.startActivity(intent);
                });

                if (p.getRePostFromUserPhotoUrl().length() != 0) {

                    imageLoader.get(p.getRePostFromUserPhotoUrl(), ImageLoader.getImageListener(holder.mReAuthorPhoto, R.drawable.profile_default_photo, R.drawable.profile_default_photo));

                } else {

                    holder.mReAuthorPhoto.setVisibility(View.VISIBLE);
                    holder.mReAuthorPhoto.setImageResource(R.drawable.profile_default_photo);
                }

                if (p.getRePostFromUserVerify() == 1) {

                    holder.mReAuthorIcon.setVisibility(View.VISIBLE);

                } else {

                    holder.mReAuthorIcon.setVisibility(View.GONE);
                }

                holder.mReAuthor.setVisibility(View.VISIBLE);
                holder.mReAuthor.setText(p.getRePostFromUserFullname());

                holder.mReAuthor.setOnClickListener(v -> {

                    Intent intent = new Intent(context, ViewItemActivity.class);
                    intent.putExtra("itemId", p.getRePostId());
                    context.startActivity(intent);
                });

                holder.mReAuthorUsername.setVisibility(View.VISIBLE);
                holder.mReAuthorUsername.setText("@" + p.getRePostFromUserUsername());

                if (p.getRePostImgUrl().length() != 0) {

                    holder.mReImageLayout.setVisibility(View.VISIBLE);

                    if (p.getReImagesCount() != 0) {

                        holder.mReImagesCounterLayout.setVisibility(View.VISIBLE);
                        holder.mReImagesCounterLabel.setText(" +" + p.getReImagesCount());

                    } else {

                        holder.mReImagesCounterLayout.setVisibility(View.GONE);
                    }

                    holder.mReItemImg.setVisibility(View.VISIBLE);
                    holder.mReImageProgressBar.setVisibility(View.VISIBLE);

                    final ProgressBar reProgressView = holder.mReImageProgressBar;
                    final ImageView reImageView = holder.mReItemImg;

                    Picasso.with(context)
                            .load(p.getRePostImgUrl())
                            .into(holder.mReItemImg, new Callback() {

                                @Override
                                public void onSuccess() {

                                    reProgressView.setVisibility(View.GONE);
                                }

                                @Override
                                public void onError() {

                                    reProgressView.setVisibility(View.GONE);
                                    reImageView.setImageResource(R.drawable.img_loading_error);
                                }
                            });
                }

                if (p.getReVideoUrl() != null && p.getReVideoUrl().length() != 0) {

                    holder.mReVideoLayout.setVisibility(View.VISIBLE);

                    holder.mRePlayVideo.setVisibility(View.GONE);
                    holder.mReVideoImg.setVisibility(View.VISIBLE);
                    holder.mReVideoProgressBar.setVisibility(View.VISIBLE);

                    if (p.getRePreviewVideoImageUrl() != null && p.getRePreviewVideoImageUrl().length() != 0) {

                        final ImageView reImageView = holder.mReVideoImg;
                        final ProgressBar reProgressView = holder.mReVideoProgressBar;
                        final ImageView rePlayButtonView = holder.mRePlayVideo;

                        Picasso.with(context)
                                .load(p.getRePreviewVideoImageUrl())
                                .into(holder.mReVideoImg, new Callback() {

                                    @Override
                                    public void onSuccess() {

                                        reProgressView.setVisibility(View.GONE);
                                        rePlayButtonView.setVisibility(View.VISIBLE);
                                    }

                                    @Override
                                    public void onError() {

                                        reProgressView.setVisibility(View.GONE);
                                        rePlayButtonView.setVisibility(View.VISIBLE);
                                        reImageView.setImageResource(R.drawable.img_loading_error);
                                    }
                                });

                    } else {

                        holder.mReVideoProgressBar.setVisibility(View.GONE);
                        holder.mReVideoImg.setImageResource(R.drawable.ic_video_preview);
                    }

                } else if (p.getReYouTubeVideoUrl() != null && p.getReYouTubeVideoUrl().length() != 0) {

                    holder.mReVideoLayout.setVisibility(View.VISIBLE);

                    holder.mReVideoImg.setVisibility(View.VISIBLE);
                    holder.mReVideoProgressBar.setVisibility(View.VISIBLE);

                    final ProgressBar reProgressView = holder.mReVideoProgressBar;
                    final ImageView rePlayButtonView = holder.mRePlayVideo;

                    Picasso.with(context)
                            .load(p.getReYouTubeVideoImg())
                            .into(holder.mReVideoImg, new Callback() {
                                @Override
                                public void onSuccess() {
                                    reProgressView.setVisibility(View.GONE);
                                    rePlayButtonView.setVisibility(View.VISIBLE);
                                }
                                @Override
                                public void onError() {
                                    // TODO Auto-generated method stub
                                }
                            });
                }

                holder.mReItemImg.setOnClickListener(new DoubleClickListener() {
                    @Override
                    public void onSingleClick(View view) {
                        // Single click action
                        Log.d("DOUBLE_CLICKED","noo");
                        ArrayList<MediaItem> images = new ArrayList<>();
                        images.add(new MediaItem("", "", p.getRePostImgUrl(), "", 0));

                        Intent i = new Intent(context, MediaViewerActivity.class);
                        i.putExtra("position", 0);
                        i.putExtra("itemId", p.getRePostId());
                        i.putExtra("count", p.getReImagesCount());
                        i.putParcelableArrayListExtra("images", images);
                        context.startActivity(i);
                    }

                    @Override
                    public void onDoubleClick(View view) {
                        // Double click action
                        Log.d("DOUBLE_CLICKED","yess");

                        if (p.getRemoveAt() != 0) {

                            return;
                        }

                        notifyDataSetChanged();

                        if (p.isMyLike()) {

                            p.setMyLike(false);
                            p.setLikesCount(p.getLikesCount() - 1);

                        } else {

                            p.setMyLike(true);
                            p.setLikesCount(p.getLikesCount() + 1);

                            holder.mItemLikeImg.setImageResource(R.drawable.ic_like_active);
                        }

                        animateIcon(holder.mItemLikeImg);

                        CustomRequest jsonReq = new CustomRequest(Request.Method.POST, METHOD_LIKES_LIKE, null,
                                response -> {

                                    try {

                                        if (!response.getBoolean("error")) {

                                            p.setLikesCount(response.getInt("likesCount"));
                                            p.setMyLike(response.getBoolean("myLike"));
                                        }

                                    } catch (JSONException e) {

                                        e.printStackTrace();

                                    } finally {

                                        Log.e("Item.Like", response.toString());
                                    }
                                }, error -> Log.e("Item.Like", error.toString())) {

                            @Override
                            protected Map<String, String> getParams() {
                                Map<String, String> params = new HashMap<String, String>();
                                params.put("accountId", Long.toString(App.getInstance().getId()));
                                params.put("accessToken", App.getInstance().getAccessToken());
                                params.put("itemType", Integer.toString(ITEM_TYPE_POST));
                                params.put("itemId", Long.toString(p.getId()));

                                return params;
                            }
                        };

                        App.getInstance().addToRequestQueue(jsonReq);
                    }
                });

//                holder.mReItemImg.setOnClickListener(v -> {
//                    ArrayList<MediaItem> images = new ArrayList<>();
//                    images.add(new MediaItem("", "", p.getRePostImgUrl(), "", 0));
//
//                    Intent i = new Intent(context, MediaViewerActivity.class);
//                    i.putExtra("position", 0);
//                    i.putExtra("itemId", p.getRePostId());
//                    i.putExtra("count", p.getReImagesCount());
//                    i.putParcelableArrayListExtra("images", images);
//                    context.startActivity(i);
//                });
                holder.mReVideoImg.setOnClickListener(new DoubleClickListener() {
                    @Override
                    public void onSingleClick(View view) {
                        // Single click action
                        if (p.getReVideoUrl().length() != 0) {

                            watchVideo(p.getReVideoUrl());

                        } else {

                            watchYoutubeVideo(p.getReYouTubeVideoCode());
                        }
                    }
                    @Override
                    public void onDoubleClick(View view) {
                        // Double click action
                        Log.d("DOUBLE_CLICKED","yess");

                        if (p.getRemoveAt() != 0) {

                            return;
                        }

                        notifyDataSetChanged();

                        if (p.isMyLike()) {

                            p.setMyLike(false);
                            p.setLikesCount(p.getLikesCount() - 1);

                        } else {

                            p.setMyLike(true);
                            p.setLikesCount(p.getLikesCount() + 1);

                            holder.mItemLikeImg.setImageResource(R.drawable.ic_like_active);
                        }

                        animateIcon(holder.mItemLikeImg);

                        CustomRequest jsonReq = new CustomRequest(Request.Method.POST, METHOD_LIKES_LIKE, null,
                                response -> {

                                    try {

                                        if (!response.getBoolean("error")) {

                                            p.setLikesCount(response.getInt("likesCount"));
                                            p.setMyLike(response.getBoolean("myLike"));
                                        }

                                    } catch (JSONException e) {

                                        e.printStackTrace();

                                    } finally {

                                        Log.e("Item.Like", response.toString());
                                    }
                                }, error -> Log.e("Item.Like", error.toString())) {

                            @Override
                            protected Map<String, String> getParams() {
                                Map<String, String> params = new HashMap<String, String>();
                                params.put("accountId", Long.toString(App.getInstance().getId()));
                                params.put("accessToken", App.getInstance().getAccessToken());
                                params.put("itemType", Integer.toString(ITEM_TYPE_POST));
                                params.put("itemId", Long.toString(p.getId()));

                                return params;
                            }
                        };

                        App.getInstance().addToRequestQueue(jsonReq);
                    }
                });

/*


                holder.mReVideoImg.setOnClickListener(v -> {

                    if (p.getReVideoUrl().length() != 0) {

                        watchVideo(p.getReVideoUrl());

                    } else {

                        watchYoutubeVideo(p.getReYouTubeVideoCode());
                    }
                });
*/

                holder.mRePlayVideo.setOnClickListener(v -> {

                    if (p.getReVideoUrl().length() != 0) {

                        watchVideo(p.getReVideoUrl());

                    } else {

                        watchYoutubeVideo(p.getReYouTubeVideoCode());
                    }
                });

                if (p.getRePostPost().length() != 0) {

                    holder.mReDescription.setVisibility(View.VISIBLE);
                    holder.mReDescription.setText(p.getRePostPost().replaceAll("<br>", "\n"));

                    holder.mReDescription.setMovementMethod(LinkMovementMethod.getInstance());

                    String textHtml = p.getRePostPost();

                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {

                        holder.mReDescription.setText(mTagSelectingTextview.addClickablePart(Html.fromHtml(textHtml, Html.FROM_HTML_MODE_LEGACY).toString(), this, hashTagHyperLinkDisabled, HASHTAGS_COLOR), TextView.BufferType.SPANNABLE);

                    } else {

                        holder.mReDescription.setText(mTagSelectingTextview.addClickablePart(Html.fromHtml(textHtml).toString(), this, hashTagHyperLinkDisabled, HASHTAGS_COLOR), TextView.BufferType.SPANNABLE);
                    }

                } else {

                    holder.mReDescription.setVisibility(View.GONE);
                }

                holder.mReTimeAgo.setVisibility(View.VISIBLE);
                holder.mReTimeAgo.setText(p.getRePostTimeAgo());


                if (p.getReUrlPreviewLink() != null && p.getReUrlPreviewLink().length() > 0) {

                    holder.mReLinkContainer.setVisibility(View.VISIBLE);

                    holder.mReLinkContainer.setOnClickListener(v -> {

                        if (!p.getReUrlPreviewLink().startsWith("https://") && !p.getReUrlPreviewLink().startsWith("http://")){

                            p.setReUrlPreviewLink("http://" + p.getReUrlPreviewLink());
                        }

                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(p.getReUrlPreviewLink()));
                        context.startActivity(i);
                    });

                    if (p.getReUrlPreviewImage() != null && p.getReUrlPreviewImage().length() != 0) {

                        imageLoader.get(p.getReUrlPreviewImage(), ImageLoader.getImageListener(holder.mReLinkImage, R.drawable.img_link, R.drawable.img_link));

                    } else {

                        holder.mReLinkImage.setImageResource(R.drawable.img_link);
                    }

                    if (p.getReUrlPreviewTitle() != null && p.getReUrlPreviewTitle().length() != 0) {

                        holder.mReLinkTitle.setText(p.getReUrlPreviewTitle());

                    } else {

                        holder.mReLinkTitle.setText("Link");
                    }

                    if (p.getReUrlPreviewDescription() != null && p.getReUrlPreviewDescription().length() != 0) {

                        holder.mReLinkDescription.setText(p.getReUrlPreviewDescription());

                    } else {

                        holder.mReLinkDescription.setText("Link");
                    }
                }


            } else {

                // original post has deleted
                // show message

                holder.mReMessageContainer.setVisibility(View.VISIBLE);

                holder.mReHeaderContainer.setVisibility(View.GONE);
                holder.mReBodyContainer.setVisibility(View.GONE);
            }

        } else {

            // not repost
            // hide repost container

            holder.mCardRepostContainer.setVisibility(View.GONE);
        }


        //animation on like
//        RotateAnimation anim = new RotateAnimation(0f, 350f, 15f, 15f);
//        anim.setInterpolator(new LinearInterpolator());
//        anim.setRepeatCount(Animation.INFINITE);
//        anim.setDuration(700);
//       // final ImageView splash = (ImageView) findViewById(R.id.splash);
//        holder.likeAnimate.startAnimation(anim);
//        holder.likeAnimate.setAnimation(null);


    }

    private void onItemMenuButtonClick(final View view, final Item post, final int position){

        onItemMenuButtonClickListener.onItemClick(post, ITEM_ACTIONS_MENU, position);
    }

    private void animateIcon(ImageView icon) {
        ScaleAnimation scale = new ScaleAnimation(1.0f, 0.8f, 1.0f, 0.8f,
                ScaleAnimation.RELATIVE_TO_SELF, 0.5f,
                ScaleAnimation.RELATIVE_TO_SELF, 0.5f);
        scale.setDuration(175);
        scale.setInterpolator(new LinearInterpolator());

        icon.startAnimation(scale);
    }

    public void watchYoutubeVideo(String id) {

        if (YOUTUBE_API_KEY.length() > 5) {

            Intent i = new Intent(context, ViewYouTubeVideoActivity.class);
            i.putExtra("videoCode", id);
            context.startActivity(i);

        } else {

            try {

                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + id));
                context.startActivity(intent);

            } catch (ActivityNotFoundException ex) {

                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + id));
                context.startActivity(intent);
            }
        }
    }

    public void watchVideo(String videoUrl) {

        Intent i = new Intent(context, VideoViewActivity.class);
        i.putExtra("videoUrl", videoUrl);
        context.startActivity(i);
    }

    private String getLocation(Item item) {

        String location = "";

        if (item.getCountry().length() > 0 || item.getCity().length() > 0) {

            if (item.getCountry().length() > 0) {

                location = item.getCountry();
            }

            if (item.getCity().length() > 0) {

                if (item.getCountry().length() > 0) {

                    location = location + ", " + item.getCity();

                } else {

                    location = item.getCity();
                }
            }
        }

        return location;
    }

    private void showCommentsDialog(final Item item, final int item_position) {
        if (item.getRemoveAt() != 0) {
            return;
        }
        final ArrayList<Comment> itemsList;
        final CommentsListAdapter itemsAdapter;

        itemsList = new ArrayList<Comment>();
        itemsAdapter = new CommentsListAdapter(context, itemsList);

        final Dialog dialog = new Dialog(context, R.style.CommentsDialogStyle);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        dialog.setContentView(R.layout.dialog_comments);
        dialog.setCancelable(true);

        final LinearLayout mItemInfoContainer = (LinearLayout) dialog.findViewById(R.id.item_info_container);
        mItemInfoContainer.setVisibility(View.GONE);

        final MaterialRippleLayout mShowLikesButton = (MaterialRippleLayout) dialog.findViewById(R.id.show_likes_button);
        final ImageView back = (ImageView) dialog.findViewById(R.id.ivBack);

        back.setOnClickListener(view -> dialog.cancel());

        mShowLikesButton.setOnClickListener(v -> {
            Intent intent = new Intent(context, LikersActivity.class);
            intent.putExtra("itemId", item.getId());
            intent.putExtra("itemType", ITEM_TYPE_POST);
            context.startActivity(intent);
        });

        final TextView mLikesCountLabel = (TextView) dialog.findViewById(R.id.likes_count_label);

        final EmojiconEditText mCommentEditor = (EmojiconEditText) dialog.findViewById(R.id.comment_editor);
        final LinearLayout mSendButton = (LinearLayout) dialog.findViewById(R.id.send_comment_button);
        final ImageView ic_action_send = (ImageView) dialog.findViewById(R.id.send_comment_icon);

        final ProgressBar mProgressBar = (ProgressBar) dialog.findViewById(R.id.progress_bar);
        mProgressBar.setVisibility(View.GONE);

        final LinearLayout mMessageLabel = (LinearLayout) dialog.findViewById(R.id.message_label);
        mMessageLabel.setVisibility(View.GONE);

        final NestedScrollView mDlgNestedView = (NestedScrollView) dialog.findViewById(R.id.nested_view);
        final RecyclerView mDlgRecyclerView = (RecyclerView) dialog.findViewById(R.id.recycler_view);

        final GridLayoutManager mLayoutManager = new GridLayoutManager(context, 1);
        mDlgRecyclerView.setLayoutManager(mLayoutManager);

        itemsAdapter.setOnMoreButtonClickListener((obj, actionId, position) -> {
            switch (actionId){
                case R.id.action_remove: {

                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                    alertDialog.setTitle(context.getText(R.string.label_delete));

                    alertDialog.setMessage(context.getText(R.string.label_delete_comment));
                    alertDialog.setCancelable(true);

                    alertDialog.setNegativeButton(context.getText(R.string.action_cancel), (dialog12, which) -> dialog12.cancel());

                    alertDialog.setPositiveButton(context.getText(R.string.action_yes), (dialog1, which) -> {

                        Api api = new Api(context);
                        api.commentDelete(itemsList.get(position).getId(), Constants.ITEM_TYPE_POST);

                        itemsList.remove(position);
                        itemsAdapter.notifyItemRemoved(position);

                        item.setCommentsCount(item.getCommentsCount() - 1);

                        notifyItemChanged(item_position);
                    });

                    alertDialog.show();

                    break;
                }

                case R.id.action_reply: {
                    if (App.getInstance().getId() != 0) {
                        replyToUserId = obj.getFromUserId();

                        mCommentEditor.setText("@" + obj.getOwner().getUsername() + ", ");
                        mCommentEditor.setSelection(mCommentEditor.getText().length());

                        mCommentEditor.requestFocus();

                    }
                    break;
                }
            }
        });

        mDlgRecyclerView.setAdapter(itemsAdapter);

        mDlgRecyclerView.setNestedScrollingEnabled(true);

        itemsAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {

            @Override
            public void onChanged() {

                super.onChanged();

                if (itemsList.size() != 0) {

                    mDlgRecyclerView.setVisibility(View.VISIBLE);
                    mProgressBar.setVisibility(View.GONE);
                    mMessageLabel.setVisibility(View.GONE);

                    mDlgNestedView.post(() -> {
                        // Select the last row so it will scroll into view...
                        mDlgNestedView.fullScroll(View.FOCUS_DOWN);
                    });

                } else {

                    mProgressBar.setVisibility(View.GONE);
                    mMessageLabel.setVisibility(View.VISIBLE);
                }
            }
        });

        if (item.getCommentsCount() != 0) {

            if (itemsList.size() == 0) {

                mMessageLabel.setVisibility(View.GONE);
                mDlgRecyclerView.setVisibility(View.GONE);
                mProgressBar.setVisibility(View.VISIBLE);

                Api api = new Api(context);
                api.getItemComments(item.getId(), itemsList, itemsAdapter);
            }

        } else {

            mMessageLabel.setVisibility(View.VISIBLE);
        }

        mCommentEditor.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mSendButton.setEnabled(!s.toString().trim().isEmpty());
            }

            @Override
            public void afterTextChanged(Editable s) {
                ic_action_send.setImageResource(R.drawable.ic_action_send_active);

            }
        });

        mSendButton.setOnClickListener(v -> {

            String text = mCommentEditor.getText().toString().trim();

            if (text.length() != 0) {

                item.setCommentsCount(item.getCommentsCount() + 1);

                notifyItemChanged(item_position);

                Api api = new Api(context);
                api.sendComment(item.getId(), Constants.ITEM_TYPE_POST, replyToUserId, text, itemsList, itemsAdapter);

                replyToUserId = 0;
            }

            mCommentEditor.setText("");
        });

        dialog.show();

        WindowManager.LayoutParams lp = new  WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        dialog.getWindow().setAttributes(lp);
    }

    @Override
    public void clickedTag(CharSequence tag) {

        Intent i = new Intent(context, HashtagsActivity.class);
        i.putExtra("hashtag", tag);
        context.startActivity(i);
    }

    @Override
    public int getItemCount() {

        return items.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {

        final Item p = items.get(position);

        if (p.getAd() == 0) {

            return 0;

        } else {

            return 1;
        }
    }
}