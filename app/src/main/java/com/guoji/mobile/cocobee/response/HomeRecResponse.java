package com.guoji.mobile.cocobee.response;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/6/20.
 */

public class HomeRecResponse implements Serializable {
    /**
     * card_id :  体验卡 40, 安心卡 33
     * days :   距离过期剩余天数
     * idcard : 551428955235145520   身份证（当对象为人的时候表示被绑定对象的身份证号，否则为车主身份证号）
     * order_id :  保单号
     * photourl :
     * pname : wanggang  姓名（当对象为人的时候表示被绑定对象姓名，否则为车主的姓名）
     * cbuyprice : 25  购买价格（当对象为人的时候为空）
     * cbuytime : 2017-05-08 14:02:00.0      购买时间（当对象为人的时候为空）
     * cbuytype : vvhhhh    车辆类型（当对象为人的时候为空）
     * ccarpicurl : /upload/551428955235145520/551428955235145520郭彬测试_1.jpg,/upload/551428955235145520/551428955235145520郭彬测试_2.jpg,/upload/551428955235145520/551428955235145520郭彬测试_3.jpg,/upload/551428955235145520/551428955235145520郭彬测试_4.jpg
     * 车辆照片（当对象为人的时候为空）
     * cdevice :    发动机号（当对象为人的时候为空）
     * cframe : ggggg   车架号（当对象为人的时候为空）
     * cno : 郭彬测试   车牌（当对象为人的时候为空）
     * islocked : 1    锁车状态（当对象为人的时候为空）
     * target_id: person,  标识是车还是人（当值为person表示人，当值为car表示车）
     * flag
     * labelid    绑定的标签id
     * birthday
     * sex   0男1女
     * ptype
     * lno  标签号
     * peid 被监护对象的主键id(人员)
     * manage_control 是否具有管理权限0不具备1具备
     * online 0线上1线下
     */

    private String card_id;
    private String days;
    private String idcard;
    private String order_id;
    private String photourl;
    private String pname;
    private String cbuyprice;
    private String cbuytime;
    private String cbuytype;
    private String ccarpicurl;
    private String cdevice;
    private String cframe;
    private String cno;
    private String islocked;
    private String target_id;
    private String labelid;
    private String remarkname;
    private String finish;
    private String birthday;
    private String sex;
    private String ptype;
    private String lno;
    private String peid;
    private String manage_control;
    private String cid;
    private String online;
    private int flag = -1;

    public String getOnline() {
        return online;
    }

    public void setOnline(String online) {
        this.online = online;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getManage_control() {
        return manage_control;
    }

    public void setManage_control(String manage_control) {
        this.manage_control = manage_control;
    }

    public String getPeid() {
        return peid;
    }

    public void setPeid(String peid) {
        this.peid = peid;
    }

    public String getLno() {
        return lno;
    }

    public void setLno(String lno) {
        this.lno = lno;
    }

    public String getPtype() {
        return ptype;
    }

    public void setPtype(String ptype) {
        this.ptype = ptype;
    }


    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getFinish() {
        return finish;
    }

    public void setFinish(String finish) {
        this.finish = finish;
    }

    public String getRemarkname() {
        return remarkname;
    }

    public void setRemarkname(String remarkname) {
        this.remarkname = remarkname;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public String getLabelid() {
        return labelid;
    }

    public void setLabelid(String labelid) {
        this.labelid = labelid;
    }

    public String getCard_id() {
        return card_id;
    }

    public void setCard_id(String card_id) {
        this.card_id = card_id;
    }

    public String getTarget_id() {
        return target_id;
    }

    public void setTarget_id(String target_id) {
        this.target_id = target_id;
    }

    public String getDays() {
        return days;
    }

    public void setDays(String days) {
        this.days = days;
    }

    public String getIdcard() {
        return idcard;
    }

    public void setIdcard(String idcard) {
        this.idcard = idcard;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getPhotourl() {
        return photourl;
    }

    public void setPhotourl(String photourl) {
        this.photourl = photourl;
    }

    public String getPname() {
        return pname;
    }

    public void setPname(String pname) {
        this.pname = pname;
    }

    public String getCbuyprice() {
        return cbuyprice;
    }

    public void setCbuyprice(String cbuyprice) {
        this.cbuyprice = cbuyprice;
    }

    public String getCbuytime() {
        return cbuytime;
    }

    public void setCbuytime(String cbuytime) {
        this.cbuytime = cbuytime;
    }

    public String getCbuytype() {
        return cbuytype;
    }

    public void setCbuytype(String cbuytype) {
        this.cbuytype = cbuytype;
    }

    public String getCcarpicurl() {
        return ccarpicurl;
    }

    public void setCcarpicurl(String ccarpicurl) {
        this.ccarpicurl = ccarpicurl;
    }

    public String getCdevice() {
        return cdevice;
    }

    public void setCdevice(String cdevice) {
        this.cdevice = cdevice;
    }

    public String getCframe() {
        return cframe;
    }

    public void setCframe(String cframe) {
        this.cframe = cframe;
    }

    public String getCno() {
        return cno;
    }

    public void setCno(String cno) {
        this.cno = cno;
    }

    public String getIslocked() {
        return islocked;
    }

    public void setIslocked(String islocked) {
        this.islocked = islocked;
    }
}
