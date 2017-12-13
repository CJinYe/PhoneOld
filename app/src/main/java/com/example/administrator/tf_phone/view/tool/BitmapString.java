package com.example.administrator.tf_phone.view.tool;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;

import java.io.IOException;

/**
 * Created by Administrator on 2016/12/20 0020.
 */

public class BitmapString {

    public static String bitmapToString(Context context, Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }
        //保存到系统图库 返回String类型uri
        String str_uri = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, null, null);
        Uri uri = Uri.parse(TextUtils.isEmpty(str_uri) ? "没有图片" : str_uri);
        if (uri == null) {
            return null;
        }
        String str = uri.toString();
        return str;
    }

    public static Bitmap stringToBitmap(Context context, String string) throws IOException {
        if (string == null) {
            return null;
        }
        Uri uri = Uri.parse((String) string);
        if (uri == null) {
            return null;
        }
        Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
        return bitmap;
    }
}
