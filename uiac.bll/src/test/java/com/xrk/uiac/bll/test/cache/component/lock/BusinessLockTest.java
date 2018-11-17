package com.xrk.uiac.bll.test.cache.component.lock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.xrk.uiac.bll.cache.CacheService;
import com.xrk.uiac.bll.cache.component.lock.BusinessLock;
import com.xrk.uiac.bll.cache.external.redis.RedisPool;
import com.xrk.uiac.dal.Dal;
import com.xrk.uiac.dal.DalTestHelper;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Response;
import redis.clients.jedis.Transaction;

public class BusinessLockTest
{
	private BusinessLock lock = new BusinessLock();
	
	private String key1 = "redispool.test.key1";
	private String key2 = "redispool.test.key2";
	private String value1 = "redispool.test.value1";
	private String value2 = "redispool.test.value2";
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception 
	{
		DalTestHelper.initDal();
		CacheService.Init();
	} 
	
	@AfterClass
	public static void tearDownAfterClass() throws Exception
	{
		Dal.dispose();
		CacheService.cleanAll();
	}
	
	@Before
	public void setUp() throws Exception
	{
		lock.clear();
	}
	
	@After
	public void tearDown() throws Exception
	{
		lock.clear();
	}
	
	@Test
	public void testContain()
	{
		assertFalse(lock.contain(key1));
		assertFalse(lock.contain(key2));
		
		lock.put(key1, value1);
		assertTrue(lock.contain(key1));
		
		lock.put(key2, value2);
		assertTrue(lock.contain(key2));
		
		lock.remove(key1);
		assertFalse(lock.contain(key1));
		assertTrue(lock.contain(key2));
		
		lock.remove(key2);
		assertFalse(lock.contain(key1));
		assertFalse(lock.contain(key2));
	}

	@Test
	public void testGet()
	{
		assertNull(lock.get(key1));
		assertNull(lock.get(key2));
		
		lock.put(key1, value1);
		lock.put(key2, value2);
		
		assertEquals(value1, lock.get(key1));
		assertEquals(value2, lock.get(key2));
		
		lock.put(key1, value2);
		assertEquals(value2, lock.get(key1));
		
		lock.remove(key1);
		lock.remove(key2);
		assertNull(lock.get(key1));
		assertNull(lock.get(key2));
	}

	@Test
	public void testPut()
	{
		assertNull(lock.get(key1));
		assertNull(lock.get(key2));
		
		lock.put(key1, value1);
		lock.put(key2, value2);
		
		assertEquals(value1, lock.get(key1));
		assertEquals(value2, lock.get(key2));
		
		lock.put(key1, value2);
		assertEquals(value2, lock.get(key1));
		
		lock.remove(key1);
		lock.remove(key2);
		assertNull(lock.get(key1));
		assertNull(lock.get(key2));
	}

	@Test
	public void testPutIfAbsent()
	{
		assertNull(lock.get(key1));
		assertNull(lock.get(key2));
		
		assertNull(lock.putIfAbsent(key1, value1));
		assertNull(lock.putIfAbsent(key2, value2));
		
		assertEquals(value1, lock.get(key1));
		assertEquals(value2, lock.get(key2));
		
		assertEquals(value1, lock.putIfAbsent(key1, value1));
		assertEquals(value2, lock.putIfAbsent(key2, value2));
		
		lock.remove(key1);
		lock.remove(key2);
		assertNull(lock.putIfAbsent(key1, value1));
		assertNull(lock.putIfAbsent(key2, value2));
	}

	@Test
	public void testRemoveString()
	{
		assertFalse(lock.contain(key1));
		assertFalse(lock.contain(key2));
		
		lock.put(key1, value1);
		assertTrue(lock.contain(key1));
		
		lock.put(key2, value2);
		assertTrue(lock.contain(key2));
		
		lock.remove(key1);
		assertFalse(lock.contain(key1));
		assertTrue(lock.contain(key2));
		
		lock.remove(key2);
		assertFalse(lock.contain(key1));
		assertFalse(lock.contain(key2));
	}

	@Test
	public void testRemoveStringString()
	{
		assertFalse(lock.contain(key1));
		assertFalse(lock.contain(key2));
		
		lock.put(key1, value1);
		assertTrue(lock.contain(key1));
		
		lock.put(key2, value2);
		assertTrue(lock.contain(key2));
		
		lock.remove(key1, value2);
		assertTrue(lock.contain(key1));
		assertTrue(lock.contain(key2));
		
		lock.remove(key1, value1);
		assertFalse(lock.contain(key1));
		assertTrue(lock.contain(key2));
		
		lock.remove(key2, value1);
		assertFalse(lock.contain(key1));
		assertTrue(lock.contain(key2));
		
		lock.remove(key2, value2);
		assertFalse(lock.contain(key1));
		assertFalse(lock.contain(key2));
	}
	
	@Test
	public void testMultiThread()
	{
		ExecutorService bgThread = Executors.newFixedThreadPool(50);
		AtomicLong errorCount = new AtomicLong(0);
		AtomicLong nullCount = new AtomicLong(0);
		int threadCount = 100;
		
		AtomicInteger clock = new AtomicInteger(threadCount);
		
		for (int i=0; i<threadCount; i++)
		{
			bgThread.execute(new Runnable()
			{
				@Override
				public void run()
				{
					if (lock.putIfAbsent(key1, value1) == null)
					{
						nullCount.incrementAndGet();
					}
					else
					{
						errorCount.incrementAndGet();
					}
					clock.decrementAndGet();
				}
			});
		}
		
		while (clock.get() > 0)
		{
			//等待所有线程的操作执行完毕
		}
		
		assertEquals(1, nullCount.get());
		assertEquals(threadCount - 1, errorCount.get());
	}
	
	@Test
	public void testMultiThreadMultiClient()
	{
		ExecutorService bgThread = Executors.newFixedThreadPool(50);
		AtomicLong errorCount = new AtomicLong(0);
		AtomicLong nullCount = new AtomicLong(0);
		
		
		int threadCount = 20;
		int lockCount = 20;
		BusinessLock[] lockList = new BusinessLock[lockCount];
		
		for (int i=0; i<lockCount; i++)
		{
			lockList[i] = new BusinessLock();
		}
		
		AtomicInteger clock = new AtomicInteger(threadCount * lockCount);
		
		
		for (int i=0; i<threadCount; i++)
		{
			for (int j=0; j<lockCount; j++)
			{
				BusinessLock theLock = lockList[j];
				
				bgThread.execute(new Runnable()
				{
					@Override
					public void run()
					{
						if (theLock.putIfAbsent(key1, value1) == null)
						{
							nullCount.incrementAndGet();
						}
						else
						{
							errorCount.incrementAndGet();
						}
						clock.decrementAndGet();
					}
				});
			}
		}
		
		while (clock.get() > 0)
		{
			//等待所有线程的操作执行完毕
		}
		
		System.out.println("nullCount:"+nullCount.get());
		System.out.println("errorCount:"+errorCount.get());
		
		assertEquals(1, nullCount.get());
		assertEquals(threadCount * lockCount - 1, errorCount.get());
	}
}