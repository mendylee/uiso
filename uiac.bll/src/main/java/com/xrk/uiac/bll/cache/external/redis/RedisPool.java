package com.xrk.uiac.bll.cache.external.redis;

import com.xrk.hws.common.logger.Logger;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.exceptions.JedisConnectionException;
import redis.clients.jedis.exceptions.JedisDataException;
import redis.clients.jedis.exceptions.JedisException;

/**
 * 
 * redis连接池
 * 实现了JedisCommands接口的部分方法
 * 实现的方法有hget, hset, hexists, hdel, del, hlen
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
public class RedisPool implements IRedisPool
{
	private JedisPool jedisPool = null;
	
	private int maxTotal = 50;
	private int maxWaitingMillis = 60000;
	private int maxIdle = 10;
	private boolean testConnection = true;
	private boolean testWhileIdel = true;
	
	//获取连接时的最大重试次数
	private int retryingCount = 10;
	//获取连接失败后的休眠时间
	private int retryingWaitingMillis = 2000;
	
	public RedisPool(String host, int port)
	{
		init(host, port, null);
	}
	
	public RedisPool(String host, int port, JedisPoolConfig config)
	{
		init(host, port, config);
	}
	
	private void init(String host, int port, JedisPoolConfig config)
	{
		if (config == null)
		{
			config = new JedisPoolConfig();
			
			//获取连接时的最大等待时间
			config.setMaxWaitMillis(maxWaitingMillis);
			//最大连接数
			config.setMaxTotal(maxTotal);
			//最大空闲连接数
			config.setMaxIdle(maxIdle);
			//获取连接时是否测试连接可用性
			config.setTestOnBorrow(testConnection);
			//
			config.setTestWhileIdle(testWhileIdel);
		}
		
		jedisPool = new JedisPool(config, host, port);
	}
	
	public JedisPool getPool()
	{
		return jedisPool;
	}
	
	@Override
	public Jedis getResource()
	{
		Jedis jedis = null;
		jedis = getResourceFromPool();
		int time = 1;
		while (jedis == null && time <= retryingCount)
		{
			Logger.debug("RedisPool, retry to get resource, time: %d", time);
			wait(retryingWaitingMillis);
			jedis = getResourceFromPool();
			time ++;
		}
		if (jedis == null)
		{
			Logger.error("RedisPool, fail to get resource from pool, jedis is null");
			throw new RuntimeException("RedisPool, fail to get resource from Pool, jedis is null");
		}
		
		return jedis;
	}
	
	/**
	 * 
	 * 从池中安全地获取单个连接
	 * 假如出现异常报错，返回空
	 *    
	 * @return
	 */
	private synchronized Jedis getResourceFromPool()
	{
		Jedis jedis = null;
		try
		{
			jedis = jedisPool.getResource();
		}
		catch (Exception e)
		{
			Logger.error(e, "RedisPool, fail to get Resource");
		}
		return jedis;
	}
	
	/**
	 * 使程序等待给定的毫秒数
	 *
	 * @param 给定的毫秒数
	 */
	private void wait(int millis)
	{
		try 
		{
			Thread.sleep(millis);
		} 
		catch (InterruptedException e)
		{
			Logger.error(e, "RedisPool, fail to sleep, msg: %s", e.getMessage());
		}
	}
	
	/**
	 * 
	 * 根据异常的类型、异常信息判断当前的jedis连接是否出故障，并返回boolean值
	 *    
	 * @param exception
	 * @return
	 */
	@Override
	public boolean handleJedisException(Exception exception)
	{
		if (exception == null)
		{
			return false;
		}
		
		if (exception instanceof JedisException)
		{
			if (exception instanceof JedisConnectionException)
	    	{
	    		Logger.error(exception, "RedisPool, Redis connection lost.");
	    	}
	    	else if (exception instanceof JedisDataException)
	    	{
	    		if ((exception.getMessage() != null) && (exception.getMessage().indexOf("READONLY") != -1)) 
	    		{
	    			Logger.error(exception, "RedisPool, Redis connection are read-only slave.");
	            }
				else
				{
					// dataException, isBroken=false
					return false;
				}
			}
			else 
			{
				Logger.error(exception, "RedisPool, Jedis exception happen.");
			}
			
			return true;
		}
    	
		Logger.error(exception, "RedisPool, normal exception happen, ");
		return false;
	}
	
	/**
	 * 
	 * 根据需求返回连接，返回失败则强制关闭该连接 
	 *    
	 * @param jedis
	 * @param isBroken
	 */
	@SuppressWarnings("deprecation")
	@Override
	public void returnResource(Jedis jedis, boolean isBroken)
	{
		if (jedis != null)
		{
			try
	    	{
		        if(isBroken)
		        {
		        	jedisPool.returnBrokenResource(jedis);
		        }
		        else
		        {
		        	jedisPool.returnResource(jedis);
		        }
	        }
	        catch (Exception e) 
	    	{
	        	Logger.error(e, "RedisPool, fail to return resource, forced close the jedis.");
	            destroyResource(jedis);
	        }
		}
    }
	
	@Override
	public void returnResource(Jedis jedis, Exception exception)
	{
		boolean isBroken = handleJedisException(exception);
		returnResource(jedis, isBroken);
	}
	
	/**
	 * 
	 * 强制销毁连接  
	 *    
	 * @param jedis
	 */
	@Override
	public void destroyResource(Jedis jedis) 
	{
		if ((jedis != null) && jedis.isConnected())
		{
			try 
			{
				try 
				{
					jedis.quit();
				} 
				catch (Exception e)
				{
					Logger.error(e, "RedisPool, destroy jedis, fail to quit jedis");
				}
				jedis.disconnect();
			} 
			catch (Exception e)
			{
				Logger.error(e, "RedisPool, destroy jedis, fail to disconnect jedis");
			}
		}
	}
}
