package com.xrk.uiac.bll.vo;

/**
 * 
 * MsgInfoVO: 短信模板业务VO
 *
 * <br>=
 * ========================= <br>
 * 公司：广州向日葵信息科技有限公司 <br>
 * 开发：zhub<zhubin@xiangrikui.com> <br>
 * 版本：1.0 <br>
 * 创建时间：2015年5月21日 <br>
 * JDK版本：1.7 <br>=
 * =========================
 */
public class MsgInfoVO
{
	/**
	 * 模板业务Code 01:注册 02:找回密码  03:绑定账号
	 */
	private String templateCode;
	private String content;

	public String getTemplateCode()
	{
		return templateCode;
	}

	public void setTemplateCode(String templateCode)
	{
		this.templateCode = templateCode;
	}

	public String getContent()
	{
		return content;
	}

	public void setContent(String content)
	{
		this.content = content;
	}

}
