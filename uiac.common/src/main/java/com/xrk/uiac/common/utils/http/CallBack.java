package com.xrk.uiac.common.utils.http;

/**
*
* HTTP异步请求的回调封装，嵌入ResponseHandler方便开发处理
*
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：lijp<lijingping@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015-4-12
 * <br>==========================
*/
public abstract class CallBack implements Runnable {
	
	protected ResponseHandler handler;
	
	public CallBack(ResponseHandler handler) {
		this.handler = handler;
	}
	
	@Override
	public abstract void run();
}