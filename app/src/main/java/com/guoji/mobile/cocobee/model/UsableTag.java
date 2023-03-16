package com.guoji.mobile.cocobee.model;

/**
 * Created by Administrator on 2016/12/14.
 */
public class UsableTag {

    private String lid;
    private String lno;


    public UsableTag() {
    }

    public UsableTag(String lid, String lno) {
        this.lid = lid;
        this.lno = lno;
    }


    public String getLid() {
        return lid;
    }

    public void setLid(String lid) {
        this.lid = lid;
    }

    public String getLno() {
        return lno;
    }

    public void setLno(String lno) {
        this.lno = lno;
    }
}
