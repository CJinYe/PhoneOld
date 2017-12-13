package com.example.administrator.tf_phone.view.tool;

import android.content.Context;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.tf_phone.R;

/**
 * Created by Administrator on 2016/11/30 0030.
 * 系统Toast与图片Toast
 */
public class ToastUtil {

    //之前显示的内容
    private static String oldMsg;
    //Toast对象
    private static Toast toast = null;
    //第一次时间
    private static long oneTime = 0;
    // 第二次时间
    private static long twoTime = 0;

    //文字Toast
    public static void showToast(Context context, String message) {
        if (toast == null) {
            toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
            toast.show();
            oneTime = System.currentTimeMillis();
        } else {
            twoTime = System.currentTimeMillis();
            if (message.equals(oldMsg)) {
                if (twoTime - oneTime > Toast.LENGTH_SHORT) {
                    toast.show();
                }
            } else {
                oldMsg = message;
                toast.setText(message);
                toast.show();
            }
        }
        oneTime = twoTime;
    }

    //图片Toast
    private static Handler handler = new Handler();
    private static Runnable runnable = new Runnable() {
        public void run() {
            toast.cancel();
            toast = null;//toast隐藏后，将其置为null
        }
    };

    public static void showPicToast(Context context, String message) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.activity_toast, null);//自定义布局
        TextView text = (TextView) view.findViewById(R.id.toast_count);//显示的提示文字
        text.setText(message);
        handler.removeCallbacks(runnable);
        if (toast == null) {//只有mToast==null时才重新创建，否则只需更改提示文字
            toast = new Toast(context);
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.BOTTOM, 0, 20);
            toast.setView(view);
        }
        handler.postDelayed(runnable, 1500);//延迟1.5秒隐藏toast
        toast.show();
    }
    public static void showLongPicToast(Context context, String message) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.activity_toast, null);//自定义布局
        TextView text = (TextView) view.findViewById(R.id.toast_count);//显示的提示文字
        text.setText(message);
        handler.removeCallbacks(runnable);
        if (toast == null) {//只有mToast==null时才重新创建，否则只需更改提示文字
            toast = new Toast(context);
            toast.setDuration(Toast.LENGTH_LONG);
            toast.setGravity(Gravity.BOTTOM, 0, 20);
            toast.setView(view);
        }
        handler.postDelayed(runnable, 2500);//延迟1.5秒隐藏toast
        toast.show();
    }
}
