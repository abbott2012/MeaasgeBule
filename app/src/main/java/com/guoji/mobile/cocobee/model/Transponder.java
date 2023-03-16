package com.guoji.mobile.cocobee.model;

public class Transponder {



    private String device_id;
    private String ip;
    private String port;
    private byte[] data; //读到的整个数据包
    private String version; //设备版本号
    private String time; //时间
    private String[] deviceGrains = new String[4];
    private int[] deviceRssis = new int[4];
    private int beepFlag;
    private int dhcpFlag;
    private int drctFlag;

    private int filterNum;
    private int stopTimeNum;//停留时间
    private int drctTimeNum;//方向过滤时间

    private int netState;

    private String lanIp;
    private String lanPort;
    private String lanZwym;
    private String lanWg;
    private String lanFwIp;
    private String lanFwPort;

    public Transponder(String device_id, String ip, String port, byte[] data, String version, String time, String[] deviceGrains, int[] deviceRssis, int beepFlag,int dhcpFlag ,int drctFlag,int filterNum, int netState,
                       String lanIp, String lanPort, String lanZwym, String lanWg, String lanFwIp, String lanFwPort, int stopTimeNum, int drctTimeNum) {
        this.device_id = device_id;
        this.ip = ip;
        this.port = port;
        this.data = data;
        this.version = version;
        this.time = time;
        this.deviceGrains = deviceGrains; //增域
        this.deviceRssis = deviceRssis;
        this.beepFlag = beepFlag;
        this.dhcpFlag = dhcpFlag;
        this.filterNum = filterNum;
        this.stopTimeNum = stopTimeNum;
        this.netState = netState;
        this.lanIp = lanIp;
        this.lanPort = lanPort;
        this.lanZwym = lanZwym;
        this.lanWg = lanWg;
        this.lanFwIp = lanFwIp;
        this.lanFwPort = lanFwPort;
        this.drctTimeNum = drctTimeNum;
        this.drctFlag = drctFlag;

    }

    public int getDrctFlag() {
        return drctFlag;
    }

    public void setDrctFlag(int drctFlag) {
        this.drctFlag = drctFlag;
    }

    public int getDrctTimeNum() {
        return drctTimeNum;
    }

    public void setDrctTimeNum(int drctTimeNum) {
        this.drctTimeNum = drctTimeNum;
    }

    public int getStopTimeNum() {
        return stopTimeNum;
    }

    public void setStopTimeNum(int stopTimeNum) {
        this.stopTimeNum = stopTimeNum;
    }

    public String getDevice_id() {
        return device_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
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

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }


    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String[] getDeviceGrains() {
        return deviceGrains;
    }

    public void setDeviceGrains(String[] deviceGrains) {
        this.deviceGrains = deviceGrains;
    }

    public int[] getDeviceRssis() {
        return deviceRssis;
    }

    public void setDeviceRssis(int[] deviceRssis) {
        this.deviceRssis = deviceRssis;
    }


    public int getBeepFlag() {
        return beepFlag;
    }

    public void setBeepFlag(int beepFlag) {
        this.beepFlag = beepFlag;
    }

    public int getDhcpFlag() {
        return dhcpFlag;
    }

    public void setDhcpFlag(int dhcpFlag) {
        this.dhcpFlag = dhcpFlag;
    }

    public int getFilterNum() {
        return filterNum;
    }

    public void setFilterNum(int filterNum) {
        this.filterNum = filterNum;
    }

    public int getNetState() {
        return netState;
    }

    public void setNetState(int netState) {
        this.netState = netState;
    }

    public String getLanIp() {
        return lanIp;
    }

    public void setLanIp(String lanIp) {
        this.lanIp = lanIp;
    }

    public String getLanPort() {
        return lanPort;
    }

    public void setLanPort(String lanPort) {
        this.lanPort = lanPort;
    }

    public String getLanZwym() {
        return lanZwym;
    }

    public void setLanZwym(String lanZwym) {
        this.lanZwym = lanZwym;
    }

    public String getLanWg() {
        return lanWg;
    }

    public void setLanWg(String lanWg) {
        this.lanWg = lanWg;
    }

    public String getLanFwIp() {
        return lanFwIp;
    }

    public void setLanFwIp(String lanFwIp) {
        this.lanFwIp = lanFwIp;
    }

    public String getLanFwPort() {
        return lanFwPort;
    }

    public void setLanFwPort(String lanFwPort) {
        this.lanFwPort = lanFwPort;
    }
}
