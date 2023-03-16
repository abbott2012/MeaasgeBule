package com.guoji.mobile.cocobee.model;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/1/19.
 */
public class OrderInfoResponse implements Serializable{
    /**
     * cid : 56022
     * cno : 粤1
     * error_msg :
     * finishtime : 2017-05-04 14:30:58
     * goods_amount : 40.00
     * grade : A1
     * id : 6
     * life : 1
     * max_price : 1200
     * model : 1500
     * msg : null
     * order_id : 560252017050414314324383
     * order_status : 待付款
     * order_status_value : 10
     * payname :
     * paytime :
     * pname : 酗斌
     * refund_returnlog :
     * service_price : 40
     * user_id : 56025
     */

    private String cid;
    private String cno;
    private String error_msg;
    private String finishtime;
    private String goods_amount;
    private String grade;
    private String id;
    private String life;
    private String max_price;
    private String model;
    private String msg;
    private String order_id;
    private String order_status;
    private String order_status_value;
    private String payname;
    private String paytime;
    private String pname;
    private String refund_returnlog;
    private String service_price;
    private String user_id;

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getCno() {
        return cno;
    }

    public void setCno(String cno) {
        this.cno = cno;
    }

    public String getError_msg() {
        return error_msg;
    }

    public void setError_msg(String error_msg) {
        this.error_msg = error_msg;
    }

    public String getFinishtime() {
        return finishtime;
    }

    public void setFinishtime(String finishtime) {
        this.finishtime = finishtime;
    }

    public String getGoods_amount() {
        return goods_amount;
    }

    public void setGoods_amount(String goods_amount) {
        this.goods_amount = goods_amount;
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

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getOrder_status() {
        return order_status;
    }

    public void setOrder_status(String order_status) {
        this.order_status = order_status;
    }

    public String getOrder_status_value() {
        return order_status_value;
    }

    public void setOrder_status_value(String order_status_value) {
        this.order_status_value = order_status_value;
    }

    public String getPayname() {
        return payname;
    }

    public void setPayname(String payname) {
        this.payname = payname;
    }

    public String getPaytime() {
        return paytime;
    }

    public void setPaytime(String paytime) {
        this.paytime = paytime;
    }

    public String getPname() {
        return pname;
    }

    public void setPname(String pname) {
        this.pname = pname;
    }

    public String getRefund_returnlog() {
        return refund_returnlog;
    }

    public void setRefund_returnlog(String refund_returnlog) {
        this.refund_returnlog = refund_returnlog;
    }

    public String getService_price() {
        return service_price;
    }

    public void setService_price(String service_price) {
        this.service_price = service_price;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }


}
