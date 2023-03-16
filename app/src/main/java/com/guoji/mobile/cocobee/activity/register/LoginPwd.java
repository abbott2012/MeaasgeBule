package com.guoji.mobile.cocobee.activity.register;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bql.utils.CheckUtils;
import com.guoji.mobile.cocobee.R;
import com.guoji.mobile.cocobee.activity.LoginActivity;
import com.guoji.mobile.cocobee.activity.base.BaseActivity;
import com.guoji.mobile.cocobee.callback.DialogCallback;
import com.guoji.mobile.cocobee.common.AppManager;
import com.guoji.mobile.cocobee.common.Path;
import com.guoji.mobile.cocobee.utils.Utils;
import com.guoji.mobile.cocobee.utils.XToastUtils;
import com.lzy.okgo.OkGo;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by Administrator on 2017/4/18.
 */

public class LoginPwd extends BaseActivity {
    @BindView(R.id.ll_back_pro)
    ImageView mLlBackPro;
    @BindView(R.id.tv_car_num)
    EditText mRegPwd;
    @BindView(R.id.delete_all)
    ImageView mDeleteAll;
    @BindView(R.id.vi_num)
    EditText mAgainPwd;
    @BindView(R.id.delete_check)
    ImageView mDeleteCheck;
    @BindView(R.id.reg_next)
    TextView mRegNext;
    @BindView(R.id.two_pwd_not_same)
    TextView mTwoPwdNotSame;
    private String mPhone;

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_login_pwd;
    }

    @Override
    protected void initViewsAndEvents(Bundle savedInstanceState) {
        Intent intent = getIntent();
        mPhone = intent.getStringExtra("phone");
        mRegNext.setEnabled(false);
        addEditTextChang();
    }

    private void addEditTextChang() {
        mRegPwd.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence text, int start, int before,
                                      int count) {
                mDeleteAll.setVisibility(View.GONE);
            }

            @Override
            public void beforeTextChanged(CharSequence text, int start,
                                          int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                mDeleteAll.setVisibility(View.VISIBLE);
                String regPwd = mRegPwd.getText().toString().trim();
                String againPwd = mAgainPwd.getText().toString().trim();
                if (CheckUtils.isAllPassword(regPwd)) {//是密码
                    if (TextUtils.equals(regPwd, againPwd)) {//两次输入相同
                        mDeleteAll.setBackgroundResource(R.drawable.confirm_correct);
                        mDeleteCheck.setBackgroundResource(R.drawable.confirm_correct);
                        mRegNext.setEnabled(true);
                        mTwoPwdNotSame.setVisibility(View.GONE);
                    } else if (!TextUtils.isEmpty(againPwd)) {
                        mDeleteAll.setBackgroundResource(R.drawable.confirm_correct);
                        mDeleteCheck.setBackgroundResource(R.drawable.confirm_error);
                        mRegNext.setEnabled(false);
                        mTwoPwdNotSame.setVisibility(View.VISIBLE);
                    } else {
                        mDeleteAll.setBackgroundResource(R.drawable.confirm_correct);
                        mDeleteCheck.setBackgroundResource(R.drawable.confirm_error);
                        mRegNext.setEnabled(false);
                        mTwoPwdNotSame.setVisibility(View.GONE);
                    }
                } else {//不是密码
                    mDeleteAll.setBackgroundResource(R.drawable.confirm_error);
                    mDeleteCheck.setBackgroundResource(R.drawable.confirm_error);
                    mRegNext.setEnabled(false);
                }
            }
        });

        mAgainPwd.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence text, int start, int before,
                                      int count) {
                mDeleteCheck.setVisibility(View.GONE);
            }

            @Override
            public void beforeTextChanged(CharSequence text, int start,
                                          int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                mDeleteCheck.setVisibility(View.VISIBLE);
                String regPwd = mRegPwd.getText().toString().trim();
                String againPwd = mAgainPwd.getText().toString().trim();
                if (TextUtils.isEmpty(againPwd)) {
                    mTwoPwdNotSame.setVisibility(View.GONE);
                    mDeleteCheck.setVisibility(View.GONE);
                    mRegNext.setEnabled(false);
                } else if (TextUtils.equals(regPwd, againPwd)) {//两次输入相同
                    mDeleteCheck.setBackgroundResource(R.drawable.confirm_correct);
                    mTwoPwdNotSame.setVisibility(View.GONE);
                    mRegNext.setEnabled(true);
                    mTwoPwdNotSame.setVisibility(View.GONE);
                } else {
                    mTwoPwdNotSame.setVisibility(View.VISIBLE);
                    mDeleteCheck.setBackgroundResource(R.drawable.confirm_error);
                    mRegNext.setEnabled(false);
                }
                if (!CheckUtils.isAllPassword(regPwd) || !CheckUtils.isAllPassword(againPwd)) {
                    mRegNext.setEnabled(false);
                }
            }
        });
    }

    @OnClick({R.id.ll_back_pro, R.id.reg_next})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_back_pro:
                finish();
                break;
            case R.id.reg_next:
//                String regPwd = mRegPwd.getText().toString().trim();
                gotoReg();
//                Intent intent = new Intent(this, InputBaseInfo.class);
//                intent.putExtra("regPwd", regPwd);
//                intent.putExtra("phone", mPhone);
//                startActivity(intent);
                break;
        }
    }

    private void gotoReg() {
        String regPwd = mRegPwd.getText().toString().trim();
        Map<String, String> params = new HashMap<String, String>();
        params.put("mobile",mPhone);
        params.put("password",regPwd);
        OkGo.post(Path.USER_REGISTER).tag(this).params(params).execute(new DialogCallback<Object>(this, "注册中...") {
            @Override
            public void onSuccess(Object o, Call call, Response response) {
                XToastUtils.showShortToast("注册成功");
                Utils.startActivity(LoginPwd.this,LoginActivity.class);
                AppManager.getAppManager().finishAllActivity();
            }
        });
    }

}
