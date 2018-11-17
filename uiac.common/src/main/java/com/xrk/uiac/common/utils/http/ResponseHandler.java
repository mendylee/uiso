package com.xrk.uiac.common.utils.http;

/**
 * 抽象类：Response回调处理.
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：lijp<lijingping@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015-4-12
 * <br>==========================
 */
public abstract class ResponseHandler
{
	public abstract  void handle(Response response) throws Exception;
}
