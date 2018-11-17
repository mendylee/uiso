package com.xrk.uiac.bll.test.cache;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.esotericsoftware.minlog.Log.Logger;
import com.xrk.uiac.dal.DalTestHelper;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Logger.class})
public class AppInfoCacheTest
{
	//private AppInfoCache cache = (AppInfoCache) CacheService.GetService(AppInfoCache.class);
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{
		PowerMockito.mockStatic(Logger.class);
		DalTestHelper.initDal();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception
	{
	}

	@Before
	public void setUp() throws Exception
	{
	}

	@After
	public void tearDown() throws Exception
	{
	}

	@Test
	public void testGet()
	{
//		UserComponent userComponent = new UserComponent();
//		try
//		{
//			for (int i=0; i<1000; i++)
//			{
//				userComponent.getUserInfo(2, "token", 100000008);
//			}
//		}
//		catch (Exception e)
//		{
//			fail("");
//		}
	}

	@Test
	public void testPut()
	{
		
	}

}
