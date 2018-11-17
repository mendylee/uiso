package com.xrk.uiac.bll.test.common;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.xrk.uiac.bll.cache.CacheService;
import com.xrk.uiac.bll.common.SeqUtils;
import com.xrk.uiac.dal.Dal;
import com.xrk.uiac.dal.DalTestHelper;

public class SeqUtilsTest
{

	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{
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
	}

	@After
	public void tearDown() throws Exception
	{
	}

	@Test
	public void testGetUid()
	{
	}
}
