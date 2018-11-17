package com.xrk.uiac.bll.cache.component;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import com.xrk.hws.common.logger.Logger;
import com.xrk.uiac.bll.cache.AbstractCache;
import com.xrk.uiac.bll.cache.CacheService;
import com.xrk.uiac.bll.cache.CheckCacheTask;
import com.xrk.uiac.bll.cache.ExpireObject;
import com.xrk.uiac.bll.cache.ICheckExpireCache;
import com.xrk.uiac.bll.entity.UserAppIdEntity;
import com.xrk.uiac.bll.entity.UserAuthorizationEntity;
import com.xrk.uiac.common.utils.BeanCopierUtils;
import com.xrk.uiac.common.utils.ThreadSafeSortList;

/**
 * 用户认证缓存管理，自动删除已过期的用户缓存内容
 * UserAuthorizedCache: UserAuthorizedCache.java.
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：shunchiguo<shunchiguo@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年5月4日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public class UserAuthorizationCache extends AbstractCache<UserAuthorizationEntity> implements ICheckExpireCache
{
	//用于超时缓存检测
	private AtomicLong lastExpireDate = new AtomicLong();
	//存放过期对象信息
	private List<ExpireObject<String>> lsExpire;
	//存放授权码与用户关系
	private UserTokenMapCache _tokenCache;
	//存放用户与已登录App关系
	private UserLoginAppCache _appCache;
	
	private Object syncObj = new Object();
	
	private long delayRemoveTime = 1000 * 10;//超时数据延时删除时间,默认10秒
	
	private ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
	
	private UserTokenMapCache getTokenCache()
	{
		if(_tokenCache == null){
			synchronized (syncObj) {
				if(_tokenCache == null){
					_tokenCache = (UserTokenMapCache) CacheService.GetService(UserTokenMapCache.class);
				}
            }			
		}
		return _tokenCache;
	}
	
	private UserLoginAppCache getAppCache()
	{
		if(_appCache == null){
			synchronized (syncObj) {
				if(_appCache == null){
					_appCache = (UserLoginAppCache) CacheService.GetService(UserLoginAppCache.class);
				}
            }			
		}
		return _appCache;
	}
	
	
	public UserAuthorizationCache()
	{		
		lastExpireDate.set(new Date().getTime());
		
		lsExpire = new ThreadSafeSortList<ExpireObject<String>>();
		//to-do:需要修改成配置项或由构造函数送入,目前暂时设置为5秒执行一次
		scheduledExecutorService.scheduleAtFixedRate(new CheckCacheTask(this), 
				1, 5, TimeUnit.SECONDS);
	}
	
	public void setDelayRemoveTime(long second)
	{
		delayRemoveTime = 1000 * second;
	}
	
	public boolean put(UserAuthorizationEntity user)
	{
		UserAuthorizationEntity userClone = new UserAuthorizationEntity();
		BeanCopierUtils.copy(user, userClone);
		
		lsExpire.add(new ExpireObject<String>(userClone.getExpireTime(), userClone.entityId()));
		
		long uid = userClone.getUid();
		HashSet<String> list = getAppCache().getList(uid);
		if(!list.contains(userClone.getAppId())){
			list.add(userClone.getAppId());
			getAppCache().put(uid, list);
		}
		
		if(!getTokenCache().contain(userClone.getAuthToken())){
			getTokenCache().put(userClone.getAuthToken(), new UserAppIdEntity(userClone.getUid(), userClone.getAppId()));
		}
		
		return super.put(userClone.entityId(), userClone);
	}
	
	public UserAuthorizationEntity getByToken(String authToken)
	{
		UserAppIdEntity entity = getTokenCache().get(authToken);
		if(entity != null)
		{
			return get(entity.getUid(), entity.getAppId());
		}
		return null;
	}
	
	public void removeByToken(String authToken)
	{
		getTokenCache().remove(authToken);
	}
	
	public String[] getUserLoginApp(long uid)
	{
		if(getAppCache().contain(uid))
		{
			HashSet<String> sets = getAppCache().getList(uid);
			return sets.toArray(new String[sets.size()]);
		}
		else
		{
			return null;
		}
	}
	
	/**
	 * 重载方法，忽略用户送入的key值
	 * @see com.xrk.uiac.bll.cache.AbstractCache#put(java.lang.Object, java.lang.Object)
	 */
	@Override
	public boolean put(Object key, UserAuthorizationEntity user)
	{
		return put(user);
	}

	public boolean remove(long uid, String appId)
	{
		String key = UserAuthorizationEntity.formatId(uid, appId);
		return remove(key);
	}
	
	public boolean remove(UserAuthorizationEntity user)
	{
		return remove(user.entityId());
	}
		
	@Override
	public boolean remove(Object objKey)
	{
		//从本地过期对象中移除
		String keyVal = "";
		if(objKey instanceof UserAuthorizationEntity)
		{
			keyVal = ((UserAuthorizationEntity)objKey).entityId();
		}
		else
		{
			keyVal = objKey.toString();
		}
				
		//获取当前的accessToken并清除
		UserAuthorizationEntity authEntity = super.get(keyVal);
		if(authEntity != null)
		{
			getTokenCache().remove(authEntity.getAuthToken());
			HashSet<String> list = getAppCache().getList(authEntity.getUid());
			list.remove(authEntity.getAppId());
			getAppCache().put(authEntity.getUid(), list);
			
			Logger.info("remove cache:uid=%s, appId=%s, accessToken=%s", authEntity.getUid(), authEntity.getUid(), authEntity.getAuthToken());
		}
		
		return super.remove(keyVal);
	}
	
	public boolean contain(long uid, String appId)
	{
		String key = UserAuthorizationEntity.formatId(uid, appId);
		return contain(key);
	}
	
	public boolean contain(UserAuthorizationEntity user)
	{
		return contain(user.entityId());
	}
	
	@Override
	public boolean contain(Object objKey)
	{
		String key = "";
		if(objKey instanceof UserAuthorizationEntity)
		{
			key = ((UserAuthorizationEntity)objKey).entityId();
		}
		else
		{
			key = objKey.toString();
		}
		return super.contain(key);
	}
	
	
	public UserAuthorizationEntity get(long uid, String appId)
	{
		String key = UserAuthorizationEntity.formatId(uid, appId);
		return super.get(key);
	}
	
	public UserAuthorizationEntity get(UserAuthorizationEntity user)
	{
		return super.get(user.entityId());
	}
	
	@Override
	public UserAuthorizationEntity get(Object objKey)
	{
		String key = "";
		if(objKey instanceof UserAuthorizationEntity)
		{
			key = ((UserAuthorizationEntity)objKey).entityId();
		}
		else
		{
			key = objKey.toString();
		}
		return super.get(key);
	}
	
	@Override
	public boolean clear()
	{		
		lsExpire.clear();
		getTokenCache().clear();
		getAppCache().clear();
		Logger.info("UserAuthorizationCache clear call!");	
		return super.clear();
	}

	public void checkExpire()
    {
    	long currDate = new Date().getTime() + delayRemoveTime;
    
    	int iRemove = 0;
    	int tot = lsExpire.size();
    	//未采用同步锁机制以提高性能，可能会存在数据在某个检测周期有脏数据的情况，
    	//不过缓存的脏数据是可接受范围
    	for(;iRemove < tot; iRemove++)
        {
    		ExpireObject<String> obj = lsExpire.get(iRemove);
        	if(obj.getExpireTime() > currDate)
        	{
        		break;
        	}
        }
    	
        for(int i=0; i<iRemove; i++)
        {
        	ExpireObject<String> obj = lsExpire.get(0);
        	
        	UserAuthorizationEntity authEntity = super.get(obj.getKey());        	
    		
        	//仅移除索引时间过期的Token，对于重复登录导致添加多个用户过期时间的则忽略掉
        	if(authEntity != null && 
    				authEntity.getExpireTime().getTime() <= obj.getExpireTime())
    		{
        		getTokenCache().remove(authEntity.getAuthToken());
        		HashSet<String> list = getAppCache().getList(authEntity.getUid());
    			list.remove(authEntity.getAppId());
    			getAppCache().put(authEntity.getUid(), list);
    			
    			this.remove(obj.getKey());
    			Logger.info("remove timeout cache:uid=%s, appId=%s, accessToken=%s", authEntity.getUid(), authEntity.getUid(), authEntity.getAuthToken());
    		}
    		
        	lsExpire.remove(0);
        }
    }
}
