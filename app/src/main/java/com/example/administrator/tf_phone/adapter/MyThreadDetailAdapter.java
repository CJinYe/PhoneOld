package com.example.administrator.tf_phone.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.administrator.tf_phone.R;
import com.example.administrator.tf_phone.bean.ThreadDetailBean;

import java.util.ArrayList;


public abstract class MyThreadDetailAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<ThreadDetailBean> mDatas;

    public MyThreadDetailAdapter(Context mContext, ArrayList<ThreadDetailBean> mDatas) {
        this.mContext = mContext;
        this.mDatas = mDatas;
    }

    public int getCount() {
        return mDatas.size();
    }

    public Object getItem(int position) {
        return mDatas.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public int getItemViewType(int position) {
        return position;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        ThreadDetailBean bean = mDatas.get(position);
        ViewHolder holder;
        holder = new ViewHolder();
        int layoutId = bean.getLayoutId();
        if (layoutId == R.layout.hei_item) {
            convertView = View.inflate(mContext, R.layout.hei_item, null);
        } else if (layoutId == R.layout.me_item) {
            convertView = View.inflate(mContext, R.layout.me_item, null);
        }
        holder.content = (TextView) convertView.findViewById(R.id.content);
        holder.cb = (CheckBox) convertView.findViewById(R.id.item_cb);
        holder.cb.setChecked(bean.getBo());
        holder.content.setText(bean.getContent());
        return convertView;
    }

    public abstract void onClick();

    public static class ViewHolder {
        TextView content;
        CheckBox cb;
    }
}
