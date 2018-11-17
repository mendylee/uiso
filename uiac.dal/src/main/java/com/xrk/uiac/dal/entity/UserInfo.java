package com.xrk.uiac.dal.entity;

import java.util.Date;

import com.xrk.hws.dal.annotations.Column;
import com.xrk.hws.dal.annotations.Table;
import com.xrk.hws.dal.common.DbType;

/**
 * 
 * 用户基本信息mongodb实体类
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：yexx<yexiaoxiao@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年5月6日
 * <br> JDK版本：1.7
 * <br>==========================
 */
@Table(name = "uiac_user_info")
public class UserInfo
{
	@Column(name = "uid", type = DbType.BIGINT)
	private long uid;
	
	@Column(name = "sex", type = DbType.INT)
	private int sex;
	
	@Column(name = "user_name", type = DbType.VARCHAR)
	private String userName;
	
	@Column(name = "mobile", type = DbType.VARCHAR)
	private String mobile;
	
	@Column(name = "mobile_is_verify", type = DbType.INT)
	private int mobileIsVerify;
	
	@Column(name = "email", type = DbType.VARCHAR)
	private String email;
	
	@Column(name = "email_is_verify", type = DbType.INT)
	private int emailIsVerify;
	
	@Column(name = "qq", type = DbType.VARCHAR)
	private String qq;
	
	@Column(name = "address", type = DbType.VARCHAR)
	private String address;
	
	@Column(name = "postcode", type = DbType.VARCHAR)
	private String postcode;
	
	@Column(name = "edit_date", type = DbType.DATETIME)
	private Date editDate;
	
	@Column(name = "app_id", type = DbType.SMALLINT)
	private int appId;
	
	@Column(name = "unverified", type = DbType.SMALLINT)
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
