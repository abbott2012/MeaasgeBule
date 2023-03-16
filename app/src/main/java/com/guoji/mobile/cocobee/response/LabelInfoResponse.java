package com.guoji.mobile.cocobee.response;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/6/20.
 */

public class LabelInfoResponse implements Serializable {
    /**
     * 包括RYZSMC(人员真实名称)、
     * XB(性别)、
     * CSRQ(出生日期)、
     * XJZD(现居住地)、
     * HJDZ(户籍地址)、
     * DH(电话)、
     * YX（邮箱）、
     * GZDW（工作单位）、
     * SFZ（身份证）
     */
    private String RYZSMC;
    private String XB;
    private String CSRQ;
    private String XJZD;
    private String HJDZ;
    private String DH;
    private String YX;
    private String GZDW;
    private String SFZ;

    public String getRYZSMC() {
        return RYZSMC;
    }

    public void setRYZSMC(String RYZSMC) {
        this.RYZSMC = RYZSMC;
    }

    public String getXB() {
        return XB;
    }

    public void setXB(String XB) {
        this.XB = XB;
    }

    public String getCSRQ() {
        return CSRQ;
    }

    public void setCSRQ(String CSRQ) {
        this.CSRQ = CSRQ;
    }

    public String getXJZD() {
        return XJZD;
    }

    public void setXJZD(String XJZD) {
        this.XJZD = XJZD;
    }

    public String getHJDZ() {
        return HJDZ;
    }

    public void setHJDZ(String HJDZ) {
        this.HJDZ = HJDZ;
    }

    public String getDH() {
        return DH;
    }

    public void setDH(String DH) {
        this.DH = DH;
    }

    public String getYX() {
        return YX;
    }

    public void setYX(String YX) {
        this.YX = YX;
    }

    public String getGZDW() {
        return GZDW;
    }

    public void setGZDW(String GZDW) {
        this.GZDW = GZDW;
    }

    public String getSFZ() {
        return SFZ;
    }

    public void setSFZ(String SFZ) {
        this.SFZ = SFZ;
    }
}
