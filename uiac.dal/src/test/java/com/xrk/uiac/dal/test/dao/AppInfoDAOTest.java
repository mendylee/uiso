package com.xrk.uiac.dal.test.dao;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.xrk.uiac.dal.DalTestHelper;
import com.xrk.uiac.dal.dao.AppInfoDAO;
import com.xrk.uiac.dal.entity.AppInfo;

@Ignore
public class AppInfoDAOTest
{

	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{
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
	public void testInsert()
	{
//		AppInfo appInfo = new AppInfo();
//		appInfo.setAppId(6);
//		appInfo.setAppName("后台系统");
//		appInfo.setIsDel(0);
//		appInfo.setAddDate(new Date());
//		appInfo.setIsThirdparty(0);
//		
//		assertEquals(0, AppInfoDAO.insert(appInfo));
	}

	@Test
	public void testFindWithAppId()
	{
		AppInfo appInfo = null;
		appInfo = AppInfoDAO.findWithAppId(1);
		assertEquals(1, appInfo.getAppId());
		assertEquals("微信", appInfo.getAppName());
	}

}
