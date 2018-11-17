package com.xrk.uiac.bll.test.component;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.xrk.hws.common.logger.Logger;
import com.xrk.uiac.bll.cache.SysConfigCache;
import com.xrk.uiac.bll.common.ParameterUtils;
import com.xrk.uiac.bll.component.impl.MsgTemplateCompont;
import com.xrk.uiac.common.utils.sms.SmsResponse;
import com.xrk.uiac.common.utils.sms.SmsUtils;
import com.xrk.uiac.dal.DalTestHelper;
import com.xrk.uiac.dal.entity.AppSysConfig;

/**
 * 
 * MsgTemplateTest: 短信模板设置单元测试类
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
@RunWith(PowerMockRunner.class)
@PrepareForTest({Logger.class, ParameterUtils.class})
@PowerMockIgnore("javax.management.*")
public class MsgTemplateTest
{
	SysConfigCache sysConfig = SysConfigCache.getInstance();
	static AppSysConfig config = null;
	@BeforeClass
	public static void init(){
		PowerMockito.mockStatic(Logger.class);		
		DalTestHelper.initDal();
		
	}
	/**
	 * 
	 * 测试查询短信模板内容
	 *
	 */
	@Test
	public void testFindTemplateContent()
	{
		PowerMockito.mockStatic(ParameterUtils.class);
		Mockito.when(ParameterUtils.isValidAppId(Mockito.anyInt())).thenReturn(true);
		
		MsgTemplateCompont compont = new MsgTemplateCompont();
		config  = new AppSysConfig();
		config.setAppId(5);
		config.setItem("01");
		config.setValue("你好！欢迎您使用注册功能，你的验证码%{code}");
		//AppSysConfigDAO.insertSysConfig(config);
		boolean bRtn = compont.setMsgTemplate(String.valueOf(config.getAppId()), config.getItem(), config.getValue());
		Assert.assertTrue(bRtn);
		
		//AppSysConfig content = AppSysConfigDAO.findConfigValue(config.getAppId(), config.getItem());
		String content = compont.findTemplateContent(String.valueOf(config.getAppId()), config.getItem());
		Assert.assertEquals(config.getValue(), content);
		SmsUtils smsUtils = new SmsUtils(sysConfig.getSmsHost());
		SmsResponse response = smsUtils.sendCaptcha("15019283272", content, 30);
		Assert.assertEquals("true", response.getResult());
	}
}
