package com.app.threads;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RetryPolicy;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.app.threads.adapter.BalanceHistoryListAdapter;
import com.app.threads.app.App;
import com.app.threads.constants.Constants;
import com.app.threads.model.BalanceItem;
import com.app.threads.util.CustomRequest;
import com.app.threads.view.LineItemDecoration;

public class BalanceHistoryFragment extends Fragment implements Constants, SwipeRefreshLayout.OnRefreshListener {

    private static final String STATE_LIST = "State Adapter Data";

    private ProgressDialog pDialog;

    private BottomSheetDialog mBottomSheetDialog;

    private TextView mMessage;
    private ImageView mSplash;

    SwipeRefreshLayout mItemsContainer;

    private ArrayList<BalanceItem> itemsList;
    private BalanceHistoryListAdapter itemsAdapter;

    private int itemId = 0;
    private int arrayLength = 0;
    private Boolean loadingMore = false;
    private Boolean viewMore = false;
    private Boolean restore = false;

    private Boolean loadingComplete = false;

    public BalanceHistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(false);

        initpDialog();

        if (savedInstanceState != null) {

            itemsList = savedInstanceState.getParcelableArrayList(STATE_LIST);
            itemsAdapter = new BalanceHistoryListAdapter(getActivity(), itemsList);

            restore = savedInstanceState.getBoolean("restore");
            itemId = savedInstanceState.getInt("itemId");
            loadingComplete = savedInstanceState.getBoolean("loadingComplete");

        } else {

            itemsList = new ArrayList<BalanceItem>();
            itemsAdapter = new BalanceHistoryListAdapter(getActivity(), itemsList);

            restore = false;
            itemId = 0;
            loadingComplete = false;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_balance_history, container, false);

        getActivity().setTitle(R.string.title_activity_balance_history);

        mItemsContainer = (SwipeRefreshLayout) rootView.findViewById(R.id.container_items);
        mItemsContainer.setOnRefreshListener(this);

        mMessage = (TextView) rootView.findViewById(R.id.message);
        mSplash = (ImageView) rootView.findViewById(R.id.splash);

        // Prepare bottom sheet

        View mBottomSheet = rootView.findViewById(R.id.bottom_sheet);
        BottomSheetBehavior mBehavior = BottomSheetBehavior.from(mBottomSheet);

        //

        NestedScrollView mNestedView = (NestedScrollView) rootView.findViewById(R.id.nested_view);

        RecyclerView mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.addItemDecoration(new LineItemDecoration(getActivity(), LinearLayout.VERTICAL));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mRecyclerView.setAdapter(itemsAdapter);

        mRecyclerView.setNestedScrollingEnabled(false);


        mNestedView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {

            if (scrollY < oldScrollY) { // up


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
        });

        if (itemsAdapter.getItemCount() == 0) {

            showMessage(getText(R.string.label_empty_list).toString());

        } else {

            hideMessage();
        }

        if (!restore) {

            showMessage(getText(R.string.msg_loading_2).toString());

            getItems();
        }

        // Inflate the layout for this fragment
        return rootView;
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
    public void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);

        outState.putBoolean("loadingComplete", true);
        outState.putBoolean("restore", true);
        outState.putInt("itemId", itemId);
        outState.putParcelableArrayList(STATE_LIST, itemsList);
    }

    public void getItems() {

        mItemsContainer.setRefreshing(true);

        CustomRequest jsonReq = new CustomRequest(Request.Method.POST, METHOD_PAYMENTS_GET, null,
                response -> {

                    if (!isAdded() || getActivity() == null) {

                        Log.e("ERROR", "BalanceHistoryFragment Not Added to Activity");

                        return;
                    }

                    try {

                        arrayLength = 0;

                        if (!loadingMore) {

                            itemsList.clear();
                        }

                        if (!response.getBoolean("error")) {

                            itemId = response.getInt("itemId");

                            JSONArray itemsArray = response.getJSONArray("items");

                            arrayLength = itemsArray.length();

                            if (arrayLength > 0) {

                                for (int i = 0; i < itemsArray.length(); i++) {

                                    JSONObject itemObj = (JSONObject) itemsArray.get(i);

                                    BalanceItem balanceItem = new BalanceItem(itemObj);

                                    itemsList.add(balanceItem);
                                }
                            }
                        }

                    } catch (JSONException e) {

                        e.printStackTrace();

                    } finally {

                        loadingComplete();
                    }
                }, error -> {

                    if (!isAdded() || getActivity() == null) {

                        Log.e("ERROR", "BalanceHistoryFragment Not Added to Activity");

                        return;
                    }

                    loadingComplete();
                }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("accountId", Long.toString(App.getInstance().getId()));
                params.put("accessToken", App.getInstance().getAccessToken());
                params.put("itemId", Integer.toString(itemId));

                return params;
            }
        };

        RetryPolicy policy = new DefaultRetryPolicy((int) TimeUnit.SECONDS.toMillis(15), DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

        jsonReq.setRetryPolicy(policy);

        App.getInstance().addToRequestQueue(jsonReq);
    }

    public void loadingComplete() {

        if (arrayLength == LIST_ITEMS) {

            viewMore = true;

        } else {

            viewMore = false;
        }

        itemsAdapter.notifyDataSetChanged();

        if (itemsAdapter.getItemCount() == 0) {

            showMessage(getText(R.string.label_empty_list).toString());

        } else {

            hideMessage();
        }

        loadingComplete = true;
        loadingMore = false;
        mItemsContainer.setRefreshing(false);

        getActivity().invalidateOptionsMenu();
    }

    public void showMessage(String message) {

        mMessage.setText(message);
        mMessage.setVisibility(View.VISIBLE);

        mSplash.setVisibility(View.VISIBLE);
    }

    public void hideMessage() {

        mMessage.setVisibility(View.GONE);

        mSplash.setVisibility(View.GONE);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {

        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);
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

}