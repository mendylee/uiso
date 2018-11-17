package com.xrk.uiac.service.handler;

import com.google.gson.Gson;
import com.xrk.uiac.bll.component.IMsgTemplateComponent;
import com.xrk.uiac.bll.component.impl.MsgTemplateCompont;
import com.xrk.uiac.bll.vo.MsgInfoVO;
import com.xrk.uiac.service.annotation.HttpMethod;
import com.xrk.uiac.service.annotation.HttpMethod.METHOD;
import com.xrk.uiac.service.annotation.HttpMethod.STATUS_CODE;
import com.xrk.uiac.service.annotation.HttpRouterInfo;
import com.xrk.uiac.service.entity.SimpleResponseEntity;

/**
 * 
 * MsgTemplateHandler: 短信模板设置接口
 *
 * <br>=
 * ========================= <br>
 * 公司：广州向日葵信息科技有限公司 <br>
 * 开发：zhub<zhubin@xiangrikui.com> <br>
 * 版本：1.0 <br>
 * 创建时间：2015年5月20日 <br>
 * JDK版本：1.7 <br>=
 * =========================
 */
@HttpRouterInfo(router = "msg")
public class MsgTemplateHandler extends AbstractHttpWorkerHandler
{
	private IMsgTemplateComponent msgTemplateComponent = null;

	public MsgTemplateHandler() {
		// TODO Auto-generated constructor stub
		msgTemplateComponent = new MsgTemplateCompont();
	}

	/**
	 * 
	 * 设置短信模板内容
	 * 
	 * @param appId
	 *            应用ID
	 * @param msgInfo
	 *            基础信息，JSON二次包装
	 * @return
	 */
	@HttpMethod(uri = "template", method = METHOD.POST, code = STATUS_CODE.OK)
	public SimpleResponseEntity<Boolean> setTemplate(String appId, String msgInfo)
	{
		MsgInfoVO msg = new Gson().fromJson(msgInfo, MsgInfoVO.class);
		Boolean bRtn = false;
		if (msg != null) {
			bRtn = msgTemplateComponent.setMsgTemplate(appId, msg.getTemplateCode(), msg.getContent());
		}
		return new SimpleResponseEntity<Boolean>(bRtn);
	}
}
