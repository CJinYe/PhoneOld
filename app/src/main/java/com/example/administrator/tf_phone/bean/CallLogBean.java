package com.example.administrator.tf_phone.bean;

/**
 * Created by Administrator on 2016/12/3 0003.
 * 通话记录
 */
public class CallLogBean {

    private int id;
    private String name; // 名称
    private String number; // 号码
    private String date; // 日期
    private String date_time;//时间
    private int type; // 来电:1，拨出:2,未接:3
    private int count; // 通话次数
    public boolean isConstants;//是否是联系人

    public String getDate_time() {
        return date_time;
    }

    public void setDate_time(String date_time) {
        this.date_time = date_time;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
