package com.xrk.uiac.dal.test.dao;

import static org.junit.Assert.assertEquals;

import java.util.Date;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.xrk.uiac.dal.DalTestHelper;
import com.xrk.uiac.dal.dao.PushObserverDAO;
import com.xrk.uiac.dal.entity.PushObserver;

public class PushObserverDAOTest
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
	public void testInsertPushObserver()
	{
		PushObserver pushObserver = new PushObserver();
		pushObserver.setAppId(123);
		pushObserver.setCallBackUrl("http://www.baidu.com");
		pushObserver.setAddDate(new Date());
		assertEquals(1, PushObserverDAO.insertPushObserver(pushObserver));
	}
	
	@Test
	public void testDeletePushObserver(){
		PushObserver pushObserver = new PushObserver();
		pushObserver.setAppId(3);
		pushObserver.setCallBackUrl("http://www.baidu.com");
		pushObserver.setAddDate(new Date());
		assertEquals(1, PushObserverDAO.insertPushObserver(pushObserver));
		PushObserver findPushObserver = PushObserverDAO.findWithAppId(3);
		assertEquals("http://www.baidu.com", findPushObserver.getCallBackUrl());
		assertEquals(1, PushObserverDAO.deletePushObserver(332));
	}
	

}
