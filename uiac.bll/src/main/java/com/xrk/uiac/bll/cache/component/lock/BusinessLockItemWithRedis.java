package com.xrk.uiac.bll.cache.component.lock;

import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.xrk.hws.common.logger.Logger;
import com.xrk.uiac.bll.cache.AbstractCache;
import com.xrk.uiac.bll.cache.external.redis.IRedisPool;
import com.xrk.uiac.bll.cache.external.redis.RedisManager;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Response;
import redis.clients.jedis.Transaction;

/**
 * 
 * 基于Redis缓存实现的业务锁基础元件
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：yexx<yexiaoxiao@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年10月10日
 * <br> JDK版本：1.8
 * <br>==========================
 */
public class BusinessLockItemWithRedis extends AbstractCache<String> implements IBusinessLockItem
{
	private IRedisPool redisPool = null;
	
	private Lock lock = new ReentrantLock();
	private static final String COUNT_PRE_KEY = "BusinessLockItemWithRedis";
	
	public BusinessLockItemWithRedis()
	{
		redisPool = RedisManager.getInstance().getPool();
	}
	
	@Override
	public boolean contain(String key)
	{
		return super.contain(key);
	}
	
	@Override
	public String get(String key)
	{
		//return super.get(key);
		//重写该方法，不调用父类的需要序列化的方法
		
		String ret = null;
		Jedis jedis = null;
		boolean isBroken = false;
		
		try
		{
			jedis = redisPool.getResource();
			ret = jedis.hget(CacheKeyPre, key);
		}
		catch (Exception e)
		{
			Logger.error("BusinessLockItemWithRedis, fail to get, key: %s, msg: %s", key, e.getMessage());
			isBroken = redisPool.handleJedisException(e);
		}
		finally
		{
			redisPool.returnResource(jedis, isBroken);
		}
		
		return ret;
	}
	
	@Override
	public boolean removeAndCount(String key)
	{
		Jedis jedis = null;
		boolean ret = true;
		boolean isBroken = false;
		
		try
		{
			jedis = redisPool.getResource();
			String countKey = COUNT_PRE_KEY + key;
			
			//执行事务并获取返回对象
			Transaction transaction = jedis.multi();
			transaction.hdel(CacheKeyPre, countKey);
			transaction.hdel(CacheKeyPre, key);
			List<Response<?>> response = transaction.execGetResponse();
			
			//根据这两个结果记录日志并返回
			ret = ((Long) (response.get(0).get()) == 1);
			if (!ret)
			{
				Logger.debug("BusinessLockItemWithRedis, fail to remove countkey, countkey: %s", countKey); 
			}
			else
			{
				ret = ((Long) (response.get(1).get()) == 1);
				if (!ret)
				{
					Logger.debug("BusinessLockItemWithRedis, fail to remove key, key: %s", key);
				}
			}
		}
		catch (Exception e)
		{
			Logger.error("BusinessLockItemWithRedis, fail to remove and count, key: %s, msg: %s", key, e.getMessage());
			isBroken = redisPool.handleJedisException(e);
		}
		finally
		{
			//用完后将实例返回连接池
			redisPool.returnResource(jedis, isBroken);
		}
		
		return ret;
	}
	
	@Override
	public long putAndCount(String key, String value)
	{
		Jedis jedis = null;
		long ret = -1;
		boolean isBroken = false;
		
		try
		{
			jedis = redisPool.getResource();
			
			String countKey = COUNT_PRE_KEY + key;
			
			//执行事务并获取返回对象
			Transaction transaction = jedis.multi();
			transaction.hset(CacheKeyPre, key, value);
			transaction.hincrBy(CacheKeyPre, countKey, 1);
			List<Response<?>> response = transaction.execGetResponse();
			
			//累加方法的返回值为第二个元素
			ret = ((Long) response.get(1).get()).longValue();
		}
		catch (Exception e)
		{
			Logger.error("BusinessLockItemWithRedis, fail to put and count, key: %s, value: %s, msg: %s", key, value, e.getMessage());
			isBroken = redisPool.handleJedisException(e);
		}
		finally
		{
			//用完后将实例返回连接池
			redisPool.returnResource(jedis, isBroken);
		}
		
		return ret;
	}
	
	@Override
	public void clearAll()
	{
		Jedis jedis = null;
		boolean isBroken = false;
		
		try
		{
			jedis = redisPool.getResource();
			jedis.del(CacheKeyPre);
		}
		catch (Exception e)
		{
			Logger.error("BusinessLockItemWithRedis, fail to clear all, msg: %s", e.getMessage());
			isBroken = redisPool.handleJedisException(e);
		}
		finally
		{
			//用完后将实例返回连接池
			redisPool.returnResource(jedis, isBroken);
		}
	}
	
	@Override
	public void lock()
	{
		lock.lock();
	}
	
	@Override
	public void unlock()
	{
		lock.unlock();
	}
}
