package com.guoji.mobile.cocobee.response;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/6/22.
 */

public class HomePositionResponse implements Serializable{

    /**
     * eqlat : 22.557174
     * eqlng : 113.950591
     */

    private String eqlat;
    private String eqlng;

    public String getEqlat() {
        return eqlat;
    }

    public void setEqlat(String eqlat) {
        this.eqlat = eqlat;
    }

    public String getEqlng() {
        return eqlng;
    }

    public void setEqlng(String eqlng) {
        this.eqlng = eqlng;
    }
}
