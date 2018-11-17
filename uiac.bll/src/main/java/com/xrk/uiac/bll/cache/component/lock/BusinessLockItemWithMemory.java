package com.xrk.uiac.bll.cache.component.lock;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.xrk.hws.common.logger.Logger;
import com.xrk.uiac.bll.cache.AbstractCache;

/**
 * 
 * 基于内存缓存实现的业务锁的基础元件
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：yexx<yexiaoxiao@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年10月10日
 * <br> JDK版本：1.8
 * <br>==========================
 */
public class BusinessLockItemWithMemory extends AbstractCache<String> implements IBusinessLockItem
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
			put(key, value);
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
		boolean ret = true;
		String countKey = COUNT_PRE_KEY + key;
		lock.lock();
		try
		{
			if (!super.remove(countKey))
			{
				ret = false;
				Logger.error("BusinessLockItemWithMemory, fail to remove countkey, countkey: %s", countKey);
			}
			else
			{
				if (!super.remove(key))
				{
					ret = false;
					Logger.error("BusinessLockItemWithMemory, fail to remove key, key: %s", key);
				}
			}
		}
		finally
		{
			lock.unlock();
		}
		return ret;
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
