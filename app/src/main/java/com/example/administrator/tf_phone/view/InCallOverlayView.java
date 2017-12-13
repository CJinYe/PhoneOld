package com.example.administrator.tf_phone.view;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.v4.app.NotificationCompat;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import com.example.administrator.tf_phone.MainActivity;
import com.example.administrator.tf_phone.R;
import com.example.administrator.tf_phone.activity.TelephoneActivity;
import com.example.administrator.tf_phone.conf.Constants;
import com.example.administrator.tf_phone.utils.PhoneUtil;

import java.io.IOException;
import java.io.InputStream;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * @author Administrator
 * @version $Rev$
 * @time 2017-1-16 13:50
 * @des ${TODO}
 * @updateAuthor $Author$
 * @updateDate $Date$
 * @updateDes ${TODO}
 */
public class InCallOverlayView extends Overlay {


    /**
     * 来电号码extra
     */
    public static final String EXTRA_PHONE_NUM = "phoneNum";

    private static Context mContext = null;


    /**
     * 挂电话按钮
     */
    private static Button mEndCallBt = null;

    /**
     * 接听电话按钮
     */
    private static Button mAnswerCallBt = null;

    private static CircleImageView mInCallPic;
    private static TextView mInCallTtie;
    private static String phoneName;
    private static String phoneNumber;
    private static Bitmap mBmp_pic;
    private static NotificationManager mNotificationManager;


    /**
     * 显示
     *
     * @param context 上下文对象
     * @param number
     */
    public static void show(final Context context, String number) {
        synchronized (monitor) {
            mContext = context;
            phoneNumber = number;
            init(context, number, R.layout.call_over_layout);
            InputMethodManager imm = (InputMethodManager) context
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            //            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,
            //                    InputMethodManager.HIDE_IMPLICIT_ONLY);
            //            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
            //            imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);


            //            context.registerReceiver(mEndCallReceiver, new IntentFilter("ACTION_END_CALL"));
        }
    }


    /**
     * 隐藏
     *
     * @param context
     */
    public static void hide(final Context context) {
        synchronized (monitor) {
            if (mOverlay != null) {
                try {

                    //                    PhoneUtil.sendAnswerCallBroadCast(mContext,number);
                    WindowManager wm = (WindowManager) context
                            .getSystemService(Context.WINDOW_SERVICE);
                    // Remove view from WindowManager
                    wm.removeView(mOverlay);


                } catch (Exception e) {
                    e.printStackTrace();
                }
                mOverlay = null;
            }
        }
    }

    /**
     * 初始化布局
     *
     * @param context 上下文对象
     * @param number  电话号码
     * @param layout  布局文件
     * @return 布局
     */
    private static ViewGroup init(Context context, String number, int layout) {
        WindowManager.LayoutParams params = getShowingParams();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        int wmHeight = display.getHeight();
        int wmWidth = display.getWidth();
        if (wmHeight > wmWidth) {
            params.height = wmWidth;
        } else {
            params.height = wmHeight;
        }
        //        params.height = display.getHeight();
        ViewGroup overlay = init(context, layout, params);
        initView(overlay, number);
        getContacts(mContext, number);

        return overlay;
    }

    /**
     * 初始化界面
     */
    private static void initView(View view, String phoneNum) {

        // 初始化各个控件
        mAnswerCallBt = (Button) view.findViewById(R.id.telephone_btn_incall_answer_call);
        mInCallPic = (CircleImageView) view.findViewById(R.id.telephone_iv_incall_pic);
        mEndCallBt = (Button) view.findViewById(R.id.telephone_btn_incall_end_call);
        mInCallTtie = (TextView) view.findViewById(R.id.telephone_tv_incall_title);
        TextView inCallNumber = (TextView) view.findViewById(R.id.telephone_tv_incall_number);


        inCallNumber.setText(phoneNum);

        initNotification(phoneNum);

        addListener();
    }


    private static void getContacts(final Context context, final String number) {


        // 获得Uri
        Uri uriNumber2Contacts = Uri.parse("content://com.android.contacts/" + "data/phones/filter/" + number);
        // 查询Uri，返回数据集
        Cursor cursorCantacts = context.getContentResolver().query(uriNumber2Contacts, null, null, null, null);
        // 如果该联系人存在
        if (cursorCantacts.getCount() > 0) {
            // 移动到第一条数据     　　　　　
            cursorCantacts.moveToFirst();

            int nameFieldColumnIndex = cursorCantacts
                    .getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME);
            phoneName = cursorCantacts.getString(nameFieldColumnIndex);

            // 获得该联系人的contact_id     　　　　　
            Long contactID = cursorCantacts.getLong(cursorCantacts.getColumnIndex("contact_id"));
            // 获得contact_id的Uri     　　　　
            Uri uri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactID);
            // 打开头像图片的InputStream     　　　　
            InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(context.getContentResolver(), uri);
            // 从InputStream获得bitmap     　　　
            mBmp_pic = BitmapFactory.decodeStream(input);


        }


        if (phoneName == null) {

            mInCallTtie.setText("来电");
        } else {
            mInCallTtie.setText("来电 : " + phoneName);
        }
        if (mBmp_pic == null) {
            mInCallPic.setImageResource(R.drawable.icon);
        } else {
            mInCallPic.setImageBitmap(mBmp_pic);
        }
    }


    /**
     * 添加监听器
     */
    private static void addListener() {
        mEndCallBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mNotificationManager != null) {
                    mNotificationManager.cancelAll();
                }
                PhoneUtil.endCall(mContext);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        hide(mContext);
                    }
                }, 4000);
            }
        });
        mAnswerCallBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(mContext, TelephoneActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(Constants.TELEPHONE_NUMBER, phoneNumber);
                intent.putExtra(Constants.TELEPHONE_STATE, Constants.ACTION_ANSWER_CALL);
                mContext.startActivity(intent);
                answerRingingCallWithBroadcast();
                answerRingingCallWithBroadcast();
                if (mNotificationManager != null) {
                    mNotificationManager.cancelAll();
                }
                //PhoneUtil.answerCall(mContext);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        hide(mContext);
                    }
                },800);
            }
        });
    }

    /**
     * 获取显示参数
     *
     * @return
     */
    private static WindowManager.LayoutParams getShowingParams() {
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
        // TYPE_TOAST TYPE_SYSTEM_OVERLAY 在其他应用上层 在通知栏下层 位置不能动鸟
        // TYPE_PHONE 在其他应用上层 在通知栏下层
        // TYPE_PRIORITY_PHONE TYPE_SYSTEM_ALERT 在其他应用上层 在通知栏上层 没试出来区别是啥
        // TYPE_SYSTEM_ERROR 最顶层(通过对比360和天天动听歌词得出)
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.x = 0;
        params.y = 0;
        params.format = PixelFormat.RGBA_8888;// value = 1
        params.gravity = Gravity.TOP;
        params.flags = WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM
                | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD;
        //        params.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        params.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;

        return params;
    }

    private static void initNotification(String phoneNum) {
        mNotificationManager = (NotificationManager) mContext.getSystemService(NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext);

        Intent notificationIntent = new Intent(mContext, MainActivity.class);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        //        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 1, notificationIntent, 0);

        Resources res = mContext.getResources();
        Bitmap bmp = BitmapFactory.decodeResource(res, R.drawable.call_come_no);

        mBuilder.setContentTitle("你有未接来电")//设置通知栏标题
                .setContentText(phoneNum)
                .setContentIntent(pendingIntent)
                .setLargeIcon(bmp)
                //  .setNumber(number) //设置通知集合的数量
                .setTicker("你有未接来电") //通知首次出现在通知栏，带上升动画效果的
                .setWhen(System.currentTimeMillis())//通知产生的时间，会在通知信息里显示，一般是系统获取到的时间
                .setPriority(Notification.PRIORITY_DEFAULT) //设置该通知优先级
                //  .setAutoCancel(true)//设置这个标志当用户单击面板就可以让通知将自动取消
                .setOngoing(false)//ture，设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)
                .setDefaults(Notification.DEFAULT_VIBRATE)//向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合
                //Notification.DEFAULT_ALL  Notification.DEFAULT_SOUND 添加声音 // requires VIBRATE permission
                .setSmallIcon(R.drawable.call_come_no);//设置通知小ICON
        Notification notif = mBuilder.build();
        notif.flags = Notification.FLAG_ONGOING_EVENT;
        mNotificationManager.notify(1, notif);
    }


    /**
     * 伪造一个有线耳机插入，并按接听键的广播，让系统开始接听电话。
     */
    private static void answerRingingCallWithBroadcast() {

        try {
            Runtime.getRuntime().exec("input keyevent " +
                    Integer.toString(KeyEvent.KEYCODE_HEADSETHOOK));
        } catch (IOException e) {
            // Runtime.exec(String) had an I/O problem, try to fall back
            String enforcedPerm = "android.permission.CALL_PRIVILEGED";
            Intent btnDown = new Intent(Intent.ACTION_MEDIA_BUTTON).putExtra(
                    Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_DOWN,
                            KeyEvent.KEYCODE_HEADSETHOOK));
            Intent btnUp = new Intent(Intent.ACTION_MEDIA_BUTTON).putExtra(
                    Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_UP,
                            KeyEvent.KEYCODE_HEADSETHOOK));

            mContext.sendOrderedBroadcast(btnDown, enforcedPerm);
            mContext.sendOrderedBroadcast(btnUp, enforcedPerm);
        }


//        try {
//            Method method = Class.forName("android.os.ServiceManager")
//                    .getMethod("getService", String.class);
//
//            IBinder binder = (IBinder) method.invoke(null, new Object[]{TELEPHONY_SERVICE});
//            ITelephony telephony = ITelephony.Stub.asInterface(binder);
//            telephony.answerRingingCall();
//        } catch (NoSuchMethodException e) {
//            Log.d("Sandy", "", e);
//        } catch (ClassNotFoundException e) {
//            Log.d("Sandy", "", e);
//        } catch (Exception e) {
//            Log.d("Sandy", "", e);
//            try {
//                Log.e("Sandy", "for version 4.1 or larger");
//                Intent intent1 = new Intent("android.intent.action.MEDIA_BUTTON");
//                KeyEvent keyEvent = new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_HEADSETHOOK);
//                intent1.putExtra("android.intent.extra.KEY_EVENT", keyEvent);
//
//                mContext.sendOrderedBroadcast(intent1, "android.permission.CALL_PRIVILEGED");
//            } catch (Exception e2) {
//                Log.d("Sandy", "", e2);
//                Intent meidaButtonIntent = new Intent(Intent.ACTION_MEDIA_BUTTON);
//                KeyEvent keyEvent = new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_HEADSETHOOK);
//                meidaButtonIntent.putExtra(Intent.EXTRA_KEY_EVENT, keyEvent);
//                mContext.sendOrderedBroadcast(meidaButtonIntent, null);
//            }
//        }

    }

}
