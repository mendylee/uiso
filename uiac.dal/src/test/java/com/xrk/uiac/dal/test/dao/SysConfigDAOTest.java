package com.xrk.uiac.dal.test.dao;

import org.junit.Assert;
import org.junit.Test;

import com.xrk.uiac.dal.DalTestHelper;
import com.xrk.uiac.dal.dao.SysConfigDAO;
import com.xrk.uiac.dal.entity.SysConfig;

/**
 * 
 * SysConfigDAOTest: 系统设置DAO测试类
 *
 * <br>=
 * ========================= <br>
 * 公司：广州向日葵信息科技有限公司 <br>
 * 开发：zhub<zhubin@xiangrikui.com> <br>
 * 版本：1.0 <br>
 * 创建时间：2015年5月13日 <br>
 * JDK版本：1.7 <br>=
 * =========================
 */
public class SysConfigDAOTest
{
	/**
	 * 
	 * 测试查找所有配置项
	 *
	 */
	@Test
	public void testInsertSysConfig()
	{
		DalTestHelper.initDal();
		SysConfig config = new SysConfig();
		config.setItem("time");
		config.setValue("5");
		int result = SysConfigDAO.insertSysConfig(config);
		Assert.assertEquals(1, result);
	}

	/**
	 * 
	 * 测试查找配置项
	 *
	 */
	@Test
	public void testFindConfigValue()
	{
		DalTestHelper.initDal();
		SysConfig config = new SysConfig();
		config.setItem("time");
		config.setValue("5");
		SysConfigDAO.insertSysConfig(config);
		SysConfig result = SysConfigDAO.findConfigValue("time");
		Assert.assertEquals("5", result.getValue());
	}

}
