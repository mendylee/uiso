package com.xrk.uiac.dal.entity;

import java.util.Date;

import com.xrk.hws.dal.annotations.Column;
import com.xrk.hws.dal.annotations.Index;
import com.xrk.hws.dal.annotations.Indexes;
import com.xrk.hws.dal.annotations.Table;
import com.xrk.hws.dal.common.DbType;

/**
 * 
 * PushObserver: 授权观察者实体类
 *
 * <br>=
 * ========================= <br>
 * 公司：广州向日葵信息科技有限公司 <br>
 * 开发：zhub<zhubin@xiangrikui.com> <br>
 * 版本：1.0 <br>
 * 创建时间：2015年5月15日 <br>
 * JDK版本：1.7 <br>=
 * =========================
 */
@Table(name = "uiac_push_observer")
@Indexes(value = { @Index(name = "app_id", value = "app_id", unique = true) })
public class PushObserver
{
	@Column(name = "serial_id", type = DbType.BIGINT)
	private long serial_id;
	@Column(name = "app_id", type = DbType.INT)
	private int appId;
	@Column(name = "callback_url", type = DbType.VARCHAR)
	private String callBackUrl;
	@Column(name = "add_date", type = DbType.DATETIME)
	private Date addDate;

	
	public long getSerial_id()
	{
		return serial_id;
	}

	public void setSerial_id(long serial_id)
	{
		this.serial_id = serial_id;
	}

	

	public int getAppId()
	{
		return appId;
	}

	public void setAppId(int appId)
	{
		this.appId = appId;
	}

	public String getCallBackUrl()
	{
		return callBackUrl;
	}

	public void setCallBackUrl(String callBackUrl)
	{
		this.callBackUrl = callBackUrl;
	}

	public Date getAddDate()
	{
		return addDate;
	}

	public void setAddDate(Date addDate)
	{
		this.addDate = addDate;
	}

}
