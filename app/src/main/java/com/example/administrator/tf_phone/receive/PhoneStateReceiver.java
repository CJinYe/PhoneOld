
package com.example.administrator.tf_phone.receive;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import com.example.administrator.tf_phone.MainActivity;
import com.example.administrator.tf_phone.activity.InCallActivity;
import com.example.administrator.tf_phone.activity.TelephoneActivity;
import com.example.administrator.tf_phone.conf.Constants;
import com.example.administrator.tf_phone.view.CallOverlayView;
import com.example.administrator.tf_phone.view.InCallOverlayView;
import com.example.administrator.tf_phone.view.OverlayView;


public class PhoneStateReceiver extends BroadcastReceiver {

    /**
     * 电话管理
     */
    private TelephonyManager telMgr = null;

    private static final Object monitor = new Object();
    private Context mContext;

    @Override
    public void onReceive(Context context, final Intent intent) {
        String action = intent.getAction();
        mContext = context;
        final String number = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);//得到来电号码

        final String phoneNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);//得到拨打号码
        telMgr = (TelephonyManager) mContext.getSystemService(Service.TELEPHONY_SERVICE);
        int simState = telMgr.getSimState();//SIM卡信息

        if (action.equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
            Constants.isOutCall = true;


            if (simState == TelephonyManager.SIM_STATE_READY) {//SIM卡就绪
                if ("10086".equals(phoneNumber) || "10010".equals(phoneNumber) || "10000".equals(phoneNumber)) {
                    Constants.isOutCall10086 = true;
                    return;
                } else {
                    //                    CallOverlayView.show(mContext,phoneNumber);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //                    showWindow(ctx, "18899751483", 100);
                            showActivity(mContext, phoneNumber, Constants.ACTION_NEW_OUTGOING_CALL);

                        }
                    }, 1300);
                    //                                        new Handler().postDelayed(new Runnable() {
                    //                                            @Override
                    //                                            public void run() {
                    //                                                CallOverlayView.hide(mContext);
                    //                                            }
                    //                                        }, 3000);

                }
            }


        } else {

            switch (telMgr.getCallState()) {

                case TelephonyManager.CALL_STATE_RINGING:// 来电响铃
                    Constants.isOutCall = false;

                    Constants.isInCall = true;

                    //                    if (Build.VERSION.SDK_INT < 23 && Build.VERSION.SDK_INT > 21) {
                    //
                    //                        new Handler().postDelayed(new Runnable() {
                    //                            @Override
                    //                            public void run() {
                    //                                InCallOverlayView.show(mContext, number);
                    //                            }
                    //                        }, 1200);
                    //                    }else {
                    //                        InCallOverlayView.show(mContext, number);
                    //                        if (Build.VERSION.SDK_INT >= 23){
                    //                            if (Settings.canDrawOverlays(context)) {
                    //                                InCallOverlayView.show(mContext, number);
                    //                            }else {
                    //                                showInCallActivity(mContext,number);
                    //                            }
                    //                        }
                    //                    }

                    if (Build.VERSION.SDK_INT >= 23) {
                        if (Settings.canDrawOverlays(context)) {
                            InCallOverlayView.show(mContext, number);
                        } else {
                            showInCallActivity(mContext, number);
                        }
                        if (MainActivity.main != null) {
                            MainActivity.main.finish();
                        }
                    } else if (Build.VERSION.SDK_INT >= 21) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                InCallOverlayView.show(mContext, number);

                                //                                showInCallActivity(mContext, number);
                            }
                        }, 1800);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (MainActivity.main != null) {
                                    MainActivity.main.finish();
                                }
                            }
                        }, 500);
                    } else {
                        InCallOverlayView.show(mContext, number);
                        if (MainActivity.main != null) {
                            MainActivity.main.finish();
                        }
                    }

                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:// 接听电话
                    Constants.CALL_STATE_OFFHOOK = true;
                    if (Constants.isOutCall10086) {
                        Constants.isInCall = true;
                        Constants.isOutCall10086 = false;
                    }

                    if (!Constants.isOutCall) {

                        //                        new Handler().postDelayed(new Runnable() {
                        //                            @Override
                        //                            public void run() {
                        //                                Log.d("actionaa", "接听接听-===" + telMgr.getCallState());
                        //                                //                    showWindow(ctx, "18899751483", 100);
                        //                                showActivity(mContext, number, Constants.ACTION_NEW_OUTGOING_CALL);
                        //
                        //                            }
                        //                        }, 100);
                    }else {
                        Constants.isOutCallFinish = true;
                        Constants.isOutCall = false;
                    }


                    break;
                case TelephonyManager.CALL_STATE_IDLE:// 挂断电话

                    if (Constants.isInCall) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent1 = new Intent(mContext, MainActivity.class);
                                intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                mContext.startActivity(intent1);
                            }
                        }, 1000);
                        Constants.isInCall = false;
                    }

                    if (Constants.isOutCallFinish){
                        TelephoneActivity.telephoneActivity.finish();
                        Constants.isOutCallFinish = false;
                    }

                    Constants.CALL_STATE_OFFHOOK = false;
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            InCallOverlayView.hide(mContext);
                            CallOverlayView.hide(mContext);
                            //                            closeWindow(mContext,number);
                        }
                    }, 1800);
                    //                    PhoneUtil.sendEndCallBroadCast(mContext);
                    break;

                default:
                    break;
            }

        }


    }


    /**
     * 显示来电Activity
     *
     * @param ctx
     * @param number
     */
    private void showActivity(Context ctx, String number, String phoneState) {
        Intent intent = new Intent(ctx, TelephoneActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Constants.TELEPHONE_NUMBER, number);
        intent.putExtra(Constants.TELEPHONE_STATE, phoneState);
        ctx.startActivity(intent);
    }

    /**
     * 显示来电Activity
     *
     * @param ctx
     * @param number
     */
    private void showInCallActivity(Context ctx, String number) {
        Intent intent = new Intent(ctx, InCallActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Constants.TELEPHONE_NUMBER, number);
        ctx.startActivity(intent);
    }

    /**
     * 显示来电弹窗
     *
     * @param ctx    上下文对象
     * @param number 电话号码
     */
    private void showWindow(Context ctx, String number) {
        //        if (Build.VERSION.SDK_INT >= 23)
        //            if (Settings.canDrawOverlays(ctx)) {
        //                OverlayView.show(ctx, number);
        //            } else {
        //                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
        //                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //                ctx.startActivity(intent);
        //            }
        //        else {
        //        }
        OverlayView.show(ctx, number);


    }


    /**
     * 关闭来电弹窗
     *
     * @param ctx 上下文对象
     */
    private void closeWindow(Context ctx, String number) {
        OverlayView.hide(ctx, number);
    }

}
