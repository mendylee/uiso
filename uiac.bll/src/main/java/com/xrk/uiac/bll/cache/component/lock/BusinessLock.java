package com.xrk.uiac.bll.cache.component.lock;

import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.xrk.uiac.bll.cache.SysConfigCache;

/**
 * 
 * 业务锁，实现与concurrentHashMap类似，根据key的hash值分段执行线程安全的操作。
 * 默认为16个分段。
 * 每一个分段都是一个缓存类的实例
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：yexx<yexiaoxiao@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年10月9日
 * <br> JDK版本：1.8
 * <br>==========================
 */
public class BusinessLock
{
	private IBusinessLockItem[] lockList = null;
	private final int DEFAULT_LOCK_COUNT = 16;
	
	public BusinessLock()
	{
		init(DEFAULT_LOCK_COUNT);
	}
	
	public BusinessLock(int lockCount)
	{
		if (lockCount < 1)
		{
			lockCount = DEFAULT_LOCK_COUNT;
		}
		init(lockCount);
	}
	
	private void init(int lockCount)
	{
		lockList = new IBusinessLockItem[lockCount];
		
		String cacheClass = SysConfigCache.getInstance().getCacheClass();
		
		if (cacheClass.contains("Redis"))
		{
			for (int i=0; i<lockCount; i++)
			{
				lockList[i] = new BusinessLockItemWithRedis();
			}
		}
		else if (cacheClass.contains("Memcached"))
		{
			for (int i=0; i<lockCount; i++)
			{
				lockList[i] = new BusinessLockItemWithMemcached();
			}
		}
		else
		{
			//默认用内存缓存
			for (int i=0; i<lockCount; i++)
			{
				lockList[i] = new BusinessLockItemWithMemory();
			}
		}
	}
	
	/**
	 * 
	 * 给key做hash运算，取得数组内的相应元素作为锁的实例 
	 *    
	 * @param key
	 * @return
	 */
	private IBusinessLockItem getLock(String key)
	{
		if (key == null || key.isEmpty())
		{
			throw new NullPointerException("BusinessLock, key is null");
		}
		return lockList[key.hashCode() & (lockList.length - 1)];
	}
	
	public boolean contain(String key)
	{
		IBusinessLockItem lock = getLock(key);
		lock.lock();
		try
		{
			return lock.contain(key);
		}
		finally
		{
			lock.unlock();
		}
	}
	
	public String get(String key)
	{
		IBusinessLockItem lock = getLock(key);
		lock.lock();
		try
		{
			return lock.get(key);
		}
		finally
		{
			lock.unlock();
		}
	}
	
	private String put(String key, String value, boolean onlyIfAbsent)
	{
		IBusinessLockItem lock = getLock(key);
		lock.lock();
		try
		{
			if (onlyIfAbsent)
			{
				//尝试获取原始值
				String ori = lock.get(key);
				if (ori == null)
				{
					//putAndCount是原子操作，主要是插入的同时对插入次数做统计
					long count = lock.putAndCount(key, value);
					return (count == 1) ? null : value;
				}
				else
				{
					return ori;
				}
			}
			lock.putAndCount(key, value);
			return null;
		}
		finally
		{
			lock.unlock();
		}
	}
	
	public void put(String key, String value)
	{
		put(key, value, false);
	}
	
	public String putIfAbsent(String key, String value)
	{
		return put(key, value, true);
	}
	
	private boolean remove(String key, String value, boolean onlyIfEqual)
	{
		IBusinessLockItem lock = getLock(key);
		lock.lock();
		try
		{
			if (onlyIfEqual)
			{
				if (value != null && value.equals(lock.get(key)))
				{
					return lock.removeAndCount(key);
				}
				return false;
			}
			else
			{
				return lock.removeAndCount(key);
			}
		}
		finally
		{
			lock.unlock();
		}
	}
	
	public boolean remove(String key)
	{
		return remove(key, null, false);
	}
	
	public boolean remove(String key, String value)
	{
		return remove(key, value, true);
	}
	
	public long clear()
	{
		long count = 0;
		ReentrantReadWriteLock rwlock = new ReentrantReadWriteLock();
		rwlock.writeLock().lock();
		try
		{
			for (IBusinessLockItem lockItem : lockList)
			{
				lockItem.clearAll();
			}
		}
		finally
		{
			rwlock.writeLock().unlock();
		}
		
		return count;
	}
}
