package com.example.administrator.tf_phone.view.tool;

import android.content.Context;
import android.media.SoundPool;

import com.example.administrator.tf_phone.R;

import java.util.HashMap;

/**
 * Created by Administrator on 2016/12/16 0016.
 * 拨号声音
 */

public class PlayMusic {

    public static SoundPool mSoundPool;
    public static HashMap<Integer, Integer> soundID = new HashMap<Integer, Integer>();

    public static void playmusic(Context context) {
//        //当前系统的SDK版本大于等于21(Android 5.0)时
//        if (Build.VERSION.SDK_INT >= 21) {
//            SoundPool.Builder builder = new SoundPool.Builder();
//            //传入音频数量
//            builder.setMaxStreams(12);
//            //AudioAttributes是一个封装音频各种属性的方法
//            AudioAttributes.Builder attrBuilder = new AudioAttributes.Builder();
//            //设置音频流的合适的属性
//            attrBuilder.setLegacyStreamType(0);
//            //加载一个AudioAttributes
//            builder.setAudioAttributes(attrBuilder.build());
//            mSoundPool = builder.build();
//        }
//        //当系统的SDK版本小于21时
//        else {
            //设置最多可容纳15个音频流，音频的品质为5
//            mSoundPool = new SoundPool(12, 0, 5);
            mSoundPool = new SoundPool(12, 1, 1);

//        }
        soundID.put(0, mSoundPool.load(context, R.raw.dail_0, 1));
        soundID.put(1, mSoundPool.load(context, R.raw.dail_1, 1));
        soundID.put(2, mSoundPool.load(context, R.raw.dail_2, 1));
        soundID.put(3, mSoundPool.load(context, R.raw.dail_3, 1));
        soundID.put(4, mSoundPool.load(context, R.raw.dail_4, 1));
        soundID.put(5, mSoundPool.load(context, R.raw.dail_5, 1));
        soundID.put(6, mSoundPool.load(context, R.raw.dail_6, 1));
        soundID.put(7, mSoundPool.load(context, R.raw.dail_7, 1));
        soundID.put(8, mSoundPool.load(context, R.raw.dail_8, 1));
        soundID.put(9, mSoundPool.load(context, R.raw.dail_9, 1));
        soundID.put(10, mSoundPool.load(context, R.raw.jinghao, 1));
        soundID.put(11, mSoundPool.load(context, R.raw.xinghao, 1));
    }
}
