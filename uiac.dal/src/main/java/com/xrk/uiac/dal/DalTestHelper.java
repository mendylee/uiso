package com.xrk.uiac.dal;

import java.io.InputStream;
import java.io.StringBufferInputStream;


/**
 * 
 * Dal测试帮助类，单机测试时，可通过该类初始化Dal
 * 
 * 单机测试时，调用Dal.init()方法时需要传入InputStream，该类用String构造InputStream传入Dal
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：yexx<yexiaoxiao@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年8月21日 上午11:49:05
 * <br> JDK版本：1.8
 * <br>==========================
 */
public class DalTestHelper
{
	private static String baseConfig = "db.type=%d\r\nhost=%s\r\nport=%s\r\nuser=%s\r\npassword=%s\r\ndb.name=%s\r\nunittest=true";
	
	public static void initDal()
	{
		InputStream in = new StringBufferInputStream(getMongoConfig());
		Dal.init(in);
	}
	
	public static String getPgConfig()
	{
		return String.format(baseConfig, 2, "127.0.0.1", "5432", "postgres", "20130103", "uiac");
	}
	
	public static String getMongoConfig()
	{
		return String.format(baseConfig, 1, "192.168.9.16", "27017", "", "", "uiac");
	}
	
	public static String getMysqlConfig()
	{
		return String.format(baseConfig, 2, "127.0.0.1", "3306", "root", "20130103", "uiac");
	}
}
