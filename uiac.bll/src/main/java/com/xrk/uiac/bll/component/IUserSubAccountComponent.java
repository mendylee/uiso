package com.xrk.uiac.bll.component;

import java.util.List;

import com.xrk.uiac.bll.exception.BusinessException;
import com.xrk.uiac.bll.response.GetSubAccountInfoResponse;

/**
 * 
 * 用户子账号管理接口
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：yexx<yexiaoxiao@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年5月19日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public interface IUserSubAccountComponent
{
	/**
	 * 
	 * 绑定子账号
	 *    
	 * @param appId			appId
	 * @param accessToken	accessToken
	 * @param uid			用户uid
	 * @param tempId		子账号临时id
	 * @return
	 * @throws BusinessException
	 */
	boolean bindSubAccount(int appId, String accessToken, long uid, String tempId, int subAppId) throws BusinessException;
	
	
	/**
	 * 
	 * 子账号解绑  
	 *    
	 * @param appId			appId
	 * @param accessToken	accessToken
	 * @param uid			用户uid
	 * @param subAccount	要解绑的账号id
	 * @param subAppId  子帐号所在应用
	 * @return
	 * @throws BusinessException
	 */
	boolean unbindSubAccount(int appId, String accessToken, long uid, String subAccount, int subAppId) throws BusinessException;
	
	/**
	 * 
	 * 根据指定的子帐号及绑定应用信息，查询用户信息  
	 *    
	 * @param appId
	 * @param subAccount
	 * @param subAppId
	 * @return
	 * @throws BusinessException
	 */
	GetSubAccountInfoResponse getSubAccount(int appId, String subAccount, int subAppId) throws BusinessException;
	
	/**
	 * 
	 * 获取子账号列表
	 *    
	 * @param appId
	 * @param accessToken
	 * @param uid
	 * @return
	 * @throws BusinessException
	 */
	List<GetSubAccountInfoResponse> getSubAccountList(int appId, String accessToken, long uid) throws BusinessException;
}