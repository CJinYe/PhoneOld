package com.example.administrator.tf_phone;

import android.Manifest;
import android.app.NotificationManager;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.android.internal.telephony.ITelephony;
import com.example.administrator.tf_phone.adapter.PhoneCallInfoAdapter;
import com.example.administrator.tf_phone.bean.CallLogBean;
import com.example.administrator.tf_phone.conf.Constants;
import com.example.administrator.tf_phone.view.BaseActivity;
import com.example.administrator.tf_phone.view.tool.CallPhone;
import com.example.administrator.tf_phone.view.tool.HasSimCard;
import com.example.administrator.tf_phone.view.tool.PlayMusic;
import com.example.administrator.tf_phone.view.tool.ToastUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import kr.co.namee.permissiongen.PermissionGen;

import static com.example.administrator.tf_phone.view.tool.PlayMusic.mSoundPool;
import static com.example.administrator.tf_phone.view.tool.PlayMusic.soundID;

public class MainActivity extends BaseActivity implements View.OnClickListener, View.OnLongClickListener {

    private EditText phone_text;
    private Button btn_one, btn_two, btn_three, btn_four, btn_five,
            btn_six, btn_seven, btn_eight, btn_nine, btn_zero, btn_jin, btn_xin;
    private ImageButton delete_image;
    private Button call_phone, main_new_btn, contacts_btn, favorite_btn, exitall_app, message_btn;
    private ListView list_call_records;
    private AsyncQueryHandler asyncQuery;
    private PhoneCallInfoAdapter adapter;
    private List<CallLogBean> callLogs = new ArrayList<CallLogBean>();
    public static MainActivity main ;
    private String str0, str1, str2, str3, str4, str5, str6, str7, str8, str9, strjin, strxin;

//        @Override
//        protected void onCreate(Bundle savedInstanceState) {
//            super.onCreate(savedInstanceState);
//            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
//            requestWindowFeature(Window.FEATURE_NO_TITLE);
//            setContentView(R.layout.activity_main);
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    Constants.isOnClickItemOutCall = false;
//                }
//            }, 2000);
//            main = this;
//            initPermissions();
//            initView();
//            initShow();
//            getDatas();
//            PlayMusic.playmusic(this);
//            Constants.isMainAddContacts = false;
//            //        getContentResolver().delete(Uri.parse("content://sms"), "address="+"1008611", null);
//        }

    protected void initContentView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Constants.isOnClickItemOutCall = false;
            }
        }, 2000);

        main = this;
        initPermissions();
        initView();
        initShow();
        getDatas();
        PlayMusic.playmusic(this);
        Constants.isMainAddContacts = false;
    }


    /**
     * 申请权限
     */
    private void initPermissions() {
        //        申请默认短信
        String myPackageName = getPackageName();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            String smsPackName = Telephony.Sms.getDefaultSmsPackage(this);
            if (smsPackName != null) {
                if (!smsPackName.equals(myPackageName)) {
                    Intent intent =
                            new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
                    intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME,
                            myPackageName);
                    startActivity(intent);
                }
            }
        }

        if (Build.VERSION.SDK_INT >= 22) {

            String[] permissions = new String[]{
                    Manifest.permission.READ_CONTACTS,
                    Manifest.permission.WRITE_CONTACTS,
                    Manifest.permission.CALL_PHONE,
                    Manifest.permission.WAKE_LOCK,
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_SMS,
                    Manifest.permission.RECEIVE_SMS,
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_CALL_LOG,
                    Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS,
                    Manifest.permission.SEND_SMS,
                    Manifest.permission.MODIFY_PHONE_STATE,
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.SYSTEM_ALERT_WINDOW,
                    Manifest.permission.DISABLE_KEYGUARD,
                    Manifest.permission.BROADCAST_SMS

            };
            PermissionGen.needPermission(this, 100, permissions);


        }

    }

    private void initView() {

        //        callLogs = new ArrayList<CallLogBean>();

        phone_text = (EditText) this.findViewById(R.id.phone_text);
        delete_image = (ImageButton) this.findViewById(R.id.delete_image);
        btn_one = (Button) this.findViewById(R.id.btn_one);
        btn_two = (Button) this.findViewById(R.id.btn_two);
        btn_three = (Button) this.findViewById(R.id.btn_three);
        btn_four = (Button) this.findViewById(R.id.btn_four);
        btn_five = (Button) this.findViewById(R.id.btn_five);
        btn_six = (Button) this.findViewById(R.id.btn_six);
        btn_seven = (Button) this.findViewById(R.id.btn_seven);
        btn_eight = (Button) this.findViewById(R.id.btn_eight);
        btn_nine = (Button) this.findViewById(R.id.btn_nine);
        btn_zero = (Button) this.findViewById(R.id.btn_zero);
        btn_jin = (Button) this.findViewById(R.id.btn_jin);
        btn_xin = (Button) this.findViewById(R.id.btn_xin);
        call_phone = (Button) this.findViewById(R.id.call_phone);
        contacts_btn = (Button) this.findViewById(R.id.contacts_btn);
        main_new_btn = (Button) this.findViewById(R.id.main_new_btn);
        favorite_btn = (Button) this.findViewById(R.id.favorite_btn);
        exitall_app = (Button) this.findViewById(R.id.exitall_app);
        message_btn = (Button) this.findViewById(R.id.message_btn);
        list_call_records = (ListView) this.findViewById(R.id.list_call_records);
        setAdapter(callLogs);
    }

    private void initShow() {
        btn_one.setOnClickListener(this);
        btn_two.setOnClickListener(this);
        btn_three.setOnClickListener(this);
        btn_four.setOnClickListener(this);
        btn_five.setOnClickListener(this);
        btn_six.setOnClickListener(this);
        btn_seven.setOnClickListener(this);
        btn_eight.setOnClickListener(this);
        btn_nine.setOnClickListener(this);
        btn_zero.setOnClickListener(this);
        btn_jin.setOnClickListener(this);
        btn_xin.setOnClickListener(this);
        delete_image.setOnClickListener(this);
        delete_image.setOnLongClickListener(this);
        call_phone.setOnClickListener(this);
        contacts_btn.setOnClickListener(this);
        main_new_btn.setOnClickListener(this);
        favorite_btn.setOnClickListener(this);
        exitall_app.setOnClickListener(this);
        message_btn.setOnClickListener(this);
        list_call_records.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                return;
            }
        });
        asyncQuery = new MyAsyncQueryHandler(getContentResolver());
    }

    private void getNumberPhone() {
        str0 = phone_text.getText().toString().trim();
        str1 = phone_text.getText().toString().trim();
        str2 = phone_text.getText().toString().trim();
        str3 = phone_text.getText().toString().trim();
        str4 = phone_text.getText().toString().trim();
        str5 = phone_text.getText().toString().trim();
        str6 = phone_text.getText().toString().trim();
        str7 = phone_text.getText().toString().trim();
        str8 = phone_text.getText().toString().trim();
        str9 = phone_text.getText().toString().trim();
        strjin = phone_text.getText().toString().trim();
        strxin = phone_text.getText().toString().trim();
    }

    private void getPhoneEdit() {
        str1 += "1";
        str2 += "2";
        str3 += "3";
        str4 += "4";
        str5 += "5";
        str6 += "6";
        str7 += "7";
        str8 += "8";
        str9 += "9";
        str0 += "0";
        strjin += "#";
        strxin += "*";
    }


    public void onClick(View v) {
        AudioManager mgr = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        float streamVolumeCurrent = mgr.getStreamVolume(AudioManager.STREAM_MUSIC);
        float streamVolumeMax = mgr.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        float volume = streamVolumeCurrent / streamVolumeMax;
        getNumberPhone();
        getPhoneEdit();
        switch (v.getId()) {
            case R.id.btn_one:
                phone_text.setText(str1);
                mSoundPool.play(soundID.get(1), 0.3f, 0.3f, 0, 0, 1);
                break;
            case R.id.btn_two:
                phone_text.setText(str2);
                mSoundPool.play(soundID.get(2), 0.3f, 0.3f, 0, 0, 1);
                break;
            case R.id.btn_three:
                phone_text.setText(str3);
                mSoundPool.play(soundID.get(3), 0.3f, 0.3f, 0, 0, 1);
                break;
            case R.id.btn_four:
                phone_text.setText(str4);
                mSoundPool.play(soundID.get(4), 0.3f, 0.3f, 0, 0, 1);
                break;
            case R.id.btn_five:
                phone_text.setText(str5);
                mSoundPool.play(soundID.get(5), 0.3f, 0.3f, 0, 0, 1);
                break;
            case R.id.btn_six:
                phone_text.setText(str6);
                mSoundPool.play(soundID.get(6), 0.3f, 0.3f, 0, 0, 1);
                break;
            case R.id.btn_seven:
                phone_text.setText(str7);
                mSoundPool.play(soundID.get(7), 0.3f, 0.3f, 0, 0, 1);
                break;
            case R.id.btn_eight:
                phone_text.setText(str8);
                mSoundPool.play(soundID.get(8), 0.3f, 0.3f, 0, 0, 1);
                break;
            case R.id.btn_nine:
                phone_text.setText(str9);
                mSoundPool.play(soundID.get(9), 0.3f, 0.3f, 0, 0, 1);
                break;
            case R.id.btn_zero:
                phone_text.setText(str0);
                mSoundPool.play(soundID.get(0), 0.3f, 0.3f, 0, 0, 1);
                break;
            case R.id.btn_jin:
                phone_text.setText(strjin);
                mSoundPool.play(soundID.get(10), 0.3f, 0.3f, 0, 0, 1);
                break;
            case R.id.btn_xin:
                phone_text.setText(strxin);
                mSoundPool.play(soundID.get(11), 0.3f, 0.3f, 0, 0, 1);
                break;
            case R.id.delete_image:
                String phoneStr = phone_text.getText().toString().trim();
                if (TextUtils.isEmpty(phoneStr)) {
                } else {
                    phone_text.setText(phoneStr.substring(0, phoneStr.length() - 1));
                }
                break;
            case R.id.call_phone:
                String phone = phone_text.getText().toString().trim();
                if (TextUtils.isEmpty(phone)) {
                    ToastUtil.showPicToast(MainActivity.this, "未输入号码");
                    return;
                }
                HasSimCard.hasSimCard(this);
                TelephonyManager mTelephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                if (mTelephonyManager.getSimState() != TelephonyManager.SIM_STATE_READY){
                    return;
                }
                CallPhone.callphone(this, phone);
                phone_text.setText("");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                }, 2000);
                break;
            case R.id.contacts_btn:
                startActivity(new Intent(MainActivity.this, ContactsActivity.class));
                finish();
                break;
            case R.id.main_new_btn:
                String phonetext = phone_text.getText().toString().trim();
                Intent intent = new Intent(MainActivity.this, AddContactsActivity.class);
                intent.putExtra("phonetext", phonetext);
                phone_text.setText("");
                Constants.isMainAddContacts = true;
                startActivity(intent);
                finish();
                break;
            case R.id.favorite_btn:
                startActivity(new Intent(MainActivity.this, FavoriteActivity.class));
                finish();
                break;
            case R.id.exitall_app:
                MainActivity.this.finish();
                Intent mIntent = new Intent();
                mIntent.setAction(Intent.ACTION_MAIN);
                mIntent.addCategory(Intent.CATEGORY_HOME);
                startActivity(mIntent);
                break;
            case R.id.message_btn:
                startActivity(new Intent(MainActivity.this, MessageActivity.class));
                finish();
                break;
            default:
                break;
        }
    }

    /**
     * 删除按钮长按事件
     */
    public boolean onLongClick(View v) {
        phone_text.setText("");
        return true;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    private void getDatas() {
        Uri uri = CallLog.Calls.CONTENT_URI;
        // 查询的列
        String[] projection = {CallLog.Calls.DATE, // 日期
                CallLog.Calls.NUMBER, // 号码
                CallLog.Calls.TYPE, // 类型
                CallLog.Calls.CACHED_NAME, // 名字
                CallLog.Calls._ID, // id
        };
        asyncQuery.startQuery(0, null, uri, projection, null, null,
                CallLog.Calls.DEFAULT_SORT_ORDER);
    }

    /**
     * 查询通话记录
     */
    private class MyAsyncQueryHandler extends AsyncQueryHandler {


        private String mPhoneName;

        public MyAsyncQueryHandler(ContentResolver cr) {
            super(cr);
        }

        protected void onQueryComplete(int token, Object cookie, Cursor cursor) {

            if (cursor != null && cursor.getCount() > 0) {
                SimpleDateFormat sfd = new SimpleDateFormat("MM/dd hh:mm");
                Date date;
                cursor.moveToFirst(); // 游标移动到第一项
                for (int i = 0; i < cursor.getCount(); i++) {
                    if (i <= 100) {

                        mPhoneName = "";
                        String cachedName = "";
                        cursor.moveToPosition(i);
                        date = new Date(cursor.getLong(cursor.getColumnIndex(CallLog.Calls.DATE)));
                        String number = cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER));
                        int type = cursor.getInt(cursor.getColumnIndex(CallLog.Calls.TYPE));
                        cachedName = cursor.getString(cursor.getColumnIndex(CallLog.Calls.CACHED_NAME));// 缓存的名称与电话号码
                        int id = cursor.getInt(cursor.getColumnIndex(CallLog.Calls._ID));
                        Uri uriNumber2Contacts = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
                        Cursor cursorCantacts = getContentResolver().query(uriNumber2Contacts, null, null, null, null);
                        if (cursorCantacts.getCount() > 0) {
                            // 移动到第一条数据     　　　　　
                            cursorCantacts.moveToFirst();
                            int nameFieldColumnIndex = cursorCantacts
                                    .getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME);
                            mPhoneName = cursorCantacts.getString(nameFieldColumnIndex);
                            cursorCantacts.close();
                        }
                        cursorCantacts.close();

                        CallLogBean callLogBean = new CallLogBean();

                        //                        if (TextUtils.isEmpty(cachedName)) {
                        //                            if (TextUtils.isEmpty(mPhoneName)) {
                        //                                cachedName = number;
                        //                            } else {
                        //                                cachedName = mPhoneName;
                        //                            }
                        //                        }

                        if (TextUtils.isEmpty(mPhoneName)) {
                            mPhoneName = number;
                            callLogBean.isConstants = false;
                        }else {
                            callLogBean.isConstants = true;
                        }


                        callLogBean.setId(id);
                        callLogBean.setName(mPhoneName);
                        callLogBean.setNumber(number);
                        callLogBean.setType(type);
                        callLogBean.setDate(sfd.format(date));
                        callLogs.add(callLogBean);
                    }
                }
                if (callLogs.size() > 0) {
                    Log.d("initContentView"," "+callLogs.size());

//                    setAdapter(callLogs);
                }


                cursor.close();
            }
            super.onQueryComplete(token, cookie, cursor);
        }
    }

    private void setAdapter(List<CallLogBean> callLogs) {
        adapter = new PhoneCallInfoAdapter(this, callLogs);
        list_call_records.setAdapter(adapter);
    }

    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            Intent mIntent = new Intent();
            mIntent.setAction(Intent.ACTION_MAIN);
            mIntent.addCategory(Intent.CATEGORY_HOME);
            startActivity(mIntent);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onStart() {

//        if (adapter != null) {
//            adapter.notifyDataSetChanged();
//        }
        //        getDatas();
        NotificationManager NotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        NotificationManager.cancelAll();
        super.onStart();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
    }

    private void cancelNotification() {

        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        Class<TelephonyManager> c = TelephonyManager.class;
        Method getITelephonyMethod = null;
        try {
            getITelephonyMethod = c.getDeclaredMethod("getITelephony", (Class[]) null);
            getITelephonyMethod.setAccessible(true);
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        try {
            //ITelephony iTelephony = ITelephony.Stub.asInterface(ServiceManager.getService("phone"));
            ITelephony iTelephony = (ITelephony) getITelephonyMethod.invoke(tm, (Object[]) null);
            if (iTelephony != null) {
                iTelephony.cancelMissedCallsNotification();//删除未接来电通知
            } else {
                Log.w("CallLogAdapter", "Telephony service is null, can't call " +
                        "cancelMissedCallsNotification");
            }
        } catch (RemoteException e) {
            Log.e("CallLogAdapter", "Failed to clear missed calls notification due to remote exception");
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}

