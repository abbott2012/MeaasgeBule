package com.guoji.mobile.cocobee.utils;

import android.app.Activity;
import android.content.Context;

import com.guoji.mobile.cocobee.common.ElectricVehicleApp;
import com.guoji.mobile.cocobee.common.PayConfig;
import com.guoji.mobile.cocobee.model.WXGPay;
import com.guoji.mobile.cocobee.model.WXPay;
import com.switfpass.pay.MainApplication;
import com.switfpass.pay.activity.PayPlugin;
import com.switfpass.pay.bean.RequestMsg;
import com.tencent.mm.opensdk.constants.Build;
import com.tencent.mm.opensdk.modelpay.PayReq;


/**
 * ClassName: WXPayUtils <br>
 * Description: 微信支付相关<br>
 * Author: Cyarie <br>
 * Created: 2016/12/8 11:55 <br>
 * Update Time：<br>
 * Update Description：<br>
 */
public class WXPayUtils {


    /**
     * 检测是否支持微信支付
     */
    public static boolean checkIsSupportWXPay(Context context) {
        if (context instanceof Activity) {
            boolean isPaySupported = ElectricVehicleApp.sApi.getWXAppSupportAPI() >= Build.PAY_SUPPORTED_SDK_INT;
            boolean isWXInstalled = ElectricVehicleApp.sApi.isWXAppInstalled();
            if (!isPaySupported) {//微信客户端版本过低
                XToastUtils.showShortToast("微信版本过低");
                return false;
            } else if (!isWXInstalled) {//没有安装微信客户端
                XToastUtils.showShortToast("没有安装微信客户端");
                return false;
            }
        } else {
            throw new IllegalArgumentException("context must be instanceof Activity");
        }
        return true;
    }


    /**
     * 微信支付
     */
    public static void payByWX(WXPay wxPay) {
        PayReq req = new PayReq();
        //        req.appId = wxPay.getAppId();
        req.appId = wxPay.getAppid();
        req.partnerId = wxPay.getPartnerid();
        req.prepayId = wxPay.getPrepayid();
        req.nonceStr = wxPay.getNoncestr();
        req.timeStamp = wxPay.getTimestamp();
        req.packageValue = "Sign=WXPay";
        req.sign = wxPay.getSign();//签名后台返回，签名看在本地还是服务器
        //        req.extData = "app data";
        // 在支付之前，如果应用没有注册到微信，应该先调用IWXMsg.registerApp将应用注册到微信
        ElectricVehicleApp.sApi.sendReq(req);//支付结果在.wxapi.WXPayEntryActivity
    }

    /**
     * 光大微信支付
     */
    public static void payByGWX(Activity activity, WXGPay wxgPay) {
        RequestMsg msg = new RequestMsg();
        msg.setTokenId(wxgPay.getToken_id());
        //微信
        msg.setTradeType(MainApplication.WX_APP_TYPE);
        msg.setAppId(PayConfig.WX_APP_ID);//wxd3a1cdf74d0c41b3
        PayPlugin.unifiedAppPay(activity, msg);
    }


}
