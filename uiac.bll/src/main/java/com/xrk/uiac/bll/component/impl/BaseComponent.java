package com.xrk.uiac.bll.component.impl;

import com.xrk.hws.common.logger.Logger;
import com.xrk.uiac.bll.common.ParameterUtils;
import com.xrk.uiac.bll.common.UserConstants;
import com.xrk.uiac.bll.exception.BUSINESS_CODE;
import com.xrk.uiac.bll.exception.BusinessException;
import com.xrk.uiac.bll.exception.UnauthorizedException;
import com.xrk.uiac.bll.exception.VerifyException;

public class BaseComponent
{
	protected void validateUserStatus(int status) throws BusinessException
	{
		if (ParameterUtils.isValidUser(status))
		{
			Logger.debug("User is disabled");
			throw new VerifyException(BUSINESS_CODE.USER_IS_DISABLED, "User is disabled");
		}
	}
	
	protected void validateAccessToken(String accessToken, long uid, int appId) throws BusinessException
	{
		int tokenVal = ParameterUtils.validateAccessToken(accessToken, uid, appId);
		if (tokenVal == UserConstants.ACCESSTOKEN_VALIDATION_RESULT_INVALID)
		{
			Logger.debug("Invalid accessToken.");
			throw new UnauthorizedException(BUSINESS_CODE.ACCESS_TOKEN_INVALID, "accessToken is invalid");
		}
		else if (tokenVal == UserConstants.ACCESSTOKEN_VALIDATION_RESULT_EXPIRE)
		{
			Logger.debug("AccessToken expires now.");
			throw new UnauthorizedException(BUSINESS_CODE.ACCESS_TOKEN_EXPIRE, "accessToken expires now");
		}
	}
	
	protected void validateAppId(int appId) throws BusinessException
	{
		if (!ParameterUtils.isValidAppId(appId))
		{
			Logger.debug("Invalid appId. appId: %d", appId);
			throw new VerifyException(BUSINESS_CODE.APP_ID_INVALID, "appId is invalid");
		}
	}
	
	protected void valiadateUrl(String url) throws BusinessException
    {
		if (!ParameterUtils.isValidUrl(url))
		{
			Logger.debug("Invalid url: %s", (url == null) ? "" : url);
			throw new VerifyException(BUSINESS_CODE.URL_INVALID, "url is invalid");
		}
    }
	
	protected void validateUid(long uid) throws BusinessException
	{
		if (uid == 0)
		{
			Logger.debug("Invalid uid, uid: %d", uid);
			throw new VerifyException(BUSINESS_CODE.UID_INVALID, "Uid is invalid"); 
		}
	}
	
	protected void validateMobile(String mobile) throws BusinessException
	{
		if (!ParameterUtils.isValidMobile(mobile))
		{
			Logger.debug("Invalid mobile number. mobile: %s", (mobile == null) ? "" : mobile);
			throw new VerifyException(BUSINESS_CODE.MOBILE_INVALID, "Mobile number is invalid");
		}
	}
	
	protected int validateUserCode(String userCode) throws BusinessException
	{
		int userCodeType = getUserCodeType(userCode);
		if (userCodeType == 0)
		{
			Logger.debug("Account is invalid, account: %s", (userCode == null) ? "null" : userCode);
			throw new VerifyException(BUSINESS_CODE.ACCOUNT_INVALID, "Account is invalid");
		}
		return userCodeType;
	}
	
	protected int getUserCodeType(String userCode)
	{
		if (ParameterUtils.isValidMobile(userCode))
		{
			return UserConstants.USERCODE_TYPE_MOBILE;
		}
		else if (ParameterUtils.isValidEmail(userCode))
		{
			return UserConstants.USERCODE_TYPE_EMAIL;
		}
		else if (ParameterUtils.isValidNormalUserCode(userCode))
		{
			return UserConstants.USERCODE_TYPE_NORMAL;
		}
		else
		{
			return 0;
		}
	}
}