package com.guoji.mobile.cocobee.response;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/6/21.
 */

public class SelectServiceResponse implements Serializable{


    /**
     * card_ddesc : 交四十元押金,免费试用三十天,不满意全额退款
     * target_name : 守护车辆
     * card_name : 体验卡
     * target_id : 38
     * id : 1
     * card_mark : 预支四十元免费体验三十天!不满意携标签随时可退!逾期未退自动帮您升级为正式版，不再缴纳任何费用,可以连续使用三年!(金额不再退还)
     * card_id : 40
     * orgid : 7
     */

    private String card_ddesc;
    private String target_name;
    private String card_name;
    private String target_id;
    private String id;
    private String card_mark;
    private String card_id;
    private String orgid;

    public String getCard_ddesc() {
        return card_ddesc;
    }

    public void setCard_ddesc(String card_ddesc) {
        this.card_ddesc = card_ddesc;
    }

    public String getTarget_name() {
        return target_name;
    }

    public void setTarget_name(String target_name) {
        this.target_name = target_name;
    }

    public String getCard_name() {
        return card_name;
    }

    public void setCard_name(String card_name) {
        this.card_name = card_name;
    }

    public String getTarget_id() {
        return target_id;
    }

    public void setTarget_id(String target_id) {
        this.target_id = target_id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCard_mark() {
        return card_mark;
    }

    public void setCard_mark(String card_mark) {
        this.card_mark = card_mark;
    }

    public String getCard_id() {
        return card_id;
    }

    public void setCard_id(String card_id) {
        this.card_id = card_id;
    }

    public String getOrgid() {
        return orgid;
    }

    public void setOrgid(String orgid) {
        this.orgid = orgid;
    }
}
