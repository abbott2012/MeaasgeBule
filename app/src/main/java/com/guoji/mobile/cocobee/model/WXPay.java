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
public class WXPay implements Serializable {


    /**
     * partnerId : 1398039202
     * timestamp : 1480560536
     * sign : 01FDD49CB3ECD0FDE09FCA55DADCF7F3
     * nonceStr : KV1dEvBeofOnmGwU0wwAOtbuM1jUg8b3
     * prePayId : wx20161201105149c7e908a9cd0288566103
     * appId : wxcdf8ce91b048e0c5
     */

    private String partnerid;
    private String timestamp;
    private String sign;
    private String noncestr;
    private String prepayid;
    private String appid;

    public String getPartnerid() {
        return partnerid;
    }

    public void setPartnerid(String partnerid) {
        this.partnerid = partnerid;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getNoncestr() {
        return noncestr;
    }

    public void setNoncestr(String noncestr) {
        this.noncestr = noncestr;
    }

    public String getPrepayid() {
        return prepayid;
    }

    public void setPrepayid(String prepayid) {
        this.prepayid = prepayid;
    }

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }
}
