package com.example.administrator.tf_phone;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.administrator.tf_phone.activity.AddMessageActivity;
import com.example.administrator.tf_phone.adapter.ContactsListAdapter;
import com.example.administrator.tf_phone.bean.ContactsModelBean;
import com.example.administrator.tf_phone.sqldb.MyAppSqlite;
import com.example.administrator.tf_phone.view.BaseActivity;
import com.example.administrator.tf_phone.view.tool.BitmapString;
import com.example.administrator.tf_phone.view.tool.CallPhone;
import com.example.administrator.tf_phone.view.tool.HasSimCard;
import com.example.administrator.tf_phone.view.tool.OperationSqlite;
import com.example.administrator.tf_phone.view.tool.PlayMusic;
import com.example.administrator.tf_phone.view.tool.ToastUtil;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.administrator.tf_phone.conf.Constants.isUpdateContacts;
import static com.example.administrator.tf_phone.view.tool.MyAppApiConfig.PHONES_CONTACT_ID_INDEX;
import static com.example.administrator.tf_phone.view.tool.MyAppApiConfig.PHONES_DISPLAY_NAME_INDEX;
import static com.example.administrator.tf_phone.view.tool.MyAppApiConfig.PHONES_NUMBER_INDEX;
import static com.example.administrator.tf_phone.view.tool.MyAppApiConfig.PHONES_PHOTO_ID_INDEX;
import static com.example.administrator.tf_phone.view.tool.MyAppApiConfig.PHONES_PROJECTION;
import static com.example.administrator.tf_phone.view.tool.PlayMusic.mSoundPool;
import static com.example.administrator.tf_phone.view.tool.PlayMusic.soundID;

/**
 * 通讯录界面
 */
public class ContactsActivity extends BaseActivity implements View.OnClickListener, View.OnLongClickListener, AdapterView.OnItemClickListener {

    private static final String TAG = "ContactsActivity";
    private List<ContactsModelBean> list = new ArrayList<>();
    private List<ContactsModelBean> contacts = new ArrayList<>();
    private ContactsListAdapter mAdapter;
    private ListView contacts_list;
    private TextView contact_phone, contact_username, conacts_number,contact_city;
    private LinearLayout liner_info_start, liner_info_end, linear_backage;
    private ImageButton delete_image, line_image, contacts_back;
    private PopupWindow popupWindow;
    private Button message_btn, message_callphone, contacts_news, call_phone;
    private Button btn_one, btn_two, btn_three, btn_four, btn_five,
            btn_six, btn_seven, btn_eight, btn_nine, btn_zero, btn_jin, btn_xin;
    private String str0, str1, str2, str3, str4, str5, str6, str7, str8, str9, strjin, strxin;
    private EditText contacts_edit;
    private CircleImageView contacts_icon;
    private ContactsModelBean bean;
    private boolean isContactsDetails;
    private int index;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    protected void initContentView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_contacts);
        isUpdateContacts = false;
        isContactsDetails = false;
        initView();
        initShow();
        getContacts();
        PlayMusic.playmusic(this);

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

                            //查看联系人备注
                            String noteWhere = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
                            String[] noteWhereParams = new String[]{String.valueOf(contactid), ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE};
                            Cursor noteCur = resolver.query(ContactsContract.Data.CONTENT_URI, null, noteWhere, noteWhereParams, null);
                            if (noteCur.moveToFirst()) {
                                model.note = noteCur.getString(noteCur.getColumnIndex(ContactsContract.CommonDataKinds.Note.NOTE));
                            }
                            noteCur.close();

                            //查看联系人地址
                            String cityWhere = ContactsContract.Data.RAW_CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
                            String[] cityWhereParams = new String[]{String.valueOf(contactid), ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE};
                            Cursor cityCur = resolver.query(ContactsContract.Data.CONTENT_URI, null, cityWhere, cityWhereParams, null);
                            if (cityCur.moveToFirst()) {
                                model.city = cityCur.getString(cityCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.CITY));
                            }
                            cityCur.close();


                            list.add(model);
                        }
                        cursor.close();
                    }
                    runOnUiThread(new Runnable() {
                        public void run() {
                            contacts.addAll(list);
                            conacts_number.setText(contacts.size() + "位联系人");
                            mAdapter = new ContactsListAdapter(ContactsActivity.this, contacts);
                            contacts_list.setAdapter(mAdapter);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }


    private void initView() {
        contacts_list = (ListView) this.findViewById(R.id.contacts_list);
        contacts_icon = (CircleImageView) this.findViewById(R.id.contacts_icon);
        liner_info_start = (LinearLayout) this.findViewById(R.id.liner_info_start);
        liner_info_end = (LinearLayout) this.findViewById(R.id.liner_info_end);
        contact_username = (TextView) this.findViewById(R.id.contact_username);
        contact_phone = (TextView) this.findViewById(R.id.contact_phone);
        linear_backage = (LinearLayout) this.findViewById(R.id.linear_backage);
        delete_image = (ImageButton) this.findViewById(R.id.delete_image);
        line_image = (ImageButton) this.findViewById(R.id.line_image);
        contacts_back = (ImageButton) this.findViewById(R.id.contacts_back);
        contacts_news = (Button) this.findViewById(R.id.contacts_news);
        message_btn = (Button) this.findViewById(R.id.message_btn);
        message_callphone = (Button) this.findViewById(R.id.message_callphone);
        call_phone = (Button) this.findViewById(R.id.call_phone);
        contacts_edit = (EditText) this.findViewById(R.id.contacts_edit);
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
        conacts_number = (TextView) this.findViewById(R.id.conacts_number);
        contact_city = (TextView) this.findViewById(R.id.contact_city);
        contacts_edit.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            public void afterTextChanged(Editable editable) {
                String temp = contacts_edit.getText().toString().trim();
                contacts.clear();
                for (int i = 0; i < list.size(); i++) {
                    ContactsModelBean model = list.get(i);
                    //contains是否为包含为true,无false
                    if (model.getPhone().contains(temp)) {
                        contacts.add(model);
                    }
                }
                if (mAdapter == null) {
                } else {
                    mAdapter.notifyDataSetChanged();
                    conacts_number.setText(contacts.size() + "位联系人");
                }
            }
        });
    }

    private void initShow() {
        contacts_icon.setOnClickListener(this);
        contacts_edit.setOnClickListener(this);
        line_image.setOnClickListener(this);
        message_btn.setOnClickListener(this);
        contacts_back.setOnClickListener(this);
        message_callphone.setOnClickListener(this);
        contacts_news.setOnClickListener(this);
        delete_image.setOnClickListener(this);
        delete_image.setOnLongClickListener(this);
        contacts_list.setOnItemClickListener(this);
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
        call_phone.setOnClickListener(this);
    }

    public void onBackPressed() {
        list.clear();
        super.onBackPressed();
    }

    private void SetVisibility() {
        liner_info_start.setVisibility(View.GONE);
        liner_info_end.setVisibility(View.VISIBLE);
        linear_backage.setBackgroundResource(R.drawable.contacts_start);
        delete_image.setVisibility(View.GONE);
        line_image.setVisibility(View.VISIBLE);
        contacts_edit.setText("");
    }

    private void SetVisble() {
        liner_info_start.setVisibility(View.VISIBLE);
        liner_info_end.setVisibility(View.GONE);
        linear_backage.setBackgroundResource(R.drawable.contacts_start);
        delete_image.setVisibility(View.VISIBLE);
        line_image.setVisibility(View.GONE);
    }

    private void getNumberPhone() {
        str0 = contacts_edit.getText().toString().trim();
        str1 = contacts_edit.getText().toString().trim();
        str2 = contacts_edit.getText().toString().trim();
        str3 = contacts_edit.getText().toString().trim();
        str4 = contacts_edit.getText().toString().trim();
        str5 = contacts_edit.getText().toString().trim();
        str6 = contacts_edit.getText().toString().trim();
        str7 = contacts_edit.getText().toString().trim();
        str8 = contacts_edit.getText().toString().trim();
        str9 = contacts_edit.getText().toString().trim();
        strjin = contacts_edit.getText().toString().trim();
        strxin = contacts_edit.getText().toString().trim();
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
        getNumberPhone();
        getPhoneEdit();
        switch (v.getId()) {
            case R.id.line_image:
                ShowPopWindow();
                if (popupWindow.isShowing()) {
                    popupWindow.dismiss();
                } else {
                    popupWindow.showAsDropDown(v);
                }
                break;
            case R.id.message_btn:
                Intent intentMessage = new Intent(ContactsActivity.this, AddMessageActivity.class);
                intentMessage.putExtra("number", bean.getPhone());
                startActivity(intentMessage);
                break;
            case R.id.contacts_back:
                if (isContactsDetails) {
                    liner_info_start.setVisibility(View.VISIBLE);
                    liner_info_end.setVisibility(View.GONE);
                    linear_backage.setBackgroundResource(R.drawable.contacts_start);
                    delete_image.setVisibility(View.VISIBLE);
                    line_image.setVisibility(View.GONE);
                    contacts_edit.setText("");
                    isContactsDetails = false;
                } else {
                    Intent intent = new Intent(ContactsActivity.this, MainActivity.class);
                    startActivity(intent);
                    ContactsActivity.this.finish();
                }
                break;
            case R.id.message_callphone:
                HasSimCard.hasSimCard(this);
                String conacts_phonenumber = contact_phone.getText().toString().trim();
                CallPhone.callphone(this, conacts_phonenumber);
                break;
            case R.id.contacts_news:
                Intent intent1 = new Intent(ContactsActivity.this, AddContactsActivity.class);
                startActivity(intent1);
                break;
            case R.id.btn_one:
                contacts_edit.setText(str1);
                mSoundPool.play(soundID.get(1), 0.3f, 0.3f, 0, 0, 1);
                break;
            case R.id.btn_two:
                contacts_edit.setText(str2);
                mSoundPool.play(soundID.get(2), 0.3f, 0.3f, 0, 0, 1);
                break;
            case R.id.btn_three:
                contacts_edit.setText(str3);
                mSoundPool.play(soundID.get(3), 0.3f, 0.3f, 0, 0, 1);
                break;
            case R.id.btn_four:
                contacts_edit.setText(str4);
                mSoundPool.play(soundID.get(4), 0.3f, 0.3f, 0, 0, 1);
                break;
            case R.id.btn_five:
                contacts_edit.setText(str5);
                mSoundPool.play(soundID.get(5), 0.3f, 0.3f, 0, 0, 1);
                break;
            case R.id.btn_six:
                contacts_edit.setText(str6);
                mSoundPool.play(soundID.get(6), 0.3f, 0.3f, 0, 0, 1);
                break;
            case R.id.btn_seven:
                contacts_edit.setText(str7);
                mSoundPool.play(soundID.get(7), 0.3f, 0.3f, 0, 0, 1);
                break;
            case R.id.btn_eight:
                contacts_edit.setText(str8);
                mSoundPool.play(soundID.get(8), 0.3f, 0.3f, 0, 0, 1);
                break;
            case R.id.btn_nine:
                contacts_edit.setText(str9);
                mSoundPool.play(soundID.get(9), 0.3f, 0.3f, 0, 0, 1);
                break;
            case R.id.btn_zero:
                contacts_edit.setText(str0);
                mSoundPool.play(soundID.get(0), 0.3f, 0.3f, 0, 0, 1);
                break;
            case R.id.btn_jin:
                contacts_edit.setText(strjin);
                mSoundPool.play(soundID.get(11), 0.3f, 0.3f, 0, 0, 1);
                break;
            case R.id.btn_xin:
                contacts_edit.setText(strxin);
                mSoundPool.play(soundID.get(11), 1, 1, 0, 0, 1);
                break;
            case R.id.delete_image:
                String phoneStr = contacts_edit.getText().toString().trim();
                if (TextUtils.isEmpty(phoneStr)) {
                } else {
                    contacts_edit.setText(phoneStr.substring(0, phoneStr.length() - 1));
                }
                break;
            case R.id.call_phone:
                HasSimCard.hasSimCard(this);
                String contacts = contacts_edit.getText().toString().trim();
                CallPhone.callphone(this, contacts);
                contacts_edit.setText("");
                break;
            case R.id.contacts_icon:
                Intent inten = new Intent(ContactsActivity.this, PicActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable("image", bean.getPhoneBitmap());
                inten.putExtras(bundle);
                startActivity(inten);
                break;
            default:
                break;
        }
    }

    /**
     * popWindow收藏联系人
     */
    private void SaveFavorite() {
        Bitmap bitmap = bean.getPhoneBitmap();
        String headPortrait = BitmapString.bitmapToString(this, bitmap);
        String phone = bean.getPhone();
        OperationSqlite.AddFavorite(bean.getPhone(), bean.getName(), headPortrait);
        ToastUtil.showPicToast(this, "保存成功");
    }

    private void ShowPopWindow() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.contacts_popupwindow, null);
        Button compile_btn = (Button) view.findViewById(R.id.compile_btn);
        Button delete_contacts_btn = (Button) view.findViewById(R.id.delete_contacts_btn);
        //        Button message_btn = (Button) view.findViewById(R.id.message_btn);
        // message_btn.setBackgroundResource(R.drawable.save_contacts);
        //        message_btn.setOnClickListener(new View.OnClickListener() {
        //            public void onClick(View view) {
        //                SaveFavorite();
        //                popupWindow.dismiss();
        //            }
        //        });
        compile_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                StartIntentCompile();
                popupWindow.dismiss();
            }
        });
        delete_contacts_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                ShowDialog();
                popupWindow.dismiss();
            }
        });
        popupWindow = new PopupWindow(view, 450, 350);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.alpha = 0.6f;
        getWindow().setAttributes(layoutParams);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            public void onDismiss() {
                WindowManager.LayoutParams layoutParams1 = getWindow().getAttributes();
                layoutParams1.alpha = 1f;
                getWindow().setAttributes(layoutParams1);
            }
        });
    }

    private void ShowDialog() {
        final Dialog dialog = new Dialog(ContactsActivity.this);
        LayoutInflater inflater = LayoutInflater.from(ContactsActivity.this);
        View view = inflater.inflate(R.layout.contacts_dialog, null);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(view);
        ImageButton dialog_cancel = (ImageButton) view.findViewById(R.id.dialog_cancel);
        ImageButton dialog_delete = (ImageButton) view.findViewById(R.id.dialog_delete);
        TextView dialog_name = (TextView) view.findViewById(R.id.dialog_name);
        dialog_name.setText(bean.getName() + "  ?");
        dialog_cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog_delete.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (TextUtils.isEmpty(bean.getName())) {
                } else {

                    if (isCollect(bean.getPhone())) {
                        OperationSqlite.DeleteFavorite(bean.getPhone());
                    }

                    OperationSqlite.DeleteId(ContactsActivity.this, bean.getId());
                    SetVisble();
                    ToastUtil.showPicToast(ContactsActivity.this, "删除成功");
                    dialog.dismiss();
                    startActivity(new Intent(ContactsActivity.this, ContactsActivity.class));
                    ContactsActivity.this.finish();
                }
            }
        });
        dialog.show();
    }

    /**
     * 删除按钮长按事件
     */
    public boolean onLongClick(View view) {
        contacts_edit.setText("");
        return true;
    }

    /**
     * ListView点击
     */
    public void onItemClick(AdapterView<?> adapterView, View view, int postion, long l) {
        bean = list.get(postion);
        isContactsDetails = true;
        SetVisibility();
        contact_username.setText(bean.getName());
        contact_phone.setText(bean.getPhone());
        contact_city.setText(bean.city);
        contacts_icon.setImageBitmap(bean.getPhoneBitmap());
        index = postion;
    }

    /**
     * Dialog编辑
     */
    private void StartIntentCompile() {
        SetVisble();
        //        OperationSqlite.DeleteId(this,bean.getId());
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putParcelable("image", bean.getPhoneBitmap());
        bundle.putString("name", bean.getName());
        bundle.putString("phone", bean.getPhone());
        bundle.putLong("id", bean.getId());
        bundle.putString("note", bean.note);
        bundle.putString("city", bean.city);
        intent.putExtras(bundle);
        intent.setClass(ContactsActivity.this, CompileActivity.class);
        startActivity(intent);
    }

    /**
     * 再次返回时刷新List
     */
    protected void onRestart() {
        super.onRestart();
        contacts.clear();
        list.clear();
        getContacts();
        contacts.addAll(list);
        mAdapter.notifyDataSetChanged();
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void onStart() {
        super.onStart();
    }

    protected void onResume() {
        super.onResume();
    }

    protected void onPause() {
        super.onPause();
    }

    protected void onStop() {
        super.onStop();
    }

    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent(ContactsActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    private boolean isCollect(String phoneNum) {

        SQLiteDatabase dbl = MyAppSqlite.db.getReadableDatabase();
        String sql = "select * from collect where phone=?";
        Cursor cursor = dbl.query("collect", null, "phone=?", new String[]{phoneNum}, null, null, null);
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            cursor.close();
            dbl.close();
            return true;
        } else {
            cursor.close();
            dbl.close();
            return false;
        }
    }
}


