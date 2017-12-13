package com.example.administrator.tf_phone.adapter;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.tf_phone.R;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.administrator.tf_phone.view.tool.MyAppApiConfig.contentProjection;

/**
 * Created by Administrator on 2016/12/9 0009.
 */

public abstract class MessageAdapter extends CursorAdapter {

    private ViewHolder viewHolder;
    private Context mContext;
    private Cursor cursor;
    private String phoneName;
    private Bitmap mBmp_pic;

    public MessageAdapter(Context context, Cursor c) {
        super(context, c);
        this.mContext = context;
        this.cursor = c;
    }

    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {

        View view = null;
        if (view == null) {
            viewHolder = new ViewHolder();
            view = View.inflate(mContext.getApplicationContext(), R.layout.item, null);
        viewHolder.message_name = (TextView) view.findViewById(R.id.meaage_name);
        viewHolder.message_count = (TextView) view.findViewById(R.id.message_count);
        viewHolder.message_time = (TextView) view.findViewById(R.id.message_time);
        viewHolder.message_conent = (TextView) view.findViewById(R.id.message_content);
        viewHolder.message_icon = (CircleImageView) view.findViewById(R.id.message_iv_icon);
        viewHolder.message_unread = (ImageView) view.findViewById(R.id.message_iv_unread);
        view.setTag(viewHolder);
        }
        return view;
    }

    public void bindView(final View view, final Context context, Cursor cursor) {
        if (view != null) {
            viewHolder = (ViewHolder) view.getTag();
            final TextView namee = viewHolder.message_name;
            TextView countt = viewHolder.message_count;
            TextView timee = viewHolder.message_time;
            TextView contentt = viewHolder.message_conent;
             final String thread_id = cursor.getString(0);
            String content = cursor.getString(1);
            int count = cursor.getInt(2);
            final String address = cursor.getString(3);
            String date = cursor.getString(4);
            String read = cursor.getString(5);
            getContacts(context,address);
            contentt.setText(content);
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(Long.parseLong(date + ""));
            SimpleDateFormat format = new SimpleDateFormat("MM/dd HH:mm:ss");
            String formatTime = format.format(calendar.getTime());
            String phoneName = null;
            Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(address));
            Cursor concatCursor = mContext.getContentResolver().query(uri, contentProjection, null, null, null);
            if (concatCursor.moveToFirst()) {
                phoneName = concatCursor.getString(1);
            }
            concatCursor.close();

            if (mBmp_pic!=null){
                viewHolder.message_icon.setImageBitmap(mBmp_pic);
            }else {
                viewHolder.message_icon.setImageResource(R.drawable.icon);
            }

            if (read.equals("0")){
                viewHolder.message_unread.setVisibility(View.VISIBLE);
            }else {
                viewHolder.message_unread.setVisibility(View.INVISIBLE);
            }

            if (phoneName != null) {
                namee.setText(phoneName);
                countt.setText("(" + count + ")");
                timee.setText(formatTime);
            } else {
                namee.setText(address);
                countt.setText("(" + count + ")");
                timee.setText(formatTime);
            }
            final String finalPhoneName = phoneName;
            view.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    view.findViewById(R.id.message_iv_unread).setVisibility(View.INVISIBLE);
                    Uri uri = Uri.parse("content://sms");
                    ContentResolver resolver = context.getContentResolver();
                    ContentValues values = new ContentValues();
                    values.put("read",1);
                    resolver.update(uri,values,"address = ?",new String[]{address});
                    MessageAdapter.this.onClick(v, thread_id, address, finalPhoneName);
                }
            });
        }
    }

    class ViewHolder {
        TextView message_name;
        TextView message_count;
        TextView message_time;
        TextView message_conent;
        ImageView message_unread;
        CircleImageView message_icon;
    }

    protected abstract void onClick(View view, String id, String phone,String name);

    private void getContacts(Context context, String number) {
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
}


