package com.guoji.mobile.cocobee.fragment.me;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bql.utils.CheckUtils;
import com.guoji.mobile.cocobee.R;
import com.guoji.mobile.cocobee.activity.PolicyInfoActivity;
import com.guoji.mobile.cocobee.common.AppConstants;
import com.guoji.mobile.cocobee.common.Path;
import com.guoji.mobile.cocobee.fragment.base.BaseFragment;
import com.guoji.mobile.cocobee.response.AdResponse;
import com.guoji.mobile.cocobee.response.HomeRecResponse;
import com.guoji.mobile.cocobee.utils.XToastUtils;
import com.guoji.mobile.cocobee.view.GlideImageLoader;
import com.jude.rollviewpager.RollPagerView;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Administrator on 2017/6/30.
 */

public class CarDetailFragment extends BaseFragment {

    private static HomeRecResponse homeRecResponse;
    @BindView(R.id.roll_pager_view)
    RollPagerView mRollPagerView;
    @BindView(R.id.tv_type)
    TextView mTvType;
    @BindView(R.id.tv_car_num)
    TextView mTvCarNum;
    @BindView(R.id.tv_che_jia)
    TextView mTvCheJia;
    @BindView(R.id.tv_biao_qian)
    TextView mTvBiaoQian;
    @BindView(R.id.tv_buy_time)
    TextView mTvBuyTime;
    @BindView(R.id.tv_buy_price)
    TextView mTvBuyPrice;
    @BindView(R.id.tv_baodan)
    TextView mTvBaodan;
    @BindView(R.id.ll_has_buy_service)
    LinearLayout mLlHasBuyService;
    @BindView(R.id.banner)
    Banner mBanner;

    private List<AdResponse> mAdResponseList = new ArrayList<>();
    private List<String> imgs = new ArrayList<>();

    public static CarDetailFragment getInstance(HomeRecResponse homeRecResponse) {
        CarDetailFragment.homeRecResponse = homeRecResponse;
        CarDetailFragment fragment = new CarDetailFragment();
        return fragment;
    }

    @Override
    protected void initViewsAndEvents(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            homeRecResponse = (HomeRecResponse) savedInstanceState.getSerializable("homeRecResponse");
        }
        initCarPic();
        initView();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("homeRecResponse", homeRecResponse);
    }

    private void initView() {
        mTvType.setText(homeRecResponse.getCbuytype());
        mTvCarNum.setText(homeRecResponse.getCno());
        mTvCheJia.setText(homeRecResponse.getCframe());
        mTvBiaoQian.setText(homeRecResponse.getLno());
        mTvBuyTime.setText(homeRecResponse.getCbuytime());
        if (CheckUtils.equalsString(homeRecResponse.getCbuyprice(), AppConstants.TYPE_CARD_ONLINE)) {
            mTvBuyPrice.setText("");
        } else {
            mTvBuyPrice.setText(homeRecResponse.getCbuyprice() + "元");
        }
        if (CheckUtils.equalsString(homeRecResponse.getOnline(), AppConstants.TYPE_CARD_ONLINE)) {//线上
            if (CheckUtils.equalsString(homeRecResponse.getCard_id(), AppConstants.TYPE_CARD_ANXIN)) {//安心卡
                mTvBaodan.setText("安心卡");
            } else if (CheckUtils.equalsString(homeRecResponse.getCard_id(), AppConstants.TYPE_CARD_TIYAN)) {//体验卡
                mTvBaodan.setText("体验卡");
            } else if (CheckUtils.equalsString(homeRecResponse.getCard_id(), AppConstants.TYPE_CARD_DINZHI)) {//定制卡
                mTvBaodan.setText("定制卡");
            } else {
                mTvBaodan.setText("未购买服务");
            }
        } else {//线下
            if (CheckUtils.equalsString(homeRecResponse.getCard_id(), AppConstants.TYPE_CARD_DINZHI)) {//定制卡
                mTvBaodan.setText("定制卡");
            } else {
                mTvBaodan.setText("线下推广卡");
            }
        }

    }

    private void initCarPic() {
        mAdResponseList.clear();
        String ccarpicurl = homeRecResponse.getCcarpicurl();
        String[] carUrls = ccarpicurl.split(",");
        if (carUrls.length > 0) {
            for (int i = 0; i < carUrls.length; i++) {
                imgs.add(Path.IMG_BASIC_PATH + carUrls[i]);
                AdResponse adResponse = new AdResponse();
                adResponse.setAd_pic_url(carUrls[i]);
                mAdResponseList.add(adResponse);
            }
        }
        initBanner();
    }


    private void initBanner() {
        //设置banner样式
        mBanner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR);
        //设置图片加载器
        mBanner.setImageLoader(new GlideImageLoader());
        //设置图片集合
        mBanner.setImages(imgs);
        //设置banner动画效果
        mBanner.setBannerAnimation(Transformer.Default);
        //设置指示器的位置
        mBanner.setIndicatorGravity(BannerConfig.CENTER);
//        mBanner.isAutoPlay(false);
        //banner设置方法全部调用完毕时最后调用
        mBanner.start();
    }

    @Override
    protected void initToolbarHere() {
        initToolbarWithLeftText("车辆详情", "我的");
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.fragment_car_detail;
    }

    @OnClick({R.id.ll_has_buy_service})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_has_buy_service://已购服务
                if (CheckUtils.equalsString(homeRecResponse.getCard_id(), AppConstants.TYPE_CARD_ANXIN) || CheckUtils.equalsString(homeRecResponse.getCard_id(), AppConstants.TYPE_CARD_DINZHI)) {//安心卡,定制卡
                    Intent intent = new Intent(_mActivity, PolicyInfoActivity.class);
                    intent.putExtra("lno", homeRecResponse.getLno());
                    startActivity(intent);
                } else if (CheckUtils.equalsString(homeRecResponse.getCard_id(), AppConstants.TYPE_CARD_TIYAN)) {//体验卡
                    start(ServiceDetailFragment.getInstance(homeRecResponse));
                } else {
                    XToastUtils.showShortToast("该卡不具备该功能");
                }
                break;
        }
    }

}
