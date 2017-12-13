package com.example.administrator.tf_phone;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.administrator.tf_phone.conf.Constants;
import com.example.administrator.tf_phone.sqldb.MyAppSqlite;
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

/**
 * 个人编辑界面
 */
public class CompileActivityCopy extends BaseActivity implements View.OnClickListener {

    private ImageButton cancle_compile, save_compile, add_compile, change_compile, delete_compile;
    private CircleImageView profile_image;
    private EditText compile_username, compile_phone1, compile_phone2,
            compile_phone3, compile_phone4, compile_phone5;
    private String user, phone, city ,note;
    private ImageButton compile_default1, compile_default2, compile_default3, compile_default4, compile_default5;
    private Bitmap photo;
    private Drawable drawable;
    private TakePhotoPopWin takePhotoPopWin;
    private boolean state;
    private String phoneinfo;
    private boolean isDefaultOne;
    private long mId;
    private Bitmap mBitmapNormal;
    private Uri mUritempFile;

    protected void initContentView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_compile);
        isDefaultOne = true;
        getDatas();
        initView();
        initShow();
        EditChange();
    }

    private void getDatas() {
        Bundle bundle = getIntent().getExtras();
        user = (String) bundle.get("name");
        phone = (String) bundle.get("phone");
        mId = (long) bundle.get("id");
        mBitmapNormal = bundle.getParcelable("image");
        city = (String) bundle.get("city");
        note = (String) bundle.get("note");
        photo = mBitmapNormal;
        Log.d("COmpile","city = "+city+"   ,note = "+note);
    }

    private void initView() {
        cancle_compile = (ImageButton) this.findViewById(R.id.cancle_compile);
        save_compile = (ImageButton) this.findViewById(R.id.save_compile);
        add_compile = (ImageButton) this.findViewById(R.id.add_compile);
        change_compile = (ImageButton) this.findViewById(R.id.change_compile);
        delete_compile = (ImageButton) this.findViewById(R.id.delete_compile);
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
        profile_image.setImageBitmap(mBitmapNormal);
        compile_username.setText(user);
        compile_phone1.setText(phone);
    }

    private void initShow() {
        cancle_compile.setOnClickListener(this);
        save_compile.setOnClickListener(this);
        add_compile.setOnClickListener(this);
        delete_compile.setOnClickListener(this);
        change_compile.setOnClickListener(this);
        state = true;
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancle_compile:
                //                OperationSqlite.InsertDatas(this,user,phone,mBitmapNormal);
                if ( Constants.isMainAddContacts ){
                    Intent intent = new Intent(CompileActivityCopy.this,MainActivity.class);
                    startActivity(intent);
                }
                CompileActivityCopy.this.finish();
                break;
            case R.id.save_compile:
                if (isDefaultOne) {
                    phoneinfo = compile_phone1.getText().toString().trim();
                }
                OperationSqlite.DeleteId(this,mId);
                if(isCollect(phone)){
                    String user_info = compile_username.getText().toString().trim();
                    String headPortrait = BitmapString.bitmapToString(this, photo);
                    OperationSqlite.DeleteFavorite(phone);
                    OperationSqlite.AddFavorite(phoneinfo,user_info,headPortrait);
                }
                isUpdateContacts = true;//通知主页面修改了通讯录,更改通话记录的名称
                UserInfo();
                break;
            case R.id.compile_default1:
                UpdataSetDefault();
                phoneinfo = compile_phone1.getText().toString().trim();
                compile_default1.setBackgroundResource(R.drawable.set_defaultt);
                SetSate();
                break;
            case R.id.compile_default2:
                UpdataSetDefault();
                phoneinfo = compile_phone2.getText().toString().trim();
                compile_default2.setBackgroundResource(R.drawable.set_defaultt);
                SetSate();
                break;
            case R.id.compile_default3:
                UpdataSetDefault();
                phoneinfo = compile_phone3.getText().toString().trim();
                compile_default3.setBackgroundResource(R.drawable.set_defaultt);
                SetSate();
                break;
            case R.id.compile_default4:
                UpdataSetDefault();
                phoneinfo = compile_phone4.getText().toString().trim();
                compile_default4.setBackgroundResource(R.drawable.set_defaultt);
                SetSate();
                break;
            case R.id.compile_default5:
                UpdataSetDefault();
                phoneinfo = compile_phone5.getText().toString().trim();
                compile_default5.setBackgroundResource(R.drawable.set_defaultt);
                SetSate();
                break;
            case R.id.add_compile:
                OperationSqlite.DeleteId(this,mId);
                OperationSqlite.DeleteFavorite(phone);
                isUpdateContacts = true;//通知主页面修改了通讯录,更改通话记录的名称
                if (isDefaultOne) {
                    phoneinfo = compile_phone1.getText().toString().trim();
                }
                AddFavorite();
                UserInfo();
                break;
            case R.id.change_compile:
                showPopFormBottom(v);
                break;
            case R.id.delete_compile:

                if (isCollect(phone)){
                    OperationSqlite.DeleteFavorite(phone);
                }

                OperationSqlite.DeleteId(this, mId);
                ToastUtil.showPicToast(this, "删除成功");
                CompileActivityCopy.this.finish();
                break;
            default:
                break;
        }
    }

    /**
     * 默认状态
     */
    private void SetSate() {
        if (TextUtils.isEmpty(phoneinfo)) {
            ToastUtil.showPicToast(getApplicationContext(), "号码为空，设置失败");
            UpdataSetDefault();
            state = false;
            return;
        }
        state = true;
        isDefaultOne = false;
    }

    private void UserInfo() {
        String user_info = compile_username.getText().toString().trim();
        String phone1 = compile_phone1.getText().toString().trim();
        String phone2 = compile_phone2.getText().toString().trim();
        String phone3 = compile_phone3.getText().toString().trim();
        String phone4 = compile_phone4.getText().toString().trim();
        String phone5 = compile_phone5.getText().toString().trim();
        if (TextUtils.isEmpty(user_info)) {
            ToastUtil.showPicToast(this, "姓名为空!");
            return;
        } else if (TextUtils.isEmpty(phone1) & TextUtils.isEmpty(phone2) & TextUtils.isEmpty(phone3)
                & TextUtils.isEmpty(phone4) & TextUtils.isEmpty(phone5)) {
            ToastUtil.showPicToast(this, "号码为空!");
            return;
        } else if (state == false) {
            ToastUtil.showPicToast(this, "请设置默认号码");
            return;
        }
        OperationSqlite.InsertDatas(this, user_info, phoneinfo, photo, city, note);
        ToastUtil.showPicToast(this, "修改成功！");
        CompileActivityCopy.this.finish();
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

    /**
     * 添加至收藏夹
     */
    private void AddFavorite() {
        Bundle bundle = getIntent().getExtras();
        Bitmap bitmap = bundle.getParcelable("image");
        String headPortrait = BitmapString.bitmapToString(this, photo);
        String user_info = compile_username.getText().toString().trim();
        OperationSqlite.AddFavorite(phoneinfo, user_info, headPortrait);
        ToastUtil.showPicToast(this, "添加成功");
    }

    public void showPopFormBottom(View view) {
        takePhotoPopWin = new TakePhotoPopWin(this, onClickListener);
        takePhotoPopWin.showAtLocation(findViewById(R.id.change_compile), Gravity.CENTER, 0, 0);
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_take_photo:
                    PhotoView.getPhotoGraph(CompileActivityCopy.this);
                    takePhotoPopWin.dismiss();
                    break;
                case R.id.btn_pick_photo:
                    PhotoView.getMapdepot(CompileActivityCopy.this);
                    takePhotoPopWin.dismiss();
                    break;
                default:
                    break;
            }
        }
    };

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
                        ToastUtil.showPicToast(getApplicationContext(), "未找到存储卡，无法存储照片！");
                    }
                    break;
                case RESULT_REQUEST_CODE:
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
        intent.putExtra("outputY", 500);
        //        intent.putExtra("return-data", true);
        mUritempFile = Uri.parse("file://" + "/" + Environment.getExternalStorageDirectory().getPath() + "/" + "small.jpg");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mUritempFile);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        startActivityForResult(intent, RESULT_REQUEST_CODE);
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
            drawable = new BitmapDrawable(this.getResources(), photo);
            profile_image.setImageDrawable(drawable);
        }else {
            photo = mBitmapNormal;
        }
    }

    private void EditChange() {
        compile_username.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            public void afterTextChanged(Editable editable) {
                //                OperationSqlite.DeleteUserName(CompileActivity.this, user);
                //                OperationSqlite.DeleteId(CompileActivity.this, mId);
            }
        });
        compile_phone1.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            public void afterTextChanged(Editable editable) {
                //                OperationSqlite.DeleteId(CompileActivity.this, mId);
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode==KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_MOVE_HOME){
            ToastUtil.showPicToast(this, ""+keyCode);
            Log.d("keykey",keyCode+"");
            //            OperationSqlite.InsertDatas(this,user,phone,mBitmapNormal);
        }
        return super.onKeyDown(keyCode, event);
    }

    private boolean isCollect(String phoneNum) {

        SQLiteDatabase dbl = MyAppSqlite.db.getReadableDatabase();
        String sql = "select * from collect where phone=?";
        Cursor cursor = dbl.query("collect", null, "phone=?", new String[]{phoneNum}, null, null, null);
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            cursor.close();
            dbl.close();
            return true;
        } else {
            cursor.close();
            dbl.close();
            return false;
        }
    }
}