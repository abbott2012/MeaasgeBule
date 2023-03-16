package com.guoji.mobile.cocobee.response;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/8/21.
 */

public class SelectAddressResponse implements Serializable{
    private String orgname;
    private String orgid;
    private String is_nextl;

    public String getIs_nextl() {
        return is_nextl;
    }

    public void setIs_nextl(String is_nextl) {
        this.is_nextl = is_nextl;
    }

    public String getOrgname() {
        return orgname;
    }

    public void setOrgname(String orgname) {
        this.orgname = orgname;
    }

    public String getOrgid() {
        return orgid;
    }

    public void setOrgid(String orgid) {
        this.orgid = orgid;
    }
}
