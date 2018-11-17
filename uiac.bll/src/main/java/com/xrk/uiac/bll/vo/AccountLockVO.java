package com.xrk.uiac.bll.vo;

/**
 * 
 * 注册用户时的账户锁
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：yexx<yexiaoxiao@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年9月16日
 * <br> JDK版本：1.8
 * <br>==========================
 */
public class AccountLockVO
{
	private String account;
	private int accountType;
	private boolean isLocked;
	
	public String getAccount()
	{
		return account;
	}
	public void setAccount(String account)
	{
		this.account = account;
	}
	public int getAccountType()
	{
		return accountType;
	}
	public void setAccountType(int accountType)
	{
		this.accountType = accountType;
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
