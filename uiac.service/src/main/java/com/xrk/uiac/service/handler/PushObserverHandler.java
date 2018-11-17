package com.xrk.uiac.service.handler;

import com.xrk.hws.common.logger.Logger;
import com.xrk.uiac.bll.component.IPushObserverCompont;
import com.xrk.uiac.bll.component.impl.PushObserverCompont;
import com.xrk.uiac.bll.exception.BusinessException;
import com.xrk.uiac.bll.response.PushResponse;
import com.xrk.uiac.service.annotation.HttpMethod;
import com.xrk.uiac.service.annotation.HttpMethod.METHOD;
import com.xrk.uiac.service.annotation.HttpMethod.STATUS_CODE;
import com.xrk.uiac.service.annotation.HttpRouterInfo;

/**
 * 
 * PushObserverHandler: 授权推送控制接口Handle
 *
 * <br>=
 * ========================= <br>
 * 公司：广州向日葵信息科技有限公司 <br>
 * 开发：zhub<zhubin@xiangrikui.com> <br>
 * 版本：1.0 <br>
 * 创建时间：2015年5月13日 <br>
 * JDK版本：1.7 <br>=
 * =========================
 */
@HttpRouterInfo(router = "Authorize")
public class PushObserverHandler extends AbstractHttpWorkerHandler
{
	IPushObserverCompont pushObserverCompont;

	public PushObserverHandler() {
		// TODO Auto-generated constructor stub
		if (pushObserverCompont == null) {
			pushObserverCompont = new PushObserverCompont();
		}
	}

	/**
	 * 
	 * 注册观察者
	 * 
	 * @param appId
	 *            应用ID
	 * @param callBackUrl
	 *            回调地址
	 * @return
	 * @throws BusinessException 
	 */
	@HttpMethod(uri = "push", method = METHOD.POST, code = STATUS_CODE.CREATED)
	public PushResponse regiestObserver(CustomParameter head, String callBackUrl) throws BusinessException
	{
		//try {
			return pushObserverCompont.regiestObserver(head.getAppId(), callBackUrl);
		//}
		//catch (Exception e) {
			// TODO: handle exception
		//	Logger.error("regiest Observer fail", e.getMessage());
	//	}
	}

	/**
	 * 
	 * 解除回调者.
	 * 
	 * @param appId
	 * @return
	 * @throws BusinessException 
	 */
	@HttpMethod(uri = "push", method = METHOD.DELETE, code = STATUS_CODE.OK)
	public PushResponse deleteObserver(CustomParameter head) throws BusinessException
	{
			return pushObserverCompont.removeObserver(head.getAppId());
	}
	/**
	 * 
	 * 授权推送消息接收
	 *    
	 * @param authorize
	 */
	@HttpMethod(uri = "accept", method = METHOD.POST, code = STATUS_CODE.OK)
	public void revicePush(String authorize)
	{
		Logger.info(authorize);
		
	}
}
