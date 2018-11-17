package com.xrk.uiac.bll.response;

import java.util.Map;

/**
 * 
 * 查询用户信息接口返回实体
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：yexx<yexiaoxiao@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年5月14日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public class GetUserInfoResponse
{
	private long uid;
	private UserInfoResponse userInfo;
	private Map<String, String> extendInfo;
	
	public long getUid()
	{
		return uid;
	}
	public void setUid(long uid)
	{
		this.uid = uid;
	}
	public UserInfoResponse getUserInfo()
	{
		return userInfo;
	}
	public void setUserInfo(UserInfoResponse userInfo)
	{
		this.userInfo = userInfo;
	}
	public Map<String, String> getExtendInfo()
	{
		return extendInfo;
	}
	public void setExtendInfo(Map<String, String> extendInfo)
	{
		this.extendInfo = extendInfo;
	}
}
