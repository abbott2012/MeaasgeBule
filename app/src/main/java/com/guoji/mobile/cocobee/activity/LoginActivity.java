package com.guoji.mobile.cocobee.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.guoji.mobile.cocobee.R;
import com.guoji.mobile.cocobee.activity.base.BaseActivity;
import com.guoji.mobile.cocobee.activity.register.RegActivity;
import com.guoji.mobile.cocobee.common.JsonResult;
import com.guoji.mobile.cocobee.common.Path;
import com.guoji.mobile.cocobee.callback.StringDialogCallback;
import com.guoji.mobile.cocobee.model.User;
import com.guoji.mobile.cocobee.utils.Utils;
import com.guoji.mobile.cocobee.utils.XToastUtils;
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
 * 登录页面
 * Created by _H_JY on 2017/2/27.
 */

public class LoginActivity extends BaseActivity {

    @BindView(R.id.et_login_phone)
    EditText mEtLoginPhone;
    @BindView(R.id.et_login_pwd)
    EditText mEtLoginPwd;
    @BindView(R.id.iv_see_pwd)
    CheckBox mIvSeePwd;
    @BindView(R.id.login)
    TextView mLogin;
    @BindView(R.id.find_pwd_tv)
    TextView mFindPwdTv;
    @BindView(R.id.user_reg)
    TextView mUserReg;

    private Context context;

    @Override
    protected boolean isHideSmartBar() {
        return true;
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_login;
    }

    @Override
    protected void initViewsAndEvents(Bundle savedInstanceState) {
        context = this;
        String userLoginNum = Utils.getUserLoginNum();
        mEtLoginPhone.setText(userLoginNum);
        mEtLoginPhone.setSelection(userLoginNum.length());
        initListener();
    }

    private void initListener() {
        mIvSeePwd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    //选择状态 显示明文--设置为可见的密码
                    mEtLoginPwd.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                } else {
                    //默认状态显示密码--设置文本 要一起写才能起作用 InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD
                    mEtLoginPwd.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
            }
        });
    }

    @OnClick({R.id.iv_see_pwd, R.id.login, R.id.find_pwd_tv, R.id.user_reg})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.login://登录
                // 各种判断；
                final String phonenumber = mEtLoginPhone.getText().toString();
                final String password = mEtLoginPwd.getText().toString();

                if (TextUtils.isEmpty(phonenumber)) {
                    Toast.makeText(this, "请输入账号", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(this, "请输入密码", Toast.LENGTH_SHORT).show();
                    return;
                }

                Map<String, String> params = new HashMap<String, String>();
                params.put("account", phonenumber);
                params.put("password", password);
                OkGo.post(Path.LOGIIN_PATH).tag(this).params(params).execute(new StringDialogCallback(this, "登录中...") {
                    @Override
                    public void onSuccess(String result, Call call, Response response) {
                        if (!TextUtils.isEmpty(result)) {
                            JsonResult jr = new Gson().fromJson(result, new TypeToken<JsonResult>() {
                            }.getType());

                            if (jr != null && jr.getStatusCode() == 200 && jr.isFlag()) {
                                String jsonStr = jr.getResult();
                                if (!TextUtils.isEmpty(jsonStr)) {
                                    user = new Gson().fromJson(jsonStr, User.class);
                                    if (user != null) {
                                        Utils.putUserLoginInfo(user);
                                        Utils.putUserLoginNum(phonenumber);
                                        Utils.startActivity(LoginActivity.this,MainActivity1.class);
                                        finish();

                                        Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(LoginActivity.this, jr.getMessage(), Toast.LENGTH_SHORT).show();
                                    }

                                } else {
                                    Toast.makeText(LoginActivity.this, jr.getMessage(), Toast.LENGTH_SHORT).show();
                                }

                            } else if (jr != null) {
                                Toast.makeText(LoginActivity.this, jr.getMessage(), Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            Toast.makeText(LoginActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        XToastUtils.showShortToast("您的网络不给力,请检查您的网络状态!");
                    }
                });


                break;
            case R.id.find_pwd_tv://忘记密码
//                startActivity(new Intent(context, CheckPhoneAct.class));
                Utils.startActivity(LoginActivity.this, ForgetPwdActivity.class);
                break;
            case R.id.user_reg://注册
                startActivity(new Intent(context, RegActivity.class));
                break;
        }
    }

}
