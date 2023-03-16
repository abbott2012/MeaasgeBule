package com.guoji.mobile.cocobee.response;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/6/29.
 */

public class FamilyResponse implements Serializable{

    /**
     * photourl : /upload/1.jpg  //监护家庭成员头像地址
     * manage_control : 0  //管理权限（0：无管理权限 1：有管理权限）
     * remark_relation :  //备注关系
     * id : 59501 //关系表主键id
     * t_people_id  //人员主键
     * IsNotRegister   0代表邀请中 1代表邀请完成
     */

    private String photourl;
    private String manage_control;
    private String remark_relation;
    private String id;
    private String t_people_id;
    private String IsNotRegister;

    public String getIsNotRegister() {
        return IsNotRegister;
    }

    public void setIsNotRegister(String isNotRegister) {
        IsNotRegister = isNotRegister;
    }

    public String getT_people_id() {
        return t_people_id;
    }

    public void setT_people_id(String t_people_id) {
        this.t_people_id = t_people_id;
    }

    public String getPhotourl() {
        return photourl;
    }

    public void setPhotourl(String photourl) {
        this.photourl = photourl;
    }

    public String getManage_control() {
        return manage_control;
    }

    public void setManage_control(String manage_control) {
        this.manage_control = manage_control;
    }

    public String getRemark_relation() {
        return remark_relation;
    }

    public void setRemark_relation(String remark_relation) {
        this.remark_relation = remark_relation;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
