package com.xrk.uiac.bll.cache.component;

import com.xrk.uiac.bll.cache.AbstractCache;
import com.xrk.uiac.bll.push.RemoteSubscriber;
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
public class RemoteSubscriberCache extends AbstractCache<RemoteSubscriber>
{
	@Override
    public RemoteSubscriber get(Object key)
    {
		RemoteSubscriber subscriber = super.get(key);
		if (subscriber == null)
		{
			return null;
		}
		RemoteSubscriber cloneSubscriber = new RemoteSubscriber();
		BeanCopierUtils.copy(subscriber, cloneSubscriber);
	    return cloneSubscriber;
    }

	@Override
    public boolean put(Object key, RemoteSubscriber subscriber)
    {
		if (subscriber == null)
		{
			return false;
		}
		RemoteSubscriber cloneSubscriber = new RemoteSubscriber();
		BeanCopierUtils.copy(subscriber, cloneSubscriber);
	    return super.put(key, cloneSubscriber);
    }
}
