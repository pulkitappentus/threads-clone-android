package com.app.threads;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.facebook.FacebookSdk;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import com.app.threads.app.App;
import com.app.threads.common.ActivityBase;

public class AppActivity extends ActivityBase {

    TextView tvLogin,tvCreate;

    protected Location mLastLocation;

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

        setContentView(R.layout.activity_app);
        FacebookSdk.sdkInitialize(getApplicationContext());

        // Get Firebase token

        // Check GPS is enabled
        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER) && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            FusedLocationProviderClient mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            mFusedLocationClient.getLastLocation().addOnCompleteListener(this, task -> {
                if (task.isSuccessful() && task.getResult() != null) {

                    mLastLocation = task.getResult();

                    App.getInstance().setLat(mLastLocation.getLatitude());
                    App.getInstance().setLng(mLastLocation.getLongitude());

                    // Save data

                    App.getInstance().saveData();

                    // Send location data to server

                    App.getInstance().setLocation();

                } else {

                    Log.d("GPS", "AppActivity getLastLocation:exception", task.getException());
                }
            });
        }

        tvLogin = (TextView) findViewById(R.id.tvLogin);
        tvCreate = (TextView) findViewById(R.id.tvCreate);

        tvLogin.setOnClickListener(view -> {

            Intent i = new Intent(AppActivity.this, LoginActivity.class);
            startActivity(i);
        });

        tvCreate.setOnClickListener(view -> {
            Intent i = new Intent(AppActivity.this, RegisterActivity.class);
            startActivity(i);
        });

    }




}
