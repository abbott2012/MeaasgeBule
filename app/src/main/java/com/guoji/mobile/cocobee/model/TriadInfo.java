package com.guoji.mobile.cocobee.model;

/**
 * 三元组信息
 */
public class TriadInfo {

    private String ProductKey;
    private String DeviceName;
    private String DeviceSecret;

    public String getProductKey() {
        return ProductKey;
    }

    public void setProductKey(String productKey) {
        ProductKey = productKey;
    }

    public String getDeviceName() {
        return DeviceName;
    }

    public void setDeviceName(String deviceName) {
        DeviceName = deviceName;
    }

    public String getDeviceSecret() {
        return DeviceSecret;
    }

    public void setDeviceSecret(String deviceSecret) {
        DeviceSecret = deviceSecret;
    }

    @Override
    public String toString() {
        return "{" +
                "ProductKey='" + ProductKey + '\'' +
                ", DeviceName='" + DeviceName + '\'' +
                ", DeviceSecret='" + DeviceSecret + '\'' +
                '}';
    }
}
