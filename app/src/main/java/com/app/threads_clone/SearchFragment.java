package com.app.threads_clone;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.app.threads_clone.adapter.SearchListAdapter;
import com.app.threads_clone.app.App;
import com.app.threads_clone.constants.Constants;
import com.app.threads_clone.dialogs.SearchSettingsDialog;
import com.app.threads_clone.model.Profile;
import com.app.threads_clone.util.CustomRequest;

public class SearchFragment extends Fragment implements Constants, SwipeRefreshLayout.OnRefreshListener {

    private static final String STATE_LIST = "State Adapter Data";
    SearchView searchView = null;

    RecyclerView mRecyclerView;
    TextView mMessage, mHeaderText;

    ImageView mSplash,ivFilter,ivClear;
    EditText etSearchData;

    LinearLayout mHeaderContainer;
    SwipeRefreshLayout mItemsContainer;

    private ArrayList<Profile> itemsList;
    private SearchListAdapter itemsAdapter;

    public String queryText = "", currentQuery = "", oldQuery = "";
    private int search_gender = -1, search_online = -1, search_photo = -1, preload_gender = -1;

    public int itemCount;
    private int userId = 0;
    private int itemId = 0;
    private int arrayLength = 0;
    private Boolean loadingMore = false;
    private Boolean viewMore = false;
    private Boolean preload = true;

    int pastVisiblesItems = 0, visibleItemCount = 0, totalItemCount = 0;

//    public SearchFragment() {
//        // Required empty public constructor
//    }

    public SearchFragment newInstance(Boolean pager) {
        SearchFragment myFragment = new SearchFragment();

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

        Intent i = getActivity().getIntent();
        if (i.getStringExtra("query") != null) {
            currentQuery = queryText = i.getStringExtra("query");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_search, container, false);
        Boolean restore = false;
        if (savedInstanceState != null) {

           itemsList = savedInstanceState.getParcelableArrayList(STATE_LIST);
            itemsAdapter = new SearchListAdapter(getActivity(), itemsList);

            currentQuery = queryText = savedInstanceState.getString("queryText");

            preload = savedInstanceState.getBoolean("preload");
            restore = savedInstanceState.getBoolean("restore");
            itemId = savedInstanceState.getInt("itemId");
            userId = savedInstanceState.getInt("userId");
            itemCount = savedInstanceState.getInt("itemCount");
            search_gender = savedInstanceState.getInt("search_gender");
            preload_gender = savedInstanceState.getInt("preload_gender");
            search_online = savedInstanceState.getInt("search_online");

        } else {

            itemsList = new ArrayList<Profile>();
            itemsAdapter = new SearchListAdapter(getActivity(), itemsList);

            preload = true;
            restore = false;
            itemId = 0;
            userId = 0;
            itemCount = 0;
            search_gender = -1;
            preload_gender = -1;
            search_online = -1;
        }

        ivFilter = (ImageView) rootView.findViewById(R.id.ivFilter);
        etSearchData = (EditText) rootView.findViewById(R.id.etSearchData);
        ivClear = (ImageView) rootView.findViewById(R.id.ivClear);
        ivClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                etSearchData.setText("");
                queryText = etSearchData.getText().toString();
                searchStart();
                ivClear.setVisibility(View.GONE);
            }
        });

        mItemsContainer = (SwipeRefreshLayout) rootView.findViewById(R.id.container_items);
        mItemsContainer.setOnRefreshListener(this);

        mMessage = (TextView) rootView.findViewById(R.id.message);
        mSplash = (ImageView) rootView.findViewById(R.id.splash);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);

        final LinearLayoutManager mLayoutManager = new GridLayoutManager(getActivity(),1);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(itemsAdapter);

        itemsAdapter.setOnItemClickListener(new SearchListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, Profile profile) {
//                if (profile.isFriend()) {
//                    removeFromFriends();
//
//                } else {
//                    addFollower();
//                }
            }
        });

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    visibleItemCount = mLayoutManager.getChildCount();
                    totalItemCount = mLayoutManager.getItemCount();
                    pastVisiblesItems = mLayoutManager.findFirstVisibleItemPosition();

                    if (!loadingMore) {

                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount && (viewMore) && !(mItemsContainer.isRefreshing())) {

                            Log.e("...", "Last Item Wow !");

                            if (preload) {
                                loadingMore = true;
                                preload();

                            } else {
                                currentQuery = getCurrentQuery();
                                if (currentQuery.equals(oldQuery)) {

                                    loadingMore = true;

                                    search();
                                }
                            }
                        }
                    }
                }
            }
        });

        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), mRecyclerView, new RecyclerItemClickListener.OnItemClickListener() {

            @Override
            public void onItemClick(View view, int position) {

                Profile u = (Profile) itemsList.get(position);

                Intent intent = new Intent(getActivity(), ProfileActivity.class);
                intent.putExtra("profileId", u.getId());
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(View view, int position) {
                // ...
            }
        }));

        if (itemsAdapter.getItemCount() == 0) {

            showMessage(getText(R.string.enter_a_few_words_to_search).toString());

        } else {
            hideMessage();
        }

        etSearchData.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                queryText = s.toString();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {


            }
        });

        etSearchData.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_SEARCH) {
                    queryText = textView.toString();
                    searchStart();
                    ivClear.setVisibility(View.VISIBLE);
                    return true;
                }
                return false;
            }

        });


        ivFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /** Getting the fragment manager */
                android.app.FragmentManager fm = getActivity().getFragmentManager();

                /** Instantiating the DialogFragment class */
                SearchSettingsDialog alert = new SearchSettingsDialog();

                /** Creating a bundle object to store the selected item's index */
                Bundle b  = new Bundle();

                /** Storing the selected item's index in the bundle object */
                b.putInt("searchGender", search_gender);
                b.putInt("searchOnline", search_online);
                b.putInt("searchPhoto", search_photo);


                /** Setting the bundle object to the dialog fragment object */
                alert.setArguments(b);

                /** Creating the dialog fragment object, which will in turn open the alert dialog window */

                alert.show(fm, "alert_dialog_search_settings");

                final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
            }
        });
        if (queryText.length() == 0) {

            if (itemsAdapter.getItemCount() == 0) {

                showMessage(getText(R.string.enter_a_few_words_to_search).toString());

            } else {
                hideMessage();
            }

        } else {

            if (itemsAdapter.getItemCount() == 0) {

                showMessage(getString(R.string.label_search_results_error));

            } else {
                hideMessage();
            }
        }

        if (!restore) {
            if (queryText.length() > 0) {
                preload = false;
                search();

            } else {
                if (preload) {
                    preload();
                }
            }
        }


        // Inflate the layout for this fragment
        return rootView;
    }

    public void onCloseSettingsDialog(int searchGender, int searchOnline, int searchPhoto) {

        search_gender = searchGender;
        search_online = searchOnline;
        search_photo = searchPhoto;

        String q = getCurrentQuery();

        if (preload) {

            itemId = 0;

            preload();

        } else {

            if (q.length() > 0) {

                searchStart();
            }
        }
    }

    @Override
    public void onRefresh() {

        currentQuery = queryText;

        currentQuery = currentQuery.trim();

        if (App.getInstance().isConnected() && currentQuery.length() != 0) {

            userId = 0;
            search();

        } else {

            mItemsContainer.setRefreshing(false);
        }
    }

    public String getCurrentQuery() {
        String searchText = etSearchData.getText().toString();
        searchText = searchText.trim();

        return searchText;
    }

    public void searchStart() {
        preload = false;

        currentQuery = getCurrentQuery();

        if (App.getInstance().isConnected()) {

            userId = 0;
            search();

        } else {
            Toast.makeText(getActivity(), getText(R.string.msg_network_error), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("queryText", queryText);
        outState.putBoolean("restore", true);
        outState.putBoolean("preload", preload);
        outState.putInt("itemId", itemId);
        outState.putInt("userId", userId);
        outState.putInt("itemCount", itemCount);
        outState.putInt("search_gender", search_gender);
        outState.putInt("preload_gender", preload_gender);
        outState.putInt("search_online", search_online);
        outState.putParcelableArrayList(STATE_LIST, itemsList);
    }

    public void search() {
        mItemsContainer.setRefreshing(true);
        CustomRequest jsonReq = new CustomRequest(Request.Method.POST, METHOD_APP_SEARCH, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        if (!isAdded() || getActivity() == null) {

                            Log.e("ERROR", "SearchFragment Not Added to Activity");

                            return;
                        }

                        try {

                            if (!loadingMore) {
                                itemsList.clear();
                            }

                            arrayLength = 0;

                            if (!response.getBoolean("error")) {

                                itemCount = response.getInt("itemCount");
                                oldQuery = response.getString("query");
                                userId = response.getInt("itemId");

                                if (response.has("items")) {

                                    JSONArray usersArray = response.getJSONArray("items");

                                    arrayLength = usersArray.length();

                                    if (arrayLength > 0) {

                                        for (int i = 0; i < usersArray.length(); i++) {

                                            JSONObject profileObj = (JSONObject) usersArray.get(i);

                                            Profile profile = new Profile(profileObj);

                                            itemsList.add(profile);
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

                    Log.e("ERROR", "SearchFragment Not Added to Activity");

                    return;
                }

                loadingComplete();

                Log.e("SearchFragment search()", error.toString());
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("accountId", Long.toString(App.getInstance().getId()));
                params.put("accessToken", App.getInstance().getAccessToken());
                params.put("query", currentQuery);
                params.put("userId", Integer.toString(userId));
                params.put("gender", Integer.toString(search_gender));
                params.put("online", Integer.toString(search_online));
                params.put("photo", Integer.toString(search_photo));

                return params;
            }
        };

        App.getInstance().addToRequestQueue(jsonReq);
    }

    public void preload() {

        if (preload) {

            mItemsContainer.setRefreshing(true);

            CustomRequest jsonReq = new CustomRequest(Request.Method.POST, METHOD_APP_SEARCH_PRELOAD, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            if (!isAdded() || getActivity() == null) {

                                Log.e("ERROR", "SearchFragment Not Added to Activity");

                                return;
                            }

                            try {

                                if (!loadingMore) {

                                    itemsList.clear();
                                }

                                arrayLength = 0;

                                if (!response.getBoolean("error")) {

                                    itemId = response.getInt("itemId");

                                    if (response.has("items")) {

                                        JSONArray usersArray = response.getJSONArray("items");

                                        arrayLength = usersArray.length();

                                        if (arrayLength > 0) {

                                            for (int i = 0; i < usersArray.length(); i++) {

                                                JSONObject profileObj = (JSONObject) usersArray.get(i);

                                                Profile u = new Profile(profileObj);

                                                itemsList.add(u);
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

                        Log.e("ERROR", "SearchFragment Not Added to Activity");

                        return;
                    }

                    loadingComplete();
                    Toast.makeText(getActivity(), getString(R.string.error_data_loading), Toast.LENGTH_LONG).show();
                }
            }) {

                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("accountId", Long.toString(App.getInstance().getId()));
                    params.put("accessToken", App.getInstance().getAccessToken());
                    params.put("itemId", Integer.toString(itemId));
                    params.put("gender", Integer.toString(search_gender));
                    params.put("online", Integer.toString(search_online));
                    params.put("photo", Integer.toString(search_photo));

                    return params;
                }
            };

            App.getInstance().addToRequestQueue(jsonReq);
        }
    }

    public void loadingComplete() {

        if (arrayLength == LIST_ITEMS) {

            viewMore = true;

        } else {

            viewMore = false;
        }

        itemsAdapter.notifyDataSetChanged();

        loadingMore = false;

        mItemsContainer.setRefreshing(false);

        if (itemsAdapter.getItemCount() == 0) {

            showMessage(getString(R.string.label_search_results_error));

        } else {

            hideMessage();

            if (preload) {

            } else {

            }
        }
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
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    static class RecyclerItemClickListener implements RecyclerView.OnItemTouchListener {

        public interface OnItemClickListener {

            void onItemClick(View view, int position);

            void onItemLongClick(View view, int position);
        }

        private final OnItemClickListener mListener;

        private final GestureDetector mGestureDetector;

        public RecyclerItemClickListener(Context context, final RecyclerView recyclerView, RecyclerItemClickListener.OnItemClickListener listener) {

            mListener = listener;

            mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {

                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {

                    View childView = recyclerView.findChildViewUnder(e.getX(), e.getY());

                    if (childView != null && mListener != null) {

                        mListener.onItemLongClick(childView, recyclerView.getChildAdapterPosition(childView));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView view, MotionEvent e) {

            View childView = view.findChildViewUnder(e.getX(), e.getY());

            if (childView != null && mListener != null && mGestureDetector.onTouchEvent(e)) {

                mListener.onItemClick(childView, view.getChildAdapterPosition(childView));
            }

            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView view, MotionEvent motionEvent) {

        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }
    //
//    public void removeFromFriends() {
//
//        loading = true;
//
//        showpDialog();
//
//        CustomRequest jsonReq = new CustomRequest(Request.Method.POST, METHOD_FRIENDS_REMOVE, null,
//                new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//
//                        if (!isAdded() || getActivity() == null) {
//
//                            Log.e("ERROR", "ProfileFragment Not Added to Activity");
//
//                            return;
//                        }
//
//                        try {
//
//                            if (!response.getBoolean("error")) {
//
//                                profile.setFriend(false);
//                                profile.setFriendsCount(profile.getFriendsCount() - 1);
//
//                                updateProfile();
//                            }
//
//                        } catch (JSONException e) {
//
//                            e.printStackTrace();
//
//                        } finally {
//
//                            loading = false;
//
//                            hidepDialog();
//                        }
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//
//                if (!isAdded() || getActivity() == null) {
//
//                    Log.e("ERROR", "ProfileFragment Not Added to Activity");
//
//                    return;
//                }
//
//                loading = false;
//
//                hidepDialog();
//            }
//        }) {
//
//            @Override
//            protected Map<String, String> getParams() {
//                Map<String, String> params = new HashMap<String, String>();
//                params.put("accountId", Long.toString(App.getInstance().getId()));
//                params.put("accessToken", App.getInstance().getAccessToken());
//                params.put("friendId", Long.toString(profile.getId()));
//
//                return params;
//            }
//        };
//
//        App.getInstance().addToRequestQueue(jsonReq);
//    }
//
//    public void addFollower() {
//
//        showpDialog();
//
//        CustomRequest jsonReq = new CustomRequest(Request.Method.POST, METHOD_PROFILE_FOLLOW, null,
//                new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//
//                        if (!isAdded() || getActivity() == null) {
//
//                            Log.e("ERROR", "ProfileFragment Not Added to Activity");
//
//                            return;
//                        }
//
//                        try {
//
//                            if (!response.getBoolean("error")) {
//
//                                profile.setFollow(response.getBoolean("follow"));
//                                profile.setFollowersCount(response.getInt("followersCount"));
//
//                                updateProfileActionMainButton();
//
//                                changeAccessMode();
//                            }
//
//                        } catch (JSONException e) {
//
//                            e.printStackTrace();
//
//                        } finally {
//
//                            hidepDialog();
//                        }
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//
//                if (!isAdded() || getActivity() == null) {
//
//                    Log.e("ERROR", "ProfileFragment Not Added to Activity");
//
//                    return;
//                }
//
//                hidepDialog();
//            }
//        }) {
//
//            @Override
//            protected Map<String, String> getParams() {
//                Map<String, String> params = new HashMap<String, String>();
//                params.put("accountId", Long.toString(App.getInstance().getId()));
//                params.put("accessToken", App.getInstance().getAccessToken());
//                params.put("profileId", Long.toString(profile_id));
//
//                return params;
//            }
//        };
//
//        App.getInstance().addToRequestQueue(jsonReq);
//    }
}