package com.example.administrator.tf_phone.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.administrator.tf_phone.R;
import com.example.administrator.tf_phone.bean.FavoriteBean;

import java.util.List;

import static com.example.administrator.tf_phone.R.id.favorite_icon;
import static com.example.administrator.tf_phone.R.id.favorite_phone;
import static com.example.administrator.tf_phone.R.id.favorite_username;

/**
 * Created by Administrator on 2016/12/7 0007.
 */

public class FavoriteAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;
    private List<FavoriteBean> list;

    public FavoriteAdapter(Context context, List<FavoriteBean> list) {
        this.context = context;
        this.list = list;
        this.inflater = LayoutInflater.from(context);
    }

    public int getCount() {
        if (list.size()!=0){

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

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        FavoriteBean favorite = list.get(position);
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = View.inflate(context,R.layout.gridview_item, null);
            holder.favorite_phone = (TextView) convertView.findViewById(favorite_phone);
            holder.favorite_username = (TextView) convertView.findViewById(favorite_username);
            holder.favorite_icon = (de.hdodenhof.circleimageview.CircleImageView) convertView.findViewById(favorite_icon);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.favorite_icon.setImageBitmap(favorite.getFav_icon());
        holder.favorite_phone.setText(favorite.getFav_phone());
        holder.favorite_username.setText(favorite.getFav_username());
        return convertView;
    }

    class ViewHolder {
        de.hdodenhof.circleimageview.CircleImageView favorite_icon;
        TextView favorite_phone;
        TextView favorite_username;
    }
}
