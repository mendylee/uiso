package com.xrk.uiac.dal.entity;

import java.util.Date;

import com.xrk.hws.dal.annotations.Column;
import com.xrk.hws.dal.annotations.Table;
import com.xrk.hws.dal.common.DbType;

/**
 * 
 * 应用信息实体类
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：yexx<yexiaoxiao@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年5月19日
 * <br> JDK版本：1.7
 * <br>==========================
 */

@Table(name="uiac_app_info")
public class AppInfo
{
	@Column(name="app_id", type=DbType.INT)
	private int appId;
	
	@Column(name="app_name", type=DbType.VARCHAR)
	private String appName;
	
	@Column(name="is_thirdparty", type=DbType.INT)
	private int isThirdparty;
	
	@Column(name="remark", type=DbType.VARCHAR)
	private String remark;
	
	@Column(name="add_date", type=DbType.VARCHAR)
	private Date addDate;
	
	@Column(name="is_del", type=DbType.INT)
	private int isDel;

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