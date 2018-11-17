package com.xrk.uiac.bll.test.push;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import org.junit.Test;

import com.google.gson.Gson;
import com.xrk.uiac.bll.push.PushClient;
import com.xrk.uiac.bll.push.PushQueue;
import com.xrk.uiac.bll.push.PushSubjectCenter;
import com.xrk.uiac.bll.push.PushType;
import com.xrk.uiac.bll.push.RemoteSubscriber;
import com.xrk.uiac.bll.vo.UserAuthorizationVO;

/**
 * 
 * PushTest: 推送单元测试类
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：zhub<zhubin@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年5月11日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public class PushTest
{

	public static void main(String[] args) throws InterruptedException
	{
		RemoteSubscriber remoteSubscriber = new RemoteSubscriber();
		remoteSubscriber.setAppId("1");
		remoteSubscriber.setCallBackUrl("htttp://www.baidu.com");
	    PushSubjectCenter.getInstance().regiestObserver(remoteSubscriber);
	    RemoteSubscriber remoteSubscriber1 = new RemoteSubscriber();
	    remoteSubscriber1.setAppId("2");
	    remoteSubscriber1.setCallBackUrl("htttp://www.sina.com");
	    PushSubjectCenter.getInstance().regiestObserver(remoteSubscriber1);
		ThreadPoolExecutor pool = (ThreadPoolExecutor) Executors.newCachedThreadPool();
		for (int i = 0; i < 5; i++) {
			pool.submit(new pushSendThread());
			
        }
		//new Thread(new pushSendThread()).start();
		
		

	}

	@Test
	public void sendPush()
	{
		ThreadPoolExecutor pool = (ThreadPoolExecutor) Executors.newCachedThreadPool();
		pool.submit(new pushSendThread());
	}

}

class pushSendThread implements Runnable
{
	public void run()
	{
		while (PushQueue.getInstance().isEmpty()) {
			System.out.println("队列没了，继续send");
			for (int i = 0; i < 50; i++) {
				UserAuthorizationVO authorizationVO = new UserAuthorizationVO();
				authorizationVO.setAppId(i+"");
				authorizationVO.setUid(i);
				authorizationVO.setAccessToken("hi+"+i);
				String message = new Gson().toJson(authorizationVO);
				PushClient.send(authorizationVO.getAppId(),PushType.Authroize_login, message);
			}
		}

	}
}