package com.example.administrator.tf_phone.bean;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/12/3 0003.
 * 联系人  序列化
 */
public class ContactsModelBean implements Serializable {

    private String name;
    private String phone;
    private long id;
    private String phonebook_label;
    private String sortLetters;  //显示数据拼音的首字母

    private Bitmap phoneBitmap;

    public String note;
    public String city;

    public Bitmap getPhoneBitmap() {
        return phoneBitmap;
    }

    public void setPhoneBitmap(Bitmap phoneBitmap) {
        this.phoneBitmap = phoneBitmap;
    }
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getSortLetters() {
        return sortLetters;
    }

    public void setSortLetters(String sortLetters) {
        this.sortLetters = sortLetters;
    }

    public String getPhonebook_label() {
        return phonebook_label;
    }

    public void setPhonebook_label(String phonebook_label) {
        this.phonebook_label = phonebook_label;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
