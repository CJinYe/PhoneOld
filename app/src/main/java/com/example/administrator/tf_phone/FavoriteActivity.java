package com.example.administrator.tf_phone;

import android.app.Dialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.administrator.tf_phone.adapter.FavoriteAdapter;
import com.example.administrator.tf_phone.bean.FavoriteBean;
import com.example.administrator.tf_phone.sqldb.MyAppSqlite;
import com.example.administrator.tf_phone.view.BaseActivity;
import com.example.administrator.tf_phone.view.tool.BitmapString;
import com.example.administrator.tf_phone.view.tool.CallPhone;
import com.example.administrator.tf_phone.view.tool.HasSimCard;
import com.example.administrator.tf_phone.view.tool.OperationSqlite;
import com.example.administrator.tf_phone.view.tool.ToastUtil;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 收藏夹界面
 */
public class FavoriteActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    private ImageButton favorite_back;
    private GridView gridView;
    private FavoriteAdapter favAdapter;
    private List<FavoriteBean> lists = new ArrayList<>();
    private String mPhoneName;
    private Bitmap mBmp_head;

    protected void initContentView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_favorite);

        initView();
        initShow();
        getDatas();
        try {
            GetQueryData();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getDatas() {
        favAdapter = new FavoriteAdapter(this, lists);
        gridView.setAdapter(favAdapter);
    }

    private void initShow() {
        favorite_back.setOnClickListener(this);
        gridView.setOnItemClickListener(this);
        gridView.setOnItemLongClickListener(this);
    }

    private void initView() {
        favorite_back = (ImageButton) this.findViewById(R.id.favorite_back);
        gridView = (GridView) this.findViewById(R.id.gridView);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.favorite_back:
                Intent intent = new Intent(FavoriteActivity.this, MainActivity.class);
                startActivity(intent);
                FavoriteActivity.this.finish();
                break;
            default:
                break;
        }
    }

    public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int postion, long l) {

        final FavoriteBean bean = lists.get(postion);
        final Dialog dialog = new Dialog(FavoriteActivity.this);
        LayoutInflater inflater = LayoutInflater.from(FavoriteActivity.this);
        View vieww = inflater.inflate(R.layout.contacts_dialog, null);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(vieww);
        ImageButton dialog_cancel = (ImageButton) vieww.findViewById(R.id.dialog_cancel);
        ImageButton dialog_delete = (ImageButton) vieww.findViewById(R.id.dialog_delete);
        TextView dialog_name = (TextView) vieww.findViewById(R.id.dialog_name);
        dialog_name.setText(bean.getFav_username() + "  ?");
        dialog_cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog_delete.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                OperationSqlite.DeleteFavorite(bean.getFav_phone());
                ToastUtil.showPicToast(FavoriteActivity.this, "删除成功");
                lists.remove(postion);
                favAdapter.notifyDataSetChanged();
                dialog.dismiss();
            }
        });
        dialog.show();
        return true;
    }

    private void GetQueryData() throws IOException {
        SQLiteDatabase db = MyAppSqlite.db.getReadableDatabase();
        String sql = "select * from collect";
        Cursor cursor = db.rawQuery(sql, null);
        while (cursor.moveToNext()) {
            String fav_phone = cursor.getString((cursor.getColumnIndex("phone")));
            String fav_username = cursor.getString((cursor.getColumnIndex("username")));
            String headPortrait = cursor.getString(cursor.getColumnIndex("picture"));
            Bitmap headPortraitBitmap = BitmapString.stringToBitmap(this, headPortrait);

            //            if (TextUtils.isEmpty(fav_username)){
            //                fav_username = fav_phone;
            //            }
            //            if (headPortraitBitmap==null){
            //                headPortraitBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.icon_back);
            //            }

            FavoriteBean favbean = new FavoriteBean(fav_phone, fav_username, headPortraitBitmap);
            lists.add(favbean);
        }
        cursor.close();
        db.close();
    }

    public void onItemClick(AdapterView<?> adapterView, View view, int postion, long l) {
        FavoriteBean bean = lists.get(postion);
        TelephonyManager mTelephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (mTelephonyManager.getSimState() != TelephonyManager.SIM_STATE_READY){
            return;
        }
        HasSimCard.hasSimCard(this);
        CallPhone.callphone(this, bean.getFav_phone());
    }

    private void getContacts(final String phoneNum) {

        try {
            // 获得Uri
            //                    Uri uriNumber2Contacts = Uri.parse("content://com.android.contacts/" + "data/phones/filter/" + phoneNum);
            Uri uriNumber2Contacts = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNum));
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            Intent intent = new Intent(FavoriteActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

}
