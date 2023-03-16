package com.guoji.mobile.cocobee.activity.me;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bql.utils.CheckUtils;
import com.bql.utils.EventManager;
import com.guoji.mobile.cocobee.R;
import com.guoji.mobile.cocobee.activity.BarCodeScanAct;
import com.guoji.mobile.cocobee.activity.base.BaseActivity;
import com.guoji.mobile.cocobee.activity.pay.BuyPoliceActivity;
import com.guoji.mobile.cocobee.callback.StringComCallback;
import com.guoji.mobile.cocobee.common.AppConstants;
import com.guoji.mobile.cocobee.common.JsonResult;
import com.guoji.mobile.cocobee.common.Path;
import com.guoji.mobile.cocobee.callback.StringDialogCallback;
import com.guoji.mobile.cocobee.model.Car;
import com.guoji.mobile.cocobee.model.User;
import com.guoji.mobile.cocobee.utils.Utils;
import com.guoji.mobile.cocobee.utils.XToastUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.Call;
import okhttp3.Response;

/**
 * 标签绑定
 * Created by Administrator on 2017/4/21.
 */

public class LabelBinding extends BaseActivity {
    @BindView(R.id.ll_back_pro)
    ImageView mBackPro;
    @BindView(R.id.et_label_bind)
    EditText mEtLabelBind;
    @BindView(R.id.iv_scan)
    TextView mIvScan;
    @BindView(R.id.reg_next)
    TextView mRegNext;

    private Car mCar;
    private User mUser;

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_label_binding;
    }

    @Override
    protected void initViewsAndEvents(Bundle savedInstanceState) {
        //获取intent传递过来的数据
        Intent intent = getIntent();
        mCar = (Car) intent.getSerializableExtra("car_info");
        mUser = Utils.getUserLoginInfo();
    }

    //车主绑定标签
    private void binderCarLabel(final String labelBind) {

        Map<String, String> params = new HashMap<String, String>();
        params.put("cno", mCar.getCno());
        params.put("lno", labelBind);

        OkGo.post(Path.CAR_LABEL_BIND).tag(this).params(params).execute(new StringDialogCallback(this, "标签绑定中...") {
            @Override
            public void onSuccess(String result, Call call, Response response) {
                if (!TextUtils.isEmpty(result)) {
                    JsonResult jsonResult = new Gson().fromJson(result, new TypeToken<JsonResult>() {
                    }.getType());
                    if (jsonResult != null && jsonResult.isFlag() && jsonResult.getStatusCode() == 200) {//标签绑定成功
                        EventBus.getDefault().post(new EventManager(AppConstants.LABEL_BIND_SUCCESS));
                        XToastUtils.showShortToast("标签绑定成功");
                        if (CheckUtils.equalsString(mUser.getConfigsafe(), "0")) {//不具备购买保险权限
                            finish();
                        } else {
                            SweetAlertDialog continueDialog = new SweetAlertDialog(LabelBinding.this, SweetAlertDialog.WARNING_TYPE);
                            continueDialog.setTitleText("绑定成功,是否购买保险?");
                            continueDialog.showCancelButton(true).setCancelText("否");
                            continueDialog.setConfirmText("是");
                            continueDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    sweetAlertDialog.dismiss();
//                                Intent intent = new Intent(LabelBinding.this, HandlePolicyInfoAct.class);
                                    Intent intent = new Intent(LabelBinding.this, BuyPoliceActivity.class);
                                    intent.putExtra("from", 2146);
                                    intent.putExtra("tagid", labelBind);//标签号
                                    intent.putExtra("name", mUser.getPname());//车主姓名
                                    intent.putExtra("phone", mUser.getMobile());//
                                    intent.putExtra("idcard", mUser.getIdcard());//身份证号码
                                    intent.putExtra("grade", mCar.getCno());//车牌号
                                    intent.putExtra("model", mCar.getCbuytype());//品牌型号
                                    intent.putExtra("motor", mCar.getCdevice());//发送机号
                                    intent.putExtra("date", new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
                                    startActivity(intent);
                                    finish();
                                }
                            });

                            continueDialog.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    sweetAlertDialog.dismiss();
                                    finish();
                                }
                            });
                            continueDialog.show();
                        }
                    } else if (jsonResult != null) { //标签绑定失败
                        XToastUtils.showShortToast(jsonResult.getMessage());
                    } else {
                        XToastUtils.showShortToast("绑定失败");
                    }
                } else {
                    XToastUtils.showShortToast("绑定失败");
                }
            }

            @Override
            public void onError(Call call, Response response, Exception e) {
                super.onError(call, response, e);
                XToastUtils.showShortToast("绑定标签出错");
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1666 && resultCode == 1667) {
            String scanResult = data.getStringExtra("scanResult");
            mEtLabelBind.setText(scanResult);
            return;
        }
    }

    private void checkTag(final String scanResult) {
        if (TextUtils.isEmpty(scanResult)) {
            return;
        }

        Map<String, String> params = new HashMap<String, String>();
        params.put("lno", scanResult);

        OkGo.post(Path.CHECK_TAG_PATH).tag(this).params(params).execute(new StringComCallback() {
            @Override
            public void onSuccess(String result, Call call, Response response) {
                if (!TextUtils.isEmpty(result)) {
                    JsonResult jsonResult = new Gson().fromJson(result, new TypeToken<JsonResult>() {
                    }.getType());
                    if (jsonResult != null && jsonResult.isFlag() && jsonResult.getStatusCode() == 200) {//标签已经存在且未被绑定
                        binderCarLabel(scanResult);
                    } else if (jsonResult != null) { //不存在,标签不存在
                        XToastUtils.showShortToast(jsonResult.getMessage());
                    } else {
                        XToastUtils.showShortToast("暂时未查到相关标签信息");
                    }
                } else {
                    XToastUtils.showShortToast("暂时未查到相关标签信息");
                }
            }

            @Override
            public void onError(Call call, Response response, Exception e) {
                super.onError(call, response, e);
                XToastUtils.showShortToast("请求标签信息出错");
            }
        });


    }

    @OnClick({R.id.ll_back_pro, R.id.iv_scan, R.id.reg_next})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_back_pro:
                finish();
                break;
            case R.id.iv_scan:
                startActivityForResult(new Intent(this, BarCodeScanAct.class), 1666);
                break;
            case R.id.reg_next:
                String labelBind = mEtLabelBind.getText().toString().trim();
                checkTag(labelBind);
                break;
        }
    }
}
