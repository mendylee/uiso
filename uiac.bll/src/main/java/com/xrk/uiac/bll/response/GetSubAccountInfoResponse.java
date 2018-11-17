package com.xrk.uiac.bll.response;

/**
 * 
 * 获取用户子账号列表返回实体
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：yexx<yexiaoxiao@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年5月19日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public class GetSubAccountInfoResponse
{
	private long uid;
	private String bindAppId;
	private String bindAppName;
	private String account;
	private String appId;
	private String appName;
	private boolean thirdParty;
	
	public String getAccount()
	{
		return account;
	}
	public void setAccount(String account)
	{
		this.account = account;
	}
	public String getAppId()
	{
		return appId;
	}
	public void setAppId(String appId)
	{
		this.appId = appId;
	}
	public String getAppName()
	{
		return appName;
	}
	public void setAppName(String appName)
	{
		this.appName = appName;
	}
	public boolean getThirdParty()
	{
		return thirdParty;
	}
	public void setThirdParty(boolean thirdParty)
	{
		this.thirdParty = thirdParty;
	}
	public String getBindAppId()
    {
	    return bindAppId;
    }
	public void setBindAppId(String bindAppId)
    {
	    this.bindAppId = bindAppId;
    }
	public String getBindAppName()
    {
	    return bindAppName;
    }
	public void setBindAppName(String bindAppName)
    {
	    this.bindAppName = bindAppName;
    }
	public long getUid()
    {
	    return uid;
    }
	public void setUid(long uid)
    {
	    this.uid = uid;
    }
}
