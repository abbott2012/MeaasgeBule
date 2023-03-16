package com.guoji.mobile.cocobee.response;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/4/17.
 */

public class UserRegInfo implements Serializable {
//    pname true string 用户姓名
//    mobile true string 用户手机号码
//    password true string 登录密码
//    idcard true string 身份证号
//    sex true string 性别（0：男，1：女）
//    birthday true string 生日日期
//    orgid true string 用户组织机构
//    address true string 现居地址
//    regiaddr true string 户籍地址
    private String pname;
    private String mobile;
    private String password;
    private String idcard;
    private String sex;
    private String birthday;
    private String orgid;
    private String address;
    private String ptype;
    private String regiaddr;

    public String getRegiaddr() {
        return regiaddr;
    }

    public void setRegiaddr(String regiaddr) {
        this.regiaddr = regiaddr;
    }

    public String getPtype() {
        return ptype;
    }

    public void setPtype(String ptype) {
        this.ptype = ptype;
    }

    public String getPname() {
        return pname;
    }

    public void setPname(String pname) {
        this.pname = pname;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getIdcard() {
        return idcard;
    }

    public void setIdcard(String idcard) {
        this.idcard = idcard;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getOrgid() {
        return orgid;
    }

    public void setOrgid(String orgid) {
        this.orgid = orgid;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
