package com.xrk.uiac.common.utils.sms;

/**
 * 
 * SmsConstants: 短信常量类
 *
 * <br>=
 * ========================= <br>
 * 公司：广州向日葵信息科技有限公司 <br>
 * 开发：zhub<zhubin@xiangrikui.com> <br>
 * 版本：1.0 <br>
 * 创建时间：2015年5月21日 <br>
 * JDK版本：1.7 <br>=
 * =========================
 */
public class SmsConstants
{
	/**
	 * 发送短信验证码业务类型
	 */
	public static final String B_CODE = "AL";
	/**
	 * 
	 */
	public static final String DEFAULT_TEMPLATE_CONTENT = "【向日葵】验证码为： %{code}。工作人员不会向您索要，请勿向任何人泄露";
	/**
	 * 测试沙箱环境发送验证码API地址
	 */
	public static final String TEST_SEND_CAPTCHA_URL = "http://192.168.9.16:3008/api/sms/get_captcha";
	/**
	 * 正式环境发送验证码API地址
	 */
	public static final String SEND_CAPTCHA_URL = "http://192.168.30.86:3008/api/sms/get_captch";
	/**
	 * 测试沙箱环境验证短信验证码API地址
	 */
	public static final String TEST_CHECK_CAPTCHA_URL = "http://192.168.9.16:3008/api/sms/check_captcha";
	/**
	 * 正式环境验证短信验证码API地址
	 */
	public static final String CHECK_CAPTCHA_URL = "http://192.168.30.86:3008/api/sms/check_captcha";
}
