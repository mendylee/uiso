package com.xrk.uiac.bll.test.push;

import org.junit.Test;

import com.google.gson.Gson;
import com.xrk.uiac.bll.push.PushClient;
import com.xrk.uiac.bll.push.PushSubjectCenter;
import com.xrk.uiac.bll.push.PushType;
import com.xrk.uiac.bll.push.RemoteSubscriber;
import com.xrk.uiac.bll.vo.UserAuthorizationVO;

/**
 * 
 * PushSubjectTest: 推送主题单元测试类
 *
 * <br>=
 * ========================= <br>
 * 公司：广州向日葵信息科技有限公司 <br>
 * 开发：zhub<zhubin@xiangrikui.com> <br>
 * 版本：1.0 <br>
 * 创建时间：2015年5月18日 <br>
 * JDK版本：1.7 <br>=
 * =========================
 */
public class PushSubjectTest
{

	@Test
	public void testRegiestObserver()
	{
		RemoteSubscriber remoteSubscriber = new RemoteSubscriber();
		remoteSubscriber.setAppId("1");
		remoteSubscriber.setCallBackUrl("http://www.baidu.com");
		PushSubjectCenter.getInstance().regiestObserver(remoteSubscriber);
		RemoteSubscriber remoteSubscriber1 = new RemoteSubscriber();
		remoteSubscriber1.setAppId("2");
		remoteSubscriber1.setCallBackUrl("http://www.sina.com");
		PushSubjectCenter.getInstance().regiestObserver(remoteSubscriber1);
	}

	@Test
	public void testSendPush()
	{
		// 注册观察者
		RemoteSubscriber remoteSubscriber = new RemoteSubscriber();
		remoteSubscriber.setAppId("1");
		remoteSubscriber.setCallBackUrl("http://www.baidu.com");
		PushSubjectCenter.getInstance().regiestObserver(remoteSubscriber);
		// 授权推送
		UserAuthorizationVO authorizationVO = new UserAuthorizationVO();
		authorizationVO.setAppId(remoteSubscriber.getAppId());
		authorizationVO.setUid(3);
		authorizationVO.setAccessToken("234213421342134");
		String message = new Gson().toJson(authorizationVO);
		PushClient.send(authorizationVO.getAppId(),PushType.Authroize_login, message);
	}
	
	

	public static void main(String[] args)
	{
		// 注册观察者
		RemoteSubscriber remoteSubscriber = new RemoteSubscriber();
		remoteSubscriber.setAppId("1");
		remoteSubscriber.setCallBackUrl("http://127.0.0.1:8081/Authorize/accept");
		PushSubjectCenter.getInstance().regiestObserver(remoteSubscriber);
		// 授权推送
		UserAuthorizationVO authorizationVO = new UserAuthorizationVO();
		authorizationVO.setAppId(remoteSubscriber.getAppId());
		authorizationVO.setUid(3);
		authorizationVO.setAccessToken("234213421342134");
		String message = new Gson().toJson(authorizationVO);
		PushClient.send(authorizationVO.getAppId(),PushType.Authroize_login, message);
	}
}
