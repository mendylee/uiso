package com.xrk.uiac.bll.cache.component.lock;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.xrk.uiac.bll.cache.AbstractCache;

/**
 * 
 * 基于Memcached缓存实现的业务锁的基础元件
 * 
 * 暂未完全实现
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：yexx<yexiaoxiao@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年10月10日
 * <br> JDK版本：1.8
 * <br>==========================
 */
public class BusinessLockItemWithMemcached extends AbstractCache<String> implements IBusinessLockItem
{
	private Lock lock = new ReentrantLock();
	private static final String COUNT_PRE_KEY = "BusinessLockItemWithMemory";

	@Override
	public boolean contain(String key)
	{
		return super.contain(key);
	}

	@Override
	public String get(String key)
	{
		return super.get(key);
	}

	@Override
	public long putAndCount(String key, String value)
	{
		String countKey = COUNT_PRE_KEY + key;
		lock.lock();
		try
		{
			String countStr = get(countKey);
			int count = (countStr == null) ? 0 : Integer.parseInt(countStr);
			count ++;
			put(countKey, String.valueOf(count));
			return count;
		}
		finally
		{
			lock.unlock();
		}
	}

	@Override
	public boolean removeAndCount(String key)
	{
		//TODO 临时处理
		String countKey = COUNT_PRE_KEY + key;
		super.remove(countKey);
		return super.remove(key);
	}
	
	@Override
	public void clearAll()
	{
		super.clear();
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
