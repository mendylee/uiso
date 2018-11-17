package com.xrk.uiac.bll.component.impl;

import com.google.gson.Gson;
import com.xrk.hws.common.logger.Logger;
import com.xrk.uiac.bll.common.UserConstants;
import com.xrk.uiac.bll.component.IUserStatusComponent;
import com.xrk.uiac.bll.exception.BUSINESS_CODE;
import com.xrk.uiac.bll.exception.BusinessException;
import com.xrk.uiac.bll.exception.InternalServerException;
import com.xrk.uiac.bll.vo.UserVO;
import com.xrk.uiac.common.utils.BeanCopierUtils;
import com.xrk.uiac.dal.dao.UserDAO;
import com.xrk.uiac.dal.entity.User;

/**
 * 
 * 用户账号状态管理接口实现类
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：yexx<yexiaoxiao@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年5月18日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public class UserStatusComponent extends BaseUserComponent implements IUserStatusComponent
{

	@Override
    public boolean disableUser(int appId, String accessToken, long uid, long targetUid) throws BusinessException
    {
		Logger.debug("UserStatusComponent, disable user, appId: %d, accessToken: %s, uid: %d, targetUid: %d", appId, accessToken, uid, targetUid);
		
		boolean ret = true;
		User u = null;
		UserAuthorizeSynCompont authorizeSynComponent = null;
		UserVO uvo = null;
		
		/* 校验appId有效性 */
		validateAppId(appId);
		
		/* 校验uid */
		validateUid(uid);
		
		/* 校验targetId */
		validateUid(targetUid);
		
		/* 校验accessToken有效性 */
		validateAccessToken(accessToken, uid, appId);
		
		/* 检验操作权限 */
		validateAuth(appId, uid);
		
		/* 查询用户信息，校验目标用户是否存在 */
		u = getUserFromCache(targetUid);
		
		/* 检验重复操作 */
		if (u.getStatus() == UserConstants.ACCOUNT_STATUS_DISABLED)
		{
			//Logger
			throw new InternalServerException(BUSINESS_CODE.DISABLE_USER_OPERATION_REPEATED, "User is disabled");
		}
		
		if (UserDAO.updateStatus(targetUid, UserConstants.ACCOUNT_STATUS_DISABLED) == 0)
		{
			Logger.error("Fail to disable user, uid: %d", targetUid);
			ret = false;
		}
		
		/* 同步 */
		if (ret)
		{
			u.setStatus(UserConstants.ACCOUNT_STATUS_DISABLED);
			
			//将User更新至缓存
			if (!putUserInCache(u))
			{
				Logger.error("Fail to put User in cache, uid: %d", u.getUid());
			}
			
			authorizeSynComponent = new UserAuthorizeSynCompont();
			uvo = new UserVO();
			BeanCopierUtils.copy(u, uvo);
			Logger.debug("Syn user after disabling user, entity: %s", (new Gson()).toJson(uvo));
			authorizeSynComponent.synAuthroizeUserStatus(uvo);
		}
		
	    return ret;
    }

	@Override
    public boolean enableUser(int appId, String accessToken, long uid, long targetUid) throws BusinessException
    {
		Logger.debug("UserStatusComponent, enable user, appId: %d, accessToken: %s, uid: %d, targetUid: %d", appId, accessToken, uid, targetUid);
		
		boolean ret = true;
		User u = null;
		UserAuthorizeSynCompont authorizeSynComponent = null;
		UserVO uvo = null;
		
		/* 校验appId有效性 */
		validateAppId(appId);
		
		/* 校验uid */
		validateUid(uid);
		
		/* 校验targetId */
		validateUid(targetUid);
		
		/* 校验accessToken有效性 */
		validateAccessToken(accessToken, uid, appId);
		
		/* 检验操作权限 */
		validateAuth(appId, uid);
		
		/* 查询用户信息，校验目标用户是否存在 */
		u = getUserFromCache(targetUid);
		
		/* 检验重复操作 */
		if (u.getStatus() == UserConstants.ACCOUNT_STATUS_ENABLED)
		{
			//Logger
			throw new InternalServerException(BUSINESS_CODE.ENABLE_USER_OPERATION_REPEATED, "User is enabled");
		}
		
		if (UserDAO.updateStatus(targetUid, UserConstants.ACCOUNT_STATUS_ENABLED) == 0)
		{
			Logger.error("Fail to enable user, uid: %d", targetUid);
			ret = false;
		}
		
		/* 同步 */
		if (ret)
		{
			u.setStatus(UserConstants.ACCOUNT_STATUS_ENABLED);
			
			//将User更新至缓存
			if (!putUserInCache(u))
			{
				Logger.error("Fail to put User in cache, uid: %d", u.getUid());
			}
			
			authorizeSynComponent = new UserAuthorizeSynCompont();
			uvo = new UserVO();
			BeanCopierUtils.copy(u, uvo);
			Logger.debug("Syn user after enabling user, entity: %s", (new Gson()).toJson(uvo));
			authorizeSynComponent.synAuthroizeUserStatus(uvo);
		}
		
	    return ret;
    }

	@Override
    public boolean getUserStatus(int appId, String accessToken, long uid, long targetUid) throws BusinessException
    {
		Logger.debug("UserStatusComponent, get user status, appId: %d, accessToken: %s, uid: %d, targetUid: %d", appId, accessToken, uid, targetUid);
		
		User u = null;
		
		/* 校验appId有效性 */
		validateAppId(appId);

		/* 校验uid */
		validateUid(uid);
		
		/* 校验targetId */
		validateUid(targetUid);
		
		/* 校验accessToken有效性 */
		validateAccessToken(accessToken, uid, appId);
		
		/* 检验操作权限 */
		validateAuth(appId, uid);
		
		/* 查询用户信息，校验目标用户是否存在 */
		u = getUserFromCache(targetUid);
		
		if (u.getStatus() == UserConstants.ACCOUNT_STATUS_ENABLED)
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
	 * 验证用户是否有权限管理普通用户的状态  
	 *    
	 * @param appId
	 * @param uid
	 * @throws BusinessException
	 */
	protected void validateAuth(int appId, long uid) throws BusinessException
	{
		//TODO 后续完善权限控制
	}
}