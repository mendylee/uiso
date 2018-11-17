package com.xrk.uiac.bll.push;
/**
 * 
 * Subscription: push消息实体
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：zhub<zhubin@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年5月18日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public class Subscription
{
	private PushType pushType;
	private String message;
	public PushType getPushType()
	{
		return pushType;
	}
	public void setPushType(PushType pushType)
	{
		this.pushType = pushType;
	}
	public String getMessage()
	{
		return message;
	}
	public void setMessage(String message)
	{
		this.message = message;
	}
}
