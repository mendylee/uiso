package com.xrk.uiac.bll.cache.component;

import java.util.concurrent.LinkedBlockingDeque;

import com.xrk.uiac.bll.cache.AbstractCache;
import com.xrk.uiac.bll.entity.PushQueueEntity;
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
public class PushQueueCache extends AbstractCache<PushQueueEntity>
{
	@Override
    public PushQueueEntity get(Object key)
    {
		PushQueueEntity queue = super.get(key);
		if (queue == null)
		{
			return null;
		}
		PushQueueEntity cloneQueue = new PushQueueEntity();
		BeanCopierUtils.copy(queue, cloneQueue);
	    return cloneQueue;
    }

	@Override
    public boolean put(Object key, PushQueueEntity queue)
    {
		if (queue == null)
		{
			return false;
		}
		PushQueueEntity cloneQueue = new PushQueueEntity();
		BeanCopierUtils.copy(queue, cloneQueue);
	    return super.put(key, cloneQueue);
    }
	
	
	public LinkedBlockingDeque<String> getQueue(Object key)
	{
		PushQueueEntity queueEntity = get(key);
		return (queueEntity == null) ? null : queueEntity.getQueue();
	}
	
	public boolean putQueue(Object key, LinkedBlockingDeque<String> queue)
	{
		PushQueueEntity queueEntity = new PushQueueEntity();
		queueEntity.setQueue(queue);
		return put(key, queueEntity);
	}
}
