package com.example.administrator.tf_phone.view.tool;

import android.net.Uri;
import android.provider.ContactsContract;

/**
 * Created by Administrator on 2016/12/15 0015.
 * 常量
 */

public class MyAppApiConfig {

    //讯飞语音的SDKID
    public static final String SDKID = "584b77ce";
    //头像名称
    public static final String IMAGE_FILE_NAME = "image.jpg";
    //添加联系人请求码
    public static final int IMAGE_REQUEST_CODE = 0;
    public static final int CAMERA_REQUEST_CODE = 1;
    public static final int RESULT_REQUEST_CODE = 2;
    //来电:CALL_COME_IN，拨出:CALL_COME_OUT,未接:CALL_COME_NO
    public static final int CALL_COME_IN = 1;
    public static final int CALL_COME_OUT = 2;
    public static final int CALL_COME_NO = 3;

    //联系人名字、号码、头像ID  联系人ID
    public static final String[] PHONES_PROJECTION = {ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Phone.PHOTO_ID,
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
    ContactsContract.CommonDataKinds.Note.NOTE,
    ContactsContract.CommonDataKinds.StructuredPostal.CITY};

    //联系人名称
    public static final int PHONES_DISPLAY_NAME_INDEX = 0;
    //电话号码
    public static final int PHONES_NUMBER_INDEX = 1;
    //联系人头像ID
    public static final int PHONES_PHOTO_ID_INDEX = 2;
    //联系人ID
    public static final int PHONES_CONTACT_ID_INDEX = 3;
    //联系人备注
    public static final int PHONES_CONTACT_ID_NOTE = 4;
    //联系人城市
    public static final int PHONES_CONTACT_ID_CITY = 5;

    //联系人名字、号码、头像ID  联系人ID
    public static final String[] PHONES_PROJECTION2 = {ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Phone.PHOTO_ID,
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
            ContactsContract.CommonDataKinds.Note.NOTE,
            ContactsContract.CommonDataKinds.StructuredPostal.CITY};


    //所有短信
    public static final String[] projection = new String[]{"sms.thread_id AS _id", "sms.body AS snippet",
            "groups.msg_count AS msg_count", "sms.address AS address", "sms.date AS date","sms.read AS read"};
    //发送与接收短信
    public static final String[] projection_two = new String[]{"_id", "address", "person",
            "body", "type", "date","read"};
    //信息地址
    public static final Uri uri = Uri.parse("content://sms/");
    //短信
    public static final String[] contentProjection = new String[]{ContactsContract.PhoneLookup._ID,
            ContactsContract.PhoneLookup.DISPLAY_NAME};
    public static final String[] contactProjection = new String[]{ContactsContract.PhoneLookup._ID,
            ContactsContract.PhoneLookup.DISPLAY_NAME};

}
