package com.xrk.uiac.common.utils;

import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 线程安全的排序列表类，适用于需要对列表内容进行排序的场景
 * ThreadSafeSortList: ThreadSafeSortList.java.
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：shunchiguo<shunchiguo@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年5月5日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public class ThreadSafeSortList<E extends SortEntity<?>> extends ArrayList<E>
{		
    private static final long serialVersionUID = 1L;
	private ReentrantLock lock;
	
	public ThreadSafeSortList()
	{
		super();
		lock = new ReentrantLock(true);		
	}
	
	public ThreadSafeSortList(int initialCapacity)
	{
		super(initialCapacity);
		lock = new ReentrantLock(true);		
	}
	
	@Override
    public int size() {
    	 final ReentrantLock lock = this.lock;
         lock.lock();
         try {
             return super.size();
         } finally {
             lock.unlock();
         }
    }
    
	@Override
    public boolean isEmpty() {
    	final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            return super.isEmpty();
        } finally {
            lock.unlock();
        }
    }
    
	@Override
    public Object[] toArray() {
		final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            return super.toArray();
        } finally {
            lock.unlock();
        }
    }
    
    @Override
    public <T> T[] toArray(T[] a) {
    	final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            return super.toArray(a);
        } finally {
            lock.unlock();
        }
    }
    
    @Override
    public E get(int index) {
    	final ReentrantLock lock = this.lock;
        lock.lock();
        try {        	
        	if(index >= super.size() || index < 0){
        		return null;
        	}
        	
            return super.get(index);
        } finally {
            lock.unlock();
        }
    }
    
    @Override
    public E set(int index, E element){
    	//to-do:nothing 禁止调用此方法
    	return null;
    }
    
    @Override
    public void add(int index, E element)
    {
    	// to-do:nothing 禁止调用此方法
    }
    
    @Override
    public boolean add(E e) {
    	return addData(e);
    }
    
    private int getIndex(E e)
    {
    	long key = e.getSortKey();
    	int size = super.size();
    	if(size == 0)
    	{
    		return 0;
    	}
    	
    	int start = 0;//开始位置 
    	int end = size-1;//结束位置
    	int pi = 0;
    	while(true)
    	{
    		pi = start + (end - start)/2;
    		if((end -start) < 2)
    		{
    			if(get(start).getSortKey() > key)
    			{
    				pi = start;
    				break;
    			}
    			
    			if(size == 1)
    			{
    				pi = 1;
    				break;
    			}
    			
    			if(get(end).getSortKey() > key)
    			{
    				pi = end;
    				break;
    			}
    			
    			pi = end+1;
    			break;
    		}
    		
    		long tmpKey = get(pi).getSortKey();
    		if(tmpKey == key)
    		{
    			break;
    		}
    		else if(tmpKey > key)
    		{
    			end = pi;
    		}
    		else
    		{
    			start = pi;
    		}
    	}
    	return pi;
    }
    
    private boolean addData(E e) {
    	final ReentrantLock lock = this.lock;
        lock.lock();
        try {
        	//将数据插入到指定位置
        	super.add(getIndex(e), e);
			return true;
        } finally {
            lock.unlock();
        }
    }
    
    @Override
    public E remove(int index) {
    	final ReentrantLock lock = this.lock;
        lock.lock();       
        try {
        	 if(index >= super.size() || index < 0){
         		return null;
         	}        	
            return super.remove(index);
        } finally {
            lock.unlock();
        }
    }
    
    @Override
    public boolean remove(Object o) {
    	final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            return super.remove(o);
        } finally {
            lock.unlock();
        }
    }
    
    @Override
    public void clear() {
    	final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            super.clear();
        } finally {
            lock.unlock();
        }
    }
}
