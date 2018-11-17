package com.xrk.uiac.dal.dao;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.xrk.hws.dal.core.DataSet;
import com.xrk.uiac.dal.Dal;
import com.xrk.uiac.dal.entity.UserInfo;

/**
 * 
 * UserInfo实体类的DAO
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：yexx<yexiaoxiao@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年5月11日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public class UserInfoDAO
{
	private static final String COM_INSERT_USERINFO = "insertUserInfo";
	private static final String COM_FIND_WITH_UID = "findWithUid";
	private static final String COM_FIND_WITH_MOBILE = "findWithMobile";
	private static final String COM_UPDATE_USERINFO = "updateUserInfo";
	private static final String COM_UPDATE_TO_BIND_MOBILE = "updateToBindMobile";
	private static final String COM_UPDATE_TO_BIND_EMAIL = "updateToBindEmail";
	private static final String COM_UPDATE_WITH_MOBILE = "updateWithMobile";
	//测试用
	private static final String COM_DELETE_WITH_UID = "deleteUser";
	
	private static DataSet dataSet = Dal.getDataSet(UserInfo.class);
	
	public static int insert(UserInfo userInfo)
	{
		return dataSet.insertOne(COM_INSERT_USERINFO, userInfo, true, null);
	}
	
	public static UserInfo findWithUid(long uid)
	{
		Object[] paras = new Object[1];
		paras[0] = new Long(uid);
		return dataSet.findOne(COM_FIND_WITH_UID, paras);
	}
	
	public static int deleteUser(long uid)
	{
		Object[] paras = new Object[1];
		paras[0] = new Long(uid);
		return dataSet.deleteMulti(COM_DELETE_WITH_UID, paras);
	}
	
	public static UserInfo findWithMobile(String mobile)
	{
		Object[] paras = new Object[1];
		paras[0] = mobile;
		return dataSet.findOne(COM_FIND_WITH_MOBILE, paras);
	}
	
	public static int update(long uid, Map<String, Object> srcMap)
	{
		//将请求参数map转换成适合插入数据库的参数map
		Map<String, Object> parasMap = getUpdateMap(srcMap);
		
		Object[] qParas = new Object[1];
		qParas[0] = new Long(uid);
		return dataSet.updateOne(COM_UPDATE_USERINFO, qParas, parasMap, false, false);
	}
	
	public static int updateMobileBindingStatus(long uid, String mobile, int bindingStatus)
	{
		Object[] qParas = new Object[1];
		Object[] upParas = new Object[2];
		qParas[0] = new Long(uid);
		upParas[0] = new Integer(bindingStatus);
		upParas[1] = mobile;
		return dataSet.updateOne(COM_UPDATE_TO_BIND_MOBILE, qParas, upParas, false, false);
	}
	
	public static int updateEmailBindingStatus(long uid, String email, int bindingStatus)
	{
		Object[] qParas = new Object[1];
		Object[] upParas = new Object[2];
 		qParas[0] = new Long(uid);
 		upParas[0] = new Integer(bindingStatus);
		upParas[1] = email;
		return dataSet.updateOne(COM_UPDATE_TO_BIND_EMAIL, qParas, upParas, false, false);
	}
	
	public static int updateWithMobile(long uid, String mobile)
	{
		Object[] qParas = new Object[1];
		Object[] upParas = new Object[1];
		qParas[0] = new Long(uid);
		upParas[0] = mobile;
		return dataSet.updateOne(COM_UPDATE_WITH_MOBILE, qParas, upParas, true, false);
	}
	
	//将请求参数map转换成用于记录更新的键值对map
	//即，将源map中的key(sex, username..)转换成用于数据库存储的key(sex, user_name...)，值不变
	public static Map<String, Object> getUpdateMap(Map<String, Object> srcMap)
	{
		if (srcMap == null)
		{
			return null;
		}
		
		String srcNameKey = "userName";
		String destNameKey = "user_name";
		String srcDateKey = "editDate";
		String destDateKey = "edit_date";
		String srcMobileIsVerifyKey = "mobileIsVerify";
		String destMobileIsVerifyKey = "mobile_is_verify";
		String srcEmailIsVerifyKey = "emailIsVerify";
		String destEmailIsVerifyKey = "email_is_verify";
		String sexKey = "sex";
		Map<String, Object> destMap = new HashMap<String, Object>();
		Set<Entry<String, Object>> entries = srcMap.entrySet();
		
		for (Entry<String, Object> entry : entries)
		{
			String key = entry.getKey();
			if (srcNameKey.equals(key))
			{
				destMap.put(destNameKey, entry.getValue());
			}
			else if (srcDateKey.equals(key))
			{
				destMap.put(destDateKey, entry.getValue());
			}
			else if (srcMobileIsVerifyKey.equals(key))
			{
				destMap.put(destMobileIsVerifyKey, entry.getValue());
			}
			else if (srcEmailIsVerifyKey.equals(key))
			{
				destMap.put(destEmailIsVerifyKey, entry.getValue());
			}
			else if (sexKey.equals(key))
			{
				Object value = new Integer((int) Math.round((Double) entry.getValue()));
				destMap.put(sexKey, value);
			}
			else
			{
				destMap.put(entry.getKey(), entry.getValue());
			}
		}
		
		return destMap;
	}
}
