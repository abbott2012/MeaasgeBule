package com.guoji.mobile.cocobee.model;

/**
 * Created by Administrator on 2016/12/17.
 */
public class PoliceStation {
    private String orgid;
    private String orgname;


    public PoliceStation() {
    }


    public PoliceStation(String orgid, String orgname) {
        this.orgid = orgid;
        this.orgname = orgname;
    }


    public String getOrgid() {
        return orgid;
    }

    public void setOrgid(String orgid) {
        this.orgid = orgid;
    }

    public String getOrgname() {
        return orgname;
    }

    public void setOrgname(String orgname) {
        this.orgname = orgname;
    }
}
