package com.xrk.uiac.bll.entity;

import java.util.Date;

/**
 * 
 * UserInfoEntity: 用户基本信息缓存实体
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：yexx<yexiaoxiao@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年4月28日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public class UserInfoEntity
{
	private long uid;
	private int sex;
	private String userName;
	private String mobile;
	private int mobileIsVerify;
	private String email;
	private int emailIsVerify;
	private String qq;
	private String address;
	private String postcode;
	private Date editDate;
	private int appId;
	private int unverified;
	
	public int getUnverified()
	{
		return unverified;
	}

	public void setUnverified(int unverified)
	{
		this.unverified = unverified;
	}
	
	public int getAppId()
	{
		return appId;
	}
	public void setAppId(int appId)
	{
		this.appId = appId;
	}
	public long getUid()
	{
		return uid;
	}
	public void setUid(long uid)
	{
		this.uid = uid;
	}
	public int getSex()
	{
		return sex;
	}
	public void setSex(int sex)
	{
		this.sex = sex;
	}
	public String getUserName()
	{
		return userName;
	}
	public void setUserName(String userName)
	{
		this.userName = userName;
	}
	public String getMobile()
	{
		return mobile;
	}
	public void setMobile(String mobile)
	{
		this.mobile = mobile;
	}
	public int getMobileIsVerify()
	{
		return mobileIsVerify;
	}
	public void setMobileIsVerify(int mobileIsVerify)
	{
		this.mobileIsVerify = mobileIsVerify;
	}
	public String getEmail()
	{
		return email;
	}
	public void setEmail(String email)
	{
		this.email = email;
	}
	public int getEmailIsVerify()
	{
		return emailIsVerify;
	}
	public void setEmailIsVerify(int emailIsVerify)
	{
		this.emailIsVerify = emailIsVerify;
	}
	public String getQq()
	{
		return qq;
	}
	public void setQq(String qq)
	{
		this.qq = qq;
	}
	public String getAddress()
	{
		return address;
	}
	public void setAddress(String address)
	{
		this.address = address;
	}
	public String getPostcode()
	{
		return postcode;
	}
	public void setPostcode(String postcode)
	{
		this.postcode = postcode;
	}
	public Date getEditDate()
	{
		return editDate;
	}
	public void setEditDate(Date editDate)
	{
		this.editDate = editDate;
	}
}
