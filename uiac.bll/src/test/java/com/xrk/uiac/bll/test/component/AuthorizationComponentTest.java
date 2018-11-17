package com.xrk.uiac.bll.test.component;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Date;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
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

import com.xrk.hws.common.logger.Logger;
import com.xrk.uiac.bll.cache.CacheService;
import com.xrk.uiac.bll.cache.SysConfigCache;
import com.xrk.uiac.bll.common.ParameterUtils;
import com.xrk.uiac.bll.common.UserConstants;
import com.xrk.uiac.bll.component.IAuthorizationComponent;
import com.xrk.uiac.bll.component.IUserCaptchaComponent;
import com.xrk.uiac.bll.component.IUserSubAccountComponent;
import com.xrk.uiac.bll.component.impl.AuthorizationComponent;
import com.xrk.uiac.bll.component.impl.UserAuthorizeSynCompont;
import com.xrk.uiac.bll.component.impl.UserCaptchaComponent;
import com.xrk.uiac.bll.entity.UserIdentityEntity;
import com.xrk.uiac.bll.exception.BUSINESS_CODE;
import com.xrk.uiac.bll.exception.BusinessException;
import com.xrk.uiac.bll.exception.NotFoundException;
import com.xrk.uiac.bll.exception.VerifyException;
import com.xrk.uiac.bll.push.PushClient;
import com.xrk.uiac.bll.response.GetSubAccountInfoResponse;
import com.xrk.uiac.bll.vo.UserAuthorizationVO;
import com.xrk.uiac.common.utils.Codec;
import com.xrk.uiac.dal.DalTestHelper;

/**
 * 授权认证组件单元测试用例
 * AuthorizationComponentTest: AuthorizationComponentTest.java.
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：shunchiguo<shunchiguo@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年5月20日
 * <br> JDK版本：1.7
 * <br>==========================
 */
@RunWith(PowerMockRunner.class)
@PowerMockIgnore( {"javax.management.*"})
@PrepareForTest({SysConfigCache.class, PushClient.class, Logger.class, ParameterUtils.class})
public class AuthorizationComponentTest
{	
	private UserIdentityEntity CreateUser(long uid, int status, String pwd)
	{
		UserIdentityEntity user = new UserIdentityEntity();
		user.setUid(uid);
		user.setPassword(pwd);
		user.setStatus(status);
		return user;
	}
	
	private IAuthorizationComponent auth;
	private long uid1 = 1;
	private long uid2 = 2;
	private String subAccount1 = "wx-openid1111111";
	private String subAccount2 = "wx-openid2222222";
	private String appId1 = "100";
	private String appId2 = "200";
	private String pwd1 = "1234567890";
	private String pwd2 = "0987654321";
	private long timestamp = 0;
	
	@BeforeClass
	public static void SetUpClass()
	{        		
		DalTestHelper.initDal();
		//日志类、推送消息类静态方法Mock
        PowerMockito.mockStatic(PushClient.class);
        PowerMockito.mockStatic(Logger.class);
        
      //mock静态方法，以及相应的返回值
  		PowerMockito.mockStatic(SysConfigCache.class);
  		SysConfigCache sysconfig = mock(SysConfigCache.class);
  		when(sysconfig.getAccessTokenExpireTime()).thenReturn((long) 20 * 1000 * 60);
  		when(sysconfig.getCacheClass()).thenReturn("com.xrk.uiac.bll.cache.external.redis.RedisCache");
  		when(sysconfig.getRedisMasterName()).thenReturn("master1");
  		when(sysconfig.isRedisCluster()).thenReturn(true);
  		//TODO 临时
  		when(sysconfig.getRedisAddr()).thenReturn("192.168.6.217:26380,192.168.6.218:26380");
  		when(SysConfigCache.getInstance()).thenReturn(sysconfig);	
  		
		CacheService.Init();
	}
	
	@Before
	public void SetUp() throws BusinessException
	{
		//mock静态方法，以及相应的返回值
		PowerMockito.mockStatic(SysConfigCache.class);
		SysConfigCache sysconfig = mock(SysConfigCache.class);
		when(sysconfig.getAccessTokenExpireTime()).thenReturn((long) 20 * 1000 * 60);
		when(sysconfig.getCacheClass()).thenReturn("com.xrk.uiac.bll.cache.MemoryCache");		
		when(SysConfigCache.getInstance()).thenReturn(sysconfig);
		
		//模拟参数配置类
		PowerMockito.mockStatic(ParameterUtils.class);
		when(ParameterUtils.isValidAppId(Mockito.anyInt())).thenReturn(true);
		
		pwd1 = Codec.hexMD5(String.format("%s%s", Codec.hexMD5(pwd1), uid1));
		pwd2 = Codec.hexMD5(String.format("%s%s", Codec.hexMD5(pwd2), uid2));
		timestamp = new Date().getTime();
		
		//用户同步组件类Mock
		UserAuthorizeSynCompont userSync = mock(UserAuthorizeSynCompont.class);
		when(userSync.queryAuthroizeUser(String.valueOf(uid1))).thenReturn(CreateUser(uid1, 1, pwd1));
		when(userSync.queryAuthroizeUser(String.valueOf(uid2))).thenReturn(CreateUser(uid2, 1, pwd2));
		
		//验证码发送组件类Mock
		IUserCaptchaComponent userCaptcha = mock(UserCaptchaComponent.class);
	    when(userCaptcha.validateCaptcha(Mockito.anyInt(), Mockito.anyString(), Mockito.eq("00000"), 
	    		Mockito.eq(UserConstants.CAPTCHA_CHECKTYPE_LOGIN)))
	    		.thenReturn(false);
	    		//.thenThrow(new VerifyException(BUSINESS_CODE.VALIDATION_CAPTCHA_CAPTCHA_INVALID, "Invalid captcha"));
	           
	    when(userCaptcha.validateCaptcha(Mockito.anyInt(), Mockito.anyString(), Mockito.eq("12345"), 
	    		Mockito.eq(UserConstants.CAPTCHA_CHECKTYPE_LOGIN))).thenReturn(true);
	    
	    when(userCaptcha.validateCaptcha(Mockito.anyInt(), Mockito.anyString(), Mockito.eq("54321"), 
	    		Mockito.eq(UserConstants.CAPTCHA_CHECKTYPE_LOGIN))).thenReturn(true);
    	
	    //子账号组件
	    IUserSubAccountComponent userSubAccount = mock(IUserSubAccountComponent.class);
	    when(userSubAccount.getSubAccount(Integer.parseInt(appId1), subAccount1, Integer.parseInt(appId2)))
	    .thenReturn(CreateSubAccount(appId1, subAccount1, appId2, uid1));
	    when(userSubAccount.getSubAccount(Integer.parseInt(appId2), subAccount2, Integer.parseInt(appId1)))
	    .thenReturn(CreateSubAccount(appId2, subAccount2, appId1, uid2));
//	    when(userSubAccount.getSubAccount(Mockito.anyInt(), Mockito.anyString(), Mockito.anyInt()))
//	    .thenReturn(value);
	    
		//日志类、推送消息类静态方法Mock
        PowerMockito.mockStatic(PushClient.class);
        PowerMockito.mockStatic(Logger.class);

		auth = new AuthorizationComponent(userSync, userCaptcha, userSubAccount);
	}
	
	private GetSubAccountInfoResponse CreateSubAccount(String appId, String bindAppId, String subAccount, long uid)
    {
		GetSubAccountInfoResponse sub = new GetSubAccountInfoResponse();
		sub.setAccount(subAccount);
		sub.setAppId(appId);
		sub.setBindAppId(bindAppId);
		sub.setThirdParty(false);
		sub.setUid(uid);
	    return sub;
    }

	@After  
    public void tearDown() throws Exception {
    	//在每个测试方法后运行
        //System.out.println("Tear down");  
    }  
   
    @AfterClass  
    public static void tearDownAfterClass() {  
        //System.out.println("Tear down After class");  
    }  
    
    @Rule
    public ExpectedException thrown = ExpectedException.none();
		
	@Test  
    public void testLogin() throws BusinessException{  
		//测试方法
        System.out.println("Test AuthorizationComponent.login");
      	
        String crpwd1 = Codec.hexMD5(String.format("%s%s", pwd1, timestamp));
        String crpwd2 = Codec.hexMD5(String.format("%s%s", pwd2, timestamp));
        
        String accessToken = "";
        String refreshToken = "";
        long delayTime = 0;
     
        UserAuthorizationVO userVo = auth.login(uid1, appId1, crpwd1, timestamp, "", delayTime);
        Assert.assertNotNull(userVo);
        Assert.assertEquals(uid1, userVo.getUid());
        Assert.assertEquals(appId1, userVo.getAppId());
        accessToken = userVo.getAccessToken();
        refreshToken = userVo.getRefreshToken();
        long expireTime = userVo.getExpireDate();
        
        //第二次登录，访问码不会变更，过期时间变更
        userVo = auth.login(uid1, appId1, crpwd1, timestamp, "", delayTime);
        Assert.assertNotNull(userVo);
        Assert.assertEquals(uid1, userVo.getUid());
        Assert.assertEquals(appId1, userVo.getAppId());
        Assert.assertEquals(accessToken, userVo.getAccessToken());
        Assert.assertEquals(refreshToken, userVo.getRefreshToken());
        Assert.assertNotEquals(expireTime, userVo.getExpireDate());
        
        //同一用户登录不同应用
        userVo = auth.login(uid1, appId2, crpwd1, timestamp, "", delayTime);
        Assert.assertNotNull(userVo);
        Assert.assertEquals(uid1, userVo.getUid());
        Assert.assertEquals(appId2, userVo.getAppId());
        
        //另一用户登录	        
        userVo = auth.login(uid2, appId2, crpwd2, timestamp, "", delayTime);
        Assert.assertNotNull(userVo);
        Assert.assertEquals(uid2, userVo.getUid());
        Assert.assertEquals(appId2, userVo.getAppId());
        
        //注销
        auth.logout(userVo.getAccessToken());
        auth.logout(uid1, appId2);
        auth.logout(accessToken);
        
        //测试验证码登录
        String mobile = "13311223344";
        String captcha1 = "12345";
        String captcha2 = "54321";
        userVo = auth.login(uid1, mobile, appId1, captcha1, "", delayTime);
        Assert.assertNotNull(userVo);
        Assert.assertEquals(uid1, userVo.getUid());
        Assert.assertEquals(appId1, userVo.getAppId());
        
        userVo = auth.login(uid1, mobile, appId2, captcha2, "", delayTime);
        Assert.assertNotNull(userVo);
        Assert.assertEquals(uid1, userVo.getUid());
        Assert.assertEquals(appId2, userVo.getAppId());
        
        //注销
       // auth.logout(userVo.getAccessToken());
        auth.logout(uid1);
        
        //验证子账号登录
        //第一种情况，找不到用户        
        thrown.expect(NotFoundException.class);
        thrown.expect(new ExceptionCodeMatches(BUSINESS_CODE.USER_INVALID));
        userVo = auth.login(subAccount1, appId1, appId1, "", expireTime);
        
        //第二种情况，可以正常登录
        userVo = auth.login(subAccount1, appId1, appId2, "", expireTime);
        Assert.assertNotNull(userVo);
        Assert.assertEquals(uid1, userVo.getUid());
        Assert.assertEquals(appId1, userVo.getAppId());
        accessToken = userVo.getAccessToken();
        refreshToken = userVo.getRefreshToken();
        expireTime = userVo.getExpireDate();
        
        //第二次登录，访问码不会变更，过期时间变更
        userVo = auth.login(subAccount1, appId1, appId2, "", delayTime);
        Assert.assertNotNull(userVo);
        Assert.assertEquals(uid1, userVo.getUid());
        Assert.assertEquals(appId1, userVo.getAppId());
        Assert.assertEquals(accessToken, userVo.getAccessToken());
        Assert.assertEquals(refreshToken, userVo.getRefreshToken());
        Assert.assertNotEquals(expireTime, userVo.getExpireDate());
        
        //查询授权Token应该获取到相同的内容
        UserAuthorizationVO userVo2 =auth.queryToken(userVo.getAccessToken());
        Assert.assertEquals(userVo2.getUid(), userVo.getUid());
        Assert.assertEquals(userVo2.getAppId(), userVo.getAppId());
        Assert.assertEquals(userVo2.getAccessToken(), userVo.getAccessToken());
        Assert.assertEquals(userVo2.getLoginDate(), userVo.getLoginDate());        
        
        //注销
        auth.logout(userVo.getAccessToken());
        //再次验证Token        
        thrown.expect(NotFoundException.class);
        thrown.expect(new ExceptionCodeMatches(BUSINESS_CODE.ACCESS_TOKEN_INVALID));
        auth.queryToken(userVo.getAccessToken());        
	}	
	
	@Test  
    public void testLoginException_Captcha() throws BusinessException{  
		//测试方法
        System.out.println("Test AuthorizationComponent.testLoginException_Captcha");
        
       //验证不正确
    	thrown.expect(VerifyException.class);
        thrown.expect(new ExceptionCodeMatches(BUSINESS_CODE.VALIDATION_CAPTCHA_CAPTCHA_INVALID));
        String mobile = "13311223344";
        String captcha = "00000";
        long delayTime = 0;
        auth.login(uid1, mobile, appId1, captcha, "", delayTime);
	}
	
	@Test  
    public void testLoginException_NotFind() throws BusinessException{  
		//测试方法
        System.out.println("Test AuthorizationComponent.LoginException_NotFind");
        
       //登录用户不存在
        String crpwd1 = Codec.hexMD5(String.format("%s%s", pwd1, timestamp));
    	thrown.expect(NotFoundException.class);
        thrown.expect(new ExceptionCodeMatches(BUSINESS_CODE.USER_INVALID));
        long tmpUid = 44444;
        long delayTime = 0;
        auth.login(tmpUid, appId1, crpwd1, timestamp, "", 0);
	}
    
	@Test  
    public void testLoginException_PasswordLen() throws BusinessException{    
        //密码长度不正确
		System.out.println("Test AuthorizationComponent.LoginException_PasswordLen");
    	thrown.expect(VerifyException.class);
        thrown.expect(new ExceptionCodeMatches(BUSINESS_CODE.PARAMER_INVAILD));
        thrown.expectMessage(new ExceptionMessageMatches("密码格式不正确"));
        String tmpPwd = "12345678";
        long delayTime = 0;
        auth.login(uid1, appId1, tmpPwd, timestamp, "", delayTime);
	}
    
	@Test  
    public void testLoginException_AppID() throws BusinessException{          
		System.out.println("Test AuthorizationComponent.LoginException_AppID");
		String crpwd1 = Codec.hexMD5(String.format("%s%s", pwd1, timestamp));
    	thrown.expect(VerifyException.class);
        thrown.expect(new ExceptionCodeMatches(BUSINESS_CODE.PARAMER_INVAILD));
        thrown.expectMessage(new ExceptionMessageMatches("应用ID未输入"));
        long delayTime = 40;
        auth.login(uid1, "", crpwd1, timestamp, "", delayTime);
	}
	
	@Test  
    public void testLoginException_Timestamp() throws BusinessException{          
		System.out.println("Test AuthorizationComponent.LoginException_Timestamp");
		String crpwd1 = Codec.hexMD5(String.format("%s%s", pwd1, timestamp));   
        //时间戳已过期
    	thrown.expect(VerifyException.class);
        thrown.expect(new ExceptionCodeMatches(BUSINESS_CODE.TIMESTAMP_INVALID));
        thrown.expectMessage(new ExceptionMessageMatches("时间戳无效"));
        long timestamp1 = timestamp - 11 * 60 * 1000;
        long delayTime = 0;
        auth.login(uid1, appId1, crpwd1, timestamp1, "", delayTime);
	}
	
	@Test  
    public void testLoginException_Password() throws BusinessException{          
		System.out.println("Test AuthorizationComponent.LoginException_Password");
		String crpwd1 = Codec.hexMD5(String.format("%s%s", pwd2, timestamp));   
    	thrown.expect(VerifyException.class);
        thrown.expect(new ExceptionCodeMatches(BUSINESS_CODE.AUTH_PASSWORD_INVALID));
        thrown.expectMessage(new ExceptionMessageMatches("密码不正确"));
        long delayTime = 0;
        auth.login(uid1, appId1, crpwd1, timestamp, "", delayTime);       
    }  
   
    @Test  
    public void testLogout() throws BusinessException {
        System.out.println("Test AuthorizationComponent.logout"); 
        
        String crpwd1 = Codec.hexMD5(String.format("%s%s", pwd1, timestamp));
        String crpwd2 = Codec.hexMD5(String.format("%s%s", pwd2, timestamp));
        long delayTime = 0;
        
        Assert.assertEquals(0, auth.queryUserToken(uid1).size());
        Assert.assertEquals(0, auth.queryUserToken(uid2).size());
        Assert.assertEquals(0, auth.queryUserToken(99999).size());
        
        //登录用户
        UserAuthorizationVO userVo = auth.login(uid1, appId1, crpwd1, timestamp, "", delayTime);
        Assert.assertNotNull(userVo);
        Assert.assertEquals(1, auth.queryUserToken(uid1).size());
        Assert.assertEquals(0, auth.queryUserToken(uid2).size());
        //String tokenU1_1 = userVo.getAccessToken();
        
        userVo = auth.login(uid1, appId2, crpwd1, timestamp, "", delayTime);
        Assert.assertNotNull(userVo);
        Assert.assertEquals(2, auth.queryUserToken(uid1).size());
        Assert.assertEquals(0, auth.queryUserToken(uid2).size());
        String tokenU1_2 = userVo.getAccessToken();
        
        userVo = auth.login(uid2, appId1, crpwd2, timestamp, "", delayTime);
        Assert.assertNotNull(userVo);
        Assert.assertEquals(2, auth.queryUserToken(uid1).size());
        Assert.assertEquals(1, auth.queryUserToken(uid2).size());
        String tokenU2_1 = userVo.getAccessToken();
        
        userVo = auth.login(uid2, appId2, crpwd2, timestamp, "", delayTime);
        Assert.assertNotNull(userVo);
        Assert.assertEquals(2, auth.queryUserToken(uid1).size());
        Assert.assertEquals(2, auth.queryUserToken(uid2).size());
        String tokenU2_2 = userVo.getAccessToken();
        
        Assert.assertTrue(auth.logout(uid1, appId1));
        Assert.assertEquals(1, auth.queryUserToken(uid1).size());
        Assert.assertEquals(2, auth.queryUserToken(uid2).size());
        
        Assert.assertTrue(auth.logout(tokenU2_1));
        Assert.assertEquals(1, auth.queryUserToken(uid1).size());
        Assert.assertEquals(1, auth.queryUserToken(uid2).size());
        
        Assert.assertTrue(auth.logout(uid2, appId2));
        Assert.assertEquals(1, auth.queryUserToken(uid1).size());
        Assert.assertEquals(0, auth.queryUserToken(uid2).size());
        
        Assert.assertTrue(auth.logout(tokenU1_2));
        Assert.assertEquals(0, auth.queryUserToken(uid1).size());
        Assert.assertEquals(0, auth.queryUserToken(uid2).size());
        
        thrown.expect(NotFoundException.class);
        thrown.expect(new ExceptionCodeMatches(BUSINESS_CODE.ACCESS_TOKEN_INVALID));
        thrown.expectMessage(new ExceptionMessageMatches("未找到授权访问码信息"));
        auth.logout(tokenU2_2);
    }  
   
    @Test  
    public void testLogoutException() throws BusinessException{          
		System.out.println("Test AuthorizationComponent.LogoutException");
		thrown.expect(NotFoundException.class);
        thrown.expect(new ExceptionCodeMatches(BUSINESS_CODE.ACCESS_TOKEN_INVALID));
        thrown.expectMessage(new ExceptionMessageMatches("未找到授权访问码信息"));
        auth.logout(9988877, appId1);
    }
     
    private void CompareVo(UserAuthorizationVO userVo, UserAuthorizationVO userVo1)
    {
        Assert.assertEquals(userVo.getUid(), userVo1.getUid());
        Assert.assertEquals(userVo.getAppId(), userVo1.getAppId());
        Assert.assertEquals(userVo.getAccessToken(), userVo1.getAccessToken());
        Assert.assertEquals(userVo.getRefreshToken(), userVo1.getRefreshToken());
        //Assert.assertEquals(userVo.getExpireDate(), userVo1.getExpireDate());
    }
    @Test
    public void testUpdateToken() throws BusinessException {  
    	System.out.println("Test AuthorizationComponent.updateToken");
        
        String crpwd1 = Codec.hexMD5(String.format("%s%s", pwd1, timestamp));
        String crpwd2 = Codec.hexMD5(String.format("%s%s", pwd2, timestamp));
        long delayTime = 0;
        
        Assert.assertEquals(0, auth.queryUserToken(uid1).size());
        Assert.assertEquals(0, auth.queryUserToken(uid2).size());
        
        UserAuthorizationVO userVo=null;
        //未登录时
        try
        {
        	userVo = auth.updateToken(uid1, appId1, "", "");
        }
        catch(NotFoundException ex)
        {
        	Assert.assertEquals(BUSINESS_CODE.USER_INVALID, ex.getErrCode());
        	Assert.assertEquals("未找到用户的登录信息", ex.getMessage());
        }
        
        //登录用户
        userVo = auth.login(uid1, appId1, crpwd1, timestamp, "", delayTime);
        Assert.assertNotNull(userVo);
        Assert.assertEquals(1, auth.queryUserToken(uid1).size());
        Assert.assertEquals(0, auth.queryUserToken(uid2).size());
        String tokenU1_1 = userVo.getAccessToken();
        userVo = auth.updateToken(uid1, appId1, userVo.getRefreshToken(), userVo.getAccessToken());
        Assert.assertNotEquals(tokenU1_1, userVo.getAccessToken());
        //原Token已被注销
        try
        {
        	userVo = auth.queryToken(tokenU1_1);
        }
        catch(NotFoundException ex)
        {
        	Assert.assertEquals(BUSINESS_CODE.ACCESS_TOKEN_INVALID, ex.getErrCode());
        	Assert.assertEquals("未找到授权访问码信息", ex.getMessage());
        }
        
        //如果使用不正确的Token更新，也会触发异常
        tokenU1_1 = userVo.getAccessToken();
        try
        {
        	userVo = auth.updateToken(uid1, appId1, tokenU1_1, userVo.getRefreshToken());
        }
        catch(VerifyException ex)
        {
        	Assert.assertEquals(BUSINESS_CODE.AUTH_REFRESH_TOKEN_WRONG, ex.getErrCode());
        	Assert.assertEquals("refreshToken不正确", ex.getMessage());
        }
        
        UserAuthorizationVO userVo1 = auth.queryToken(tokenU1_1);
        CompareVo(userVo, userVo1);
                
        userVo = auth.login(uid1, appId2, crpwd1, timestamp, "", delayTime);
        Assert.assertNotNull(userVo);
        Assert.assertEquals(2, auth.queryUserToken(uid1).size());
        Assert.assertEquals(0, auth.queryUserToken(uid2).size());
        String tokenU1_2 = userVo.getAccessToken();
        userVo = auth.updateToken(uid1, appId2, userVo.getRefreshToken(), userVo.getAccessToken());
        Assert.assertNotEquals(tokenU1_2, userVo.getAccessToken());
        tokenU1_2 = userVo.getAccessToken();
        userVo1 = auth.queryToken(tokenU1_2);
        CompareVo(userVo, userVo1);
        
        userVo = auth.login(uid2, appId1, crpwd2, timestamp, "", delayTime);
        Assert.assertNotNull(userVo);
        Assert.assertEquals(2, auth.queryUserToken(uid1).size());
        Assert.assertEquals(1, auth.queryUserToken(uid2).size());
        String tokenU2_1 = userVo.getAccessToken();
        userVo = auth.updateToken(uid2, appId1, userVo.getRefreshToken(), userVo.getAccessToken());
        Assert.assertNotEquals(tokenU2_1, userVo.getAccessToken());
        tokenU2_1 = userVo.getAccessToken();
        userVo1 = auth.queryToken(tokenU2_1);
        CompareVo(userVo, userVo1);
        
        userVo = auth.login(uid2, appId2, crpwd2, timestamp, "", delayTime);
        Assert.assertNotNull(userVo);
        Assert.assertEquals(2, auth.queryUserToken(uid1).size());
        Assert.assertEquals(2, auth.queryUserToken(uid2).size());
        String tokenU2_2 = userVo.getAccessToken();
        userVo = auth.updateToken(uid2, appId2, userVo.getRefreshToken(), userVo.getAccessToken());
        Assert.assertNotEquals(tokenU2_2, userVo.getAccessToken());
        tokenU2_2 = userVo.getAccessToken();
        userVo1 = auth.queryToken(tokenU2_2);
        CompareVo(userVo, userVo1);         
        
        auth.logout(uid1, appId1);
        auth.logout(uid1, appId2);
        auth.logout(uid2, appId1);
        auth.logout(uid2, appId2);
    }  
}
