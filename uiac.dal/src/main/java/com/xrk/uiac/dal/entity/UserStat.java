package com.xrk.uiac.dal.entity;

import java.util.Date;

import com.xrk.hws.dal.annotations.Column;
import com.xrk.hws.dal.annotations.Table;
import com.xrk.hws.dal.common.DbType;

@Table(name = "uiac_user_stat")
public class UserStat
{
	@Column(name = "uid", type = DbType.BIGINT)
	private long uid;
		
	@Column(name = "first_login_date", type = DbType.DATETIME)
	private Date firstLoginDate;
	
	@Column(name = "last_login_date", type = DbType.DATETIME)
	private Date lastLoginDate;
	
	@Column(name = "login_num", type = DbType.INT)
	private int loginNum;
	
	public long getUid()
	{
		return uid;
	}

	public void setUid(long uid)
	{
		this.uid = uid;
	}

	public Date getFirstLoginDate()
	{
		return firstLoginDate;
	}

	public void setFirstLoginDate(Date firstLoginDate)
	{
		this.firstLoginDate = firstLoginDate;
	}

	public Date getLastLoginDate()
	{
		return lastLoginDate;
	}

	public void setLastLoginDate(Date lastLoginDate)
	{
		this.lastLoginDate = lastLoginDate;
	}

	public int getLoginNum()
	{
		return loginNum;
	}

	public void setLoginNum(int loginNum)
	{
		this.loginNum = loginNum;
	}
}
