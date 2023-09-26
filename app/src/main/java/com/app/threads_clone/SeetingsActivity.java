package com.app.threads_clone;

import static com.app.threads_clone.constants.Constants.CLIENT_ID;
import static com.app.threads_clone.constants.Constants.METHOD_ACCOUNT_LOGOUT;
import static com.app.threads_clone.constants.Constants.METHOD_APP_TERMS;
import static com.app.threads_clone.constants.Constants.VOLLEY_REQUEST_SECONDS;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.app.threads_clone.app.App;
import com.app.threads_clone.util.CustomRequest;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class SeetingsActivity extends AppCompatActivity {

    LinearLayout llPushNotification, llLikes, llPrivacy, llChangePass, llComment, llDeactiveAcc, llTerm, llAck, llLogout, llContactUs;

    private Boolean loading = false;

    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seetings);

        llPushNotification = (LinearLayout) findViewById(R.id.llPushNotification);
        llLikes = (LinearLayout) findViewById(R.id.llYourLike);
        llPrivacy = (LinearLayout) findViewById(R.id.llPrivacy);
        llChangePass = (LinearLayout) findViewById(R.id.llChangePass);
        llComment = (LinearLayout) findViewById(R.id.llAccessPost);
        llDeactiveAcc = (LinearLayout) findViewById(R.id.llDeactiveAccount);
        llTerm = (LinearLayout) findViewById(R.id.llTermUse);
        llAck = (LinearLayout) findViewById(R.id.llAck);
        llLogout = (LinearLayout) findViewById(R.id.llLogout);
        llContactUs = (LinearLayout) findViewById(R.id.llContactUs);

        initpDialog();


        llPushNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(SeetingsActivity.this, NotificationsSettingsActivity.class);
                startActivity(i);
            }
        });


        llLikes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


        llPrivacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(SeetingsActivity.this, PrivacySettingsActivity.class);
                startActivity(i);
            }
        });


        llChangePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(SeetingsActivity.this, ChangePasswordActivity.class);
                startActivity(i);
            }
        });


        llComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


        llDeactiveAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(SeetingsActivity.this, DeactivateActivity.class);
                startActivity(i);
            }
        });


        llTerm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(SeetingsActivity.this, WebViewActivity.class);
                i.putExtra("url", METHOD_APP_TERMS);
                i.putExtra("title", getText(R.string.settings_terms));
                startActivity(i);
            }
        });


        llAck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


        llLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {
                View view = LayoutInflater.from(SeetingsActivity.this).inflate(R.layout.dialog_logout, null);
                BottomSheetDialog dialog = new BottomSheetDialog(SeetingsActivity.this, R.style.AppBottomSheetDialogTheme);
                dialog.setContentView(view);

                TextView  no = (TextView) dialog.findViewById(R.id.tvNo);
                TextView  yes = (TextView) dialog.findViewById(R.id.tvYes);

                no.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.cancel();
                    }
                });

                yes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        loading = true;

                        showpDialog();

                        CustomRequest jsonReq = new CustomRequest(Request.Method.POST, METHOD_ACCOUNT_LOGOUT, null,
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {

                                        try {

                                            if (!response.getBoolean("error")) {

                                                Log.d("Logout", "Logout success");
                                            }

                                        } catch (JSONException e) {

                                            e.printStackTrace();

                                        } finally {

                                            loading = false;

                                            hidepDialog();

                                            App.getInstance().removeData();
                                            App.getInstance().readData();

                                            App.getInstance().setNotificationsCount(0);
                                            App.getInstance().setMessagesCount(0);
                                            App.getInstance().setId(0);
                                            App.getInstance().setUsername("");
                                            App.getInstance().setFullname("");

                                            Intent intent = new Intent(SeetingsActivity.this, AppActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(intent);
                                        }
                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                loading = false;
                                hidepDialog();
                            }
                        }) {

                            @Override
                            protected Map<String, String> getParams() {
                                Map<String, String> params = new HashMap<String, String>();
                                params.put("clientId", CLIENT_ID);
                                params.put("accountId", Long.toString(App.getInstance().getId()));
                                params.put("accessToken", App.getInstance().getAccessToken());

                                return params;
                            }
                        };

                        RetryPolicy policy = new DefaultRetryPolicy((int) TimeUnit.SECONDS.toMillis(VOLLEY_REQUEST_SECONDS), DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

                        jsonReq.setRetryPolicy(policy);

                        App.getInstance().addToRequestQueue(jsonReq);
                    }
                });
                dialog.show();
            }
        });


        llContactUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(SeetingsActivity.this, SupportActivity.class);
                startActivity(i);
            }
        });


    }
    protected void initpDialog() {

        pDialog = new ProgressDialog(SeetingsActivity.this);
        pDialog.setMessage(getString(R.string.msg_loading));
        pDialog.setCancelable(false);
    }

    protected void showpDialog() {

        if (!pDialog.isShowing())
            pDialog.show();
    }

    protected void hidepDialog() {

        if (pDialog.isShowing())
            pDialog.dismiss();
    }

}