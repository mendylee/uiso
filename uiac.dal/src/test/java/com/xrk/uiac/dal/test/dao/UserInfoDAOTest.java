package com.xrk.uiac.dal.test.dao;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.xrk.uiac.dal.DalTestHelper;
import com.xrk.uiac.dal.dao.UserInfoDAO;
import com.xrk.uiac.dal.entity.UserInfo;

@Ignore
public class UserInfoDAOTest
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
	public void test()
	{
		
		UserInfo ui = new UserInfo();
		ui.setUserName("fuckdd");
		assertEquals(0, UserInfoDAO.insert(ui));
	}

}
