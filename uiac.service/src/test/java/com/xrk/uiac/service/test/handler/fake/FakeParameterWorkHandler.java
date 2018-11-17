package com.xrk.uiac.service.test.handler.fake;

import java.util.Date;

import com.xrk.uiac.service.annotation.HttpMethod;
import com.xrk.uiac.service.annotation.HttpMethod.METHOD;
import com.xrk.uiac.service.annotation.HttpMethod.STATUS_CODE;
import com.xrk.uiac.service.annotation.HttpRouterInfo;
import com.xrk.uiac.service.handler.AbstractHttpWorkerHandler;

/**
 * 模拟测试基本参数的接收方法
 * FakeParameterWorkHandler: FakeParameterWorkHandler.java.
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：shunchiguo<shunchiguo@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年5月26日
 * <br> JDK版本：1.7
 * <br>==========================
 */
@HttpRouterInfo(router = "paramtest")
public class FakeParameterWorkHandler extends AbstractHttpWorkerHandler
{
		@HttpMethod(uri="", method=METHOD.GET, code=STATUS_CODE.OK)
		public String match_all(short s1, int i1, long l1, float f1, double d1, boolean b1, Date dt1, Date dt2)
		{
			return String.format("%s_%s_%s_%s_%s_%s_%s_%s", s1, i1, l1, f1, d1, b1, dt1.getTime(), dt2.getTime());
		}
}
