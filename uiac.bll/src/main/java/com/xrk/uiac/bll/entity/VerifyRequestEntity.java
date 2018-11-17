package com.xrk.uiac.bll.entity;

import java.util.Date;

/**
 * 
 * 验证结果缓存实体
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：yexx<yexiaoxiao@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年7月30日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public class VerifyRequestEntity
{
	private String mobile;
	
	private int checkType;
	
	private Date requestTime;
	
	private Date expireTime;

	private int verifyStatus;

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
