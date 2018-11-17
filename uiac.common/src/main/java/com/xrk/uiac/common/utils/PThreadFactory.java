package com.xrk.uiac.common.utils;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 
 * PThreadFactory: PThreadFactory.java.
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：zhub<zhubin@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年5月18日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public class PThreadFactory implements ThreadFactory 
{
	/**
	 * 线程组.
	 */
    final ThreadGroup group;
    /**
     * 线程号.
     */
    final AtomicInteger threadNumber = new AtomicInteger(1);
    /**
     * 线程名称前缀.
     */
    final String namePrefix;

    public PThreadFactory(String poolName) 
    {
        SecurityManager s = System.getSecurityManager();
        group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
        namePrefix = poolName + "-thread-";
    }

    public Thread newThread(Runnable r) 
    {
        Thread t = new Thread(group, r, namePrefix + threadNumber.getAndIncrement(), 0);
        if (t.isDaemon()) {
            t.setDaemon(false);
        }
        if (t.getPriority() != Thread.NORM_PRIORITY) 
        {
            t.setPriority(Thread.NORM_PRIORITY);
        }
        return t;
    }

}
