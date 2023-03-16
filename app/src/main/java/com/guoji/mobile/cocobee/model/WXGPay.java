package com.guoji.mobile.cocobee.model;

import java.io.Serializable;

/**
 * ClassName: Alipay <br>
 * Description: 微信支付信息<br>
 * Author: Cyarie <br>
 * Created: 2016/12/1 10:53 <br>
 * Update Time：<br>
 * Update Description：<br>
 */
public class WXGPay implements Serializable {
//[{"appid":"wx6ae0f4feabfcb549","token_id":"1781b382c728976dfe06fb3b990d09800"}]
    private String token_id;
    private String appid;
    public String getToken_id() {
        return token_id;
    }

    public void setToken_id(String token_id) {
        this.token_id = token_id;
    }

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }
}
