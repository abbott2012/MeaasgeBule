package com.guoji.mobile.cocobee.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.guoji.mobile.cocobee.R;
import com.guoji.mobile.cocobee.activity.base.BaseToolbarActivity;
import com.guoji.mobile.cocobee.callback.StringDialogCallback;
import com.guoji.mobile.cocobee.common.JsonResult;
import com.guoji.mobile.cocobee.common.Path;
import com.guoji.mobile.cocobee.utils.Tools;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Response;

/**
 * 忘记密码页面
 * Created by _H_JY on 2017/3/14.
 */
public class ForgetPwdActivity extends BaseToolbarActivity implements View.OnClickListener {

    private EditText phone_et;
    private EditText check_code_et;
    private Button get_checkcode_btn;
    private TextView sure_btn;

    private TimeCount timeCount;

    private int type;


    private boolean isPhoneExist = false;

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_forget_pwd;
    }

    @Override
    protected void initViewsAndEvents(Bundle savedInstanceState) {
        initToolbar("忘记密码");
        timeCount = new TimeCount(60000, 1000);

        initView();
    }

    private void initView() {
        phone_et = (EditText) findViewById(R.id.phone_et);
        check_code_et = (EditText) findViewById(R.id.check_code_et);
        get_checkcode_btn = (Button) findViewById(R.id.get_checkcode_btn);
        sure_btn = (TextView) findViewById(R.id.sure_btn);


        sure_btn.setOnClickListener(this);
        get_checkcode_btn.setOnClickListener(this);
    }


    @Override
    protected void onDestroy() {


        stopCountTimer();
        OkGo.getInstance().cancelTag(this);

        super.onDestroy();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.get_checkcode_btn:
                type = 1; //表示获取验证码
                getCheckCode();
                break;

            case R.id.sure_btn:
                type = 2; //表示验证是否可以重置密码
                sure();
                break;
        }
    }


    private void getCheckCode() {

        final String phone = phone_et.getText().toString().trim();
        if (TextUtils.isEmpty(phone)) {
            Toast.makeText(ForgetPwdActivity.this, "请填写手机号码", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!Tools.isMobileNO(phone)) {
            Toast.makeText(ForgetPwdActivity.this, "手机号码格式不正确", Toast.LENGTH_SHORT).show();
            return;
        }


        get_checkcode_btn.setEnabled(false);

        //先验证手机号码是否存在
        Map<String, String> params = new HashMap<String, String>();
        params.put("mobile", phone);

        OkGo.post(Path.CHECK_PHONE_EXIST_PATH).tag(this).params(params).execute(new StringDialogCallback(ForgetPwdActivity.this, "正在验证手机...") {
            @Override
            public void onSuccess(String result, Call call, Response response) {
                get_checkcode_btn.setEnabled(true);
                sure_btn.setEnabled(true);

                if (!TextUtils.isEmpty(result)) {
                    JsonResult jsonResult = new Gson().fromJson(result, new TypeToken<JsonResult>() {
                    }.getType());
                    if (jsonResult != null && jsonResult.isFlag() && jsonResult.getStatusCode() == 200) {

                        isPhoneExist = true;

                        Map<String, String> params = new HashMap<String, String>();
                        params.put("mobile", phone);
                        OkGo.post(Path.GET_CHECK_CODE_PATH).tag(this).params(params).execute(new StringDialogCallback(ForgetPwdActivity.this, "正在获取验证码...") {
                            @Override
                            public void onSuccess(String s, Call call, Response response) {
                                if (!TextUtils.isEmpty(s)) {
                                    JsonResult jsonResult = new Gson().fromJson(s, new TypeToken<JsonResult>() {
                                    }.getType());
                                    if (jsonResult != null && jsonResult.isFlag() && jsonResult.getStatusCode() == 200) {
                                        timeCount.start();
                                        Toast.makeText(ForgetPwdActivity.this, "获取验证码成功", Toast.LENGTH_SHORT).show();
                                    } else if (jsonResult != null && jsonResult.getStatusCode() == 400) {
                                        Toast.makeText(ForgetPwdActivity.this, "获取验证码失败", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(ForgetPwdActivity.this, "获取验证码失败", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(ForgetPwdActivity.this, "获取验证码失败", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onError(Call call, Response response, Exception e) {
                                super.onError(call, response, e);
                                Toast.makeText(ForgetPwdActivity.this, "获取验证码失败", Toast.LENGTH_SHORT).show();
                            }

                        });


                    } else if (jsonResult != null && jsonResult.getStatusCode() == 400) {
                        isPhoneExist = false;
                        Toast.makeText(ForgetPwdActivity.this, "手机号码未注册", Toast.LENGTH_SHORT).show();
                    } else {
                        isPhoneExist = false;
                        Toast.makeText(ForgetPwdActivity.this, "手机号码后台校验失败", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    isPhoneExist = false;
                    Toast.makeText(ForgetPwdActivity.this, "手机号码后台校验失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Call call, Response response, Exception e) {
                super.onError(call, response, e);
                get_checkcode_btn.setEnabled(true);
                sure_btn.setEnabled(true);
                isPhoneExist = false;
                Toast.makeText(ForgetPwdActivity.this, "手机号码后台校验失败", Toast.LENGTH_SHORT).show();
            }

        });


    }


    private void sure() {

        final String phone = phone_et.getText().toString().trim();
        if (TextUtils.isEmpty(phone)) {
            Toast.makeText(ForgetPwdActivity.this, "请填写手机号码", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!Tools.isMobileNO(phone)) {
            Toast.makeText(ForgetPwdActivity.this, "手机号码格式不正确", Toast.LENGTH_SHORT).show();
            return;
        }


        final String messageCode = check_code_et.getText().toString().trim();
        if (TextUtils.isEmpty(messageCode)) {
            Toast.makeText(ForgetPwdActivity.this, "请填写验证码", Toast.LENGTH_SHORT).show();
            return;
        }


        if (!isPhoneExist) {
            Toast.makeText(ForgetPwdActivity.this, "手机号码还未通过验证", Toast.LENGTH_SHORT).show();
            return;
        }


        sure_btn.setEnabled(false);


        Map<String, String> params = new HashMap<String, String>();
        params.put("mobile", phone);
        params.put("messagecode", messageCode);
        OkGo.post(Path.FIND_PWD_CHECK_PATH).tag(this).params(params).execute(new StringDialogCallback(ForgetPwdActivity.this, "正在验证...") {
            @Override
            public void onSuccess(String result, Call call, Response response) {
                get_checkcode_btn.setEnabled(true);
                sure_btn.setEnabled(true);

                if (!TextUtils.isEmpty(result)) {
                    JsonResult jsonResult = new Gson().fromJson(result, new TypeToken<JsonResult>() {
                    }.getType());
                    if (jsonResult != null && jsonResult.isFlag() && jsonResult.getStatusCode() == 200) {
//                        Intent i = new Intent(ForgetPwdActivity.this, ChangePwdAct.class);
                        Intent i = new Intent(ForgetPwdActivity.this, ChangePwdActivity.class);
//                        i.putExtra("fromPage", 1);//1表示从手机验证页面跳转过去
                        i.putExtra("phone", phone);//把验证通过的电话号码也传过去
                        startActivity(i);
                    } else if (jsonResult != null && jsonResult.getStatusCode() == 400) { //验证码填写错误
                        Toast.makeText(ForgetPwdActivity.this, "验证码填写错误", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ForgetPwdActivity.this, "验证失败", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ForgetPwdActivity.this, "验证失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Call call, Response response, Exception e) {
                super.onError(call, response, e);
                get_checkcode_btn.setEnabled(true);
                sure_btn.setEnabled(true);

                Toast.makeText(ForgetPwdActivity.this, "验证失败", Toast.LENGTH_SHORT).show();
            }

        });


    }


    class TimeCount extends CountDownTimer {

        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            get_checkcode_btn.setClickable(false);
            get_checkcode_btn.setText("剩余" + millisUntilFinished / 1000 + "秒");
        }

        @Override
        public void onFinish() {
            get_checkcode_btn.setText("获取验证码");
            get_checkcode_btn.setClickable(true);
            // get_checkcode_btn.setBackgroundColor(Color.parseColor("#4EB84A"));

        }
    }


    private void stopCountTimer() {
        if (timeCount != null) {
            timeCount.cancel();
            timeCount = null;
        }
    }

}
