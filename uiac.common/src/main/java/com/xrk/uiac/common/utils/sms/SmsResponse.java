package com.xrk.uiac.common.utils.sms;
/**
 * 
 * SmsResponse: 短信验证码结果类
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：zhub<zhubin@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年5月21日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public class SmsResponse
{
	/**
	 * 出错信息，result为false才会有
	 */
	private String message;	
	/**
	 * 手机号
	 */
	private String phone;
	/**
	 * 返回结果，true or false
	 */
	private String result;

	public String getMessage()
	{
		return message;
	}

	public void setMessage(String message)
	{
		this.message = message;
	}

	public String getPhone()
	{
		return phone;
	}

	public void setPhone(String phone)
	{
		this.phone = phone;
	}

	public String getResult()
	{
		return result;
	}

	public void setResult(String result)
	{
		this.result = result;
	}

}
