package com.xrk.uiac.bll.cache.component;

import com.xrk.uiac.bll.cache.AbstractCache;
import com.xrk.uiac.bll.entity.UserInfoEntity;
import com.xrk.uiac.common.utils.BeanCopierUtils;

/**
 * 
 * UserInfoCache: 用户基本信息缓存处理类
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：yexx<yexiaoxiao@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年4月28日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public class UserInfoCache extends AbstractCache<UserInfoEntity>
{
	@Override
    public UserInfoEntity get(Object key)
    {
		UserInfoEntity userInfo = super.get(key);
		if (userInfo == null)
		{
			return null;
		}
		UserInfoEntity cloneUserInfo = new UserInfoEntity();
		BeanCopierUtils.copy(userInfo, cloneUserInfo);
		
	    return cloneUserInfo;
    }

	@Override
    public boolean put(Object key, UserInfoEntity userInfo)
    {
		if (userInfo == null)
		{
			return false;
		}
		UserInfoEntity cloneUserInfo = new UserInfoEntity();
		BeanCopierUtils.copy(userInfo, cloneUserInfo);
		
	    return super.put(key, cloneUserInfo);
    }
}
