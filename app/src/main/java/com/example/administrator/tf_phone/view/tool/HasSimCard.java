package com.example.administrator.tf_phone.view.tool;

import android.content.Context;
import android.telephony.TelephonyManager;

/**
 * Created by Administrator on 2016/12/12 0012.
 * 判断有无SIM卡
 */

public class HasSimCard {

    public static void hasSimCard(Context context) {

        TelephonyManager mTelephonyManager = (TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE);
        int simState = mTelephonyManager.getSimState();
        String hintMessage = "";
        switch (simState) {
            case TelephonyManager.SIM_STATE_UNKNOWN:
                hintMessage = "SIM卡不可用";
                break;
            case TelephonyManager.SIM_STATE_ABSENT:
                hintMessage = "没有SIM卡可用的设备";
                break;
            case TelephonyManager.SIM_STATE_PIN_REQUIRED:
                hintMessage = "已锁定:需要用户的SIM卡销解锁";
                break;
            case TelephonyManager.SIM_STATE_PUK_REQUIRED:
                hintMessage = "已锁定:需要用户的SIM卡PUK解锁 ";
                break;
            case TelephonyManager.SIM_STATE_NETWORK_LOCKED:
                hintMessage = "已锁定:需要一个网络销解锁";
                break;
            case TelephonyManager.SIM_STATE_READY:
                hintMessage = "SIM卡已就绪";
                break;
            default:
                break;
        }
        //有SD卡不弹出Toast
        if (hintMessage.equals("SIM卡已就绪")) {
        } else {
            ToastUtil.showPicToast(context, hintMessage);
            return;
        }
    }
}
