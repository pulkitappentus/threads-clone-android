package com.app.threads;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import androidx.annotation.NonNull;

import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Protocol;
import com.squareup.okhttp.RequestBody;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.app.threads.app.App;
import com.app.threads.common.ActivityBase;
import com.app.threads.util.CustomRequest;
import com.app.threads.util.Helper;

public class RegisterActivity extends ActivityBase {

    public static final int SELECT_PHOTO_IMG = 20;
    public static final int CREATE_PHOTO_IMG = 21;

    private Location mLastLocation;
    LinearLayout llLogin;
    private String selectedPhotoImg = "", newPhotoImageFileName = "";

    private Uri outputFileUri;

    private ViewPager mViewPager;
    private int[] screens;
    private Button mButtonBack;
    Button male,female,other;
    ImageView ivBack;
    CheckBox cbHidePassword;

    private RelativeLayout mNavigator;
    ImageView ivCheck;

    private EditText mUsername, mFullname, mPassword, mEmail, birthDate;

    // Screen 1ivImageChoose
    private ImageView ChoosePhoto;
    private CircularImageView photo_image;

    // Screen 2
    private Button mButtonChooseAge;
    private Button mButtonChooseGender;


    // Screen 2, 3
    private ImageView mImage;

    // Screen 3
    private Button mButtonGrantLocationPermission;


    private int age = 0, gender = 0; // gender: 0 - unknown; 1 = male; 2 = female
    private String username = "";
    private String password = "";
    private String email = "";
    private final String language = "en";
    private String fullname = "";
    private final String photo_url = "";
    private String referrer = "";
    private String facebook_id = "";

    CallbackManager callbackManager;

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

        if (AccessToken.getCurrentAccessToken()!= null) LoginManager.getInstance().logOut();
        callbackManager = CallbackManager.Factory.create();

        Intent i = getIntent();
        facebook_id = i.getStringExtra("facebookId");

        if (facebook_id == null) {
            facebook_id = "";
        }
        setContentView(R.layout.activity_register);

        if (savedInstanceState != null) {
            age = savedInstanceState.getInt("age");
            gender = savedInstanceState.getInt("gender");
            username = savedInstanceState.getString("username");
            password = savedInstanceState.getString("password");
            email = savedInstanceState.getString("email");
            fullname = savedInstanceState.getString("fullname");
            referrer = savedInstanceState.getString("referrer");
            facebook_id = savedInstanceState.getString("facebook_id");
            selectedPhotoImg = savedInstanceState.getString("selectedPhotoImg");
        }


        mViewPager = (ViewPager) findViewById(R.id.view_pager);

        screens = new int[]{
                R.layout.register_screen_1,
                R.layout.register_screen_2,
        };

        //addMarkers(0);
        MyViewPagerAdapter myViewPagerAdapter = new MyViewPagerAdapter();
        mViewPager.setAdapter(myViewPagerAdapter);
        mViewPager.addOnPageChangeListener(viewPagerPageChangeListener);

        mViewPager.beginFakeDrag();


    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);

        outState.putInt("age", age);
        outState.putInt("gender", gender);

        outState.putString("username", username);
        outState.putString("password", password);
        outState.putString("email", email);
        outState.putString("fullname", fullname);
        outState.putString("referrer", referrer);
        outState.putString("facebook_id", facebook_id);
        outState.putString("selectedPhotoImg", selectedPhotoImg);
    }

    private void updateView() {

        int current = mViewPager.getCurrentItem();

        setStatusBarColor(this, current);

        switch (current) {

            case 0: {
                if (username.length() != 0) {

                    mUsername.setText(username);
                }

                if (fullname.length() != 0) {

                    mFullname.setText(fullname);
                }

                if (password.length() != 0) {

                    mPassword.setText(password);
                }

                if (email.length() != 0) {
                    mEmail.setText(email);
                }
                break;
            }


            default: {
                break;
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        callbackManager.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SELECT_PHOTO_IMG && resultCode == RESULT_OK && null != data) {

            Uri selectedImage = data.getData();

            newPhotoImageFileName = Helper.randomString(6) + ".jpg";

            Helper helper = new Helper(getApplicationContext());
            helper.saveImg(selectedImage, newPhotoImageFileName);

            selectedPhotoImg = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + File.separator + newPhotoImageFileName;

            photo_image.setImageURI(null);
            photo_image.setVisibility(View.VISIBLE);
            ChoosePhoto.setVisibility(View.GONE);
            photo_image.setImageURI(FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", new File(selectedPhotoImg)));

        } else if (requestCode == CREATE_PHOTO_IMG && resultCode == RESULT_OK) {

            try {

                newPhotoImageFileName = "photo.jpg";
                selectedPhotoImg = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + File.separator + "photo.jpg";
                photo_image.setVisibility(View.VISIBLE);
                ChoosePhoto.setVisibility(View.GONE);
                photo_image.setImageURI(null);
                photo_image.setImageURI(FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", new File(selectedPhotoImg)));

//                mContainerImg.setVisibility(View.VISIBLE);

            } catch (Exception ex) {

                photo_image.setImageURI(null);
                photo_image.setImageResource(R.drawable.profile_default_photo);
                selectedPhotoImg = "";

                Log.v("OnCameraCallBack", ex.getMessage());
            }

        } else if (requestCode == 10001) {

            if (mViewPager.getCurrentItem() == 4) {

                updateView();
            }
        }
    }

    @Override
    public void onResume() {

        super.onResume();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {

            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE_PHOTO: {

                // If request is cancelled, the result arrays are empty.

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // Granted

                    choiceImage();

                } else if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {

                    if (!ActivityCompat.shouldShowRequestPermissionRationale(RegisterActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                        // Denied
                        showNoStoragePermissionSnackbar();
                    }
                }
                return;
            }

            case MY_PERMISSIONS_REQUEST_ACCESS_LOCATION: {

                // If request is cancelled, the result arrays are empty.

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // Granted

                    LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);

                    if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER) && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                        FusedLocationProviderClient mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

                        mFusedLocationClient.getLastLocation().addOnCompleteListener(this, new OnCompleteListener<Location>() {
                            @Override
                            public void onComplete(@NonNull Task<Location> task) {

                                if (task.isSuccessful() && task.getResult() != null) {

                                    mLastLocation = task.getResult();

                                    App.getInstance().setLat(mLastLocation.getLatitude());
                                    App.getInstance().setLng(mLastLocation.getLongitude());

                                    App.getInstance().saveData();

                                } else {
                                    Log.d("GPS", "getLastLocation:exception", task.getException());
                                }
                            }
                        });
                    }

                    animateIcon(mImage);

                    mButtonGrantLocationPermission.setEnabled(false);
                    mButtonGrantLocationPermission.setText(R.string.action_grant_access_success);

                } else if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {

                    if (!ActivityCompat.shouldShowRequestPermissionRationale(RegisterActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                        // Denied

                        showNoLocationPermissionSnackbar();

                        mButtonGrantLocationPermission.setEnabled(true);
                        mButtonGrantLocationPermission.setText(R.string.action_grant_access);
                    }
                }

                return;
            }

        }
    }

    public void openApplicationSettings() {

        Intent appSettingsIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + this.getPackageName()));
        startActivityForResult(appSettingsIntent, 10001);
    }

    public void showNoStoragePermissionSnackbar() {

        Snackbar.make(findViewById(android.R.id.content), getString(R.string.label_no_storage_permission) , Snackbar.LENGTH_LONG).setAction(getString(R.string.action_settings), new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                openApplicationSettings();

                Toast.makeText(RegisterActivity.this, getString(R.string.label_grant_storage_permission), Toast.LENGTH_SHORT).show();
            }

        }).show();
    }

    public void showNoLocationPermissionSnackbar() {

        Snackbar.make(findViewById(android.R.id.content), getString(R.string.label_no_location_permission) , Snackbar.LENGTH_LONG).setAction(getString(R.string.action_settings), new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                openApplicationSettings();

                Toast.makeText(RegisterActivity.this, getString(R.string.label_grant_location_permission), Toast.LENGTH_SHORT).show();
            }

        }).show();
    }

    public int getColorWrapper(Context context, int id) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            return context.getColor(id);

        } else {

            //noinspection deprecation
            return context.getResources().getColor(id);
        }
    }

    public void setStatusBarColor(Activity act, int index) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            Window window = act.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

            switch (index) {

                case 0: {
                    window.setStatusBarColor(getColorWrapper(act, R.color.statusBarColor));
                    break;
                }

                case 1: {

                    window.setStatusBarColor(getColorWrapper(act, R.color.register_screen_2));

                    break;
                }

                case 2: {

                    window.setStatusBarColor(getColorWrapper(act, R.color.register_screen_3));

                    break;
                }

                case 3: {

                    window.setStatusBarColor(getColorWrapper(act, R.color.register_screen_4));

                    break;
                }

                default: {

                    window.setStatusBarColor(Color.TRANSPARENT);

                    break;
                }
            }
        }
    }

    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {

            if (position == screens.length - 1) {

            } else {
            }

            switch (position) {

                case 0: {

                    setStatusBarColor(RegisterActivity.this, 0);

                    break;
                }
                case 1: {

                    setStatusBarColor(RegisterActivity.this, 1);

                    break;
                }

                case 2: {

                    setStatusBarColor(RegisterActivity.this, 2);

                    break;
                }

                case 3: {

                    setStatusBarColor(RegisterActivity.this, 3);

                    break;
                }

                default: {

                    break;
                }
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };


    public class MyViewPagerAdapter extends PagerAdapter {

        public MyViewPagerAdapter() {

        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View view = layoutInflater.inflate(screens[position], container, false);
            container.addView(view);

            switch (position) {

                case 0: {

                    mUsername = (EditText) view.findViewById(R.id.etUserName);
                    mFullname = (EditText) view.findViewById(R.id.etFullName);
                    mPassword = (EditText) view.findViewById(R.id.etPassword);
                    mEmail = (EditText) view.findViewById(R.id.etEmailAddress);
                    ivCheck = (ImageView) view.findViewById(R.id.ivCheck);

                    llLogin = (LinearLayout) view.findViewById(R.id.llLogin);
                     ivBack= findViewById(R.id.ivBack);
                    ivBack.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            finish();
                        }
                    });

                    llLogin.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent i = new Intent(RegisterActivity.this, LoginActivity.class);
                            startActivity(i);
                        }
                    });

                    cbHidePassword= findViewById(R.id.cbHidePassword);
                    cbHidePassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if(!isChecked){
                                mPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());

                            } else{
                                mPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                            }
                        }
                    });

                    LoginButton mFacebookAuth = (LoginButton) view.findViewById(R.id.button_facebook_login);
                    mFacebookAuth.setPermissions("public_profile"); // "email",

                    // Registering CallbackManager with the LoginButton
                    mFacebookAuth.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

                        @Override
                        public void onSuccess(LoginResult loginResult) {

                            // Retrieving access token using the LoginResult
                            AccessToken accessToken = loginResult.getAccessToken();

                            useLoginInformation(accessToken);
                        }

                        @Override
                        public void onCancel() {

                        }
                        @Override
                        public void onError(FacebookException error) {

                        }
                    });

                    Button mButtonContinue = (Button) view.findViewById(R.id.btnSignUp);
                    mButtonContinue.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            hideKeyboard();

                            next();
                        }
                    });

                    mUsername.addTextChangedListener(new TextWatcher() {

                        public void afterTextChanged(Editable s) {

                            if (App.getInstance().isConnected() && check_username()) {

                                CustomRequest jsonReq = new CustomRequest(Request.Method.POST, METHOD_APP_CHECKUSERNAME, null,
                                        new Response.Listener<JSONObject>() {
                                            @Override
                                            public void onResponse(JSONObject response) {

                                                try {

                                                    if (response.getBoolean("error")) {

                                                        mUsername.setError(getString(R.string.error_login_taken));
                                                    }

                                                } catch (JSONException e) {

                                                    e.printStackTrace();

                                                }
                                            }
                                        }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {

                                        Log.e("Username()", error.toString());

                                    }
                                }) {

                                    @Override
                                    protected Map<String, String> getParams() {
                                        Map<String, String> params = new HashMap<String, String>();
                                        params.put("username", username);

                                        return params;
                                    }
                                };

                                App.getInstance().addToRequestQueue(jsonReq);
                            }
                        }

                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        }

                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                        }
                    });

                    mFullname.addTextChangedListener(new TextWatcher() {

                        public void afterTextChanged(Editable s) {

                            check_fullname();
                        }

                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                        public void onTextChanged(CharSequence s, int start, int before, int count) {}
                    });

                    mPassword.addTextChangedListener(new TextWatcher() {

                        public void afterTextChanged(Editable s) {

                            check_password();
                        }

                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                        public void onTextChanged(CharSequence s, int start, int before, int count) {}
                    });

                    mEmail.addTextChangedListener(new TextWatcher() {

                        public void afterTextChanged(Editable s) {

                            if (App.getInstance().isConnected() && check_email()) {

                                CustomRequest jsonReq = new CustomRequest(Request.Method.POST, METHOD_APP_CHECK_EMAIL, null,
                                        new Response.Listener<JSONObject>() {
                                            @Override
                                            public void onResponse(JSONObject response) {

                                                try {

                                                    if (response.getBoolean("error")) {

                                                        mEmail.setError(getString(R.string.error_email_taken));
                                                    }

                                                } catch (JSONException e) {

                                                    e.printStackTrace();

                                                }
                                            }
                                        }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {

                                        Log.e("Email()", error.toString());

                                    }
                                }) {

                                    @Override
                                    protected Map<String, String> getParams() {
                                        Map<String, String> params = new HashMap<String, String>();
                                        params.put("email", email);

                                        return params;
                                    }
                                };

                                App.getInstance().addToRequestQueue(jsonReq);
                            }
                        }

                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        }

                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                        }
                    });

                    break;
                }

                case 1: {
                    Button mButtonFinish = findViewById(R.id.btnFinish);
                    birthDate=findViewById(R.id.etDateBirth);

                    ivBack= findViewById(R.id.ivBack);
                    ivBack.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            finish();
                        }
                    });

                    if (selectedPhotoImg != null && selectedPhotoImg.length() > 0) {
                        photo_image.setVisibility(View.VISIBLE);
                        ChoosePhoto.setVisibility(View.GONE);
                        photo_image.setImageURI(FileProvider.getUriForFile(RegisterActivity.this, BuildConfig.APPLICATION_ID + ".provider", new File(selectedPhotoImg)));
                    }

                    ChoosePhoto = (ImageView) view.findViewById(R.id.ivImageChoose);
                    photo_image = (CircularImageView) view.findViewById(R.id.photo_image);
                    ChoosePhoto.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            selectPhoto();
                        }
                    });

                    birthDate.addTextChangedListener(new TextWatcher() {

                        @Override
                        public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {

                        }

                        @Override
                        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                          //  Toast.makeText(getApplicationContext(),"before text change",Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void afterTextChanged(Editable arg0) {
                            SimpleDateFormat dateFormat= new SimpleDateFormat("dd/MM/yyyy");
                            try {
                                Date d=dateFormat.parse(birthDate.getText().toString());
                                age= Integer.parseInt(getYear(d));
                                Log.e("hkfghkfg",getYear(d));
                            }
                            catch(Exception e) {
                                System.out.println("Excep"+e);
                            }
                        }
                    });

                    male=findViewById(R.id.btnMale);
                    female=findViewById(R.id.btnFemale);
                    other=findViewById(R.id.btnOther);
                    male.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            gender=1;
                            male.setTextColor(getResources().getColor(R.color.white));
                            female.setTextColor(getResources().getColor(R.color.black));
                            other.setTextColor(getResources().getColor(R.color.black));

                            male.setBackgroundResource(R.drawable.bg_button_selected);
                            female.setBackgroundResource(R.drawable.bg_grey);
                            other.setBackgroundResource(R.drawable.bg_grey);

                        }
                    });

                    female.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            gender=2;
                            female.setTextColor(getResources().getColor(R.color.white));
                            male.setTextColor(getResources().getColor(R.color.black));
                            other.setTextColor(getResources().getColor(R.color.black));

                            male.setBackgroundResource(R.drawable.bg_grey);
                            female.setBackgroundResource(R.drawable.bg_button_selected);
                            other.setBackgroundResource(R.drawable.bg_grey);
                        }
                    });

                    other.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            gender=0;
                            other.setTextColor(getResources().getColor(R.color.white));
                            male.setTextColor(getResources().getColor(R.color.black));
                            female.setTextColor(getResources().getColor(R.color.black));

                            other.setBackgroundResource(R.drawable.bg_button_selected);
                            female.setBackgroundResource(R.drawable.bg_grey);
                            male.setBackgroundResource(R.drawable.bg_grey);

                        }
                    });

                    mButtonFinish.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {

                            int current = mViewPager.getCurrentItem();

                            if (current < screens.length - 1) {

                                switch (current) {

                                    case 1: {

                                        if (selectedPhotoImg.length() != 0) {

                                         //   mViewPager.setCurrentItem(current + 1);

                                        } else {

                                            Toast.makeText(RegisterActivity.this, getString(R.string.register_screen_2_msg), Toast.LENGTH_SHORT).show();
                                            animateIcon(photo_image);
                                        }

                                        break;
                                    }

                                    case 2: {
                                        if (gender > 0 && age > 17) {
                                            mViewPager.setCurrentItem(current + 1);

                                        } else {
                                            Toast.makeText(RegisterActivity.this, getString(R.string.register_screen_3_msg), Toast.LENGTH_SHORT).show();
                                            animateIcon(mImage);
                                        }

                                        break;
                                    }

                                    default: {
                                        mViewPager.setCurrentItem(current + 1);

                                        break;
                                    }
                                }

                                updateView();

                            } else {
                                register();
                            }
                        }
                    });

                    break;
                }

            }

            updateView();

            return view;
        }

        @Override
        public int getCount() {

            return screens.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {

            View view = (View) object;
            container.removeView(view);
        }
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
    public void onBackPressed(){

        finish();
    }

    private void useLoginInformation(AccessToken accessToken) {

        /**
         Creating the GraphRequest to fetch user details
         1st Param - AccessToken
         2nd Param - Callback (which will be invoked once the request is successful)
         **/

        showpDialog();

        GraphRequest request = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {

            //OnCompleted is invoked once the GraphRequest is successful
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {

                try {

                    if (object.has("id")) {

                        facebook_id = object.getString("id");
                    }

                    if (object.has("name")) {

                        fullname = object.getString("name");
                    }

                    if (object.has("email")) {

                        email = object.getString("email");
                    }

                } catch (JSONException e) {

                    Log.e("Facebook Login", "Could not parse malformed JSON: \"" + object.toString() + "\"");

                } finally {

                    if (AccessToken.getCurrentAccessToken() != null) LoginManager.getInstance().logOut();

                    if (!facebook_id.equals("")) {

                        signinByFacebookId();

                    } else {

                        hidepDialog();
                    }
                }
            }
        });

        // We set parameters to the GraphRequest using a Bundle.
        Bundle parameters = new Bundle();
        // parameters.putString("fields", "id,name,email,picture.width(200)");
        parameters.putString("fields", "id, name");
        request.setParameters(parameters);
        // Initiate the GraphRequest
        request.executeAsync();
    }

    public void signinByFacebookId() {
        CustomRequest jsonReq = new CustomRequest(Request.Method.POST, METHOD_ACCOUNT_LOGINBYFACEBOOK, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        if (App.getInstance().authorize(response)) {

                            if (App.getInstance().getState() == ACCOUNT_STATE_ENABLED) {

                                go();

                            } else if (App.getInstance().getState() == ACCOUNT_STATE_BLOCKED) {

                                Toast.makeText(RegisterActivity.this, getText(R.string.msg_account_blocked), Toast.LENGTH_SHORT).show();

                            } else if (App.getInstance().getState() == ACCOUNT_STATE_DEACTIVATED) {

                                Toast.makeText(RegisterActivity.this, getText(R.string.msg_account_deactivated), Toast.LENGTH_SHORT).show();
                            }

                        } else {

                            if (!facebook_id.equals("")) {
                                updateView();
                            }
                        }

                        hidepDialog();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.e("Facebook Login", "Error");

                hidepDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("facebookId", facebook_id);
                params.put("clientId", CLIENT_ID);
                params.put("appType", Integer.toString(APP_TYPE_ANDROID));
                params.put("fcm_regId", App.getInstance().getGcmToken());
                params.put("lang", "en");

                return params;
            }
        };

        RetryPolicy policy = new DefaultRetryPolicy((int) TimeUnit.SECONDS.toMillis(VOLLEY_REQUEST_SECONDS), DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

        jsonReq.setRetryPolicy(policy);

        App.getInstance().addToRequestQueue(jsonReq);
    }

    private void animateIcon(ImageView icon) {
        ScaleAnimation scale = new ScaleAnimation(1.0f, 0.8f, 1.0f, 0.8f,
                ScaleAnimation.RELATIVE_TO_SELF, 0.5f,
                ScaleAnimation.RELATIVE_TO_SELF, 0.5f);
        scale.setDuration(175);
        scale.setInterpolator(new LinearInterpolator());

        icon.startAnimation(scale);
    }

    private void selectPhoto() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE_PHOTO);

            } else {

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE_PHOTO);
            }

        } else {

            choiceImage();
        }
    }

    private void choiceImage() {
        View  view = LayoutInflater.from(this).inflate(R.layout.dialog_add_file, null);
        BottomSheetDialog dialog = new BottomSheetDialog(this, R.style.AppBottomSheetDialogTheme);
        dialog.setContentView(view);
        ImageView ivCamera=dialog.findViewById(R.id.ivCamera);
        ivCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                File sdImageMainDirectory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "photo.jpg");
                outputFileUri = FileProvider.getUriForFile(RegisterActivity.this, BuildConfig.APPLICATION_ID + ".provider", sdImageMainDirectory);

                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
                cameraIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivityForResult(cameraIntent, CREATE_PHOTO_IMG);
                dialog.cancel();
            }
        });

        ImageView ivgallery=dialog.findViewById(R.id.ivGallery);
        ivgallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(Intent.createChooser(intent, getText(R.string.label_select_img)), SELECT_PHOTO_IMG);
                dialog.cancel();

            }
        });
        dialog.show();
    }


    private void choiceAge() {

        AlertDialog.Builder builderSingle = new AlertDialog.Builder(this);

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);

        for (int i = 18; i < 101; i++) {

            arrayAdapter.add(Integer.toString(i));
        }

        builderSingle.setTitle(getText(R.string.register_screen_3_title));


        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                age = which + 18;

                updateView();
            }
        });

        builderSingle.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
            }
        });

        AlertDialog d = builderSingle.create();
        d.show();
    }

    private void choiceGender() {

        AlertDialog.Builder builderSingle = new AlertDialog.Builder(this);

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);

        arrayAdapter.add(getString(R.string.label_male));
        arrayAdapter.add(getString(R.string.label_female));

        builderSingle.setTitle(getText(R.string.action_choose_gender));


        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                gender = which + 1;

                updateView();
            }
        });

        builderSingle.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
            }
        });

        AlertDialog d = builderSingle.create();
        d.show();
    }

    private void grantLocationPermission() {

        if (ContextCompat.checkSelfPermission(RegisterActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(RegisterActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(RegisterActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) || ActivityCompat.shouldShowRequestPermissionRationale(RegisterActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)){

                ActivityCompat.requestPermissions(RegisterActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_ACCESS_LOCATION);
            } else {
                ActivityCompat.requestPermissions(RegisterActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_ACCESS_LOCATION);
            }
        }
    }

    private void next() {
        if (verifyRegForm()) {

            mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1);

            updateView();
        }
    }

    private void go() {

        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("signup", true);
        intent.putExtra("pageId", 2); // 2 = profile page
        startActivity(intent);
    }

    public Boolean verifyRegForm() {

        username = mUsername.getText().toString();
        fullname = mFullname.getText().toString();
        password = mPassword.getText().toString();
        email = mEmail.getText().toString();
        //   referrer = mReferrer.getText().toString();

        mUsername.setError(null);
        mFullname.setError(null);
        mPassword.setError(null);
        mEmail.setError(null);

        Helper helper = new Helper();

        if (username.length() == 0) {

            mUsername.setError(getString(R.string.error_field_empty),null);

            return false;
        }

        if (username.length() < 5) {

            mUsername.setError(getString(R.string.error_small_username),null);

            return false;
        }

        if (!helper.isValidLogin(username)) {

            mUsername.setError(getString(R.string.error_wrong_format),null);

            return false;
        }

        if (fullname.length() == 0) {

            mFullname.setError(getString(R.string.error_field_empty));

            return false;
        }

        if (fullname.length() < 2) {

            mFullname.setError(getString(R.string.error_small_fullname));

            return false;
        }

        if (password.length() == 0) {

            mPassword.setError(getString(R.string.error_field_empty));

            return false;
        }

        if (password.length() < 6) {

            mPassword.setError(getString(R.string.error_small_password));

            return false;
        }

        if (!helper.isValidPassword(password)) {

            mPassword.setError(getString(R.string.error_wrong_format));

            return false;
        }

        if (email.length() == 0) {

            mEmail.setError(getString(R.string.error_field_empty));

            return false;
        }

        if (!helper.isValidEmail(email)) {

            mEmail.setError(getString(R.string.error_wrong_format));
            return false;
        }

        return true;
    }

    public Boolean check_username() {

        username = mUsername.getText().toString();

        Helper helper = new Helper();

        if (username.length() == 0) {

            mUsername.setError(getString(R.string.error_field_empty));
            ivCheck.setImageDrawable(getResources().getDrawable(R.drawable.ic_wrong_field));
            return false;
        }

        if (username.length() < 5) {

            // mUsername.setError(getString(R.string.error_small_username));
            ivCheck.setImageDrawable(getResources().getDrawable(R.drawable.ic_wrong_field));
            return false;
        }

        if (!helper.isValidLogin(username)) {

            //  mUsername.setError(getString(R.string.error_wrong_format));
            ivCheck.setImageDrawable(getResources().getDrawable(R.drawable.ic_wrong_field));

            return false;
        }

        mUsername.setError(null);
        ivCheck.setImageDrawable(getResources().getDrawable(R.drawable.ic_right_field));
        return  true;
    }

    public Boolean check_fullname() {

        fullname = mFullname.getText().toString();

        if (fullname.length() == 0) {

            mFullname.setError(getString(R.string.error_field_empty));

            return false;
        }

        if (fullname.length() < 2) {

            mFullname.setError(getString(R.string.error_small_fullname));

            return false;
        }

        mFullname.setError(null);

        return  true;
    }

    public Boolean check_password() {

        password = mPassword.getText().toString();

        Helper helper = new Helper();

        if (password.length() == 0) {

            mPassword.setError(getString(R.string.error_field_empty));

            return false;
        }

        if (password.length() < 6) {

            mPassword.setError(getString(R.string.error_small_password));

            return false;
        }

        if (!helper.isValidPassword(password)) {

            mPassword.setError(getString(R.string.error_wrong_format));

            return false;
        }

        mPassword.setError(null);

        return true;
    }

    public Boolean check_email() {

        email = mEmail.getText().toString();

        Helper helper = new Helper();

        if (email.length() == 0) {

            mEmail.setError(getString(R.string.error_field_empty));

            return false;
        }

        if (!helper.isValidEmail(email)) {

            mEmail.setError(getString(R.string.error_wrong_format));
            return false;
        }

        mEmail.setError(null);
        return true;
    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {

            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void register() {
        showpDialog();

        CustomRequest jsonReq = new CustomRequest(Request.Method.POST, METHOD_ACCOUNT_SIGNUP, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        Log.e("Profile", "Malformed JSON: \"" + response.toString() + "\"");

                        if (App.getInstance().authorize(response)) {

                            Log.e("Profile", "Malformed JSON: \"" + response.toString() + "\"");

                            // Upload profile photo

                            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), newPhotoImageFileName);

                            final OkHttpClient client = new OkHttpClient();

                            client.setProtocols(Arrays.asList(Protocol.HTTP_1_1));

                            try {

                                RequestBody requestBody = new MultipartBuilder()
                                        .type(MultipartBuilder.FORM)
                                        .addFormDataPart("uploaded_file", file.getName(), RequestBody.create(MediaType.parse("text/csv"), file))
                                        .addFormDataPart("accountId", Long.toString(App.getInstance().getId()))
                                        .addFormDataPart("accessToken", App.getInstance().getAccessToken())
                                        .addFormDataPart("imgType", Integer.toString(UPLOAD_TYPE_PHOTO))
                                        .build();

                                com.squareup.okhttp.Request request = new com.squareup.okhttp.Request.Builder()
                                        .url(METHOD_PROFILE_UPLOAD_IMAGE)
                                        .addHeader("Accept", "application/json;")
                                        .post(requestBody)
                                        .build();

                                client.newCall(request).enqueue(new Callback() {

                                    @Override
                                    public void onFailure(com.squareup.okhttp.Request request, IOException e) {

                                        go();

                                        Log.e("failure", request.toString());
                                    }

                                    @Override
                                    public void onResponse(com.squareup.okhttp.Response response) throws IOException {

                                        String jsonData = response.body().string();

                                        Log.e("response", jsonData);

                                        try {

                                            JSONObject result = new JSONObject(jsonData);

                                            if (!result.getBoolean("error")) {

                                                if (result.has("lowPhotoUrl")) {

                                                    App.getInstance().setPhotoUrl(result.getString("lowPhotoUrl"));
                                                }

                                                if (result.has("moderateImgUrl")) {

                                                    App.getInstance().setPhotoUrl(result.getString("moderateImgUrl"));
                                                }
                                            }

                                            Log.d("My App", response.toString());

                                        } catch (Throwable t) {

                                            Log.e("My App", "Could not parse malformed JSON: \"" + t.getMessage() + "\"");

                                        } finally {

                                            go();
                                        }
                                    }
                                });

                            } catch (Exception ex) {
                                // Handle the error

                                go();
                            }

                        } else {

                            hidepDialog();

                            switch (App.getInstance().getErrorCode()) {

                                case ERROR_CLIENT_ID : {

                                    Toast.makeText(RegisterActivity.this, getString(R.string.error_client_id), Toast.LENGTH_SHORT).show();
                                }

                                case ERROR_CLIENT_SECRET : {

                                    Toast.makeText(RegisterActivity.this, getString(R.string.error_client_secret), Toast.LENGTH_SHORT).show();
                                }

                                case ERROR_LOGIN_TAKEN : {

                                    mViewPager.setCurrentItem(0);

                                    Toast.makeText(RegisterActivity.this, getString(R.string.error_login_taken), Toast.LENGTH_SHORT).show();

                                    break;
                                }

                                case ERROR_EMAIL_TAKEN : {

                                    mViewPager.setCurrentItem(0);

                                    Toast.makeText(RegisterActivity.this, getString(R.string.error_email_taken), Toast.LENGTH_SHORT).show();

                                    break;
                                }

                                case ERROR_MULTI_ACCOUNT : {

                                    Toast.makeText(RegisterActivity.this, getString(R.string.label_multi_account_msg), Toast.LENGTH_SHORT).show();

                                    break;
                                }

                                default: {

                                    Log.e("Profile", "Could not parse malformed JSON: \"" + response.toString() + "\"");
                                    break;
                                }
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.e("signup()", error.toString());

                hidepDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("username", username);
                params.put("fullname", fullname);
                params.put("password", password);
                params.put("photo", photo_url);
                params.put("email", email);
                params.put("referrer", referrer);
                params.put("language", language);
                params.put("facebookId", facebook_id);
                params.put("gender", Integer.toString(gender));
                params.put("age", Integer.toString(age));
                params.put("clientId", CLIENT_ID);
                params.put("hash", Helper.md5(Helper.md5(username) + CLIENT_SECRET));
                params.put("appType", Integer.toString(APP_TYPE_ANDROID));
                params.put("fcm_regId", App.getInstance().getGcmToken());
                 Log.e("dfjkdfn",params.toString());
                return params;
            }
        };

        RetryPolicy policy = new DefaultRetryPolicy((int) TimeUnit.SECONDS.toMillis(VOLLEY_REQUEST_SECONDS), DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonReq.setRetryPolicy(policy);

        App.getInstance().addToRequestQueue(jsonReq);
    }

    public static String getYear(Date value) {
        Calendar date = Calendar.getInstance();
        Calendar today = Calendar.getInstance();

        date.setTime(value);

        /* get raw year between dates */
        int year = today.get(Calendar.YEAR) - date.get(Calendar.YEAR);

        /* calculate exact year */
        if (
                (date.get(Calendar.MONTH) > today.get(Calendar.MONTH)) ||
                        (date.get(Calendar.MONTH) == today.get(Calendar.MONTH) && date.get(Calendar.DATE) > today.get(Calendar.DATE))
        ) {
            year--;
        }

        return year > 0 ? Integer.toString(year) : "0";
    }
}
