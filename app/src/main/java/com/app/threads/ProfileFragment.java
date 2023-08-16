package com.app.threads;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.balysv.materialripple.MaterialRippleLayout;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.app.threads.adapter.AdvancedItemListAdapter;
import com.app.threads.adapter.FeelingsListAdapter;
import com.app.threads.adapter.FriendsSpotlightListAdapter;
import com.app.threads.adapter.GallerySpotlightListAdapter;
import com.app.threads.adapter.GiftsSelectListAdapter;
import com.app.threads.app.App;
import com.app.threads.constants.Constants;
import com.app.threads.model.BaseGift;
import com.app.threads.model.GalleryItem;
import com.app.threads.model.Item;
import com.app.threads.model.Profile;
import com.app.threads.util.Api;
import com.app.threads.util.CustomRequest;
import com.app.threads.util.Helper;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Protocol;
import com.squareup.okhttp.RequestBody;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ProfileFragment extends Fragment implements Constants, SwipeRefreshLayout.OnRefreshListener {

    private static final String STATE_LIST = "State Adapter Data";
    private static final String STATE_GALLERY_SPOTLIGHT_LIST = "State Adapter Data 2";
    private static final String STATE_FRIENDS_SPOTLIGHT_LIST = "State Adapter Data 3";

    private ProgressDialog pDialog;

    int reason=10;

    private static final String TAG = ProfileFragment.class.getSimpleName();
    private static final int MODE_PHOTO = 1;
    private static final int MODE_COVER = 2;

    private static final int SELECT_PHOTO = 1;
    private static final int SELECT_COVER = 2;
    private static final int PROFILE_EDIT = 3;
    private static final int PROFILE_NEW_POST = 4;
    private static final int CREATE_PHOTO = 5;
    private static final int CREATE_COVER = 6;
    private static final int PROFILE_FEELINGS = 7;

    String[] names = {};

    //LinearLayout mProfileActionSendMessage, mProfileActionMore;
    ConstraintLayout clMainAdd,clEditProfile;

    //Button  mGallerySpotlightMoreButton;
    //mFriendsSpotlightMoreButton,
    TextView mFriendsSpotlightCount, profile_action_main;
    //mGallerySpotlightCount
    //mGallerySpotlightTitle
    //mFriendsSpotlightTitle,
    CardView mFriendsSpotlight;
    //mGallerySpotlight;
    RecyclerView mFriendsSpotlightRecyclerView;
    //mGallerySpotlightRecyclerView;
    //ImageView ivUser;

    private ArrayList<GalleryItem> gallerySpotlightList;
    private GallerySpotlightListAdapter gallerySpotlightAdapter;

    private ArrayList<Profile> friendsSpotlightList;
    private FriendsSpotlightListAdapter friendsSpotlightAdapter;

    // LinearLayout mProfileSiteContainer;
    //mProfileFacebookContainer

    //mProfileStatusContainer,mProfileActiveContainer
    ImageView profileIcon;
    //profileOnlineIcon

    TextView profileFullname, profileUsername, mProfileWallMsg, mProfileErrorScreenMsg, mProfileDisabledScreenMsg;
    TextView mProfileBio;
    //mProfileInstagramPage
    //mProfileFacebookPage
    //mProfileLocation
    //mGiftsCount
    //mItemsCount

    //mProfileActive
    RelativeLayout mProfileLoadingScreen, mProfileErrorScreen, mProfileDisabledScreen;
    //LinearLayout mProfileLocationContainer;
    //mProfileInfoContainer;

    SwipeRefreshLayout mProfileRefreshLayout;
    NestedScrollView mNestedScrollView;
    RecyclerView mRecyclerView;

    ImageView profileCover,ivBack,icBuyGift,icSetting;
    //ivMore
    //ivSendMessage
    CircularImageView profilePhoto, mFeelingIcon;


    private BottomSheetBehavior mBehavior;
    private BottomSheetDialog mBottomSheetDialog;

  //  private RelativeLayout mSetProfileCoverButton;
    CircularImageView ciSetProfileImage;

    //MaterialRippleLayout mProfileItemsBtn, mProfileGiftsBtn;
    Profile profile;

    private ArrayList<Item> itemsList;
    private AdvancedItemListAdapter itemsAdapter;

    private String selectedPhoto, selectedCover;
    private Uri outputFileUri;

    private Boolean loadingComplete = false;
    private Boolean loadingMore = false;
    private Boolean viewMore = false;

    public long profile_id;
    int itemId = 0;
    int arrayLength = 0;
    int accessMode = 0;

    private Boolean loading = false;
    private Boolean restore = false;
    private Boolean preload = false;
    private Boolean pager = false;
    private Boolean loaded = false;

    private Boolean isMainScreen = false;

    private int giftPosition = 0;

    public ProfileFragment() {
        // Required empty public constructor
    }

    public ProfileFragment newInstance(Boolean pager) {
        ProfileFragment myFragment = new ProfileFragment();

        Bundle args = new Bundle();
        args.putBoolean("pager", pager);
        myFragment.setArguments(args);

        return myFragment;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        initpDialog();
        Intent i = getActivity().getIntent();

        profile_id = i.getLongExtra("profileId", 0);
        String profile_mention = i.getStringExtra("profileMention");
        if (profile_id == 0 && (profile_mention == null || profile_mention.length() == 0)) {
            profile_id = App.getInstance().getId();
            isMainScreen = true;
        }

        if (isMainScreen) {
            setHasOptionsMenu(false);
        } else {
            setHasOptionsMenu(true);
        }

        profile = new Profile();
        profile.setId(profile_id);

        itemsList = new ArrayList<Item>();
        itemsAdapter = new AdvancedItemListAdapter(getActivity(), itemsList);

        gallerySpotlightList = new ArrayList<GalleryItem>();
        gallerySpotlightAdapter = new GallerySpotlightListAdapter(getActivity(), gallerySpotlightList);

        friendsSpotlightList = new ArrayList<Profile>();
        friendsSpotlightAdapter = new FriendsSpotlightListAdapter(getActivity(), friendsSpotlightList);

        if (getArguments() != null) {
            pager = getArguments().getBoolean("pager", false);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        if (savedInstanceState != null) {

            itemsList = savedInstanceState.getParcelableArrayList(STATE_LIST);
            itemsAdapter = new AdvancedItemListAdapter(getActivity(), itemsList);

            gallerySpotlightList = savedInstanceState.getParcelableArrayList(STATE_GALLERY_SPOTLIGHT_LIST);
            gallerySpotlightAdapter = new GallerySpotlightListAdapter(getActivity(), gallerySpotlightList);

            friendsSpotlightList = savedInstanceState.getParcelableArrayList(STATE_FRIENDS_SPOTLIGHT_LIST);
            friendsSpotlightAdapter = new FriendsSpotlightListAdapter(getActivity(), friendsSpotlightList);

            itemId = savedInstanceState.getInt("itemId");

            restore = savedInstanceState.getBoolean("restore");
            loading = savedInstanceState.getBoolean("loading");
            preload = savedInstanceState.getBoolean("preload");
            loaded = savedInstanceState.getBoolean("loaded");
            pager = savedInstanceState.getBoolean("pager");

            profile = savedInstanceState.getParcelable("profileObj");
          //  Log.e("isFriend", profile.isFriend().toString());
        } else {
            itemsList = new ArrayList<Item>();
            itemsAdapter = new AdvancedItemListAdapter(getActivity(), itemsList);

            gallerySpotlightList = new ArrayList<GalleryItem>();
            gallerySpotlightAdapter = new GallerySpotlightListAdapter(getActivity(), gallerySpotlightList);

            friendsSpotlightList = new ArrayList<Profile>();
            friendsSpotlightAdapter = new FriendsSpotlightListAdapter(getActivity(), friendsSpotlightList);

            itemId = 0;

            restore = false;
            loading = false;
            preload = false;
            loaded = false;
            pager = false;
        }

        if (loading) {
            showpDialog();
        }

        mProfileRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.profileRefreshLayout);
        mProfileRefreshLayout.setOnRefreshListener(this);

        mNestedScrollView = (NestedScrollView) rootView.findViewById(R.id.nestedScrollView);

        mProfileLoadingScreen = (RelativeLayout) rootView.findViewById(R.id.profileLoadingScreen);
        mProfileErrorScreen = (RelativeLayout) rootView.findViewById(R.id.profileErrorScreen);
        mProfileDisabledScreen = (RelativeLayout) rootView.findViewById(R.id.profileDisabledScreen);

        mProfileErrorScreenMsg = (TextView) rootView.findViewById(R.id.profileErrorScreenMsg);
        mProfileDisabledScreenMsg = (TextView) rootView.findViewById(R.id.profileDisabledScreenMsg);
        profile_action_main = (TextView) rootView.findViewById(R.id.profile_action_main);
        //ivSendMessage = (ImageView) rootView.findViewById(R.id.ivSendMessage);
        //ivMore = (ImageView) rootView.findViewById(R.id.ivMore);
        //ivUser = (ImageView) rootView.findViewById(R.id.ivUser);
        ivBack = (ImageView) rootView.findViewById(R.id.ivBack);
        icBuyGift = (ImageView) rootView.findViewById(R.id.icBuyGift);
        icSetting = (ImageView) rootView.findViewById(R.id.icSetting);

        // Prepare bottom sheet

        View mBottomSheet = rootView.findViewById(R.id.bottom_sheet);
        mBehavior = BottomSheetBehavior.from(mBottomSheet);

        // Prepare set photo and cover buttons

        ciSetProfileImage = (CircularImageView) rootView.findViewById(R.id.ciSetProfileImage);
        //mSetProfileCoverButton = (RelativeLayout) rootView.findViewById(R.id.setProfileCoverButton);

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });

        ciSetProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                    if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                        requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE_PHOTO);

                    } else {

                        requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE_PHOTO);
                    }

                } else {

                    showChoiceImageDialog(MODE_PHOTO);
                }
            }
        });

//        mSetProfileCoverButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//
//                    if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
//
//                        requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE_COVER);
//
//                    } else {
//                        requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE_COVER);
//                    }
//
//                } else {
//                    showChoiceImageDialog(MODE_COVER);
//                }
//            }
//        });

        // Start prepare action buttons

        clMainAdd = (ConstraintLayout) rootView.findViewById(R.id.clMainAdd);
        clEditProfile = (ConstraintLayout) rootView.findViewById(R.id.clEditProfile);
        //mProfileActionSendMessage = (LinearLayout) rootView.findViewById(R.id.profile_action_send_message);
        //mProfileActionMore = (LinearLayout) rootView.findViewById(R.id.profile_action_more);

        clMainAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (profile.getId() == App.getInstance().getId()) {
                    //Intent intent = new Intent(getActivity(), NewItemActivity.class);
                    //startActivityForResult(intent, PROFILE_NEW_POST);

                }
                else {
                    if (profile.isFriend()) {
                        removeFromFriends();

                    } else {
                        addFollower();
                    }
                }
            }
        });

//        mProfileActionSendMessage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent i = new Intent(getActivity(), ChatActivity.class);
//                i.putExtra("chatId", 0);
//                i.putExtra("profileId", profile.getId());
//                i.putExtra("withProfile", profile.getFullname());
//
//                i.putExtra("with_user_username", profile.getUsername());
//                i.putExtra("with_user_fullname", profile.getFullname());
//                i.putExtra("with_user_photo_url", profile.getNormalPhotoUrl());
//
//                i.putExtra("with_user_state", profile.getState());
//                i.putExtra("with_user_verified", profile.getVerify());
//
//                startActivityForResult(i, PROFILE_EDIT);
//            }
//        });

        icBuyGift.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //choiceGiftDialog();
                //fghj
                showMoreDialog();
            }
        });

        icSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), SeetingsActivity.class);
                startActivity(i);
            }
        });


//        mProfileActionMore.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                showMoreDialog();
//            }
//        });

        clEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), AccountSettingsActivity.class);
                i.putExtra("profileId", App.getInstance().getId());
                i.putExtra("sex", profile.getSex());
                i.putExtra("year", profile.getYear());
                i.putExtra("month", profile.getMonth());
                i.putExtra("day", profile.getDay());
                i.putExtra("fullname", profile.getFullname());
                i.putExtra("location", profile.getLocation());
                i.putExtra("facebookPage", profile.getFacebookPage());
                i.putExtra("instagramPage", profile.getInstagramPage());
                i.putExtra("bio", profile.getBio());
                i.putExtra("userName",profile.getUsername());
                startActivityForResult(i, PROFILE_EDIT);
            }
        });

        // Start prepare Friends Spotlight

       // mFriendsSpotlightTitle = (TextView) rootView.findViewById(R.id.friendsSpotlightTitle);
        mFriendsSpotlightCount = (TextView) rootView.findViewById(R.id.friendsSpotlightCount);
       // mFriendsSpotlightMoreButton = (Button) rootView.findViewById(R.id.friendsSpotlightMoreBtn);
        mFriendsSpotlight = (CardView) rootView.findViewById(R.id.friendsSpotlight);
        mFriendsSpotlightRecyclerView = (RecyclerView) rootView.findViewById(R.id.friendsSpotlightRecyclerView);

        mFriendsSpotlight.setVisibility(View.GONE);

        mFriendsSpotlightRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        mFriendsSpotlightRecyclerView.setAdapter(friendsSpotlightAdapter);

        friendsSpotlightAdapter.setOnItemClickListener(new FriendsSpotlightListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Profile obj) {

                Intent intent = new Intent(getActivity(), ProfileActivity.class);
                intent.putExtra("profileId", obj.getId());
                startActivity(intent);
            }
        });

        // Start prepare Gallery Spotlight

//        mGallerySpotlightTitle = (TextView) rootView.findViewById(R.id.gallerySpotlightTitle);
//        mGallerySpotlightCount = (TextView) rootView.findViewById(R.id.gallerySpotlightCount);
//        mGallerySpotlightMoreButton = (Button) rootView.findViewById(R.id.gallerySpotlightMoreBtn);
//        mGallerySpotlight = (CardView) rootView.findViewById(R.id.gallerySpotlight);
//        mGallerySpotlightRecyclerView = (RecyclerView) rootView.findViewById(R.id.gallerySpotlightRecyclerView);
//
//        mGallerySpotlight.setVisibility(View.GONE);
//
//        mGallerySpotlightRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
//        mGallerySpotlightRecyclerView.setAdapter(gallerySpotlightAdapter);

        gallerySpotlightAdapter.setOnItemClickListener(new GallerySpotlightListAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(GalleryItem obj) {

                Intent intent = new Intent(getActivity(), ViewImageActivity.class);
                intent.putExtra("itemId", obj.getId());
                startActivity(intent);
            }
        });

        // END Gallery Spotlight

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
        itemsAdapter.setOnMoreButtonClickListener(new AdvancedItemListAdapter.OnItemMenuButtonClickListener() {
            @Override
            public void onItemClick(Item obj, int actionId, int position) {

                switch (actionId) {

                    case ITEM_ACTION_REPOST: {
                        if (obj.getFromUserId() != App.getInstance().getId()) {

                            if (obj.getRePostFromUserId() != App.getInstance().getId()) {

                                repost(position);

                            } else {

                                Toast.makeText(getActivity(), getActivity().getString(R.string.msg_not_make_repost), Toast.LENGTH_SHORT).show();
                            }

                        } else {

                            Toast.makeText(getActivity(), getActivity().getString(R.string.msg_not_make_repost), Toast.LENGTH_SHORT).show();
                        }

                        break;
                    }

                    case ITEM_ACTIONS_MENU: {
                        showItemActionDialog(position);

                        break;
                    }
                }
            }
        });

        final GridLayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 1);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(itemsAdapter);
        mRecyclerView.setNestedScrollingEnabled(false);

        mNestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

                if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {

                    if (!loadingMore && (viewMore) && !(mProfileRefreshLayout.isRefreshing())) {

                        mProfileRefreshLayout.setRefreshing(true);

                        loadingMore = true;

                        getItems();
                    }
                }
            }
        });

        profileFullname = (TextView) rootView.findViewById(R.id.profileFullname);
        profileUsername = (TextView) rootView.findViewById(R.id.profileUsername);

        //mItemsCount = (TextView) rootView.findViewById(R.id.profileItemsCount);
        //mGiftsCount = (TextView) rootView.findViewById(R.id.profileGiftsCount);

       // mProfileItemsBtn = (MaterialRippleLayout) rootView.findViewById(R.id.profileItemsBtn);
       // mProfileGiftsBtn = (MaterialRippleLayout) rootView.findViewById(R.id.profileGiftsBtn);


       // mProfileInfoContainer = (LinearLayout) rootView.findViewById(R.id.profileInfoContainer);

        //mProfileLocationContainer = (LinearLayout) rootView.findViewById(R.id.profileLocationContainer);
        //profileOnlineIcon = (ImageView) rootView.findViewById(R.id.profileOnlineIcon);
        profileIcon = (ImageView) rootView.findViewById(R.id.profileIcon);

        //mProfileStatusContainer = (LinearLayout) rootView.findViewById(R.id.profileStatusContainer);
        //mProfileActiveContainer = (LinearLayout) rootView.findViewById(R.id.profileActiveContainer);
        //mProfileFacebookContainer = (LinearLayout) rootView.findViewById(R.id.profileFacebookContainer);
        //mProfileSiteContainer = (LinearLayout) rootView.findViewById(R.id.profileSiteContainer);

        //mProfileActive = (TextView) rootView.findViewById(R.id.profileActive);
        //mProfileLocation = (TextView) rootView.findViewById(R.id.profileLocation);
        //mProfileFacebookPage = (TextView) rootView.findViewById(R.id.profileFacebookUrl);
        //mProfileInstagramPage = (TextView) rootView.findViewById(R.id.profileSiteUrl);
        mProfileBio = (TextView) rootView.findViewById(R.id.profileStatus);

//        mGallerySpotlightMoreButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                showProfileGallery(profile.getId());
//            }
//        });

//        mProfileFacebookPage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                if (!profile.getFacebookPage().startsWith("https://") && !profile.getFacebookPage().startsWith("http://")) {
//
//                    profile.setFacebookPage("http://" + profile.getFacebookPage());
//                }
//
//                Intent i = new Intent(Intent.ACTION_VIEW);
//                i.setData(Uri.parse(profile.getFacebookPage()));
//                startActivity(i);
//            }
//        });

//        mProfileInstagramPage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                if (!profile.getInstagramPage().startsWith("https://") && !profile.getInstagramPage().startsWith("http://")) {
//
//                    profile.setInstagramPage("http://" + profile.getInstagramPage());
//                }
//
//                Intent i = new Intent(Intent.ACTION_VIEW);
//                i.setData(Uri.parse(profile.getInstagramPage()));
//                startActivity(i);
//            }
//        });

//        mFriendsSpotlightMoreButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showProfileFriends(profile.getId());
//            }
//        });

//        mProfileGiftsBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showProfileGifts(profile.getId());
//            }
//        });

        mProfileWallMsg = (TextView) rootView.findViewById(R.id.profileMessage);

        profilePhoto = (CircularImageView) rootView.findViewById(R.id.profilePhoto);
        profileCover = (ImageView) rootView.findViewById(R.id.profileCover);

        profilePhoto.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (profile.getNormalPhotoUrl().length() > 0) {

                    Intent i = new Intent(getActivity(), PhotoViewActivity.class);
                    i.putExtra("imgUrl", profile.getNormalPhotoUrl());
                    startActivity(i);
                }
            }
        });

        mFeelingIcon = (CircularImageView) rootView.findViewById(R.id.feelingIcon);

        mFeelingIcon.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                if (App.getInstance().getId() == profile.getId()) {

                    choiceFeelingDialog();
                }
            }
        });

        if (profile.getFullname() == null || profile.getFullname().length() == 0) {

            if (!pager && !loaded) {

                if (App.getInstance().isConnected()) {
                    showLoadingScreen();
                    getData();

                    Log.e("Profile", "OnReload");

                } else {

                    showErrorScreen();
                }
            }

        } else {

            if (App.getInstance().isConnected()) {

                if (profile.getState() == ACCOUNT_STATE_ENABLED) {

                    showContentScreen();

                    loadingComplete();
                    updateProfile();

                } else {

                    showDisabledScreen();
                }

            } else {
                showErrorScreen();
            }
        }

        // Inflate the layout for this fragment
        return rootView;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {

        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser) {

            Handler handler = new Handler();

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    if (isAdded()) {

                        if (!loaded) {

                            showLoadingScreen();
                            getData();
                        }
                    }
                }
            }, 50);
        }
    }

    public void onDestroyView() {

        super.onDestroyView();

        hidepDialog();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt("itemId", itemId);

        outState.putBoolean("restore", restore);
        outState.putBoolean("loading", loading);
        outState.putBoolean("preload", preload);
        outState.putBoolean("loaded", loaded);
        outState.putBoolean("pager", pager);

        outState.putParcelable("profileObj", profile);
        outState.putParcelableArrayList(STATE_LIST, itemsList);
        outState.putParcelableArrayList(STATE_GALLERY_SPOTLIGHT_LIST, gallerySpotlightList);
        outState.putParcelableArrayList(STATE_FRIENDS_SPOTLIGHT_LIST, friendsSpotlightList);

        //
//        outState.putInt("itemId",itemId);
//        outState.putBoolean("restore",restore);
//        outState.putBoolean("loading",loading);
//        outState.putBoolean("preload",preload);
//        outState.putBoolean("loaded",loaded);
//        outState.putBoolean("pager",pager);
//
//        outState.putParcelable("profileObj",profile);
//        outState.putParcelableArrayList(STATE_LIST,itemsList);
//        outState.putParcelableArrayList(STATE_GALLERY_SPOTLIGHT_LIST,gallerySpotlightList);
//        outState.putParcelableArrayList(STATE_FRIENDS_SPOTLIGHT_LIST,friendsSpotlightList);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SELECT_PHOTO && resultCode == getActivity().RESULT_OK && null != data) {

            Uri selectedImage = data.getData();

            String newFileName = Helper.randomString(6) + ".jpg";

            Helper helper = new Helper(getContext());
            helper.saveImg(selectedImage, newFileName);

            File f = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), newFileName);

            uploadFile(f, IMAGE_TYPE_PROFILE_PHOTO);

        } else if (requestCode == SELECT_COVER && resultCode == getActivity().RESULT_OK && null != data) {

            Uri selectedImage = data.getData();

            String newFileName = Helper.randomString(6) + ".jpg";

            Helper helper = new Helper(getContext());
            helper.saveImg(selectedImage, newFileName);

            File f = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), newFileName);

            uploadFile(f, IMAGE_TYPE_PROFILE_COVER);

        } else if (requestCode == PROFILE_EDIT && resultCode == getActivity().RESULT_OK) {

            profile.setFullname(data.getStringExtra("fullname"));
            profile.setLocation(data.getStringExtra("location"));
            profile.setFacebookPage(data.getStringExtra("facebookPage"));
            profile.setInstagramPage(data.getStringExtra("instagramPage"));
            profile.setBio(data.getStringExtra("bio"));

            profile.setSex(data.getIntExtra("sex", 0));

            profile.setYear(data.getIntExtra("year", 0));
            profile.setMonth(data.getIntExtra("month", 0));
            profile.setDay(data.getIntExtra("day", 0));

            updateProfile();

        } else if (requestCode == PROFILE_NEW_POST && resultCode == getActivity().RESULT_OK) {

            getData();

        } else if (requestCode == PROFILE_FEELINGS && resultCode == getActivity().RESULT_OK) {


            profile.setMood(data.getIntExtra("feeling", 0));

            Log.e("Return", Integer.toString(profile.getMood()));

            updateFeeling();

        }
        else if (requestCode == CREATE_PHOTO && resultCode == getActivity().RESULT_OK) {

            try {

                File f = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "photo.jpg");

                uploadFile(f, IMAGE_TYPE_PROFILE_PHOTO);

            } catch (Exception ex) {

                Log.v("OnCameraCallBack", ex.getMessage());
            }

        } else if (requestCode == CREATE_COVER && resultCode == getActivity().RESULT_OK) {

            try {

                File f = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "cover.jpg");

                uploadFile(f, IMAGE_TYPE_PROFILE_COVER);

            } catch (Exception ex) {

                Log.v("OnCameraCallBack", ex.getMessage());
            }

        } else if (requestCode == ITEM_EDIT && resultCode == getActivity().RESULT_OK) {

            int position = data.getIntExtra("position", 0);

            if (data.getExtras() != null) {

                Item item = (Item) data.getExtras().getParcelable("item");

                itemsList.set(position, item);
            }

            itemsAdapter.notifyDataSetChanged();

        } else if (requestCode == ITEM_REPOST && resultCode == getActivity().RESULT_OK) {

            int position = data.getIntExtra("position", 0);

            Item item = itemsList.get(position);

            item.setMyRePost(true);
            item.setRePostsCount(item.getRePostsCount() + 1);

            itemsAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {

            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE_PHOTO: {

                // If request is cancelled, the result arrays are empty.

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    showChoiceImageDialog(MODE_PHOTO);

                } else if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {

                    if (!ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                        showNoStoragePermissionSnackbar();
                    }
                }

                return;
            }

            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE_COVER: {

                // If request is cancelled, the result arrays are empty.

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    showChoiceImageDialog(MODE_COVER);

                } else if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {

                    if (!ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                        showNoStoragePermissionSnackbar();
                    }
                }

                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    public void showNoStoragePermissionSnackbar() {

        Snackbar.make(getView(), getString(R.string.label_no_storage_permission), Snackbar.LENGTH_LONG).setAction(getString(R.string.action_settings), new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                openApplicationSettings();

                Toast.makeText(getActivity(), getString(R.string.label_grant_storage_permission), Toast.LENGTH_SHORT).show();
            }

        }).show();
    }

    public void openApplicationSettings() {

        Intent appSettingsIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + getActivity().getPackageName()));
        startActivityForResult(appSettingsIntent, 10001);
    }

    @Override
    public void onRefresh() {

        if (App.getInstance().isConnected()) {

            getData();

        } else {

            mProfileRefreshLayout.setRefreshing(false);
        }
    }

    public void updateFeeling() {

        if (profile.getMood() > 0) {

            //mFeelingIcon.setVisibility(View.VISIBLE);

            showFeeling(Constants.WEB_SITE + "feelings/" + profile.getMood() + ".png");

        } else {

            if (App.getInstance().getId() == profile.getId()) {

            } else {

                mFeelingIcon.setVisibility(View.GONE);
            }
        }
    }

    public void showProfileFriends(long profileId) {

        if (profile.getAllowShowMyFriends() == 0 || App.getInstance().getId() == profile.getId()) {

            Intent intent = new Intent(getActivity(), FriendsActivity.class);
            intent.putExtra("profileId", profileId);
            startActivity(intent);

        } else {

            if (profile.getAllowShowMyFriends() == 1 && profile.isFriend()) {

                Intent intent = new Intent(getActivity(), FriendsActivity.class);
                intent.putExtra("profileId", profileId);
                startActivity(intent);

            }
        }
    }

    public void updateProfile() {
        updateFeeling();

        if (profile.getLastActive() == 0) {

            //mProfileActive.setText(getString(R.string.label_offline));
            //profileOnlineIcon.setVisibility(View.GONE);

        } else {

            if (profile.isOnline()) {

                //mProfileActive.setText(getString(R.string.label_online));
                //profileOnlineIcon.setVisibility(View.VISIBLE);

            } else {

                //mProfileActive.setText(profile.getLastActiveTimeAgo());
                //profileOnlineIcon.setVisibility(View.GONE);
            }
        }

        // Profile Info
       // mProfileInfoContainer.setVisibility(View.GONE);

        if (profile.getAllowShowMyInfo() == 0 || App.getInstance().getId() == profile.getId()) {

            //mProfileInfoContainer.setVisibility(View.VISIBLE);

        } else {

            if (profile.getAllowShowMyInfo() == 1 && profile.isFriend()) {

               // mProfileInfoContainer.setVisibility(View.VISIBLE);
            }
        }
        profileUsername.setText("@" + profile.getUsername());
//        mProfileLocation.setText(profile.getLocation());
//        mProfileFacebookPage.setText(profile.getFacebookPage());
//        mProfileInstagramPage.setText(profile.getInstagramPage());
        mProfileBio.setText(profile.getBio());

        // update action buttons is your profile
        if (profile.getId() == App.getInstance().getId()) {
            ivBack.setVisibility(View.GONE);
            icBuyGift.setVisibility(View.GONE);
            icSetting.setVisibility(View.VISIBLE);
          //  mProfileActionSendMessage.setVisibility(View.GONE);
            ciSetProfileImage.setVisibility(View.VISIBLE);
            //mSetProfileCoverButton.setVisibility(View.VISIBLE);
            //mSetProfileCoverButton.setVisibility(View.VISIBLE);
            clEditProfile.setVisibility(View.VISIBLE);
         //   mProfileActionMore.setVisibility(View.GONE);

        } else {
            ivBack.setVisibility(View.VISIBLE);
            icBuyGift.setVisibility(View.VISIBLE);
            icSetting.setVisibility(View.GONE);
            ciSetProfileImage.setVisibility(View.GONE);
           // mSetProfileCoverButton.setVisibility(View.GONE);
            //mProfileActionSendMessage.setVisibility(View.GONE);

         //   mProfileActionSendMessage.setVisibility(View.VISIBLE);
            clEditProfile.setVisibility(View.GONE);
            //mProfileActionMore.setVisibility(View.VISIBLE);

            if (!profile.isInBlackList()) {
                if ((profile.getAllowMessages() == 1) || (profile.getAllowMessages() == 0 && profile.isFriend())) {
                    //mProfileActionSendMessage.setVisibility(View.VISIBLE);
                }
            }
        }

        if (profile.getLocation() != null && profile.getLocation().length() != 0) {
           // mProfileLocationContainer.setVisibility(View.VISIBLE);
        } else {
           // mProfileLocationContainer.setVisibility(View.GONE);
        }

        if (profile.getFacebookPage() != null && profile.getFacebookPage().length() != 0) {
            //mProfileFacebookContainer.setVisibility(View.VISIBLE);
        } else {
            //mProfileFacebookContainer.setVisibility(View.GONE);
        }

        if (profile.getInstagramPage() != null && profile.getInstagramPage().length() != 0) {

            //mProfileSiteContainer.setVisibility(View.VISIBLE);

        } else {
           // mProfileSiteContainer.setVisibility(View.GONE);
        }

        if (profile.getBio() != null && profile.getBio().length() != 0) {
           // mProfileStatusContainer.setVisibility(View.VISIBLE);

        } else {
            //mProfileStatusContainer.setVisibility(View.GONE);
        }

        updateFullname();
        updateFriendsCount();
        updateItemsCount();
        updatePhotosCount();
        updateGiftsCount();
        updateProfileActionMainButton();

        showPhoto(profile.getLowPhotoUrl());

        showCover(profile.getNormalCoverUrl());

        showContentScreen();

        if (this.isVisible()) {

            try {

                getActivity().invalidateOptionsMenu();

            } catch (Exception e) {

                e.printStackTrace();
            }
        }
    }

    private void updateFullname() {

        if (profile.getFullname().length() == 0) {

            profileFullname.setText(profile.getUsername());
            if (!isMainScreen) getActivity().setTitle(profile.getUsername());


        } else {

            profileFullname.setText(profile.getFullname());
            if (!isMainScreen) getActivity().setTitle(profile.getFullname());
        }

        if (!profile.isVerify()) {
            profileIcon.setVisibility(View.GONE);

        } else {
            profileIcon.setVisibility(View.VISIBLE);
        }
    }

    private void updateFriendsCount() {

        mFriendsSpotlight.setVisibility(View.GONE);

        if (profile.getFriendsCount() != 0 && friendsSpotlightAdapter.getItemCount() != 0) {
            if (profile.getAllowShowMyGifts() == 0 || App.getInstance().getId() == profile.getId() || profile.isFriend()) {
                mFriendsSpotlight.setVisibility(View.VISIBLE);
                mFriendsSpotlightCount.setText(profile.getFriendsCount() + " " + getString(R.string.label_friends));
            }
        }
    }

    private void updatePhotosCount() {
        //mGallerySpotlight.setVisibility(View.GONE);

        if (profile.getGalleryItemsCount() != 0 && gallerySpotlightAdapter.getItemCount() != 0) {

            if (profile.getAllowShowMyGallery() == 0 || App.getInstance().getId() == profile.getId() || profile.isFriend()) {

              //  mGallerySpotlight.setVisibility(View.VISIBLE);
              //  mGallerySpotlightCount.setText(profile.getGalleryItemsCount() + " " + getString(R.string.label_photos));
            }
        }
    }

    private void updateGiftsCount() {

        // mGiftsCount.setText(Integer.toString(profile.getGiftsCount()));
    }

    private void updateItemsCount() {

        if (profile.getItemsCount() == 0) {

            mProfileWallMsg.setVisibility(View.VISIBLE);

            if (App.getInstance().getId() == profile.getId()) {

                mProfileWallMsg.setText(getText(R.string.label_you_havent_items));

            } else {

                mProfileWallMsg.setText(getText(R.string.label_user_havent_items));
            }

        } else {

            mProfileWallMsg.setVisibility(View.GONE);
        }

        //mItemsCount.setText(Integer.toString(profile.getItemsCount()));
    }

    public void updateProfileActionMainButton() {

        if (profile.getId() == App.getInstance().getId()) {
            profile_action_main.setText(R.string.action_new_item);
            clMainAdd.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_back_btn));
            profile_action_main.setTextColor(this.getResources().getColor(R.color.black));
            //ivUser.setImageResource(R.drawable.ic_new_post);

        } else {
            if (profile.isFriend()) {
                profile_action_main.setText(R.string.action_remove_from_friends);
                clMainAdd.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_back_btn));
                profile_action_main.setTextColor(this.getResources().getColor(R.color.black));
                //mAddFriendIcon.setImageResource(R.drawable.ic_friend_delete);

            } else {

                if (profile.isFollow()) {

                    profile_action_main.setText(R.string.action_cancel_friends_request);
                    clMainAdd.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_back_btn));
                    profile_action_main.setTextColor(this.getResources().getColor(R.color.black));
                    // mAddFriendIcon.setImageResource(R.drawable.ic_friend_cancel);

                } else {

                    profile_action_main.setText(R.string.action_add_to_friends);
                    clMainAdd.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_back_black));
                    profile_action_main.setTextColor(this.getResources().getColor(R.color.white));
                    //mAddFriendIcon.setImageResource(R.drawable.ic_friend_add);
                }
            }
        }
    }

    public void getData() {
        Log.e("dfkll","fdgfk");
        CustomRequest jsonReq = new CustomRequest(Request.Method.POST, METHOD_PROFILE_GET, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        if (!isAdded() || getActivity() == null) {

                            Log.e("ERROR", "ProfileFragment Not Added to Activity");

                            return;
                        }
                        try {
                            if (!response.getBoolean("error")) {

                                profile = new Profile(response);
                                //   Log.e("vknfdgvkn",profile.isFriend().toString());

                                changeAccessMode();

                                if (profile.getItemsCount() > 0) {

                                    getItems();
                                }

                                if (profile.getState() == ACCOUNT_STATE_ENABLED) {

                                    showContentScreen();

                                    updateProfile();

                                } else {

                                    showDisabledScreen();
                                }
                            }

                        } catch (JSONException e) {

                            e.printStackTrace();

                            Log.d("Response GetData", response.toString());

                        } finally {

                            if (profile.getState() == ACCOUNT_STATE_ENABLED && profile.getGalleryItemsCount() > 0) {

                                getGallerySpotlight();
                            }

                            if (profile.getState() == ACCOUNT_STATE_ENABLED && profile.getFriendsCount() > 0) {

                                getFriendsSpotlight();
                            }

                            loaded = true;
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (!isAdded() || getActivity() == null) {

                    Log.e("ERROR", "ProfileFragment Not Added to Activity");

                    return;
                }

                Log.e("Error GetData", error.toString());

                showErrorScreen();

                loaded = true;
            }
        }) {

            @Override
            protected Map<String, String> getParams() {

                Log.d("TOKENWERTYUIO",App.getInstance().getAccessToken());
                Log.d("TOKENWERTYUIO",Long.toString(App.getInstance().getId()));

                Map<String, String> params = new HashMap<String, String>();
                params.put("accountId", Long.toString(App.getInstance().getId()));
                params.put("accessToken", App.getInstance().getAccessToken());
                params.put("profileId", Long.toString(profile_id));

                return params;
            }
        };

        jsonReq.setRetryPolicy(new RetryPolicy() {

            @Override
            public int getCurrentTimeout() {

                return 50000;
            }

            @Override
            public int getCurrentRetryCount() {

                return 50000;
            }

            @Override
            public void retry(VolleyError error) {

            }
        });

        App.getInstance().addToRequestQueue(jsonReq);
    }

    public void getFriendsSpotlight() {

        CustomRequest jsonReq = new CustomRequest(Request.Method.POST, METHOD_FRIENDS_GET, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        if (!isAdded() || getActivity() == null) {

                            Log.e("ERROR", "ProfileFragment Not Added to Activity");

                            return;
                        }

                        try {

                            friendsSpotlightList.clear();

                            if (!response.getBoolean("error")) {

                                if (response.has("items")) {

                                    JSONArray friendsArray = response.getJSONArray("items");

                                    arrayLength = friendsArray.length();

                                    if (arrayLength > 0) {

                                        for (int i = 0; i < friendsArray.length(); i++) {

                                            JSONObject userObj = (JSONObject) friendsArray.get(i);

                                            Profile item = new Profile(userObj);

                                            friendsSpotlightList.add(item);
                                        }
                                    }
                                }
                            }

                        } catch (JSONException e) {

                            e.printStackTrace();

                        } finally {

                            Log.d("Friends", response.toString());

                            loadingComplete();

                            updateFriendsCount();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (!isAdded() || getActivity() == null) {

                    Log.e("ERROR", "ProfileFragment Not Added to Activity");

                    return;
                }

                Log.e("getFriendsSpotlight", error.toString());
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("accountId", Long.toString(App.getInstance().getId()));
                params.put("accessToken", App.getInstance().getAccessToken());
                params.put("profileId", Long.toString(profile_id));
                params.put("itemId", Integer.toString(0));
                params.put("language", "en");

                return params;
            }
        };

        App.getInstance().addToRequestQueue(jsonReq);
    }

    public void getGallerySpotlight() {

        CustomRequest jsonReq = new CustomRequest(Request.Method.POST, METHOD_GALLERY_GET, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        if (!isAdded() || getActivity() == null) {

                            Log.e("ERROR", "ProfileFragment Not Added to Activity");

                            return;
                        }

                        try {

                            gallerySpotlightList.clear();

                            if (!response.getBoolean("error")) {

                                if (response.has("items")) {

                                    JSONArray galleryArray = response.getJSONArray("items");

                                    arrayLength = galleryArray.length();

                                    if (arrayLength > 0) {

                                        for (int i = 0; i < galleryArray.length(); i++) {

                                            JSONObject galleryObj = (JSONObject) galleryArray.get(i);

                                            GalleryItem item = new GalleryItem(galleryObj);

                                            gallerySpotlightList.add(item);
                                        }
                                    }
                                }
                            }

                        } catch (JSONException e) {

                            e.printStackTrace();

                        } finally {

                            Log.d("Gallery", response.toString());

                            loadingComplete();

                            updatePhotosCount();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (!isAdded() || getActivity() == null) {

                    Log.e("ERROR", "ProfileFragment Not Added to Activity");

                    return;
                }

                Log.e("getGallerySpotlight", error.toString());
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("accountId", Long.toString(App.getInstance().getId()));
                params.put("accessToken", App.getInstance().getAccessToken());
                params.put("profileId", Long.toString(profile_id));
                params.put("itemId", Integer.toString(0));
                params.put("language", "en");

                return params;
            }
        };

        App.getInstance().addToRequestQueue(jsonReq);
    }

    public void removeFromFriends() {

        loading = true;

        showpDialog();

        CustomRequest jsonReq = new CustomRequest(Request.Method.POST, METHOD_FRIENDS_REMOVE, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        if (!isAdded() || getActivity() == null) {

                            Log.e("ERROR", "ProfileFragment Not Added to Activity");

                            return;
                        }

                        try {

                            if (!response.getBoolean("error")) {

                                profile.setFriend(false);
                                profile.setFriendsCount(profile.getFriendsCount() - 1);

                                updateProfile();
                            }

                        } catch (JSONException e) {

                            e.printStackTrace();

                        } finally {

                            loading = false;

                            hidepDialog();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (!isAdded() || getActivity() == null) {

                    Log.e("ERROR", "ProfileFragment Not Added to Activity");

                    return;
                }

                loading = false;

                hidepDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("accountId", Long.toString(App.getInstance().getId()));
                params.put("accessToken", App.getInstance().getAccessToken());
                params.put("friendId", Long.toString(profile.getId()));

                return params;
            }
        };

        App.getInstance().addToRequestQueue(jsonReq);
    }

    public void addFollower() {

        showpDialog();

        CustomRequest jsonReq = new CustomRequest(Request.Method.POST, METHOD_PROFILE_FOLLOW, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        if (!isAdded() || getActivity() == null) {

                            Log.e("ERROR", "ProfileFragment Not Added to Activity");

                            return;
                        }

                        try {

                            if (!response.getBoolean("error")) {

                                profile.setFollow(response.getBoolean("follow"));
                                profile.setFollowersCount(response.getInt("followersCount"));

                                updateProfileActionMainButton();

                                changeAccessMode();
                            }

                        } catch (JSONException e) {

                            e.printStackTrace();

                        } finally {

                            hidepDialog();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (!isAdded() || getActivity() == null) {

                    Log.e("ERROR", "ProfileFragment Not Added to Activity");

                    return;
                }

                hidepDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("accountId", Long.toString(App.getInstance().getId()));
                params.put("accessToken", App.getInstance().getAccessToken());
                params.put("profileId", Long.toString(profile_id));

                return params;
            }
        };

        App.getInstance().addToRequestQueue(jsonReq);
    }

    public void changeAccessMode() {

        if (App.getInstance().getId() == profile.getId() || profile.isFriend()) {

            accessMode = 1;

        } else {

            accessMode = 0;
        }
    }

    public void showFeeling(String imgUrl) {

        if (imgUrl != null && imgUrl.length() > 0) {

            ImageLoader imageLoader = App.getInstance().getImageLoader();

            imageLoader.get(imgUrl, ImageLoader.getImageListener(mFeelingIcon, R.drawable.mood, R.drawable.mood));
        }
    }

    public void showPhoto(String photoUrl) {

        if (photoUrl.length() > 0) {

            ImageLoader imageLoader = App.getInstance().getImageLoader();

            imageLoader.get(photoUrl, ImageLoader.getImageListener(profilePhoto, R.drawable.profile_default_photo, R.drawable.profile_default_photo));
        }
    }

    public void showCover(String coverUrl) {

        if (coverUrl.length() > 0) {

            ImageLoader imageLoader = App.getInstance().getImageLoader();

            imageLoader.get(coverUrl, ImageLoader.getImageListener(profileCover, R.drawable.profile_default_cover, R.drawable.profile_default_cover));

//            if (Build.VERSION.SDK_INT > 15) {
//
//                profileCover.setImageAlpha(200);
//            }
        }
    }

    public void getItems() {

        if (loadingMore) {

            mProfileRefreshLayout.setRefreshing(true);

        } else {

            itemId = 0;
        }

        CustomRequest jsonReq = new CustomRequest(Request.Method.POST, METHOD_WALL_GET, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        if (!isAdded() || getActivity() == null) {

                            Log.e("ERROR", "ProfileFragment Not Added to Activity");

                            return;
                        }

                        try {

                            if (!loadingMore) {

                                itemsList.clear();
                            }

                            arrayLength = 0;

                            if (!response.getBoolean("error")) {

                                itemId = response.getInt("postId");

                                if (response.has("posts")) {

                                    JSONArray itemsArray = response.getJSONArray("posts");

                                    arrayLength = itemsArray.length();

                                    if (arrayLength > 0) {

                                        for (int i = 0; i < itemsArray.length(); i++) {

                                            JSONObject itemObj = (JSONObject) itemsArray.get(i);

                                            Item item = new Item(itemObj);

                                            item.setAd(0);

                                            itemsList.add(item);

                                            // Ad after first item
                                            if (i == MY_AD_AFTER_ITEM_NUMBER && App.getInstance().getAdmob() == ENABLED) {

                                                Item ad = new Item(itemObj);

                                                ad.setAd(1);

                                                itemsList.add(ad);
                                            }
                                        }
                                    }
                                }
                            }

                        } catch (JSONException e) {

                            e.printStackTrace();

                        } finally {

                            loadingComplete();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (!isAdded() || getActivity() == null) {

                    Log.e("ERROR", "ProfileFragment Not Added to Activity");

                    return;
                }

                loadingComplete();

                Log.e("Profile getItems Error", error.toString());
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("accountId", Long.toString(App.getInstance().getId()));
                params.put("accessToken", App.getInstance().getAccessToken());
                params.put("profileId", Long.toString(profile.getId()));
                params.put("itemId", Integer.toString(itemId));
                params.put("accessMode", Integer.toString(accessMode));

                return params;
            }
        };

        App.getInstance().addToRequestQueue(jsonReq);
    }

    public void loadingComplete() {

        if (arrayLength == LIST_ITEMS) {

            viewMore = true;

        } else {

            viewMore = false;
        }

        itemsAdapter.notifyDataSetChanged();
        friendsSpotlightAdapter.notifyDataSetChanged();
        gallerySpotlightAdapter.notifyDataSetChanged();

        mProfileRefreshLayout.setRefreshing(false);

        loadingMore = false;
    }

    public void showLoadingScreen() {

        if (!isMainScreen) getActivity().setTitle(getText(R.string.title_activity_profile));

        mProfileRefreshLayout.setVisibility(View.GONE);
        mProfileErrorScreen.setVisibility(View.GONE);
        mProfileDisabledScreen.setVisibility(View.GONE);

        mProfileLoadingScreen.setVisibility(View.VISIBLE);

        loadingComplete = false;
    }

    public void showErrorScreen() {

        if (!isMainScreen) getActivity().setTitle(getText(R.string.title_activity_profile));

        mProfileLoadingScreen.setVisibility(View.GONE);
        mProfileDisabledScreen.setVisibility(View.GONE);
        mProfileRefreshLayout.setVisibility(View.GONE);

        mProfileErrorScreen.setVisibility(View.VISIBLE);

        loadingComplete = false;
    }

    public void showDisabledScreen() {

        if (profile.getState() != ACCOUNT_STATE_ENABLED) {

            mProfileDisabledScreenMsg.setText(getText(R.string.msg_account_blocked));
        }

        getActivity().setTitle(getText(R.string.label_account_disabled));

        mProfileRefreshLayout.setVisibility(View.GONE);
        mProfileLoadingScreen.setVisibility(View.GONE);
        mProfileErrorScreen.setVisibility(View.GONE);

        mProfileDisabledScreen.setVisibility(View.VISIBLE);

        loadingComplete = false;
    }

    public void showContentScreen() {

        if (!isMainScreen) {

            getActivity().setTitle(profile.getFullname());
        }

        mProfileDisabledScreen.setVisibility(View.GONE);
        mProfileLoadingScreen.setVisibility(View.GONE);
        mProfileErrorScreen.setVisibility(View.GONE);

        mProfileRefreshLayout.setVisibility(View.VISIBLE);
        mProfileRefreshLayout.setRefreshing(false);

        loadingComplete = true;
        restore = true;
    }

    public void delete(final int position) {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setTitle(getText(R.string.label_delete));

        alertDialog.setMessage(getText(R.string.label_delete_msg));
        alertDialog.setCancelable(true);

        alertDialog.setNegativeButton(getText(R.string.action_no), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.cancel();
            }
        });

        alertDialog.setPositiveButton(getText(R.string.action_yes), new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {

                final Item item = itemsList.get(position);

                itemsList.remove(position);
                itemsAdapter.notifyDataSetChanged();

                profile.setItemsCount(profile.getItemsCount() - 1);

                updateProfile();

                if (itemsAdapter.getItemCount() == 0) {

//            showEmptyScreen();

                } else {

                    showContentScreen();
                }

                if (App.getInstance().isConnected()) {

                    Api api = new Api(getActivity());

                    api.postDelete(item.getId(), 1);

                } else {

                    Toast.makeText(getActivity(), getText(R.string.msg_network_error), Toast.LENGTH_SHORT).show();
                }
            }
        });

        alertDialog.show();
    }

    public void share(final int position) {

        final Item item = itemsList.get(position);

        Api api = new Api(getActivity());
        api.postShare(item);
    }

    public void repost(final int position) {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setTitle(getText(R.string.label_post_share));

        alertDialog.setMessage(getText(R.string.label_post_share_desc));
        alertDialog.setCancelable(true);

        alertDialog.setNegativeButton(getText(R.string.action_no), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.cancel();
            }
        });

        alertDialog.setPositiveButton(getText(R.string.action_yes), new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {

                Item item = itemsList.get(position);

                Intent i = new Intent(getActivity(), NewItemActivity.class);
                i.putExtra("position", position);
                i.putExtra("repost", item);
                startActivityForResult(i, ITEM_REPOST);
            }
        });

        alertDialog.show();
    }

    public void report(final int position) {

        String[] profile_report_categories = new String[]{

                getText(R.string.label_profile_report_0).toString(),
                getText(R.string.label_profile_report_1).toString(),
                getText(R.string.label_profile_report_2).toString(),
                getText(R.string.label_profile_report_3).toString(),

        };

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setTitle(getText(R.string.label_post_report_title));

        alertDialog.setSingleChoiceItems(profile_report_categories, 0, null);
        alertDialog.setCancelable(true);

        alertDialog.setNegativeButton(getText(R.string.action_cancel), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.cancel();
            }
        });

        alertDialog.setPositiveButton(getText(R.string.action_ok), new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {

                AlertDialog alert = (AlertDialog) dialog;
                int reason = alert.getListView().getCheckedItemPosition();

                final Item item = itemsList.get(position);

                Api api = new Api(getActivity());

                api.newReport(item.getId(), REPORT_TYPE_ITEM, reason);

                Toast.makeText(getActivity(), getActivity().getString(R.string.label_post_reported), Toast.LENGTH_SHORT).show();
            }
        });

        alertDialog.show();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        super.onCreateOptionsMenu(menu, inflater);

        menu.clear();

        inflater.inflate(R.menu.menu_profile, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {

        super.onPrepareOptionsMenu(menu);

        if (loadingComplete) {

            if (profile.getState() != ACCOUNT_STATE_ENABLED) {

                //hide all menu items
                hideMenuItems(menu, false);
            }

            if (App.getInstance().getId() == profile_id) {

                menu.removeItem(R.id.action_new_gift);

                //hide all menu items
                hideMenuItems(menu, false);

            } else {

                //show all menu items
                hideMenuItems(menu, true);
            }

        } else {

            //hide all menu items
            hideMenuItems(menu, false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_new_gift: {

                if (!profile.isInBlackList()) {

                    choiceGiftDialog();

                } else {

                    Toast.makeText(getActivity(), getString(R.string.error_action), Toast.LENGTH_SHORT).show();
                }

                return true;
            }

            default: {

                return super.onOptionsItemSelected(item);
            }
        }
    }

    private void hideMenuItems(Menu menu, boolean visible) {

        for (int i = 0; i < menu.size(); i++) {

            menu.getItem(i).setVisible(visible);
        }
    }

    protected void initpDialog() {

        pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage(getString(R.string.msg_loading));
        pDialog.setCancelable(false);
    }

    protected void showpDialog() {

        if (!pDialog.isShowing()) pDialog.show();
    }

    protected void hidepDialog() {

        if (pDialog.isShowing()) pDialog.dismiss();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    public void profileReport() {

        String[] profile_report_categories = new String[]{

                getText(R.string.label_profile_report_0).toString(),
                getText(R.string.label_profile_report_1).toString(),
                getText(R.string.label_profile_report_2).toString(),
                getText(R.string.label_profile_report_3).toString(),

        };

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setTitle(getText(R.string.label_post_report_title));

        alertDialog.setSingleChoiceItems(profile_report_categories, 0, null);
        alertDialog.setCancelable(true);

        alertDialog.setNegativeButton(getText(R.string.action_cancel), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.cancel();
            }
        });

        alertDialog.setPositiveButton(getText(R.string.action_ok), new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {

                AlertDialog alert = (AlertDialog) dialog;
                int reason = alert.getListView().getCheckedItemPosition();

                Api api = new Api(getActivity());

                api.newReport(profile.getId(), REPORT_TYPE_PROFILE, reason);

                Toast.makeText(getActivity(), getText(R.string.label_profile_reported), Toast.LENGTH_SHORT).show();
            }
        });

        alertDialog.show();
    }

    public void report() {
        final View view = getLayoutInflater().inflate(R.layout.dialog_reports, null);
        BottomSheetDialog  mBottomSheetDialog = new BottomSheetDialog(getActivity(), R.style.AppBottomSheetDialogTheme);
        mBottomSheetDialog.setContentView(view);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mBottomSheetDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }


        doKeepDialog(mBottomSheetDialog);
        TextView cancel=mBottomSheetDialog.findViewById(R.id.tvCancel);
        TextView report=mBottomSheetDialog.findViewById(R.id.tvYes);
        RadioGroup radioGroup=mBottomSheetDialog.findViewById(R.id.radioGroup);
        RadioButton radio1=mBottomSheetDialog.findViewById(R.id.rbSpam);
        RadioButton radio2=mBottomSheetDialog.findViewById(R.id.rbHateSpeech);
        RadioButton radio3=mBottomSheetDialog.findViewById(R.id.rbNudity);
        RadioButton radio4=mBottomSheetDialog.findViewById(R.id.rbFakeProfile);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId)
            {
                if(radio1.isChecked()){
                    reason=0;
                }else  if(radio2.isChecked()){
                    reason=1;
                }else if(radio3.isChecked()){
                    reason=2;
                }else{
                    reason=3;
                }
            }
        });


        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBottomSheetDialog.cancel();
            }
        });

        report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Api api = new Api(getActivity());
                api.newReport(profile.getId(), REPORT_TYPE_PROFILE, reason);
                Toast.makeText(getActivity(), getActivity().getString(R.string.label_post_reported), Toast.LENGTH_SHORT).show();
                mBottomSheetDialog.cancel();

                Toast.makeText(getActivity(), getText(R.string.label_profile_reported), Toast.LENGTH_SHORT).show();
            }
        });
        mBottomSheetDialog.show();

    }

    public void blockUser() {
        final View view = getLayoutInflater().inflate(R.layout.dialog_block, null);
        BottomSheetDialog  mBottomSheetDialog = new BottomSheetDialog(getActivity(), R.style.AppBottomSheetDialogTheme);
        mBottomSheetDialog.setContentView(view);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mBottomSheetDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }


        doKeepDialog(mBottomSheetDialog);
        CircularImageView userProfile=mBottomSheetDialog.findViewById(R.id.userProfile);
        TextView tvUserName=mBottomSheetDialog.findViewById(R.id.tvReportPost);
        TextView block=mBottomSheetDialog.findViewById(R.id.tvYes);

        tvUserName.setText(profile.getUsername());

        ImageLoader imageLoader = App.getInstance().getImageLoader();

        imageLoader.get(profile.getLowPhotoUrl(), ImageLoader.getImageListener(userProfile, R.drawable.profile_default_photo, R.drawable.profile_default_photo));

        block.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!profile.isBlocked()) {
                    loading = true;

                    showpDialog();

                    CustomRequest jsonReq = new CustomRequest(Request.Method.POST, METHOD_BLACKLIST_ADD, null,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {

                                    if (!isAdded() || getActivity() == null) {

                                        Log.e("ERROR", "ProfileFragment Not Added to Activity");

                                        return;
                                    }

                                    try {

                                        if (!response.getBoolean("error")) {

                                            profile.setBlocked(true);

                                            Toast.makeText(getActivity(), getString(R.string.msg_profile_added_to_blacklist), Toast.LENGTH_SHORT).show();
                                        }

                                    } catch (JSONException e) {

                                        e.printStackTrace();

                                    } finally {

                                        loading = false;

                                        hidepDialog();
                                    }
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            if (!isAdded() || getActivity() == null) {

                                Log.e("ERROR", "ProfileFragment Not Added to Activity");

                                return;
                            }

                            loading = false;

                            hidepDialog();
                        }
                    }) {

                        @Override
                        protected Map<String, String> getParams() {
                            Map<String, String> params = new HashMap<String, String>();
                            params.put("accountId", Long.toString(App.getInstance().getId()));
                            params.put("accessToken", App.getInstance().getAccessToken());
                            params.put("profileId", Long.toString(profile.getId()));
                            params.put("reason", "example");

                            return params;
                        }
                    };

                    App.getInstance().addToRequestQueue(jsonReq);

                }
                else {

                    loading = true;
                    showpDialog();

                    CustomRequest jsonReq = new CustomRequest(Request.Method.POST, METHOD_BLACKLIST_REMOVE, null,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {

                                    if (!isAdded() || getActivity() == null) {

                                        Log.e("ERROR", "ProfileFragment Not Added to Activity");
                                        return;
                                    }

                                    try {

                                        if (!response.getBoolean("error")) {

                                            profile.setBlocked(false);

                                            Toast.makeText(getActivity(), getString(R.string.msg_profile_removed_from_blacklist), Toast.LENGTH_SHORT).show();
                                        }

                                    } catch (JSONException e) {

                                        e.printStackTrace();

                                    } finally {

                                        loading = false;
                                        hidepDialog();
                                    }
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            if (!isAdded() || getActivity() == null) {

                                Log.e("ERROR", "ProfileFragment Not Added to Activity");

                                return;
                            }

                            loading = false;

                            hidepDialog();
                        }
                    }) {

                        @Override
                        protected Map<String, String> getParams() {
                            Map<String, String> params = new HashMap<String, String>();
                            params.put("accountId", Long.toString(App.getInstance().getId()));
                            params.put("accessToken", App.getInstance().getAccessToken());
                            params.put("profileId", Long.toString(profile.getId()));

                            return params;
                        }
                    };
                    App.getInstance().addToRequestQueue(jsonReq);
                }
            }
        });
        mBottomSheetDialog.show();

    }

    public void profileBlock() {

        if (!profile.isBlocked()) {

            AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
            alertDialog.setTitle(getText(R.string.action_block));

            alertDialog.setMessage("@" + profile.getUsername() + " " + getText(R.string.label_block_msg) + " @" + profile.getUsername() + ".");
            alertDialog.setCancelable(true);

            alertDialog.setNegativeButton(getText(R.string.action_no), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {

                    dialog.cancel();
                }
            });

            alertDialog.setPositiveButton(getText(R.string.action_yes), new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {

                    loading = true;

                    showpDialog();

                    CustomRequest jsonReq = new CustomRequest(Request.Method.POST, METHOD_BLACKLIST_ADD, null,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {

                                    if (!isAdded() || getActivity() == null) {

                                        Log.e("ERROR", "ProfileFragment Not Added to Activity");

                                        return;
                                    }

                                    try {

                                        if (!response.getBoolean("error")) {

                                            profile.setBlocked(true);

                                            Toast.makeText(getActivity(), getString(R.string.msg_profile_added_to_blacklist), Toast.LENGTH_SHORT).show();
                                        }

                                    } catch (JSONException e) {

                                        e.printStackTrace();

                                    } finally {

                                        loading = false;

                                        hidepDialog();
                                    }
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            if (!isAdded() || getActivity() == null) {

                                Log.e("ERROR", "ProfileFragment Not Added to Activity");

                                return;
                            }

                            loading = false;

                            hidepDialog();
                        }
                    }) {

                        @Override
                        protected Map<String, String> getParams() {
                            Map<String, String> params = new HashMap<String, String>();
                            params.put("accountId", Long.toString(App.getInstance().getId()));
                            params.put("accessToken", App.getInstance().getAccessToken());
                            params.put("profileId", Long.toString(profile.getId()));
                            params.put("reason", "example");

                            return params;
                        }
                    };

                    App.getInstance().addToRequestQueue(jsonReq);
                }
            });

            alertDialog.show();

        }
        else {

            loading = true;
            showpDialog();

            CustomRequest jsonReq = new CustomRequest(Request.Method.POST, METHOD_BLACKLIST_REMOVE, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            if (!isAdded() || getActivity() == null) {

                                Log.e("ERROR", "ProfileFragment Not Added to Activity");
                                return;
                            }

                            try {

                                if (!response.getBoolean("error")) {

                                    profile.setBlocked(false);

                                    Toast.makeText(getActivity(), getString(R.string.msg_profile_removed_from_blacklist), Toast.LENGTH_SHORT).show();
                                }

                            } catch (JSONException e) {

                                e.printStackTrace();

                            } finally {

                                loading = false;
                                hidepDialog();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    if (!isAdded() || getActivity() == null) {

                        Log.e("ERROR", "ProfileFragment Not Added to Activity");

                        return;
                    }

                    loading = false;

                    hidepDialog();
                }
            }) {

                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("accountId", Long.toString(App.getInstance().getId()));
                    params.put("accessToken", App.getInstance().getAccessToken());
                    params.put("profileId", Long.toString(profile.getId()));

                    return params;
                }
            };
            App.getInstance().addToRequestQueue(jsonReq);
        }
    }

    public Boolean uploadFile(File file, final int type) {
        loading = true;
        showpDialog();

        final OkHttpClient client = new OkHttpClient();
        client.setProtocols(Arrays.asList(Protocol.HTTP_1_1));

        try {
            RequestBody requestBody = new MultipartBuilder()
                    .type(MultipartBuilder.FORM)
                    .addFormDataPart("uploaded_file", file.getName(), RequestBody.create(MediaType.parse("text/csv"), file))
                    .addFormDataPart("accountId", Long.toString(App.getInstance().getId()))
                    .addFormDataPart("accessToken", App.getInstance().getAccessToken())
                    .addFormDataPart("imgType", Integer.toString(type))
                    .build();

            com.squareup.okhttp.Request request = new com.squareup.okhttp.Request.Builder()
                    .url(METHOD_PROFILE_UPLOAD_IMAGE)
                    .post(requestBody)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(com.squareup.okhttp.Request request, IOException e) {
                    loading = false;
                    hidepDialog();
                    Log.e("failure", request.toString());
                }

                @Override
                public void onResponse(com.squareup.okhttp.Response response) throws IOException {
                    String jsonData = response.body().string();
                    Log.e("response", jsonData);

                    try {
                        JSONObject result = new JSONObject(jsonData);
                        Log.e("fvbjxd",result.toString());

                        if (!result.getBoolean("error")) {
                            switch (type) {

                                case 0: {
                                    profile.setLowPhotoUrl(result.getString("lowPhotoUrl"));
                                    profile.setBigPhotoUrl(result.getString("bigPhotoUrl"));
                                    profile.setNormalPhotoUrl(result.getString("normalPhotoUrl"));

                                    App.getInstance().setPhotoUrl(result.getString("lowPhotoUrl"));

                                    break;
                                }

                                default: {
                                    profile.setNormalCoverUrl(result.getString("normalCoverUrl"));
                                    App.getInstance().setCoverUrl(result.getString("normalCoverUrl"));
                                    break;
                                }
                            }
                        }

                        Log.d("My App", response.toString());

                    } catch (Throwable t) {
                       // Log.e("My App", "Could not parse malformed JSON: \"" + response.body().string() + "\"");

                    } finally {
                        loading = false;
                        hidepDialog();
                        Helper.deleteFile(getContext(), file);
                        getData();
                    }

                }
            });

            return true;

        }
        catch (Exception ex) {
            // Handle the error
             Log.e("khjngk",ex.toString());
            loading = false;

            hidepDialog();
        }

        return false;
    }

    public void showProfileGallery(long profileId) {
        if (profile.getAllowShowMyGallery() == 0 || App.getInstance().getId() == profile.getId()) {
            Intent intent = new Intent(getActivity(), GalleryActivity.class);
            intent.putExtra("profileId", profile.getId());
            startActivity(intent);

        } else {
            if (profile.getAllowShowMyGallery() == 1 && profile.isFriend()) {
                Intent intent = new Intent(getActivity(), GalleryActivity.class);
                intent.putExtra("profileId", profile.getId());
                startActivity(intent);
            }
        }
    }

    public void showProfileGifts(long profileId) {
        if (profile.getAllowShowMyGifts() == 0 || App.getInstance().getId() == profile.getId()) {

            Intent intent = new Intent(getActivity(), GiftsActivity.class);
            intent.putExtra("profileId", profileId);
            startActivity(intent);

        } else {
            if (profile.getAllowShowMyGifts() == 1 && profile.isFriend()) {
                Intent intent = new Intent(getActivity(), GiftsActivity.class);
                intent.putExtra("profileId", profileId);
                startActivity(intent);
            }
        }
    }

    private void showMoreDialog() {
        if (mBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            mBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
        final View view = getLayoutInflater().inflate(R.layout.profile_sheet_list, null);
        MaterialRippleLayout mRefreshButton = (MaterialRippleLayout) view.findViewById(R.id.refresh_button);
        MaterialRippleLayout mEditButton = (MaterialRippleLayout) view.findViewById(R.id.edit_button);
        MaterialRippleLayout mGiftButton = (MaterialRippleLayout) view.findViewById(R.id.gift_button);
        MaterialRippleLayout mOpenUrlButton = (MaterialRippleLayout) view.findViewById(R.id.open_url_button);
        MaterialRippleLayout mCopyUrlButton = (MaterialRippleLayout) view.findViewById(R.id.copy_url_button);
        MaterialRippleLayout mReportButton = (MaterialRippleLayout) view.findViewById(R.id.report_button);

        MaterialRippleLayout mBlockButton = (MaterialRippleLayout) view.findViewById(R.id.block_button);
        ImageView mBlockIcon = (ImageView) view.findViewById(R.id.block_icon);
        TextView mBlockTitle = (TextView) view.findViewById(R.id.block_label);

        if (!WEB_SITE_AVAILABLE) {
            mOpenUrlButton.setVisibility(View.GONE);
            mCopyUrlButton.setVisibility(View.GONE);
        }

        if (App.getInstance().getId() == profile.getId()) {
            mBlockButton.setVisibility(View.GONE);
            mReportButton.setVisibility(View.GONE);
            mGiftButton.setVisibility(View.GONE);
            mEditButton.setVisibility(View.VISIBLE);

        } else {
            mBlockButton.setVisibility(View.VISIBLE);
            mReportButton.setVisibility(View.VISIBLE);
            mGiftButton.setVisibility(View.VISIBLE);
            mEditButton.setVisibility(View.GONE);

            if (profile.isBlocked()) {
                mBlockIcon.setImageResource(R.drawable.ic_unlock);
                mBlockTitle.setText(getString(R.string.action_unblock));

            } else {
                mBlockIcon.setImageResource(R.drawable.ic_block);
                mBlockTitle.setText(getString(R.string.action_block));
            }

            mReportButton.setVisibility(View.VISIBLE);
        }

        mRefreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBottomSheetDialog.dismiss();
                mProfileRefreshLayout.setRefreshing(true);
                onRefresh();
            }
        });

        mEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBottomSheetDialog.dismiss();
                if (App.getInstance().getId() == profile.getId()) {

                    Intent i = new Intent(getActivity(), AccountSettingsActivity.class);
                    i.putExtra("profileId", App.getInstance().getId());
                    i.putExtra("sex", profile.getSex());
                    i.putExtra("year", profile.getYear());
                    i.putExtra("month", profile.getMonth());
                    i.putExtra("day", profile.getDay());
                    i.putExtra("fullname", profile.getFullname());
                    i.putExtra("location", profile.getLocation());
                    i.putExtra("facebookPage", profile.getFacebookPage());
                    i.putExtra("instagramPage", profile.getInstagramPage());
                    i.putExtra("bio", profile.getBio());
                    startActivityForResult(i, PROFILE_EDIT);
                }
            }
        });

        mGiftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBottomSheetDialog.dismiss();
                if (!profile.isInBlackList()) {
                    //choiceGiftDialog();
                    mBottomSheetDialog.dismiss();
                    share(0);
                } else {
                    Toast.makeText(getActivity(), getString(R.string.error_action), Toast.LENGTH_SHORT).show();
                }
            }
        });

        mBlockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mBottomSheetDialog.dismiss();

                //profileBlock();
                blockUser();
            }
        });

        mReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mBottomSheetDialog.dismiss();

                //profileReport();
                report();
            }
        });

        mCopyUrlButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mBottomSheetDialog.dismiss();

                ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(getActivity().CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText(profile.getUsername(), API_DOMAIN + profile.getUsername());
                clipboard.setPrimaryClip(clip);

                Toast.makeText(getActivity(), getText(R.string.msg_profile_link_copied), Toast.LENGTH_SHORT).show();
            }
        });

        mOpenUrlButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                mBottomSheetDialog.dismiss();

                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(API_DOMAIN + profile.getUsername()));
                startActivity(i);
            }
        });


        mBottomSheetDialog = new BottomSheetDialog(getActivity(), R.style.AppBottomSheetDialogTheme);
        mBottomSheetDialog.setContentView(view);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            mBottomSheetDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        mBottomSheetDialog.show();

        doKeepDialog(mBottomSheetDialog);

        mBottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {

            @Override
            public void onDismiss(DialogInterface dialog) {

                mBottomSheetDialog = null;
            }
        });
    }

    private void showChoiceImageDialog(final int mode) {

        if (mBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {

            mBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }

        final View view = getLayoutInflater().inflate(R.layout.choice_image_sheet_list, null);

        MaterialRippleLayout mGalleryButton = (MaterialRippleLayout) view.findViewById(R.id.gallery_button);
        MaterialRippleLayout mCameraButton = (MaterialRippleLayout) view.findViewById(R.id.camera_button);

        mGalleryButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                mBottomSheetDialog.dismiss();

                if (mode == MODE_PHOTO) {

                    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(Intent.createChooser(intent, getText(R.string.label_select_img)), SELECT_PHOTO);

                } else {

                    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(Intent.createChooser(intent, getText(R.string.label_select_img)), SELECT_COVER);
                }
            }
        });

        mCameraButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                mBottomSheetDialog.dismiss();
                if (mode == MODE_PHOTO) {
                    try {
                        File sdImageMainDirectory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "photo.jpg");
                        outputFileUri = FileProvider.getUriForFile(getActivity(), getActivity().getPackageName() + ".provider", sdImageMainDirectory);

                        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
                        cameraIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        startActivityForResult(cameraIntent, CREATE_PHOTO);

                    } catch (Exception e) {
                        Toast.makeText(getActivity(), "Error occured. Please try again later.", Toast.LENGTH_SHORT).show();
                    }

                } else {

                    try {

                        File sdImageMainDirectory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "cover.jpg");
                        outputFileUri = FileProvider.getUriForFile(getActivity(), getActivity().getPackageName() + ".provider", sdImageMainDirectory);

                        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
                        cameraIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        startActivityForResult(cameraIntent, CREATE_COVER);

                    } catch (Exception e) {

                        Toast.makeText(getActivity(), "Error occured. Please try again later.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        mBottomSheetDialog = new BottomSheetDialog(getActivity());

        mBottomSheetDialog.setContentView(view);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            mBottomSheetDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        mBottomSheetDialog.show();

        doKeepDialog(mBottomSheetDialog);

        mBottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {

            @Override
            public void onDismiss(DialogInterface dialog) {

                mBottomSheetDialog = null;
            }
        });
    }

    private void showItemActionDialog(final int position) {

        if (mBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            mBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }

        final View view = getLayoutInflater().inflate(R.layout.item_action_sheet_list, null);

        MaterialRippleLayout mEditButton = (MaterialRippleLayout) view.findViewById(R.id.edit_button);
        MaterialRippleLayout mDeleteButton = (MaterialRippleLayout) view.findViewById(R.id.delete_button);
        MaterialRippleLayout mShareButton = (MaterialRippleLayout) view.findViewById(R.id.share_button);
        MaterialRippleLayout mRepostButton = (MaterialRippleLayout) view.findViewById(R.id.repost_button);
        MaterialRippleLayout mReportButton = (MaterialRippleLayout) view.findViewById(R.id.report_button);
        MaterialRippleLayout mOpenUrlButton = (MaterialRippleLayout) view.findViewById(R.id.open_url_button);
        MaterialRippleLayout mCopyUrlButton = (MaterialRippleLayout) view.findViewById(R.id.copy_url_button);

        if (!WEB_SITE_AVAILABLE) {

            mOpenUrlButton.setVisibility(View.GONE);
            mCopyUrlButton.setVisibility(View.GONE);
        }

        final Item item = itemsList.get(position);

        if (item.getFromUserId() == App.getInstance().getId()) {

            mEditButton.setVisibility(View.GONE);

            if (item.getPostType() == POST_TYPE_DEFAULT) {

                mEditButton.setVisibility(View.VISIBLE);
            }

            mDeleteButton.setVisibility(View.VISIBLE);

            mRepostButton.setVisibility(View.GONE);
            mReportButton.setVisibility(View.GONE);

        } else {

            mEditButton.setVisibility(View.GONE);
            mDeleteButton.setVisibility(View.GONE);

            mRepostButton.setVisibility(View.VISIBLE);
            mReportButton.setVisibility(View.VISIBLE);
        }

        mEditButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                mBottomSheetDialog.dismiss();

                Intent i = new Intent(getActivity(), NewItemActivity.class);
                i.putExtra("item", item);
                i.putExtra("position", position);
                startActivityForResult(i, ITEM_EDIT);
            }
        });

        mDeleteButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                mBottomSheetDialog.dismiss();

                delete(position);
            }
        });

        mShareButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                mBottomSheetDialog.dismiss();

                share(position);
            }
        });

        mRepostButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                mBottomSheetDialog.dismiss();

                repost(position);
            }
        });

        mReportButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                mBottomSheetDialog.dismiss();

                report(position);
            }
        });

        mCopyUrlButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBottomSheetDialog.dismiss();

                ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(getActivity().CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("post url", item.getLink());
                clipboard.setPrimaryClip(clip);

                Toast.makeText(getActivity(), getText(R.string.msg_post_link_copied), Toast.LENGTH_SHORT).show();
            }
        });

        mOpenUrlButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                mBottomSheetDialog.dismiss();

                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(item.getLink()));
                startActivity(i);
            }
        });

        mBottomSheetDialog = new BottomSheetDialog(getActivity());

        mBottomSheetDialog.setContentView(view);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            mBottomSheetDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        mBottomSheetDialog.show();

        doKeepDialog(mBottomSheetDialog);

        mBottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {

            @Override
            public void onDismiss(DialogInterface dialog) {

                mBottomSheetDialog = null;
            }
        });
    }

    private void choiceGiftDialog() {
        final GiftsSelectListAdapter giftsAdapter;
        giftsAdapter = new GiftsSelectListAdapter(getActivity(), App.getInstance().getGiftsList());
        final Dialog dialog = new Dialog(getActivity(), R.style.CommentsDialogStyle);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        dialog.getWindow().setAttributes(layoutParams);
        dialog.setContentView(R.layout.dialog_gifts);
        dialog.setCancelable(true);

        LinearLayout mDlgGiftPreviewLayout = (LinearLayout) dialog.findViewById(R.id.gift_preview_layout);
        EditText mDlgGiftMessageEdit = (EditText) dialog.findViewById(R.id.message);
        ImageView mDlgPreviewImage = (ImageView) dialog.findViewById(R.id.image);
        ImageView back = (ImageView) dialog.findViewById(R.id.ivBack);
        TextView tvHeading = (TextView) dialog.findViewById(R.id.tvHeading);
        tvHeading.setText("Gifts");

        final ProgressBar mProgressBar = (ProgressBar) dialog.findViewById(R.id.progress_bar);
        mProgressBar.setVisibility(View.GONE);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        TextView mDlgSubtitle = (TextView) dialog.findViewById(R.id.subtitle_label);
        mDlgSubtitle.setText(String.format(Locale.getDefault(), getString(R.string.account_balance_label), App.getInstance().getBalance()));

        AppCompatButton mDlgBalanceButton = (AppCompatButton) dialog.findViewById(R.id.balance_button);
        ImageView ivBack = (ImageView) dialog.findViewById(R.id.ivBack);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        mDlgBalanceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), BalanceActivity.class);
                startActivityForResult(i, 1945);
                dialog.dismiss();
            }
        });

        AppCompatButton mDlgCancelButton = (AppCompatButton) dialog.findViewById(R.id.cancel_button);
        mDlgCancelButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                dialog.dismiss();
            }
        });

        //  mDlgSendButton.setVisibility(View.GONE);
        mDlgGiftPreviewLayout.setVisibility(View.GONE);

        NestedScrollView mDlgNestedView = (NestedScrollView) dialog.findViewById(R.id.nested_view);
        final RecyclerView mDlgRecyclerView = (RecyclerView) dialog.findViewById(R.id.recycler_view);

        final LinearLayoutManager mLayoutManager = new GridLayoutManager(getActivity(), Helper.getStickersGridSpanCount(getActivity()));
        mDlgRecyclerView.setLayoutManager(mLayoutManager);
        mDlgRecyclerView.setHasFixedSize(true);
        mDlgRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mDlgRecyclerView.setAdapter(giftsAdapter);

        mDlgRecyclerView.setNestedScrollingEnabled(true);

        giftsAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {

            @Override
            public void onChanged() {

                super.onChanged();

                if (App.getInstance().getGiftsList().size() != 0) {


                    mDlgRecyclerView.setVisibility(View.VISIBLE);
                    mProgressBar.setVisibility(View.GONE);
                }
            }


        });

        giftsAdapter.setOnItemClickListener(new GiftsSelectListAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(BaseGift obj, int position) {

                if (App.getInstance().getBalance() >= Integer.valueOf(obj.getCost())) {

                    dialogBuy(obj, position, dialog);

                    mDlgGiftPreviewLayout.setVisibility(View.GONE);

                } else {
                    Toast.makeText(getActivity(), getString(R.string.error_credits), Toast.LENGTH_SHORT).show();
                }
            }
        });

        if (App.getInstance().getGiftsList().size() == 0) {
            mDlgRecyclerView.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.VISIBLE);

            Api api = new Api(getActivity());
            api.getGifts(giftsAdapter);
        }

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        dialog.getWindow().setAttributes(lp);

        dialog.show();

        doKeepDialog(dialog);
    }

    private void dialogBuy(BaseGift obj, int position, Dialog dialog) {

        final View view = getLayoutInflater().inflate(R.layout.dialog_selected_gift, null);
        ImageView ivImageChoose = (ImageView) view.findViewById(R.id.ivImageChoose);
        Button send = (Button) view.findViewById(R.id.btnsend);
        EditText etComment = (EditText) view.findViewById(R.id.etComment);
        // TextView tvCredit = (TextView) view.findViewById(R.id.tvCredit);

        ImageLoader imageLoader = App.getInstance().getImageLoader();
        imageLoader.get(obj.getImgUrl(), ImageLoader.getImageListener(ivImageChoose, R.drawable.ic_gifts, R.drawable.ic_gifts));
        //tvCredit.setText(obj.getCost().toString());
        giftPosition = position;


        send.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                String messageText = etComment.getText().toString().trim();

                CustomRequest jsonReq = new CustomRequest(Request.Method.POST, METHOD_GIFTS_SEND, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {

                                try {

                                    if (!response.getBoolean("error")) {

                                        App.getInstance().setBalance(App.getInstance().getBalance() - Integer.valueOf(App.getInstance().getGiftsList().get(giftPosition).getCost()));
                                    }

                                } catch (JSONException e) {

                                    e.printStackTrace();

                                } finally {
                                    Snackbar snackbar = Snackbar.make(getView(), getText(R.string.msg_gift_sent), Snackbar.LENGTH_LONG);
                                    Snackbar.SnackbarLayout snackbarLayout = (Snackbar.SnackbarLayout) snackbar.getView();
                                    FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) snackbarLayout.getLayoutParams();
                                    layoutParams.setMargins(0, 0, 32, 32);
                                    snackbarLayout.setLayoutParams(layoutParams);
                                    snackbar.show();

                                    profile.setGiftsCount(profile.getGiftsCount() + 1);
                                    updateGiftsCount();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Snackbar snackbar = Snackbar.make(getView(), getText(R.string.msg_gift_sent), Snackbar.LENGTH_LONG);
                        Snackbar.SnackbarLayout snackbarLayout = (Snackbar.SnackbarLayout) snackbar.getView();
                        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) snackbarLayout.getLayoutParams();
                        layoutParams.setMargins(0, 0, 32, 32);
                        snackbarLayout.setLayoutParams(layoutParams);
                        snackbar.show();
                    }
                }) {

                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("accountId", Long.toString(App.getInstance().getId()));
                        params.put("accessToken", App.getInstance().getAccessToken());
                        params.put("giftAnonymous", Integer.toString(0));
                        params.put("message", messageText);
                        params.put("giftId", Long.toString(App.getInstance().getGiftsList().get(giftPosition).getId()));
                        params.put("giftTo", Long.toString(profile.getId()));

                        return params;
                    }
                };
                App.getInstance().addToRequestQueue(jsonReq);
                mBottomSheetDialog.dismiss();
                dialog.dismiss();
            }
        });

        mBottomSheetDialog = new BottomSheetDialog(getActivity(), R.style.AppBottomSheetDialogTheme);
        mBottomSheetDialog.setContentView(view);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            mBottomSheetDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        mBottomSheetDialog.show();

        doKeepDialog(mBottomSheetDialog);

        mBottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {

            @Override
            public void onDismiss(DialogInterface dialog) {

                mBottomSheetDialog = null;
            }
        });


    }

    private void choiceFeelingDialog() {
        final FeelingsListAdapter feelingsAdapter;
        feelingsAdapter = new FeelingsListAdapter(getActivity(), App.getInstance().getFeelingsList());

        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.dialog_feelings);
        dialog.setCancelable(true);

        final ProgressBar mProgressBar = (ProgressBar) dialog.findViewById(R.id.progress_bar);
        mProgressBar.setVisibility(View.GONE);

        TextView mDlgTitle = (TextView) dialog.findViewById(R.id.title_label);
        mDlgTitle.setText(R.string.dlg_choice_feeling_title);

        AppCompatButton mDlgCancelButton = (AppCompatButton) dialog.findViewById(R.id.cancel_button);
        mDlgCancelButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                dialog.dismiss();
            }
        });

        NestedScrollView mDlgNestedView = (NestedScrollView) dialog.findViewById(R.id.nested_view);
        final RecyclerView mDlgRecyclerView = (RecyclerView) dialog.findViewById(R.id.recycler_view);

        final LinearLayoutManager mLayoutManager = new GridLayoutManager(getActivity(), Helper.getStickersGridSpanCount(getActivity()));
        mDlgRecyclerView.setLayoutManager(mLayoutManager);
        mDlgRecyclerView.setHasFixedSize(true);
        mDlgRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mDlgRecyclerView.setAdapter(feelingsAdapter);

        mDlgRecyclerView.setNestedScrollingEnabled(true);

        feelingsAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {

            @Override
            public void onChanged() {

                super.onChanged();
                if (App.getInstance().getFeelingsList().size() != 0) {
                    mDlgRecyclerView.setVisibility(View.VISIBLE);
                    mProgressBar.setVisibility(View.GONE);
                }
            }
        });

        feelingsAdapter.setOnItemClickListener(new FeelingsListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {

                Api api = new Api(getActivity());
                api.setFelling(position);

                profile.setMood(position);

                updateFeeling();

                dialog.dismiss();
            }
        });

        if (App.getInstance().getFeelingsList().size() == 0) {

            mDlgRecyclerView.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.VISIBLE);

            Api api = new Api(getActivity());
            api.getFeelings(feelingsAdapter);
        }

        dialog.show();
        doKeepDialog(dialog);
    }

    private void animateActionButtonIcon(CircularImageView icon) {

        // rotate animation

        // RotateAnimation rotate = new RotateAnimation(0, 45, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        // rotate.setDuration(175);
        // rotate.setInterpolator(new LinearInterpolator());


        // Scale animation

        ScaleAnimation scale = new ScaleAnimation(1.0f, 0.8f, 1.0f, 0.8f,
                ScaleAnimation.RELATIVE_TO_SELF, 0.5f,
                ScaleAnimation.RELATIVE_TO_SELF, 0.5f);
        scale.setDuration(175);
        scale.setInterpolator(new LinearInterpolator());

        icon.startAnimation(scale);
    }

    // Prevent dialog dismiss when orientation changes
    private static void doKeepDialog(Dialog dialog) {
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        dialog.getWindow().setAttributes(lp);
    }
}