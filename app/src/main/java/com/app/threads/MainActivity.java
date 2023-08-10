package com.app.threads;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;

import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.core.view.MenuItemCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.ads.MobileAds;

import java.util.Locale;

import com.app.threads.adapter.SectionsPagerAdapter;
import com.app.threads.app.App;
import com.app.threads.common.ActivityBase;

public class MainActivity extends ActivityBase {
    Toolbar mToolbar;

    private AppBarLayout mAppBarLayout;

    ViewPager mViewPager;

    TabLayout mTabLayout;

    Boolean action = false;

    SectionsPagerAdapter adapter;

    int pageId = 0;

    int tab_position = 0;

    String query = "";

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor("#ffffff"));
            View decor = getWindow().getDecorView();
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        setContentView(R.layout.activity_main);
        // ge intent
        Intent i = getIntent();

        tab_position = i.getIntExtra("pageId", 0);

        // Initialize Google Admob

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        Boolean restore = false;
        if (savedInstanceState != null) {
            restore = savedInstanceState.getBoolean("restore");
        } else {
            restore = false;
        }

        //mAppBarLayout = (AppBarLayout) findViewById(R.id.appbar_layout);

        // getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // getSupportActionBar().setHomeButtonEnabled(true);

        // ViewPager

        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        adapter = new SectionsPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new FeedFragment(), "");
        adapter.addFragment(new SearchFragment().newInstance(true), "");
       // adapter.addFragment(new FriendsFragment().newInstance(true), "");
      //  adapter.addFragment(new ProfileFragment().newInstance(true), "");
        adapter.addFragment(new NewItemFragment().newInstance(true), "");
        adapter.addFragment(new NotificationsFragment(), "Activity");
        //adapter.addFragment(new MenuFragment(), "");
        adapter.addFragment(new ProfileFragment().newInstance(true), "");
        mViewPager.setAdapter(adapter);
        mViewPager.setCurrentItem(tab_position);

        mTabLayout = (TabLayout) findViewById(R.id.tab_layout);
        mTabLayout.setupWithViewPager(mViewPager);

        initTabs();
        refreshBadges();

        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                tab_position = tab.getPosition();
                updateActiveTab(tab.getPosition());

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

                updateInactiveTab(tab.getPosition());
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

                // animateTab(tab.getPosition());
            }
        });
    }

    private void animateTab(int tab_position) {

        ImageView tab_icon;

        tab_icon = (ImageView) mTabLayout.getTabAt(tab_position).getCustomView().findViewById(R.id.tab_icon);

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

        tab_icon.startAnimation(scale);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);

        outState.putBoolean("restore", true);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void initTabs() {

        ImageView tab_icon;
        TextView tab_badge;
        RelativeLayout tab_layout;

        // Feed tab
        mTabLayout.getTabAt(0).setCustomView(R.layout.tab_layout);
        tab_icon = (ImageView) mTabLayout.getTabAt(0).getCustomView().findViewById(R.id.tab_icon);
        tab_icon.setImageResource(R.drawable.ic_feed_unselected);
        tab_badge = (TextView) mTabLayout.getTabAt(0).getCustomView().findViewById(R.id.tab_badge);
        tab_badge.setText("");
        tab_badge.setVisibility(View.GONE);
        tab_layout = (RelativeLayout) mTabLayout.getTabAt(0).getCustomView().findViewById(R.id.tab_main_layout);

        tab_layout.setOnTouchListener(new View.OnTouchListener() {

            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_DOWN){

                    animateTab(0);
                }

                return false;
            }
        });

        // Groups tab

        mTabLayout.getTabAt(1).setCustomView(R.layout.tab_layout);
        tab_icon = (ImageView)mTabLayout.getTabAt(1).getCustomView().findViewById(R.id.tab_icon);
        tab_icon.setImageResource(R.drawable.ic_search_unselected);
        tab_badge = (TextView)mTabLayout.getTabAt(1).getCustomView().findViewById(R.id.tab_badge);
        tab_badge.setText("");
        tab_badge.setVisibility(View.GONE);
        tab_layout = (RelativeLayout) mTabLayout.getTabAt(1).getCustomView().findViewById(R.id.tab_main_layout);

        tab_layout.setOnTouchListener(new View.OnTouchListener() {

            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_DOWN){

                    animateTab(1);
                }

                return false;
            }
        });


        // Profile tab

        mTabLayout.getTabAt(2).setCustomView(R.layout.tab_layout);
        tab_icon = (ImageView)mTabLayout.getTabAt(2).getCustomView().findViewById(R.id.tab_icon);
        tab_icon.setImageResource(R.drawable.ic_un_create_post);
        tab_badge = (TextView)mTabLayout.getTabAt(2).getCustomView().findViewById(R.id.tab_badge);
        tab_badge.setText("");
        tab_badge.setVisibility(View.GONE);
        tab_layout = (RelativeLayout) mTabLayout.getTabAt(2).getCustomView().findViewById(R.id.tab_main_layout);

        tab_layout.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_DOWN){

                    animateTab(2);
                }

                return false;
            }
        });

        // Notifications tab

        mTabLayout.getTabAt(3).setCustomView(R.layout.tab_layout);
        tab_icon = (ImageView)mTabLayout.getTabAt(3).getCustomView().findViewById(R.id.tab_icon);
        tab_icon.setImageResource(R.drawable.ic_notification_unselected);
        tab_badge = (TextView)mTabLayout.getTabAt(3).getCustomView().findViewById(R.id.tab_badge);
        tab_badge.setText("");
        tab_badge.setVisibility(View.GONE);
        tab_layout = (RelativeLayout) mTabLayout.getTabAt(3).getCustomView().findViewById(R.id.tab_main_layout);

        tab_layout.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_DOWN){

                    animateTab(3);
                }

                return false;
            }
        });

        // Menu tab

        mTabLayout.getTabAt(4).setCustomView(R.layout.tab_layout);
        tab_icon = (ImageView)mTabLayout.getTabAt(4).getCustomView().findViewById(R.id.tab_icon);
        tab_icon.setImageResource(R.drawable.ic_profile_unselected);
        tab_badge = (TextView)mTabLayout.getTabAt(4).getCustomView().findViewById(R.id.tab_badge);
        tab_badge.setText("");
        tab_badge.setVisibility(View.GONE);
        tab_layout = (RelativeLayout) mTabLayout.getTabAt(4).getCustomView().findViewById(R.id.tab_main_layout);

        tab_layout.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_DOWN){

                    animateTab(4);
                }

                return false;
            }
        });

        // Get selected tab

        updateActiveTab(tab_position);
    }

    private void updateActiveTab(int tab_position) {

        ImageView tab_icon;

        tab_icon = mTabLayout.getTabAt(tab_position).getCustomView().findViewById(R.id.tab_icon);

        switch (tab_position) {

            case 0: {

                tab_icon.setImageResource(R.drawable.ic_feed_selected);

                break;
            }

            case 1: {

                tab_icon.setImageResource(R.drawable.ic_search_selected);

                break;
            }

            case 2: {

                tab_icon.setImageResource(R.drawable.ic_create_post);

                break;
            }

            case 3: {

                tab_icon.setImageResource(R.drawable.ic_notification_selected);

                break;
            }

            default: {

                tab_icon.setImageResource(R.drawable.ic_profile_selected);

                break;
            }
        }
    }

    private void updateInactiveTab(int tab_position) {

        ImageView tab_icon;

        tab_icon = mTabLayout.getTabAt(tab_position).getCustomView().findViewById(R.id.tab_icon);

        switch (tab_position) {

            case 0: {
                tab_icon.setImageResource(R.drawable.ic_feed_unselected);
                break;
            }

            case 1: {

                tab_icon.setImageResource(R.drawable.ic_search_unselected);

                break;
            }

            case 2: {

                tab_icon.setImageResource(R.drawable.ic_un_create_post);

                break;
            }

            case 3: {

                tab_icon.setImageResource(R.drawable.ic_notification_unselected);

                break;
            }

            default: {

                tab_icon.setImageResource(R.drawable.ic_profile_unselected);

                break;
            }
        }
    }

    private void refreshBadges() {

        TextView tab_badge;

        // Friends tab

        tab_badge = mTabLayout.getTabAt(1).getCustomView().findViewById(R.id.tab_badge);

        if (App.getInstance().getNewFriendsCount() > 0) {

            if (App.getInstance().getNewFriendsCount() > 9) {

                tab_badge.setText("9+");

            } else {

                tab_badge.setText(String.format(Locale.getDefault(), "%d", App.getInstance().getNewFriendsCount()));
            }

            tab_badge.setVisibility(View.VISIBLE);

        } else {

            tab_badge.setVisibility(View.GONE);
        }

        // Notifications tab

        tab_badge = mTabLayout.getTabAt(3).getCustomView().findViewById(R.id.tab_badge);

        if (App.getInstance().getNotificationsCount() > 0) {


            if (App.getInstance().getNotificationsCount() > 9) {

                tab_badge.setText("9+");

            } else {

                tab_badge.setText(String.format(Locale.getDefault(), "%d", App.getInstance().getNotificationsCount()));
            }

            tab_badge.setVisibility(View.VISIBLE);

        } else {

            tab_badge.setText("");
            tab_badge.setVisibility(View.GONE);
        }

        // Menu tab

        tab_badge = mTabLayout.getTabAt(4).getCustomView().findViewById(R.id.tab_badge);

        if (App.getInstance().getGuestsCount() > 0 || App.getInstance().getNewFriendsCount() > 0) {

            tab_badge.setVisibility(View.VISIBLE);

        } else {

            tab_badge.setVisibility(View.GONE);
        }

        invalidateOptionsMenu();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);

        final MenuItem menuItem = menu.findItem(R.id.action_messenger);
        View actionView = MenuItemCompat.getActionView(menuItem);
        View messenger_badge = actionView.findViewById(R.id.messenger_badge);

        if (messenger_badge != null) {

            if (App.getInstance().getMessagesCount() == 0) {

                messenger_badge.setVisibility(View.GONE);

            } else {

                messenger_badge.setVisibility(View.GONE);

                String count_txt = App.getInstance().getMessagesCount() + "";

                if (App.getInstance().getMessagesCount() > 9) count_txt = "9+";

                ((TextView) messenger_badge.findViewById(R.id.counter)).setText(count_txt);
            }
        }

        actionView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                onOptionsItemSelected(menuItem);
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {

            case android.R.id.home: {

                return true;
            }

            case R.id.action_messenger: {

                Intent i = new Intent(MainActivity.this, DialogsActivity.class);
                startActivity(i);
                return true;
            }

            case R.id.action_search: {

                Intent i = new Intent(MainActivity.this, SearchActivity.class);
                startActivityForResult(i, 1001);

                return true;
            }

            default: {

                return super.onOptionsItemSelected(item);
            }
        }
    }

    public void hideKeyboard() {

        View view = this.getCurrentFocus();

        if (view != null) {

            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_DOWN) {

            View v = getCurrentFocus();

            if ( v instanceof EditText) {

                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);

                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {

                    v.clearFocus();

                     hideKeyboard();
                }
            }
        }

        return super.dispatchTouchEvent(event);
    }

    @Override
    protected void onResume() {

        super.onResume();

        refreshBadges();

        registerReceiver(mMessageReceiver, new IntentFilter(TAG_UPDATE_BADGES));
    }

    @Override
    public void onPause() {

        super.onPause();

        unregisterReceiver(mMessageReceiver);
    }

    //This is the handler that will manager to process the broadcast intent
    private final BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            // Extract data included in the Intent
            // String message = intent.getStringExtra("message");

            refreshBadges();
        }
    };

}
