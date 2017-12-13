package com.example.administrator.tf_phone.adapter;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.tf_phone.R;
import com.example.administrator.tf_phone.bean.ContactsModelBean;
import com.example.administrator.tf_phone.sqldb.MyAppSqlite;
import com.example.administrator.tf_phone.view.tool.BitmapString;
import com.example.administrator.tf_phone.view.tool.OperationSqlite;
import com.example.administrator.tf_phone.view.tool.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.administrator.tf_phone.R.id.conacts_icon;

/**
 * Created by Administrator on 2016/12/3 0003.
 */
public class ContactsListAdapter extends BaseAdapter {

    private Context mContext;
    private List<ContactsModelBean> list = new ArrayList<>();
    private String username, phone;

    public ContactsListAdapter(Context mContext, List<ContactsModelBean> contactlist) {
        this.mContext = mContext;
        this.list = contactlist;
    }

    public int getCount() {
        if (list!=null){
        return list.size();
        }
        return 0;
    }

    public Object getItem(int position) {
        return list.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        final ContactsModelBean modelBean = list.get(position);
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.conacts_list_item, null);
            holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            holder.tv_group_phone = (TextView) convertView.findViewById(R.id.tv_group_phone);
            holder.collect_image = (ImageView) convertView.findViewById(R.id.collect_image);
            holder.conacts_icon = (CircleImageView) convertView.findViewById(conacts_icon);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.collect_image.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                username = holder.tv_name.getText().toString().trim();
                //                    phone = holder.tv_group_phone.getText().toString().trim();
                phone = modelBean.getPhone();
                Bitmap bitmap = modelBean.getPhoneBitmap();
                String headPortrait = BitmapString.bitmapToString(mContext, bitmap);
                if (holder.state) {
                    if (TextUtils.isEmpty(username) && TextUtils.isEmpty(phone)) {
                    } else {
                        OperationSqlite.DeleteFavorite(phone);
                        ToastUtil.showPicToast(mContext, "取消收藏");
                        holder.collect_image.setBackgroundResource(R.drawable.collect_start);
                        holder.state = false;
                    }
                } else {
                    OperationSqlite.AddFavorite(phone, username, headPortrait);
                    ToastUtil.showPicToast(mContext, "收藏成功");
                    holder.collect_image.setBackgroundResource(R.drawable.collect_end);
                    holder.state = true;
                }
            }
        });


        holder.tv_name.setText(modelBean.getName());
        holder.tv_group_phone.setText(modelBean.getPhone());
        holder.conacts_icon.setImageBitmap(modelBean.getPhoneBitmap());
        SQLiteDatabase dbl = MyAppSqlite.db.getReadableDatabase();
        String sql = "select * from collect where phone=?";
        Cursor cursor = dbl.query("collect", null, "phone=?", new String[]{modelBean.getPhone()}, null, null, null);
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            holder.collect_image.setBackgroundResource(R.drawable.collect_end);
            holder.state = true;
        } else {
            holder.collect_image.setBackgroundResource(R.drawable.collect_start);
            holder.state = false;
        }
        if (cursor != null) {
            cursor.close();
            dbl.close();
            cursor = null;
        }
        return convertView;
    }

     class ViewHolder {
        TextView tv_name;
        TextView tv_group_phone;
        ImageView collect_image;
        CircleImageView conacts_icon;
        boolean state;
    }
}





