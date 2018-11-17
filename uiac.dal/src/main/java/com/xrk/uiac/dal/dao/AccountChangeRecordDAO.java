package com.xrk.uiac.dal.dao;

import com.xrk.hws.dal.core.DataSet;
import com.xrk.uiac.dal.Dal;
import com.xrk.uiac.dal.entity.AccountChangeRecord;

/**
 * 
 * AccountChangeRecordDAO: AccountChangeRecordDAO.java.
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：yexx<yexiaoxiao@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年7月29日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public class AccountChangeRecordDAO
{
	private static final String COM_INSERT = "insert";
	private static final String COM_FIND_WITH_UID = "findWithUid";
	private static final String COM_DELETE_ALL = "deleteAll";
	
	private static DataSet dataSet = Dal.getDataSet(AccountChangeRecord.class);
	
	public static int insert(AccountChangeRecord record)
	{
		return dataSet.insertOne(COM_INSERT, record, true, null);
	}
	
	public static AccountChangeRecord findWithUid(long uid)
	{
		Object[] paras = new Object[1];
		paras[0] = new Long(uid);
		return dataSet.findOne(COM_FIND_WITH_UID, paras);
	}
	
	public static int deleteAll()
	{
		return dataSet.deleteMulti(COM_DELETE_ALL, null);
	}
}
