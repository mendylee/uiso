package com.xrk.uiac.bll.common;

import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.google.common.base.Strings;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.xrk.hws.common.logger.Logger;
import com.xrk.uiac.common.utils.PgConnectionPool;
import com.xrk.uiac.dal.DalSetting;

/**
 * 
 * 利用数据库维护自增长id的工具类
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：yexx<yexiaoxiao@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年7月29日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public class SeqUtils
{
	private static PgConnectionPool pgPool = null;
	private static MongoClient mongoClient = null;
	
	private static int dbType;
	private static String dbName;
	
	//uiac_seq表相关常量
	private static final String SEQ_TABLE_NAME = "uiac_seq";
	private static final String SEQ_SID_FEILD_NAME = "sid";
	private static final String SEQ_TABNAME_FEILD_NAME = "tab_name";
	
	//需要SeqUtils维护原子自增长Id的数据表的名称
	private static final String TABLE_USER = "uiac_user";
	private static final String TABLE_ACCOUNT_CHANGE_RECORD = "uiac_account_change_record";
	//private static final String TABLE_PASSWORD_PROTECTION = "uiac_password_protection";
	private static final String TABLE_PASSWORD_QUESTION = "uiac_password_question";
	private static final String TABLE_PUSH_OBSERVER = "uiac_push_observer";
	private static final String TABLE_APP_INFO_EXTEND = "uiac_app_info_extend";
	private static final String TABLE_USER_SUB_ACCOUNT = "uiac_user_sub_account";
	
	private static final int SID_MAX_RETRYING_COUNT = 5;
	private static final int SID_RETRYING_WAITING_TIME = 50;

	static
	{
		init();
	}
	
	public static long getUid()
	{
		return getIncId(TABLE_USER);
	}
	
	public static long getAccountChangeSerialId()
	{
		return getIncId(TABLE_ACCOUNT_CHANGE_RECORD);
	}
	
	public static long getQuestionId()
	{
		return getIncId(TABLE_PASSWORD_QUESTION);
	}
	
	public static long getPushObserverSerialId()
	{
		return getIncId(TABLE_PUSH_OBSERVER);
	}
	
	public static long getSubAccountId()
	{
		return getIncId(TABLE_USER_SUB_ACCOUNT);
	}
	
	public static int getAppInfoExtendSerialId()
	{
		return (int) getIncId(TABLE_APP_INFO_EXTEND);
	}
	
	private static long getIncId(String tabName)
	{
		long seqId = getIncIdFromDb(tabName);
		if (seqId != -1)
		{
			return seqId;
		}
		else
		{
			int time = 1;
			while (time <= SID_MAX_RETRYING_COUNT)
			{
				//测试日志代码，打印出重试次数
				Logger.info("SeqUtils, retrying, time: %d", time);
				
				waiting(SID_RETRYING_WAITING_TIME);
				seqId = getIncIdFromDb(tabName);
				if (seqId != -1)
				{
					return seqId;
				}
				time ++;
			}
			
			//重试5次仍然失败
			Logger.error("SeqUtils, fail to generate id, table name : %s", tabName);
			return -1;
		}
	}
	
	private static long getIncIdFromDb(String tabName)
	{
		long id = -1;
		switch (dbType)
		{
			case DalSetting.DBTYPE_MONGO:
				id = getIncIdFromMongo(tabName);
				break;
			case DalSetting.DBTYPE_MYSQL:
				id = getIncIdFromMysql(tabName);
				break;
			case DalSetting.DBTYPE_PG:
				id = getIncIdFromPg(tabName);
				break;
			default:
				Logger.error("fail to get Inc id, db type is error, db type: %d", dbType);
		}
		return id;
	}
		
	private static long getIncIdFromMongo(String tabName)
	{
		DBObject query = new BasicDBObject();
		DBObject update = new BasicDBObject();
		DBObject fields = new BasicDBObject();
		DBObject result = new BasicDBObject();
		DBObject inc = new BasicDBObject();
		
		inc.put(SEQ_SID_FEILD_NAME, 1);
		fields.put(SEQ_SID_FEILD_NAME, 1);
		query.put(SEQ_TABNAME_FEILD_NAME, tabName);
		update.put("$inc", inc);
		
		//调用findAndModify参数，对指定的记录里的id值进行原子增长
		try
		{
			result = mongoClient
					.getDB(dbName)
					.getCollection(SEQ_TABLE_NAME)
					.findAndModify(query, fields, null, false, update, true, true);
		}
		catch (Exception e)
		{
			Logger.error("Failed to get inc id from mongo, db error, tabName: %s, msg: %s", tabName, e.getMessage());
			return -1;
		}
		
		if (result == null || !result.containsField(SEQ_SID_FEILD_NAME))
		{
			Logger.error("Failed to get inc id from mongo, result is error, tabName: %s", tabName);
			return -1;
		}
		
		return Long.valueOf(result.get(SEQ_SID_FEILD_NAME).toString());
		//return -1;
	}
	
	private static long getIncIdFromMysql(String tabName)
	{
		return -1;
	}
	
	private static long getIncIdFromPg(String tabName)
	{
		long id = -1;
		
		Connection conn = null;
		try
		{
			conn = pgPool.getConnection();
		}
		catch (SQLException e)
		{
			Logger.error("seqUtils, fail to get connection from pg pool, msg: %s", e.getMessage());
			return id;
		}
		
		String getSql = String.format("select %s from %s where %s='%s'", 
				SEQ_SID_FEILD_NAME, 
				SEQ_TABLE_NAME,
				SEQ_TABNAME_FEILD_NAME,
				tabName);
		String upSql = String.format("update %s set %s = (%s+1) where %s='%s'", 
				SEQ_TABLE_NAME,
				SEQ_SID_FEILD_NAME,
				SEQ_SID_FEILD_NAME,
				SEQ_TABNAME_FEILD_NAME,
				tabName);
		 
        ResultSet rs = null;
        PreparedStatement ps = null;
        
        try 
        {
        	//执行事务
        	//执行事务之前必须设置为手动提交
        	conn.setAutoCommit(false);
        	ps = conn.prepareStatement(upSql);
        	ps.executeUpdate();
        	ps = conn.prepareStatement(getSql);
        	rs = ps.executeQuery();
        	conn.commit();
        	
			if (rs.next())
	        {
	        	id = rs.getLong(1);
	        }
		}
		catch (SQLException e)
        {
			Logger.error("seqUtils, fail to excute sql, transaction error, msg: %s", e.getMessage());
			
			//回滚
			try
			{
				conn.rollback();
			}
			catch (SQLException rbe)
			{
				Logger.error("seqUtils, fail to rollback, msg: %s", rbe.getMessage());
			}
		}
        finally
        {	
        	try
        	{
        		//重新设置为自动提交
        		conn.setAutoCommit(true);
        	}
        	catch (SQLException e)
        	{
        		Logger.error("seqUtils, fail to set autocommit, msg: %s", e.getMessage());
        	}
        	
        	//每次使用完一定要返还连接
        	//否则连接很快会被全部占用
        	pgPool.returnConnection(conn);
		}
		
		return id;
	}
	
	private static void init()
	{
		//先获取数据库类型
		dbType = DalSetting.getDbType();
		//获取数据库的库名，默认uiac
		dbName = DalSetting.getDbName();
		
		switch (dbType)
		{
			case DalSetting.DBTYPE_MONGO:
				initMongoClient();
				break;
			case DalSetting.DBTYPE_MYSQL:
				initMysqlClient();
				break;
			case DalSetting.DBTYPE_PG:
				initPgClient();
				break;
			default:
				Logger.error("fail to init db, db type is error, db type: %d", dbType);
		}
	}
	
	private static void initMysqlClient()
	{
		//
		
		return;
	}
	
	private static void initPgClient()
	{
		//url的后缀-pg为测试阶段临时加上。
		Logger.info("seqUtils, init pg start...");
		
		String url = String.format("jdbc:postgresql://%s:%s/%s", DalSetting.getServer1Host(), DalSetting.getServer1Port(), dbName);
		try
		{
			pgPool = new PgConnectionPool(url, DalSetting.getUser(), DalSetting.getPassword());
		}
		catch (Exception e)
		{
			Logger.error("seqUtils, fail to init pg connection pool!!! msg: %s", e.getMessage());
			return;
		}
		
		if (pgPool == null)
		{
			Logger.error("seqUtils, fail to init pg connection pool!!!");
		}
		
		Logger.info("seqUtils, init pg end...");
	}
	
	private static void initMongoClient()
	{
		try
        {
	        mongoClient = new MongoClient(new MongoClientURI(getMongoUrl()));
        }
        catch (UnknownHostException e)
        {
        	Logger.error("Failed to get mongoClient instance, msg: %s", e.getMessage());
        }
	}
	
	private static String getMongoUrl() 
	{
		StringBuffer mongoUrl = new StringBuffer();

		mongoUrl.append("mongodb://");
		if (!Strings.isNullOrEmpty(DalSetting.getUser()))
		{
			mongoUrl.append(DalSetting.getUser());
			mongoUrl.append(":");
			mongoUrl.append(DalSetting.getPassword());
			mongoUrl.append("@");
		}
		mongoUrl.append(DalSetting.getServer1Host());
		mongoUrl.append(":");
		mongoUrl.append(DalSetting.getServer1Port());
		if (!Strings.isNullOrEmpty(DalSetting.getServer2Host()))
		{
			mongoUrl.append(",");
			mongoUrl.append(DalSetting.getServer2Host());
			mongoUrl.append(":");
			mongoUrl.append(DalSetting.getServer2Port());
		}
		mongoUrl.append("/");
		mongoUrl.append(DalSetting.getDbName());
		
		return mongoUrl.toString();
	}
	
	private static boolean waiting(int ms)
	{
		try
		{
			Thread.sleep(ms);
			return true;
		}
		catch (InterruptedException e)
		{
			Logger.error("seqUtils, fail to sleep, ms: %d, msg: %s", ms, e.getMessage());
			return false;
		}
	}
}