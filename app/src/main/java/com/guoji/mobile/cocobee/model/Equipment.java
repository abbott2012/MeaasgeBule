package com.guoji.mobile.cocobee.model;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/2/27.
 */
public class Equipment implements Serializable{

    private String eqno;
    private String eqlng;
    private String eqlat;
    private String eqisconn;
    private String createtime;
    private String eqaddr;
    private String eqip;
    private String eqport;
    private String hearttime;


    public Equipment(String eqno, String eqlng, String eqlat, String eqisconn, String createtime, String eqaddr, String eqip, String eqport, String hearttime) {
        this.eqno = eqno;
        this.eqlng = eqlng;
        this.eqlat = eqlat;
        this.eqisconn = eqisconn;
        this.createtime = createtime;
        this.eqaddr = eqaddr;
        this.eqip = eqip;
        this.eqport = eqport;
        this.hearttime = hearttime;
    }


    public String getEqno() {
        return eqno;
    }

    public void setEqno(String eqno) {
        this.eqno = eqno;
    }

    public String getEqlng() {
        return eqlng;
    }

    public void setEqlng(String eqlng) {
        this.eqlng = eqlng;
    }

    public String getEqlat() {
        return eqlat;
    }

    public void setEqlat(String eqlat) {
        this.eqlat = eqlat;
    }

    public String getEqisconn() {
        return eqisconn;
    }

    public void setEqisconn(String eqisconn) {
        this.eqisconn = eqisconn;
    }

    public String getCreatetime() {
        return createtime;
    }

    public void setCreatetime(String createtime) {
        this.createtime = createtime;
    }

    public String getEqaddr() {
        return eqaddr;
    }

    public void setEqaddr(String eqaddr) {
        this.eqaddr = eqaddr;
    }

    public String getEqip() {
        return eqip;
    }

    public void setEqip(String eqip) {
        this.eqip = eqip;
    }

    public String getEqport() {
        return eqport;
    }

    public void setEqport(String eqport) {
        this.eqport = eqport;
    }

    public String getHearttime() {
        return hearttime;
    }

    public void setHearttime(String hearttime) {
        this.hearttime = hearttime;
    }




}
