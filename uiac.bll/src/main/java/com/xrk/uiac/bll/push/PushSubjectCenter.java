package com.xrk.uiac.bll.push;

import com.xrk.hws.common.logger.Logger;


/**
 * 
 * SubjectManagerCenter: 主题管理中心
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
public class PushSubjectCenter implements Subject
{

	private volatile static PushSubjectCenter instance = null;
	private static Object obj = new Object();

	private PushSubjectCenter()
	{
	}

	public static PushSubjectCenter getInstance()
	{
		if (instance == null)
		{
			synchronized (obj) 
			{
				if(instance == null)
					instance = new PushSubjectCenter();
            }
		}
		return instance;
	}

	public synchronized void regiestObserver(RemoteSubscriber subscriber)
	{
		Logger.info("regiest observer app:"+subscriber.getAppId());
		PushObserverManager.getInstance().add(subscriber);
	}

	public synchronized  void removeObserver(RemoteSubscriber subscribe)
	{
		PushObserverManager.getInstance().delete(subscribe);
		PushQueue.getInstance().delete(subscribe.getAppId());

	}
}