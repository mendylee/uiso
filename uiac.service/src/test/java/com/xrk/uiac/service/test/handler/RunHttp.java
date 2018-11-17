package com.xrk.uiac.service.test.handler;

import java.net.InetSocketAddress;

import com.xrk.hws.http.HttpServer;
import com.xrk.uiac.service.handler.AbstractHttpWorkerHandler;
import com.xrk.uiac.service.test.handler.fake.FakeExceptionWorkHandler;
import com.xrk.uiac.service.test.handler.fake.FakeHttpWorkerHandler;
import com.xrk.uiac.service.test.handler.fake.FakeMethodWorkHandler;
import com.xrk.uiac.service.test.handler.fake.FakeParameterWorkHandler;

/**
 * 运行HTTP服务对象
 * RunHttp: RunHttp.java.
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：shunchiguo<shunchiguo@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年5月26日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public class RunHttp implements Runnable
{
	private int httpPort = 8081;
	public RunHttp(int port)
	{
		httpPort = port;
	}
    public void run()
    {
		HttpServer server = new HttpServer();
		server.init(3, 8, 3, null);
		server.addListen(new InetSocketAddress(httpPort));
		
		AbstractHttpWorkerHandler handler = new FakeHttpWorkerHandler();
		handler.register(server);
		handler = new FakeMethodWorkHandler();
		handler.register(server);
		handler = new FakeParameterWorkHandler();
		handler.register(server);
		handler = new FakeExceptionWorkHandler();
		handler.register(server);
		
		server.run();
		System.out.println("http server stop!");
    }
} 