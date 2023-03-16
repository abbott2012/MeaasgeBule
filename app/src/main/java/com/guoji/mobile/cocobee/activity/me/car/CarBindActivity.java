package com.guoji.mobile.cocobee.activity.me.car;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

//import com.aiseminar.ui.CameraActivity;
import com.bql.utils.CheckUtils;
import com.bql.utils.EventManager;
import com.guoji.mobile.cocobee.R;
import com.guoji.mobile.cocobee.activity.CameraCarNumAct;
import com.guoji.mobile.cocobee.activity.base.BaseActivity;
import com.guoji.mobile.cocobee.common.AppConstants;
import com.guoji.mobile.cocobee.model.Car;
import com.guoji.mobile.cocobee.utils.XToastUtils;
import com.jzxiang.pickerview.TimePickerDialog;
import com.jzxiang.pickerview.data.Type;
import com.jzxiang.pickerview.listener.OnDateSetListener;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 车辆信息
 */
public class CarBindActivity extends BaseActivity implements OnDateSetListener {


    @BindView(R.id.ll_back_pro)
    ImageView mBackPro;
    @BindView(R.id.car_num)
    EditText mCarNum;
    @BindView(R.id.vi_num)
    EditText mVINum;
    @BindView(R.id.buy_price)
    EditText mBuyPrice;
    @BindView(R.id.buy_time)
    EditText mBuyTime;
    @BindView(R.id.model_type)
    EditText mModelType;
    @BindView(R.id.reg_next)
    TextView mRegNext;
    @BindView(R.id.tv_car_num)
    TextView mTvCarNum;

    private static ProgressDialog progressDialog;
    private Handler handler = new Handler();

    private TimePickerDialog mDialogAll;
    long tenYears = 10L * 365 * 1000 * 60 * 60 * 24L;


    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_bind_car;
    }

    @Override
    protected void initViewsAndEvents(Bundle savedInstanceState) {
        initTimePicker();
    }

    //初始化时间选择
    private void initTimePicker() {
        mDialogAll = new TimePickerDialog.Builder()
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


    @OnClick({R.id.tv_car_num, R.id.buy_time, R.id.reg_next, R.id.ll_back_pro})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_back_pro:
                finish();
                break;
            case R.id.tv_car_num://车牌识别
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setItems(new String[]{"电动车车牌识别", "汽车车牌识别"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                final Runnable mTasks = new Runnable() {
                                    public void run() {
                                        closeProgressDialog();
                                        startActivityForResult(new Intent(CarBindActivity.this, CameraCarNumAct.class), 6613);
                                    }
                                };
                                progressDialog = ProgressDialog.show(CarBindActivity.this, "", "正在跳转到识别界面…", true, false);

                                new Thread() {
                                    public void run() {
                                        try {
                                            sleep(1000);// 设置休眠时间
                                            handler.post(mTasks);// 发送
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }.start();
                                break;


                            case 1:
                                final Runnable mTasksTwo = new Runnable() {
                                    public void run() {
                                        closeProgressDialog();
//                                        startActivityForResult(new Intent(CarBindActivity.this, CameraActivity.class), 2323);
                                    }
                                };


                                progressDialog = ProgressDialog.show(CarBindActivity.this, "",
                                        "正在跳转到识别界面…", true, false);
                                new Thread() {
                                    public void run() {
                                        try {
                                            sleep(1000);// 设置休眠时间
                                            handler.post(mTasksTwo);// 发送
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }.start();
                                break;
                        }
                    }
                });

                builder.create().show();

                break;
            case R.id.buy_time://购买时间
                if (!mDialogAll.isAdded()) { //判断是否已经添加，避免因重复添加导致异常
                    mDialogAll.show(getSupportFragmentManager(), "all");
                }
                break;
            case R.id.reg_next://下一步
                String carNum = mCarNum.getText().toString().trim();
                String buyPrice = mBuyPrice.getText().toString().trim();
                String buyTime = mBuyTime.getText().toString().trim();
                String vINum = mVINum.getText().toString().trim();
                String modelType = mModelType.getText().toString().trim();
                if (CheckUtils.isEmpty(carNum)) {
                    XToastUtils.showLongToast("请输入车牌号");
                } else if (CheckUtils.isEmpty(buyPrice)) {
                    XToastUtils.showLongToast("请输入购买价格");
                } else if (CheckUtils.isEmpty(buyTime)) {
                    XToastUtils.showLongToast("请输入购买时间");
                } else {
                    Car car = new Car();
                    car.setCno(carNum);
                    car.setCbuyprice(buyPrice);
                    car.setCbuytime(buyTime);
                    car.setCframe(vINum);
                    car.setCbuytype(modelType);
                    Intent intent = new Intent(this, UploadCarPicActivity.class);
                    intent.putExtra("car", car);
                    startActivity(intent);
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2323 && resultCode == 2324) {
            mCarNum.setText(data.getStringExtra("plate"));
            return;
        } else if (requestCode == 6613 && resultCode == 6614) {
            String cnum = data.getStringExtra("cnum");
            mCarNum.setText(cnum);
            return;
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
//        closeProgressDialog();
    }

    public static void closeProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }

    }

    @Override
    public void onDateSet(TimePickerDialog timePickerView, long millseconds) {
        mBuyTime.setText(getDateToString(millseconds));
    }

    public String getDateToString(long time) {

        Date d = new Date(time);
        SimpleDateFormat sf = null;
//        （购买时间）或者其他
        sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
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
            case AppConstants.CAR_INFO_UPDATE_SUCCESS://车辆信息录入成功
                finish();
                break;
        }
    }
}
