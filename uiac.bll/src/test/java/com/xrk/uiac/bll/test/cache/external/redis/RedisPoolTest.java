package com.xrk.uiac.bll.test.cache.external.redis;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.xrk.uiac.bll.cache.external.redis.RedisPool;

public class RedisPoolTest
{
	private RedisPool redisPool = new RedisPool("127.0.0.1", 6379);
	private String key1 = "redispool.test.key1";
	private String key2 = "redispool.test.key2";
	private String field1 = "redispool.test.field1";
	private String field2 = "redispool.test.field2";
	private String value1 = "redispool.test.value1";
	private String value2 = "redispool.test.value2";
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{

	} 
	
	@AfterClass
	public static void tearDownAfterClass() throws Exception
	{

	}
	
	@Before
	public void setUp() throws Exception
	{
		
	}
	
	@After
	public void tearDown() throws Exception
	{
		
	}
	
	@Test
	public void testHget()
	{

	}

	@Test
	public void testHset()
	{

	}

	@Test
	public void testHexists()
	{

	}

	@Test
	public void testHdel()
	{
		//redisPool.hset(key1, field1, value1);
		//redisPool.hset(key1, field2, value2);
		//redisPool.hdel(key1, field1);
	}

	@Test
	public void testDel()
	{
		//redisPool.hset(key1, field1, value1);
		//redisPool.hset(key1, field2, value2);
		//redisPool.del(key1);
	}

	@Test
	public void testHlen()
	{

	}

}
