package com.xrk.uiac.service.handler;

import com.xrk.hws.http.context.HttpContext;
import com.xrk.uiac.bll.cache.CacheService;
import com.xrk.uiac.service.annotation.HttpMethod;
import com.xrk.uiac.service.annotation.HttpMethod.METHOD;
import com.xrk.uiac.service.annotation.HttpMethod.STATUS_CODE;
import com.xrk.uiac.service.annotation.HttpRouterInfo;

@HttpRouterInfo(router = "Debug")
public class DebugHandler extends AbstractHttpWorkerHandler
{
	@HttpMethod(uri="/cache", method=METHOD.DELETE, code=STATUS_CODE.OK)
	public void cleanCache(CustomParameter head, HttpContext ctx)
	{
		CacheService.reset();
	}
}
