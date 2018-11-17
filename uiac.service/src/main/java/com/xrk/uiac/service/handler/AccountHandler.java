package com.xrk.uiac.service.handler;

import java.util.List;

import com.xrk.hws.common.logger.Logger;
import com.xrk.hws.http.context.HttpContext;
import com.xrk.uiac.bll.component.impl.UserBindingComponent;
import com.xrk.uiac.bll.component.impl.UserCaptchaComponent;
import com.xrk.uiac.bll.component.impl.UserComponent;
import com.xrk.uiac.bll.component.impl.UserPasswordComponent;
import com.xrk.uiac.bll.component.impl.UserStatusComponent;
import com.xrk.uiac.bll.component.impl.UserSubAccountComponent;
import com.xrk.uiac.bll.exception.BUSINESS_CODE;
import com.xrk.uiac.bll.exception.BusinessException;
import com.xrk.uiac.bll.exception.NotFoundException;
import com.xrk.uiac.bll.exception.PreconditionFailedException;
import com.xrk.uiac.bll.exception.VerifyException;
import com.xrk.uiac.bll.response.CheckParameterResponse;
import com.xrk.uiac.bll.response.CreateUserResponse;
import com.xrk.uiac.bll.response.GetBindingStatusResponse;
import com.xrk.uiac.bll.response.GetSubAccountInfoResponse;
import com.xrk.uiac.bll.response.GetUserInfoResponse;
import com.xrk.uiac.bll.response.SendCaptchaResponse;
import com.xrk.uiac.service.annotation.HttpMethod;
import com.xrk.uiac.service.annotation.HttpMethod.METHOD;
import com.xrk.uiac.service.annotation.HttpMethod.STATUS_CODE;
import com.xrk.uiac.service.annotation.HttpRouterInfo;
import com.xrk.uiac.service.entity.SimpleResponseEntity;

/**
 * 
 * 用户管理相关Handler
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：yexx<yexiaoxiao@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年5月14日
 * <br> JDK版本：1.7
 * <br>==========================
 */

@HttpRouterInfo(router = "Account")
public class AccountHandler extends AbstractHttpWorkerHandler
{
	private UserComponent userComponent = new UserComponent();
	private UserPasswordComponent passwordComponent = new UserPasswordComponent();;
	private UserStatusComponent statusComponent = new UserStatusComponent();
	private UserBindingComponent bindingComponent = new UserBindingComponent();
	private UserSubAccountComponent subAccountComponent = new UserSubAccountComponent();
	private UserCaptchaComponent captchaComponent = new UserCaptchaComponent();
	
	@HttpMethod(uri="/parameter", method=METHOD.GET, code=STATUS_CODE.OK)
	public CheckParameterResponse checkParameter(String mobile, CustomParameter head, HttpContext ctx) throws BusinessException
	{
		Logger.debug("checkparameter hear");
		return userComponent.checkParameter(getAppId(head.getAppId()), mobile);
	}
	
	@HttpMethod(uri="/user", method=METHOD.POST, code=STATUS_CODE.CREATED)
	public CreateUserResponse createUser(String mobile, String password, String userInfo, String extendInfo, String unverified, CustomParameter head, HttpContext ctx) throws BusinessException
	{
		return userComponent.createUser(getAppId(head.getAppId()), mobile, password, userInfo, extendInfo, isUnverified(unverified));
	}
	
	@HttpMethod(uri="/user", method=METHOD.DELETE, code=STATUS_CODE.OK)
	public SimpleResponseEntity<Boolean> rollbackUser(String userCode, String password, CustomParameter head, HttpContext ctx) throws BusinessException
	{
		boolean result = userComponent.rollbackUser(getAppId(head.getAppId()), userCode, password);
		return new SimpleResponseEntity<Boolean>(result);
	}
	
	@HttpMethod(uri="/user/(\\d+)", method=METHOD.GET, code=STATUS_CODE.OK, priority=5)
	public GetUserInfoResponse getUserInfo(CustomParameter head, HttpContext ctx) throws BusinessException
	{
		long uid = getUid(head.getUriGroup().get(0)); 
		return userComponent.getUserInfo(getAppId(head.getAppId()), head.getAccessToken(), uid);
	}
	
	@HttpMethod(uri="/user", method=METHOD.GET, code=STATUS_CODE.OK)
	public GetUserInfoResponse getUserInfo(String account, CustomParameter head, HttpContext ctx) throws BusinessException
	{
		return userComponent.getUserInfo(getAppId(head.getAppId()), head.getAccessToken(), account);
	}
	
	@HttpMethod(uri="/user/(\\d+)", method=METHOD.PUT, code=STATUS_CODE.OK)
	public SimpleResponseEntity<Boolean> updateUserInfo(String userInfo, String extendInfo, CustomParameter head, HttpContext ctx) throws BusinessException
	{
		long uid = getUid(head.getUriGroup().get(0));
		boolean result = userComponent.updateUserInfo(getAppId(head.getAppId()), head.getAccessToken(), uid, userInfo, extendInfo);
		return new SimpleResponseEntity<Boolean>(result);
	}
	
	@HttpMethod(uri="/password/(\\d+)", method=METHOD.PUT, code=STATUS_CODE.OK)
	public SimpleResponseEntity<Boolean> updatePassword(String oldPwd, String password, String unverified, CustomParameter head, HttpContext ctx) throws BusinessException
	{
		long uid = getUid(head.getUriGroup().get(0));
		boolean result = passwordComponent.updatePassword(getAppId(head.getAppId()), head.getAccessToken(), uid, oldPwd, password, isUnverified(unverified));
		return new SimpleResponseEntity<Boolean>(result);
	}
	
	@HttpMethod(uri="/password/(\\d+)", method=METHOD.POST, code=STATUS_CODE.CREATED)
	public SimpleResponseEntity<Boolean> resetPassword(String password, String unverified, CustomParameter head, HttpContext ctx) throws BusinessException
	{
		long uid = getUid(head.getUriGroup().get(0));
		boolean result = passwordComponent.resetPassword(getAppId(head.getAppId()), uid, password, isUnverified(unverified));
		return new SimpleResponseEntity<Boolean>(result);
	}
	
	@HttpMethod(uri="/status/(\\d+)", method=METHOD.PUT, code=STATUS_CODE.OK)
	public SimpleResponseEntity<Boolean> disableUser(long targetId, CustomParameter head, HttpContext ctx) throws BusinessException
	{
		long uid = getUid(head.getUriGroup().get(0));
		boolean result = statusComponent.disableUser(getAppId(head.getAppId()), head.getAccessToken(), uid, targetId);
		return new SimpleResponseEntity<Boolean>(result);
	}
	
	@HttpMethod(uri="/status/(\\d+)", method=METHOD.DELETE, code=STATUS_CODE.OK)
	public SimpleResponseEntity<Boolean> enableUser(long targetId, CustomParameter head, HttpContext ctx) throws BusinessException
	{
		long uid = getUid(head.getUriGroup().get(0));
		boolean result = statusComponent.enableUser(getAppId(head.getAppId()), head.getAccessToken(), uid, targetId);
		return new SimpleResponseEntity<Boolean>(result);
	}
	
	@HttpMethod(uri="/status/(\\d+)", method=METHOD.GET, code=STATUS_CODE.OK)
	public SimpleResponseEntity<Boolean> getUserStatus(long targetId, CustomParameter head, HttpContext ctx) throws BusinessException
	{
		long uid = getUid(head.getUriGroup().get(0));
		boolean result = statusComponent.getUserStatus(getAppId(head.getAppId()), head.getAccessToken(), uid, targetId);
		return new SimpleResponseEntity<Boolean>(result);
	}
	
	@HttpMethod(uri="/bind/(\\d+)", method=METHOD.GET, code=STATUS_CODE.OK)
	public GetBindingStatusResponse getBindingStatus(CustomParameter head, HttpContext ctx) throws BusinessException
	{
		long uid = getUid(head.getUriGroup().get(0));		
		return bindingComponent.getBindingStatus(getAppId(head.getAppId()), head.getAccessToken(), uid);
	}
	
	@HttpMethod(uri="/bind/(\\d+)", method=METHOD.PUT, code=STATUS_CODE.OK)
	public SimpleResponseEntity<Boolean> bindMobile(String mobile, String unverified, CustomParameter head, HttpContext ctx) throws BusinessException
	{
		long uid = getUid(head.getUriGroup().get(0));
		boolean result = bindingComponent.bindMobile(getAppId(head.getAppId()), head.getAccessToken(), uid, mobile, isUnverified(unverified));
		return new SimpleResponseEntity<Boolean>(result);
	}
	
	@HttpMethod(uri="/bind/(\\d+)", method=METHOD.DELETE, code=STATUS_CODE.OK)
	public SimpleResponseEntity<Boolean> unbindMobile(String unverified, CustomParameter head, HttpContext ctx) throws BusinessException
	{
		long uid = getUid(head.getUriGroup().get(0));
		
		boolean result = bindingComponent.unBindMobile(getAppId(head.getAppId()), head.getAccessToken(), uid, isUnverified(unverified));
		return new SimpleResponseEntity<Boolean>(result);
	}
	
	@HttpMethod(uri="/subAccount/(\\d+)", method=METHOD.POST, code=STATUS_CODE.CREATED)
	public SimpleResponseEntity<Boolean> bindSubAccount(String tempId, int subAppId, CustomParameter head, HttpContext ctx) throws BusinessException
	{
		long uid = getUid(head.getUriGroup().get(0));
		boolean result = subAccountComponent.bindSubAccount(getAppId(head.getAppId()), head.getAccessToken(), uid, tempId, subAppId);
		return new SimpleResponseEntity<Boolean>(result);
	}
	
	@HttpMethod(uri="/subAccount/(\\d+)", method=METHOD.DELETE, code=STATUS_CODE.OK)
	public SimpleResponseEntity<Boolean> unbindSubAccount(int subAppId, String subAccount, CustomParameter head, HttpContext ctx) throws BusinessException
	{
		long uid = getUid(head.getUriGroup().get(0));
		boolean result = subAccountComponent.unbindSubAccount(getAppId(head.getAppId()), head.getAccessToken(), uid, subAccount, subAppId);
		return new SimpleResponseEntity<Boolean>(result);
	}
	
	@HttpMethod(uri="/subAccount/(\\d+)", method=METHOD.GET, code=STATUS_CODE.OK, priority=5)
	public List<GetSubAccountInfoResponse> getSubAccountList(CustomParameter head, HttpContext ctx) throws BusinessException
	{
		long uid = getUid(head.getUriGroup().get(0));
		return subAccountComponent.getSubAccountList(getAppId(head.getAppId()), head.getAccessToken(), uid);
	}
	
	@HttpMethod(uri="/subAccount", method=METHOD.GET, code=STATUS_CODE.OK, priority=10)
	public GetSubAccountInfoResponse getSubAccount(String subAccount, int subAppId, CustomParameter head, HttpContext ctx) throws BusinessException
	{
		GetSubAccountInfoResponse account = subAccountComponent.getSubAccount(getAppId(head.getAppId()), subAccount, subAppId);
		if(account == null){
				throw new NotFoundException(BUSINESS_CODE.USER_INVALID, "未找到用户");
		}
		else{
			return account;
		}
	}
	
	@HttpMethod(uri="/captcha", method=METHOD.PUT, code=STATUS_CODE.OK)
	public SendCaptchaResponse sendCaptcha(String mobile, int checkType, CustomParameter head, HttpContext ctx) throws BusinessException
	{
		return captchaComponent.sendCaptcha(getAppId(head.getAppId()), mobile, checkType);
	}
	
	@HttpMethod(uri="/captcha", method=METHOD.GET, code=STATUS_CODE.OK)
	public SimpleResponseEntity<Boolean> validateCaptcha(String mobile, String captcha, int checkType, CustomParameter head, HttpContext ctx) throws BusinessException
	{
		boolean result = captchaComponent.validateCaptcha(getAppId(head.getAppId()), mobile, captcha, checkType);
		return new SimpleResponseEntity<Boolean>(result);
	}
	
	@HttpMethod(uri="/wenba/tempId", method=METHOD.GET, code=STATUS_CODE.OK)
	public SimpleResponseEntity<Long> getWenbaTempId(CustomParameter head, HttpContext ctx) throws BusinessException
	{
		long result = userComponent.getWenbaTempId(getAppId(head.getAppId()));
		return new SimpleResponseEntity<Long>(result);
	}
	
	@HttpMethod(uri="/synUserAccount",method=METHOD.PUT,code=STATUS_CODE.OK)
	public SimpleResponseEntity<Boolean> synUserAccount(String uid, String mobile)throws BusinessException
	{
		boolean result = userComponent.synUserAccount(Long.parseLong(uid), mobile);
		return new SimpleResponseEntity<Boolean>(result);
	}
	
	private boolean isUnverified(String val)
	{
		return (val != null && val.equals("true"));
	}
	
	private int getAppId(String val) throws BusinessException
	{
		int appId = 0;
		try
		{
			appId = Integer.parseInt(val);
		}
		catch (NumberFormatException e)
		{
			throw new VerifyException(BUSINESS_CODE.APP_ID_INVALID, "appId is not an integer");
		}
		return appId;
	}
	
	private long getUid(String val) throws BusinessException
	{
		long uid = 0;
		try
		{
			uid = Long.parseLong(val);
		}
		catch(NumberFormatException ex)
		{
			Logger.error(ex, ex.getMessage());
			throw new PreconditionFailedException(BUSINESS_CODE.PARAMER_INVAILD, String.format("查询的用户ID不正确!uid=%s", val));
		}
		return uid;
	}
}