package com.example.administrator.tf_phone;

import android.app.Dialog;
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
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

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
public class CompileActivity extends BaseActivity implements View.OnClickListener {

    private ImageButton cancle_compile, save_compile, add_compile, change_compile, delete_compile;
    private CircleImageView profile_image;
    private EditText compile_username, compile_phone, compile_city,
            compile_note;
    private String user, phone, city, note;
    private Bitmap photo;
    private Drawable drawable;
    private TakePhotoPopWin takePhotoPopWin;
    private String phoneinfo;
    private long mId;
    private Bitmap mBitmapNormal;
    private Uri mUritempFile;

    protected void initContentView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_compile);
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
    }

    private void initView() {
        cancle_compile = (ImageButton) this.findViewById(R.id.cancle_compile);
        save_compile = (ImageButton) this.findViewById(R.id.save_compile);
        add_compile = (ImageButton) this.findViewById(R.id.add_compile);
        change_compile = (ImageButton) this.findViewById(R.id.change_compile);
        delete_compile = (ImageButton) this.findViewById(R.id.delete_compile);
        profile_image = (CircleImageView) this.findViewById(R.id.profile_image);
        compile_username = (EditText) this.findViewById(R.id.compile_username);
        compile_phone = (EditText) this.findViewById(R.id.compile_phone1);
        compile_city = (EditText) this.findViewById(R.id.compile_phone2);
        compile_note = (EditText) this.findViewById(R.id.compile_phone3);
        profile_image.setImageBitmap(mBitmapNormal);
        compile_username.setText(user);
        compile_phone.setText(phone);
        compile_city.setText(city);
        compile_note.setText(note);
    }

    private void initShow() {
        cancle_compile.setOnClickListener(this);
        save_compile.setOnClickListener(this);
        add_compile.setOnClickListener(this);
        delete_compile.setOnClickListener(this);
        change_compile.setOnClickListener(this);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancle_compile:
                //                OperationSqlite.InsertDatas(this,user,phone,mBitmapNormal);
                if (Constants.isMainAddContacts) {
                    Intent intent = new Intent(CompileActivity.this, MainActivity.class);
                    startActivity(intent);
                }
                CompileActivity.this.finish();
                break;
            case R.id.save_compile:
                phoneinfo = compile_phone.getText().toString().trim();
                OperationSqlite.DeleteId(this, mId);
                if (isCollect(phone)) {
                    String user_info = compile_username.getText().toString().trim();
                    String headPortrait = BitmapString.bitmapToString(this, photo);
                    OperationSqlite.DeleteFavorite(phone);
                    OperationSqlite.AddFavorite(phoneinfo, user_info, headPortrait);
                }
                isUpdateContacts = true;//通知主页面修改了通讯录,更改通话记录的名称
                UserInfo();
                break;
            case R.id.add_compile:
                OperationSqlite.DeleteId(this, mId);
                OperationSqlite.DeleteFavorite(phone);
                isUpdateContacts = true;//通知主页面修改了通讯录,更改通话记录的名称
                phoneinfo = compile_phone.getText().toString().trim();
                AddFavorite();
                UserInfo();
                break;
            case R.id.change_compile:
                showPopFormBottom(v);
                break;
            case R.id.delete_compile:
                ShowDialog();
                break;
            default:
                break;
        }
    }

    private void ShowDialog() {
        final Dialog dialog = new Dialog(CompileActivity.this);
        LayoutInflater inflater = LayoutInflater.from(CompileActivity.this);
        View view = inflater.inflate(R.layout.contacts_dialog, null);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(view);
        ImageButton dialog_cancel = (ImageButton) view.findViewById(R.id.dialog_cancel);
        ImageButton dialog_delete = (ImageButton) view.findViewById(R.id.dialog_delete);
        TextView dialog_name = (TextView) view.findViewById(R.id.dialog_name);
        dialog_name.setText(user + "  ?");
        dialog_cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog_delete.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (isCollect(phone)) {
                    OperationSqlite.DeleteFavorite(phone);
                }
                OperationSqlite.DeleteId(CompileActivity.this, mId);
                ToastUtil.showPicToast(CompileActivity.this, "删除成功");
                CompileActivity.this.finish();
                dialog.dismiss();
            }
        });
        dialog.show();
    }


    private void UserInfo() {
        String user_info = compile_username.getText().toString().trim();
        String phone1 = compile_phone.getText().toString().trim();
        String city = compile_city.getText().toString().trim();
        String note = compile_note.getText().toString().trim();
        if (TextUtils.isEmpty(user_info)) {
            ToastUtil.showPicToast(this, "姓名不能为空!");
            return;
        } else if (TextUtils.isEmpty(phone1)) {
            ToastUtil.showPicToast(this, "号码不能为空!");
            return;
        }
        OperationSqlite.InsertDatas(this, user_info, phone1, photo, city, note);
        ToastUtil.showPicToast(this, "修改成功！");
        CompileActivity.this.finish();
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
                    PhotoView.getPhotoGraph(CompileActivity.this);
                    takePhotoPopWin.dismiss();
                    break;
                case R.id.btn_pick_photo:
                    PhotoView.getMapdepot(CompileActivity.this);
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
        } else {
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
        compile_phone.addTextChangedListener(new TextWatcher() {
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
        if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_MOVE_HOME) {
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