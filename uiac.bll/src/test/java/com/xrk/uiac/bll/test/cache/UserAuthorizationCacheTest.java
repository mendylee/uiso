package com.xrk.uiac.bll.test.cache;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.xrk.hws.common.logger.Logger;
import com.xrk.uiac.bll.cache.CacheService;
import com.xrk.uiac.bll.cache.SysConfigCache;
import com.xrk.uiac.bll.cache.component.UserAuthorizationCache;
import com.xrk.uiac.bll.entity.UserAuthorizationEntity;
import com.xrk.uiac.dal.DalTestHelper;

@RunWith(PowerMockRunner.class)
@PrepareForTest({SysConfigCache.class, Logger.class})
public class UserAuthorizationCacheTest
{
	@BeforeClass
	public static void SetUpClass()
	{		
		DalTestHelper.initDal();
	}
	
	private UserAuthorizationCache cache;
	
	@Before
	public void SetUp()
	{
		//日志类静态方法Mock
		PowerMockito.mockStatic(Logger.class);
		
		//mock静态方法，以及相应的返回值
		PowerMockito.mockStatic(SysConfigCache.class);
		SysConfigCache sysconfig = mock(SysConfigCache.class);
		when(sysconfig.getCacheClass()).thenReturn("com.xrk.uiac.bll.cache.MemoryCache");		
		when(SysConfigCache.getInstance()).thenReturn(sysconfig);	
		
		CacheService.Init();
		
		//在测试方法运行之前运行
		cache = new UserAuthorizationCache();
		cache.setDelayRemoveTime(0);//默认超时立即删除
	}
	private UserAuthorizationEntity CreateUser(long uid, String appId, String authToken, String refreshToken, int expireSecond,
	                                        Set<String> scope)
	{
		Calendar now = Calendar.getInstance();
		now.add(Calendar.SECOND, expireSecond);
		UserAuthorizationEntity user = new UserAuthorizationEntity();
		user.setUid(uid);
		user.setAppId(appId);
		user.setAuthToken(authToken);
		user.setRefreshToken(refreshToken);
		user.setExpireTime(now.getTime());
		user.setLoginTime(new Date());
		user.setScope(scope);
		return user;
	}

	@Test
	public void testPut()
	{
		System.out.println("Test UserAuthorizationTest.Put");
		// 测试方法
		long uid = 1111;
		String appId = "wx";
		String token = "author token";
		String refToken = "refresh token";
		
		String appId2 = "dlr";		
		String token2 = "author token222222";
		String refToken2 = "refresh token222222222";
		int expire = 2;
		Set<String> scope = new HashSet<String>();
		scope.add("scope1");
		scope.add("scope2");
		scope.add("scope3");
		
		UserAuthorizationEntity user = CreateUser(uid, appId, token, refToken, expire, scope);
		
		// 不同的添加方法，用户应该保持一致
		Assert.assertTrue(cache.put(user));
		Assert.assertTrue(cache.put("2222", user));
		Assert.assertTrue(cache.put("", user));
		Assert.assertEquals(1, cache.size());
		Assert.assertEquals(uid, cache.get(uid, appId).getUid());
		Assert.assertEquals(token, cache.get(user).getAuthToken());
		Assert.assertEquals(refToken, cache.get(user).getRefreshToken());
		Assert.assertEquals(scope.size(), cache.get(uid, appId).getScope().size());
		
		//同一用户可以在不同的App登录
		UserAuthorizationEntity user2 = CreateUser(uid, appId2, token2, refToken2, expire, scope);
		Assert.assertTrue(cache.put(user2));
		Assert.assertEquals(2, cache.size());
		Assert.assertEquals(uid, cache.get(uid, appId2).getUid());
		Assert.assertEquals(token2, cache.get(uid, appId2).getAuthToken());
		Assert.assertEquals(refToken2, cache.get(user2).getRefreshToken());
		Assert.assertEquals(scope.size(), cache.get(uid, appId2).getScope().size());
		
		//更新用户信息到缓存，获取到的内容也会变更
		token = "edit token test";
		refToken = "edit ref token test";
		user.setAuthToken(token);
		user.setRefreshToken(refToken);
		Assert.assertTrue(cache.put(user));
		Assert.assertEquals(token, cache.get(uid, appId).getAuthToken());
		Assert.assertEquals(refToken, cache.get(user).getRefreshToken());
		
		//休眠缓存过期时间，缓存应该过期了
		try {
	        Thread.sleep((expire+1) * 1000);
        }
        catch (InterruptedException e) {
	        e.printStackTrace();
        }
		Assert.assertEquals(0, cache.size());
		
		int total = 1000;
		Assert.assertEquals(0, cache.size());
		//测试超时缓存
		expire = 5;
		for(int i=0; i<total; i++)
		{
			Assert.assertTrue(cache.put(CreateUser(i, appId, token, refToken, expire, scope)));
		}
		try {
	        Thread.sleep(3*1000);
        }
        catch (InterruptedException e) {
	        e.printStackTrace();
        }
		//再添加一批
		for(int i=total; i<total*2; i++)
		{
			Assert.assertTrue(cache.put(CreateUser(i, appId, token, refToken, expire, scope)));
		}
		Assert.assertEquals(total*2, cache.size());
		
		//再休眠3秒，第一批已过期
		try {
	        Thread.sleep(3*1000);
        }
        catch (InterruptedException e) {
	        e.printStackTrace();
        }
		Assert.assertEquals(total, cache.size());
		
		//休眠3秒第二批也过期
		try {
	        Thread.sleep(3*1000);
        }
        catch (InterruptedException e) {
	        e.printStackTrace();
        }
		Assert.assertEquals(0, cache.size());		
	}

	@Test
	public void testContain()
	{
		System.out.println("Test UserAuthorizationTest.Contain");
		long uid1 = 1111;
		long uid2 = 2222;
		String appId = "wx";
		String appId2 = "app222";
		String token = "author token";
		String refToken = "refresh token";
		int expire = 2;
		Set<String> scope = new HashSet<String>();
		scope.add("scope1");

		Assert.assertEquals(0, cache.size());
		Assert.assertFalse(cache.contain(uid1, appId));
		Assert.assertFalse(cache.contain(uid2, appId));

		UserAuthorizationEntity user1 = CreateUser(uid1, appId, token, refToken, expire, scope);
		Assert.assertTrue(cache.put(user1));
		Assert.assertEquals(1, cache.size());
		Assert.assertTrue(cache.contain(uid1, appId));
		Assert.assertFalse(cache.contain(uid1, appId2));
		Assert.assertTrue(cache.contain(user1));
		
		Assert.assertFalse(cache.contain(uid2, appId));
		Assert.assertFalse(cache.contain(uid2, appId2));

		UserAuthorizationEntity user1_2 = CreateUser(uid1, appId2, token, refToken, expire, scope);
		Assert.assertTrue(cache.put(user1_2));
		Assert.assertEquals(2, cache.size());
		Assert.assertTrue(cache.contain(uid1, appId));
		Assert.assertTrue(cache.contain(uid1, appId2));
		Assert.assertTrue(cache.contain(user1_2));
		
		Assert.assertFalse(cache.contain(uid2, appId));
		Assert.assertFalse(cache.contain(uid2, appId2));
		
		UserAuthorizationEntity user2 = CreateUser(uid2, appId, token, refToken, expire, scope);
		Assert.assertTrue(cache.put(user2));
		Assert.assertEquals(3, cache.size());
		Assert.assertTrue(cache.contain(uid2, appId));
		Assert.assertFalse(cache.contain(uid2, appId2));
		Assert.assertTrue(cache.contain(user2));
		
		//移除缓存
		Assert.assertTrue(cache.remove(user1));
		Assert.assertFalse(cache.contain(user1));
		Assert.assertFalse(cache.contain(uid1, appId));
		Assert.assertTrue(cache.contain(uid1, appId2));
		
		Assert.assertTrue(cache.contain(uid2, appId));

		Assert.assertFalse(cache.contain(12345));
		cache.clear();
	}

	@Test
	public void testRemove()
	{
		System.out.println("Test UserAuthorizationTest.Remove");
		
		String token = "author token";
		String refToken = "refresh token";
		String appId = "wx";
		String appId2 = "appid 2";
		int expire = 3;
		Set<String> scope = new HashSet<String>();
		scope.add("scope1");
		
		int total = 1000;
		Assert.assertEquals(0, cache.size());
		for(int i=0; i<total; i++)
		{
			Assert.assertTrue(cache.put(CreateUser(i, appId, token, refToken, expire, scope)));
			Assert.assertTrue(cache.put(CreateUser(i, appId2, token, refToken, expire, scope)));
		}
		Assert.assertEquals(total*2, cache.size());
		//移除部份缓存
		int removeTot = 500;
		for(int i=0; i<removeTot;i++)
		{
			Assert.assertTrue(cache.remove(i*2, appId));
		}
		Assert.assertEquals(total*2 - removeTot, cache.size());

		try {
	        Thread.sleep((expire+1)*1000);
        }
        catch (InterruptedException e) {
	        e.printStackTrace();
        }
		Assert.assertEquals(0, cache.size());		
	}
	
	@Test
	public void testGetByToken()
	{
		System.out.println("Test UserAuthorizationTest.GetByToken");
		String token = "author token";
		String refToken = "refresh token";
		String appId1 = "wx";
		String appId2 = "qq";
		int expire = 3;
		Set<String> scope = new HashSet<String>();
		scope.add("scope1");
		
		int total = 1000;
		Assert.assertEquals(0, cache.size());
		
		for(int i=0; i<total; i++)
		{
			Assert.assertTrue(cache.put(CreateUser(i, appId1, String.format("app1_%s_%s", token, i), String.format("app1_%s_%s", refToken, i), expire, scope)));
			Assert.assertTrue(cache.put(CreateUser(i, appId2, String.format("app2_%s_%s", token, i), String.format("app2_%s_%s", refToken, i), expire*2, scope)));
		}
		Assert.assertEquals(total*2, cache.size());
		for(int i=0; i<total; i++)
		{
			Assert.assertEquals(i, cache.getByToken(String.format("app1_%s_%s", token, i)).getUid());
			Assert.assertEquals(i, cache.getByToken(String.format("app2_%s_%s", token, i)).getUid());
		}
		
		try {
	        Thread.sleep((expire+1)*1000);
        }
        catch (InterruptedException e) {
	        e.printStackTrace();
        }
		
		for(int i=0; i<total; i++)
		{
			Assert.assertNull(cache.getByToken(String.format("app1_%s_%s", token, i)));
			Assert.assertEquals(i, cache.getByToken(String.format("app2_%s_%s", token, i)).getUid());
		}
		cache.clear();
	}
	
	@Test
	public void testGetUserLoginApp()
	{
		System.out.println("Test UserAuthorizationTest.GetUserLoginApp");
		long uid = 1;
		String token = "author token";
		String refToken = "refresh token";
		String appId1 = "wx";
		String appId2 = "qq";
		int expire = 3;
		Set<String> scope = new HashSet<String>();
		scope.add("scope1");
		
		String[] lsApp = cache.getUserLoginApp(uid);
		if(lsApp != null && lsApp.length != 0){
			Assert.fail(String.format("expected appId is null or 0, but was:%s", lsApp.length));
		}
		
		Assert.assertTrue(cache.put(CreateUser(uid, appId1, String.format("app1_%s_%s", token, 0), String.format("app1_%s_%s", refToken, 0), expire, scope)));
		Assert.assertEquals(1, cache.getUserLoginApp(uid).length);
		Assert.assertTrue(cache.put(CreateUser(uid, appId2, String.format("app2_%s_%s", token, 0), String.format("app2_%s_%s", refToken, 0), expire, scope)));
		Assert.assertEquals(2, cache.getUserLoginApp(uid).length);
		Assert.assertTrue(cache.put(CreateUser(uid, appId2, String.format("app2_%s_%s", token, 1111), String.format("app2_%s_%s", refToken, 1111), expire, scope)));
		Assert.assertEquals(2, cache.getUserLoginApp(uid).length);
		cache.remove(uid, appId2);
		Assert.assertEquals(1, cache.getUserLoginApp(uid).length);
		cache.remove(uid, appId1);
		Assert.assertEquals(0, cache.getUserLoginApp(uid).length);
	}

	
	@After
	public void tearDown() throws Exception
	{
		cache.clear();
		cache = null;
	}

	@AfterClass
	public static void tearDownAfterClass()
	{
		System.out.println("Tear down After class");
	}
	
}
