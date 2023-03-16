package com.guoji.mobile.cocobee.model;

/**
 * Created by marktrace on 16/11/1.
 */
public class Tag {

    private String tagId;

    private String lowVotageWarn;

    private int rssi;

    private int position;

    private int totalTagNum;

    private int angle; //角度

    private float lengthFactor; //半径长度系数

    private boolean isNetWorkTag = false;

    private String flag;//1代表车辆标签信息 2代表人员标签信息

    private String cno; //车牌号
    private long time; //读取时间
    private double temp; //标签温度

    

    public boolean equals(Object obj) {
        if (obj instanceof Tag) {
            Tag u = (Tag) obj;
            return this.tagId.equals(u.tagId);
        }
        return super.equals(obj);

    }

    public Tag() {
    }

    public Tag(String tagId, int rssi, int position,int totalTagNum) {
        this.tagId = tagId;
        this.rssi = rssi;
        this.position = position;
        this.totalTagNum = totalTagNum;
    }

    public Tag(String tagId, String lowVotageWarn, int rssi, int position) {
        this.tagId = tagId;
        this.lowVotageWarn = lowVotageWarn;
        this.rssi = rssi;
        this.position = position;
    }

    public double getTemp() {
        return temp;
    }

    public void setTemp(double temp) {
        this.temp = temp;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getTagId() {
        return tagId;
    }

    public void setTagId(String tagId) {
        this.tagId = tagId;
    }

    public String getLowVotageWarn() {
        return lowVotageWarn;
    }

    public void setLowVotageWarn(String lowVotageWarn) {
        this.lowVotageWarn = lowVotageWarn;
    }

    public int getRssi() {
        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }


    public int getTotalTagNum() {
        return totalTagNum;
    }

    public void setTotalTagNum(int totalTagNum) {
        this.totalTagNum = totalTagNum;
    }


    public int getAngle() {
        return angle;
    }

    public void setAngle(int angle) {
        this.angle = angle;
    }

    public float getLengthFactor() {
        return lengthFactor;
    }

    public void setLengthFactor(float lengthFactor) {
        this.lengthFactor = lengthFactor;
    }


    public boolean isNetWorkTag() {
        return isNetWorkTag;
    }

    public void setIsNetWorkTag(boolean isNetWorkTag) {
        this.isNetWorkTag = isNetWorkTag;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }


    public String getCno() {
        return cno;
    }

    public void setCno(String cno) {
        this.cno = cno;
    }

    @Override
    public String toString() {
        return "Tag{" +
                "tagId='" + tagId + '\'' +
                ", lowVotageWarn='" + lowVotageWarn + '\'' +
                ", rssi=" + rssi +
                ", position=" + position +
                ", totalTagNum=" + totalTagNum +
                ", angle=" + angle +
                ", lengthFactor=" + lengthFactor +
                ", isNetWorkTag=" + isNetWorkTag +
                ", flag='" + flag + '\'' +
                ", cno='" + cno + '\'' +
                ", time=" + time +
                ", temp='" + temp + '\'' +
                '}';
    }
}
