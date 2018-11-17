package com.xrk.uiac.common.utils.sms;
/**
 * 
 * SmsException: 短信异常类
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：zhub<zhubin@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年5月21日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public class SmsException extends RuntimeException
{

	private static final long serialVersionUID = -6657427616048610696L;

	public SmsException(Exception e) {
		super(e);
	}
}
