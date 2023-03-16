package com.guoji.mobile.cocobee.response;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/4/17.
 */

public class CarInfoResponse implements Serializable {

    /**
     * cbuyprice : 11
     * cbuytime : 2017-01-14 00:00:00.0
     * cbuytype :
     * ccarpicurl :
     * ccolor :
     * cdevice : 11
     * cframe : 11
     * cid : 2
     * cno : 11
     * idcard : 30458795124185552x
     * isinsurance : 1
     * islocked : 1
     * lno :
     * mobile : 13325346231
     * pname : 郭彬
     * safeclass :
     * safeendtime :
     * safelimit :
     * safeorder :
     * safeplace :
     * safeprice :
     * safestarttime :
     * order_id:保单号(为null代表未购买保单)
     */
    private String cbuyprice;
    private String cbuytime;
    private String cbuytype;
    private String ccarpicurl;
    private String ccolor;
    private String cdevice;
    private String cframe;
    private String cid;
    private String cno;
    private String idcard;
//    private String isinsurance;
    private String islocked;
    private String lno;
    private String mobile;
    private String pname;
//    private String safeclass;
//    private String safeendtime;
//    private String safelimit;
//    private String safeorder;
//    private String safeplace;
//    private String safeprice;
//    private String safestarttime;
    private String order_id;//保单号
    private String service_price;//保单服务费
    private String max_price;//保单限额
    private String payTime;//保单生效时间
    private String maturity_date;//保单结束时间
    private String grade;//保单等级

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getService_price() {
        return service_price;
    }

    public void setService_price(String service_price) {
        this.service_price = service_price;
    }

    public String getMax_price() {
        return max_price;
    }

    public void setMax_price(String max_price) {
        this.max_price = max_price;
    }

    public String getPayTime() {
        return payTime;
    }

    public void setPayTime(String payTime) {
        this.payTime = payTime;
    }

    public String getMaturity_date() {
        return maturity_date;
    }

    public void setMaturity_date(String maturity_date) {
        this.maturity_date = maturity_date;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getCbuyprice() {
        return cbuyprice;
    }

    public void setCbuyprice(String cbuyprice) {
        this.cbuyprice = cbuyprice;
    }

    public String getCbuytime() {
        return cbuytime;
    }

    public void setCbuytime(String cbuytime) {
        this.cbuytime = cbuytime;
    }

    public String getCbuytype() {
        return cbuytype;
    }

    public void setCbuytype(String cbuytype) {
        this.cbuytype = cbuytype;
    }

    public String getCcarpicurl() {
        return ccarpicurl;
    }

    public void setCcarpicurl(String ccarpicurl) {
        this.ccarpicurl = ccarpicurl;
    }

    public String getCcolor() {
        return ccolor;
    }

    public void setCcolor(String ccolor) {
        this.ccolor = ccolor;
    }

    public String getCdevice() {
        return cdevice;
    }

    public void setCdevice(String cdevice) {
        this.cdevice = cdevice;
    }

    public String getCframe() {
        return cframe;
    }

    public void setCframe(String cframe) {
        this.cframe = cframe;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getCno() {
        return cno;
    }

    public void setCno(String cno) {
        this.cno = cno;
    }

    public String getIdcard() {
        return idcard;
    }

    public void setIdcard(String idcard) {
        this.idcard = idcard;
    }

    public String getIslocked() {
        return islocked;
    }

    public void setIslocked(String islocked) {
        this.islocked = islocked;
    }

    public String getLno() {
        return lno;
    }

    public void setLno(String lno) {
        this.lno = lno;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getPname() {
        return pname;
    }

    public void setPname(String pname) {
        this.pname = pname;
    }

}
