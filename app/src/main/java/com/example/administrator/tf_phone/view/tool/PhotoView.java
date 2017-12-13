package com.example.administrator.tf_phone.view.tool;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.File;

import static com.example.administrator.tf_phone.view.tool.MyAppApiConfig.CAMERA_REQUEST_CODE;
import static com.example.administrator.tf_phone.view.tool.MyAppApiConfig.IMAGE_FILE_NAME;
import static com.example.administrator.tf_phone.view.tool.MyAppApiConfig.IMAGE_REQUEST_CODE;

/**
 * Created by Administrator on 2016/12/15 0015.
 * 拍照、图库
 */

public class PhotoView {

    //拍照
    public static void getPhotoGraph(Activity activity) {
        Intent intentFromCapture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // 判断存储卡是否可以用，可用进行存储
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
            File file = new File(path, IMAGE_FILE_NAME);
            intentFromCapture.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
        }
        activity.startActivityForResult(intentFromCapture, CAMERA_REQUEST_CODE);
    }

    //图库
    public static void getMapdepot(Activity activity) {
        Intent intentFromGallery = new Intent();
        intentFromGallery.setType("image/*"); // 设置文件类型
        intentFromGallery.setAction(Intent.ACTION_GET_CONTENT);
//        intentFromGallery.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
//                "image/*");
        activity.startActivityForResult(intentFromGallery, IMAGE_REQUEST_CODE);
    }
}
