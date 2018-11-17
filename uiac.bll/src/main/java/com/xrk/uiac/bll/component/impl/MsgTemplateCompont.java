package com.xrk.uiac.bll.component.impl;

import com.xrk.hws.common.logger.Logger;
import com.xrk.uiac.bll.component.IMsgTemplateComponent;
import com.xrk.uiac.common.utils.sms.SmsConstants;
import com.xrk.uiac.dal.dao.AppSysConfigDAO;
import com.xrk.uiac.dal.entity.AppSysConfig;

/**
 * 
 * MsgTemplateCompont: 短信模板管理实现类
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
public class MsgTemplateCompont extends BaseComponent implements IMsgTemplateComponent
{

	@Override
	public boolean setMsgTemplate(String appId, String templateCode, String value)
	{
		boolean result = false;
		try {
			//验证appId
			//validateAppId(Integer.parseInt(appId));
			AppSysConfig config = new AppSysConfig();
			config.setAppId(Integer.parseInt(appId));
			config.setItem(templateCode);
			config.setValue(value);
			int rtn = AppSysConfigDAO.insertSysConfig(config);
			result = rtn == 1;
        }
        catch (Exception e) {
        	Logger.error(e, e.getMessage());
        }
		return result;
	}

	@Override
	public String findTemplateContent(String appId, String templateCode)
	{
		AppSysConfig config = AppSysConfigDAO.findConfigValue(Integer.parseInt(appId), templateCode);
		if(config!=null){
			return config.getValue();
		}
		return SmsConstants.DEFAULT_TEMPLATE_CONTENT;
	}

}
