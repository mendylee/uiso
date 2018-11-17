package com.xrk.uiac.bll.component.impl;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.google.gson.Gson;
import com.xrk.hws.common.logger.Logger;
import com.xrk.uiac.bll.common.SeqUtils;
import com.xrk.uiac.bll.common.UserConstants;
import com.xrk.uiac.bll.component.IUserBindingComponent;
import com.xrk.uiac.bll.exception.BUSINESS_CODE;
import com.xrk.uiac.bll.exception.BusinessException;
import com.xrk.uiac.bll.exception.InternalServerException;
import com.xrk.uiac.bll.response.GetBindingStatusResponse;
import com.xrk.uiac.bll.vo.UserVO;
import com.xrk.uiac.common.utils.BeanCopierUtils;
import com.xrk.uiac.dal.dao.AccountChangeRecordDAO;
import com.xrk.uiac.dal.dao.UserDAO;
import com.xrk.uiac.dal.dao.UserInfoDAO;
import com.xrk.uiac.dal.entity.AccountChangeRecord;
import com.xrk.uiac.dal.entity.User;
import com.xrk.uiac.dal.entity.UserInfo;

/**
 * 
 * 用户绑定业务接口实现类
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：yexx<yexiaoxiao@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年5月19日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public class UserBindingComponent extends BaseUserComponent implements IUserBindingComponent
{
	private ExecutorService bgThread = Executors.newSingleThreadExecutor();
	
	@Override
	public GetBindingStatusResponse getBindingStatus(int appId, String accessToken, long uid) throws BusinessException
	{
		Logger.debug("UserBindingComponent, get binding status, appId: %d, accessToken: %s, uid: %d", appId, accessToken, uid);
		
		UserInfo ui = null;
		GetBindingStatusResponse response = null;
		
		/* 校验appId有效性 */
		validateAppId(appId);
		
		/* 校验uid */
		validateUid(uid);
		
		/* 校验accessToken有效性 */
		validateAccessToken(accessToken, uid, appId);
		
		/* 校验用户是否存在 */
		getUserFromCache(uid);
		
		ui = getUserInfoFromCache(uid);
		
		response = new GetBindingStatusResponse();
		if (ui.getMobileIsVerify() == UserConstants.MOBILE_IS_VERIFIED)
		{
			response.setMobile(true);
		} 
		else
		{
			response.setMobile(false);
		}
		
		return response;
	}

	@Override
	public boolean bindMobile(int appId, String accessToken, long uid, String mobile, boolean unverified) throws BusinessException
	{
		Logger.debug("UserBindingComponent, bind mobile, appId: %d, accessToken: %s, uid: %d, mobile: %s, unverified: %s", 
				appId, accessToken, uid, mobile, unverified);
		
		User u = null;
		UserInfo ui = null;
		boolean ret = true;
		int userCodeType = 0;
		UserAuthorizeSynCompont authorizeSynComponent = null;
		UserVO uvo = null;
		AccountChangeRecord record = null;
		String oldMobile = null;
		int oldBindingStatus = 0;
		
		/* 校验手机号是否合法 */
		validateMobile(mobile);
		
		if (!unverified)
		{
			/* 校验验证码状态 */
			validateCaptcha(UserConstants.CAPTCHA_CHECKTYPE_BINDING, mobile);
		}
		
		/* 校验appId有效性 */
		validateAppId(appId);
		
		/* 校验uid */
		validateUid(uid);
		
		/* 校验accessToken有效性 */
		validateAccessToken(accessToken, uid, appId);
		
		/* 获取用户信息，校验用户是否存在 */
		u = getUserFromCache(uid);
		userCodeType = getUserCodeType(u.getAccount());
		if (userCodeType == 0)
		{
			Logger.error("Account is invalid, account: %s", u.getAccount());
			throw new InternalServerException(BUSINESS_CODE.INTERNAL_SERVER_ERROR, "Account is invalid");
		}
		
		/* 判断手机号是否已被绑定 */
		ui = UserInfoDAO.findWithMobile(mobile);
		if (ui != null && ui.getMobileIsVerify() == UserConstants.MOBILE_IS_VERIFIED)
		{
			if (ui.getUid() == uid)
			{
				//用户已经绑定手机, 重复操作
				Logger.error("The binding-mobile operation is repeated, uid: %d, mobile: %s", uid, mobile);
				throw new InternalServerException(BUSINESS_CODE.BINDING_MOBILE_OPERATION_REPEATED, "The binding-mobile operation is repeated");
			}
			else 
			{
				//手机号已被他人占用
				Logger.error("The mobile number is occupied, uid: %d, mobile: %s", uid, mobile);
				throw new InternalServerException(BUSINESS_CODE.BINDING_MOBILE_NUMBER_OCCUPIED, "The mobile number is occupied");
			}
		}
		
		/* 获取用户自己的用户信息 */
		ui = getUserInfoFromCache(uid);
		oldMobile = ui.getMobile();
		oldBindingStatus = ui.getMobileIsVerify();
		if (UserInfoDAO.updateMobileBindingStatus(uid, mobile, UserConstants.MOBILE_IS_VERIFIED) == 0)
		{
			Logger.error("Fail to bind mobile, uid: %d, mobile: %s", uid, mobile);
			ret = false;
		}
		else
		{
			//假如用户是以手机号为主账号，则更新成功之后，必须将主账号也更新为最新的手机号
			if (userCodeType == UserConstants.USERCODE_TYPE_MOBILE)
			{
				//添加账号修改记录
				record = new AccountChangeRecord();
				record.setOldAccount(u.getAccount());
				record.setNewAccount(mobile);
				record.setAppId(appId);
				record.setAddDate(new Date());
				record.setUid(uid);
				record.setAccountType(userCodeType);
				record.setRemark(UserConstants.REMARK_BINDING_CHANGE_ACCOUNT);
				//record.setSerialId(SeqUtils.getAccountChangeSerialId());
				long serialId = SeqUtils.getAccountChangeSerialId();
				if (serialId != -1)
				{
					record.setSerialId(serialId);
				}
				else
				{
					Logger.error("Fail to generate accountChangeSerialId, uid: %d, oldAccount: %s, newAccount: %s", uid, u.getAccount(), mobile);
					throw new InternalServerException(BUSINESS_CODE.INTERNAL_SERVER_ERROR, "fail to generate serial id");
				}
				
				if (UserDAO.updateAccount(uid, mobile) == 0)
				{
					Logger.error("Fail to update account, uid: %d, account: %s", uid, mobile);
					ret = false;
				}
			}
		}
		
		//更新至缓存
		if (ret)
		{
			ui.setMobileIsVerify(UserConstants.MOBILE_IS_VERIFIED);
			ui.setMobile(mobile);
			
			if (!putUserInfoInCache(ui))
			{
				ret = false;
				Logger.error("Fail to put UserInfo in cache, uid: %d", uid);
			}
			else
			{
				u = new User();
				u = getUserFromCache(uid);
				u.setAccount(mobile);
				if (!putUserInCache(u))
				{
					ret = false;
					Logger.error("Fail to put User in cache, uid: %d", uid);
				}
				else
				{
					//当主账号为手机号时，重新绑定了手机号要将账号修改的状态同步
					if (userCodeType == UserConstants.USERCODE_TYPE_MOBILE)
					{
						//主账号已修改，因此删除旧账号缓存
						userCache.removeWithAccount(oldMobile);
						
						authorizeSynComponent = new UserAuthorizeSynCompont();
						uvo = new UserVO();
						BeanCopierUtils.copy(u, uvo);
						
						//修改账号，同步新的账号
						Logger.debug("Syn user after binding new mobile, mobile: %s, entity: %s", mobile, (new Gson()).toJson(uvo));
						if (!authorizeSynComponent.synAddAuthroizeUser(uvo))
						{
							ret = false;
							Logger.error("fail to syn user after binding new mobile, account: %s, uid: %d", uvo.getAccount(), uvo.getUid());
						}
						
						//将账号切换记录添加进数据库
						AccountChangeRecordDAO.insert(record);
					}
				}
			}
		}
		
		if (!ret)
		{
			rollback(uid, mobile, oldMobile, oldBindingStatus, userCodeType);
		}
		
		return ret;
	}

	@Override
	public boolean unBindMobile(int appId, String accessToken, long uid, boolean unverified) throws BusinessException
	{
		Logger.debug("UserBindingComponent, unbind mobile, appId: %d, accessToken: %s, uid: %d, unverified: %s", appId, accessToken, uid, unverified);
		
		User u = null;
		UserInfo ui = null;
		boolean ret = true;
		int userCodeType = 0;
		String oldMobile = null;
		
		/* 校验appId有效性 */
		validateAppId(appId);
		
		/* 校验uid */
		validateUid(uid);
		
		/* 校验accessToken有效性 */
		validateAccessToken(accessToken, uid, appId);
		
		/* 判断用户是否绑定了手机，校验用户是否存在 */
		u = getUserFromCache(uid);
		ui = getUserInfoFromCache(uid);
		
		if (!unverified)
		{
			/* 校验验证码状态 */
			validateCaptcha(UserConstants.CAPTCHA_CHECKTYPE_BINDING, ui.getMobile());
		}

		/* 主账号为手机号的用户不允许解绑手机 */
		userCodeType = getUserCodeType(u.getAccount());
		if (userCodeType == 0)
		{
			Logger.error("Account is invalid, account: %s", u.getAccount());
			throw new InternalServerException(BUSINESS_CODE.INTERNAL_SERVER_ERROR, "Account is invalid");
		}
		else if (userCodeType == UserConstants.USERCODE_TYPE_MOBILE)
		{
			//主账号为手机号的用户不允许解绑手机
			Logger.error("The unbinding-mobile operation is not allowed, uid: %d", uid);
			throw new InternalServerException(BUSINESS_CODE.UNBINDING_MOBILE_OPERATION_NOT_ALLOWED, "The unbinding-mobile operation is not allowed");
		}
		
		if (ui.getMobileIsVerify() == UserConstants.MOBILE_IS_NOT_VERIFIED)
		{
			//重复操作
			Logger.error("The unbinding-mobile operation is repeated");
			throw new InternalServerException(BUSINESS_CODE.UNBINDING_MOBILE_OPERATION_REPEATED, "The unbinding-mobile operation is repeated");
		}
		
		oldMobile = ui.getMobile();
		if (UserInfoDAO.updateMobileBindingStatus(uid, "", UserConstants.MOBILE_IS_NOT_VERIFIED) == 0)
		{
			ret = false;
		}
		
		//更新至缓存
		if (ret)
		{
			ui.setMobile("");
			ui.setMobileIsVerify(UserConstants.MOBILE_IS_NOT_VERIFIED);
			if (!putUserInfoInCache(ui))
			{
				ret = false;
				Logger.error("Fail to put UserInfo in cache, uid: %d", uid);
			}
		}
		
		if (!ret)
		{
			rollback(uid, null, oldMobile, UserConstants.MOBILE_IS_VERIFIED, userCodeType);
		}
		
		return ret;
	}
	
	/**
	 * 
	 * 绑定失败，将userinfo记录、user记录、userinfo缓存、user缓存的数据恢复到操作前的状态 
	 *    
	 * @param uid
	 * @param oldMobile
	 * @param oldBindingStatus
	 * @param userCodeType
	 */
	private void rollback(long uid, String newMobile, String oldMobile, int oldBindingStatus, int userCodeType)
	{
		bgThread.execute(new Runnable()
		{
			@Override
            public void run()
            {
				UserInfo ui = null;
				User u = null;
				try
				{
					ui = getUserInfoFromCache(uid);
				}
				catch (Exception e)
				{
					Logger.error("rollback, fail to get userinfo from cache, uid: %d", uid);
					return;
				}
				//恢复用户基本信息数据表的数据
				if (UserInfoDAO.updateMobileBindingStatus(uid, oldMobile, oldBindingStatus) != 0)
				{
					//恢复用户基本信息缓存的数据
					ui.setMobile(oldMobile);
					ui.setMobileIsVerify(oldBindingStatus);
					if (putUserInfoInCache(ui))
					{
						//假如用户账号类型是手机号，还要恢复用户账号
						if (userCodeType == UserConstants.USERCODE_TYPE_MOBILE && newMobile != null)
						{
							try
							{
								u = getUserFromCache(uid);
							}
							catch (Exception e)
							{
								Logger.error("rollback, fail to get user from cache, uid: %d", uid);
								return;
							}
							if (UserDAO.updateAccount(uid, oldMobile) != 0)
							{
								u.setAccount(oldMobile);
								//移除可能存在的缓存记录
								if (userCache.removeWithAccount(newMobile))
								{
									Logger.debug("remove success, %s", newMobile);
								}
								else
								{
									Logger.debug("remove fail, %s", newMobile);
								}
								
								if (putUserInCache(u))
								{
									Logger.debug("rollback successfully! uid: %d, oldMobile: %s, oldBindingStatus: %d", uid, oldMobile, oldBindingStatus);
								}
								else
								{
									Logger.error("rollback, fail to rollback user in cache, uid: %d, mobile: %s, bindingstatus: %d", uid, oldMobile, oldBindingStatus);
								}
							}
							else
							{
								Logger.error("rollback, fail to rollback user in db, uid: %d, account: %s", uid, oldMobile);
							}
						}
					}
					else
					{
						Logger.error("rollback, fail to rollback userinfo in cache, uid: %d, mobile: %s, bindingstatus: %d", uid, oldMobile, oldBindingStatus);
					}
				}
				else
				{
					Logger.error("rollback, fail to rollback userinfo in db, uid: %d, mobile: %s, bindingstatus: %d", uid, oldMobile, oldBindingStatus);
				}
            }	
		});
	}
}