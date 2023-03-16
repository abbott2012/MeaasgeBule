package com.guoji.mobile.cocobee.model;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/12/17.
 */
public class TypeResponse implements Serializable{
//"dname":"酒店","did":"46"
    private String dname;
    private String did;

    public String getDid() {
        return did;
    }

    public void setDid(String did) {
        this.did = did;
    }

    public String getDname() {
        return dname;
    }

    public void setDname(String dname) {
        this.dname = dname;
    }
}
