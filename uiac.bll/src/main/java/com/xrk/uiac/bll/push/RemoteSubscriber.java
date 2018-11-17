package com.xrk.uiac.bll.push;

import java.util.Date;
import java.util.List;

import com.google.gson.Gson;
import com.xrk.hws.common.logger.Logger;
import com.xrk.uiac.common.utils.http.HTTP;
import com.xrk.uiac.common.utils.http.Request;
import com.xrk.uiac.common.utils.http.Response;
import com.xrk.uiac.common.utils.http.ResponseHandler;

/**
 * 
 * RemoteSubscriber: 远程订阅者服务对象
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：zhub<zhubin@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年5月20日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public class RemoteSubscriber implements SubscriberObserver
{
	private String appId;
	private String callBackUrl;
	private Date addDate;

	public String getAppId()
	{
		return appId;
	}

	public void setAppId(String appId)
	{
		this.appId = appId;
	}

	public String getCallBackUrl()
	{
		return callBackUrl;
	}

	public void setCallBackUrl(String callBackUrl)
	{
		this.callBackUrl = callBackUrl;
	}

	public Date getAddDate()
	{
		return addDate;
	}

	public void setAddDate(Date addDate)
	{
		this.addDate = addDate;
	}

	public void push(List<String> messages)
	{
		// 执行请求发送开始推送
		Logger.debug("push work start appId=" + appId + ",callBackUrl=" + callBackUrl);
		Logger.debug("push message size=" + messages.size());
		String pushMessage = new Gson().toJson(messages);
		Request request = new Request(callBackUrl);
		try {
			byte[] body = pushMessage.getBytes("UTF-8");
			request.setBody(body);
			request.addHeader("Content-Length", String.valueOf(body.length));
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
		// 开始推送
		HTTP.asyncPOST(request, 10, new ResponseHandler() {

			@Override
			public void handle(Response response) throws Exception
			{
				// TODO Auto-generated method stub
				if (response == null) {

				}
				if (response.getStatusCode() == 200) {
					Logger.info("appId=" + appId + ",callBackUrl=" + callBackUrl
					        + "result push success");
				}
			}
		});
	}

}
