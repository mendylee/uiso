package com.xrk.uiac.bll.component.impl;

import java.util.Date;

import com.xrk.hws.common.logger.Logger;
import com.xrk.uiac.bll.common.SeqUtils;
import com.xrk.uiac.bll.component.IPushObserverCompont;
import com.xrk.uiac.bll.exception.BUSINESS_CODE;
import com.xrk.uiac.bll.exception.BusinessException;
import com.xrk.uiac.bll.exception.InternalServerException;
import com.xrk.uiac.bll.exception.VerifyException;
import com.xrk.uiac.bll.push.PushSubjectCenter;
import com.xrk.uiac.bll.push.RemoteSubscriber;
import com.xrk.uiac.bll.response.PushResponse;
import com.xrk.uiac.dal.dao.PushObserverDAO;
import com.xrk.uiac.dal.entity.PushObserver;

/**
 * 
 * PushObserverCompont: 推送推送观察者组件实现类
 *
 * <br>=
 * ========================= <br>
 * 公司：广州向日葵信息科技有限公司 <br>
 * 开发：zhub<zhubin@xiangrikui.com> <br>
 * 版本：1.0 <br>
 * 创建时间：2015年5月15日 <br>
 * JDK版本：1.7 <br>=
 * =========================
 */
public class PushObserverCompont extends BaseComponent implements IPushObserverCompont
{

	public PushResponse regiestObserver(String appId, String callBackUrl) throws BusinessException
	{
		PushResponse response = null;
		// 验证appId参数
		validateAppId(Integer.parseInt(appId));
		// 验证回调地址
		valiadateUrl(callBackUrl);

		PushObserver findPushObserver = PushObserverDAO.findWithAppId(Integer.parseInt(appId));
		if (findPushObserver != null) {
			Logger.info("It's appId pushobserver:"+appId+"exist,please reset");
			throw new VerifyException(BUSINESS_CODE.PUSH_OBSERVER_EXISTS, "pushobserver was used by other person");
		}
		PushObserver pushObserver = new PushObserver();
		pushObserver.setAppId(Integer.parseInt(appId));
		pushObserver.setCallBackUrl(callBackUrl);
		pushObserver.setAddDate(new Date());
		//pushObserver.setSerial_id(SeqUtils.getPushObserverSerialId());
		long serialId = SeqUtils.getPushObserverSerialId();
		if (serialId != -1)
		{
			pushObserver.setSerial_id(serialId);
		}
		else
		{
			Logger.error("fail to generate pushObserver serial_id");
			throw new InternalServerException(BUSINESS_CODE.INTERNAL_SERVER_ERROR, "fail to generate serial id");
		}
		
		int result = 0;
		// 写入到数据库中
		try {
			result = PushObserverDAO.insertPushObserver(pushObserver);
		}
		catch (Exception e) {
			// TODO: handle exception
			throw new InternalServerException(BUSINESS_CODE.INTERNAL_SERVER_ERROR,
			                                  "regiestObserver fail");
		}

		if (result == 1) {
			System.out.println("regiest success!!");
			// 成功即注册到推送主题订阅中心
			RemoteSubscriber remoteSubscriber = new RemoteSubscriber();
			remoteSubscriber.setAppId(appId);
			remoteSubscriber.setCallBackUrl(callBackUrl);
			PushSubjectCenter.getInstance().regiestObserver(remoteSubscriber);
		}
		response = new PushResponse();
		response.setAppID(appId);
		response.setStatus("0");
		response.setRegTime(pushObserver.getAddDate());
		return response;
	}

	@Override
	public PushResponse removeObserver(String appId) throws BusinessException
	{
		PushResponse response = null;
		PushObserver findPushObserver = PushObserverDAO.findWithAppId(Integer.parseInt(appId));
		if (findPushObserver == null) {
			Logger.info("It's appId pushobserver:"+appId+"not exist,please reset");
			throw new VerifyException(BUSINESS_CODE.PUSH_OBSERVER_NOT_FOUND, "pushobserver not found");
		}
		int result = 0;
		try {
			result = PushObserverDAO.deletePushObserver(Integer.parseInt(appId));
		}
		catch (Exception e) {
			// TODO: handle exception
			throw new InternalServerException(BUSINESS_CODE.INTERNAL_SERVER_ERROR,
			                                  "removeObserver fail");
		}

		if (result == 1) {
			// 成功即解除该APPID到推送主题订阅中心
			RemoteSubscriber remoteSubscriber = new RemoteSubscriber();
			remoteSubscriber.setAppId(appId);
			PushSubjectCenter.getInstance().removeObserver(remoteSubscriber);
		}
		response = new PushResponse();
		response.setAppID(appId);
		response.setStatus("0");
		response.setDelTime(new Date());
		return response;
	}

}
