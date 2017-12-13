package com.example.administrator.tf_phone.adapter;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.administrator.tf_phone.AddContactsActivity;
import com.example.administrator.tf_phone.CompileActivity;
import com.example.administrator.tf_phone.MainActivity;
import com.example.administrator.tf_phone.R;
import com.example.administrator.tf_phone.bean.CallLogBean;
import com.example.administrator.tf_phone.conf.Constants;
import com.example.administrator.tf_phone.view.tool.CallPhone;
import com.example.administrator.tf_phone.view.tool.HasSimCard;
import com.example.administrator.tf_phone.view.tool.OperationSqlite;
import com.example.administrator.tf_phone.view.tool.SideslipView;

import java.io.InputStream;
import java.util.Calendar;
import java.util.List;

import static com.example.administrator.tf_phone.R.id.call_type;
import static com.example.administrator.tf_phone.R.id.linear_side;
import static com.example.administrator.tf_phone.R.id.number;
import static com.example.administrator.tf_phone.view.tool.MyAppApiConfig.CALL_COME_IN;
import static com.example.administrator.tf_phone.view.tool.MyAppApiConfig.CALL_COME_OUT;

public class PhoneCallInfoAdapter extends BaseAdapter {

    private Context context;
    private List<CallLogBean> callLogs;
    private LayoutInflater inflater;

    public PhoneCallInfoAdapter(Context context, List<CallLogBean> callLogs) {
        super();
        this.context = context;
        this.callLogs = callLogs;
        this.inflater = LayoutInflater.from(context);
    }

    public int getCount() {
        if (callLogs.size() <= 100 && callLogs.size() != 0) {
            return callLogs.size();
        }
        if (callLogs.size() > 100) {
            return 100;
        }
        return 0;
    }

    public Object getItem(int position) {
        return callLogs.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        final CallLogBean callLog = callLogs.get(position);
        final ViewHolder holder = new ViewHolder();
        convertView = inflater.inflate(R.layout.simple_calllog_item, null);
        holder.call_type = (ImageView) convertView.findViewById(call_type);
        holder.name = (TextView) convertView.findViewById(R.id.namee);
        holder.number = (TextView) convertView.findViewById(number);
        holder.time = (TextView) convertView.findViewById(R.id.time);
        holder.image_delete = (Button) convertView.findViewById(R.id.image_delete);
        holder.image_news = (Button) convertView.findViewById(R.id.image_news);
        holder.sideview = (SideslipView) convertView.findViewById(R.id.side_view);
        holder.linear_side = (LinearLayout) convertView.findViewById(linear_side);
        if (!callLog.isConstants) {
            ((Button) convertView.findViewById(R.id.image_news)).setText("新建");
        } else {
            ((Button) convertView.findViewById(R.id.image_news)).setText("编辑");
        }

        //        //拨打
        //        holder.linear_side.setOnClickListener(new View.OnClickListener() {
        //            public void onClick(View view) {
        //                HasSimCard.hasSimCard(context);
        //                CallPhone.callphone(context, callLog.getNumber());
        //                holder.sideview.close();
        //            }
        //        });
        final int MIN_CLICK_DELAY_TIME = 2000;
        final long[] lastClickTime = {0};
        long currentTime = Calendar.getInstance().getTimeInMillis();
        if (currentTime - lastClickTime[0] > MIN_CLICK_DELAY_TIME || !Constants.isOnClickItemOutCall) {
            holder.linear_side.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    HasSimCard.hasSimCard(context);
                    TelephonyManager mTelephonyManager = (TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE);
                    if (mTelephonyManager.getSimState() != TelephonyManager.SIM_STATE_READY){
                        return;
                    }
                    long currentTime = Calendar.getInstance().getTimeInMillis();
                    lastClickTime[0] = currentTime;
                    Constants.isOnClickItemOutCall = true;
                    CallPhone.callphone(context, callLog.getNumber());
                    //                    }
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            MainActivity main = (MainActivity) context;
                            main.finish();
                        }
                    }, 500);
                    holder.sideview.close();
                }
            });
        }
        //删除
        holder.image_delete.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                OperationSqlite.DeleteFirstPhoneInfo(context, callLog.getNumber());
                callLogs.remove(position);
                holder.sideview.close();
                notifyDataSetChanged();
            }
        });
        //新建
        holder.image_news.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Constants.isMainAddContacts = true;
                if (!callLog.isConstants) {
                    Intent intent = new Intent(context, AddContactsActivity.class);
                    intent.putExtra("phonetext", callLog.getNumber());
                    context.startActivity(intent);
                } else {
                    Intent intent = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("image", getContacts(callLog.getNumber()));
                    bundle.putString("name", callLog.getName());
                    bundle.putString("phone", callLog.getNumber());
                    bundle.putLong("id", callLog.getId());
                    intent.putExtras(bundle);
                    intent.setClass(context, CompileActivity.class);
                    context.startActivity(intent);
                }


                MainActivity main = (MainActivity) context;
                main.finish();
                holder.sideview.close();
            }
        });
        if (callLog.getType() == CALL_COME_IN) {
            holder.call_type.setBackgroundResource(R.drawable.call_come_in);
        } else if (callLog.getType() == CALL_COME_OUT) {
            holder.call_type.setBackgroundResource(R.drawable.call_come_out);
        } else {
            holder.call_type.setBackgroundResource(R.drawable.call_come_no);
            holder.name.setTextColor(Color.RED);
            holder.number.setTextColor(Color.RED);
            holder.time.setTextColor(Color.RED);
        }



        holder.name.setText(callLog.getName());
        holder.number.setText(callLog.getNumber());
        holder.time.setText(callLog.getDate());
        return convertView;
    }

    private static class ViewHolder {
        ImageView call_type;
        TextView name;
        TextView number;
        TextView time;
        SideslipView sideview;
        Button image_delete;
        Button image_news;
        LinearLayout linear_side;
    }

    private Bitmap getContacts(String number) {
        Bitmap constantsPhoto = null;

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
            InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(context.getContentResolver(), uri);
            // 从InputStream获得bitmap     　　　
            constantsPhoto = BitmapFactory.decodeStream(input);
        }
        return constantsPhoto;
    }
}