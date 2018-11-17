package com.xrk.uiac.bll.component.impl;

import com.xrk.hws.common.logger.Logger;
import com.xrk.uiac.bll.cache.CacheService;
import com.xrk.uiac.bll.cache.component.UserIdentityCache;
import com.xrk.uiac.bll.common.UserConstants;
import com.xrk.uiac.bll.component.IAuthorizationComponent;
import com.xrk.uiac.bll.component.IUserAuththorizeSynComponent;
import com.xrk.uiac.bll.entity.UserIdentityEntity;
import com.xrk.uiac.bll.vo.UserVO;
import com.xrk.uiac.common.utils.BeanCopierUtils;
import com.xrk.uiac.dal.dao.UserDAO;
import com.xrk.uiac.dal.entity.User;

/**
 * AuthorizeUserServiceImpl: 认证用户缓存信息接口实现类
 *
 * <br>=
 * ========================= <br>
 * 公司：广州向日葵信息科技有限公司 <br>
 * 开发：zhub<zhubin@xiangrikui.com> <br>
 * 版本：1.0 <br>
 * 创建时间：2015年4月27日 <br>
 * JDK版本：1.7 <br>=
 * =========================
 */
public class UserAuthorizeSynCompont implements IUserAuththorizeSynComponent
{

	private UserIdentityCache userIdentityCache;

	public UserAuthorizeSynCompont(UserIdentityCache userCache) {
		this.userIdentityCache = userCache;
	}
	
	public UserAuthorizeSynCompont() {
		this.userIdentityCache = (UserIdentityCache)CacheService.GetService(UserIdentityCache.class);
	}

	public boolean synAddAuthroizeUser(UserVO userInfoVO)
	{
		// 先在缓存中查找
		Object key = userInfoVO.getUid();
		UserIdentityEntity userIdentityEntity = userIdentityCache.get(key);
		if (userIdentityEntity == null) {
			// 注册用户对象转换为认证用户cache对象
			userIdentityEntity = new UserIdentityEntity();
			BeanCopierUtils.copy(userInfoVO, userIdentityEntity);
		}else{
			//在绑定账号中缓存已存在则修改账号
			userIdentityEntity.setAccount(userInfoVO.getAccount());
		}
		return userIdentityCache.put(userIdentityEntity);
	}

	public boolean synEditPassWdAuthroizeUser(UserVO userInfoVO)
	{
		// 先在缓存中查找
		Object key = userInfoVO.getUid();
		UserIdentityEntity userIdentityEntity = userIdentityCache.get(key);
		if (userIdentityEntity == null) {
			// 注册用户对象转换为认证用户cache对象
			userIdentityEntity = new UserIdentityEntity();
			BeanCopierUtils.copy(userInfoVO, userIdentityEntity);
		}
		else {
			// 将当期设置的新密码重新设置后放入到cache中
			userIdentityEntity.setPassword(userInfoVO.getPassword());
		}
		return userIdentityCache.put(userIdentityEntity);
	}

	public boolean synAuthroizeUserStatus(UserVO userInfoVO)
	{
		// 先在缓存中查找
		Object key = userInfoVO.getUid();
		UserIdentityEntity userIdentityEntity = userIdentityCache.get(key);
		if (userIdentityEntity == null) {
			// 注册用户对象转换为认证用户cache对象
			userIdentityEntity = new UserIdentityEntity();
			BeanCopierUtils.copy(userInfoVO, userIdentityEntity);
		}
		else {
			// 将当期设置的新密码重新设置后放入到cache中
			userIdentityEntity.setStatus((userInfoVO.getStatus()));
		}
				
		if(userIdentityEntity.getStatus() == UserConstants.ACCOUNT_STATUS_DISABLED)
		{
			//通知登录控件，注销请求
			IAuthorizationComponent authComponent = AuthorizationComponent.getInstance();
			try
			{
				authComponent.logout(userIdentityEntity.getUid());
			}
			catch(Throwable ex)
			{
				Logger.error(ex, ex.getMessage());
			}
		}
		
		return userIdentityCache.put(userIdentityEntity);
	}

	@Override
	public UserIdentityEntity queryAuthroizeUser(String uid)
	{
		// TODO Auto-generated method stub
		Logger.info("queryAuthroizeUser cache uid=" + uid);
		UserIdentityEntity userIdentityEntity = userIdentityCache.get(uid);
		if (userIdentityEntity != null) {
			Logger.debug("queryAuthroizeUser cache hit");
			return userIdentityEntity;
		}
		else{
			userIdentityEntity = new UserIdentityEntity();
		}
		
		User user = UserDAO.findExistingUserWithUid(Long.parseLong(uid));
		if(user!=null){
			//DB中查到再次同步到认证用户cache
			UserVO userInfoVO = new UserVO();
			userInfoVO.setUid(user.getUid());
			userInfoVO.setAccount(user.getAccount());
			userInfoVO.setPassword(user.getPassword());
			userInfoVO.setStatus(user.getStatus());
			BeanCopierUtils.copy(userInfoVO, userIdentityEntity);
			synAddAuthroizeUser(userInfoVO);
			return userIdentityEntity;
		}
		return null;
	}

}
