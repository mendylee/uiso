package com.xrk.uiac.bll.common;

import com.xrk.hws.common.logger.Logger;
import com.xrk.hws.dal.exception.DalException;
import com.xrk.uiac.bll.cache.CacheService;
import com.xrk.uiac.bll.cache.SysConfigCache;
import com.xrk.uiac.bll.cache.component.AppInfoCache;
import com.xrk.uiac.bll.cache.component.VerifyRequestCache;
import com.xrk.uiac.bll.component.impl.AuthorizationComponent;
import com.xrk.uiac.bll.entity.VerifyRequestEntity;
import com.xrk.uiac.bll.vo.UserAuthorizationVO;
import com.xrk.uiac.bll.vo.VerifyRequestVO;
import com.xrk.uiac.common.utils.BeanCopierUtils;
import com.xrk.uiac.common.utils.Codec;
import com.xrk.uiac.common.utils.ValidateUtils;
import com.xrk.uiac.dal.dao.VerifyRequestDAO;
import com.xrk.uiac.dal.entity.VerifyRequest;

/**
 * 
 * 参数工具类, 用于参数有效性校验或者参数转换
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：yexx<yexiaoxiao@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年5月15日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public class ParameterUtils
{
	/**
	 * 
	 * 对传入的密码和用户编码进行混淆加密.  
	 *    
	 * @param srcPassword
	 * @param userCode
	 * @return
	 */
	public static String encryptPassword(String srcPassword, String extendPara)
	{
		return Codec.hexMD5(srcPassword + extendPara);
	}
	
	/**
	 * 
	 * 验证密码有效性  
	 *    
	 * @param password
	 * @return
	 */
	public static boolean isValidPassword(String password)
	{
		if (password != null && password.length() == UserConstants.PASSWORD_MD5_LEN)
		{
			return true;
		}
		return false;
	}
	
	public static boolean isValidUser(int status)
	{
		return status == UserConstants.ACCOUNT_STATUS_ENABLED;
	}
	
	/**
	 * 
	 * 验证手机号码有效性
	 *    
	 * @param mobile
	 * @return
	 */
	public static boolean isValidMobile(String mobile)
	{
		return ValidateUtils.isMobile(mobile);
	}
	
	/**
	 * 
	 * 验证邮箱的有效性  
	 *    
	 * @param email
	 * @return
	 */
	public static boolean isValidEmail(String email)
	{
		return ValidateUtils.isEmail(email);
	}
	
	/**
	 * 
	 * 验证qq号的有效性
	 *
	 * @param qq
	 * @return
	 */
	public static boolean isValidQQ(String qq)
	{
		return ValidateUtils.isQQ(qq);
	}
	
	/**
	 * 
	 * 验证url是否合法
	 *    
	 * @param url
	 * @return
	 */
	public static boolean isValidUrl(String url)
	{
		return ValidateUtils.isUrl(url);
	}
	
	/**
	 * 
	 * 校验userCode是否是合法的用户账号
	 *    
	 * @param userCode
	 * @return
	 */
	public static boolean isValidNormalUserCode(String userCode)
	{
		//后续增加基本的字符串长度、特殊字符排除等校验，暂时只排除空字符
		if (userCode != null && !userCode.isEmpty())
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	/**
	 * 
	 * 校验验证码字段的格式有效性
	 * 常用的验证码为4位或者6位
	 *    
	 * @param captcha
	 * @return
	 */
	public static boolean isValidCaptcha(String captcha)
	{
		if(captcha != null 
				&& !captcha.isEmpty() 
				&& captcha.length() > 3)
		{
			return true;
		}
		return false;
	}
	
	/**
	 * 
	 * 校验验证码请求类型的有效性
	 *    
	 * @param checkType
	 * @return
	 */
	public static boolean isValidCheckType(int checkType)
	{
		if (checkType == UserConstants.CAPTCHA_CHECKTYPE_BINDING
				|| checkType == UserConstants.CAPTCHA_CHECKTYPE_REGISTER
				|| checkType == UserConstants.CAPTCHA_CHECKTYPE_RESET_PASSWORD
				|| checkType == UserConstants.CAPTCHA_CHECKTYPE_LOGIN)
		{
			return true;
		}
		return false;
	}
	
	/**
	 * 
	 * 校验性别参数的有效性
	 *    
	 * @param sex
	 * @return
	 */
	public static boolean isValidSex(int sex)
	{
		if (sex == UserConstants.ACCOUNT_GENDER_FEMALE 
				|| sex == UserConstants.ACCOUNT_GENDER_MALE
				|| sex == UserConstants.ACCOUNT_GENDER_NOT_SET)
		{
			return true;
		}
		return false;
	}
	
	/**
	 * 
	 * 校验手机、邮箱等绑定结果参数的有效性  
	 *    
	 * @param isVerify
	 * @return
	 */
	public static boolean isValidVerifyType(int isVerify)
	{
		if (isVerify == UserConstants.MOBILE_IS_NOT_VERIFIED
				|| isVerify == UserConstants.MOBILE_IS_VERIFIED)
		{
			return true;
		}
		return false;
	}
	
	/**
	 * 
	 * 校验邮编是否合法
	 *    
	 * @param postcode
	 * @return
	 */
	public static boolean isValidPostcode(String postcode)
	{
		return ValidateUtils.isPostcode(postcode);
	}
	
	/**
	 * 
	 * 验证appId有效性
	 *    
	 * @param appId
	 * @return
	 */
	public static boolean isValidAppId(int appId)
	{
		AppInfoCache cache = (AppInfoCache) CacheService.GetService(AppInfoCache.class);
		return cache.get(appId) != null;
	}
	
	/**
	 * 
	 * 校验token有效性 
	 *    
	 * @param accessToken
	 * @return
	 */
	public static int validateAccessToken(String accessToken, long uid, int appId)
	{
		//当系统配置设定了不需要验证时，默认返回验证成功
		if (!SysConfigCache.getInstance().requireAuth())
		{
			return UserConstants.ACCESSTOKEN_VALIDATION_RESULT_SUCCESS;
		}
		
		if (accessToken == null || accessToken.isEmpty())
		{
			return UserConstants.ACCESSTOKEN_VALIDATION_RESULT_INVALID;
		}
		
		AuthorizationComponent authComponent = AuthorizationComponent.getInstance();
		UserAuthorizationVO uae = null;
		
		try
		{
			uae = authComponent.queryToken(accessToken);
			
			//授权组件里已经进行了token过期的排除工作，这里也预设一次token过期的排除工作
			if (uae == null 
					|| uae.getUid() != uid 
					|| Integer.valueOf(uae.getAppId()) != appId)
			{
				return UserConstants.ACCESSTOKEN_VALIDATION_RESULT_INVALID;
			}
			else if (uae.getExpireDate() < System.currentTimeMillis())
			{
				return UserConstants.ACCESSTOKEN_VALIDATION_RESULT_EXPIRE;
			}
			else
			{
				return UserConstants.ACCESSTOKEN_VALIDATION_RESULT_SUCCESS;
			}
		}
		catch (Exception e)
		{
			Logger.error("Failed to query token, msg: %s", e.getMessage());
			return UserConstants.ACCESSTOKEN_VALIDATION_RESULT_INVALID;
		}
	}
	
	/**
	 * 
	 * 校验当前操作的验证码的验证状态  
	 *    
	 * @param checkType	验证类型
	 * @return
	 */
	public static int validateCaptchaStatus(int checkType, String mobile) throws DalException
	{
		//调试模式下都返回成功
		if (RunConfig.DEBUG && isValidCheckType(checkType))
		{
			return UserConstants.CAPTCHA_VALIDATION_RESULT_SUCCESS;
		}
		
		if (!isValidCheckType(checkType))
		{
			return UserConstants.CAPTCHA_VALIDATION_RESULT_UNVERIFIED;
		}
		
		VerifyRequestVO vrvo = null;
		VerifyRequestEntity vre = null;
		VerifyRequest vr = null;
		VerifyRequestCache verifyRequestCache = (VerifyRequestCache) CacheService.GetService(VerifyRequestCache.class);
		vre = verifyRequestCache.get(mobile);
		if (vre == null)
		{
			vr = VerifyRequestDAO.findWithMobile(mobile);
			if (vr != null)
			{
				vrvo = new VerifyRequestVO();
				BeanCopierUtils.copy(vr, vrvo);
			}
		}
		else
		{
			vrvo = new VerifyRequestVO();
			BeanCopierUtils.copy(vre, vrvo);
		}
		
		if (vrvo == null)
		{
			return UserConstants.CAPTCHA_VALIDATION_RESULT_UNVERIFIED;
		}
		else if (vrvo.getVerifyStatus() == UserConstants.VERIFY_STATUS_SUCCESS)
		{
			long expireTime = vrvo.getVerifyTime().getTime() + getValidationExpireTime(checkType) * 1000;
			if (expireTime < System.currentTimeMillis())
			{
				return UserConstants.CAPTCHA_VALIDATION_RESULT_EXPIRE;
			}
			return UserConstants.CAPTCHA_VALIDATION_RESULT_SUCCESS;
		}
		return UserConstants.CAPTCHA_VALIDATION_RESULT_UNVERIFIED;
	}
	
	/**
	 * 
	 * 获取验证结果的有效时间  
	 *    
	 * @param checkType
	 * @return
	 */
	private static int getValidationExpireTime(int checkType)
	{
		int value = SysConfigCache.getInstance().getValidationExpireTime();
		if (value != 0)
		{
			return value;
		}
		return UserConstants.DEFAULT_VALIDATION_EXPIRE_TIME;
	}
}