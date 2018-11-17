package com.xrk.uiac.bll.component.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.google.gson.Gson;
import com.xrk.hws.common.logger.Logger;
import com.xrk.uiac.bll.cache.CacheService;
import com.xrk.uiac.bll.cache.SysConfigCache;
import com.xrk.uiac.bll.cache.component.UserAuthorizationCache;
import com.xrk.uiac.bll.common.RunConfig;
import com.xrk.uiac.bll.common.UserConstants;
import com.xrk.uiac.bll.component.IAuthorizationComponent;
import com.xrk.uiac.bll.component.IUserAuththorizeSynComponent;
import com.xrk.uiac.bll.component.IUserCaptchaComponent;
import com.xrk.uiac.bll.component.IUserSubAccountComponent;
import com.xrk.uiac.bll.entity.UserAuthorizationEntity;
import com.xrk.uiac.bll.entity.UserIdentityEntity;
import com.xrk.uiac.bll.exception.BUSINESS_CODE;
import com.xrk.uiac.bll.exception.BusinessException;
import com.xrk.uiac.bll.exception.NotFoundException;
import com.xrk.uiac.bll.exception.VerifyException;
import com.xrk.uiac.bll.push.PushClient;
import com.xrk.uiac.bll.push.PushType;
import com.xrk.uiac.bll.response.GetSubAccountInfoResponse;
import com.xrk.uiac.bll.vo.UserAuthorizationVO;
import com.xrk.uiac.common.utils.Codec;
import com.xrk.uiac.dal.dao.UserStatDAO;

/**
 * 认证服务业务实现类 AuthorizationComponent: AuthorizationComponent.java.
 *
 * <br>=
 * ========================= <br>
 * 公司：广州向日葵信息科技有限公司 <br>
 * 开发：shunchiguo<shunchiguo@xiangrikui.com> <br>
 * 版本：1.0 <br>
 * 创建时间：2015年5月6日 <br>
 * JDK版本：1.7 <br>=
 * =========================
 */
public class AuthorizationComponent extends BaseComponent implements IAuthorizationComponent
{
	private static final long MAX_EXPIRE_TIME = 10;
	private static long TOKEN_EXPIRE_TIME = 1000 * 60 * 20;// 默认20分钟过期，由系统参数配置
	
	private static AuthorizationComponent _authComp = null;
	private static Object obj = new Object();
	
	private static ExecutorService bgThread = Executors.newSingleThreadExecutor();
	
	public static AuthorizationComponent getInstance()
	{
		if(_authComp == null)
		{
			synchronized (obj) {
	            if(_authComp == null){
	            	_authComp = new AuthorizationComponent(new UserAuthorizeSynCompont(),  
	            			new UserCaptchaComponent(), new UserSubAccountComponent());
	            }
            }
		}
		
		return _authComp;
	}

	UserAuthorizationCache authCache;
	IUserAuththorizeSynComponent userSync;
	IUserCaptchaComponent userCaptcha;
	IUserSubAccountComponent userSubAccount;

	public AuthorizationComponent(IUserAuththorizeSynComponent userSync, IUserCaptchaComponent userCaptcha, IUserSubAccountComponent userSubAccount) {
		authCache = (UserAuthorizationCache) CacheService.GetService(UserAuthorizationCache.class);		
		this.userSync = userSync;
		this.userCaptcha = userCaptcha;
		this.userSubAccount = userSubAccount;
		TOKEN_EXPIRE_TIME = SysConfigCache.getInstance().getAccessTokenExpireTime();
		Logger.info("token_expire_time: %d", TOKEN_EXPIRE_TIME);
	}
	
	private UserIdentityEntity ValidLoginParam(long uid, String appId) throws BusinessException
	{	
		/* appId参数检查 */
		if (appId == null || appId.isEmpty()) {
			// 应用ID未输入
			throw new VerifyException(BUSINESS_CODE.PARAMER_INVAILD, "应用ID未输入");
		}
		
		// 应用与用户关系暂时先不判断
		try
		{
			validateAppId(Integer.parseInt(appId));
		}
		catch(NumberFormatException e)
		{
			throw new VerifyException(BUSINESS_CODE.APP_ID_INVALID, "appId is invalid");
		}

		UserIdentityEntity user = userSync.queryAuthroizeUser(String.valueOf(uid));
		if (user == null || user.getStatus() != UserConstants.ACCOUNT_STATUS_ENABLED) {
			// 用户不存在或不可用异常
			throw new NotFoundException(BUSINESS_CODE.USER_INVALID, "未找到用户");
		}
		
		return user;
	}
	
	private long getDelayTime(long expireTime){
		long tokenTime = TOKEN_EXPIRE_TIME;
		if(expireTime > 0){
			tokenTime  = expireTime * 1000 * 60;
		}
		return RunConfig.DEBUG ? SysConfigCache.getInstance().getAccessTokenExpireTime()  : tokenTime;
	}
	
	private UserAuthorizationVO processLogin(long uid, String appId, Set<String> lsScope, long expireTime)
	{	
		Date dtNow = new Date();
		long delayTime = getDelayTime(expireTime);
		long expireDate = dtNow.getTime() + delayTime;
		// 判断用户是否已登录
		UserAuthorizationEntity authEntity = authCache.get(uid, appId);
		if (authEntity != null) {
			// 更新过期时间
			authEntity.setExpireTime(new Date(expireDate));
			authEntity.setDelayTime(delayTime);
		}
		else {
			// 生成访问授权信息
			String accessToken = Codec.RandomString();
			String refreshToken = Codec.RandomString();

			// 将用户信息放入缓存
			authEntity = new UserAuthorizationEntity();
			authEntity.setAppId(appId);
			authEntity.setAuthToken(accessToken);
			authEntity.setExpireTime(new Date(expireDate));
			authEntity.setLoginTime(dtNow);
			authEntity.setRefreshToken(refreshToken);
			authEntity.setScope(lsScope);
			authEntity.setUid(uid);
			authEntity.setDelayTime(delayTime);
		}
		
		authCache.put(authEntity);
		UserAuthorizationVO userVo = UserAuthorizationVO.parse(authEntity);
		
		//更新用户统计数据
		pushUpdateTask(uid);

		// 通知注册订阅服务
		notice(appId, userVo, PushType.Authroize_login);
		
		//注册成功，记录日志
		Logger.info("login successfully! uid: %d, accessToken: %s, refreshToken: %s, appId: %s", userVo.getUid(), userVo.getAccessToken(), userVo.getRefreshToken(), userVo.getAppId());
		
		return userVo;
	}
	
	private void pushUpdateTask(long uid)
    {		
		//将其推送到队列，交由线程池更新，不影响登录流程
		bgThread.execute(new Runnable() {			
			@Override
			public void run()
			{								
				UserStatDAO.update(uid);
			}
		});
	}

	/**
	 * @see com.xrk.uiac.bll.component.IAuthorizationComponent#login(long, java.lang.String, java.lang.String, long, java.lang.String)
	 */
	public UserAuthorizationVO login(long uid, String appId, String passwd, long timestamp,
	                                 String scope, long expireTime) throws BusinessException
	{
		Logger.info("login call:uid=%s, appId=%s, passwd=%s, timestamp=%s, scope=%s", uid, appId,
		        passwd, timestamp, scope);
		
		Set<String> lsScope = new HashSet<String>();
		if (scope != null) {
			//lsScope.addAll(scope.split(","));
		}
		
		// 验证参数
		if (passwd.length() != UserConstants.PASSWORD_MD5_LEN) {
			// 密码长度不正确
			throw new VerifyException(BUSINESS_CODE.PARAMER_INVAILD, "密码格式不正确");
		}
		
		Date dtNow = new Date();
		long time = (dtNow.getTime() - timestamp) / 1000 / 60;
		if (time > MAX_EXPIRE_TIME) {
			throw new VerifyException(BUSINESS_CODE.TIMESTAMP_INVALID, "时间戳无效");
		}
				
		UserIdentityEntity user = ValidLoginParam(uid, appId);
		
		// 加密密码
		String srcPwd = user.getPassword();
		srcPwd = Codec.hexMD5(String.format("%s%s", srcPwd, timestamp));
		if (!passwd.equals(srcPwd)) {
			// 密码不正确
			throw new VerifyException(BUSINESS_CODE.AUTH_PASSWORD_INVALID, "密码不正确");
		}
		
		return processLogin(uid, appId, lsScope, expireTime);
	}
	
	@Override
	public UserAuthorizationVO login(long uid, String appId, String scope, long expireTime) throws BusinessException
	{
		Logger.info("login call:uid=%s, appId=%s, scope=%s", uid, appId, scope);
		Set<String> lsScope = new HashSet<String>();
		if (scope != null) {
			//lsScope.addAll(scope.split(","));
		}
		ValidLoginParam(uid, appId);
		return processLogin(uid, appId, lsScope, expireTime);
	}
	
	public UserAuthorizationVO login(long uid, String mobile, String appId, String captcha, String scope, long expireTime) throws BusinessException
	{
		Logger.info("login for captcha call: uid=%s, mobile=%s, appId=%s, captcha=%s, scope=%s",  
				uid, mobile, appId, captcha, scope);
		
		Set<String> lsScope = new HashSet<String>();
		if (scope != null) {
		}
		
		//UserIdentityEntity user = ValidLoginParam(uid, appId);
		ValidLoginParam(uid, appId);
		
		int app = Integer.parseInt(appId);
		
		if(!userCaptcha.validateCaptcha(app, mobile, captcha, UserConstants.CAPTCHA_CHECKTYPE_LOGIN))
		{
			throw new VerifyException(BUSINESS_CODE.VALIDATION_CAPTCHA_CAPTCHA_INVALID, "Invalid captcha");
		}
		
		//验证成功即算登录成功
		return processLogin(uid, appId, lsScope, expireTime);
	}
	
	@Override
    public UserAuthorizationVO login(String subAccount, String appId, String subAppId,
                                     String scope, long expireTime) throws BusinessException
    {
		Logger.info("login for sub account call: subAccount=%s, appId=%s, subAppId=%s, scope=%s",  
				subAccount,  appId, subAppId, scope);
		
		Set<String> lsScope = new HashSet<String>();
		if (scope != null) {
		}
		
		//根据子账号查找出系统用户
		int app = Integer.parseInt(appId);
		int subApp = Integer.parseInt(subAppId);
		validateAppId(app);
		validateAppId(subApp);
		
		GetSubAccountInfoResponse subUser = userSubAccount.getSubAccount(app, subAccount, subApp);
		if(subUser == null){
			throw new NotFoundException(BUSINESS_CODE.USER_INVALID, "未找到用户");
		}
		
		//验证成功即算登录成功
		return processLogin(subUser.getUid(), appId, lsScope, expireTime);
    }

	/**
	 * 
	 * 发送需要通知的消息到消息推送服务  
	 *    
	 * @param appId
	 * @param userVo
	 * @param authType
	 */
	private void notice(String appId, UserAuthorizationVO userVo, PushType authType)
	{
		try
		{
			PushClient.send(appId, authType, new Gson().toJson(userVo));
		}
		catch(Throwable ex)
		{
			Logger.error(ex, "notice to push server error:%s", ex.getMessage());
		}
	}

	/**
	 * @see com.xrk.uiac.bll.component.IAuthorizationComponent#updateToken(long, java.lang.String, java.lang.String, java.lang.String)
	 */
	public UserAuthorizationVO updateToken(long uid, String appId, String refreshToken,
	                                       String accessToken) throws BusinessException
	{
		Logger.info("updateToken call:uid=%s, appId=%s, refreshToken=%s, accessToken=%s", uid,
		        appId, refreshToken, accessToken);
		UserAuthorizationEntity authEntity = authCache.get(uid, appId);
		if (authEntity == null) {
			throw new NotFoundException(BUSINESS_CODE.USER_INVALID, "未找到用户的登录信息");
		}
		long dtNow = new Date().getTime();
		if (authEntity.getExpireTime().getTime() < dtNow) {
			throw new VerifyException(BUSINESS_CODE.AUTH_REFRESH_TOKEN_INVALID, "refreshToken已过期");
		}

		if (authEntity.getAuthToken().equals(accessToken)
		        && authEntity.getRefreshToken().equals(refreshToken)) {
			// 生成访问授权信息
			String newAccessToken = Codec.RandomString();
			String newRefreshToken = Codec.RandomString();

			// 将用户信息放入缓存
			long expireDate = dtNow + TOKEN_EXPIRE_TIME; 
			authEntity.setAppId(appId);
			authEntity.setAuthToken(newAccessToken);
			authEntity.setExpireTime(new Date(expireDate)); 
			authEntity.setRefreshToken(newRefreshToken);
			authEntity.setUid(uid);
			authCache.put(authEntity);
			//清除旧Token的关系
			authCache.removeByToken(accessToken);
		}
		else {
			throw new VerifyException(BUSINESS_CODE.AUTH_REFRESH_TOKEN_WRONG, "refreshToken不正确");
		}

		UserAuthorizationVO userVo = UserAuthorizationVO.parse(authEntity);
		// 通知注册订阅服务
		notice(appId, userVo, PushType.Authorize_Update);

		return userVo;
	}

	/**
	 * @see com.xrk.uiac.bll.component.IAuthorizationComponent#queryToken(java.lang.String)
	 */
	public UserAuthorizationVO queryToken(String accessToken) throws BusinessException
	{
		Logger.info("queryToken call:token=%s", accessToken);
		if(accessToken == null || accessToken.isEmpty()){
			throw new NotFoundException(BUSINESS_CODE.ACCESS_TOKEN_INVALID, "未找到授权访问码信息");
		}
		
		UserAuthorizationEntity entity = authCache.getByToken(accessToken);

		long timestamp = new Date().getTime();
		if (entity == null || entity.getExpireTime().getTime() < timestamp) {
			throw new NotFoundException(BUSINESS_CODE.ACCESS_TOKEN_INVALID, "未找到授权访问码信息");
		}
		
		// 更新过期时间
		Date dtNow = new Date();		
		long expireDate = dtNow.getTime() + entity.getDelayTime();
		entity.setExpireTime(new Date(expireDate));
		authCache.put(entity);
		
		return UserAuthorizationVO.parse(entity);
	}

	/**
	 * 
	 * @throws BusinessException 
	 * @see com.xrk.uiac.bll.component.IAuthorizationComponent#queryUserToken(long)
	 */
	public List<UserAuthorizationVO> queryUserToken(long uid) throws BusinessException
	{
		Logger.info("queryToken by user id call:uid=%s", uid);
		String[] ary = authCache.getUserLoginApp(uid);
		List<UserAuthorizationVO> lsOut = new ArrayList<UserAuthorizationVO>();
		
		if(ary == null)
		{
			//throw new NotFoundException(BUSINESS_CODE.AUTH_USER_NOT_LOGIN, "未找到用户登录信息");
			return lsOut;
		}
		long timestamp = new Date().getTime();
		
		for(String appId : ary)
		{
			String key = UserAuthorizationEntity.formatId(uid, appId);
			UserAuthorizationEntity entity = authCache.get(key);
			if(entity != null && entity.getExpireTime().getTime() > timestamp)
			{
				lsOut.add(UserAuthorizationVO.parse(entity));
			}
		}
		return lsOut;
	}

	/**
	 * 
	 * @see com.xrk.uiac.bll.component.IAuthorizationComponent#logout(long)
	 */
	public boolean logout(long uid) throws BusinessException
	{
		Logger.info("logout by user id call:userId=%s", uid);
		String[] apps = authCache.getUserLoginApp(uid);
		if(apps == null)
		{
			return false;
		}
		
		for(String s : apps)
		{
			if(s == null || s.isEmpty())
				continue;
			
			logout(uid, s);
		}
		
		return true;
	}
	
	/**
	 * 
	 * @see com.xrk.uiac.bll.component.IAuthorizationComponent#logout(java.lang.String)
	 */
	public boolean logout(String accessToken) throws BusinessException
	{
		Logger.info("logout by token call:token=%s", accessToken);
		
		UserAuthorizationVO entity = queryToken(accessToken);
		if (entity == null) {
			// 未找到要注销的用户
			Logger.warn("logout call!not find accessToken=%s", accessToken);
			throw new NotFoundException(BUSINESS_CODE.ACCESS_TOKEN_INVALID, "未找到授权访问码信息");
		}
		// 移除已登录的用户信息
		return logout(entity);
	}

	/**
	 * 
	 * @see com.xrk.uiac.bll.component.IAuthorizationComponent#logout(long, java.lang.String)
	 */
	public boolean logout(long uid, String appId) throws BusinessException
	{
		Logger.info("logout by user id and appId call:uid=%s, appId", uid, appId);
		
		UserAuthorizationEntity user = authCache.get(uid, appId);
		if (user == null) {
			Logger.warn("logout call!not find user authorization! uid=%s, appId=%s", uid, appId);
			throw new NotFoundException(BUSINESS_CODE.ACCESS_TOKEN_INVALID, "未找到授权访问码信息");
		}		
		return logout(UserAuthorizationVO.parse(user));
	}
	
	/**
	 * 
	 * 调用注销操作，移除已缓存的认证信息  
	 *    
	 * @param user
	 * @return
	 */
	private boolean logout(UserAuthorizationVO user)
	{
		Logger.info("logout call: uid=%s, appId=%s", user.getUid(), user.getAppId());
		// 移除缓存信息
		if (authCache.remove(user.getUid(), user.getAppId())) {			
			 notice(String.valueOf(user.getAppId()), user, PushType.Authroize_Logout);
			// 通知推送服务
			return true;
		}
		else {
			return false;
		}
	}
}
