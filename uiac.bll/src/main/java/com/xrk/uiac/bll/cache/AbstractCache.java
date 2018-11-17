package com.xrk.uiac.bll.cache;

import java.util.Map;

import com.xrk.uiac.common.utils.ClassTypeParameter;

/**
 * 缓存基础类，实现基本的缓存逻辑
 * AbstractCache: AbstractCache.java.
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：shunchiguo<shunchiguo@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年4月27日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public abstract class AbstractCache<T>
{
	protected String CacheKeyPre;
	ICache<T> cache = null;
	public AbstractCache()
	{
		CacheKeyPre = this.getClass().getName();
		try {
			String className = SysConfigCache.getInstance().getCacheClass();
			@SuppressWarnings("unchecked")
	        Class<ICache<T>> cacheClass = (Class<ICache<T>>) Class.forName(className);
	        cache = cacheClass.newInstance();	            
	        cache.setClassType(getType(), CacheKeyPre);
	    }
	    catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
	        e.printStackTrace();
	    }
	}
	
	@SuppressWarnings("unchecked")
	protected Class<T> getType()
	{
		Class<?> classes = ClassTypeParameter.find(this, AbstractCache.class, "T");
		return (Class<T>)classes;
	}
	
	public T get(Object key)
	{
		return cache.get(key.toString());
	}
	
	public boolean put(Object key, T value)
	{
		return cache.put(key.toString(), value);
	}
	
	public boolean remove(Object key)
	{
		return cache.remove(key.toString());
	}
	
	public boolean clear()
	{
		return cache.clear();
	}
	
	public boolean contain(Object key)
	{
		return cache.contain(key.toString());
	}
	
	public long size()
	{
		return cache.size();
	}

	public Map<String, T> getAll()
	{
		return cache.getAll();
	}
}
