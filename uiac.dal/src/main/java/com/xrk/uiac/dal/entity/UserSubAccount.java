package com.xrk.uiac.dal.entity;

import java.util.Date;

import com.xrk.hws.dal.annotations.Column;
import com.xrk.hws.dal.annotations.Table;
import com.xrk.hws.dal.common.DbType;

/**
 * 
 * 用户子账号mongodb实体类
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：yexx<yexiaoxiao@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年5月8日
 * <br> JDK版本：1.7
 * <br>==========================
 */

@Table(name = "uiac_user_sub_account")
public class UserSubAccount
{
	@Column(name = "sub_account_id", type = DbType.BIGINT)
	private long subAccountId;
	
	@Column(name = "uid", type = DbType.BIGINT)
	private long uid;
	
	@Column(name = "bind_app_id", type = DbType.INT)
	private long bindAppId;
	
	@Column(name = "app_id", type = DbType.INT)
	private int appId;
	
	@Column(name = "account", type = DbType.VARCHAR)
	private String account;
	
	@Column(name = "add_date", type = DbType.DATETIME)
	private Date addDate;

	public long getSubAccountId()
	{
		return subAccountId;
	}

	public void setSubAccountId(long subAccountId)
	{
		this.subAccountId = subAccountId;
	}

	public long getUid()
	{
		return uid;
	}

	public void setUid(long uid)
	{
		this.uid = uid;
	}

	public int getAppId()
	{
		return appId;
	}

	public void setAppId(int appId)
	{
		this.appId = appId;
	}

	public String getAccount()
	{
		return account;
	}

	public void setAccount(String account)
	{
		this.account = account;
	}

	public Date getAddDate()
	{
		return addDate;
	}

	public void setAddDate(Date addDate)
	{
		this.addDate = addDate;
	}

	public long getBindAppId()
    {
	    return bindAppId;
    }

	public void setBindAppId(long bindAppId)
    {
	    this.bindAppId = bindAppId;
    }
}
