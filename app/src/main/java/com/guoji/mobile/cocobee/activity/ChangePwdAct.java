package com.guoji.mobile.cocobee.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.guoji.mobile.cocobee.R;
import com.guoji.mobile.cocobee.common.AppManager;
import com.guoji.mobile.cocobee.common.JsonResult;
import com.guoji.mobile.cocobee.common.Path;
import com.guoji.mobile.cocobee.callback.StringDialogCallback;
import com.guoji.mobile.cocobee.utils.Utils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Response;

/**
 * 修改密码页面
 * Created by _H_JY on 2017/2/21.
 */
public class ChangePwdAct extends BaseAct implements View.OnClickListener {

    private Context context;
    private ImageButton back_ib;
    private EditText old_pwd_et, new_pwd_et, new_pwd_sure_et;
    private Button sure_btn;
    private TextView old_pwd_tip_tv;


    private int type;
    private String phone;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_change_pwd);

        context = this;

        type = getIntent().getIntExtra("fromPage", -1);

        initView();
    }


    private void initView() {
        back_ib = (ImageButton) findViewById(R.id.back_ib);
        old_pwd_et = (EditText) findViewById(R.id.old_pwd_et);
        new_pwd_et = (EditText) findViewById(R.id.new_pwd_et);
        new_pwd_sure_et = (EditText) findViewById(R.id.sure_new_pwd_et);
        sure_btn = (Button) findViewById(R.id.sure_btn);
        old_pwd_tip_tv = (TextView) findViewById(R.id.old_pwd_tip_tv);

        if (type == 1) {
            old_pwd_tip_tv.setVisibility(View.GONE);
            old_pwd_et.setVisibility(View.GONE);
            phone = getIntent().getStringExtra("phone");
        } else {
            old_pwd_tip_tv.setVisibility(View.VISIBLE);
            new_pwd_et.setVisibility(View.VISIBLE);
        }


        back_ib.setOnClickListener(this);
        sure_btn.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_ib:
                finish();
                break;

            case R.id.sure_btn:

                final String oldPwdStr = old_pwd_et.getText().toString().trim();
                if (type != 1) { //不是验证找回密码方式
                    if (user == null) {
                        Toast.makeText(context, "请先登录", Toast.LENGTH_SHORT).show();
                        return;
                    }


                    if (TextUtils.isEmpty(oldPwdStr)) {
                        Toast.makeText(context, "请输入原密码", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }


                final String newPwdStr = new_pwd_et.getText().toString().trim();
                if (TextUtils.isEmpty(newPwdStr)) {
                    Toast.makeText(context, "请输入新密码", Toast.LENGTH_SHORT).show();
                    return;
                }


                String newPwdSureStr = new_pwd_sure_et.getText().toString().trim();
                if (TextUtils.isEmpty(newPwdSureStr)) {
                    Toast.makeText(context, "请输入确认密码", Toast.LENGTH_SHORT).show();
                    return;
                }


                if (!TextUtils.equals(newPwdStr, newPwdSureStr)) {
                    Toast.makeText(context, "新密码两次输入不一致", Toast.LENGTH_SHORT).show();
                    return;
                }


                sure_btn.setEnabled(false);


                Map<String, String> params = new HashMap<String, String>();
                String path = "";
                if (type == 2) {
                    path = Path.CHANGE_PWD_PATH;
                    params.put("account", user.getIdcard());
                    params.put("password", newPwdStr);
                    params.put("oldpassword", oldPwdStr);
                } else {
                    params.put("mobile", phone);
                    params.put("password", newPwdStr);
                    path = Path.RESET_PWD_PATH;
                }

                OkGo.post(path).tag(this).params(params).execute(new StringDialogCallback(ChangePwdAct.this, "密码修改中...") {
                    @Override
                    public void onSuccess(String result, Call call, Response response) {
                        sure_btn.setEnabled(true); //按钮复原
                        if (!TextUtils.isEmpty(result)) {
                            JsonResult jr = new Gson().fromJson(result, new TypeToken<JsonResult>() {
                            }.getType());
                            if (jr != null && jr.isFlag() && jr.getStatusCode() == 200) {
                                Toast.makeText(context, "修改成功，请重新登录", Toast.LENGTH_SHORT).show();
                                user = null;
                                Utils.putUserLoginInfo(user);
                                AppManager.getAppManager().finishAllActivity();
                                startActivity(new Intent(context, LoginActivity.class));
                                finish();
                            } else {
                                if (jr.getStatusCode() == 300 && type == 2) { //旧密码错误
                                    Toast.makeText(context, "原密码错误，无法修改", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(context, "密码修改失败", Toast.LENGTH_SHORT).show();
                                }
                            }
                        } else {
                            Toast.makeText(context, "密码修改失败", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        sure_btn.setEnabled(true); //按钮复原
                        Toast.makeText(context, "密码修改失败", Toast.LENGTH_SHORT).show();
                    }

                });


                break;
        }
    }


    @Override
    protected void onDestroy() {
        OkGo.getInstance().cancelTag(this);
        super.onDestroy();
    }
}
