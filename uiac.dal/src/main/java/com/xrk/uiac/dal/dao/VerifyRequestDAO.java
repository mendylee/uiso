package com.xrk.uiac.dal.dao;

import java.util.Date;

import com.xrk.hws.dal.core.DataSet;
import com.xrk.uiac.dal.Dal;
import com.xrk.uiac.dal.entity.VerifyRequest;

/**
 * 
 * VerifyRequest实体类的DAO
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：yexx<yexiaoxiao@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年5月20日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public class VerifyRequestDAO
{
	private static final String COM_INSERT = "insert";
	private static final String COM_FIND_WITH_MOBILE = "findWithMobile";
	private static final String COM_DELETE_WITH_MOBILE = "deleteWithMobile";
	private static final String COM_UPDATE_WITH_MOBILE = "updateWithMobile";
	
	private static DataSet dataSet = Dal.getDataSet(VerifyRequest.class);
	
	public static int insert(VerifyRequest verifyRequest)
	{
		return dataSet.insertOne(COM_INSERT, verifyRequest, true, null);
	}
	
	public static VerifyRequest findWithMobile(String mobile)
	{
		Object[] paras = new Object[1];
		paras[0] = mobile;
		return dataSet.findOne(COM_FIND_WITH_MOBILE, paras);
	}
	
	public static int updateStatusWithMobile(String mobile, int status)
	{
		Object[] qParas = new Object[1];
		Object[] upParas = new Object[1];
		qParas[0] = mobile;
		upParas[0] = new Integer(status);
		return dataSet.updateOne(COM_UPDATE_WITH_MOBILE, qParas, upParas, false, false);
	}
	
	public static int deleteWithMobile(String mobile)
	{
		Object[] paras = new Object[1];
		paras[0] = mobile;
		return dataSet.deleteMulti(COM_DELETE_WITH_MOBILE, paras);
	}
}
