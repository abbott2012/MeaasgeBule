package com.guoji.mobile.cocobee.model;

import java.util.Arrays;

/**
 * 扫描扫描tag的类，主要位了解决在获取温度的时候数据分包的问题
 * <p>
 * <p>
 * 帧头 sof : 0x0B
 * 命令码 cmd : 0x74
 * 指令数据长度 len : 0x14
 * 操作码 opt : 0x00 (操作成功)
 * 数据 data
 * 校验 checksum :
 */


public class ScanTempTagMsg {

    private int tagZT;// 帧头  1个字节
    private int tagCmd;// 命令码  1个字节
    private int tagDataLen;// 数据长度 1个字节
    private int tagOpt;// 操作码 1个字节

    private byte[] data; //读到的整个数据包

    private int tagType; // 标签类型 1个字节
    private String tagId;// tagId 6个字节
    private int reservedByte;//保留字节 4个字节
    private String temp; //携带信息(温度)     2个字节
    private int tagStatus; //标签状态 1个字节
    private int tagVersion;  // 标签版本  1个字节
    private int tagRssi;// 标签类型 1个字节

    private int tagCheckSum;// 校验 1个字节

    public ScanTempTagMsg(int tagZT, int tagCmd, int tagDataLen, int tagOpt, byte[] data,
                          int tagType, String tagId, int reservedByte, String temp, int tagStatus,
                          int tagVersion, int tagRssi, int tagCheckSum) {
        this.tagZT = tagZT;
        this.tagCmd = tagCmd;
        this.tagDataLen = tagDataLen;
        this.tagOpt = tagOpt;
        this.data = data;
        this.tagType = tagType;
        this.tagId = tagId;
        this.reservedByte = reservedByte;
        this.temp = temp;
        this.tagStatus = tagStatus;
        this.tagVersion = tagVersion;
        this.tagRssi = tagRssi;
        this.tagCheckSum = tagCheckSum;
    }

    public int getTagZT() {
        return tagZT;
    }

    public void setTagZT(int tagZT) {
        this.tagZT = tagZT;
    }

    public int getTagCmd() {
        return tagCmd;
    }

    public void setTagCmd(int tagCmd) {
        this.tagCmd = tagCmd;
    }

    public int getTagDataLen() {
        return tagDataLen;
    }

    public void setTagDataLen(int tagDataLen) {
        this.tagDataLen = tagDataLen;
    }

    public int getTagOpt() {
        return tagOpt;
    }

    public void setTagOpt(int tagOpt) {
        this.tagOpt = tagOpt;
    }

    public int getTagType() {
        return tagType;
    }

    public void setTagType(int tagType) {
        this.tagType = tagType;
    }

    public String getTagId() {
        return tagId;
    }

    public void setTagId(String tagId) {
        this.tagId = tagId;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public int getReservedByte() {
        return reservedByte;
    }

    public void setReservedByte(int reservedByte) {
        this.reservedByte = reservedByte;
    }

    public String getTemp() {
        return temp;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }

    public int getTagStatus() {
        return tagStatus;
    }

    public void setTagStatus(int tagStatus) {
        this.tagStatus = tagStatus;
    }

    public int getTagVersion() {
        return tagVersion;
    }

    public void setTagVersion(int tagVersion) {
        this.tagVersion = tagVersion;
    }

    public int getTagRssi() {
        return tagRssi;
    }

    public void setTagRssi(int tagRssi) {
        this.tagRssi = tagRssi;
    }

    public int getTagCheckSum() {
        return tagCheckSum;
    }

    public void setTagCheckSum(int tagCheckSum) {
        this.tagCheckSum = tagCheckSum;
    }

    @Override
    public String toString() {
        return "ScanTempTagMsg{" +
                "tagZT=" + tagZT +
                ", tagCmd=" + tagCmd +
                ", tagDataLen=" + tagDataLen +
                ", tagOpt=" + tagOpt +
                ", tagType=" + tagType +
                ", tagId='" + tagId + '\'' +
                ", data=" + Arrays.toString(data) +
                ", reservedByte=" + reservedByte +
                ", temp='" + temp + '\'' +
                ", tagStatus=" + tagStatus +
                ", tagVersion=" + tagVersion +
                ", tagRssi=" + tagRssi +
                ", tagCheckSum=" + tagCheckSum +
                '}';
    }
}
