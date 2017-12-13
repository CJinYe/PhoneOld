package com.example.administrator.tf_phone.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.administrator.tf_phone.R;

import static com.example.administrator.tf_phone.view.tool.PlayMusic.mSoundPool;
import static com.example.administrator.tf_phone.view.tool.PlayMusic.soundID;

/**
 * @author Administrator
 * @version $Rev$
 * @time 2017-1-21 14:12
 * @des ${TODO}
 * @updateAuthor $Author$
 * @updateDate $Date$
 * @updateDes ${TODO}
 */
public class KeyBoardFragment extends Fragment implements View.OnClickListener {

    private View mView;
    private EditText mFragmentPhoneText;
    private ImageButton mFragmentKeyboardDelete;
    private Button mBtnOne;
    private Button mBtnTwo;
    private Button mBtnThree;
    private Button mBtnFour;
    private Button mBtnFive;
    private Button mBtnSix;
    private Button mBtnSeven;
    private Button mBtnEight;
    private Button mBtnNine;
    private Button mBtnJin;
    private Button mBtnZero;
    private Button mBtnXin;
    private String str0;
    private String str1;
    private String str2;
    private String str3;
    private String str4;
    private String str5;
    private String str6;
    private String str7;
    private String str8;
    private String str9;
    private String strjin;
    private String strxin;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_phone_keyboard, container, false);
        initView();
        initData();
        initListener();
        return mView;
    }

    private void initView() {
        mFragmentPhoneText = (EditText) mView.findViewById(R.id.fragment_phone_text);
        mFragmentKeyboardDelete = (ImageButton) mView.findViewById(R.id.fragment_keyboard_delete);
        mBtnOne = (Button) mView.findViewById(R.id.fragment_keyboard_btn_one);
        mBtnTwo = (Button) mView.findViewById(R.id.fragment_keyboard_btn_two);
        mBtnThree = (Button) mView.findViewById(R.id.fragment_keyboard_btn_three);
        mBtnFour = (Button) mView.findViewById(R.id.fragment_keyboard_btn_four);
        mBtnFive = (Button) mView.findViewById(R.id.fragment_keyboard_btn_five);
        mBtnSix = (Button) mView.findViewById(R.id.fragment_keyboard_btn_six);
        mBtnSeven = (Button) mView.findViewById(R.id.fragment_keyboard_btn_seven);
        mBtnEight = (Button) mView.findViewById(R.id.fragment_keyboard_btn_eight);
        mBtnNine = (Button) mView.findViewById(R.id.fragment_keyboard_btn_nine);
        mBtnJin = (Button) mView.findViewById(R.id.fragment_keyboard_btn_jin);
        mBtnZero = (Button) mView.findViewById(R.id.fragment_keyboard_btn_zero);
        mBtnXin = (Button) mView.findViewById(R.id.fragment_keyboard_btn_xin);

        mFragmentPhoneText.setOnClickListener(this);
        mFragmentKeyboardDelete.setOnClickListener(this);
        mBtnOne.setOnClickListener(this);
        mBtnTwo.setOnClickListener(this);
        mBtnThree.setOnClickListener(this);
        mBtnFour.setOnClickListener(this);
        mBtnFive.setOnClickListener(this);
        mBtnSix.setOnClickListener(this);
        mBtnSeven.setOnClickListener(this);
        mBtnEight.setOnClickListener(this);
        mBtnNine.setOnClickListener(this);
        mBtnJin.setOnClickListener(this);
        mBtnZero.setOnClickListener(this);
        mBtnXin.setOnClickListener(this);
    }

    private void initData() {

    }

    private void initListener() {
    }

    @Override
    public void onClick(View v) {
        getNumberPhone();
        getPhoneEdit();
        switch (v.getId()) {
            case R.id.fragment_keyboard_delete:
                String phoneStr = mFragmentPhoneText.getText().toString().trim();
                if (TextUtils.isEmpty(phoneStr)) {
                } else {
                    mFragmentPhoneText.setText(phoneStr.substring(0, phoneStr.length() - 1));
                }
                break;
            case R.id.fragment_keyboard_btn_one:
//                KeyEvent event = new KeyEvent(KeyEvent.ACTION_DOWN,KeyEvent.KEYCODE_1);
//                getActivity().onKeyDown(KeyEvent.KEYCODE_1,event);
                mFragmentPhoneText.setText(str1);
                mSoundPool.play(soundID.get(1), 1, 1, 0, 0, 1);
                break;
            case R.id.fragment_keyboard_btn_two:
                mFragmentPhoneText.setText(str2);
                mSoundPool.play(soundID.get(1), 1, 1, 0, 0, 1);
                break;
            case R.id.fragment_keyboard_btn_three:
                mFragmentPhoneText.setText(str3);
                mSoundPool.play(soundID.get(3), 1, 1, 0, 0, 1);
                break;
            case R.id.fragment_keyboard_btn_four:
                mFragmentPhoneText.setText(str4);
                mSoundPool.play(soundID.get(4), 1, 1, 0, 0, 1);
                break;
            case R.id.fragment_keyboard_btn_five:
                mFragmentPhoneText.setText(str5);
                mSoundPool.play(soundID.get(5), 1, 1, 0, 0, 1);
                break;
            case R.id.fragment_keyboard_btn_six:
                mFragmentPhoneText.setText(str6);
                mSoundPool.play(soundID.get(6), 1, 1, 0, 0, 1);
                break;
            case R.id.fragment_keyboard_btn_seven:
                mFragmentPhoneText.setText(str7);
                mSoundPool.play(soundID.get(7), 1, 1, 0, 0, 1);
                break;
            case R.id.fragment_keyboard_btn_eight:
                mFragmentPhoneText.setText(str8);
                mSoundPool.play(soundID.get(8), 1, 1, 0, 0, 1);
                break;
            case R.id.fragment_keyboard_btn_nine:
                mFragmentPhoneText.setText(str9);
                mSoundPool.play(soundID.get(9), 1, 1, 0, 0, 1);
                break;
            case R.id.fragment_keyboard_btn_jin:
                mFragmentPhoneText.setText(strjin);
                mSoundPool.play(soundID.get(10), 1, 1, 0, 0, 1);
                break;
            case R.id.fragment_keyboard_btn_zero:
                mFragmentPhoneText.setText(str0);
                mSoundPool.play(soundID.get(0), 1, 1, 0, 0, 1);
                break;
            case R.id.fragment_keyboard_btn_xin:
                mFragmentPhoneText.setText(strxin);
                mSoundPool.play(soundID.get(11), 1, 1, 0, 0, 1);
                break;
        }
    }

    private void submit() {
        // validate
        String text = mFragmentPhoneText.getText().toString().trim();
        if (TextUtils.isEmpty(text)) {
            Toast.makeText(getContext(), "text不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        // TODO validate success, do something


    }

    private void getNumberPhone() {
        str0 = mFragmentPhoneText.getText().toString().trim();
        str1 = mFragmentPhoneText.getText().toString().trim();
        str2 = mFragmentPhoneText.getText().toString().trim();
        str3 = mFragmentPhoneText.getText().toString().trim();
        str4 = mFragmentPhoneText.getText().toString().trim();
        str5 = mFragmentPhoneText.getText().toString().trim();
        str6 = mFragmentPhoneText.getText().toString().trim();
        str7 = mFragmentPhoneText.getText().toString().trim();
        str8 = mFragmentPhoneText.getText().toString().trim();
        str9 = mFragmentPhoneText.getText().toString().trim();
        strjin = mFragmentPhoneText.getText().toString().trim();
        strxin = mFragmentPhoneText.getText().toString().trim();
    }

    private void getPhoneEdit() {
        str1 += "1";
        str2 += "2";
        str3 += "3";
        str4 += "4";
        str5 += "5";
        str6 += "6";
        str7 += "7";
        str8 += "8";
        str9 += "9";
        str0 += "0";
        strjin += "#";
        strxin += "*";
    }
}
