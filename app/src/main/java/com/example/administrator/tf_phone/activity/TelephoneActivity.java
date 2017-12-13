package com.example.administrator.tf_phone.activity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.ContactsContract;
import android.support.v4.app.NotificationCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.administrator.tf_phone.MainActivity;
import com.example.administrator.tf_phone.R;
import com.example.administrator.tf_phone.Service.ScreenService;
import com.example.administrator.tf_phone.adapter.TelephonyRightPagerAdapter;
import com.example.administrator.tf_phone.bean.ContactsModelBean;
import com.example.administrator.tf_phone.bean.MessageBean;
import com.example.administrator.tf_phone.conf.Constants;
import com.example.administrator.tf_phone.receive.HomeWatcherReceiver;
import com.example.administrator.tf_phone.receive.ScreenListener;
import com.example.administrator.tf_phone.sqldb.MyAppSqlite;
import com.example.administrator.tf_phone.utils.PhoneUtil;
import com.example.administrator.tf_phone.view.BaseActivity;
import com.example.administrator.tf_phone.view.NoScrollViewPager;
import com.example.administrator.tf_phone.view.tool.BitmapString;
import com.example.administrator.tf_phone.view.tool.OperationSqlite;
import com.example.administrator.tf_phone.view.tool.ToastUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static android.R.attr.keycode;
import static com.example.administrator.tf_phone.view.tool.MyAppApiConfig.PHONES_CONTACT_ID_INDEX;
import static com.example.administrator.tf_phone.view.tool.MyAppApiConfig.PHONES_DISPLAY_NAME_INDEX;
import static com.example.administrator.tf_phone.view.tool.MyAppApiConfig.PHONES_NUMBER_INDEX;
import static com.example.administrator.tf_phone.view.tool.MyAppApiConfig.PHONES_PHOTO_ID_INDEX;
import static com.example.administrator.tf_phone.view.tool.MyAppApiConfig.PHONES_PROJECTION;
import static com.example.administrator.tf_phone.view.tool.MyAppApiConfig.contentProjection;
import static com.example.administrator.tf_phone.view.tool.MyAppApiConfig.projection;
import static com.example.administrator.tf_phone.view.tool.MyAppApiConfig.projection_two;
import static com.example.administrator.tf_phone.view.tool.MyAppApiConfig.uri;


/**
 * @author Administrator
 * @version $Rev$
 * @time 2017-1-12 18:50
 * @des ${TODO}
 * @updateAuthor $Author$
 * @updateDate $Date$
 * @updateDes ${TODO}
 */
public class TelephoneActivity extends BaseActivity implements View.OnClickListener {
//public class TelephoneActivity extends BaseActivity implements View.OnClickListener {

    /**
     * 挂断电话action
     */
    public static final String ACTION_END_CALL = "ACTION_END_CALL";
    /**
     * 接听电话action
     */
    public static final String ACTION_ANSWER_CALL = "ACTION_ANSWER_CALL";
    public static TelephoneActivity telephoneActivity;
    private String phoneNum;


    //点亮屏幕的Intent
    private Intent scrOnIntent = null;

    private static HomeWatcherReceiver mHomeKeyReceiver = null;
    private List<MessageBean> messageData = new ArrayList<>();
    /**
     * 电话挂断广播接收器
     */
    private BroadcastReceiver mEndCallReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && intent.getAction().equals(ACTION_END_CALL)) {
                finish();
                Intent mIntent = new Intent(TelephoneActivity.this, MainActivity.class);
                startActivity(mIntent);
                if (!Constants.ACTION_ANSWER_CALL.equals(mPhoneState)) {
                    if (!isEndCall) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                            }
                        }, 500);
                    }
                } else {
                    if (!isEndCall) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                finish();
                                Intent mIntent = new Intent(TelephoneActivity.this, MainActivity.class);
                                startActivity(mIntent);
                            }
                        }, 500);
                    }
                }
            }
        }
    };
    /**
     * 电话接听广播接收器
     */
    private BroadcastReceiver mAnswerCallReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && intent.getAction().equals(ACTION_ANSWER_CALL)) {
                phoneNum = getIntent().getStringExtra(Constants.TELEPHONE_NUMBER);
            }
        }
    };
    private Button mEndCallBt;
    private Button mAnswerCallBt;
    private Button mMenuSilence;
    private Button mMenuHandsFree;
    private Button mMenuEndCall;
    private Chronometer mChronometer;
    private NotificationManager mNotificationManager;
    private Button mMenuRecordBt;
    private MediaRecorder mMediaRecorder;
    private de.hdodenhof.circleimageview.CircleImageView mAnswerPhone;
    private ListView mAnswerContacts;
    private Button mMenuCollectBt;
    private Bitmap mBmp_head;
    private String mPhoneName;
    private boolean mIsCollect;
    private TextView mAnswerNumber;
    private AudioManager mAudioManager;
    private String mPhoneState;
    private GridView mAnswerMenuGv;
    private Button mMenuContacts;
    private Button mMenuMessage;
    private Button mMenuKeyboard;
    private NoScrollViewPager mRightViewPager;
    private boolean isEndCall;
    private List<ContactsModelBean> mMContacts;
    public static boolean isOnPause;//是否被系统覆盖了

    //    @SuppressLint("HandlerLeak")
    //    private Handler handler = new Handler() {
    //        @Override
    //        public void handleMessage(Message msg) {
    //            // super.handleMessage(msg);
    //            switch (msg.what) {
    //                case MSG_OK:
    //                    String json = msg.getData().getString("data");
    //                    ArrayList data = new ArrayList<City>();
    //                    if (Utils.parseData(data, json)) {
    //                        mCityAdapter = new CityAdapter(OverLayActivity.this, data);
    //                        mCityList.setAdapter(mCityAdapter);
    //                        mCityAdapter.notifyDataSetChanged();
    //                    }
    //
    //                    mLoadingLayout.setVisibility(View.GONE);
    //                    mRetLayout.setVisibility(View.VISIBLE);
    //                    break;
    //                case MSG_FAILED:
    //                    mLoadingTv.setText(msg.obj + "");
    //                    break;
    //                default:
    //                    break;
    //            }
    //        }
    //    };


    @Override
    protected void initContentView(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        telephoneActivity = this;
        //        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        //        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,
        //                InputMethodManager.HIDE_IMPLICIT_ONLY);
        //        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

        setContentView(R.layout.activity_telephone);
        isOnPause = true;
        isEndCall = false;
        mPhoneState = getIntent().getStringExtra(Constants.TELEPHONE_STATE);
        phoneNum = getIntent().getStringExtra(Constants.TELEPHONE_NUMBER);

        //如果电话处于闲置状态直接finish
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (telephonyManager.getCallState() == TelephonyManager.CALL_STATE_IDLE) {
            finish();
        }

        // 点亮屏幕
        scrOnIntent = new Intent(this, ScreenService.class);
        startService(scrOnIntent);

        getContacts();
        // 初始化界面
        initView();
        // 添加监听器
        initListener();

        registerReceiver(mEndCallReceiver, new IntentFilter(ACTION_END_CALL));
        // registerReceiver(mAnswerCallReceiver, new IntentFilter(ACTION_ANSWER_CALL));

        ScreenListener screenListener = new ScreenListener(this);
        screenListener.begin(new ScreenListener.ScreenStateListener() {
            @Override
            public void onScreenOn() {
            }

            @Override
            public void onScreenOff() {
                isOnPause = false;
            }

            @Override
            public void onUserPresent() {
            }
        });

    }


    /**
     * 初始化界面
     */
    private void initView() {


        //        TextView phoneNumber = (TextView) findViewById(R.id.telephone_tv_incall_number);
        mAnswerNumber = (TextView) findViewById(R.id.telephone_tv_answer_number);
        //RelativeLayout inCall = (RelativeLayout) findViewById(R.id.telephone_rl_incall);
        LinearLayout outCall = (LinearLayout) findViewById(R.id.telephone_rl_outcall);
        //计时器
        mChronometer = (Chronometer) findViewById(R.id.telephone_cm_answer_Chronometer);
        mChronometer.setFormat("%s");
        mMenuSilence = (Button) findViewById(R.id.telephone_btn_menu_silence);
        mMenuHandsFree = (Button) findViewById(R.id.telephone_btn_menu_hands_free);
        mMenuEndCall = (Button) findViewById(R.id.telephone_btn_menu_end_call);
        mMenuCollectBt = (Button) findViewById(R.id.telephone_btn_menu_collect);
        mMenuRecordBt = (Button) findViewById(R.id.telephone_btn_menu_record);
        mMenuContacts = (Button) findViewById(R.id.telephone_btn_menu_contacts);
        mMenuMessage = (Button) findViewById(R.id.telephone_btn_menu_message);
        mMenuKeyboard = (Button) findViewById(R.id.telephone_btn_menu_keyboard);
        mAnswerPhone = (de.hdodenhof.circleimageview.CircleImageView) findViewById(R.id.telephone_iv_answer_phone);
        //        mAnswerContacts = (ListView) findViewById(R.id.telephone_lv_answer_contacts);
        mRightViewPager = (NoScrollViewPager) findViewById(R.id.telephone_pager);
        //        mAnswerMenuGv = (GridView) findViewById(R.id.telephone_gv_answer_menu);

        getContactss();
        if (mBmp_head != null) {
            mAnswerPhone.setImageBitmap(mBmp_head);
        }
        if (mPhoneName != null) {
            mAnswerNumber.setText(mPhoneName);
        } else {
            mAnswerNumber.setText(phoneNum);//设置号码
        }

        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        //设置免提的图标
        if (mAudioManager.isSpeakerphoneOn()) {
            mMenuHandsFree.setBackgroundResource(R.drawable.phone_answer_hands_free_selector);
        } else {
            mMenuHandsFree.setBackgroundResource(R.drawable.phone_answer_hands_free_normal);
        }
        //打电话或者接听

        mChronometer.setBase(SystemClock.elapsedRealtime());//重置计时器
        mChronometer.start();
        //        initNotification();//初始化通知栏


        //查询数据库显示是否收藏
        if (phoneNum != null) {
            initCollect();
        }

        //初始化右侧界面
        TelephonyRightPagerAdapter telephonyRightPagerAdapter = new TelephonyRightPagerAdapter(getSupportFragmentManager(), mMContacts, messageData);
        mRightViewPager.setAdapter(telephonyRightPagerAdapter);
        mRightViewPager.setOffscreenPageLimit(0);
        //        mRightViewPager.setCurrentItem(2);


    }

    private void initCollect() {

        SQLiteDatabase dbl = MyAppSqlite.db.getReadableDatabase();
        String sql = "select * from collect where phone=?";
        Cursor cursor = dbl.query("collect", null, "phone=?", new String[]{phoneNum}, null, null, null);
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            mMenuCollectBt.setBackgroundResource(R.drawable.phone_answer_collect_selector);
            mIsCollect = true;
        } else {
            mMenuCollectBt.setBackgroundResource(R.drawable.phone_answer_collect_normal);
            mIsCollect = false;
        }


        if (cursor != null) {
            cursor.close();
            dbl.close();
        }
    }

    /**
     * 通知栏
     */
    private void initNotification() {
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);

        Intent notificationIntent = new Intent(this, TelephoneActivity.class);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        //       PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, notificationIntent, 0);

        Resources res = getResources();
        Bitmap bmp = BitmapFactory.decodeResource(res, R.drawable.notification_phone_big_icon);

        mBuilder.setContentTitle("正在通话....")//设置通知栏标题
                .setContentText(phoneNum)
                .setContentIntent(pendingIntent)
                .setLargeIcon(bmp)
                //  .setNumber(number) //设置通知集合的数量
                .setTicker("正在通话") //通知首次出现在通知栏，带上升动画效果的
                .setWhen(System.currentTimeMillis())//通知产生的时间，会在通知信息里显示，一般是系统获取到的时间
                .setPriority(Notification.PRIORITY_DEFAULT) //设置该通知优先级
                //  .setAutoCancel(true)//设置这个标志当用户单击面板就可以让通知将自动取消
                .setOngoing(false)//ture，设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)
                .setDefaults(Notification.DEFAULT_VIBRATE)//向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合
                //Notification.DEFAULT_ALL  Notification.DEFAULT_SOUND 添加声音 // requires VIBRATE permission
                .setSmallIcon(R.drawable.notification_phone_icon);//设置通知小ICON
        Notification notif = mBuilder.build();
        notif.flags = Notification.FLAG_ONGOING_EVENT;
        mNotificationManager.notify(0, notif);
    }


    private void getContacts() {
        try {
            // 获得Uri
                                Uri uriNumber2Contacts = Uri.parse("content://com.android.contacts/" + "data/phones/filter/" + phoneNum);
//            Uri uriNumber2Contacts = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNum));
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
                mBmp_head = BitmapFactory.decodeStream(input);

            }
            cursorCantacts.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * 添加监听器
     */
    private void initListener() {
        mMenuEndCall.setOnClickListener(this);//挂断
        mMenuSilence.setOnClickListener(this);//静音
        mMenuHandsFree.setOnClickListener(this);//免提
        mMenuRecordBt.setOnClickListener(this);//录音
        mMenuCollectBt.setOnClickListener(this);//收藏
        mMenuContacts.setOnClickListener(this);//通讯录
        mMenuMessage.setOnClickListener(this);//短信
        mMenuKeyboard.setOnClickListener(this);//键盘
    }


    @Override
    protected void onStop() {
        stopService(scrOnIntent);
        isOnPause = true;
        super.onStop();
    }

    @Override
    protected void onDestroy() {

        unregisterReceiver(mEndCallReceiver);
        //       unregisterReceiver(mAnswerCallReceiver);
        if (mNotificationManager != null) {
            mNotificationManager.cancelAll();
        }
        Intent mIntent = new Intent(TelephoneActivity.this, MainActivity.class);
        startActivity(mIntent);
        super.onDestroy();

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            isOnPause = false;
            Intent home = new Intent(Intent.ACTION_MAIN);
            home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            home.addCategory(Intent.CATEGORY_HOME);
            startActivity(home);
            return true;
            // moveTaskToBack(false);
        }

        if (keyCode == KeyEvent.KEYCODE_TV_CONTENTS_MENU || keycode == KeyEvent.KEYCODE_MENU || keycode == KeyEvent.KEYCODE_TV_CONTENTS_MENU) {
            isOnPause = false;

            return false;
        }
        //        if (keyCode == KeyEvent.KEYCODE_HOME) {
        //            Log.d("onPauseonPauseonPause", "KEYCODE_HOME");
        //            isOnPause = false;
        //            Intent home = new Intent(Intent.ACTION_MAIN);
        //            home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //            home.addCategory(Intent.CATEGORY_HOME);
        //            startActivity(home);
        //            return false;
        //        }


        return super.onKeyDown(keyCode, event);
    }


    @Override
    protected void onPause() {
        super.onPause();
        unregisterHomeKeyReceiver(this);
        Log.d("onPauseonPauseonPause", "onPause");
        if (isOnPause) {
            finish();
            Intent intent = new Intent(this, TelephoneActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(Constants.TELEPHONE_NUMBER, phoneNum);
            intent.putExtra(Constants.TELEPHONE_STATE, Constants.ACTION_NEW_OUTGOING_CALL);
            startActivity(intent);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        registerHomeKeyReceiver(this);
    }

    private static void registerHomeKeyReceiver(Context context) {
        mHomeKeyReceiver = new HomeWatcherReceiver();
        IntentFilter homeFilter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        context.registerReceiver(mHomeKeyReceiver, homeFilter);
    }

    private static void unregisterHomeKeyReceiver(Context context) {
        if (null != mHomeKeyReceiver) {
            context.unregisterReceiver(mHomeKeyReceiver);
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.telephone_btn_menu_contacts://通讯录
                mMenuContacts.setBackgroundResource(R.drawable.phone_answer_contacts_selector);
                mMenuMessage.setBackgroundResource(R.drawable.phone_answer_message_normal);
                mMenuKeyboard.setBackgroundResource(R.drawable.phone_answer_keyboard_normal);
                mRightViewPager.setCurrentItem(0);
                break;
            case R.id.telephone_btn_menu_message://短信
                mMenuContacts.setBackgroundResource(R.drawable.phone_answer_contacts_normal);
                mMenuMessage.setBackgroundResource(R.drawable.phone_answer_message_selector);
                mMenuKeyboard.setBackgroundResource(R.drawable.phone_answer_keyboard_normal);
                mRightViewPager.setCurrentItem(1);
                break;
            case R.id.telephone_btn_menu_keyboard://键盘
                mMenuContacts.setBackgroundResource(R.drawable.phone_answer_contacts_normal);
                mMenuMessage.setBackgroundResource(R.drawable.phone_answer_message_normal);
                mMenuKeyboard.setBackgroundResource(R.drawable.phone_answer_keyboard_selector);
                mRightViewPager.setCurrentItem(2);
                break;
            case R.id.telephone_btn_menu_collect://收藏
                if (mIsCollect) {
                    OperationSqlite.DeleteFavorite(phoneNum);
                    mMenuCollectBt.setBackgroundResource(R.drawable.phone_answer_collect_normal);
                    mIsCollect = false;
                } else {
                    String name;
                    String headPortrait;
                    headPortrait = BitmapString.bitmapToString(TelephoneActivity.this, mBmp_head);
                    //                    if(mBmp_head == null){
                    //                        Bitmap sourceBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.icon_back);
                    //                        headPortrait = BitmapString.bitmapToString(TelephoneActivity.this, sourceBitmap);
                    //                    }else {
                    //                        headPortrait = BitmapString.bitmapToString(TelephoneActivity.this, mBmp_head);
                    //                    }
                    if (!TextUtils.isEmpty(mPhoneName)) {
                        OperationSqlite.AddFavorite(phoneNum, mPhoneName, headPortrait);
                        mMenuCollectBt.setBackgroundResource(R.drawable.phone_answer_collect_selector);
                        mIsCollect = true;
                    } else {
                        ToastUtil.showPicToast(this, "请先将联系人添加至通讯录！");
                    }
                }
                break;
            case R.id.telephone_btn_menu_record://录音
                try {
                    if (mMediaRecorder == null) {
                        //                            Environment.getExternalStorageDirectory()
                        //                            getCacheDir();
                        File file = new File(Environment.getExternalStorageDirectory(), phoneNum + System.currentTimeMillis() + ".3gp");
                        mMediaRecorder = new MediaRecorder();
                        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);   //获得声音数据源
                        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);   // 按3gp格式输出
                        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                        mMediaRecorder.setOutputFile(file.getAbsolutePath());   //输出文件
                        mMediaRecorder.prepare();    //准备
                        mMediaRecorder.start();
                        mMenuRecordBt.setBackgroundResource(R.drawable.phone_answer_record_selector);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.telephone_btn_menu_silence://静音
                if (mAudioManager.isMicrophoneMute()) {
                    mAudioManager.setMicrophoneMute(false);
                    mMenuSilence.setBackgroundResource(R.drawable.phone_answer_silence_normal);
                } else {
                    mAudioManager.setMicrophoneMute(true);
                    mMenuSilence.setBackgroundResource(R.drawable.phone_answer_silence_selector);
                }
                break;
            case R.id.telephone_btn_menu_hands_free://免提
                mAudioManager.setMode(AudioManager.ROUTE_SPEAKER);
                int currVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL);

                if (!mAudioManager.isSpeakerphoneOn()) {
                    mAudioManager.setSpeakerphoneOn(true);
                    mAudioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL,
                            mAudioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL),
                            AudioManager.STREAM_VOICE_CALL);

                    mMenuHandsFree.setBackgroundResource(R.drawable.phone_answer_hands_free_selector);
                } else {
                    mAudioManager.setSpeakerphoneOn(false);
                    mAudioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, currVolume,
                            AudioManager.STREAM_VOICE_CALL);
                    mMenuHandsFree.setBackgroundResource(R.drawable.phone_answer_hands_free_normal);
                }
                break;
            case R.id.telephone_btn_menu_end_call://挂断
                Constants.isOutCallFinish = false;
                PhoneUtil.endCall(TelephoneActivity.this);
                if (mMediaRecorder != null) {
                    mMediaRecorder.stop();
                    mMediaRecorder.release();
                    mMediaRecorder = null;
                }
                if (mNotificationManager != null) {
                    mNotificationManager.cancelAll();
                }

                isEndCall = true;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                        Intent mIntent = new Intent(TelephoneActivity.this, MainActivity.class);
                        startActivity(mIntent);
                    }
                }, 500);
                break;
        }
    }

    private void getContactss() {
        mMContacts = new ArrayList<>();
        new Thread() {
            @Override
            public void run() {
                //                mContacts = new ArrayList<>();
                ContentResolver resolver = getContentResolver();
                //查询联系人数据，query的参数Phone.SORT_KEY_PRIMARY表示将结果集按Phone.SORT_KEY_PRIMARY排序
                Cursor cursor = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        PHONES_PROJECTION, null, null, ContactsContract.CommonDataKinds.Phone.SORT_KEY_PRIMARY);
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        ContactsModelBean model = new ContactsModelBean();
                        String phone = cursor.getString(PHONES_NUMBER_INDEX);
                        model.setPhone(phone);
                        Long contactid = cursor.getLong(PHONES_CONTACT_ID_INDEX);
                        Long photoid = cursor.getLong(PHONES_PHOTO_ID_INDEX);
                        Bitmap contactPhoto = null;
                        //photoid 大于0 表示联系人有头像 如果没有给此人设置头像则给他一个默认的
                        if (photoid > 0) {
                            Uri uri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactid);
                            InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(resolver, uri);
                            contactPhoto = BitmapFactory.decodeStream(input);
                        } else {
                            contactPhoto = BitmapFactory.decodeResource(getResources(), R.drawable.icon);
                        }
                        if (TextUtils.isEmpty(model.getPhone())) {
                            continue;
                        }
                        model.setPhoneBitmap(contactPhoto);
                        model.setName(cursor.getString(PHONES_DISPLAY_NAME_INDEX));
                        mMContacts.add(model);


//                        if (phone.replaceAll(" ", "").equals(phoneNum.replaceAll(" ", ""))) {
//                            mPhoneName = cursor.getString(PHONES_DISPLAY_NAME_INDEX);
//                            mBmp_head = contactPhoto;
//                        }

                    }
                    cursor.close();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                    }
                });
            }
        }.start();
    }

    private void getMessageData() {
        //        messageData = new ArrayList<>();

        new Thread() {
            @Override
            public void run() {


                String messageAddress = "";
                Cursor cursor = getContentResolver().query(uri, projection_two, null, null, " date desc");
                if (cursor != null && cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    //清理数据,防止重复显示
                    //返回游标是否指
                    // 向第最后一行的位置
                    while (cursor.moveToNext()) {
                        Bitmap pic = null;
                        MessageBean messageBean = new MessageBean();

                        String address = cursor.getString(cursor.getColumnIndex("address"));
                        String _id = cursor.getString(cursor.getColumnIndex("_id"));
                        String body = cursor.getString(cursor.getColumnIndex("body"));
                        int count = 3;
                        String date = cursor.getString(cursor.getColumnIndex("date"));
                        String person = cursor.getString(cursor.getColumnIndex("person"));
                        String type = cursor.getString(cursor.getColumnIndex("type"));
                        String read = cursor.getString(cursor.getColumnIndex("read"));
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTimeInMillis(Long.parseLong(date + ""));
                        SimpleDateFormat format = new SimpleDateFormat("MM/dd HH:mm:ss");
                        String formatTime = format.format(calendar.getTime());
                        String phoneName = null;
                        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(address));
                        Cursor concatCursor = getContentResolver().query(uri, contentProjection, null, null, null);
                        if (concatCursor.moveToFirst()) {
                            phoneName = concatCursor.getString(1);
                        }

                        // 获得Uri
                        //        Uri uriNumber2Contacts = Uri.parse("content://com.android.contacts/" + "data/phones/filter/" + number);
                        Uri uriNumber2Contacts = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(address));
                        // 查询Uri，返回数据集
                        Cursor cursorCantacts = getContentResolver().query(uriNumber2Contacts, null, null, null, null);
                        // 如果该联系人存在
                        if (cursorCantacts.getCount() > 0) {
                            // 移动到第一条数据     　　　　　
                            cursorCantacts.moveToFirst();
                            // 获得该联系人的contact_id     　　　　　
                            Long contactID = cursorCantacts.getLong(cursorCantacts.getColumnIndex("contact_id"));
                            // 获得contact_id的Uri     　　　　
                            Uri uri1 = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactID);
                            // 打开头像图片的InputStream     　　　　
                            InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(getContentResolver(), uri1);
                            // 从InputStream获得bitmap     　　　
                            pic = BitmapFactory.decodeStream(input);
                        }


                        concatCursor.close();
                        messageBean._id = _id;
                        messageBean.address = address;
                        messageBean.body = body;
                        messageBean.person = person;
                        messageBean.type = type;
                        messageBean.date = formatTime;
                        messageBean.name = phoneName;
                        messageBean.read = read;
                        messageBean.pic = pic;

                        if (messageAddress.contains(address)) {
                        } else {
                            messageAddress = messageAddress + address;
                            messageData.add(messageBean);
                        }

                        //为假时将跳出循环，即 Cursor 数据循环完毕
                    }
                    cursor.close();
                }
            }
        }.start();
    }

    private void getMessageDataCopy() {
        //        messageData = new ArrayList<>();

        new Thread() {
            @Override
            public void run() {
                Cursor cursor = getContentResolver().query(Uri.parse("content://sms/conversations/"), projection, null, null, " date desc");
                //清理数据,防止重复显示
                //返回游标是否指
                // 向第最后一行的位置
                while (cursor.moveToNext()) {
                    Bitmap pic = null;
                    MessageBean messageBean = new MessageBean();

                    String address = cursor.getString(3);
                    String _id = cursor.getString(0);
                    String body = cursor.getString(1);
                    String date = cursor.getString(4);
                    String read = cursor.getString(5);
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(Long.parseLong(date + ""));
                    SimpleDateFormat format = new SimpleDateFormat("MM/dd HH:mm:ss");
                    String formatTime = format.format(calendar.getTime());
                    String phoneName = null;
                    Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(address));
                    Cursor concatCursor = getContentResolver().query(uri, contentProjection, null, null, null);
                    if (concatCursor.moveToFirst()) {
                        phoneName = concatCursor.getString(1);
                    }

                    // 获得Uri
                    //        Uri uriNumber2Contacts = Uri.parse("content://com.android.contacts/" + "data/phones/filter/" + number);
                    Uri uriNumber2Contacts = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(address));
                    // 查询Uri，返回数据集
                    Cursor cursorCantacts = getContentResolver().query(uriNumber2Contacts, null, null, null, null);
                    // 如果该联系人存在
                    if (cursorCantacts.getCount() > 0) {
                        // 移动到第一条数据     　　　　　
                        cursorCantacts.moveToFirst();
                        // 获得该联系人的contact_id     　　　　　
                        Long contactID = cursorCantacts.getLong(cursorCantacts.getColumnIndex("contact_id"));
                        // 获得contact_id的Uri     　　　　
                        Uri uri1 = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactID);
                        // 打开头像图片的InputStream     　　　　
                        InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(getContentResolver(), uri1);
                        // 从InputStream获得bitmap     　　　
                        pic = BitmapFactory.decodeStream(input);
                        cursorCantacts.close();
                    }


                    concatCursor.close();
                    messageBean._id = _id;
                    messageBean.address = address;
                    messageBean.body = body;
                    messageBean.date = formatTime;
                    messageBean.name = phoneName;
                    messageBean.read = read;
                    messageBean.pic = pic;

                    messageData.add(messageBean);

                    //为假时将跳出循环，即 Cursor 数据循环完毕
                }
                cursor.close();
            }
        }.start();

    }
}
