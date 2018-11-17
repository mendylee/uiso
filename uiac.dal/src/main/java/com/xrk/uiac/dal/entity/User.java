package com.xrk.uiac.dal.entity;

import java.util.Date;

import com.xrk.hws.dal.annotations.Column;
import com.xrk.hws.dal.annotations.Table;
import com.xrk.hws.dal.common.DbType;

/**
 * 
 * 用户信息mongodb实体类
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：yexx<yexiaoxiao@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年5月6日
 * <br> JDK版本：1.7
 * <br>==========================
 */

@Table(name="uiac_user")
public class User
{
	@Column(name="uid", type = DbType.BIGINT)
	private long uid;
	
	@Column(name="account", type = DbType.VARCHAR)
	private String account;
	
	@Column(name="password", type = DbType.VARCHAR)
	private String password;
	
	@Column(name="status", type = DbType.INT)
	private int status;
	
	@Column(name="add_date", type = DbType.DATETIME)
	private Date addDate;
	
	@Column(name="is_del", type = DbType.INT)
	private int isDel;

	public long getUid()
	{
		return uid;
	}

	public void setUid(long uid)
	{
		this.uid = uid;
	}

	public String getAccount()
	{
		return account;
	}

	public void setAccount(String account)
	{
		this.account = account;
	}

	public String getPassword()
	{
		return password;
	}

	public void setPassword(String password)
	{
		this.password = password;
	}

	public int getStatus()
	{
		return status;
	}

	public void setStatus(int status)
	{
		this.status = status;
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
