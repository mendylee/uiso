package com.xrk.uiac.bll.vo;

/**
 * 
 * UserSubAccountVO: 用户子账户
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：yexx<yexiaoxiao@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年4月30日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public class UserSubAccountVO
{
	private long subAccountId;
	private long uid;
	private long addDate;
	private int appId;
	private String account;
	
	public long getSubAccountId()
	{
		return subAccountId;
	}
	public void setSubAccountId(long subAccountId)
	{
		this.subAccountId = subAccountId;
	}
	public long getUid()
	{
		return uid;
	}
	public void setUid(long uid)
	{
		this.uid = uid;
	}
	public long getAddDate()
	{
		return addDate;
	}
	public void setAddDate(long addDate)
	{
		this.addDate = addDate;
	}
	public int getAppId()
	{
		return appId;
	}
	public void setAppId(int appId)
	{
		this.appId = appId;
	}
	public String getAccount()
	{
		return account;
	}
	public void setAccount(String account)
	{
		this.account = account;
	}
}
