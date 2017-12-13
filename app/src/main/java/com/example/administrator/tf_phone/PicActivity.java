package com.example.administrator.tf_phone;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;

import com.example.administrator.tf_phone.view.BaseActivity;

import de.hdodenhof.circleimageview.CircleImageView;

public class PicActivity extends BaseActivity {

    private CircleImageView pic_image;

    protected void initContentView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_pic);

        initView();
        getData();
    }


    private void initView() {
        pic_image = (CircleImageView) this.findViewById(R.id.pic_image);
        pic_image.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                PicActivity.this.finish();
            }
        });
    }

    private void getData() {
        Intent intent = getIntent();
        Bitmap bitmap = intent.getParcelableExtra("image");
        pic_image.setImageBitmap(bitmap);
    }
}
