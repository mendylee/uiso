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
import com.xrk.uiac.bll.component.impl.UserPasswordComponent;
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
@PrepareForTest({ParameterUtils.class, UserDAO.class, Logger.class})
public class UserPasswordComponentTest
{
	private UserComponent userComponent = new UserComponent();
	private UserPasswordComponent passwordComponent = new UserPasswordComponent();
	private Set<Long> testUsers = new HashSet<Long>();
	private Gson gson = new Gson();
	private long index = 1;
	private String password = "password";
	private String newPassword = "newPassword";
	private String invalidPassword = "wrong";
	private int invalidAppid = 10;
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
		PowerMockito.when(UserDAO.updatePassword(Mockito.anyLong(), Mockito.anyString())).thenCallRealMethod();
	}

	@After
	public void tearDown() throws Exception
	{
		clean();
	}

	@Test
	public void testUpdatePassword()
	{
		CreateUserResponse createResponse = null;
		String mobile = null;
		long uid = 0;
		
		mobile = getMobile();
		//预设mock
		PowerMockito.mockStatic(ParameterUtils.class);
		PowerMockito.when(ParameterUtils.isValidMobile(mobile)).thenReturn(true);
		PowerMockito.when(ParameterUtils.isValidAppId(appId)).thenReturn(true);
		PowerMockito.when(ParameterUtils.isValidPassword(password)).thenReturn(true);
		PowerMockito.when(ParameterUtils.isValidPassword(newPassword)).thenReturn(true);
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
		
		//正确修改密码
		try
		{
			//确认原密码
			String dbPwd;
			dbPwd = UserDAO.findWithUid(uid).getPassword();
			assertEquals(dbPwd, ParameterUtils.encryptPassword(password, mobile));
			
			//修改密码
			assertTrue(passwordComponent.updatePassword(appId, accessToken, uid, password, newPassword, false));
			dbPwd = UserDAO.findWithUid(uid).getPassword();
			assertEquals(dbPwd, ParameterUtils.encryptPassword(newPassword, mobile));
			
			//恢复密码
			assertTrue(passwordComponent.updatePassword(appId, accessToken, uid, newPassword, password, false));
		}
		catch (Exception e)
		{
			fail("");
		}
		
		//appId不合法
		try 
		{
			passwordComponent.updatePassword(invalidAppid, accessToken, uid, password, newPassword, false);
        }
        catch (BusinessException e)
		{
	        assertEquals(BUSINESS_CODE.APP_ID_INVALID, e.getErrCode());
        }
		
		//用户不存在
		try 
		{
			passwordComponent.updatePassword(appId, accessToken, invalidUid, password, newPassword, false);
        }
        catch (BusinessException e)
		{
	        assertEquals(BUSINESS_CODE.USER_INVALID, e.getErrCode());
        }
		
		//uid不合法
		try 
		{
			passwordComponent.updatePassword(appId, accessToken, zeroUid, password, newPassword, false);
        }
        catch (BusinessException e)
		{
	        assertEquals(BUSINESS_CODE.UID_INVALID, e.getErrCode());
        }
		
		//原密码错误
		try 
		{
			passwordComponent.updatePassword(appId, accessToken, uid, newPassword, newPassword, false);
        }
        catch (BusinessException e)
		{
	        assertEquals(BUSINESS_CODE.UPDATE_PASSWORD_OLD_PASSWORD_WRONG, e.getErrCode());
        }
		
		//密码格式不对
		try 
		{
			passwordComponent.updatePassword(appId, accessToken, uid, password, invalidPassword, false);
        }
        catch (BusinessException e)
		{
	        assertEquals(BUSINESS_CODE.UPDATE_PASSWORD_PASSWORD_INVALID, e.getErrCode());
        }
		
		//accessToken不合法
		try 
		{
			passwordComponent.updatePassword(appId, invalidToken, uid, password, newPassword, false);
        }
        catch (BusinessException e)
		{
	        assertEquals(BUSINESS_CODE.ACCESS_TOKEN_INVALID, e.getErrCode());
        }
		
		//accessToken过期
		try 
		{
			passwordComponent.updatePassword(appId, expireToken, uid, password, newPassword, false);
        }
        catch (BusinessException e)
		{
	        assertEquals(BUSINESS_CODE.ACCESS_TOKEN_EXPIRE, e.getErrCode());
        }
		
		//验证码验证失败
		try
		{
			PowerMockito.when(ParameterUtils.validateCaptchaStatus(UserConstants.CAPTCHA_CHECKTYPE_RESET_PASSWORD, mobile)).thenReturn(UserConstants.CAPTCHA_VALIDATION_RESULT_UNVERIFIED);
			passwordComponent.updatePassword(appId, accessToken, uid, password, newPassword, false);
		}
		catch (BusinessException e)
		{
			assertEquals(BUSINESS_CODE.CAPTCHA_VALIDATION_UNVERIFIED, e.getErrCode());
		}
		
		//验证码验证过期
		try
		{
			PowerMockito.when(ParameterUtils.validateCaptchaStatus(UserConstants.CAPTCHA_CHECKTYPE_RESET_PASSWORD, mobile)).thenReturn(UserConstants.CAPTCHA_VALIDATION_RESULT_EXPIRE);
			passwordComponent.updatePassword(appId, accessToken, uid, password, newPassword, false);
		}
		catch (BusinessException e)
		{
			assertEquals(BUSINESS_CODE.CAPTCHA_VALIDATION_EXPIRE, e.getErrCode());
		}
		
		PowerMockito.when(ParameterUtils.validateCaptchaStatus(UserConstants.CAPTCHA_CHECKTYPE_RESET_PASSWORD, mobile)).thenReturn(UserConstants.CAPTCHA_VALIDATION_RESULT_SUCCESS);
		//密码更新失败
		try 
		{
			PowerMockito.when(UserDAO.updatePassword(Mockito.anyLong(), Mockito.anyString())).thenReturn(0);
			assertFalse(passwordComponent.updatePassword(appId, accessToken, uid, password, newPassword, false));
        }
        catch (BusinessException e)
		{
	        fail("");
        }
		PowerMockito.when(UserDAO.updatePassword(Mockito.anyLong(), Mockito.anyString())).thenCallRealMethod();
	}

	@Test
	public void testResetPassword()
	{
		CreateUserResponse createResponse = null;
		String mobile = null;
		long uid = 0;
		
		mobile = getMobile();
		//预设mock
		PowerMockito.mockStatic(ParameterUtils.class);
		PowerMockito.when(ParameterUtils.isValidMobile(mobile)).thenReturn(true);
		PowerMockito.when(ParameterUtils.isValidAppId(appId)).thenReturn(true);
		PowerMockito.when(ParameterUtils.isValidPassword(password)).thenReturn(true);
		PowerMockito.when(ParameterUtils.isValidPassword(newPassword)).thenReturn(true);
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
		
		//正确重置密码
		try
		{
			//确认原密码
			String dbPwd;
			dbPwd = UserDAO.findWithUid(uid).getPassword();
			assertEquals(dbPwd, ParameterUtils.encryptPassword(password, mobile));
			
			//修改密码
			assertTrue(passwordComponent.resetPassword(appId, uid, newPassword, false));
			dbPwd = UserDAO.findWithUid(uid).getPassword();
			assertEquals(dbPwd, ParameterUtils.encryptPassword(newPassword, mobile));
			
			//恢复密码
			assertTrue(passwordComponent.resetPassword(appId, uid, password, false));
		}
		catch (Exception e)
		{
			fail("");
		}
		
		//appId不合法
		try
		{
			passwordComponent.resetPassword(invalidAppid, uid, newPassword, false);
        }
        catch (BusinessException e)
		{
	        assertEquals(BUSINESS_CODE.APP_ID_INVALID, e.getErrCode());
        }
		
		//用户不存在
		try
		{
			passwordComponent.resetPassword(appId, invalidUid, newPassword, false);
        }
        catch (BusinessException e)
		{
	        assertEquals(BUSINESS_CODE.USER_INVALID, e.getErrCode());
        }
		
		//uid不合法
		try
		{
			passwordComponent.resetPassword(appId, zeroUid, newPassword, false);
        }
        catch (BusinessException e)
		{
	        assertEquals(BUSINESS_CODE.UID_INVALID, e.getErrCode());
        }
		
		//密码格式不对
		try
		{
			passwordComponent.resetPassword(appId, uid, invalidPassword, false);
        }
        catch (BusinessException e)
		{
	        assertEquals(BUSINESS_CODE.RESET_PASSWORD_PASSWORD_INVALID, e.getErrCode());
        }
		
		//验证码验证失败
		try
		{
			PowerMockito.when(ParameterUtils.validateCaptchaStatus(UserConstants.CAPTCHA_CHECKTYPE_RESET_PASSWORD, mobile)).thenReturn(UserConstants.CAPTCHA_VALIDATION_RESULT_UNVERIFIED);
			passwordComponent.resetPassword(appId, uid, newPassword, false);
		}
		catch (BusinessException e)
		{
			assertEquals(BUSINESS_CODE.CAPTCHA_VALIDATION_UNVERIFIED, e.getErrCode());
		}
		
		//验证码验证过期
		try
		{
			PowerMockito.when(ParameterUtils.validateCaptchaStatus(UserConstants.CAPTCHA_CHECKTYPE_RESET_PASSWORD, mobile)).thenReturn(UserConstants.CAPTCHA_VALIDATION_RESULT_EXPIRE);
			passwordComponent.resetPassword(appId, uid, newPassword, false);
		}
		catch (BusinessException e)
		{
			assertEquals(BUSINESS_CODE.CAPTCHA_VALIDATION_EXPIRE, e.getErrCode());
		}
		
		PowerMockito.when(ParameterUtils.validateCaptchaStatus(UserConstants.CAPTCHA_CHECKTYPE_RESET_PASSWORD, mobile)).thenReturn(UserConstants.CAPTCHA_VALIDATION_RESULT_SUCCESS);
		
		//密码加密失败
		try
		{
			PowerMockito.when(ParameterUtils.encryptPassword(Mockito.anyString(), Mockito.anyString())).thenReturn(null);
			passwordComponent.resetPassword(appId, uid, newPassword, false);
		}
		catch (BusinessException e)
		{
			assertEquals(BUSINESS_CODE.RESET_PASSWORD_PASSWORD_INVALID, e.getErrCode());
		}
		PowerMockito.when(ParameterUtils.encryptPassword(Mockito.anyString(), Mockito.anyString())).thenCallRealMethod();
		
		//密码重置失败
		try
		{
			PowerMockito.when(UserDAO.updatePassword(Mockito.anyLong(), Mockito.anyString())).thenReturn(0);
			assertFalse(passwordComponent.resetPassword(appId, uid, newPassword, false));
		}
		catch (BusinessException e)
		{
			fail("");
		}
		PowerMockito.when(UserDAO.updatePassword(Mockito.anyLong(), Mockito.anyString())).thenCallRealMethod();
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
		userInfo.setPostcode("342432");
		userInfo.setQq("424234324");
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
