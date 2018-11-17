package com.xrk.uiac.common.utils.sms;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.xrk.hws.common.logger.Logger;
import com.xrk.uiac.common.utils.http.HTTP;
import com.xrk.uiac.common.utils.http.Request;
import com.xrk.uiac.common.utils.http.Response;
import com.xrk.uiac.common.utils.http.ResponseHandler;

/**
 * 
 * SmsUtils: 短信验证码工具类
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：zhub<zhubin@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年5月21日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public class SmsUtils
{
	private String host;
	
	public SmsUtils(String host)
	{
		this.host = host;
	}
	
	/**
	 * 
	 * 发送验证码
	 * 
	 * @param phone
	 *            手机号
	 * @param content
	 *            短信内容%{code}是验证码显示位置
	 * @param expired
	 *            验证码失效期以秒为单位，默认300
	 * @return
	 */
	public  SmsResponse sendCaptcha(String phone, String content, int expired)
	{
		Logger.info("SmsUtils, send captcha, phone: %s", phone);
		SmsResponse result = null;
		try 
		{
			Request request = new Request(host+"/api/sms/get_captcha");
			Map<String, String> params = new HashMap<>();
			params.put("b_code", SmsConstants.B_CODE);
			params.put("content", content);
			params.put("phone", phone);
			request.setParams(params);
			HTTP.asyncPOST(request, 1, new ResponseHandler(){
                @Override
                public void handle(Response response) throws Exception {
                    Logger.info("SmsUtils, send captcha, phone: %s, result: %s", phone, response.getContent());
                }
			});
			result = new SmsResponse();
			result.setPhone(phone);
			result.setResult("true");
			result.setMessage("success");
		}
		catch (Exception e) 
		{
			Logger.error("SmsUtils, send captcha fail", e);
			throw new SmsException(e);
		}
		return result;
	}
	/**
	 * 
	 * 验证短信验证码  
	 *    
	 * @param phone		手机号
	 * @param captcha	验证码
	 * @return
	 */
	public  SmsResponse checkCaptcha(String phone, String captcha)
	{
		Logger.info("SmsUtils, check captcha, phone: %s", phone);
		SmsResponse result = null;
		try 
		{
			Request request = new Request(host+"/api/sms/check_captcha");
			Map<String, String> params = new HashMap<>();
			params.put("b_code", SmsConstants.B_CODE);
			params.put("phone", phone);
			params.put("captcha", captcha);
			request.setParams(params);
			Response response = HTTP.POST(request, 2);
			result = new Gson().fromJson(response.getContent(), SmsResponse.class);
			Logger.info("SmsUtils, check captcha, phone: %s, result: %s", phone, response.getContent());
		}
		catch (Exception e) {
			Logger.error("SmsUtils, check captcha fail", e);
			throw new SmsException(e);
		}

		return result;
	}
}
