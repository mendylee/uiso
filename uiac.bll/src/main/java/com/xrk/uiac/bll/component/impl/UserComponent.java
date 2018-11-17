package com.xrk.uiac.bll.component.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xrk.hws.common.logger.Logger;
import com.xrk.uiac.bll.cache.component.lock.LockClient;
import com.xrk.uiac.bll.common.ParameterUtils;
import com.xrk.uiac.bll.common.SeqUtils;
import com.xrk.uiac.bll.common.UserConstants;
import com.xrk.uiac.bll.component.IUserComponent;
import com.xrk.uiac.bll.exception.BUSINESS_CODE;
import com.xrk.uiac.bll.exception.BadRequestException;
import com.xrk.uiac.bll.exception.BusinessException;
import com.xrk.uiac.bll.exception.GoneException;
import com.xrk.uiac.bll.exception.InternalServerException;
import com.xrk.uiac.bll.exception.VerifyException;
import com.xrk.uiac.bll.response.CheckParameterResponse;
import com.xrk.uiac.bll.response.CreateUserResponse;
import com.xrk.uiac.bll.response.GetUserInfoResponse;
import com.xrk.uiac.bll.response.UserInfoResponse;
import com.xrk.uiac.bll.vo.AccountLockVO;
import com.xrk.uiac.bll.vo.UserInfoLockVO;
import com.xrk.uiac.bll.vo.UserVO;
import com.xrk.uiac.common.utils.BeanCopierUtils;
import com.xrk.uiac.dal.dao.UserDAO;
import com.xrk.uiac.dal.dao.UserExtendInfoDAO;
import com.xrk.uiac.dal.dao.UserInfoDAO;
import com.xrk.uiac.dal.entity.User;
import com.xrk.uiac.dal.entity.UserExtendInfo;
import com.xrk.uiac.dal.entity.UserInfo;
import com.xrk.uiac.dal.entity.UserStat;

/**
 * 
 * 用户基本信息操作业务实现类
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：yexx<yexiaoxiao@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年5月13日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public class UserComponent extends BaseUserComponent implements IUserComponent
{
	private ExecutorService bgThread = Executors.newSingleThreadExecutor();
	
	@Override
    public CheckParameterResponse checkParameter(int appId, String userCode) throws BusinessException
    {
		Logger.debug("UserComponent, check parameter, appId: %d, userCode: %s", appId, userCode);
		
		CheckParameterResponse response = null;
		User u = null;
		
		/* userCode参数格式检查 */
		validateMobile(userCode);
		
		/* appId参数检查 */
		validateAppId(appId);
		
		/* 验证userCode是否被占用 */
		/* 排除掉is_del为1的数据 */
		u = UserDAO.findExistingUserWithAccount(userCode);
		
		response = new CheckParameterResponse();
		if (u != null)
		{
			Logger.debug("UserCode was used by other person, userCode: %s", userCode);
			response.setMobile(false);
		}
		else
		{
			response.setMobile(true);
		}
		
	    return response;
    }
	
	@Override
	public CreateUserResponse createUser(int appId, String userCode, String password, String userInfo, String extendInfo, boolean unverified) throws BusinessException
	{
		AccountLockVO accountLockVO = new AccountLockVO();
		try
		{
			return processCreating(appId, userCode, password, userInfo, extendInfo, unverified, accountLockVO);
		}
		catch (BusinessException e)
		{
			throw e;		
		}
		finally
		{
			//成功或失败，都去锁
			//先判断调用processCreating操作时是否执行了加锁操作
			//避免非法解锁
			if (accountLockVO != null && accountLockVO.isLocked())
			{
				LockClient.getInstance().unlockAccount(userCode);
			}
		}
	}
	
    private CreateUserResponse processCreating(int appId, String userCode, String password, String userInfo, String extendInfo, boolean unverified, AccountLockVO accountLockVO) throws BusinessException
    {
		Logger.debug("UserComponent, create user, appId: %d, userCode: %s, password: %s, userInfo: %s, extendInfo: %s, unverified: %s", 
				appId, userCode, password, userInfo, extendInfo, unverified);
		
		CreateUserResponse response = null;
		User u = null;
		UserInfo ui = null;
		List<UserExtendInfo> eiList = null;
		String encPassword = null;
		Gson gson = null;
		long uid = 0;
		UserAuthorizeSynCompont authorizeSynComponent = null;
		UserVO uvo = null;
		Date date = null;
		int userCodeType = 0;
		
		/* userCode参数格式检查 */
		/* 暂时默认为只支持手机号码注册，后期开放普通账号、邮箱账号的注册 */
		userCodeType = validateUserCode(userCode);
		
		/* 准备插入新用户数据，锁住账号 */
		if (!LockClient.getInstance().lockAccount(userCode, userCodeType))
		{
			Logger.error("internal server error, account was locked by another operation, account: %s", userCode);
			throw new BadRequestException(BUSINESS_CODE.ACTION_IS_LOCKED, "internal server error, account was locked by another operation");
		}
		
		if (accountLockVO != null)
		{
			accountLockVO.setAccount(userCode);
			accountLockVO.setAccountType(userCodeType);
			accountLockVO.setLocked(true);
		}
		
		/* 该版本暂时过滤其他的账号类型，只支持以手机号为主账号 */
		if (userCodeType != UserConstants.USERCODE_TYPE_MOBILE)
		{
			Logger.error("UserCode is invalid, it is supposed to be a mobile number in this version, type: %d", userCodeType);
			throw new VerifyException(BUSINESS_CODE.CREATE_USER_USERCODE_INVALID, "UserCode is invalid, it is supposed to be a mobile number in this version");
		}
		
		if (!unverified)
		{
			/* 验证码校验状态检查 */
			validateCaptcha(UserConstants.CAPTCHA_CHECKTYPE_REGISTER, userCode);
		}
		
		/* appId参数检查 */
		validateAppId(appId);
		
		/* 用户密码参数检查 */
		if (!ParameterUtils.isValidPassword(password))
		{
			Logger.debug("Invalid password parameter. userCode: %s, password: %s", userCode, password != null ? password : "");
			throw new VerifyException(BUSINESS_CODE.CREATE_USER_PASSWORD_INVALID, "Password is invalid");
		}
		
		gson = new Gson();
		
		/* 用户基本信息参数检查 */
		if (userInfo != null && !userInfo.isEmpty())
		{
			try
			{
				ui = gson.fromJson(userInfo, UserInfo.class);
			}
			catch (Exception e)
			{
				Logger.debug("Fail to convert json to UserInfo, json: %s, msg: %s", userInfo, e.getMessage());
				throw new VerifyException(BUSINESS_CODE.CREATE_USER_USERINFO_INVALID, "User based infomation is invalid");
			}
			validateUserInfo(ui, UserConstants.USERINFO_VALIDATION_TYPE_CREATE_USER);
		}
		
		/* 用户扩展信息参数检查 */
		if (extendInfo != null && !extendInfo.isEmpty())
		{
			eiList = UserExtendInfoDAO.getExtendInfoFromJson(0, extendInfo);
			if (eiList == null)
			{
				Logger.error("Fail to convert json to List<UserExtendInfo>, json: %s", extendInfo);
				throw new VerifyException(BUSINESS_CODE.CREATE_USER_EXTENDINFO_INVALID, "User extended Information is invalid");
			}
		}
		
		encPassword = ParameterUtils.encryptPassword(password, userCode);
		if (encPassword == null)
		{
			Logger.error("Fail to encrypt password, password: %s, userCode: %s", password, userCode);
			throw new VerifyException(BUSINESS_CODE.CREATE_USER_PASSWORD_INVALID, "make an error while encrypting password");
		}

		/* 验证userCode是否被占用 */
		u = UserDAO.findExistingUserWithAccount(userCode);
		if (u != null)
		{
			Logger.error("UserCode was used by other person, userCode: %s", userCode);
			throw new VerifyException(BUSINESS_CODE.CREATE_USER_USER_EXISTS, "UserCode was used by other person");
		}
		
		date = new Date(System.currentTimeMillis());
		u = new User();
		u.setAccount(userCode);
		u.setPassword(encPassword);
		u.setAddDate(date);
		u.setIsDel(UserConstants.ACCOUNT_DEL_STATUS_NORMAL);
		u.setStatus(UserConstants.ACCOUNT_STATUS_ENABLED);
		//u.setUid(...);
		
		long seqId = SeqUtils.getUid();
		if (seqId != -1)
		{
			u.setUid(seqId);
		}
		else
		{
			//获取失败，抛出异常
			Logger.error("Fail to generate uid, userCode: %s", userCode);
			throw new InternalServerException(BUSINESS_CODE.INTERNAL_SERVER_ERROR, "fail to generate uid");
		}
		
		//插入用户信息
		if (UserDAO.insertUser(u) == 0)
		{
			Logger.error("Fail to insert user, userCode: %s", userCode);
			throw new InternalServerException(BUSINESS_CODE.INTERNAL_SERVER_ERROR, "Fail to insert user");
		}
		
		//将User更新至缓存
		if (!putUserInCache(u))
		{
			Logger.warn("Fail to put User in cache, uid: &d", u.getUid());
		}
		
		//获取用户uid
		uid = u.getUid();
		
		//插入基本信息
		//假如插入失败, 注册失败，数据回滚
		if (ui == null)
		{
			ui = new UserInfo();
		}
		ui.setUid(uid);
		ui.setEditDate(date);
		ui.setAppId(appId);
		if (unverified)
		{
			ui.setUnverified(UserConstants.USER_IS_UNVERIFIED);
		}
		else
		{
			ui.setUnverified(UserConstants.USER_IS_VERIFIED);
		}
		
		//根据主账号的类型，自动更新绑定状态，并且更新mobile或者email字段
		//主账号为手机号时，自动设置为已绑定手机，邮箱同理。
		if (userCodeType == UserConstants.USERCODE_TYPE_MOBILE)
		{
			ui.setMobile(userCode);
			ui.setMobileIsVerify(UserConstants.MOBILE_IS_VERIFIED);
			ui.setEmailIsVerify(UserConstants.EMAIL_IS_NOT_VERIFIED);
		}
		else if (userCodeType == UserConstants.USERCODE_TYPE_EMAIL)
		{
			ui.setEmail(userCode);
			ui.setMobileIsVerify(UserConstants.MOBILE_IS_NOT_VERIFIED);
			ui.setEmailIsVerify(UserConstants.EMAIL_IS_VERIFIED);
		}
		else
		{
			ui.setMobileIsVerify(UserConstants.MOBILE_IS_NOT_VERIFIED);
			ui.setEmailIsVerify(UserConstants.EMAIL_IS_NOT_VERIFIED);
		}
		if (UserInfoDAO.insert(ui) == 0)
		{
			Logger.warn("Fail to insert userinfo after registering, uid: %d", uid);
			//已插入User实体，回滚时删除该条记录
			rollback(uid, userCode, true, false, false);
			throw new InternalServerException(BUSINESS_CODE.INTERNAL_SERVER_ERROR, "Fail to insert userinfo after registering");
		}
		else
		{
			//将UserInfo更新至缓存
			if (!putUserInfoInCache(ui))
			{
				Logger.warn("Fail to put UserInfo in cache, uid: %d", uid);
			}
		}
		
		//插入扩展信息
		//假如插入失败, 暂时不影响注册
		if (eiList != null)
		{
			for (UserExtendInfo ei : eiList)
			{
				ei.setUid(uid);
			}
			if (UserExtendInfoDAO.insertList(eiList) == 0)
			{
				Logger.warn("Fail to insert userextendinfo after registering, uid: %d", uid);
				//已插入User和UserInfo实体，回滚时删除这两条记录
				rollback(uid, userCode, true, true, false);
				throw new InternalServerException(BUSINESS_CODE.INTERNAL_SERVER_ERROR, "Fail to insert userextendinfo after registering");
			}
			else
			{
				if (!putExtendInfoInCache(uid, eiList, false))
				{
					Logger.warn("Fail to put userextendinfo in cache after registering, uid: %d", uid);
				}
			}
		}
		
		//同步状态
		authorizeSynComponent = new UserAuthorizeSynCompont();
		uvo = new UserVO();
		BeanCopierUtils.copy(u, uvo);
		Logger.debug("Syn user after registering, entity: %s", gson.toJson(uvo));
		authorizeSynComponent.synAddAuthroizeUser(uvo);
		
		//组装返回实体
		response = new CreateUserResponse();
		response.setAppId(appId);
		response.setUid(uid);
		response.setUserCode(userCode);
		
	    return response;
    }
    
    @Override
    public boolean rollbackUser(int appId, String userCode, String password) throws BusinessException
    {
    	AccountLockVO lockVO = new AccountLockVO();
    	try
    	{
    		return processRollbackingUser(appId, userCode, password, lockVO);
    	}
    	catch (BusinessException e)
    	{
    		throw e;
    	}
    	finally
    	{
    		if (lockVO != null && lockVO.isLocked())
    		{
    			LockClient.getInstance().unlockAccount(userCode);
    		}
		}
    }
    
    private boolean processRollbackingUser(int appId, String userCode, String password, AccountLockVO lockVO) throws BusinessException
    {
    	Logger.debug("UserComponent, rollback user, appId: %d, userCode: %s, password: %s", appId, userCode, password);
    	
    	User u = null;
    	long uid = 0;
    	int userCodeType = 0;
    	boolean ret = true;
    	String encPwd = null;
    	
    	/* 校验AppId */
    	validateAppId(appId);
    	
    	userCodeType = getUserCodeType(userCode);
    	
    	/* 锁住账号 */
    	if (!LockClient.getInstance().lockAccount(userCode, userCodeType))
    	{
    		Logger.error("rollbackUser, fail to lock account, account id locked by another operation, account: %s", userCode);
    		throw new BadRequestException(BUSINESS_CODE.ACTION_IS_LOCKED, "rollbackUser, fail to lock account, account is locked by another operation");
    	}
    	if (lockVO != null)
    	{
    		lockVO.setAccount(userCode);
    		lockVO.setAccountType(userCodeType);
    		lockVO.setLocked(true);
    	}
    	
    	/* 先获取用户，假如用户不存在则直接返回 */
    	u = getUserFromCache(userCode);
    	uid = u.getUid();
    	
    	/* 校验密码是否相等 */
    	if (ParameterUtils.isValidPassword(password))
    	{
    		encPwd = ParameterUtils.encryptPassword(password, userCode);
    		if (encPwd == null || !encPwd.equals(u.getPassword()))
    		{
    			//密码错误
    			Logger.error("rollbackUser, password id null or password is not true, encPassword : %s", encPwd);
    			throw new BadRequestException(BUSINESS_CODE.ROLLBACK_USER_WRONG_PASSWORD, "password is null or password is not true");
    		}
    	}
    	else
    	{
    		//缺少密码参数
    		Logger.error("rollbackUser, password is invalid, account: %s, uid: %d, password: %s", userCode, uid, password);
    		throw new BadRequestException(BUSINESS_CODE.ROLLBACK_USER_INVALID_PASSWORD, "password is invalid");
    	}
    	
    	/* 删除用户扩展信息 */
    	/* 因不确定用户扩展信息是否存在，这里不判断是否删除成功 */
    	if (UserExtendInfoDAO.deleteWithUid(uid) == 0)
    	{
    		Logger.debug("rollbackUser, no extendInfo is deleted, account: %s, uid: %d", userCode, uid);
    		//不抛异常
    	}
    	extendInfoCache.remove(uid);
    	
    	/* 删除用户基本信息 */
    	if (UserInfoDAO.deleteUser(uid) == 0)
    	{
    		Logger.debug("rollbackUser, fail to delete userInfo or no userInfo, account: %s, uid: %d", userCode, uid);
    		//不抛异常
    		//ret = false;
    	}
    	userInfoCache.remove(uid);
    	
    	/* 删除用户账号信息 */
    	if (UserDAO.deleteUser(uid) == 0)
    	{
    		Logger.debug("rollbackUser, fail to delete userInfo, account: %s, uid: %d", userCode, uid);
    		throw new InternalServerException(BUSINESS_CODE.INTERNAL_SERVER_ERROR, "fail to delete user");
    	}
    	userCache.removeWithUid(uid);
    	userCache.removeWithAccount(userCode);
    	
    	return ret;
    }

	@Override
    public GetUserInfoResponse getUserInfo(int appId, String accessToken, long uid) throws BusinessException
    {
		Logger.debug("UserComponent, get userinfo with uid, appId: %d, accessToken: %s, uid: %d", appId, accessToken, uid);
		
		GetUserInfoResponse response = null;
		UserInfo ui = null;
		User u = null;
		List<UserExtendInfo> eiList = null;
		UserStat us = null;
		UserInfoResponse uiResponse = null;
		
		/* appId参数检查 */
		validateAppId(appId);
		
		/* uid参数检查 */
		validateUid(uid);
		
		/* accessToken参数检查 */
		validateAccessToken(accessToken, uid, appId);
		
		/* 判断用户是否存在 */
		u = getUserFromCache(uid);
		
		/* 根据uid查询用户基本信息 */
		ui = getUserInfoFromCache(uid);
		uiResponse = new UserInfoResponse();
		BeanCopierUtils.copy(ui, uiResponse);
		
		/* 获取addDate、status */
		uiResponse.setAddDate(u.getAddDate());
		uiResponse.setStatus(u.getStatus());
		uiResponse.setAccount(u.getAccount());
		
		eiList = getExtendInfoFromCache(uid);
		
		Map<String, String> extendInfoMap = new HashMap<String, String>();
		if (eiList != null && eiList.size() > 0)
		{			
			for (UserExtendInfo ei : eiList)
			{
				extendInfoMap.put(ei.getExtKey(), ei.getExtValue());
			}
		}
		
		/* 获取loginDate、loginTimes */
		us = getUserStatFromCache(uid);
		if (us != null)
		{
			uiResponse.setLoginDate(us.getLastLoginDate());
			uiResponse.setLoginTimes(us.getLoginNum());
		}
		
		//组装响应实体类
		response = new GetUserInfoResponse();
		response.setUid(uid);
		response.setUserInfo(uiResponse);
		response.setExtendInfo(extendInfoMap);
		
	    return response;
    }
	
	@Override
	public GetUserInfoResponse getUserInfo(int appId, String accessToken, String account) throws BusinessException
	{
		Logger.debug("UserComponent, get userinfo with account, appId: %d, accessToken: %s, account: %s", appId, accessToken, account);
		
		return getUserInfo(appId, accessToken, getUidWithUserCode(account));
	}

	@Override
	public boolean updateUserInfo(int appId, String accessToken, long uid, String userInfo, String extendInfo) throws BusinessException
	{
		UserInfoLockVO lockVO = new UserInfoLockVO();
		try
		{
			return processUpdatingUserInfo(appId, accessToken, uid, userInfo, extendInfo, lockVO);
		}
		catch (Exception e)
		{
			throw e;
		}
		finally 
		{
			if (lockVO != null && lockVO.isLocked())
			{
				LockClient.getInstance().unlockUserInfo(uid);
			}
		}
	}
	
    public boolean processUpdatingUserInfo(int appId, String accessToken, long uid, String userInfo, String extendInfo, UserInfoLockVO lockVO) throws BusinessException
    {
		Logger.debug("UserComponent, update userinfo, appId: %d, accessToken: %s, uid: %d, userInfo: %s, extendInfo: %s", 
				appId, accessToken, uid, userInfo, extendInfo);
		
		List<UserExtendInfo> eiList = null;
		Map<String, Object> uiMap = null;
		User u = null;
		UserInfo ui = null;
		UserInfo upUi = null;
		Gson gson = null;
		boolean ret = true;
		int userCodeType = 0;

		/* appId参数检查 */
		validateAppId(appId);
		
		/* 验证uid */
		validateUid(uid);
		
		/* 将此操作加锁 */
		if (!LockClient.getInstance().lockUserInfo(uid))
		{
			Logger.error("fail to lock userInfo, userInfo is locked by another operation, uid: %d", uid);
			throw new BadRequestException(BUSINESS_CODE.ACTION_IS_LOCKED, "fail to lock userInfo, userInfo is locked by another operation");
		}
		if (lockVO != null)
		{
			lockVO.setAppId(appId);
			lockVO.setLocked(true);
			lockVO.setUid(uid);
		}
		
		/* accessToken参数检查 */
		validateAccessToken(accessToken, uid, appId);		
		
		/* 用户存在性检查 */
		u = getUserFromCache(uid);
		
		/* 用户编码类型 */
		userCodeType = validateUserCode(u.getAccount());
		
		ui = getUserInfoFromCache(uid);
		
		gson = new Gson();
		if (userInfo != null && !userInfo.isEmpty())
		{
			try
			{
				upUi = gson.fromJson(userInfo, UserInfo.class);
			}
			catch (Exception e)
			{
				Logger.error("Fail to convert json to user info object, uid: %d, json: %s, msg: %s", uid, userInfo, e.getMessage());
				throw new VerifyException(BUSINESS_CODE.UPDATE_USERINFO_BASEINFO_INVALID, "User based infomation is invalid");
			}
			//校验待更新的参数
			validateUserInfo(upUi, UserConstants.USERINFO_VALIDATION_TYPE_UPDATE);
			
			try
			{
				uiMap = gson.fromJson(userInfo, new TypeToken<Map<String, Object>>(){}.getType());
				uiMap.put("editDate", new Date());
			}
			catch (Exception e)
			{
				uiMap = null;
				Logger.error("Fail to convert json to user info map, uid: %d, json: %s, msg: %s", uid, userInfo, e.getMessage());
				throw new VerifyException(BUSINESS_CODE.UPDATE_USERINFO_BASEINFO_INVALID, "User based infomation is invalid");
			}
			
			//预处理、校验参数，防止恶意修改某些状态
			if (uiMap.containsKey("uid"))
			{
				uiMap.remove("uid");
			}
			if (uiMap.containsKey("appId"))
			{
				uiMap.remove("appId");
			}
			if (uiMap.containsKey("mobileIsVerify"))
			{
				uiMap.remove("mobileIsVerify");
			}
			if (uiMap.containsKey("emailIsVerify"))
			{ 
				uiMap.remove("emailIsVerify");
			}
			if (uiMap.containsKey("unverified"))
			{
				uiMap.remove("unverified");
			}
			
			//手机号为主账号时，不允许直接修改手机号
			//邮箱为主账号时，不允许直接修改邮箱地址
			if (userCodeType == UserConstants.USERCODE_TYPE_MOBILE)
			{
				if (uiMap.containsKey("mobile"))
				{
					uiMap.remove("mobile");
				}
			}
			else if (userCodeType == UserConstants.USERCODE_TYPE_EMAIL)
			{
				if (uiMap.containsKey("email"))
				{
					uiMap.remove("email");
				}
			}
			
			//更新基本信息
			if (UserInfoDAO.update(uid, uiMap) == 0)
			{
				//更新失败
				Logger.error("Fail to update user info, uid: %s", uid);
				ret = false;
			}
			
			//更新成功的同时将UserInfo更新至缓存
			if (ret)
			{
				if (!updateUserInfoWithMap(ui, uiMap) || !putUserInfoInCache(ui))
				{
					Logger.error("Fail to put UserInfo in cache, uid: %d", ui.getUid());
				}
			}
		}
		
		//更新扩展信息
		if (extendInfo != null && !extendInfo.isEmpty())
		{
			eiList = UserExtendInfoDAO.getExtendInfoFromJson(uid, extendInfo);

			if (eiList == null)
			{
				Logger.error("Fail to convert json to user extended info list, uid: %d, json: %s", uid, extendInfo);
				throw new VerifyException(BUSINESS_CODE.UPDATE_USERINFO_EXTENDINFO_INVALID, "User extend infomation is invalid");
			}
			
			boolean eRet = true;
			//更新扩展信息
			if (UserExtendInfoDAO.updateWithUid(uid, eiList) == 0)
			{
				//更新失败
				Logger.error("Fail to update user extended info, uid: %d", uid);
				ret = false;
				eRet = false;
			}
			
			//更新失败, 为了防止出现缓存与数据库数据不统一的情况, 暂时强制删除缓存里的内容
			//更新成功, 将新的内容添加到缓存
			if (!eRet)
			{
				extendInfoCache.remove(uid);
			}
			else
			{
				putExtendInfoInCache(uid, eiList, true);
			}
		}
		
		//更新完成, 返回
	    return ret;
    }

	@Override
    public long getUidWithUserCode(String userCode) throws BusinessException
    {
		Logger.debug("UserComponent, get uid with usercode, usercode: %s", userCode);
		
		User u = new User();
		
		/* 校验userCode */
		validateUserCode(userCode);
		
		/* 用户存在性检查，同时获取用户uid */
		u = getUserFromCache(userCode);
		
	    return u.getUid();
    }

	@Override
	public long getWenbaTempId(int appId) throws BusinessException
	{
		Logger.debug("UserComponent, get wenba temp id, appId: %d", appId);
		
		/* 校验appId */
		validateAppId(appId);
		
		long tempId = SeqUtils.getUid();
		
		if (tempId == -1)
		{
			Logger.error("fail to generate wenba tempId, appId: %d", appId);
			throw new InternalServerException(BUSINESS_CODE.INTERNAL_SERVER_ERROR, "fail to generate wenba id");
		}
		
	    return tempId;
	}

	@Override
    public boolean synUserAccount(long uid, String newAccount) throws BusinessException
    {
		Logger.debug("UserComponent, syn user account, uid: %d, newAccount: %s", uid, newAccount);
		
		/* 校验mobile手机号 */
		//放开限制，新账号不一定为手机号类型
		//validateMobile(mobile);
		
		
		/* 查询此用户 */
		User user  = UserDAO.findWithUid(uid);
		if (user == null)
		{
			//用户不存在
			Logger.error("Cannot find this user, uid: %s", uid);
			throw new GoneException(BUSINESS_CODE.USER_INVALID, "Cannot find this user");
		}
		
//		//如果此账号账号就是手机号，不需要同步更新
//		if (!ParameterUtils.isValidMobile(oldAccount))
//		{
//			
//		}
		
		String oldAccount = user.getAccount();
		try 
		{
			User isUser  = UserDAO.findExistingUserWithAccount(newAccount);
			if(isUser == null)
			{
				UserDAO.updateUserAccount(uid, newAccount);
				user.setAccount(newAccount);
				
				//假如用户新账号为手机号，则同时更新用户的手机号
				if (ParameterUtils.isValidMobile(newAccount))
				{
					UserInfoDAO.updateWithMobile(uid, newAccount);
					//更新缓存
					UserInfo ui = getUserInfoFromCache(uid);
					ui.setMobile(newAccount);
					putUserInfoInCache(ui);
				}
				else if (ParameterUtils.isValidMobile(oldAccount))
				{
					//假如用户新账号不是手机号，且旧账号是手机号，应该把用户的手机号清空
					UserInfoDAO.updateWithMobile(uid, "");
					//更新缓存
					UserInfo ui = getUserInfoFromCache(uid);
					ui.setMobile("");
					putUserInfoInCache(ui);
				}
				
				//主账号已修改，因此同步账号
				//删除旧账号缓存
				userCache.removeWithAccount(oldAccount);
				
				//更新uid为key的缓存
				putUserInCache(user);
				
				UserAuthorizeSynCompont authorizeSynComponent = new UserAuthorizeSynCompont();
				UserVO uvo = new UserVO();
				BeanCopierUtils.copy(user, uvo);
				
				//修改账号，同步新的账号
				Logger.debug("syn user after updating user, account: %s, entity: %s", newAccount, (new Gson()).toJson(uvo));
				if (!authorizeSynComponent.synAddAuthroizeUser(uvo))
				{
					Logger.error("fail to syn user after updating user, account: %s, uid: %d", uvo.getAccount(), uvo.getUid());
					return false;
				}
			}
			else
			{
				//用户已存在
				Logger.info("this user exist, mobile: %s", newAccount);
				return false;
			}
        }
        catch (Exception e)
		{
        	Logger.error("fail to update user account, uid: %d", uid, e.getMessage());
			throw new InternalServerException(BUSINESS_CODE.INTERNAL_SERVER_ERROR, "fail to update user account");
        }
		
	    return true;
    }
	
	private void rollback(long uid, String account, boolean user, boolean userinfo, boolean extendinfo)
	{	
		Logger.debug("UserComponent, rollback call, uid: %d, account: %s, user: %b, userinfo: %b, extendinfo: %b", uid, account, user, userinfo, extendinfo);
		bgThread.execute(new Runnable()
		{
			@Override
            public void run()
            {
				Logger.debug("UserComponent, rollback start, uid: %d, account: %s, user: %b, userinfo: %b, extendinfo: %b", uid, account, user, userinfo, extendinfo);
				if (userinfo)
				{
					if (UserInfoDAO.deleteUser(uid) == 0)
					{
						Logger.error("rollback, fail to delete userinfo from db, uid: %d", uid);
					}
					if (!userInfoCache.remove(uid))
					{
						Logger.error("rollback, fail to remove userinfo from cache, uid: %d", uid);
					}
				}
				if (extendinfo)
				{
					if (UserExtendInfoDAO.deleteWithUid(uid) == 0)
					{
						Logger.error("rollback, fail to delete extendinfo from db, uid: %d", uid);
					}
					if (!extendInfoCache.remove(uid))
					{
						Logger.error("rollback, fail to delete extendinfo from cache, uid: %d", uid);
					}
				}
				if (user)
				{
					if (UserDAO.deleteUser(uid) == 0)
					{
						Logger.error("rollback, fail to delete user from db, uid: %d", uid);
					}
					if (!userCache.removeWithUid(uid))
					{
						Logger.error("rollback, fail to remove user from cache, uid: %d", uid);
					}
					if (!userCache.removeWithAccount(account))
					{
						Logger.error("rollback, fail to remove user from cache, account: %s", account);
					}
				}
            }
		});
	}

	@Override
	public boolean recoverUser(int appId, long uid) throws BusinessException
	{
		Logger.debug("UserComponent, recoverUser, appId: %d, uid: %d", appId, uid);
		
		// 该方法涉及到对已删除账号的操作，不使用缓存，直接对数据库操作。
		// 不考虑用户数据的完整性问题。
		
		User u = null;
		String account = null;
		boolean ret = true;
		
		/* 校验uid合法性 */
		validateUid(uid);
		
		/* 校验appId的合法性 */
		validateAppId(appId);
		
		//直接从数据库查，且不过滤"is_del"字段
		u = UserDAO.findWithUid(uid);
		if (u == null)
		{
			//用户不存在
			Logger.error("recoverUser, cannot find this user, uid: %d", uid);
			throw new GoneException(BUSINESS_CODE.USER_INVALID, "cannot find this user");
		}
		
		if (u.getIsDel() == UserConstants.ACCOUNT_DEL_STATUS_NORMAL)
		{
			//用户未被删除
			Logger.info("recoverUser, user was not deleted, uid: %d", uid);
			//无需进行接下来的操作
			//直接返回结果
			return true;
		}
		
		account = u.getAccount();
		
		/* 校验该账号是否允许恢复（账号未被占用） */
		List<User> users = UserDAO.findListWithAccount(account);
		if (users != null && users.size() > 0)
		{
			for (User user : users)
			{
				if (user.getIsDel() == UserConstants.ACCOUNT_DEL_STATUS_NORMAL)
				{
					//查到一个未被删除的账号，且uid与目标账号相同
					//此处用以过滤前期出现的少数重复账号，待稳定后可去除
					if (user.getUid() == uid)
					{
						//报内部错误
						Logger.error("recoverUser, this uid was used by another user, uid: %d, account: %s", uid, account);
						throw new InternalServerException(BUSINESS_CODE.INTERNAL_SERVER_ERROR, "this uid was userd by another user");
					}
					
					//该账号已被占用，不可恢复
					Logger.debug("recoverUser, this account was used by another user, account: %s, uid: %d", account, uid);
					throw new VerifyException(BUSINESS_CODE.RECOVER_USER_ACCOUNT_OCCUPIED, "this account was used by another user");
				}
			}
		}
		else
		{
			//内部错误，查询失败
			Logger.error("recoverUser, fail to find user from db, account: %s, uid: %d", account, uid);
			throw new InternalServerException(BUSINESS_CODE.INTERNAL_SERVER_ERROR, "fail to find user from db");
		}
		
		//校验完成，确认可恢复
		int records = UserDAO.updateDeleteStatus(uid, UserConstants.ACCOUNT_DEL_STATUS_NORMAL);
		
		if (records == 0)
		{
			Logger.error("recoverUser, fail to recover user, uid: %d, account: %s", uid, account);
			ret = false;
		}
		else if (records > 1)
		{
			Logger.error("recoverUser, more than 1 user are recovered, uid: %d, account: %s", uid, account);
		}
		
		return ret;
	}
}
