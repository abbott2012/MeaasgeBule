package com.guoji.mobile.cocobee.common;

public class JsonResult{

	/**
	 * 返回状态是否成功
	 */
	private boolean flag = false;

	/**
	 * 提示信息
	 */
	private String message;

	/**
	 * 请求返回的json结果
	 */
	private String result;


	/**
	 * 状态码
	 */
	private int statusCode;

	//头像上传返回的url
	private String url;

	/**
	 * 查人
	 */
	private String persons;

	/**
	 * 查车
	 */
	private String cars;

	public JsonResult() {

	}

	public String getPersons() {
		return persons;
	}

	public void setPersons(String persons) {
		this.persons = persons;
	}

	public String getCars() {
		return cars;
	}

	public void setCars(String cars) {
		this.cars = cars;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public JsonResult(boolean flag, String message, String result, int statusCode) {
		this.flag = flag;
		this.message = message;
		this.result = result;
		this.statusCode = statusCode;
	}

	public boolean isFlag() {
		return flag;
	}

	public void setFlag(boolean flag) {
		this.flag = flag;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	@Override
	public String toString() {
		return "JsonResult{" +
				"flag=" + flag +
				", message='" + message + '\'' +
				", result='" + result + '\'' +
				", statusCode=" + statusCode +
				'}';
	}
}
