package com.guoji.mobile.cocobee.model;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/10/27.
 */
public class Collector implements Serializable {

    private String collectorId;
    private String ip;
    private String port;
    private String latitude;
    private String longitude;
    private String address;
    private String imei;
    private String number;
    private String identitycode; //110快速识别编码
    private String orgid;
    private String ip1;
    private String ip2;
    private String ip3;
    private String ip4;
    private String librepeat;

    public String getLibrepeat() {
        return librepeat;
    }

    public void setLibrepeat(String librepeat) {
        this.librepeat = librepeat;
    }

    public String getIp1() {
        return ip1;
    }

    public void setIp1(String ip1) {
        this.ip1 = ip1;
    }

    public String getIp2() {
        return ip2;
    }

    public void setIp2(String ip2) {
        this.ip2 = ip2;
    }

    public String getIp3() {
        return ip3;
    }

    public void setIp3(String ip3) {
        this.ip3 = ip3;
    }

    public String getIp4() {
        return ip4;
    }

    public void setIp4(String ip4) {
        this.ip4 = ip4;
    }

    public Collector() {
    }

    public Collector(String collectorId, String ip, String port, String latitude, String longitude, String address, String imei, String number, String orgid, String ip1, String ip2, String ip3, String ip4,String librepeat) {
        this.collectorId = collectorId;
        this.ip = ip;
        this.port = port;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
        this.imei = imei;
        this.number = number;
        this.orgid = orgid;
        this.ip1 = ip1;
        this.ip2 = ip2;
        this.ip3 = ip3;
        this.ip4 = ip4;
        this.librepeat = librepeat;
    }

    public String getIdentitycode() {
        return identitycode;
    }

    public void setIdentitycode(String identitycode) {
        this.identitycode = identitycode;
    }

    public String getCollectorId() {
        return collectorId;
    }

    public void setCollectorId(String collectorId) {
        this.collectorId = collectorId;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
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

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getOrgid() {
        return orgid;
    }

    public void setOrgid(String orgid) {
        this.orgid = orgid;
    }
}
