package com.xrk.uiac.dal.dao;

import java.util.List;

import com.esotericsoftware.kryo.util.IntArray;
import com.xrk.hws.dal.core.DataSet;
import com.xrk.uiac.dal.Dal;
import com.xrk.uiac.dal.entity.UserSubAccount;

/**
 * 
 * UserSubAccount实体类DAO
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：yexx<yexiaoxiao@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年5月11日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public class UserSubAccountDAO
{
	private static final String COM_INSERT = "insert";
	private static final String COM_FIND_WITH_UID_APPID = "findWithUidAndAppid";
	private static final String COM_FIND_WIDTH_SUBACCOUNT_APPID ="findWithSubAccountAndAppid";
	private static final String COM_M_FIND_WITH_UID = "mfindWithUid";
	private static final String COM_M_FIND_WITH_UID_APPID ="mfindWithUidAndAppid";
	private static final String COM_M_FIND_WITH_UID_BIND_APPID ="mfindWithUidAndBindAppid";
	
	private static final String COM_DELETE_WITH_UID_APPID = "deleteWithUidAndAppid";
	private static final String COM_DELETE_WITH_UID_SUBACCOUNT = "deleteWithSubAccount";
	
	
	//测试模板
	private static final String COM_DELETE_WITH_UID = "deleteWithUid";
	
	private static DataSet dataSet = Dal.getDataSet(UserSubAccount.class);
	
	public static int insertSubAccount(UserSubAccount subAccount)
	{
		return dataSet.insertOne(COM_INSERT, subAccount, true, null);
	}
	
	public static UserSubAccount findWithUidAppid(long uid, int appId, int bindAppId)
	{
		Object[] paras = new Object[3];
		paras[0] = new Long(uid);
		paras[1] = new Integer(appId);
		paras[2] = new Integer(bindAppId);
		UserSubAccount account = dataSet.findOne(COM_FIND_WITH_UID_APPID, paras);
		return account;
	}
	
	public static UserSubAccount findWithSubAccountAppID(String account, int appId, int bindAppId)
	{
		Object[] paras = new Object[3];
		paras[0] = new Integer(appId);
		paras[1] = new Integer(bindAppId);
		paras[2] = account;
		
		return dataSet.findOne(COM_FIND_WIDTH_SUBACCOUNT_APPID, paras);
	}
	
	public static List<UserSubAccount> findListWithUid(long uid)
	{
		Object[] paras = new Object[1];
		paras[0] = new Long(uid);
		return dataSet.findMulti(COM_M_FIND_WITH_UID, paras, new Object[0]);
	}
	
	public static List<UserSubAccount> findListWithUidAndAppId(long uid, int appId)
	{
		Object[] paras = new Object[2];
		paras[0] = new Long(uid);
		paras[1] = new Integer(appId);
		return dataSet.findMulti(COM_M_FIND_WITH_UID_APPID, paras, new Object[0]);
	}
	
	public static List<UserSubAccount> findListWithUidAndBindAppId(long uid, int appId, int bindAppId)
	{
		Object[] paras = new Object[3];
		paras[0] = new Long(uid);
		paras[1] = new Integer(appId);
		paras[2] = new Integer(bindAppId);
		return dataSet.findMulti(COM_M_FIND_WITH_UID_BIND_APPID, paras, new Object[0]);
	}
	
	public static int deleteWithUidAppid(long uid, int appId)
	{
		Object[] paras = new Object[2];
		paras[0] = new Long(uid);
		paras[1] = new Integer(appId);
		return dataSet.deleteMulti(COM_DELETE_WITH_UID_APPID, paras);
	}
	
	public static int deleteWithUidSubAccount(String account, int appId, int bindAppId)
	{
		Object[] paras = new Object[3];
		paras[0] = new Integer(appId);
		paras[1] = new Integer(bindAppId);
		paras[2] = account;
		return dataSet.deleteMulti(COM_DELETE_WITH_UID_SUBACCOUNT, paras);
	}
	
	public static int deleteWithUid(long uid)
	{
		Object[] paras = new Object[1];
		paras[0] = new Long(uid);
		return dataSet.deleteMulti(COM_DELETE_WITH_UID, paras);
	}
}
