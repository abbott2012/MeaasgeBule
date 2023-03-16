package com.guoji.mobile.cocobee.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bql.utils.EventManager;
import com.bql.utils.ManifestUtils;
import com.guoji.mobile.cocobee.R;
import com.guoji.mobile.cocobee.activity.ChangePwdOrgActivity;
import com.guoji.mobile.cocobee.activity.LoginActivity;
import com.guoji.mobile.cocobee.activity.me.DownLoadMapActivity;
import com.guoji.mobile.cocobee.common.AppConstants;
import com.guoji.mobile.cocobee.common.AppManager;
import com.guoji.mobile.cocobee.common.ElectricVehicleApp;
import com.guoji.mobile.cocobee.fragment.base.BaseFragment;
import com.guoji.mobile.cocobee.model.User;
import com.guoji.mobile.cocobee.utils.UpdateManager;
import com.guoji.mobile.cocobee.utils.Utils;
import com.guoji.mobile.cocobee.utils.XToastUtils;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by Administrator on 2017/6/3.
 */

public class SettingFragment extends BaseFragment {
    @BindView(R.id.tv_cur_version)
    TextView tvCurVersion;
    @BindView(R.id.out_login)
    TextView outLogin;
    @BindView(R.id.ll_chang_pwd)
    LinearLayout mLlChangPwd;
    @BindView(R.id.ll_no_wifi_map)
    LinearLayout mLlNoWifiMap;
    @BindView(R.id.ll_check_version)
    LinearLayout mLlCheckVersion;
    private User mUserLoginInfo;
    private ElectricVehicleApp mApp;

    public static SettingFragment getInstance() {
        SettingFragment fragment = new SettingFragment();
        return fragment;
    }

    @Override
    protected void initViewsAndEvents(Bundle savedInstanceState) {
        mUserLoginInfo = Utils.getUserLoginInfo();
        mApp = ElectricVehicleApp.getApp();
        initView();
    }

    private void initView() {
        String versionName = ManifestUtils.getVersionName(_mActivity);
        tvCurVersion.setText(versionName);
    }

    @Override
    protected void initToolbarHere() {
        initToolbarWithLeftText("设置", "我的");
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.fragment_setting;
    }


    @OnClick({R.id.out_login, R.id.ll_no_wifi_map, R.id.ll_chang_pwd, R.id.ll_check_version,})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.out_login:
                if (mUserLoginInfo != null) {
                    SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(_mActivity, SweetAlertDialog.CUSTOM_IMAGE_TYPE);
                    sweetAlertDialog.setCustomImage(R.drawable.exit_tip);
                    sweetAlertDialog.setTitleText("退出当前用户？");
                    sweetAlertDialog.setConfirmText("确定");
                    sweetAlertDialog.showCancelButton(true);
                    sweetAlertDialog.setCancelText("取消");
                    sweetAlertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            sweetAlertDialog.dismiss();
                            mUserLoginInfo = null;
                            Utils.putUserLoginInfo(mUserLoginInfo);
                            EventBus.getDefault().post(new EventManager(AppConstants.OUT_LOGIN_SUCCESS));
                            AppManager.getAppManager().releaseBluetoothResource(_mActivity); //释放蓝牙资源
                            startActivity(new Intent(_mActivity, LoginActivity.class));
                            AppManager.getAppManager().finishAllActivity();
                        }
                    });

                    sweetAlertDialog.show();
                } else {
                    startActivity(new Intent(_mActivity, LoginActivity.class));
                }
                break;
            case R.id.ll_chang_pwd:
                Intent i = new Intent(_mActivity, ChangePwdOrgActivity.class);
                startActivity(i);
                break;
            case R.id.ll_no_wifi_map://离线地图下载
                startActivity(new Intent(_mActivity, DownLoadMapActivity.class));
                break;
            case R.id.ll_check_version:
                if (mApp.getCheckVersionResult() == 1) { //有最新版本
                    UpdateManager updateManager = new UpdateManager(_mActivity);
                    updateManager.checkUpdateInfo(false); //false代表手动检查更新
                } else {
                    XToastUtils.showShortToast("已是最新版本");
                }
                break;

        }
    }
}
