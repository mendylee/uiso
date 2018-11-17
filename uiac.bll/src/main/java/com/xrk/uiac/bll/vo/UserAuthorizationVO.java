package com.xrk.uiac.bll.vo;

import com.xrk.uiac.bll.entity.UserAuthorizationEntity;

public class UserAuthorizationVO
{
	private long uid;
	private String appId;
	private String accessToken;
	private String refreshToken;
	private long expireDate;
	private long loginDate;
	public long getUid()
    {
	    return uid;
    }
	public void setUid(long uid)
    {
	    this.uid = uid;
    }
	public String getAppId()
    {
	    return appId;
    }
	public void setAppId(String appId)
    {
	    this.appId = appId;
    }
	public String getAccessToken()
    {
	    return accessToken;
    }
	public void setAccessToken(String accessToken)
    {
	    this.accessToken = accessToken;
    }
	public String getRefreshToken()
    {
	    return refreshToken;
    }
	public void setRefreshToken(String refreshToken)
    {
	    this.refreshToken = refreshToken;
    }
	public long getExpireDate()
    {
	    return expireDate;
    }
	public void setExpireDate(long expireDate)
    {
	    this.expireDate = expireDate;
    }
	public long getLoginDate()
    {
	    return loginDate;
    }
	public void setLoginDate(long loginDate)
    {
	    this.loginDate = loginDate;
    }
	public static UserAuthorizationVO parse(UserAuthorizationEntity authEntity)
	{
		UserAuthorizationVO userToken = new UserAuthorizationVO();
		userToken.setAccessToken(authEntity.getAuthToken());
		userToken.setAppId(authEntity.getAppId());
		userToken.setRefreshToken(authEntity.getRefreshToken());
		userToken.setUid(authEntity.getUid());
		userToken.setExpireDate(authEntity.getExpireTime().getTime());
		userToken.setLoginDate(authEntity.getLoginTime().getTime());
		return userToken;
	}
	
}
