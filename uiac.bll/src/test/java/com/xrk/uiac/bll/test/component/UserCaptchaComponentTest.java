package com.xrk.uiac.bll.test.component;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.google.gson.Gson;
import com.xrk.uiac.bll.cache.CacheService;
import com.xrk.uiac.bll.cache.SysConfigCache;
import com.xrk.uiac.bll.cache.component.VerifyRequestCache;
import com.xrk.uiac.bll.common.ParameterUtils;
import com.xrk.uiac.bll.common.SeqUtils;
import com.xrk.uiac.bll.common.UserConstants;
import com.xrk.uiac.bll.component.impl.UserCaptchaComponent;
import com.xrk.uiac.bll.component.impl.UserComponent;
import com.xrk.uiac.bll.entity.VerifyRequestEntity;
import com.xrk.uiac.bll.exception.BUSINESS_CODE;
import com.xrk.uiac.bll.exception.BusinessException;
import com.xrk.uiac.bll.response.CreateUserResponse;
import com.xrk.uiac.bll.response.SendCaptchaResponse;
import com.xrk.uiac.common.utils.sms.SmsResponse;
import com.xrk.uiac.common.utils.sms.SmsUtils;
import com.xrk.uiac.dal.Dal;
import com.xrk.uiac.dal.DalTestHelper;
import com.xrk.uiac.dal.dao.AppSysConfigDAO;
import com.xrk.uiac.dal.dao.UserDAO;
import com.xrk.uiac.dal.dao.UserExtendInfoDAO;
import com.xrk.uiac.dal.dao.UserInfoDAO;
import com.xrk.uiac.dal.dao.VerifyRequestDAO;
import com.xrk.uiac.dal.entity.AppSysConfig;
import com.xrk.uiac.dal.entity.UserInfo;
import com.xrk.uiac.dal.entity.VerifyRequest;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore( {"javax.management.*"}) 
@PrepareForTest({ParameterUtils.class, VerifyRequestDAO.class, UserCaptchaComponent.class})
public class UserCaptchaComponentTest
{
	private VerifyRequestCache verifyRequestCache = (VerifyRequestCache) CacheService.GetService(VerifyRequestCache.class);
	private UserComponent userComponent = null;
	private Set<Long> testUsers = new HashSet<Long>();
	private Gson gson = new Gson();
	private long index = 1;
	private String password = "password";
	private int invalidAppid = -1;
	private int appId = 1002;
	private int appId3 = 1003;
	private String invalidMobile = "158000";
	private int checkType = UserConstants.CAPTCHA_CHECKTYPE_REGISTER;
	private int invalidCheckType = 0;
	private String captcha = "123456";
	private String wrongCaptcha = "234567";
	private String invalidCaptcha = "124";
	SysConfigCache sysConfig = SysConfigCache.getInstance();
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{
		//PowerMockito.mockStatic(Logger.class);		
		DalTestHelper.initDal();
		CacheService.Init();
		
		AppSysConfig config = new AppSysConfig();
		config.setAppId(1002);
		config.setItem(String.valueOf(UserConstants.CAPTCHA_CHECKTYPE_REGISTER));
		config.setValue("你好！你的验证码%{code}");
		config.setSerialId(SeqUtils.getAppInfoExtendSerialId());
		try
		{
			AppSysConfigDAO.insertSysConfig(config);
		}
		catch (Exception e)
		{
			
		}
		
		config = new AppSysConfig();
		config.setAppId(1003);
		config.setItem(String.valueOf(UserConstants.CAPTCHA_CHECKTYPE_REGISTER));
		config.setValue("");
		config.setSerialId(SeqUtils.getAppInfoExtendSerialId());
		try
		{
			AppSysConfigDAO.insertSysConfig(config);
		}
		catch (Exception e)
		{
			
		}
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception
	{
		Dal.dispose();
		CacheService.cleanAll();
	}

	@Before
	public void setUp() throws Exception
	{
		//PowerMockito.mockStatic(Logger.class);
		//对VerifyRequestDAO进行Mock操作
		PowerMockito.mockStatic(VerifyRequestDAO.class);
		PowerMockito.when(VerifyRequestDAO.deleteWithMobile(Mockito.anyString())).thenCallRealMethod();
		PowerMockito.when(VerifyRequestDAO.findWithMobile(Mockito.anyString())).thenCallRealMethod();
		PowerMockito.when(VerifyRequestDAO.insert(Mockito.any(VerifyRequest.class))).thenCallRealMethod();
		PowerMockito.when(VerifyRequestDAO.updateStatusWithMobile(Mockito.anyString(), Mockito.anyInt())).thenCallRealMethod();
		
		userComponent = new UserComponent();
	}

	@After
	public void tearDown() throws Exception
	{
		clean();
	}

	@Test
	public void testSendCaptcha()
	{
		SmsUtils smsUtils = PowerMockito.mock(SmsUtils.class);		
		try
		{
			PowerMockito.whenNew(SmsUtils.class).withAnyArguments().thenReturn(smsUtils);
		}
		catch (Exception e)
		{
			fail("");
		}
		
		UserCaptchaComponent captchaComponent = new UserCaptchaComponent();
		
		CreateUserResponse createResponse = null;
		SendCaptchaResponse sendResponse = null;
		String mobile = null;
		SmsResponse smsr = null;
		
		mobile = getMobile();
		//预设mock
		PowerMockito.mockStatic(ParameterUtils.class);
		PowerMockito.when(ParameterUtils.isValidMobile(mobile)).thenReturn(true);
		PowerMockito.when(ParameterUtils.isValidMobile(invalidMobile)).thenReturn(false);
		PowerMockito.when(ParameterUtils.isValidAppId(appId)).thenReturn(true);
		PowerMockito.when(ParameterUtils.isValidAppId(appId3)).thenReturn(true);
		PowerMockito.when(ParameterUtils.isValidPassword(password)).thenReturn(true);
		PowerMockito.when(ParameterUtils.isValidCheckType(checkType)).thenReturn(true);
		PowerMockito.when(ParameterUtils.isValidCheckType(invalidCheckType)).thenReturn(false);
		PowerMockito.when(ParameterUtils.encryptPassword(Mockito.anyString(), Mockito.anyString())).thenCallRealMethod();
		PowerMockito.when(ParameterUtils.validateCaptchaStatus(UserConstants.CAPTCHA_CHECKTYPE_BINDING, mobile)).thenReturn(UserConstants.CAPTCHA_VALIDATION_RESULT_SUCCESS);
		PowerMockito.when(ParameterUtils.isValidEmail(Mockito.anyString())).thenCallRealMethod();
		PowerMockito.when(ParameterUtils.isValidPostcode(Mockito.anyString())).thenCallRealMethod();
		PowerMockito.when(ParameterUtils.isValidQQ(Mockito.anyString())).thenCallRealMethod();
		PowerMockito.when(ParameterUtils.isValidSex(Mockito.anyInt())).thenCallRealMethod();
		
		smsr = new SmsResponse();
		smsr.setMessage("haha");
		smsr.setResult(UserConstants.BOOLEAN_TRUE);
		smsr.setPhone(mobile);
		// = new SmsUtils(sysConfig.getSmsHost());
		PowerMockito.when(smsUtils.sendCaptcha(Mockito.anyString(), Mockito.anyString(), Mockito.anyInt())).thenReturn(smsr);
		
		//正确插入用户
		try
		{
			createResponse = userComponent.createUser(appId, mobile, password, getUserInfo(), getExtendInfo(), false);
			assertNotNull(createResponse);
			assertEquals(mobile, createResponse.getUserCode());
			assertEquals(appId, createResponse.getAppId());
			assertNotEquals(0, createResponse.getUid());
			testUsers.add(createResponse.getUid());
		}
		catch (Exception e)
		{
			fail("");
		}
		
		//数据库操作出错
		PowerMockito.when(VerifyRequestDAO.insert(Mockito.any(VerifyRequest.class))).thenReturn(0);
		try
		{
			captchaComponent.sendCaptcha(appId, mobile, checkType);
		}
		catch (BusinessException e)
		{
			assertEquals(BUSINESS_CODE.INTERNAL_SERVER_ERROR, e.getErrCode());
		}
		PowerMockito.when(VerifyRequestDAO.insert(Mockito.any(VerifyRequest.class))).thenCallRealMethod();
		
		//未查询到短信模板
		try
		{
			captchaComponent.sendCaptcha(appId3, mobile, checkType);
		}
		catch (BusinessException e)
		{
			assertEquals(BUSINESS_CODE.INTERNAL_SERVER_ERROR, e.getErrCode());
		}
		
		//正确发送验证码
		try
		{
			sendResponse = captchaComponent.sendCaptcha(appId, mobile, checkType);
			assertNotNull(sendResponse);
			assertNotEquals(0, sendResponse.getTimeout());
		}
		catch (Exception e)
		{
			fail("");
		}
		
		//appId不合法
		try
		{
			captchaComponent.sendCaptcha(invalidAppid, mobile, checkType);
		}
		catch (BusinessException e)
		{
			assertEquals(BUSINESS_CODE.APP_ID_INVALID, e.getErrCode());
		}
		
		//mobile不合法
		try
		{
			captchaComponent.sendCaptcha(appId, invalidMobile, checkType);
		}
		catch (BusinessException e)
		{
			assertEquals(BUSINESS_CODE.MOBILE_INVALID, e.getErrCode());
		}
		
		//checkType不合法
		try
		{
			captchaComponent.sendCaptcha(appId, mobile, invalidCheckType);
		}
		catch (BusinessException e)
		{
			assertEquals(BUSINESS_CODE.SEND_CAPTCHA_CHECKTYPE_INVALID, e.getErrCode());
		}
		
		//短信发送失败，返回为null
		PowerMockito.when(smsUtils.sendCaptcha(Mockito.anyString(), Mockito.anyString(), Mockito.anyInt())).thenReturn(null);
		try
		{
			captchaComponent.sendCaptcha(appId, mobile, checkType);
		}
		catch (BusinessException e)
		{
			assertEquals(BUSINESS_CODE.INTERNAL_SERVER_ERROR, e.getErrCode());
		}
		
		//短信发送失败，返回为false
		smsr = new SmsResponse();
		smsr.setMessage("haha");
		smsr.setResult(UserConstants.BOOLEAN_FALSE);
		smsr.setPhone(mobile);
		PowerMockito.when(smsUtils.sendCaptcha(Mockito.anyString(), Mockito.anyString(), Mockito.anyInt())).thenReturn(smsr);
		try
		{
			captchaComponent.sendCaptcha(appId, mobile, checkType);
		}
		catch (BusinessException e)
		{
			assertEquals(BUSINESS_CODE.INTERNAL_SERVER_ERROR, e.getErrCode());
		}
	}

	@Test
	public void testValidateCaptcha()
	{
		SmsUtils smsUtils = PowerMockito.mock(SmsUtils.class);		
		try
		{
			PowerMockito.whenNew(SmsUtils.class).withAnyArguments().thenReturn(smsUtils);
		}
		catch (Exception e)
		{
			fail("");
		}
		
		UserCaptchaComponent captchaComponent = new UserCaptchaComponent();
		
		CreateUserResponse createResponse = null;
		SendCaptchaResponse sendResponse = null;
		String mobile = null;
		
		mobile = getMobile();
		//预设mock
		PowerMockito.mockStatic(ParameterUtils.class);
		PowerMockito.mockStatic(SmsUtils.class);
		PowerMockito.when(ParameterUtils.isValidMobile(mobile)).thenReturn(true);
		PowerMockito.when(ParameterUtils.isValidMobile(invalidMobile)).thenReturn(false);
		PowerMockito.when(ParameterUtils.isValidAppId(appId)).thenReturn(true);
		PowerMockito.when(ParameterUtils.isValidPassword(password)).thenReturn(true);
		PowerMockito.when(ParameterUtils.isValidCheckType(checkType)).thenReturn(true);
		PowerMockito.when(ParameterUtils.isValidCheckType(invalidCheckType)).thenReturn(false);
		PowerMockito.when(ParameterUtils.isValidCaptcha(Mockito.anyString())).thenCallRealMethod();
		PowerMockito.when(ParameterUtils.encryptPassword(Mockito.anyString(), Mockito.anyString())).thenCallRealMethod();
		PowerMockito.when(ParameterUtils.validateCaptchaStatus(UserConstants.CAPTCHA_CHECKTYPE_BINDING, mobile)).thenReturn(UserConstants.CAPTCHA_VALIDATION_RESULT_SUCCESS);
		PowerMockito.when(ParameterUtils.isValidEmail(Mockito.anyString())).thenCallRealMethod();
		PowerMockito.when(ParameterUtils.isValidPostcode(Mockito.anyString())).thenCallRealMethod();
		PowerMockito.when(ParameterUtils.isValidQQ(Mockito.anyString())).thenCallRealMethod();
		PowerMockito.when(ParameterUtils.isValidSex(Mockito.anyInt())).thenCallRealMethod();
		
		SmsResponse smsr = new SmsResponse();
		smsr.setMessage("haha");
		smsr.setResult(UserConstants.BOOLEAN_TRUE);
		smsr.setPhone(mobile);
		PowerMockito.when(smsUtils.sendCaptcha(Mockito.anyString(), Mockito.anyString(), Mockito.anyInt())).thenReturn(smsr);
		
		SmsResponse sr = new SmsResponse();
		sr.setResult("true");
		sr.setPhone(mobile);
		PowerMockito.when(smsUtils.checkCaptcha(mobile, captcha)).thenReturn(sr);
		
		//正确插入用户
		try
		{
			createResponse = userComponent.createUser(appId, mobile, password, getUserInfo(), getExtendInfo(), false);
			assertNotNull(createResponse);
			assertEquals(mobile, createResponse.getUserCode());
			assertEquals(appId, createResponse.getAppId());
			assertNotEquals(0, createResponse.getUid());
			testUsers.add(createResponse.getUid());
		}
		catch (Exception e)
		{
			fail("");
		}
		
		//正确发送验证码, 并正确验证
		try
		{
			sendResponse = captchaComponent.sendCaptcha(appId, mobile, checkType);
			assertNotNull(sendResponse);
			assertNotEquals(0, sendResponse.getTimeout());
			
			//数据库操作失败
			PowerMockito.when(VerifyRequestDAO.insert(Mockito.any(VerifyRequest.class))).thenReturn(0);
//			assertFalse(captchaComponent.validateCaptcha(appId, mobile, captcha, checkType));
			PowerMockito.when(VerifyRequestDAO.insert(Mockito.any(VerifyRequest.class))).thenCallRealMethod();
			
			
			sendResponse = captchaComponent.sendCaptcha(appId, mobile, checkType);
			assertTrue(captchaComponent.validateCaptcha(appId, mobile, captcha, checkType));
		}
		catch (Exception e)
		{
			fail("");
		}
		
		//appId不合法
		try
		{
			captchaComponent.validateCaptcha(invalidAppid, mobile, captcha, checkType);
		}
		catch (BusinessException e)
		{
			assertEquals(BUSINESS_CODE.APP_ID_INVALID, e.getErrCode());
		}
		
		//mobile不合法
		try
		{
			captchaComponent.validateCaptcha(appId, invalidMobile, captcha, checkType);
		}
		catch (BusinessException e)
		{
			assertEquals(BUSINESS_CODE.MOBILE_INVALID, e.getErrCode());
		}
		
		//checkType不合法
		try
		{
			captchaComponent.validateCaptcha(appId, mobile, captcha, invalidCheckType);
		}
		catch (BusinessException e)
		{
			assertEquals(BUSINESS_CODE.VALIDATION_CAPTCHA_CHECKTYPE_INVALID, e.getErrCode());
		}
		
		//验证码不合法
		try
		{
			captchaComponent.validateCaptcha(appId, mobile, invalidCaptcha, checkType);
		}
		catch (BusinessException e)
		{
			assertEquals(BUSINESS_CODE.VALIDATION_CAPTCHA_CAPTCHA_INVALID, e.getErrCode());
		}
		
		//verifyrequest超时,导致验证码过期
		try
		{
			sendResponse = captchaComponent.sendCaptcha(appId, mobile, checkType);
			assertNotNull(sendResponse);
			assertNotEquals(0, sendResponse.getTimeout());
			//VerifyRequest vr = VerifyRequestDAO.findWithMobile(mobile);
			//设置为现已过期
			//vr.setExpireTime(new Date(System.currentTimeMillis() - 10000));
			//VerifyRequestDAO.deleteWithMobile(mobile);
			//VerifyRequestDAO.insert(vr);
			VerifyRequestEntity vre = verifyRequestCache.get(mobile);
			vre.setExpireTime(new Date(System.currentTimeMillis() - 10000));
			verifyRequestCache.put(mobile, vre);
			captchaComponent.validateCaptcha(appId, mobile, captcha, checkType);
		}
		catch (BusinessException e)
		{
			assertEquals(BUSINESS_CODE.VALIDATION_CAPTCHA_CAPTCHA_EXPIRY, e.getErrCode());
		}
		
		//verifyrequest为空,导致验证码过期
		try
		{
			sendResponse = captchaComponent.sendCaptcha(appId, mobile, checkType);
			assertNotNull(sendResponse);
			assertNotEquals(0, sendResponse.getTimeout());
			VerifyRequestDAO.deleteWithMobile(mobile);
			captchaComponent.validateCaptcha(appId, mobile, captcha, checkType);
		}
		catch (BusinessException e)
		{
			assertEquals(BUSINESS_CODE.VALIDATION_CAPTCHA_CAPTCHA_EXPIRY, e.getErrCode());
		}
		
		//验证出错
		try
		{
			//重新发送验证码
			captchaComponent.sendCaptcha(appId, mobile, checkType);
			sr = new SmsResponse();
			sr.setResult("false");
			sr.setPhone(mobile);
			PowerMockito.when(smsUtils.checkCaptcha(Mockito.anyString(), Mockito.anyString())).thenReturn(sr);
			assertFalse(captchaComponent.validateCaptcha(appId, mobile, wrongCaptcha, checkType));
		}
		catch (BusinessException e)
		{
			fail("");
		}
		
		//短信发送失败，返回为null
		try
		{
			captchaComponent.sendCaptcha(appId, mobile, checkType);
			PowerMockito.when(smsUtils.checkCaptcha(Mockito.anyString(), Mockito.anyString())).thenCallRealMethod();
			captchaComponent.validateCaptcha(appId, mobile, wrongCaptcha, checkType);
		}
		catch (BusinessException e)
		{
			assertEquals(BUSINESS_CODE.INTERNAL_SERVER_ERROR, e.getErrCode());
		}
	}
	
	@Ignore
	@Test
	public void testCaptcha()
	{
		SmsUtils smsUtils = PowerMockito.mock(SmsUtils.class);		
		try
		{
			PowerMockito.whenNew(SmsUtils.class).withAnyArguments().thenReturn(smsUtils);
		}
		catch (Exception e)
		{
			fail("");
		}
		
		UserCaptchaComponent captchaComponent = new UserCaptchaComponent();
		String mobile = null;
		
		PowerMockito.mockStatic(ParameterUtils.class);
		//PowerMockito.mockStatic(SmsUtils.class);
		PowerMockito.when(ParameterUtils.isValidMobile(Mockito.anyString())).thenCallRealMethod();
		PowerMockito.when(ParameterUtils.isValidCheckType(checkType)).thenReturn(true);
		PowerMockito.when(ParameterUtils.isValidAppId(appId)).thenReturn(true);
		PowerMockito.when(ParameterUtils.isValidCaptcha(captcha)).thenReturn(true);
		
		SmsResponse smsr = new SmsResponse();
		smsr.setMessage("haha");
		smsr.setResult(UserConstants.BOOLEAN_TRUE);
		smsr.setPhone("");
		PowerMockito.when(smsUtils.sendCaptcha(Mockito.anyString(), Mockito.anyString(), Mockito.anyInt())).thenReturn(smsr);
		
		SmsResponse sr = new SmsResponse();
		sr.setResult("true");
		sr.setPhone("");
		PowerMockito.when(smsUtils.checkCaptcha(Mockito.anyString(), Mockito.anyString())).thenReturn(sr);
		
		try
		{
			CaptchaThread ct = null;
			for (int i=0; i<40; i++)
			{
				ct = new CaptchaThread(appId, captcha, checkType, this, captchaComponent);
				System.out.println("thread: " + ct.getName());
				ct.run();
			}
		}
		catch (Exception e)
		{
			fail("");
		}
	}

	protected static class CaptchaThread extends Thread
    {
    	private int appId;
    	private String captcha;
    	private int checkType;
    	private UserCaptchaComponentTest ins;
    	private UserCaptchaComponent component;
    	
    	@SuppressWarnings("unused")
        private CaptchaThread()
    	{
    		
        }
    	
    	public CaptchaThread(int appId, String captcha, int checkType, UserCaptchaComponentTest ins, UserCaptchaComponent component)
    	{
    		this.appId = appId;
    		this.captcha = captcha;
    		this.checkType = checkType;
    		this.ins = ins;
    		this.component = component;
    	}
    	
    	@Override
    	public void run()
    	{
    		try
    		{
    			for (int i=0; i<500; i++)
    			{
    				String mobile = ins.getMobile();
            	    component.sendCaptcha(appId, mobile, checkType);
            	    if (!component.validateCaptcha(appId, mobile, captcha, checkType))
            	    {
            	    	System.out.println("error: " + mobile + " index: " + i);
            	    }
            	    else
            	    {
            	    	System.out.println(mobile + " index: " + i);
            	    }
    			}
    		}
    	    catch (Exception e)
    		{
    	    	e.printStackTrace();
    		}
    	}
    }

	private void clean()
	{
		for (Long uid : testUsers)
		{
			UserInfoDAO.deleteUser(uid);
			UserExtendInfoDAO.deleteWithUid(uid);
			UserDAO.deleteUser(uid);
		}
		testUsers.clear();
		index = 1;
	}
	
	private String getUserInfo()
	{
		UserInfo userInfo = new UserInfo();
		userInfo.setAddress("测试地址");
		userInfo.setEditDate(new Date());
		userInfo.setEmail("test@text.com");
		userInfo.setPostcode("534544");
		userInfo.setQq("23423423");
		userInfo.setSex(1);
		userInfo.setUserName("测试用户名");
		return gson.toJson(userInfo);
	}
	
	private String getExtendInfo()
	{
		String json = "{\"a\":\"av\",\"b\":\"bv\",\"c\":\"cv\",\"d\":\"dv\"}";
		return json;
	}
	
	private synchronized String getMobile()
	{
		String mobile = String.valueOf(18800000000l + index);
		index++;
		return mobile;
	}
}
