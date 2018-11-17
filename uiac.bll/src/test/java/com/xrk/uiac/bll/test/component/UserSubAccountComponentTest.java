package com.xrk.uiac.bll.test.component;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.google.gson.Gson;
import com.xrk.hws.common.logger.Logger;
import com.xrk.uiac.bll.cache.CacheService;
import com.xrk.uiac.bll.common.ParameterUtils;
import com.xrk.uiac.bll.common.UserConstants;
import com.xrk.uiac.bll.component.impl.UserComponent;
import com.xrk.uiac.bll.component.impl.UserSubAccountComponent;
import com.xrk.uiac.bll.exception.BUSINESS_CODE;
import com.xrk.uiac.bll.exception.BusinessException;
import com.xrk.uiac.bll.exception.VerifyException;
import com.xrk.uiac.bll.response.CreateUserResponse;
import com.xrk.uiac.bll.response.GetSubAccountInfoResponse;
import com.xrk.uiac.dal.Dal;
import com.xrk.uiac.dal.DalTestHelper;
import com.xrk.uiac.dal.dao.UserDAO;
import com.xrk.uiac.dal.dao.UserExtendInfoDAO;
import com.xrk.uiac.dal.dao.UserInfoDAO;
import com.xrk.uiac.dal.dao.UserSubAccountDAO;
import com.xrk.uiac.dal.entity.UserInfo;
import com.xrk.uiac.dal.entity.UserSubAccount;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore( {"javax.management.*"}) 
@PrepareForTest({ParameterUtils.class, UserSubAccountDAO.class, Logger.class})
public class UserSubAccountComponentTest
{
	private UserComponent userComponent = new UserComponent();
	private UserSubAccountComponent subAccountComponent = new UserSubAccountComponent();
	private Set<Long> testUsers = new HashSet<Long>();
	private Gson gson = new Gson();
	private long index = 1;
	private String password = "password";
	private int invalidAppid = -1;
	private int appId = 1001;
	private String accessToken = "access";
	private String expireToken = "expire";
	private String invalidToken = "invalid";
	private long invalidUid = Long.MAX_VALUE;
	private long zeroUid = 0;
	
	private int subAppId1 = 1002;
	private String subAccount1 = "subAccount1";
	private int subAppId2 = 1003;
	private String subAccount2 = "subAccount2";
	
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
		CacheService.cleanAll();
	}

	@Before
	public void setUp() throws Exception
	{
		PowerMockito.mockStatic(Logger.class);
		//对UserSubAccountDAO进行mock
		PowerMockito.mockStatic(UserSubAccountDAO.class);
		PowerMockito.when(UserSubAccountDAO.deleteWithUid(Mockito.anyLong())).thenCallRealMethod();
		PowerMockito.when(UserSubAccountDAO.deleteWithUidAppid(Mockito.anyLong(), Mockito.anyInt())).thenCallRealMethod();
		PowerMockito.when(UserSubAccountDAO.deleteWithUidSubAccount(Mockito.anyString(), Mockito.anyInt(), Mockito.anyInt())).thenCallRealMethod();		
		PowerMockito.when(UserSubAccountDAO.findListWithUid(Mockito.anyLong())).thenCallRealMethod();
		PowerMockito.when(UserSubAccountDAO.findWithUidAppid(Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt())).thenCallRealMethod();
		PowerMockito.when(UserSubAccountDAO.findListWithUidAndAppId(Mockito.anyLong(), Mockito.anyInt())).thenCallRealMethod();
		PowerMockito.when(UserSubAccountDAO.findListWithUidAndBindAppId(Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt())).thenCallRealMethod();
		PowerMockito.when(UserSubAccountDAO.findWithSubAccountAppID(Mockito.anyString(), Mockito.anyInt(), Mockito.anyInt())).thenCallRealMethod();
		PowerMockito.when(UserSubAccountDAO.findListWithUid(Mockito.anyLong())).thenCallRealMethod();
				
		PowerMockito.when(UserSubAccountDAO.insertSubAccount(Mockito.any(UserSubAccount.class))).thenCallRealMethod();
	}

	@After
	public void tearDown() throws Exception
	{
		clean();
	}

	@Rule
    public ExpectedException thrown = ExpectedException.none();
	
	@Test
	public void testBindSubAccount() throws BusinessException
	{
		System.out.println("Test UserSubAccountComponentTest.testBindSubAccount");
		CreateUserResponse createResponse = null;
		String mobile = null;
		long uid = 0;
		
		mobile = getMobile();
		//预设mock
		PowerMockito.mockStatic(ParameterUtils.class);
		PowerMockito.when(ParameterUtils.isValidMobile(mobile)).thenReturn(true);
		PowerMockito.when(ParameterUtils.isValidAppId(appId)).thenReturn(true);
		PowerMockito.when(ParameterUtils.isValidAppId(subAppId1)).thenReturn(true);
		PowerMockito.when(ParameterUtils.isValidAppId(subAppId2)).thenReturn(true);
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
		
		//正确绑定子账号
		try
		{
			//数据库操作失败
			PowerMockito.when(UserSubAccountDAO.insertSubAccount(Mockito.any(UserSubAccount.class))).thenReturn(0);
			assertFalse(subAccountComponent.bindSubAccount(appId, accessToken, uid, subAccount1, subAppId1));
			PowerMockito.when(UserSubAccountDAO.insertSubAccount(Mockito.any(UserSubAccount.class))).thenCallRealMethod();
			
			assertTrue(subAccountComponent.bindSubAccount(appId, accessToken, uid, subAccount1, subAppId1));
			UserSubAccount usa = UserSubAccountDAO.findWithUidAppid(uid, appId, subAppId1);
			assertEquals(appId, usa.getAppId());
			assertEquals(subAppId1, usa.getBindAppId());
			assertEquals(uid, usa.getUid());
			assertEquals(subAccount1, usa.getAccount());
			
			assertTrue(subAccountComponent.bindSubAccount(appId, accessToken, uid, subAccount1, subAppId2));
			usa = UserSubAccountDAO.findWithUidAppid(uid, appId, subAppId2);
			assertEquals(appId, usa.getAppId());
			assertEquals(subAppId2, usa.getBindAppId());
			assertEquals(uid, usa.getUid());
			assertEquals(subAccount1, usa.getAccount());
			
			assertTrue(subAccountComponent.bindSubAccount(appId, accessToken, uid, subAccount2, subAppId1));
			usa = UserSubAccountDAO.findWithSubAccountAppID(subAccount2, appId, subAppId1);
			assertEquals(appId, usa.getAppId());
			assertEquals(subAppId1, usa.getBindAppId());
			assertEquals(uid, usa.getUid());
			assertEquals(subAccount2, usa.getAccount());
			
			assertTrue(subAccountComponent.bindSubAccount(appId, accessToken, uid, subAccount2, subAppId2));
			usa = UserSubAccountDAO.findWithSubAccountAppID(subAccount2, appId, subAppId2);
			assertEquals(appId, usa.getAppId());
			assertEquals(subAppId2, usa.getBindAppId());
			assertEquals(uid, usa.getUid());
			assertEquals(subAccount2, usa.getAccount());
			
			assertTrue(subAccountComponent.bindSubAccount(subAppId1, accessToken, uid, subAccount1, appId));
			usa = UserSubAccountDAO.findWithUidAppid(uid, subAppId1, appId);
			assertEquals(subAppId1, usa.getAppId());
			assertEquals(appId, usa.getBindAppId());
			assertEquals(uid, usa.getUid());
			assertEquals(subAccount1, usa.getAccount());
		}
		catch (BusinessException e)
		{
			fail("test bind subaccount error:"+e.getMessage());
		}
		
		//appId不合法
		try
		{
			subAccountComponent.bindSubAccount(invalidAppid, accessToken, uid, subAccount2, subAppId2);
		}
		catch (BusinessException e)
		{
			assertEquals(BUSINESS_CODE.APP_ID_INVALID, e.getErrCode());
		}
		
		//appId不合法
		 thrown.expect(VerifyException.class);
	     thrown.expect(new ExceptionCodeMatches(BUSINESS_CODE.BINDING_SUBACCOUNT_SUB_APPID_INVALID));
	     subAccountComponent.bindSubAccount(appId, accessToken, uid, subAccount2, invalidAppid);
	     		
		//accessToken不合法
		try
		{
			subAccountComponent.bindSubAccount(appId, invalidToken, uid, subAccount2, subAppId2);
		}
		catch (BusinessException e)
		{
			assertEquals(BUSINESS_CODE.ACCESS_TOKEN_INVALID, e.getErrCode());
		}
		
		//accessToken过期
		try
		{
			subAccountComponent.bindSubAccount(appId, expireToken, uid, subAccount2, subAppId2);
		}
		catch (BusinessException e)
		{
			assertEquals(BUSINESS_CODE.ACCESS_TOKEN_EXPIRE, e.getErrCode());
		}
		
		//uid不合法
		try
		{
			subAccountComponent.bindSubAccount(appId, accessToken, invalidUid, subAccount2, subAppId2);
		}
		catch (BusinessException e)
		{
			assertEquals(BUSINESS_CODE.USER_INVALID, e.getErrCode());
		}
		
		//用户不存在
		try
		{
			subAccountComponent.bindSubAccount(appId, accessToken, zeroUid, subAccount2, subAppId2);
		}
		catch (BusinessException e)
		{
			assertEquals(BUSINESS_CODE.UID_INVALID, e.getErrCode());
		}
		
		//tempId不合法
		try
		{
			subAccountComponent.bindSubAccount(appId, accessToken, uid, null, subAppId2);
		}
		catch (BusinessException e)
		{
			assertEquals(BUSINESS_CODE.BINDING_SUBACCOUNT_TEMPID_INVALID, e.getErrCode());
		}
		
		//重复操作
		try
		{
			subAccountComponent.bindSubAccount(appId, accessToken, uid, subAccount1, subAppId1);
		}
		catch (BusinessException e)
		{
			assertEquals(BUSINESS_CODE.BINDING_SUBACCOUNT_OPERATION_REPEATED, e.getErrCode());
		}
	}

	@Test
	public void testUnbindSubAccount()
	{
		CreateUserResponse createResponse = null;
		String mobile = null;
		long uid = 0;
		
		mobile = getMobile();
		//预设mock
		PowerMockito.mockStatic(ParameterUtils.class);
		PowerMockito.when(ParameterUtils.isValidMobile(mobile)).thenReturn(true);
		PowerMockito.when(ParameterUtils.isValidAppId(appId)).thenReturn(true);
		PowerMockito.when(ParameterUtils.isValidAppId(subAppId1)).thenReturn(true);
		PowerMockito.when(ParameterUtils.isValidAppId(subAppId2)).thenReturn(true);
		PowerMockito.when(ParameterUtils.isValidPassword(password)).thenReturn(true);
		PowerMockito.when(ParameterUtils.validateAccessToken(Mockito.anyString(), Mockito.anyLong(), Mockito.anyInt()))
		.thenReturn(UserConstants.ACCESSTOKEN_VALIDATION_RESULT_SUCCESS);
		
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
		
		//正确解绑子账号
		try
		{		
			assertTrue(subAccountComponent.bindSubAccount(appId, accessToken, uid, subAccount1, subAppId1));
			assertTrue(subAccountComponent.bindSubAccount(appId, accessToken, uid, subAccount1, subAppId2));
			assertTrue(subAccountComponent.bindSubAccount(appId, accessToken, uid, subAccount2, subAppId1));
			assertTrue(subAccountComponent.bindSubAccount(appId, accessToken, uid, subAccount2, subAppId2));
			
			assertTrue(subAccountComponent.bindSubAccount(subAppId1, accessToken, uid, subAccount1, appId));
			assertTrue(subAccountComponent.bindSubAccount(subAppId1, accessToken, uid, subAccount2, appId));
			
			UserSubAccount usa = UserSubAccountDAO.findWithSubAccountAppID(subAccount1, appId, subAppId1);
			assertEquals(appId, usa.getAppId());
			assertEquals(subAppId1, usa.getBindAppId());
			assertEquals(uid, usa.getUid());
			assertEquals(subAccount1, usa.getAccount());
			
			usa = UserSubAccountDAO.findWithSubAccountAppID(subAccount2, appId, subAppId2);
			assertEquals(appId, usa.getAppId());
			assertEquals(subAppId2, usa.getBindAppId());
			assertEquals(uid, usa.getUid());
			assertEquals(subAccount2, usa.getAccount());
			
			//数据库操作失败
			PowerMockito.when(UserSubAccountDAO.deleteWithUidSubAccount(Mockito.anyString(), Mockito.anyInt(), Mockito.anyInt())).thenReturn(0);
			assertFalse(subAccountComponent.unbindSubAccount(appId, accessToken, uid, subAccount1, subAppId1));
			PowerMockito.when(UserSubAccountDAO.deleteWithUidSubAccount(Mockito.anyString(), Mockito.anyInt(), Mockito.anyInt())).thenCallRealMethod();
			
			assertTrue(subAccountComponent.unbindSubAccount(appId, accessToken, uid, subAccount1, subAppId1));
			assertNull(UserSubAccountDAO.findWithSubAccountAppID(subAccount1, appId, subAppId1));
			
			assertTrue(subAccountComponent.unbindSubAccount(appId, accessToken, uid, subAccount1, subAppId2));
			assertNull(UserSubAccountDAO.findWithSubAccountAppID(subAccount1, appId, subAppId2));
			
			assertTrue(subAccountComponent.unbindSubAccount(appId, accessToken, uid, subAccount2, subAppId1));
			assertNull(UserSubAccountDAO.findWithSubAccountAppID(subAccount2, appId, subAppId1));
			
			assertTrue(subAccountComponent.unbindSubAccount(appId, accessToken, uid, subAccount2, subAppId2));
			assertNull(UserSubAccountDAO.findWithSubAccountAppID(subAccount2, appId, subAppId2));
			
			assertTrue(subAccountComponent.unbindSubAccount(subAppId1, accessToken, uid, subAccount1, appId));
			assertNull(UserSubAccountDAO.findWithSubAccountAppID(subAccount1, subAppId1, appId));
			
			assertTrue(subAccountComponent.unbindSubAccount(subAppId1, accessToken, uid, subAccount2, appId));
			assertNull(UserSubAccountDAO.findWithSubAccountAppID(subAccount2, subAppId1, appId));
		}
		catch (Exception e)
		{
			fail("testUnbindSubAccount error:"+e.getMessage());
		}
		
		//appId不合法
		try
		{
			subAccountComponent.unbindSubAccount(invalidAppid, accessToken, uid, subAccount2, subAppId2);
		}
		catch (BusinessException e)
		{
			assertEquals(BUSINESS_CODE.APP_ID_INVALID, e.getErrCode());
		}
		
		//accessToken不合法
		try
		{
			subAccountComponent.unbindSubAccount(appId, invalidToken, uid, subAccount2, subAppId2);
		}
		catch (BusinessException e)
		{
			assertEquals(BUSINESS_CODE.ACCESS_TOKEN_INVALID, e.getErrCode());
		}
		
		//accessToken过期
		try
		{
			subAccountComponent.unbindSubAccount(appId, expireToken, uid, subAccount2, subAppId2);
		}
		catch (BusinessException e)
		{
			assertEquals(BUSINESS_CODE.ACCESS_TOKEN_EXPIRE, e.getErrCode());
		}

		//uid不合法
		try
		{
			subAccountComponent.unbindSubAccount(appId, accessToken, invalidUid, subAccount2, subAppId2);
		}
		catch (BusinessException e)
		{
			assertEquals(BUSINESS_CODE.USER_INVALID, e.getErrCode());
		}
		
		//用户不存在
		try
		{
			subAccountComponent.unbindSubAccount(appId, accessToken, zeroUid, subAccount2, subAppId2);
		}
		catch (BusinessException e)
		{
			assertEquals(BUSINESS_CODE.UID_INVALID, e.getErrCode());
		}
		
		//subAccountId不合法
		try
		{
			subAccountComponent.unbindSubAccount(appId, accessToken, uid, subAccount2, invalidAppid);
		}
		catch (BusinessException e)
		{
			assertEquals(BUSINESS_CODE.UNBINDING_SUBACCOUNT_SUB_APPID_INVALID, e.getErrCode());
		}
		
		//subAccountId不存在
		try
		{
			subAccountComponent.unbindSubAccount(appId, accessToken, uid, subAccount2, subAppId1);
		}
		catch (BusinessException e)
		{
			assertEquals(BUSINESS_CODE.UNBINGDING_SUBACCOUNT_NOT_FOUND, e.getErrCode());
		}
	}

	@Test
	public void testGetSubAccountList()
	{
		CreateUserResponse createResponse = null;
		List<GetSubAccountInfoResponse> listResponse = null;
		
		String mobile = null;
		long uid = 0;
		
		mobile = getMobile();
		//预设mock
		PowerMockito.mockStatic(ParameterUtils.class);
		PowerMockito.when(ParameterUtils.isValidMobile(mobile)).thenReturn(true);
		PowerMockito.when(ParameterUtils.isValidAppId(appId)).thenReturn(true);
		PowerMockito.when(ParameterUtils.isValidAppId(subAppId1)).thenReturn(true);
		PowerMockito.when(ParameterUtils.isValidAppId(subAppId2)).thenReturn(true);
		PowerMockito.when(ParameterUtils.isValidPassword(password)).thenReturn(true);
		PowerMockito.when(ParameterUtils.validateAccessToken(Mockito.anyString(), Mockito.anyLong(), Mockito.anyInt()))
		.thenReturn(UserConstants.ACCESSTOKEN_VALIDATION_RESULT_SUCCESS);
		
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
		
		//正确绑定两个子账号
		try
		{
			assertTrue(subAccountComponent.bindSubAccount(appId, accessToken, uid, subAccount1, subAppId1));
			assertTrue(subAccountComponent.bindSubAccount(appId, accessToken, uid, subAccount1, subAppId2));
			
			assertTrue(subAccountComponent.bindSubAccount(appId, accessToken, uid, subAccount2, subAppId1));
			assertTrue(subAccountComponent.bindSubAccount(appId, accessToken, uid, subAccount2, subAppId2));
			
			assertTrue(subAccountComponent.bindSubAccount(subAppId1, accessToken, uid, subAccount1, appId));
			assertTrue(subAccountComponent.bindSubAccount(subAppId1, accessToken, uid, subAccount2, appId));
			
			UserSubAccount usa = UserSubAccountDAO.findWithSubAccountAppID(subAccount1, appId, subAppId1);
			assertEquals(appId, usa.getAppId());
			assertEquals(subAppId1, usa.getBindAppId());
			assertEquals(uid, usa.getUid());
			assertEquals(subAccount1, usa.getAccount());
		}
		catch (BusinessException e)
		{
			fail("");
		}
		
		//正确获取子账号列表
		try
		{
			listResponse = subAccountComponent.getSubAccountList(subAppId1, accessToken, uid);
			assertNotNull(listResponse);
			assertEquals(2, listResponse.size());
			GetSubAccountInfoResponse infoResponse = listResponse.get(0);
			//assertEquals("微聊", infoResponse.getAppName());
			//(false, infoResponse.getThirdParty());
			assertEquals(String.valueOf(subAppId1), infoResponse.getAppId());
			assertEquals(String.valueOf(appId), infoResponse.getBindAppId());
			assertEquals(subAccount1, infoResponse.getAccount());
			
			infoResponse = listResponse.get(1);
			//assertEquals("保险人", infoResponse.getAppName());
			//assertEquals(true, infoResponse.getThirdParty());
			assertEquals(String.valueOf(subAppId1), infoResponse.getAppId());
			assertEquals(String.valueOf(appId), infoResponse.getBindAppId());
			assertEquals(subAccount2, infoResponse.getAccount());
			
			for(GetSubAccountInfoResponse sub : listResponse){
				assertTrue(subAccountComponent.unbindSubAccount(subAppId1, accessToken, sub.getUid(), sub.getAccount(), Integer.parseInt(sub.getBindAppId())));
				assertNull(UserSubAccountDAO.findWithSubAccountAppID(sub.getAccount(), subAppId1, Integer.parseInt(sub.getBindAppId())));
			}
			
			listResponse = subAccountComponent.getSubAccountList(appId, accessToken, uid);
			assertNotNull(listResponse);
			assertEquals(4, listResponse.size());
			
			for(GetSubAccountInfoResponse sub : listResponse){
				assertTrue(subAccountComponent.unbindSubAccount(appId, accessToken, sub.getUid(), sub.getAccount(), Integer.parseInt(sub.getBindAppId())));
				assertNull(UserSubAccountDAO.findWithSubAccountAppID(sub.getAccount(), appId, Integer.parseInt(sub.getBindAppId())));
			}
			
			listResponse = subAccountComponent.getSubAccountList(appId, accessToken, uid);
			assertNotNull(listResponse);
			assertEquals(0, listResponse.size());
		}
		catch (BusinessException e)
		{
			fail("");
		}
		
		//appId不合法
		try
		{
			subAccountComponent.getSubAccountList(invalidAppid, accessToken, uid);
		}
		catch (BusinessException e)
		{
			assertEquals(BUSINESS_CODE.APP_ID_INVALID, e.getErrCode());
		}
		
		//accessToken不合法
		try
		{
			subAccountComponent.getSubAccountList(subAppId1, invalidToken, uid);
		}
		catch (BusinessException e)
		{
			assertEquals(BUSINESS_CODE.ACCESS_TOKEN_INVALID, e.getErrCode());
		}
		
		//accessToken过期
		try
		{
			subAccountComponent.getSubAccountList(subAppId1, expireToken, uid);
		}
		catch (BusinessException e)
		{
			assertEquals(BUSINESS_CODE.ACCESS_TOKEN_EXPIRE, e.getErrCode());
		}
		
		//用户不存在
		try
		{
			subAccountComponent.getSubAccountList(subAppId1, accessToken, invalidUid);
		}
		catch (BusinessException e)
		{
			assertEquals(BUSINESS_CODE.USER_INVALID, e.getErrCode());
		}
		
		//uid不合法
		try
		{
			subAccountComponent.getSubAccountList(subAppId1, accessToken, zeroUid);
		}
		catch (BusinessException e)
		{
			assertEquals(BUSINESS_CODE.UID_INVALID, e.getErrCode());
		}				
	}

	private void clean()
	{
		for (Long uid : testUsers)
		{
			UserInfoDAO.deleteUser(uid);
			UserExtendInfoDAO.deleteWithUid(uid);
			UserSubAccountDAO.deleteWithUid(uid);
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
		userInfo.setPostcode("423423");
		userInfo.setQq("23423434");
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
		String mobile = String.format("1881000000%d", index);
		index++;
		return mobile;
	}
}
