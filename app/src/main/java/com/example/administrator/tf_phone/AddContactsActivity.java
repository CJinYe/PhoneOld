package com.example.administrator.tf_phone;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.example.administrator.tf_phone.conf.Constants;
import com.example.administrator.tf_phone.view.BaseActivity;
import com.example.administrator.tf_phone.view.TakePhotoPopWin;
import com.example.administrator.tf_phone.view.tool.BitmapString;
import com.example.administrator.tf_phone.view.tool.OperationSqlite;
import com.example.administrator.tf_phone.view.tool.PhotoView;
import com.example.administrator.tf_phone.view.tool.ToastUtil;

import java.io.File;
import java.io.FileNotFoundException;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.administrator.tf_phone.conf.Constants.isUpdateContacts;
import static com.example.administrator.tf_phone.view.tool.MyAppApiConfig.CAMERA_REQUEST_CODE;
import static com.example.administrator.tf_phone.view.tool.MyAppApiConfig.IMAGE_FILE_NAME;
import static com.example.administrator.tf_phone.view.tool.MyAppApiConfig.IMAGE_REQUEST_CODE;
import static com.example.administrator.tf_phone.view.tool.MyAppApiConfig.RESULT_REQUEST_CODE;

public class AddContactsActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "AddContactsActivity";
    private LinearLayout liner_compile_backage;
    private ImageButton cancle_compile, delete_compile, change_compile, save_compile, add_compile;
    private CircleImageView profile_image;
    private TakePhotoPopWin takePhotoPopWin;
    private EditText compile_username, compile_phone, compile_city, compile_note;
    private String user_str;
    private Drawable drawable;
    private Bitmap photo;
    private String mobile_number, mobile_city, mobile_note;
    private Uri mUritempFile;

    protected void initContentView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_add_contacts);
        initView();
        initShow();
        setBackage();
    }

    private void setBackage() {
        Intent intent = getIntent();
        String phoneadd;
        phoneadd = intent.getStringExtra("phonetext");
        compile_phone.setText(phoneadd);
        liner_compile_backage.setBackgroundResource(R.drawable.add_contacts_backage);
    }

    private void getDatas() {
        user_str = compile_username.getText().toString().trim();
        mobile_number = compile_phone.getText().toString().trim();
        mobile_city = compile_city.getText().toString().trim();
        mobile_note = compile_note.getText().toString().trim();

    }

    private void initShow() {
        delete_compile.setVisibility(View.GONE);
        cancle_compile.setOnClickListener(this);
        change_compile.setOnClickListener(this);
        save_compile.setOnClickListener(this);
        add_compile.setOnClickListener(this);
    }

    private void initView() {
        liner_compile_backage = (LinearLayout) this.findViewById(R.id.liner_compile_backage);
        cancle_compile = (ImageButton) this.findViewById(R.id.cancle_compile);
        delete_compile = (ImageButton) this.findViewById(R.id.delete_compile);
        change_compile = (ImageButton) this.findViewById(R.id.change_compile);
        save_compile = (ImageButton) this.findViewById(R.id.save_compile);
        add_compile = (ImageButton) this.findViewById(R.id.add_compile);
        profile_image = (CircleImageView) this.findViewById(R.id.profile_image);
        compile_username = (EditText) this.findViewById(R.id.compile_username);
        compile_phone = (EditText) this.findViewById(R.id.compile_phone1);
        compile_city = (EditText) this.findViewById(R.id.compile_phone2);
        compile_note = (EditText) this.findViewById(R.id.compile_phone3);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancle_compile:
                if (Constants.isMainAddContacts) {
                    Intent intent = new Intent(AddContactsActivity.this, MainActivity.class);
                    startActivity(intent);
                }
                AddContactsActivity.this.finish();
                break;
            case R.id.change_compile:
                showPopFormBottom(v);
                break;
            case R.id.save_compile:
                isUpdateContacts = true;//通知主页面修改了通讯录,更改通话记录的名称
                getDatas();
                InsertState();
                if (Constants.isMainAddContacts) {
                    Intent intent = new Intent(AddContactsActivity.this, MainActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.add_compile:
                isUpdateContacts = true;//通知主页面修改了通讯录,更改通话记录的名称
                getDatas();
                if (TextUtils.isEmpty(user_str)) {
                    ToastUtil.showPicToast(this, "姓名为空");
                    return;
                } else if (TextUtils.isEmpty(mobile_number)) {
                    ToastUtil.showPicToast(this, "号码为空");
                    return;
                }
                //插入数据
                OperationSqlite.InsertDatas(this, user_str, mobile_number, photo,mobile_city,mobile_note);
                AddInfoSqlite();
                if (Constants.isMainAddContacts) {
                    Intent intent = new Intent(AddContactsActivity.this, MainActivity.class);
                    startActivity(intent);
                }
                finish();
                break;
            default:
                break;
        }
    }

    private void AddInfoSqlite() {
        profile_image.setDrawingCacheEnabled(true);
        Bitmap bit = Bitmap.createBitmap(profile_image.getDrawingCache());
        profile_image.setDrawingCacheEnabled(false);
        String headPortrait = BitmapString.bitmapToString(this, bit);
        OperationSqlite.AddFavorite(mobile_number, user_str, headPortrait);
        ToastUtil.showPicToast(this, "收藏成功");
    }

    public void showPopFormBottom(View view) {
        takePhotoPopWin = new TakePhotoPopWin(this, onClickListener);
        takePhotoPopWin.showAtLocation(findViewById(R.id.change_compile), Gravity.CENTER, 0, 0);
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_take_photo:
                    PhotoView.getPhotoGraph(AddContactsActivity.this);
                    takePhotoPopWin.dismiss();
                    break;
                case R.id.btn_pick_photo:
                    PhotoView.getMapdepot(AddContactsActivity.this);
                    takePhotoPopWin.dismiss();
                    break;
                default:
                    break;
            }
        }
    };


    /**
     * 插入状态
     */
    private void InsertState() {
        if (TextUtils.isEmpty(user_str)) {
            ToastUtil.showPicToast(this, "姓名为空");
            return;
        } else if (TextUtils.isEmpty(mobile_number)) {
            ToastUtil.showPicToast(this, "号码为空");
            return;
        }
        //插入数据
        OperationSqlite.InsertDatas(this, user_str, mobile_number, photo, mobile_city, mobile_note);
        ToastUtil.showPicToast(this, "保存成功");

        AddContactsActivity.this.finish();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_CANCELED) {
            switch (requestCode) {
                case IMAGE_REQUEST_CODE:
                    StartPhotoZoom(data.getData());
                    break;
                case CAMERA_REQUEST_CODE:
                    String state = Environment.getExternalStorageState();
                    if (state.equals(Environment.MEDIA_MOUNTED)) {
                        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
                        File tempFile = new File(path, IMAGE_FILE_NAME);
                        StartPhotoZoom(Uri.fromFile(tempFile));
                    } else {
                        ToastUtil.showPicToast(this, "未找到存储卡，无法存储照片！");
                    }
                    break;
                case RESULT_REQUEST_CODE: // 图片缩放完成后
                    if (data != null) {
                        getImageToView(data);
                    }
                    break;
                default:
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    /**
     * 裁剪图片方法实现
     */
    public void StartPhotoZoom(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 500);
        //        intent.putExtra("return-data", true);
        intent.putExtra("outputY", 500);
        //        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        //        intent.putExtra("noFaceDetection", false);
        //        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);

        //uritempFile为Uri类变量，实例化uritempFile
        mUritempFile = Uri.parse("file://" + "/" + Environment.getExternalStorageDirectory().getPath() + "/" + "small.jpg");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mUritempFile);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());

        startActivityForResult(intent, RESULT_REQUEST_CODE);

        //        String path = "";
        //        int sdkVersion = Integer.valueOf(android.os.Build.VERSION.SDK);
        //        if (sdkVersion > 19) {
        //            path = uri.getPath();//5.0直接返回的是图片路径，5.0以下是一个和数据库有关的索引值
        //        } else {
        //            String[] proj = { MediaStore.Images.Media.DATA };
        //            Cursor cursor = getContentResolver().query(uri,
        //                    proj, null, null, null);
        //            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        //            cursor.moveToFirst();
        //            path = cursor.getString(column_index);
        //        }
        //        BitmapFactory.Options opts = new BitmapFactory.Options();
        //        opts.inSampleSize = 1;
        //        Bitmap picture = BitmapFactory.decodeFile(path, opts);
        //        photo = picture;


    }

    /**
     * 保存裁剪之后的图片数据
     */
    private void getImageToView(Intent data) {
        Bundle extras = data.getExtras();
        if (extras != null) {
            //            photo = extras.getParcelable("data");
            try {
                photo = BitmapFactory.decodeStream(getContentResolver().openInputStream(mUritempFile));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            //                    photo = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
            //                photo = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
            drawable = new BitmapDrawable(this.getResources(), photo);
            profile_image.setImageDrawable(drawable);
        } else {

        }

    }


    protected void onDestroy() {
        super.onDestroy();
        if (photo != null && !photo.isRecycled()) {
            // 回收并且置为null
            photo.recycle();
            photo = null;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (Constants.isMainAddContacts) {
                Intent intent = new Intent(AddContactsActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
