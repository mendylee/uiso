package com.xrk.uiac.bll.entity;

import java.util.Date;
import java.util.Set;

/**
 * 用户授权认证信息实体
 * UserAuthorizedEntity: UserAuthorizedEntity.java.
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：shunchiguo<shunchiguo@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年4月30日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public class UserAuthorizationEntity
{
	//用户ID
	private long uid;
	//所属应用ID
	private String appId;
	//授权码
	private String authToken;
	//刷新授权码
	private String refreshToken;
	//登录时间
	private Date loginTime;
	//过期时间 
	private Date expireTime;
	//默认过期时间
	private long delayTime;
	//授权范围
	private Set<String> scope;
		
	public long getUid()
    {
	    return uid;
    }
	public void setUid(long uid)
    {
	    this.uid = uid;
    }
	public String getAuthToken()
    {
	    return authToken;
    }
	public void setAuthToken(String authToken)
    {
	    this.authToken = authToken;
    }
	public String getRefreshToken()
    {
	    return refreshToken;
    }
	public void setRefreshToken(String refreshToken)
    {
	    this.refreshToken = refreshToken;
    }
	
	public Set<String> getScope()
    {
	    return scope;
    }
	public void setScope(Set<String> scope)
    {
	    this.scope = scope;
    }
	
	public Date getExpireTime()
    {
	    return expireTime;
    }
	public void setExpireTime(Date expireTime)
    {
	    this.expireTime = expireTime;
    }
	public Date getLoginTime()
    {
	    return loginTime;
    }
	public void setLoginTime(Date loginTime)
    {
	    this.loginTime = loginTime;
    }
	public String getAppId()
    {
	    return appId;
    }
	public void setAppId(String appId)
    {
	    this.appId = appId;
    }
	public long getDelayTime()
    {
	    return delayTime;
    }
	public void setDelayTime(long delayTime)
    {
	    this.delayTime = delayTime;
    }
	/**
	 * 
	 * 获取实体的唯一标识  
	 *    
	 * @return
	 */
	public String entityId()
	{
		return formatId(uid, appId);
	}
	
	public static String formatId(long uid, String appId)
    {
		return String.format("%s_%s", uid, appId);
    }
	
}
