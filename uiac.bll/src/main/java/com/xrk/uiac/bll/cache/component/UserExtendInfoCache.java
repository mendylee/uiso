package com.xrk.uiac.bll.cache.component;

import com.xrk.uiac.bll.cache.AbstractCache;
import com.xrk.uiac.bll.entity.UserExtendInfoEntity;
import com.xrk.uiac.common.utils.BeanCopierUtils;

/**
 * 
 * 用户扩展信息缓存
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：yexx<yexiaoxiao@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年5月29日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public class UserExtendInfoCache extends AbstractCache<UserExtendInfoEntity>
{
	//暂时这样处理, 有更好的解决方案后替换掉现有的缓存实体
	
	
	@Override
    public UserExtendInfoEntity get(Object key)
    {
		UserExtendInfoEntity value = super.get(key);
		if (value == null)
		{
			return null;
		}
		UserExtendInfoEntity cloneValue = new UserExtendInfoEntity();
		BeanCopierUtils.copy(value, cloneValue);
	    
		return cloneValue;
    }

	@Override
    public boolean put(Object key, UserExtendInfoEntity value)
    {
		if (value == null)
		{
			return false;
		}
		UserExtendInfoEntity cloneValue = new UserExtendInfoEntity();
		BeanCopierUtils.copy(value, cloneValue);
	    
		return super.put(key, cloneValue);
    }
}
