package com.app.threads;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import com.google.android.material.tabs.TabLayout;
import androidx.viewpager.widget.ViewPager;

import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.app.threads.adapter.SectionsPagerAdapter;
import com.app.threads.common.ActivityBase;

public class GroupsActivity extends ActivityBase {
    ViewPager mViewPager;
    TabLayout mTabLayout;
    SectionsPagerAdapter adapter;

    ImageView ivBack;

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

        setContentView(R.layout.activity_groups);

        Boolean restore = false;
        if (savedInstanceState != null) {
            restore = savedInstanceState.getBoolean("restore");
        } else {
            restore = false;
        }


        ivBack = (ImageView) findViewById(R.id.ivBack);
        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        adapter = new SectionsPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new GroupsFragment(), getString(R.string.tab_groups));
        adapter.addFragment(new ManagedGroupsFragment(), getString(R.string.tab_managed_groups));
        mViewPager.setAdapter(adapter);
        mViewPager.setCurrentItem(0);

        // TabLayout
        mTabLayout = (TabLayout) findViewById(R.id.tab_layout);
        mTabLayout.setupWithViewPager(mViewPager);

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("restore", true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {
            case android.R.id.home: {
                finish();
                return true;
            }
            default: {
                return super.onOptionsItemSelected(item);
            }
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
