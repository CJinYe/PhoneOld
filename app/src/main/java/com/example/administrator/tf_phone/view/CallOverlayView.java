package com.example.administrator.tf_phone.view;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.android.internal.telephony.ITelephony;
import com.example.administrator.tf_phone.MainActivity;
import com.example.administrator.tf_phone.R;
import com.example.administrator.tf_phone.activity.TelephoneActivity;
import com.example.administrator.tf_phone.adapter.ContactsListAdapter;
import com.example.administrator.tf_phone.bean.ContactsModelBean;
import com.example.administrator.tf_phone.conf.Constants;
import com.example.administrator.tf_phone.sqldb.MyAppSqlite;
import com.example.administrator.tf_phone.utils.PhoneUtil;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.NOTIFICATION_SERVICE;
import static android.content.Context.TELEPHONY_SERVICE;
import static com.example.administrator.tf_phone.view.tool.MyAppApiConfig.PHONES_CONTACT_ID_INDEX;
import static com.example.administrator.tf_phone.view.tool.MyAppApiConfig.PHONES_DISPLAY_NAME_INDEX;
import static com.example.administrator.tf_phone.view.tool.MyAppApiConfig.PHONES_NUMBER_INDEX;
import static com.example.administrator.tf_phone.view.tool.MyAppApiConfig.PHONES_PHOTO_ID_INDEX;
import static com.example.administrator.tf_phone.view.tool.MyAppApiConfig.PHONES_PROJECTION;

/**
 * @author Administrator
 * @version $Rev$
 * @time 2017-1-16 13:50
 * @des ${TODO}
 * @updateAuthor $Author$
 * @updateDate $Date$
 * @updateDes ${TODO}
 */
public class CallOverlayView extends Overlay {


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

    private static String phoneName;
    private static String phoneNumber;
    private static Bitmap mBmp_pic;
    private static NotificationManager mNotificationManager;
    private static CircleImageView mOverTelephoneIvAnswerPhone;
    private static TextView mOverTelephoneTvAnswerNumber;
    private static Button mTelephoneBtnMenuHandsFree;
    private static Button mTelephoneBtnMenuCollect;
    private static Button mTelephoneBtnMenuEndCall;
    private static ListView mTelephoneLvContacts;
    private static List<ContactsModelBean> mContacts;

    /**
     * 电话接听广播接收器
     */
    //    private BroadcastReceiver mAnswerCallReceiver = new BroadcastReceiver() {
    //        @Override
    //        public void onReceive(Context context, Intent intent) {
    //            if (intent != null && intent.getAction().equals(TelephonyManager.CALL_STATE_OFFHOOK)) {
    //                Intent intentAnswer = new Intent(context, TelephoneActivity.class);
    //                intentAnswer.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    //                intentAnswer.putExtra(Constants.TELEPHONE_NUMBER, number);
    //                context.startActivity(intent);
    //            }
    //        }
    //    };

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
            init(context, number, R.layout.call_over_telephone);
            InputMethodManager imm = (InputMethodManager) context
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
//            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,
//                    InputMethodManager.HIDE_IMPLICIT_ONLY);
//            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
//            imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);

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
        getContacts();
        ViewGroup overlay = init(context, layout, params);
        initView(overlay, number);
        initCollect(number);

        return overlay;
    }

    /**
     * 初始化界面
     */
    private static void initView(View view, String phoneNum) {

        getContacts(mContext, phoneNum);
        // 初始化各个控件
        mOverTelephoneIvAnswerPhone = (CircleImageView) view.findViewById(R.id.over_telephone_iv_answer_phone);
        mOverTelephoneTvAnswerNumber = (TextView) view.findViewById(R.id.over_telephone_tv_answer_number);
        mTelephoneBtnMenuHandsFree = (Button) view.findViewById(R.id.telephone_btn_menu_hands_free);
        mTelephoneBtnMenuCollect = (Button) view.findViewById(R.id.telephone_btn_menu_collect);
        mTelephoneBtnMenuEndCall = (Button) view.findViewById(R.id.telephone_btn_menu_end_call);
        mTelephoneLvContacts = (ListView) view.findViewById(R.id.over_telephone_lv_answer_contacts);
        //设置免提的图标

        AudioManager mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        //设置免提的图标
        if (mAudioManager.isSpeakerphoneOn()) {
            mTelephoneBtnMenuHandsFree.setBackgroundResource(R.drawable.phone_answer_hands_free_selector);
        } else {
            mTelephoneBtnMenuHandsFree.setBackgroundResource(R.drawable.phone_answer_hands_free_normal);
        }

        ContactsListAdapter mAdapter = new ContactsListAdapter(mContext, mContacts);
        mTelephoneLvContacts.setAdapter(mAdapter);
        mOverTelephoneIvAnswerPhone.setImageResource(R.drawable.icon);

        mTelephoneLvContacts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                return;
            }
        });

        mTelephoneBtnMenuEndCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PhoneUtil.endCall(mContext);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        hide(mContext);
                    }
                }, 1300);
            }
        });


        if (phoneName == null) {
            mOverTelephoneTvAnswerNumber.setText(phoneNumber);
        } else {
            mOverTelephoneTvAnswerNumber.setText(phoneName);
        }
        if (mBmp_pic == null) {
            mOverTelephoneIvAnswerPhone.setImageResource(R.drawable.icon);
        } else {
            mOverTelephoneIvAnswerPhone.setImageBitmap(mBmp_pic);
        }

        //        initNotification(phoneNum);

        //        addListener();
    }

    private static void getContacts( Context context,String number) {
        phoneName = null;
        mBmp_pic = null;

        // 获得Uri
        Uri uriNumber2Contacts = Uri.parse("content://com.android.contacts/" + "data/phones/filter/" + number);
//        Uri uriNumber2Contacts = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
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
            InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(mContext.getContentResolver(), uri);
            // 从InputStream获得bitmap     　　　
            mBmp_pic = BitmapFactory.decodeStream(input);


        }



    }

    private static void initCollect(String phoneNumber) {

        SQLiteDatabase dbl = MyAppSqlite.db.getReadableDatabase();
        String sql = "select * from collect where phone=?";
        Cursor cursor = dbl.query("collect", null, "phone=?", new String[]{phoneNumber}, null, null, null);
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            mTelephoneBtnMenuCollect.setBackgroundResource(R.drawable.phone_answer_collect_selector);
        } else {
            mTelephoneBtnMenuCollect.setBackgroundResource(R.drawable.phone_answer_collect_normal);
        }


        if (cursor != null) {
            cursor.close();
            dbl.close();
        }
    }

    private static void getContacts() {
        new Thread() {
            public void run() {
                mContacts = new ArrayList<>();
                ContentResolver resolver = mContext.getContentResolver();
                //查询联系人数据，query的参数Phone.SORT_KEY_PRIMARY表示将结果集按Phone.SORT_KEY_PRIMARY排序
                Cursor cursor = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        PHONES_PROJECTION, null, null, ContactsContract.CommonDataKinds.Phone.SORT_KEY_PRIMARY);
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        ContactsModelBean model = new ContactsModelBean();
                        model.setPhone(cursor.getString(PHONES_NUMBER_INDEX));
                        Long contactid = cursor.getLong(PHONES_CONTACT_ID_INDEX);
                        Long photoid = cursor.getLong(PHONES_PHOTO_ID_INDEX);
                        Bitmap contactPhoto = null;
                        //photoid 大于0 表示联系人有头像 如果没有给此人设置头像则给他一个默认的
                        if (photoid > 0) {
                            Uri uri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactid);
                            InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(resolver, uri);
                            contactPhoto = BitmapFactory.decodeStream(input);
                        } else {
                            contactPhoto = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.icon);
                        }
                        if (TextUtils.isEmpty(model.getPhone())) {
                            continue;
                        }
                        model.setPhoneBitmap(contactPhoto);
                        model.setName(cursor.getString(PHONES_DISPLAY_NAME_INDEX));
                        mContacts.add(model);
                    }
                    cursor.close();
                }


            }
        }.start();
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
            }
        });
        mAnswerCallBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mNotificationManager != null) {
                    mNotificationManager.cancelAll();
                }
                //PhoneUtil.answerCall(mContext);
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
                        Intent intent = new Intent("android.intent.action.MEDIA_BUTTON");
                        KeyEvent keyEvent = new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_HEADSETHOOK);
                        intent.putExtra("android.intent.extra.KEY_EVENT", keyEvent);

                        mContext.sendOrderedBroadcast(intent, "android.permission.CALL_PRIVILEGED");
                    } catch (Exception e2) {
                        Log.d("Sandy", "", e2);
                        Intent meidaButtonIntent = new Intent(Intent.ACTION_MEDIA_BUTTON);
                        KeyEvent keyEvent = new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_HEADSETHOOK);
                        meidaButtonIntent.putExtra(Intent.EXTRA_KEY_EVENT, keyEvent);
                        mContext.sendOrderedBroadcast(meidaButtonIntent, null);
                    }
                }

                Intent intent = new Intent(mContext, TelephoneActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(Constants.TELEPHONE_NUMBER, phoneNumber);
                intent.putExtra(Constants.TELEPHONE_STATE, Constants.ACTION_ANSWER_CALL);
                mContext.startActivity(intent);


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
     * 获取界面显示的高度 ，默认为手机高度的2/3
     *
     * @param context 上下文对象
     * @return
     */
    //    private static int getHeight(Context context, int percentScreen) {
    //        return getLarger(context) * percentScreen / 100;
    //    }
    //
    //    @SuppressWarnings("deprecation")
    //    private static int getLarger(Context context) {
    //        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
    //        Display display = wm.getDefaultDisplay();
    //        int height = 0;
    //        if (Utils.hasHoneycombMR2()) {
    //            height = getLarger(display);
    //        } else {
    //            height = display.getHeight() > display.getWidth() ? display.getHeight() : display
    //                    .getWidth();
    //        }
    //        System.out.println("getLarger: " + height);
    //        return height;
    //    }
    //
    //    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    //    private static int getLarger(Display display) {
    //        Point size = new Point();
    //        display.getSize(size);
    //        return size.y > size.x ? size.y : size.x;
    //    }
}
