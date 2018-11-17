package com.xrk.uiac.dal.test;

import java.util.concurrent.atomic.AtomicLong;

import com.xrk.uiac.dal.dao.UserDAO;

public class TestUtils
{
	private static AtomicLong atom_uid = new AtomicLong(800000000);
	
	/**
	 * 
	 * 获取伪uid的临时方法 
	 *    
	 * @return
	 */
	public static long getUid()
	{
		return atom_uid.getAndIncrement();
	}
}
