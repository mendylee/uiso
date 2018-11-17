package com.xrk.uiac.bll.entity;

/**
 * 用户应用关联对象
 * UserAppIdEntity: UserAppIdEntity.java.
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：shunchiguo<shunchiguo@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年8月14日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public class UserAppIdEntity
{
	private long uid;
	private String appId;
	
	public UserAppIdEntity()
	{
		uid = 0;
		appId = "0";
	}
	
	public UserAppIdEntity(long userId, String appID)
	{
		uid = userId;
		appId = appID;
	}
	public long getUid()
	{
		return uid;
	}
	public String getAppId()
	{
		return appId;
	}
}
