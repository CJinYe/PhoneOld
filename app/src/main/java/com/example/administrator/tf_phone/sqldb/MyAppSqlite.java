package com.example.administrator.tf_phone.sqldb;

import com.example.administrator.tf_phone.utils.CrashHandler;
import com.example.administrator.tf_phone.view.BaseActivity;
import com.icox.updateapp.CrashApplication;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/12/6 0006.
 */
//public class MyAppSqlite extends Application {
    public class MyAppSqlite extends CrashApplication {

    public static MyDateBaseHelp db;
    //管理所有activity销毁
    List<BaseActivity> mActivityList = new ArrayList<>();
    //记录最后销毁的activity
    public BaseActivity baseActivity;

    public void onCreate() {
        super.onCreate();
        //创建数据库
        db = new MyDateBaseHelp(getApplicationContext());
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(getApplicationContext());
    }

    /**
     * 提供一个添加activity的方法
     * 每新建一个activity就增加到集合里面
     */
    public void addActivity(BaseActivity activity) {
        if (!mActivityList.contains(activity)) {
            mActivityList.add(activity);
        }
    }

    //提供一个移除activity的方法
    public void removeActivity(BaseActivity activity) {
        if (mActivityList.contains(activity)) {
            mActivityList.remove(activity);
        }
    }

    /**
     * 提供一个清空集合的方法
     * 就是使用循环对集合里的activity逐个销魂
     */
    public void clearAllActivity() {
        baseActivity = mActivityList.get(mActivityList.size() - 1);
        for (int i = 0; i < mActivityList.size(); i++) {
            BaseActivity activity = mActivityList.get(i);
            activity.finish();
        }
        mActivityList.clear();
    }
}
