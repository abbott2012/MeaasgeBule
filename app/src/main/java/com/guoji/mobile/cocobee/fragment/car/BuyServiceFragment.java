package com.guoji.mobile.cocobee.fragment.car;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bql.utils.CheckUtils;
import com.bql.utils.EventManager;
import com.guoji.mobile.cocobee.R;
import com.guoji.mobile.cocobee.activity.pay.SelectPayActivity;
import com.guoji.mobile.cocobee.callback.DialogCallback;
import com.guoji.mobile.cocobee.common.AppConstants;
import com.guoji.mobile.cocobee.common.Path;
import com.guoji.mobile.cocobee.fragment.base.BaseFragment;
import com.guoji.mobile.cocobee.model.OrderInfo;
import com.guoji.mobile.cocobee.model.OrderInfoResponse;
import com.guoji.mobile.cocobee.model.User;
import com.guoji.mobile.cocobee.response.CardLabelResponse;
import com.guoji.mobile.cocobee.response.SelectServiceResponse;
import com.guoji.mobile.cocobee.utils.Utils;
import com.guoji.mobile.cocobee.utils.XToastUtils;
import com.google.gson.Gson;
import com.lzy.okgo.OkGo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by Administrator on 2017/6/21.
 */

public class BuyServiceFragment extends BaseFragment {

    @BindView(R.id.tv_card_title)
    TextView mTvCardTitle;
    @BindView(R.id.tv_card_content)
    TextView mTvCardContent;
    @BindView(R.id.ll_card_type)
    LinearLayout mLlCardType;
    @BindView(R.id.tv_pay_price)
    TextView mTvPayPrice;
    @BindView(R.id.tv_goto_pay)
    TextView mTvGotoPay;
    @BindView(R.id.tv_card_detail)
    TextView mTvCardDetail;

    private static SelectServiceResponse selectServiceResponse;
    private static CardLabelResponse cardLabelResponse;
    @BindView(R.id.card_detail_title)
    TextView mCardDetailTitle;
    private User mUserLoginInfo;

    public static BuyServiceFragment getInstance(SelectServiceResponse selectServiceResponse, CardLabelResponse cardLabelResponse) {
        BuyServiceFragment.selectServiceResponse = selectServiceResponse;
        BuyServiceFragment.cardLabelResponse = cardLabelResponse;
        BuyServiceFragment fragment = new BuyServiceFragment();
        return fragment;
    }

    @Override
    protected void initViewsAndEvents(Bundle savedInstanceState) {
        initView();
    }

    private void initView() {
        mTvCardTitle.setText(selectServiceResponse.getCard_name());
        mTvCardContent.setText(selectServiceResponse.getCard_ddesc());
        if (CheckUtils.equalsString(selectServiceResponse.getCard_id(), "33")) {//安心卡
            mLlCardType.setBackgroundResource(R.drawable.card_bg_orange);
            mCardDetailTitle.setText("安心卡说明");
        } else {
            mLlCardType.setBackgroundResource(R.drawable.card_bg_purple);
            mCardDetailTitle.setText("体验卡说明");
        }
        mTvCardDetail.setText(selectServiceResponse.getCard_mark());
        mTvPayPrice.setText(cardLabelResponse.getService_price());
    }

    @Override
    protected void initToolbarHere() {
        initToolbar("购买服务");
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.fragment_buy_service_tiyan;
    }

    @OnClick({R.id.tv_goto_pay})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_goto_pay:
                //防止一秒内点击多次
                if (!Utils.isFastClick()) {
                    return;
                }
                addOrder();
                break;
        }
    }

    //生成订单
    private void addOrder() {
        mUserLoginInfo = Utils.getUserLoginInfo();
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setSafeconfig_id(Integer.parseInt(cardLabelResponse.getId()));
        orderInfo.setOrder_type(2);//Android端
        orderInfo.setUser_id(Integer.parseInt(mUserLoginInfo.getPid()));
        orderInfo.setGoods_amount(Double.parseDouble(cardLabelResponse.getService_price()));
        orderInfo.setCard_id(selectServiceResponse.getCard_id());
        orderInfo.setTarget_id(selectServiceResponse.getTarget_id());

        Map<String, String> params = new HashMap<String, String>();
        params.put("json", new Gson().toJson(orderInfo));

        OkGo.post(Path.PAY_ORDER_ADD).tag(this).params(params).execute(new DialogCallback<List<OrderInfoResponse>>(_mActivity, "订单生成中...") {

            @Override
            public void onSuccess(List<OrderInfoResponse> orderInfoResponses, Call call, Response response) {
                if (orderInfoResponses != null && orderInfoResponses.size() > 0) {
                    gotoPay(orderInfoResponses.get(0));
                } else {
                    XToastUtils.showShortToast("暂无订单信息");
                }
            }
        });
    }

    //跳转支付界面
    private void gotoPay(OrderInfoResponse orderInfoResponse) {
        Intent intent = new Intent(_mActivity, SelectPayActivity.class);
        intent.putExtra("orderInfoResponse", orderInfoResponse);
        intent.putExtra("selectServiceResponse", selectServiceResponse);
        startActivity(intent);
    }

    @Override
    public boolean registerEventBus() {
        return true;
    }

    @Override
    protected void onHandleEvent(EventManager eventManager) {
        super.onHandleEvent(eventManager);
        switch (eventManager.getEventCode()) {
            case AppConstants.PAY_FINISH://支付完成
                _mActivity.finish();
                break;
        }
    }
}
