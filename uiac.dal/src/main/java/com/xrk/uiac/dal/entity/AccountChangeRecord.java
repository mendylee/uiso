package com.xrk.uiac.dal.entity;

import java.util.Date;

import com.xrk.hws.dal.annotations.Column;
import com.xrk.hws.dal.annotations.Table;
import com.xrk.hws.dal.common.DbType;

/**
 * 
 * 重新绑定手机号、邮箱时可能产生的账号切换记录
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：yexx<yexiaoxiao@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年7月29日
 * <br> JDK版本：1.7
 * <br>==========================
 */
@Table(name="uiac_account_change_record")
public class AccountChangeRecord
{
	@Column(name="app_id", type=DbType.INT)
	private int appId;
	
	@Column(name="uid", type=DbType.BIGINT)
	private long uid;
	
	@Column(name="old_account", type=DbType.VARCHAR)
	private String oldAccount;

	@Column(name="new_account", type=DbType.VARCHAR)
	private String newAccount;
	
	@Column(name="add_date", type=DbType.DATETIME)
	private Date addDate;
	
	@Column(name="account_type", type=DbType.SMALLINT)
	private int accountType;
	
	@Column(name="remark", type=DbType.VARCHAR)
	private String remark;
	
	@Column(name="serial_id", type=DbType.BIGINT)
	private long serialId;
	
	public long getSerialId()
	{
		return serialId;
	}

	public void setSerialId(long serialId)
	{
		this.serialId = serialId;
	}

	public String getRemark()
	{
		return remark;
	}

	public void setRemark(String remark)
	{
		this.remark = remark;
	}
	
	public int getAccountType()
	{
		return accountType;
	}
	
	public void setAccountType(int accountType)
	{
		this.accountType = accountType;
	}

	public int getAppId()
	{
		return appId;
	}

	public void setAppId(int appId)
	{
		this.appId = appId;
	}

	public long getUid()
	{
		return uid;
	}

	public void setUid(long uid)
	{
		this.uid = uid;
	}

	public String getOldAccount()
	{
		return oldAccount;
	}

	public void setOldAccount(String oldAccount)
	{
		this.oldAccount = oldAccount;
	}

	public String getNewAccount()
	{
		return newAccount;
	}

	public void setNewAccount(String newAccount)
	{
		this.newAccount = newAccount;
	}

	public Date getAddDate()
	{
		return addDate;
	}

	public void setAddDate(Date addDate)
	{
		this.addDate = addDate;
	}
}
