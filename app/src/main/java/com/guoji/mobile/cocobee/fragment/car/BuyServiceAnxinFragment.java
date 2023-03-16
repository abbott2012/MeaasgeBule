package com.guoji.mobile.cocobee.fragment.car;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bql.baseadapter.recycleView.BH;
import com.bql.baseadapter.recycleView.QuickRcvAdapter;
import com.bql.utils.CheckUtils;
import com.bql.utils.EventManager;
import com.guoji.mobile.cocobee.R;
import com.guoji.mobile.cocobee.activity.pay.InsuranceAgreementActivity;
import com.guoji.mobile.cocobee.activity.pay.SelectPayActivity;
import com.guoji.mobile.cocobee.adapter.MyCarLabelAdapter;
import com.guoji.mobile.cocobee.callback.DialogCallback;
import com.guoji.mobile.cocobee.common.AppConstants;
import com.guoji.mobile.cocobee.common.Path;
import com.guoji.mobile.cocobee.fragment.base.BaseFragment;
import com.guoji.mobile.cocobee.model.OrderInfo;
import com.guoji.mobile.cocobee.model.OrderInfoResponse;
import com.guoji.mobile.cocobee.model.User;
import com.guoji.mobile.cocobee.response.CardLabelResponse;
import com.guoji.mobile.cocobee.response.SelectServiceResponse;
import com.guoji.mobile.cocobee.utils.Clickable;
import com.guoji.mobile.cocobee.utils.DateUtils;
import com.guoji.mobile.cocobee.utils.Utils;
import com.guoji.mobile.cocobee.utils.XToastUtils;
import com.guoji.mobile.cocobee.view.ListLineDecoration;
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

public class BuyServiceAnxinFragment extends BaseFragment {


    @BindView(R.id.tv_card_title)
    TextView mTvCardTitle;
    @BindView(R.id.tv_card_content)
    TextView mTvCardContent;
    @BindView(R.id.ll_card_type)
    LinearLayout mLlCardType;
    @BindView(R.id.card_detail_title)
    TextView mCardDetailTitle;
    @BindView(R.id.tv_time_price)
    TextView mTvTimePrice;
    @BindView(R.id.ll_police_label)
    LinearLayout mLlPoliceLabel;
    @BindView(R.id.tv_max_price)
    TextView mTvMaxPrice;
    @BindView(R.id.tv_police_life)
    TextView mTvPoliceLife;
    @BindView(R.id.tv_buy_time)
    TextView mTvBuyTime;
    @BindView(R.id.check_box)
    CheckBox mCheckBox;
    @BindView(R.id.user_xieyi)
    TextView mUserXieyi;
    @BindView(R.id.tv_pay_price)
    TextView mTvPayPrice;
    @BindView(R.id.tv_goto_pay)
    TextView mTvGotoPay;

    private static SelectServiceResponse selectServiceResponse;
    private static CardLabelResponse mCardLabelResponse;
    @BindView(R.id.ll_pay_price)
    LinearLayout mLlPayPrice;
    private User mUserLoginInfo;

    public static BuyServiceAnxinFragment getInstance(SelectServiceResponse selectServiceResponse, CardLabelResponse cardLabelResponse) {
        BuyServiceAnxinFragment.selectServiceResponse = selectServiceResponse;
        BuyServiceAnxinFragment.mCardLabelResponse = cardLabelResponse;
        BuyServiceAnxinFragment fragment = new BuyServiceAnxinFragment();
        return fragment;
    }

    @Override
    protected void initViewsAndEvents(Bundle savedInstanceState) {
        mUserLoginInfo = Utils.getUserLoginInfo();
        initView();
        initUserXieyi();
        //默认选中
        mCheckBox.setChecked(true);
    }

    private void initUserXieyi() {
        String s = "我已阅读并同意《电动车智能防盗险条款》的所有内容";
        SpannableString spannableString = new SpannableString(s);
        spannableString.setSpan(new Clickable(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(_mActivity, InsuranceAgreementActivity.class));
            }

        }), 7, 19, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        mUserXieyi.setText(spannableString);
        mUserXieyi.setHighlightColor(getResources().getColor(android.R.color.transparent));
        mUserXieyi.setMovementMethod(LinkMovementMethod.getInstance());
    }


    private void initView() {
        if (selectServiceResponse == null) {
            return;
        }
        mTvCardTitle.setText(selectServiceResponse.getCard_name());
        mTvCardContent.setText(selectServiceResponse.getCard_ddesc());
        mLlCardType.setBackgroundResource(R.drawable.card_bg_orange);
        mCardDetailTitle.setText("安心卡说明");
        initLabelInfo(mCardLabelResponse);

    }

    private void initLabelInfo(CardLabelResponse cardLabelResponse) {
        if (cardLabelResponse == null){
            return;
        }
        mTvTimePrice.setVisibility(View.VISIBLE);
        mTvMaxPrice.setVisibility(View.VISIBLE);
        mTvPoliceLife.setVisibility(View.VISIBLE);
        mTvBuyTime.setVisibility(View.VISIBLE);
        mLlPayPrice.setVisibility(View.VISIBLE);
        mTvTimePrice.setText("¥ " + cardLabelResponse.getService_price() + " / " + cardLabelResponse.getLife() + " 年");
        mTvMaxPrice.setText(cardLabelResponse.getMax_price() + "元");
        mTvPoliceLife.setText(cardLabelResponse.getLife() + "年");
        mTvBuyTime.setText(DateUtils.getTomorow("yyyy-MM-dd"));
        mTvPayPrice.setText(cardLabelResponse.getService_price());
    }

    @Override
    protected void initToolbarHere() {
        initToolbar("购买服务");
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.fragment_buy_service_anxin;
    }

    @OnClick({R.id.tv_time_price, R.id.tv_goto_pay, R.id.check_box})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_time_price:
                getCardPrice();
                break;
            case R.id.tv_goto_pay:
                //防止一秒内点击多次
                if (!Utils.isFastClick()) {
                    return;
                }
                String timePrice = mTvTimePrice.getText().toString().trim();
                if (CheckUtils.isEmpty(timePrice) || mCardLabelResponse == null) {
                    XToastUtils.showShortToast("请选择保险档位");
                } else if (!mCheckBox.isChecked()) {
                    XToastUtils.showShortToast("请同意电动车防盗险条款");
                } else {
                    addOrder();
                }
                break;
        }
    }

    //生成订单
    private void addOrder() {
        mUserLoginInfo = Utils.getUserLoginInfo();
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setSafeconfig_id(Integer.parseInt(mCardLabelResponse.getId()));
        orderInfo.setOrder_type(2);//Android端
        orderInfo.setUser_id(Integer.parseInt(mUserLoginInfo.getPid()));
        orderInfo.setGoods_amount(Double.parseDouble(mCardLabelResponse.getService_price()));
        orderInfo.setCard_id(selectServiceResponse.getCard_id());
        orderInfo.setTarget_id(selectServiceResponse.getTarget_id());

        Map<String, String> params = new HashMap<String, String>();
        params.put("json", new Gson().toJson(orderInfo));

        OkGo.post(Path.PAY_ORDER_ADD).tag(this).params(params).execute(new DialogCallback<List<OrderInfoResponse>>(getActivity(), "订单生成中...") {

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

    //获取档位信息
    private void getCardPrice() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("orgid", mUserLoginInfo.getOrgid());
        params.put("card_id", selectServiceResponse.getCard_id());
        params.put("target_id", selectServiceResponse.getTarget_id());

        OkGo.post(Path.GET_CAR_LABEL_PRICE).params(params).execute(new DialogCallback<List<CardLabelResponse>>(_mActivity, "获取卡档位中...") {

            @Override
            public void onSuccess(List<CardLabelResponse> cardLabelResponses, Call call, Response response) {
                if (cardLabelResponses != null && cardLabelResponses.size() > 0) {
                    initDialog(cardLabelResponses);
                } else {
                    XToastUtils.showShortToast("该账户未配置保险");
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


    //弹选择框
    private void initDialog(List<CardLabelResponse> cardLabelResponses) {
        View view1 = View.inflate(_mActivity, R.layout.label_list, null);
        final AlertDialog alertDialog = Utils.showCornerDialog(_mActivity, view1, 270, 270);
        RecyclerView listView = (RecyclerView) alertDialog.findViewById(R.id.listView);
        TextView tvTitleCarLabel = (TextView) alertDialog.findViewById(R.id.tv_title_car_label);
        tvTitleCarLabel.setText("请选择保险档位(" + cardLabelResponses.size() + ")");
        MyCarLabelAdapter adapter = new MyCarLabelAdapter(_mActivity, cardLabelResponses);
        listView.setAdapter(adapter);
        listView.addItemDecoration(new ListLineDecoration());
        listView.setLayoutManager(new LinearLayoutManager(_mActivity));
        adapter.setOnItemClickListener(new QuickRcvAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(BH viewHolder, int position) {
                alertDialog.dismiss();
                mCardLabelResponse = cardLabelResponses.get(position);
                initLabelInfo(mCardLabelResponse);
            }
        });
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
