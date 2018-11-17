package com.xrk.uiac.bll.cache;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 内存缓存，使用HashMap实现
 * MemoryCache: MemoryCache.java.
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：shunchiguo<shunchiguo@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年5月27日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public class MemoryCache<T> implements ICache<T>
{
	private Map<Object, T> hsMap;
	
	public MemoryCache() 
	{
	    hsMap = new ConcurrentHashMap<Object, T>();
    }
	
	@Override
    public T get(String key)
    {	   
	    return hsMap.get(key);
    }

	@Override
    public boolean put(String key, T value)
    {
	    return put(key, value, 0);
    }
	
	//内存缓存暂时不实现过期检测
	@Override
    public boolean put(String key, T value, int expireTime)
    {
	    hsMap.put(key, value);
	    return true;
    }

	@Override
    public boolean remove(String key)
    {
	    T val = hsMap.remove(key);
	    return val != null;
    }

	@Override
    public boolean clear()
    {
	    hsMap.clear();
	    return true;
    }

	@Override
    public boolean contain(String key)
    {
	    return hsMap.containsKey(key);
    }

	@Override
    public long size()
    {
	    return hsMap.size();
    }

	@Override
    public void setClassType(Class<T> type, String cacheName)
    {	    
    }

	@Override
	public Map<String, T> getAll()
	{
		Map<String, T> retMap = new HashMap<String, T>();
		//利用concurrentMap的特性线程安全地遍历map的副本，并赋值给新的map
		Iterator<Map.Entry<Object, T>> it = hsMap.entrySet().iterator();
		while (it.hasNext())
		{
			Map.Entry<Object, T> entry = it.next();
			retMap.put(String.valueOf(entry.getKey()), entry.getValue());
		}
		return retMap;
	}
	
}