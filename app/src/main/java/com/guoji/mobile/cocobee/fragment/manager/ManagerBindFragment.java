package com.guoji.mobile.cocobee.fragment.manager;

import android.app.ProgressDialog;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baoyz.actionsheet.ActionSheet;
import com.bql.baseadapter.recycleView.BH;
import com.bql.baseadapter.recycleView.QuickRcvAdapter;
import com.bql.utils.CheckUtils;
import com.guoji.mobile.cocobee.R;
import com.guoji.mobile.cocobee.activity.BarCodeScanAct;
import com.guoji.mobile.cocobee.activity.CameraIDCardAct;
import com.guoji.mobile.cocobee.adapter.PersonTypeAdapter;
import com.guoji.mobile.cocobee.adapter.SelectOrgAdapter;
import com.guoji.mobile.cocobee.adapter.SelectServiceTypeAdapter;
import com.guoji.mobile.cocobee.callback.DialogCallback;
import com.guoji.mobile.cocobee.callback.JsonCallback;
import com.guoji.mobile.cocobee.common.AppConstants;
import com.guoji.mobile.cocobee.common.Path;
import com.guoji.mobile.cocobee.fragment.base.BaseFragment;
import com.guoji.mobile.cocobee.model.PoliceStation;
import com.guoji.mobile.cocobee.model.User;
import com.guoji.mobile.cocobee.response.PersonTypeResponse;
import com.guoji.mobile.cocobee.response.SelectServiceResponse;
import com.guoji.mobile.cocobee.utils.Devcode;
import com.guoji.mobile.cocobee.utils.ImageUtil;
import com.guoji.mobile.cocobee.utils.SharedPreferencesHelper;
import com.guoji.mobile.cocobee.utils.Tools;
import com.guoji.mobile.cocobee.utils.Utils;
import com.guoji.mobile.cocobee.utils.XToastUtils;
import com.guoji.mobile.cocobee.view.ListLineDecoration;
import com.guoji.mobile.cocobee.view.SwitchMultiButton;
import com.jzxiang.pickerview.TimePickerDialog;
import com.jzxiang.pickerview.data.Type;
import com.jzxiang.pickerview.listener.OnDateSetListener;
import com.lzy.okgo.OkGo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import chinaSafe.idcard.android.AuthParameterMessage;
import chinaSafe.idcard.android.AuthService;
import cn.finalteam.galleryfinal.GalleryFinal;
import cn.finalteam.galleryfinal.model.PhotoInfo;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by Administrator on 2017/8/7.
 * 管理员绑定标签
 */

public class ManagerBindFragment extends BaseFragment implements OnDateSetListener {

    @BindView(R.id.ll_back_pro)
    ImageView mLLBackPro;
    @BindView(R.id.switchMultiButton)
    SwitchMultiButton mSwitchMultiButton;
    @BindView(R.id.et_select_service_type)
    EditText mEtSelectServiceType;
    @BindView(R.id.iv_scan)
    ImageView mIvScan;
    @BindView(R.id.et_label_bind)
    EditText mEtLabelBind;
    @BindView(R.id.et_phone_num)
    EditText mEtPhoneNum;
    @BindView(R.id.et_select_organization)
    EditText mEtSelectOrganization;
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
    @BindView(R.id.et_car_type)
    EditText mEtCarType;
    @BindView(R.id.et_car_num)
    EditText mEtCarNum;
    @BindView(R.id.et_select_buy_time)
    EditText mEtSelectBuyTime;
    @BindView(R.id.et_car_buy_price)
    EditText mEtCarBuyPrice;
    @BindView(R.id.plate_iv)
    ImageView mPlateIv;
    @BindView(R.id.vehicle_front_iv)
    ImageView mVehicleFrontIv;
    @BindView(R.id.center_pic_ll)
    LinearLayout mCenterPicLl;
    @BindView(R.id.vehicle_side_one_iv)
    ImageView mVehicleSideOneIv;
    @BindView(R.id.vehicle_side_two_iv)
    ImageView mVehicleSideTwoIv;
    @BindView(R.id.vehicle_side_ll)
    LinearLayout mVehicleSideLl;
    @BindView(R.id.tv_bind)
    TextView mTvBind;
    @BindView(R.id.layout_top)
    RelativeLayout mLayoutTop;
    @BindView(R.id.ll_car_info)
    LinearLayout mLlCarInfo;
    @BindView(R.id.uname_et_person)
    EditText mUnameEtPerson;
    @BindView(R.id.check_box_man_person)
    CheckBox mCheckBoxManPerson;
    @BindView(R.id.check_box_woman_person)
    CheckBox mCheckBoxWomanPerson;
    @BindView(R.id.ubirthday_et_person)
    EditText mUbirthdayEtPerson;
    @BindView(R.id.et_select_user_type_person)
    EditText mEtSelectUserTypePerson;
    @BindView(R.id.et_user_idcard_person)
    EditText mEtUserIdcardPerson;
    @BindView(R.id.plate_iv_person)
    ImageView mPlateIvPerson;
    @BindView(R.id.ll_person_info)
    LinearLayout mLlPersonInfo;
    @BindView(R.id.tv_car_title)
    TextView mTvCarTitle;
    @BindView(R.id.tv_name_title)
    TextView mTvNameTitle;
    @BindView(R.id.plate_iv_tiyan)
    ImageView mPlateIvTiyan;
    @BindView(R.id.ll_tiyan_info)
    LinearLayout mLlTiyanInfo;
    @BindView(R.id.uname_et_tiyan)
    EditText mUnameEtTiyan;
    private TimePickerDialog mDialogAll;
    long tenYears = 10L * 365 * 1000 * 60 * 60 * 24L;
    private TimePickerDialog.Builder timePickerBuilder;
    private int timeDialogFlag = 0;

    //绑定车还是人的类型
    private int bindFlag = 1;
    private final int TYPE_BIND_CAR = 1;
    private final int TYPE_BIND_PEOPLE = 2;
    private User mUserLoginInfo;
    private PersonTypeResponse mPersonTypeResponse;
    private SelectServiceResponse mSelectServiceResponse;
    private PoliceStation mPoliceStation;

    //拍照相册类型
    private final int REQUEST_CODE_CAMERA = 1000;
    private final int REQUEST_CODE_GALLERY = 1001;

    //OCR
    private Handler handler = new Handler();
    private static ProgressDialog progressDialog;
    private AuthService.authBinder authBinder;
    //    private int returnAuthority = -1;
    private String sn;
    private String devcode = Devcode.devcode;// 项目授权开发码
    private int btnFlag = 0;//1第一张 2第二张 3第三张 4第四张
    private boolean isMutil = false;
    private boolean isAddToList = false;

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
                .setCyclic(false)
                .setMinMillseconds(System.currentTimeMillis() - 3 * tenYears)
                .setMaxMillseconds(System.currentTimeMillis())
                .setThemeColor(getResources().getColor(R.color.timepicker_dialog_bg))
                .setType(Type.YEAR_MONTH_DAY)
                .setWheelItemTextNormalColor(getResources().getColor(R.color.timetimepicker_default_text_color))
                .setWheelItemTextSelectorColor(getResources().getColor(R.color.timepicker_toolbar_bg))
                .setWheelItemTextSize(12);

        mDialogAll = timePickerBuilder.build();

    }

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

                int returnAuthority = authBinder.getIDCardAuth(apm);
                if (returnAuthority != 0) { //激活失败。清楚已存储的序列号

                    switch (returnAuthority) {
                        case -10007:
                            XToastUtils.showShortToast("序列号错误");
                            break;

                        case -10008:
                            XToastUtils.showShortToast("序列号已被使用");
                            break;

                        case -10012:
                            break;

                        default:
                            XToastUtils.showShortToast("returnAuthority:" + returnAuthority);
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
                            Intent intent = new Intent(_mActivity, CameraIDCardAct.class);
                            intent.putExtra("nMainId", SharedPreferencesHelper.getInt(_mActivity, "nMainId", 2));
                            intent.putExtra("devcode", devcode);
                            intent.putExtra("flag", 0);
                            startActivityForResult(intent, 6611);

                        }
                    };
                    progressDialog = ProgressDialog.show(_mActivity, "", "正在跳转到识别界面…", true, false);

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
                XToastUtils.showShortToast(getString(R.string.license_verification_failed));

                activeOCR();

            } finally {
                if (authBinder != null) {
                    _mActivity.unbindService(authConn);
                }
            }
        }
    };

    private void activeOCR() {
        AlertDialog.Builder builder = new AlertDialog.Builder(_mActivity);
        builder.setTitle("权限激活");
        final EditText editText = new EditText(_mActivity);
        LinearLayout ll = new LinearLayout(_mActivity);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.leftMargin = Tools.dip2px(_mActivity, 20);
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
                    XToastUtils.showShortToast("请输入序列号");
                    return;
                }

                Intent authIntent = new Intent(_mActivity, AuthService.class);
                _mActivity.bindService(authIntent, authConn, Service.BIND_AUTO_CREATE);
                alertDialog.dismiss();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        closeProgressDialog();
    }

    public static void closeProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }

    }

    public static ManagerBindFragment getInstance() {
        ManagerBindFragment fragment = new ManagerBindFragment();
        return fragment;
    }

    @Override
    protected void initViewsAndEvents(Bundle savedInstanceState) {
        mUserLoginInfo = Utils.getUserLoginInfo();
        initTimePicker();
        setMutilSelect();
    }


    private void setMutilSelect() {
        mSwitchMultiButton.setUnselectedTextColor(R.color.cadetblue);
        mSwitchMultiButton.setText(Arrays.asList("车辆绑定", "人员绑定"));
        mSwitchMultiButton.setOnSwitchListener(new SwitchMultiButton.OnSwitchListener() {
            @Override
            public void onSwitch(int position, String tabText) {
                switch (position) {
                    case 0:
                        bindFlag = TYPE_BIND_CAR; //车辆绑定
                        break;
                    case 1:
                        bindFlag = TYPE_BIND_PEOPLE; //人员绑定
                        break;
                }
                if (mSelectServiceResponse != null && CheckUtils.equalsString(mSelectServiceResponse.getTarget_id(), AppConstants.TYPE_CAR) && bindFlag == TYPE_BIND_CAR) {//车
                    setCardTypeView();
                } else if (mSelectServiceResponse != null && CheckUtils.equalsString(mSelectServiceResponse.getTarget_id(), AppConstants.TYPE_PEPOLE) && bindFlag == TYPE_BIND_PEOPLE) {//人
                    setCardTypeView();
                } else {
                    mSelectServiceResponse = null;
                    mEtSelectServiceType.setText("");
                    changeView();
                    clearPic();
                }
            }
        });
    }

    private void clearPic() {
        carAnxinFront = null;
        carAnxinSideLeft = null;
        carAnxinSideRight = null;
        carAnxinBack = null;
        personAnxin = null;
        tiyan = null;
        mPlateIv.setImageResource(R.drawable.pic_front);
        mVehicleFrontIv.setImageResource(R.drawable.pic_back);
        mVehicleSideOneIv.setImageResource(R.drawable.pic_left);
        mVehicleSideTwoIv.setImageResource(R.drawable.pic_right);
        mPlateIvPerson.setImageResource(R.drawable.pic_front);
        mPlateIvTiyan.setImageResource(R.drawable.pic_front);
        mPhotoList.clear();
        mUnameEtTiyan.setText("");
    }

    private void changeView() {
        mLlTiyanInfo.setVisibility(View.GONE);
        if (bindFlag == TYPE_BIND_PEOPLE) { //人员绑定
            mLlCarInfo.setVisibility(View.GONE);
            mLlPersonInfo.setVisibility(View.VISIBLE);
        } else { //车辆绑定
            mLlCarInfo.setVisibility(View.VISIBLE);
            mLlPersonInfo.setVisibility(View.GONE);
        }
    }


    @Override
    protected void initToolbarHere() {
    }

    @Override
    public boolean isHideToolbarLayout() {
        return true;
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.fragment_bind_car;
    }


    @OnClick({R.id.ll_back_pro, R.id.et_select_service_type, R.id.iv_scan, R.id.et_select_organization, R.id.ocr_idcard_iv, R.id.check_box_man, R.id.check_box_woman,
            R.id.ubirthday_et, R.id.et_select_buy_time, R.id.plate_iv, R.id.vehicle_front_iv, R.id.vehicle_side_one_iv, R.id.vehicle_side_two_iv, R.id.tv_bind,
            R.id.check_box_man_person, R.id.check_box_woman_person, R.id.ubirthday_et_person, R.id.et_select_user_type_person, R.id.plate_iv_person, R.id.plate_iv_tiyan})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_back_pro://返回
                _mActivity.finish();
                break;
            case R.id.et_select_service_type://选择服务
                if (mPoliceStation == null) {
                    XToastUtils.showShortToast("请先选择组织机构");
                } else {
                    getserviceType();
                }
                break;
            case R.id.iv_scan://标签扫描
                startActivityForResult(new Intent(_mActivity, BarCodeScanAct.class), AppConstants.SCAN_LABEL_REQUEST_CODE);
                break;
            case R.id.et_select_organization://组织机构
                if (mSelectServiceResponse != null) {//服务类型不为空,置空
                    mSelectServiceResponse = null;
                    mEtSelectServiceType.setText("");
                }
                getOrg();
                break;
            case R.id.ocr_idcard_iv://身份证识别
                if (!Utils.isFastClick()) {
                    return;
                }
                Intent authIntent = new Intent(_mActivity, AuthService.class);
                _mActivity.bindService(authIntent, authConn, Service.BIND_AUTO_CREATE);
                break;
            case R.id.check_box_man://男
                mCheckBoxMan.setChecked(true);
                mCheckBoxWoman.setChecked(false);
                setCheckBox(mCheckBoxMan, mCheckBoxWoman);
                break;
            case R.id.check_box_woman://女
                mCheckBoxMan.setChecked(false);
                mCheckBoxWoman.setChecked(true);
                setCheckBox(mCheckBoxWoman, mCheckBoxMan);
                break;
            case R.id.ubirthday_et://出生日期
                timePickerBuilder.setMinMillseconds(System.currentTimeMillis() - 3 * tenYears);
                timePickerBuilder.setMaxMillseconds(System.currentTimeMillis());
                if (!mDialogAll.isAdded()) { //判断是否已经添加，避免因重复添加导致异常
                    mDialogAll.show(getFragmentManager(), "all");
                }
                timeDialogFlag = 1;
                break;
            case R.id.et_select_buy_time://购买时间
                timePickerBuilder.setMinMillseconds(System.currentTimeMillis() - 3 * tenYears);
                timePickerBuilder.setMaxMillseconds(System.currentTimeMillis());
                if (!mDialogAll.isAdded()) { //判断是否已经添加，避免因重复添加导致异常
                    mDialogAll.show(getFragmentManager(), "all");
                }
                timeDialogFlag = 2;
                break;


            //人员
            case R.id.check_box_man_person://男
                mCheckBoxManPerson.setChecked(true);
                mCheckBoxWomanPerson.setChecked(false);
                setCheckBox(mCheckBoxManPerson, mCheckBoxWomanPerson);
                break;
            case R.id.check_box_woman_person://女
                mCheckBoxManPerson.setChecked(false);
                mCheckBoxWomanPerson.setChecked(true);
                setCheckBox(mCheckBoxWomanPerson, mCheckBoxManPerson);
                break;
            case R.id.ubirthday_et_person://生日
                timePickerBuilder.setMinMillseconds(System.currentTimeMillis() - 3 * tenYears);
                timePickerBuilder.setMaxMillseconds(System.currentTimeMillis());
                if (!mDialogAll.isAdded()) { //判断是否已经添加，避免因重复添加导致异常
                    mDialogAll.show(getFragmentManager(), "all");
                }
                timeDialogFlag = 3;
                break;
            case R.id.et_select_user_type_person://人员类型
                getPersonType();
                break;
            //车安心卡4张图片
            case R.id.plate_iv:
                if (mSelectServiceResponse == null) {
                    XToastUtils.showShortToast("请先选择服务类型");
                    return;
                }
                btnFlag = 1;
                if (carAnxinFront == null) {
                    isAddToList = true;
                    showOpen(4 - mPhotoList.size());
                } else {
                    isAddToList = false;
                    showOpen(0);
                }
                break;
            case R.id.vehicle_front_iv:
                if (mSelectServiceResponse == null) {
                    XToastUtils.showShortToast("请先选择服务类型");
                    return;
                }
                btnFlag = 2;
                if (carAnxinSideLeft == null) {
                    isAddToList = true;
                    showOpen(4 - mPhotoList.size());
                } else {
                    isAddToList = false;
                    showOpen(0);
                }
                break;
            case R.id.vehicle_side_one_iv:
                if (mSelectServiceResponse == null) {
                    XToastUtils.showShortToast("请先选择服务类型");
                    return;
                }
                btnFlag = 3;
                if (carAnxinSideRight == null) {
                    isAddToList = true;
                    showOpen(4 - mPhotoList.size());
                } else {
                    isAddToList = false;
                    showOpen(0);
                }
                break;
            case R.id.vehicle_side_two_iv:
                if (mSelectServiceResponse == null) {
                    XToastUtils.showShortToast("请先选择服务类型");
                    return;
                }
                btnFlag = 4;
                if (carAnxinBack == null) {
                    isAddToList = true;
                    showOpen(4 - mPhotoList.size());
                } else {
                    isAddToList = false;
                    showOpen(0);
                }
                break;
            //人安心卡一张图片
            case R.id.plate_iv_person:
                if (mSelectServiceResponse == null) {
                    XToastUtils.showShortToast("请先选择服务类型");
                    return;
                }
                showOpen(0);
                break;
            //体验卡一张图片
            case R.id.plate_iv_tiyan:
                if (mSelectServiceResponse == null) {
                    XToastUtils.showShortToast("请先选择服务类型");
                    return;
                }
                showOpen(0);
                break;
            case R.id.tv_bind://绑定
                if (!Utils.isFastClick()) {
                    return;
                }
                sureBind();

                break;

        }
    }

    //确定绑定 6种情况
    private void sureBind() {
        String orgnization = mEtSelectOrganization.getText().toString().trim();//组织机构
        String phoneNum = mEtPhoneNum.getText().toString().trim();//电话号码
        String labelNum = mEtLabelBind.getText().toString().trim();//标签号
        if (CheckUtils.isEmpty(orgnization)) {
            XToastUtils.showShortToast("请选择组织机构");
        } else if (CheckUtils.isEmpty(phoneNum)) {
            XToastUtils.showShortToast("请输入手机号码");
        } else if (!CheckUtils.isMobilePhone(phoneNum)) {
            XToastUtils.showShortToast("手机号码格式不正确");
        } else if (mSelectServiceResponse == null) {
            XToastUtils.showShortToast("请选择服务类型");
        } else if (CheckUtils.isEmpty(labelNum)) {
            XToastUtils.showShortToast("请输入标签号");
        } else if (bindFlag == TYPE_BIND_CAR) {//车
            carInputSet(orgnization, labelNum);
        } else if (bindFlag == TYPE_BIND_PEOPLE) {//人
            personInputSet(orgnization, labelNum);
        }

    }


    private void personInputSet(String orgnization, String labelNum) {
        if (CheckUtils.equalsString(mSelectServiceResponse.getCard_id(), AppConstants.TYPE_CARD_TIYAN)) {//体验卡
            String carNameTiyan = mUnameEtTiyan.getText().toString().trim();//人的体验卡名字
            if (CheckUtils.isEmpty(carNameTiyan)) {
                XToastUtils.showShortToast("请输入姓名");
            } else if (CheckUtils.isEmpty(tiyan)) {
                XToastUtils.showShortToast("请上传照片");
            } else {//人的体验卡绑定
                checkTag(orgnization, labelNum);
            }
        } else {
            //被监护人
            String namePerson = mUnameEtPerson.getText().toString().trim();//姓名
            String birthdayPerson = mUbirthdayEtPerson.getText().toString().trim();//生日
            String userType = mEtSelectUserTypePerson.getText().toString().trim();//人物类型
            if (CheckUtils.isEmpty(namePerson)) {
                XToastUtils.showShortToast("请输入姓名");
            } else if (!mCheckBoxWomanPerson.isChecked() && !mCheckBoxManPerson.isChecked()) {
                XToastUtils.showShortToast("请选择性别");
            } else if (CheckUtils.isEmpty(birthdayPerson)) {
                XToastUtils.showShortToast("请选择出生日期");
            } else if (CheckUtils.isEmpty(userType)) {
                XToastUtils.showShortToast("请选择人物类型");
            } else if (CheckUtils.isEmpty(personAnxin)) {
                XToastUtils.showShortToast("请上传照片");
            } else {
                checkTag(orgnization, labelNum);
            }

        }
    }

    private void carInputSet(String orgnization, String labelNum) {
        if (CheckUtils.equalsString(mSelectServiceResponse.getCard_id(), AppConstants.TYPE_CARD_TIYAN)) {//体验卡
            String carNameTiyan = mUnameEtTiyan.getText().toString().trim();//车的体验卡名字
            if (CheckUtils.isEmpty(carNameTiyan)) {
                XToastUtils.showShortToast("请输入车辆名称");
            } else if (CheckUtils.isEmpty(tiyan)) {
                XToastUtils.showShortToast("请上传照片");
            } else {//车的体验卡绑定
                checkTag(orgnization, labelNum);
            }
        } else {
            //车主信息
            String idcard = mUcardidEt.getText().toString().trim();//车主身份证
            String name = mUnameEt.getText().toString().trim();//车主姓名
            String birthday = mUbirthdayEt.getText().toString().trim();//车主生日
            String userFirstaddress = mUresidenceEt.getText().toString().trim();//车主户籍地址

            //车辆信息
            String carType = mEtCarType.getText().toString().trim();//品牌型号
            String carNum = mEtCarNum.getText().toString().trim();//车牌号
            String buyTime = mEtSelectBuyTime.getText().toString().trim();//购买时间
            String buyPrice = mEtCarBuyPrice.getText().toString().trim();//购买价格

            String idCArdNum = "";
            try {
                idCArdNum = CheckUtils.IDCardValidate(idcard);
            } catch (ParseException e) {
                e.printStackTrace();
//                XToastUtils.showLongToast("身份证无效");
            }
            if (CheckUtils.isEmpty(idcard)) {
                XToastUtils.showLongToast("请输入身份证号码");
            } else if (!CheckUtils.equalsString(idCArdNum, "")) {
                XToastUtils.showLongToast("身份证无效");
            } else if (CheckUtils.isEmpty(name)) {
                XToastUtils.showLongToast("请输入姓名");
            } else if (!mCheckBoxWoman.isChecked() && !mCheckBoxMan.isChecked()) {
                XToastUtils.showLongToast("请选择性别");
            } else if (CheckUtils.isEmpty(birthday)) {
                XToastUtils.showLongToast("请选择出生日期");
            } else if (CheckUtils.isEmpty(userFirstaddress)) {
                XToastUtils.showLongToast("请选择户籍地址");
            } else if (CheckUtils.isEmpty(carType)) {
                XToastUtils.showShortToast("请输入品牌型号");
            } else if (CheckUtils.isEmpty(carNum)) {
                XToastUtils.showShortToast("请输入车牌号");
            } else if (CheckUtils.isEmpty(buyTime)) {
                XToastUtils.showShortToast("请选择购买时间");
            } else if (CheckUtils.isEmpty(buyPrice)) {
                XToastUtils.showShortToast("请输入购买价格");
            } else if (CheckUtils.isEmpty(carAnxinFront)) {
                XToastUtils.showShortToast("请上传车辆照片");
            } else if (CheckUtils.isEmpty(carAnxinSideLeft)) {
                XToastUtils.showShortToast("请上传车辆照片");
            } else if (CheckUtils.isEmpty(carAnxinSideRight)) {
                XToastUtils.showShortToast("请上传车辆照片");
            } else if (CheckUtils.isEmpty(carAnxinBack)) {
                XToastUtils.showShortToast("请上传车辆照片");
            } else {
                checkTag(orgnization, labelNum);
            }
        }
    }

    //校验标签号
    private void checkTag(String orgnization, final String labelNum) {
        Map<String, String> params = new HashMap<>();
        String path;
        params.put("lno", labelNum);
        if (CheckUtils.equalsString(mSelectServiceResponse.getCard_id(), AppConstants.TYPE_CARD_DINZHI)) {//定制卡标签校验
            params.put("orgid", mUserLoginInfo.getOrgid());
            path = Path.BIND_LABEL_DINZHI;
        } else {
            path = Path.CHECK_TAG_PATH;
        }

        OkGo.post(path).tag(this).params(params).execute(new JsonCallback<Object>(_mActivity) {

            @Override
            public void onSuccess(Object o, Call call, Response response) {
                bindLabel(orgnization, labelNum);
            }
        });

    }

    //绑定标签
    private void bindLabel(String orgnization, String labelNum) {
        String phoneNum = mEtPhoneNum.getText().toString().trim();//电话号码
        Map<String, String> params = new HashMap<>();
        String path = "";
        params.put("", orgnization);
        params.put("", phoneNum);
        params.put("", labelNum);
        params.put("", mSelectServiceResponse.getCard_id());
        if (bindFlag == TYPE_BIND_CAR) {//车
            if (CheckUtils.equalsString(mSelectServiceResponse.getCard_id(), AppConstants.TYPE_CARD_TIYAN)) {//车的体验卡
                String carNameTiyan = mUnameEtTiyan.getText().toString().trim();//车的体验卡名字
                String tiyan64 = Utils.base64Pic(tiyan);
                params.put("", carNameTiyan);
                params.put("", tiyan64);
            } else {//车的安心卡
                //车主信息
                String idcard = mUcardidEt.getText().toString().trim();//车主身份证
                String name = mUnameEt.getText().toString().trim();//车主姓名
                String birthday = mUbirthdayEt.getText().toString().trim();//车主生日
                String userFirstaddress = mUresidenceEt.getText().toString().trim();//车主户籍地址
                String sex = "";//性别
                if (mCheckBoxMan.isChecked()) {//0男 1女
                    sex = "0";
                } else if (mCheckBoxWoman.isChecked()) {
                    sex = "1";
                }

                //车辆信息
                String carType = mEtCarType.getText().toString().trim();//品牌型号
                String carNum = mEtCarNum.getText().toString().trim();//车牌号
                String buyTime = mEtSelectBuyTime.getText().toString().trim();//购买时间
                String buyPrice = mEtCarBuyPrice.getText().toString().trim();//购买价格

                //4张图片
                String front64 = Utils.base64Pic(carAnxinFront);
                String left64 = Utils.base64Pic(carAnxinSideLeft);
                String right64 = Utils.base64Pic(carAnxinSideRight);
                String back64 = Utils.base64Pic(carAnxinBack);

                params.put("", idcard);
                params.put("", name);
                params.put("", sex);
                params.put("", birthday);
                params.put("", userFirstaddress);
                params.put("", carType);
                params.put("", carNum);
                params.put("", buyTime);
                params.put("", buyPrice);
                params.put("", front64 + "," + left64 + "," + right64 + "," + back64);
            }
        } else if (bindFlag == TYPE_BIND_PEOPLE) {//人
            if (CheckUtils.equalsString(mSelectServiceResponse.getCard_id(), AppConstants.TYPE_CARD_TIYAN)) {//人的体验卡
                String personNameTiyan = mUnameEtTiyan.getText().toString().trim();//人的体验卡名字
                String tiyan64 = Utils.base64Pic(tiyan);
                params.put("", personNameTiyan);
                params.put("", tiyan64);
            } else {//人的安心卡
                String namePerson = mUnameEtPerson.getText().toString().trim();//姓名
                String birthdayPerson = mUbirthdayEtPerson.getText().toString().trim();//生日
                String userType = mEtSelectUserTypePerson.getText().toString().trim();//人物类型
                String sex = "";//性别
                if (mCheckBoxMan.isChecked()) {//0男 1女
                    sex = "0";
                } else if (mCheckBoxWoman.isChecked()) {
                    sex = "1";
                }
                String person64 = Utils.base64Pic(personAnxin);

                params.put("", namePerson);
                params.put("", sex);
                params.put("", birthdayPerson);
                params.put("", userType);
                params.put("", person64);
            }
        }

        OkGo.post(Path.CAR_LABEL_BIND).tag(this).params(params).execute(new DialogCallback<Object>(_mActivity, "标签绑定中...") {

            @Override
            public void onSuccess(Object o, Call call, Response response) {
                XToastUtils.showShortToast("标签绑定成功");
            }
        });

    }


    //获取组织机构
    private void getOrg() {
        Map<String, String> params = new HashMap<>();
        params.put("orgids", mUserLoginInfo.getOrgids());
        params.put("orgid", mUserLoginInfo.getOrgid());
        OkGo.post(Path.GET_POLICE_STATIONS).tag(this).params(params).execute(new DialogCallback<List<PoliceStation>>(_mActivity, "获取组织机构中...") {
            @Override
            public void onSuccess(List<PoliceStation> policeStations, Call call, Response response) {
                initOrgDialog(policeStations);
            }

        });
    }

    private void initOrgDialog(List<PoliceStation> policeStations) {
        View view1 = View.inflate(_mActivity, R.layout.label_list, null);
        final AlertDialog alertDialog = Utils.showCornerDialog(_mActivity, view1, 270, 270);
        RecyclerView listView = (RecyclerView) alertDialog.findViewById(R.id.listView);
        TextView tvTitleCarLabel = (TextView) alertDialog.findViewById(R.id.tv_title_car_label);
        tvTitleCarLabel.setText("请选择组织机构");
        SelectOrgAdapter adapter = new SelectOrgAdapter(_mActivity, policeStations);
        listView.setAdapter(adapter);
        listView.addItemDecoration(new ListLineDecoration());
        listView.setLayoutManager(new LinearLayoutManager(_mActivity));
        adapter.setOnItemClickListener(new QuickRcvAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(BH viewHolder, int position) {
                alertDialog.dismiss();
                mPoliceStation = policeStations.get(position);
                mEtSelectOrganization.setText(mPoliceStation.getOrgname());
            }
        });
    }

    //获取服务类型
    private void getserviceType() {
        Map<String, String> params = new HashMap<>();
        params.put("orgid", mPoliceStation.getOrgid());
        if (bindFlag == TYPE_BIND_CAR) {
            params.put("target_id", AppConstants.TYPE_CAR);//查车传38,查人传39
        } else {
            params.put("target_id", AppConstants.TYPE_PEPOLE);//查车传38,查人传39
        }

        OkGo.post(Path.GET_CARD_INFO).tag(this).params(params).execute(new DialogCallback<List<SelectServiceResponse>>(_mActivity, "获取服务类型中...") {

            @Override
            public void onSuccess(List<SelectServiceResponse> selectServiceResponses, Call call, Response response) {
                initServiceTypeDialog(selectServiceResponses);
            }
        });
    }

    //弹服务类型选择框
    private void initServiceTypeDialog(List<SelectServiceResponse> selectServiceResponses) {
        View view1 = View.inflate(_mActivity, R.layout.label_list, null);
        final AlertDialog alertDialog = Utils.showCornerDialog(_mActivity, view1, 270, 270);
        RecyclerView listView = (RecyclerView) alertDialog.findViewById(R.id.listView);
        TextView tvTitleCarLabel = (TextView) alertDialog.findViewById(R.id.tv_title_car_label);
        tvTitleCarLabel.setText("请选择服务类型");
        SelectServiceTypeAdapter adapter = new SelectServiceTypeAdapter(_mActivity, selectServiceResponses);
        listView.setAdapter(adapter);
        listView.addItemDecoration(new ListLineDecoration());
        listView.setLayoutManager(new LinearLayoutManager(_mActivity));
        adapter.setOnItemClickListener(new QuickRcvAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(BH viewHolder, int position) {
                alertDialog.dismiss();
                mSelectServiceResponse = selectServiceResponses.get(position);
                mEtSelectServiceType.setText(mSelectServiceResponse.getCard_name());
                setCardTypeView();
                clearPic();
            }
        });
    }

    private void setCardTypeView() {
        if (CheckUtils.equalsString(mSelectServiceResponse.getCard_id(), AppConstants.TYPE_CARD_TIYAN)) {//体验卡
            mLlCarInfo.setVisibility(View.GONE);
            mLlPersonInfo.setVisibility(View.GONE);
            mLlTiyanInfo.setVisibility(View.VISIBLE);
            if (bindFlag == 1) {//车的体验卡
                mTvNameTitle.setText("车辆名称");
            } else {//人的体验卡
                mTvNameTitle.setText("姓名");
            }
        } else if (bindFlag == 2) {//人
            mLlCarInfo.setVisibility(View.GONE);
            mLlPersonInfo.setVisibility(View.VISIBLE);
            mLlTiyanInfo.setVisibility(View.GONE);
        } else {
            mLlCarInfo.setVisibility(View.VISIBLE);
            mLlPersonInfo.setVisibility(View.GONE);
            mLlTiyanInfo.setVisibility(View.GONE);
        }
    }


    //获取人员类型
    private void getPersonType() {
        OkGo.post(Path.GET_PERSON_TYPE).tag(this).execute(new DialogCallback<List<PersonTypeResponse>>(_mActivity, "获取人员类型中...") {
            @Override
            public void onSuccess(List<PersonTypeResponse> personTypeResponses, Call call, Response response) {
                initDialog(personTypeResponses);
            }
        });
    }

    //弹人员类型选择框
    private void initDialog(List<PersonTypeResponse> personTypeResponses) {
        View view1 = View.inflate(_mActivity, R.layout.label_list, null);
        final AlertDialog alertDialog = Utils.showCornerDialog(_mActivity, view1, 270, 270);
        RecyclerView listView = (RecyclerView) alertDialog.findViewById(R.id.listView);
        TextView tvTitleCarLabel = (TextView) alertDialog.findViewById(R.id.tv_title_car_label);
        tvTitleCarLabel.setText("请选择人员类型");
        PersonTypeAdapter adapter = new PersonTypeAdapter(_mActivity, personTypeResponses);
        listView.setAdapter(adapter);
        listView.addItemDecoration(new ListLineDecoration());
        listView.setLayoutManager(new LinearLayoutManager(_mActivity));
        adapter.setOnItemClickListener(new QuickRcvAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(BH viewHolder, int position) {
                alertDialog.dismiss();
                mPersonTypeResponse = personTypeResponses.get(position);
                mEtSelectUserTypePerson.setText(mPersonTypeResponse.getDname());
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //标签扫描
        if (requestCode == AppConstants.SCAN_LABEL_REQUEST_CODE && resultCode == AppConstants.SCAN_LABEL_RESULT_CODE) {
            String scanResult = data.getStringExtra("scanResult");
            mEtLabelBind.setText(scanResult);
        }

        //身份证
        if (resultCode == 6612 && requestCode == 6611) {
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

    }

    private void setCheckBox(CheckBox checkBoxMan, CheckBox checkBoxWoman) {
        checkBoxMan.setChecked(true);
        checkBoxMan.setButtonDrawable(R.drawable.select_point);
        checkBoxMan.setTextColor(getResources().getColor(R.color.color_3270ed));
        checkBoxWoman.setChecked(false);
        checkBoxWoman.setTextColor(getResources().getColor(R.color.color_cccccc));
        checkBoxWoman.setButtonDrawable(R.drawable.no_select_point);
    }

    @Override
    public void onDateSet(TimePickerDialog timePickerView, long millseconds) {
        switch (timeDialogFlag) {
            case 1:
                mUbirthdayEt.setText(getDateToString(millseconds));
                break;
            case 2:
                mEtSelectBuyTime.setText(getDateToString(millseconds));
                break;
            case 3:
                mUbirthdayEtPerson.setText(getDateToString(millseconds));
                break;
        }
    }

    public String getDateToString(long time) {

        Date d = new Date(time);
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        return sf.format(d);
    }

    //弹相册拍照框
    private void showOpen(int mutil) {
        ActionSheet.createBuilder(_mActivity, getFragmentManager())
                .setCancelButtonTitle("取消(Cancel)")
                .setOtherButtonTitles("打开相册(Open Gallery)", "拍照(Camera)")
                .setCancelableOnTouchOutside(true)
                .setListener(new ActionSheet.ActionSheetListener() {
                    @Override
                    public void onDismiss(ActionSheet actionSheet, boolean isCancel) {

                    }

                    @Override
                    public void onOtherButtonClick(ActionSheet actionSheet, int index) {
                        switch (index) {
                            case 0:
                                if (mutil != 0) {
                                    isMutil = true;
                                    GalleryFinal.openGalleryMuti(REQUEST_CODE_GALLERY, mutil, mOnHanlderResultCallback);
                                } else {
                                    isMutil = false;
                                    GalleryFinal.openGallerySingle(REQUEST_CODE_GALLERY, mOnHanlderResultCallback);
                                }
                                break;
                            case 1://拍照
                                isMutil = false;
                                GalleryFinal.openCamera(REQUEST_CODE_CAMERA, mOnHanlderResultCallback);
                                break;
                        }
                    }
                }).show();

    }


    private ArrayList<PhotoInfo> mPhotoList = new ArrayList<>();
    private String carAnxinFront, carAnxinSideLeft, carAnxinSideRight, carAnxinBack, personAnxin, tiyan;
    private GalleryFinal.OnHanlderResultCallback mOnHanlderResultCallback = new GalleryFinal.OnHanlderResultCallback() {
        @Override
        public void onHanlderSuccess(int reqeustCode, List<PhotoInfo> resultList) {
            if (resultList != null) {
                if (bindFlag == 1) {//车
                    if (CheckUtils.equalsString(mSelectServiceResponse.getCard_id(), AppConstants.TYPE_CARD_TIYAN)) {//体验卡
                        tiyan = resultList.get(0).getPhotoPath();
                        ImageUtil.loadSelectPic(_mActivity, tiyan, mPlateIvTiyan);
                    } else if (isMutil) {
                        setCarAnxinPicMutil(resultList);
                    } else {
                        setCarAnxinPicSingle(resultList);
                    }
                } else if (bindFlag == 2) {//人
                    if (CheckUtils.equalsString(mSelectServiceResponse.getCard_id(), AppConstants.TYPE_CARD_TIYAN)) {//体验卡
                        tiyan = resultList.get(0).getPhotoPath();
                        ImageUtil.loadSelectPic(_mActivity, tiyan, mPlateIvTiyan);
                    } else {
                        personAnxin = resultList.get(0).getPhotoPath();
                        ImageUtil.loadSelectPic(_mActivity, personAnxin, mPlateIvPerson);
                    }
                }
            }
        }

        @Override
        public void onHanlderFailure(int requestCode, String errorMsg) {
            XToastUtils.showShortToast(errorMsg);
        }
    };

    //设置车安心卡单选/拍照结果
    private void setCarAnxinPicSingle(List<PhotoInfo> resultList) {
        if (isAddToList) {
            mPhotoList.addAll(resultList);
        }
        switch (btnFlag) {
            case 1://点击第一张
                carAnxinFront = resultList.get(0).getPhotoPath();
                ImageUtil.loadSelectPic(_mActivity, carAnxinFront, mPlateIv);
                break;
            case 2:
                carAnxinSideLeft = resultList.get(0).getPhotoPath();
                ImageUtil.loadSelectPic(_mActivity, carAnxinSideLeft, mVehicleFrontIv);
                break;
            case 3:
                carAnxinSideRight = resultList.get(0).getPhotoPath();
                ImageUtil.loadSelectPic(_mActivity, carAnxinSideRight, mVehicleSideOneIv);
                break;
            case 4:
                carAnxinBack = resultList.get(0).getPhotoPath();
                ImageUtil.loadSelectPic(_mActivity, carAnxinBack, mVehicleSideTwoIv);
                break;
        }

    }

    private void setCarAnxinPicMutil(List<PhotoInfo> resultList) {
        mPhotoList.addAll(resultList);
        switch (mPhotoList.size()) {
            case 1:
                carAnxinFront = mPhotoList.get(0).getPhotoPath();
                ImageUtil.loadSelectPic(_mActivity, carAnxinFront, mPlateIv);
                break;
            case 2:
                carAnxinFront = mPhotoList.get(0).getPhotoPath();
                carAnxinSideLeft = mPhotoList.get(1).getPhotoPath();
                ImageUtil.loadSelectPic(_mActivity, carAnxinFront, mPlateIv);
                ImageUtil.loadSelectPic(_mActivity, carAnxinSideLeft, mVehicleFrontIv);
                break;
            case 3:
                carAnxinFront = mPhotoList.get(0).getPhotoPath();
                carAnxinSideLeft = mPhotoList.get(1).getPhotoPath();
                carAnxinSideRight = mPhotoList.get(2).getPhotoPath();
                ImageUtil.loadSelectPic(_mActivity, carAnxinFront, mPlateIv);
                ImageUtil.loadSelectPic(_mActivity, carAnxinSideLeft, mVehicleFrontIv);
                ImageUtil.loadSelectPic(_mActivity, carAnxinSideRight, mVehicleSideOneIv);
                break;
            case 4:
                carAnxinFront = mPhotoList.get(0).getPhotoPath();
                carAnxinSideLeft = mPhotoList.get(1).getPhotoPath();
                carAnxinSideRight = mPhotoList.get(2).getPhotoPath();
                carAnxinBack = mPhotoList.get(3).getPhotoPath();
                ImageUtil.loadSelectPic(_mActivity, carAnxinFront, mPlateIv);
                ImageUtil.loadSelectPic(_mActivity, carAnxinSideLeft, mVehicleFrontIv);
                ImageUtil.loadSelectPic(_mActivity, carAnxinSideRight, mVehicleSideOneIv);
                ImageUtil.loadSelectPic(_mActivity, carAnxinBack, mVehicleSideTwoIv);
                break;
        }
    }

}
