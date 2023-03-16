package com.guoji.mobile.cocobee.model;

/**
 * Created by marktrace on 16/10/28.
 */
public class AlarmInfo {

    private String logid;
    private String carOwnerName; //车主名称
    private String carOwnerId; //车主身份证
    private String carOwnerPhone; //车主电话
    private String deviceNum; //设备编号
    private String alarmAddress; //报警地点
    private String alarmTime; //报警时间
    private String alarmStatus; //报警状态
    private String atype; //报警类型：车辆报警，人员报警
    private String alarmlng;
    private String alarmlat;
    private String cno; //车牌号
    private String cid;


    public AlarmInfo() {
    }

    public AlarmInfo(String carOwnerName, String carOwnerId, String carOwnerPhone, String deviceNum, String alarmAddress, String alarmTime, String alarmStatus) {
        this.carOwnerName = carOwnerName;
        this.carOwnerId = carOwnerId;
        this.carOwnerPhone = carOwnerPhone;
        this.deviceNum = deviceNum;
        this.alarmAddress = alarmAddress;
        this.alarmTime = alarmTime;
        this.alarmStatus = alarmStatus;
    }

    public String getCarOwnerName() {
        return carOwnerName;
    }

    public void setCarOwnerName(String carOwnerName) {
        this.carOwnerName = carOwnerName;
    }

    public String getCarOwnerId() {
        return carOwnerId;
    }

    public void setCarOwnerId(String carOwnerId) {
        this.carOwnerId = carOwnerId;
    }

    public String getCarOwnerPhone() {
        return carOwnerPhone;
    }

    public void setCarOwnerPhone(String carOwnerPhone) {
        this.carOwnerPhone = carOwnerPhone;
    }

    public String getDeviceNum() {
        return deviceNum;
    }

    public void setDeviceNum(String deviceNum) {
        this.deviceNum = deviceNum;
    }

    public String getAlarmAddress() {
        return alarmAddress;
    }

    public void setAlarmAddress(String alarmAddress) {
        this.alarmAddress = alarmAddress;
    }

    public String getAlarmTime() {
        return alarmTime;
    }

    public void setAlarmTime(String alarmTime) {
        this.alarmTime = alarmTime;
    }

    public String getAlarmStatus() {
        return alarmStatus;
    }

    public void setAlarmStatus(String alarmStatus) {
        this.alarmStatus = alarmStatus;
    }

    public String getAtype() {
        return atype;
    }

    public void setAtype(String atype) {
        this.atype = atype;
    }

    public String getAlarmlng() {
        return alarmlng;
    }

    public void setAlarmlng(String alarmlng) {
        this.alarmlng = alarmlng;
    }

    public String getAlarmlat() {
        return alarmlat;
    }

    public void setAlarmlat(String alarmlat) {
        this.alarmlat = alarmlat;
    }

    public String getCno() {
        return cno;
    }

    public void setCno(String cno) {
        this.cno = cno;
    }

    public String getLogid() {
        return logid;
    }

    public void setLogid(String logid) {
        this.logid = logid;
    }


    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }


    @Override
    public String toString() {
        return "AlarmInfo{" +
                "logid='" + logid + '\'' +
                ", carOwnerName='" + carOwnerName + '\'' +
                ", carOwnerId='" + carOwnerId + '\'' +
                ", carOwnerPhone='" + carOwnerPhone + '\'' +
                ", deviceNum='" + deviceNum + '\'' +
                ", alarmAddress='" + alarmAddress + '\'' +
                ", alarmTime='" + alarmTime + '\'' +
                ", alarmStatus='" + alarmStatus + '\'' +
                ", atype='" + atype + '\'' +
                ", alarmlng='" + alarmlng + '\'' +
                ", alarmlat='" + alarmlat + '\'' +
                ", cno='" + cno + '\'' +
                ", cid='" + cid + '\'' +
                '}';
    }
}
