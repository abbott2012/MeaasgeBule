package com.guoji.mobile.cocobee.model;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/1/19.
 */
public class CarScallResponse implements Serializable{
    /**
     * grade : A1  保险档位
     * id : 2 保险配置id
     * life : 1  保险年限
     * max_price : 1200  保险最高保额
     * model : 1500  保险型号
     * service_price : 40  保险服务费用
     *  type  number 保险类型id
     * dname  string 保险类型名称
     */
    private String grade;
    private String id;
    private String life;
    private String max_price;
    private String model;
    private String service_price;
    private long type;
    private String dname;

    public long getType() {
        return type;
    }

    public void setType(long type) {
        this.type = type;
    }

    public String getDname() {
        return dname;
    }

    public void setDname(String dname) {
        this.dname = dname;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLife() {
        return life;
    }

    public void setLife(String life) {
        this.life = life;
    }

    public String getMax_price() {
        return max_price;
    }

    public void setMax_price(String max_price) {
        this.max_price = max_price;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getService_price() {
        return service_price;
    }

    public void setService_price(String service_price) {
        this.service_price = service_price;
    }


}
