package com.xrk.uiac.common.test.utils;

import org.junit.Test;

import com.xrk.uiac.common.utils.BeanCopierUtils;

import junit.framework.TestCase;
/**
 * 
 * BeanCoiper单元测试类
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：lijingping<lijingping@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2018年11月05日
 * <br> JDK版本：1.8
 * <br>==========================
 */
public class BeanCopierUtilTest extends TestCase
{

	
	@Test
	public void testCopy(){
		User user = new User();
		user.setUserName("zhangsan");
		user.setAge(22);
		user.setPasswd("123456");
		
		AuthroizeUser targetUser = new AuthroizeUser();
		BeanCopierUtils.copy(user, targetUser);
		assertEquals(user.getUserName(), targetUser.getUserName());
		assertNull(targetUser.getPassword());//属性名称不匹配
	}
}
