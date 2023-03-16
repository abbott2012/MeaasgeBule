package com.guoji.mobile.cocobee.model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by marktrace on 16/11/2.
 */
public class TagRecord implements Serializable{


    private String lid;
    private String lno; //标签号
    private String date;
    private boolean isLocalRecord = false;
    private String pname;
    private String mobile;

    private String cno;
    private String flag;//1代表车辆标签信息 2代表人员标签信息
    private String ccarpicurl;//车辆图片
    private String photourl;//人员图片
    private String gender; //0代表男;1代表女


    public TagRecord() {
    }


    public TagRecord(String lno) {
        this.lno = lno;
        this.date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }


    public TagRecord(String lno, String date) {
        this.lno = lno;
        this.date = date;
    }

    public String getLno() {
        return lno;
    }

    public void setLno(String lno) {
        this.lno = lno;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }


    public String getLid() {
        return lid;
    }

    public void setLid(String lid) {
        this.lid = lid;
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

    public String getCno() {
        return cno;
    }

    public void setCno(String cno) {
        this.cno = cno;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getCcarpicurl() {
        return ccarpicurl;
    }

    public void setCcarpicurl(String ccarpicurl) {
        this.ccarpicurl = ccarpicurl;
    }

    public String getPhotourl() {
        return photourl;
    }

    public void setPhotourl(String photourl) {
        this.photourl = photourl;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public boolean isLocalRecord() {
        return isLocalRecord;
    }

    public void setIsLocalRecord(boolean isLocalRecord) {
        this.isLocalRecord = isLocalRecord;
    }


    @Override
    public String toString() {
        return "TagRecord{" +
                "lid='" + lid + '\'' +
                ", lno='" + lno + '\'' +
                ", date='" + date + '\'' +
                ", isLocalRecord=" + isLocalRecord +
                ", pname='" + pname + '\'' +
                ", mobile='" + mobile + '\'' +
                ", cno='" + cno + '\'' +
                ", flag='" + flag + '\'' +
                ", ccarpicurl='" + ccarpicurl + '\'' +
                ", photourl='" + photourl + '\'' +
                ", gender='" + gender + '\'' +
                '}';
    }
}
