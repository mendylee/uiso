package com.xrk.uiac.bll.component;

import com.xrk.uiac.bll.exception.BusinessException;
import com.xrk.uiac.bll.response.CheckParameterResponse;
import com.xrk.uiac.bll.response.CreateUserResponse;
import com.xrk.uiac.bll.response.GetUserInfoResponse;

/**
 * 
 * 用户基本信息操作接口
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：yexx<yexiaoxiao@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年5月13日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public interface IUserComponent
{
	
	/**
	 * 
	 * 验证用户编码是否被占用
	 *    
	 * @param appId			应用id
	 * @param userCode		用户编码，默认为手机，暂时只支持手机
	 * @return				返回参数验证的实体
	 * @throws BusinessException
	 */
	CheckParameterResponse checkParameter(int appId, String userCode) throws BusinessException;
	
	/**
	 * 
	 * 账号注册接口
	 *    
	 * @param appId				应用id
	 * @param userCode			用户编码，默认为手机
	 * @param password			密码
	 * @param userInfo			用户基本信息
	 * @param extendInfo		用户扩展信息
	 * @param unverified		是否需要验证码校验结果，默认需要
	 * @return
	 * @throws BusinessException
	 */
	CreateUserResponse createUser(int appId, String userCode, String password, String userInfo, String extendInfo, boolean unverified) throws BusinessException;
	
	/**
	 * 
	 * 注册失败后，调用此接口回滚之前插入的数据 
	 *    
	 * @param appId				应用id
	 * @param userCode			用户编码，默认为手机
	 * @param password			用户密码
	 * @return
	 * @throws BusinessException
	 */
	boolean rollbackUser(int appId, String userCode, String password) throws BusinessException;
	
	/**
	 * 
	 * 通过用户uid获取用户基本信息
	 *    
	 * @param appId			应用id
	 * @param accessToken	token
	 * @param uid			用户uid
	 * @return
	 * @throws BusinessException
	 */
	GetUserInfoResponse getUserInfo(int appId, String accessToken, long uid) throws BusinessException;
	
	/**
	 * 
	 * 通过用户账号获取用户基本信息
	 *    
	 * @param appId			应用id
	 * @param accessToken	token
	 * @param account		账号
	 * @return
	 * @throws BusinessException
	 */
	GetUserInfoResponse getUserInfo(int appId, String accessToken, String account) throws BusinessException;
	
	/**
	 * 
	 * 更新用户信息接口
	 *    
	 * @param uid			用户uid
	 * @param userInfo		用户基本信息
	 * @param extendInfo	用户扩展信息
	 * @return
	 * @throws BusinessException
	 */
	boolean updateUserInfo(int appId, String accessToken, long uid, String userInfo, String extendInfo) throws BusinessException;
	
	/**
	 * 
	 * 通过用户编码, 获取用户uid 
	 *    
	 * @param userCode
	 * @return
	 * @throws BusinessException
	 */
	long getUidWithUserCode(String userCode) throws BusinessException;
	
	/**
	 * 
	 * 获取问吧的临时Id 
	 *    
	 * @param appId			应用id
	 * @return
	 * @throws BusinessException
	 */
	long getWenbaTempId(int appId) throws BusinessException;
	
	/**
	 * 
	 * 同步用户信息,UIAC上线后部分旧系统历史数据会将手机号传递过来进行修改
	 *    
	 * @param appId
	 * @param mobile
	 * @return
	 * @throws BusinessException
	 */
	boolean synUserAccount(long appId, String mobile) throws BusinessException;
	
	/**
	 * 
	 * 将指定账号（已删除）重新恢复为可用（未删除）状态。
	 * 只用在账号升级的业务逻辑里，即进行旧账号升级时，先调用此方法将用户恢复为可用状态，再调用synUserAccount方法更改用户的账号，完成账号升级。 
	 *    
	 * @param appId
	 * @param account
	 * @return
	 * @throws BusinessException
	 */
	boolean recoverUser(int appId, long uid) throws BusinessException;
}
