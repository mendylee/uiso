package com.xrk.uiac.bll.component;

import com.xrk.uiac.bll.exception.BusinessException;
import com.xrk.uiac.bll.response.GetBindingStatusResponse;

/**
 * 
 * 用户账号绑定接口
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：yexx<yexiaoxiao@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年5月18日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public interface IUserBindingComponent
{
	/**
	 * 
	 * 获取用户手机、邮箱的绑定状态, 暂时只返回手机绑定的结果
	 *    
	 * @param appId			appId
	 * @param accessToken	accessToken
	 * @param uid			用户uid
	 * @return
	 * @throws BusinessException
	 */
	GetBindingStatusResponse getBindingStatus(int appId, String accessToken, long uid) throws BusinessException;
	
	/**
	 * 
	 * 绑定手机号 
	 *    
	 * @param appId
	 * @param accessToken
	 * @param uid
	 * @param mobile
	 * @return
	 * @throws BusinessException
	 */
	boolean bindMobile(int appId, String accessToken, long uid, String mobile, boolean unverified) throws BusinessException;
	
	/**
	 * 
	 * 手机号解绑 
	 *    
	 * @param appId
	 * @param accessToken
	 * @param uid
	 * @return
	 * @throws BusinessException
	 */
	boolean unBindMobile(int appId, String accessToken, long uid, boolean unverified) throws BusinessException;
}
