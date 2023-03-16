package com.guoji.mobile.cocobee.wxapi;

import android.content.Intent;
import android.os.Bundle;

import com.bql.utils.EventManager;
import com.bql.utils.SystemBarUtils;
import com.guoji.mobile.cocobee.activity.base.BaseActivity;
import com.guoji.mobile.cocobee.common.AppConstants;
import com.guoji.mobile.cocobee.common.ElectricVehicleApp;
import com.guoji.mobile.cocobee.utils.XToastUtils;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;

import org.greenrobot.eventbus.EventBus;

/**
 * Author: hcy
 * Description:微信支付结果
 * Created: 2016/10/26 08:50
 */
public class WXPayEntryActivity extends BaseActivity implements IWXAPIEventHandler {

    //    private IWXAPI api;


    @Override
    protected int getContentViewLayoutID() {
        return 0;
    }

    @Override
    protected void initViewsAndEvents(Bundle savedInstanceState) {
        //        api = WXAPIFactory.createWXAPI(this, PayConfig.WX_APP_ID);
        ElectricVehicleApp.sApi.handleIntent(getIntent(), this);
        SystemBarUtils.immersiveStatusBar(this, 0);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
//        ElectricVehicleApp.sApi.handleIntent(intent, this);
    }

    @Override
    protected boolean toggleOverridePendingTransition() {
        return true;
    }

    @Override
    protected TransitionMode getOverridePendingTransitionMode() {
        return TransitionMode.NONE;
    }

    @Override
    public void onReq(BaseReq req) {
    }

    @Override
    public void onResp(BaseResp resp) {
        if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {//支付类型
            switch (resp.errCode) {
                case BaseResp.ErrCode.ERR_OK://支付成功
                    EventBus.getDefault().post(new EventManager(AppConstants.WX_PAY_SUCCESS));
                    System.out.println("支付成功");
                    finish();
                    break;
                case BaseResp.ErrCode.ERR_COMM://支付失败
                    XToastUtils.showShortToast("支付失败");
                    finish();
                    break;
                case BaseResp.ErrCode.ERR_USER_CANCEL://用户取消支付
                    EventBus.getDefault().post(new EventManager(AppConstants.WX_PAY_CANCEL));
                    XToastUtils.showShortToast("用户取消");
                    finish();
                    break;
                case -4:// 认证被否决
                    XToastUtils.showShortToast("认证被否决");
                    finish();
                    break;

                case -3:// 发送失败
                    XToastUtils.showShortToast("发送失败");
                    finish();
                    break;
                case -5:// 不支持的错误
                    XToastUtils.showShortToast("不支持的错误");
                    finish();
                    break;
            }
        }
    }
}