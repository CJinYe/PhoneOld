package com.example.administrator.tf_phone.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.administrator.tf_phone.bean.ContactsModelBean;
import com.example.administrator.tf_phone.bean.MessageBean;
import com.example.administrator.tf_phone.fragment.ContactsFragment;
import com.example.administrator.tf_phone.fragment.KeyBoardFragment;
import com.example.administrator.tf_phone.fragment.MessageFragment;

import java.util.List;

/**
 * @author Administrator
 * @version $Rev$
 * @time 2017-1-21 13:41
 * @des 接通界面右侧的页面
 * @updateAuthor $Author$
 * @updateDate $Date$
 * @updateDes ${TODO}
 */
public class TelephonyRightPagerAdapter extends FragmentPagerAdapter {
    List<ContactsModelBean> contactss;
    private List<MessageBean> messageData;

    public TelephonyRightPagerAdapter(FragmentManager supportFragmentManager, List<ContactsModelBean> mContacts, List<MessageBean> messageData) {
        super(supportFragmentManager);
        this.contactss = mContacts;
        this.messageData = messageData;
    }

    @Override
    public Fragment getItem(int position) {
        //        Fragment fragment =  TelephonyFragmentFactory.createTelephonyFragment(position);
        Fragment fragment = null;
        switch (position) {
            case 0:
                ContactsFragment ContactsFragment = new ContactsFragment();
                ContactsFragment.setData(contactss);
                return ContactsFragment;
            case 1:
                MessageFragment MessageFragment = new MessageFragment();
                MessageFragment.setData(messageData);
                return MessageFragment;
            case 2:
                KeyBoardFragment KeyBoardFragment = new KeyBoardFragment();
                return KeyBoardFragment;
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return 3;
    }
}
