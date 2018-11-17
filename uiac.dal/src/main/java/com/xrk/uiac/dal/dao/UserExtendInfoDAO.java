package com.xrk.uiac.dal.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xrk.hws.common.logger.Logger;
import com.xrk.hws.dal.core.DataSet;
import com.xrk.uiac.dal.Dal;
import com.xrk.uiac.dal.DalSetting;
import com.xrk.uiac.dal.entity.UserExtendInfo;

/**
 * 
 * UserExtendInfo实体类DAO
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：yexx<yexiaoxiao@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年5月11日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public class UserExtendInfoDAO
{
	private static final String COM_M_INSERT = "minsert";
	private static final String COM_INSERT = "insert";
	private static final String COM_M_FIND_WITH_UID = "mfindWithUid";
	private static final String COM_UPDATE_WITH_UID = "updateWithUid";
	private static final String COM_FIND_WITH_UID_KEY = "findWithUidAndKey";
	
	//测试用
	private static final String COM_DELETE_WITH_UID = "deleteWithUid";

	private static DataSet dataSet = Dal.getDataSet(UserExtendInfo.class);
	
	public static int insert(UserExtendInfo ei)
	{
		return dataSet.insertOne(COM_INSERT, ei, true, null);
	}
	
	public static int insertList(List<UserExtendInfo> list)
	{
		return dataSet.insertMulti(COM_M_INSERT, list, true, null);
	}
	
	public static int deleteWithUid(long uid)
	{
		Object[] paras = new Object[1];
		paras[0] = new Long(uid);
		return dataSet.deleteMulti(COM_DELETE_WITH_UID, paras);
	}
	
	public static List<UserExtendInfo> findListWithUid(long uid)
	{
		Object[] paras = new Object[1];
		paras[0] = new Long(uid);
		return dataSet.findMulti(COM_M_FIND_WITH_UID, paras, new Object[0]);
	}
	
	public static UserExtendInfo findWithUidAndKey(long uid, String key)
	{
		Object[] paras = new Object[2];
		paras[0] = new Long(uid);
		paras[1] = key;
		return dataSet.findOne(COM_FIND_WITH_UID_KEY, paras);
	}
	
	//涉及到多对多的更新，可能出现其中几项更新失败的情况
	//暂时的处理方式是，只要有一条数据更新失败，则返回0
	//后期可以加上事务处理逻辑，实现脏数据的回滚操作
	public static int updateWithUid(long uid, List<UserExtendInfo> list)
	{
		int ret = 1;
		Object[] qParas = new Object[2];
		Object[] upParas = new Object[1];
		
		qParas[0] = new Long(uid);
		
		for (UserExtendInfo ei : list)
		{
			if (ei != null)
			{
				qParas[1] = ei.getExtKey();
				upParas[0] = ei.getExtValue();
				if (dataSet.updateOne(COM_UPDATE_WITH_UID, qParas, upParas, true, false) == 0)
				{
					//假如是pg，pg暂时不支持upsert，因此手动upsert
					if (DalSetting.getDbType() == DalSetting.DBTYPE_PG)
					{
						if (findWithUidAndKey(uid, ei.getExtKey()) == null)
						{
							if (insert(ei) == 0)
							{
								//手动insert失败
								
								Logger.error("Upsert data, fail to insert data, db: pg, className: %s, action: %s, uid: %d, key: %s, value: %s", 
										UserExtendInfo.class, COM_UPDATE_WITH_UID, uid, ei.getExtKey(), ei.getExtValue());
								
								ret = 0;
							}
						}
						else
						{
							//upsert失败
							
							Logger.error("Upsert data, fail to update data, db: pg, className: %s, action: %s, uid: %d, key: %s, value: %s", 
									UserExtendInfo.class, COM_UPDATE_WITH_UID, uid, ei.getExtKey(), ei.getExtValue());
							
							ret = 0;
						}
					}
					else
					{
						//其中一条数据更新失败
						Logger.error("Upsert data fail. className: %s, action: %s, uid: %d, key: %s, value: %s", 
								UserExtendInfo.class, COM_UPDATE_WITH_UID, uid, ei.getExtKey(), ei.getExtValue());
						
						ret = 0;
					}
				}
			}
		}
		
		return ret;
	}

	public static List<UserExtendInfo> getExtendInfoFromJson(long uid, String jsonStr)
	{
		if (jsonStr == null || jsonStr.isEmpty())
		{
			return null;
		}
		
		Map<String, String> extendInfoMap = new HashMap<String, String>();
		List<UserExtendInfo> extendInfoList = new ArrayList<UserExtendInfo>();
		Gson gson = new Gson();
		
		try
		{
			extendInfoMap = gson.fromJson(jsonStr, new TypeToken<Map<String, String>>(){}.getType());
		}
		catch (Exception e)
		{
			Logger.error("Failed to convert json. json: %s", jsonStr);
			return null;
		}
		
		if (extendInfoMap != null)
		{
			UserExtendInfo extendInfo = null;
			Set<Entry<String, String>> entries = extendInfoMap.entrySet();
			for (Entry<String, String> entry : entries)
			{
				extendInfo = new UserExtendInfo();
				extendInfo.setUid(uid);
				extendInfo.setExtKey(entry.getKey());
				extendInfo.setExtValue(entry.getValue());
				extendInfoList.add(extendInfo);
			}
		}
		else
		{
			return null;
		}
		
		return extendInfoList;
	}
	
	public static String getJsonFromExtendInfoList(List<UserExtendInfo> list)
	{
		if (list == null || list.size() == 0)
		{
			return null;
		}
		
		Map<String, String> extendInfoMap = new HashMap<String, String>();
		for (UserExtendInfo ei : list)
		{
			extendInfoMap.put(ei.getExtKey(), ei.getExtValue());
		}
		
		Gson gson = new Gson();
		String json = null;
		
		try
		{
			json = gson.toJson(extendInfoMap);
		}
		catch (Exception e)
		{
			Logger.error("Failed to convert Map to json.");
			json = null;
		}
		
		return json;
	}
}
