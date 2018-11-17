package com.xrk.uiac.dal.entity;

import java.util.Date;

import com.xrk.hws.dal.annotations.Column;
import com.xrk.hws.dal.annotations.Table;
import com.xrk.hws.dal.common.DbType;

/**
 * 
 * 验证请求实体类
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：yexx<yexiaoxiao@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年5月20日
 * <br> JDK版本：1.7
 * <br>==========================
 */
@Table(name = "uiac_verify_request")
public class VerifyRequest
{
	@Column(name = "mobile", type = DbType.VARCHAR)
	private String mobile;
	
	@Column(name = "check_type", type = DbType.INT)
	private int checkType;
	
	@Column(name = "request_time", type = DbType.DATETIME)
	private Date requestTime;
	
	@Column(name = "expire_time", type = DbType.DATETIME)
	private Date expireTime;
	
	@Column(name = "verify_status", type = DbType.INT)
	private int verifyStatus;
	
	@Column(name = "verify_time", type = DbType.DATETIME)
	private Date verifyTime;

	public String getMobile()
	{
		return mobile;
	}

	public void setMobile(String mobile)
	{
		this.mobile = mobile;
	}

	public int getCheckType()
	{
		return checkType;
	}

	public void setCheckType(int checkType)
	{
		this.checkType = checkType;
	}

	public Date getRequestTime()
	{
		return requestTime;
	}

	public void setRequestTime(Date requestTime)
	{
		this.requestTime = requestTime;
	}

	public Date getExpireTime()
	{
		return expireTime;
	}

	public void setExpireTime(Date expireTime)
	{
		this.expireTime = expireTime;
	}

	public int getVerifyStatus()
	{
		return verifyStatus;
	}

	public void setVerifyStatus(int verifyStatus)
	{
		this.verifyStatus = verifyStatus;
	}

	public Date getVerifyTime()
	{
		return verifyTime;
	}

	public void setVerifyTime(Date verifyTime)
	{
		this.verifyTime = verifyTime;
	}
}
