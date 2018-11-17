package com.xrk.uiac.bll.entity;
/**
 * 认证用户缓存实体
 * UserInentityEntity: UserInentityEntity.java.
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：shunchiguo<shunchiguo@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年4月27日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public class UserIdentityEntity {
	private long uid;
	private String password;
	private int status;
	private String account;	
	
	public long getUid()
	{
		return uid;
	}
	public void setUid(long uid)
	{
		this.uid = uid;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getAccount()
    {
	    return account;
    }
	public void setAccount(String account)
    {
	    this.account = account;
    }
}
