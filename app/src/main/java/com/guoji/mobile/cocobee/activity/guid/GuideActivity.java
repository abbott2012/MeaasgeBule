package com.guoji.mobile.cocobee.activity.guid;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.guoji.mobile.cocobee.R;
import com.guoji.mobile.cocobee.activity.LoginActivity;
import com.guoji.mobile.cocobee.activity.MainActivity1;
import com.guoji.mobile.cocobee.activity.base.BaseActivity;
import com.guoji.mobile.cocobee.adapter.GuidAdapter;
import com.guoji.mobile.cocobee.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Administrator on 2017/4/28.
 */

public class GuideActivity extends BaseActivity {

    @BindView(R.id.viewpager)
    ViewPager mViewpager;
    @BindView(R.id.indicator)
    LinearLayout mIndicator;
    @BindView(R.id.go_into)
    TextView mGoInto;
    private List<View> viewList;
    private ImageView[] indicatorImgs;
    private int[] imgResArr = new int[]{R.drawable.app_itro1, R.drawable.app_itro2, R.drawable.app_itro3};
//    private TextView mGoInto;

    @Override
    protected int getContentViewLayoutID() {
        //状态栏与应用同色
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        return R.layout.activity_guid;
    }

    @Override
    protected void initViewsAndEvents(Bundle savedInstanceState) {
        initData();
        initView();

    }

    public void initData() {
        indicatorImgs = new ImageView[imgResArr.length];
        viewList = new ArrayList<>(imgResArr.length);
        for (int i = 0; i < imgResArr.length; i++) {
            View view = LayoutInflater.from(this).inflate(R.layout.guid_viewpager, null);
            view.setBackgroundResource(R.color.white);
            ((ImageView) view.findViewById(R.id.guide_image)).setBackgroundResource(imgResArr[i]);
            viewList.add(view);
            indicatorImgs[i] = new ImageView(this);
            if (i == 0) {
                indicatorImgs[i].setBackgroundResource(R.drawable.select_point);
            } else {
                indicatorImgs[i].setBackgroundResource(R.drawable.not_select_point);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-2, -2);
                layoutParams.setMargins(20, 0, 0, 0);
                indicatorImgs[i].setLayoutParams(layoutParams);
            }
            mIndicator.addView(indicatorImgs[i]);
        }
    }

    public void initView() {
        mViewpager.setAdapter(new GuidAdapter(viewList));

        mViewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setIndicator(position);
                if (position == (viewList.size() - 1)) {
                    mGoInto.setVisibility(View.VISIBLE);
                } else {
                    mGoInto.setVisibility(View.GONE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public void setIndicator(int targetIndex) {
        for (int i = 0; i < indicatorImgs.length; i++) {
            indicatorImgs[i].setBackgroundResource(R.drawable.select_point);
            if (targetIndex != i) {
                indicatorImgs[i].setBackgroundResource(R.drawable.not_select_point);
            }
        }
    }

    @OnClick(R.id.go_into)
    public void onViewClicked() {
        Utils.putUserIsFirst(false);
//        startActivity(new Intent(this, LoginActivity.class));
        startActivity(new Intent(this, MainActivity1.class));
        finish();
    }

}
