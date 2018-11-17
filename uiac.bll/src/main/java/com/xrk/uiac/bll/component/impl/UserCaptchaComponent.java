package com.xrk.uiac.bll.component.impl;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.xrk.hws.common.logger.Logger;
import com.xrk.uiac.bll.cache.CacheService;
import com.xrk.uiac.bll.cache.SysConfigCache;
import com.xrk.uiac.bll.cache.component.VerifyRequestCache;
import com.xrk.uiac.bll.common.ParameterUtils;
import com.xrk.uiac.bll.common.UserConstants;
import com.xrk.uiac.bll.component.IUserCaptchaComponent;
import com.xrk.uiac.bll.entity.VerifyRequestEntity;
import com.xrk.uiac.bll.exception.BUSINESS_CODE;
import com.xrk.uiac.bll.exception.BusinessException;
import com.xrk.uiac.bll.exception.InternalServerException;
import com.xrk.uiac.bll.exception.NotFoundException;
import com.xrk.uiac.bll.exception.VerifyException;
import com.xrk.uiac.bll.response.SendCaptchaResponse;
import com.xrk.uiac.bll.vo.VerifyRequestVO;
import com.xrk.uiac.common.utils.BeanCopierUtils;
import com.xrk.uiac.common.utils.sms.SmsResponse;
import com.xrk.uiac.common.utils.sms.SmsUtils;
import com.xrk.uiac.dal.dao.VerifyRequestDAO;
import com.xrk.uiac.dal.entity.VerifyRequest;

/**
 * 
 * 验证码操作接口实现类
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：yexx<yexiaoxiao@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年5月21日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public class UserCaptchaComponent extends BaseUserComponent implements IUserCaptchaComponent
{
	private SmsUtils smsUtils = new SmsUtils(SysConfigCache.getInstance().getSmsHost());
	private VerifyRequestCache verifyRequestCache = (VerifyRequestCache) CacheService.GetService(VerifyRequestCache.class);
	private ExecutorService bgThread = Executors.newSingleThreadExecutor();

	@Override
    public SendCaptchaResponse sendCaptcha(int appId, String mobile, int checkType) throws BusinessException
    {
		Logger.debug("UserCaptchaComponent, send captcha, appId: %d, mobile: %s, checkType: %d", appId, mobile, checkType);
		
		SendCaptchaResponse response = null;
		VerifyRequestVO vrvo = null;
		long currentTime = 0;
		long validTime = 0;
		String msgContent = null;
		MsgTemplateCompont msgTemplateComponent = null;
		SmsResponse smsResponse = null;
		
		/* 校验appId */
		validateAppId(appId);
		
		/* 校验手机号 */
		validateMobile(mobile);
		
		/* 校验验证码类型 */
		if (!ParameterUtils.isValidCheckType(checkType))
		{
			Logger.error("Invalid checkType, checkType: %d, mobile: %s", checkType, mobile);
			throw new VerifyException(BUSINESS_CODE.SEND_CAPTCHA_CHECKTYPE_INVALID, "Invalid checkType");
		}
		
		/* 写入一条验证记录 */    		
		currentTime = System.currentTimeMillis();
		validTime = getCaptchaExpireTime(checkType) * 1000l;
		
		vrvo = new VerifyRequestVO();
		vrvo.setCheckType(checkType);
		vrvo.setRequestTime(new Date(currentTime));
		vrvo.setMobile(mobile);
		vrvo.setExpireTime(new Date(currentTime + validTime));
		
		if (!insertVerifyRequest(mobile, vrvo))
		{
			Logger.error("Fail to insert verifyRequst, mobile: %s, checkType: %d", mobile, checkType);
			throw new InternalServerException(BUSINESS_CODE.INTERNAL_SERVER_ERROR, "Fail to insert verifyRequest");			
		}

		/* 获取短信模板, 并调用发送验证码接口 */
		msgTemplateComponent = new MsgTemplateCompont();
		msgContent = msgTemplateComponent.findTemplateContent(String.valueOf(appId), String.valueOf(checkType));
		if (msgContent == null || msgContent.isEmpty())
		{
			Logger.error("Fail to find message template, appId: %d, checkType: %d", appId, checkType);
			throw new InternalServerException(BUSINESS_CODE.INTERNAL_SERVER_ERROR, "Fail to find message template");
		}
		
		try
		{
			smsResponse = smsUtils.sendCaptcha(mobile, msgContent, getCaptchaExpireTime(checkType));
		}
		catch (Exception e)
		{
			Logger.error("Fail to send message, SMSException, appId: %d, checkType: %d, mobile: %s, msg: %s", appId, checkType, mobile, e.getMessage());
			throw new InternalServerException(BUSINESS_CODE.INTERNAL_SERVER_ERROR, "Fail to send message, SMSException");
		}
		
		if (smsResponse == null)
		{
			Logger.error("Fail to send message, smsResponse is null, appId: %d, checkType: %d, mobile: %s", appId, checkType, mobile);
			throw new InternalServerException(BUSINESS_CODE.INTERNAL_SERVER_ERROR, "Fail to send message, smsResponse is null");
		}
		else if (UserConstants.BOOLEAN_FALSE.equals(smsResponse.getResult()))
		{
			Logger.error("Fail to send message, smsResult is false, appId: %d, checkType: %d, mobile: %s, msg: %s", appId, checkType, mobile, smsResponse.getMessage());
			throw new InternalServerException(BUSINESS_CODE.INTERNAL_SERVER_ERROR, "Fail to send message, smsResult is false");
		}
		
		/* 组装返回参数 */
		response = new SendCaptchaResponse();
		response.setTimeout((int) (validTime) / 1000);
		
	    return response;
    }

	@Override
    public boolean validateCaptcha(int appId, String mobile, String captcha, int checkType) throws BusinessException
    {
		Logger.debug("UserCaptchaComponent, validate captcha, appId: %d, mobile: %s, captcha: %s, checkType: %d", appId, mobile, captcha, checkType);
		
		boolean ret = true;
		VerifyRequestVO vrvo = null;
		SmsResponse smsResponse = null;
		
		/* 校验appId */
		validateAppId(appId);
		
		/* 校验手机号 */
		validateMobile(mobile);
		
		/* 校验验证码类型 */
		if (!ParameterUtils.isValidCheckType(checkType))
		{
			Logger.error("Invalid checkType, checkType: %d, mobile: %s", checkType, mobile);
			throw new VerifyException(BUSINESS_CODE.VALIDATION_CAPTCHA_CHECKTYPE_INVALID, "Invalid checkType");
		}
		
		/* 校验验证码格式 */
		if (!ParameterUtils.isValidCaptcha(captcha))
		{
			Logger.error("Invalid captcha, captcha: %d, mobile: %s", captcha, mobile);
			throw new VerifyException(BUSINESS_CODE.VALIDATION_CAPTCHA_CAPTCHA_INVALID, "Invalid captcha");
		}
		
		vrvo = getVerifyRequest(mobile);
		
		//验证是否过期
		if (vrvo == null || isExpired(vrvo.getExpireTime()))
		{
			Logger.error("Captcha expires, mobile: %s", mobile);
			throw new NotFoundException(BUSINESS_CODE.VALIDATION_CAPTCHA_CAPTCHA_EXPIRY, "Captcha expires");
		}
		
		//验证校验类型是否相等
		if (vrvo.getCheckType() != checkType)
		{
			Logger.error("CheckType is invalid, wrong one: %d, correct one: %d", checkType, vrvo.getCheckType());
			throw new VerifyException(BUSINESS_CODE.VALIDATION_CAPTCHA_CHECKTYPE_INVALID, "CheckType is invalid");
		}
		
		/* 调用接口验证验证码 */
		try
		{
			smsResponse = smsUtils.checkCaptcha(mobile, String.valueOf(captcha));
        }
        catch (Exception e) 
		{
        	Logger.error("Fail to check captcha, SMSException, appId: %d, checkType: %d, mobile: %s, msg: %s", appId, checkType, mobile, e.getMessage());
			throw new InternalServerException(BUSINESS_CODE.INTERNAL_SERVER_ERROR, "Fail to check captcha, SMSException");
        }
		
		if (smsResponse == null)
		{
			Logger.error("Fail to check captcha, smsResponse is null, appId: %d, checkType: %d, mobile: %s", appId, checkType, mobile);
			throw new InternalServerException(BUSINESS_CODE.INTERNAL_SERVER_ERROR, "Fail to check captcha, smsResponse is null");
		}
		else if (UserConstants.BOOLEAN_FALSE.equals(smsResponse.getResult()))
		{
			Logger.error("Fail to check captcha, appId: %d, checkType: %d, mobile: %s, msg: %s", appId, checkType, mobile, smsResponse.getMessage());
			ret = false;
		}
		
		/* 验证成功, 写入数据库 */
		if (ret)
		{
			vrvo.setVerifyStatus(UserConstants.VERIFY_STATUS_SUCCESS);
			vrvo.setVerifyTime(new Date());
			
			if (!insertVerifyRequest(mobile, vrvo))
			{
				Logger.error("Fail to insert verifyRuquest, mobile: %s", mobile);
				ret = false;
			}
		}
		
	    return ret;
    }
	
	private VerifyRequestVO getVerifyRequest(String mobile)
	{
		VerifyRequestEntity vre = null;
		VerifyRequest vr = null;
		VerifyRequestVO vrvo = null;
		
		vre = verifyRequestCache.get(mobile);
		if (vre == null)
		{
			//缓存里不存在，从数据库查
			vr = VerifyRequestDAO.findWithMobile(mobile);
			if (vr != null)
			{
				vrvo = new VerifyRequestVO();
				BeanCopierUtils.copy(vr, vrvo);
			}
		}
		else
		{
			//直接从数据库里查询到
			vrvo = new VerifyRequestVO();
			BeanCopierUtils.copy(vre, vrvo);
		}
		
		return vrvo;
	}
	
	private boolean insertVerifyRequest(String mobile, VerifyRequestVO vrvo)
	{
		VerifyRequestEntity vre = null;
		boolean ret = false;
		
		vre = new VerifyRequestEntity();
		BeanCopierUtils.copy(vrvo, vre);
		ret = verifyRequestCache.put(mobile, vre);
		if (!ret)
		{
			Logger.error("Failed to put verifyRequest in cache, mobile: %s", mobile);
		}
		
		//插入数据库
		insertInDB(vrvo);
		return ret;
	}
	
	private void insertInDB(VerifyRequestVO vrvo)
	{
		bgThread.execute(new Runnable()
		{
			@Override
            public void run()
            {
				VerifyRequest vr = new VerifyRequest();
				BeanCopierUtils.copy(vrvo, vr);
				//插入前先删除可能存在的数据
				VerifyRequestDAO.deleteWithMobile(vrvo.getMobile());
				if (VerifyRequestDAO.insert(vr) == 0)
				{
					Logger.error("Failed to insert verifyRequest in db, mobile: %s", vrvo.getMobile());
				}
            }
		});
	}

	/**
	 * 
	 * 获取验证码的有效时间
	 *    
	 * @param checkType
	 * @return
	 */
	private int getCaptchaExpireTime(int checkType)
	{
		int value = SysConfigCache.getInstance().getCaptchaExpireTime();
		if (value != 0)
		{
			return value;
		}
		return UserConstants.DEFAULT_CAPTCHA_EXPIRE_TIME;
	}
		
	/**
	 * 
	 * 判断验证请求是否到期
	 *    
	 * @param expiredTime
	 * @return
	 */
	private boolean isExpired(Date expiredTime)
	{
		if (expiredTime == null || expiredTime.before(new Date()))
		{
			return true;
		}
		return false;
	}
}
