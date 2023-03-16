package com.guoji.mobile.cocobee.fragment.me;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bql.utils.CheckUtils;
import com.guoji.mobile.cocobee.R;
import com.guoji.mobile.cocobee.activity.me.LabelBindingCarAnXinActivity;
import com.guoji.mobile.cocobee.activity.me.LabelBindingPersonAnXinActivity;
import com.guoji.mobile.cocobee.callback.JsonCallback;
import com.guoji.mobile.cocobee.common.AppConstants;
import com.guoji.mobile.cocobee.common.Path;
import com.guoji.mobile.cocobee.fragment.base.BaseFragment;
import com.guoji.mobile.cocobee.model.Car;
import com.guoji.mobile.cocobee.model.User;
import com.guoji.mobile.cocobee.response.HomeRecResponse;
import com.guoji.mobile.cocobee.response.SelectServiceResponse;
import com.guoji.mobile.cocobee.utils.Utils;
import com.lzy.okgo.OkGo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by Administrator on 2017/6/30.
 */

public class ServiceDetailFragment extends BaseFragment {

    private static HomeRecResponse homeRecResponse;
    @BindView(R.id.tv_card_title)
    TextView mTvCardTitle;
    @BindView(R.id.tv_card_content)
    TextView mTvCardContent;
    @BindView(R.id.tv_card_detail)
    TextView mTvCardDetail;
    @BindView(R.id.tv_to_out_time)
    TextView mTvToOutTime;
    @BindView(R.id.tv_update)
    TextView mTvUpdate;
    @BindView(R.id.ll_card_type)
    LinearLayout mLlCardType;
    @BindView(R.id.card_detail_title)
    TextView mCardDetailTitle;
    @BindView(R.id.ll_remain_day)
    LinearLayout mLlRemainDay;
    private User mUserLoginInfo;
    private SelectServiceResponse mSelectServiceResponse;
    private SelectServiceResponse mSelectServiceResponseAnXin;
    private SelectServiceResponse mSelectServiceResponseDinZhi;

    public static ServiceDetailFragment getInstance(HomeRecResponse homeRecResponse) {
        ServiceDetailFragment.homeRecResponse = homeRecResponse;
        ServiceDetailFragment fragment = new ServiceDetailFragment();
        return fragment;
    }

    @Override
    protected void initViewsAndEvents(Bundle savedInstanceState) {
        mUserLoginInfo = Utils.getUserLoginInfo();
        initData();
    }

    private void initData() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("orgid", mUserLoginInfo.getOrgid());
        params.put("target_id", homeRecResponse.getTarget_id());

        OkGo.post(Path.GET_CARD_INFO).params(params).execute(new JsonCallback<List<SelectServiceResponse>>(_mActivity) {

            @Override
            public void onSuccess(List<SelectServiceResponse> selectServiceResponses, Call call, Response response) {
                if (selectServiceResponses == null || selectServiceResponses.size() == 0) {
                    return;
                }

                for (int i = 0; i < selectServiceResponses.size(); i++) {
                    SelectServiceResponse selectServiceResponse = selectServiceResponses.get(i);
                    if (CheckUtils.equalsString(selectServiceResponse.getCard_id(), AppConstants.TYPE_CARD_TIYAN)) {//体验卡
                        mSelectServiceResponse = selectServiceResponse;
                    } else if (CheckUtils.equalsString(selectServiceResponse.getCard_id(), AppConstants.TYPE_CARD_ANXIN)) {//安心卡
                        mSelectServiceResponseAnXin = selectServiceResponse;
                    } else if (CheckUtils.equalsString(selectServiceResponse.getCard_id(), AppConstants.TYPE_CARD_DINZHI)) {//定制卡
                        mSelectServiceResponseDinZhi = selectServiceResponse;
                    }
                }
                initView();

            }
        });
    }

    private void initView() {

        if (CheckUtils.equalsString(homeRecResponse.getCard_id(), AppConstants.TYPE_CARD_ANXIN)) {//安心卡
            mCardDetailTitle.setText("安心卡说明");
            mLlCardType.setBackgroundResource(R.drawable.card_bg_orange);
            mLlRemainDay.setVisibility(View.GONE);
            mTvUpdate.setVisibility(View.GONE);
            mTvCardTitle.setText(mSelectServiceResponseAnXin.getCard_name());
            mTvCardContent.setText(mSelectServiceResponseAnXin.getCard_ddesc());
            mTvCardDetail.setText(mSelectServiceResponseAnXin.getCard_mark());
        } else if (CheckUtils.equalsString(homeRecResponse.getCard_id(), AppConstants.TYPE_CARD_DINZHI)) {//定制卡
            mCardDetailTitle.setText("定制卡说明");
            mLlCardType.setBackgroundResource(R.drawable.corner_rec_blue);
            mLlRemainDay.setVisibility(View.GONE);
            mTvUpdate.setVisibility(View.GONE);
            mTvCardTitle.setText(mSelectServiceResponseDinZhi.getCard_name());
            mTvCardContent.setText(mSelectServiceResponseDinZhi.getCard_ddesc());
            mTvCardDetail.setText(mSelectServiceResponseDinZhi.getCard_mark());
        } else if (CheckUtils.equalsString(homeRecResponse.getCard_id(), AppConstants.TYPE_CARD_TIYAN)) {
            mCardDetailTitle.setText("体验卡说明");
            mLlCardType.setBackgroundResource(R.drawable.card_bg_purple);
            mLlRemainDay.setVisibility(View.VISIBLE);
            mTvUpdate.setVisibility(View.VISIBLE);
            mTvCardTitle.setText(mSelectServiceResponse.getCard_name());
            mTvCardContent.setText(mSelectServiceResponse.getCard_ddesc());
            mTvCardDetail.setText(mSelectServiceResponse.getCard_mark());
            mTvToOutTime.setText("服务剩余" + homeRecResponse.getDays() + "天,建议立即升级为安心卡");
        }
    }

    @Override
    protected void initToolbarHere() {
        initToolbar("服务详情");
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.fragment_service_update;
    }


    @OnClick({R.id.tv_update})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_update://升级为安心卡
                gotoUpdate();
                break;
        }
    }

    private void gotoUpdate() {
        Intent intent = null;
        if (CheckUtils.equalsString(homeRecResponse.getTarget_id(), "38")) {//车
            if (CheckUtils.isEmpty(mUserLoginInfo.getIdcard())) {
//                intent = new Intent(getContext(), CarInputBaseInfo.class);
            } else {
                intent = new Intent(getContext(), LabelBindingCarAnXinActivity.class);
                Car car = new Car();
                car.setuIdCard(mUserLoginInfo.getIdcard());
                car.setuName(mUserLoginInfo.getPname());
                car.setuSex(mUserLoginInfo.getSex());
                car.setuBirthday(mUserLoginInfo.getBirthday());
                car.setuFirstAddress(mUserLoginInfo.getRegiaddr());
                intent.putExtra("car", car);
            }
        } else if (CheckUtils.equalsString(homeRecResponse.getTarget_id(), "39")) {//人
            intent = new Intent(getContext(), LabelBindingPersonAnXinActivity.class);
        }

        if (intent != null) {
            intent.putExtra("selectServiceResponse", mSelectServiceResponse);
            intent.putExtra("orderId", homeRecResponse.getOrder_id());
            intent.putExtra("lno", homeRecResponse.getLno());
        }
        startActivity(intent);
    }

}
