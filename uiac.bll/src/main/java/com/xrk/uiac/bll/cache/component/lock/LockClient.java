package com.xrk.uiac.bll.cache.component.lock;

import com.xrk.hws.common.logger.Logger;

/**
 * 
 * 重写业务锁
 * 用支持redis、jvm缓存、memcached等多种缓存类型的BusinessLock代替原有的concurrentHashMap
 * BusinessLock提供类似于concurrentHashMap的方法
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：yexx<yexiaoxiao@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年10月9日
 * <br> JDK版本：1.8
 * <br>==========================
 */
public class LockClient
{
	private volatile static LockClient instance = null;
	
	private BusinessLock accountLocks = null;
	private BusinessLock subAccountLocks = null;
	private BusinessLock userInfoLocks = null;

	private LockClient()
	{
		//初始化锁，其中绑定子账号的并发量较低，采用容量为4的业务锁更省资源
		accountLocks = new BusinessLock();
		subAccountLocks = new BusinessLock(4);
		userInfoLocks = new BusinessLock();
		
		Logger.info("init lock cache");
	}
	
	public void reset()
	{
		accountLocks.clear();
		Logger.info("reset lock cache");
	}
	
	public static LockClient getInstance()
	{
		if (instance == null)
		{
			synchronized(LockClient.class)
			{
				if (instance == null)
				{
					instance = new LockClient();
				}
			}
		}
		return instance;
	}
	
	/**
	 * 
	 * 注册的时候锁住账号，不让其他线程有同时多次注册的机会
	 *    
	 * @param account
	 * @param accountType
	 * @return
	 */
	public boolean lockAccount(String account, int accountType)
	{
		Logger.debug("lock account, account: %s, accountType: %d", account, accountType);
		
		//利用putIfAbsent的特性
		//putIfAbsent线程安全
		//当key account存在时，不插入新数据，且返回原有的存在的数据，因此putIfAbsent()不为null
		//当key account不存在时，插入新数据，且返回null
		if (accountLocks.putIfAbsent(account, String.valueOf(accountType)) == null)
		{
			Logger.debug("lock account successfully!, account: %s, accountType: %d", account, accountType);
			return true;
		}
		else
		{
			Logger.debug("lock account error, account is locked by another operation, account: %s, accountType: %d", account, accountType);
			return false;
		}
	}
	
	/**
	 * 
	 * 注册操作完成（或者出错）后解锁账号
	 *    
	 * @param account
	 */
	public void unlockAccount(String account)
	{
		if (account != null && !account.isEmpty())
		{
			Logger.debug("unlock account, account: %s", account);
			accountLocks.remove(account);
		}
	}
	
	/**
	 * 
	 * 绑定子账号时锁住子账号，避免重复插入
	 *    
	 * @param subAccount
	 * @param subAppId
	 * @return
	 */
	public boolean lockSubAccount(String subAccount, int subAppId)
	{
		Logger.debug("lock subAccount, subAccount: %s, subAppId: %d", subAccount, subAppId);
		
		if (subAccountLocks.putIfAbsent(subAccount, String.valueOf(subAppId)) == null)
		{
			Logger.debug("lock subAccount successfully!, subAccount: %s, subAppId: %d", subAccount, subAppId);
			return true;
		}
		else
		{
			Logger.debug("lock subAccount error, subAccount is locked by another operation, subAccount: %s, subAppId: %d", subAccount, subAppId);
			return false;
		}
	}
	
	/**
	 * 
	 * 绑定子账号完成（或者出错）后解锁子账号 
	 *    
	 * @param subAccount
	 * @param subAppId
	 */
	public void unlockSubAccount(String subAccount, int subAppId)
	{
		if (subAccount != null && !subAccount.isEmpty())
		{
			//利用BusinessLock的新remove方法
			//直接在BusinessLock内部实现传入的subAppId与锁住的subAppId的线程安全的比较
			//仅当两者相等时解锁
			if (subAccountLocks.remove(subAccount, String.valueOf(subAppId)))
			{
				Logger.debug("unlock subAccount successfully! subAccount: %s, subAppId: %d", subAccount, subAppId);
			}
			else
			{
				Logger.error("fail to unlock subAccount! subAccount: %s, subAppId: %d", subAccount, subAppId);
			}
		}
	}
	
	/**
	 * 
	 * 以uid为键锁住用户基本信息
	 *    
	 * @param uid
	 * @return
	 */
	public boolean lockUserInfo(long uid)
	{
		Logger.debug("lock userInfo, uid: %d", uid);
		
		if (userInfoLocks.putIfAbsent(String.valueOf(uid), "1") == null)
		{
			Logger.debug("lock userInfo successfully! uid: %d", uid);
			return true;
		}
		else
		{
			Logger.debug("lock userInfo error, userInfo is locked by another operation, uid: %d", uid);
			return false;
		}
	}
	
	/**
	 * 
	 * 以uid为键解锁用户基本信息 
	 *    
	 * @param uid
	 */
	public void unlockUserInfo(long uid)
	{
		if (uid != 0)
		{
			Logger.debug("unlock userInfo, uid: %d", uid);
			userInfoLocks.remove(String.valueOf(uid));
		}
	}
}
