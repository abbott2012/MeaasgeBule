package com.guoji.mobile.cocobee.model;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/1/16.
 */
public class ConveniencePoint implements Serializable{

    private  int sid;
    private String latitude;
    private String longitude;
    private String address;
    private String orgid;
    private String name;  //便民点名称
    private String category; //便民点类别:1加油站/2充电站/3维修站

    public ConveniencePoint() {
    }


    public ConveniencePoint(int sid, String latitude, String longitude, String address, String orgid, String name, String category) {
        this.sid = sid;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
        this.orgid = orgid;
        this.name = name;
        this.category = category;
    }


    public int getSid() {
        return sid;
    }

    public void setSid(int sid) {
        this.sid = sid;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getOrgid() {
        return orgid;
    }

    public void setOrgid(String orgid) {
        this.orgid = orgid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public String toString() {
        return "ConveniencePoint{" +
                "sid=" + sid +
                ", latitude='" + latitude + '\'' +
                ", longitude='" + longitude + '\'' +
                ", address='" + address + '\'' +
                ", orgid='" + orgid + '\'' +
                ", name='" + name + '\'' +
                ", category='" + category + '\'' +
                '}';
    }
}
