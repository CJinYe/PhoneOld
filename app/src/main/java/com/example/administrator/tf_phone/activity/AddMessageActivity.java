package com.example.administrator.tf_phone.activity;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.administrator.tf_phone.MessageActivity;
import com.example.administrator.tf_phone.R;
import com.example.administrator.tf_phone.adapter.ContactsListAdapter;
import com.example.administrator.tf_phone.bean.ContactsModelBean;
import com.example.administrator.tf_phone.view.BaseActivity;
import com.example.administrator.tf_phone.view.JsonParser;
import com.example.administrator.tf_phone.view.tool.HasSimCard;
import com.example.administrator.tf_phone.view.tool.PlayMusic;
import com.example.administrator.tf_phone.view.tool.ToastUtil;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

import static com.example.administrator.tf_phone.R.id.message_add_delete_image;
import static com.example.administrator.tf_phone.view.tool.MyAppApiConfig.PHONES_CONTACT_ID_INDEX;
import static com.example.administrator.tf_phone.view.tool.MyAppApiConfig.PHONES_DISPLAY_NAME_INDEX;
import static com.example.administrator.tf_phone.view.tool.MyAppApiConfig.PHONES_NUMBER_INDEX;
import static com.example.administrator.tf_phone.view.tool.MyAppApiConfig.PHONES_PHOTO_ID_INDEX;
import static com.example.administrator.tf_phone.view.tool.MyAppApiConfig.PHONES_PROJECTION;
import static com.example.administrator.tf_phone.view.tool.MyAppApiConfig.SDKID;
import static com.example.administrator.tf_phone.view.tool.PlayMusic.mSoundPool;
import static com.example.administrator.tf_phone.view.tool.PlayMusic.soundID;
import static com.iflytek.cloud.SpeechConstant.APPID;

/**
 * @author Administrator
 * @version $Rev$
 * @time 2017-2-6 13:56
 * @des ${TODO}
 * @updateAuthor $Author$
 * @updateDate $Date$
 * @updateDes ${TODO}
 */
public class AddMessageActivity extends BaseActivity implements View.OnClickListener {
    @InjectView(R.id.message_add_back)
    ImageButton mMessageAddBack;
    @InjectView(R.id.message_cancle)
    ImageButton mMessageCancle;
    @InjectView(R.id.message_add_phone_top)
    TextView mMessageAddPhoneTop;
    @InjectView(R.id.message_add_contacts)
    ImageButton mMessageAddContacts;
    @InjectView(R.id.message_add_phone_text)
    EditText mMessageAddPhoneText;
    @InjectView(message_add_delete_image)
    ImageButton mMessageAddDeleteImage;
    @InjectView(R.id.btn_one)
    Button mBtnOne;
    @InjectView(R.id.btn_two)
    Button mBtnTwo;
    @InjectView(R.id.btn_three)
    Button mBtnThree;
    @InjectView(R.id.btn_four)
    Button mBtnFour;
    @InjectView(R.id.btn_five)
    Button mBtnFive;
    @InjectView(R.id.btn_six)
    Button mBtnSix;
    @InjectView(R.id.btn_seven)
    Button mBtnSeven;
    @InjectView(R.id.btn_eight)
    Button mBtnEight;
    @InjectView(R.id.btn_nine)
    Button mBtnNine;
    @InjectView(R.id.btn_jin)
    Button mBtnJin;
    @InjectView(R.id.btn_zero)
    Button mBtnZero;
    @InjectView(R.id.btn_xin)
    Button mBtnXin;
    @InjectView(R.id.message_add_ll_keyboard)
    LinearLayout mMessageAddLlKeyboard;
    @InjectView(R.id.message_add_lv_contacts)
    ListView mMessageAddLvContacts;
    @InjectView(R.id.message_add_edt_message)
    EditText mMessageAddEdtMessage;
    @InjectView(R.id.message_add_voice)
    Button mMessageAddVoice;
    @InjectView(R.id.message_add_send)
    Button mMessageAddSend;
    private String str0, str1, str2, str3, str4, str5, str6, str7, str8, str9, strjin, strxin;
    private AddMessageActivity.MySendBr sendBr = new AddMessageActivity.MySendBr();
    private AddMessageActivity.MyDeliverBr deliverBr = new AddMessageActivity.MyDeliverBr();
    private List<ContactsModelBean> list = new ArrayList<>();
    private List<ContactsModelBean> contacts = new ArrayList<>();
    private ContactsListAdapter mAdapter;

    @Override
    protected void initContentView(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_add_message);
        ButterKnife.inject(this);
        String number = getIntent().getStringExtra("number");
        mMessageAddPhoneText.setText(number);
        RegsterBrodCast();
        initView();
        initData();
        initListener();
        PlayMusic.playmusic(this);
        getContacts();
    }

    private void initData() {

    }

    private void initView() {

    }

    private void initListener() {
        mMessageAddBack.setOnClickListener(this);
        mMessageCancle.setOnClickListener(this);
        mMessageAddPhoneTop.setOnClickListener(this);
        mMessageAddContacts.setOnClickListener(this);
        mMessageAddPhoneText.setOnClickListener(this);
        mMessageAddDeleteImage.setOnClickListener(this);
        mBtnOne.setOnClickListener(this);
        mBtnTwo.setOnClickListener(this);
        mBtnThree.setOnClickListener(this);
        mBtnFour.setOnClickListener(this);
        mBtnFive.setOnClickListener(this);
        mBtnSix.setOnClickListener(this);
        mBtnSeven.setOnClickListener(this);
        mBtnEight.setOnClickListener(this);
        mBtnNine.setOnClickListener(this);
        mBtnJin.setOnClickListener(this);
        mBtnZero.setOnClickListener(this);
        mBtnXin.setOnClickListener(this);
        mMessageAddVoice.setOnClickListener(this);
        mMessageAddSend.setOnClickListener(this);
        mMessageAddLvContacts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ContactsModelBean bean =  contacts.get(position);
                mMessageAddPhoneText.setText(bean.getPhone());
                mMessageAddLvContacts.setVisibility(View.GONE);
                mMessageAddLlKeyboard.setVisibility(View.VISIBLE);
            }
        });

        mMessageAddDeleteImage.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mMessageAddPhoneText.setText("");
                return true;
            }
        });
    }

    @Override
    public void onClick(View v) {
        getNumberPhone();
        getPhoneEdit();
        switch (v.getId()){
            case R.id.btn_one:
                mMessageAddPhoneText.setText(str1);
                mSoundPool.play(soundID.get(1), 0.3f, 0.3f, 0, 0, 1);
                break;
            case R.id.btn_two:
                mMessageAddPhoneText.setText(str2);
                mSoundPool.play(soundID.get(2), 0.3f, 0.3f, 0, 0, 1);
                break;
            case R.id.btn_three:
                mMessageAddPhoneText.setText(str3);
                mSoundPool.play(soundID.get(3), 0.3f, 0.3f, 0, 0, 1);
                break;
            case R.id.btn_four:
                mMessageAddPhoneText.setText(str4);
                mSoundPool.play(soundID.get(4), 0.3f, 0.3f, 0, 0, 1);
                break;
            case R.id.btn_five:
                mMessageAddPhoneText.setText(str5);
                mSoundPool.play(soundID.get(5), 0.3f, 0.3f, 0, 0, 1);
                break;
            case R.id.btn_six:
                mMessageAddPhoneText.setText(str6);
                mSoundPool.play(soundID.get(6), 0.3f, 0.3f, 0, 0, 1);
                break;
            case R.id.btn_seven:
                mMessageAddPhoneText.setText(str7);
                mSoundPool.play(soundID.get(7), 0.3f, 0.3f, 0, 0, 1);
                break;
            case R.id.btn_eight:
                mMessageAddPhoneText.setText(str8);
                mSoundPool.play(soundID.get(8), 0.3f, 0.3f, 0, 0, 1);
                break;
            case R.id.btn_nine:
                mMessageAddPhoneText.setText(str9);
                mSoundPool.play(soundID.get(9), 0.3f, 0.3f, 0, 0, 1);
                break;
            case R.id.btn_zero:
                mMessageAddPhoneText.setText(str0);
                mSoundPool.play(soundID.get(0), 0.3f, 0.3f, 0, 0, 1);
                break;
            case R.id.btn_jin:
                mMessageAddPhoneText.setText(strjin);
                mSoundPool.play(soundID.get(10), 0.3f, 0.3f, 0, 0, 1);
                break;
            case R.id.btn_xin:
                mMessageAddPhoneText.setText(strxin);
                mSoundPool.play(soundID.get(11), 0.3f, 0.3f, 0, 0, 1);
                break;
            case R.id.message_add_send:
                HasSimCard.hasSimCard(this);
                SendMessage();
                Intent intent1 = new Intent(AddMessageActivity.this, MessageActivity.class);
                startActivity(intent1);
                finish();
                break;
            case R.id.message_add_voice:
                SendVoice();
                break;
            case R.id.message_add_back:
                Intent intent = new Intent(AddMessageActivity.this, MessageActivity.class);
                startActivity(intent);
                finish();
                break;
            case message_add_delete_image:
                String phoneStr = mMessageAddPhoneText.getText().toString().trim();
                if (TextUtils.isEmpty(phoneStr)) {
                } else {
                    mMessageAddPhoneText.setText(phoneStr.substring(0, phoneStr.length() - 1));
                }
                break;
            case R.id.message_add_contacts:
                mMessageAddLvContacts.setVisibility(View.VISIBLE);
                mMessageAddLlKeyboard.setVisibility(View.GONE);

                break;
        }

    }

    private void getNumberPhone() {
        str0 = mMessageAddPhoneText.getText().toString().trim();
        str1 = mMessageAddPhoneText.getText().toString().trim();
        str2 = mMessageAddPhoneText.getText().toString().trim();
        str3 = mMessageAddPhoneText.getText().toString().trim();
        str4 = mMessageAddPhoneText.getText().toString().trim();
        str5 = mMessageAddPhoneText.getText().toString().trim();
        str6 = mMessageAddPhoneText.getText().toString().trim();
        str7 = mMessageAddPhoneText.getText().toString().trim();
        str8 = mMessageAddPhoneText.getText().toString().trim();
        str9 = mMessageAddPhoneText.getText().toString().trim();
        strjin = mMessageAddPhoneText.getText().toString().trim();
        strxin = mMessageAddPhoneText.getText().toString().trim();
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

    /**
     * 发送短信
     */
    private void SendMessage() {
        String phoneName = mMessageAddPhoneText.getText().toString().trim();
        String smsContent = mMessageAddEdtMessage.getText().toString().trim();

        if (phoneName.startsWith("1") && phoneName.length()==11){
            phoneName = "+86"+phoneName;
        }

        if (TextUtils.isEmpty(smsContent)) {
            ToastUtil.showPicToast(this, "发送内容为空！");
            return;
        }
        if (TextUtils.isEmpty(phoneName)) {
            ToastUtil.showPicToast(this, "发送对象为空！");
            return;
        }
        /**
         * 发送系统短信需要一个类-smsManager
         * 1、短信发送成功对方接受到的话，自己会受到广播
         * 2、发送成功也将会受到广播
         * 区别：deliveryIntent  对方受到短信然后对方给移动。。回馈，然后移动。。在回馈给自己
         * sentIntent 说明自己发送给移动的通讯商了
         */
        SmsManager manager = android.telephony.SmsManager.getDefault();
        Intent mySendIntent = new Intent("SENT_SMS_ACTION");
        Intent myDeliverIntent = new Intent("DELIVERED_SMS_ACTION");
        PendingIntent sentIntent = PendingIntent.getBroadcast(this, 0, mySendIntent, 0);
        PendingIntent deliveryIntent = PendingIntent.getBroadcast(this, 0, myDeliverIntent, 0);
        if (smsContent.length() > 70) {
            ArrayList<String> divideMessage = manager.divideMessage(smsContent);
            for (int i = 0; i < divideMessage.size(); i++) {
                manager.sendTextMessage(phoneName, null, divideMessage.get(i), sentIntent, deliveryIntent);
            }
        } else {
            manager.sendTextMessage(phoneName, null, smsContent, sentIntent, deliveryIntent);
        }
        //发送后清空文本内容
        mMessageAddEdtMessage.setText("");
        String smsPackName = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            smsPackName = Telephony.Sms.getDefaultSmsPackage(this);
            if (smsPackName != null) {
                if (smsPackName.equals(getPackageName())) {
                    ContentValues values = new ContentValues();
                    values.clear();
                    values.put("address", phoneName); // 发送地址
                    values.put("body", smsContent);// 消息内容
                    values.put("date", System.currentTimeMillis());// 创建时间
                    values.put("read", 1); // 0：未读； 1：已读
                    values.put("type", 2);// 1：接收； 2：发送
                    getContentResolver().insert(Uri.parse("content://sms/sent"), values);
                }
            }
        }

    }


    /**
     * 语音发送
     */
    private void SendVoice() {
        // 语音配置对象初始化
        SpeechUtility.createUtility(this, APPID + "=" + SDKID);
        // 1.创建SpeechRecognizer对象，第2个参数：本地听写时传InitListener
        SpeechRecognizer mIat = SpeechRecognizer.createRecognizer(this, null);
        // 交互动画
        RecognizerDialog iatDialog = new RecognizerDialog(this, null);
        // 2.设置听写参数，
        mIat.setParameter(SpeechConstant.DOMAIN, "iat"); // domain:域名
        mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
        mIat.setParameter(SpeechConstant.ACCENT, "mandarin"); // mandarin:普通话
        //3.开始听写
        iatDialog.setListener(new RecognizerDialogListener() {
            public void onResult(RecognizerResult results, boolean isLast) {
                // 自动生成的方法存根
                String text = JsonParser.parseIatResult(results.getResultString());
                mMessageAddEdtMessage.append(text);
            }

            public void onError(SpeechError error) {
                // 自动生成的方法存根
                error.getPlainDescription(true);
            }
        });
        // 开始听写
        iatDialog.show();
    }

    /**
     * 注册广播
     */
    private void RegsterBrodCast() {
        registerReceiver(sendBr, new IntentFilter("SENT_SMS_ACTION"));
        registerReceiver(deliverBr, new IntentFilter("DELIVERED_SMS_ACTION"));
    }

    /**
     * 广播
     */
    class MySendBr extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            //注销广播
            context.unregisterReceiver(this);
            if (getResultCode() == Activity.RESULT_OK) {
                ToastUtil.showPicToast(AddMessageActivity.this, "发送成功!");
            }
            //            if (intent != null && intent.equals("SENT_SMS_ACTION")) {
            //                ToastUtil.showToast(getApplicationContext(), "发送失败1!");
            //            }
        }
    }

    class MyDeliverBr extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            //注销广播
            context.unregisterReceiver(this);
            if (intent != null && intent.getAction().equals("DELIVERED_SMS_ACTION")) {
                ToastUtil.showPicToast(AddMessageActivity.this, "发送成功!");
            } else {
                ToastUtil.showPicToast(AddMessageActivity.this, "发送失败!");
            }
        }
    }

    private void getContacts() {
        new Thread() {
            public void run() {
                try {
                    ContentResolver resolver = getContentResolver();
                    //查询联系人数据，query的参数Phone.SORT_KEY_PRIMARY表示将结果集按Phone.SORT_KEY_PRIMARY排序
                    Cursor cursor = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            PHONES_PROJECTION, null, null, ContactsContract.CommonDataKinds.Phone.SORT_KEY_PRIMARY);
                    if (cursor != null) {
                        while (cursor.moveToNext()) {
                            ContactsModelBean model = new ContactsModelBean();
                            model.setPhone(cursor.getString(PHONES_NUMBER_INDEX).trim());
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
                            model.setId(contactid);
                            model.setPhoneBitmap(contactPhoto);
                            model.setName(cursor.getString(PHONES_DISPLAY_NAME_INDEX));
                            list.add(model);
                        }
                        cursor.close();
                    }
                    runOnUiThread(new Runnable() {
                        public void run() {
                            contacts.addAll(list);
                            mAdapter = new ContactsListAdapter(AddMessageActivity.this, contacts);
                            mMessageAddLvContacts.setAdapter(mAdapter);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
}
