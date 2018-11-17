package com.xrk.uiac.bll.cache.external.redis;

import redis.clients.jedis.Jedis;

/**
 * 
 * Redis连接池接口，实现基本的连接池操作方法
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：yexx<yexiaoxiao@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年11月16日
 * <br> JDK版本：1.8
 * <br>==========================
 */
public interface IRedisPool
{
	/**
	 * 
	 * 获取jedis实例 
	 *    
	 * @return
	 */
	Jedis getResource();
	
	/**
	 * 
	 * 通过传入的异常体判断jedis连接是否已被损坏
	 *    
	 * @param exception
	 * @return
	 */
	boolean handleJedisException(Exception exception);
	
	/**
	 * 
	 * 返回实例
	 *    
	 * @param jedis
	 * @param exception
	 */
	void returnResource(Jedis jedis, Exception exception);
	
	/**
	 * 
	 * 返回实例  
	 *    
	 * @param jedis
	 * @param isBroken
	 */
	void returnResource(Jedis jedis, boolean isBroken);
	
	/**
	 * 
	 * 强制销毁连接
	 *    
	 * @param jedis
	 */
	void destroyResource(Jedis jedis);
}
