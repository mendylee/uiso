package com.xrk.uiac.bll.vo;

import java.util.Date;

/**
 * 
 * 用户基本信息业务实体
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：yexx<yexiaoxiao@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年4月29日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public class UserVO
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
