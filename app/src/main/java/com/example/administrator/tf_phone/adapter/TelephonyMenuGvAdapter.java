package com.example.administrator.tf_phone.adapter;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaRecorder;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.tf_phone.R;
import com.example.administrator.tf_phone.sqldb.MyAppSqlite;
import com.example.administrator.tf_phone.view.tool.BitmapString;
import com.example.administrator.tf_phone.view.tool.OperationSqlite;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Administrator
 * @version $Rev$
 * @time 2017-1-21 9:21
 * @des ${TODO}
 * @updateAuthor $Author$
 * @updateDate $Date$
 * @updateDes ${TODO}
 */
public class TelephonyMenuGvAdapter extends BaseAdapter {

    private Bitmap mBmp_head;
    private String mPhoneName;
    private String mPhoneNum;
    private Context mContext;
    private boolean mIsCollect;
    private MediaRecorder mMediaRecorder;

    int[] menuPic = new int[]{
            R.drawable.phone_answer_contacts,
            R.drawable.phone_answer_message_normal,
            R.drawable.phone_answer_keyboard_normal,
            R.drawable.selector_answer_collect,
            R.drawable.selector_answer_record,
            R.drawable.selector_answer_silence,
            R.drawable.selector_answer_hands_free,
            R.drawable.ic_lockscreen_decline_normal
    };
    String[] menuTitle = new String[]{
            "通讯录",
            "信息",
            "键盘",
            "收藏",
            "录音",
            "静音",
            "免提",
            "挂断"
    };
    private final AudioManager mAudioManager;

    public TelephonyMenuGvAdapter(Context context, String phoneNum, String phoneName, Bitmap bmp_head) {
        mContext = context;
        mPhoneNum = phoneNum;
        mPhoneName = phoneName;
        mBmp_head = bmp_head;
        mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
    }

    @Override
    public int getCount() {
        return menuPic.length;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = View.inflate(mContext, R.layout.item_telephony_menu, null);
            holder.phoneMenuTitle = (TextView) convertView.findViewById(R.id.item_telephony_menu_tv);
            holder.phoneMenuPic = (ImageView) convertView.findViewById(R.id.item_telephony_menu_iv);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.phoneMenuPic.setImageResource(menuPic[position]);
        holder.phoneMenuTitle.setText(menuTitle[position]);
        convertView.setOnClickListener(new ItemOnClickListener(position, holder));

        //初始化免提图标
        initHandsFree(position, holder);
        //初始化收藏图标
        initCollect(position, holder);
        return convertView;
    }

    private void initCollect(int position, ViewHolder holder) {
        if (menuTitle[position].equals("收藏")) {
            if (mPhoneNum != null) {
                SQLiteDatabase dbl = MyAppSqlite.db.getReadableDatabase();
                String sql = "select * from collect where phone=?";
                Cursor cursor = dbl.query("collect", null, "phone=?", new String[]{mPhoneNum}, null, null, null);
                cursor.moveToFirst();
                if (cursor.getCount() > 0) {
                    holder.phoneMenuPic.setBackgroundResource(R.drawable.phone_answer_collect_selector);
                    mIsCollect = true;
                } else {
                    holder.phoneMenuPic.setBackgroundResource(R.drawable.phone_answer_collect_normal);
                    mIsCollect = false;
                }

                if (cursor != null) {
                    cursor.close();
                    dbl.close();
                }
            }
        }

    }

    private void initHandsFree(int position, ViewHolder holder) {
        if (menuTitle[position].equals("免提")) {
            AudioManager mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
            //设置免提的图标
            if (mAudioManager.isSpeakerphoneOn()) {
                holder.phoneMenuPic.setBackgroundResource(R.drawable.phone_answer_hands_free_selector);
            } else {
                holder.phoneMenuPic.setBackgroundResource(R.drawable.phone_answer_hands_free_normal);
            }
        }

    }

    class ItemOnClickListener implements View.OnClickListener {
        private ViewHolder holder;
        private int position;

        public ItemOnClickListener(int position, ViewHolder holder) {
            this.position = position;
            this.holder = holder;
        }

        @Override
        public void onClick(View v) {
            switch (position) {
                case 0://通讯录
                    Toast.makeText(mContext, "通讯录", Toast.LENGTH_SHORT).show();
                    break;
                case 1://短信
                    Toast.makeText(mContext, "短信", Toast.LENGTH_SHORT).show();
                    break;
                case 2://键盘
                    Toast.makeText(mContext, "键盘", Toast.LENGTH_SHORT).show();
                    break;
                case 3://收藏
                    if (mIsCollect) {
                        OperationSqlite.DeleteFavorite(mPhoneNum);
                        //                            mAnswerCollectBt.setBackgroundResource(R.drawable.phone_answer_collect_normal);
                        //                            mIsCollect = false;
                    } else {
                        String headPortrait = BitmapString.bitmapToString(mContext, mBmp_head);
                        OperationSqlite.AddFavorite(mPhoneNum, mPhoneName, headPortrait);
                        //                            mAnswerCollectBt.setBackgroundResource(R.drawable.phone_answer_collect_selector);
                        //                            mIsCollect = true;
                    }
                    break;
                case 4://录音
                    try {
                        if (mMediaRecorder == null) {
                            //                            Environment.getExternalStorageDirectory()
                            //                            getCacheDir();
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日    HH:mm:ss");
                            Date date = new Date(System.currentTimeMillis());
                            String time = simpleDateFormat.format(date);
                            File file = new File(Environment.getExternalStorageDirectory(), "通话录音 : " + mPhoneNum + "--" + time + ".3gp");
                            mMediaRecorder = new MediaRecorder();
                            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);   //获得声音数据源
                            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);   // 按3gp格式输出
                            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                            mMediaRecorder.setOutputFile(file.getAbsolutePath());   //输出文件
                            mMediaRecorder.prepare();    //准备
                            mMediaRecorder.start();
                            holder.phoneMenuPic.setBackgroundResource(R.drawable.phone_answer_record_selector);
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case 5://静音
                    if (mAudioManager.isMicrophoneMute()) {
                        mAudioManager.setMicrophoneMute(false);
                        //                        telephonyMenu.setBackgroundResource(R.drawable.phone_answer_silence_normal);
                    } else {
                        mAudioManager.setMicrophoneMute(true);
                        //                        telephonyMenu.setBackgroundResource(R.drawable.phone_answer_silence_selector);
                    }
                    break;
                case 6://免提

                    mAudioManager.setMode(AudioManager.ROUTE_SPEAKER);
                    int currVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL);

                    if (!mAudioManager.isSpeakerphoneOn()) {
                        mAudioManager.setSpeakerphoneOn(true);
                        mAudioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL,
                                mAudioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL),
                                AudioManager.STREAM_VOICE_CALL);

                        //                            mHandsFree.setBackgroundResource(R.drawable.phone_answer_hands_free_selector);
                    } else {
                        mAudioManager.setSpeakerphoneOn(false);
                        mAudioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, currVolume,
                                AudioManager.STREAM_VOICE_CALL);
                        //                            mHandsFree.setBackgroundResource(R.drawable.phone_answer_hands_free_normal);
                    }
                    break;
                case 7://挂断
                    //                    PhoneUtil.endCall(TelephoneActivity.this);
                    //                    if (mMediaRecorder != null) {
                    //                        mMediaRecorder.stop();
                    //                        mMediaRecorder.release();
                    //                        mMediaRecorder = null;
                    //                    }
                    //                    if (mNotificationManager != null) {
                    //                        mNotificationManager.cancelAll();
                    //                    }
                    //                    finish();
                    break;
            }

        }
    }
}

class ViewHolder {
    ImageView phoneMenuPic;
    TextView phoneMenuTitle;
}

