package com.xrk.uiac.bll.test.cache;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.Random;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.xrk.uiac.bll.cache.component.UserInfoCache;
import com.xrk.uiac.bll.vo.UserVO;
import com.xrk.uiac.common.utils.BeanCopierUtils;
import com.xrk.uiac.dal.Dal;
import com.xrk.uiac.dal.entity.UserInfo;

public class UserInfoCacheTest
{
//	private static final int[] MOBILE_PREF = {130, 131, 132, 133, 134, 135, 136, 137, 138, 150, 155, 158, 180, 188, 189, 186};
//	private static final String POSTCODE = "xrk";
//
//	UserInfoCache uiCache;
//	
//	@BeforeClass
//	public static void setUpBeforeClass() throws Exception
//	{
//	}
//
//	@AfterClass
//	public static void tearDownAfterClass() throws Exception
//	{
//		//所有测试结束后清空所有测试数据
//		clearAllTestData();
//	}
//
//	@Before
//	public void setUp() throws Exception
//	{
//		//每一项测试开始之前清空所有测试数据
//		clearAllTestData();
//		uiCache = new UserInfoCache();
//	}
//
//	@After
//	public void tearDown() throws Exception
//	{
//		
//	}
//
//	@Test
//	public void testGetObject()
//	{
//		final String dbusername = "dbusername1";
//		final String cacheusername = "cacheusername1";
//
//		final long uid1 = 10000001;
//		final long uid2 = 10000002;
//		final long nouid3 = 10000003;
//		
//		UserInfo userifo1 = new UserInfo();
//		userifo1 = getRandomUser(uid1);
//		userifo1.setUserName(dbusername);
//		//assertEquals(0, Dal.insert(UserInfo.class, "insertone", userifo1, true, null));
//		
//		UserInfo userinfo2 = new UserInfo();
//		userinfo2 = getRandomUser(uid2);
//		userinfo2.setUserName(cacheusername);
//		//assertEquals(0, Dal.insert(UserInfo.class, "insertone", userinfo2, true, null));
//	
//		//assertEquals(2, Dal.count(UserInfo.class, "countalltestdata", new Object[0]));
//		
//		UserInfoVO uivo2 = new UserInfoVO();
//		BeanCopierUtils.copy(userinfo2, uivo2);
//		assertTrue(uiCache.put(uid2, uivo2));
//		uiCache.put(uid2, uivo2);
//		
//		//uivo2存在于缓存中，直接取出
//		uivo2 = null;
//		uivo2 = uiCache.get(uid2);
//		assertNotNull(uivo2);
//		assertEquals(cacheusername, uivo2.getUserName());
//		assertEquals(uid2, uivo2.getUid());
//		//不影响数据库
//		//assertEquals(2, Dal.count(UserInfo.class, "countalltestdata", new Object[0]));
//		
//		//uivo1不存在于缓存中，转而从数据库取出，并写入缓存
//		UserInfoVO uivo1 = null;
//		uivo1 = uiCache.get(uid1);
//		assertNotNull(uivo1);
//		assertEquals(dbusername, uivo1.getUserName());
//		assertEquals(uid1, uivo1.getUid());
//		//同时已经写入缓存
//		assertTrue(uiCache.contain(uid1));
//		//不影响数据库
//		//assertEquals(2, Dal.count(UserInfo.class, "countalltestdata", new Object[0]));
//		
//		//uivo3不存在于缓存中，获取该值时返回false，且不影响缓存和数据库
//		UserInfoVO uivo3 = null;
//		uivo3 = uiCache.get(nouid3);
//		assertNull(uivo3);
//		//不影响缓存
//		assertFalse(uiCache.contain(nouid3));
//		//不影响数据库
//		//assertEquals(2, Dal.count(UserInfo.class, "countalltestdata", new Object[0]));
//	}
//	
//	@Test
//	public void testPutObject()
//	{
//		final String username1 = "testputuser1";
//		final String username2 = "testputuser2";
//		final String username3 = "testputuser3";
//		
//		final long uid1 = 10000001;
//		final long uid2 = 10000002;
//		final long uid3 = 10000003;
//		
//		UserInfoVO uivo1 = new UserInfoVO();
//		uivo1.setUid(uid1);
//		uivo1.setUserName(username1);
//		
//		//缓存内部不存在该对象时, 正常插入
//		assertFalse(uiCache.contain(uid1));
//		assertTrue(uiCache.put(uid1, uivo1));
//		uivo1 = null;
//		uivo1 = uiCache.get(uid1);
//		assertEquals(uid1, uivo1.getUid());
//		assertEquals(username1, uivo1.getUserName());
//		
//		//缓存内部存在该对象时，更新对象
//		uivo1.setUserName(username3);
//		assertTrue(uiCache.put(uid1, uivo1));
//		uivo1 = null;
//		uivo1 = uiCache.get(uid1);
//		assertEquals(uid1, uivo1.getUid());
//		assertEquals(username3, uivo1.getUserName());
//		
//		//不可自定义key，默认用uid做key
//		UserInfoVO uivo2 = new UserInfoVO();
//		uivo2.setUid(uid2);
//		uivo2.setUserName(username2);
//		assertFalse(uiCache.contain(uid2));
//		assertTrue(uiCache.put(username2, uivo2));
//		assertFalse(uiCache.contain(username2));
//		assertTrue(uiCache.contain(uid2));
//		
//		//当value为null时，返回false
//		UserInfoVO uivo3 = null;
//		assertFalse(uiCache.put(uid3, uivo3));
//		assertFalse(uiCache.contain(uid3));
//	}
//
//	public static UserInfo getRandomUser(long uid)
//	{
//		UserInfo ui = new UserInfo();
//		int prefCount = MOBILE_PREF.length;
//		
//		ui.setUid(uid);
//		
//		String mobile = String.format(
//				"%s%s", 
//				String.valueOf(MOBILE_PREF[(int) Math.floor(Math.random() * prefCount)]),
//				String.valueOf((long) (Math.random() * 90000000l + 10000000l)));
//		ui.setMobile(mobile);
//		
//		ui.setEditDate(new Date(System.currentTimeMillis()));
//		
//		ui.setUserName(getRandomString());
//		
//		ui.setQq(String.valueOf((long) (Math.random() * 900000000l + 100000)));
//		
//		//为了方便清空数据库里的测试数据，增加一个所有对象的值都相等的字段
//		ui.setPostcode("xrk");
//		
//		return ui;
//	}
//	
//	public static String getRandomString() 
//	{
//		int length = (int) (Math.random() * 10 + 6);
//		final String base = "abcdefghijklmnopqrstuvwxyz0123456789"; 
//		Random random = new Random(); 
//		StringBuffer sb = new StringBuffer(); 
//		for (int i = 0; i < length; i++) { 
//			int number = random.nextInt(base.length()); 
//			sb.append(base.charAt(number)); 
//		} 
//		return sb.toString(); 
//	}
//	
//	public static void clearAllTestData()
//	{
//		Object[] qp = new Object[1];
//		qp[0] = POSTCODE;
//		//Dal.delete(UserInfo.class, "deletewithpostcode", qp);
//	}
}
