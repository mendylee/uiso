package com.xrk.uiac.bll.test.component;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.xrk.hws.common.logger.Logger;
import com.xrk.uiac.bll.cache.CacheService;
import com.xrk.uiac.bll.cache.component.UserIdentityCache;
import com.xrk.uiac.bll.component.impl.UserAuthorizeSynCompont;
import com.xrk.uiac.bll.entity.UserIdentityEntity;
import com.xrk.uiac.bll.vo.UserVO;
import com.xrk.uiac.dal.Dal;
import com.xrk.uiac.dal.DalTestHelper;

/**
 * 
 * AuthorizeUserCompontTest: 授权缓存同步组件单元测试类
 *
 * <br>=
 * ========================= <br>
 * 公司：广州向日葵信息科技有限公司 <br>
 * 开发：zhub<zhubin@xiangrikui.com> <br>
 * 版本：1.0 <br>
 * 创建时间：2015年4月30日 <br>
 * JDK版本：1.7 <br>=
 * =========================
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({Logger.class})
@PowerMockIgnore("javax.management.*")
public class AuthorizeUserCompontTest
{
	private UserIdentityCache userCache;
	private UserAuthorizeSynCompont userCompont;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{
		PowerMockito.mockStatic(Logger.class);
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
	public void SetUp()
	{
		PowerMockito.mockStatic(Logger.class);
		// 在测试方法运行之前运行
		userCache = (UserIdentityCache) CacheService.GetService(UserIdentityCache.class);	
		userCompont = new UserAuthorizeSynCompont(userCache);
	}
	/**
	 * 
	 * 同步认证用户测试  
	 *
	 */
	@Test
	public void testSynAddAuthroizeUser()
	{
		UserVO userInfoVO= new UserVO();
		userInfoVO.setUid(54321);
		//userInfoVO.setUserName("jerry");
		userInfoVO.setPassword("123456");
		userInfoVO.setStatus(0);
		userCompont.synAddAuthroizeUser(userInfoVO);
		UserIdentityEntity user =  userCache.get(54321);
		//Assert.assertEquals(1, userCache.size());
		Assert.assertNotNull(user);
		Assert.assertEquals("123456",user.getPassword());
		Assert.assertEquals(0,user.getStatus());
	}
	
	/**
	 * 
	 * 修改密码同步测试  
	 *
	 */
	@Test
	public void testSynEditPassWdAuthroizeUser()
	{		
		UserVO userInfoVO= new UserVO();
		userInfoVO.setUid(123);
		//userInfoVO.setUserName("jerry");
		userInfoVO.setPassword("123456");
		userInfoVO.setStatus(0);
		userCompont.synAddAuthroizeUser(userInfoVO);
		//修改密码
		userInfoVO.setPassword("888888");
		userCompont.synEditPassWdAuthroizeUser(userInfoVO);
		UserIdentityEntity user =  userCache.get(123);
		//Assert.assertEquals(1, userCache.size());
		Assert.assertNotNull(user);
		Assert.assertEquals("888888",user.getPassword());
		//Assert.assertEquals(0,user.getStatus());
		
		userInfoVO= new UserVO();
		userInfoVO.setUid(66554);
		//userInfoVO.setUserName("jerry");
		userInfoVO.setPassword("8764342321");
		userInfoVO.setStatus(0);
		userCompont.synEditPassWdAuthroizeUser(userInfoVO);
	}
	
	/**
	 * 
	 * 用户状态变更测试  
	 *
	 */
	@Test
	public void testSynAuthroizeUserStatus()
	{
		UserVO userInfoVO= new UserVO();
		userInfoVO.setUid(123);
		//userInfoVO.setUserName("jerry");
		userInfoVO.setPassword("123456");
		userInfoVO.setStatus(0);
		userCompont.synAddAuthroizeUser(userInfoVO);
		//修改状态
		userInfoVO.setStatus(1);
		userCompont.synAuthroizeUserStatus(userInfoVO);
		userCompont.synEditPassWdAuthroizeUser(userInfoVO);
		UserIdentityEntity user =  userCache.get(123);
		//Assert.assertEquals(1, userCache.size());
		Assert.assertNotNull(user);
		Assert.assertEquals("123456",user.getPassword());
		Assert.assertEquals(1,user.getStatus());
		
		userInfoVO= new UserVO();
		userInfoVO.setUid(2234);
		//userInfoVO.setUserName("jerry");
		userInfoVO.setPassword("123456");
		userInfoVO.setStatus(0);
		userCompont.synAuthroizeUserStatus(userInfoVO);
		
		//查找用户
		UserIdentityEntity tmpUser = userCompont.queryAuthroizeUser(String.valueOf(userInfoVO.getUid()));
		Assert.assertEquals(userInfoVO.getUid(), tmpUser.getUid());
		Assert.assertEquals(userInfoVO.getPassword(), tmpUser.getPassword());
		Assert.assertEquals(userInfoVO.getStatus(), tmpUser.getStatus());
		
		tmpUser = userCompont.queryAuthroizeUser("9988221111111");
		Assert.assertNull(tmpUser);		
	}

}
