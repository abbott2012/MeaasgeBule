package com.guoji.mobile.cocobee.activity.me;

import android.app.ProgressDialog;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bql.utils.CheckUtils;
import com.bql.utils.EventManager;
import com.guoji.mobile.cocobee.R;
import com.guoji.mobile.cocobee.activity.CameraIDCardAct;
import com.guoji.mobile.cocobee.activity.base.BaseToolbarActivity;
import com.guoji.mobile.cocobee.callback.DialogCallback;
import com.guoji.mobile.cocobee.common.AppConstants;
import com.guoji.mobile.cocobee.common.AppManager;
import com.guoji.mobile.cocobee.common.Path;
import com.guoji.mobile.cocobee.model.Car;
import com.guoji.mobile.cocobee.response.SelectServiceResponse;
import com.guoji.mobile.cocobee.utils.Devcode;
import com.guoji.mobile.cocobee.utils.SharedPreferencesHelper;
import com.guoji.mobile.cocobee.utils.Tools;
import com.guoji.mobile.cocobee.utils.Utils;
import com.guoji.mobile.cocobee.utils.XToastUtils;
import com.jzxiang.pickerview.TimePickerDialog;
import com.jzxiang.pickerview.data.Type;
import com.jzxiang.pickerview.listener.OnDateSetListener;
import com.lzy.okgo.OkGo;

import org.greenrobot.eventbus.EventBus;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import chinaSafe.idcard.android.AuthParameterMessage;
import chinaSafe.idcard.android.AuthService;
import okhttp3.Call;
import okhttp3.Response;

/**
 * 填写基本信息
 * Created by Administrator on 2017/4/14.
 */

public class CarInputBaseInfo extends BaseToolbarActivity implements OnDateSetListener {

    @BindView(R.id.ocr_idcard_iv)
    ImageView mOcrIdcardIv;
    @BindView(R.id.ucardid_et)
    EditText mUcardidEt;
    @BindView(R.id.uname_et)
    EditText mUnameEt;
    @BindView(R.id.check_box_man)
    CheckBox mCheckBoxMan;
    @BindView(R.id.check_box_woman)
    CheckBox mCheckBoxWoman;
    @BindView(R.id.ubirthday_et)
    EditText mUbirthdayEt;
    @BindView(R.id.uresidence_et)
    EditText mUresidenceEt;
    @BindView(R.id.tv_bind_biaoqian)
    TextView mTvBindBiaoqian;
    private Context context;
    private int ocr_idcard_type = 0;

    private Handler handler = new Handler();
    private static ProgressDialog progressDialog;
    private AuthService.authBinder authBinder;
    private int ReturnAuthority = -1;
    private String sn;
    private String devcode = Devcode.devcode;// 项目授权开发码


    private TimePickerDialog mDialogAll;
    long tenYears = 10L * 365 * 1000 * 60 * 60 * 24L;


    public ServiceConnection authConn = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            authBinder = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            authBinder = (AuthService.authBinder) service;
            try {

                AuthParameterMessage apm = new AuthParameterMessage();
                //apm.datefile = "assets"; // PATH+"/IDCard/wtdate.lsc";//预留
                apm.devcode = devcode;// 预留
                apm.sn = sn;

                ReturnAuthority = authBinder.getIDCardAuth(apm);
                if (ReturnAuthority != 0) { //激活失败。清楚已存储的序列号

                    switch (ReturnAuthority) {
                        case -10007:
                            Toast.makeText(getApplicationContext(),
                                    "序列号错误",
                                    Toast.LENGTH_SHORT).show();
                            break;

                        case -10008:
                            Toast.makeText(getApplicationContext(),
                                    "序列号已被使用",
                                    Toast.LENGTH_SHORT).show();
                            break;

                        case -10012:
                            break;

                        default:
                            Toast.makeText(getApplicationContext(),
                                    "ReturnAuthority:" + ReturnAuthority,
                                    Toast.LENGTH_SHORT).show();
                            break;
                    }


                    activeOCR();

                } else {//激活成功

                    /*跳转到识别界面*/
                    /**
                     * 由于在相机界面释放相机等资源会耗费很多时间， 为了优化用户体验，需要在调用相机的那个界面的oncreate()方法中
                     * 调用AppManager.getAppManager().finishAllActivity();
                     * 如果调用和识别的显示界面是同一个界面只需调用一次即可， 如果是不同界面，需要在显示界面的oncreate()方法中
                     * 调用AppManager.getAppManager().finishAllActivity();即可，
                     * 否则会造成相机资源的内存溢出。
                     */

                    final Runnable mTasks = new Runnable() {
                        public void run() {
                            Intent intent = new Intent(CarInputBaseInfo.this, CameraIDCardAct.class);
                            intent.putExtra("nMainId", SharedPreferencesHelper.getInt(getApplicationContext(), "nMainId", 2));
                            intent.putExtra("devcode", devcode);
                            intent.putExtra("flag", 0);
                            if (ocr_idcard_type == 1) {
                                startActivityForResult(intent, 6611);
                            } else if (ocr_idcard_type == 2) {
                                startActivityForResult(intent, 6161);
                            }
                        }
                    };
                    progressDialog = ProgressDialog.show(CarInputBaseInfo.this, "",
                            "正在跳转到识别界面…", true, false);

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

                }

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(),
                        getString(R.string.license_verification_failed),
                        Toast.LENGTH_LONG).show();

                activeOCR();

            } finally {
                if (authBinder != null) {
                    unbindService(authConn);
                }
            }
        }
    };
    private SelectServiceResponse mSelectServiceResponse;
    private String mOrderId;
    private String mLno;


    private void activeOCR() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("权限激活");
        final EditText editText = new EditText(context);
        LinearLayout ll = new LinearLayout(context);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.leftMargin = Tools.dip2px(context, 20);
        ll.addView(editText, layoutParams);

        editText.setHint("请输入序列号");
        builder.setView(ll);

        builder.setNegativeButton("取消", null);
        builder.setPositiveButton("激活", null);
        final AlertDialog alertDialog = builder.create();
        alertDialog.show(); //先show，这种情况下如果用户未输入文件名，则对话框不会消失

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sn = editText.getText().toString().trim();
                if (TextUtils.isEmpty(sn)) {
                    Toast.makeText(context, "请输入序列号", Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent authIntent = new Intent(CarInputBaseInfo.this, AuthService.class);
                bindService(authIntent, authConn, Service.BIND_AUTO_CREATE);
                alertDialog.dismiss();
            }
        });
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_input_base_info;
    }

    @Override
    protected void initViewsAndEvents(Bundle savedInstanceState) {
        initToolbar("录入个人信息");
        context = this;
        AppManager.getAppManager().addActivity(this);
        ButterKnife.bind(this);
        initTimePicker();
        initIntent();
    }

    private void initIntent() {
        Intent intent = getIntent();
        mSelectServiceResponse = (SelectServiceResponse) intent.getSerializableExtra("selectServiceResponse");
        mOrderId = intent.getStringExtra("orderId");
        mLno = intent.getStringExtra("lno");
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
                .setCyclic(false)
                .setMinMillseconds(System.currentTimeMillis() - 15 * tenYears) //当前时间减去一百五十年
                .setMaxMillseconds(System.currentTimeMillis())
                .setCurrentMillseconds(System.currentTimeMillis())
                .setThemeColor(getResources().getColor(R.color.timepicker_dialog_bg))
                .setType(Type.YEAR_MONTH_DAY)
                .setWheelItemTextNormalColor(getResources().getColor(R.color.timetimepicker_default_text_color))
                .setWheelItemTextSelectorColor(getResources().getColor(R.color.timepicker_toolbar_bg))
                .setWheelItemTextSize(12)
                .build();

    }


    @Override
    protected void onResume() {
        super.onResume();
        closeProgressDialog();
    }

    public static void closeProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 6612) {
            if (requestCode == 6611) {
                String cardid = data.getStringExtra("cardid");
                String name = data.getStringExtra("name");
                String gender = data.getStringExtra("gender");
                String birth = data.getStringExtra("birth");
                String address = data.getStringExtra("address");

                if (!TextUtils.isEmpty(cardid)) {
                    mUcardidEt.setText(cardid);
                }

                if (!TextUtils.isEmpty(name)) {
                    mUnameEt.setText(name);
                }

                if (TextUtils.equals("男", gender)) {
                    setCheckBox(mCheckBoxMan, mCheckBoxWoman);
                } else if (TextUtils.equals("女", gender)) {
                    setCheckBox(mCheckBoxWoman, mCheckBoxMan);
                }

                if (!TextUtils.isEmpty(birth)) {
                    mUbirthdayEt.setText(birth);
                }

                if (!TextUtils.isEmpty(address)) {
                    mUresidenceEt.setText(address);
                }
            }

            return;

        }
    }

    private void setCheckBox(CheckBox checkBoxMan, CheckBox checkBoxWoman) {
        checkBoxMan.setChecked(true);
        checkBoxMan.setButtonDrawable(R.drawable.select_point);
        checkBoxMan.setTextColor(getResources().getColor(R.color.color_3270ed));
        checkBoxWoman.setChecked(false);
        checkBoxWoman.setTextColor(getResources().getColor(R.color.color_cccccc));
        checkBoxWoman.setButtonDrawable(R.drawable.not_select_point);
    }

    @Override
    public void onDateSet(TimePickerDialog timePickerView, long millseconds) {
        mUbirthdayEt.setText(getDateToString(millseconds));
    }

    public String getDateToString(long time) {

        Date d = new Date(time);
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        return sf.format(d);
    }

    @OnClick({R.id.ocr_idcard_iv, R.id.check_box_man, R.id.check_box_woman, R.id.ubirthday_et, R.id.tv_bind_biaoqian})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ocr_idcard_iv://ocr身份证识别
                //防止一秒内点击多次
                if (!Utils.isFastClick()) {
                    return;
                }
                ocr_idcard_type = 1;
                Intent authIntent = new Intent(this, AuthService.class);
                bindService(authIntent, authConn, Service.BIND_AUTO_CREATE);
                break;
            case R.id.check_box_man:
                mCheckBoxMan.setChecked(true);
                mCheckBoxWoman.setChecked(false);
                setCheckBox(mCheckBoxMan, mCheckBoxWoman);
                break;
            case R.id.check_box_woman:
                mCheckBoxMan.setChecked(false);
                mCheckBoxWoman.setChecked(true);
                setCheckBox(mCheckBoxWoman, mCheckBoxMan);
                break;
            case R.id.ubirthday_et:
                if (!mDialogAll.isAdded()) { //判断是否已经添加，避免因重复添加导致异常
                    mDialogAll.show(getSupportFragmentManager(), "all");
                }
                break;
            case R.id.tv_bind_biaoqian:
                //防止一秒内点击多次
                if (!Utils.isFastClick()) {
                    return;
                }
                String ucardidEt = mUcardidEt.getText().toString().trim();//身份证
                String unameEt = mUnameEt.getText().toString().trim();//姓名
                String ubirthdayEt = mUbirthdayEt.getText().toString().trim();//生日
                String userFirstaddress = mUresidenceEt.getText().toString().trim();//户籍地址
                String idCArdNum = "";
                try {
                    idCArdNum = CheckUtils.IDCardValidate(ucardidEt);
                } catch (ParseException e) {
                    e.printStackTrace();
                    XToastUtils.showLongToast("身份证无效");
                }
                if (CheckUtils.isEmpty(ucardidEt)) {
                    XToastUtils.showLongToast("请输入身份证号码");
                } else if (CheckUtils.isEmpty(unameEt)) {
                    XToastUtils.showLongToast("请输入姓名");
                } else if (!CheckUtils.equalsString(idCArdNum, "")) {
                    XToastUtils.showLongToast("身份证无效");
                } else if (!mCheckBoxWoman.isChecked() && !mCheckBoxMan.isChecked()) {
                    XToastUtils.showLongToast("请选择性别");
                } else if (CheckUtils.isEmpty(ubirthdayEt)) {
                    XToastUtils.showLongToast("请选择出生日期");
                } else if (CheckUtils.isEmpty(userFirstaddress)) {
                    XToastUtils.showLongToast("请选择户籍地址");
                } else {//下一步
                    //调用完善个人信息接口
                    if (mCheckBoxMan.isChecked()) {//0男 1女
                        updateUserInfo(ucardidEt, unameEt, "0", userFirstaddress, ubirthdayEt);
                    } else if (mCheckBoxWoman.isChecked()) {
                        updateUserInfo(ucardidEt, unameEt, "1", userFirstaddress, ubirthdayEt);
                    }
                }
                break;
        }
    }

    //完善个人信息
    private void updateUserInfo(String ucardidEt, String unameEt, String sex, String userFirstaddress, String ubirthdayEt) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("idcard", ucardidEt);
        params.put("pname", unameEt);
        params.put("sex", sex);
        params.put("regiaddr", userFirstaddress);
        params.put("birthday", ubirthdayEt);
        params.put("pid", user.getPid());
        OkGo.post(Path.UPDATE_USE_INFO).tag(this).params(params).execute(new DialogCallback<Object>(CarInputBaseInfo.this, "信息上传中...") {
            @Override
            public void onSuccess(Object o, Call call, Response response) {
                user.setIdcard(ucardidEt);
                user.setPname(unameEt);
                user.setSex(sex);
                user.setBirthday(ubirthdayEt);
                user.setRegiaddr(userFirstaddress);
                Utils.putUserLoginInfo(user);
                EventBus.getDefault().post(new EventManager(AppConstants.INFO_PUT_SUCCESS));
                gotoNex(ucardidEt, unameEt, sex, userFirstaddress, ubirthdayEt);
            }
        });
    }

    private void gotoNex(String ucardidEt, String unameEt, String sex, String userFirstaddress, String ubirthdayEt) {
        Intent intent = new Intent(CarInputBaseInfo.this, LabelBindingCarAnXinActivity.class);
        Car car = new Car();
        car.setuIdCard(ucardidEt);
        car.setuName(unameEt);
        car.setuSex(sex);
        car.setuBirthday(ubirthdayEt);
        car.setuFirstAddress(userFirstaddress);
        intent.putExtra("car", car);
        intent.putExtra("selectServiceResponse", mSelectServiceResponse);
        if (mOrderId != null) {//为空说明是定制卡
            intent.putExtra("orderId", mOrderId);
        }
        if (mLno != null) {//标签号不为空,说明是升级安心卡
            intent.putExtra("lno", mLno);
        }
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
            case AppConstants.CAR_INFO_UPDATE_SUCCESS://车安心卡绑定成功
                finish();
                break;
        }
    }
}
