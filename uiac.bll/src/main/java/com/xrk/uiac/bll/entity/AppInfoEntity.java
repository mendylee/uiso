package com.xrk.uiac.bll.entity;

import java.util.Date;

/**
 * 
 * 应用信息缓存实体
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：yexx<yexiaoxiao@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年5月20日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public class AppInfoEntity
{
	private int appId;
	private String appName;
	private int isThirdparty;
	private String remark;
	private Date addDate;
	private int isDel;
	
	public AppInfoEntity()
	{
		appId = 0;
		appName = "";
		isThirdparty = 0;
		remark = "";
		addDate = new Date();
		isDel = 0;
	}
	public int getAppId()
	{
		return appId;
	}
	public void setAppId(int appId)
	{
		this.appId = appId;
	}
	public String getAppName()
	{
		return appName;
	}
	public void setAppName(String appName)
	{
		this.appName = appName;
	}
	public int getIsThirdparty()
	{
		return isThirdparty;
	}
	public void setIsThirdparty(int isThirdparty)
	{
		this.isThirdparty = isThirdparty;
	}
	public String getRemark()
	{
		return remark;
	}
	public void setRemark(String remark)
	{
		this.remark = remark;
	}
	public Date getAddDate()
	{
		return addDate;
	}
	public void setAddDate(Date addDate)
	{
		this.addDate = addDate;
	}
	public int getIsDel()
	{
		return isDel;
	}
	public void setIsDel(int isDel)
	{
		this.isDel = isDel;
	}
}
