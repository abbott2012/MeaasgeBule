package com.guoji.mobile.cocobee.model;

/**
 * Created by Administrator on 2016/10/26.
 */
public class Location { //轨迹模型
    private int id;
    private String lng;
    private String lat;
    private String pic_url;
    private String building_name;
    private String EQNO;
    private int bid;
    private String address;
    private String flag;


    public String getFlag() {
        return flag;
    }
    public void setFlag(String flag) {
        this.flag = flag;
    }
    public String getEQNO() {
        return EQNO;
    }
    public void setEQNO(String EQNO) {
        this.EQNO = EQNO;
    }
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public int getBid() {
        return bid;
    }
    public void setBid(int bid) {
        this.bid = bid;
    }
    public String getLng() {
        return lng;
    }
    public void setLng(String lng) {
        this.lng = lng;
    }
    public String getLat() {
        return lat;
    }
    public void setLat(String lat) {
        this.lat = lat;
    }
    public String getPic_url() {
        return pic_url;
    }
    public void setPic_url(String pic_url) {
        this.pic_url = pic_url;
    }
    public String getBuilding_name() {
        return building_name;
    }
    public void setBuilding_name(String building_name) {
        this.building_name = building_name;
    }


    public boolean equals(Object obj) {
        if (obj instanceof Location) {
            Location u = (Location) obj;
            return this.lat.equals(u.lat) && this.lng.equals(u.lng);
        }
        return super.equals(obj);

    }

}
