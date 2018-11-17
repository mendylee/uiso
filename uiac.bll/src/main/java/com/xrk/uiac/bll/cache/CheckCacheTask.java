package com.xrk.uiac.bll.cache;

import com.xrk.hws.common.logger.Logger;

/**
 * 检查缓存队列已过期缓存
 * CheckCacheTask: CheckCacheTask.java.
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：shunchiguo<shunchiguo@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年4月30日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public class CheckCacheTask implements Runnable
{
	boolean isRuning = false;
	ICheckExpireCache cache;
	public CheckCacheTask(ICheckExpireCache cacheObj)
	{
		cache = cacheObj;
	}
	public void run()
	{
		if(isRuning)
		{
			return;
		}
		
		isRuning = true;
		try
		{
			cache.checkExpire();
		}
		catch(Exception ex)
		{
			Logger.error(ex, ex.getMessage());
		}
		finally
		{
			isRuning = false;
		}
	}

}
