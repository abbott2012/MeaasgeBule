package com.guoji.mobile.cocobee.activity.me;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bql.utils.CheckUtils;
import com.bql.utils.EventManager;
import com.guoji.mobile.cocobee.R;
import com.guoji.mobile.cocobee.activity.BarCodeScanAct;
import com.guoji.mobile.cocobee.activity.base.BaseToolbarActivity;
import com.guoji.mobile.cocobee.activity.me.car.UploadCarPicActivity;
import com.guoji.mobile.cocobee.callback.JsonCallback;
import com.guoji.mobile.cocobee.common.AppConstants;
import com.guoji.mobile.cocobee.common.Path;
import com.guoji.mobile.cocobee.model.Car;
import com.guoji.mobile.cocobee.model.User;
import com.guoji.mobile.cocobee.response.SelectServiceResponse;
import com.guoji.mobile.cocobee.utils.Utils;
import com.guoji.mobile.cocobee.utils.XToastUtils;
import com.jzxiang.pickerview.TimePickerDialog;
import com.jzxiang.pickerview.data.Type;
import com.jzxiang.pickerview.listener.OnDateSetListener;
import com.lzy.okgo.OkGo;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;

/**
 * 标签绑定
 * Created by Administrator on 2017/4/21.
 */

public class LabelBindingCarAnXinActivity extends BaseToolbarActivity implements OnDateSetListener {

    @BindView(R.id.et_label_bind)
    EditText mEtLabelBind;
    @BindView(R.id.iv_scan)
    ImageView mIvScan;
    @BindView(R.id.et_name)
    EditText mEtName;
    @BindView(R.id.et_car_num)
    EditText mEtCarNum;
    @BindView(R.id.et_car_jia_num)
    EditText mEtCarJiaNum;
    @BindView(R.id.et_car_buy_price)
    EditText mEtCarBuyPrice;
    @BindView(R.id.et_car_buy_time)
    EditText mEtCarBuyTime;
    @BindView(R.id.tv_bind_biaoqian)
    TextView mTvBindBiaoqian;
    private User mUser;

    private TimePickerDialog mDialogAll;
    long tenYears = 10L * 365 * 1000 * 60 * 60 * 24L;
    private SelectServiceResponse mSelectServiceResponse;
    private String mOrderId;
    private String mLno;
    private TimePickerDialog.Builder mBuilder;

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_label_binding_car_an_xin;
    }

    @Override
    protected void initViewsAndEvents(Bundle savedInstanceState) {
        initToolbar("绑定标签");
        initIntent();
        //获取intent传递过来的数据
        mUser = Utils.getUserLoginInfo();
        mBuilder = new TimePickerDialog.Builder();
    }


    //初始化时间选择
    private void initTimePicker() {
        mDialogAll = mBuilder
                .setCallBack(this)
                .setCancelStringId("取消")
                .setSureStringId("确定")
                .setTitleStringId("请选择时间")
                .setYearText("年")
                .setMonthText("月")
                .setDayText("日")
                .setHourText("时")
                .setMinuteText("分")
                .setCyclic(false)
                .setMinMillseconds(System.currentTimeMillis() - 15 * tenYears) //当前时间减去一百五十年
                .setMaxMillseconds(System.currentTimeMillis())
                .setCurrentMillseconds(System.currentTimeMillis())
                .setThemeColor(getResources().getColor(R.color.timepicker_dialog_bg))
                .setType(Type.ALL)
                .setWheelItemTextNormalColor(getResources().getColor(R.color.timetimepicker_default_text_color))
                .setWheelItemTextSelectorColor(getResources().getColor(R.color.timepicker_toolbar_bg))
                .setWheelItemTextSize(12)
                .build();

    }


    private void initIntent() {
        Intent intent = getIntent();
        mSelectServiceResponse = (SelectServiceResponse) intent.getSerializableExtra("selectServiceResponse");
        mOrderId = intent.getStringExtra("orderId");
        mLno = intent.getStringExtra("lno");
        initView();
    }


    private void initView() {
        if (mLno != null) {//标签号不为空,说明是体验卡升级安心卡
            mEtLabelBind.setText(mLno);
            mEtLabelBind.setFocusable(false);
        }
    }

    private void checkTag(final String scanResult) {
        Map<String, String> params = new HashMap<String, String>();
        String path;
        params.put("lno", scanResult);
        if (CheckUtils.equalsString(mSelectServiceResponse.getCard_id(), AppConstants.TYPE_CARD_DINZHI)) {
            params.put("orgid", mUser.getOrgid());
            path = Path.BIND_LABEL_DINZHI;
        } else {
            path = Path.CHECK_TAG_PATH;
        }
        OkGo.post(path).tag(this).params(params).execute(new JsonCallback<Object>(LabelBindingCarAnXinActivity.this) {

            @Override
            public void onSuccess(Object o, Call call, Response response) {
                gotoUpPic();
            }
        });

    }

    //进入图片上传页面
    private void gotoUpPic() {
        String labelNum = mEtLabelBind.getText().toString().trim();//标签号
        String type = mEtName.getText().toString().trim();//品牌型号
        String carNum = mEtCarNum.getText().toString().trim();//车牌号
        String carJiaNum = mEtCarJiaNum.getText().toString().trim();//车架号
        String carBuyPrice = mEtCarBuyPrice.getText().toString().trim();//购买价格
        String carBuyTime = mEtCarBuyTime.getText().toString().trim();//购买时间
        Intent intent = new Intent(LabelBindingCarAnXinActivity.this, UploadCarPicActivity.class);
        Car car = new Car();
        car.setLabelNum(labelNum);
        car.setCbuytype(type);
        car.setCno(carNum);
        car.setCframe(carJiaNum);
        car.setCbuyprice(carBuyPrice);
        car.setCbuytime(carBuyTime);
        intent.putExtra("car", car);
        intent.putExtra("selectServiceResponse", mSelectServiceResponse);
        intent.putExtra("orderId", mOrderId);
        intent.putExtra("lno", mLno);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1666 && resultCode == 1667) {
            String scanResult = data.getStringExtra("scanResult");
            mEtLabelBind.setText(scanResult);
            return;
        }
    }

    @OnClick({R.id.iv_scan, R.id.et_car_buy_time, R.id.tv_bind_biaoqian})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_scan:
                if (mLno != null) {
                    return;
                }
                startActivityForResult(new Intent(this, BarCodeScanAct.class), 1666);
                break;
            case R.id.et_car_buy_time://购买时间
                initTimePicker();
                if (!mDialogAll.isAdded()) { //判断是否已经添加，避免因重复添加导致异常
                    mDialogAll.show(getSupportFragmentManager(), "all");
                }
                break;
            case R.id.tv_bind_biaoqian://下一步
                //防止一秒内点击多次
                if (!Utils.isFastClick()) {
                    return;
                }
                String labelNum = mEtLabelBind.getText().toString().trim();//标签号
                String name = mEtName.getText().toString().trim();//品牌型号
                String carNum = mEtCarNum.getText().toString().trim();//车牌号
                String carBuyPrice = mEtCarBuyPrice.getText().toString().trim();//购买价格
                String carBuyTime = mEtCarBuyTime.getText().toString().trim();//购买时间
                int i = 0;
                if (!CheckUtils.isEmpty(carBuyPrice)) {
                    i = Integer.parseInt(carBuyPrice);
                }
                if (CheckUtils.isEmpty(labelNum)) {
                    XToastUtils.showShortToast("请输入标签号或扫描标签号");
                } else if (CheckUtils.isEmpty(name)) {
                    XToastUtils.showShortToast("请输入车辆的品牌型号");
                } else if (CheckUtils.isEmpty(carNum)) {
                    XToastUtils.showShortToast("请输入车牌号");
                } else if (CheckUtils.isEmpty(carBuyPrice)) {
                    XToastUtils.showShortToast("请输入车辆的购买价格");
                } else if (i == 0) {
                    XToastUtils.showShortToast("购买价格不能为0");
                } else if (CheckUtils.isEmpty(carBuyTime)) {
                    XToastUtils.showShortToast("请输入车辆的购买时间");
                } else if (mLno != null) {
                    gotoUpPic();
                } else {
                    checkTag(labelNum);
                }
                break;
        }
    }

    @Override
    public void onDateSet(TimePickerDialog timePickerView, long millseconds) {
        mEtCarBuyTime.setText(getDateToString(millseconds));
    }

    public String getDateToString(long time) {

        Date d = new Date(time);
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        （购买时间）或者其他
        return sf.format(d);
    }

    @Override
    protected boolean isBindEventBusHere() {
        return true;
    }

    @Override
    protected void onHandleEvent(EventManager eventManager) {
        super.onHandleEvent(eventManager);
        switch (eventManager.getEventCode()) {
            case AppConstants.CAR_INFO_UPDATE_SUCCESS://车安心卡绑定成功
                finish();
                break;
        }
    }
}
