package com.xrk.uiac.service.test.handler.fake;

import com.xrk.hws.http.context.HttpContext;
import com.xrk.uiac.service.annotation.HttpMethod;
import com.xrk.uiac.service.annotation.HttpRouterInfo;
import com.xrk.uiac.service.annotation.HttpMethod.METHOD;
import com.xrk.uiac.service.annotation.HttpMethod.STATUS_CODE;
import com.xrk.uiac.service.handler.AbstractHttpWorkerHandler;
import com.xrk.uiac.service.handler.CustomParameter;

/**
 * 模拟测试HTTP处理器的路由方法
 * FakeHttpWorkerHandler: FakeHttpWorkerHandler.java.
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：shunchiguo<shunchiguo@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年5月26日
 * <br> JDK版本：1.7
 * <br>==========================
 */
@HttpRouterInfo(router = "test_\\d+_(a|b)")
public class FakeHttpWorkerHandler extends AbstractHttpWorkerHandler
{
	public FakeHttpWorkerHandler()
	{
		super();
	}

	@HttpMethod(uri="/noparam", method=METHOD.POST, code=STATUS_CODE.NO_CONTENT)
	public void noParamPost(CustomParameter head, HttpContext ctx)
	{
		
	}
	
	@HttpMethod(uri="/noparam", method=METHOD.GET, code=STATUS_CODE.OK)
	public boolean noParamGet(CustomParameter head, HttpContext ctx)
	{
		return true;
	}
	
	@HttpMethod(uri="/noparam/(\\d+)", method=METHOD.GET, code=STATUS_CODE.OK, priority=9)
	public String noParamGetId(CustomParameter head, HttpContext ctx)
	{
		return head.getUriGroup().get(0);
	}
	
	@HttpMethod(uri="/noparam/(\\d+)/(\\w+)", method=METHOD.GET, code=STATUS_CODE.OK, priority=8)
	public String noParamGetMulId(CustomParameter head, HttpContext ctx)
	{
		return head.getUriGroup().get(0)+"_"+ head.getUriGroup().get(1);
	}
	
	@HttpMethod(uri="/noparam", method=METHOD.PUT, code=STATUS_CODE.CREATED)
	public String noParamPut(int p1, String p2, boolean p3, CustomParameter head, HttpContext ctx)
	{
		if(p3)
		{
			return String.format("%s_%s", p2, p1);
		}
		else
		{
			return String.format("%s_%s", p1, p2);
		}
	}
	
	@HttpMethod(uri="/noparam", method=METHOD.DELETE, code=STATUS_CODE.NO_CONTENT)
	public void noParamDelete(CustomParameter head, HttpContext ctx)
	{
		String version = head.getClientVersion();
		String appId = head.getAppId();
		String token = head.getAccessToken();
		String uri = head.getUri();
		String tmp = String.format("\"%s_%s_%s_%s\"", version, appId, token, uri);
		ctx.response.headers().add("TMP_DELETE", tmp);
	}	
}
