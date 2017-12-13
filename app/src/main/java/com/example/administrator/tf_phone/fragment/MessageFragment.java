package com.example.administrator.tf_phone.fragment;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.example.administrator.tf_phone.R;
import com.example.administrator.tf_phone.adapter.MessageAdapter;
import com.example.administrator.tf_phone.adapter.MyThreadDetailAdapter;
import com.example.administrator.tf_phone.bean.MessageBean;
import com.example.administrator.tf_phone.bean.ThreadDetailBean;
import com.example.administrator.tf_phone.view.tool.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import static com.example.administrator.tf_phone.view.tool.MyAppApiConfig.contactProjection;
import static com.example.administrator.tf_phone.view.tool.MyAppApiConfig.projection;
import static com.example.administrator.tf_phone.view.tool.MyAppApiConfig.projection_two;
import static com.example.administrator.tf_phone.view.tool.MyAppApiConfig.uri;

/**
 * @author Administrator
 * @version $Rev$
 * @time 2017-1-21 14:11
 * @des ${TODO}
 * @updateAuthor $Author$
 * @updateDate $Date$
 * @updateDes ${TODO}
 */
public class MessageFragment extends Fragment implements View.OnClickListener {

    private View mView;
    private ImageButton mMessageBtnBack;
    private ListView mMessageLvHome;
    private ListView mMessageLvDetails;
    private String meeageId;
    private String phoneNumber,phoneName;
    private Context mContext;
    private ArrayList<ThreadDetailBean> mDatas;
    private MessageAdapter mHomeAdapter;
    private MyThreadDetailAdapter mThreadDetailAdapter;
    private TextView mMessageTvTitle ,mMessageTvNumber;
    private List<MessageBean> messageData;

//    public MessageFragment(List<MessageBean> messageData) {
//        this.messageData = messageData;
//    }
    public void setData(List<MessageBean> messageData){
        this.messageData = messageData;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_phone_message, container, false);
        mContext = getActivity();
        MyAsyncQueryHandler asyncQueryHandler = new MyAsyncQueryHandler(mContext.getContentResolver());
        asyncQueryHandler.startQuery(0, null, Uri.parse("content://sms/conversations/"), projection, null, null, " date desc");
        initView();
        initData();
        return mView;

    }

    private void initData() {
        mDatas = new ArrayList<>();

        //MyThreadDetailAdapter}中添加抽象方法,用于item点击,暂时未加
        mThreadDetailAdapter = new MyThreadDetailAdapter(mContext, mDatas) {
            public void onClick() {
                //MyThreadDetailAdapter}中添加抽象方法,用于item点击,暂时未加
            }
        };
        mMessageLvDetails.setAdapter(mThreadDetailAdapter);

        mHomeAdapter = new MessageAdapter(mContext,null) {
            @Override
            protected void onClick(View view, String id, String phone , String name) {
                phoneNumber = phone;
                phoneName = name;
                praseThreadDetailCursor();
                mMessageLvDetails.setVisibility(View.VISIBLE);
                mMessageBtnBack.setVisibility(View.VISIBLE);
                mMessageLvHome.setVisibility(View.GONE);
                mThreadDetailAdapter.notifyDataSetChanged();

                mMessageTvNumber.setVisibility(View.VISIBLE);
                mMessageTvTitle.setVisibility(View.GONE);

                if(phoneName!=null){
                    mMessageTvNumber.setText(phoneName);
                }else {
                    mMessageTvNumber.setText(phoneNumber);
                }
            }

            public void onClick(String address, String name) {
                phoneNumber = address;
                phoneName = name;
                praseThreadDetailCursor();
                mMessageLvDetails.setVisibility(View.VISIBLE);
                mMessageBtnBack.setVisibility(View.VISIBLE);
                mMessageLvHome.setVisibility(View.GONE);
                mThreadDetailAdapter.notifyDataSetChanged();

                mMessageTvNumber.setVisibility(View.VISIBLE);
                mMessageTvTitle.setVisibility(View.GONE);

                if(phoneName!=null){
                    mMessageTvNumber.setText(phoneName);
                }else {
                    mMessageTvNumber.setText(phoneNumber);
                }
            }

            protected void onClick(View view, String phone) {
                phoneNumber = phone;
                praseThreadDetailCursor();
                mMessageLvDetails.setVisibility(View.VISIBLE);
                mMessageBtnBack.setVisibility(View.VISIBLE);
                mMessageLvHome.setVisibility(View.GONE);
                mThreadDetailAdapter.notifyDataSetChanged();
                //点击其他信息时恢复初始界面
                // Initialinterface();
                //                number = 0;
                //                state = false;
            }
        };
        mMessageLvHome.setAdapter(mHomeAdapter);
    }

    private void initView() {
        mMessageBtnBack = (ImageButton) mView.findViewById(R.id.fragment_message_btn_back);
        mMessageLvHome = (ListView) mView.findViewById(R.id.fragment_message_lv_home);
        mMessageLvDetails = (ListView) mView.findViewById(R.id.fragment_message_lv_details);
        mMessageTvTitle = (TextView) mView.findViewById(R.id.fragment_message_title);
        mMessageTvNumber = (TextView) mView.findViewById(R.id.fragment_message_number);
        mMessageBtnBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fragment_message_btn_back:
                mMessageLvDetails.setVisibility(View.GONE);
                mMessageBtnBack.setVisibility(View.GONE);
                mMessageTvNumber.setVisibility(View.GONE);
                mMessageLvHome.setVisibility(View.VISIBLE);
                mMessageTvTitle.setVisibility(View.VISIBLE);
                break;
        }
    }

    /**
     * 获取所有短信
     */
    private void praseThreadDetailCursor() {
        //"date desc"：代表数据是倒序 "date asc"：表示顺序"date asc"
        Cursor cursor = mContext.getContentResolver().query(uri, projection_two, "address=?", new String[]{phoneNumber}, "date asc");
        //        Cursor cursor = getContentResolver().query(uri, projection_two, null, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            //清理数据,防止重复显示
            mDatas.clear();
            //返回游标是否指
            // 向第最后一行的位置
            while (!cursor.isAfterLast()) {
                int id = cursor.getInt(cursor.getColumnIndex("_id"));
                String address = cursor.getString(cursor.getColumnIndex("address"));
                String body = cursor.getString(cursor.getColumnIndex("body"));
                String type = cursor.getString(cursor.getColumnIndex("type"));
                String date = cursor.getString(cursor.getColumnIndex("date"));
                String contactName = null;
                Uri contactNameUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(address));
                Cursor contactNameCursor = mContext.getContentResolver().query(contactNameUri, contactProjection, null, null, null);
                if (contactNameCursor != null && contactNameCursor.moveToFirst()) {
                    contactName = contactNameCursor.getString(1);
                    contactNameCursor.close();
                }
                if (type.equals("1")) {
                    ThreadDetailBean heiBean = null;
                    if (contactName != null) {
                        heiBean = new ThreadDetailBean(contactName, date, body, R.layout.hei_item,id);
                    } else {
                        heiBean = new ThreadDetailBean(address, date, body, R.layout.hei_item,id);
                    }
                    mDatas.add(heiBean);
                } else {
                    ThreadDetailBean mBean = new ThreadDetailBean(null, date, body, R.layout.me_item,id);
                    mDatas.add(mBean);
                }
                //为假时将跳出循环，即 Cursor 数据循环完毕
                cursor.moveToNext();
            }
        }
        if (mDatas.size() > 0) {
            //有数据时刷新适配器
            //            mThreadDetailAdapter.notifyDataSetChanged();
        } else {
            ToastUtil.showPicToast(mContext, "无法获取到短信或没有短信！");
        }
        cursor.close();
        Log.d("messageData",mDatas.size()+"");
    }

    class MyAsyncQueryHandler extends AsyncQueryHandler {
        public MyAsyncQueryHandler(ContentResolver cr) {
            super(cr);
        }

        protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
            super.onQueryComplete(token, cookie, cursor);
            mHomeAdapter.changeCursor(cursor);
        }
    }
}
