package com.guoji.mobile.cocobee.response;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/4/17.
 */

public class AdResponse implements Serializable {

    /**
     * ad_begin_time : 2017-04-11 15:25:44.0
     * ad_end_time : 2017-04-11 15:25:46.0
     * ad_pic_url : http://218.17.157.214:8989/Pro_Marktrace_Electrocar/upload/tianmao.jpg
     * ad_text : 商业广告
     * ad_title : 测试1
     * ad_url : http://www.marktrace.com
     * ap_height : 480
     * ap_status : 1
     * ap_sys_type : 0
     * ap_width : 320
     */

    private String ad_begin_time;
    private String ad_end_time;
    private String ad_pic_url;
    private String ad_text;
    private String ad_title;
    private String ad_url;
    private String ap_height;
    private String ap_status;
    private String ap_sys_type;
    private String ap_width;

    public String getAd_begin_time() {
        return ad_begin_time;
    }

    public void setAd_begin_time(String ad_begin_time) {
        this.ad_begin_time = ad_begin_time;
    }

    public String getAd_end_time() {
        return ad_end_time;
    }

    public void setAd_end_time(String ad_end_time) {
        this.ad_end_time = ad_end_time;
    }

    public String getAd_pic_url() {
        return ad_pic_url;
    }

    public void setAd_pic_url(String ad_pic_url) {
        this.ad_pic_url = ad_pic_url;
    }

    public String getAd_text() {
        return ad_text;
    }

    public void setAd_text(String ad_text) {
        this.ad_text = ad_text;
    }

    public String getAd_title() {
        return ad_title;
    }

    public void setAd_title(String ad_title) {
        this.ad_title = ad_title;
    }

    public String getAd_url() {
        return ad_url;
    }

    public void setAd_url(String ad_url) {
        this.ad_url = ad_url;
    }

    public String getAp_height() {
        return ap_height;
    }

    public void setAp_height(String ap_height) {
        this.ap_height = ap_height;
    }

    public String getAp_status() {
        return ap_status;
    }

    public void setAp_status(String ap_status) {
        this.ap_status = ap_status;
    }

    public String getAp_sys_type() {
        return ap_sys_type;
    }

    public void setAp_sys_type(String ap_sys_type) {
        this.ap_sys_type = ap_sys_type;
    }

    public String getAp_width() {
        return ap_width;
    }

    public void setAp_width(String ap_width) {
        this.ap_width = ap_width;
    }
}
