package com.guoji.mobile.cocobee.model;

/**
 * Created by marktrace on 16/11/1.
 */
public class TagSearch {

    private String tagId;

    private String lowVotageWarn;

    private int rssi;

    private int position;

    private int totalTagNum;

    private int angle; //角度

    private float lengthFactor; //半径长度系数

    private long time; //读取时间

    private String desc; //资产描述
    private String isRead; //盘点情况
    private String readTime; //盘点时间


    public boolean equals(Object obj) {
        if (obj instanceof TagSearch) {
            TagSearch u = (TagSearch) obj;
            return this.tagId.equals(u.tagId);
        }
        return super.equals(obj);

    }

    public TagSearch() {
    }

    public TagSearch(String tagId, int rssi, int position, int totalTagNum) {
        this.tagId = tagId;
        this.rssi = rssi;
        this.position = position;
        this.totalTagNum = totalTagNum;
    }

    public TagSearch(String tagId, String lowVotageWarn, int rssi, int position) {
        this.tagId = tagId;
        this.lowVotageWarn = lowVotageWarn;
        this.rssi = rssi;
        this.position = position;
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

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getIsRead() {
        return isRead;
    }

    public void setIsRead(String isRead) {
        this.isRead = isRead;
    }

    public String getReadTime() {
        return readTime;
    }

    public void setReadTime(String readTime) {
        this.readTime = readTime;
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
                '}';
    }
}
