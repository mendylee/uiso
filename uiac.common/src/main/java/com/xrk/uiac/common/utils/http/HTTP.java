package com.xrk.uiac.common.utils.http;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.CheckedFuture;
import com.ning.http.client.cookie.Cookie;
import com.xrk.hws.common.logger.Logger;
import com.xrk.uiac.common.utils.PThreadFactory;

/**
*
* HTTP客户端
*
* <br>==========================
* <br> 公司：广州向日葵信息科技有限公司
* <br> 开发：lijp<lijingping@xiangrikui.com>
* <br> 版本：1.0
* <br> 创建时间：2015-4-12
* <br>==========================
*/
public class HTTP {
	private static int http_callback_pool = 16;
	static {
		http_callback_pool = Integer.parseInt("16");
	}
	
	private static Executor executor = new ScheduledThreadPoolExecutor(http_callback_pool, new PThreadFactory("http_callback_executor"), new ThreadPoolExecutor.AbortPolicy());
	
	/**
	 * 设置async异步访问http的回调Executor
	 * @param executor
	 */
	public static void setAsyncExecutor(Executor executor) {
		HTTP.executor = executor;
	}
	
	/** 
	 * GET请求 
	 * <br>请求处理
	 * <br>Request request = new Request("http://www.xiangrikui.com", 5);
	 * <br>request.addHeader("Content-Type", "text/html");
	 * <br>响应处理
	 * <br>Response response = HTTP.GET(request, 5);
	 * <br>int code = response.getStatusCode();
	 * <br>String content = response.getContent("UTF-8");
	 * */
	public static Response GET(Request request, int seconds) {
        CheckedFuture<com.ning.http.client.Response, HttpIoException> future = SimpleHttpClient.getInstance().doGet(request.url, request.getHeaders(), request.getClientCookies(), null);
        try {
        	com.ning.http.client.Response response= future.checkedGet(seconds, TimeUnit.SECONDS);
        	if (response==null) {
        		throw new RuntimeException("cannot connected to server");
        	}
            return new Response(response);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
	}
	
	/**
	 * GET请求，批量并发，异步回调
	 * @param requests
	 * @param seconds
	 * @return
	 */
	public static List<Response> multiGET(List<Request> requests, final int seconds) {
		List<Future<com.ning.http.client.Response>> futureList = Lists.newLinkedList();
		for (Request request : requests) {
			CheckedFuture<com.ning.http.client.Response, HttpIoException> future = SimpleHttpClient.getInstance().doGet(request.url, request.getHeaders(), request.getClientCookies(), null);
			futureList.add(future);
		}
        List<com.ning.http.client.Response> res = Lists.transform(futureList, new Function<Future<com.ning.http.client.Response>, com.ning.http.client.Response>() {
            @Override
            public com.ning.http.client.Response apply(Future<com.ning.http.client.Response> input) {
                try {
                    return input.get(seconds, TimeUnit.SECONDS);
                } catch (Exception e) {
                    throw new HttpIoException(e);
                }
            }
        });
        
        List<Response> results = new ArrayList<Response>();
        for (com.ning.http.client.Response r : res) {
        	results.add(new Response(r));
        }
        return results;
	}
	
	/**
	 * GET请求，异步处理
	 * <br>HTTP.asyncGET(request, new ResponseHandler() {
	 * <br>    @Override
	 * <br>    public void handle(Response response) throws Exception {
	 * <br>    }
	 * <br>});
	 * @param request
	 * @param handler
	 */
	public static void asyncGET(Request request, final int seconds, ResponseHandler handler) {
        final CheckedFuture<com.ning.http.client.Response, HttpIoException> future = SimpleHttpClient.getInstance().doGet(request.url, request.getHeaders(), request.getClientCookies(), null);
        try {
        	future.addListener(new CallBack(handler) {
				@Override
				public void run() {
					com.ning.http.client.Response response = null;
					try {
						response = future.get(seconds, TimeUnit.SECONDS);
						if (response==null) {
							Logger.error("cannot connected to http server");
							handler.handle(null);
							return;
						}
						handler.handle(new Response(response));
					} catch (Exception e) {
						Logger.error(e, "ResponseHandler.handle error");
					}
				}
			}, executor);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
	}
	
	/**
	 * POST请求
	 * <br>请求处理
	 * <br>Request request = new Request("http://www.xiangrikui.com");
	 * <br>request.addHeader("Content-Type", "text/html");
	 * <br>request.addParam("user","1001");
	 * <br>request.setBody(buff);   使用该接口，addParam()将失效 
	 * <br>
	 * <br>响应处理
	 * <br>Response response = HTTP.POST(request, 5);
	 * <br>int code = response.getStatusCode();
	 * <br>String content = response.getContent("UTF-8");
	 * */
	public static Response POST(Request request, int seconds) {
		String url = request.url;
		Map<String, String> params = request.getParams();
		Map<String, String> headers = request.getHeaders();
		List<Cookie> cookies = request.getClientCookies();
		if (request.getBody() != null) {
			params = null;
		}
        CheckedFuture<com.ning.http.client.Response, HttpIoException> future = SimpleHttpClient.getInstance().doPostAll(url, params, headers, cookies, request.getBody(), null);
        try {
        	com.ning.http.client.Response response = future.checkedGet(seconds, TimeUnit.SECONDS);
        	if (response==null) {
        		throw new RuntimeException("cannot connected to server");
        	}
            return new Response(response);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
	}
	
	/**
	 * POST请求，批量并发，异步回调
	 * @param requests
	 * @param seconds
	 * @return
	 */
	public static List<Response> multiPOST(List<Request> requests, final int seconds) {
		List<Future<com.ning.http.client.Response>> futureList = Lists.newLinkedList();
		for (Request request : requests) {
			String url = request.url;
			Map<String, String> params = request.getParams();
			Map<String, String> headers = request.getHeaders();
			List<Cookie> cookies = request.getClientCookies();
			if (request.getBody() != null) {
				params = null;
			}
			CheckedFuture<com.ning.http.client.Response, HttpIoException> future = SimpleHttpClient.getInstance().doPostAll(url, params, headers, cookies, request.getBody(), null);
			futureList.add(future);
		}
        List<com.ning.http.client.Response> res = Lists.transform(futureList, new Function<Future<com.ning.http.client.Response>, com.ning.http.client.Response>() {
            @Override
            public com.ning.http.client.Response apply(Future<com.ning.http.client.Response> input) {
                try {
                    return input.get(seconds, TimeUnit.SECONDS);
                } catch (Exception e) {
                    throw new HttpIoException(e);
                }
            }
        });
        
        List<Response> results = new ArrayList<Response>();
        for (com.ning.http.client.Response r : res) {
        	results.add(new Response(r));
        }
        return results;
	}
	
	/**
	 * POST请求，异步处理
	 * <br>HTTP.asyncPOST(request, new ResponseHandler() {
	 * <br>    @Override
	 * <br>    public void handle(Response response) throws Exception {
	 * <br>    }
	 * <br>});
	 * @param request
	 * @param handler
	 */
	public static void asyncPOST(Request request, final int seconds, ResponseHandler handler) {
		String url = request.url;
		Map<String, String> params = request.getParams();
		Map<String, String> headers = request.getHeaders();
		List<Cookie> cookies = request.getClientCookies();
		if (request.getBody() != null) {
			params = null;
		}
        final CheckedFuture<com.ning.http.client.Response, HttpIoException> future = SimpleHttpClient.getInstance().doPostAll(url, params, headers, cookies, request.getBody(), null);
        try {
        	future.addListener(new CallBack(handler) {
				@Override
				public void run() {
					com.ning.http.client.Response response = null;
					try {
						response = future.get(seconds, TimeUnit.SECONDS);
						if (response==null) {
							Logger.error("cannot connected to http server");
							handler.handle(null);
							return;
						}
						handler.handle(new Response(response));
					} catch (Exception e) {
						Logger.error(e, "ResponseHandler.handle error");
					}
				}
			}, executor);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
	}
}
