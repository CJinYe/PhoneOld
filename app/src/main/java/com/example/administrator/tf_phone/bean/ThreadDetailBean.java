package com.example.administrator.tf_phone.bean;

/**
 * 短信
 */
public class ThreadDetailBean {
    private String phone;//电话
    private String date;//时间
    private String content;//内容
    private int layoutId;//ID
    private boolean bo;//状态
    private int id;

    public boolean getBo() {
        return bo;
    }

    public void setBo(boolean bo) {
        this.bo = bo;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ThreadDetailBean(String phone, String date, String content, int layoutId ,int id) {
        super();
        this.phone = phone;
        this.date = date;
        this.content = content;
        this.layoutId = layoutId;
        this.id = id;

    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getLayoutId() {
        return layoutId;
    }

    public void setLayoutId(int layoutId) {
        this.layoutId = layoutId;
    }

}
