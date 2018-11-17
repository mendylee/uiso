package com.xrk.uiac.bll.cache.external.memcached;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicLong;

import net.spy.memcached.BinaryConnectionFactory;
import net.spy.memcached.MemcachedClient;
import net.spy.memcached.internal.OperationFuture;

import com.xrk.hws.common.logger.Logger;
import com.xrk.uiac.bll.cache.ICache;
import com.xrk.uiac.bll.cache.SysConfigCache;
import com.xrk.uiac.bll.cache.external.KryoSerializer;
import com.xrk.uiac.common.utils.Codec;

/**
 * Memcached缓存实现类，使用Kryo库进行对象序列化操作
 * MemcachedCache: MemcachedCache.java.
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：shunchiguo<shunchiguo@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年5月27日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public class MemcachedCache<T> implements ICache<T>
{
	Class<T> classType = null;
	private MemcachedClient mcClient = null;
	private int exp = 24 * 60 * 60;// 过期时间,默认为1天
	private String cacheName;
	private String currVersion = "";
	AtomicLong recordCount = new AtomicLong(0);

	public MemcachedCache() {
		init();		
	}
	
	private boolean init()
	{
		try {
			List<InetSocketAddress> mcServers = new ArrayList<InetSocketAddress>();
			String mcAddr = SysConfigCache.getInstance().getMemcacheAddr();
			String[] aryAddr = mcAddr.split(",");
			for (String addr : aryAddr) {
				if (addr.isEmpty()) {
					continue;
				}

				String[] ary = addr.split(":");
				if (ary.length != 2) {
					Logger.warn("Memcached config wrong addr:%s", addr);
					continue;
				}

				if (!ary[1].matches("\\d+")) {
					Logger.warn("Memcached config wrong port!addr=%s", addr);
					continue;
				}

				int port = Integer.parseInt(ary[1]);
				InetSocketAddress mcServer = new InetSocketAddress(ary[0], port);
				mcServers.add(mcServer);
			}

			mcClient = new MemcachedClient(new BinaryConnectionFactory(), mcServers);
			if (mcClient == null) {
				return false;
			}
		}
		catch (IOException e) {
			Logger.error(e, "init memcached cached error:%s", e.getMessage());
			return false;
		}
		return true;
	}

	private void reset()
	{
		recordCount.set(0);
	    currVersion = Codec.hexMD5(String.format("%s_%s", this.cacheName, (new Date()).getTime()));
	}
	
	@Override
    public void setClassType(Class<T> type, String cacheName)
    {
	    classType = type;
	    this.cacheName = cacheName;
	    reset();
    }

	private Class<T> getClassType()
	{
		return classType;
	}
	
	private String getKey(Object key)
	{
		return String.format("%s_%s", this.currVersion, key.toString());
	}
	
	@Override
	public T get(String key)
	{
		byte[] bt = (byte[]) mcClient.get(getKey(key));
		if (bt == null) {
			return null;
		}

		return KryoSerializer.Deserializer(getClassType(), bt);
	}

	public boolean put(String key, T value)
	{
		return put(key, value, exp);
	}

	@Override
	public boolean put(String key, T value, int expireTime)
	{
		byte[] bt = KryoSerializer.Serializer(value);
		boolean noExists = mcClient.get(getKey(key)) == null;
		OperationFuture<Boolean> op = null;
		if(noExists)
		{
			op = mcClient.add(getKey(key), expireTime, bt);
		}
		else
		{
			op = mcClient.set(getKey(key), expireTime, bt);
		}
			
		try {
			boolean bRtn = op.get();
			if(noExists && bRtn)
			{
				recordCount.incrementAndGet();
			}
			return bRtn;
		}
		catch (InterruptedException | ExecutionException e) {
			Logger.error(e, "memcached put error:key=%s,errmsg=%s", key, e.getMessage());
		}
		
		return false;
	}

	@Override
	public boolean remove(String key)
	{
		OperationFuture<Boolean> op = mcClient.delete(getKey(key));
		try {
			boolean bRtn = op.get();
			if(bRtn)
			{
				recordCount.decrementAndGet();
			}
			return bRtn;
		}
		catch (InterruptedException | ExecutionException e) {
			Logger.error(e, "memcached remove error:key=%s,errmsg=%s", key, e.getMessage());
		}
		return false;
	}

	@Override
	public boolean clear()
	{
//		OperationFuture<Boolean> op = mcClient.flush();
//		try {
//			return op.get();
//		}
//		catch (InterruptedException | ExecutionException e) {
//			Logger.error(e, "memcached clear error:errmsg=%s",  e.getMessage());
//		}		
		reset();
		return true;
	}

	@Override
	public boolean contain(String key)
	{
		Object obj = mcClient.get(getKey(key));
		return obj != null;
	}

	@Override
	public long size()
	{
		long val = recordCount.get();
		return val;
//		Map<SocketAddress, Map<String, String>> map = mcClient.getStats("sizes");
//		int total = 0;		
//		for(Map<String, String> m : map.values())
//		{
//			for(String val : m.values())
//			{
//				try
//				{
//					total += Integer.parseInt(val);
//				}
//				catch(NumberFormatException e)
//				{					
//				}
//			}
//		}
//		return total;
	}

	@Override
	public Map<String, T> getAll()
	{
		//TODO 暂不实现
		return null;
	}
}
