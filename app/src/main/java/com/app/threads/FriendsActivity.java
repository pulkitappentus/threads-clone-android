package com.app.threads;


import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.threads.common.ActivityBase;

public class FriendsActivity extends ActivityBase {
     Fragment fragment;
    Boolean restore = false;
    ImageView ivBack;
    private Boolean gcm = false;

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

        setContentView(R.layout.activity_friends);
        TextView heading= findViewById(R.id.tvHeading);
        ImageView ivBack= findViewById(R.id.ivBack);
        heading.setText("Friends");

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        if (savedInstanceState != null) {
            fragment = getSupportFragmentManager().getFragment(savedInstanceState, "currentFragment");
            restore = savedInstanceState.getBoolean("restore");
            gcm = savedInstanceState.getBoolean("gcm");

        } else {

            fragment = new FriendsFragment();
            restore = false;
            gcm = false;
        }

        Intent i = getIntent();

        gcm = i.getBooleanExtra("gcm", false);

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container_body, fragment)
                .commit();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);

        outState.putBoolean("restore", true);
        outState.putBoolean("gcm", gcm);
        getSupportFragmentManager().putFragment(outState, "currentFragment", fragment);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {

            case android.R.id.home: {

                getMainActivity();

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

        getMainActivity();
    }

    public void getMainActivity() {

        if (gcm) {

            ActivityCompat.finishAffinity(FriendsActivity.this);

            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(i);

        } else {

            finish();
        }
    }
}