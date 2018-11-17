package com.xrk.uiac.bll.component;

import com.xrk.uiac.bll.exception.BusinessException;
import com.xrk.uiac.bll.response.SendCaptchaResponse;

/**
 * 
 * 验证码操作接口
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：yexx<yexiaoxiao@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年5月21日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public interface IUserCaptchaComponent
{
	/**
	 * 
	 * 发送验证码 
	 *    
	 * @param appId
	 * @param mobile
	 * @param checkType
	 * @return
	 * @throws BusinessException
	 */
	SendCaptchaResponse sendCaptcha(int appId, String mobile, int checkType) throws BusinessException;
	
	/**
	 * 
	 * 校验验证码
	 *    
	 * @param appId
	 * @param mobile
	 * @param captcha
	 * @param checkType
	 * @return
	 * @throws BusinessException
	 */
	boolean validateCaptcha(int appId, String mobile, String captcha, int checkType) throws BusinessException;
}
