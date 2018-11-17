package com.xrk.uiac.bll.component;

import com.xrk.uiac.bll.entity.UserIdentityEntity;
import com.xrk.uiac.bll.vo.UserVO;

/**
 * 认证用户缓存信息接口
 * IAuthorizeUserService: IAuthorizeUserService.java.
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：zhub<zhubin@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年4月27日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public interface IUserAuththorizeSynComponent
{

	/**
	 * 
	 * 注册成功后同步认证用户cache接口.  
	 *    
	 * @param userInfoVO
	 * @return
	 */
	public boolean synAddAuthroizeUser(UserVO userInfoVO);
	
	/**
	 * 
	 * 用户修改密码同步认证用户cache接口 
	 * @param userInfoVO   
	 * @return
	 */
	public boolean synEditPassWdAuthroizeUser(UserVO userInfoVO);
	
	/**
	 * 
	 * 认证用户状态变更接口 
	 *    
	 * @param userInfoVO
	 * @return
	 */
	public boolean synAuthroizeUserStatus(UserVO userInfoVO);
	
	/**
	 * 
	 * 根据用户ID查询认证用户cache
	 *    
	 * @param uid
	 * @return
	 */
	public UserIdentityEntity queryAuthroizeUser(String uid);
	
}
