package com.app.threads_clone.dialogs;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.app.threads_clone.R;
import com.app.threads_clone.constants.Constants;


public class SearchSettingsDialog extends DialogFragment implements Constants {

    TextView genderMale, genderFemale,genderOther, online, photo;
    Button btnApply;
    boolean checkOnline,checkPhoto,male=true,female=true,other=true;

    private int searchGender, searchOnline, searchPhoto;

    /** Declaring the interface, to invoke a callback function in the implementing activity class */
    AlertPositiveListener alertPositiveListener;

    /** An interface to be implemented in the hosting activity for "OK" button click listener */
    public interface AlertPositiveListener {

        void onCloseSettingsDialog(int searchGender, int searchOnline, int searchPhoto);
    }

    /** This is a callback method executed when this fragment is attached to an activity.
     *  This function ensures that, the hosting activity implements the interface AlertPositiveListener
     * */
    public void onAttach(android.app.Activity activity) {

        super.onAttach(activity);
        try {
            alertPositiveListener = (AlertPositiveListener) activity;

        } catch(ClassCastException e){
            // The hosting activity does not implemented the interface AlertPositiveListener
            throw new ClassCastException(activity.toString() + " must implement AlertPositiveListener");
        }
    }

    /** This is a callback method which will be executed
     *  on creating this fragment
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        /** Getting the arguments passed to this fragment */
        Bundle bundle = getArguments();

        searchGender = bundle.getInt("searchGender");
        searchOnline = bundle.getInt("searchOnline");
        searchPhoto = bundle.getInt("searchPhoto");

        /** Creating a builder for the alert dialog window */

        final View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_search_settings, null);
        BottomSheetDialog mBottomSheetDialog = new BottomSheetDialog(getActivity(), R.style.AppBottomSheetDialogTheme);
        mBottomSheetDialog.setContentView(view);
      /*  AlertDialog.Builder b = new AlertDialog.Builder(getActivity());

        *//** Setting a title for the window */

        genderMale = (TextView) view.findViewById(R.id.genderMale);
        genderFemale = (TextView) view.findViewById(R.id.genderFemale);
        genderOther = (TextView) view.findViewById(R.id.tvOther);
        online = (TextView) view.findViewById(R.id.tvOnline);
        photo = (TextView) view.findViewById(R.id.tvOnlyWithPhoto);

        btnApply = (Button) view.findViewById(R.id.btnApply);

        online.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkOnline){
                    checkOnline=false;
                    online.setTextColor(getResources().getColor(R.color.black));
                    online.setBackground(getResources().getDrawable(R.drawable.bg_btn_unselect));

                }else{
                    checkOnline=true;
                    online.setTextColor(getResources().getColor(R.color.white));
                    online.setBackground(getResources().getDrawable(R.drawable.bg_btn_selected));
                }
            }
        });

        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkPhoto){
                    checkPhoto=false;
                    photo.setTextColor(getResources().getColor(R.color.black));
                    photo.setBackground(getResources().getDrawable(R.drawable.bg_btn_unselect));
                }else{
                    checkPhoto=true;
                    photo.setTextColor(getResources().getColor(R.color.white));
                    photo.setBackground(getResources().getDrawable(R.drawable.bg_btn_selected));
                }
            }
        });

        genderMale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(male){
                    male=false;
                    genderMale.setTextColor(getResources().getColor(R.color.black));
                    genderMale.setBackground(getResources().getDrawable(R.drawable.bg_btn_unselect));
                }else{
                    male=true;
                    genderMale.setTextColor(getResources().getColor(R.color.white));
                    genderMale.setBackground(getResources().getDrawable(R.drawable.bg_btn_selected));
                }
            }
        });

        genderFemale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(female){
                    female=false;
                    genderFemale.setTextColor(getResources().getColor(R.color.black));
                    genderFemale.setBackground(getResources().getDrawable(R.drawable.bg_btn_unselect));
                }else{
                    female=true;
                    genderFemale.setTextColor(getResources().getColor(R.color.white));
                    genderFemale.setBackground(getResources().getDrawable(R.drawable.bg_btn_selected));
                }
            }
        });

        genderOther.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(other){
                    other=false;
                    genderOther.setTextColor(getResources().getColor(R.color.black));
                    genderOther.setBackground(getResources().getDrawable(R.drawable.bg_btn_unselect));
                }else{
                    other=true;
                    genderOther.setTextColor(getResources().getColor(R.color.white));
                    genderOther.setBackground(getResources().getDrawable(R.drawable.bg_btn_selected));
                }
            }
        });

        setGender(searchGender);
        setOnline(searchOnline);
        setPhoto(searchPhoto);

        btnApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBottomSheetDialog.dismiss();
                alertPositiveListener.onCloseSettingsDialog(getGender(), getOnline(), getPhoto());
            }
        });

        mBottomSheetDialog.show();
        return  mBottomSheetDialog;

        /** Setting a positive button and its listener */


        /** Creating the alert dialog window using the builder class */
      //  final AlertDialog d = b.create();

        /** Return the alert dialog window */
     //   return d;
    }

    public int getGender() {
        if (male && female) {
            return -1;
        }

        if (male) {
            return 1;
        }

        if (female) {
            return 2;
        }

        return -1;
    }

    public void setGender(int gender) {
        switch (gender) {
            case 1: {
                male=true;
                female=false;
                genderFemale.setTextColor(getResources().getColor(R.color.black));
                genderMale.setTextColor(getResources().getColor(R.color.white));
                genderMale.setBackground(getResources().getDrawable(R.drawable.bg_btn_selected));
                genderFemale.setBackground(getResources().getDrawable(R.drawable.bg_btn_unselect));
                break;
            }

            case 2: {
                male=false;
                female=true;
                genderFemale.setTextColor(getResources().getColor(R.color.white));
                genderMale.setTextColor(getResources().getColor(R.color.black));
                genderMale.setBackground(getResources().getDrawable(R.drawable.bg_btn_unselect));
                genderFemale.setBackground(getResources().getDrawable(R.drawable.bg_btn_selected));
                break;
            }

            default: {
                male=true;
                female=true;
                genderFemale.setTextColor(getResources().getColor(R.color.white));
                genderMale.setTextColor(getResources().getColor(R.color.white));
                genderMale.setBackground(getResources().getDrawable(R.drawable.bg_btn_selected));
                genderFemale.setBackground(getResources().getDrawable(R.drawable.bg_btn_selected));
                break;
            }
        }
    }

    public int getOnline() {

        if (checkOnline) {

            return 0;
        }

        return -1;
    }

    public void setOnline(int online) {
        if (online == -1) {
            checkOnline=false;
            this.online.setTextColor(getResources().getColor(R.color.black));
            this.online.setBackground(getResources().getDrawable(R.drawable.bg_btn_unselect));

        } else {
            checkOnline=true;
            this.online.setTextColor(getResources().getColor(R.color.white));
            this.online.setBackground(getResources().getDrawable(R.drawable.bg_btn_selected));
        }
    }

    public int getPhoto() {

        if (checkPhoto) {

            return 0;
        }

        return -1;
    }

    public void setPhoto(int photo) {
        if (photo == -1) {
            checkPhoto=false;
            this.photo.setTextColor(getResources().getColor(R.color.black));
            this.photo.setBackground(getResources().getDrawable(R.drawable.bg_btn_unselect));

        } else {
            checkPhoto=true;
            this.photo.setTextColor(getResources().getColor(R.color.white));
            this.photo.setBackground(getResources().getDrawable(R.drawable.bg_btn_selected));
        }
    }
}