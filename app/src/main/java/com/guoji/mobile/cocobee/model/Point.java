package com.guoji.mobile.cocobee.model;

import java.io.Serializable;

public class Point implements Serializable {
    private String latitude = null;
    private String longitude = null;
    private String address = null;
    private String identitycode = null;
    private String orgid = null;
    private String isconn = null;//0代表不在线，1代表在线
    private String poinstall = null;//0代表未安装，1代表已安装
    private String poid; //点位ID,新增加的

   /* public Object clone() {
        Point o = null;
        try {
            o = (Point) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return o;
    }*/


    public Point() {
    }

    public Point(String latitude, String longitude, String address, String identitycode, String orgid, String isconn, String poinstall, String poid) {

        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
        this.identitycode = identitycode;
        this.orgid = orgid;
        this.isconn = isconn;
        this.poinstall = poinstall;
        this.poid = poid;
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


    public String getIdentitycode() {
        return identitycode;
    }

    public void setIdentitycode(String identitycode) {
        this.identitycode = identitycode;
    }


    public String getOrgid() {
        return orgid;
    }

    public void setOrgid(String orgid) {
        this.orgid = orgid;
    }


    public String getIsconn() {
        return isconn;
    }

    public void setIsconn(String isconn) {
        this.isconn = isconn;
    }

    public String getPoinstall() {
        return poinstall;
    }

    public void setPoinstall(String poinstall) {
        this.poinstall = poinstall;
    }


    public String getPoid() {
        return poid;
    }

    public void setPoid(String poid) {
        this.poid = poid;
    }

    @Override
    public String toString() {
        return "Point{" +
                "latitude='" + latitude + '\'' +
                ", longitude='" + longitude + '\'' +
                ", address='" + address + '\'' +
                ", identitycode='" + identitycode + '\'' +
                ", orgid='" + orgid + '\'' +
                ", isconn='" + isconn + '\'' +
                ", poinstall='" + poinstall + '\'' +
                ", poid='" + poid + '\'' +
                '}';
    }
}
