package com.guoji.mobile.cocobee.model;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/1/19.
 */
public class OrderInfo implements Serializable{
//    safeconfig_id true number 保险配置表t_safeconfig的id
//    goods_amount true number 保险金额
//    user_id true number 用户id
//    order_type true number 订单类型，0-微信端、1-web端、2-Android端、3-IOS端
//    msg false string 买家留言
//    cno true number 车牌号

    private long safeconfig_id;
    private double goods_amount;
    private long user_id;
    private int order_type;
    private String msg;
    private String cno;
    private String card_id;  //体验安心卡id
    private String target_id; //车辆人员id

    public String getCard_id() {
        return card_id;
    }

    public void setCard_id(String card_id) {
        this.card_id = card_id;
    }

    public String getTarget_id() {
        return target_id;
    }

    public void setTarget_id(String target_id) {
        this.target_id = target_id;
    }

    public String getCno() {
        return cno;
    }

    public void setCno(String cno) {
        this.cno = cno;
    }

    public long getSafeconfig_id() {
        return safeconfig_id;
    }

    public void setSafeconfig_id(long safeconfig_id) {
        this.safeconfig_id = safeconfig_id;
    }

    public double getGoods_amount() {
        return goods_amount;
    }

    public void setGoods_amount(double goods_amount) {
        this.goods_amount = goods_amount;
    }

    public long getUser_id() {
        return user_id;
    }

    public void setUser_id(long user_id) {
        this.user_id = user_id;
    }

    public int getOrder_type() {
        return order_type;
    }

    public void setOrder_type(int order_type) {
        this.order_type = order_type;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

}
