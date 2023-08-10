package com.app.threads;

import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.tabs.TabLayout;
import androidx.viewpager.widget.ViewPager;

import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.app.threads.adapter.SectionsPagerAdapter;
import com.app.threads.common.ActivityBase;

public class MarketActivity extends ActivityBase {
    ViewPager mViewPager;
    TabLayout mTabLayout;
    ImageView ivBack;

    SectionsPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_market);

        Boolean restore = false;
        if (savedInstanceState != null) {
            restore = savedInstanceState.getBoolean("restore");
        } else {
            restore = false;
        }

        // ViewPager

        ivBack = (ImageView) findViewById(R.id.ivBack);
        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        adapter = new SectionsPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new MarketFragment(), getString(R.string.tab_market));
        adapter.addFragment(new MarketMyItemsFragment(), getString(R.string.tab_my_products));
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
        // your code.

        finish();
    }
}
