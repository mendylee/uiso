package com.xrk.uiac.bll.cache.component;

import com.xrk.uiac.bll.cache.AbstractCache;
import com.xrk.uiac.bll.entity.UserIdentityEntity;
import com.xrk.uiac.common.utils.BeanCopierUtils;

/**
 * 认证用户缓存类
 * UserIdentityCache: UserIdentityCache.java.
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：zhub<zhubin@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年4月28日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public class UserIdentityCache extends AbstractCache<UserIdentityEntity>
{
	/**
	 * 
	 * 添加认证用户缓存
	 *    
	 * @param user
	 * @return
	 */
	public boolean put(UserIdentityEntity user)
	{
		UserIdentityEntity userClone = new UserIdentityEntity();
		BeanCopierUtils.copy(user, userClone);
		
		return super.put(user.getUid(), userClone);
	}

	/**
	 * 重载方法，忽略用户送入的key值
	 * @see com.xrk.uiac.bll.cache.AbstractCache#put(java.lang.Object, java.lang.Object)
	 */
	@Override
	public boolean put(Object key, UserIdentityEntity user)
	{
		return put(user);
	}
	
}
