package com.app.threads;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.app.threads.app.App;
import com.app.threads.constants.Constants;
import com.app.threads.util.CustomRequest;

public class SettingsFragment extends PreferenceFragment implements Constants {
    private SwitchPreference allowComments, allowMessages, allowGalleryComments;
    private ProgressDialog pDialog;

    int mAllowComments, mAllowMessages, mAllowGalleryComments;

    TextView aboutDialogAppVersion;

    private Boolean loading = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        initpDialog();

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.settings);

       Preference pref = findPreference("settings_version");

        pref.setTitle(getString(R.string.app_name) + " v" + getString(R.string.app_version));

        pref = findPreference("settings_logout");

        pref.setSummary(App.getInstance().getUsername());

        Preference recentlyDeletedPreference = findPreference("settings_recently_deleted");

        recentlyDeletedPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

            public boolean onPreferenceClick(Preference arg0) {

                Intent i = new Intent(getActivity(), RecentlyDeletedActivity.class);
                startActivity(i);

                return true;
            }
        });

        Preference balancePreference = findPreference("settings_balance");

        balancePreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

            public boolean onPreferenceClick(Preference arg0) {

                Intent i = new Intent(getActivity(), BalanceActivity.class);
                startActivity(i);

                return true;
            }
        });

        Preference balanceHistoryPreference = findPreference("settings_balance_history");

        balanceHistoryPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference arg0) {

                Intent i = new Intent(getActivity(), BalanceHistoryActivity.class);
                startActivity(i);
                return true;
            }
        });

        Preference logoutPreference = findPreference("settings_logout");
        Preference aboutPreference = findPreference("settings_version");
        Preference changePassword = findPreference("settings_change_password");
        Preference itemDeactivateAccount = findPreference("settings_deactivate_account");
        Preference itemServices = findPreference("settings_services");
        Preference itemTerms = findPreference("settings_terms");
        Preference itemThanks = findPreference("settings_thanks");
        Preference itemBlackList = findPreference("settings_blocked_list");
        Preference itemReferralsList = findPreference("settings_referrals_list");
        Preference itemNotifications = findPreference("settings_push_notifications");
        Preference itemPrivacy = findPreference("settings_privacy");
        Preference itemContactUs = findPreference("settings_contact_us");

        itemContactUs.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference arg0) {

                Intent i = new Intent(getActivity(), SupportActivity.class);
                startActivity(i);

                return true;
            }
        });

        itemPrivacy.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference arg0) {
                Intent i = new Intent(getActivity(), PrivacySettingsActivity.class);
                startActivity(i);

                return true;
            }
        });

        itemNotifications.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference arg0) {

                Intent i = new Intent(getActivity(), NotificationsSettingsActivity.class);
                startActivity(i);

                return true;
            }
        });

        itemBlackList.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference arg0) {
                Intent i = new Intent(getActivity(), BlackListActivity.class);
                startActivity(i);

                return true;
            }
        });

        itemReferralsList.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference arg0) {
                Intent i = new Intent(getActivity(), ReferralsActivity.class);
                startActivity(i);
                return true;
            }
        });

        itemThanks.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

            public boolean onPreferenceClick(Preference arg0) {

                Intent i = new Intent(getActivity(), WebViewActivity.class);
                i.putExtra("url", METHOD_APP_THANKS);
                i.putExtra("title", getText(R.string.settings_thanks));
                startActivity(i);

                return true;
            }
        });

        itemTerms.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference arg0) {

                Intent i = new Intent(getActivity(), WebViewActivity.class);
                i.putExtra("url", METHOD_APP_TERMS);
                i.putExtra("title", getText(R.string.settings_terms));
                startActivity(i);

                return true;
            }
        });

        aboutPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference arg0) {
                View view = LayoutInflater.from(getActivity()).inflate(R.layout.about_dialog, null);
                BottomSheetDialog dialog = new BottomSheetDialog(getActivity(), R.style.AppBottomSheetDialogTheme);
                dialog.setContentView(view);

                aboutDialogAppVersion = (TextView) dialog.findViewById(R.id.aboutDialogAppVersion);
                Button btnOk = (Button) dialog.findViewById(R.id.btnOk);

                aboutDialogAppVersion.setText("Version " + getString(R.string.app_version));

                btnOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.cancel();
                    }
                });


                dialog.show();

                return false;
            }
        });

        logoutPreference.setSummary(App.getInstance().getUsername());

        logoutPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference arg0) {
                View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_logout, null);
                BottomSheetDialog dialog = new BottomSheetDialog(getActivity(), R.style.AppBottomSheetDialogTheme);
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

                                           Intent intent = new Intent(getActivity(), AppActivity.class);
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
               return true;
            }
        });

        changePassword.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

            @Override
            public boolean onPreferenceClick(Preference preference) {

                Intent i = new Intent(getActivity(), ChangePasswordActivity.class);
                startActivity(i);

                return true;
            }
        });

        itemDeactivateAccount.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

            @Override
            public boolean onPreferenceClick(Preference preference) {

                Intent i = new Intent(getActivity(), DeactivateActivity.class);
                startActivity(i);

                return true;
            }
        });

        itemServices.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

            @Override
            public boolean onPreferenceClick(Preference preference) {

                Intent i = new Intent(getActivity(), ServicesActivity.class);
                startActivity(i);

                return true;
            }
        });

        if (!FACEBOOK_AUTHORIZATION) {

            PreferenceCategory headerGeneral = (PreferenceCategory) findPreference("header_general");

            headerGeneral.removePreference(itemServices);
        }

        allowComments = (SwitchPreference) getPreferenceManager().findPreference("allowComments");

        allowComments.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {

                if (newValue instanceof Boolean) {

                    Boolean value = (Boolean) newValue;

                    if (value) {

                        mAllowComments = 1;

                    } else {

                        mAllowComments = 0;
                    }

                    if (App.getInstance().isConnected()) {

                        setAllowComments();

                    } else {

                        Toast.makeText(getActivity().getApplicationContext(), getText(R.string.msg_network_error), Toast.LENGTH_SHORT).show();
                    }
                }

                return true;
            }
        });

        allowGalleryComments = (SwitchPreference) getPreferenceManager().findPreference("allowGalleryComments");

        allowGalleryComments.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {

                if (newValue instanceof Boolean) {

                    Boolean value = (Boolean) newValue;

                    if (value) {

                        mAllowGalleryComments = 1;

                    } else {

                        mAllowGalleryComments = 0;
                    }

                    if (App.getInstance().isConnected()) {

                        setAllowGalleryComments();

                    } else {

                        Toast.makeText(getActivity().getApplicationContext(), getText(R.string.msg_network_error), Toast.LENGTH_SHORT).show();
                    }
                }

                return true;
            }
        });

        allowMessages = (SwitchPreference) getPreferenceManager().findPreference("allowMessages");

        allowMessages.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {

                if (newValue instanceof Boolean) {

                    Boolean value = (Boolean) newValue;

                    if (value) {

                        mAllowMessages = 1;

                    } else {

                        mAllowMessages = 0;
                    }

                    if (App.getInstance().isConnected()) {

                        setAllowMessages();

                    } else {

                        Toast.makeText(getActivity().getApplicationContext(), getText(R.string.msg_network_error), Toast.LENGTH_SHORT).show();
                    }
                }

                return true;
            }
        });

        checkAllowComments(App.getInstance().getAllowComments());
        checkAllowGalleryComments(App.getInstance().getAllowGalleryComments());
        checkAllowMessages(App.getInstance().getAllowMessages());
    }

    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {

            loading = savedInstanceState.getBoolean("loading");

        } else {

            loading = false;
        }

        if (loading) {

            showpDialog();
        }
    }

    public void onDestroyView() {

        super.onDestroyView();

        hidepDialog();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);

        outState.putBoolean("loading", loading);
    }

    public void checkAllowComments(int value) {

        if (value == 1) {

            allowComments.setChecked(true);
            mAllowComments = 1;

        } else {

            allowComments.setChecked(false);
            mAllowComments = 0;
        }
    }

    public void checkAllowGalleryComments(int value) {

        if (value == 1) {

            allowGalleryComments.setChecked(true);
            mAllowGalleryComments = 1;

        } else {

            allowGalleryComments.setChecked(false);
            mAllowGalleryComments = 0;
        }
    }

    public void checkAllowMessages(int value) {

        if (value == 1) {

            allowMessages.setChecked(true);
            mAllowMessages = 1;

        } else {

            allowMessages.setChecked(false);
            mAllowMessages = 0;
        }
    }

    public void setAllowComments() {

        loading = true;

        showpDialog();

        CustomRequest jsonReq = new CustomRequest(Request.Method.POST, METHOD_ACCOUNT_SET_ALLOW_COMMENTS, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {

                            if (!response.getBoolean("error")) {

                                App.getInstance().setAllowComments(response.getInt("allowComments"));

                                checkAllowComments(App.getInstance().getAllowComments());
                            }

                        } catch (JSONException e) {

                            e.printStackTrace();

                        } finally {

                            loading = false;

                            hidepDialog();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                loading = false;

                hidepDialog();

                Toast.makeText(getActivity().getApplicationContext(), getText(R.string.error_data_loading), Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("clientId", CLIENT_ID);
                params.put("accountId", Long.toString(App.getInstance().getId()));
                params.put("accessToken", App.getInstance().getAccessToken());
                params.put("allowComments", Integer.toString(mAllowComments));

                return params;
            }
        };

        App.getInstance().addToRequestQueue(jsonReq);
    }

    public void setAllowGalleryComments() {

        loading = true;

        showpDialog();

        CustomRequest jsonReq = new CustomRequest(Request.Method.POST, METHOD_ACCOUNT_SET_ALLOW_GALLERY_COMMENTS, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {

                            if (!response.getBoolean("error")) {

                                App.getInstance().setAllowGalleryComments(response.getInt("allowGalleryComments"));

                                checkAllowGalleryComments(App.getInstance().getAllowGalleryComments());
                            }

                        } catch (JSONException e) {

                            e.printStackTrace();

                        } finally {

                            loading = false;

                            hidepDialog();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                loading = false;

                hidepDialog();

                Toast.makeText(getActivity().getApplicationContext(), getText(R.string.error_data_loading), Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("clientId", CLIENT_ID);
                params.put("accountId", Long.toString(App.getInstance().getId()));
                params.put("accessToken", App.getInstance().getAccessToken());
                params.put("allowGalleryComments", Integer.toString(mAllowGalleryComments));

                return params;
            }
        };

        App.getInstance().addToRequestQueue(jsonReq);
    }

    public void setAllowMessages() {

        loading = true;

        showpDialog();

        CustomRequest jsonReq = new CustomRequest(Request.Method.POST, METHOD_ACCOUNT_SET_ALLOW_MESSAGES, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {

                            if (!response.getBoolean("error")) {

                                App.getInstance().setAllowMessages(response.getInt("allowMessages"));

                                checkAllowMessages(App.getInstance().getAllowMessages());
                            }

                        } catch (JSONException e) {

                            e.printStackTrace();

                        } finally {

                            loading = false;

                            hidepDialog();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                loading = false;

                hidepDialog();

                Toast.makeText(getActivity().getApplicationContext(), getText(R.string.error_data_loading), Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("clientId", CLIENT_ID);
                params.put("accountId", Long.toString(App.getInstance().getId()));
                params.put("accessToken", App.getInstance().getAccessToken());
                params.put("allowMessages", Integer.toString(mAllowMessages));

                return params;
            }
        };

        App.getInstance().addToRequestQueue(jsonReq);
    }

    protected void initpDialog() {

        pDialog = new ProgressDialog(getActivity());
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