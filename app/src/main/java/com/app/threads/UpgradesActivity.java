package com.app.threads;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.app.threads.common.ActivityBase;
public class UpgradesActivity extends ActivityBase {

    Fragment fragment;
    ImageView ivBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upgrades);

        ivBack = (ImageView) findViewById(R.id.ivBack);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        if (savedInstanceState != null) {

            fragment = getSupportFragmentManager().getFragment(savedInstanceState, "currentFragment");

        } else {

            fragment = new UpgradesFragment();
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        fragment.onActivityResult(requestCode, resultCode, data);
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
