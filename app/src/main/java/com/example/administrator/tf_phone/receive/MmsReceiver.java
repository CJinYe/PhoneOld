package com.example.administrator.tf_phone.receive;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

/**
 * @author Administrator
 * @version $Rev$
 * @time 2017-2-9 14:08
 * @des ${TODO}
 * @updateAuthor $Author$
 * @updateDate $Date$
 * @updateDes ${TODO}
 */
public class MmsReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context,"您有新的消息MmsReceiver", Toast.LENGTH_LONG);
        Log.d("SMSBroadcastReceiver","MmsReceiver");
    }
}
