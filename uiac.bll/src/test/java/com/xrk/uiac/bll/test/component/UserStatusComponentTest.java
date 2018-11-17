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
import com.xrk.uiac.bll.component.impl.UserComponent;
import com.xrk.uiac.bll.component.impl.UserStatusComponent;
import com.xrk.uiac.bll.exception.BUSINESS_CODE;
import com.xrk.uiac.bll.exception.BusinessException;
import com.xrk.uiac.bll.response.CreateUserResponse;
import com.xrk.uiac.dal.Dal;
import com.xrk.uiac.dal.DalTestHelper;
import com.xrk.uiac.dal.dao.UserDAO;
import com.xrk.uiac.dal.dao.UserExtendInfoDAO;
import com.xrk.uiac.dal.dao.UserInfoDAO;
import com.xrk.uiac.dal.entity.User;
import com.xrk.uiac.dal.entity.UserInfo;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore( {"javax.management.*"})
@PrepareForTest({ParameterUtils.class, UserDAO.class, UserInfoDAO.class, Logger.class})
public class UserStatusComponentTest
{
	private UserComponent userComponent = new UserComponent();
	private UserStatusComponent statusComponent = new UserStatusComponent();
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
		//对UserDAO进行mock
		PowerMockito.mockStatic(UserDAO.class);
		PowerMockito.when(UserDAO.findWithAccount(Mockito.anyString())).thenCallRealMethod();
		PowerMockito.when(UserDAO.findWithUid(Mockito.anyLong())).thenCallRealMethod();
		PowerMockito.when(UserDAO.insertUser(Mockito.any(User.class))).thenCallRealMethod();
		PowerMockito.when(UserDAO.deleteUser(Mockito.anyLong())).thenCallRealMethod();
		PowerMockito.when(UserDAO.count()).thenCallRealMethod();
		PowerMockito.when(UserDAO.updateStatus(Mockito.anyLong(), Mockito.anyInt())).thenCallRealMethod();
	}

	@After
	public void tearDown() throws Exception
	{
		clean();
	}
	
	@Test
	public void testGetUserStatus()
	{
		CreateUserResponse createResponse = null; 
		String mobile = null;
		String mobile2 = null;
		long targetId = 0;
		long uid = 0;
		
		mobile = getMobile();
		mobile2 = getMobile();
		//预设mock
		PowerMockito.mockStatic(ParameterUtils.class);
		PowerMockito.when(ParameterUtils.isValidMobile(mobile)).thenReturn(true);
		PowerMockito.when(ParameterUtils.isValidMobile(mobile2)).thenReturn(true);
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
			createResponse = userComponent.createUser(appId, mobile2, password, getUserInfo(), getExtendInfo(), false);
			testUsers.add(createResponse.getUid());
			targetId = createResponse.getUid();
		}
		catch (Exception e)
		{
			fail("");
		}
		
		PowerMockito.when(ParameterUtils.validateAccessToken(expireToken, uid, appId)).thenReturn(UserConstants.ACCESSTOKEN_VALIDATION_RESULT_EXPIRE);
		PowerMockito.when(ParameterUtils.validateAccessToken(accessToken, uid, appId)).thenReturn(UserConstants.ACCESSTOKEN_VALIDATION_RESULT_SUCCESS);
		PowerMockito.when(ParameterUtils.validateAccessToken(invalidToken, uid, appId)).thenReturn(UserConstants.ACCESSTOKEN_VALIDATION_RESULT_INVALID);
		
		//正确获取用户状态
		try
		{
			assertEquals(UserConstants.ACCOUNT_STATUS_ENABLED, UserDAO.findWithUid(targetId).getStatus());
			assertTrue(statusComponent.disableUser(appId, accessToken, uid, targetId));
			assertEquals(UserConstants.ACCOUNT_STATUS_DISABLED, UserDAO.findWithUid(targetId).getStatus());
			
			assertFalse(statusComponent.getUserStatus(appId, accessToken, uid, targetId));
			
			//恢复状态
			assertTrue(statusComponent.enableUser(appId, accessToken, uid, targetId));
			assertEquals(UserConstants.ACCOUNT_STATUS_ENABLED, UserDAO.findWithUid(targetId).getStatus());
			
			assertTrue(statusComponent.getUserStatus(appId, accessToken, uid, targetId));
		}
		catch (Exception e)
		{
			fail("");
		}

		//appId不合法
		try
		{
	        statusComponent.getUserStatus(invalidAppid, accessToken, uid, targetId);
        }
        catch (BusinessException e)
		{
	        assertEquals(BUSINESS_CODE.APP_ID_INVALID, e.getErrCode());
        }
		
		//accessToken不合法
		try
		{
	        statusComponent.getUserStatus(appId, invalidToken, uid, targetId);
        }
        catch (BusinessException e)
		{
	        assertEquals(BUSINESS_CODE.ACCESS_TOKEN_INVALID, e.getErrCode());
        }
		
		//accessToken过期
		try
		{
	        statusComponent.getUserStatus(appId, expireToken, uid, targetId);
        }
        catch (BusinessException e)
		{
	        assertEquals(BUSINESS_CODE.ACCESS_TOKEN_EXPIRE, e.getErrCode());
        }
		
		//uid不合法
		try
		{
	        statusComponent.getUserStatus(appId, accessToken, zeroUid, targetId);
        }
        catch (BusinessException e)
		{
	        assertEquals(BUSINESS_CODE.UID_INVALID, e.getErrCode());
        }
		
		//uid不合法
		try
		{
	        statusComponent.getUserStatus(appId, accessToken, uid, zeroUid);
        }
        catch (BusinessException e)
		{
	        assertEquals(BUSINESS_CODE.UID_INVALID, e.getErrCode());
        }
		
		//用户不存在
		try
		{
	        statusComponent.getUserStatus(appId, accessToken, uid, invalidUid);
        }
        catch (BusinessException e)
		{
	        assertEquals(BUSINESS_CODE.USER_INVALID, e.getErrCode());
        }
	}

	@Test
	public void testDisableUser()
	{
		CreateUserResponse createResponse = null;
		String mobile = null;
		String mobile2 = null;
		long targetId = 0;
		long uid = 0;
		
		mobile = getMobile();
		mobile2 = getMobile();
		//预设mock
		PowerMockito.mockStatic(ParameterUtils.class);
		PowerMockito.when(ParameterUtils.isValidMobile(mobile)).thenReturn(true);
		PowerMockito.when(ParameterUtils.isValidMobile(mobile2)).thenReturn(true);
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
			createResponse = userComponent.createUser(appId, mobile2, password, getUserInfo(), getExtendInfo(), false);
			testUsers.add(createResponse.getUid());
			targetId = createResponse.getUid();
		}
		catch (Exception e)
		{
			fail("");
		}
		
		PowerMockito.when(ParameterUtils.validateAccessToken(expireToken, uid, appId)).thenReturn(UserConstants.ACCESSTOKEN_VALIDATION_RESULT_EXPIRE);
		PowerMockito.when(ParameterUtils.validateAccessToken(accessToken, uid, appId)).thenReturn(UserConstants.ACCESSTOKEN_VALIDATION_RESULT_SUCCESS);
		PowerMockito.when(ParameterUtils.validateAccessToken(invalidToken, uid, appId)).thenReturn(UserConstants.ACCESSTOKEN_VALIDATION_RESULT_INVALID);
		
		//正确禁用用户
		try
		{
			//更新状态失败
			PowerMockito.when(UserDAO.updateStatus(Mockito.anyLong(), Mockito.anyInt())).thenReturn(0);
			assertFalse(statusComponent.disableUser(appId, accessToken, uid, targetId));
			PowerMockito.when(UserDAO.updateStatus(Mockito.anyLong(), Mockito.anyInt())).thenCallRealMethod();
			
			assertEquals(UserConstants.ACCOUNT_STATUS_ENABLED, UserDAO.findWithUid(targetId).getStatus());
			assertTrue(statusComponent.disableUser(appId, accessToken, uid, targetId));
			assertEquals(UserConstants.ACCOUNT_STATUS_DISABLED, UserDAO.findWithUid(targetId).getStatus());
			
			//恢复状态
			assertTrue(statusComponent.enableUser(appId, accessToken, uid, targetId));
			assertEquals(UserConstants.ACCOUNT_STATUS_ENABLED, UserDAO.findWithUid(targetId).getStatus());
		}
		catch (Exception e)
		{
			fail("");
		}
		
		//重复操作
		try
		{
			statusComponent.disableUser(appId, accessToken, uid, targetId);
			statusComponent.disableUser(appId, accessToken, uid, targetId);
		}
		catch (BusinessException e)
		{
			assertEquals(BUSINESS_CODE.DISABLE_USER_OPERATION_REPEATED, e.getErrCode());
		}
		
		//appId不合法
		try
		{
	        statusComponent.disableUser(invalidAppid, accessToken, uid, targetId);
        }
        catch (BusinessException e)
		{
	        assertEquals(BUSINESS_CODE.APP_ID_INVALID, e.getErrCode());
        }
		
		//accessToken不合法
		try
		{
	        statusComponent.disableUser(appId, invalidToken, uid, targetId);
        }
        catch (BusinessException e)
		{
	        assertEquals(BUSINESS_CODE.ACCESS_TOKEN_INVALID, e.getErrCode());
        }
		
		//accessToken过期
		try
		{
	        statusComponent.disableUser(appId, expireToken, uid, targetId);
        }
        catch (BusinessException e)
		{
	        assertEquals(BUSINESS_CODE.ACCESS_TOKEN_EXPIRE, e.getErrCode());
        }
		
		//uid不合法
		try
		{
	        statusComponent.disableUser(appId, accessToken, zeroUid, targetId);
        }
        catch (BusinessException e)
		{
	        assertEquals(BUSINESS_CODE.UID_INVALID, e.getErrCode());
        }
		
		//uid不合法
		try
		{
	        statusComponent.disableUser(appId, accessToken, uid, zeroUid);
        }
        catch (BusinessException e)
		{
	        assertEquals(BUSINESS_CODE.UID_INVALID, e.getErrCode());
        }
		
		//用户不存在
		try
		{
	        statusComponent.disableUser(appId, accessToken, uid, invalidUid);
        }
        catch (BusinessException e)
		{
	        assertEquals(BUSINESS_CODE.USER_INVALID, e.getErrCode());
        }
	}

	@Test
	public void testEnableUser()
	{
		CreateUserResponse createResponse = null;
		String mobile = null;
		String mobile2 = null;
		long targetId = 0;
		long uid = 0;
		
		mobile = getMobile();
		mobile2 = getMobile();
		//预设mock
		PowerMockito.mockStatic(ParameterUtils.class);
		PowerMockito.when(ParameterUtils.isValidMobile(mobile)).thenReturn(true);
		PowerMockito.when(ParameterUtils.isValidMobile(mobile2)).thenReturn(true);
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
			createResponse = userComponent.createUser(appId, mobile2, password, getUserInfo(), getExtendInfo(), false);
			testUsers.add(createResponse.getUid());
			targetId = createResponse.getUid();
		}
		catch (Exception e)
		{
			fail("");
		}
		
		PowerMockito.when(ParameterUtils.validateAccessToken(expireToken, uid, appId)).thenReturn(UserConstants.ACCESSTOKEN_VALIDATION_RESULT_EXPIRE);
		PowerMockito.when(ParameterUtils.validateAccessToken(accessToken, uid, appId)).thenReturn(UserConstants.ACCESSTOKEN_VALIDATION_RESULT_SUCCESS);
		PowerMockito.when(ParameterUtils.validateAccessToken(invalidToken, uid, appId)).thenReturn(UserConstants.ACCESSTOKEN_VALIDATION_RESULT_INVALID);
		
		//正确禁用并解禁用户
		try
		{
			assertEquals(UserConstants.ACCOUNT_STATUS_ENABLED, UserDAO.findWithUid(targetId).getStatus());
			assertTrue(statusComponent.disableUser(appId, accessToken, uid, targetId));
			assertEquals(UserConstants.ACCOUNT_STATUS_DISABLED, UserDAO.findWithUid(targetId).getStatus());
			
			//更新状态失败
			PowerMockito.when(UserDAO.updateStatus(Mockito.anyLong(), Mockito.anyInt())).thenReturn(0);
			assertFalse(statusComponent.enableUser(appId, accessToken, uid, targetId));
			PowerMockito.when(UserDAO.updateStatus(Mockito.anyLong(), Mockito.anyInt())).thenCallRealMethod();
			
			//恢复状态
			assertTrue(statusComponent.enableUser(appId, accessToken, uid, targetId));
			assertEquals(UserConstants.ACCOUNT_STATUS_ENABLED, UserDAO.findWithUid(targetId).getStatus());
		}
		catch (Exception e)
		{
			fail("");
		}
		
		//重复操作
		try
		{
			statusComponent.enableUser(appId, accessToken, uid, targetId);
		}
		catch (BusinessException e)
		{
			assertEquals(BUSINESS_CODE.ENABLE_USER_OPERATION_REPEATED, e.getErrCode());
		}
		
		//appId不合法
		try
		{
	        statusComponent.enableUser(invalidAppid, accessToken, uid, targetId);
        }
        catch (BusinessException e)
		{
	        assertEquals(BUSINESS_CODE.APP_ID_INVALID, e.getErrCode());
        }
		
		//accessToken不合法
		try
		{
	        statusComponent.enableUser(appId, invalidToken, uid, targetId);
        }
        catch (BusinessException e)
		{
	        assertEquals(BUSINESS_CODE.ACCESS_TOKEN_INVALID, e.getErrCode());
        }
		
		//accessToken过期
		try
		{
	        statusComponent.enableUser(appId, expireToken, uid, targetId);
        }
        catch (BusinessException e)
		{
	        assertEquals(BUSINESS_CODE.ACCESS_TOKEN_EXPIRE, e.getErrCode());
        }
		
		//uid不合法
		try
		{
	        statusComponent.enableUser(appId, accessToken, zeroUid, targetId);
        }
        catch (BusinessException e)
		{
	        assertEquals(BUSINESS_CODE.UID_INVALID, e.getErrCode());
        }
		
		//uid不合法
		try
		{
	        statusComponent.enableUser(appId, accessToken, uid, zeroUid);
        }
        catch (BusinessException e)
		{
	        assertEquals(BUSINESS_CODE.UID_INVALID, e.getErrCode());
        }
		
		//用户不存在
		try
		{
	        statusComponent.enableUser(appId, accessToken, uid, invalidUid);
        }
        catch (BusinessException e)
		{
	        assertEquals(BUSINESS_CODE.USER_INVALID, e.getErrCode());
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
		userInfo.setPostcode("423423");
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
	
	private String getMobile()
	{
		String mobile = String.format("1880000000%d", index);
		index++;
		return mobile;
	}
}
