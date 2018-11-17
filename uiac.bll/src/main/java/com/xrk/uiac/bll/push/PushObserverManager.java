package com.xrk.uiac.bll.push;

import com.xrk.hws.common.logger.Logger;
import com.xrk.uiac.bll.cache.CacheService;
import com.xrk.uiac.bll.cache.component.RemoteSubscriberCache;

/**
 * 
 * PushObserverManager: 推送观察者管理器
 *
 * <br>=
 * ========================= <br>
 * 公司：广州向日葵信息科技有限公司 <br>
 * 开发：zhub<zhubin@xiangrikui.com> <br>
 * 版本：1.0 <br>
 * 创建时间：2015年5月12日 <br>
 * JDK版本：1.7 <br>=
 * =========================
 */
public class PushObserverManager
{
	private RemoteSubscriberCache pushCache = null;

	private volatile static PushObserverManager instance = null;

	private static Object obj = new Object();
	
	private PushObserverManager() 
	{
		pushCache = (RemoteSubscriberCache) CacheService.GetService(RemoteSubscriberCache.class);
	}

	public static PushObserverManager getInstance()
	{
		if (instance == null) 
		{
			synchronized (obj) 
			{
				if(instance == null)
				{
					instance = new PushObserverManager();
				}
            }
		}
		return instance;
	}

	public void add(RemoteSubscriber remoteSubscriber)
	{
		pushCache.put(remoteSubscriber.getAppId(), remoteSubscriber);
	}

	public RemoteSubscriber getRemoteSubscriber(String appId)
	{
		if (pushCache.contain(appId))
		{
			return pushCache.get(appId);
		}
		return null;
	}

	public void delete(RemoteSubscriber remoteSubscriber)
	{
		Logger.debug("delete observer app:"+remoteSubscriber.getAppId());
		if (pushCache.contain(remoteSubscriber.getAppId()))
		{
			pushCache.remove(remoteSubscriber.getAppId());
		}
	}
	
	public int size()
	{
		Long size = pushCache.size();
		return size.intValue();
	}

}