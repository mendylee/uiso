package com.xrk.uiac.bll.cache;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.xrk.hws.common.logger.Logger;
import com.xrk.uiac.dal.dao.SysConfigDAO;
import com.xrk.uiac.dal.entity.SysConfig;

/**
 * 
 * 系统配置项缓存
 * 由于系统配置项的特殊性，系统配置应该在所有缓存类实例化之前被初始化。
 * 所以该类并没有存在component包里面，不被CacheService所保存并统一初始化
 * 
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：yexx<yexiaoxiao@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年9月29日
 * <br> JDK版本：1.8
 * <br>==========================
 */
public class SysConfigCache
{
	private volatile static SysConfigCache instance;

	private Map<String, String> configMap = null;
	boolean isLoad = false;

	private SysConfigCache()
	{
		configMap = new HashMap<>();
		init();
	}
	
	public void reset()
	{
		configMap.clear();
		init();
		Logger.info("reset sysconfig cache");
	}

	public static SysConfigCache getInstance()
	{
		if (instance == null)
		{
			synchronized (SysConfigCache.class)
			{
				if(instance == null)
				{
					instance = new SysConfigCache();
				}
            }			
		}
		return instance;
	}

	/**
	 * 
	 * 初始化cache *
	 */
	private void init()
	{
		Logger.info("load sysconfig start...");
		try
		{
			List<SysConfig> configList = SysConfigDAO.findSysConfig();
			if (configList != null) {
				for (SysConfig sysConfig : configList) {
					configMap.put(sysConfig.getItem(), sysConfig.getValue());
					Logger.debug("add cache: Key = %s, Value = %s", sysConfig.getItem(), sysConfig.getValue());
				}
				Logger.info("load sysconfig end... total=" + configList.size());
			}
			isLoad = true;
		}
		catch(Exception ex){
			Logger.error(ex, "load sysconfig error : %s", ex.getMessage());
			throw ex;
		}
	}

	/**
	 * 
	 * 根据系统配置项找到value
	 * 
	 * @param 系统配置项
	 * @return
	 */
	public String getValue(String item)
	{
		if (!isLoad)
		{
			init();
		}
		
		return configMap.get(item);
	}

	
	public long getAccessTokenExpireTime()
	{
		long expireTime = 20;// 默认为20分钟过期
		String val = getValue("ACCESS_TOKEN_EXPIRE_TIME");
		if (val != null && !val.isEmpty())
		{
			try 
			{
				expireTime = Integer.parseInt(val);
			}
			catch (Exception e) 
			{
				e.printStackTrace();
			}
		}
		// 单位默认为分钟，对外返回为毫秒格式
		return expireTime * 1000 * 60;
	}

	public int getHttpPort()
    {
		int httpPort = 8081;
		String val = getValue("HTTP_LISTEN_PORT");
		if (val != null && !val.isEmpty()) 
		{
			try 
			{
				httpPort = Integer.parseInt(val);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		return httpPort;
    }

	public String getCacheClass()
    {
		String val = getValue("CACHE_CLASS");
		if (val == null || val.isEmpty())
		{
			val = "com.xrk.uiac.bll.cache.MemoryCache";
		}
		return val;
    }

	public String getMemcacheAddr()
    {
		String val = getValue("MEMCACHED_SERVER_ADDR");
		if (val == null || val.isEmpty())
		{
			val = "127.0.0.1:11211";
		}
		return val;
    }
	
	public String getRedisAddr()
    {
		String val = getValue("REDIS_SERVER_ADDR");
		if (val == null || val.isEmpty())
		{
			val = "127.0.0.1:6379";
		}
		return val;
    }
	
	public String getSmsHost()
    {
		String val = getValue("SMS_HOST");
		if (val == null || val.isEmpty())
		{
			val = "";
		}
		return val;
    }
	
	//是否需要验证
	//默认为需要验证
	public boolean requireAuth()
	{
		String val = getValue("REQUIRE_AUTH");
		if (val == null || val.isEmpty())
		{
			return true;
		}
		return Boolean.parseBoolean(val);
	}

	public boolean isRedisCluster()
    {
		String val = getValue("REDIS_SERVER_IS_CLUSTER");
		if (val == null || val.isEmpty()) 
		{
			val = "false";
		}
		return Boolean.parseBoolean(val);
    }
	
	public String getRedisMasterName()
	{
		String val = getValue("REDIS_MASTER_NAME");
		if (val == null || val.isEmpty())
		{
			val = "master1";
		}
		return val;
	}

	public int getPushInterval()
    {
		int interval = 2 * 1000;
		String val = getValue("PUSH_INTERVAL");
		if (val != null && !val.isEmpty()) 
		{
			try 
			{
				interval = Integer.parseInt(val);
			}
			catch (Exception e) 
			{
				Logger.error("SysConfigCache, fail to get pushInterval, val: %s, msg: %s", val, e.getMessage());
			}
		}
		return interval;
    }

	public int getPushQueueSize()
    {
		int interval = 1000;
		String val = getValue("PUSH_QUEUE_SIZE");
		if (val != null && !val.isEmpty()) 
		{
			try 
			{
				interval = Integer.parseInt(val);
			}
			catch (Exception e) 
			{
				Logger.error("SysConfigCache, fail to get pushQueueSize, val: %s, msg: %s", val, e.getMessage());
			}
		}
		return interval;
    }
	
	public int getCaptchaExpireTime()
	{
		int time = 0;
		String val = getValue("CAPTCHA_EXPIRE_TIME");
		if (val != null && !val.isEmpty())
		{
			try
			{
				time = Integer.parseInt(val);
			}
			catch (Exception e)
			{
				Logger.error("SysConfigCache, fail to get captchaExpireTime, val: %s, msg: %s", val, e.getMessage());
			}
		}
		return time;
	}
	
	public int getValidationExpireTime()
	{
		int time = 0;
		String val = getValue("VALIDATION_EXPIRE_TIME");
		if (val != null && !val.isEmpty())
		{
			try
			{
				time = Integer.parseInt(val);
			}
			catch (Exception e)
			{
				Logger.error("SysConfigCache, fail to get validationExpireTime, val: %s, msg: %s", val, e.getMessage());
			}
		}
		return time;
	}
	
	public boolean isDebugProject()
    {
		String val = getValue("IS_DEBUG_PROJECT");
		if (val == null || val.isEmpty()) 
		{
			val = "false";
		}
		return Boolean.parseBoolean(val);
    }
}