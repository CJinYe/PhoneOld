package com.example.administrator.tf_phone.bean;

import android.graphics.Bitmap;

/**
 * Created by Administrator on 2016/12/7 0007.
 * 收藏夹
 */

public class FavoriteBean {

    private Bitmap fav_icon;
    private String fav_username;
    private String fav_phone;

    public Bitmap getFav_icon() {
        return fav_icon;
    }

    public void setFav_icon(Bitmap fav_icon) {
        this.fav_icon = fav_icon;
    }

    public FavoriteBean(String fav_phone, String fav_username, Bitmap fav_icon) {
        super();
        this.fav_phone = fav_phone;
        this.fav_username = fav_username;
        this.fav_icon = fav_icon;
    }


    public String getFav_username() {
        return fav_username;
    }

    public void setFav_username(String fav_username) {
        this.fav_username = fav_username;
    }

    public String getFav_phone() {
        return fav_phone;
    }

    public void setFav_phone(String fav_phone) {
        this.fav_phone = fav_phone;
    }

    public String toString() {
        return fav_phone + ", " + fav_username;
    }
}
