package com.xrk.uiac.bll.entity;

import java.util.concurrent.LinkedBlockingDeque;

public class PushQueueEntity
{
	private LinkedBlockingDeque<String> queue;
	
	public LinkedBlockingDeque<String> getQueue()
	{
		return queue;
	}
	
	public void setQueue(LinkedBlockingDeque<String> queue)
	{
		this.queue = queue;
	}
}
