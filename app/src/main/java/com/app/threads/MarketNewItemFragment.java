package com.app.threads;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;

import com.android.volley.toolbox.ImageLoader;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.RequestBody;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import github.ankushsachdeva.emojicon.EmojiconEditText;
import com.app.threads.app.App;
import com.app.threads.constants.Constants;
import com.app.threads.dialogs.PostImageChooseDialog;
import com.app.threads.util.CustomRequest;
import com.app.threads.util.Helper;

public class MarketNewItemFragment extends Fragment implements Constants {

    public static final int RESULT_OK = -1;

    private ProgressDialog pDialog;

    EmojiconEditText mItemEdit;
    EditText mItemTitle, mItemPrice;
    ImageView mChoiceItemImg;
    ImageView mChoiceItemImgs;
    TextView tvUserName,tvPost;
    CircularImageView civUserImage;


    String title = "", description = "", imgUrl = "", previewImgUrl = "", postArea = "", postCountry = "", postCity = "", postLat = "0.000000", postLng = "0.000000";
    private String selectedPostImg = "", newItemImageFileName = "";
    private Uri outputFileUri;

    private int price = 0, btn_number = 0;

    private Boolean loading = false;
    private final Boolean img2_upload = false;
    private final Boolean img3_upload = false;
    private final Boolean img4_upload = false;

    public MarketNewItemFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setRetainInstance(true);

        setHasOptionsMenu(true);

        initpDialog();

        Intent i = getActivity().getIntent();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_market_new_item, container, false);

        if (loading) {
            showpDialog();
        }

        mItemEdit = (EmojiconEditText) rootView.findViewById(R.id.itemDescription);
        mItemTitle = (EditText) rootView.findViewById(R.id.itemTitle);
        mItemPrice = (EditText) rootView.findViewById(R.id.itemPrice);
        civUserImage = (CircularImageView) rootView.findViewById(R.id.civUserImage);
        tvUserName = (TextView) rootView.findViewById(R.id.tvUserName);

        mChoiceItemImg = (ImageView) rootView.findViewById(R.id.choiceItemImg);
        mChoiceItemImgs = (ImageView) rootView.findViewById(R.id.choiceItemImgs);
        tvPost = (TextView) rootView.findViewById(R.id.tvPost);

        if (price != 0) {
            mItemPrice.setText(Integer.toString(price));
        }

        mItemTitle.setText(title);
        mItemEdit.setText(description);

        setEditTextMaxLength(POST_CHARACTERS_LIMIT);

        mChoiceItemImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (selectedPostImg.length() == 0) {

                    btn_number = 1;

                    if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE_PHOTO);

                        } else {

                            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE_PHOTO);
                        }

                    } else {
                        chooseImage(1);
                      //  choiceImage(1);
                    }

                } else {

                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                    alertDialog.setTitle(getText(R.string.action_remove));

                    alertDialog.setMessage(getText(R.string.label_delete_img));
                    alertDialog.setCancelable(true);

                    alertDialog.setNeutralButton(getText(R.string.action_cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            dialog.cancel();
                        }
                    });

                    alertDialog.setPositiveButton(getText(R.string.action_remove), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                            mChoiceItemImg.setImageResource(R.drawable.ic_action_camera);
                            selectedPostImg = "";
                            dialog.cancel();
                        }
                    });

                    alertDialog.show();
                }
            }
        });

        mItemEdit.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                if (selectedPostImg != null && selectedPostImg.length() > 0) {

                    if (title.length() > 0) {

                        if (price > 0) {
                            if (description.length() > 0) {

                                tvPost.setBackground(getResources().getDrawable(R.drawable.bg_gredient));

                            } else {

                                Toast toast= Toast.makeText(getActivity(), getText(R.string.msg_item_select_description), Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                            }

                        } else {

                            Toast toast= Toast.makeText(getActivity(), getText(R.string.msg_item_select_price), Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                        }

                    } else {

                        Toast toast= Toast.makeText(getActivity(), getText(R.string.msg_item_select_title), Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    }

                } else {

                    Toast toast= Toast.makeText(getActivity(), getText(R.string.msg_item_select_img), Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                int cnt = s.length();

                if (cnt == 0) {

                    getActivity().setTitle(getText(R.string.title_activity_new_item));

                } else {

                    getActivity().setTitle(Integer.toString(POST_CHARACTERS_LIMIT - cnt));
                }
            }

        });

        mItemTitle.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                getActivity().setTitle(getText(R.string.title_activity_new_item));
            }

        });

        mItemPrice.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                getActivity().setTitle(getText(R.string.title_activity_new_item));
            }

        });

        if (selectedPostImg != null && selectedPostImg.length() > 0) {
            mChoiceItemImg.setVisibility(View.GONE);
            mChoiceItemImgs.setVisibility(View.VISIBLE);

            mChoiceItemImgs.setImageURI(FileProvider.getUriForFile(getActivity(), BuildConfig.APPLICATION_ID + ".provider", new File(selectedPostImg)));
        }
         if(isAdded()){
             App.getInstance().getImageLoader().get(App.getInstance().getPhotoUrl(), ImageLoader.getImageListener(civUserImage, R.drawable.profile_default_photo, R.drawable.profile_default_photo));
             tvUserName.setText(App.getInstance().getUsername());
         }

        tvPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (App.getInstance().isConnected()) {

                    description = mItemEdit.getText().toString();
                    description = description.trim();

                    if (mItemPrice.getText().toString().length() > 0) {

                        price = Integer.parseInt(mItemPrice.getText().toString());

                    } else {

                        price = 0;
                    }

                    title = mItemTitle.getText().toString();
                    title = title.trim();

                    if (selectedPostImg != null && selectedPostImg.length() > 0) {

                        if (title.length() > 0) {

                            if (price > 0) {

                                if (description.length() > 0) {

                                    loading = true;

                                    showpDialog();

                                    File f = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), newItemImageFileName);

                                    uploadFile(METHOD_MARKET_UPLOAD_IMG, f, 0);

                                } else {

                                    Toast toast= Toast.makeText(getActivity(), getText(R.string.msg_item_select_description), Toast.LENGTH_SHORT);
                                    toast.setGravity(Gravity.CENTER, 0, 0);
                                    toast.show();
                                }

                            } else {

                                Toast toast= Toast.makeText(getActivity(), getText(R.string.msg_item_select_price), Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                            }

                        } else {

                            Toast toast= Toast.makeText(getActivity(), getText(R.string.msg_item_select_title), Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                        }

                    } else {

                        Toast toast= Toast.makeText(getActivity(), getText(R.string.msg_item_select_img), Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    }

                }
                else {
                    Toast toast= Toast.makeText(getActivity(), getText(R.string.msg_network_error), Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }

            }
        });


        // Inflate the layout for this fragment
        return rootView;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {

            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE_PHOTO: {

                // If request is cancelled, the result arrays are empty.

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    choiceImage(btn_number);

                } else if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {

                    if (!ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                        showNoStoragePermissionSnackbar();
                    }
                }

                return;
            }
        }
    }

    public void showNoStoragePermissionSnackbar() {

        Snackbar.make(getView(), getString(R.string.label_no_storage_permission) , Snackbar.LENGTH_LONG).setAction(getString(R.string.action_settings), new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                openApplicationSettings();

                Toast.makeText(getActivity(), getString(R.string.label_grant_storage_permission), Toast.LENGTH_SHORT).show();
            }

        }).show();
    }

    public void openApplicationSettings() {

        Intent appSettingsIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + getActivity().getPackageName()));
        startActivityForResult(appSettingsIntent, 10001);
    }

    public void setEditTextMaxLength(int length) {

        InputFilter[] FilterArray = new InputFilter[1];
        FilterArray[0] = new InputFilter.LengthFilter(length);
        mItemEdit.setFilters(FilterArray);
    }

    public void onDestroyView() {
        super.onDestroyView();
        hidepDialog();
    }

    protected void initpDialog() {
        pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage(getString(R.string.msg_loading));
        pDialog.setCancelable(false);
    }

    protected void showpDialog() {
        if (!pDialog.isShowing()) pDialog.show();
    }

    protected void hidepDialog() {
        if (pDialog.isShowing()) pDialog.dismiss();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_post: {

                return true;
            }

            default: {

                break;
            }
        }

        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SELECT_POST_IMG && resultCode == RESULT_OK && null != data) {

            Uri selectedImage = data.getData();

            switch (btn_number) {

                case 1: {

                    newItemImageFileName = Helper.randomString(6) + ".jpg";

                    Helper helper = new Helper(getContext());
                    helper.saveImg(selectedImage, newItemImageFileName);

                    selectedPostImg = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + File.separator + newItemImageFileName;
                    mChoiceItemImg.setVisibility(View.GONE);
                    mChoiceItemImgs.setVisibility(View.VISIBLE);
                    mChoiceItemImgs.setImageURI(FileProvider.getUriForFile(getActivity(), BuildConfig.APPLICATION_ID + ".provider", new File(selectedPostImg)));

                    break;
                }

                default: {

                    break;
                }
            }

        } else if (requestCode == CREATE_POST_IMG && resultCode == getActivity().RESULT_OK) {

            switch (btn_number) {

                case 1: {

                    selectedPostImg = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + File.separator + newItemImageFileName;
                    mChoiceItemImg.setVisibility(View.GONE);
                    mChoiceItemImgs.setVisibility(View.VISIBLE);
                    mChoiceItemImgs.setImageURI(FileProvider.getUriForFile(getActivity(), BuildConfig.APPLICATION_ID + ".provider", new File(selectedPostImg)));

                    break;
                }

                default: {

                    break;
                }
            }
        }
    }

    public void choiceImage(int btn) {

        btn_number = btn;

        android.app.FragmentManager fm = getActivity().getFragmentManager();

        PostImageChooseDialog alert = new PostImageChooseDialog();

        alert.show(fm, "alert_dialog_image_choose");
    }

    public void imageFromGallery() {

        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(intent, getText(R.string.label_select_img)), SELECT_POST_IMG);
    }

    public void imageFromCamera() {

        try {

            switch (btn_number) {

                case 1: {

                    newItemImageFileName = Helper.randomString(6) + ".jpg";

                    File sdImageMainDirectory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), newItemImageFileName);
                    outputFileUri = FileProvider.getUriForFile(getActivity(), BuildConfig.APPLICATION_ID + ".provider", sdImageMainDirectory);

                    break;
                }
            }

            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            cameraIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivityForResult(cameraIntent, CREATE_POST_IMG);

        }
        catch (Exception e) {

            Toast.makeText(getActivity(), "Error occured. Please try again later.", Toast.LENGTH_SHORT).show();
        }
    }

    public void sendItem() {

        CustomRequest jsonReq = new CustomRequest(Request.Method.POST, METHOD_MARKET_NEW_ITEM, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {

                            if (!response.getBoolean("error")) {


                            }

                        } catch (JSONException e) {

                            e.printStackTrace();

                        } finally {

                            sendPostSuccess();

                            Log.d("Success", response.toString());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                sendPostSuccess();

                Log.e("Error", error.toString());
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("accountId", Long.toString(App.getInstance().getId()));
                params.put("accessToken", App.getInstance().getAccessToken());
                params.put("price", Integer.toString(price));
                params.put("title", title);
                params.put("description", description);
                params.put("imgUrl", imgUrl);
                params.put("postArea", postArea);
                params.put("postCountry", postCountry);
                params.put("postCity", postCity);
                params.put("postLat", postLat);
                params.put("postLng", postLng);



                return params;
            }
        };

        App.getInstance().addToRequestQueue(jsonReq);
    }

    public void sendPostSuccess() {

        loading = false;

        hidepDialog();

        Intent i = new Intent();
        getActivity().setResult(RESULT_OK, i);

        Toast.makeText(getActivity(), getText(R.string.msg_item_posted), Toast.LENGTH_SHORT).show();

        getActivity().finish();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    public Boolean uploadFile(String serverURL, File file, final int imgId) {
        final OkHttpClient client = new OkHttpClient();
        try {
            RequestBody requestBody = new MultipartBuilder()
                    .type(MultipartBuilder.FORM)
                    .addFormDataPart("uploaded_file", file.getName(), RequestBody.create(MediaType.parse("text/csv"), file))
                    .addFormDataPart("accountId", Long.toString(App.getInstance().getId()))
                    .addFormDataPart("accessToken", App.getInstance().getAccessToken())
                    .build();

            com.squareup.okhttp.Request request = new com.squareup.okhttp.Request.Builder()
                    .url(serverURL)
                    .addHeader("Accept", "application/json;")
                    .post(requestBody)
                    .build();

            client.newCall(request).enqueue(new Callback() {

                @Override
                public void onFailure(com.squareup.okhttp.Request request, IOException e) {

                    loading = false;

                    hidepDialog();

                    Log.e("failure", request.toString());
                }

                @Override
                public void onResponse(com.squareup.okhttp.Response response) throws IOException {

                    String jsonData = response.body().string();

                    Log.e("response", jsonData);

                    try {

                        JSONObject result = new JSONObject(jsonData);

                        if (!result.getBoolean("error")) {

                            switch (imgId) {

                                case 0: {

                                    imgUrl = result.getString("imgUrl");

                                    break;
                                }

                                default: {

                                    break;
                                }
                            }
                        }

                        Log.d("My App", response.toString());

                    } catch (Throwable t) {

                        Log.e("My App", "Could not parse malformed JSON: \"" + t.getMessage() + "\"");

                    } finally {

                        Log.e("response", jsonData);

                        Helper.deleteFile(getContext(), file);

                        sendItem();
                    }

                }
            });

            return true;

        } catch (Exception ex) {
            // Handle the error

            loading = false;

            hidepDialog();
        }

        return false;
    }

    private void chooseImage(int btn) {
        btn_number = btn;

        View  view = LayoutInflater.from(requireActivity()).inflate(R.layout.dialog_add_file, null);
        BottomSheetDialog dialog = new BottomSheetDialog(requireActivity(), R.style.AppBottomSheetDialogTheme);
        dialog.setContentView(view);
        ImageView ivCamera=dialog.findViewById(R.id.ivCamera);
        ivCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {

                    switch (btn_number) {

                        case 1: {

                            newItemImageFileName = Helper.randomString(6) + ".jpg";

                            File sdImageMainDirectory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), newItemImageFileName);
                            outputFileUri = FileProvider.getUriForFile(getActivity(), BuildConfig.APPLICATION_ID + ".provider", sdImageMainDirectory);

                            break;
                        }
                    }

                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
                    cameraIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivityForResult(cameraIntent, CREATE_POST_IMG);

                }
                catch (Exception e) {

                    Toast.makeText(getActivity(), "Error occured. Please try again later.", Toast.LENGTH_SHORT).show();
                }
                dialog.cancel();
            }
        });

        ImageView ivgallery=dialog.findViewById(R.id.ivGallery);
        ivgallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(Intent.createChooser(intent, getText(R.string.label_select_img)), SELECT_POST_IMG);
                dialog.cancel();


            }
        });
        dialog.show();
    }
}