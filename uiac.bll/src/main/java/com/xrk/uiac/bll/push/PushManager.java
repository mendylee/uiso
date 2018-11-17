package com.xrk.uiac.bll.push;

import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.xrk.hws.common.logger.Logger;
import com.xrk.uiac.bll.cache.SysConfigCache;

/**
 * 
 * PushManager: 推送消息管理类
 *
 * <br>=
 * ========================= <br>
 * 公司：广州向日葵信息科技有限公司 <br>
 * 开发：zhub<zhubin@xiangrikui.com> <br>
 * 版本：1.0 <br>
 * 创建时间：2015年5月8日 <br>
 * JDK版本：1.7 <br>=
 * =========================
 */
public class PushManager
{

	private static ScheduledThreadPoolExecutor timer = null;

	private static PushManager instance = null;

	private static Boolean enable;

	private static int interval = 5 * 1000;

	private static Object obj = new Object();
	
	private PushManager() {
		// 开启定时器
		timer = new ScheduledThreadPoolExecutor(1);
		// 从config中读取
		interval = SysConfigCache.getInstance().getPushInterval();
		//timer.schedule(new PushTask(), interval, TimeUnit.MILLISECONDS);
		timer.scheduleAtFixedRate(new PushTask(), 1000, interval,  TimeUnit.MILLISECONDS);
	}

	public static PushManager getInstance()
	{
		if (instance == null) {
			synchronized (obj) {
				if(instance == null)
					instance = new PushManager();
            }			
		}
		return instance;
	}

	public static boolean isEnable()
	{
		if (enable == null) {
			// 从config中读取
			enable = "true".equals("true");
		}
		return enable;
	}

	/**
	 * 
	 * 写入到推送队列中.
	 * 
	 * @param appId
	 * @param value
	 */
	public void send(String appId, String value)
	{
		PushQueue.getInstance().put(appId, value);
	}

	/**
	 * 
	 * 当某个key的推送队列满时，取出执行.
	 * 
	 * @param appId
	 * @return
	 */
	public boolean execute(String appId)
	{
		Logger.info("push queue full execute");
		String message = PushQueue.getInstance().get(appId);
		Logger.info("push full execute:" + message);
		return true;
	}

	// 定时任务取出待发送队列进行推送
	private class PushTask implements Runnable
	{
		boolean isRuning = false;
		public void run()
		{
			if(isRuning){
				return;
			}
			
			isRuning = true;
			
			try {
				if (PushQueue.getInstance().isEmpty()) {
					Logger.debug("push queue is empty");
					return;
				}

				List<String> pushList = PushQueue.getInstance().getQueues();
				Logger.debug("now push queue size=%s", pushList.size());
				for (String queue : pushList) {
					//取出当前队列下的数据
					List<String> pushMessages = PushQueue.getInstance().getAll(queue);
					if(pushMessages.size() == 0)
					{
						continue;
					}
					
					//取出此应用的观察者进行推送
					SubscriberObserver observer =  PushObserverManager.getInstance().getRemoteSubscriber(queue);
					//通知观察者进行推送
					if(observer!=null){	
						Logger.info("ready notice queue appId:"+queue+",size:"+pushMessages.size());
						observer.push(pushMessages);
					}
				}

			}
			catch (Exception e) {
				e.printStackTrace();
				Logger.error(e, "push task run error:", e.getMessage());
			}
			finally {
				isRuning = false;
				//timer.schedule(new PushTask(), interval, TimeUnit.MILLISECONDS);				
			}
		}
	}

}
