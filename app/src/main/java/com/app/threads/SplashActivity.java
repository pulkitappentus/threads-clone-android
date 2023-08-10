package com.app.threads;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.app.threads.app.App;
import com.app.threads.common.ActivityBase;
import com.app.threads.util.CustomRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SplashActivity extends ActivityBase {

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
        setContentView(R.layout.activity_splash);

    }


    @Override
    protected void  onStart() {
        super.onStart();
        if (App.getInstance().isConnected() && App.getInstance().getId() != 0) {
            //   showLoadingScreen();
            CustomRequest jsonReq = new CustomRequest(Request.Method.POST, METHOD_ACCOUNT_AUTHORIZE, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            if (App.getInstance().authorize(response)) {

                                if (App.getInstance().getState() == ACCOUNT_STATE_ENABLED) {
                                    App.getInstance().updateGeoLocation();
                                    Log.e("gjfhbjk","success");
                                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);

                                } else {
                                    Log.e("gjfhbjk","fail");
                                    startActivity(new Intent(SplashActivity.this, AppActivity.class));
                                    finish();

                                }

                            } else {
                                Log.e("gjfhbjk","fggail");
                                startActivity(new Intent(SplashActivity.this, AppActivity.class));
                                finish();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("gjfhbjk","fgdfdfgail");
                    startActivity(new Intent(SplashActivity.this, AppActivity.class));
                    finish();

                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("clientId", CLIENT_ID);
                    params.put("accountId", Long.toString(App.getInstance().getId()));
                    params.put("accessToken", App.getInstance().getAccessToken());
                    params.put("appType", Integer.toString(APP_TYPE_ANDROID));
                    params.put("fcm_regId", App.getInstance().getGcmToken());

                    return params;
                }
            };
            App.getInstance().addToRequestQueue(jsonReq);

        } else {
            startActivity(new Intent(SplashActivity.this, AppActivity.class));
            finish();
        }
    }
}