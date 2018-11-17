package com.xrk.uiac.service.test.handler.fake;

import com.xrk.hws.http.context.HttpContext;
import com.xrk.uiac.service.annotation.HttpMethod;
import com.xrk.uiac.service.annotation.HttpRouterInfo;
import com.xrk.uiac.service.annotation.HttpMethod.METHOD;
import com.xrk.uiac.service.annotation.HttpMethod.STATUS_CODE;
import com.xrk.uiac.service.handler.AbstractHttpWorkerHandler;
import com.xrk.uiac.service.handler.CustomParameter;

/**
 * 模拟测试处理器中各个处理方法的路由操作
 * FakeMethodWorkHandler: FakeMethodWorkHandler.java.
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：shunchiguo<shunchiguo@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年5月26日
 * <br> JDK版本：1.7
 * <br>==========================
 */
@HttpRouterInfo(router = "param")
public class FakeMethodWorkHandler extends AbstractHttpWorkerHandler
{
	public FakeMethodWorkHandler()
	{
		super();
	}
	
	//分组匹配
	@HttpMethod(uri="/group/(\\w+)/(\\d+)/([a-f])", method=METHOD.GET, code=STATUS_CODE.OK, priority=4)
	public String group_3(CustomParameter head, HttpContext ctx)
	{
		return String.format("%s_%s_%s", head.getUriGroup().get(0), head.getUriGroup().get(1), head.getUriGroup().get(2));
	}
	
	//仅作重载检测测试
	public String group_3(int i1, int i2, int i3, int i4)
	{
		return "";
	}
	
	//分组匹配,2个参数
	@HttpMethod(uri="/group/(\\w+)/(\\d+)", method=METHOD.GET, code=STATUS_CODE.OK, priority=5)
	public String group_2(CustomParameter head, HttpContext ctx)
	{
		return String.format("%s_%s", head.getUriGroup().get(0), head.getUriGroup().get(1));
	}
	
	//分组匹配,1个参数
	@HttpMethod(uri="/group/(\\w+)", method=METHOD.GET, code=STATUS_CODE.OK, priority=6)
	public String group_1(CustomParameter head, HttpContext ctx)
	{
		return String.format("%s", head.getUriGroup().get(0));
	}
	
	//正则式匹配
	@HttpMethod(uri="/group_\\d+", method=METHOD.GET, code=STATUS_CODE.OK, priority=7)
	public String regex(CustomParameter head, HttpContext ctx)
	{
		return "regex";
	}
	
	//正则式匹配
	@HttpMethod(uri="/(first|second|\\d+)", method=METHOD.GET, code=STATUS_CODE.OK, priority=7)
	public String mul_match(CustomParameter head, HttpContext ctx)
	{
		return head.getUriGroup().get(0);
	}
	
	//匹配字符串
	@HttpMethod(uri="/group", method=METHOD.GET, code=STATUS_CODE.OK, priority=8)
	public String match_str(CustomParameter head, HttpContext ctx)
	{
		return "group";
	}
	
	//匹配所有
	@HttpMethod(uri="", method=METHOD.GET, code=STATUS_CODE.OK)
	public String match_all(CustomParameter head, HttpContext ctx)
	{
		return "string";
	}
}
