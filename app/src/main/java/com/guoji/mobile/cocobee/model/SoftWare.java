package com.guoji.mobile.cocobee.model;

import java.io.Serializable;


public class SoftWare implements Serializable {

    private static final long serialVersionUID = -5273445580763617916L;
    /**
     * '主键',
     */
    private String id;
    /**
     * 平台:0表示安卓客户端
     */
    private Integer platform;
    /**
     * SDK版本
     */
    private String sdk_version;
    /**
     * App名称
     */
    private String app_name;
    /**
     * App版本名称
     */
    private String app_version;
    /**
     * App版本号
     */
    private String app_version_id;
    /**
     * 是否升级1升级 0不升级 2强制升级
     */
    private Integer type;
    /**
     * 日期
     */
    private String create_time;
    /**
     * 更新日期
     */
    private String update_time;
    /**
     * 下载apk链接
     */
    private String url;
    /**
     * 安装包大小
     */
    private String size;
    /**
     * 更新日志
     */
    private String update_log;

    private int isdel;

    public SoftWare() {
        super();
    }

    public SoftWare(String id, Integer platform, String sdk_version, String app_name,
                    String app_version, String app_version_id, Integer type,
                    String create_time, String update_time, String url, String size, String update_log,
                    int isdel) {
        super();
        this.platform = platform;
        this.id = id;
        this.sdk_version = sdk_version;
        this.app_name = app_name;
        this.app_version = app_version;
        this.app_version_id = app_version_id;
        this.type = type;
        this.create_time = create_time;
        this.url = url;
        this.size = size;
        this.update_log = update_log;
        this.isdel = isdel;
        this.update_time = update_time;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSdk_version() {
        return sdk_version;
    }

    public void setSdk_version(String sdk_version) {
        this.sdk_version = sdk_version;
    }

    public String getApp_name() {
        return app_name;
    }

    public void setApp_name(String app_name) {
        this.app_name = app_name;
    }

    public String getApp_version() {
        return app_version;
    }

    public void setApp_version(String app_version) {
        this.app_version = app_version;
    }

    public String getApp_version_id() {
        return app_version_id;
    }

    public void setApp_version_id(String app_version_id) {
        this.app_version_id = app_version_id;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getUpdate_log() {
        return update_log;
    }

    public void setUpdate_log(String update_log) {
        this.update_log = update_log;
    }

    public int getIsdel() {
        return isdel;
    }

    public void setIsdel(int isdel) {
        this.isdel = isdel;
    }

    public String getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(String update_time) {
        this.update_time = update_time;
    }

    public Integer getPlatform() {
        return platform;
    }

    public void setPlatform(Integer platform) {
        this.platform = platform;
    }


}