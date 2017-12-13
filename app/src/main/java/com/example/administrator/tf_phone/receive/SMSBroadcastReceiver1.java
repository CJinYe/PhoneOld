package com.example.administrator.tf_phone.receive;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsMessage;

import com.example.administrator.tf_phone.view.tool.ToastUtil;

import java.text.SimpleDateFormat;
import java.util.Date;

import static android.provider.Telephony.Sms.Intents.SMS_RECEIVED_ACTION;

/**
 * @author Administrator
 * @version $Rev$
 * @time 2017-2-11 15:21
 * @des ${TODO}
 * @updateAuthor $Author$
 * @updateDate $Date$
 * @updateDes ${TODO}
 */
public class SMSBroadcastReceiver1 extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
//        Toast.makeText(context,"您有新的小心,请注意查收",Toast.LENGTH_LONG).show();
        ToastUtil.showLongPicToast(context, "您有新的消息,请注意查收！");
        //        if (intent.getAction().equals(SMS_RECEIVED_ACTION)) {
        //            Object[] pdus = (Object[]) intent.getExtras().get("pdus");
        //            for (Object pdu : pdus) {
        //                SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdu);
        //                String address = smsMessage.getDisplayOriginatingAddress();
        //                //短信内容
        //                String body = smsMessage.getDisplayMessageBody();
        //                long date = smsMessage.getTimestampMillis();
        //                Date tiemDate = new Date(date);
        //                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //                String time = simpleDateFormat.format(tiemDate);
        //                PhoneMessage phoneMessage = new PhoneMessage();
        //            }
        //        }


//        if (Build.VERSION.SDK_INT >= 20) {

            if (intent.getAction().equals(SMS_RECEIVED_ACTION)) {
                StringBuffer SMSAddress = new StringBuffer();
                StringBuffer SMSContent = new StringBuffer();
                String time = "";
                String address = "";
                long date = 0;
                Bundle bundle = intent.getExtras();
                if (bundle != null) {
                    Object[] pdusObjects = (Object[]) bundle.get("pdus");
                    SmsMessage[] messages = new SmsMessage[pdusObjects.length];
                    for (int i = 0; i < pdusObjects.length; i++) {
                        messages[i] = SmsMessage
                                .createFromPdu((byte[]) pdusObjects[i]);
                        SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdusObjects[i]);
                        address = smsMessage.getDisplayOriginatingAddress();
                    }


                    for (SmsMessage message : messages) {
                        SMSAddress.append(message.getDisplayOriginatingAddress());
                        SMSContent.append(message.getDisplayMessageBody());
                        date = message.getTimestampMillis();
                        Date tiemDate = new Date(date);
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd HH:mm:ss");
                        time = simpleDateFormat.format(tiemDate);

                    }

                    String smsPackName = null;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                        smsPackName = Telephony.Sms.getDefaultSmsPackage(context);
                        if (smsPackName != null) {
                            if (smsPackName.equals(context.getPackageName())) {
                                ContentResolver cr = context.getContentResolver();
                                ContentValues values = new ContentValues();
                                values.put("address", address);
                                //                values.put("address","95599");
                                values.put("type", 1);
                                values.put("date", date);
                                values.put("read", 0); // 0：未读； 1：已读
                                values.put("body", SMSContent.toString());
                                //                values.put("body", "【中国农业银行】您尾号为9173的农行账户于02月13日09时58分完成了一笔转存交易,金额为100,000,000.00,余额为100,009,707.00");
                                cr.insert(Uri.parse("content://sms/"), values);
                            }
                        }
                    }

//                    ContentResolver cr = context.getContentResolver();
//                    ContentValues values = new ContentValues();
//                    values.put("address", address);
//                    //                values.put("address","95599");
//                    values.put("type", 1);
//                    values.put("date", date);
//                    values.put("read", 0); // 0：未读； 1：已读
//                    values.put("body", SMSContent.toString());
//                    //                values.put("body", "【中国农业银行】您尾号为9173的农行账户于02月13日09时58分完成了一笔转存交易,金额为100,000,000.00,余额为100,009,707.00");
//                    cr.insert(Uri.parse("content://sms/"), values);
                }
            }
        }
//    }
}
