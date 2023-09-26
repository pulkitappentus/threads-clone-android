package com.app.threads_clone;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Location;
import android.location.LocationManager;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.provider.Settings;
import androidx.annotation.NonNull;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Size;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.balysv.materialripple.MaterialRippleLayout;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.mikhaellopez.circularimageview.CircularImageView;
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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import github.ankushsachdeva.emojicon.EditTextImeBackListener;
import github.ankushsachdeva.emojicon.EmojiconEditText;
import github.ankushsachdeva.emojicon.EmojiconGridView;
import github.ankushsachdeva.emojicon.EmojiconsPopup;
import github.ankushsachdeva.emojicon.emoji.Emojicon;
import com.app.threads_clone.adapter.FeelingsListAdapter;
import com.app.threads_clone.adapter.MediaListAdapter;
import com.app.threads_clone.app.App;
import com.app.threads_clone.constants.Constants;
import com.app.threads_clone.model.Item;
import com.app.threads_clone.model.MediaItem;
import com.app.threads_clone.util.Api;
import com.app.threads_clone.util.CustomRequest;
import com.app.threads_clone.util.Helper;

public class NewItemFragment extends Fragment implements Constants {
    private static final int BUFFER_SIZE = 1024 * 2;

    private static final int VIDEO_FILES_LIMIT = 1;
    private static final int IMAGE_FILES_LIMIT = 7;

    public static final int REQUEST_TAKE_GALLERY_VIDEO = 1001;
    private static final String STATE_LIST = "State Adapter Data";

    public static final int RESULT_OK = -1;

    private static final int ITEM_FEELINGS = 1;
    RadioButton rbPublic,rbFriend;

    protected Location mLastLocation;

    private MaterialRippleLayout mOpenBottomSheet;

    private BottomSheetBehavior mBehavior;
    private BottomSheetDialog mBottomSheetDialog;
    TextView tvPost;

    private LinearLayout mImagesLayout;

    private LinearLayout mRepostLayout;
    private TextView mRepostAuthorTitle, mRepostContent;
    private CircularImageView mRepostAuthorPhoto;
    private ImageView mRepostImage;

    private ArrayList<MediaItem> itemsList;
    private MediaListAdapter itemsAdapter;

    private final List<String> filesList = new ArrayList<>();
    private CircularImageView mPhoto;
    private TextView mFullname;

//    private ImageView mAccessModeIcon;
    private TextView mAccessModeTitle;
    private LinearLayout mAccessModeLayout;

   // private TextView mLocationTitle;
   // private LinearLayout mLocationLayout;

   // private ImageView mFeelingIcon;
   // private LinearLayout mFeelingLayout;

    private ProgressDialog pDialog;
    EmojiconEditText mPostEdit;
    ImageView mEmojiBtn;

    private long group_id = 0;
    private int position = 0;

    private Item item;

    private Boolean loading = false;

    private String selectedCameraImg = "";

    EmojiconsPopup popup;

//    public NewItemFragment() {
//        // Required empty public constructor
//    }

    public NewItemFragment newInstance(Boolean pager) {
        NewItemFragment myFragment = new NewItemFragment();

        Bundle args = new Bundle();
        args.putBoolean("pager", pager);
        myFragment.setArguments(args);

        return myFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
        initpDialog();

        Intent i = getActivity().getIntent();
        group_id = i.getLongExtra("groupId", 0);
        position = i.getIntExtra("position", 0);

        Item repost = new Item();

        if (i.getExtras() != null) {
            item = (Item) i.getExtras().getParcelable("item");
            if (item == null) {
                item = new Item();
            }
            if (item.getGroupId() != 0) group_id = item.getGroupId();
            repost = (Item) i.getExtras().getParcelable("repost");
            if (repost == null) {

                repost = new Item();
            }

        } else {
            item = new Item();
        }

        if (repost.getId() != 0) {
            if (repost.getRePostId() == 0) {
                item.setRePostId(repost.getId());
                item.setRePostPost(repost.getPost());
                item.setRePostFromUserFullname(repost.getFromUserFullname());
                item.setRePostFromUserPhotoUrl(repost.getFromUserPhotoUrl());
                item.setRePostImgUrl(repost.getImgUrl());

            } else {

                item.setRePostId(repost.getRePostId());
                item.setRePostPost(repost.getRePostPost());
                item.setRePostFromUserFullname(repost.getRePostFromUserFullname());
                item.setRePostFromUserPhotoUrl(repost.getRePostFromUserPhotoUrl());
                item.setRePostImgUrl(repost.getRePostImgUrl());
            }
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_new_item, container, false);

        if (savedInstanceState != null) {
            item = savedInstanceState.getParcelable("item");
            itemsList = savedInstanceState.getParcelableArrayList(STATE_LIST);
        } else {
            itemsList = new ArrayList<>();
        }
        itemsAdapter = new MediaListAdapter(getActivity(), itemsList);

        popup = new EmojiconsPopup(rootView, getActivity());
        popup.setSizeForSoftKeyboard();
        item.setAccessMode(0);

        popup.setOnEmojiconClickedListener(new EmojiconGridView.OnEmojiconClickedListener() {
            @Override
            public void onEmojiconClicked(Emojicon emojicon) {
                mPostEdit.append(emojicon.getEmoji());
            }
        });

        popup.setOnEmojiconBackspaceClickedListener(new EmojiconsPopup.OnEmojiconBackspaceClickedListener() {

            @Override
            public void onEmojiconBackspaceClicked() {

                KeyEvent event = new KeyEvent(0, 0, 0, KeyEvent.KEYCODE_DEL, 0, 0, 0, 0, KeyEvent.KEYCODE_ENDCALL);
                mPostEdit.dispatchKeyEvent(event);
            }
        });

        popup.setOnDismissListener(new PopupWindow.OnDismissListener() {

            @Override
            public void onDismiss() {
                setIconEmojiKeyboard();
            }
        });

        popup.setOnSoftKeyboardOpenCloseListener(new EmojiconsPopup.OnSoftKeyboardOpenCloseListener() {
            @Override
            public void onKeyboardOpen() {

            }

            @Override
            public void onKeyboardClose() {

                if (popup.isShowing())

                    popup.dismiss();
            }
        });

        popup.setOnEmojiconClickedListener(new EmojiconGridView.OnEmojiconClickedListener() {

            @Override
            public void onEmojiconClicked(Emojicon emojicon) {

                mPostEdit.append(emojicon.getEmoji());
            }
        });

        popup.setOnEmojiconBackspaceClickedListener(new EmojiconsPopup.OnEmojiconBackspaceClickedListener() {

            @Override
            public void onEmojiconBackspaceClicked() {

                KeyEvent event = new KeyEvent(0, 0, 0, KeyEvent.KEYCODE_DEL, 0, 0, 0, 0, KeyEvent.KEYCODE_ENDCALL);
                mPostEdit.dispatchKeyEvent(event);
            }
        });

        if (loading) {

            showpDialog();
        }

        // Prepare bottom sheet

        View mBottomSheet = rootView.findViewById(R.id.bottom_sheet);
        mBehavior = BottomSheetBehavior.from(mBottomSheet);



        mPhoto = (CircularImageView) rootView.findViewById(R.id.photo_image);
        mFullname = (TextView) rootView.findViewById(R.id.fullname_label);


        mRepostLayout = (LinearLayout) rootView.findViewById(R.id.repost_layout);
        LinearLayout mDeleteRepost = (LinearLayout) rootView.findViewById(R.id.repost_delete);
        ImageView ivBack = (ImageView) rootView.findViewById(R.id.ivBack);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });

        mDeleteRepost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                item.setRePostId(0);
                updateRepostLayout();
            }
        });

        mRepostAuthorPhoto = (CircularImageView) rootView.findViewById(R.id.repost_author_photo_image);
        mRepostImage = (ImageView) rootView.findViewById(R.id.repost_image);

        mRepostAuthorTitle = (TextView) rootView.findViewById(R.id.repost_author_fullname_label);
        mRepostContent = (TextView) rootView.findViewById(R.id.repost_text);

        Button mViewRepostButton = (Button) rootView.findViewById(R.id.repost_view);

        mViewRepostButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), ViewItemActivity.class);
                intent.putExtra("itemId", item.getRePostId());
                getActivity().startActivity(intent);
            }
        });

        mImagesLayout = (LinearLayout) rootView.findViewById(R.id.images_layout);
        RecyclerView mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);

        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(),3, LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setAdapter(itemsAdapter);

        itemsAdapter.setOnItemClickListener(new MediaListAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(int position, int action) {
                if (action != 0) {
                    itemsList.remove(position);
                    itemsAdapter.notifyDataSetChanged();

                    updateMediaLayout();
                }
            }
        });

        //
        //mAccessModeIcon = (ImageView) rootView.findViewById(R.id.access_mode_image);
        mAccessModeTitle = (TextView) rootView.findViewById(R.id.access_mode_label);
        mAccessModeLayout = (LinearLayout) rootView.findViewById(R.id.access_mode_layout);


        mAccessModeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View  view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_share, null);
                BottomSheetDialog dialog = new BottomSheetDialog(getActivity(), R.style.AppBottomSheetDialogTheme);
                dialog.setContentView(view);

                 rbPublic= dialog.findViewById(R.id.rbPublic);
                 rbFriend= dialog.findViewById(R.id.rbFriend);

                rbPublic.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        item.setAccessMode(0);
                        updateAccessMode();
                        dialog.dismiss();
                    }
                });

                rbFriend.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        item.setAccessMode(1);
                        updateAccessMode();
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });

       // ImageView mLocationIcon = (ImageView) rootView.findViewById(R.id.location_image);
       // mLocationTitle = (TextView) rootView.findViewById(R.id.location_label);
//        mLocationLayout = (LinearLayout) rootView.findViewById(R.id.location_layout);
//
//        mLocationLayout.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//
//                deleteLocation();
//            }
//        });

        //mFeelingIcon = (ImageView) rootView.findViewById(R.id.feeling_image);
        //TextView mFeelingTitle = (TextView) rootView.findViewById(R.id.feeling_label);
        //mFeelingLayout = (LinearLayout) rootView.findViewById(R.id.feeling_layout);

//        mFeelingLayout.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//
//                deleteFeeling();
//            }
//        });

        mPostEdit = (EmojiconEditText) rootView.findViewById(R.id.postEdit);
        mPostEdit.setText(item.getPost());

        mPostEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if (isAdded()) {

                    if (hasFocus) {

                        //got focus

                        if (EMOJI_KEYBOARD) {

                          //  mEmojiBtn.setVisibility(View.VISIBLE);
                        }

                    } else {

                       // mEmojiBtn.setVisibility(View.GONE);
                    }
                }
            }
        });

        setEditTextMaxLength(POST_CHARACTERS_LIMIT);
        tvPost=rootView.findViewById(R.id.tvPost);

        mPostEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
               if(!mPostEdit.getText().toString().isEmpty()){
                   tvPost.setBackground(getResources().getDrawable(R.drawable.bg_gredient));
               }

            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                int cnt = s.length();

                if (cnt == 0) {

                    updateTitle();

                } else {
                    getActivity().setTitle(Integer.toString(POST_CHARACTERS_LIMIT - cnt));
                }
            }

        });

        ImageView mAddImageButton = (ImageView) rootView.findViewById(R.id.ivAttach);
        //MaterialRippleLayout mAddImageButton = (MaterialRippleLayout) rootView.findViewById(R.id.rlPhoto);
//        MaterialRippleLayout mAddVideoButton = (MaterialRippleLayout) rootView.findViewById(R.id.rlVideo);
//        MaterialRippleLayout mAddLocationButton = (MaterialRippleLayout) rootView.findViewById(R.id.rlLocation);
//        MaterialRippleLayout mAddFeelingButton = (MaterialRippleLayout) rootView.findViewById(R.id.rlFeeling);

        mAddImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (getMediaCount(0) < IMAGE_FILES_LIMIT) {

                    if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE_PHOTO);

                        } else {

                            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE_PHOTO);
                        }

                    } else {

                        choiceImageAction();
                    }

                } else {

                    Toast.makeText(getActivity(), String.format(Locale.getDefault(), getString(R.string.images_limit_of), IMAGE_FILES_LIMIT), Toast.LENGTH_SHORT).show();
                }
            }
        });

//        mAddVideoButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                if (getMediaCount(1) < VIDEO_FILES_LIMIT) {
//
//                    if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//
//                        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
//
//                            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE_VIDEO_IMAGE);
//
//                        } else {
//                            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE_VIDEO_IMAGE);
//                        }
//
//                    } else {
//
//                        choiceVideo();
//                    }
//
//                } else {
//
//                    Toast.makeText(getActivity(), String.format(Locale.getDefault(), getString(R.string.video_limit_of), VIDEO_FILES_LIMIT), Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//
//        mAddLocationButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                if (App.getInstance().getCountry().length() == 0 && App.getInstance().getCity().length() == 0) {
//
//                    if (App.getInstance().getLat() != 0.000000 && App.getInstance().getLng() != 0.000000) {
//
//                        App.getInstance().getAddress(App.getInstance().getLat(), App.getInstance().getLng());
//                    }
//                }
//
//                if (App.getInstance().getCountry().length() != 0 || App.getInstance().getCity().length() != 0) {
//                    setLocation();
//
//                } else {
//
//                    if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//
//                        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) || ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION)){
//
//                            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_ACCESS_LOCATION);
//
//                        } else {
//
//                            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_ACCESS_LOCATION);
//                        }
//
//                    } else {
//
//                        Intent i = new Intent(getActivity(), LocationActivity.class);
//                        startActivityForResult(i, 77);
//                    }
//                }
//            }
//        });
//
//        mAddFeelingButton.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//
//                choiceFeeling();
//            }
//        });

        tvPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideEmojiKeyboard();
                //item.setPost(mPostEdit.getText().toString().trim());
                item.setPost(mPostEdit.getText().toString().trim());
                if (itemsList.size() == 0 && item.getRePostId() == 0 && item.getPost().length() == 0) {
                    Toast toast= Toast.makeText(getActivity(), getText(R.string.msg_enter_item), Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                } else {
                    loading = true;
                    showpDialog();
                    if (itemsList.size() != 0) {
                        uploadImages(0);
                    }
                    else {
                        sendPost();
                    }
                }
            }
        });



        EditTextImeBackListener er = new EditTextImeBackListener() {

            @Override
            public void onImeBack() {

                hideEmojiKeyboard();
            }
        };

        mPostEdit.setOnEditTextImeBackListener(er);

        updateTitle();
        updateProfileInfo();
        updateAccessMode();
        updateLocation();
        updateFeeling();
        updateMediaLayout();
        updateRepostLayout();

        // Inflate the layout for this fragment
        return rootView;
    }

    private void updateTitle() {

        if (isAdded()) {

            if (item.getId() != 0) {

                getActivity().setTitle(getText(R.string.title_edit_item));

            } else {

                getActivity().setTitle(getText(R.string.title_new_item));
            }
        }
    }

    private void updateProfileInfo() {

        if (isAdded()) {

            if (App.getInstance().getPhotoUrl() != null && App.getInstance().getPhotoUrl().length() > 0) {

                App.getInstance().getImageLoader().get(App.getInstance().getPhotoUrl(), ImageLoader.getImageListener(mPhoto, R.drawable.profile_default_photo, R.drawable.profile_default_photo));

            } else {

                mPhoto.setImageResource(R.drawable.profile_default_photo);
            }
            mFullname.setText(App.getInstance().getFullname());
        }
    }

    private void updateAccessMode() {
        if (group_id != 0) {

            mAccessModeLayout.setVisibility(View.GONE);

        } else {
            mAccessModeLayout.setVisibility(View.VISIBLE);
            if (item.getAccessMode() == 0) {
                mAccessModeTitle.setText(getString(R.string.label_post_to_public));
                //mAccessModeIcon.setImageResource(R.drawable.ic_public);

            } else {
                mAccessModeTitle.setText(getString(R.string.label_post_to_friends));
                // mAccessModeIcon.setImageResource(R.drawable.ic_share_friend);
            }
        }
    }

    private void updateLocation() {
        String location = "";
        //mLocationLayout.setVisibility(View.GONE);

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

//            if (location.length() > 0) {
//                mLocationLayout.setVisibility(View.VISIBLE);
//                mLocationTitle.setText(location);
//            }
        }
    }

    public void setLocation() {
        item.setCountry(App.getInstance().getCountry());
        item.setCity(App.getInstance().getCity());

        item.setLat(App.getInstance().getLat());
        item.setLng(App.getInstance().getLng());

        updateLocation();
    }

    public void deleteLocation() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setTitle(getText(R.string.dlg_delete_location_title));

        alertDialog.setMessage(getText(R.string.dlg_delete_location_subtitle));
        alertDialog.setCancelable(true);

        alertDialog.setNegativeButton(getText(R.string.action_cancel), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.cancel();
            }
        });

        alertDialog.setPositiveButton(getText(R.string.action_yes), new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {

                item.setCountry("");
                item.setCity("");

                item.setLat(0.000000);
                item.setLng(0.000000);

                updateLocation();
            }
        });

        alertDialog.show();
    }

    private void updateFeeling() {

//        mFeelingLayout.setVisibility(View.GONE);
//
//        if (item.getFeeling() != 0) {
//
//            ImageLoader imageLoader = App.getInstance().getImageLoader();
//
//            imageLoader.get(Constants.WEB_SITE + "feelings/" + item.getFeeling() + ".png", ImageLoader.getImageListener(mFeelingIcon, R.drawable.mood, R.drawable.mood));
//            mFeelingLayout.setVisibility(View.VISIBLE);
//        }
    }

    public void deleteFeeling() {

        AlertDialog.Builder builderSingle = new AlertDialog.Builder(getActivity());

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1);

        arrayAdapter.add(getText(R.string.action_remove).toString());
        arrayAdapter.add(getText(R.string.action_edit).toString());

        builderSingle.setTitle(getText(R.string.dlg_delete_feeling_title));


        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                switch (which) {

                    case 0: {

                        item.setFeeling(0);

                        updateFeeling();

                        break;
                    }

                    default: {

                        choiceFeeling();

                        break;
                    }
                }
            }
        });

        builderSingle.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
            }
        });

        AlertDialog d = builderSingle.create();
        d.show();
    }

    private void choiceFeeling() {

        choiceFeelingDialog();
    }

    private void updateMediaLayout() {

        mImagesLayout.setVisibility(View.GONE);

        if (item.getId() != 0 && itemsAdapter.getItemCount() == 0) {

            if (item.getVideoUrl().length() > 0) {

                itemsList.add(new MediaItem("", "", item.getPreviewVideoImgUrl(), item.getVideoUrl(), 1));
            }

            if (item.getImgUrl().length() > 0) {

                itemsList.add(new MediaItem("", "", item.getImgUrl(), "", 0));
            }

            if (item.getImagesCount() != 0) {

                item.setImagesCount(0);

                getMediaItems();
            }

            item.setImgUrl("");
            item.setVideoUrl("");
            item.setPreviewVideoImgUrl("");
            item.getMediaList().clear();
        }

        if (itemsAdapter.getItemCount() > 0) {

            mImagesLayout.setVisibility(View.VISIBLE);

            itemsAdapter.notifyDataSetChanged();
        }
    }

    private void updateRepostLayout() {
        mRepostLayout.setVisibility(View.GONE);
        if (item.getRePostId() != 0) {

            mRepostLayout.setVisibility(View.VISIBLE);

            if (item.getRePostFromUserPhotoUrl().length() != 0) {

                App.getInstance().getImageLoader().get(item.getRePostFromUserPhotoUrl(), ImageLoader.getImageListener(mRepostAuthorPhoto, R.drawable.profile_default_photo, R.drawable.profile_default_photo));

            } else {

                mRepostAuthorPhoto.setImageResource(R.drawable.img_loading);
            }

            mRepostAuthorTitle.setText(item.getRePostFromUserFullname());

            if (item.getRePostImgUrl().length() != 0) {

                App.getInstance().getImageLoader().get(item.getRePostImgUrl(), ImageLoader.getImageListener(mRepostImage, R.drawable.img_loading, R.drawable.img_loading));

                mRepostImage.setVisibility(View.VISIBLE);

            } else {

                mRepostImage.setVisibility(View.GONE);
            }

            if (item.getRePostPost().length() != 0) {

                mRepostContent.setText(item.getRePostPost());
                mRepostContent.setVisibility(View.VISIBLE);

            } else {
                mRepostContent.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {

            case MY_PERMISSIONS_REQUEST_ACCESS_LOCATION: {

                // If request is cancelled, the result arrays are empty.

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // Check GPS is enabled
                    LocationManager lm = (LocationManager) getActivity().getSystemService(getActivity().LOCATION_SERVICE);

                    if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER) && ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                        FusedLocationProviderClient mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());

                        mFusedLocationClient.getLastLocation().addOnCompleteListener(getActivity(), new OnCompleteListener<Location>() {
                            @Override
                            public void onComplete(@NonNull Task<Location> task) {

                                if (task.isSuccessful() && task.getResult() != null) {

                                    mLastLocation = task.getResult();

                                    // Set geo data to App class

                                    App.getInstance().setLat(mLastLocation.getLatitude());
                                    App.getInstance().setLng(mLastLocation.getLongitude());

                                    // Save data

                                    App.getInstance().saveData();

                                    // Get address

                                    App.getInstance().getAddress(App.getInstance().getLat(), App.getInstance().getLng());

                                    setLocation();

                                } else {
                                    Log.d("GPS", "New Item getLastLocation:exception", task.getException());
                                }
                            }
                        });
                    }

                    setLocation();

                } else if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {

                    if (!ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) || !ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION)) {

                        showNoLocationPermissionSnackbar();
                    }
                }

                return;
            }

            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE_PHOTO: {

                // If request is cancelled, the result arrays are empty.

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    choiceImageAction();

                } else if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {

                    if (!ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                        showNoStoragePermissionSnackbar();
                    }
                }

                return;
            }

            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE_VIDEO_IMAGE: {

                // If request is cancelled, the result arrays are empty.

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    choiceVideo();

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

        Snackbar.make(getView(), getString(R.string.label_no_storage_permission) , Snackbar.LENGTH_LONG).setAction(getString(R.string.action_settings), new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                openApplicationSettings();

                Toast.makeText(getActivity(), getString(R.string.label_grant_storage_permission), Toast.LENGTH_SHORT).show();
            }

        }).show();
    }

    public void showNoLocationPermissionSnackbar() {

        Snackbar.make(getView(), getString(R.string.label_no_location_permission) , Snackbar.LENGTH_LONG).setAction(getString(R.string.action_settings), new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                openApplicationSettings();

                Toast.makeText(getActivity(), getString(R.string.label_grant_location_permission), Toast.LENGTH_SHORT).show();
            }

        }).show();
    }

    public void openApplicationSettings() {

        Intent appSettingsIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + getActivity().getPackageName()));
        startActivityForResult(appSettingsIntent, 10001);
    }

    public void setEditTextMaxLength(int length) {
        InputFilter[] FilterArray = new InputFilter[1];
        FilterArray[0] = new InputFilter.LengthFilter(length);
        mPostEdit.setFilters(FilterArray);
    }

    public void hideEmojiKeyboard() {

        popup.dismiss();
    }

    public void setIconEmojiKeyboard() {

      //  mEmojiBtn.setBackgroundResource(R.drawable.ic_emoji);
    }

    public void setIconSoftKeyboard() {
        // mEmojiBtn.setBackgroundResource(R.drawable.ic_keyboard);
    }

    public void onDestroyView() {
        super.onDestroyView();

        hidepDialog();

        deleteTmpFiles();
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
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelableArrayList(STATE_LIST, itemsList);
        outState.putParcelable("item", item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_post: {

                hideEmojiKeyboard();

             //   this.item.setPost(mPostEdit.getText().toString().trim());
                this.item.setPost(mPostEdit.getText().toString());

                if (itemsList.size() == 0 && this.item.getRePostId() == 0 && this.item.getPost().length() == 0) {

                    Toast toast= Toast.makeText(getActivity(), getText(R.string.msg_enter_item), Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();

                } else {

                    loading = true;
                    showpDialog();

                    if (itemsList.size() != 0) {
                        uploadImages(0);
                        return true;
                    }
                    sendPost();
                }

                return true;
            }

            default: {
                break;
            }
        }

        return false;
    }

    private void uploadImages(int index) {

        Log.e("uploadImages", "uploadImages:" + index);

        if (itemsList.size() > 0) {

            if (index < itemsList.size()) {

                boolean need_upload = false;

                for (int i = 0; i < itemsList.size(); i++) {

                    if (itemsList.get(i).getImageUrl().length() == 0) {

                        need_upload = true;
                    }
                }

                if (need_upload) {

                    for (int i = index; i < itemsList.size(); i++) {

                        if (itemsList.get(i).getImageUrl().length() == 0) {

                            if (itemsList.get(i).getType() == 0) {

                                File f = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), itemsList.get(i).getSelectedImageFileName());

                                uploadFile(METHOD_ITEMS_UPLOAD_IMG, f, i);

                            } else {

                                File f = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), itemsList.get(i).getSelectedImageFileName());
                                File f2 = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES), itemsList.get(i).getSelectedVideoFileName());

                                uploadVideoFile(METHOD_VIDEO_UPLOAD, f, f2, i);
                            }

                            break;
                        }
                    }

                } else {

                    sendPost();
                }

            } else {

                sendPost();
            }

        } else {

            sendPost();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SELECT_POST_IMG && resultCode == RESULT_OK && null != data) {

            Uri selectedImage = data.getData();

            String selectedImageFile = Helper.randomString(6) + ".jpg";

            Helper helper = new Helper(getContext());
            helper.saveImg(selectedImage, selectedImageFile);

            itemsList.add(new MediaItem(selectedImageFile, "", "", "", 0));
            itemsAdapter.notifyDataSetChanged();

            filesList.add(selectedImageFile);

            updateMediaLayout();

        } else if (requestCode == CREATE_POST_IMG && resultCode == getActivity().RESULT_OK) {

            itemsList.add(new MediaItem(selectedCameraImg, "", "", "", 0));
            itemsAdapter.notifyDataSetChanged();

            filesList.add(selectedCameraImg);

            updateMediaLayout();

        } else if (requestCode == 77 && resultCode == getActivity().RESULT_OK) {

            setLocation();

        } else if (requestCode == REQUEST_TAKE_GALLERY_VIDEO && resultCode == getActivity().RESULT_OK) {

            Uri selectedVideoUri = data.getData();

            String selectedPostVideo = Helper.randomString(6) + ".mp4";
            String selectedPostImg = Helper.randomString(6) + ".jpg";

            long filesize;

            ParcelFileDescriptor pdf = null;

            try {
                pdf = getActivity().getContentResolver().openFileDescriptor(selectedVideoUri, "r");

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            filesize = pdf.getStatSize();
            if (filesize > VIDEO_FILE_MAX_SIZE) {
                Toast.makeText(getActivity(), getString(R.string.msg_video_too_large), Toast.LENGTH_SHORT).show();

            } else {
                InputStream is;
                OutputStream os;
                try {
                    is = (FileInputStream) getContext().getContentResolver().openInputStream(selectedVideoUri);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

                        ContentValues values = new ContentValues();
                        values.put(MediaStore.Images.Media.DISPLAY_NAME, selectedPostVideo);
                        values.put(MediaStore.Images.Media.MIME_TYPE, "video/mp4");
                        values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_MOVIES);

                        ContentResolver contentResolver = getContext().getContentResolver();

                        os = (FileOutputStream) contentResolver.openOutputStream(contentResolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values));

                    } else {

                        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES), selectedPostVideo);
                        os = new FileOutputStream(file);
                    }

                    try {

                        byte[] buf = new byte[1024];
                        int len;

                        while ((len = is.read(buf)) >= 0) {

                            os.write(buf, 0, len);
                        }

                    } finally {

                        //os.flush();
                        is.close();
                        os.close();

                        File videoFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES), selectedPostVideo);

                        if (videoFile.length() != filesize) {

                            selectedPostImg = "";
                            selectedPostVideo = "";

                            Helper.deleteFile(getContext(), videoFile);

                            Toast.makeText(getActivity(), getString(R.string.msg_video_copy_error), Toast.LENGTH_SHORT).show();

                        } else {

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

                                Bitmap thumb = null;

                                try {

                                    thumb = ThumbnailUtils.createVideoThumbnail(videoFile, new Size(256, 256), null);

                                } catch (IOException e) {

                                    thumb = BitmapFactory.decodeResource(getResources(), R.drawable.ic_video_preview_src);
                                }

                                Matrix matrix = new Matrix();
                                Bitmap bmThumbnail = Bitmap.createBitmap(thumb, 0, 0, thumb.getWidth(), thumb.getHeight(), matrix, true);

                                ContentValues values = new ContentValues();
                                values.put(MediaStore.Images.Media.DISPLAY_NAME, selectedPostImg);
                                values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
                                values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES);

                                ContentResolver contentResolver = getContext().getContentResolver();

                                OutputStream imageOutStream;

                                imageOutStream = contentResolver.openOutputStream(contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values));

                                bmThumbnail.compress(Bitmap.CompressFormat.JPEG, 90, imageOutStream);
                                imageOutStream.flush();
                                imageOutStream.close();

                            } else {

                                try {

                                    OutputStream imageOutStream;

                                    Bitmap thumb = ThumbnailUtils.createVideoThumbnail(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES) + File.separator + selectedPostVideo, MediaStore.Images.Thumbnails.MINI_KIND);

                                    Matrix matrix = new Matrix();
                                    Bitmap bmThumbnail = Bitmap.createBitmap(thumb, 0, 0, thumb.getWidth(), thumb.getHeight(), matrix, true);

                                    File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), selectedPostImg);

                                    imageOutStream = new FileOutputStream(file);

                                    bmThumbnail.compress(Bitmap.CompressFormat.JPEG, 90, imageOutStream);
                                    imageOutStream.flush();
                                    imageOutStream.close();

                                } catch (IOException e) {

                                    e.printStackTrace();
                                }
                            }

                            itemsList.add(new MediaItem(selectedPostImg, selectedPostVideo, "", "", 1));
                            itemsAdapter.notifyDataSetChanged();

                            filesList.add(selectedPostImg);
                            filesList.add(selectedPostVideo);
                        }

                        updateMediaLayout();
                    }

                } catch (FileNotFoundException e) {

                    e.printStackTrace();

                } catch (IOException e) {

                    e.printStackTrace();
                }
            }

        } else if (requestCode == ITEM_FEELINGS && resultCode == getActivity().RESULT_OK) {


            item.setFeeling(data.getIntExtra("feeling", 0));

            updateFeeling();
        }
    }

    public void choiceVideo() {

        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(intent, getActivity().getString(R.string.label_select_video)), REQUEST_TAKE_GALLERY_VIDEO);
    }

    public void choiceImageAction() {
        View  view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_add_file, null);
        BottomSheetDialog dialog = new BottomSheetDialog(getActivity(), R.style.AppBottomSheetDialogTheme);
        dialog.setContentView(view);
        ImageView ivCamera=dialog.findViewById(R.id.ivCamera);
        ivCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                selectedCameraImg = Helper.randomString(6) + ".jpg";

                File sdImageMainDirectory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), selectedCameraImg);

                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(getActivity(), BuildConfig.APPLICATION_ID + ".provider", sdImageMainDirectory));
                cameraIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivityForResult(cameraIntent, CREATE_POST_IMG);
                dialog.cancel();
            }
        });

        ImageView ivgallery=dialog.findViewById(R.id.ivGallery);
        ivgallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(Intent.createChooser(intent, getText(R.string.label_select_img)), SELECT_POST_IMG);

                dialog.cancel();

            }
        });
        dialog.show();


    }

    public void sendPost() {

        item.getMediaList().clear();
        item.setImgUrl("");
        item.setVideoUrl("");
        item.setPreviewVideoImgUrl("");
        item.setImagesCount(0);

        if (itemsList.size() != 0) {

            for (int i = 0; i < itemsList.size(); i++) {

                if (itemsList.get(i).getType() == 0) {

                    if (item.getImgUrl().length() == 0) {

                        item.setImgUrl(itemsList.get(i).getImageUrl());

                    } else {

                        item.getMediaList().add(itemsList.get(i));
                        item.setImagesCount(item.getImagesCount() + 1);
                    }

                } else {

                    item.setVideoUrl(itemsList.get(i).getVideoUrl());
                    item.setPreviewVideoImgUrl(itemsList.get(i).getImageUrl());
                }
            }
        }

        if (this.item.getId() != 0) {
            savePost();

        } else {

            newPost();
        }
    }

    private void savePost() {
        CustomRequest jsonReq = new CustomRequest(Request.Method.POST, METHOD_ITEMS_EDIT, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {

                            if (!response.getBoolean("error")) {


                            }

                        } catch (JSONException e) {

                            e.printStackTrace();

                        } finally {

                            savePostSuccess();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                savePostSuccess();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("accountId", Long.toString(App.getInstance().getId()));
                params.put("accessToken", App.getInstance().getAccessToken());
                params.put("groupId", Long.toString(group_id));
                params.put("postId", Long.toString(item.getId()));
                params.put("rePostId", Long.toString(item.getRePostId()));
                params.put("postMode", Integer.toString(item.getAccessMode()));
                params.put("postText", item.getPost());
                params.put("postImg", item.getImgUrl());
                params.put("postArea", item.getArea());
                params.put("postCountry", item.getCountry());
                params.put("postCity", item.getCity());
                params.put("postLat", Double.toString(item.getLat()));
                params.put("postLng", Double.toString(item.getLng()));

                params.put("feeling", Integer.toString(item.getFeeling()));

                if (item.getMediaList().size() != 0) {

                    Collections.reverse(item.getMediaList());

                    for (int i = 0; i < item.getMediaList().size(); i++) {

                        if (item.getMediaList().get(i).getType() == 0) {

                            params.put("images[" + i + "]", item.getMediaList().get(i).getImageUrl());
                        }
                    }
                }
                params.put("videoImgUrl", item.getPreviewVideoImgUrl());
                params.put("videoUrl", item.getVideoUrl());
                Log.e("params",params.toString());
                return params;
            }
        };

        int socketTimeout = 0;//0 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

        jsonReq.setRetryPolicy(policy);

        App.getInstance().addToRequestQueue(jsonReq);
    }

    public void savePostSuccess() {
        loading = false;
        hidepDialog();
        if (isAdded()) {

            Intent i = new Intent();
            i.putExtra("item", item);
            i.putExtra("position", position);

            getActivity().setResult(RESULT_OK, i);

            Toast.makeText(getActivity(), getText(R.string.msg_item_saved), Toast.LENGTH_SHORT).show();

            deleteTmpDir();

            getActivity().finish();
        }
    }

    private void newPost() {
        CustomRequest jsonReq = new CustomRequest(Request.Method.POST, METHOD_ITEMS_NEW, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (!response.getBoolean("error")) {

                            }

                        } catch (JSONException e) {

                            e.printStackTrace();

                        } finally {
                            sendPostSuccess();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                sendPostSuccess();

//                    Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("accountId", Long.toString(App.getInstance().getId()));
                params.put("accessToken", App.getInstance().getAccessToken());
                params.put("groupId", Long.toString(group_id));
                params.put("rePostId", Long.toString(item.getRePostId()));
                params.put("postMode", Integer.toString(item.getAccessMode()));
                params.put("postText", item.getPost());
                params.put("postImg", item.getImgUrl());
                params.put("postArea", item.getArea());
                params.put("postCountry", item.getCountry());
                params.put("postCity", item.getCity());
                params.put("postLat", Double.toString(item.getLat()));
                params.put("postLng", Double.toString(item.getLng()));

                params.put("feeling", Integer.toString(item.getFeeling()));


                if (item.getMediaList().size() != 0) {

                    Collections.reverse(item.getMediaList());

                    for (int i = 0; i < item.getMediaList().size(); i++) {

                        if (item.getMediaList().get(i).getType() == 0) {

                            params.put("images[" + i + "]", item.getMediaList().get(i).getImageUrl());
                        }
                    }
                }
                params.put("videoImgUrl", item.getPreviewVideoImgUrl());
                params.put("videoUrl", item.getVideoUrl());
                Log.e("fgjfkjg",params.toString());
                return params;
            }
        };

        int socketTimeout = 0;//0 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

        jsonReq.setRetryPolicy(policy);
        App.getInstance().addToRequestQueue(jsonReq);
    }

    public void sendPostSuccess() {
        loading = false;
        hidepDialog();

        if (isAdded()) {
            Intent i = new Intent();
            getActivity().setResult(RESULT_OK, i);

            Toast.makeText(getActivity(), getText(R.string.msg_item_posted), Toast.LENGTH_SHORT).show();

            deleteTmpDir();

            getActivity().finish();
        }
    }

    private void deleteTmpDir() {

        File dir = new File(Environment.getExternalStorageDirectory() + File.separator + APP_TEMP_FOLDER);

        try {

            if (dir.exists()) {

                File[] entries = dir.listFiles();

                for (File currentFile: entries){

                    currentFile.delete();
                }
            }

        } catch (Exception e) {

            Log.e("deleteTmpDir", "Unable to delete tmp folder");
        }
    }



    @Override
    public void onAttach(Context context) {

        super.onAttach(context);
    }

    @Override
    public void onDetach() {

        super.onDetach();
    }


    private void showBottomSheet() {
        if (mBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {

            mBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }

        if (App.getInstance().getCountry().length() == 0 && App.getInstance().getCity().length() == 0) {

            if (App.getInstance().getLat() != 0.000000 && App.getInstance().getLng() != 0.000000) {

                App.getInstance().getAddress(App.getInstance().getLat(), App.getInstance().getLng());
            }
        }

        final View view = getLayoutInflater().inflate(R.layout.item_editor_sheet_list, null);

        MaterialRippleLayout mAddImageButton = (MaterialRippleLayout) view.findViewById(R.id.add_image_button);
        MaterialRippleLayout mAddVideoButton = (MaterialRippleLayout) view.findViewById(R.id.add_video_button);
        MaterialRippleLayout mAddLocationButton = (MaterialRippleLayout) view.findViewById(R.id.add_location_button);
        MaterialRippleLayout mAddFeelingButton = (MaterialRippleLayout) view.findViewById(R.id.add_feeling_button);

        mAddImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mBottomSheetDialog != null) {

                    mBottomSheetDialog.dismiss();
                }

                if (getMediaCount(0) < IMAGE_FILES_LIMIT) {

                    if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE_PHOTO);

                        } else {

                            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE_PHOTO);
                        }

                    } else {

                        choiceImageAction();
                    }

                } else {

                    Toast.makeText(getActivity(), String.format(Locale.getDefault(), getString(R.string.images_limit_of), IMAGE_FILES_LIMIT), Toast.LENGTH_SHORT).show();
                }
            }
        });

        mAddVideoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mBottomSheetDialog != null) {

                    mBottomSheetDialog.dismiss();
                }

                if (getMediaCount(1) < VIDEO_FILES_LIMIT) {

                    if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE_VIDEO_IMAGE);

                        } else {

                            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE_VIDEO_IMAGE);
                        }

                    } else {

                        choiceVideo();
                    }

                } else {

                    Toast.makeText(getActivity(), String.format(Locale.getDefault(), getString(R.string.video_limit_of), VIDEO_FILES_LIMIT), Toast.LENGTH_SHORT).show();
                }
            }
        });

        mAddLocationButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (mBottomSheetDialog != null) {

                    mBottomSheetDialog.dismiss();
                }

                if (App.getInstance().getCountry().length() != 0 || App.getInstance().getCity().length() != 0) {

                    setLocation();

                } else {

                    if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) || ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION)){

                            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_ACCESS_LOCATION);

                        } else {

                            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_ACCESS_LOCATION);
                        }

                    } else {

                        Intent i = new Intent(getActivity(), LocationActivity.class);
                        startActivityForResult(i, 77);
                    }
                }
            }
        });

        mAddFeelingButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (mBottomSheetDialog != null) {

                    mBottomSheetDialog.dismiss();
                }

                choiceFeeling();
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

    // Prevent dialog dismiss when orientation changes
    private static void doKeepDialog(Dialog dialog){

        WindowManager.LayoutParams lp = new  WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        dialog.getWindow().setAttributes(lp);
    }


    private int getMediaCount(int type) {

        int count = 0;

        if (itemsList.size() > 0) {

            for (int i = 0; i < itemsList.size(); i++) {

                if (itemsList.get(i).getType() == type) {

                    count++;
                }
            }
        }

        return count;
    }

    public Boolean uploadFile(String serverURL, File file, final int index) {

        Log.e("uploadFile", "index:" + index);

        final OkHttpClient client = new OkHttpClient();

        client.setProtocols(Arrays.asList(Protocol.HTTP_1_1));

        try {
            RequestBody requestBody = new MultipartBuilder()
                    .type(MultipartBuilder.FORM)
                    .addFormDataPart("uploaded_file", file.getName(), RequestBody.create(MediaType.parse("text/csv"), file))
                    .addFormDataPart("accountId", Long.toString(App.getInstance().getId()))
                    .addFormDataPart("accessToken", App.getInstance().getAccessToken())
                    .build();

            com.squareup.okhttp.Request request = new com.squareup.okhttp.Request.Builder()
                    .url(serverURL)
                    .addHeader("Accept", "application/json;")
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

                    try {

                        JSONObject result = new JSONObject(jsonData);

                        if (!result.getBoolean("error")) {

                            itemsList.get(index).setImageUrl(result.getString("imgUrl"));
                        }

                        Log.d("My App", response.toString());

                    } catch (Throwable t) {

                        Log.e("My App", "Could not parse malformed JSON: \"" + t.getMessage() + "\"");

                    } finally {

                        Log.e("response", jsonData);

                        uploadImages(index );
                    }
                }
            });

            return true;

        } catch (Exception ex) {
            // Handle the error

            loading = false;

            hidepDialog();
        }

        return false;
    }

    public Boolean uploadVideoFile(String serverURL, File file, File videoFile, final int index) {

        final OkHttpClient client = new OkHttpClient();

        client.setProtocols(Arrays.asList(Protocol.HTTP_1_1));

        try {

            RequestBody requestBody = new MultipartBuilder()
                    .type(MultipartBuilder.FORM)
                    .addFormDataPart("uploaded_file", file.getName(), RequestBody.create(MediaType.parse("text/csv"), file))
                    .addFormDataPart("uploaded_video_file", videoFile.getName(), RequestBody.create(MediaType.parse("text/csv"), videoFile))
                    .addFormDataPart("accountId", Long.toString(App.getInstance().getId()))
                    .addFormDataPart("accessToken", App.getInstance().getAccessToken())
                    .build();
            Log.e("requestBody",requestBody.toString());

            com.squareup.okhttp.Request request = new com.squareup.okhttp.Request.Builder()
                    .url(serverURL)
                    .addHeader("Accept", "application/json;")
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

                    Log.e("responseVideo", jsonData);

                    try {

                        JSONObject result = new JSONObject(jsonData);

                        if (!result.getBoolean("error")) {

                            itemsList.get(index).setImageUrl(result.getString("imgFileUrl"));
                            itemsList.get(index).setVideoUrl(result.getString("videoFileUrl"));
                        }

                        Log.d("My App", response.toString());

                    } catch (Throwable t) {

                        Log.e("My App", "Could not parse malformed JSON: \"" + t.getMessage() + "\"");

                    } finally {

                        Log.e("response", jsonData);

                        uploadImages(index);
                    }

                }
            });

            return true;

        } catch (Exception ex) {
            // Handle the error

            loading = false;

            hidepDialog();
        }

        return false;
    }

    public void getMediaItems() {

        CustomRequest jsonReq = new CustomRequest(Request.Method.POST, METHOD_ITEM_GET_IMAGES, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        if (!isAdded() || getActivity() == null) {

                            Log.e("ERROR", "NewItemFragment Not Added to Activity");

                            return;
                        }

                        try {

                            int arrayLength = 0;

                            if (!response.getBoolean("error")) {

                                if (response.has("items")) {

                                    JSONArray itemsArray = response.getJSONArray("items");

                                    arrayLength = itemsArray.length();

                                    if (arrayLength > 0) {

                                        for (int i = 0; i < itemsArray.length(); i++) {

                                            JSONObject itemObj = (JSONObject) itemsArray.get(i);

                                            MediaItem item = new MediaItem();

                                            item.setImageUrl(itemObj.getString("imgUrl"));
                                            item.setType(0);

                                            itemsList.add(item);
                                        }
                                    }
                                }
                            }

                        } catch (JSONException e) {

                            e.printStackTrace();

                        } finally {

                            updateMediaLayout();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (!isAdded() || getActivity() == null) {

                    Log.e("ERROR", "NewItemFragment Not Added to Activity");

                    return;
                }

                updateMediaLayout();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("accountId", Long.toString(App.getInstance().getId()));
                params.put("accessToken", App.getInstance().getAccessToken());
                params.put("itemId", Long.toString(item.getId()));
                params.put("language", "en");

                return params;
            }
        };

        App.getInstance().addToRequestQueue(jsonReq);
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

        TextView cancel_button = (TextView) dialog.findViewById(R.id.cancel_button);
        cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        NestedScrollView mDlgNestedView = (NestedScrollView) dialog.findViewById(R.id.nested_view);
        final RecyclerView mDlgRecyclerView = (RecyclerView) dialog.findViewById(R.id.recycler_view);

        final LinearLayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 2);
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

                item.setFeeling(position);

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

    public String getExt(String filePath){

        int strLength = filePath.lastIndexOf(".");

        if (strLength > 0) return filePath.substring(strLength + 1).toLowerCase();

        return null;
    }

    public void deleteTmpFiles() {

        if (filesList.size() != 0) {

            for (int i = 0; i < filesList.size(); i++) {

                String ext = getExt(filesList.get(i));
                File file;

                if (ext != null && ext.equals("jpg")) {

                    file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), filesList.get(i));

                } else {

                    file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES), filesList.get(i));
                }

                Helper.deleteFile(getContext(), file);
            }
        }
    }

}