package com.xrk.uiac.service.handler;

import java.util.List;

import com.google.common.base.Strings;
import com.xrk.hws.common.libs.Codec;
import com.xrk.hws.common.logger.Logger;
import com.xrk.hws.http.context.HttpContext;
import com.xrk.uiac.bll.cache.CacheService;
import com.xrk.uiac.bll.cache.component.UserIdentityCache;
import com.xrk.uiac.bll.component.IAuthorizationComponent;
import com.xrk.uiac.bll.component.IUserAuththorizeSynComponent;
import com.xrk.uiac.bll.component.IUserComponent;
import com.xrk.uiac.bll.component.impl.AuthorizationComponent;
import com.xrk.uiac.bll.component.impl.UserAuthorizeSynCompont;
import com.xrk.uiac.bll.component.impl.UserCaptchaComponent;
import com.xrk.uiac.bll.component.impl.UserComponent;
import com.xrk.uiac.bll.component.impl.UserSubAccountComponent;
import com.xrk.uiac.bll.entity.UserIdentityEntity;
import com.xrk.uiac.bll.exception.BUSINESS_CODE;
import com.xrk.uiac.bll.exception.BusinessException;
import com.xrk.uiac.bll.exception.PreconditionFailedException;
import com.xrk.uiac.bll.vo.UserAuthorizationVO;
import com.xrk.uiac.service.annotation.HttpMethod;
import com.xrk.uiac.service.annotation.HttpMethod.METHOD;
import com.xrk.uiac.service.annotation.HttpMethod.STATUS_CODE;
import com.xrk.uiac.service.annotation.HttpRouterInfo;
import com.xrk.uiac.service.entity.SimpleResponseEntity;

/**
 * 认证接口控制处理类
 * AuthorizationHandler: AuthorizationHandler.java.
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：shunchiguo<shunchiguo@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年5月13日
 * <br> JDK版本：1.7
 * <br>==========================
 */
@HttpRouterInfo(router = "AccessToken")
public class AuthorizationHandler extends AbstractHttpWorkerHandler
{
	IAuthorizationComponent auth;
	IUserComponent userObj;
	IUserAuththorizeSynComponent userSync;
	public AuthorizationHandler()
	{
		super();
		
		UserIdentityCache userCache = (UserIdentityCache) CacheService.GetService(UserIdentityCache.class);
		userSync = new UserAuthorizeSynCompont(userCache);
		auth = new AuthorizationComponent(userSync, new UserCaptchaComponent(), new UserSubAccountComponent());
		userObj = new UserComponent();
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
			throw  new PreconditionFailedException(BUSINESS_CODE.PARAMER_INVAILD, String.format("查询的用户ID不正确!uid=%s", val));
		}
		return uid;
	}
	
	private long getDelayTime(Object expireTime){
		long delayTime = 0;//使用系统默认时间
		if(expireTime != null){
			Float f = Float.parseFloat(expireTime.toString());
			delayTime = f.longValue();
		}
		return delayTime;
	}
	
	@HttpMethod(uri="", method=METHOD.POST, code=STATUS_CODE.CREATED)
	public UserAuthorizationVO login(String account, String password, long timestamp, String scope, Object expireTime, CustomParameter head, HttpContext ctx) throws BusinessException
	{
		long uid = userObj.getUidWithUserCode(account);
		long delayTime = getDelayTime(expireTime);
		return auth.login(uid, head.getAppId(), password, timestamp, scope, delayTime);
	}
	
	/**
	 * 
	 * @param password
	 * @param timestamp
	 * @param scope
	 * @param expireTime
	 * @param head
	 * @param ctx
	 * @return
	 * @throws BusinessException
	 */
	@HttpMethod(uri="/(\\d+)", method=METHOD.POST, code=STATUS_CODE.CREATED, priority=5)
	public UserAuthorizationVO loginForUid(String password, long timestamp, String scope, Object expireTime, CustomParameter head, HttpContext ctx) throws BusinessException
	{
		long uid = getUid(head.getUriGroup().get(0));
		UserIdentityEntity user = userSync.queryAuthroizeUser(String.valueOf(uid));
		if(user == null){
			Logger.error("not find user id!appId=%s, clientVersion=%s, token=%s, uri=%s",
					head.getAppId(), head.getClientVersion(), head.getAccessToken(), head.getUri());
			throw new PreconditionFailedException(BUSINESS_CODE.PARAMER_INVAILD, "未找到要查询的用户ID");
		}
		
		long delayTime = getDelayTime(expireTime);
		if (Strings.isNullOrEmpty(password)) {
		    return auth.login(uid, head.getAppId(), scope, delayTime);
		} else {
		    password = Codec.hexMD5(String.format("%s%s", Codec.hexMD5(password+user.getAccount()), timestamp));
	        return auth.login(uid, head.getAppId(), password, timestamp, scope, delayTime);
		}
	}
	
	@HttpMethod(uri="/captcha", method=METHOD.POST, code=STATUS_CODE.CREATED, priority=6)
	public UserAuthorizationVO loginForCaptcha(String mobile, String captcha, String scope, Object expireTime, CustomParameter head, HttpContext ctx) throws BusinessException
	{
		long uid = userObj.getUidWithUserCode(mobile);
		long delayTime = getDelayTime(expireTime);
		return auth.login(uid, mobile, head.getAppId(), captcha, scope, delayTime);
	}
	
	@HttpMethod(uri="/subaccount", method=METHOD.POST, code=STATUS_CODE.CREATED, priority=6)
	public UserAuthorizationVO loginForSubAccount(String subaccount, String subAppId, String scope, Object expireTime, CustomParameter head, HttpContext ctx) throws BusinessException
	{
		//long uid = userObj.getUidWithUserCode(mobile);
		long delayTime = getDelayTime(expireTime);
		return auth.login(subaccount, head.getAppId(), subAppId, scope, delayTime);
	}
	
	@HttpMethod(uri="", method=METHOD.PUT, code=STATUS_CODE.OK)
	public UserAuthorizationVO updateToken(long uid, String refreshToken, CustomParameter head) throws BusinessException
	{
		return auth.updateToken(uid, head.getAppId(), refreshToken, head.getAccessToken());
	}

	@HttpMethod(uri="/(\\d+)", method=METHOD.GET, code=STATUS_CODE.OK, priority = 1)
	public List<UserAuthorizationVO> queryLoginInfo(CustomParameter head) throws BusinessException
	{
		if(head.getUriGroup().size() == 0)
		{
			Logger.error("not find user id!appId=%s, clientVersion=%s, token=%s, uri=%s",
					head.getAppId(), head.getClientVersion(), head.getAccessToken(), head.getUri());
			throw new PreconditionFailedException(BUSINESS_CODE.PARAMER_INVAILD, "未找到要查询的用户ID");
		}
		
		long uid = getUid(head.getUriGroup().get(0));		
		//验证Token是否有效
		verifyToken(head);
		
		return auth.queryUserToken(uid);
	}
	
	@HttpMethod(uri="", method=METHOD.GET, code=STATUS_CODE.OK)
	public UserAuthorizationVO verifyToken(CustomParameter head) throws BusinessException
	{
		return auth.queryToken(head.getAccessToken());
	}
	
	@HttpMethod(uri="", method=METHOD.DELETE, code=STATUS_CODE.OK)
	public SimpleResponseEntity<Boolean> logout(CustomParameter head) throws BusinessException
	{
		boolean bRtn = auth.logout(head.getAccessToken());
		return new SimpleResponseEntity<Boolean>(bRtn);
	}
}
