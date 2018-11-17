package com.xrk.uiac.bll.cache;

import java.util.Map;

/**
 * 缓存实现接口类
 * ICache: ICache.java.
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：shunchiguo<shunchiguo@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年5月27日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public interface ICache<T>
{

	/**
	 * 
	 * 设置当前缓存存储的类对象  
	 *    
	 * @param type
	 */
	public abstract void setClassType(Class<T> type, String cacheName);
	
	/**
	 * 根据指定的Key获取缓存值，如果不存在，则返回null  
	 *    
	 * @param key
	 * @return
	 */
	public abstract T get(String key);

	/**
	 * 
	 * 往缓存中推送要存储的数据，如果数据存在则更新缓存内容
	 *    
	 * @param key
	 * @param value
	 */
	public abstract boolean put(String key, T value);
	
	/**
	 * 
	 * 往缓存中设置一个存储数据，并设置过期时间(单位为当前时间往后的秒数，不能超过30天，或UINX时间)
	 *    
	 * @param key
	 * @param value
	 * @param expireTime
	 * @return
	 */
	public abstract boolean put(String key, T value, int expireTime);

	/**
	 * 
	 * 从缓存中移除指定键值的缓存  
	 *    
	 * @param key
	 */
	public abstract boolean remove(String key);

	/**
	 * 
	 * 清空缓存对象  
	 *
	 */
	public abstract boolean clear();

	/**
	 * 
	 * 检查缓存中是否包含指定键值的缓存  
	 *    
	 * @param key
	 * @return
	 */
	public abstract boolean contain(String key);

	/**
	 * 
	 * 获取缓存存储数据总数  
	 *    
	 * @return
	 */
	public abstract long size();

	/**
	 * 
	 * 获取所有键值对 
	 *    
	 * @return
	 */
	public abstract Map<String, T> getAll();
}