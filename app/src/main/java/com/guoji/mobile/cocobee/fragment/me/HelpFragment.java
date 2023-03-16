package com.guoji.mobile.cocobee.fragment.me;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.guoji.mobile.cocobee.R;
import com.guoji.mobile.cocobee.adapter.GuidAdapter;
import com.guoji.mobile.cocobee.fragment.base.BaseFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by Administrator on 2017/8/7.
 */

public class HelpFragment extends BaseFragment {

    @BindView(R.id.viewpager)
    ViewPager mViewpager;
    @BindView(R.id.indicator)
    LinearLayout mIndicator;
    //    @BindView(R.id.banner)
//    Banner mBanner;
    private List<Integer> imgs = new ArrayList<>();

    private List<View> viewList;
    private ImageView[] indicatorImgs;
    private int[] imgResArr = new int[]{R.drawable.teaching1, R.drawable.teaching2};

    public static HelpFragment getInstance() {
        HelpFragment fragment = new HelpFragment();
        return fragment;
    }

    @Override
    protected void initViewsAndEvents(Bundle savedInstanceState) {
//        initBanner();
        initData();
        initView();

    }
    public void initData() {
        indicatorImgs = new ImageView[imgResArr.length];
        viewList = new ArrayList<>(imgResArr.length);
        for (int i = 0; i < imgResArr.length; i++) {
            View view = LayoutInflater.from(_mActivity).inflate(R.layout.guid_viewpager, null);
            view.setBackgroundResource(R.color.white);
            ((ImageView) view.findViewById(R.id.guide_image)).setBackgroundResource(imgResArr[i]);
            viewList.add(view);
            indicatorImgs[i] = new ImageView(_mActivity);
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


    @Override
    protected void initToolbarHere() {
        initToolbar("帮助");
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.fragment_help;
    }



//    private void initBanner() {
//        imgs.add(R.drawable.teaching1);
//        imgs.add(R.drawable.teaching2);
//        //设置banner样式
//        mBanner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR);
//        //设置图片加载器
//        mBanner.setImageLoader(new GlideImageLoader());
//        //设置图片集合
//        mBanner.setImages(imgs);
//        //设置banner动画效果
//        mBanner.setBannerAnimation(Transformer.Default);
//        //设置标题集合（当banner样式有显示title时）
////        mBanner.setBannerTitles(titles);
//        //设置自动轮播，默认为true
//        mBanner.isAutoPlay(false);
//        //设置轮播时间
////        mBanner.setDelayTime(1500);
//        //设置指示器位置（当banner模式中有指示器时）
//        mBanner.setIndicatorGravity(BannerConfig.CENTER);
//        //banner设置方法全部调用完毕时最后调用
//        mBanner.start();
//    }


}
