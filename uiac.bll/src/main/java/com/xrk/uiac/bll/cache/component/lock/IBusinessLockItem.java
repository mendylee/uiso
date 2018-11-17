package com.xrk.uiac.bll.cache.component.lock;

/**
 * 
 * 业务锁基础元件方法接口
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：yexx<yexiaoxiao@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年10月10日
 * <br> JDK版本：1.8
 * <br>==========================
 */
public interface IBusinessLockItem
{
	/**
	 * 
	 * 前三个是基础方法，只要保证方法为原子操作即可 
	 *    
	 * @param key
	 * @return
	 */
	public boolean contain(String key);
	public String get(String key);
	public void clearAll();
	
	
	/**
	 * 
	 * 移除的同时将计数清零
	 * 失败则返回false 
	 *    
	 * @param key
	 * @return
	 */
	public boolean removeAndCount(String key);
	
	/**
	 * 
	 * 插入的同时计数，并返回最新的计数值，用以判断本次插入操作时第几次插入操作
	 * 操作失败时，返回 -1
	 *    
	 * @param key
	 * @param value
	 * @return
	 */
	public long putAndCount(String key, String value);
	
	/**
	 * 
	 * 锁住当前元件
	 *
	 */
	public void lock();
	
	/**
	 * 
	 * 解锁当前元件
	 *
	 */
	public void unlock();
}
