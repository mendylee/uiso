package com.xrk.uiac.bll.component;

import com.xrk.uiac.bll.exception.BusinessException;

/**
 * 
 * 用户密码相关操作接口
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：yexx<yexiaoxiao@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年5月18日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public interface IUserPasswordComponent
{
	
	/**
	 * 
	 * 修改用户密码  
	 *    
	 * @param appId			appId
	 * @param accessToken	accessToken
	 * @param uid			用户uid
	 * @param oldPwd		旧密码, 原始密码经过MD5加密后的值
	 * @param newPwd		新密码, 原始新密码经过MD5加密后的值
	 * @return
	 * @throws BusinessException
	 */
	boolean updatePassword(int appId, String accessToken, long uid, String oldPwd, String newPwd, boolean unverified) throws BusinessException;
	
	/**
	 * 
	 * 重置用户密码  
	 *    
	 * @param appId			appId
	 * @param uid			用户uid
	 * @param password		新密码, 原始新密码经过MD5加密后的值
	 * @return
	 * @throws BusinessException
	 */
	boolean resetPassword(int appId, long uid, String password, boolean unverified) throws BusinessException;
}
