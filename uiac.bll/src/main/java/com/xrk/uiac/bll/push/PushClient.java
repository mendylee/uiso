package com.xrk.uiac.bll.push;

import com.google.gson.Gson;
import com.xrk.hws.common.logger.Logger;

/**
 * 
 * PushClient: 推送client
 *
 * <br>=
 * ========================= <br>
 * 公司：广州向日葵信息科技有限公司 <br>
 * 开发：zhub<zhubin@xiangrikui.com> <br>
 * 版本：1.0 <br>
 * 创建时间：2015年5月11日 <br>
 * JDK版本：1.7 <br>=
 * =========================
 */
public class PushClient
{
	/**
	 * 
	 * send要推送的消息
	 * 
	 * @param 应用ID或key
	 * @param 业务对象
	 */
	public static void send(String appKey, PushType pushType, String message)
	{
		if (!PushManager.isEnable()) {
			Logger.info("sorry,push server is not enable ");
			throw new RuntimeException("sorry,push server is not enable");
		}
		SubscriberObserver observer =  PushObserverManager.getInstance().getRemoteSubscriber(appKey);
		if(observer ==null){
			Logger.info("sorry,It's observer:"+appKey+"not regiest push");
			return;
		}
		Subscription subscription = new Subscription();
		subscription.setPushType(pushType);
		subscription.setMessage(message);
		String pushMessage = new Gson().toJson(subscription);
		
		// 发送数据到pushManager
		PushManager.getInstance().send(appKey, pushMessage);
	}
}
