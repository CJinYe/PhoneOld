package com.example.administrator.tf_phone.view.tool;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;

import com.example.administrator.tf_phone.R;
import com.example.administrator.tf_phone.sqldb.MyAppSqlite;

import java.io.ByteArrayOutputStream;

/**
 * Created by Administrator on 2016/12/23 0023.
 * 数据库操作
 */

public class OperationSqlite {

    //防止数据重复添加
    public static void AddFavorite(String phone, String user, String headPortrait) {
        //写入数据库
        SQLiteDatabase db = MyAppSqlite.db.getWritableDatabase();
        //插入姓名、电话
        String sql = "replace into collect(phone,username,picture) values ('" + phone + "','" + user + "','" + headPortrait + "')";
        //执行语句
        db.execSQL(sql);
        //关闭数据库
        db.close();
    }

    //删除[此处phone作为主键]
    public static void DeleteFavorite(String phone) {
        SQLiteDatabase dbone = MyAppSqlite.db.getWritableDatabase();
        String sqlone = "delete from collect where phone='" + phone + "'";
        dbone.execSQL(sqlone);
        //        dbone.delete("collect","phone",new String[]{phone});
        dbone.close();
    }

    // 根据姓名删除联系人
    public static void DeleteUserName(Context context, String username) {
        //根据姓名求id
        Uri uri = Uri.parse("content://com.android.contacts/raw_contacts");
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(uri, new String[]{ContactsContract.CommonDataKinds.Phone.
                DISPLAY_NAME}, "display_name=?", new String[]{username}, null);
        //根据id删除data中的相应数据
        resolver.delete(uri, "display_name=?", new String[]{username});
    }

    // 根据Id删除联系人
    public static void DeleteId(Context context, long Id) {
        //根据姓名求id
        ContentResolver resolver = context.getContentResolver();
        String where = ContactsContract.Data._ID + " =?";
        String[] whereparams = new String[]{Id + ""};
        Uri uri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, Id);
        resolver.delete(uri, where, whereparams);

    }


    //删除该联系人最近一次通话记录
    public static void DeleteFirstPhoneInfo(Context context, String callLog) {
        ContentResolver resolver = context.getContentResolver();
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        //删除最近一次的按ID顺序
        //删除格式：number=? and
        //两种：(type=1 or type=3)
        //一种：type=3
        //全部删除：(type=1 or type=2 or type=3)
        //"date desc"：代表数据是倒序 "date asc"：表示顺序
        Cursor cursor = resolver.query(CallLog.Calls.CONTENT_URI, new String[]{"_id"}, "number=? and " +
                "(type=1 or type=2 or type=3)", new String[]{callLog}, "_id desc limit 1");
        if (cursor.moveToFirst()) {
            int id = cursor.getInt(0);
            resolver.delete(CallLog.Calls.CONTENT_URI, "_id=?", new String[]{id + ""});
            ToastUtil.showPicToast(context, "删除成功！");
        }
    }

    //插入内置数据库
    public static void InsertDatas(Context context, String name, String phone, Bitmap photo, String city, String note) {
        ContentValues values = new ContentValues();
        // 根据RawContacts表中已有的rawContactId使用情况自动生成新联系人的rawContac
        Uri rawContactUri = context.getContentResolver().insert(ContactsContract.RawContacts.CONTENT_URI, values);
        long rawContactId = ContentUris.parseId(rawContactUri);

        if (!TextUtils.isEmpty(note)) {
            values.clear();
            values.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId);
            values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE);
            values.put(ContactsContract.CommonDataKinds.Note.NOTE, note);
            context.getContentResolver().insert(ContactsContract.Data.CONTENT_URI, values);
        }

        if (!TextUtils.isEmpty(city)) {
            values.clear();
            values.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId);
            values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE);
            values.put(ContactsContract.CommonDataKinds.StructuredPostal.CITY, city);
            context.getContentResolver().insert(ContactsContract.Data.CONTENT_URI, values);
        }


        //向data表插入姓名数据
        if (!TextUtils.isEmpty(name)) {
            values.clear();
            values.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId);
            values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE);
            values.put(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, name);
            context.getContentResolver().insert(ContactsContract.Data.CONTENT_URI, values);

        }
        // 向data表插入电话数据
        if (!TextUtils.isEmpty(phone)) {
            values.clear();
            values.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId);
            values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
            values.put(ContactsContract.CommonDataKinds.Phone.NUMBER, phone);
            //            values.put(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_OTHER);//TYPE_MOBILE
            values.put(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE);//TYPE_MOBILE
            context.getContentResolver().insert(ContactsContract.Data.CONTENT_URI, values);
        }
        //插入更换的
        if (photo != null && !photo.isRecycled()) {
            final ByteArrayOutputStream os = new ByteArrayOutputStream();
            // 将Bitmap压缩成PNG编码，质量为100%存储
            photo.compress(Bitmap.CompressFormat.PNG, 100, os);
            byte[] avatar = os.toByteArray();
            values.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId);
            values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE);
            values.put(ContactsContract.Contacts.Photo.PHOTO, avatar);
            context.getContentResolver().insert(ContactsContract.Data.CONTENT_URI, values);
        } else {
            //插入默认的
            Bitmap sourceBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_back);
            // 向data表插入头像数据
            final ByteArrayOutputStream os = new ByteArrayOutputStream();
            // 将Bitmap压缩成PNG编码，质量为100%存储
            sourceBitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
            byte[] avatar = os.toByteArray();
            values.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId);
            values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE);
            values.put(ContactsContract.Contacts.Photo.PHOTO, avatar);
            context.getContentResolver().insert(ContactsContract.Data.CONTENT_URI, values);
        }
    }

    //插入内置数据库
    public static void UpdateData(Context context, String name, String phone, Bitmap photo) {
        ContentValues values = new ContentValues();
        // 根据RawContacts表中已有的rawContactId使用情况自动生成新联系人的rawContac
        Uri rawContactUri = context.getContentResolver().insert(ContactsContract.RawContacts.CONTENT_URI, values);
        long rawContactId = ContentUris.parseId(rawContactUri);
        //向data表插入姓名数据

        String where =
                ContactsContract.Data.RAW_CONTACT_ID + "=? AND " + ContactsContract.Data.MIMETYPE + "=? ";
        String[] selectionArgs = new String[]{String.valueOf(rawContactId), ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE};
        //向data表插入姓名数据
        if (!TextUtils.isEmpty(name)) {
            values.clear();
            values.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId);
            values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE);
            values.put(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, name);
            values.put(ContactsContract.CommonDataKinds.StructuredPostal.CITY, "深圳");
            values.put(ContactsContract.CommonDataKinds.Note.NOTE, "傻逼");
            //            context.getContentResolver().update(ContactsContract.Data.CONTENT_URI, values, where, selectionArgs);
            context.getContentResolver().insert(ContactsContract.Data.CONTENT_URI, values);
        }
        // 向data表插入电话数据
        if (!TextUtils.isEmpty(phone)) {
            values.clear();
            values.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId);
            values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
            values.put(ContactsContract.CommonDataKinds.Phone.NUMBER, phone);
            values.put(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_OTHER);//TYPE_MOBILE
            //            context.getContentResolver().update(ContactsContract.Data.CONTENT_URI, values, where, selectionArgs);
            context.getContentResolver().insert(ContactsContract.Data.CONTENT_URI, values);
        }
        //插入更换的
        if (photo != null && !photo.isRecycled()) {
            //            values.clear();
            // 回收并且置为null
            //            photo.recycle();
            //            photo = null;
            // 向data表插入头像数据
            final ByteArrayOutputStream os = new ByteArrayOutputStream();
            // 将Bitmap压缩成PNG编码，质量为100%存储
            photo.compress(Bitmap.CompressFormat.PNG, 100, os);
            byte[] avatar = os.toByteArray();
            //            values.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId);
            //            values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE);
            values.put(ContactsContract.Contacts.Photo.PHOTO, avatar);
            //            context.getContentResolver().update(ContactsContract.Data.CONTENT_URI, values,where,selectionArgs);
            //            context.getContentResolver().insert(ContactsContract.Data.CONTENT_URI, values);


            //rawContactsId -> contactId
            int id = context.getContentResolver().update(ContactsContract.Data.CONTENT_URI, values, where,
                    selectionArgs);
        } else {
            //            values.clear();
            //插入默认的
            Bitmap sourceBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_lockscreen_answer_normal);
            // 向data表插入头像数据
            final ByteArrayOutputStream os = new ByteArrayOutputStream();
            // 将Bitmap压缩成PNG编码，质量为100%存储
            sourceBitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
            byte[] avatar = os.toByteArray();
            values.put(ContactsContract.Contacts.Photo.PHOTO, avatar);
            context.getContentResolver().update(ContactsContract.Data.CONTENT_URI, values, where, selectionArgs);
            //            context.getContentResolver().insert(ContactsContract.Data.CONTENT_URI, values);
        }
    }
}
