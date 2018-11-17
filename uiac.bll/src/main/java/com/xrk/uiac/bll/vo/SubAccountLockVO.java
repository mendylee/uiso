package com.xrk.uiac.bll.vo;

/**
 * 
 * 绑定子账号时使用的子账号锁
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：yexx<yexiaoxiao@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年9月16日
 * <br> JDK版本：1.8
 * <br>==========================
 */
public class SubAccountLockVO
{
	private String subAccount;
	private int subAppId;
	private boolean isLocked;
	
	public String getSubAccount()
	{
		return subAccount;
	}
	public void setSubAccount(String subAccount)
	{
		this.subAccount = subAccount;
	}
	public int getSubAppId()
	{
		return subAppId;
	}
	public void setSubAppId(int subAppId)
	{
		this.subAppId = subAppId;
	}
	public boolean isLocked()
	{
		return isLocked;
	}
	public void setLocked(boolean isLocked)
	{
		this.isLocked = isLocked;
	}
}
