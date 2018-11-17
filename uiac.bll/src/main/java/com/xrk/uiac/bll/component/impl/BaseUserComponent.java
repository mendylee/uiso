package com.xrk.uiac.bll.component.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.xrk.hws.common.logger.Logger;
import com.xrk.uiac.bll.cache.CacheService;
import com.xrk.uiac.bll.cache.component.UserCache;
import com.xrk.uiac.bll.cache.component.UserExtendInfoCache;
import com.xrk.uiac.bll.cache.component.UserInfoCache;
import com.xrk.uiac.bll.common.ParameterUtils;
import com.xrk.uiac.bll.common.UserConstants;
import com.xrk.uiac.bll.entity.UserEntity;
import com.xrk.uiac.bll.entity.UserExtendInfoEntity;
import com.xrk.uiac.bll.entity.UserInfoEntity;
import com.xrk.uiac.bll.exception.BUSINESS_CODE;
import com.xrk.uiac.bll.exception.BusinessException;
import com.xrk.uiac.bll.exception.GoneException;
import com.xrk.uiac.bll.exception.InternalServerException;
import com.xrk.uiac.bll.exception.VerifyException;
import com.xrk.uiac.common.utils.BeanCopierUtils;
import com.xrk.uiac.dal.dao.UserDAO;
import com.xrk.uiac.dal.dao.UserExtendInfoDAO;
import com.xrk.uiac.dal.dao.UserInfoDAO;
import com.xrk.uiac.dal.dao.UserStatDAO;
import com.xrk.uiac.dal.entity.User;
import com.xrk.uiac.dal.entity.UserExtendInfo;
import com.xrk.uiac.dal.entity.UserInfo;
import com.xrk.uiac.dal.entity.UserStat;

/**
 * 
 * 用户信息管理组件的基类, 主要是封装了一些公共流程
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：yexx<yexiaoxiao@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年5月18日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public abstract class BaseUserComponent extends BaseComponent
{
	protected UserCache userCache = (UserCache) CacheService.GetService(UserCache.class);
	protected UserInfoCache userInfoCache = (UserInfoCache) CacheService.GetService(UserInfoCache.class);
	protected UserExtendInfoCache extendInfoCache = (UserExtendInfoCache) CacheService.GetService(UserExtendInfoCache.class);
	
	protected Gson gson = new Gson();
	
	protected void validateCaptcha(int checkType, String userCode) throws BusinessException
	{
		int captchaVal = ParameterUtils.validateCaptchaStatus(checkType, userCode);
		if (captchaVal == UserConstants.CAPTCHA_VALIDATION_RESULT_EXPIRE) 
		{
			//过期
			Logger.debug("Captcha expires now, checkType: %d, userCode: %s", checkType, userCode);
			throw new VerifyException(BUSINESS_CODE.CAPTCHA_VALIDATION_EXPIRE, "Captcha expires now");
		}
		else if (captchaVal == UserConstants.CAPTCHA_VALIDATION_RESULT_UNVERIFIED)
		{
			//未验证
			Logger.debug("Captcha is error, or captcha is unverified: %d, userCode: %s", checkType, userCode);
			throw new VerifyException(BUSINESS_CODE.CAPTCHA_VALIDATION_UNVERIFIED, "Captcha is error, or captcha is unverified");
		}
	}
	
	protected void validateUserInfo(UserInfo ui, int type) throws BusinessException
	{
		if (ui != null)
		{
			switch (type)
			{
				case UserConstants.USERINFO_VALIDATION_TYPE_CREATE_USER:
				{
					//性别参数不正确
					if (!ParameterUtils.isValidSex(ui.getSex()))
					{
						Logger.debug("Create user, sex is invalid, sex: %d", ui.getSex());
						throw new VerifyException(BUSINESS_CODE.CREATE_USER_SEX_INVALID, "Sex is invalid");
					}
					//手机号码参数不正确
					if (ui.getMobile() != null && !ui.getMobile().isEmpty() && !ParameterUtils.isValidMobile(ui.getMobile()))
					{
						Logger.debug("Create user, mobile is invalid, mobile: %s", ui.getMobile());
						throw new VerifyException(BUSINESS_CODE.CREATE_USER_MOBILE_INVALID, "mobile is invalid");
					}
					//电子邮箱参数不正确
					if (ui.getEmail() != null && !ui.getEmail().isEmpty() && !ParameterUtils.isValidEmail(ui.getEmail()))
					{
						Logger.debug("Create user, email is invalid, email: %s", ui.getEmail());
						throw new VerifyException(BUSINESS_CODE.CREATE_USER_EMAIL_INVALID, "email is invalid");
					}
					//qq号码参数不正确
					if (ui.getQq() != null && !ui.getQq().isEmpty() && !ParameterUtils.isValidQQ(ui.getQq()))
					{
						Logger.debug("Create user, qq is invalid, qq: %s", ui.getQq());
						throw new VerifyException(BUSINESS_CODE.CREATE_USER_QQ_INVALID, "qq is invalid");
					}
					//邮政编码不合法
					if (ui.getPostcode() != null && !ui.getPostcode().isEmpty() && !ParameterUtils.isValidPostcode(ui.getPostcode()))
					{
						Logger.debug("Create user, postcode is invalid, postcode: %s", ui.getPostcode());
						throw new VerifyException(BUSINESS_CODE.CREATE_USER_POSTCODE_INVALID, "postcode is invalid");
					}
					break;
				}
				case UserConstants.USERINFO_VALIDATION_TYPE_UPDATE:
				{
					//性别参数不正确
					if (!ParameterUtils.isValidSex(ui.getSex()))
					{
						Logger.debug("Update info, sex is invalid, uid: %d, sex: %d", ui.getUid(), ui.getSex());
						throw new VerifyException(BUSINESS_CODE.UPDATE_USERINFO_SEX_INVALID, "Sex is invalid");
					}
					//手机号码参数不正确
					if (ui.getMobile() != null && !ui.getMobile().isEmpty() && !ParameterUtils.isValidMobile(ui.getMobile()))
					{
						Logger.debug("Update info, mobile is invalid, uid: %d, mobile: %s", ui.getUid(), ui.getMobile());
						throw new VerifyException(BUSINESS_CODE.UPDATE_USERINFO_MOBILE_INVALID, "mobile is invalid");
					}
					//电子邮箱参数不正确
					if (ui.getEmail() != null && !ui.getEmail().isEmpty() && !ParameterUtils.isValidEmail(ui.getEmail()))
					{
						Logger.debug("Update info, email is invalid, uid: %d, email: %s", ui.getUid(), ui.getEmail());
						throw new VerifyException(BUSINESS_CODE.UPDATE_USERINFO_EMAIL_INVALID, "email is invalid");
					}
					//qq号码参数不正确
					if (ui.getQq() != null && !ui.getQq().isEmpty() && !ParameterUtils.isValidQQ(ui.getQq()))
					{
						Logger.debug("Update info, qq is invalid, uid: %d, qq: %s", ui.getUid(), ui.getQq());
						throw new VerifyException(BUSINESS_CODE.UPDATE_USERINFO_QQ_INVALID, "qq is invalid");
					}
					//邮政编码不合法
					if (ui.getPostcode() != null && !ui.getPostcode().isEmpty() && !ParameterUtils.isValidPostcode(ui.getPostcode()))
					{
						Logger.debug("Update info, postcode is invalid, uid: %d, postcode: %s", ui.getUid(), ui.getPostcode());
						throw new VerifyException(BUSINESS_CODE.UPDATE_USERINFO_POSTCODE_INVALID, "postcode is invalid");
					}
					break;
				}
			}
		}
	}
	
	protected boolean putUserInCache(User u)
	{
		UserEntity ue = new UserEntity();
		BeanCopierUtils.copy(u, ue);
		
		Logger.debug("AccountComponentCache, put user in cache, key: %s, value: %s", ue.getUid(), gson.toJson(ue));
		if (!userCache.putWithUid(ue.getUid(), ue))
		{
			Logger.error("Fail to put UserEntity in cache, uid: %d, uid-key: %d", ue.getUid(), ue.getUid());
			return false;
		}
		
		Logger.debug("AccountComponentCache, put user in cache, key: %s, value: %s", ue.getAccount(), gson.toJson(ue));
		if (!userCache.putWithAccount(ue.getAccount(), ue))
		{
			Logger.error("Fail to put UserEntity in cache, uid: %d, account-key: %s", ue.getUid(), ue.getAccount());
			return false;
		}
		
		return true;
	}
	
	protected boolean putUserInfoInCache(UserInfo ui)
	{
		UserInfoEntity uie = new UserInfoEntity();
		BeanCopierUtils.copy(ui, uie);
		
		Logger.debug("AccountComponentCache, put user info in cache, key: %s, value: %s", uie.getUid(), gson.toJson(uie));
		if (!userInfoCache.put(uie.getUid(), uie))
		{
			Logger.error("Fail to put UserInfoEntity in cache, uid: %d", uie.getUid());
			return false;
		}
		
		return true;
	}
	
	protected boolean putExtendInfoInCache(long uid, List<UserExtendInfo> upList, boolean update)
	{
		List<UserExtendInfo> oldList = null;
		UserExtendInfoEntity eie = null;
		
		if (upList == null)
		{
			return false;
		}
		
		//注册时，直接插入数据即可
		eie = new UserExtendInfoEntity();
		if (!update)
		{
			eie.setList(upList);
			Logger.debug("AccountComponentCache, create user, put user extend info in cache, key: %s, value: %s", uid, gson.toJson(eie));
			return extendInfoCache.put(uid, eie);
		}

		//更新数据时，得先将更新的list与原有list合并
		oldList = getExtendInfoFromCache(uid);
		if (oldList != null && oldList.size() > 0)
		{
			boolean isExisted;
			UserExtendInfo oei = null;
			int size = oldList.size();
			for (UserExtendInfo ei : upList)
			{
				isExisted = false;
				for (int i=0; i<size; i++)
				{
					oei = oldList.get(i);
					if (oei.getExtKey().equals(ei.getExtKey()))
					{
						isExisted = true;
						oei.setExtValue(ei.getExtValue());
						break;
					};
				}
				if (!isExisted)
				{
					oldList.add(ei);
				}
			}
			eie.setList(oldList);
			Logger.debug("AccountComponentCache, merge data, put user extend info in cache, key: %s, value: %s", uid, gson.toJson(eie));
			return extendInfoCache.put(uid, eie);
		}
		else
		{
			//旧数据为空，直接插入即可
			eie.setList(upList);
			Logger.debug("AccountComponentCache, append data, put user extend info in cache, key: %s, value: %s", uid, gson.toJson(eie));
			return extendInfoCache.put(uid, eie);
		}
	}
	
	protected List<UserExtendInfo> getExtendInfoFromCache(long uid)
	{
		List<UserExtendInfo> uiList = null;
		UserExtendInfoEntity eie = null;
		
		//先从缓存里查
		eie = extendInfoCache.get(uid);
		if (eie == null)
		{
			//从数据库查
			//暂时业务端并未使用extendInfo表，因此用户获取的扩展信息都为空，所以这里暂时将空的列表也缓存起来，减少数据库的请求量
			uiList = UserExtendInfoDAO.findListWithUid(uid);
			Logger.debug("AccountComponentCache, get user extend info from db, key: %s, value: %s", uid, gson.toJson(uiList));
			
			eie = new UserExtendInfoEntity();
			if (uiList == null)
			{
				Logger.debug("AccountComponentCache, extend info is null, insert a null object, key: %s", uid);
			}
			else
			{
				//uiList不为null，插入缓存中
				eie.setList(uiList);
			}

			Logger.debug("AccountComponentCache, get user extend info from db, and put them in the cache, key: %s", uid);
			extendInfoCache.put(uid, eie);
		}
		else
		{
			Logger.debug("AccountComponentCache, get user extend info from cache, key: %s, value: %s", uid, gson.toJson(eie));
			uiList = eie.getList();
		}
		
		return uiList;
	}
	
	protected User getUserFromCache(String account) throws BusinessException
	{
		User u = null;
		UserEntity ue = null;
		boolean getFromDb = false;
		
		//先从缓存里查
		ue = userCache.getWithAccount(account);
		if (ue == null)
		{
			//从数据库查未被标记为删除的数据
			u = UserDAO.findExistingUserWithAccount(account);
			Logger.debug("AccountComponentCache, get user from db, key: %s, value: %s", account, gson.toJson(u));
			getFromDb = true;
			if (u == null)
			{
				//用户不存在
				Logger.debug("Cannot find this user with account from db, account: %s", account);
				throw new GoneException(BUSINESS_CODE.USER_INVALID, "Cannot find this user");
			}
			else
			{
				//用户存在, 返回结果之前, 插入缓存
				Logger.debug("AccountComponentCache, get user from db, and put it in cache, key: %s", account);
				putUserInCache(u);
			}
		}
		else
		{
			Logger.debug("AccountComponentCache, get user from cache, key: %s, value: %s", account, gson.toJson(ue));
			//缓存库里查到了
			if (ue.getIsDel() == UserConstants.ACCOUNT_DEL_STATUS_DELETE)
			{
				//用户不存在
				Logger.debug("find this user with account from cache, but this user is deleted, account: %s", account);
				throw new GoneException(BUSINESS_CODE.USER_INVALID, "Cannot find this user");
			}
			
			u = new User();
			BeanCopierUtils.copy(ue, u);
		}
		
		//TODO 待线上redis稳定后删除
		//
		//临时方法，防止出现键值不匹配的情况
		if (account != null && !account.equals(u.getAccount()))
		{
			//键值不匹配，抛出内部错误
			Logger.error("AccountComponentCache error, fail to get user from %s, account != user.getAccount(), account: %s, user.getAccount(): %s", (getFromDb ? "db" : "cache"), account, u.getAccount());
			throw new InternalServerException(BUSINESS_CODE.INTERNAL_SERVER_ERROR, "fail to get user");
		}
		
		return u;
	}
		
	protected User getUserFromCache(long uid) throws BusinessException
	{
		User u = null;
		UserEntity ue = null;
		boolean getFromDb = false;
		
		//先从缓存里查
		ue = userCache.getWithUid(uid);
		if (ue == null)
		{
			//从数据库里查未被标记为删除的数据
			u = UserDAO.findExistingUserWithUid(uid);
			Logger.debug("AccountComponentCache, get user from db, key: %s, value: %s", uid, gson.toJson(u));
			getFromDb = true;
			if (u == null)
			{
				//用户不存在
				Logger.debug("Cannot find this user with uid from db, uid: %d", uid);
				throw new GoneException(BUSINESS_CODE.USER_INVALID, "Cannot find this user");
			}
			else
			{
				//用户存在, 返回结果之前, 插入缓存
				Logger.debug("AccountComponentCache, get user from db, and put it in the cache, key: %s", uid);
				putUserInCache(u);
			}
		}
		else
		{
			//缓存库里查到了
			Logger.debug("AccountComponentCache, get user from cache, key: %s, value: %s", uid, gson.toJson(ue));
			
			if (ue.getIsDel() == UserConstants.ACCOUNT_DEL_STATUS_DELETE)
			{
				//用户已被删除
				Logger.debug("find this user with uid from cache, but this user is deleted, uid: %d", uid);
				throw new GoneException(BUSINESS_CODE.USER_INVALID, "Cannot find this user");
			}
			
			u = new User();
			BeanCopierUtils.copy(ue, u);
		}
		
		//TODO 待线上redis稳定后删除
		//
		//临时方法，防止出现键值不匹配的情况
		if (uid != u.getUid())
		{
			//键值不匹配，抛出内部错误
			Logger.error("AccountComponentCache error, fail to get user from %s, uid != user.getUid(), uid: %d, user.getUid(): %d", (getFromDb ? "db" : "cache"), uid, u.getUid());
			throw new InternalServerException(BUSINESS_CODE.INTERNAL_SERVER_ERROR, "fail to get user");
		}
		
		return u;
	}
	
	protected UserInfo getUserInfoFromCache(long uid) throws BusinessException
	{
		UserInfo ui = null;
		UserInfoEntity uie = null;
		boolean getFromDb = false;
		
		//先从缓存里查
		uie = userInfoCache.get(uid);
		if (uie == null)
		{
			//从数据库里查
			ui = UserInfoDAO.findWithUid(uid);
			Logger.debug("AccountComponentCache, get user info from db, key: %s, value: %s", uid, gson.toJson(ui));
			getFromDb = true;
			if (ui == null)
			{
				//用户不存在
				Logger.debug("Cannot find this user with uid from db, uid: %d", uid);
				throw new GoneException(BUSINESS_CODE.USER_INVALID, "Cannot find this user");
			}
			else
			{
				//用户存在, 返回结果之前, 插入缓存
				Logger.debug("AccountComponentCache, get user info from db, and put it in the cache, key: %s", uid);
				putUserInfoInCache(ui);
			}
		}
		else
		{
			//缓存库里查到了
			Logger.debug("AccountComponentCache, get user info from cache, key: %s, value: %s", uid, gson.toJson(uie));
			ui = new UserInfo();
			BeanCopierUtils.copy(uie, ui);
		}
		
		//TODO 待线上redis稳定后删除
		//
		//临时方法，防止出现键值不匹配的情况
		if (uid != ui.getUid())
		{
			//键值不匹配，抛出内部错误
			Logger.error("AccountComponentCache error, fail to get user info from %s, uid != userInfo.getUid(), uid: %d, userInfo.getUid(): %d", (getFromDb ? "db" : "cache"), uid, ui.getUid());
			throw new InternalServerException(BUSINESS_CODE.INTERNAL_SERVER_ERROR, "fail to get user info");
		}
		
		return ui;
	}
	
	protected UserStat getUserStatFromCache(long uid) throws BusinessException
	{
		UserStat us = null;
		//暂时未做缓存，只实现从数据库取的逻辑
		us = UserStatDAO.findWithUid(uid);
		
		return us;
	}
	
	protected boolean updateUserInfoWithMap(UserInfo ui, Map<String, Object> paras)
	{
		//临时处理方案
		
		String sexKey = "sex";
		String usernameKey = "userName";
		String mobileKey = "mobile";
		String emailKey = "email";
		String qqKey = "qq";
		String addressKey = "address";
		String postcodeKey = "postcode";
		String editDateKey = "editDate";

		if (paras.containsKey(editDateKey))
		{
			ui.setEditDate((Date) paras.get(editDateKey));
		}
		if (paras.containsKey(postcodeKey))
		{
			ui.setPostcode((String) paras.get(postcodeKey));
		}
		if (paras.containsKey(addressKey))
		{
			ui.setAddress((String) paras.get(addressKey));
		}
		if (paras.containsKey(qqKey))
		{
			ui.setQq((String) paras.get(qqKey));
		}
		if (paras.containsKey(emailKey))
		{
			ui.setEmail((String) paras.get(emailKey));
		}
		if (paras.containsKey(mobileKey))
		{
			ui.setMobile((String) paras.get(mobileKey));
		}
		if (paras.containsKey(usernameKey))
		{
			ui.setUserName((String) paras.get(usernameKey));
		}
		if (paras.containsKey(sexKey))
		{
			ui.setSex(((Number) paras.get(sexKey)).intValue());
		}
		
		return true;
	}
}