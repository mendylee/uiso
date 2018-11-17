package com.xrk.uiac.bll.component.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.xrk.hws.common.logger.Logger;
import com.xrk.uiac.bll.cache.CacheService;
import com.xrk.uiac.bll.cache.component.AppInfoCache;
import com.xrk.uiac.bll.cache.component.UserSubAccountCache;
import com.xrk.uiac.bll.cache.component.lock.LockClient;
import com.xrk.uiac.bll.common.ParameterUtils;
import com.xrk.uiac.bll.common.SeqUtils;
import com.xrk.uiac.bll.common.UserConstants;
import com.xrk.uiac.bll.component.IUserSubAccountComponent;
import com.xrk.uiac.bll.exception.BUSINESS_CODE;
import com.xrk.uiac.bll.exception.BusinessException;
import com.xrk.uiac.bll.exception.ConflictException;
import com.xrk.uiac.bll.exception.InternalServerException;
import com.xrk.uiac.bll.exception.NotFoundException;
import com.xrk.uiac.bll.exception.VerifyException;
import com.xrk.uiac.bll.response.GetSubAccountInfoResponse;
import com.xrk.uiac.bll.vo.SubAccountLockVO;
import com.xrk.uiac.dal.dao.UserSubAccountDAO;
import com.xrk.uiac.dal.entity.AppInfo;
import com.xrk.uiac.dal.entity.UserSubAccount;

public class UserSubAccountComponent extends BaseUserComponent implements IUserSubAccountComponent
{
	private AppInfoCache appInfoCache = (AppInfoCache) CacheService.GetService(AppInfoCache.class);
	private UserSubAccountCache subAccountCache = (UserSubAccountCache) CacheService.GetService(UserSubAccountCache.class);
	
	@Override
	public boolean bindSubAccount(int appId, String accessToken, long uid, String tempId, int subAppId) throws BusinessException
	{
		SubAccountLockVO lockVO = new SubAccountLockVO();
		try
		{
			return processBinding(appId, accessToken, uid, tempId, subAppId, lockVO);
		}
		catch (BusinessException e)
		{
			throw e;
		}
		finally
		{
			if (lockVO != null && lockVO.isLocked())
			{
				LockClient.getInstance().unlockSubAccount(lockVO.getSubAccount(), lockVO.getSubAppId());
			}
		}
	}
	
	private boolean processBinding(int appId, String accessToken, long uid, String tempId, int subAppId, SubAccountLockVO lockVO) throws BusinessException
	{
		Logger.debug("UserSubAccountComponent, bind sub account, appId: %d, accessToken: %s, uid: %d, tempId: %s, subAppId: %d", 
				appId, accessToken, uid, tempId, subAppId);
		
		boolean ret = true;
		UserSubAccount subAccount = null;
		
		/* 校验appId有效性 */
		validateAppId(appId);
		//validateAppId(subAppId);
		
		/* 校验uid */
		validateUid(uid);
		
		/* 校验accessToken有效性 */
		validateAccessToken(accessToken, uid, appId);
		
		/* 校验用户是否存在 */
		getUserFromCache(uid);
		
		/* 校验tempId */
		if (tempId == null || tempId.isEmpty())
		{
			Logger.error("Invalid tempId, uid: %d, tempId: %s", uid, tempId);
			throw new VerifyException(BUSINESS_CODE.BINDING_SUBACCOUNT_TEMPID_INVALID, "Invalid tempId");
		}
		
		/* 检验subAppId有效性 */
		if (!ParameterUtils.isValidAppId(subAppId))
		{
			Logger.error("Invalid subAppId, uid: %d, subAppId: %d", uid, subAppId);
			throw new VerifyException(BUSINESS_CODE.BINDING_SUBACCOUNT_SUB_APPID_INVALID, "Invalid subAppId");
		}
		
		if (!LockClient.getInstance().lockSubAccount(tempId, subAppId))
		{
			//子账号和子账号ID已被锁住
			Logger.error("subAccount is locked by another operation, subAccount: %s, subAppId: %d", tempId, subAppId);
			throw new ConflictException(BUSINESS_CODE.ACTION_IS_LOCKED, "subAccount is locked by another operation");
		}
		if (lockVO != null)
		{
			lockVO.setLocked(true);
			lockVO.setSubAccount(tempId);
			lockVO.setSubAppId(subAppId);
		}
		
		/* 校验是否重复绑定 */
		//subAccount = UserSubAccountDAO.findWithUidAppid(uid, subAppId);
		subAccount = UserSubAccountDAO.findWithSubAccountAppID(tempId, appId, subAppId);
		
		if (subAccount != null)
		{
			//已绑定该app账号, 不可重复绑定
			Logger.error("SubAccount is existed, uid: %d, subAppId: %d", uid, subAppId);
			throw new ConflictException(BUSINESS_CODE.BINDING_SUBACCOUNT_OPERATION_REPEATED, "bind sub account operation is repeated");
		}
		
		subAccount = new UserSubAccount();
		//subAccount.setSubAccountId(SeqUtils.getSubAccountId());
		subAccount.setAppId(appId);
		subAccount.setUid(uid);
		subAccount.setAddDate(new Date());
		subAccount.setAccount(tempId);
		subAccount.setBindAppId(subAppId);
		
		long subAccountId = SeqUtils.getSubAccountId();
		if (subAccountId != -1)
		{
			subAccount.setSubAccountId(subAccountId);
		}
		else
		{
			Logger.error("Fail to generate subAccountId, uid: %d, appId: %d, account: %s", uid, subAppId, tempId);
			throw new InternalServerException(BUSINESS_CODE.INTERNAL_SERVER_ERROR, "Fail to generate subAccountId");
		}
		
		/* 绑定该子账号 */
		if (UserSubAccountDAO.insertSubAccount(subAccount) == 0)
		{
			Logger.error("Fail to insert subaccount, uid: %d, subAppId: %d", uid, subAppId);
			ret = false;
		}
				
		return ret;
	}

	@Override
	public boolean unbindSubAccount(int appId, String accessToken, long uid, String subAccount, int subAppId) throws BusinessException
	{
		Logger.debug("UserSubAccountComponent, unbind sub account, appId: %d, accessToken: %s, uid: %d, subAppId: %d", appId, accessToken, uid, subAppId);
		
		boolean ret = true;
		UserSubAccount userSubAccount = null;
		
		/* 校验appId有效性 */
		validateAppId(appId);
		//validateAppId(subAppId);
		
		/* 校验uid */
		validateUid(uid);
		
		/* 校验accessToken有效性 */
		validateAccessToken(accessToken, uid, appId);
		
		/* 校验用户是否存在 */
		getUserFromCache(uid);
		
		/* 检验subAppId有效性 */
		if (!ParameterUtils.isValidAppId(subAppId))
		{
			Logger.error("Invalid subAppId, uid: %d, subAppId: %d", uid, subAppId);
			throw new VerifyException(BUSINESS_CODE.UNBINDING_SUBACCOUNT_SUB_APPID_INVALID, "Invalid subAppId");
		}
		
		/* 检验子账号是否存在 */
		/* 后续再决定是否需要这一步操作 */
		//userSubAccount = UserSubAccountDAO.findWithUidAppid(uid, subAppId);
		userSubAccount = UserSubAccountDAO.findWithSubAccountAppID(subAccount, appId, subAppId);
		
		if (userSubAccount == null)
		{
			Logger.error("SubAccount not found, uid: %d, subAppId: %d", uid, subAppId);
			throw new NotFoundException(BUSINESS_CODE.UNBINGDING_SUBACCOUNT_NOT_FOUND, "SubAccount not found");
		}
		
		/* 子账号解绑 */
		//if (UserSubAccountDAO.deleteWithUidAppid(uid, subAppId) == 0)
		if(UserSubAccountDAO.deleteWithUidSubAccount(subAccount, appId, subAppId) == 0)
		{
			//解绑(删除)失败
			Logger.error("Fail to delete subAccount, uid:%d, subAppId: %d",  uid, subAppId);
			ret = false;
		}
		
		//移除缓存
		String key = getCacheKey(appId, subAppId, subAccount);
		subAccountCache.remove(key);
		
		return ret;
	}

	@Override
	public List<GetSubAccountInfoResponse> getSubAccountList(int appId, String accessToken, long uid) throws BusinessException
	{
		Logger.debug("UserSubAccountComponent, get sub account list, appId: %d, accessToken: %s, uid: %d", appId, accessToken, uid);
		
		List<GetSubAccountInfoResponse> responseList = null;
		List<UserSubAccount> subAccList = null;
		
		/* 校验appId有效性 */
		validateAppId(appId);
		
		/* 校验uid */
		validateUid(uid);
		
		/* 校验accessToken有效性 */
		validateAccessToken(accessToken, uid, appId);
		
		/* 校验用户是否存在 */
		getUserFromCache(uid);
		
		/* 获取指定应用下的子账号列表 */
		subAccList = UserSubAccountDAO.findListWithUidAndAppId(uid, appId);
		
		responseList = new ArrayList<GetSubAccountInfoResponse>();
		if (subAccList != null)
		{
			for (UserSubAccount userSubAccount : subAccList) 
			{
	            if (userSubAccount != null)
	            {
	            	GetSubAccountInfoResponse responseItem = getSubAccountResponse(userSubAccount);
	            	if(responseItem != null){
	            		responseList.add(responseItem);
	            	}
	            }
            }
		}
		
		return responseList;
	}

	private GetSubAccountInfoResponse getSubAccountResponse(UserSubAccount userSubAccount)
    {
		GetSubAccountInfoResponse responseItem = null;
		
		AppInfo appInfo = appInfoCache.get(userSubAccount.getAppId());
    	if (appInfo != null)
    	{
    		responseItem = new GetSubAccountInfoResponse();
    		responseItem.setUid(userSubAccount.getUid());
    		responseItem.setAccount(userSubAccount.getAccount());
    		responseItem.setAppId(String.valueOf(userSubAccount.getAppId()));
    		responseItem.setAppName(appInfo.getAppName());
    		responseItem.setThirdParty(appInfo.getIsThirdparty() == UserConstants.APP_IS_THIRDPARTY);    		
    	}
    	else
    	{
    		Logger.error("appInfo is null, appId : %d", userSubAccount.getAppId());
    	}
    	
    	appInfo = appInfoCache.get(userSubAccount.getBindAppId());
    	if (responseItem != null && appInfo != null)
    	{
    		responseItem.setBindAppId(String.valueOf(userSubAccount.getBindAppId()));
    		responseItem.setBindAppName(appInfo.getAppName());
    	}
	    return responseItem;
    }

	@Override
    public GetSubAccountInfoResponse getSubAccount(int appId, String subAccount, int subAppId)
                                                                                   throws BusinessException
    {
		Logger.debug("GetSubAccountInfoResponse, get sub account, appId: %d, subAccount: %s, bindAppId:%s"
				, appId, subAccount, subAppId);
		/* 校验appId有效性 */
		validateAppId(appId);
		validateAppId(subAppId);
		
		String key = getCacheKey(appId, subAppId, subAccount);
		GetSubAccountInfoResponse response = subAccountCache.get(key);
		if(response == null){
			UserSubAccount account = UserSubAccountDAO.findWithSubAccountAppID(subAccount, appId, subAppId);
			if(account != null){
				response = getSubAccountResponse(account);
				subAccountCache.put(key, response);
			}
		}
		
	    return response;
    }
	
	private String getCacheKey(int appId, int subAppId, String subAccount){
		return String.format("%s_%s_%s", appId, subAppId, subAccount);
	}
}