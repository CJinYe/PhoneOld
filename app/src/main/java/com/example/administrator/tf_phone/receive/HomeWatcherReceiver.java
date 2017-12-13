package com.example.administrator.tf_phone.receive;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import static com.example.administrator.tf_phone.activity.TelephoneActivity.isOnPause;

/**
 * @author Administrator
 * @version $Rev$
 * @time 2017-2-14 11:58
 * @des ${TODO}
 * @updateAuthor $Author$
 * @updateDate $Date$
 * @updateDes ${TODO}
 */
public class HomeWatcherReceiver extends BroadcastReceiver {
    private static final String LOG_TAG = "HomeReceiver";
    private static final String SYSTEM_DIALOG_REASON_KEY = "reason";
    private static final String SYSTEM_DIALOG_REASON_RECENT_APPS = "recentapps";
    private static final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";
    private static final String SYSTEM_DIALOG_REASON_LOCK = "lock";
    private static final String SYSTEM_DIALOG_REASON_ASSIST = "assist";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.i(LOG_TAG, "onReceive: action: " + action);
        if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
            // android.intent.action.CLOSE_SYSTEM_DIALOGS
            String reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);

            if (SYSTEM_DIALOG_REASON_HOME_KEY.equals(reason)) {
                // 短按Home键
                isOnPause = false;

            }
            else if (SYSTEM_DIALOG_REASON_RECENT_APPS.equals(reason)) {
                isOnPause = false;
                // 长按Home键 或者 activity切换键

            }
            else if (SYSTEM_DIALOG_REASON_LOCK.equals(reason)) {
                isOnPause = false;
                // 锁屏
            }
            else if (SYSTEM_DIALOG_REASON_ASSIST.equals(reason)) {
                isOnPause = false;
                // samsung 长按Home键
            }

        }
    }

}
