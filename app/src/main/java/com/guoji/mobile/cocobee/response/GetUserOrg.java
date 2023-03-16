package com.guoji.mobile.cocobee.response;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/4/17.
 */

public class GetUserOrg implements Serializable {

    /**
     * orgid : 1
     * orgname : 湖南省公安厅
     */

    private String orgid;
    private String orgname;
    private String selectPosition;

    public String getSelectPosition() {
        return selectPosition;
    }

    public void setSelectPosition(String selectPosition) {
        this.selectPosition = selectPosition;
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
