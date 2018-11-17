package com.xrk.uiac.dal.dao;

import java.util.List;

import com.xrk.hws.common.logger.Logger;
import com.xrk.hws.dal.core.DataSet;
import com.xrk.uiac.dal.Dal;
import com.xrk.uiac.dal.entity.AppInfo;

/**
 * 
 * 应用信息DAO
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：yexx<yexiaoxiao@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年5月19日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public class AppInfoDAO
{
	private static final String COM_INSERT = "insert";
	private static final String COM_M_INSERT = "minsert";
	private static final String COM_FIND_WITH_APPID = "findWithAppId";
	private static final String COM_FIND_ALL = "findAll";
	
	private static DataSet dataSet = Dal.getDataSet(AppInfo.class);
	
	public static int insert(AppInfo appInfo)
	{
		return dataSet.insertOne(COM_INSERT, appInfo, false, null);
	}
	
	public static int insertList(List<AppInfo> list)
	{
		return dataSet.insertMulti(COM_M_INSERT, list, false, null);
	}
	
	public static AppInfo findWithAppId(int appId)
	{
		Object[] paras = new Object[1];
		paras[0] = new Integer(appId);
		return dataSet.findOne(COM_FIND_WITH_APPID, paras);
	}
	
	public static List<AppInfo> findAll()
	{
		return dataSet.findMulti(COM_FIND_ALL, null, null);
	}
}
