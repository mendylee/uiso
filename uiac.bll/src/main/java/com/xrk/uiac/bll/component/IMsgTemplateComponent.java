package com.xrk.uiac.bll.component;


/**
 * 
 * IMsgTemplateComponent: 短信模板设置组件类
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：zhub<zhubin@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年5月20日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public interface IMsgTemplateComponent
{
	/**
	 * 
	 * 设置短信模板内容
	 *    
	 * @param appId		应用ID
	 * @param item		配置项
	 * @param value		配置值
	 * @return
	 */
	public boolean setMsgTemplate(String appId,String item,String value);
	/**
	 * 
	 * 根据应用ID和类型获取短信模板内容	
	 *    
	 * @param appId				应用ID
	 * @param templateCode		模板业务Code
	 * @return
	 */
	public String findTemplateContent(String appId,String templateCode);
	
}
