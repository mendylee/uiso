package com.xrk.uiac.bll.component.impl;

import com.google.gson.Gson;
import com.xrk.hws.common.logger.Logger;
import com.xrk.uiac.bll.common.ParameterUtils;
import com.xrk.uiac.bll.common.UserConstants;
import com.xrk.uiac.bll.component.IUserPasswordComponent;
import com.xrk.uiac.bll.exception.BUSINESS_CODE;
import com.xrk.uiac.bll.exception.BusinessException;
import com.xrk.uiac.bll.exception.VerifyException;
import com.xrk.uiac.bll.vo.UserVO;
import com.xrk.uiac.common.utils.BeanCopierUtils;
import com.xrk.uiac.dal.dao.UserDAO;
import com.xrk.uiac.dal.entity.User;

/**
 * 
 * 用户密码相关操作组件
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：yexx<yexiaoxiao@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年5月18日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public class UserPasswordComponent extends BaseUserComponent implements IUserPasswordComponent
{
	@Override
    public boolean updatePassword(int appId, String accessToken, long uid, String oldPwd, String newPwd, boolean unverified) throws BusinessException
    {
		Logger.debug("UserPasswordComponent, update password, appId: %d, accessToken: %s, uid: %d, oldPwd: %s, newPwd: %s, unverified: %s", 
				appId, accessToken, uid, oldPwd, newPwd, unverified);
		
		boolean ret = true;
		User u = null;
		String encOldPwd = null;
		String encNewPwd = null;
		UserAuthorizeSynCompont authorizeSynComponent = null;
		UserVO uvo = null;
		
		/* 校验appId有效性 */
		validateAppId(appId);
		
		/* 校验uid */
		validateUid(uid);
		
		/* 校验accessToken有效性 */
		validateAccessToken(accessToken, uid, appId);
		
		/* 获取用户信息，校验用户是否存在 */
		u = getUserFromCache(uid);
		
		/* 校验密码格式 */
		if (!ParameterUtils.isValidPassword(oldPwd) || !ParameterUtils.isValidPassword(newPwd))
		{
			Logger.error("Password invalid, uid: %d, old: %s, new: %s", uid, oldPwd, newPwd);
			throw new VerifyException(BUSINESS_CODE.UPDATE_PASSWORD_PASSWORD_INVALID, "Password is invalid");
		}
		
		if (!unverified)
		{
			/* 校验验证码的验证状态 */
			validateCaptcha(UserConstants.CAPTCHA_CHECKTYPE_RESET_PASSWORD, u.getAccount());
		}
		
		/* 校验旧密码是否正确 */
		encOldPwd = ParameterUtils.encryptPassword(oldPwd, u.getAccount());
		if (encOldPwd == null || !encOldPwd.equals(u.getPassword()))
		{
			Logger.error("Old password is wrong, uid: %d", uid);
			throw new VerifyException(BUSINESS_CODE.UPDATE_PASSWORD_OLD_PASSWORD_WRONG, "Old password is wrong");
		}
		
		/* 对密码进行MD5加密 */
		encNewPwd = ParameterUtils.encryptPassword(newPwd, u.getAccount());
		if (encNewPwd == null)
		{
			Logger.error("make an error while encrypting password, password: %s", newPwd);
			throw new VerifyException(BUSINESS_CODE.UPDATE_PASSWORD_PASSWORD_INVALID, "make an error while encrypting password");
		}
		
		/* 更新密码 */
		if (UserDAO.updatePassword(uid, encNewPwd) == 0)
		{
			Logger.error("Fail to update password, uid: %d, pwd: %s", uid, encNewPwd);
			ret = false;
		}
		
		/* 同步 */
		if (ret)
		{
			u.setPassword(encNewPwd);
			
			//将User更新至缓存
			if (!putUserInCache(u))
			{
				Logger.error("Fail to put User in cache, uid: %d", uid);
			}
			
			authorizeSynComponent = new UserAuthorizeSynCompont();
			uvo = new UserVO();
			BeanCopierUtils.copy(u, uvo);
			Logger.debug("Syn user after updating password, new password: %s, entity: %s", newPwd, (new Gson()).toJson(uvo));
			authorizeSynComponent.synEditPassWdAuthroizeUser(uvo);
		}
		
	    return ret;
    }

	@Override
    public boolean resetPassword(int appId, long uid, String password, boolean unverified) throws BusinessException
    {
		Logger.debug("UserPasswordComponent, reset password, appId: %d, uid: %d, password: %s, unverified: %s", appId, uid, password, unverified);
		
		boolean ret = true;
		User u = null;
		String encNewPwd = null;
		UserAuthorizeSynCompont authorizeSynComponent = null;
		UserVO uvo = null;
		
		/* 校验appId有效性 */
		validateAppId(appId);
		
		/* 校验uid */
		validateUid(uid);

		/* 获取用户信息，校验用户是否存在 */
		u = getUserFromCache(uid);
		
		/* 校验密码格式 */
		if (!ParameterUtils.isValidPassword(password))
		{
			Logger.error("Password is invalid, password: %s", (password == null) ? "" : password);
			throw new VerifyException(BUSINESS_CODE.RESET_PASSWORD_PASSWORD_INVALID, "Password is invalid");
		}
		
		if (!unverified)
		{
			/* 校验验证码的验证状态 */
			validateCaptcha(UserConstants.CAPTCHA_CHECKTYPE_RESET_PASSWORD, u.getAccount());
		}
		
		/* 对密码进行MD5加密 */
		encNewPwd = ParameterUtils.encryptPassword(password, u.getAccount());
		if (encNewPwd == null)
		{
			Logger.error("make an error while encrypting password, password: %s", password);
			throw new VerifyException(BUSINESS_CODE.RESET_PASSWORD_PASSWORD_INVALID, "make an error while encrypting password");
		}
		
		/* 更新密码 */
		if (UserDAO.updatePassword(uid, encNewPwd) == 0)
		{
			Logger.error("Fail to update password, uid: %d, pwd: %s", uid, encNewPwd);
			ret = false;
		}
		
		/* 同步 */
		if (ret)
		{
			u.setPassword(encNewPwd);
			
			//将User更新至缓存
			if (!putUserInCache(u))
			{
				Logger.error("Fail to put User in cache, uid: %d", u.getUid());
			}
			
			authorizeSynComponent = new UserAuthorizeSynCompont();
			uvo = new UserVO();
			BeanCopierUtils.copy(u, uvo);
			Logger.debug("Syn user after resetting password, new password: %s, entity: %s", password, (new Gson()).toJson(uvo));
			authorizeSynComponent.synEditPassWdAuthroizeUser(uvo);
		}
		
	    return ret;
    }
}
