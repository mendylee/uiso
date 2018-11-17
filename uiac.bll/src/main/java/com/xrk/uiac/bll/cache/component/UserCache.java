package com.xrk.uiac.bll.cache.component;

import com.xrk.uiac.bll.cache.AbstractCache;
import com.xrk.uiac.bll.entity.UserEntity;
import com.xrk.uiac.common.utils.BeanCopierUtils;

/**
 * 
 * 用户基本信息缓存
 * 为了不引起键名的冲突，不同的键类别提供不同的方法。
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：yexx<yexiaoxiao@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年11月25日
 * <br> JDK版本：1.8
 * <br>==========================
 */
public class UserCache extends AbstractCache<UserEntity>
{
	//业务方法
	
	public UserEntity getWithAccount(String account)
	{
		String key = getCacheKeyWithAccount(account);
		return get(key);
	}
	
	public UserEntity getWithUid(long uid)
	{
		String key = getCacheKeyWithUid(uid);
		return get(key);
	}
	
	public boolean putWithAccount(String account, UserEntity user)
	{
		String key = getCacheKeyWithAccount(account);
		return put(key, user);
	}
	
	public boolean putWithUid(long uid, UserEntity user)
	{
		String key = getCacheKeyWithUid(uid);
		return put(key, user);
	}
	
	public boolean removeWithAccount(String account)
	{
		String key = getCacheKeyWithAccount(account);
		return remove(key);
	}
	
	public boolean removeWithUid(long uid)
	{
		String key = getCacheKeyWithUid(uid);
		return remove(key);
	}
	
	private String getCacheKeyWithAccount(String account)
	{
		//用账号作为缓存的key时，将原账号进行一定格式的封装，以区分以uid为key的缓存
		return "account#" + account;
	}
	
	private String getCacheKeyWithUid(long uid)
	{
		//用uid作为缓存的key时，将原账号进行一定格式的封装，以区分以账号为key的缓存
		return "uid#" + uid;
	}
	
	@Override
    public UserEntity get(Object key)
    {
		UserEntity user = super.get(key);
		if (user == null)
		{
			return null;
		}
		UserEntity cloneUser = new UserEntity();
		BeanCopierUtils.copy(user, cloneUser);
	    
		return cloneUser;
    }

	@Override
    public boolean put(Object key, UserEntity user)
    {
		if (user == null)
		{
			return false;
		}
		UserEntity cloneUser = new UserEntity();
		BeanCopierUtils.copy(user, cloneUser);
		
	    return super.put(key, cloneUser);
    }
}
