package com.app.threads;


import android.os.Bundle;
import android.view.MenuItem;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.app.threads.common.ActivityBase;

public class RecentlyDeletedActivity extends ActivityBase {

    Fragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_recently_deleted);
        if (savedInstanceState != null) {

            fragment = getSupportFragmentManager().getFragment(savedInstanceState, "currentFragment");

        } else {

            fragment = new RecentlyDeletedFragment();
          //  getSupportActionBar().setTitle(R.string.title_activity_recently_deleted);
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container_body, fragment)
                .commit();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        getSupportFragmentManager().putFragment(outState, "currentFragment", fragment);
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