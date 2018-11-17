package com.xrk.uiac.dal.test.dao;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.xrk.uiac.dal.DalTestHelper;
import com.xrk.uiac.dal.dao.AppSysConfigDAO;
import com.xrk.uiac.dal.entity.AppSysConfig;
import com.xrk.uiac.dal.entity.SysConfig;

/**
 * 
 * SysConfigDAOTest: 系统设置DAO测试类
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：zhub<zhubin@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年5月13日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public class AppSysConfigDAOTest
{
	/**
	 * 
	 * 测试查找所有配置项
	 *
	 */
	@Test
	public void testFindSysConfig(){
		DalTestHelper.initDal();
		AppSysConfig config = new AppSysConfig();
		config.setAppId(2);
		config.setItem("time");
		config.setValue("5");
		AppSysConfigDAO.insertSysConfig(config);
		List<AppSysConfig> list =  AppSysConfigDAO.findSysConfig();
		SysConfig config1 = new SysConfig();
		config.setAppId(3);
		config1.setItem("34");
		config1.setValue("5");
		System.out.println("testFindSysConfig:listSize="+list.size());
		for (AppSysConfig appSysConfig : list) {
	        System.out.println(appSysConfig.getItem());
	        System.out.println(appSysConfig.getValue());
        }
		//Assert.assertEquals(2, list.size());
	}
	/**
	 * 
	 * 测试查找配置项
	 *
	 */
	@Test
	public void testFindConfigValue(){
		DalTestHelper.initDal();
		AppSysConfig config = new AppSysConfig();
		config.setAppId(2);
		config.setItem("time");
		config.setValue("6");
		AppSysConfigDAO.insertSysConfig(config);
		AppSysConfig result = AppSysConfigDAO.findConfigValue(2,"time");
		Assert.assertEquals("6", result.getValue());
		Assert.assertEquals("time", result.getItem());
	}
	

	

}
