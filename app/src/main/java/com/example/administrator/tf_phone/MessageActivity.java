package com.example.administrator.tf_phone;

import android.Manifest;
import android.app.Activity;
import android.app.AppOpsManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.AsyncQueryHandler;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.administrator.tf_phone.activity.AddMessageActivity;
import com.example.administrator.tf_phone.adapter.MessageAdapter;
import com.example.administrator.tf_phone.adapter.MyThreadDetailAdapter;
import com.example.administrator.tf_phone.bean.ThreadDetailBean;
import com.example.administrator.tf_phone.utils.SmsWriteOpUtil;
import com.example.administrator.tf_phone.view.JsonParser;
import com.example.administrator.tf_phone.view.tool.CallPhone;
import com.example.administrator.tf_phone.view.tool.HasSimCard;
import com.example.administrator.tf_phone.view.tool.ToastUtil;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.example.administrator.tf_phone.view.tool.MyAppApiConfig.SDKID;
import static com.example.administrator.tf_phone.view.tool.MyAppApiConfig.contactProjection;
import static com.example.administrator.tf_phone.view.tool.MyAppApiConfig.contentProjection;
import static com.example.administrator.tf_phone.view.tool.MyAppApiConfig.projection;
import static com.example.administrator.tf_phone.view.tool.MyAppApiConfig.projection_two;
import static com.example.administrator.tf_phone.view.tool.MyAppApiConfig.uri;
import static com.iflytek.cloud.SpeechConstant.APPID;

/**
 * 短信界面
 * AsyncQueryHandler 异步数据库查询 CursorAdapter
 * 就是数据库的特有的适配器 获取短信的url
 */
public class MessageActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemLongClickListener, AdapterView.OnItemClickListener {

    private TextView message_phone_top;
    private ImageButton message_back, message_line_pop, message_cancle, message_add;
    private Button message_send, messsage_voice, message_delete;
    private PopupWindow popupWindow;
    private EditText message_info_edit;
    private ListView message_info_list, message_list;
    private MessageAdapter mAdapter;
    private MyAsyncQueryHandler asyncQueryHandler;
    //接收者ID与电话
    private String thread_id, phone, constantsName;
    private MyThreadDetailAdapter mThreadDetailAdapter;
    private ArrayList<ThreadDetailBean> mDatas;
    private String phoneName;
    private MessageActivity.MySendBr sendBr = new MessageActivity.MySendBr();
    private MessageActivity.MyDeliverBr deliverBr = new MessageActivity.MyDeliverBr();
    private MessageActivity.mySmsContentObserver observer = new MessageActivity.mySmsContentObserver(new Handler());
    private LinearLayout message_checkbox, message_linear_send;
    private Button bt_selectall;
    private Button bt_cancel;
    private Button bt_deselectall;
    // 记录选中的条目数量
    private int number = 0;
    //根据item点击postion刷新数据
    private int index;
    private boolean state = false;
    private ThreadDetailBean bean;
    //用来显示删除后的短信List
    private List<ThreadDetailBean> list = new ArrayList<>();
    private List<ThreadDetailBean> messageDeleList = new ArrayList<>();
    private int id;

    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int postion, long l) {
        bean = mDatas.get(postion);
        //通过view找ID

        CheckBox checkBox = (CheckBox) view.findViewById(R.id.item_cb);
        if (state == false) {
            message_add.setVisibility(View.GONE);
            //返回键隐藏
            message_back.setVisibility(View.GONE);
            //取消键显示
            message_cancle.setVisibility(View.VISIBLE);
            //PopWindow键隐藏
            message_line_pop.setVisibility(View.GONE);
            //删除键显示
            message_delete.setVisibility(View.VISIBLE);
            //隐藏语音输入框
            message_linear_send.setVisibility(View.GONE);
            //显示全选、反选等布局
            message_checkbox.setVisibility(View.VISIBLE);
            //显示checkbox
            checkBox.setVisibility(View.VISIBLE);
            state = true;
        }
        return true;
    }

    public void onItemClick(AdapterView<?> adapterView, View view, int postion, long l) {
        bean = mDatas.get(postion);
        messageDeleList.add(bean);
        if (state == true) {
            // 改变CheckBox的状态
            CheckBox checkBox = (CheckBox) view.findViewById(R.id.item_cb);
            checkBox.setVisibility(View.VISIBLE);
            checkBox.toggle();
            mDatas.get(postion).setBo(checkBox.isChecked());
            // 调整选定条目
            if (checkBox.isChecked() == true) {
                number++;
            } else {
                number--;
            }
            message_phone_top.setText("已选中" + number + "项");
            index = postion;
        }
    }

    class mySmsContentObserver extends ContentObserver {
        public mySmsContentObserver(Handler handler) {
            super(handler);
        }

        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            RefreshAdapter();

        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_message);
        RegsterBrodCast();
        asyncQueryHandler = new MyAsyncQueryHandler(getContentResolver());
        //        asyncQueryHandler.startQuery(0, null, Uri.parse("content://sms/"), projection_two, null, null, " date desc");
        asyncQueryHandler.startQuery(0, null, Uri.parse("content://sms/conversations/"), projection, null, null, " date desc");
        initSmsPermissions();
        initView();
        initShow();
        initAdapter();

        //        praseThreadDetailCursor();
        //        bindView();
        //                testReadConversation();
    }

    private void initSmsPermissions() {
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
    }

    private void initWriteSms() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            String defaultSmsApp = Telephony.Sms.getDefaultSmsPackage(this);
        }
        Intent intent = null;
        try {
            intent = new Intent(this, Class.forName(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT));
            intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, getPackageName());
            startActivity(intent);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 注册广播
     */
    private void RegsterBrodCast() {
        registerReceiver(sendBr, new IntentFilter("SENT_SMS_ACTION"));
        registerReceiver(deliverBr, new IntentFilter("DELIVERED_SMS_ACTION"));
        getContentResolver().registerContentObserver(uri, true, observer);
    }

    /**
     * 初始化adapter
     */
    public void initAdapter() {
        mDatas = new ArrayList<ThreadDetailBean>();
        //        mAdapter = new MessageAdapterCopy(this) {
        //            @Override
        //            public void onClick(String address, String name) {
        //                MessageActivity.this.thread_id = address;
        //                phoneName = name;
        //                praseThreadDetailCursor();
        //                //点击其他信息时恢复初始界面
        //                Initialinterface();
        //                number = 0;
        //                state = false;
        //            }
        //        };
        mAdapter = new MessageAdapter(this, null) {
            protected void onClick(View view, String id, String phone, String name) {
                MessageActivity.this.thread_id = id;
                phoneName = phone;
                praseThreadDetailCursor();
                constantsName = name;
                //点击其他信息时恢复初始界面
                Initialinterface();
                number = 0;
                state = false;
            }
        };
        mThreadDetailAdapter = new MyThreadDetailAdapter(this, mDatas) {
            public void onClick() {
                //MyThreadDetailAdapter}中添加抽象方法,用于item点击,暂时未加
            }
        };
        message_list.setAdapter(mThreadDetailAdapter);
        // 获取数据库数据
        message_info_list.setAdapter(mAdapter);
    }

    class MyAsyncQueryHandler extends AsyncQueryHandler {
        public MyAsyncQueryHandler(ContentResolver cr) {
            super(cr);
        }

        protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
            super.onQueryComplete(token, cookie, cursor);
            mAdapter.changeCursor(cursor);
        }
    }

    private void initShow() {
        message_back.setOnClickListener(this);
        message_line_pop.setOnClickListener(this);
        message_send.setOnClickListener(this);
        messsage_voice.setOnClickListener(this);
        message_list.setOnItemClickListener(this);
        message_list.setOnItemLongClickListener(this);
        bt_selectall.setOnClickListener(this);
        bt_cancel.setOnClickListener(this);
        bt_deselectall.setOnClickListener(this);
        message_cancle.setOnClickListener(this);
        message_delete.setOnClickListener(this);
        message_add.setOnClickListener(this);
    }

    private void initView() {
        message_back = (ImageButton) this.findViewById(R.id.message_back);
        message_line_pop = (ImageButton) this.findViewById(R.id.message_line_pop);
        message_phone_top = (TextView) this.findViewById(R.id.message_phone_top);
        message_info_list = (ListView) this.findViewById(R.id.message_info_list);
        message_list = (ListView) this.findViewById(R.id.message_list);
        message_send = (Button) this.findViewById(R.id.message_send);
        message_info_edit = (EditText) this.findViewById(R.id.message_info_edit);
        messsage_voice = (Button) this.findViewById(R.id.messsage_voice);
        bt_selectall = (Button) this.findViewById(R.id.bt_selectall);
        bt_cancel = (Button) this.findViewById(R.id.bt_cancleselectall);
        bt_deselectall = (Button) this.findViewById(R.id.bt_deselectall);
        message_checkbox = (LinearLayout) this.findViewById(R.id.message_checkbox);
        message_linear_send = (LinearLayout) this.findViewById(R.id.message_linear_send);
        message_cancle = (ImageButton) this.findViewById(R.id.message_cancle);
        message_delete = (Button) this.findViewById(R.id.message_delete);
        message_add = (ImageButton) this.findViewById(R.id.message_add);
    }

    /**
     * 刷新适配器
     */
    private void RefreshAdapter() {
        //发送短信
        //        praseThreadDetailCursor();
        mAdapter.notifyDataSetChanged();
        mThreadDetailAdapter.notifyDataSetChanged();

    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.message_back:
                Intent intent = new Intent(MessageActivity.this, MainActivity.class);
                startActivity(intent);
                MessageActivity.this.finish();
                break;
            case R.id.message_line_pop:
                ShowPopWindow();
                if (popupWindow.isShowing()) {
                    popupWindow.dismiss();
                } else {
                    popupWindow.showAsDropDown(v);
                }
                break;
            case R.id.message_send:
                HasSimCard.hasSimCard(this);
                SendMessage();
                break;
            case R.id.messsage_voice:
                SendVoice();
                break;
            //全选按钮
            case R.id.bt_selectall:
                Selectall();
                break;
            //取消按钮
            case R.id.bt_deselectall:
                DeselectAll();
                break;
            //反选
            case R.id.bt_cancleselectall:
                CancleSelectall();
                break;
            case R.id.message_cancle:
                messageDeleList.clear();
                Initialinterface();
                state = false;
                break;
            case R.id.message_delete:
                ShowDialog();
                break;
            case R.id.message_add:
                Intent intent1 = new Intent(MessageActivity.this, AddMessageActivity.class);
                startActivity(intent1);
                finish();
                break;
            default:
                break;
        }
    }

    /**
     * 显示初始界面
     */
    private void Initialinterface() {
        if (phoneName != null) {
            message_phone_top.setText(phoneName);
        } else {
            message_phone_top.setText(thread_id);
        }
        //返回键显示
        message_back.setVisibility(View.VISIBLE);
        message_add.setVisibility(View.VISIBLE);
        //取消键隐藏
        message_cancle.setVisibility(View.GONE);
        //PopWindow键隐藏
        message_line_pop.setVisibility(View.VISIBLE);
        //删除键显示
        message_delete.setVisibility(View.GONE);
        //显示语音输入框
        message_linear_send.setVisibility(View.VISIBLE);
        //隐藏全选、反选等布局
        message_checkbox.setVisibility(View.GONE);
        //名字显示
        message_phone_top.setVisibility(View.VISIBLE);
        //内容显示
        message_list.setVisibility(View.VISIBLE);
    }

    /**
     * 刷新listview和TextView的显示
     */
    private void dataChanged() {
        mThreadDetailAdapter.notifyDataSetChanged();
        message_phone_top.setText("已选中" + number + "项");
    }

    /**
     * 全选
     */
    private void Selectall() {
        number = 0;
        // 遍历list的长度，将Adapter中的值全部设为true
        messageDeleList.clear();
        messageDeleList.addAll(mDatas);
        for (int i = 0; i < mDatas.size(); i++) {
            // 改变boolean
            mDatas.get(i).setBo(true);
            // 如果为选中
            if (mDatas.get(i).getBo()) {
                number++;
            }
        }
        // 刷新listview和TextView的显示
        dataChanged();
    }

    /**
     * 反选
     */
    private void CancleSelectall() {
        number = 0;
        // 遍历list的长度，将已选的设为未选，未选的设为已选
        for (int i = 0; i < mDatas.size(); i++) {
            if (mDatas.get(i).getBo()) {
                mDatas.get(i).setBo(false);
                messageDeleList.remove(mDatas.get(i));
            } else {
                mDatas.get(i).setBo(true);
                messageDeleList.add(mDatas.get(i));
            }
            dataChanged();
            // 如果为选中
            if (mDatas.get(i).getBo()) {
                number++;
            }
            message_phone_top.setText("已选中" + number + "项");
        }
    }

    /**
     * 取消按钮
     */
    private void DeselectAll() {
        messageDeleList.clear();
        number = 0;
        // 遍历list的长度，将已选的按钮设为未选
        for (int i = 0; i < mDatas.size(); i++) {
            // 改值
            mDatas.get(i).setBo(false);

            // 刷新
            dataChanged();
            if (mDatas.get(i).getBo()) {
                number++;
            }
        }
        dataChanged();
    }

    /**
     * PopWindow弹出框
     */
    private void ShowPopWindow() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.message_popupwindow, null);
        Button compile_btn = (Button) view.findViewById(R.id.compile_btn);
        Button message_btn = (Button) view.findViewById(R.id.message_btn);
        Button delete_contacts_btn = (Button) view.findViewById(R.id.delete_contacts_btn);
        compile_btn.setBackgroundResource(R.drawable.call_phone);
        delete_contacts_btn.setBackgroundResource(R.drawable.delete_contacts);
        compile_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                popupWindow.dismiss();
                if (TextUtils.isEmpty(phoneName)) {
                    ToastUtil.showPicToast(MessageActivity.this, "拨打对象为空");
                    return;
                }
                HasSimCard.hasSimCard(MessageActivity.this);
                if (phoneName.contains("+86")) {
                    String callPhone = phoneName.substring(3);
                    CallPhone.callphone(MessageActivity.this, callPhone);
                } else {
                    CallPhone.callphone(MessageActivity.this, phoneName);
                }

            }
        });
        message_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (constantsName == null && phoneName != null) {
                    Intent intent = new Intent(MessageActivity.this, AddContactsActivity.class);
                    if (phoneName.contains("+86")) {
                        String callPhone = phoneName.substring(3);
                        intent.putExtra("phonetext", callPhone);
                    } else {
                        intent.putExtra("phonetext", phoneName);
                    }
                    startActivity(intent);
                } else {
                    ToastUtil.showPicToast(MessageActivity.this, "联系人已存在！");
                }
                popupWindow.dismiss();
            }
        });
        delete_contacts_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ShowContactsDialog();
                popupWindow.dismiss();
            }
        });
        popupWindow = new PopupWindow(view, 450, 350);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        //popupWindow.showAsDropDown(line_image, Gravity.BOTTOM, 0, 0);
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.alpha = 0.7f;
        getWindow().setAttributes(layoutParams);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            public void onDismiss() {
                WindowManager.LayoutParams layoutParams1 = getWindow().getAttributes();
                layoutParams1.alpha = 1f;
                getWindow().setAttributes(layoutParams1);
            }
        });
    }

    /**
     * 自带分享
     */
    private void ShareMessage() {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, "This is Text Count");
        shareIntent.setType("text/plain");
        //设置分享列表的标题，并且每次都显示分享列表
        startActivity(Intent.createChooser(shareIntent, "分享到"));
    }

    public void ShowDialog() {
        final Dialog dialog = new Dialog(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.confirm_dialog, null);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(view);
        ImageButton dialog_cancel = (ImageButton) view.findViewById(R.id.dialog_cancel);
        ImageButton dialog_delete = (ImageButton) view.findViewById(R.id.dialog_delete);
        dialog_cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog_delete.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                for (int i = 0; i < mDatas.size(); i++) {
                    if ((mDatas.get(i).getBo())) {
                        list.add(mDatas.get(i));
                    }
                }
                //未选择要删除的postion
                if (number == 0) {
                    ToastUtil.showPicToast(MessageActivity.this, "未选择删除对象");
                    //关闭Dialog
                    dialog.dismiss();
                    return;
                } else {
                    //删除短信
                    //                                        deleteSMS(bean.getPhone());
                    deleteSelectedSms();
                    messageDeleList.clear();
                    //移出选中postion
                    mDatas.removeAll(list);
                    //刷新适配器
                    mThreadDetailAdapter.notifyDataSetChanged();
                    //关闭Dialog
                    dialog.dismiss();
                    //恢复信息界面
                    Initialinterface();
                    //点击状态置回List需长按才执行单击事件
                    state = false;
                    //顶部显示选中数据置回0
                    number = 0;
                }
            }
        });
        dialog.show();
    }


    public void ShowContactsDialog() {
        final Dialog dialog = new Dialog(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.confirm_dialog, null);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(view);
        ImageButton dialog_cancel = (ImageButton) view.findViewById(R.id.dialog_cancel);
        ImageButton dialog_delete = (ImageButton) view.findViewById(R.id.dialog_delete);
        dialog_cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog_delete.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //mAdapter.
                if (!SmsWriteOpUtil.isWriteEnabled(getApplicationContext())) {
                    SmsWriteOpUtil.setWriteEnabled(
                            getApplicationContext(), true);
                }
                String queryString = "ADDRESS=" + phoneName;
                Uri.parse("content://sms");

                //                                getContentResolver().delete(Uri.parse("content://sms"), "thread_id="+thread_id, null);
                getContentResolver().delete(Uri.parse("content://sms/conversations/" + thread_id), null, null);
                //名字显示
                message_phone_top.setVisibility(View.INVISIBLE);
                //内容显示
                message_list.setVisibility(View.INVISIBLE);
                mAdapter.notifyDataSetChanged();
                RefreshAdapter();
                dialog.dismiss();
            }
        });
        dialog.show();
    }
    /**
     * 删除短信
     */
    public void deleteSMS(String number) {

        ContentResolver resolver = getContentResolver();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        //删除最近一次的按ID顺序
        //删除格式：number=? and
        //两种：(type=1 or type=3)
        //一种：type=3
        //全部删除：(type=1 or type=2 or type=3)
        //"date desc"：代表数据是倒序 "date asc"：表示顺序
        Cursor cursor = resolver.query(CallLog.Calls.CONTENT_URI, new String[]{"_id"}, "number=? and " +
                "(type=1 or type=2 or type=3)", new String[]{number}, "_id desc limit 1");
        if (cursor.moveToFirst()) {
            int id = cursor.getInt(0);
            resolver.delete(CallLog.Calls.CONTENT_URI, "_id=?", new String[]{id + ""});
            ToastUtil.showPicToast(this, "删除成功！");
        }


        //        int result;
        //        try {
        //            ContentResolver CR = getContentResolver();
        //            Uri uriSms = Uri.parse("content://sms/inbox");
        //            Cursor c = CR.query(uriSms, new String[]{"_id", "thread_id"}, null, null, null);
        //            if (null != c && c.moveToFirst()) {
        //                do {
        //                    long threadId = c.getLong(1);
        //                    result = CR.delete(Uri.parse("content://sms/conversations/" + threadId), null, null);
        //                    Log.e("deleteSMS", "threadId:: " + threadId + "  result::" + result);
        //                    ToastUtil.showPicToast(this, "删除成功！");
        //                } while (c.moveToNext());
        //            }
        //        } catch (Exception e) {
        //            Log.e("deleteSMS", "Exception:: " + e);
        //        }


        //        ContentResolver resolver = getContentResolver();
        //        // 查询收信箱里所有的短信
        //        Cursor cursor = resolver.query(Uri.parse("content://sms/inbox"), new String[]{"_id", "thread_id"}, null, null, null);
        //        if (cursor != null) {
        //            cursor.moveToFirst();
        //            //int a = cursor.getCount();
        //            //  int b = cursor.getColumnCount();
        //            long threadId = cursor.getLong(1);
        //            resolver.delete(Uri.parse("content://sms/conversations/" + threadId), null, null);
        //        }

        //②：
        //        ContentResolver CR = getContentResolver();
        //        try {
        //            // 准备系统短信收信箱的uri地址
        //            Uri uri = Uri.parse("content://sms/inbox");
        //            // 查询收信箱里所有的短信
        //            Cursor cursor = CR.query(uri, new String[]{"_id", "address", "person", "body", "date", "type"}, null, null, null);
        //            int count = cursor.getCount();
        //            if (count > 0) {
        //                while (cursor.moveToNext()) {
        //                    String body = cursor.getString(cursor.getColumnIndex("body"));// 获取信息内容
        //                    if (body.contains(smscontent)) {
        //                        int id = cursor.getInt(cursor.getColumnIndex("_id"));
        //                        CR.delete(Uri.parse("content://sms"), "_id=" + id, null);
        //                        ToastUtil.showPicToast(MessageActivity.this, "deleteSMS: " + bean.getContent() + id);
        //                    }
        //                }
        //            }
        //        } catch (Exception e) {
        //            Log.e("e", e.getMessage());
        //        }
    }

    //删除已选择的短信
    private void deleteSelectedSms() {
        final StringBuilder sb = new StringBuilder();

        int count = 0;
        for (int i = messageDeleList.size() - 1; i >= 0; i--) {
            sb.append(messageDeleList.get(i).getId());
            sb.append(",");

            //            int count1 = getContentResolver().delete(Uri.parse("content://sms/"), "_id='" + messageDeleList.get(i).getId() + "'", null);
            //            Log.d("deleteSms", "" + messageDeleList.get(i).getId() + "--- 删除  = " + count1);
        }
        //删除
        new Thread(new Runnable() {
            @Override
            public void run() {
                String SMS_URI_ALL = "content://sms/";
                Uri uri = Uri.parse(SMS_URI_ALL);
                String whereClause = "_id in(" + sb.substring(0, sb.length() - 1) + ")";
                int count = getContentResolver().delete(uri, whereClause, null);
            }
        }).start();

    }

    /**
     * 获取所有短信
     */
    private void praseThreadDetailCursor() {
        //"date desc"：代表数据是倒序 "date asc"：表示顺序"date asc"
        Cursor cursor = getContentResolver().query(uri, projection_two, "thread_id=?", new String[]{thread_id}, "date asc");
        //        Cursor cursor = getContentResolver().query(uri, projection_two, "address=?",new String[]{thread_id} , "date asc");
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            //清理数据,防止重复显示
            mDatas.clear();
            //返回游标是否指
            // 向第最后一行的位置
            while (!cursor.isAfterLast()) {
                id = cursor.getInt(cursor.getColumnIndex("_id"));
                String address = cursor.getString(cursor.getColumnIndex("address"));
                String body = cursor.getString(cursor.getColumnIndex("body"));
                String type = cursor.getString(cursor.getColumnIndex("type"));
                String date = cursor.getString(cursor.getColumnIndex("date"));
                String contactName = null;
                Uri contactNameUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(address));
                Cursor contactNameCursor = getContentResolver().query(contactNameUri, contactProjection, null, null, null);
                if (contactNameCursor != null && contactNameCursor.moveToFirst()) {
                    contactName = contactNameCursor.getString(1);
                }
                contactNameCursor.close();
                if (type.equals("1")) {
                    ThreadDetailBean heiBean = null;
                    if (contactName != null) {
                        heiBean = new ThreadDetailBean(contactName, date, body, R.layout.hei_item, id);
                    } else {
                        heiBean = new ThreadDetailBean(address, date, body, R.layout.hei_item, id);
                    }
                    mDatas.add(heiBean);
                } else {
                    ThreadDetailBean mBean = new ThreadDetailBean(null, date, body, R.layout.me_item, id);
                    mDatas.add(mBean);
                }
                //为假时将跳出循环，即 Cursor 数据循环完毕
                cursor.moveToNext();
            }
        }
        if (mDatas.size() > 0) {
            //有数据时刷新适配器
            mThreadDetailAdapter.notifyDataSetChanged();
        } else {
            ToastUtil.showPicToast(this, "无法获取到短信或没有短信！");
        }
        cursor.close();
    }

    /**
     * 发送短信
     */
    private void SendMessage() {

        String smsContent = message_info_edit.getText().toString().trim();
        if (TextUtils.isEmpty(smsContent)) {
            ToastUtil.showPicToast(this, "发送内容为空！");
            return;
        }
        if (TextUtils.isEmpty(phoneName)) {
            ToastUtil.showPicToast(this, "发送对象为空！");
            message_info_edit.setText("");
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
        message_info_edit.setText("");

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

        //        if (Build.VERSION.SDK_INT >= 20) {
        //            ContentValues values = new ContentValues();
        //            values.clear();
        //            values.put("address", phoneName); // 发送地址
        //            values.put("body", smsContent);// 消息内容
        //            values.put("date", System.currentTimeMillis());// 创建时间
        //            values.put("read", 1); // 0：未读； 1：已读
        //            values.put("type", 2);// 1：接收； 2：发送
        //            getContentResolver().insert(Uri.parse("content://sms/sent"), values);
        //        }

    }

    /**
     * 广播
     */
    class MySendBr extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            //注销广播
            context.unregisterReceiver(this);
            if (getResultCode() == Activity.RESULT_OK) {
                RefreshAdapter();
                ToastUtil.showPicToast(MessageActivity.this, "发送成功!");
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
                RefreshAdapter();
                ToastUtil.showPicToast(MessageActivity.this, "发送成功!");
            } else {
                ToastUtil.showPicToast(MessageActivity.this, "发送失败!");
            }
        }
    }

    /**
     * 关闭广播
     */
    protected void onDestroy() {
        //        unregisterReceiver(sendBr);
        //        unregisterReceiver(deliverBr);
        super.onDestroy();
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
                message_info_edit.append(text);
            }

            public void onError(SpeechError error) {
                // 自动生成的方法存根
                error.getPlainDescription(true);
            }
        });
        // 开始听写
        iatDialog.show();
    }

    public void bindView() {
        //        if (view != null) {
        Cursor cursor = getContentResolver().query(Uri.parse("content://sms/conversations/"), null, null, null, " date desc");
        int smsbodyColumn = cursor.getColumnIndex("body");
        String smsBody = cursor.getString(smsbodyColumn);


        final String thread_id = cursor.getString(0);
        String content = cursor.getString(5);
        int count = cursor.getInt(4);
        final String address = cursor.getString(1);
        final String date = cursor.getString(2);
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
        concatCursor.close();
        if (phoneName != null) {
        } else {
        }
        //        }
    }

    /**
     * 读取会话信息
     */
    public void testReadConversation() {
        ContentResolver resolver = getContentResolver();
        Uri uri = Uri.parse("content://sms/conversations/");
        String[] projection1 = new String[]{"groups.group_thread_id AS group_id", "groups.msg_count AS msg_count",
                "groups.group_date AS last_date", "sms.body AS last_msg", "sms.address AS contact"};

        //读取收件箱中指定号码的短信
        Cursor cursor = managedQuery(Uri.parse("content://sms/conversations"), new String[]{"_id", "address", "read", "body"},
                " thread_id=? ", new String[]{"thread_id"}, "_id desc");//按id排序，如果按date排序的话，修改手机时间后，读取的短信就不准了
        if (cursor != null && cursor.getCount() > 0) {
            ContentValues values = new ContentValues();
            values.put("read", "1");        //修改短信为已读模式
            cursor.moveToNext();
            int smsbodyColumn = cursor.getColumnIndex("body");
            String smsBody = cursor.getString(smsbodyColumn);

        }

        //在用managedQuery的时候，不能主动调用close()方法， 否则在Android 4.0+的系统上， 会发生崩溃
        if (Build.VERSION.SDK_INT < 14) {
            cursor.close();
        }
    }


    private int checkMode() {
        AppOpsManager appOps = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
        Class c = appOps.getClass();
        try {
            Class[] cArg = new Class[3];
            cArg[0] = int.class;
            cArg[1] = int.class;
            cArg[2] = String.class;
            Method lMethod = c.getDeclaredMethod("checkOp", cArg);
            return (Integer) lMethod.invoke(appOps, 15, Binder.getCallingUid(), getPackageName());
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent(MessageActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }
}



