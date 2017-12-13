package com.example.administrator.tf_phone.sqldb;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Administrator on 2016/12/6 0006.
 */
public class MyDateBaseHelp extends SQLiteOpenHelper {

    private final static String DBNAME = "old_phoneinfo.db";//数据库名字
    private static int version = 1;//数据库版本

    public MyDateBaseHelp(Context context) {

        super(context, DBNAME, null, version);
    }

    public void onCreate(SQLiteDatabase db) {
        //创建表：收藏表(名字、号码、头像)
        String collectsql = "create table collect(uid integer," +
                "phone varchar primary key,username varchar,picture string) ";
        db.execSQL(collectsql);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
