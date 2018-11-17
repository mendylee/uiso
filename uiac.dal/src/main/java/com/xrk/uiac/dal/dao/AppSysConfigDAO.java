package com.xrk.uiac.dal.dao;

import java.util.List;

import com.xrk.hws.common.logger.Logger;
import com.xrk.hws.dal.core.DataSet;
import com.xrk.uiac.dal.Dal;
import com.xrk.uiac.dal.entity.AppSysConfig;
import com.xrk.uiac.dal.entity.SysConfig;

/**
 * 
 * SysConfigDAO: 系统配置实体DAO
 *
 * <br>=
 * ========================= <br>
 * 公司：广州向日葵信息科技有限公司 <br>
 * 开发：zhub<zhubin@xiangrikui.com> <br>
 * 版本：1.0 <br>
 * 创建时间：2015年5月13日 <br>
 * JDK版本：1.7 <br>=
 * =========================
 */
public class AppSysConfigDAO
{
	private static final String COM_INSERT_SYS_CONFIG = "insertSysConfig";
	private static final String COM_FIND_SYS_CONFIG = "findSysConfig";
	private static final String COM_FIND_CONFIG_VALUE = "findConfigValue";

	private static DataSet dataSet = Dal.getDataSet(AppSysConfig.class);

	public static int insertSysConfig(AppSysConfig conf)
	{
		if (dataSet == null) {
			Logger.error("Get dataSet fail. className: %s, action: %s", AppSysConfig.class,
			        COM_INSERT_SYS_CONFIG);
			return 1;
		}

		return dataSet.insertOne(COM_INSERT_SYS_CONFIG, conf, true, null);
	}

	public static List<AppSysConfig> findSysConfig()
	{
		return dataSet.findMulti(COM_FIND_SYS_CONFIG, null, null);
	}

	public static AppSysConfig findConfigValue( int appId,String item)
	{
		Object[] queryParams = new Object[2];
		queryParams[0] = appId;
		queryParams[1] = item;
		return dataSet.findOne(COM_FIND_CONFIG_VALUE, queryParams);
	}

}
