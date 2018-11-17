package com.xrk.uiac.bll.component;

import com.xrk.uiac.bll.exception.BusinessException;

/**
 * 
 * 用户账号状态管理接口
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：yexx<yexiaoxiao@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年5月18日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public interface IUserStatusComponent
{
	
	/**
	 * 
	 * 禁用账号
	 *    
	 * @param appId			该操作的来源appId
	 * @param accessToken	accessToken
	 * @param uid			目标用户uid
	 * @return
	 * @throws BusinessException
	 */
	boolean disableUser(int appId, String accessToken, long uid, long targetUid) throws BusinessException;
	
	/**
	 * 
	 * 解禁账号 
	 *    
	 * @param appId			该操作的来源appId
	 * @param accessToken	accessToken
	 * @param uid			目标用户uid
	 * @return
	 * @throws BusinessException
	 */
	boolean enableUser(int appId, String accessToken, long uid, long targetUid) throws BusinessException;
	
	/**
	 * 
	 * 获取用户状态
	 *    
	 * @param appId			该操作的来源appId
	 * @param accessToken	accessToken
	 * @param uid			目标用户uid
	 * @return				1, 账号被禁用; 2, 账号未被禁用
	 * @throws BusinessException
	 */
	boolean getUserStatus(int appId, String accessToken, long uid, long targetUid) throws BusinessException;
}
