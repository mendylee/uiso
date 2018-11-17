package com.xrk.uiac.bll.test.common;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.xrk.uiac.bll.cache.CacheService;
import com.xrk.uiac.bll.common.SeqUtils;
import com.xrk.uiac.dal.DalTestHelper;

public class SeqUtilsPerformanceTest
{
	private static ExecutorService bgThread = Executors.newFixedThreadPool(50);
	
	public static void main(String[] args)
	{
		DalTestHelper.initDal();
		CacheService.Init();

		for (int i=0; i<50; i++)
		{	
			bgThread.execute(new Runnable()
			{
				@Override
				public void run()
				{
					for (int i=0; i<50000; i++)
					{
						System.out.println(Thread.currentThread().getName() + " : " + SeqUtils.getUid());
					}
				}
			});
		}
	}
}