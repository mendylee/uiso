package com.xrk.uiac.common.test.utils;

import java.lang.reflect.Array;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.xrk.uiac.common.utils.ThreadSafeSortList;
import com.xrk.uiac.common.utils.SortEntity;

/**
 * 
 * SafeSortListTest: SafeSortListTest.java.
 *
 * <br>=
 * ========================= <br>
 * 公司：广州向日葵信息科技有限公司 <br>
 * 开发：shunchiguo<shunchiguo@xiangrikui.com> <br>
 * 版本：1.0 <br>
 * 创建时间：2015年5月4日 <br>
 * JDK版本：1.7 <br>=
 * =========================
 */
public class ThreadSafeSortListTest
{
	@BeforeClass
	public static void SetUpClass()
	{
		
	}

	private ThreadSafeSortList<SortEntity<Integer>> list;

	@Before
	public void SetUp()
	{
		//System.out.println("Tear SetUp");
		list = new ThreadSafeSortList<SortEntity<Integer>>();
	}

	void shutdownAndAwaitTermination(ExecutorService pool, long timeOut)
	{
		pool.shutdown(); // Disable new tasks from being submitted
		try {
			// Wait a while for existing tasks to terminate
			if (!pool.awaitTermination(timeOut, TimeUnit.SECONDS)) {
				pool.shutdownNow(); // Cancel currently executing tasks
				// Wait a while for tasks to respond to being cancelled
				if (!pool.awaitTermination(timeOut, TimeUnit.SECONDS))
					System.err.println("Pool did not terminate");
			}
		}
		catch (InterruptedException ie) {
			// (Re-)Cancel if current thread also interrupted
			pool.shutdownNow();
			// Preserve interrupt status
			Thread.currentThread().interrupt();
		}
	}

	@Test
	public void testAdd()
	{
		System.out.println("Test ThreadSafeSortList.Add");
		// 增加排序对象
		for (int i = 0; i < 100; i++) {
			SortEntity<Integer> entity = new SortEntity<Integer>(i, i + 1);
			Assert.assertTrue(list.add(entity));
		}

		// 同一个对象修改值后增加两次
		SortEntity<Integer> entity1 = new SortEntity<Integer>(1000, 10001);
		list.add(entity1);
		entity1.setSortKey(988);
		entity1.setValue(989);
		list.add(entity1);

		Assert.assertEquals(102, list.size());
		SortEntity<Integer> tmp1 = list.get(100);
		SortEntity<Integer> tmp2 = list.get(101);
		Assert.assertEquals(tmp1.getSortKey(), tmp2.getSortKey());
		Assert.assertEquals(tmp1.getValue(), tmp2.getValue());
		Assert.assertEquals(988, tmp1.getSortKey());

		list.remove(101);
		Assert.assertEquals(101, list.size());
		tmp1 = list.get(100);
		Assert.assertEquals(988, tmp1.getSortKey());

		// 判断中间的值是否正确
		tmp1 = list.get(51);
		Assert.assertEquals(51, tmp1.getSortKey());
		Integer val = 52;
		Assert.assertEquals(val, tmp1.getValue());

		// 判断超出范围的值，应该返回空值
		Assert.assertNull(list.get(552));
		Assert.assertNull(list.get(-1));

		// 在排序数组中间插入一个值
		val = 51101;
		entity1 = new SortEntity<Integer>(51, val);
		list.add(entity1);

		tmp1 = list.get(51);
		Assert.assertEquals(51, tmp1.getSortKey());
		Assert.assertEquals(val, tmp1.getValue());

		val = 99951101;
		entity1 = new SortEntity<Integer>(72, val);
		list.add(entity1);
		tmp1 = list.get(73);
		Assert.assertEquals(72, tmp1.getSortKey());
		Assert.assertEquals(val, tmp1.getValue());

		list.clear();
		Assert.assertEquals(0, list.size());

		// 测试多线程操作
		final Random random = new Random();
		final int tot = 100;// 每个线程添加的队列数
		final int ThreadCount = 50;// 线程总数
		final ThreadSafeSortList<SortEntity<Integer>> list1 = new ThreadSafeSortList<SortEntity<Integer>>(
		                                                                                                  tot
		                                                                                                          * ThreadCount);
		ExecutorService pool = Executors.newFixedThreadPool(ThreadCount);
		for (int j = 0; j < ThreadCount; j++) {
			pool.execute(new Runnable() {
				public void run()
				{
					if ((random.nextInt(10) % 2) == 0) {
						for (int i = 0; i < tot; i++) {
							try {

								Thread.sleep(random.nextInt(100));
								list1.add(new SortEntity<Integer>(i, i));

								// int threadId =
								// (int)Thread.currentThread().getId();
								// System.out.printf("tid:%s, sort val:%s\n",
								// threadId, i);
							}
							catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
					else {
						for (int i = tot - 1; i >= 0; i--) {
							try {

								Thread.sleep(random.nextInt(100));
								list1.add(new SortEntity<Integer>(i, i));
								// int threadId =
								// (int)Thread.currentThread().getId();
								// System.out.printf("tid:%s, sort val:%s\n",
								// threadId, i);
							}
							catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
				}
			});
		}

		shutdownAndAwaitTermination(pool, 120);

		// 排序列表总数为线程数*每个线程添加的数量
		Assert.assertEquals(tot * ThreadCount, list1.size());
		// 对于排序结果，每个线程周期的第一个和最后一个值总是相同的
		for (int i = 0; i < ThreadCount; i++) {
			int idx = i * ThreadCount;
			int idx_e = idx + ThreadCount - 1;
			tmp1 = list1.get(idx);
			tmp2 = list1.get(idx_e);

			val = i;
			Assert.assertEquals(i, tmp1.getSortKey());
			Assert.assertEquals(val, tmp1.getValue());
			Assert.assertEquals(i, tmp2.getSortKey());
			Assert.assertEquals(val, tmp2.getValue());

			Assert.assertEquals(tmp2.getSortKey(), tmp1.getSortKey());
			Assert.assertEquals(tmp2.getValue(), tmp1.getValue());
		}
		list1.clear();
	}

	@Test
	public void testRemove()
	{
		System.out.println("Test ThreadSafeSortList.Remove");
		int tot = 100;
		// 增加排序对象
		for (int i = 0; i < tot; i++) {
			SortEntity<Integer> entity = new SortEntity<Integer>(i, i + 1);
			Assert.assertTrue(list.add(entity));
		}

		list.remove(10);
		Assert.assertEquals(tot - 1, list.size());

		list.remove(10);
		Assert.assertEquals(tot - 2, list.size());

		Integer intObj = new Integer(20);
		Assert.assertFalse(list.remove(intObj));
		
		list.remove(20);
		Assert.assertEquals(tot - 3, list.size());
		
		Object o = new Object();
		Assert.assertFalse(list.remove(o));

		for (int i = 0; i < 20; i++) {
			list.remove(i + 2);
		}
		Assert.assertEquals(tot - 23, list.size());

		Assert.assertNull(list.remove(tot));
		Assert.assertNull(list.remove(-1));

		while (list.size() > 0) {
			list.remove(0);
		}
		Assert.assertEquals(0, list.size());
		list.clear();

		// 测试多线程移除操作
		final Random random = new Random();
		final int totNum = 100;// 每个线程添加的队列数
		final int ThreadCount = 50;// 线程总数
		tot = totNum * ThreadCount;// 队列总大小
		final ThreadSafeSortList<SortEntity<Integer>> list1 = new ThreadSafeSortList<SortEntity<Integer>>(
		                                                                                                  tot);
		for (int i = 0; i < tot; i++) {
			SortEntity<Integer> entity = new SortEntity<Integer>(i, i + 1);
			Assert.assertTrue(list1.add(entity));
		}
		Assert.assertEquals(tot, list1.size());

		// 每次删除队列第一个数据
		//System.out.println("Test Multi threading remove index zero begin.");
		ExecutorService pool = Executors.newFixedThreadPool(ThreadCount);
		for (int j = 0; j < ThreadCount; j++) {
			pool.execute(new Runnable() {
				public void run()
				{
					if ((random.nextInt(10) % 2) == 0) {
						for (int i = 0; i < totNum; i++) {
							try {

								Thread.sleep(random.nextInt(100));
								list1.remove(0);
							}
							catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
					else {
						for (int i = totNum - 1; i >= 0; i--) {
							try {

								Thread.sleep(random.nextInt(100));
								list1.remove(0);
							}
							catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
				}
			});
		}

		shutdownAndAwaitTermination(pool, 120);
		//System.out.println("Test Multi threading remove index zero end.");

		// 排序列表总数为线程数*每个线程添加的数量
		Assert.assertEquals(0, list1.size());

		for (int i = 0; i < tot; i++) {
			SortEntity<Integer> entity = new SortEntity<Integer>(i, i + 1);
			Assert.assertTrue(list1.add(entity));
		}
		Assert.assertEquals(tot, list1.size());
		//System.out.println("Test Multi threading remove random index begin.");
		// 每次乱序删除队列中的数据
		pool = Executors.newFixedThreadPool(ThreadCount);
		for (int j = 0; j < ThreadCount; j++) {
			pool.execute(new Runnable() {
				public void run()
				{
					for (int i = 0; i < totNum; i++) {
						try {							
							Thread.sleep(random.nextInt(100));
							int size = list1.size() - 1;
							int removeIndex = 0;
							if(size > 0)
							{
								removeIndex = random.nextInt(size);
							}
							//乱序删除可能会失败，因此需要有异常检测
							while (list1.remove(removeIndex) == null && size >= 0) {
								//System.out.printf("tid:%s, tname:%s, remove failed:%s\n", Thread
								//        .currentThread().getId(), Thread.currentThread().getName(), removeIndex);
								size = list1.size() - 1;
								removeIndex = 0;
								if(size > 0)
								{
									removeIndex = random.nextInt(size);
								}
							}							
						}
						catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			});
		}
		shutdownAndAwaitTermination(pool, 120);
		//System.out.println("Test Multi threading remove random index end.");
		Assert.assertEquals(0, list1.size());
		list1.clear();
	}

	@Test
	public void testSet()
	{
		System.out.println("Test ThreadSafeSortList.Set");
		int tot =100;		
		for (int i = 0; i < tot; i++) {
			SortEntity<Integer> entity = new SortEntity<Integer>(i, i + 1);
			Assert.assertTrue(list.add(entity));
		}
		// 测试方法
		Assert.assertNull(list.set(0, new SortEntity<Integer>(1, 1)));
		Assert.assertNull(list.set(99, new SortEntity<Integer>(2, 1)));
		Assert.assertNull(list.set(100, new SortEntity<Integer>(2, 1)));
		Assert.assertNull(list.set(2100, new SortEntity<Integer>(2, 1)));
		
		list.clear();
	}

	@Test
	public void testGet()
	{
		System.out.println("Test ThreadSafeSortList.Get");
		final int tot =100;
		final int ThreadCount = 50;// 线程总数
		final Random random = new Random();
		final ThreadSafeSortList<SortEntity<Integer>> list1 = new ThreadSafeSortList<SortEntity<Integer>>(tot);
		
		Assert.assertNull(list1.get(-222));
		Assert.assertNull(list1.get(32));
		Assert.assertTrue(list1.isEmpty());
		Object[] obj =list1.toArray();
		Assert.assertEquals(0, obj.length);
		
		@SuppressWarnings("unchecked")
        SortEntity<Integer>[] tempAry = (SortEntity<Integer>[]) Array.newInstance(SortEntity.class, 0);		
		tempAry = list1.toArray(tempAry);
		//if(obj)
		
		for (int i = 0; i < tot; i++) {
			SortEntity<Integer> entity = new SortEntity<Integer>(i, i + 1);
			Assert.assertTrue(list1.add(entity));
		}
		
		Assert.assertEquals(tot, list1.size());
		Assert.assertEquals(0, list1.get(0).getSortKey());
		Assert.assertEquals(10, list1.get(10).getSortKey());
		Assert.assertNull(list1.get(120));
		
		//多线程读取
		ExecutorService pool = Executors.newFixedThreadPool(ThreadCount);
		for (int j = 0; j < ThreadCount; j++) {
			pool.execute(new Runnable() {
				public void run()
				{					
						for (int i = 0; i < tot; i++) {
							try {

								Thread.sleep(random.nextInt(100));
								Assert.assertEquals(i, list1.get(i).getSortKey());
							}
							catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
			});
		}
		shutdownAndAwaitTermination(pool, 120);
		list1.clear();
	}

	@After
	public void tearDown() throws Exception
	{
		list.clear();
		list = null; 
		//System.out.println("Tear down");
	}

	@AfterClass
	public static void tearDownAfterClass()
	{
		System.out.println("Tear down After class");
	}
}
