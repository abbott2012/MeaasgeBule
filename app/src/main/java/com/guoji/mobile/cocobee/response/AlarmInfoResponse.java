package com.guoji.mobile.cocobee.response;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/5/15.
 */

public class AlarmInfoResponse implements Serializable{
//    lno true string 标签号
//    aaddress true string 地址
//    adesc true string 报警描述
//    alarmlng true string 经度
//    alarmlat true string 纬度
//    atime true string 报警时间
//    apeople true string 用户姓名
//    atype true string 类型（1：代表车 2：代表人）
//    cno true string 车牌（如果为人则返回空）
//    cbuytype true string 品牌型号
//    status true string （报警状态：0：待确认，1：已确认，2：待审核，3：审核通过，4：退回）

    private String lno;
    private String aaddress;
    private String adesc;
    private String alarmlng;
    private String alarmlat;
    private String atime;
    private String apeople;
    private String atype;
    private String cno;
    private String status;
    private String cbuytype;

    public String getCbuytype() {
        return cbuytype;
    }

    public void setCbuytype(String cbuytype) {
        this.cbuytype = cbuytype;
    }

    public String getLno() {
        return lno;
    }

    public void setLno(String lno) {
        this.lno = lno;
    }

    public String getAaddress() {
        return aaddress;
    }

    public void setAaddress(String aaddress) {
        this.aaddress = aaddress;
    }

    public String getAdesc() {
        return adesc;
    }

    public void setAdesc(String adesc) {
        this.adesc = adesc;
    }

    public String getAlarmlng() {
        return alarmlng;
    }

    public void setAlarmlng(String alarmlng) {
        this.alarmlng = alarmlng;
    }

    public String getAlarmlat() {
        return alarmlat;
    }

    public void setAlarmlat(String alarmlat) {
        this.alarmlat = alarmlat;
    }

    public String getAtime() {
        return atime;
    }

    public void setAtime(String atime) {
        this.atime = atime;
    }

    public String getApeople() {
        return apeople;
    }

    public void setApeople(String apeople) {
        this.apeople = apeople;
    }

    public String getAtype() {
        return atype;
    }

    public void setAtype(String atype) {
        this.atype = atype;
    }

    public String getCno() {
        return cno;
    }

    public void setCno(String cno) {
        this.cno = cno;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
