package com.guoji.mobile.cocobee.model;

import java.io.Serializable;
import java.util.List;

public class OfflineMapCityBean implements Serializable{
	private String cityName;
	private int cityCode;
	/**
	 * 下载的进度
	 */
	private int progress;
    private List<OfflineMapCityBean> childCities;

    public List<OfflineMapCityBean> getChildCities() {
        return childCities;
    }

    public void setChildCities(List<OfflineMapCityBean> childCities) {
        this.childCities = childCities;
    }

    private Flag flag = Flag.NO_STATUS;
	/**
	 * 下载的状态：无状态，暂停，正在下载
	 * @author zhy
	 *
	 */
	public enum Flag
	{
		NO_STATUS,PAUSE,DOWNLOADING
	}

	public Flag getFlag()
	{
		return flag;
	}

	public void setFlag(Flag flag)
	{
		this.flag = flag;
	}

	public OfflineMapCityBean()
	{
	}

	public OfflineMapCityBean(String cityName, int cityCode, int progress)
	{
		this.cityName = cityName;
		this.cityCode = cityCode;
		this.progress = progress;
	}

	public String getCityName()
	{
		return cityName;
	}

	public void setCityName(String cityName)
	{
		this.cityName = cityName;
	}

	public int getCityCode()
	{
		return cityCode;
	}

	public void setCityCode(int cityCode)
	{
		this.cityCode = cityCode;
	}

	public int getProgress()
	{
		return progress;
	}

	public void setProgress(int progress)
	{
		this.progress = progress;
	}

}
