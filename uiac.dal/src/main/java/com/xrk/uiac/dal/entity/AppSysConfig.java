package com.xrk.uiac.dal.entity;

import com.xrk.hws.dal.annotations.Column;
import com.xrk.hws.dal.annotations.Table;
import com.xrk.hws.dal.common.DbType;

/**
 * 
 * AppSysConfig: 系统应用设置
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：zhub<zhubin@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年6月10日
 * <br> JDK版本：1.7
 * <br>==========================
 */
@Table(name = "uiac_app_info_extend")
public class AppSysConfig
{
	@Column(name="serial_id", type = DbType.INT)
	private int serialId;
	@Column(name = "app_id", type = DbType.INT)
	private int appId;
	@Column(name = "prop_key", type = DbType.VARCHAR)
	private String item;
	@Column(name = "prop_value", type = DbType.VARCHAR)
	private String value;
	
	public int getSerialId()
	{
		return serialId;
	}
	public void setSerialId(int serialId)
	{
		this.serialId = serialId;
	}
	public int getAppId()
	{
		return appId;
	}
	public void setAppId(int appId)
	{
		this.appId = appId;
	}
	public String getItem()
	{
		return item;
	}
	public void setItem(String item)
	{
		this.item = item;
	}
	public String getValue()
	{
		return value;
	}
	public void setValue(String value)
	{
		this.value = value;
	}
}
