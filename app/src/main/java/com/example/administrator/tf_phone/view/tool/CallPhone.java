package com.example.administrator.tf_phone.view.tool;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;

import com.example.administrator.tf_phone.view.CallOverlayView;

import static com.example.administrator.tf_phone.view.tool.MyAppApiConfig.PHONES_NUMBER_INDEX;

/**
 * Created by Administrator on 2016/12/15 0015.
 */

public class CallPhone {

    public static void callphone(final Context context, String phone) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.CALL_PHONE}, PHONES_NUMBER_INDEX);
        } else {
            HasSimCard.hasSimCard(context);
            TelephonyManager mTelephonyManager = (TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE);
            if (mTelephonyManager.getSimState() != TelephonyManager.SIM_STATE_READY){
                return;
            }


            Intent intent = new Intent(Intent.ACTION_CALL);
            Uri data = Uri.parse("tel:" + phone);
            intent.setData(data);
            context.startActivity(intent);
            if ("10086".equals(phone) || "10010".equals(phone) || "10000".equals(phone)) {
            } else {

                if (Build.VERSION.SDK_INT < 23) {
                    CallOverlayView.show(context, phone);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            CallOverlayView.hide(context);
                        }
                    }, 3000);
                }else {
                    if (Settings.canDrawOverlays(context)) {
                        CallOverlayView.show(context, phone);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                CallOverlayView.hide(context);
                            }
                        }, 3800);
                    }
                }

            }

        }
    }
}
