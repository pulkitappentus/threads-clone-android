package com.app.threads;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.MenuItem;

import com.app.threads.common.ActivityBase;
import com.app.threads.dialogs.ItemDeleteDialog;


public class MarketViewItemActivity extends ActivityBase implements ItemDeleteDialog.AlertPositiveListener {

    Fragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_market_view_item);

        if (savedInstanceState != null) {
            fragment = getSupportFragmentManager().getFragment(savedInstanceState, "currentFragment");
        } else {
            fragment = new MarketViewItemFragment();
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.container_body, fragment).commit();
    }

    @Override
    protected void onSaveInstanceState (Bundle outState) {

        super.onSaveInstanceState(outState);

        getSupportFragmentManager().putFragment(outState, "currentFragment", fragment);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        fragment.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onItemDelete(int position) {

        MarketViewItemFragment p = (MarketViewItemFragment) fragment;
        p.onItemDelete(position);
    }

    @Override
    public void onBackPressed(){

        finish();
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
}
