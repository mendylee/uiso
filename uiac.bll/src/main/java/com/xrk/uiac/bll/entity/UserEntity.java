package com.xrk.uiac.bll.entity;

import java.util.Date;

public class UserEntity
{
	private long uid;
	private String account;
	private String password;
	private int status;
	private Date addDate;
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
