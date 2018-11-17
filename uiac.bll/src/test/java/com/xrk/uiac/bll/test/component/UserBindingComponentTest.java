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
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.esotericsoftware.minlog.Log.Logger;
import com.google.gson.Gson;
import com.xrk.uiac.bll.cache.CacheService;
import com.xrk.uiac.bll.common.ParameterUtils;
import com.xrk.uiac.bll.common.UserConstants;
import com.xrk.uiac.bll.component.impl.UserBindingComponent;
import com.xrk.uiac.bll.component.impl.UserComponent;
import com.xrk.uiac.bll.exception.BUSINESS_CODE;
import com.xrk.uiac.bll.exception.BusinessException;
import com.xrk.uiac.bll.response.CreateUserResponse;
import com.xrk.uiac.bll.response.GetBindingStatusResponse;
import com.xrk.uiac.dal.Dal;
import com.xrk.uiac.dal.DalTestHelper;
import com.xrk.uiac.dal.dao.AccountChangeRecordDAO;
import com.xrk.uiac.dal.dao.UserDAO;
import com.xrk.uiac.dal.dao.UserExtendInfoDAO;
import com.xrk.uiac.dal.dao.UserInfoDAO;
import com.xrk.uiac.dal.entity.User;
import com.xrk.uiac.dal.entity.UserInfo;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore( {"javax.management.*"}) 
@PrepareForTest({ParameterUtils.class, UserDAO.class, UserInfoDAO.class, Logger.class})
public class UserBindingComponentTest
{
	private UserComponent userComponent = new UserComponent();
	private Set<Long> testUsers = new HashSet<Long>();
	private Gson gson = new Gson();
	private long index = 1;
	private String password = "password";
	private int invalidAppid = -1;
	private int appId = 1002;
	private String accessToken = "access";
	private String expireToken = "expire";
	private String invalidToken = "invalid";
	private long invalidUid = 1;
	private long zeroUid = 0;
	private String bindingMobile = "16800001234";
	private String bindingMobile2 = "16800002345";
	private String invalidMobile = "168000";
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{
		PowerMockito.mockStatic(Logger.class);
		
		DalTestHelper.initDal();
		CacheService.Init();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception
	{
		Dal.dispose();
		CacheService.cleanAll(false);
	}

	@Before
	public void setUp() throws Exception
	{
		PowerMockito.mockStatic(Logger.class);
		//对UserDAO进行mock
		PowerMockito.mockStatic(UserDAO.class);
		PowerMockito.when(UserDAO.findWithAccount(Mockito.anyString())).thenCallRealMethod();
		PowerMockito.when(UserDAO.findWithUid(Mockito.anyLong())).thenCallRealMethod();
		PowerMockito.when(UserDAO.insertUser(Mockito.any(User.class))).thenCallRealMethod();
		PowerMockito.when(UserDAO.deleteUser(Mockito.anyLong())).thenCallRealMethod();
		PowerMockito.when(UserDAO.count()).thenCallRealMethod();
		PowerMockito.when(UserDAO.updateAccount(Mockito.anyLong(), Mockito.anyString())).thenCallRealMethod();
		PowerMockito.when(UserDAO.updateStatus(Mockito.anyLong(), Mockito.anyInt())).thenCallRealMethod();
		
		//对UserInfoDAO进行mock
		PowerMockito.mockStatic(UserInfoDAO.class);
		PowerMockito.when(UserInfoDAO.deleteUser(Mockito.anyLong())).thenCallRealMethod();
		PowerMockito.when(UserInfoDAO.findWithMobile(Mockito.anyString())).thenCallRealMethod();
		PowerMockito.when(UserInfoDAO.findWithUid(Mockito.anyLong())).thenCallRealMethod();
		PowerMockito.when(UserInfoDAO.getUpdateMap(Mockito.anyMap())).thenCallRealMethod();
		PowerMockito.when(UserInfoDAO.insert(Mockito.any(UserInfo.class))).thenCallRealMethod();
		PowerMockito.when(UserInfoDAO.update(Mockito.anyLong(), Mockito.anyMap())).thenCallRealMethod();
		PowerMockito.when(UserInfoDAO.updateMobileBindingStatus(Mockito.anyLong(), Mockito.anyString(), Mockito.anyInt())).thenCallRealMethod();
	}

	@After
	public void tearDown() throws Exception
	{
		clean();
		CacheService.cleanAll(false);
	}

	@Test
	public void testGetBindingStatus()
	{
		UserBindingComponent bindingComponent = new UserBindingComponent();
		CreateUserResponse createResponse = null;
		GetBindingStatusResponse getBindingStatusResponse = null;
		String mobile = null;
		String mobile2 = null;
		long uid = 0;
		long uid2 = 0;
		
		mobile = getMobile();
		mobile2 = getMobile();
		//预设mock
		PowerMockito.mockStatic(ParameterUtils.class);
		PowerMockito.when(ParameterUtils.isValidMobile(mobile)).thenReturn(true);
		PowerMockito.when(ParameterUtils.isValidMobile(mobile2)).thenReturn(true);
		PowerMockito.when(ParameterUtils.isValidMobile(bindingMobile)).thenReturn(true);
		PowerMockito.when(ParameterUtils.isValidMobile(bindingMobile2)).thenReturn(true);
		PowerMockito.when(ParameterUtils.isValidAppId(appId)).thenReturn(true);
		PowerMockito.when(ParameterUtils.isValidPassword(password)).thenReturn(true);
		PowerMockito.when(ParameterUtils.encryptPassword(Mockito.anyString(), Mockito.anyString())).thenCallRealMethod();
		PowerMockito.when(ParameterUtils.validateCaptchaStatus(UserConstants.CAPTCHA_CHECKTYPE_BINDING, mobile)).thenReturn(UserConstants.CAPTCHA_VALIDATION_RESULT_SUCCESS);
		PowerMockito.when(ParameterUtils.validateCaptchaStatus(UserConstants.CAPTCHA_CHECKTYPE_BINDING, mobile2)).thenReturn(UserConstants.CAPTCHA_VALIDATION_RESULT_SUCCESS);
		PowerMockito.when(ParameterUtils.isValidEmail(Mockito.anyString())).thenCallRealMethod();
		PowerMockito.when(ParameterUtils.isValidPostcode(Mockito.anyString())).thenCallRealMethod();
		PowerMockito.when(ParameterUtils.isValidQQ(Mockito.anyString())).thenCallRealMethod();
		PowerMockito.when(ParameterUtils.isValidSex(Mockito.anyInt())).thenCallRealMethod();
		
		//正确插入用户
		try
		{
			createResponse = userComponent.createUser(appId, mobile, password, getUserInfo(), getExtendInfo(), false);
			assertNotNull(createResponse);
			assertEquals(mobile, createResponse.getUserCode());
			assertEquals(appId, createResponse.getAppId());
			assertNotEquals(0, createResponse.getUid());
			testUsers.add(createResponse.getUid());
			uid = createResponse.getUid();
			
			createResponse = userComponent.createUser(appId, mobile2, password, getUserInfo(), getExtendInfo(), false);
			testUsers.add(createResponse.getUid());
			uid2 = createResponse.getUid();
		}
		catch (Exception e)
		{
			fail("");
		}
		
		PowerMockito.when(ParameterUtils.validateAccessToken(expireToken, uid, appId)).thenReturn(UserConstants.ACCESSTOKEN_VALIDATION_RESULT_EXPIRE);
		PowerMockito.when(ParameterUtils.validateAccessToken(accessToken, uid, appId)).thenReturn(UserConstants.ACCESSTOKEN_VALIDATION_RESULT_SUCCESS);
		PowerMockito.when(ParameterUtils.validateAccessToken(invalidToken, uid, appId)).thenReturn(UserConstants.ACCESSTOKEN_VALIDATION_RESULT_INVALID);
		
		//正确绑定手机号
		try
		{
			UserInfo ui = UserInfoDAO.findWithUid(uid);
			
			//默认绑定手机号
			//assertEquals(UserConstants.MOBILE_IS_NOT_VERIFIED, ui.getMobileIsVerify());
			assertEquals(UserConstants.MOBILE_IS_VERIFIED, ui.getMobileIsVerify());
			
			ui = UserInfoDAO.findWithUid(uid);
			assertEquals(UserConstants.MOBILE_IS_VERIFIED, ui.getMobileIsVerify());
			
			//注册的手机号就是他本身的绑定手机号
			assertEquals(mobile, ui.getMobile());
			
			bindingComponent.bindMobile(appId, accessToken, uid, bindingMobile, false);
		}
		catch (BusinessException e)
		{
			assertEquals(BUSINESS_CODE.BINDING_MOBILE_NUMBER_OCCUPIED, e.getErrCode());
		}

//		//正确获取绑定状态
//		try
//		{
//			getBindingStatusResponse = bindingComponent.getBindingStatus(appId, accessToken, uid);
//			assertTrue(getBindingStatusResponse.getMobile());
//			
//			getBindingStatusResponse = bindingComponent.getBindingStatus(appId, accessToken, uid2);
//			assertFalse(getBindingStatusResponse.getMobile());
//		}
//		catch (BusinessException e)
//		{
//			fail("");
//		}
//		
//		//更新为另一个手机号后，取消已绑定的状态
//		try
//		{
//			assertTrue(userComponent.updateUserInfo(appId, mobile, uid, "{\"mobile\":\"" + bindingMobile2 + "\"}", null));
//			getBindingStatusResponse = bindingComponent.getBindingStatus(appId, accessToken, uid);
//			assertFalse(getBindingStatusResponse.getMobile());
//		}
//		catch (BusinessException e)
//		{
//			fail("");
//		}
		
		//appId不合法
		try
		{
			bindingComponent.getBindingStatus(invalidAppid, accessToken, uid);
		}
		catch (BusinessException e)
		{
			assertEquals(BUSINESS_CODE.APP_ID_INVALID, e.getErrCode());
		}
		
		//accessToken不合法
		try
		{
			bindingComponent.getBindingStatus(appId, invalidToken, uid);
		}
		catch (BusinessException e)
		{
			assertEquals(BUSINESS_CODE.ACCESS_TOKEN_INVALID, e.getErrCode());
		}
		
		//accessToken过期
		try
		{
			bindingComponent.getBindingStatus(appId, expireToken, uid);
		}
		catch (BusinessException e)
		{
			assertEquals(BUSINESS_CODE.ACCESS_TOKEN_EXPIRE, e.getErrCode());
		}
		
		//uid不合法
		try
		{
			bindingComponent.getBindingStatus(appId, accessToken, zeroUid);
		}
		catch (BusinessException e)
		{
			assertEquals(BUSINESS_CODE.UID_INVALID, e.getErrCode());
		}
	}

	@Test
	public void testBindMobile()
	{
		UserBindingComponent bindingComponent = new UserBindingComponent();
		CreateUserResponse createResponse = null;
		String mobile = null;
		String mobile2 = null;
		long uid = 0;
		long uid2 = 0;
		
		mobile = getMobile();
		mobile2 = getMobile();
		//预设mock
		PowerMockito.mockStatic(ParameterUtils.class);
		PowerMockito.when(ParameterUtils.isValidMobile(mobile)).thenReturn(true);
		PowerMockito.when(ParameterUtils.isValidMobile(mobile2)).thenReturn(true);
		PowerMockito.when(ParameterUtils.isValidMobile(bindingMobile)).thenReturn(true);
		PowerMockito.when(ParameterUtils.isValidMobile(bindingMobile2)).thenReturn(true);
		PowerMockito.when(ParameterUtils.isValidAppId(appId)).thenReturn(true);
		PowerMockito.when(ParameterUtils.isValidPassword(password)).thenReturn(true);
		PowerMockito.when(ParameterUtils.encryptPassword(Mockito.anyString(), Mockito.anyString())).thenCallRealMethod();
		PowerMockito.when(ParameterUtils.validateCaptchaStatus(UserConstants.CAPTCHA_CHECKTYPE_BINDING, mobile)).thenReturn(UserConstants.CAPTCHA_VALIDATION_RESULT_SUCCESS);
		PowerMockito.when(ParameterUtils.isValidEmail(Mockito.anyString())).thenCallRealMethod();
		PowerMockito.when(ParameterUtils.isValidPostcode(Mockito.anyString())).thenCallRealMethod();
		PowerMockito.when(ParameterUtils.isValidQQ(Mockito.anyString())).thenCallRealMethod();
		PowerMockito.when(ParameterUtils.isValidSex(Mockito.anyInt())).thenCallRealMethod();
		
		//正确插入用户
		try
		{
			createResponse = userComponent.createUser(appId, mobile, password, getUserInfo(), getExtendInfo(), false);
			assertNotNull(createResponse);
			assertEquals(mobile, createResponse.getUserCode());
			assertEquals(appId, createResponse.getAppId());
			assertNotEquals(0, createResponse.getUid());
			testUsers.add(createResponse.getUid());
			uid = createResponse.getUid();
			
			createResponse = userComponent.createUser(appId, mobile2, password, getUserInfo(), getExtendInfo(), false);
			testUsers.add(createResponse.getUid());
			uid2 = createResponse.getUid();
		}
		catch (Exception e)
		{
			fail("");
		}
		
		PowerMockito.when(ParameterUtils.validateAccessToken(expireToken, uid, appId)).thenReturn(UserConstants.ACCESSTOKEN_VALIDATION_RESULT_EXPIRE);
		PowerMockito.when(ParameterUtils.validateAccessToken(accessToken, uid, appId)).thenReturn(UserConstants.ACCESSTOKEN_VALIDATION_RESULT_SUCCESS);
		PowerMockito.when(ParameterUtils.validateAccessToken(invalidToken, uid, appId)).thenReturn(UserConstants.ACCESSTOKEN_VALIDATION_RESULT_INVALID);
		
		//更新绑定状态失败（代码覆盖率）
		try
		{
			PowerMockito.when(UserInfoDAO.updateMobileBindingStatus(Mockito.anyLong(), Mockito.anyString(), Mockito.anyInt())).thenReturn(0);
			assertFalse(bindingComponent.bindMobile(appId, accessToken, uid, bindingMobile, false));
		}
		catch (BusinessException e)
		{
			fail("");
		}
		PowerMockito.when(UserInfoDAO.updateMobileBindingStatus(Mockito.anyLong(), Mockito.anyString(), Mockito.anyInt())).thenCallRealMethod();

		//正确绑定手机号
		try
		{
			UserInfo ui = UserInfoDAO.findWithUid(uid);
			User u = UserDAO.findWithUid(uid);
			assertEquals(UserConstants.MOBILE_IS_VERIFIED, ui.getMobileIsVerify());
			assertEquals(mobile, ui.getMobile());
			assertEquals(mobile, u.getAccount());
			
			//绑定另一个手机号
			assertTrue(bindingComponent.bindMobile(appId, accessToken, uid, bindingMobile, false));
			ui = UserInfoDAO.findWithUid(uid);
			u = UserDAO.findWithUid(uid);
			
			assertEquals(UserConstants.MOBILE_IS_VERIFIED, ui.getMobileIsVerify());
			assertEquals(bindingMobile, ui.getMobile());
			assertEquals(bindingMobile, u.getAccount());
		}
		catch (BusinessException e)
		{
			fail("");
		}
		
		//appId不合法
		try
		{
			bindingComponent.bindMobile(invalidAppid, accessToken, uid, bindingMobile, false);
		}
		catch (BusinessException e)
		{
			assertEquals(BUSINESS_CODE.APP_ID_INVALID, e.getErrCode());
		}
		
		//accessToken不合法
		try
		{
			bindingComponent.bindMobile(appId, invalidToken, uid, bindingMobile, false);
		}
		catch (BusinessException e)
		{
			assertEquals(BUSINESS_CODE.ACCESS_TOKEN_INVALID, e.getErrCode());
		}
		
		//accessToken过期
		try
		{
			bindingComponent.bindMobile(appId, expireToken, uid, bindingMobile, false);
		}
		catch (BusinessException e)
		{
			assertEquals(BUSINESS_CODE.ACCESS_TOKEN_EXPIRE, e.getErrCode());
		}

		//手机号不合法
		try
		{
			bindingComponent.bindMobile(appId, accessToken, uid, invalidMobile, false);
		}
		catch (BusinessException e)
		{
			assertEquals(BUSINESS_CODE.MOBILE_INVALID, e.getErrCode());
		}
		
		//uid不合法
		try
		{
			bindingComponent.bindMobile(appId, accessToken, zeroUid, bindingMobile, false);
		}
		catch (BusinessException e)
		{
			assertEquals(BUSINESS_CODE.UID_INVALID, e.getErrCode());
		}
		
		//手机号被占用
		try
		{
			bindingComponent.bindMobile(appId, accessToken, uid2, bindingMobile, false);
		}
		catch (BusinessException e)
		{
			assertEquals(BUSINESS_CODE.BINDING_MOBILE_NUMBER_OCCUPIED, e.getErrCode());
		}
		
		//重复绑定同一个号码
		try
		{
			bindingComponent.bindMobile(appId, accessToken, uid, bindingMobile, false);
		}
		catch (BusinessException e)
		{
			assertEquals(BUSINESS_CODE.BINDING_MOBILE_OPERATION_REPEATED, e.getErrCode());
		}
		
		//验证码验证失败
		try
		{
			PowerMockito.when(ParameterUtils.validateCaptchaStatus(UserConstants.CAPTCHA_CHECKTYPE_BINDING, bindingMobile)).thenReturn(UserConstants.CAPTCHA_VALIDATION_RESULT_UNVERIFIED);
			bindingComponent.bindMobile(appId, accessToken, uid, bindingMobile, false);
		}
		catch (BusinessException e)
		{
			assertEquals(BUSINESS_CODE.CAPTCHA_VALIDATION_UNVERIFIED, e.getErrCode());
		}
		
		//验证码验证结果过期
		try
		{
			PowerMockito.when(ParameterUtils.validateCaptchaStatus(UserConstants.CAPTCHA_CHECKTYPE_BINDING, bindingMobile)).thenReturn(UserConstants.CAPTCHA_VALIDATION_RESULT_EXPIRE);
			bindingComponent.bindMobile(appId, accessToken, uid, bindingMobile, false);
		}
		catch (BusinessException e)
		{
			assertEquals(BUSINESS_CODE.CAPTCHA_VALIDATION_EXPIRE, e.getErrCode());
		}
		
	}

	@Test
	public void testUnBindMobile()
	{
		UserBindingComponent bindingComponent = new UserBindingComponent();
		CreateUserResponse createResponse = null;
		String mobile = null;
		long uid = 0;
		
		mobile = getMobile();
		//预设mock
		PowerMockito.mockStatic(ParameterUtils.class);
		PowerMockito.when(ParameterUtils.isValidMobile(mobile)).thenReturn(true);
		PowerMockito.when(ParameterUtils.isValidMobile(bindingMobile)).thenReturn(true);
		PowerMockito.when(ParameterUtils.isValidAppId(appId)).thenReturn(true);
		PowerMockito.when(ParameterUtils.isValidPassword(password)).thenReturn(true);
		PowerMockito.when(ParameterUtils.encryptPassword(Mockito.anyString(), Mockito.anyString())).thenCallRealMethod();
		PowerMockito.when(ParameterUtils.validateCaptchaStatus(UserConstants.CAPTCHA_CHECKTYPE_BINDING, mobile)).thenReturn(UserConstants.CAPTCHA_VALIDATION_RESULT_SUCCESS);
		PowerMockito.when(ParameterUtils.isValidEmail(Mockito.anyString())).thenCallRealMethod();
		PowerMockito.when(ParameterUtils.isValidPostcode(Mockito.anyString())).thenCallRealMethod();
		PowerMockito.when(ParameterUtils.isValidQQ(Mockito.anyString())).thenCallRealMethod();
		PowerMockito.when(ParameterUtils.isValidSex(Mockito.anyInt())).thenCallRealMethod();
		
		//正确插入用户
		try
		{
			createResponse = userComponent.createUser(appId, mobile, password, getUserInfo(), getExtendInfo(), false);
			assertNotNull(createResponse);
			assertEquals(mobile, createResponse.getUserCode());
			assertEquals(appId, createResponse.getAppId());
			assertNotEquals(0, createResponse.getUid());
			testUsers.add(createResponse.getUid());
			uid = createResponse.getUid();
		}
		catch (Exception e)
		{
			fail("");
		}
		
		PowerMockito.when(ParameterUtils.validateAccessToken(expireToken, uid, appId)).thenReturn(UserConstants.ACCESSTOKEN_VALIDATION_RESULT_EXPIRE);
		PowerMockito.when(ParameterUtils.validateAccessToken(accessToken, uid, appId)).thenReturn(UserConstants.ACCESSTOKEN_VALIDATION_RESULT_SUCCESS);
		PowerMockito.when(ParameterUtils.validateAccessToken(invalidToken, uid, appId)).thenReturn(UserConstants.ACCESSTOKEN_VALIDATION_RESULT_INVALID);
		
		//正确绑定手机号, 解绑手机号
		try
		{
			UserInfo ui = UserInfoDAO.findWithUid(uid);
			assertEquals(UserConstants.MOBILE_IS_VERIFIED, ui.getMobileIsVerify());
			
			//assertTrue(bindingComponent.bindMobile(appId, accessToken, uid, bindingMobile));
			//ui = UserInfoDAO.findWithUid(uid);
			//assertEquals(UserConstants.MOBILE_IS_VERIFIED, ui.getMobileIsVerify());
			//assertEquals(bindingMobile, ui.getMobile());
			
			//更新绑定状态失败
			//PowerMockito.when(UserInfoDAO.updateMobileBindingStatus(Mockito.anyLong(), Mockito.anyString(), Mockito.anyInt())).thenReturn(0);
			//assertFalse(bindingComponent.unBindMobile(appId, accessToken, uid));
			//PowerMockito.when(UserInfoDAO.updateMobileBindingStatus(Mockito.anyLong(), Mockito.anyString(), Mockito.anyInt())).thenCallRealMethod();
			
			bindingComponent.unBindMobile(appId, accessToken, uid, false);
			//ui = UserInfoDAO.findWithUid(uid);
			//assertEquals(UserConstants.MOBILE_IS_NOT_VERIFIED, ui.getMobileIsVerify());
			//assertNotEquals(bindingMobile, ui.getMobile());
		}
		catch (BusinessException e)
		{
			//fail("");
			assertEquals(BUSINESS_CODE.UNBINDING_MOBILE_OPERATION_NOT_ALLOWED, e.getErrCode());
		}
		
		//appId不合法
		try
		{
			bindingComponent.unBindMobile(invalidAppid, accessToken, uid, false);
		}
		catch (BusinessException e)
		{
			assertEquals(BUSINESS_CODE.APP_ID_INVALID, e.getErrCode());
		}
		
		//accessToken不合法
		try
		{
			bindingComponent.unBindMobile(appId, invalidToken, uid, false);
		}
		catch (BusinessException e)
		{
			assertEquals(BUSINESS_CODE.ACCESS_TOKEN_INVALID, e.getErrCode());
		}
		
		//accessToken过期
		try
		{
			bindingComponent.unBindMobile(appId, expireToken, uid, false);
		}
		catch (BusinessException e)
		{
			assertEquals(BUSINESS_CODE.ACCESS_TOKEN_EXPIRE, e.getErrCode());
		}
		
		//uid不合法
		try
		{
			bindingComponent.unBindMobile(appId, accessToken, zeroUid, false);
		}
		catch (BusinessException e)
		{
			assertEquals(BUSINESS_CODE.UID_INVALID, e.getErrCode());
		}
		
		//用户不存在
		try
		{
			bindingComponent.unBindMobile(appId, accessToken, invalidUid, false);
		}
		catch (BusinessException e)
		{
			assertEquals(BUSINESS_CODE.USER_INVALID, e.getErrCode());
		}
		
		//重复操作
//		try
//		{
//			bindingComponent.unBindMobile(appId, accessToken, uid);
//		}
//		catch (BusinessException e)
//		{
//			assertEquals(BUSINESS_CODE.UNBINDING_MOBILE_OPERATION_REPEATED, e.getErrCode());
//		}
		
		//验证码验证失败
		try
		{
			PowerMockito.when(ParameterUtils.validateCaptchaStatus(Mockito.anyInt(), Mockito.anyString())).thenReturn(UserConstants.CAPTCHA_VALIDATION_RESULT_UNVERIFIED);
			bindingComponent.unBindMobile(appId, accessToken, uid, false);
		}
		catch (BusinessException e)
		{
			assertEquals(BUSINESS_CODE.CAPTCHA_VALIDATION_UNVERIFIED, e.getErrCode());
		}
		
		//验证码验证结果过期
		try
		{
			PowerMockito.when(ParameterUtils.validateCaptchaStatus(Mockito.anyInt(), Mockito.anyString())).thenReturn(UserConstants.CAPTCHA_VALIDATION_RESULT_EXPIRE);
			bindingComponent.unBindMobile(appId, accessToken, uid, false);
		}
		catch (BusinessException e)
		{
			assertEquals(BUSINESS_CODE.CAPTCHA_VALIDATION_EXPIRE, e.getErrCode());
		}
	}

	private void clean()
	{
		for (Long uid : testUsers)
		{
			UserInfoDAO.deleteUser(uid);
			UserExtendInfoDAO.deleteWithUid(uid);
			UserDAO.deleteUser(uid);
			AccountChangeRecordDAO.deleteAll();
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
		userInfo.setPostcode("342433");
		userInfo.setQq("423432424");
		userInfo.setSex(1);
		userInfo.setUserName("测试用户名");
		userInfo.setMobile(bindingMobile);
		return gson.toJson(userInfo);
	}
	
	private String getExtendInfo()
	{
		String json = "{\"a\":\"av\",\"b\":\"bv\",\"c\":\"cv\",\"d\":\"dv\"}";
		return json;
	}
	
	private String getMobile()
	{
		String mobile = String.format("1880000000%d", index);
		index++;
		return mobile;
	}
}
