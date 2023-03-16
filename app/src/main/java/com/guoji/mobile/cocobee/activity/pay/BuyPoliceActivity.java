package com.guoji.mobile.cocobee.activity.pay;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bql.baseadapter.recycleView.BH;
import com.bql.baseadapter.recycleView.QuickRcvAdapter;
import com.bql.utils.CheckUtils;
import com.bql.utils.EventManager;
import com.guoji.mobile.cocobee.R;
import com.guoji.mobile.cocobee.activity.base.BaseActivity;
import com.guoji.mobile.cocobee.adapter.MyAdapter;
import com.guoji.mobile.cocobee.callback.DialogCallback;
import com.guoji.mobile.cocobee.callback.StringComCallback;
import com.guoji.mobile.cocobee.common.AppConstants;
import com.guoji.mobile.cocobee.common.JsonResult;
import com.guoji.mobile.cocobee.common.Path;
import com.guoji.mobile.cocobee.model.CarScallResponse;
import com.guoji.mobile.cocobee.model.OrderInfo;
import com.guoji.mobile.cocobee.model.OrderInfoResponse;
import com.guoji.mobile.cocobee.utils.Clickable;
import com.guoji.mobile.cocobee.utils.DateUtils;
import com.guoji.mobile.cocobee.utils.Utils;
import com.guoji.mobile.cocobee.utils.XToastUtils;
import com.guoji.mobile.cocobee.view.ListLineDecoration;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;

/**
 * 购买保险
 * Created by Administrator on 2017/4/24.
 */

public class BuyPoliceActivity extends BaseActivity {


    @BindView(R.id.ll_back_pro)
    ImageView mLlBackPro;
    @BindView(R.id.ll_police_label)
    LinearLayout mLlPoliceLabel;
    @BindView(R.id.tv_max_price)
    TextView mTvMaxPrice;
    @BindView(R.id.tv_police_life)
    TextView mTvPoliceLife;
    @BindView(R.id.tv_buy_time)
    TextView mTvBuyTime;
    @BindView(R.id.tv_car_num)
    TextView mTvCarNum;
    @BindView(R.id.check_box)
    CheckBox mCheckBox;
    @BindView(R.id.user_xieyi)
    TextView mUserXieyi;
    @BindView(R.id.tv_to_buy_price)
    TextView mTvToBuyPrice;
    @BindView(R.id.tv_buy)
    TextView mTvBuy;
    @BindView(R.id.tv_time_price)
    TextView mTvTimePrice;


    private String tagid, name, phone, idcard, cno, model, motor;
    private int fromFlag = 0;
    private List<CarScallResponse> mPolicyList;
    private CarScallResponse mCarScallResponse;


    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_buy_police;
    }

    @Override
    protected void initViewsAndEvents(Bundle savedInstanceState) {
        user = Utils.getUserLoginInfo();
        //默认选中
        mCheckBox.setChecked(true);
        initIntent();
        initCheckBox();
        initView();
        loadData();

    }

    private void initCheckBox() {
        if (mCheckBox.isChecked()) {
            mCheckBox.setBackgroundResource(R.drawable.box_checked);
        } else {
            mCheckBox.setBackgroundResource(R.drawable.box_unchecked);
        }
    }

    private void loadData() {
        //获取保险档次
        OkGo.post(Path.CAR_STALL_INFO_PATH).tag(this).params("user_id", user.getPid()).execute(new StringComCallback(){
            @Override
            public void onSuccess(String result, Call call, Response response) {
                if (!TextUtils.isEmpty(result)) {
                    Gson gson = new Gson();
                    final JsonResult jr = gson.fromJson(result, new TypeToken<JsonResult>() {
                    }.getType());
                    if (jr != null && jr.getStatusCode() == 200) {
                        String jsonString = jr.getResult();
                        if (!TextUtils.isEmpty(jsonString)) {
                            mPolicyList = gson.fromJson(jsonString, new TypeToken<List<CarScallResponse>>() {
                            }.getType());
                            if (mPolicyList != null && mPolicyList.size() > 0) {
                                mCarScallResponse = mPolicyList.get(0);
                                setView(mCarScallResponse);
                            }

                        } else {
                            XToastUtils.showShortToast(jr.getMessage());
                        }
                    } else {
                        XToastUtils.showShortToast("获取档位信息失败");
                    }
                } else {
                    XToastUtils.showShortToast("获取档位信息失败");
                }
            }

            @Override
            public void onError(Call call, Response response, final Exception e) {
                super.onError(call, response, e);
                XToastUtils.showShortToast("获取档位信息失败");
            }

        });

    }

    private void setView(CarScallResponse carScallResponse) {
        mTvTimePrice.setVisibility(View.VISIBLE);
        mTvTimePrice.setText("¥ " + carScallResponse.getService_price() + " --- " + carScallResponse.getLife() + " 年");
        mTvMaxPrice.setText(carScallResponse.getMax_price());
        mTvMaxPrice.setVisibility(View.VISIBLE);
        mTvPoliceLife.setText(carScallResponse.getLife() + " 年");
        mTvPoliceLife.setVisibility(View.VISIBLE);
        mTvBuyTime.setText(DateUtils.getTomorow("yyyy-MM-dd"));
        mTvBuyTime.setVisibility(View.VISIBLE);
        mTvToBuyPrice.setText("¥ " + carScallResponse.getService_price());
        mTvToBuyPrice.setVisibility(View.VISIBLE);
    }

    private void initView() {
        String s = "我已充分了解该保险产品,已阅读并同意《电动车智能防盗险条款》内的所有内容";
        String s1 = "《电动车智能防盗险条款》";
        //高亮字体
//        Utils.highlightStr(mUserXieyi, s, s1, getResources().getColor(R.color.color_3270ed));
        SpannableString spannableString = new SpannableString(s);
        spannableString.setSpan(new Clickable(new View.OnClickListener() {

            @Override

            public void onClick(View v) {
                startActivity(new Intent(BuyPoliceActivity.this, InsuranceAgreementActivity.class));
            }

        }), 18, 30, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        mUserXieyi.setText(spannableString);
        mUserXieyi.setHighlightColor(getResources().getColor(android.R.color.transparent));
        mUserXieyi.setMovementMethod(LinkMovementMethod.getInstance());
        mTvCarNum.setText(cno);

    }

    private void initIntent() {
        Intent intent = getIntent();
        fromFlag = intent.getIntExtra("from", -1);
        if (fromFlag == 2146) { //上传成功后购买保单
            tagid = intent.getStringExtra("tagid");//标签号
            name = intent.getStringExtra("name");//车主姓名
            phone = intent.getStringExtra("phone");//电话
            idcard = intent.getStringExtra("idcard");//身份证号码
            cno = intent.getStringExtra("grade");//车牌号
            model = intent.getStringExtra("model");//品牌型号
            motor = intent.getStringExtra("motor");//发动机号
        }

    }


    @OnClick({R.id.ll_back_pro, R.id.ll_police_label, R.id.tv_buy, R.id.check_box})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_back_pro:
                finish();
                break;
            case R.id.ll_police_label://档位选择
                if (mPolicyList != null && mPolicyList.size() > 0) {
                    initDialog();
                } else {
                    //请求档位信息
                    loadData();
                }
                break;
            case R.id.tv_buy:
                //防止一秒内点击多次
                if (!Utils.isFastClick()) {
                   return;
                }
                String timePrice = mTvTimePrice.getText().toString().trim();
                if (CheckUtils.isEmpty(timePrice) || mCarScallResponse == null) {
                    XToastUtils.showShortToast("请选择保险档位");
                } else if (!mCheckBox.isChecked()) {
                    XToastUtils.showShortToast("请同意电动车防盗险条款");
                } else {
                    addOrder();
                }
                break;
            case R.id.check_box:
                initCheckBox();
                break;
        }
    }

    private void initDialog() {
        View view1 = View.inflate(this, R.layout.label_list, null);
        final AlertDialog alertDialog = Utils.showCornerDialog(this, view1, 270, 270);
        RecyclerView listView = (RecyclerView) alertDialog.findViewById(R.id.listView);
        TextView tvTitleCarLabel = (TextView) alertDialog.findViewById(R.id.tv_title_car_label);
        tvTitleCarLabel.setText("请选择保险档位(" + mPolicyList.size() + ")");
        MyAdapter adapter = new MyAdapter(this, mPolicyList);
        listView.setAdapter(adapter);
        listView.addItemDecoration(new ListLineDecoration());
        listView.setLayoutManager(new LinearLayoutManager(this));
        adapter.setOnItemClickListener(new QuickRcvAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(BH viewHolder, int position) {
                alertDialog.dismiss();
                mCarScallResponse = mPolicyList.get(position);
                setView(mCarScallResponse);
            }
        });
    }


    //生成订单
    private void addOrder() {

        OrderInfo orderInfo = new OrderInfo();
        String cno = mTvCarNum.getText().toString().trim();
        orderInfo.setSafeconfig_id(Integer.parseInt(mCarScallResponse.getId()));
        orderInfo.setCno(cno);
        orderInfo.setOrder_type(2);//Android端
        orderInfo.setUser_id(Integer.parseInt(user.getPid()));
        orderInfo.setGoods_amount(Double.parseDouble(mCarScallResponse.getService_price()));

        Map<String, String> params = new HashMap<String, String>();
        params.put("json", new Gson().toJson(orderInfo));

        OkGo.post(Path.PAY_ORDER_ADD).tag(this).params(params).execute(new DialogCallback<List<OrderInfoResponse>>(this, "订单生成中...") {

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
        Intent intent = new Intent(this, SelectPayActivity.class);
        intent.putExtra("orderInfoResponse", orderInfoResponse);
        startActivity(intent);
    }

    @Override
    protected boolean isBindEventBusHere() {
        return true;
    }

    @Override
    protected void onHandleEvent(EventManager eventManager) {
        super.onHandleEvent(eventManager);
        switch (eventManager.getEventCode()) {
            case AppConstants.PAY_FINISH://支付完成
                finish();
                break;
        }
    }
}
