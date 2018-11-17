package com.xrk.uiac.bll.cache.component;

import java.util.Arrays;
import java.util.HashSet;

import com.xrk.uiac.bll.cache.AbstractCache;

/**
 * 用户已登录AppID缓存
 * UserLoginAppCache: UserLoginAppCache.java.
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：shunchiguo<shunchiguo@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年8月14日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public class UserLoginAppCache extends AbstractCache<String>
{
	/**
	 * 设置用户已登录的AppID信息，各个ID用逗号分隔
	 * @see com.xrk.uiac.bll.cache.AbstractCache#put(java.lang.Object, java.lang.Object)
	 */
	@Override
	public boolean put(Object uid, String appList)
	{
		return super.put(uid, appList);
	}
	
	public boolean put(Object uid, HashSet<String> listAppId)
	{
		String appList = String.join(",", listAppId);
		return put(uid, appList);
	}
	
	public HashSet<String> getList(Object uid)
	{
		String appList = super.get(uid);
		HashSet<String> list = new HashSet<String>();
		if(appList != null && !appList.isEmpty())
		{
			list.addAll(Arrays.asList(appList.split(",")));
		}
		return list;
	}
}
