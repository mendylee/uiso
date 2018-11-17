package com.xrk.uiac.dal.dao;

import com.xrk.hws.dal.core.DataSet;
import com.xrk.uiac.dal.Dal;
import com.xrk.uiac.dal.entity.PushObserver;
import com.xrk.uiac.dal.entity.User;

/**
 * 
 * PushObserverDAO: 授权推送观察者DAO
 *
 * <br>=
 * ========================= <br>
 * 公司：广州向日葵信息科技有限公司 <br>
 * 开发：zhub<zhubin@xiangrikui.com> <br>
 * 版本：1.0 <br>
 * 创建时间：2015年5月15日 <br>
 * JDK版本：1.7 <br>=
 * =========================
 */
public class PushObserverDAO
{
	private static final String COM_INSERT_PUSH_OBSERVER = "insertPushObserver";

	private static final String COM_DELETE_PUSH_OBSERVER = "deletePushObserver";
	
	private static final String COM_FIND_PUSH_OBSERVER = "findPushObserver";

	private static DataSet dataSet = Dal.getDataSet(PushObserver.class);

	/**
	 * 
	 * 插入一条观察者记录
	 *    
	 * @param pushObserver
	 * @return
	 */
	public static int insertPushObserver(PushObserver pushObserver)
	{
		return dataSet.insertOne(COM_INSERT_PUSH_OBSERVER, pushObserver, false, null);
	}
	/**
	 * 
	 * 删除一条观察者记录 
	 *    
	 * @param appId
	 * @return
	 */
	public static int deletePushObserver(int appId){
		Object[] queryParas = new Object[1];
		queryParas[0]=appId;
		return dataSet.deleteMulti(COM_DELETE_PUSH_OBSERVER, queryParas);
	}
	/**
	 * 
	 * 根据appId查询观察者
	 *    
	 * @param appId
	 * @return
	 */
	public static PushObserver findWithAppId(int appId)
	{
		Object[] paras = new Object[1];
		paras[0] = new Long(appId);
		return dataSet.findOne(COM_FIND_PUSH_OBSERVER, paras);
	}
}
