package com.guoji.mobile.cocobee.model;

/**
 * Created by Administrator on 2017/1/19.
 */
public class Policy {

  /*  private String pname; //车主姓名

    private String idcard; //车主身份证号

    private String mobile; //车主手机号码

    private String safeorder; //凭证流水号

//    private String safeprice; //防盗标签服务费
    private String service_price; //防盗标签服务费

    private String safestarttime; //开始时间

    private String safeendtime; //结束时间

    private String isgrabsafe;

    private String isthirdsafe;

    private String cno; //车牌号

    private String safelimit; //

    private String safeclass; //车价档次

    private String orderinfor; //2400-60

    private String safelno; //电子标签号

    private String createtime; //车辆录入平台日期

    private String safeplace;  //保险区域范围

    private String cbuytype; //车辆型号

    private String cdevice; //发动机号*/

    /**
     * cdevice true string 发动机号
     * grade true string 保单等级（目前只有A、B等级）
     * maturity_date true string 保单有效结束日期
     * payTime true string 保单生效日期
     * lno true string 服务标签号
     * idcard true string 身份证号
     * order_id true string 保单号
     * cbuytype true string 车辆型号
     * mobile true string 电话号码
     * isthirdsafe true string 是否购买第三者责任：0：否，1：是
     * createtime true string 车辆录入平台日期
     * max_price true string 保险赔偿最高限额
     * service_price true string 保险服务费
     * cno true string 车牌
     */

    private String cdevice; //发动机号
    private String grade;// 保单等级（目前只有A、B等级）
    private String maturity_date; //保险有效结束日期
    private String paytime;// 保单生效日期
    private String lno;// 服务标签号
    private String idcard;// 身份证号
    private String order_id;// 保单号
    private String cbuytype;// 车辆型号
    private String mobile;// 电话号码
    private String isthirdsafe;// 是否购买第三者责任：0：否，1：是
    private String createtime;// 车辆录入平台日期
    private String max_price;// 保险赔偿最高限额
    private String service_price;// 保险服务费
    private String cno;//车牌
    private String pname;//车主姓名

    public String getPname() {
        return pname;
    }

    public void setPname(String pname) {
        this.pname = pname;
    }

    public String getCdevice() {
        return cdevice;
    }

    public void setCdevice(String cdevice) {
        this.cdevice = cdevice;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getMaturity_date() {
        return maturity_date;
    }

    public void setMaturity_date(String maturity_date) {
        this.maturity_date = maturity_date;
    }

    public String getPayTime() {
        return paytime;
    }

    public void setPayTime(String paytime) {
        this.paytime = paytime;
    }

    public String getLno() {
        return lno;
    }

    public void setLno(String lno) {
        this.lno = lno;
    }

    public String getIdcard() {
        return idcard;
    }

    public void setIdcard(String idcard) {
        this.idcard = idcard;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getCbuytype() {
        return cbuytype;
    }

    public void setCbuytype(String cbuytype) {
        this.cbuytype = cbuytype;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getIsthirdsafe() {
        return isthirdsafe;
    }

    public void setIsthirdsafe(String isthirdsafe) {
        this.isthirdsafe = isthirdsafe;
    }

    public String getCreatetime() {
        return createtime;
    }

    public void setCreatetime(String createtime) {
        this.createtime = createtime;
    }

    public String getMax_price() {
        return max_price;
    }

    public void setMax_price(String max_price) {
        this.max_price = max_price;
    }

    public String getService_price() {
        return service_price;
    }

    public void setService_price(String service_price) {
        this.service_price = service_price;
    }

    public String getCno() {
        return cno;
    }

    public void setCno(String cno) {
        this.cno = cno;
    }
}
