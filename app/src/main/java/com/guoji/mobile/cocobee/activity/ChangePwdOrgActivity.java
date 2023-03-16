package com.guoji.mobile.cocobee.activity;

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
import com.guoji.mobile.cocobee.activity.base.BaseToolbarActivity;
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
 * 修改密码页面
 * Created by _H_JY on 2017/2/21.
 */
public class ChangePwdOrgActivity extends BaseToolbarActivity {
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
    @BindView(R.id.tv_org_pwd)
    EditText tvOrgPwd;
    @BindView(R.id.org_delete_all)
    ImageView orgDeleteAll;

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_org_pwd;
    }

    @Override
    protected void initViewsAndEvents(Bundle savedInstanceState) {
        initToolbar("修改密码");
        mRegNext.setEnabled(false);
        addEditTextChang();
    }

    private void addEditTextChang() {
        tvOrgPwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                orgDeleteAll.setVisibility(View.GONE);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                orgDeleteAll.setVisibility(View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String regPwd = mRegPwd.getText().toString().trim();
                String againPwd = mAgainPwd.getText().toString().trim();
                String oldPwd = tvOrgPwd.getText().toString().trim();
                if (CheckUtils.isAllPassword(oldPwd) && CheckUtils.isAllPassword(regPwd) && TextUtils.equals(regPwd, againPwd)) {//两次输入相同
                    mDeleteAll.setBackgroundResource(R.drawable.confirm_correct);
                    mDeleteCheck.setBackgroundResource(R.drawable.confirm_correct);
                    mRegNext.setEnabled(true);
                    mTwoPwdNotSame.setVisibility(View.GONE);
                } else {
                    mRegNext.setEnabled(false);
                }
            }
        });
        mRegPwd.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence text, int start, int before,
                                      int count) {
                mDeleteAll.setVisibility(View.GONE);
            }

            @Override
            public void beforeTextChanged(CharSequence text, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                mDeleteAll.setVisibility(View.VISIBLE);
                String regPwd = mRegPwd.getText().toString().trim();
                String againPwd = mAgainPwd.getText().toString().trim();
                String oldPwd = tvOrgPwd.getText().toString().trim();
                if (CheckUtils.isAllPassword(oldPwd) && CheckUtils.isAllPassword(regPwd)) {//是密码
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
                } else if (CheckUtils.isAllPassword(oldPwd)) {//不是密码
                    mDeleteAll.setBackgroundResource(R.drawable.confirm_error);
                    mDeleteCheck.setBackgroundResource(R.drawable.confirm_error);
                    mRegNext.setEnabled(false);
                } else {//不是手机号
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
                String oldPwd = tvOrgPwd.getText().toString().trim();
                if (CheckUtils.isAllPassword(oldPwd) && CheckUtils.isAllPassword(regPwd) && TextUtils.equals(regPwd, againPwd)) {//两次输入相同
                    mDeleteCheck.setBackgroundResource(R.drawable.confirm_correct);
                    mTwoPwdNotSame.setVisibility(View.GONE);
                    mRegNext.setEnabled(true);
                    mTwoPwdNotSame.setVisibility(View.GONE);
                } else if (TextUtils.isEmpty(againPwd)) {
                    mTwoPwdNotSame.setVisibility(View.GONE);
                    mDeleteCheck.setVisibility(View.GONE);
                    mRegNext.setEnabled(false);
                } else if (CheckUtils.isAllPassword(oldPwd)) {
                    mTwoPwdNotSame.setVisibility(View.VISIBLE);
                    mDeleteCheck.setBackgroundResource(R.drawable.confirm_error);
                    mRegNext.setEnabled(false);
                } else {
                    mRegNext.setEnabled(false);
                }

            }
        });
    }

    @OnClick({R.id.reg_next, R.id.org_delete_all})
    public void onViewClicked(View view) {
        switch (view.getId()) {

            case R.id.reg_next:
                setPwd();
                break;
            case R.id.org_delete_all:
                tvOrgPwd.setText("");
                orgDeleteAll.setVisibility(View.GONE);
                break;
        }
    }

    private void setPwd() {
        Map<String, String> params = new HashMap<String, String>();
        String pwd = mRegPwd.getText().toString().trim();
        String oldPwd = tvOrgPwd.getText().toString().trim();
        params.put("mobile", user.getMobile());
        params.put("password", pwd);
        params.put("oldpassword", oldPwd);
        OkGo.post(Path.CHANGE_PWD_PATH).tag(this).params(params).execute(new DialogCallback<Object>(ChangePwdOrgActivity.this, "密码修改中...") {

            @Override
            public void onSuccess(Object o, Call call, Response response) {
                XToastUtils.showShortToast("密码修改成功,请重新登录");
                user = null;
                Utils.putUserLoginInfo(user);
                AppManager.getAppManager().finishAllActivity();
                Utils.startActivity(ChangePwdOrgActivity.this, LoginActivity.class);
                finish();
            }

        });

    }
}

