package com.xrk.uiac.bll.response;

/**
 * 
 * 发送校验码响应实体
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：yexx<yexiaoxiao@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年5月21日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public class SendCaptchaResponse
{
	private int timeout;

	public int getTimeout()
	{
		return timeout;
	}

	public void setTimeout(int timeout)
	{
		this.timeout = timeout;
	}
}
