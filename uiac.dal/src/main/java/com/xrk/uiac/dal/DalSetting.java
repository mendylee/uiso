package com.xrk.uiac.dal;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.xrk.hws.common.logger.Logger;

/**
 * 
 * 从app.properties里读取配置项并初始化
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：yexx<yexiaoxiao@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年8月21日 上午11:04:36
 * <br> JDK版本：1.8
 * <br>==========================
 */
public class DalSetting
{
	//键名
	public static final String KEY_DBTYPE = "db.type";
	public static final String KEY_DBNAME = "db.name";
	public static final String KEY_SERVER1_HOST = "server1.host";
	public static final String KEY_SERVER1_PORT = "server1.port";
	public static final String KEY_SERVER2_HOST = "server2.host";
	public static final String KEY_SERVER2_PORT = "server2.port";
	public static final String KEY_USER = "user";
	public static final String KEY_PASSWORD = "password";
	
	public static final int DBTYPE_MONGO = 1;
	public static final int DBTYPE_MYSQL = 3;
	public static final int DBTYPE_PG = 2;
	
	public static final String DBTYPE_NAME_MONGO = "mongo";
	public static final String DBTYPE_NAME_PG = "pg";
	public static final String DBTYPE_NAME_MYSQL = "mysql";
	
	public static final String UNIT_TEST = "unittest";
	
	private static Properties properties;
	private static String dbName;
	private static int dbType;
	
	public static void init(InputStream in)
	{
		properties = new Properties();
		try 
		{
	        properties.load(in);
        }
        catch (IOException e) 
		{
        	Logger.error("Failed to load app.properties, msg: %s", e.getMessage());
        	return;
        }
		
		dbType = getDbType();
		
		if (dbType == DBTYPE_MONGO)
		{
			dbName = DBTYPE_NAME_MONGO;
		}
		else if (dbType == DBTYPE_MYSQL)
		{
			dbName = DBTYPE_NAME_MYSQL;
		}
		else if (dbType == DBTYPE_PG)
		{
			dbName = DBTYPE_NAME_PG;
		}
		else
		{
			Logger.error("db type is invalid !!!");
		}
		
		if(properties.getProperty(UNIT_TEST, "") != ""){
			dbName += "_test";
		}
	}
	
	/**
	 * 
	 * 数据库类型的名字，比如mongo、pg、mysql 
	 *    
	 * @return
	 */
	public static String getDbTypeName()
	{
		return dbName;
	}
	
	/**
	 * 
	 * 数据库类型，1-mongo，2-pg，3-mysql 
	 *    
	 * @return
	 */
	public static int getDbType()
	{
		return Integer.parseInt(properties.getProperty(KEY_DBTYPE, "0"));
	}
	
	/**
	 * 
	 * 数据库的库名，默认是uiac
	 *    
	 * @return
	 */
	public static String getDbName()
	{
		return properties.getProperty(KEY_DBNAME, "uiac");
	}
	
	/**
	 * 
	 * 数据库的用户名
	 *    
	 * @return
	 */
	public static String getUser()
	{
		return properties.getProperty(KEY_USER);
	}
	
	/**
	 * 
	 * 数据库的密码  
	 *    
	 * @return
	 */
	public static String getPassword()
	{
		return properties.getProperty(KEY_PASSWORD);
	}
	
	/**
	 * 
	 * 数据库的地址，默认是127.0.0.1
	 *    
	 * @return
	 */
	public static String getServer1Host()
	{
		return properties.getProperty(KEY_SERVER1_HOST, "127.0.0.1");
	}
	
	/**
	 * 
	 * 数据库的端口
	 *    
	 * @return
	 */
	public static String getServer1Port()
	{
		return properties.getProperty(KEY_SERVER1_PORT);
	}
	
	/**
	 * 
	 * 数据库的地址，默认是127.0.0.1
	 *    
	 * @return
	 */
	public static String getServer2Host()
	{
		return properties.getProperty(KEY_SERVER2_HOST, "127.0.0.1");
	}
	
	/**
	 * 
	 * 数据库的端口
	 *    
	 * @return
	 */
	public static String getServer2Port()
	{
		return properties.getProperty(KEY_SERVER2_PORT);
	}
}
