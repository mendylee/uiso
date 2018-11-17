package com.xrk.uiac.bll.vo;

/**
 * 
 * 用户基本信息锁
 * 现用于更新用户基本信息接口
 * 更新用户基本信息时，可能存在用户扩展信息的插入，因此加此锁，防止重复插入
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：yexx<yexiaoxiao@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年9月17日
 * <br> JDK版本：1.8
 * <br>==========================
 */
public class UserInfoLockVO
{
	private int appId;
	private long uid;
	private boolean isLocked;
	
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
	public boolean isLocked()
	{
		return isLocked;
	}
	public void setLocked(boolean isLocked)
	{
		this.isLocked = isLocked;
	}
}
