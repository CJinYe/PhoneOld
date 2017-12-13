package com.example.administrator.tf_phone.receive;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

/**
 * @author Administrator
 * @version $Rev$
 * @time 2017-2-7 17:34
 * @des ${TODO}
 * @updateAuthor $Author$
 * @updateDate $Date$
 * @updateDes ${TODO}
 */
public class SMSBroadcastReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context,"您有新的消息,请注意查收", Toast.LENGTH_LONG);
        Log.d("SMSBroadcastReceiver","SMSBroadcastReceiver");
    }
}
