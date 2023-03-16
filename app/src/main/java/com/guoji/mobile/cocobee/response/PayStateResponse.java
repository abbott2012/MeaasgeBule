package com.guoji.mobile.cocobee.response;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/5/19.
 * 支付状态参数类
 */

public class PayStateResponse implements Serializable{
    //        order_status true number 订单状态，0-已取消 10-待付款 20-已付款 30-已失效
//        pay_msg true string 支付留言
//        payment_id true string 支付方式id
//        user_id true string 用户id

    private int  order_status;
    private String pay_msg;
    private String payment_id;
    private String user_id;

    public int getOrder_status() {
        return order_status;
    }

    public void setOrder_status(int order_status) {
        this.order_status = order_status;
    }

    public String getPay_msg() {
        return pay_msg;
    }

    public void setPay_msg(String pay_msg) {
        this.pay_msg = pay_msg;
    }

    public String getPayment_id() {
        return payment_id;
    }

    public void setPayment_id(String payment_id) {
        this.payment_id = payment_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
}
