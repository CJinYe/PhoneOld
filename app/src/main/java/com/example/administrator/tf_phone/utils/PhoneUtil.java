package com.example.administrator.tf_phone.utils;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.android.internal.telephony.ITelephony;
import com.example.administrator.tf_phone.activity.TelephoneActivity;
import com.example.administrator.tf_phone.conf.Constants;

import java.lang.reflect.Method;

/**
 * @author Administrator
 * @version $Rev$
 * @time 2017-1-13 9:34
 * @des ${TODO}
 * @updateAuthor $Author$
 * @updateDate $Date$
 * @updateDes ${TODO}
 */
public class PhoneUtil {
    /**
     * 关闭输入法
     * @param context
     */
    public static void closeEditer(Activity context) {
        View view = context.getWindow().peekDecorView();
        if (view != null) {
            InputMethodManager inputmanger = (InputMethodManager)context
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /**
     * 收起输入法
     * @param context
     * @param view
     */
    public static void HideKeyboard(Context context, View view) {
        if (null == view)
            return;
        InputMethodManager imm = (InputMethodManager)context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    /**
     * 发送挂断电话的广播
     * @param context
     */
    public static void sendEndCallBroadCast(Context context) {
        Intent i = new Intent();
        i.setAction(TelephoneActivity.ACTION_END_CALL);
        context.sendBroadcast(i);
    }
    /**
     * 发送挂断电话的广播
     * @param context
     */
    public static void sendAnswerCallBroadCast(Context context,String number) {
        Intent i = new Intent();
        i.setAction(TelephoneActivity.ACTION_ANSWER_CALL);
        i.putExtra(Constants.TELEPHONE_NUMBER, number);
        context.sendBroadcast(i);
    }

    /**
     * 挂断电话
     * @param context
     */
    public static synchronized void endCall(Context context) {
        TelephonyManager mTelMgr = (TelephonyManager)context
                .getSystemService(Service.TELEPHONY_SERVICE);
        Class<TelephonyManager> c = TelephonyManager.class;
        try {
            Method getITelephonyMethod = c.getDeclaredMethod("getITelephony", (Class[])null);
            getITelephonyMethod.setAccessible(true);
            ITelephony iTelephony = null;
            System.out.println("End call.");
            iTelephony = (ITelephony)getITelephonyMethod.invoke(mTelMgr, (Object[])null);
            iTelephony.endCall();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Fail to answer ring call.");
        }
    }

    /**
     * 接听电话
     * @param context
     */
    public static synchronized void answerRingingCall(Context context) {
        // 据说该方法只能用于Android2.3及2.3以上的版本上，但本人在2.2上测试可以使用
        try {
            // 插耳机
            Intent localIntent1 = new Intent(Intent.ACTION_HEADSET_PLUG);
            localIntent1.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            localIntent1.putExtra("state", 1);
            localIntent1.putExtra("microphone", 1);
            localIntent1.putExtra("name", "Headset");
            context.sendOrderedBroadcast(localIntent1, "android.permission.CALL_PRIVILEGED");

            // 按下耳机按钮
            Intent localIntent2 = new Intent(Intent.ACTION_MEDIA_BUTTON);
            KeyEvent localKeyEvent1 = new KeyEvent(KeyEvent.ACTION_DOWN,
                    KeyEvent.KEYCODE_HEADSETHOOK);
            localIntent2.putExtra("android.intent.extra.KEY_EVENT", localKeyEvent1);
            context.sendOrderedBroadcast(localIntent2, "android.permission.CALL_PRIVILEGED");

            // 放开耳机按钮
            Intent localIntent3 = new Intent(Intent.ACTION_MEDIA_BUTTON);
            KeyEvent localKeyEvent2 = new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_HEADSETHOOK);
            localIntent3.putExtra("android.intent.extra.KEY_EVENT", localKeyEvent2);
            context.sendOrderedBroadcast(localIntent3, "android.permission.CALL_PRIVILEGED");

            // 拔出耳机
            Intent localIntent4 = new Intent(Intent.ACTION_HEADSET_PLUG);
            localIntent4.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            localIntent4.putExtra("state", 0);
            localIntent4.putExtra("microphone", 1);
            localIntent4.putExtra("name", "Headset");
            context.sendOrderedBroadcast(localIntent4, "android.permission.CALL_PRIVILEGED");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 接电话
     * @param context
     */
    public static synchronized void answerCall(Context context) {
        TelephonyManager mTelMgr = (TelephonyManager)context
                .getSystemService(Service.TELEPHONY_SERVICE);
        Class<TelephonyManager> c = TelephonyManager.class;
        try {
            Method getITelephonyMethod = c.getDeclaredMethod("getITelephony", (Class[])null);
            getITelephonyMethod.setAccessible(true);
            ITelephony iTelephony = null;
            iTelephony = (ITelephony)getITelephonyMethod.invoke(mTelMgr, (Object[])null);
            iTelephony.answerRingingCall();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Fail to answer ring call.");
        }
    }

}
