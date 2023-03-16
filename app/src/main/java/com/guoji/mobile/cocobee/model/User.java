package com.guoji.mobile.cocobee.model;

import java.io.Serializable;

public class User implements Serializable{

	/**
	 * pid true string 人员主键
	 username true string 用户名（默认身份证号）
	 pname true string 姓名
	 mobile true string 电话
	 ptype true string （该返回字段已取消）人员类别：1：车主，2：患者，3：老人，4：学生，5：易走失人员，6：警务人员，7：保安人员，8：协警人员
	 idcard true string 身份证号
	 approleid true string 用户角色ID：1：民警，2：普通用户，3：技术人员，4：合作伙伴，5：超级用户
	 orgids true string 返回用户下面的所有组织机构
	 orgid true string 用户的组织机构号
	 labelid false string （该返回字段已取消）当用户为非车主时返回用户绑定的人员标签号（不是标签id）
	 address true string 现居地址
	 regiaddr true string 户籍地址
	 birthday true string 生日
	 idcardphotourl true string 身份证照片url(两张照片用逗号隔开)
	 photourl true string 用户图像url
	 configsafe true string 为'0'代表没有配置保险相关信息，否则已经配置
	 * */
	private String pid;//用户主键
    private String username; //用户名，用于登录
    private String password; //密码
    private String pname;//用户名字
    private String sex;//性别
    private String birthday;//生日
    private String address;//现居住地
    private String regiaddr;//户籍地址
    private String mobile;//电话
    private String email;//电子邮件
    private String unit;//工作单位
    private String ptype;//人员类别：1：车主，2：标签人员
    private String idcard;//身份证
    private String idcardphotourl;//身份证照片url
    private String urgencyname;//紧急联系人姓名
    private String urgencymobile;//紧急联系人电话
    private String status;//用户状态：0-正常；1-停用；2-试用；3-其它（欠费？）
    private String validdate;//标签有效期，单位：年
    private String labelid;//人员标签ID，关联标签表主键ID
    private String orgid;//组织机构ID，外键，和组织机构表关联
    private String orgname;//组织机构名
    private String createuser;//受理人
    private String createtime;//受理时间
    private String modifyuser;//修改人
    private String modifytime;//修改时间
    private Integer approleid;//用户角色ID：1：民警，2：普通用户，3：技术人员，4：合作伙伴，5：超级用户
    private String remark;//备注
	private String guardian; //监护人身份证号
	private String orgids; //过滤数据用的属性
	private String photourl; //用户头像
	private String configsafe; //为'0'代表没有配置保险相关信息，否则已经配置

	public User(){}

	public String getPhotourl() {
		return photourl;
	}

	public void setPhotourl(String photourl) {
		this.photourl = photourl;
	}

	public User(String pid, String username, String password, String pname, String sex, String birthday, String address, String regiaddr, String mobile, String email, String unit, String ptype, String idcard, String idcardphotourl, String urgencyname, String urgencymobile, String status, String validdate, String labelid, String orgid, String orgname, String createuser, String createtime, String modifyuser, String modifytime, Integer approleid, String remark,String configsafe) {
		this.pid = pid;
		this.username = username;
		this.password = password;
		this.pname = pname;
		this.sex = sex;
		this.birthday = birthday;
		this.address = address;
		this.regiaddr = regiaddr;
		this.mobile = mobile;
		this.email = email;
		this.unit = unit;
		this.ptype = ptype;
		this.idcard = idcard;
		this.idcardphotourl = idcardphotourl;
		this.urgencyname = urgencyname;
		this.urgencymobile = urgencymobile;
		this.status = status;
		this.validdate = validdate;
		this.labelid = labelid;
		this.orgid = orgid;
		this.orgname = orgname;
		this.createuser = createuser;
		this.createtime = createtime;
		this.modifyuser = modifyuser;
		this.modifytime = modifytime;
		this.approleid = approleid;
		this.remark = remark;
		this.configsafe = configsafe;
	}

	public String getConfigsafe() {
		return configsafe;
	}

	public void setConfigsafe(String configsafe) {
		this.configsafe = configsafe;
	}
	public String getPid() {
		return pid;
	}


	public void setPid(String pid) {
		this.pid = pid;
	}


	public String getUsername() {
		return username;
	}


	public void setUsername(String username) {
		this.username = username;
	}


	public String getPassword() {
		return password;
	}


	public void setPassword(String password) {
		this.password = password;
	}


	public String getPname() {
		return pname;
	}


	public void setPname(String pname) {
		this.pname = pname;
	}


	public String getSex() {
		return sex;
	}


	public void setSex(String sex) {
		this.sex = sex;
	}


	public String getBirthday() {
		return birthday;
	}


	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}


	public String getAddress() {
		return address;
	}


	public void setAddress(String address) {
		this.address = address;
	}


	public String getRegiaddr() {
		return regiaddr;
	}


	public void setRegiaddr(String regiaddr) {
		this.regiaddr = regiaddr;
	}


	public String getMobile() {
		return mobile;
	}


	public void setMobile(String mobile) {
		this.mobile = mobile;
	}


	public String getEmail() {
		return email;
	}


	public void setEmail(String email) {
		this.email = email;
	}


	public String getUnit() {
		return unit;
	}


	public void setUnit(String unit) {
		this.unit = unit;
	}


	public String getPtype() {
		return ptype;
	}


	public void setPtype(String ptype) {
		this.ptype = ptype;
	}


	public String getIdcard() {
		return idcard;
	}


	public void setIdcard(String idcard) {
		this.idcard = idcard;
	}


	public String getIdcardphotourl() {
		return idcardphotourl;
	}


	public void setIdcardphotourl(String idcardphotourl) {
		this.idcardphotourl = idcardphotourl;
	}


	public String getUrgencyname() {
		return urgencyname;
	}


	public void setUrgencyname(String urgencyname) {
		this.urgencyname = urgencyname;
	}


	public String getUrgencymobile() {
		return urgencymobile;
	}


	public void setUrgencymobile(String urgencymobile) {
		this.urgencymobile = urgencymobile;
	}


	public String getStatus() {
		return status;
	}


	public void setStatus(String status) {
		this.status = status;
	}


	public String getValiddate() {
		return validdate;
	}


	public void setValiddate(String validdate) {
		this.validdate = validdate;
	}


	public String getLabelid() {
		return labelid;
	}


	public void setLabelid(String labelid) {
		this.labelid = labelid;
	}


	public String getOrgid() {
		return orgid;
	}


	public void setOrgid(String orgid) {
		this.orgid = orgid;
	}


	public String getOrgname() {
		return orgname;
	}


	public void setOrgname(String orgname) {
		this.orgname = orgname;
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


	public String getModifyuser() {
		return modifyuser;
	}


	public void setModifyuser(String modifyuser) {
		this.modifyuser = modifyuser;
	}


	public String getModifytime() {
		return modifytime;
	}


	public void setModifytime(String modifytime) {
		this.modifytime = modifytime;
	}


	public Integer getApproleid() {
		return approleid;
	}


	public void setApproleid(Integer approleid) {
		this.approleid = approleid;
	}


	public String getRemark() {
		return remark;
	}


	public void setRemark(String remark) {
		this.remark = remark;
	}


	public String getGuardian() {
		return guardian;
	}

	public void setGuardian(String guardian) {
		this.guardian = guardian;
	}


	public String getOrgids() {
		return orgids;
	}

	public void setOrgids(String orgids) {
		this.orgids = orgids;
	}

	@Override
    public String toString() {
        return "{" +
                "pid='" + pid + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", pname=" + pname +
                ", sex='" + sex + '\'' +
                ", email='" + email + '\'' +
                ", birthday='" + birthday + '\'' +
                ", address='" + address + '\'' +
                ", regiaddr='" + regiaddr + '\'' +
                ", mobile='" + mobile + '\'' +
                ", email='" + email + '\'' +
                ", unit='" + unit + '\'' +
                ", ptype='" + ptype + '\'' +
                ", idcard='" + idcard + '\'' +
                ", idcardphotourl='" + idcardphotourl + '\'' +
                ", urgencyname=" + urgencyname +
                ", urgencymobile='" + urgencymobile + '\'' +
                ", status='" + status + '\'' +
                ", labelid='" + labelid + '\'' +
                ", orgid='" + orgid + '\'' +
                ", orgname='" + orgname + '\'' +
                ", createuser='" + createuser + '\'' +
                ", createtime='" + createtime + '\'' +
                ", modifyuser='" + modifyuser + '\'' +
                ", modifytime='" + modifytime + '\'' +
                ", approleid='" + approleid + '\'' +
                ", remark='" + remark + '\'' +
                ", configsafe='" + configsafe + '\'' +
                '}';
    }
}
