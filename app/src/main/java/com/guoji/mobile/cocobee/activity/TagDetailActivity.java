package com.guoji.mobile.cocobee.activity;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.guoji.mobile.cocobee.R;
import com.guoji.mobile.cocobee.activity.base.BaseToolbarActivity;
import com.guoji.mobile.cocobee.callback.JsonCallback;
import com.guoji.mobile.cocobee.common.Path;
import com.guoji.mobile.cocobee.response.LabelInfoResponse;
import com.guoji.mobile.cocobee.response.TagResponse;
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
 * 标签详情页面
 * Created by _H_JY on 2017/1/9.
 */
public class TagDetailActivity extends BaseToolbarActivity implements OnDateSetListener {

    @BindView(R.id.tv_label_num)
    TextView mTvLabelNum;
    @BindView(R.id.tv_latlont)
    TextView mTvLatlont;
    @BindView(R.id.tv_up_time)
    TextView mTvUpTime;
    @BindView(R.id.ll_car_user_info)
    LinearLayout mLlCarUserInfo;
    @BindView(R.id.tv_name)
    TextView mTvName;
    @BindView(R.id.tv_sex)
    TextView mTvSex;
    @BindView(R.id.tv_birth_time)
    TextView mTvBirthTime;
    @BindView(R.id.tv_address_now)
    TextView mTvAddressNow;
    @BindView(R.id.tv_address_origin)
    TextView mTvAddressOrigin;
    @BindView(R.id.tv_phone_num)
    TextView mTvPhoneNum;
    @BindView(R.id.tv_identity)
    TextView mTvIdentity;
    @BindView(R.id.tv_email)
    TextView mTvEmail;
    @BindView(R.id.tv_work_place)
    TextView mTvWorkPlace;
    @BindView(R.id.ll_up_location)
    LinearLayout mLlUpLocation;
    @BindView(R.id.view1)
    View mView1;
    @BindView(R.id.ll_up_time)
    LinearLayout mLlUpTime;
    @BindView(R.id.view2)
    View mView2;

    private TagResponse mTagResponse;
    private int mScanType;
    private LocationClient locationClient;
    private double longitude;
    private double latitude;
    private String address;
    private TimePickerDialog.Builder timePickerBuilder;
    private TimePickerDialog mDialogAll;
    long tenYears = 10L * 365 * 1000 * 60 * 60 * 24L;

    //初始化时间选择
    private void initTimePicker() {
        timePickerBuilder = new TimePickerDialog.Builder()
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
                .setMinMillseconds(System.currentTimeMillis() - 3 * tenYears)
                .setMaxMillseconds(System.currentTimeMillis())
                .setThemeColor(getResources().getColor(R.color.timepicker_dialog_bg))
                .setType(Type.ALL)
                .setWheelItemTextNormalColor(getResources().getColor(R.color.timetimepicker_default_text_color))
                .setWheelItemTextSelectorColor(getResources().getColor(R.color.timepicker_toolbar_bg))
                .setWheelItemTextSize(12);

        mDialogAll = timePickerBuilder.build();

    }


    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_tag_detail;
    }

    @Override
    protected void initViewsAndEvents(Bundle savedInstanceState) {
        mTagResponse = (TagResponse) getIntent().getSerializableExtra("tagResponse");
        mScanType = mTagResponse.getScanType();
        if (mScanType == 1) {
            initToolbar("发卡模式");
            setFakaView(View.GONE);
        } else {
            initToolbar("查车模式");
            setFakaView(View.GONE);
        }
        // 默认软键盘不弹出
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
//        getBaiduAddress();
        initTimePicker();
        initView();
        getLabelInfo();
    }

    //设置查车发卡
    private void setFakaView(int visible) {
        mLlUpLocation.setVisibility(visible);
        mLlUpLocation.setVisibility(visible);
        mView1.setVisibility(visible);
        mView2.setVisibility(visible);
    }

    private void initView() {
        mTvLabelNum.setText(mTagResponse.getLno());
    }

    private void getBaiduAddress() {
    /*使用百度SDK获取经纬度*/
        locationClient = new LocationClient(this);
        LocationClientOption option = new LocationClientOption();
        option.setIsNeedAddress(true);
        option.setOpenGps(true);        //是否打开GPS
        option.setCoorType("bd09ll");       //设置返回值的坐标类型。
        option.setPriority(LocationClientOption.NetWorkFirst);  //设置定位优先级
        option.setProdName("Cocobee"); //设置产品线名称。强烈建议您使用自定义的产品线名称，方便我们以后为您提供更高效准确的定位服务。
        option.setScanSpan(30000);  //设置定时定位的时间间隔为10分钟。单位毫秒
        locationClient.setLocOption(option);

        // 注册位置监听器
        locationClient.registerLocationListener(new BDLocationListener() {
            @Override
            public void onReceiveLocation(BDLocation location) {
                if (location == null) {
                    return;
                }
                longitude = location.getLongitude(); //获取经度
                latitude = location.getLatitude();
                address = location.getAddrStr();


                String locationStr = "经度:" + location.getLongitude() + " 纬度:" + location.getLatitude();

                mTvLatlont.setText(locationStr);
            }
        });
        locationClient.start();
                    /*
                     *当所设的整数值大于等于1000（ms）时，定位SDK内部使用定时定位模式。
                     *调用requestLocation( )后，每隔设定的时间，定位SDK就会进行一次定位。
                     *如果定位SDK根据定位依据发现位置没有发生变化，就不会发起网络请求，
                     *返回上一次定位的结果；如果发现位置改变，就进行网络请求进行定位，得到新的定位结果。
                     *定时定位时，调用一次requestLocation，会定时监听到定位结果。
                     */
        locationClient.requestLocation();
    }


    @OnClick({R.id.tv_up_time/*, R.id.tv_upload, R.id.tv_download*/})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_up_time:
                timePickerBuilder.setMaxMillseconds(System.currentTimeMillis());
                if (!mDialogAll.isAdded()) { //判断是否已经添加，避免因重复添加导致异常
                    mDialogAll.show(getSupportFragmentManager(), "all");
                }
                break;

        }
    }

    //获取标签信息
    private void getLabelInfo() {
        Map<String, String> params = new HashMap<>();
        params.put("BQCODE", mTagResponse.getLno());

        OkGo.post(Path.SEARCH_CAR_DOWNLOAD).params(params).execute(new JsonCallback<LabelInfoResponse>(TagDetailActivity.this) {

            @Override
            public void onSuccess(LabelInfoResponse labelInfoResponse, Call call, Response response) {
                if (labelInfoResponse != null) {
                    setView(labelInfoResponse);
                } else {
                    XToastUtils.showShortToast("暂无相关信息");
                }
            }
        });

    }

    private void setView(LabelInfoResponse labelInfoResponse) {
        mTvName.setText(labelInfoResponse.getRYZSMC());
        mTvSex.setText(labelInfoResponse.getXB());
        mTvBirthTime.setText(labelInfoResponse.getCSRQ());
        mTvAddressNow.setText(labelInfoResponse.getXJZD());
        mTvAddressOrigin.setText(labelInfoResponse.getHJDZ());
        mTvPhoneNum.setText(labelInfoResponse.getDH());
        mTvIdentity.setText(labelInfoResponse.getSFZ());
        mTvEmail.setText(labelInfoResponse.getYX());
        mTvWorkPlace.setText(labelInfoResponse.getGZDW());
    }

    @Override
    public void onDateSet(TimePickerDialog timePickerView, long millseconds) {
        mTvUpTime.setText(getDateToString(millseconds));
    }

    public String getDateToString(long time) {

        Date d = new Date(time);
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sf.format(d);
    }

    @Override
    protected void onDestroy() {
        // 退出时销毁定位
//        locationClient.stop();
        super.onDestroy();
    }

}
