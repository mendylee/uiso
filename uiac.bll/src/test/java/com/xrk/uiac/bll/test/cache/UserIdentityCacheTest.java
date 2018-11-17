package com.xrk.uiac.bll.test.cache;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
import com.xrk.uiac.bll.cache.SysConfigCache;
import com.xrk.uiac.bll.cache.component.UserIdentityCache;
import com.xrk.uiac.bll.entity.UserIdentityEntity;
import com.xrk.uiac.dal.DalTestHelper;

/**
 * 用户身份信息缓存类单元测试 UserIdentityCacheTest: UserIdentityCacheTest.java.
 *
 * <br>=
 * ========================= <br>
 * 公司：广州向日葵信息科技有限公司 <br>
 * 开发：shunchiguo<shunchiguo@xiangrikui.com> <br>
 * 版本：1.0 <br>
 * 创建时间：2015年4月29日 <br>
 * JDK版本：1.7 <br>=
 * =========================
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({SysConfigCache.class, Logger.class})
public class UserIdentityCacheTest
{

	@BeforeClass
	public static void SetUpClass()
	{
		DalTestHelper.initDal();
	}

	UserIdentityCache _user_cache;

	@Before
	public void SetUp()
	{
		//日志类静态方法Mock
		PowerMockito.mockStatic(Logger.class);
		
		//mock静态方法，以及相应的返回值
		PowerMockito.mockStatic(SysConfigCache.class);
		SysConfigCache sysconfig = mock(SysConfigCache.class);
		
		//内存缓存测试
		when(sysconfig.getCacheClass()).thenReturn("com.xrk.uiac.bll.cache.MemoryCache");
		
		//memcached测试
//		when(sysconfig.getCacheClass()).thenReturn("com.xrk.uiac.bll.cache.external.memcached.MemcachedCache");
//		when(sysconfig.getMemcacheAddr()).thenReturn("192.168.9.109:11211");
		
		//redis测试
//		when(sysconfig.getCacheClass()).thenReturn("com.xrk.uiac.bll.cache.external.redis.RedisCache");
//		when(sysconfig.getRedisAddr()).thenReturn("127.0.0.1:6379");
//		when(sysconfig.isRedisCluster()).thenReturn(false);//集群需要3.0版本才支持
		
		when(SysConfigCache.getInstance()).thenReturn(sysconfig);		
		
		// 在测试方法运行之前运行
		_user_cache = new UserIdentityCache();
	}

	private UserIdentityEntity CreateUser(long uid, int status, String pwd)
	{
		UserIdentityEntity user = new UserIdentityEntity();
		user.setUid(uid);
		user.setPassword(pwd);
		user.setStatus(status);
		return user;
	}

	@Test
	public void testPut()
	{		
		System.out.println("Test UserIdentityCacheTest.Put");
		
		// 测试方法
		long uid = 1111;
		int status = 1;
		String pwd = "test password";

		UserIdentityEntity user = CreateUser(uid, status, pwd);
		// 不同的添加方法，用户应该保持一致
		Assert.assertTrue(_user_cache.put(user));
		Assert.assertTrue(_user_cache.put("2222", user));
		Assert.assertTrue(_user_cache.put("", user));
		Assert.assertEquals(1, _user_cache.size());
		Assert.assertEquals(uid, _user_cache.get(uid).getUid());
		Assert.assertEquals(status, _user_cache.get(uid).getStatus());
		Assert.assertEquals(pwd, _user_cache.get(uid).getPassword());

		// 变更用户实体参数，内容不应该变更
		long uid2 = 2222;
		int status2 = 2;
		String pwd2 = "password 222222";
		user.setUid(uid2);
		user.setStatus(status2);
		user.setPassword(pwd2);

		Assert.assertTrue(_user_cache.put(user));
		Assert.assertEquals(2, _user_cache.size());
		Assert.assertEquals(uid, _user_cache.get(uid).getUid());
		Assert.assertEquals(status, _user_cache.get(uid).getStatus());
		Assert.assertEquals(pwd, _user_cache.get(uid).getPassword());

		Assert.assertEquals(uid2, _user_cache.get(uid2).getUid());
		Assert.assertEquals(status2, _user_cache.get(uid2).getStatus());
		Assert.assertEquals(pwd2, _user_cache.get(uid2).getPassword());

		// 外部对象消毁，不应该影响缓存对象
		user = null;
		Assert.assertEquals(uid, _user_cache.get(uid).getUid());
		Assert.assertEquals(status, _user_cache.get(uid).getStatus());
		Assert.assertEquals(pwd, _user_cache.get(uid).getPassword());

		Assert.assertEquals(uid2, _user_cache.get(uid2).getUid());
		Assert.assertEquals(status2, _user_cache.get(uid2).getStatus());
		Assert.assertEquals(pwd2, _user_cache.get(uid2).getPassword());

		// 新用户对象，替换掉原缓存中的用户对象
		status2 = 5;
		pwd2 = "new edit password";
		UserIdentityEntity user2 = CreateUser(uid2, status2, pwd2);
		Assert.assertTrue(_user_cache.put(user2));
		Assert.assertEquals(2, _user_cache.size());
		Assert.assertEquals(uid2, _user_cache.get(uid2).getUid());
		Assert.assertEquals(status2, _user_cache.get(uid2).getStatus());
		Assert.assertEquals(pwd2, _user_cache.get(uid2).getPassword());

		Assert.assertTrue(_user_cache.put(CreateUser(666, 1, "ssssssssssssssssssss")));
		Assert.assertTrue(_user_cache.put(CreateUser(661, 2, "223sdafsdafasdfasdf")));
		Assert.assertEquals(4, _user_cache.size());

		_user_cache.clear();
		Assert.assertEquals(0, _user_cache.size());		
	}
		
	@Test
	public void testContain()
	{
		System.out.println("Test UserIdentityCacheTest.Contain");
		
		long uid1 = 1111;
		long uid2 = 2222;

		Assert.assertEquals(0, _user_cache.size());
		Assert.assertFalse(_user_cache.contain(uid1));
		Assert.assertFalse(_user_cache.contain(uid2));

		Assert.assertTrue(_user_cache.put(CreateUser(uid1, 1, "ssssssssssssssssssss")));
		Assert.assertTrue(_user_cache.contain(uid1));
		Assert.assertFalse(_user_cache.contain(uid2));

		Assert.assertTrue(_user_cache.put(CreateUser(uid2, 2, "223sdafsdafasdfasdf")));
		Assert.assertTrue(_user_cache.contain(uid1));
		Assert.assertTrue(_user_cache.contain(uid2));

		Assert.assertFalse(_user_cache.contain(12345));
		_user_cache.clear();
	}

	@Test
	public void testGet()
	{
		System.out.println("Test UserIdentityCacheTest.Get");
		
		long uid1 = 1111;
		long uid2 = 2222;

		Assert.assertNull(_user_cache.get(uid1));
		Assert.assertNull(_user_cache.get(uid2));

		Assert.assertTrue(_user_cache.put(CreateUser(uid1, 1, "ssssssssssssssssssss")));
		Assert.assertNotNull(_user_cache.get(uid1));
		Assert.assertNull(_user_cache.get(uid2));

		Assert.assertTrue(_user_cache.put(CreateUser(uid2, 2, "223sdafsdafasdfasdf")));
		Assert.assertNotNull(_user_cache.get(uid1));
		Assert.assertNotNull(_user_cache.get(uid2));

		Assert.assertNull(_user_cache.get(12345));
		_user_cache.clear();
	}

	@Test
	public void testRemove()
	{
		System.out.println("Test UserIdentityCacheTest.Remove");
		
		long uid1 = 1111;
		long uid2 = 2222;

		Assert.assertNull(_user_cache.get(uid1));
		Assert.assertNull(_user_cache.get(uid2));

		Assert.assertTrue(_user_cache.put(CreateUser(uid1, 1, "ssssssssssssssssssss")));
		Assert.assertNotNull(_user_cache.get(uid1));
		Assert.assertNull(_user_cache.get(uid2));

		Assert.assertTrue(_user_cache.put(CreateUser(uid2, 2, "223sdafsdafasdfasdf")));
		Assert.assertNotNull(_user_cache.get(uid1));
		Assert.assertNotNull(_user_cache.get(uid2));

		Assert.assertTrue(_user_cache.remove(uid1));
		Assert.assertNull(_user_cache.get(uid1));
		Assert.assertNotNull(_user_cache.get(uid2));

		Assert.assertTrue(_user_cache.remove(uid2));
		Assert.assertNull(_user_cache.get(uid1));
		Assert.assertNull(_user_cache.get(uid2));
	}

	//@Ignore
	@Test
	public void testSize()
	{
		System.out.println("Test UserIdentityCacheTest.Size");
		
		int total = 1000;
		Assert.assertEquals(0, _user_cache.size());
		
		for(int i=0; i<total; i++)
		{
			Assert.assertTrue(_user_cache.put(CreateUser(i, i, String.format("password %s", i))));
		}
		Assert.assertEquals(total, _user_cache.size());
		
		Assert.assertTrue(_user_cache.put(CreateUser(1000022, 2, "223sdafsdafasdfasdf")));
		Assert.assertEquals(total+1, _user_cache.size());
				
		for(int i=0; i<total; i++)
		{
			Assert.assertTrue(_user_cache.remove(i));
		}
		Assert.assertEquals(1, _user_cache.size());
		Assert.assertTrue(_user_cache.remove(1000022));
		Assert.assertEquals(0, _user_cache.size());
	}

	@After
	public void tearDown() throws Exception
	{
		_user_cache.clear();
		_user_cache = null;
	}

	@AfterClass
	public static void tearDownAfterClass()
	{
		System.out.println("Tear down After class");
	}
}
