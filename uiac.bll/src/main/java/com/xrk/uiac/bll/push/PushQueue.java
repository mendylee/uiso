package com.xrk.uiac.bll.push;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.LinkedBlockingDeque;

import com.xrk.hws.common.logger.Logger;
import com.xrk.uiac.bll.cache.CacheService;
import com.xrk.uiac.bll.cache.SysConfigCache;
import com.xrk.uiac.bll.cache.component.PushQueueCache;
import com.xrk.uiac.bll.entity.PushQueueEntity;
import com.xrk.uiac.bll.exception.PushExceotion;

/**
 * 
 * AuthorizeMsgQueue: 授权推送消息队列类
 *
 * <br>
 * = ========================= <br>
 * 公司：广州向日葵信息科技有限公司 <br>
 * 开发：zhub<zhubin@xiangrikui.com> <br>
 * 版本：1.0 <br>
 * 创建时间：2015年5月7日 <br>
 * JDK版本：1.7 <br>
 * = =========================
 */
public class PushQueue
{
	// 双向并发阻塞队列
	private static PushQueueCache pushQueueCache = null;
	// 单例
	private volatile static PushQueue instance = null;

	// 队列最大size
	private static int MAX_QUEUE_SIZE;
	// 队列缓冲区size
	private static int BUFFER_QUEUE_SIZE;
	// 队列临界值比率
	private static double rate = 0.8;

	private static Object obj = new Object();

	private PushQueue() 
	{
		pushQueueCache = (PushQueueCache) CacheService.GetService(PushQueueCache.class);
		MAX_QUEUE_SIZE = SysConfigCache.getInstance().getPushQueueSize();
		BUFFER_QUEUE_SIZE = (int) (MAX_QUEUE_SIZE * rate);
	}

	public static PushQueue getInstance()
	{
		if (instance == null)
		{
			synchronized (obj)
			{
				if (instance == null) 
				{
					instance = new PushQueue();
				}
			}
		}
		return instance;
	}

	/**
	 * 
	 * 授权信息入队列
	 * 
	 * @param authorizationVO
	 */
	public void put(String key, String value)
	{
		if (!pushQueueCache.contain(key)) 
		{
			pushQueueCache.putQueue(key, new LinkedBlockingDeque<String>(MAX_QUEUE_SIZE));
		}
		try
		{
			LinkedBlockingDeque<String> queues = pushQueueCache.getQueue(key);
			if (queues != null)
			{
				// 队列size达到临界值时，通知观察者
				if (queues.size() >= BUFFER_QUEUE_SIZE)
				{
					Logger.info("队列已达临界值，开始通知观察者,appid=" + key + "value=" + value);
					// 当某个appId或key的队列已达最大值，触发观察者进行推送
					// 取出当前队列下的数据
					List<String> pushMessages = PushQueue.getInstance().getAll(key);
					// 通知观察者
					// 取出此应用的观察者进行推送
					SubscriberObserver observer = PushObserverManager.getInstance()
					        .getRemoteSubscriber(key);
					// 通知观察者进行推送
					if (observer != null) 
					{
						Logger.info("ready notice queue appId:" + key + ",size:" + pushMessages.size());
						observer.push(pushMessages);
					}

					return;
				}
				queues.addLast(value);
				Logger.info("put push message success:" + key + ", value:" + value);
			}

		}
		catch (Exception e) 
		{
			throw new PushExceotion("授权信息入队列异常");
		}

	}

	/**
	 * 
	 * 清空当前应用队列的数据
	 * 
	 * @param appId
	 */
	public synchronized void clear(String appId)
	{
		if (pushQueueCache.contain(appId)) 
		{
			LinkedBlockingDeque<String> queues = pushQueueCache.getQueue(appId);
			queues.clear();
		}
	}

	/**
	 * 
	 * 取出队列名称下的所有元素
	 * 
	 * @param appId
	 * @return
	 */
	public synchronized List<String> getAll(String appId)
	{
		List<String> list = new ArrayList<String>();
		LinkedBlockingDeque<String> queues = null;
		if (pushQueueCache.contain(appId)) 
		{
			queues = pushQueueCache.getQueue(appId);
			queues.drainTo(list);
		}
		return list;
	}

	public boolean delete(String appId)
	{
		if (pushQueueCache.contain(appId) && pushQueueCache.getQueue(appId).isEmpty())
		{
			pushQueueCache.remove(appId);
		}
		return true;
	}

	/**
	 * 
	 * 取出所有appId的队列
	 * 
	 * @param appId
	 * @return
	 */
	public List<String> getQueues()
	{
		List<String> list = new ArrayList<String>();
		Map<String, PushQueueEntity> srcMap = pushQueueCache.getAll();
		if (srcMap == null)
		{
			Logger.error("queue is null");
			return null;
		}
		
		Iterator<Entry<String, PushQueueEntity>> it = srcMap.entrySet().iterator();
		while (it.hasNext()) 
		{
			list.add(it.next().getKey());
		}
		return list;
	}

	/**
	 * 
	 * 根据key一次取出队列所有元素
	 * 
	 * @param appId
	 * @return
	 */
	public String get(String appId)
	{
		LinkedBlockingDeque<String> queues = null;
		String message = null;
		if (pushQueueCache.contain(appId)) 
		{
			queues = pushQueueCache.getQueue(appId);
			message = queues.removeFirst();
		}
		if (pushQueueCache.getQueue(appId).isEmpty())
		{
			pushQueueCache.remove(appId);
		}
		return message;
	}

	/**
	 * 
	 * 判断当前appId的队列是否为满
	 * 
	 * @param appId
	 * @return
	 */
	public boolean isFull(String appId)
	{
		LinkedBlockingDeque<String> queues = null;
		if (pushQueueCache.contain(appId))
		{
			queues = pushQueueCache.getQueue(appId);
			return queues.size() == MAX_QUEUE_SIZE;
		}
		return false;
	}

	/**
	 * 
	 * 取出当前appId队列的size.
	 * 
	 * @param appId
	 * @return
	 */
	public int size(String appId)
	{
		LinkedBlockingDeque<String> queues = null;
		if (pushQueueCache.contain(appId))
		{
			queues = pushQueueCache.getQueue(appId);
			return queues.size();
		}
		return 0;
	}

	/**
	 * 
	 * 判断是否为null
	 * 
	 * @return
	 */
	public boolean isEmpty()
	{
		return pushQueueCache.size() == 0;
	}

}