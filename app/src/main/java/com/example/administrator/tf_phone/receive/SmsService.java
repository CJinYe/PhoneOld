package com.example.administrator.tf_phone.receive;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
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
public class SmsService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Toast.makeText(getApplication(),"您有新的消息,请注意查收", Toast.LENGTH_LONG);
        Log.d("SMSBroadcastReceiver","SmsService");
        return null;
    }
}
