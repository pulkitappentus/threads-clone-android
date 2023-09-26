package com.app.threads_clone.model;

import android.app.Application;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONObject;

import com.app.threads_clone.constants.Constants;


public class BaseGift extends Application implements Constants, Parcelable {

    private long id;
    private int createAt, category;
    private String timeAgo, date, imgUrl,cost;

    public BaseGift() {

    }

    public BaseGift(JSONObject jsonData) {

        try {

            if (!jsonData.getBoolean("error")) {

                //

                this.setId(jsonData.getLong("id"));
                this.setCost(jsonData.getString("cost"));
                this.setCategory(jsonData.getInt("category"));
                this.setImgUrl(jsonData.getString("imgUrl"));
                this.setCreateAt(jsonData.getInt("createAt"));
                this.setDate(jsonData.getString("date"));
                this.setTimeAgo(jsonData.getString("timeAgo"));
            }

        } catch (Throwable t) {

            Log.e("BaseGift", "Could not parse malformed JSON: \"" + jsonData.toString() + "\"");

        } finally {

            Log.d("BaseGift", jsonData.toString());
        }
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCost() {

        return this.cost;
    }

    public void setCost(String cost) {

        this.cost = cost;
    }

    public void setCategory(int category) {

        this.category = category;
    }

    public void setCreateAt(int createAt) {
        this.createAt = createAt;
    }

    public void setTimeAgo(String timeAgo) {
        this.timeAgo = timeAgo;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public void setDate(String date) {
        this.date = date;
    }


    @Override
    public int describeContents() {

        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
    public static final Creator CREATOR = new Creator() {

        public BaseGift createFromParcel(Parcel in) {

            return new BaseGift();
        }

        public BaseGift[] newArray(int size) {
            return new BaseGift[size];
        }
    };
}
