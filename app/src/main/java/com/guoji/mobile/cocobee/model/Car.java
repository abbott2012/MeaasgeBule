package com.guoji.mobile.cocobee.model;

import java.io.Serializable;

public class Car implements Serializable{

    private String cid; //车辆ID，主键

    private String cframe; //车架号码

    private String ctype; //车辆类别：0：电动车，1：摩托车

    private String cno; //车牌号码

    private String ccolor; //车辆颜色

    private String islocked;// 是否锁车：0：是，1：否
    
    private String islosted;// 是否丢失：0：是，1：否

    private String isalarm; //是否报警：0：是，1：否

//    private String isinsurance;// 是否购买保险：0：是，1：否

    private String cbuyprice; //购买价格

    private String cbuytime; //购买时间

    private String cbuytype; //品牌类型

    private String cdevice; //发动机号
    
    private String ccarpicurl;//车辆照片

    private String cnopicurl; //车牌照片

    private String pid; //人员ID，关联人员表主键id，一个人可以绑定多辆车

    private String labelid; //车辆绑定标签ID，关联标签信息表主键

    private String status; //车辆状态：0-正常；1-停用；2-试用；3-其它（欠费？）

    private String createuser; //受理人
    
    private String createtime;//受理时间

    private String remark; //备注

	private String orgid;//组织架构
	private String order_id;//保单号
	private String uIdCard;//车主身份证号
	private String uName;//车主姓名
	private String uSex;//车主性别
	private String uBirthday;//车主生日
	private String uFirstAddress;//车主出生地
	private String labelNum;//标签号

	public String getLabelNum() {
		return labelNum;
	}

	public void setLabelNum(String labelNum) {
		this.labelNum = labelNum;
	}

	public String getuIdCard() {
		return uIdCard;
	}

	public void setuIdCard(String uIdCard) {
		this.uIdCard = uIdCard;
	}

	public String getuName() {
		return uName;
	}

	public void setuName(String uName) {
		this.uName = uName;
	}

	public String getuSex() {
		return uSex;
	}

	public void setuSex(String uSex) {
		this.uSex = uSex;
	}

	public String getuBirthday() {
		return uBirthday;
	}

	public void setuBirthday(String uBirthday) {
		this.uBirthday = uBirthday;
	}

	public String getuFirstAddress() {
		return uFirstAddress;
	}

	public void setuFirstAddress(String uFirstAddress) {
		this.uFirstAddress = uFirstAddress;
	}

	public String getOrder_id() {
		return order_id;
	}

	public void setOrder_id(String order_id) {
		this.order_id = order_id;
	}

	public Car() {
    }

	public String getOrgid() {
		return orgid;
	}

	public void setOrgid(String orgid) {
		this.orgid = orgid;
	}

	public String getCid() {
		return cid;
	}

	public void setCid(String cid) {
		this.cid = cid;
	}

	public String getCframe() {
		return cframe;
	}

	public void setCframe(String cframe) {
		this.cframe = cframe;
	}

	public String getCtype() {
		return ctype;
	}

	public void setCtype(String ctype) {
		this.ctype = ctype;
	}

	public String getCno() {
		return cno;
	}

	public void setCno(String cno) {
		this.cno = cno;
	}

	public String getCcolor() {
		return ccolor;
	}

	public void setCcolor(String ccolor) {
		this.ccolor = ccolor;
	}

	public String getIslocked() {
		return islocked;
	}

	public void setIslocked(String islocked) {
		this.islocked = islocked;
	}

	public String getIslosted() {
		return islosted;
	}

	public void setIslosted(String islosted) {
		this.islosted = islosted;
	}

	public String getIsalarm() {
		return isalarm;
	}

	public void setIsalarm(String isalarm) {
		this.isalarm = isalarm;
	}


	public String getCbuyprice() {
		return cbuyprice;
	}

	public void setCbuyprice(String cbuyprice) {
		this.cbuyprice = cbuyprice;
	}

	public String getCbuytime() {
		return cbuytime;
	}

	public void setCbuytime(String cbuytime) {
		this.cbuytime = cbuytime;
	}

	public String getCbuytype() {
		return cbuytype;
	}

	public void setCbuytype(String cbuytype) {
		this.cbuytype = cbuytype;
	}

	public String getCdevice() {
		return cdevice;
	}

	public void setCdevice(String cdevice) {
		this.cdevice = cdevice;
	}

	public String getCcarpicurl() {
		return ccarpicurl;
	}

	public void setCcarpicurl(String ccarpicurl) {
		this.ccarpicurl = ccarpicurl;
	}

	public String getCnopicurl() {
		return cnopicurl;
	}

	public void setCnopicurl(String cnopicurl) {
		this.cnopicurl = cnopicurl;
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public String getLabelid() {
		return labelid;
	}

	public void setLabelid(String labelid) {
		this.labelid = labelid;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getCreateuser() {
		return createuser;
	}

	public void setCreateuser(String createuser) {
		this.createuser = createuser;
	}

	public String getCreatetime() {
		return createtime;
	}

	public void setCreatetime(String createtime) {
		this.createtime = createtime;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	@Override
    public String toString() {
        return "{" +
                "cid='" + cid + '\'' +
                ", cframe='" + cframe + '\'' +
                ", ctype='" + ctype + '\'' +
                ", cno='" + cno + '\'' +
                ", ccolor='" + ccolor + '\'' +
                ", islocked='" + islocked + '\'' +
                ", islosted='" + islosted + '\'' +
                ", isalarm='" + isalarm + '\'' +
                ", order_id='" + order_id + '\'' +
                ", cbuyprice='" + cbuyprice + '\'' +
                ", cbuytime='" + cbuytime + '\'' +
                ", cbuytype='" + cbuytype + '\'' +
                ", cdevice='" + cdevice + '\'' +
                ", ccarpicurl='" + ccarpicurl + '\'' +
                ", cnopicurl='" + cnopicurl + '\'' +
                ", pid='" + pid + '\'' +
                ", labelid='" + labelid + '\'' +
                ", status='" + status + '\'' +
                ", createuser='" + createuser + '\'' +
                ", createtime='" + createtime + '\'' +
                ", remark='" + remark + '\'' +
                '}';
    }
}

