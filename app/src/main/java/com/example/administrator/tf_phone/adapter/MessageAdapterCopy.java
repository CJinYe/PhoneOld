package com.example.administrator.tf_phone.adapter;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.tf_phone.R;
import com.example.administrator.tf_phone.bean.MessageBean;
import com.example.administrator.tf_phone.bean.ThreadDetailBean;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.administrator.tf_phone.view.tool.MyAppApiConfig.contentProjection;
import static com.example.administrator.tf_phone.view.tool.MyAppApiConfig.projection_two;
import static com.example.administrator.tf_phone.view.tool.MyAppApiConfig.uri;


public abstract class MessageAdapterCopy extends BaseAdapter {

    private Context mContext;
    private ArrayList<ThreadDetailBean> mDatas;
    private List<MessageBean> list = new ArrayList<>();
    private List<MessageBean> mMessageData;
    public MessageAdapterCopy(Context mContext, List<MessageBean> messageData) {
        this.mContext = mContext;
        this.mMessageData = messageData;
//        getMessageData();
    }

    public int getCount() {
        if (mMessageData.size() != 0) {
            return mMessageData.size();
        }
        return 0;
    }

    public Object getItem(int position) {
        return mMessageData.get(position);
    }

    public long getItemId(int position) {
        return position;
    }


    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        int i = 0;

        final MessageBean arrMessageBean = mMessageData.get(position);
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = View.inflate(mContext.getApplicationContext(), R.layout.item, null);
            holder.message_name = (TextView) convertView.findViewById(R.id.meaage_name);
            holder.message_count = (TextView) convertView.findViewById(R.id.message_count);
            holder.message_time = (TextView) convertView.findViewById(R.id.message_time);
            holder.message_conent = (TextView) convertView.findViewById(R.id.message_content);
            holder.message_unread = (ImageView) convertView.findViewById(R.id.message_iv_unread);
            holder.message_icon = (CircleImageView) convertView.findViewById(R.id.message_iv_icon);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (arrMessageBean.name != null) {
            holder.message_name.setText(arrMessageBean.name);
        } else {
            holder.message_name.setText(arrMessageBean.address);

        }

        if (arrMessageBean.pic!=null){
            holder.message_icon.setImageBitmap(arrMessageBean.pic);
        }else {
            holder.message_icon.setImageResource(R.drawable.icon);
        }


        holder.message_time.setText(arrMessageBean.date);
        holder.message_conent.setText(arrMessageBean.body);

        if (arrMessageBean.read.equals("0")) {
            holder.message_unread.setVisibility(View.VISIBLE);
        } else {
            holder.message_unread.setVisibility(View.INVISIBLE);
        }

        final View finalConvertView = convertView;
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finalConvertView.findViewById(R.id.message_iv_unread).setVisibility(View.INVISIBLE);
                Uri uri = Uri.parse("content://sms");
                ContentResolver resolver = mContext.getContentResolver();
                ContentValues values = new ContentValues();
                values.put("read", 1);
                resolver.update(uri, values, "address = ?", new String[]{arrMessageBean.address});
                MessageAdapterCopy.this.onClick(arrMessageBean.address, arrMessageBean.name);
            }
        });
        return convertView;
    }

    public abstract void onClick(String address, String name);

    class ViewHolder {
        TextView message_name;
        TextView message_count;
        TextView message_time;
        TextView message_conent;
        ImageView message_unread;
        CircleImageView message_icon;

    }

    private void getMessageData() {
        String messageAddress = "";
        Cursor cursor = mContext.getContentResolver().query(uri, projection_two, null, null, " date desc");
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            //清理数据,防止重复显示
            //返回游标是否指
            // 向第最后一行的位置
            while (cursor.moveToNext()) {
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
                Cursor concatCursor = mContext.getContentResolver().query(uri, contentProjection, null, null, null);
                if (concatCursor.moveToFirst()) {
                    phoneName = concatCursor.getString(1);
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

                if (messageAddress.contains(address)) {
                } else {
                    messageAddress = messageAddress + address;
                    list.add(messageBean);
                }

                //为假时将跳出循环，即 Cursor 数据循环完毕
            }
            cursor.close();
        }
    }
}
