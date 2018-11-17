package com.xrk.uiac.bll.response;

import java.util.Date;

/**
 * 
 * 调用获取用户基本信息接口时，返回的基本信息实体
 * 系统开发时用的是UserInfo.class实体作为返回实体，为了兼容旧系统的返回，新增了这个专门用作返回实体的类
 * 相比旧实体，新增了addDate、loginDate、loginTimes三个字段
 * 其中loginDate、loginTimes字段从UserStat表中查得
 * addDate字段从User表中查得
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：yexx<yexiaoxiao@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年7月16日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public class UserInfoResponse
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
	private Date addDate;
	private Date loginDate;
	private int loginTimes;
	private int status;
	private int appId;
	private int unverified;
	private String account;
	
	public String getAccount()
	{
		return account;
	}
	public void setAccount(String account)
	{
		this.account = account;
	}
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
	public int getStatus()
	{
		return status;
	}
	public void setStatus(int status)
	{
		this.status = status;
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
	public Date getAddDate()
	{
		return addDate;
	}
	public void setAddDate(Date addDate)
	{
		this.addDate = addDate;
	}
	public Date getLoginDate()
	{
		return loginDate;
	}
	public void setLoginDate(Date loginDate)
	{
		this.loginDate = loginDate;
	}
	public int getLoginTimes()
	{
		return loginTimes;
	}
	public void setLoginTimes(int loginTimes)
	{
		this.loginTimes = loginTimes;
	}
}