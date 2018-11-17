package com.xrk.uiac.dal.entity;

import com.xrk.hws.dal.annotations.Column;
import com.xrk.hws.dal.annotations.Table;
import com.xrk.hws.dal.common.DbType;


/**
 * 
 * 用户信息mongodb实体类
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：yexx<yexiaoxiao@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年5月8日
 * <br> JDK版本：1.7
 * <br>==========================
 */
@Table(name = "uiac_user_extend_info")
public class UserExtendInfo
{
	@Column(name = "uid", type = DbType.BIGINT)
	private long uid;
	
	@Column(name = "ext_key", type = DbType.VARCHAR)
	private String extKey;
	
	@Column(name = "ext_value", type = DbType.VARCHAR)
	private String extValue;

	public long getUid()
	{
		return uid;
	}

	public void setUid(long uid)
	{
		this.uid = uid;
	}

	public String getExtKey()
	{
		return extKey;
	}

	public void setExtKey(String extKey)
	{
		this.extKey = extKey;
	}

	public String getExtValue()
	{
		return extValue;
	}

	public void setExtValue(String extValue)
	{
		this.extValue = extValue;
	}
}
