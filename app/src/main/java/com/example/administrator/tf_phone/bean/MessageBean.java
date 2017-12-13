package com.example.administrator.tf_phone.bean;

import android.graphics.Bitmap;

/**
 * @author Administrator
 * @version $Rev$
 * @time 2017-1-22 14:59
 * @des ${TODO}
 * @updateAuthor $Author$
 * @updateDate $Date$
 * @updateDes ${TODO}
 */
public class MessageBean {
    public String _id ;
    public String address ;
    public String person ;
    public String type ;
    public String body ;
    public String date ;
    public String name ;
    public String read ;
    public Bitmap pic ;
    public String toAddressString(){

        return address;
    }
}
