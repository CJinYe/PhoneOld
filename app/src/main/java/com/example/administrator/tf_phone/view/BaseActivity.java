package com.example.administrator.tf_phone.view;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.example.administrator.tf_phone.sqldb.MyAppSqlite;
import com.icox.updateapp.BaseAppActivity;

/**
 * Created by Administrator on 2016/12/19 0019.
 */

//public abstract class BaseActivity extends FragmentActivity {
public abstract class BaseActivity extends BaseAppActivity {
//BaseAppActivity
    //当执行完最后销毁的一个activity回调
    public interface Listener {
        public void end();
    }

    private Listener listener;
    public MyAppSqlite testApplication;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        initContentView(savedInstanceState);

        testApplication = (MyAppSqlite) this.getApplication();
        //每新建一个activity旧增加一个activity
        testApplication.addActivity(this);
        //输出栈里面存有的activity数量和顶层activity
        // Log.d("BASE", getTopActivity(this));
    }

    private void initWindow() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    /**
     * 初始化UI，setContentView等
     * 主要是该Activity不加载xml，让子类加载
     */
    protected abstract void initContentView(Bundle savedInstanceState);

    /**
     * 获取栈顶activity
     */
    /*protected String getTopActivity(Activity context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTaskInfos = manager.getRunningTasks(1);

        if (runningTaskInfos != null) {
            return "栈数量:" + runningTaskInfos.size() + "  栈顶Activity:" + runningTaskInfos.get(0).
                    topActivity + "  总Activity数:" + runningTaskInfos.get(0).numActivities;
        } else {
            return null;
        }
    }*/

    protected void onDestroy() {
        testApplication.removeActivity(this);
        //        Log.d("BASE", "onDestroy" + getTopActivity(this));
        super.onDestroy();
        //如果当前的activity等于最后销毁的activity，就回调
        if (testApplication.baseActivity == this) {
            listener.end();
        }
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }
}
