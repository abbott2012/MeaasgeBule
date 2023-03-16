package com.guoji.mobile.cocobee.activity.me.car;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bql.utils.CheckUtils;
import com.guoji.mobile.cocobee.R;
import com.guoji.mobile.cocobee.activity.base.BaseActivity;
import com.guoji.mobile.cocobee.common.Path;
import com.guoji.mobile.cocobee.response.CarInfoResponse;
import com.guoji.mobile.cocobee.utils.ImageUtil;

import butterknife.BindView;
import butterknife.OnClick;


/**
 * Created by Administrator on 2017/4/23.
 * 车辆详细信息
 */

public class CarDetailInfoActivity extends BaseActivity {
    @BindView(R.id.ll_back_pro)
    ImageView mBackPro;
    @BindView(R.id.tv_car_num)
    TextView mTvCarNum;
    @BindView(R.id.tv_biao_qian)
    TextView mTvBiaoQian;
    @BindView(R.id.tv_che_jia)
    TextView mTvCheJia;
    @BindView(R.id.tv_buy_price)
    TextView mTvBuyPrice;
    @BindView(R.id.tv_buy_time)
    TextView mTvBuyTime;
    @BindView(R.id.tv_type)
    TextView mTvType;
    @BindView(R.id.tv_fadongji)
    TextView mTvFadongji;
    @BindView(R.id.tv_baodan)
    TextView mTvBaodan;
    @BindView(R.id.plate_iv)
    ImageView mPlateIv;
    @BindView(R.id.vehicle_front_iv)
    ImageView mVehicleFrontIv;
    @BindView(R.id.vehicle_side_one_iv)
    ImageView mVehicleSideOneIv;
    @BindView(R.id.vehicle_side_two_iv)
    ImageView mVehicleSideTwoIv;

    private CarInfoResponse mCarInfo;

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_car_detail_info;
    }

    @Override
    protected void initViewsAndEvents(Bundle savedInstanceState) {
        Intent intent = getIntent();
        mCarInfo = (CarInfoResponse) intent.getSerializableExtra("carInfo");
        initView();
    }

    private void initView() {
        mTvCarNum.setText(mCarInfo.getCno());
        if (CheckUtils.isEmpty(mCarInfo.getLno())) {
            mTvBiaoQian.setText("未绑定标签");
        } else {
            mTvBiaoQian.setText(mCarInfo.getLno());
        }
        mTvCheJia.setText(mCarInfo.getCframe());
        mTvBuyPrice.setText(mCarInfo.getCbuyprice());
        mTvBuyTime.setText(mCarInfo.getCbuytime());
        mTvType.setText(mCarInfo.getCbuytype());
        mTvFadongji.setText(mCarInfo.getCdevice());
        if (!CheckUtils.isEmpty(mCarInfo.getOrder_id())) {
            mTvBaodan.setText(mCarInfo.getOrder_id());
        } else {
            mTvBaodan.setText("未购买保险");
        }
        String ccarpicurl = mCarInfo.getCcarpicurl();
        if (!CheckUtils.isEmpty(ccarpicurl)) {
            String[] carPic = ccarpicurl.split(",");
            if (carPic != null) {

                switch (carPic.length) {
                    case 0:
                        mPlateIv.setVisibility(View.GONE);
                        mVehicleFrontIv.setVisibility(View.GONE);
                        mVehicleSideOneIv.setVisibility(View.GONE);
                        mVehicleSideTwoIv.setVisibility(View.GONE);
                        break;
                    case 1:
                        ImageUtil.loadPic(this, Path.IMG_BASIC_PATH + carPic[0], mPlateIv);
                        mVehicleFrontIv.setVisibility(View.GONE);
                        mVehicleSideOneIv.setVisibility(View.GONE);
                        mVehicleSideTwoIv.setVisibility(View.GONE);
                        break;
                    case 2:
                        ImageUtil.loadPic(this, Path.IMG_BASIC_PATH + carPic[0], mPlateIv);
                        ImageUtil.loadPic(this, Path.IMG_BASIC_PATH + carPic[1], mVehicleFrontIv);
                        mVehicleSideOneIv.setVisibility(View.GONE);
                        mVehicleSideTwoIv.setVisibility(View.GONE);
                        break;
                    case 3:
                        ImageUtil.loadPic(this, Path.IMG_BASIC_PATH + carPic[0], mPlateIv);
                        ImageUtil.loadPic(this, Path.IMG_BASIC_PATH + carPic[1], mVehicleFrontIv);
                        ImageUtil.loadPic(this, Path.IMG_BASIC_PATH + carPic[2], mVehicleSideOneIv);
                        mVehicleSideTwoIv.setVisibility(View.GONE);
                        break;
                    case 4:
                        ImageUtil.loadPic(this, Path.IMG_BASIC_PATH + carPic[0], mPlateIv);
                        ImageUtil.loadPic(this, Path.IMG_BASIC_PATH + carPic[1], mVehicleFrontIv);
                        ImageUtil.loadPic(this, Path.IMG_BASIC_PATH + carPic[2], mVehicleSideOneIv);
                        ImageUtil.loadPic(this, Path.IMG_BASIC_PATH + carPic[3], mVehicleSideTwoIv);
                        break;
                }
            }
//            ImageUtil.loadPic("http://218.17.157.214:8989/Pro_Marktrace_Electrocar/"+carPic[3], mVehicleSideTwoIv);
        }
    }


    @OnClick({R.id.ll_back_pro})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_back_pro:
                finish();
                break;

        }
    }

}
