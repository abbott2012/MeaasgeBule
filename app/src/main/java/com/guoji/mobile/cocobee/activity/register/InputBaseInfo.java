/*
package com.guoji.mobile.cocobee.activity.register;

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
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bql.utils.CheckUtils;
import com.bql.utils.StringUtils;
import com.guoji.mobile.cocobee.R;
import com.guoji.mobile.cocobee.activity.CameraIDCardAct;
import com.guoji.mobile.cocobee.activity.LoginActivity;
import com.guoji.mobile.cocobee.callback.StringDialogCallback;
import com.guoji.mobile.cocobee.common.AppManager;
import com.guoji.mobile.cocobee.common.JsonResult;
import com.guoji.mobile.cocobee.common.Path;
import com.guoji.mobile.cocobee.response.GetUserOrg;
import com.guoji.mobile.cocobee.response.UserRegInfo;
import com.guoji.mobile.cocobee.utils.Devcode;
import com.guoji.mobile.cocobee.utils.SharedPreferencesHelper;
import com.guoji.mobile.cocobee.utils.Tools;
import com.guoji.mobile.cocobee.utils.Utils;
import com.guoji.mobile.cocobee.utils.XToastUtils;
import com.guoji.mobile.cocobee.view.MySpinner;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jzxiang.pickerview.TimePickerDialog;
import com.jzxiang.pickerview.data.Type;
import com.jzxiang.pickerview.listener.OnDateSetListener;
import com.lzy.okgo.OkGo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import chinaSafe.idcard.android.AuthParameterMessage;
import chinaSafe.idcard.android.AuthService;
import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.Call;
import okhttp3.Response;

*/
/**
 * 填写基本信息
 * Created by Administrator on 2017/4/14.
 *//*


public class InputBaseInfo extends AppCompatActivity implements OnDateSetListener {
    @BindView(R.id.uname_tip_tv)
    TextView mUnameTipTv;
    @BindView(R.id.uname_et)
    EditText mUnameEt;
    @BindView(R.id.ucardid_et)
    EditText mUcardidEt;
    @BindView(R.id.ocr_idcard_tv)
    TextView mOcrIdcardTv;
    @BindView(R.id.ubirthday_et)
    EditText mUbirthdayEt;
    @BindView(R.id.uresidence_et)
    EditText mUresidenceEt;
    @BindView(R.id.reg_next)
    TextView mRegNext;
    @BindView(R.id.check_box_man)
    CheckBox mCheckBoxMan;
    @BindView(R.id.check_box_woman)
    CheckBox mCheckBoxWoman;
    @BindView(R.id.user_type_et)
    Spinner mUserTypeEt;
    @BindView(R.id.spinner_sheng)
    MySpinner mSpinnerSheng;
    @BindView(R.id.spinner_shi)
    MySpinner mSpinnerShi;
    @BindView(R.id.spinner_xian)
    MySpinner mSpinnerXian;
    @BindView(R.id.spinner_qu)
    MySpinner mSpinnerQu;
    @BindView(R.id.ll_shen)
    LinearLayout mLlShen;
    @BindView(R.id.ll_shi)
    LinearLayout mLlShi;
    @BindView(R.id.ll_xian)
    LinearLayout mLlXian;
    @BindView(R.id.ll_qu)
    LinearLayout mLlQu;
    @BindView(R.id.ll_back_pro)
    ImageView mLlBackPro;

    private Context context;
    private int ocr_idcard_type = 0;

    private Handler handler = new Handler();
    private static ProgressDialog progressDialog;
    private AuthService.authBinder authBinder;
    private int ReturnAuthority = -1;
    private String sn;
    private String devcode = Devcode.devcode;// 项目授权开发码


    private ArrayAdapter<String> arr_adapter;
    private ArrayAdapter<String> arr_adapter_shen;
    private ArrayAdapter<String> arr_adapter_shi;
    private ArrayAdapter<String> arr_adapter_xian;
    private ArrayAdapter<String> arr_adapter_qu;

    private String mSpinner_sheng;
    private String mUser_type;
    private String mSpinner_qu;
    private String mSpinner_xian;
    private String mSpinner_shi;

    private ArrayList<GetUserOrg> shengList = new ArrayList<>();
    private ArrayList<GetUserOrg> shiList = new ArrayList<>();
    private ArrayList<GetUserOrg> xianList = new ArrayList<>();
    private ArrayList<GetUserOrg> quList = new ArrayList<>();

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

                    */
/*跳转到识别界面*//*

                    */
/**
                     * 由于在相机界面释放相机等资源会耗费很多时间， 为了优化用户体验，需要在调用相机的那个界面的oncreate()方法中
                     * 调用AppManager.getAppManager().finishAllActivity();
                     * 如果调用和识别的显示界面是同一个界面只需调用一次即可， 如果是不同界面，需要在显示界面的oncreate()方法中
                     * 调用AppManager.getAppManager().finishAllActivity();即可，
                     * 否则会造成相机资源的内存溢出。
                     *//*


                    final Runnable mTasks = new Runnable() {
                        public void run() {
                            Intent intent = new Intent(InputBaseInfo.this, CameraIDCardAct.class);
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
                    progressDialog = ProgressDialog.show(InputBaseInfo.this, "",
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
    private List<GetUserOrg> mUserOrgs;


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

                Intent authIntent = new Intent(InputBaseInfo.this, AuthService.class);
                bindService(authIntent, authConn, Service.BIND_AUTO_CREATE);
                alertDialog.dismiss();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_input_base_info);
        context = this;
        AppManager.getAppManager().addActivity(this);
        ButterKnife.bind(this);
        //给Spinner添加监听
        setListener();
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
//                .setHourText("时")
//                .setMinuteText("分")
                .setCyclic(false)
                .setMinMillseconds(System.currentTimeMillis() - 15 * tenYears) //当前时间减去一百五十年
                .setMaxMillseconds(System.currentTimeMillis() + 10 * tenYears)
                .setCurrentMillseconds(System.currentTimeMillis())
                .setThemeColor(getResources().getColor(R.color.timepicker_dialog_bg))
                .setType(Type.YEAR_MONTH_DAY)
                .setWheelItemTextNormalColor(getResources().getColor(R.color.timetimepicker_default_text_color))
                .setWheelItemTextSelectorColor(getResources().getColor(R.color.timepicker_toolbar_bg))
                .setWheelItemTextSize(12)
                .build();

    }

    private void initSpinner(ArrayAdapter<String> arr_adapter, Spinner spinner, String[] stringArray) {
        //适配器
        arr_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, stringArray);
        //设置样式
        arr_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //加载适配器
        spinner.setAdapter(arr_adapter);
    }

    private void setListener() {
        mSpinnerSheng.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mSpinner_sheng = (String) mSpinnerSheng.getItemAtPosition(i);
                mSpinnerShi.setAdapter(null);
                mSpinner_shi = "";
                mSpinnerXian.setAdapter(null);
                mSpinner_xian = "";
                mSpinnerQu.setAdapter(null);
                mSpinner_qu = "";
                mSpinnerQu.setVisibility(View.GONE);
                quList.clear();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        mSpinnerShi.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mSpinner_shi = (String) mSpinnerShi.getItemAtPosition(i);
                mSpinnerXian.setAdapter(null);
                mSpinner_xian = "";
                mSpinnerQu.setAdapter(null);
                mSpinner_qu = "";
                mSpinnerQu.setVisibility(View.GONE);
                quList.clear();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        mSpinnerXian.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mSpinner_xian = (String) mSpinnerXian.getItemAtPosition(i);
                mSpinnerQu.setAdapter(null);
                mSpinner_qu = "";
                mSpinnerQu.setVisibility(View.GONE);
                quList.clear();
                setzuzhi(mSpinner_xian, xianList, arr_adapter_qu, mSpinnerQu, quList, "4");
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        mSpinnerQu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mSpinner_qu = (String) mSpinnerQu.getItemAtPosition(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        mUserTypeEt.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mUser_type = (String) mUserTypeEt.getItemAtPosition(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
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

    @OnClick({R.id.ocr_idcard_tv, R.id.reg_next, R.id.check_box_man, R.id.check_box_woman, R.id.ll_shen, R.id.ll_shi, R.id.ll_xian, R.id.ll_qu, R.id.ll_back_pro, R.id.ubirthday_et})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ubirthday_et:
                if (!mDialogAll.isAdded()) { //判断是否已经添加，避免因重复添加导致异常
                    mDialogAll.show(getSupportFragmentManager(), "all");
                }
                break;
            case R.id.ll_back_pro:
                finish();
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
            case R.id.ocr_idcard_tv: //ocr识别身份证：车主身份证
                //防止一秒内点击多次
                if (!Utils.isFastClick()){
                    return;
                }
                ocr_idcard_type = 1;
                Intent authIntent = new Intent(this, AuthService.class);
                bindService(authIntent, authConn, Service.BIND_AUTO_CREATE);

                break;
            case R.id.ll_shen:
                getServerOrg("", "1", arr_adapter_shen, mSpinnerSheng, shengList);

                break;
            case R.id.ll_shi:
                setzuzhi(mSpinner_sheng, shengList, arr_adapter_shi, mSpinnerShi, shiList, "2");
                break;
            case R.id.ll_xian:
                setzuzhi(mSpinner_shi, shiList, arr_adapter_xian, mSpinnerXian, xianList, "3");
                break;
            case R.id.ll_qu:
//                setzuzhi(mSpinner_xian, xianList, arr_adapter_qu, mSpinnerQu, quList, "4");
                String[] array = new String[quList.size()];
                for (int i = 0; i < quList.size(); i++) {
                    array[i] = quList.get(i).getOrgname();
                }
                initSpinner(arr_adapter_qu, mSpinnerQu, array);
                mSpinnerQu.performClick();
                break;
            case R.id.reg_next:
                String unameEt = mUnameEt.getText().toString().trim();//姓名
                String ucardidEt = mUcardidEt.getText().toString().trim();//身份证
                String ubirthdayEt = mUbirthdayEt.getText().toString().trim();//生日
                String userLocationEt = StringUtils.append(mSpinner_sheng, mSpinner_shi);//现居地址
                String userFirstaddress = mUresidenceEt.getText().toString().trim();
                String idCArdNum = "";
                try {
                    idCArdNum = CheckUtils.IDCardValidate(ucardidEt);
                } catch (ParseException e) {
                    e.printStackTrace();
                    XToastUtils.showLongToast("身份证无效");
                }
                if (CheckUtils.isEmpty(unameEt)) {
                    XToastUtils.showLongToast("请输入姓名");
                } else if (CheckUtils.isEmpty(ucardidEt)) {
                    XToastUtils.showLongToast("请输入身份证号码");
                } else if (!CheckUtils.equalsString(idCArdNum, "")) {
                    XToastUtils.showLongToast("身份证无效");
                } else if (!mCheckBoxWoman.isChecked() && !mCheckBoxMan.isChecked()) {
                    XToastUtils.showLongToast("请选择性别");
                } else if (CheckUtils.isEmpty(ubirthdayEt)) {
                    XToastUtils.showLongToast("请选择出生日期");
                } else if (CheckUtils.isEmpty(mSpinner_sheng) || CheckUtils.isEmpty(mSpinner_shi)) {
                    XToastUtils.showLongToast("请选择组织架构");
                } else if (CheckUtils.isEmpty(userFirstaddress)) {
                    XToastUtils.showLongToast("请选择出生地");
                }else if (CheckUtils.isEmpty(mSpinner_xian)) {
                    XToastUtils.showLongToast("请选择组织架构");
                } else if (mSpinnerQu.getVisibility() == View.VISIBLE && CheckUtils.isEmpty(mSpinner_qu)){
                    XToastUtils.showLongToast("请选择组织架构");
                }else {//注册
                    //弹窗提示用户是否确认信息正确
                    register_prompt(unameEt, ucardidEt, ubirthdayEt, userFirstaddress);
                }
                break;
        }
    }

    //注册
    private void gotoReg(String unameEt, String ucardidEt, String ubirthdayEt, String userFirstaddress) {
        String orgid3 = null;
        String orgid4 = null;
        if (!CheckUtils.isEmpty(mSpinner_qu)) {
            for (GetUserOrg g : quList) {
                if (CheckUtils.equalsString(mSpinner_qu, g.getOrgname())) {
                    orgid4 = g.getOrgid();
                }
            }
            startReg(unameEt, ucardidEt, ubirthdayEt, StringUtils.append(mSpinner_sheng, mSpinner_shi), orgid4, mUser_type, userFirstaddress);
        } else if (!CheckUtils.isEmpty(mSpinner_xian)) {
            for (GetUserOrg g : xianList) {
                if (CheckUtils.equalsString(mSpinner_xian, g.getOrgname())) {
                    orgid3 = g.getOrgid();
                }
            }
            startReg(unameEt, ucardidEt, ubirthdayEt, StringUtils.append(mSpinner_sheng, mSpinner_shi), orgid3, mUser_type, userFirstaddress);
        } else {
            XToastUtils.showLongToast("组织架构不存在");
        }
    }

    //注册前确认提示
    private void register_prompt(final String unameEt, final String ucardidEt, final String ubirthdayEt, final String userFirstaddress) {
        SweetAlertDialog continueDialog = new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE);
        continueDialog.setTitleText("确定所填信息正确,去注册吗?");
        continueDialog.showCancelButton(true).setCancelText("否");
        continueDialog.setConfirmText("是");
        continueDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                sweetAlertDialog.dismiss();
                gotoReg(unameEt, ucardidEt, ubirthdayEt, userFirstaddress);
            }
        });

        continueDialog.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                sweetAlertDialog.dismiss();
            }
        });
        continueDialog.show();
    }

    //设置组织架构
    private void setzuzhi(String spinner_xian, ArrayList<GetUserOrg> xianList, ArrayAdapter<String> arr_adapter_qu, MySpinner spinnerQu, ArrayList<GetUserOrg> quList, String s) {
        String orgid3 = null;
        if (!CheckUtils.isEmpty(spinner_xian)) {
            for (GetUserOrg g : xianList) {
                if (CheckUtils.equalsString(spinner_xian, g.getOrgname())) {
                    orgid3 = g.getOrgid();
                }
            }
            getServerOrg(orgid3, s, arr_adapter_qu, spinnerQu, quList);
        } else {
            XToastUtils.showLongToast("请选择上一级");
        }
    }

    //注册
    //人员类别：1：车主，2：患者，3：老人，4：学生，5：易走失人员，6：警务人员，7：保安人员，8：协警人员
    private void startReg(String unameEt, String ucardidEt, String ubirthdayEt, String userLocationEt, String upolicestatuonEt, String userType, String userFirstaddress) {
        Intent intent = getIntent();
        String phone = intent.getStringExtra("phone");
        String regPwd = intent.getStringExtra("regPwd");
        Map<String, String> params = new HashMap<String, String>();
        UserRegInfo userRegInfo = new UserRegInfo();
        userRegInfo.setPname(unameEt);
        userRegInfo.setMobile(phone);
        userRegInfo.setPassword(regPwd);
        userRegInfo.setIdcard(ucardidEt);
        userRegInfo.setBirthday(ubirthdayEt);
        userRegInfo.setOrgid(upolicestatuonEt);
        userRegInfo.setAddress(userLocationEt);
        userRegInfo.setRegiaddr(userFirstaddress);
//        int userType1 = Utils.getUserType(userType);
//        userRegInfo.setPtype("" + userType1);

        if (mCheckBoxMan.isChecked()) {
            userRegInfo.setSex("0");
        } else if (mCheckBoxWoman.isChecked()) {
            userRegInfo.setSex("1");
        }
        params.put("jsonuser", new Gson().toJson(userRegInfo));
        OkGo.post(Path.USER_REGISTER).tag(this).params(params).execute(new StringDialogCallback(InputBaseInfo.this, "注册中...") {
            @Override
            public void onSuccess(String result, Call call, Response response) {

                if (!TextUtils.isEmpty(result)) {
                    JsonResult jsonResult = new Gson().fromJson(result, new TypeToken<JsonResult>() {
                    }.getType());
                    if (jsonResult != null && jsonResult.isFlag() && jsonResult.getStatusCode() == 200) {//注册成功
                        XToastUtils.showLongToast("注册成功");
                        startActivity(new Intent(InputBaseInfo.this, LoginActivity.class));
                    } else if (jsonResult != null) {//身份证已经注册
                        XToastUtils.showLongToast(jsonResult.getMessage());
                    }
                } else {
                    XToastUtils.showLongToast("注册失败");
                }
            }

            @Override
            public void onError(Call call, Response response, Exception e) {
                super.onError(call, response, e);
                XToastUtils.showLongToast("网络不给力,检查您的网络状态");
            }


        });
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

    public void getServerOrg(String fatherorgid, String orglevel, final ArrayAdapter<String> arr_adapter, final Spinner spinner, final ArrayList<GetUserOrg> list) {
        Map<String, String> params = new HashMap<String, String>();
        if (CheckUtils.equalsString(orglevel, "1")) {
            params.put("orglevel", orglevel);
        } else {
            params.put("fatherorgid", fatherorgid);//父组织架构orgid
            params.put("orglevel", orglevel);//组织机构等级（1：第一级 2：第二级 3：第三级 4：第四级）
        }
        OkGo.post(Path.GET_USER_ORG).tag(this).params(params).execute(new StringDialogCallback(InputBaseInfo.this, "查询中...") {
            @Override
            public void onSuccess(String result, Call call, Response response) {
                Gson gson = new Gson();
                if (!TextUtils.isEmpty(result)) {
                    JsonResult jsonResult = gson.fromJson(result, new TypeToken<JsonResult>() {
                    }.getType());
                    if (jsonResult != null && jsonResult.isFlag() && jsonResult.getStatusCode() == 200) {//查询成功
                        String results = jsonResult.getResult();
                        mUserOrgs = gson.fromJson(results, new TypeToken<List<GetUserOrg>>() {
                        }.getType());
                        if (CheckUtils.equalsString(orglevel, "4")) {
                            if (mUserOrgs != null && mUserOrgs.size() > 0) {//第四级有数据
                                mSpinnerQu.setVisibility(View.VISIBLE);
                                quList.addAll(mUserOrgs);
                            } else {//第四级无数据
                                mSpinnerQu.setVisibility(View.GONE);
                            }
                        } else if (mUserOrgs != null && mUserOrgs.size() > 0) {
                            list.addAll(mUserOrgs);
                            String[] array = new String[mUserOrgs.size()];
                            for (int i = 0; i < mUserOrgs.size(); i++) {
                                array[i] = mUserOrgs.get(i).getOrgname();
                            }
                            initSpinner(arr_adapter, spinner, array);
                            spinner.performClick();
                        } else {
                            XToastUtils.showShortToast("暂无相关数据");
                        }
                    } else if (jsonResult != null && jsonResult.getStatusCode() == 400) {//查询失败
                        XToastUtils.showLongToast(jsonResult.getMessage());
                    }
                } else {
                    XToastUtils.showLongToast("查询失败");
                }
            }

            @Override
            public void onError(Call call, Response response, Exception e) {
                super.onError(call, response, e);
                XToastUtils.showLongToast("网络不给力,检查您的网络状态");
            }


        });

    }

    @Override
    public void onDateSet(TimePickerDialog timePickerView, long millseconds) {
        mUbirthdayEt.setText(getDateToString(millseconds));
    }

    public String getDateToString(long time) {

        Date d = new Date(time);
        SimpleDateFormat sf = null;
//        （购买时间）或者其他
        sf = new SimpleDateFormat("yyyy-MM-dd");
        return sf.format(d);
    }
}
*/
