package com.xrk.uiac.dal.dao;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.xrk.hws.dal.core.DataSet;
import com.xrk.uiac.dal.Dal;
import com.xrk.uiac.dal.entity.UserStat;

public class UserStatDAO
{
	private static final String COM_INSERT = "insertUserStat";
	private static final String COM_FIND_WITH_UID = "findWithUid";
	private static final String COM_UPDATE_WITH_UID = "updateWithUid";
	
	private static DataSet dataSet = Dal.getDataSet(UserStat.class);	
	
	public static UserStat findWithUid(long uid)
	{
		Object[] paras = new Object[1];
		paras[0] = new Long(uid);
		return dataSet.findOne(COM_FIND_WITH_UID, paras);
	}
	
	public static int insert(UserStat userStat)
	{
		return dataSet.insertOne(COM_INSERT, userStat, true, null);
	}
	
	public static int update(long uid)
	{
		UserStat userStat = findWithUid(uid);
		if(userStat != null){
			//将请求参数map转换成适合插入数据库的参数map
			Map<String, Object> parasMap = new HashMap<String, Object>();
			parasMap.put("first_login_date", userStat.getFirstLoginDate());
			parasMap.put("last_login_date", new Date());
			parasMap.put("login_num", userStat.getLoginNum()+1);

			Object[] qParas = new Object[1];
			qParas[0] = new Long(uid);
			return dataSet.updateOne(COM_UPDATE_WITH_UID, qParas, parasMap, true, false);
		}
		else{
			userStat = new UserStat();
			userStat.setUid(uid);
			userStat.setFirstLoginDate(new Date());
			userStat.setLastLoginDate(new Date());
			userStat.setLoginNum(1);
			return insert(userStat);
		}
	}
}
