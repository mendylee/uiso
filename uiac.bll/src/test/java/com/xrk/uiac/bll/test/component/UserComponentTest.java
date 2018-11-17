package com.xrk.uiac.bll.test.component;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xrk.hws.common.logger.Logger;
import com.xrk.uiac.bll.cache.CacheService;
import com.xrk.uiac.bll.cache.component.UserExtendInfoCache;
import com.xrk.uiac.bll.common.ParameterUtils;
import com.xrk.uiac.bll.common.UserConstants;
import com.xrk.uiac.bll.component.impl.UserComponent;
import com.xrk.uiac.bll.exception.BUSINESS_CODE;
import com.xrk.uiac.bll.exception.BusinessException;
import com.xrk.uiac.bll.response.CheckParameterResponse;
import com.xrk.uiac.bll.response.CreateUserResponse;
import com.xrk.uiac.bll.response.GetUserInfoResponse;
import com.xrk.uiac.bll.response.UserInfoResponse;
import com.xrk.uiac.dal.Dal;
import com.xrk.uiac.dal.DalTestHelper;
import com.xrk.uiac.dal.dao.UserDAO;
import com.xrk.uiac.dal.dao.UserExtendInfoDAO;
import com.xrk.uiac.dal.dao.UserInfoDAO;
import com.xrk.uiac.dal.entity.User;
import com.xrk.uiac.dal.entity.UserExtendInfo;
import com.xrk.uiac.dal.entity.UserInfo;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore( {"javax.management.*"})
@PrepareForTest({ParameterUtils.class, UserDAO.class, UserInfoDAO.class, UserExtendInfoDAO.class, Logger.class})
public class UserComponentTest
{
	private UserComponent userComponent = new UserComponent();
	private Set<Long> testUsers = new HashSet<Long>();
	private Gson gson = new Gson();
	private long index = 1;
	private String password = "password";
	private String invalidPassword = "wrong";
	private int invalidAppid = 10;
	private int appId = 1002;
	private String invalidMobile = "158000";
	private String noUserAccount = "13455554444";
	private String accessToken = "access";
	private String expireToken = "expire";
	private String invalidToken = "invalid";
	private long invalidUid = 1;
	private long zeroUid = 0;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{
		//PowerMockito.mockStatic(Logger.class);
		DalTestHelper.initDal();
		CacheService.Init();
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
		PowerMockito.mockStatic(Logger.class);
		clean();
		
		//对UserDAO进行mock
		PowerMockito.mockStatic(UserDAO.class);
		PowerMockito.when(UserDAO.findWithAccount(Mockito.anyString())).thenCallRealMethod();
		PowerMockito.when(UserDAO.findWithUid(Mockito.anyLong())).thenCallRealMethod();
		PowerMockito.when(UserDAO.insertUser(Mockito.any(User.class))).thenCallRealMethod();
		PowerMockito.when(UserDAO.deleteUser(Mockito.anyLong())).thenCallRealMethod();
		PowerMockito.when(UserDAO.count()).thenCallRealMethod();
		PowerMockito.when(UserDAO.findExistingUserWithAccount(Mockito.anyString())).thenCallRealMethod();
		
		//对UserInfoDAO进行mock
		PowerMockito.mockStatic(UserInfoDAO.class);
		PowerMockito.when(UserInfoDAO.deleteUser(Mockito.anyLong())).thenCallRealMethod();
		PowerMockito.when(UserInfoDAO.findWithMobile(Mockito.anyString())).thenCallRealMethod();
		PowerMockito.when(UserInfoDAO.findWithUid(Mockito.anyLong())).thenCallRealMethod();
		PowerMockito.when(UserInfoDAO.getUpdateMap(Mockito.anyMap())).thenCallRealMethod();
		PowerMockito.when(UserInfoDAO.insert(Mockito.any(UserInfo.class))).thenCallRealMethod();
		PowerMockito.when(UserInfoDAO.update(Mockito.anyLong(), Mockito.anyMap())).thenCallRealMethod();
		PowerMockito.when(UserInfoDAO.updateMobileBindingStatus(Mockito.anyLong(), Mockito.anyString(), Mockito.anyInt())).thenCallRealMethod();
		
		//对UserExtendInfoDAO进行mock
		PowerMockito.mockStatic(UserExtendInfoDAO.class);
		PowerMockito.when(UserExtendInfoDAO.deleteWithUid(Mockito.anyLong())).thenCallRealMethod();
		PowerMockito.when(UserExtendInfoDAO.findListWithUid(Mockito.anyLong())).thenCallRealMethod();
		PowerMockito.when(UserExtendInfoDAO.getExtendInfoFromJson(Mockito.anyLong(), Mockito.anyString())).thenCallRealMethod();
		PowerMockito.when(UserExtendInfoDAO.getJsonFromExtendInfoList(Mockito.anyList())).thenCallRealMethod();
		PowerMockito.when(UserExtendInfoDAO.insertList(Mockito.anyList())).thenCallRealMethod();
		PowerMockito.when(UserExtendInfoDAO.updateWithUid(Mockito.anyLong(), Mockito.anyList())).thenCallRealMethod();
		PowerMockito.when(UserExtendInfoDAO.insert(Mockito.any(UserExtendInfo.class))).thenCallRealMethod();
		PowerMockito.when(UserExtendInfoDAO.findWithUidAndKey(Mockito.anyLong(), Mockito.anyString())).thenCallRealMethod();
	}

	@After
	public void tearDown() throws Exception
	{
		clean();
	}
	
	@Test
	public void testCheckParameter()
	{
		CheckParameterResponse response = null;
		CreateUserResponse cResponse = null;
		String mobile = null;
		
		mobile = getMobile();
		//预设mock
		PowerMockito.mockStatic(ParameterUtils.class);
		PowerMockito.when(ParameterUtils.isValidMobile(mobile)).thenReturn(true);
		PowerMockito.when(ParameterUtils.isValidAppId(appId)).thenReturn(true);
		PowerMockito.when(ParameterUtils.isValidPassword(password)).thenReturn(true);
		PowerMockito.when(ParameterUtils.isValidPassword(invalidPassword)).thenReturn(false);
		PowerMockito.when(ParameterUtils.encryptPassword(Mockito.anyString(), Mockito.anyString())).thenCallRealMethod();
		PowerMockito.when(ParameterUtils.validateCaptchaStatus(UserConstants.CAPTCHA_CHECKTYPE_RESET_PASSWORD, mobile)).thenReturn(UserConstants.CAPTCHA_VALIDATION_RESULT_SUCCESS);
		PowerMockito.when(ParameterUtils.isValidEmail(Mockito.anyString())).thenCallRealMethod();
		PowerMockito.when(ParameterUtils.isValidPostcode(Mockito.anyString())).thenCallRealMethod();
		PowerMockito.when(ParameterUtils.isValidQQ(Mockito.anyString())).thenCallRealMethod();
		PowerMockito.when(ParameterUtils.isValidSex(Mockito.anyInt())).thenCallRealMethod();
		
		//未注册时，可用
		try 
		{
	        response = userComponent.checkParameter(appId, mobile);
	        assertNotNull(response);
	        assertTrue(response.getMobile());
        }
        catch (BusinessException e) 
		{
        	fail("");
        }
		
		//正确注册
		try
		{
			cResponse = userComponent.createUser(appId, mobile, password, getUserInfo(), getExtendInfo(), false);
			assertNotNull(cResponse);
			assertEquals(mobile, cResponse.getUserCode());
			assertEquals(appId, cResponse.getAppId());
			assertNotEquals(0, cResponse.getUid());
			testUsers.add(cResponse.getUid());
		}
		catch (Exception e)
		{
			fail("");
		}
		
		//注册后，不可用
		try 
		{
	        response = userComponent.checkParameter(appId, mobile);
	        assertNotNull(response);
	        assertFalse(response.getMobile());
        }
        catch (BusinessException e) 
		{
        	fail("");
        }
		
		//appid不合法
		try
		{
			mobile = getMobile();
			PowerMockito.when(ParameterUtils.isValidMobile(mobile)).thenReturn(true);
			response = userComponent.checkParameter(invalidAppid, mobile);
			fail("fail");
		}
		catch (BusinessException e)
		{
			assertEquals(BUSINESS_CODE.APP_ID_INVALID, e.getErrCode());
		}
		
		//手机号不合法
		try
		{
			response = userComponent.checkParameter(appId, invalidMobile);
			fail("fail");
		}
		catch (BusinessException e)
		{
			assertEquals(BUSINESS_CODE.MOBILE_INVALID, e.getErrCode());
		}
	}

	@Test
	public void testCreateUser()
	{
		CreateUserResponse response = null;
		GetUserInfoResponse getResponse = null;
		String mobile = null;
		
		mobile = getMobile();
		//预设mock
		PowerMockito.mockStatic(ParameterUtils.class);
		PowerMockito.when(ParameterUtils.isValidMobile(mobile)).thenReturn(true);
		PowerMockito.when(ParameterUtils.isValidAppId(appId)).thenReturn(true);
		PowerMockito.when(ParameterUtils.isValidPassword(password)).thenReturn(true);
		PowerMockito.when(ParameterUtils.isValidPassword(invalidPassword)).thenReturn(false);
		PowerMockito.when(ParameterUtils.encryptPassword(Mockito.anyString(), Mockito.anyString())).thenCallRealMethod();
		PowerMockito.when(ParameterUtils.validateCaptchaStatus(UserConstants.CAPTCHA_CHECKTYPE_RESET_PASSWORD, mobile)).thenReturn(UserConstants.CAPTCHA_VALIDATION_RESULT_SUCCESS);
		PowerMockito.when(ParameterUtils.isValidEmail(Mockito.anyString())).thenCallRealMethod();
		PowerMockito.when(ParameterUtils.isValidPostcode(Mockito.anyString())).thenCallRealMethod();
		PowerMockito.when(ParameterUtils.isValidQQ(Mockito.anyString())).thenCallRealMethod();
		PowerMockito.when(ParameterUtils.isValidSex(Mockito.anyInt())).thenCallRealMethod();
		
		//正确注册
		try
		{
			response = userComponent.createUser(appId, mobile, password, getUserInfo(), getExtendInfo(), false);
			assertNotNull(response);
			assertEquals(mobile, response.getUserCode());
			assertEquals(appId, response.getAppId());
			assertNotEquals(0, response.getUid());
			testUsers.add(response.getUid());
			getResponse = userComponent.getUserInfo(appId, mobile, response.getUid());
			
			//以手机号注册后，用户基本信息自动增加手机号字段，并且标记为手机已绑定
			assertEquals(mobile, getResponse.getUserInfo().getMobile());
			assertEquals(UserConstants.MOBILE_IS_VERIFIED, getResponse.getUserInfo().getMobileIsVerify());
		}
		catch (Exception e)
		{
			fail("");
		}
		
		//扩展信息和基本信息为空，也能正确注册
		try
		{
			mobile = getMobile();
			PowerMockito.when(ParameterUtils.isValidMobile(mobile)).thenReturn(true);
			response = userComponent.createUser(appId, mobile, password, "", "", false);
			testUsers.add(response.getUid());
		}
		catch (BusinessException e)
		{
			fail("fail");
		}
		
		//手机号被占用
		try
		{
			response = userComponent.createUser(appId, mobile, password, getUserInfo(), getExtendInfo(), false);
			fail("fail");
		}
		catch (BusinessException e)
		{
			assertEquals(BUSINESS_CODE.CREATE_USER_USER_EXISTS, e.getErrCode());
		}
		
		//appid不合法
		try
		{
			mobile = getMobile();
			PowerMockito.when(ParameterUtils.isValidMobile(mobile)).thenReturn(true);
			response = userComponent.createUser(invalidAppid, mobile, password, getUserInfo(), getExtendInfo(), false);
			fail("fail");
		}
		catch (BusinessException e)
		{
			assertEquals(BUSINESS_CODE.APP_ID_INVALID, e.getErrCode());
		}
		
		//userinfo传入的手机号不合法
		try
		{
			response = userComponent.createUser(appId, mobile, password, "{\"mobile\":\"" + invalidMobile + "\"}", getExtendInfo(), false);
			fail("fail");
		}
		catch (BusinessException e)
		{
			assertEquals(BUSINESS_CODE.CREATE_USER_MOBILE_INVALID, e.getErrCode());
		}
		
		//userinfo性别不合法
		try
		{
			response = userComponent.createUser(appId, mobile, password, "{\"sex\":\"" + 3 + "\"}", getExtendInfo(), false);
			fail("fail");
		}
		catch (BusinessException e)
		{
			assertEquals(BUSINESS_CODE.CREATE_USER_SEX_INVALID, e.getErrCode());
		}
		
		//userinfo邮编不合法
		try
		{
			response = userComponent.createUser(appId, mobile, password, "{\"postcode\":\"" + "33423" + "\"}", getExtendInfo(), false);
			fail("fail");
		}
		catch (BusinessException e)
		{
			assertEquals(BUSINESS_CODE.CREATE_USER_POSTCODE_INVALID, e.getErrCode());
		}
		
		//userinfo qq号不合法
		try
		{
			response = userComponent.createUser(appId, mobile, password, "{\"qq\":\"" + "352939d3" + "\"}", getExtendInfo(), false);
			fail("fail");
		}
		catch (BusinessException e)
		{
			assertEquals(BUSINESS_CODE.CREATE_USER_QQ_INVALID, e.getErrCode());
		}
		
		//userinfo邮箱不合法
		try
		{
			response = userComponent.createUser(appId, mobile, password, "{\"email\":\"" + "134234@dfs.c!m" + "\"}", getExtendInfo(), false);
			fail("fail");
		}
		catch (BusinessException e)
		{
			assertEquals(BUSINESS_CODE.CREATE_USER_EMAIL_INVALID, e.getErrCode());
		}
		
		//手机号不合法
		try
		{
			response = userComponent.createUser(appId, invalidMobile, password, getUserInfo(), getExtendInfo(), false);
			fail("fail");
		}
		catch (BusinessException e)
		{
			assertEquals(BUSINESS_CODE.ACCOUNT_INVALID, e.getErrCode());
		}
		
		//用户密码不合法
		try
		{
			mobile = getMobile();
			PowerMockito.when(ParameterUtils.isValidMobile(mobile)).thenReturn(true);
			response = userComponent.createUser(appId, mobile, invalidPassword, getUserInfo(), getExtendInfo(), false);
			fail("fail");
		}
		catch (BusinessException e)
		{
			assertEquals(BUSINESS_CODE.CREATE_USER_PASSWORD_INVALID, e.getErrCode());
		}
		
		//用户密码加密失败
		PowerMockito.when(ParameterUtils.encryptPassword(Mockito.anyString(), Mockito.anyString())).thenReturn(null);
		try
		{
			mobile = getMobile();
			PowerMockito.when(ParameterUtils.isValidMobile(mobile)).thenReturn(true);
			response = userComponent.createUser(appId, mobile, password, getUserInfo(), getExtendInfo(), false);
			fail("fail");
		}
		catch (BusinessException e)
		{
			assertEquals(BUSINESS_CODE.CREATE_USER_PASSWORD_INVALID, e.getErrCode());
		}
		PowerMockito.when(ParameterUtils.encryptPassword(Mockito.anyString(), Mockito.anyString())).thenCallRealMethod();
		
		//用户基本信息格式不合法
		try
		{
			mobile = getMobile();
			PowerMockito.when(ParameterUtils.isValidMobile(mobile)).thenReturn(true);
			response = userComponent.createUser(appId, mobile, password, "{\"userName\":\"haha}", getExtendInfo(), false);
			fail("fail");
		}
		catch (BusinessException e)
		{
			assertEquals(BUSINESS_CODE.CREATE_USER_USERINFO_INVALID, e.getErrCode());
		}
		
		//用户扩展信息格式不合法
		try
		{
			mobile = getMobile();
			PowerMockito.when(ParameterUtils.isValidMobile(mobile)).thenReturn(true);
			response = userComponent.createUser(appId, mobile, password, getUserInfo(), "{\"a\":\"av}", false);
		}
		catch (BusinessException e)
		{
			assertEquals(BUSINESS_CODE.CREATE_USER_EXTENDINFO_INVALID, e.getErrCode());
		}
		
		//验证码验证失败
		try
		{
			mobile = getMobile();
			PowerMockito.when(ParameterUtils.isValidMobile(mobile)).thenReturn(true);
			PowerMockito.when(ParameterUtils.validateCaptchaStatus(UserConstants.CAPTCHA_CHECKTYPE_REGISTER, mobile)).thenReturn(UserConstants.CAPTCHA_VALIDATION_RESULT_UNVERIFIED);
			userComponent.createUser(appId, mobile, password, getUserInfo(), getExtendInfo(), false);
		}
		catch (BusinessException e)
		{
			assertEquals(BUSINESS_CODE.CAPTCHA_VALIDATION_UNVERIFIED, e.getErrCode());
		}
		
		//验证码过期
		try
		{
			mobile = getMobile();
			PowerMockito.when(ParameterUtils.isValidMobile(mobile)).thenReturn(true);
			PowerMockito.when(ParameterUtils.validateCaptchaStatus(UserConstants.CAPTCHA_CHECKTYPE_REGISTER, mobile)).thenReturn(UserConstants.CAPTCHA_VALIDATION_RESULT_EXPIRE);
			userComponent.createUser(appId, mobile, password, getUserInfo(), getExtendInfo(), false);
		}
		catch (BusinessException e)
		{
			assertEquals(BUSINESS_CODE.CAPTCHA_VALIDATION_EXPIRE, e.getErrCode());
		}
		
		//插入用户信息失败
		try
		{
			mobile = getMobile();
			PowerMockito.when(ParameterUtils.isValidMobile(mobile)).thenReturn(true);
			PowerMockito.when(UserDAO.insertUser(Mockito.any(User.class))).thenReturn(0);
			response = userComponent.createUser(appId, mobile, password, "", "", false);
			fail("fail");
		}
		catch (BusinessException e)
		{
			assertEquals(BUSINESS_CODE.INTERNAL_SERVER_ERROR, e.getErrCode());
		}
		PowerMockito.when(UserDAO.insertUser(Mockito.any(User.class))).thenCallRealMethod();
		
		//插入用户基本信息失败，注册失败
		try
		{
			mobile = getMobile();
			PowerMockito.when(ParameterUtils.isValidMobile(mobile)).thenReturn(true);
			PowerMockito.when(UserInfoDAO.insert(Mockito.any(UserInfo.class))).thenReturn(0);
			response = userComponent.createUser(appId, mobile, password, getUserInfo(), getExtendInfo(), false);
			//testUsers.add(response.getUid());
		}
		catch (BusinessException e)
		{
			assertEquals(BUSINESS_CODE.INTERNAL_SERVER_ERROR, e.getErrCode());
			//User u = UserDAO.findWithAccount(mobile);
			//assertNull(UserDAO.findWithAccount(mobile));
		}
		PowerMockito.when(UserInfoDAO.insert(Mockito.any(UserInfo.class))).thenCallRealMethod();
		
		//插入用户扩展信息失败，注册失败
		try
		{
			mobile = getMobile();
			PowerMockito.when(ParameterUtils.isValidMobile(mobile)).thenReturn(true);
			PowerMockito.when(UserExtendInfoDAO.insertList(Mockito.anyList())).thenReturn(0);
			response = userComponent.createUser(appId, mobile, password, getUserInfo(), getExtendInfo(), false);
			//testUsers.add(response.getUid());
		}
		catch (BusinessException e)
		{
			assertEquals(BUSINESS_CODE.INTERNAL_SERVER_ERROR, e.getErrCode());
			//assertNull(UserDAO.findWithAccount(mobile));
		}
		PowerMockito.when(UserExtendInfoDAO.insertList(Mockito.anyList())).thenCallRealMethod();
	}
	
	@Test
	public void testRollbackUser()
	{
		CreateUserResponse createResponse = null;
		GetUserInfoResponse getResponse = null;
		String mobile = null;
		long uid = 0;
		
		mobile = getMobile();
		//预设mock
		PowerMockito.mockStatic(ParameterUtils.class);
		PowerMockito.when(ParameterUtils.isValidMobile(mobile)).thenReturn(true);
		PowerMockito.when(ParameterUtils.isValidAppId(appId)).thenReturn(true);
		PowerMockito.when(ParameterUtils.isValidPassword(password)).thenReturn(true);
		PowerMockito.when(ParameterUtils.isValidPassword(invalidPassword)).thenReturn(false);
		PowerMockito.when(ParameterUtils.encryptPassword(Mockito.anyString(), Mockito.anyString())).thenCallRealMethod();
		PowerMockito.when(ParameterUtils.validateCaptchaStatus(UserConstants.CAPTCHA_CHECKTYPE_RESET_PASSWORD, mobile)).thenReturn(UserConstants.CAPTCHA_VALIDATION_RESULT_SUCCESS);
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
		
		//回滚成功
		try
		{
			assertTrue(userComponent.rollbackUser(appId, mobile, password));
		}
		catch (BusinessException e)
		{
			fail("");
		}
		try
		{
			getResponse = userComponent.getUserInfo(appId, accessToken, uid);
		}
		catch (BusinessException e)
		{
			assertEquals(BUSINESS_CODE.USER_INVALID, e.getErrCode());
		}
	}
	
	@Test
	public void testGetUserInfo()
	{
		CreateUserResponse createResponse = null;
		GetUserInfoResponse response = null;
		String mobile = null;
		long uid = 0;
		
		UserInfo ui = null;
		UserInfoResponse gUi = null;
		Map<String, String> extendMap = null;
		Map<String, String> gExtendMap = null;
		
		mobile = getMobile();
		//预设mock
		PowerMockito.mockStatic(ParameterUtils.class);
		PowerMockito.when(ParameterUtils.isValidMobile(mobile)).thenReturn(true);
		PowerMockito.when(ParameterUtils.isValidMobile(noUserAccount)).thenReturn(true);
		PowerMockito.when(ParameterUtils.isValidAppId(appId)).thenReturn(true);
		PowerMockito.when(ParameterUtils.isValidPassword(password)).thenReturn(true);
		PowerMockito.when(ParameterUtils.encryptPassword(Mockito.anyString(), Mockito.anyString())).thenCallRealMethod();
		PowerMockito.when(ParameterUtils.validateCaptchaStatus(UserConstants.CAPTCHA_CHECKTYPE_RESET_PASSWORD, mobile)).thenReturn(UserConstants.CAPTCHA_VALIDATION_RESULT_SUCCESS);
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
		
		//正确查询
		try
		{
			response = userComponent.getUserInfo(appId, accessToken, uid);
			assertNotNull(response);
			assertNotNull(response.getExtendInfo());
			assertNotNull(response.getUserInfo());
			assertNotEquals(0, response.getUid());
			ui = gson.fromJson(getUserInfo(), UserInfo.class);
			//gUi = gson.fromJson(response.getUserInfo(), UserInfo.class);
			gUi = response.getUserInfo(); 
			extendMap = gson.fromJson(getExtendInfo(), new TypeToken<Map<String, String>>(){}.getType());
			//gExtendMap = gson.fromJson(response.getExtendInfo(),  new TypeToken<Map<String, String>>(){}.getType());
			gExtendMap = response.getExtendInfo();
			
			assertEquals(ui.getUserName(), gUi.getUserName());
			assertEquals(ui.getQq(), gUi.getQq());
			
			Set<Entry<String, String>> entries = extendMap.entrySet();
			for (Entry<String, String> entry : entries) 
			{
	            assertEquals(entry.getValue(), gExtendMap.get(entry.getKey()));
            }
			
			response = userComponent.getUserInfo(appId, accessToken, mobile);
			assertNotNull(response);
			assertNotNull(response.getExtendInfo());
			assertNotNull(response.getUserInfo());
			assertNotEquals(0, response.getUid());
			ui = gson.fromJson(getUserInfo(), UserInfo.class);
			//gUi = gson.fromJson(response.getUserInfo(), UserInfo.class);
			gUi = response.getUserInfo(); 
			extendMap = gson.fromJson(getExtendInfo(), new TypeToken<Map<String, String>>(){}.getType());
			//gExtendMap = gson.fromJson(response.getExtendInfo(),  new TypeToken<Map<String, String>>(){}.getType());
			gExtendMap = response.getExtendInfo();
			
			assertEquals(ui.getUserName(), gUi.getUserName());
			assertEquals(ui.getQq(), gUi.getQq());
			
			entries = extendMap.entrySet();
			for (Entry<String, String> entry : entries) 
			{
	            assertEquals(entry.getValue(), gExtendMap.get(entry.getKey()));
            }
		}
		catch (Exception e)
		{
			fail("");
		}
		
		//appId不合法
		try
		{
			response = null;
			response = userComponent.getUserInfo(invalidAppid, accessToken, uid);
		}
		catch (BusinessException e)
		{
			assertEquals(BUSINESS_CODE.APP_ID_INVALID, e.getErrCode());
		}
		//appId不合法
		try
		{
			response = null;
			response = userComponent.getUserInfo(invalidAppid, accessToken, mobile);
		}
		catch (BusinessException e)
		{
			assertEquals(BUSINESS_CODE.APP_ID_INVALID, e.getErrCode());
		}
		
		//accessToken不合法
		try
		{
			response = null;
			response = userComponent.getUserInfo(appId, invalidToken, uid);
		}
		catch (BusinessException e)
		{
			assertEquals(BUSINESS_CODE.ACCESS_TOKEN_INVALID, e.getErrCode());
		}
		//accessToken不合法
		try
		{
			response = null;
			response = userComponent.getUserInfo(appId, invalidToken, mobile);
		}
		catch (BusinessException e)
		{
			assertEquals(BUSINESS_CODE.ACCESS_TOKEN_INVALID, e.getErrCode());
		}
		
		//accessToken过期
		try
		{
			response = null;
			response = userComponent.getUserInfo(appId, expireToken, uid);
		}
		catch (BusinessException e)
		{
			assertEquals(BUSINESS_CODE.ACCESS_TOKEN_EXPIRE, e.getErrCode());
		}
		//accessToken过期
		try
		{
			response = null;
			response = userComponent.getUserInfo(appId, expireToken, mobile);
		}
		catch (BusinessException e)
		{
			assertEquals(BUSINESS_CODE.ACCESS_TOKEN_EXPIRE, e.getErrCode());
		}
		
		//uid不合法
		try
		{
			response = null;
			response = userComponent.getUserInfo(appId, accessToken, zeroUid);
		}
		catch (BusinessException e)
		{
			assertEquals(BUSINESS_CODE.UID_INVALID, e.getErrCode());
		}
		//account不合法
		try
		{
			response = null;
			response = userComponent.getUserInfo(appId, accessToken, "");
		}
		catch (BusinessException e)
		{
			assertEquals(BUSINESS_CODE.ACCOUNT_INVALID, e.getErrCode());
		}
		
		//用户不存在
		try
		{
			response = null;
			response = userComponent.getUserInfo(appId, accessToken, invalidUid);
		}
		catch (BusinessException e)
		{
			assertEquals(BUSINESS_CODE.USER_INVALID, e.getErrCode());
		}
		//用户不存在
		try
		{
			response = null;
			response = userComponent.getUserInfo(appId, accessToken, noUserAccount);
		}
		catch (BusinessException e)
		{
			assertEquals(BUSINESS_CODE.USER_INVALID, e.getErrCode());
		}
		
		//获取扩展信息list失败
		try
		{
			response = null;
			PowerMockito.when(UserExtendInfoDAO.getJsonFromExtendInfoList(Mockito.anyListOf(UserExtendInfo.class))).thenReturn(null);
			response = userComponent.getUserInfo(appId, accessToken, uid);
		}
		catch (BusinessException e)
		{
			assertEquals(BUSINESS_CODE.INTERNAL_SERVER_ERROR, e.getErrCode());
		}
		try
		{
			response = null;
			PowerMockito.when(UserExtendInfoDAO.getJsonFromExtendInfoList(Mockito.anyListOf(UserExtendInfo.class))).thenReturn("");
			response = userComponent.getUserInfo(appId, accessToken, uid);
		}
		catch (BusinessException e)
		{
			assertEquals(BUSINESS_CODE.INTERNAL_SERVER_ERROR, e.getErrCode());
		}
		PowerMockito.when(UserExtendInfoDAO.getJsonFromExtendInfoList(Mockito.anyListOf(UserExtendInfo.class))).thenCallRealMethod();
	}
	
	@Test
	public void testUpdateUserInfo()
	{
		CreateUserResponse createResponse = null;
		GetUserInfoResponse getResponse = null;
		String mobile = null;
		long uid = 0;
		
		UserInfo ui = null;
		UserInfoResponse gUi = null;
		Map<String, Object> extendMap = null;
		Map<String, String> gExtendMap = null;
		
		String newUsername = "newname";
		String newPostcode = "382843";
		String newBV = "newbv";
		String newF = "f";
		String newFV = "fv";
		
		mobile = getMobile();
		//预设mock
		PowerMockito.mockStatic(ParameterUtils.class);
		PowerMockito.when(ParameterUtils.isValidMobile(mobile)).thenReturn(true);
		PowerMockito.when(ParameterUtils.isValidAppId(appId)).thenReturn(true);
		PowerMockito.when(ParameterUtils.isValidPassword(password)).thenReturn(true);
		PowerMockito.when(ParameterUtils.encryptPassword(Mockito.anyString(), Mockito.anyString())).thenCallRealMethod();
		PowerMockito.when(ParameterUtils.validateCaptchaStatus(UserConstants.CAPTCHA_CHECKTYPE_RESET_PASSWORD, mobile)).thenReturn(UserConstants.CAPTCHA_VALIDATION_RESULT_SUCCESS);
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
		
		//正确更新
		try
		{
 			ui = gson.fromJson(getUserInfo(), UserInfo.class);
			extendMap = gson.fromJson(getExtendInfo(), new TypeToken<Map<String, String>>(){}.getType());
			ui.setUserName(newUsername);
			ui.setPostcode(newPostcode);
			ui.setUid(uid);
			extendMap.put(newF, newFV);
			extendMap.put("b", newBV);
			assertTrue(userComponent.updateUserInfo(appId, accessToken, uid, gson.toJson(ui), gson.toJson(extendMap)));
			//更新完成, 重新获取
			getResponse = userComponent.getUserInfo(appId, accessToken, uid);
			assertNotNull(getResponse);
			//gUi = gson.fromJson(getResponse.getUserInfo(), UserInfo.class);
			gUi = getResponse.getUserInfo();
			//gExtendMap = gson.fromJson(getResponse.getExtendInfo(),  new TypeToken<Map<String, String>>(){}.getType());
			gExtendMap = getResponse.getExtendInfo();
			
			assertEquals(ui.getUserName(), gUi.getUserName());
			assertEquals(ui.getQq(), gUi.getQq());
			assertEquals(ui.getPostcode(), gUi.getPostcode());
			
			Set<Entry<String, Object>> entries = extendMap.entrySet();
			for (Entry<String, Object> entry : entries)
			{
	            assertEquals(entry.getValue(), gExtendMap.get(entry.getKey()));
            }
		}
		catch (Exception e)
		{
			fail("");
		}
		
		//手机号已绑定，强行更新mobileIsVerify和mobile无效
		try
		{
			mobile = getMobile();
			PowerMockito.when(ParameterUtils.isValidMobile(mobile)).thenReturn(true);
			assertTrue(userComponent.updateUserInfo(appId, accessToken, uid, "{\"mobile\":\"" + mobile + "\", \"mobileIsVerify\":0}", ""));
			getResponse = userComponent.getUserInfo(appId, accessToken, uid);
			assertNotEquals(mobile, getResponse.getUserInfo().getMobile());
			assertNotEquals(0, getResponse.getUserInfo().getMobileIsVerify());
		}
		catch (Exception e)
		{
			fail("");
		}
		
		//userinfo 手机号不合法
		try
		{
			userComponent.updateUserInfo(appId, accessToken, uid, "{\"mobile\":\"" + invalidMobile + "\"}", gson.toJson(extendMap));
		}
		catch (BusinessException e)
		{
			assertEquals(BUSINESS_CODE.UPDATE_USERINFO_MOBILE_INVALID, e.getErrCode());
		}
		
		//userinfo 性别不合法
		try
		{
			userComponent.updateUserInfo(appId, accessToken, uid, "{\"sex\":\"" + 3 + "\"}", gson.toJson(extendMap));
		}
		catch (BusinessException e)
		{
			assertEquals(BUSINESS_CODE.UPDATE_USERINFO_SEX_INVALID, e.getErrCode());
		}
		
		//userinfo qq不合法
		try
		{
			userComponent.updateUserInfo(appId, accessToken, uid, "{\"qq\":\"" + "2341242d4" + "\"}", gson.toJson(extendMap));
		}
		catch (BusinessException e)
		{
			assertEquals(BUSINESS_CODE.UPDATE_USERINFO_QQ_INVALID, e.getErrCode());
		}
		
		//userinfo email不合法
		try
		{
			userComponent.updateUserInfo(appId, accessToken, uid, "{\"email\":\"" + "4923424@jfds.c8m" + "\"}", gson.toJson(extendMap));
		}
		catch (BusinessException e)
		{
			assertEquals(BUSINESS_CODE.UPDATE_USERINFO_EMAIL_INVALID, e.getErrCode());
		}
		
		//userinfo 邮编不合法
		try
		{
			userComponent.updateUserInfo(appId, accessToken, uid, "{\"postcode\":\"" + "423423423" + "\"}", gson.toJson(extendMap));
		}
		catch (BusinessException e)
		{
			assertEquals(BUSINESS_CODE.UPDATE_USERINFO_POSTCODE_INVALID, e.getErrCode());
		}
		
		//appId不合法
		try
		{
			userComponent.updateUserInfo(invalidAppid, accessToken, uid, gson.toJson(ui), gson.toJson(extendMap));
		}
		catch (BusinessException e)
		{
			assertEquals(BUSINESS_CODE.APP_ID_INVALID, e.getErrCode());
		}
		
		//accessToken不合法
		try
		{
			userComponent.updateUserInfo(appId, invalidToken, uid, gson.toJson(ui), gson.toJson(extendMap));
		}
		catch (BusinessException e)
		{
			assertEquals(BUSINESS_CODE.ACCESS_TOKEN_INVALID, e.getErrCode());
		}
		
		//accessToken过期
		try
		{
			userComponent.updateUserInfo(appId, expireToken, uid, gson.toJson(ui), gson.toJson(extendMap));
		}
		catch (BusinessException e)
		{
			assertEquals(BUSINESS_CODE.ACCESS_TOKEN_EXPIRE, e.getErrCode());
		}
		
		//uid不合法,即uid为0
		try
		{
			userComponent.updateUserInfo(appId, accessToken, zeroUid, gson.toJson(ui), gson.toJson(extendMap));
		}
		catch (BusinessException e)
		{
			assertEquals(BUSINESS_CODE.UID_INVALID, e.getErrCode());
		}
		
		//用户不存在
		try
		{
			userComponent.updateUserInfo(appId, accessToken, invalidUid, gson.toJson(ui), gson.toJson(extendMap));
		}
		catch (BusinessException e)
		{
			assertEquals(BUSINESS_CODE.USER_INVALID, e.getErrCode());
		}
		
		//userInfo格式不合法
		try
		{
			userComponent.updateUserInfo(appId, accessToken, uid, "\"userName\":\"false", gson.toJson(extendMap));
		}
		catch (BusinessException e)
		{
			assertEquals(BUSINESS_CODE.UPDATE_USERINFO_BASEINFO_INVALID, e.getErrCode());
		}
		
		//extendInfo格式不合法
		try
		{
			userComponent.updateUserInfo(appId, accessToken, uid, gson.toJson(ui), "\"a\":\"av");
		}
		catch (BusinessException e)
		{
			assertEquals(BUSINESS_CODE.UPDATE_USERINFO_EXTENDINFO_INVALID, e.getErrCode());
		}
		
//		//更新userInfo失败
//		try
//		{
//			PowerMockito.when(UserInfoDAO.update(Mockito.anyLong(), Mockito.anyMap())).thenReturn(0);
//			assertFalse(userComponent.updateUserInfo(appId, accessToken, uid, gson.toJson(ui), gson.toJson(extendMap)));
//		}
//		catch (BusinessException e)
//		{
//			fail("");
//		}
//		PowerMockito.when(UserInfoDAO.update(Mockito.anyLong(), Mockito.anyMap())).thenCallRealMethod();
		
		//更新userExtendInfo失败
		try
		{
			PowerMockito.when(UserExtendInfoDAO.updateWithUid(Mockito.anyLong(), Mockito.anyList())).thenReturn(0);
			assertFalse(userComponent.updateUserInfo(appId, accessToken, uid, gson.toJson(ui), gson.toJson(extendMap)));
		}
		catch (BusinessException e)
		{
			fail("");
		}
		PowerMockito.when(UserExtendInfoDAO.updateWithUid(Mockito.anyLong(), Mockito.anyList())).thenCallRealMethod();
	}
	
	@Test
	public void testGetUidWithUsercode()
	{
		CreateUserResponse createResponse = null;
		long responseUid = 0;
		String mobile = null;
		String invalidMobile = "134";
		String mobile2 = null;
		long uid = 0;
		
		mobile = getMobile();
		//预设mock
		PowerMockito.mockStatic(ParameterUtils.class);
		PowerMockito.when(ParameterUtils.isValidMobile(Mockito.anyString())).thenCallRealMethod();
		PowerMockito.when(ParameterUtils.isValidAppId(appId)).thenReturn(true);
		PowerMockito.when(ParameterUtils.isValidPassword(password)).thenReturn(true);
		PowerMockito.when(ParameterUtils.encryptPassword(Mockito.anyString(), Mockito.anyString())).thenCallRealMethod();
		PowerMockito.when(ParameterUtils.validateCaptchaStatus(UserConstants.CAPTCHA_CHECKTYPE_RESET_PASSWORD, mobile)).thenReturn(UserConstants.CAPTCHA_VALIDATION_RESULT_SUCCESS);
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

		//手机号码不合法
		try
		{
			responseUid = userComponent.getUidWithUserCode(invalidMobile);
			fail("");
		}
		catch (BusinessException e)
		{
			assertEquals(BUSINESS_CODE.ACCOUNT_INVALID, e.getErrCode());
		}
		
		//账号不存在
		mobile2 = getMobile();
		mobile2 = getMobile();
		mobile2 = getMobile();
		try
		{
			responseUid = userComponent.getUidWithUserCode(mobile2);
			fail("");
		}
		catch (BusinessException e)
		{
			assertEquals(BUSINESS_CODE.USER_INVALID, e.getErrCode());
		}
		
		//查询成功
		try
		{
			responseUid = userComponent.getUidWithUserCode(mobile);
			assertEquals(uid, responseUid);
		}
		catch (BusinessException e)
		{
			fail("");
		}
	}
	
	private void clean()
	{
		UserExtendInfoCache uic = (UserExtendInfoCache) CacheService.GetService(UserExtendInfoCache.class);
		uic.clear();
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
		userInfo.setEmail("");
		userInfo.setPostcode("522655");
		userInfo.setQq("456333225");
		userInfo.setSex(1);
		userInfo.setUserName("测试用户名");
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
