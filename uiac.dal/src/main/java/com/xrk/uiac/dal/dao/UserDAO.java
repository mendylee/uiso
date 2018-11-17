package com.xrk.uiac.dal.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.xrk.hws.dal.core.DataSet;
import com.xrk.uiac.dal.Dal;
import com.xrk.uiac.dal.entity.User;

/**
 * 
 * User实体类的DAO
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：yexx<yexiaoxiao@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年5月11日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public class UserDAO
{
	private static final String COM_INSERT_USER = "insertUser";
	private static final String COM_UPDATE_USER_STATUS = "updateUserStatus";
	private static final String COM_UPDATE_PASSWORD = "updatePassword";
	private static final String COM_FIND_ALL = "findAll";
	private static final String COM_FIND_WITH_UID = "findWithUid";
	private static final String COM_FIND_WITH_ACCOUNT = "findWithAccount";
	private static final String COM_MFIND_WITH_ACCOUNT = "mfindWithAccount";
	private static final String COM_FIND_WITH_ACCOUNT_NOT_DEL = "findWithAccountAndNotDel";
	private static final String COM_FIND_WITH_UID_NOT_DEL = "findWithUidAndNotDel";
	private static final String COM_UPDATE_DELETE_STATUS = "updateDeleteStatus";
	private static final String COM_DELETE_USER = "deleteUser";
	private static final String COM_UPDATE_ACCOUNT = "updateAccount";
	private static final String COM_UPDATE_USER_ACCOUNT = "updateUserAccount";

	//测试模板
	private static final String COM_COUNT = "count";
	
	private static DataSet dataSet = Dal.getDataSet(User.class);
	
	public static int insertUser(User user)
	{
		return dataSet.insertOne(COM_INSERT_USER, user, false, null);
	}
	
	public static int updateStatus(long uid, int status)
	{
		Object[] qParas = new Object[1];
		Object[] upParas = new Object[1];
		qParas[0] = new Long(uid);
		upParas[0] = new Integer(status);
		return dataSet.updateOne(COM_UPDATE_USER_STATUS, qParas, upParas, false, false);
	}
	
	public static int updatePassword(long uid, String password)
	{
		Object[] qParas = new Object[1];
		qParas[0] = new Long(uid);
		Object[] upParas = new Object[1];
		upParas[0] = password;
		return dataSet.updateOne(COM_UPDATE_PASSWORD, qParas, upParas, false, false);
	}
	
	public static int updateAccount(long uid, String account)
	{
		Object[] qParas = new Object[1];
		qParas[0] = new Long(uid);
		Object[] upParas = new Object[1];
		upParas[0] = account;
		return dataSet.updateOne(COM_UPDATE_ACCOUNT, qParas, upParas, false, false);
	}
	
	public static int testUpdate(long uid, String account)
	{
		Object[] qParas = new Object[1];
		qParas[0] = new Long(uid);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("account", account);
		return dataSet.updateOne(COM_UPDATE_ACCOUNT, qParas, map, false, false);
	}
	
	public static List<User> findAll()
	{
		return dataSet.findMulti(COM_FIND_ALL, null, null);
	}
	
	public static User findWithUid(long uid)
	{
		Object[] paras = new Object[1];
		paras[0] = new Long(uid);
		return dataSet.findOne(COM_FIND_WITH_UID, paras);
	}
	
	public static User findWithAccount(String account)
	{
		Object[] paras = new Object[1];
		paras[0] = account;
		return dataSet.findOne(COM_FIND_WITH_ACCOUNT, paras);
	}
	
	public static List<User> findListWithAccount(String account)
	{
		Object[] paras = new Object[1];
		paras[0] = account;
		return dataSet.findMulti(COM_MFIND_WITH_ACCOUNT, paras, null);
	}
	
	public static User findExistingUserWithAccount(String account)
	{
		Object[] paras = new Object[1];
		paras[0] = account;
		return dataSet.findOne(COM_FIND_WITH_ACCOUNT_NOT_DEL, paras);
	}
	
	public static User findExistingUserWithUid(long uid)
	{
		Object[] paras = new Object[1];
		paras[0] = new Long(uid);
		return dataSet.findOne(COM_FIND_WITH_UID_NOT_DEL, paras);
	}
	
	public static int updateDeleteStatus(long uid, int delStatus)
	{
		Object[] qParas = new Object[1];
		Object[] upParas = new Object[1];
		qParas[0] = new Long(uid);
		upParas[0] = new Integer(delStatus);
		return dataSet.updateOne(COM_UPDATE_DELETE_STATUS, qParas, upParas, false, false);
	}
		
	public static long count()
	{
		return dataSet.count(COM_COUNT, new Object[0]);
	}
	
	public static int deleteUser(long uid)
	{
		Object[] paras = new Object[1];
		paras[0] = new Long(uid);
		return dataSet.deleteMulti(COM_DELETE_USER, paras);
	}
	
	public static int updateUserAccount(long uid, String account)
	{
		Object[] qParas = new Object[1];
		Object[] upParas = new Object[1];
		qParas[0] = new Long(uid);
		upParas[0] = account;
		return dataSet.updateOne(COM_UPDATE_USER_ACCOUNT, qParas, upParas, false, false);
	}
}
