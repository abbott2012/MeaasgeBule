package com.guoji.mobile.cocobee.activity;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.guoji.mobile.cocobee.R;

import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * 查看大图（保单背面查看）
 * Created by _H_JY on 2016/7/13.
 */
public class PicLookAct extends BaseAct {

    private ImageView imageView;
    private PhotoViewAttacher mAttacher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.act_piclook);
        imageView = (ImageView) findViewById(R.id.image);

        imageView.setImageResource(R.drawable.receipt_opposite);

        mAttacher = new PhotoViewAttacher(imageView);
        mAttacher.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
            @Override
            public void onPhotoTap(View view, float x, float y) {
                finish();
            }
        });
    }


}
