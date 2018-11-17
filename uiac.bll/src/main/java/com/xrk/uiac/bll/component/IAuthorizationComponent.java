package com.xrk.uiac.bll.component;

import java.util.List;

import com.xrk.uiac.bll.exception.BusinessException;
import com.xrk.uiac.bll.vo.UserAuthorizationVO;

/**
 * 授权业务处理组件
 * IAuthorizeComponent: IAuthorizeComponent.java.
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：shunchiguo<shunchiguo@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年5月6日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public interface IAuthorizationComponent
{
	/**
	 * 执行登录操作，验证登录信息，生成登录相关数据
	 *    
	 * @param uid 用户ID
	 * @param appId
	 * @param passwd
	 * @param timestamp
	 * @param scope
	 * @param expireTime
	 * @return
	 */
	UserAuthorizationVO login(long uid, String appId, String passwd, long timestamp, String scope, long expireTime) throws BusinessException;

	/**
	 * 
	 * 免密登录
	 *    
	 * @param uid
	 * @param appId
	 * @param scope
	 * @param expireTime
	 * @return
	 * @throws BusinessException
	 */
	UserAuthorizationVO login(long uid, String appId, String scope, long expireTime) throws BusinessException;
	
	/**
	 * 
	 * 用户通过手机号和密码登录  
	 *    
	 * @param uid
	 * @param mobile
	 * @param appId
	 * @param captcha
	 * @param scope
	 * @param expireTime
	 * @return
	 * @throws BusinessException
	 */
	UserAuthorizationVO login(long uid, String mobile,  String appId,  String captcha,  String scope, long expireTime) throws BusinessException;
	
	/**
	 * 
	 * 使用子帐号免密登录，此时仅判断子帐号有效性即发放授权Token  
	 *    
	 * @param subAccount
	 * @param appId
	 * @param subAppId
	 * @param scope
	 * @param expireTime
	 * @return
	 * @throws BusinessException
	 */
	UserAuthorizationVO login(String subAccount, String appId, String subAppId,  String scope, long expireTime) throws BusinessException;
	
	/**
	 * 更新访问授权码.  
	 *    
	 * @param refreshToken
	 * @param accessToken
	 * @return
	 */
	UserAuthorizationVO updateToken(long uid, String appId, String refreshToken, String accessToken) throws BusinessException;
	
	/**
	 * 
	 * 查询授权访问码的详细信息  
	 *    
	 * @param accessToken
	 * @return
	 */
	UserAuthorizationVO queryToken(String accessToken) throws BusinessException;
	
	/**
	 * 
	 * 获取指定用户的授权信息  
	 *    
	 * @param uid
	 * @return
	 */
	List<UserAuthorizationVO> queryUserToken(long uid) throws BusinessException;
	
	/**
	 * 
	 * 注销指定用户ID的所有登录信息  
	 *    
	 * @param uid
	 * @return
	 * @throws BusinessException
	 */
	
	boolean logout(long uid) throws BusinessException;
	/**
	 * 使用已获取的访问授权码注销
	 *    
	 * @param accessToken
	 * @return
	 */
	boolean logout(String accessToken) throws BusinessException;
	/**
	 *  使用用户名及所在应用ID注销
	 *    
	 * @param uid
	 * @param appId
	 * @return
	 */
	boolean logout(long uid, String appId) throws BusinessException;
}
