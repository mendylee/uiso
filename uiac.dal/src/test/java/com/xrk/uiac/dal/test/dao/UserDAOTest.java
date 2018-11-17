package com.xrk.uiac.dal.test.dao;

import static org.junit.Assert.assertEquals;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.xrk.uiac.dal.Dal;
import com.xrk.uiac.dal.DalTestHelper;
import com.xrk.uiac.dal.dao.UserDAO;
import com.xrk.uiac.dal.dao.UserExtendInfoDAO;
import com.xrk.uiac.dal.dao.UserInfoDAO;
import com.xrk.uiac.dal.entity.User;
import com.xrk.uiac.dal.entity.UserExtendInfo;
import com.xrk.uiac.dal.entity.UserInfo;

public class UserDAOTest
{
	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{
		DalTestHelper.initDal();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception
	{
		Dal.dispose();
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
	public void testBusinessLock()
	{
		run();
	}
	
	private void run()
	{
		List<User> list = UserDAO.findAll();
		long max = 0;
		long min = 19999999999l;
		int adminCount = 0;
		for (User u : list)
		{
			long mobile = Long.valueOf(u.getAccount()).longValue();
			if (mobile > max && mobile != 17900000000l)
			{
				max = mobile;
			}
			if (mobile < min && mobile != 17900000000l)
			{
				min = mobile;
			}
			if (mobile == 17900000000l)
			{
				adminCount ++;
			}
		}
		
		System.out.println("min: " + min);
		System.out.println("max: " + max);
		System.out.println("adminCount: " + adminCount);
	}

	@Ignore
	@Test
	public void testInsert()
	{
		User u = new User();
		u.setAccount("account");
		u.setPassword("aaaaaaaabbbbbbbbccccccccdddddddd");
		u.setIsDel(0);
		u.setStatus(0);
		u.setUid(1000);
		Date date = new Date();
		Timestamp timeStamp = new Timestamp(date.getTime());
		u.setAddDate(timeStamp);
		UserDAO.insertUser(u);
		
		u = null;
		u = UserDAO.findWithUid(1000);
		
		UserExtendInfo uei =  new UserExtendInfo();
		uei.setExtKey("key");
		uei.setExtValue("value");
		uei.setUid(1000);
		List<UserExtendInfo> ueiList = new ArrayList<UserExtendInfo>();
		ueiList.add(uei);
		UserExtendInfoDAO.insertList(ueiList);
		
		ueiList = null;
		uei = null;
		try
		{
			ueiList = UserExtendInfoDAO.findListWithUid(1000);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		if (UserDAO.testUpdate(1000, "new_account") == 0)
		{
			System.out.println("fail to update");
		}
		
		UserExtendInfoDAO.deleteWithUid(1000);
		UserDAO.deleteUser(1000);
	}
	
	@Ignore
	@Test
	public void synUserAccount()
	{
		User u = new User();
		u.setAccount("account@sina.com");
		u.setPassword("aaaaaaaabbbbbbbbccccccccdddddddd");
		u.setIsDel(0);
		u.setStatus(0);
		u.setUid(1000);
		Date date = new Date();
		Timestamp timeStamp = new Timestamp(date.getTime());
		u.setAddDate(timeStamp);
		UserInfo userInfo = new UserInfo();
		userInfo.setUid(1000);
		UserDAO.insertUser(u);
		UserInfoDAO.insert(userInfo);
		UserDAO.updateUserAccount(u.getUid(), "13800138000");
		UserInfoDAO.updateWithMobile(userInfo.getUid(), "13800138000");
		u = UserDAO.findWithUid(1000);
		userInfo=UserInfoDAO.findWithUid(userInfo.getUid());
		assertEquals("13800138000", u.getAccount());
		assertEquals("13800138000", userInfo.getMobile());
	}
}