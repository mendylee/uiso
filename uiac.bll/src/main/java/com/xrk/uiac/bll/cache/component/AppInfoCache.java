package com.xrk.uiac.bll.cache.component;

import java.util.List;

import com.xrk.hws.common.logger.Logger;
import com.xrk.uiac.bll.cache.AbstractCache;
import com.xrk.uiac.dal.dao.AppInfoDAO;
import com.xrk.uiac.dal.entity.AppInfo;

/**
 * 
 * 应用信息缓存，一次性抓取所有应用信息存在内存里
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：yexx<yexiaoxiao@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年7月31日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public class AppInfoCache extends AbstractCache<AppInfo>
{
	public AppInfoCache()
	{
		//一次加载
		init();
	}
	
	private void init()
	{
		Logger.info("load appInfo cache start...");
		try
		{
			List<AppInfo> appInfoList = AppInfoDAO.findAll();
			if (appInfoList != null)
			{
				for (AppInfo appInfo : appInfoList)
				{
					put(appInfo.getAppId(), appInfo);
					Logger.info("add appInfo: appId: %d", appInfo.getAppId());
				}
				Logger.info("load appInfo end... total = %d", appInfoList.size());
			}
		}
		catch (Exception e)
		{
			Logger.error("load appInfo cache error: %s", e.getMessage());
			throw e;
		}
	}
	
	@Override
	public boolean put(Object key, AppInfo value)
	{
		return super.put(key, value);
	}
	
	@Override
	public AppInfo get(Object appId)
	{
		return super.get(appId);
	}
}