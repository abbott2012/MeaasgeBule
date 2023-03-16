package com.guoji.mobile.cocobee.activity.register;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bql.utils.CheckUtils;
import com.guoji.mobile.cocobee.R;
import com.guoji.mobile.cocobee.activity.LoginActivity;
import com.guoji.mobile.cocobee.activity.base.BaseActivity;
import com.guoji.mobile.cocobee.common.JsonResult;
import com.guoji.mobile.cocobee.common.Path;
import com.guoji.mobile.cocobee.callback.StringDialogCallback;
import com.guoji.mobile.cocobee.utils.XToastUtils;
import com.guoji.mobile.cocobee.view.DelayButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;

/**
 * 注册Activity
 * Created by Administrator on 2017/4/13.
 */

public class RegActivity extends BaseActivity {
    @BindView(R.id.ll_back_pro)
    ImageView mBackPro;
    @BindView(R.id.reg_phone_tv)
    EditText mRegPhoneTv;
    @BindView(R.id.delete_all)
    ImageView mDeleteAll;
    @BindView(R.id.reg_check_et)
    EditText mRegCheckEt;
    @BindView(R.id.delete_check)
    ImageView mDeleteCheck;
    @BindView(R.id.btn_delay)
    DelayButton mBtnDelay;
    @BindView(R.id.check_box)
    CheckBox mCheckBox;
    @BindView(R.id.user_xieyi)
    TextView mUserXieyi;
    @BindView(R.id.reg_next)
    TextView mRegNext;
    @BindView(R.id.has_reg)
    TextView mHasReg;

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_reg;
    }

    @Override
    protected void initViewsAndEvents(Bundle savedInstanceState) {
        addEditTextChang();

    }

    private void addEditTextChang() {
        mRegPhoneTv.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence text, int start, int before,
                                      int count) {
                if (!TextUtils.isEmpty(text)) {
                    mDeleteAll.setVisibility(View.VISIBLE);
                } else {
                    mDeleteAll.setVisibility(View.GONE);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence text, int start,
                                          int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        mRegCheckEt.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence text, int start, int before,
                                      int count) {
                if (!TextUtils.isEmpty(text)) {
                    mDeleteCheck.setVisibility(View.VISIBLE);
                } else {
                    mDeleteCheck.setVisibility(View.GONE);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence text, int start,
                                          int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }


    @OnClick({R.id.delete_all, R.id.delete_check, R.id.btn_delay, R.id.user_xieyi, R.id.reg_next, R.id.has_reg, R.id.ll_back_pro})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_back_pro:
                finish();
            case R.id.delete_all:
                mRegPhoneTv.setText("");
                break;
            case R.id.delete_check:
                mRegCheckEt.setText("");
                break;
            case R.id.btn_delay:
                //验证手机号码
                verifyMobilePhone();
                break;
            case R.id.user_xieyi://用户协议
                startActivity(new Intent(this, UserManagerActivity.class));
                break;
            case R.id.reg_next://下一步
                String checkNum = mRegCheckEt.getText().toString().trim();
                String phone = mRegPhoneTv.getText().toString().trim();
                if (CheckUtils.isEmpty(phone)) {
                    XToastUtils.showLongToast("请输入手机号");
                } else if (!CheckUtils.isMobilePhone(phone)) {
                    XToastUtils.showLongToast("手机号码格式不正确");
                } else if (CheckUtils.isEmpty(checkNum)) {
                    XToastUtils.showLongToast("请输入验证码");
                } else if (!mCheckBox.isChecked()) {
                    XToastUtils.showLongToast("请选择阅读并同意电动车管理用户协议");
                } else {//请求接口,提交参数下一步
                    getServerVerifyCose(phone, checkNum);
                }
                break;
            case R.id.has_reg://已有账号,跳转登录
                startActivity(new Intent(this, LoginActivity.class));
                break;
        }
    }

    //验证验证码是否正确
    private void getServerVerifyCose(final String phone, String checkNum) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("mobile", phone);//电话
        params.put("messagecode", checkNum);//验证码

        OkGo.post(Path.FIND_PWD_CHECK_PATH).tag(this).params(params).execute(new StringDialogCallback(RegActivity.this, "正在验证验证码...") {
            @Override
            public void onSuccess(String result, Call call, Response response) {

                if (!TextUtils.isEmpty(result)) {
                    JsonResult jsonResult = new Gson().fromJson(result, new TypeToken<JsonResult>() {
                    }.getType());
                    if (jsonResult != null && jsonResult.isFlag() && jsonResult.getStatusCode() == 200) {//验证码正确
                        Intent intent = new Intent(RegActivity.this, LoginPwd.class);
                        intent.putExtra("phone", phone);
                        startActivity(intent);
                    } else {//手机号码未注册,发送验证码
                        XToastUtils.showLongToast("验证码错误");
                    }
                } else {
                    XToastUtils.showLongToast("验证码后台校验失败");
                }
            }

            @Override
            public void onError(Call call, Response response, Exception e) {
                super.onError(call, response, e);
//                XToastUtils.showLongToast("网络不给力,检查您的网络状态");
                XToastUtils.showLongToast(e.getMessage());
            }


        });

    }

    private void verifyMobilePhone() {
        String phone = mRegPhoneTv.getText().toString().trim();
        if (CheckUtils.isEmpty(phone)) {
            XToastUtils.showShortToast("请输入手机号码");
        } else {
            if (CheckUtils.isMobilePhone(phone)) {//是手机号码,验证手机号码是否注册
                mBtnDelay.setEnabled(false);
                checkPhone(phone);
            } else {
                XToastUtils.showShortToast("手机号码格式不正确");
            }
        }
    }

    private void checkPhone(final String phone) {

        //先验证手机号码是否存在,不存在发送验证码
        Map<String, String> params = new HashMap<String, String>();
        params.put("mobile", phone);

        OkGo.post(Path.CHECK_PHONE_EXIST_PATH).tag(this).params(params).execute(new StringDialogCallback(RegActivity.this, "正在验证手机...") {
            @Override
            public void onSuccess(String result, Call call, Response response) {

                if (!TextUtils.isEmpty(result)) {
                    JsonResult jsonResult = new Gson().fromJson(result, new TypeToken<JsonResult>() {
                    }.getType());
                    if (jsonResult != null && jsonResult.isFlag() && jsonResult.getStatusCode() == 200) {//手机号码存在
                        XToastUtils.showLongToast("该手机号码已经注册");
                        mBtnDelay.setEnabled(true);
                    } else {//手机号码未注册,发送验证码
                        sendVerifyCode(phone);
                        mBtnDelay.setEnabled(false);
                    }
                } else {
                    XToastUtils.showLongToast("手机号码后台校验失败");
                    mBtnDelay.setEnabled(true);
                }
            }

            @Override
            public void onError(Call call, Response response, Exception e) {
                super.onError(call, response, e);
                XToastUtils.showLongToast("网络不给力,检查您的网络状态");
                mBtnDelay.setEnabled(true);
            }

        });

    }

    public void sendVerifyCode(String phone) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("mobile", phone);
        OkGo.post(Path.GET_CHECK_CODE_PATH).tag(this).params(params).execute(new StringDialogCallback(RegActivity.this, "正在获取验证码...") {
            @Override
            public void onSuccess(String s, Call call, Response response) {
                if (!TextUtils.isEmpty(s)) {
                    JsonResult jsonResult = new Gson().fromJson(s, new TypeToken<JsonResult>() {
                    }.getType());
                    if (jsonResult != null && jsonResult.isFlag() && jsonResult.getStatusCode() == 200) {
                        XToastUtils.showLongToast("验证码发送成功");
                        mBtnDelay.startCountDown();
                        mBtnDelay.setEnabled(false);
                    } else {
                        XToastUtils.showLongToast("验证码发送失败");
                        mBtnDelay.setEnabled(true);
                    }
                } else {
                    XToastUtils.showLongToast("验证码发送失败");
                    mBtnDelay.setEnabled(true);
                }
            }

            @Override
            public void onError(Call call, Response response, Exception e) {
                super.onError(call, response, e);
                XToastUtils.showLongToast("获取验证码失败");
                mBtnDelay.setEnabled(true);
            }

        });
    }

}
