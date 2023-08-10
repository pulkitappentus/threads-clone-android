package com.app.threads;

import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.MergeAdapter;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.app.threads.adapter.AdvancedItemListAdapter;
import com.app.threads.adapter.ImageIconAdapter;
import com.app.threads.app.App;
import com.app.threads.constants.Constants;
import com.app.threads.model.Item;
import com.app.threads.util.Api;
import com.app.threads.util.CustomRequest;
import com.balysv.materialripple.MaterialRippleLayout;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;
import com.mikhaellopez.circularimageview.CircularImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FeedFragment extends Fragment implements Constants, SwipeRefreshLayout.OnRefreshListener {
    private static final String STATE_LIST = "State Adapter Data";

   // private CardView mNewItemBox;
    private CircularImageView mNewItemImage;
    private BottomSheetBehavior mBehavior;
    private BottomSheetDialog mBottomSheetDialog;

    private TextView mDesc;
    private TextView mMessage;
    private ImageView mSplash;
    int reason=10;

    private SwipeRefreshLayout mItemsContainer;
    private ArrayList<Item> itemsList;
    private AdvancedItemListAdapter itemsAdapter;

    private ImageIconAdapter imageIconAdapter;

    private MergeAdapter adapter;

    private int itemId = 0;
    private int arrayLength = 0;
    private Boolean loadingMore = false;
    private Boolean viewMore = false;
    private Boolean loaded = false;

    public FeedFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        adapter = new MergeAdapter();

        Boolean restore = false;
        if (savedInstanceState != null) {
            itemsList = savedInstanceState.getParcelableArrayList(STATE_LIST);
            itemsAdapter = new AdvancedItemListAdapter(getActivity(), itemsList);
            restore = savedInstanceState.getBoolean("restore");
            loaded = savedInstanceState.getBoolean("loaded");
            itemId = savedInstanceState.getInt("itemId");

        }
        else {
            itemsList = new ArrayList<>();
            itemsAdapter = new AdvancedItemListAdapter(getActivity(), itemsList);
            restore = false;
            loaded = false;
            itemId = 0;
        }

        imageIconAdapter = new ImageIconAdapter(getActivity());


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_feed, container, false);

        mItemsContainer = (SwipeRefreshLayout) rootView.findViewById(R.id.container_items);
        mItemsContainer.setOnRefreshListener(this);

        mDesc = (TextView) rootView.findViewById(R.id.desc);
        mMessage = (TextView) rootView.findViewById(R.id.message);
        mSplash = (ImageView) rootView.findViewById(R.id.splash);

        mDesc.setVisibility(View.GONE);

        // Prepare bottom sheet
        View mBottomSheet = rootView.findViewById(R.id.bottom_sheet);
        mBehavior = BottomSheetBehavior.from(mBottomSheet);

        // New item spotlight
     //   mNewItemBox = (CardView) rootView.findViewById(R.id.newItemBox);
        MaterialRippleLayout mNewItemButton = (MaterialRippleLayout) rootView.findViewById(R.id.newItemButton);

       // if (!loaded) mNewItemBox.setVisibility(View.GONE);
        mNewItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//              showPostDialog();
                Intent intent = new Intent(getActivity(), NewItemActivity.class);
                startActivityForResult(intent, FEED_NEW_POST);
            }
        });

        mNewItemImage = (CircularImageView) rootView.findViewById(R.id.newItemImage);
        TextView mNewItemTitle = (TextView) rootView.findViewById(R.id.newItemTitle);

        updateProfileInfo();

        NestedScrollView mNestedView = (NestedScrollView) rootView.findViewById(R.id.nested_view);

        RecyclerView mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        getUpdateLatLong();

        itemsAdapter.setOnMoreButtonClickListener(new AdvancedItemListAdapter.OnItemMenuButtonClickListener() {

            @Override
            public void onItemClick(Item obj, int actionId, int position) {

                switch (actionId){

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

       // mRecyclerView.setAdapter(itemsAdapter);

        adapter = new MergeAdapter(imageIconAdapter,itemsAdapter);
        mRecyclerView.setAdapter(adapter);

        mRecyclerView.setNestedScrollingEnabled(false);


        mNestedView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {

            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

                if (scrollY < oldScrollY) { // upFF


                }

                if (scrollY > oldScrollY) { // down


                }

                if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {

                    if (!loadingMore && (viewMore) && !(mItemsContainer.isRefreshing())) {

                        mItemsContainer.setRefreshing(true);

                        loadingMore = true;

                        getItems();
                    }
                }
            }
        });

        if (itemsAdapter.getItemCount() == 0) {

            showMessage(getText(R.string.label_empty_list).toString());

        } else {

            hideMessage();
        }
        ImageView ivSearch = rootView.findViewById(R.id.ivSearch);
        ImageView message = rootView.findViewById(R.id.ivMessage);

        ivSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), SearchActivity.class);
                startActivityForResult(i, 1001);
            }
        });

        message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), DialogsActivity.class);
                startActivity(i);
            }
        });


        return rootView;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {

        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser) {

            if (loaded) updateProfileInfo();

            Handler handler = new Handler();

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    if (isAdded()) {

                        if (!loaded) {

                            showMessage(getText(R.string.msg_loading_2).toString());

                            getItems();
                        }
                    }
                }
            }, 50);
        }
    }

    private void updateProfileInfo() {
        if (isAdded()) {

            if (App.getInstance().getPhotoUrl() != null && App.getInstance().getPhotoUrl().length() > 0) {

                App.getInstance().getImageLoader().get(App.getInstance().getPhotoUrl(), ImageLoader.getImageListener(mNewItemImage, R.drawable.profile_default_photo, R.drawable.profile_default_photo));

            } else {

                mNewItemImage.setImageResource(R.drawable.profile_default_photo);
            }

            if (App.getInstance().getFullname().length() != 0) {

            } else {

            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);
        outState.putBoolean("restore", true);
        outState.putBoolean("loaded", loaded);
        outState.putInt("itemId", itemId);
        outState.putParcelableArrayList(STATE_LIST, itemsList);
    }

    @Override
    public void onRefresh() {

        if (App.getInstance().isConnected()) {

            itemId = 0;
            getItems();

        } else {

            mItemsContainer.setRefreshing(false);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == FEED_NEW_POST && resultCode == getActivity().RESULT_OK && null != data) {

            itemId = 0;
            getItems();

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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        super.onCreateOptionsMenu(menu, inflater);
    }

    public void getUpdateLatLong() {
        CustomRequest jsonReq = new CustomRequest(Request.Method.POST,UPDATE_LAT_LONG, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("kgmnlf",response.toString());
                        if (!isAdded() || getActivity() == null) {
                            Log.e("ERROR", "FeedFragment Not Added to Activity");
                            return;
                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                if (!isAdded() || getActivity() == null) {

                    Log.e("ERROR", "FeedFragment Not Added to Activity");

                    return;
                }

                loadingComplete();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("accountId", Long.toString(App.getInstance().getId()));
                params.put("accessToken", App.getInstance().getAccessToken());
                params.put("lat", Double.toString(App.getInstance().getLat()));
                params.put("lng", Double.toString(App.getInstance().getLng()));
                Log.e("kjhk",params.toString());
                return params;
            }
        };
        App.getInstance().addToRequestQueue(jsonReq);
    }

    public void getItems() {
        mItemsContainer.setRefreshing(true);
        CustomRequest jsonReq = new CustomRequest(Request.Method.POST, METHOD_FEEDS_GET, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (!isAdded() || getActivity() == null) {
                            Log.e("ERROR", "FeedFragment Not Added to Activity");
                            return;
                        }

                        if (!loadingMore) {
                            itemsList.clear();
                        }

                        try {
                            arrayLength = 0;
                            if (!response.getBoolean("error")) {

                                itemId = response.getInt("itemId");

                                if (response.has("items")) {

                                    JSONArray itemsArray = response.getJSONArray("items");

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

                    Log.e("ERROR", "FeedFragment Not Added to Activity");

                    return;
                }

                loadingComplete();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("accountId", Long.toString(App.getInstance().getId()));
                params.put("accessToken", App.getInstance().getAccessToken());
                params.put("itemId", Integer.toString(itemId));
                params.put("language", "en");
                Log.e("hjgnhg",params.toString());
                return params;

            }
        };
        App.getInstance().addToRequestQueue(jsonReq);
    }

    public void loadingComplete() {
        loaded = true;
        //mNewItemBox.setVisibility(View.VISIBLE);

        if (arrayLength == LIST_ITEMS) {

            viewMore = true;

        } else {

            viewMore = false;
        }

        itemsAdapter.notifyDataSetChanged();

        if (itemsAdapter.getItemCount() == 0) {

            if (FeedFragment.this.isVisible()) {

                showMessage(getText(R.string.label_empty_list).toString());
            }

        } else {

            hideMessage();
        }

        loadingMore = false;
        mItemsContainer.setRefreshing(false);
    }

    // Item action


    private void showItemActionDialog(final int position) {

        if (mBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {

            mBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }

        final View view = getLayoutInflater().inflate(R.layout.item_action_sheet_list, null);

        View upShare = (View) view.findViewById(R.id.upShare);
        View downShare = (View) view.findViewById(R.id.downShare);

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

            downShare.setVisibility(View.GONE);

        } else {

            mEditButton.setVisibility(View.GONE);
            mDeleteButton.setVisibility(View.GONE);

            mRepostButton.setVisibility(View.VISIBLE);
            mReportButton.setVisibility(View.VISIBLE);

            upShare.setVisibility(View.GONE);
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

                Toast.makeText(getContext(),getText(R.string.linkCopied),Toast.LENGTH_SHORT).show();

//                Snackbar snackbar = Snackbar.make(getView(), getText(R.string.linkCopied), Snackbar.LENGTH_LONG);
//                Snackbar.SnackbarLayout snackbarLayout = (Snackbar.SnackbarLayout) snackbar.getView();
//                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) snackbarLayout.getLayoutParams();
//                layoutParams.setMargins(0, 0, 32, 32);
//                snackbarLayout.setLayoutParams(layoutParams);
//                snackbar.show();

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

        mBottomSheetDialog= new BottomSheetDialog(getActivity(), R.style.AppBottomSheetDialogTheme);
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

    public void delete(final int position) {

        final View view = getLayoutInflater().inflate(R.layout.dialog_delete, null);
       BottomSheetDialog mBottomSheetDialog= new BottomSheetDialog(getActivity(), R.style.AppBottomSheetDialogTheme);
        mBottomSheetDialog.setContentView(view);
        TextView tvCancel=mBottomSheetDialog.findViewById(R.id.tvCancel);
        TextView tvYes=mBottomSheetDialog.findViewById(R.id.tvYes);

         tvCancel.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 mBottomSheetDialog.cancel();
             }
         });

        tvYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Item item = itemsList.get(position);

                itemsList.remove(position);
                itemsAdapter.notifyDataSetChanged();

                if (itemsAdapter.getItemCount() == 0) {

                    showMessage(getText(R.string.label_empty_list).toString());
                }

                if (App.getInstance().isConnected()) {

                    Api api = new Api(getActivity());

                    api.postDelete(item.getId(), 1);

                } else {

                    Toast.makeText(getActivity(), getText(R.string.msg_network_error), Toast.LENGTH_SHORT).show();
                }

                mBottomSheetDialog.cancel();
            }
        });

        mBottomSheetDialog.show();
    }

    public void share(final int position) {

        final Item item = itemsList.get(position);

        Api api = new Api(getActivity());
        api.postShare(item);
    }

    public void repost(final int position) {
        View  view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_repost, null);
        BottomSheetDialog dialog = new BottomSheetDialog(getActivity(), R.style.AppBottomSheetDialogTheme);
        dialog.setContentView(view);
        dialog.setCancelable(true);
        TextView tvCancel=dialog.findViewById(R.id.tvCancel);
        TextView tvYes=dialog.findViewById(R.id.tvYes);

      tvCancel.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              dialog.cancel();
          }
      });

        tvYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Item item = itemsList.get(position);

                Intent i = new Intent(getActivity(), NewItemActivity.class);
                i.putExtra("position", position);
                i.putExtra("repost", item);
                startActivityForResult(i, ITEM_REPOST);
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public void report(final int position) {
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

                final Item item = itemsList.get(position);
                Api api = new Api(getActivity());
                api.newReport(item.getId(), REPORT_TYPE_ITEM, reason);
                Toast.makeText(getActivity(), getActivity().getString(R.string.label_post_reported), Toast.LENGTH_SHORT).show();
                mBottomSheetDialog.cancel();
            }
        });
        mBottomSheetDialog.show();

    }

    //

    public void showMessage(String message) {

        if (loaded) {

            mDesc.setVisibility(View.VISIBLE);

        } else {

            mDesc.setVisibility(View.GONE);
        }

        mMessage.setText(message);
        mMessage.setVisibility(View.VISIBLE);

        mSplash.setVisibility(View.VISIBLE);
    }

    public void hideMessage() {
        mDesc.setVisibility(View.GONE);
        mMessage.setVisibility(View.GONE);

        mSplash.setVisibility(View.GONE);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

}