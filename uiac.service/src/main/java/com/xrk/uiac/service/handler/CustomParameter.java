package com.xrk.uiac.service.handler;

import java.util.List;

/**
 * 自定义参数类，存储系统中自定义的HTTP头及URI匹配中的获取到的分组信息
 * CustomParameter: CustomParameter.java.
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：shunchiguo<shunchiguo@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年5月13日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public class CustomParameter
{
	private String clientVersion;
	private String appId;
	private String accessToken;
	private String uri;
	private List<String> uriGroup;
	
	public String getClientVersion()
    {
	    return clientVersion;
    }
	public void setClientVersion(String clientVersion)
    {
	    this.clientVersion = clientVersion;
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
	public List<String> getUriGroup()
    {
	    return uriGroup;
    }
	public void setUriGroup(List<String> uriGroup)
    {
	    this.uriGroup = uriGroup;
    }
	public String getUri()
    {
	    return uri;
    }
	public void setUri(String uri)
    {
	    this.uri = uri;
    }
}
