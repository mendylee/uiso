package com.xrk.uiac.bll.test.component;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.esotericsoftware.minlog.Log.Logger;
import com.xrk.uiac.bll.cache.CacheService;
import com.xrk.uiac.bll.common.ParameterUtils;
import com.xrk.uiac.bll.component.impl.PushObserverCompont;
import com.xrk.uiac.bll.exception.BusinessException;
import com.xrk.uiac.bll.push.PushObserverManager;
import com.xrk.uiac.bll.response.PushResponse;
import com.xrk.uiac.dal.Dal;
import com.xrk.uiac.dal.DalTestHelper;
import com.xrk.uiac.dal.dao.PushObserverDAO;
import com.xrk.uiac.dal.entity.PushObserver;

/**
 * 
 * PushCompontTest: 推送组件服务单元测试类
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
@RunWith(PowerMockRunner.class)
@PowerMockIgnore( {"javax.management.*"}) 
@PrepareForTest({ParameterUtils.class, Logger.class,PushObserverDAO.class})
public class PushObserverCompontTest
{
	static PushObserverCompont pushObserverCompont = null;
	
	@BeforeClass
	public static void SetUpClass()
	{      
		PowerMockito.mockStatic(Logger.class);
		// 在测试方法运行之前运行
		pushObserverCompont = new PushObserverCompont();
		DalTestHelper.initDal();
		CacheService.Init();
	}
	@Before
	public void SetUp()
	{
		PowerMockito.mockStatic(Logger.class);
	}

	@Test
	public void testRegiestObserver()
	{
		String appId = "4";
		String callBackUrl = "34234324234";
		PowerMockito.mockStatic(ParameterUtils.class);
		PowerMockito.when(ParameterUtils.isValidUrl(callBackUrl)).thenReturn(true);
		PowerMockito.when(ParameterUtils.isValidAppId(Integer.parseInt(appId))).thenReturn(true);
		/*PowerMockito.when(PushObserverDAO.findWithAppId(Integer.parseInt(appId))).thenReturn(new PushObserver());
		PowerMockito.when(PushObserverDAO.deletePushObserver(Integer.parseInt(appId))).thenReturn(0);*/
		 
		try {
	        pushObserverCompont.removeObserver(appId);
        }
        catch (BusinessException e1) {
	        // TODO Auto-generated catch block
	        e1.printStackTrace();
        }
		
		PushResponse response = null;
		try {
			response = pushObserverCompont.regiestObserver(appId, callBackUrl);
		}
		catch (BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Assert.assertEquals(response.getStatus(), "0");
		Assert.assertEquals(PushObserverManager.getInstance().size(), 1);
		
		try {
	        pushObserverCompont.removeObserver(appId);
        }
        catch (BusinessException e1) {
	        // TODO Auto-generated catch block
	        e1.printStackTrace();
        }
	}

	@Test
	public void testRemoveObserver()
	{
		String appId = "2";
		String callBackUrl = "http://www.sina.com";
		PushResponse addResponse = null;
		try {
	        pushObserverCompont.removeObserver(appId);
        }
        catch (BusinessException e1) {
	        // TODO Auto-generated catch block
	        e1.printStackTrace();
        }
		
        try {
        	//预设mock
    		PowerMockito.mockStatic(ParameterUtils.class);
    		PowerMockito.when(ParameterUtils.isValidAppId(Integer.parseInt(appId))).thenReturn(true);
    		PowerMockito.when(ParameterUtils.isValidUrl(callBackUrl)).thenReturn(true);
	        addResponse = pushObserverCompont.regiestObserver(appId, callBackUrl);
	        Assert.assertEquals(addResponse.getStatus(), "0");
			Assert.assertEquals(PushObserverManager.getInstance().size(), 1);
			
			PushResponse delResponse = pushObserverCompont.removeObserver(appId);
			Assert.assertEquals(delResponse.getStatus(), "0");
			Assert.assertEquals(PushObserverManager.getInstance().size(), 0);
        }
        catch (BusinessException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
        }
		
        try {
	        pushObserverCompont.removeObserver(appId);
        }
        catch (BusinessException e1) {
	        // TODO Auto-generated catch block
	        e1.printStackTrace();
        }

	}

}
