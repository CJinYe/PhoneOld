package com.example.administrator.tf_phone.activity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.android.internal.telephony.ITelephony;
import com.example.administrator.tf_phone.MainActivity;
import com.example.administrator.tf_phone.R;
import com.example.administrator.tf_phone.Service.ScreenService;
import com.example.administrator.tf_phone.conf.Constants;
import com.example.administrator.tf_phone.utils.PhoneUtil;
import com.example.administrator.tf_phone.utils.SpUtils;
import com.example.administrator.tf_phone.view.BaseActivity;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.R.attr.keycode;
import static android.R.attr.type;
import static com.example.administrator.tf_phone.activity.TelephoneActivity.ACTION_END_CALL;

/**
 * @author Administrator
 * @version $Rev$
 * @time 2017-1-16 13:50
 * @des ${TODO}
 * @updateAuthor $Author$
 * @updateDate $Date$
 * @updateDes ${TODO}
 */

public class InCallActivity extends BaseActivity {

    private Button mEndCallBt = null;


    private Button mAnswerCallBt = null;

    private Intent scrOnIntent;
    private String phoneNum;

    /**
     * 电话挂断广播接收器
     */
    private BroadcastReceiver mEndCallReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && intent.getAction().equals(ACTION_END_CALL)) {
                //如果API大于23 调到设置弹窗权限的界面 只跳转2次
                Intent intent1 = new Intent(InCallActivity.this, MainActivity.class);
                startActivity(intent1);
                Intent intentSetting = null;
                if (android.os.Build.VERSION.SDK_INT >= 23) {
                    SpUtils spUtils = new SpUtils(InCallActivity.this);
                    if (!Settings.canDrawOverlays(InCallActivity.this)) {
                        int permission = spUtils.getInt(Constants.PERMISSION_DRAW_OVERLAYS, 0);
                        if (permission < 2) {
                            intentSetting = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                            intentSetting.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intentSetting);
                            permission++;
                            spUtils.putInt(Constants.PERMISSION_DRAW_OVERLAYS, permission);
                        }
                    }
                }

                finish();
            }
        }
    };
    private CircleImageView mInCallPic;
    private String mPhoneName;
    private Bitmap mBmp_pic;
    private TextView mInCallTtie;
    private NotificationManager mNotificationManager;


    @Override
    protected void initContentView(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.call_over_layout);

        //        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        //        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,
        //                InputMethodManager.HIDE_IMPLICIT_ONLY);
        //        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);


        // 点亮屏幕
        scrOnIntent = new Intent(this, ScreenService.class);
        startService(scrOnIntent);
        phoneNum = getIntent().getStringExtra(Constants.TELEPHONE_NUMBER);
        getContacts();

        initView();
        initNotification();
        registerReceiver(mEndCallReceiver, new IntentFilter(ACTION_END_CALL));
    }


    /**
     * 初始化界面
     */
    private void initView() {

        // 初始化各个控件
        mAnswerCallBt = (Button) findViewById(R.id.telephone_btn_incall_answer_call);
        mInCallPic = (CircleImageView) findViewById(R.id.telephone_iv_incall_pic);
        mEndCallBt = (Button) findViewById(R.id.telephone_btn_incall_end_call);
        mInCallTtie = (TextView) findViewById(R.id.telephone_tv_incall_title);
        TextView inCallNumber = (TextView) findViewById(R.id.telephone_tv_incall_number);


        inCallNumber.setText(phoneNum);


        addListener();
    }

    private void getContacts() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // 获得Uri
                Uri uriNumber2Contacts = Uri.parse("content://com.android.contacts/" + "data/phones/filter/" + phoneNum);
                // 查询Uri，返回数据集
                Cursor cursorCantacts = getContentResolver().query(uriNumber2Contacts, null, null, null, null);
                // 如果该联系人存在
                if (cursorCantacts.getCount() > 0) {
                    // 移动到第一条数据     　　　　　
                    cursorCantacts.moveToFirst();

                    int nameFieldColumnIndex = cursorCantacts
                            .getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME);
                    mPhoneName = cursorCantacts.getString(nameFieldColumnIndex);

                    // 获得该联系人的contact_id     　　　　　
                    Long contactID = cursorCantacts.getLong(cursorCantacts.getColumnIndex("contact_id"));
                    // 获得contact_id的Uri     　　　　
                    Uri uri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactID);
                    // 打开头像图片的InputStream     　　　　
                    InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(getContentResolver(), uri);
                    // 从InputStream获得bitmap     　　　
                    mBmp_pic = BitmapFactory.decodeStream(input);
                    Log.d("phonenumbersss", "number = " + phoneNum + "--type = " + type + "---name = " + mPhoneName + "---mBmp_pic = " + mBmp_pic);


                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            if (mPhoneName == null) {

                                mInCallTtie.setText("来电");
                            } else {
                                mInCallTtie.setText("来电 : " + mPhoneName);
                            }
                            if (mBmp_pic == null) {
                                mInCallPic.setImageResource(R.drawable.icon);
                            } else {
                                mInCallPic.setImageBitmap(mBmp_pic);
                            }
                        }
                    });

                }
            }
        }).start();
    }



    /**
     * 添加监听器
     */
    private void addListener() {
        mEndCallBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mNotificationManager != null) {
                    mNotificationManager.cancelAll();
                }
                PhoneUtil.endCall(InCallActivity.this);
                finish();
            }
        });
        mAnswerCallBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                answerRingingCallWithBroadcast();
                answerRingingCallWithBroadcast();
                if (mNotificationManager != null) {
                    mNotificationManager.cancelAll();
                }
                //PhoneUtil.answerCall(InCallActivity.this);

                Intent intent = new Intent(InCallActivity.this, TelephoneActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(Constants.TELEPHONE_NUMBER, phoneNum);
                intent.putExtra(Constants.TELEPHONE_STATE, Constants.ACTION_ANSWER_CALL);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;//不执行父类点击事件
        }
        if (keyCode == KeyEvent.KEYCODE_TV_CONTENTS_MENU || keycode == KeyEvent.KEYCODE_MENU || keycode == KeyEvent.KEYCODE_TV_CONTENTS_MENU) {
            return true;
        }
        if (keyCode == KeyEvent.KEYCODE_HOME) {
            return true;
        }
        return super.onKeyDown(keyCode, event);//继续执行父类其他点击事件
    }

    @Override
    protected void onStop() {
        stopService(scrOnIntent);
        super.onStop();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
//        TelephonyManager telMgr = (TelephonyManager) getSystemService(Service.TELEPHONY_SERVICE);
//        if (telMgr.getCallState()==TelephonyManager.CALL_STATE_IDLE){
//            finish();
//        }
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mEndCallReceiver);
        Intent intent1 = new Intent(InCallActivity.this, MainActivity.class);
        startActivity(intent1);
        super.onDestroy();

    }

    private void initNotification() {
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);

        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        //        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, notificationIntent, 0);

        Resources res = getResources();
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
    private void answerRingingCallWithBroadcast() {

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

            sendOrderedBroadcast(btnDown, enforcedPerm);
            sendOrderedBroadcast(btnUp, enforcedPerm);
        }


        try {
            Method method = Class.forName("android.os.ServiceManager")
                    .getMethod("getService", String.class);

            IBinder binder = (IBinder) method.invoke(null, new Object[]{TELEPHONY_SERVICE});
            ITelephony telephony = ITelephony.Stub.asInterface(binder);
            telephony.answerRingingCall();
        } catch (NoSuchMethodException e) {
            Log.d("Sandy", "", e);
        } catch (ClassNotFoundException e) {
            Log.d("Sandy", "", e);
        } catch (Exception e) {
            Log.d("Sandy", "", e);
            try {
                Log.e("Sandy", "for version 4.1 or larger");
                Intent intent1 = new Intent("android.intent.action.MEDIA_BUTTON");
                KeyEvent keyEvent = new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_HEADSETHOOK);
                intent1.putExtra("android.intent.extra.KEY_EVENT", keyEvent);

                sendOrderedBroadcast(intent1, "android.permission.CALL_PRIVILEGED");
            } catch (Exception e2) {
                Log.d("Sandy", "", e2);
                Intent meidaButtonIntent = new Intent(Intent.ACTION_MEDIA_BUTTON);
                KeyEvent keyEvent = new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_HEADSETHOOK);
                meidaButtonIntent.putExtra(Intent.EXTRA_KEY_EVENT, keyEvent);
                sendOrderedBroadcast(meidaButtonIntent, null);
            }
        }
    }

}
