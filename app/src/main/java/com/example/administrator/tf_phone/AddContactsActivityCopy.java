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

public class AddContactsActivityCopy extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "AddContactsActivity";
    private LinearLayout liner_compile_backage;
    private ImageButton cancle_compile, delete_compile, change_compile, save_compile, add_compile;
    private ImageButton compile_default1, compile_default2, compile_default3, compile_default4, compile_default5;
    private CircleImageView profile_image;
    private TakePhotoPopWin takePhotoPopWin;
    private EditText compile_username, compile_phone1, compile_phone2, compile_phone3,
            compile_phone4, compile_phone5;
    private String user_str;
    private String phoneinfo;
    private Drawable drawable;
    private Bitmap photo;
    private String mobile_number1, mobile_number2, mobile_number3, mobile_number4, mobile_number5;
    private boolean state;
    private boolean isDefaultOne;
    private Uri mUritempFile;

    protected void initContentView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_add_contacts);
        isDefaultOne = true;
        initView();
        UpdataSetDefault();
        initShow();
        setBackage();
    }

    private void setBackage() {
        Intent intent = getIntent();
        String phoneadd;
        phoneadd = intent.getStringExtra("phonetext");
        compile_phone1.setText(phoneadd);
        liner_compile_backage.setBackgroundResource(R.drawable.add_contacts_backage);
    }

    private void getDatas() {
        user_str = compile_username.getText().toString().trim();
        mobile_number1 = compile_phone1.getText().toString().trim();
        mobile_number2 = compile_phone2.getText().toString().trim();
        mobile_number3 = compile_phone3.getText().toString().trim();
        mobile_number4 = compile_phone4.getText().toString().trim();
        mobile_number5 = compile_phone5.getText().toString().trim();

    }

    private void initShow() {
        delete_compile.setVisibility(View.GONE);
        cancle_compile.setOnClickListener(this);
        change_compile.setOnClickListener(this);
        save_compile.setOnClickListener(this);
        compile_default1.setOnClickListener(this);
        compile_default2.setOnClickListener(this);
        compile_default3.setOnClickListener(this);
        compile_default4.setOnClickListener(this);
        compile_default5.setOnClickListener(this);
        add_compile.setOnClickListener(this);
        compile_default1.setBackgroundResource(R.drawable.set_defaultt);
        state = true;
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
        compile_phone1 = (EditText) this.findViewById(R.id.compile_phone1);
        compile_phone2 = (EditText) this.findViewById(R.id.compile_phone2);
        compile_phone3 = (EditText) this.findViewById(R.id.compile_phone3);
        compile_phone4 = (EditText) this.findViewById(R.id.compile_phone4);
        compile_phone5 = (EditText) this.findViewById(R.id.compile_phone5);
        compile_default1 = (ImageButton) this.findViewById(R.id.compile_default1);
        compile_default2 = (ImageButton) this.findViewById(R.id.compile_default2);
        compile_default3 = (ImageButton) this.findViewById(R.id.compile_default3);
        compile_default4 = (ImageButton) this.findViewById(R.id.compile_default4);
        compile_default5 = (ImageButton) this.findViewById(R.id.compile_default5);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancle_compile:
                if ( Constants.isMainAddContacts ){
                    Intent intent = new Intent(AddContactsActivityCopy.this,MainActivity.class);
                    startActivity(intent);
                }
                AddContactsActivityCopy.this.finish();
                break;
            case R.id.change_compile:
                showPopFormBottom(v);
                break;
            case R.id.save_compile:
                isUpdateContacts = true;//通知主页面修改了通讯录,更改通话记录的名称
                if (isDefaultOne) {
                    phoneinfo = compile_phone1.getText().toString().trim();
                }
                getDatas();
                InsertState();
                if ( Constants.isMainAddContacts ){
                    Intent intent = new Intent(AddContactsActivityCopy.this,MainActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.compile_default1:
                UpdataSetDefault();
                compile_default1.setBackgroundResource(R.drawable.set_defaultt);
                phoneinfo = compile_phone1.getText().toString().trim();
                SetSate();
                break;
            case R.id.compile_default2:
                UpdataSetDefault();
                compile_default2.setBackgroundResource(R.drawable.set_defaultt);
                phoneinfo = compile_phone2.getText().toString().trim();
                SetSate();
                break;
            case R.id.compile_default3:
                UpdataSetDefault();
                compile_default3.setBackgroundResource(R.drawable.set_defaultt);
                phoneinfo = compile_phone3.getText().toString().trim();
                SetSate();
                break;
            case R.id.compile_default4:
                UpdataSetDefault();
                compile_default4.setBackgroundResource(R.drawable.set_defaultt);
                phoneinfo = compile_phone4.getText().toString().trim();
                SetSate();
                break;
            case R.id.compile_default5:
                UpdataSetDefault();
                compile_default5.setBackgroundResource(R.drawable.set_defaultt);
                phoneinfo = compile_phone5.getText().toString().trim();
                SetSate();
                break;
            case R.id.add_compile:
                isUpdateContacts = true;//通知主页面修改了通讯录,更改通话记录的名称
                if (isDefaultOne) {
                    phoneinfo = compile_phone1.getText().toString().trim();
                }
                getDatas();
                if (TextUtils.isEmpty(user_str)) {
                    ToastUtil.showPicToast(this, "姓名为空");
                    return;
                } else if (TextUtils.isEmpty(mobile_number1) & TextUtils.isEmpty(mobile_number2) & TextUtils.isEmpty(mobile_number3)
                        & TextUtils.isEmpty(mobile_number5) & TextUtils.isEmpty(mobile_number4)) {
                    ToastUtil.showPicToast(this, "号码为空");
                    return;
                } else if (state == false) {
                    ToastUtil.showPicToast(this, "请设置默认号码");
                    return;
                }
                //插入数据
                OperationSqlite.InsertDatas(this, user_str, phoneinfo, photo, null, null);
                //                if (TextUtils.isEmpty(user_str)) {
                //                    ToastUtil.showPicToast(this, "姓名为空");
                //                    return;
                //                } else if (TextUtils.isEmpty(mobile_number1) & TextUtils.isEmpty(mobile_number2) & TextUtils.isEmpty(mobile_number3)
                //                        & TextUtils.isEmpty(mobile_number5) & TextUtils.isEmpty(mobile_number4)) {
                //                    ToastUtil.showPicToast(this, "号码为空");
                //                    return;
                //                } else if (state == false) {
                //                    ToastUtil.showPicToast(this, "请设置默认号码");
                //                    return;
                //                }
                SetSate();
                AddInfoSqlite();
                if ( Constants.isMainAddContacts ){
                    Intent intent = new Intent(AddContactsActivityCopy.this,MainActivity.class);
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
        OperationSqlite.AddFavorite(phoneinfo, user_str, headPortrait);
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
                    PhotoView.getPhotoGraph(AddContactsActivityCopy.this);
                    takePhotoPopWin.dismiss();
                    break;
                case R.id.btn_pick_photo:
                    PhotoView.getMapdepot(AddContactsActivityCopy.this);
                    takePhotoPopWin.dismiss();
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 默认状态
     */
    private void SetSate() {
        if (TextUtils.isEmpty(phoneinfo)) {
            ToastUtil.showPicToast(this, "号码为空，设置失败");
            UpdataSetDefault();
            state = false;
            return;
        }
        state = true;
        isDefaultOne = false;
    }

    /**
     * 插入状态
     */
    private void InsertState() {
        if (TextUtils.isEmpty(user_str)) {
            ToastUtil.showPicToast(this, "姓名为空");
            return;
        } else if (TextUtils.isEmpty(mobile_number1) & TextUtils.isEmpty(mobile_number2) & TextUtils.isEmpty(mobile_number3)
                & TextUtils.isEmpty(mobile_number5) & TextUtils.isEmpty(mobile_number4)) {
            ToastUtil.showPicToast(this, "号码为空");
            return;
        } else if (state == false) {
            ToastUtil.showPicToast(this, "请设置默认号码");
            return;
        } else if (TextUtils.isEmpty(phoneinfo)) {
            ToastUtil.showPicToast(this, "默认号码为空");
            return;
        }
        //插入数据
        OperationSqlite.InsertDatas(this, user_str, phoneinfo, photo, null, null);
        ToastUtil.showPicToast(this, "保存成功");

        AddContactsActivityCopy.this.finish();
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

    /**
     * 更新默认图片
     */
    private void UpdataSetDefault() {

        compile_default1.setBackgroundResource(R.drawable.defaultt);
        compile_default2.setBackgroundResource(R.drawable.defaultt);
        compile_default3.setBackgroundResource(R.drawable.defaultt);
        compile_default4.setBackgroundResource(R.drawable.defaultt);
        compile_default5.setBackgroundResource(R.drawable.defaultt);
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
        if (keyCode == KeyEvent.KEYCODE_BACK){
            if ( Constants.isMainAddContacts ){
                Intent intent = new Intent(AddContactsActivityCopy.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
