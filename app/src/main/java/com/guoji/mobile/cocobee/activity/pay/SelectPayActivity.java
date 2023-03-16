package com.guoji.mobile.cocobee.activity.pay;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bql.utils.EventManager;
import com.guoji.mobile.cocobee.R;
import com.guoji.mobile.cocobee.activity.base.BaseActivity;
import com.guoji.mobile.cocobee.callback.DialogCallback;
import com.guoji.mobile.cocobee.callback.StringDialogCallback;
import com.guoji.mobile.cocobee.common.AppConstants;
import com.guoji.mobile.cocobee.common.JsonResult;
import com.guoji.mobile.cocobee.common.Path;
import com.guoji.mobile.cocobee.model.OrderInfoResponse;
import com.guoji.mobile.cocobee.model.WXPay;
import com.guoji.mobile.cocobee.response.SelectServiceResponse;
import com.guoji.mobile.cocobee.utils.Utils;
import com.guoji.mobile.cocobee.utils.WXPayUtils;
import com.guoji.mobile.cocobee.utils.XToastUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;

/**
 * 选择支付页面
 * Created by Administrator on 2017/4/24.
 */

public class SelectPayActivity extends BaseActivity {
    @BindView(R.id.ll_back_pro)
    ImageView mLlBackPro;
    @BindView(R.id.tv_pay_price)
    TextView mTvPayPrice;
    @BindView(R.id.tv_car_num)
    TextView mTvCarNum;
    @BindView(R.id.tv_car_time)
    TextView mTvCarTime;
    @BindView(R.id.iv_not_select)
    ImageView mIvNotSelect;
    @BindView(R.id.ll_ali_pay)
    LinearLayout mLlAliPay;
    @BindView(R.id.iv_has_select)
    ImageView mIvHasSelect;
    @BindView(R.id.ll_wechat_pay)
    LinearLayout mLlWechatPay;
    @BindView(R.id.sure_btn)
    TextView mSureBtn;
    private int flag = 2;//支付方式  0未选择 1支付宝 2微信
    private OrderInfoResponse mOrderInfoResponse;
    private SelectServiceResponse mSelectServiceResponse;


    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_select_pay;
    }

    @Override
    protected void initViewsAndEvents(Bundle savedInstanceState) {
        initIntent();
        isSelectedVisble(View.GONE, View.VISIBLE);//默认选中微信支付
        initView();
    }

    private void initIntent() {
        Intent intent = getIntent();
        mOrderInfoResponse = (OrderInfoResponse) intent.getSerializableExtra("orderInfoResponse");
        mSelectServiceResponse = (SelectServiceResponse) intent.getSerializableExtra("selectServiceResponse");
    }

    private void initView() {
        mTvPayPrice.setText("¥ " + mOrderInfoResponse.getGoods_amount());
        mTvCarNum.setText("投保车牌号: " + mOrderInfoResponse.getCno());
        mTvCarTime.setText("保险期限: " + mOrderInfoResponse.getLife() + "年");
    }


    @OnClick({R.id.ll_back_pro, R.id.ll_ali_pay, R.id.ll_wechat_pay, R.id.sure_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_back_pro:
                finish();
                break;
            case R.id.ll_ali_pay://支付宝支付
                isSelectedVisble(View.VISIBLE, View.GONE);
                flag = 1;
                break;
            case R.id.ll_wechat_pay://微信支付
                isSelectedVisble(View.GONE, View.VISIBLE);
                flag = 2;
                break;
            case R.id.sure_btn:
                //1秒内只允许点击一次
                if (!Utils.isFastClick()) {
                    return;
                }
                gotoPay();
                break;
        }
    }

    private void isSelectedVisble(int gone, int visible) {
        mIvNotSelect.setVisibility(gone);
        mIvHasSelect.setVisibility(visible);
    }

    private void gotoPay() {
        switch (flag) {
            case 0://未选择支付方式
                XToastUtils.showShortToast("请选择支付方式");
                break;
            case 1://支付宝
                //调用支付宝支付
                getPayInfo();
                break;
            case 2://微信
                if (WXPayUtils.checkIsSupportWXPay(this)) {
                    //获取微信支付信息
                    getPayInfo();
                }
                break;
        }
    }

    //获取支付信息
    private void getPayInfo() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("orderId", mOrderInfoResponse.getOrder_id());

        OkGo.post(Path.GET_PAY_INFO).tag(this).params(params).execute(new DialogCallback<List<WXPay>>(this, "获取支付信息中...") {

            @Override
            public void onSuccess(List<WXPay> wxPays, Call call, Response response) {
                WXPayUtils.payByWX(wxPays.get(0));
            }
        });

    }

    @Override
    protected boolean isBindEventBusHere() {
        return true;
    }

    @Override
    protected void onHandleEvent(EventManager eventManager) {
        super.onHandleEvent(eventManager);
        switch (eventManager.getEventCode()) {
            case AppConstants.WX_PAY_SUCCESS://微信支付成功
                updatePayState();
                break;
        }
    }

    private void updatePayState() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("orderId", mOrderInfoResponse.getOrder_id());

        OkGo.post(Path.CHECK_PAY_STATE).tag(this).params(params).execute(new StringDialogCallback(SelectPayActivity.this, "获取支付状态...") {
            @Override
            public void onSuccess(String result, Call call, Response response) {
                if (!TextUtils.isEmpty(result)) {
                    Gson gson = new GsonBuilder().create();
                    JsonResult jsonResult = gson.fromJson(result, new TypeToken<JsonResult>() {
                    }.getType());
                    if (jsonResult != null && jsonResult.getStatusCode() == 200) {//支付成功
                        EventBus.getDefault().post(new EventManager(AppConstants.PAY_FINISH));
                        Intent intent = new Intent(SelectPayActivity.this, PaySuccess.class);
                        intent.putExtra("payAmount", mOrderInfoResponse.getGoods_amount());
                        intent.putExtra("orderId", mOrderInfoResponse.getOrder_id());
                        intent.putExtra("selectServiceResponse", mSelectServiceResponse);
                        startActivity(intent);
                        finish();
                    } else if (jsonResult != null && jsonResult.getStatusCode() == 400) {
                        XToastUtils.showLongToast(jsonResult.getMessage());
                    } else if (jsonResult != null) {
                        XToastUtils.showLongToast(jsonResult.getMessage());
                    } else {
                        XToastUtils.showLongToast("查询失败");
                    }
                }

            }

        });
    }

}
