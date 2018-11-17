package com.xrk.uiac.bll.cache.component;

import com.xrk.uiac.bll.cache.AbstractCache;
import com.xrk.uiac.bll.entity.UserAppIdEntity;

/**
 * 用户登录Token和用户ID及AppID的映射缓存
 * UserTokenMapCache: UserTokenMapCache.java.
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：shunchiguo<shunchiguo@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年8月14日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public class UserTokenMapCache extends AbstractCache<UserAppIdEntity>
{
	/**
	 * 克隆用户缓存对象，保证对象不被外部修改
	 * @see com.xrk.uiac.bll.cache.AbstractCache#put(java.lang.Object, java.lang.Object)
	 */
	@Override
	public boolean put(Object token, UserAppIdEntity user)
	{
		UserAppIdEntity userClone = new UserAppIdEntity(user.getUid(), user.getAppId());
		return super.put(token, userClone);
	}
}
