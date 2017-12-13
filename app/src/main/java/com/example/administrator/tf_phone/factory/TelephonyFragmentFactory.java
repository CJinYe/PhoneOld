package com.example.administrator.tf_phone.factory;

import android.support.v4.app.Fragment;
import android.util.SparseArray;

import com.example.administrator.tf_phone.fragment.ContactsFragment;
import com.example.administrator.tf_phone.fragment.KeyBoardFragment;
import com.example.administrator.tf_phone.fragment.MessageFragment;

/**
 * @author Administrator
 * @version $Rev$
 * @time 2017-1-21 13:44
 * @des ${TODO}
 * @updateAuthor $Author$
 * @updateDate $Date$
 * @updateDes ${TODO}
 */
public class TelephonyFragmentFactory {
    private static final int TELEPHONY_CONTACTS_FRAGMENT = 0;
    private static final int TELEPHONY_MESSAGE_FRAGMENT = 1;
    private static final int TELEPHONY_KEYBOARD_FRAGMENT = 2;
    private static SparseArray<Fragment> telephonyFragments = new SparseArray<>();

    public static Fragment createTelephonyFragment(int position) {
        Fragment fragment = null;
        if (telephonyFragments.get(position) != null) {
            return telephonyFragments.get(position);
        }

        switch (position) {
            case TELEPHONY_CONTACTS_FRAGMENT:
                fragment = new ContactsFragment();
                break;
            case TELEPHONY_MESSAGE_FRAGMENT:
                fragment = new MessageFragment();
                break;
            case TELEPHONY_KEYBOARD_FRAGMENT:
                fragment = new KeyBoardFragment();
                break;
        }
        telephonyFragments.put(position,fragment);

        return fragment;
    }
}
