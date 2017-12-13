package com.example.administrator.tf_phone.fragment;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.administrator.tf_phone.R;
import com.example.administrator.tf_phone.adapter.ContactsListAdapter;
import com.example.administrator.tf_phone.bean.ContactsModelBean;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.administrator.tf_phone.view.tool.MyAppApiConfig.PHONES_CONTACT_ID_INDEX;
import static com.example.administrator.tf_phone.view.tool.MyAppApiConfig.PHONES_DISPLAY_NAME_INDEX;
import static com.example.administrator.tf_phone.view.tool.MyAppApiConfig.PHONES_NUMBER_INDEX;
import static com.example.administrator.tf_phone.view.tool.MyAppApiConfig.PHONES_PHOTO_ID_INDEX;
import static com.example.administrator.tf_phone.view.tool.MyAppApiConfig.PHONES_PROJECTION;

/**
 * @author Administrator
 * @version $Rev$
 * @time 2017-1-21 14:11
 * @des ${TODO}
 * @updateAuthor $Author$
 * @updateDate $Date$
 * @updateDes ${TODO}
 */
public class ContactsFragment extends Fragment {

    private View mView;
    private ImageButton mContactsBack;
    private ListView mContactsLv;
    private Context mContext;
    private List<ContactsModelBean> mContacts;
    private ImageButton mContactsBtnBack;
    private TextView mContactsTvTitle;
    private LinearLayout mContactsLlDetails;
    private TextView mContactsTvName;
    private CircleImageView mContactsIvPhone;
    private TextView mContactsTvNumber;
    private ContactsListAdapter mAdapter;

//    public ContactsFragment(List<ContactsModelBean> contactss) {
//        mContacts = contactss;
//    }
    public void setData(List<ContactsModelBean> contactss){
        mContacts = contactss;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_phone_contacts, container, false);
        Log.d("positionposition","onCreateView通讯录");
        mContext = getActivity();
        //        getContacts();
        initView();
        initData();
        return mView;

    }

    private void initView() {
        mContactsLv = (ListView) mView.findViewById(R.id.fragment_contacts_lv);
        mContactsBtnBack = (ImageButton) mView.findViewById(R.id.fragment_contacts_btn_back);
        mContactsTvTitle = (TextView) mView.findViewById(R.id.fragment_contacts_tv_title);
        mContactsLlDetails = (LinearLayout) mView.findViewById(R.id.fragment_contacts_ll_answer_details);
        mContactsIvPhone = (CircleImageView) mView.findViewById(R.id.fragment_contacts_iv_answer_phone);
        mContactsTvName = (TextView) mView.findViewById(R.id.fragment_contacts_tv_answer_name);
        mContactsTvNumber = (TextView) mView.findViewById(R.id.fragment_contacts_tv_answer_number);
    }

    private void initData() {
        mAdapter = new ContactsListAdapter(getContext(), mContacts);
        mContactsLv.setAdapter(mAdapter);
        mContactsLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    ContactsModelBean bean = mContacts.get(position);
                    mContactsLv.setVisibility(View.GONE);
                    mContactsLlDetails.setVisibility(View.VISIBLE);
                    mContactsBtnBack.setVisibility(View.VISIBLE);
                    mContactsTvTitle.setText("详情");
                    mContactsIvPhone.setImageBitmap(bean.getPhoneBitmap());
                    mContactsTvName.setText(bean.getName());
                    mContactsTvNumber.setText(bean.getPhone());
            }
        });

        mContactsBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContactsLv.setVisibility(View.VISIBLE);
                mContactsLlDetails.setVisibility(View.GONE);
                mContactsBtnBack.setVisibility(View.GONE);
                mContactsTvTitle.setText("通讯录");
            }
        });
    }

    private void getContacts() {
        mContacts = new ArrayList<>();
        new Thread() {
            public void run() {
                //                mContacts = new ArrayList<>();
                ContentResolver resolver = mContext.getContentResolver();
                //查询联系人数据，query的参数Phone.SORT_KEY_PRIMARY表示将结果集按Phone.SORT_KEY_PRIMARY排序
                Cursor cursor = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        PHONES_PROJECTION, null, null, ContactsContract.CommonDataKinds.Phone.SORT_KEY_PRIMARY);
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        ContactsModelBean model = new ContactsModelBean();
                        model.setPhone(cursor.getString(PHONES_NUMBER_INDEX));
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
                        model.setPhoneBitmap(contactPhoto);
                        model.setName(cursor.getString(PHONES_DISPLAY_NAME_INDEX));
                        mContacts.add(model);
                    }
                    cursor.close();
                }


            }
        }.start();
        //         if (mAdapter!=null){
        //                    mAdapter.notifyDataSetChanged();
        //                }
    }


    @Override
    public void onDestroy() {
        //        mContacts.clear();
        super.onDestroy();
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }


    @Override
    public void onStart() {
        //        mContactsLv = (ListView) mView.findViewById(R.id.fragment_contacts_lv);
        //        getContacts();
        //        mAdapter = new ContactsListAdapter(getContext(), mContacts);
        //        mContactsLv.setAdapter(mAdapter);
        //        if (mAdapter!=null){
        //            mAdapter.notifyDataSetChanged();
        //        }
        super.onStart();
    }
}
