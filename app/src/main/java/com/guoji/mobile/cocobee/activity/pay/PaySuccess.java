package com.guoji.mobile.cocobee.activity.pay;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.bql.utils.CheckUtils;
import com.guoji.mobile.cocobee.R;
import com.guoji.mobile.cocobee.activity.base.BaseActivity;
import com.guoji.mobile.cocobee.activity.me.LabelBindingCarAnXinActivity;
import com.guoji.mobile.cocobee.activity.me.LabelBindingPersonAnXinActivity;
import com.guoji.mobile.cocobee.activity.me.LabelBindingTiYan;
import com.guoji.mobile.cocobee.common.AppConstants;
import com.guoji.mobile.cocobee.model.User;
import com.guoji.mobile.cocobee.response.SelectServiceResponse;
import com.guoji.mobile.cocobee.utils.Utils;
import com.guoji.mobile.cocobee.view.MoneyTextView;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 支付成功
 * Created by Administrator on 2017/5/5.
 */

public class PaySuccess extends BaseActivity {
    @BindView(R.id.pay_price)
    MoneyTextView mPayPrice;
    @BindView(R.id.tv_finish)
    TextView mTvFinish;
    private SelectServiceResponse mSelectServiceResponse;
    private String mOrderId;

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_pay_success;
    }

    @Override
    protected void initViewsAndEvents(Bundle savedInstanceState) {
        Intent intent = getIntent();
        String payAmount = intent.getStringExtra("payAmount");
        mOrderId = intent.getStringExtra("orderId");
        mSelectServiceResponse = (SelectServiceResponse) intent.getSerializableExtra("selectServiceResponse");
        mPayPrice.setAmount(payAmount);
    }

    @OnClick({R.id.tv_finish})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_finish:
                Intent intent = null;
                User userLoginInfo = Utils.getUserLoginInfo();
                if (CheckUtils.equalsString(mSelectServiceResponse.getCard_id(), AppConstants.TYPE_CARD_TIYAN)) {//体验卡
                    intent = new Intent(this, LabelBindingTiYan.class);
                } else if (CheckUtils.equalsString(mSelectServiceResponse.getCard_id(), AppConstants.TYPE_CARD_ANXIN) && CheckUtils.equalsString(mSelectServiceResponse.getTarget_id(), AppConstants.TYPE_CAR)) {//安心卡,车
                    if (CheckUtils.isEmpty(userLoginInfo.getIdcard())) {
//                        intent = new Intent(this, CarInputBaseInfo.class);
                    }else {
                        intent = new Intent(this, LabelBindingCarAnXinActivity.class);
                    }
                } else if (CheckUtils.equalsString(mSelectServiceResponse.getCard_id(), AppConstants.TYPE_CARD_ANXIN) && CheckUtils.equalsString(mSelectServiceResponse.getTarget_id(), AppConstants.TYPE_PEPOLE)) {//安心卡,人
                    intent = new Intent(this, LabelBindingPersonAnXinActivity.class);
                }
                if (intent != null) {
                    intent.putExtra("selectServiceResponse", mSelectServiceResponse);
                    intent.putExtra("orderId", mOrderId);
                    startActivity(intent);
                }
                finish();
                break;
        }
    }
}
