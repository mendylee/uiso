package com.xrk.uiac.bll.cache;


/**
 * 缓存过期检测支持接口，如果需要实现缓存过期检测，需要实现此此接口
 * ICheckExpireCache: ICheckExpireCache.java.
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：shunchiguo<shunchiguo@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年4月30日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public interface ICheckExpireCache
{
	/**
	 * 
	 * 执行过期缓存的检测方法  
	 *    
	 * @return
	 */
	void checkExpire();
}
