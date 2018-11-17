package com.xrk.uiac.common.utils.http;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.CheckedFuture;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.AsyncHttpClient.BoundRequestBuilder;
import com.ning.http.client.AsyncHttpClientConfig;
import com.ning.http.client.FluentCaseInsensitiveStringsMap;
import com.ning.http.client.Response;
import com.ning.http.client.SignatureCalculator;
import com.ning.http.client.cookie.Cookie;
import com.ning.http.client.generators.ByteArrayBodyGenerator;
import com.ning.http.client.providers.netty.NettyAsyncHttpProviderConfig;

/**
 * 异步Http接口工具类
 * <p>一般情况下，直接使用SimpleHttpClient.me()这个默认的客户端作调用</p>
 * <p>需要自订参数时，直接构建<code>SimpleHttpClient.HttpClientBuilder</code>
 * 这个Builder来创建SimpleHttpClient实例</p>
 *
* <br>==========================
* <br> 公司：广州向日葵信息科技有限公司
* <br> 开发：lijp<lijingping@xiangrikui.com>
* <br> 版本：1.0
* <br> 创建时间：2015-4-12
* <br>==========================
 */
public final class SimpleHttpClient {

	private static int http_timeout;
	private static int http_connect_timeout;
	private static boolean http_tcpNoDelay;
	private static boolean http_keepAlive;
	static {
		http_timeout = Integer.parseInt("60000");
		http_connect_timeout = Integer.parseInt("5000");
		http_tcpNoDelay="true".equals("true");
		http_keepAlive="true".equals("true");
	}
	
    private static SimpleHttpClient instance = new Builder().setCompress(true).setConnectTimeout(http_connect_timeout).setReadTimeout(http_timeout).build();
    private static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

    private AsyncHttpClient client;

    /**
     * 供方便时使用的Executor，不要在里面
     */
    public static ExecutorService DEFAULT_EXECUTOR = Executors.newFixedThreadPool(
            Runtime.getRuntime().availableProcessors(), new ThreadFactoryBuilder().setDaemon(true).build());

    private SimpleHttpClient() {
    }

    /**
     * 获取默认的Http客户端
     * @return
     */
    public static SimpleHttpClient getInstance() {
        return instance;
    }

    /**
     * 一般在容器生命周期结束时调用
     */
    public void shutdown() {
        client.close();
    }

    /**
     * 执行一个异步GET调用
     * @param url 请求url
     * @return 响应的Future对象
     * @throws HttpIoException
     */
    public CheckedFuture<Response, HttpIoException> doGet(String url) {
        ListenableFuture<Response> future = new ListenableFutureAdapter<Response>(
		        client.prepareGet(url).execute());
		return Futures.makeChecked(future, new Function<Exception, HttpIoException>() {
		    @Override
		    public HttpIoException apply(Exception input) {
		        return new HttpIoException(input);
		    }
		});
    }

    public CheckedFuture<Response, HttpIoException> doGet(String url, Map<String, String> headers, List<Cookie> cookies, SignatureCalculator calculator) {
        BoundRequestBuilder builder = client.prepareGet(url);
		if (calculator != null) {
		    builder.setSignatureCalculator(calculator);
		}
		if (headers != null) {
		    FluentCaseInsensitiveStringsMap headerMap = new FluentCaseInsensitiveStringsMap();
		    for (Map.Entry<String, String> entry:headers.entrySet()) {
		    	headerMap.add(entry.getKey(), entry.getValue());
		    }
		    builder = builder.setHeaders(headerMap);
		}
		if (cookies != null) {
			for(Cookie cookie : cookies) {
				builder = builder.addCookie(cookie);
			}
		}
		ListenableFuture<Response> future = new ListenableFutureAdapter<Response>(builder.execute());
		return Futures.makeChecked(future, new Function<Exception, HttpIoException>() {
		    @Override
		    public HttpIoException apply(Exception input) {
		        return new HttpIoException(input);
		    }
		});
    }

    /**
     * 执行一个异步 POST 调用
     * @param url 请求url
     * @param para 请求体参数对
     * @return 响应的Future对象
     * @throws HttpIoException
     */
    public CheckedFuture<Response, HttpIoException> doPost(String url, Map<String, String> para) {
        return doPostAll(url, para, null, null, null);
    }

    /**
     * 执行一个异步 POST 调用
     * @param url 请求url
     * @param body post body内容
     * @return 响应的Future对象
     * @throws HttpIoException
     */
    public CheckedFuture<Response, HttpIoException> doPost(String url, String body) {
        return doPost(url, null, null, body, null);
    }

    /**
     * 执行一个异步 POST 调用
     * @param url 请求url
     * @param body http post body
     * @return 响应的Future对象
     * @throws HttpIoException
     */
    public CheckedFuture<Response, HttpIoException> doPost(String url, byte[] body) {
        return doPost(url, null, null, body, null);
    }

    /**
     * 执行一个异步 POST 调用
     * @param url 请求url
     * @param headers 请求headers
     * @param body http post body
     * @return 响应的Future对象
     * @throws HttpIoException
     */
    public CheckedFuture<Response, HttpIoException> doPost(String url, Map<String, String> headers, String body) {
    	return doPost(url, null, headers, body, null);
    }

    /**
     * 执行一个异步 POST 调用
     * @param url 请求url
     * @param headers 请求headers
     * @param body http post body
     * @return 响应的Future对象
     * @throws HttpIoException
     */
    public CheckedFuture<Response, HttpIoException> doPost(String url, Map<String, String> headers, byte[] body) {
    	return doPost(url, null, headers, body, null);
    }

    /**
     * 执行一个异步 POST 调用
     * @param url 请求url
     * @param para 请求体参数对
     * @param headers 请求headers
     * @return 响应的Future对象
     * @throws HttpIoException
     */
    public CheckedFuture<Response, HttpIoException> doPost(String url, Map<String, String> para, Map<String, String> headers) {
    	return doPostAll(url, para, headers, null, null);
    }

    /**
     * 执行一个异步 POST 调用，带签名计算
     * @param url 请求url
     * @param para 请求体参数对
     * @param calculator 签名算法，一般为UcSignatureCalculator
     * @return 响应的Future对象
     * @throws HttpIoException
     */
    public CheckedFuture<Response, HttpIoException> doPost(String url, Map<String, String> para, SignatureCalculator calculator) {
        return doPostAll(url, para, null, null, calculator);
    }

    /**
     * 执行一个异步 POST 调用，带签名计算
     * @param url 请求url
     * @param para 请求体参数对
     * @param headers 请求headers
     * @param calculator 签名算法，一般为UcSignatureCalculator
     * @return 响应的Future对象
     * @throws HttpIoException
     */
    public CheckedFuture<Response, HttpIoException> doPost(String url, Map<String, String> para, Map<String, String> headers, SignatureCalculator calculator) {
    	return doPostAll(url, para, headers, null, calculator);
    }

    /**
     * 执行一个异步 POST 调用，带签名计算
     * @param url 请求url
     * @param para 请求体参数对
     * @param headers 请求headers
     * @param body http post内容
     * @param calculator 签名算法，一般为UcSignatureCalculator
     * @return 响应的Future对象
     * @throws HttpIoException
     */
    public CheckedFuture<Response, HttpIoException> doPost(String url, Map<String, String> para, Map<String, String> headers, String body, SignatureCalculator calculator) {
    	return doPostAll(url, para, headers, body == null ? null : body.getBytes(DEFAULT_CHARSET), calculator);
    }

    /**
     * 执行一个异步 POST 调用，带签名计算
     * @param url 请求url
     * @param para 请求体参数对
     * @param headers 请求headers
     * @param body http post内容
     * @param calculator 签名算法，一般为UcSignatureCalculator
     * @return 响应的Future对象
     * @throws HttpIoException
     */
    public CheckedFuture<Response, HttpIoException> doPost(String url, Map<String, String> para, Map<String, String> headers, byte[] body, SignatureCalculator calculator) {
    	return doPostAll(url, para, headers, body, calculator);
    }

    private CheckedFuture<Response, HttpIoException> doPostAll(String url, Map<String, String> para, Map<String, String> headers, byte[] body, SignatureCalculator calculator) {
        BoundRequestBuilder builder = client.preparePost(url);
		if (calculator != null) {
		    builder.setSignatureCalculator(calculator);
		}
		if (headers != null) {
		    FluentCaseInsensitiveStringsMap headerMap = new FluentCaseInsensitiveStringsMap();
		    for (Map.Entry<String, String> entry:headers.entrySet()) {
		    	headerMap.add(entry.getKey(), entry.getValue());
		    }
		    builder = builder.setHeaders(headerMap);
		}
		if (para != null) {
		    for (Map.Entry<String, String> entry:para.entrySet()) {
		        builder = builder.addQueryParam(entry.getKey(), entry.getValue());
		    }
		}
		if (body != null) {
			builder.setBody(new ByteArrayBodyGenerator(body));
		}
		ListenableFuture<Response> future = new ListenableFutureAdapter<Response>(builder.execute());
		return Futures.makeChecked(future, new Function<Exception, HttpIoException>() {
		    @Override
		    public HttpIoException apply(Exception input) {
		        return new HttpIoException(input);
		    }
		});
    }
    
    public CheckedFuture<Response, HttpIoException> doPostAll(String url, Map<String, String> para, Map<String, String> headers, List<Cookie> cookies, byte[] body, SignatureCalculator calculator) {
        BoundRequestBuilder builder = client.preparePost(url);
		if (calculator != null) {
		    builder.setSignatureCalculator(calculator);
		}
		if (headers != null) {
		    FluentCaseInsensitiveStringsMap headerMap = new FluentCaseInsensitiveStringsMap();
		    for (Map.Entry<String, String> entry:headers.entrySet()) {
		    	headerMap.add(entry.getKey(), entry.getValue());
		    }
		    builder = builder.setHeaders(headerMap);
		}
		if (para != null) {
		    for (Map.Entry<String, String> entry:para.entrySet()) {
		        builder = builder.addQueryParam(entry.getKey(), entry.getValue());
		    }
		}
		if (cookies != null) {
			for(Cookie cookie : cookies) {
				builder = builder.addCookie(cookie);
			}
		}
		
		if (body != null) {
			builder.setBody(new ByteArrayBodyGenerator(body));
		}
		ListenableFuture<Response> future = new ListenableFutureAdapter<Response>(builder.execute());
		return Futures.makeChecked(future, new Function<Exception, HttpIoException>() {
		    @Override
		    public HttpIoException apply(Exception input) {
		        return new HttpIoException(input);
		    }
		});
    }

    /**
     * 同步执行批量Get调用
     * @param urls 请求的url列表
     * @return 响应列表
     * @throws IOException
     */
    public List<Response> doGetBatch(String... urls) {
        List<Future<Response>> futureList = Lists.newLinkedList();
        for (String url : urls) {
            futureList.add(doGet(url));
        }
        return Lists.transform(futureList, new Function<Future<Response>, Response>() {
            @Override
            public Response apply(Future<Response> input) {
                try {
                    return input.get();
                } catch (Exception e) {
                    throw new HttpIoException(e);
                }
            }
        });
    }

    public static class Builder {
        int connectTimeout = 5000;
        int readTimeout = 10000;
        int maxConnections = -1;
        int maxConnectionsPerHost = -1;
        boolean compress;
        boolean redirectEnabled;

        public Builder setConnectTimeout(int timeoutMs) {
            this.connectTimeout = timeoutMs;
            return this;
        }

        public Builder setReadTimeout(int readTimeout) {
            this.readTimeout = readTimeout;
            return this;
        }

        public Builder setMaxConnectionsTotal(int total) {
            this.maxConnections = total;
            return this;
        }

        public Builder setMaxConnectionsPerHost(int max) {
            this.maxConnectionsPerHost = max;
            return this;
        }

        public Builder setCompress(boolean doCompress) {
            this.compress = doCompress;
            return this;
        }

        public Builder setRedirect(boolean enable) {
            redirectEnabled = enable;
            return this;
        }

        /**
         * 通过手工构造AsyncHttpClientConfig，来创建http客户端
         * @param config async http client里面的配置
         * @return http客户端
         */
        public SimpleHttpClient build(AsyncHttpClientConfig config) {
            SimpleHttpClient httpClient = new SimpleHttpClient();
            httpClient.client = new AsyncHttpClient(config);
            return httpClient;
        }

        /**
         * 通过简单的配置来构造http客户端
         * @return http客户端
         */
        public SimpleHttpClient build() {
            SimpleHttpClient httpClient = new SimpleHttpClient();
            com.ning.http.client.AsyncHttpClientConfig.Builder builder = new AsyncHttpClientConfig.Builder();
            if (this.connectTimeout > 0) {
                builder = builder.setConnectTimeout(connectTimeout);
            }
            if (this.readTimeout > 0) {
                builder = builder.setRequestTimeout(readTimeout);
            }
            if (maxConnectionsPerHost > 0) {
                builder = builder.setMaxConnectionsPerHost(maxConnectionsPerHost);
            }
            if (maxConnections > 0) {
                builder = builder.setMaxConnections(maxConnections);
            }
            builder = builder.setFollowRedirect(redirectEnabled).setCompressionEnforced(compress);
            if (http_tcpNoDelay) {
            	// 开启tcpNoDelay
                NettyAsyncHttpProviderConfig providerConfig = new NettyAsyncHttpProviderConfig();
                providerConfig.addProperty("tcpNoDelay", true);
                builder.setAsyncHttpClientProviderConfig(providerConfig);
            }
            if (http_keepAlive) {
            	builder.setAllowPoolingConnections(true);
            } else {
            	builder.setAllowPoolingConnections(false);
            }
            
            httpClient.client = new AsyncHttpClient(builder.build());
            return httpClient;
        }
    }

    public static void main(String[] args) {
		SimpleHttpClient.getInstance().doPost("http://61.152.251.36:8111/", ImmutableMap.of("a", "b"));
	}
}
