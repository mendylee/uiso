package com.xrk.uiac.service.test.handler;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * HTTP处理测试类
 * HttpWorkerHandlerTest: HttpWorkerHandlerTest.java.
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：shunchiguo<shunchiguo@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年5月26日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public class HttpWorkerHandlerTest
{
	static final int HTTP_PORT = 8088;
	static Thread httpRun;

	@BeforeClass
	public static void SetUpClass()
	{

		RunHttp http = new RunHttp(HTTP_PORT);
		httpRun = java.util.concurrent.Executors.defaultThreadFactory().newThread(http);
		try {
			httpRun.start();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		//测试使用，临时休眠两秒，让HTTP服务先初始化
		try {
	        Thread.sleep(2 * 1000);
        }
        catch (InterruptedException e) {
	        e.printStackTrace();
        }
	}

	String URL = String.format("http://127.0.0.1:%s", HTTP_PORT);

	@Before
	public void SetUp()
	{
		// 在每个测试方法运行之前运行

	}
	
	class TestResponseEntity
	{
		StatusLine statusLine;
		Header[] header;
		String body;
		
		public TestResponseEntity(StatusLine status, Header[] header, String body)
		{
			this.statusLine = status;
			this.header = header;
			this.body = body;
		}
		
		public StatusLine getStatusLine()
		{
			return statusLine;
		}

		public void setStatusLine(StatusLine statusLine)
		{
			this.statusLine = statusLine;
		}

		public Header[] getHeader()
		{
			return header;
		}

		public void setHeader(Header[] header)
		{
			this.header = header;
		}

		public String getBody()
		{
			return body;
		}

		public void setBody(String body)
		{
			this.body = body;
		}
	}
	private String version = "1.0.1";
	private TestResponseEntity RequestNoBody(String url, String appId, String accessToken, boolean isDelete)
	{		
		CloseableHttpClient httpclient = HttpClients.createDefault();
		TestResponseEntity responseBody = null;
		try {
			HttpRequestBase httpPost = null;
			if(isDelete)
			{
				httpPost = new HttpDelete(url);
			}
			else
			{
				httpPost = new HttpGet(url);
			}
			httpPost.addHeader("XRK-APPID", appId);
			httpPost.addHeader("XRK-ACCESS-TOKEN", accessToken);
			httpPost.addHeader("XRK-CLIENT-VERSION", version);
			System.out.println("Executing request " + httpPost.getRequestLine());
			try {
				HttpResponse response = httpclient.execute(httpPost);
				String result = "";
				try {
			        result = EntityUtils.toString(response.getEntity());
		        }
		        catch (ParseException | IOException e) {
			        e.printStackTrace();
		        }
				catch(IllegalArgumentException e)
				{
					e.printStackTrace();
				}			
				
				responseBody = new TestResponseEntity(response.getStatusLine(), response.getAllHeaders(), result);
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		finally {
			try {
				httpclient.close();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		return responseBody;
	}
	
	private TestResponseEntity RequestBody(String url, String appId, String accessToken, String postBody, boolean isPost)
	{
		String version = "1.0.1";
		CloseableHttpClient httpclient = HttpClients.createDefault();
		TestResponseEntity responseBody = null;
		try {
			HttpEntityEnclosingRequestBase httpPost = null;
			if(isPost)
			{
				httpPost = new HttpPost(url);
			}
			else
			{
				httpPost = new HttpPut(url);
			}
			httpPost.addHeader("XRK-APPID", appId);
			httpPost.addHeader("XRK-ACCESS-TOKEN", accessToken);
			httpPost.addHeader("XRK-CLIENT-VERSION", version);
			if(!postBody.isEmpty())
			{
				HttpEntity entity = new ByteArrayEntity(postBody.getBytes("UTF-8"));
				httpPost.setEntity(entity);
			}			
			System.out.println("Executing request " + httpPost.getRequestLine());
			try {
				HttpResponse response = httpclient.execute(httpPost);
				String result = "";				
				try {
			        result = EntityUtils.toString(response.getEntity());
		        }
		        catch (ParseException | IOException e) {
			        e.printStackTrace();
		        }
				catch(IllegalArgumentException e)
				{
					e.printStackTrace();
				}				
				responseBody = new TestResponseEntity(response.getStatusLine(), response.getAllHeaders(), result);
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
        catch (UnsupportedEncodingException e) {
	        e.printStackTrace();
        }
		finally {
			try {
				httpclient.close();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		return responseBody;
	}
	
	@Test
	public void testRoute()
	{
		// 测试方法
		System.out.println("Test HttpWorkerHandler.Route");
		
		String appId = "1";
		String accessToken = "testtesttest";
		
		//测试Handler的路由规则
		//case1:Handler的URI不匹配，正确规则为test_\d+_(a|b)
		String postUrl = String.format("%s/%s/noparam", URL, "test_1111_ccc");
		TestResponseEntity response = RequestBody(postUrl, appId, accessToken, "", true);
		if(response == null || response.getStatusLine().getStatusCode() != 404)
		{
			Assert.fail(String.format("HTTP STATUS CODE:expect=%s, actual=%s", 404, response.getStatusLine().getStatusCode()));
		}
		
		//Case2:后置匹配，前置不匹配，路由判定应该失败
		postUrl = String.format("%s/%s/noparam", URL, "111test_1111_a");
		response = RequestBody(postUrl, appId, accessToken, "", true);
		if(response == null || response.getStatusLine().getStatusCode() != 404)
		{
			Assert.fail(String.format("HTTP STATUS CODE:expect=%s, actual=%s", 404, response.getStatusLine().getStatusCode()));
		}
		
		//Case3:前置匹配，后置不匹配，路由判定应该失败
		postUrl = String.format("%s/%s/noparam", URL, "test_1111_abbc");
		response = RequestBody(postUrl, appId, accessToken, "", true);
		if(response == null || response.getStatusLine().getStatusCode() != 404)
		{
			Assert.fail(String.format("HTTP STATUS CODE:expect=%s, actual=%s", 404, response.getStatusLine().getStatusCode()));
		}
		
		//Case4:测试POST方法
		postUrl = String.format("%s/%s/noparam", URL, "test_1111_a");
		response = RequestBody(postUrl, appId, accessToken, "", true);
		if(response == null || response.getStatusLine().getStatusCode() != 204)
		{
			Assert.fail(String.format("HTTP STATUS CODE:expect=%s, actual=%s", 204, response.getStatusLine().getStatusCode()));
		}
		
		//Case5:测试GET方法
		postUrl = String.format("%s/%s/noparam", URL, "test_1111_a");
		response = RequestNoBody(postUrl, appId, accessToken,  false);
		if(response == null || response.getStatusLine().getStatusCode() != 200 || !response.getBody().equals("true"))
		{
			Assert.fail(String.format("HTTP STATUS CODE:expect=%s, actual=%s", 200, response.getStatusLine().getStatusCode()));
		}
		
		//Case6:测试GET方法,URI带一个参数，需要在服务端获取分组数据的，需要在匹配规则中加小括号
		String param1 = "11223";
		postUrl = String.format("%s/%s/noparam/%s", URL, "test_1111_a", param1);
		response = RequestNoBody(postUrl, appId, accessToken,  false);
		if(response == null || response.getStatusLine().getStatusCode() != 200)
		{
			if(response != null)
			{
				System.out.println(String.format("HTTP_RESPONSE_BODY:%s", response.getBody()));
			}
			Assert.fail(String.format("HTTP STATUS CODE:expect=%s, actual=%s", 200, response.getStatusLine().getStatusCode()));			
		}
		//因为目前默认用JSON返回，字符串会带双引号
		String tmp = String.format("\"%s\"", param1);
		if(!response.getBody().equals(tmp))
		{
			Assert.fail(String.format("HTTP RESPONSE:expect=%s, actual=%s", tmp, response.getBody()));
		}
				
		//Case6:测试GET方法,URI带两个参数
		String param2 = "test_param";
		postUrl = String.format("%s/%s/noparam/%s/%s", URL, "test_1111_a", param1, param2);
		response = RequestNoBody(postUrl, appId, accessToken,  false);
		if(response == null || response.getStatusLine().getStatusCode() != 200)
		{
			Assert.fail(String.format("HTTP STATUS CODE:expect=%s, actual=%s", 200, response.getStatusLine().getStatusCode()));
		}
		
		tmp = String.format("\"%s_%s\"", param1, param2);
		if(!response.getBody().equals(tmp))
		{
			Assert.fail(String.format("HTTP RESPONSE:expect=%s, actual=%s", tmp, response.getBody()));
		}
		
		//Case7:测试PUT方法,BODY中带两参数
		postUrl = String.format("%s/%s/noparam", URL, "test_1111_b");
		String param3 = "false";
		String postBody = String.format("p1=%s&p2=%s&p3=%s", param1, param2, param3);
		response = RequestBody(postUrl, appId, accessToken, postBody, false);
		if(response == null || response.getStatusLine().getStatusCode() != 201)
		{
			Assert.fail(String.format("HTTP STATUS CODE:expect=%s, actual=%s", 201, response.getStatusLine().getStatusCode()));
		}
		
		tmp = String.format("\"%s_%s\"", param1, param2);
		if(!response.getBody().equals(tmp))
		{
			Assert.fail(String.format("HTTP RESPONSE:expect=%s, actual=%s", tmp, response.getBody()));
		}		
		
		//Case8:测试DELETE方法
		postUrl = String.format("%s/%s/noparam", URL, "test_1111_b");		
		response = RequestNoBody(postUrl, appId, accessToken, true);
		if(response == null || response.getStatusLine().getStatusCode() != 204)
		{
			Assert.fail(String.format("HTTP STATUS CODE:expect=%s, actual=%s", 204, response.getStatusLine().getStatusCode()));
		}
		
		boolean bSucc = false;
		tmp = String.format("\"%s_%s_%s_/%s/noparam\"", version, appId, accessToken, "test_1111_b");
		String tmpHead = "";
		for(Header head : response.getHeader())
		{
			if(head.getName().equals("TMP_DELETE"))
			{
				tmpHead  = head.getValue();
				if(tmpHead.equals(tmp))
				{
					bSucc = true;
					break;
				}
			}
		}
		if(!bSucc)
		{
			Assert.fail(String.format("HTTP RESPONSE:expect=%s, actual=%s", tmp, tmpHead));
		}		
	}
	
	@Test
	public void testMethodRoute()
	{
		System.out.println("Test HttpWorkerHandler.MethodRoute");
		
		String appId = "1";
		String accessToken = "testtesttest";
		
		//case1:分组URI路由，3个参数，/group/(\w+)/(\d+)/([a-f])
		String param1 = "test_1111_ccc";
		String param2 = "12098324";
		String param3 = "d";
		String postUrl = String.format("%s/param/group/%s/%s/%s", URL, param1, param2, param3);
		TestResponseEntity response = RequestNoBody(postUrl, appId, accessToken, false);
		if(response == null || response.getStatusLine().getStatusCode() != 200)
		{
			Assert.fail(String.format("HTTP STATUS CODE:expect=%s, actual=%s", 200, response.getStatusLine().getStatusCode()));
		}
		
		//因为目前默认用JSON返回，字符串会带双引号
		String tmp = String.format("\"%s_%s_%s\"", param1, param2, param3);
		if(!response.getBody().equals(tmp))
		{
			Assert.fail(String.format("HTTP RESPONSE:expect=%s, actual=%s", tmp, response.getBody()));
		}
		
		//Case2:分组URI路由，2个参数，/group/(\w+)/(\d+)
		postUrl = String.format("%s/param/group/%s/%s", URL, param1, param2);
		response = RequestNoBody(postUrl, appId, accessToken, false);
		if(response == null || response.getStatusLine().getStatusCode() != 200)
		{
			Assert.fail(String.format("HTTP STATUS CODE:expect=%s, actual=%s", 200, response.getStatusLine().getStatusCode()));
		}
		
		tmp = String.format("\"%s_%s\"", param1, param2);
		if(!response.getBody().equals(tmp))
		{
			Assert.fail(String.format("HTTP RESPONSE:expect=%s, actual=%s", tmp, response.getBody()));
		}
		
		//Case3:分组URI路由，1个参数，/group/(\w+)
		postUrl = String.format("%s/param/group/%s", URL, param1);
		response = RequestNoBody(postUrl, appId, accessToken, false);
		if(response == null || response.getStatusLine().getStatusCode() != 200)
		{
			Assert.fail(String.format("HTTP STATUS CODE:expect=%s, actual=%s", 200, response.getStatusLine().getStatusCode()));
		}
		
		tmp = String.format("\"%s\"", param1);
		if(!response.getBody().equals(tmp))
		{
			Assert.fail(String.format("HTTP RESPONSE:expect=%s, actual=%s", tmp, response.getBody()));
		}
		
		//Case4:正则式匹配，/group_\\d+
		postUrl = String.format("%s/param/group_%s", URL, param2);
		response = RequestNoBody(postUrl, appId, accessToken, false);
		if(response == null || response.getStatusLine().getStatusCode() != 200)
		{
			Assert.fail(String.format("HTTP STATUS CODE:expect=%s, actual=%s", 200, response.getStatusLine().getStatusCode()));
		}
		
		tmp = String.format("\"%s\"", "regex");
		if(!response.getBody().equals(tmp))
		{
			Assert.fail(String.format("HTTP RESPONSE:expect=%s, actual=%s", tmp, response.getBody()));
		}
		
		//Case5:正则式匹配，/(first|second|\\d+)
		postUrl = String.format("%s/param/%s", URL, param2);
		response = RequestNoBody(postUrl, appId, accessToken, false);
		if(response == null || response.getStatusLine().getStatusCode() != 200)
		{
			Assert.fail(String.format("HTTP STATUS CODE:expect=%s, actual=%s", 200, response.getStatusLine().getStatusCode()));
		}
		
		tmp = String.format("\"%s\"", param2);
		if(!response.getBody().equals(tmp))
		{
			Assert.fail(String.format("HTTP RESPONSE:expect=%s, actual=%s", tmp, response.getBody()));
		}
		
		postUrl = String.format("%s/param/%s", URL, "second");
		response = RequestNoBody(postUrl, appId, accessToken, false);
		if(response == null || response.getStatusLine().getStatusCode() != 200)
		{
			Assert.fail(String.format("HTTP STATUS CODE:expect=%s, actual=%s", 200, response.getStatusLine().getStatusCode()));
		}
		
		tmp = String.format("\"%s\"", "second");
		if(!response.getBody().equals(tmp))
		{
			Assert.fail(String.format("HTTP RESPONSE:expect=%s, actual=%s", tmp, response.getBody()));
		}
		
		postUrl = String.format("%s/param/%s", URL, "first");
		response = RequestNoBody(postUrl, appId, accessToken, false);
		if(response == null || response.getStatusLine().getStatusCode() != 200)
		{
			Assert.fail(String.format("HTTP STATUS CODE:expect=%s, actual=%s", 200, response.getStatusLine().getStatusCode()));
		}
		
		tmp = String.format("\"%s\"", "first");
		if(!response.getBody().equals(tmp))
		{
			Assert.fail(String.format("HTTP RESPONSE:expect=%s, actual=%s", tmp, response.getBody()));
		}
		
		//Case7:字符串匹配，/group
		postUrl = String.format("%s/param/%s", URL, "group");
		response = RequestNoBody(postUrl, appId, accessToken, false);
		if(response == null || response.getStatusLine().getStatusCode() != 200)
		{
			Assert.fail(String.format("HTTP STATUS CODE:expect=%s, actual=%s", 200, response.getStatusLine().getStatusCode()));
		}
		
		tmp = String.format("\"%s\"", "group");
		if(!response.getBody().equals(tmp))
		{
			Assert.fail(String.format("HTTP RESPONSE:expect=%s, actual=%s", tmp, response.getBody()));
		}
		
		postUrl = String.format("%s/param/xxxxx/eeeee/fffff/%s", URL, "group");
		response = RequestNoBody(postUrl, appId, accessToken, false);
		if(response == null || response.getStatusLine().getStatusCode() != 200)
		{
			Assert.fail(String.format("HTTP STATUS CODE:expect=%s, actual=%s", 200, response.getStatusLine().getStatusCode()));
		}
		
		tmp = String.format("\"%s\"", "group");
		if(!response.getBody().equals(tmp))
		{
			Assert.fail(String.format("HTTP RESPONSE:expect=%s, actual=%s", tmp, response.getBody()));
		}
				
		//Case8:匹配所有字符
		postUrl = String.format("%s/param/aacccdeeasdf/%s", URL, "aadcvbedewrw");
		response = RequestNoBody(postUrl, appId, accessToken, false);
		if(response == null || response.getStatusLine().getStatusCode() != 200)
		{
			Assert.fail(String.format("HTTP STATUS CODE:expect=%s, actual=%s", 200, response.getStatusLine().getStatusCode()));
		}
		
		tmp = String.format("\"%s\"", "string");
		if(!response.getBody().equals(tmp))
		{
			Assert.fail(String.format("HTTP RESPONSE:expect=%s, actual=%s", tmp, response.getBody()));
		}
	}
	
	@Test
	public void testParamer() throws java.text.ParseException
	{
		System.out.println("Test HttpWorkerHandler.Paramer");
		String appId = "1";
		String accessToken = "testtesttest";
		//case1:测试接口参数送入，匹配几种基本类型
		String param1 = "11";
		String param2 = "12345678";
		String param3 = "123456789089";
		String param4 = "123.23454322";
		String param5 = "324232.324233255";
		String param6 = "false";
		Date d1 = new Date();
		DateFormat dt = new SimpleDateFormat("yyyy-MM-dd");
		DateFormat dt2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String param7 = dt.format(d1);
		String param8 = dt2.format(d1);
		
		String postUrl = null;
        try {
	        postUrl = String.format("%s/paramtest?s1=%s&i1=%s&l1=%s&f1=%s&d1=%s&b1=%s&dt1=%s&dt2=%s",
	        		URL, param1, param2, param3, param4, param5, param6, URLEncoder.encode(param7, "utf-8"), URLEncoder.encode(param8, "utf-8"));
        }
        catch (UnsupportedEncodingException e) {
	         e.printStackTrace();
        }
		
		TestResponseEntity response = RequestNoBody(postUrl, appId, accessToken, false);
		if(response == null || response.getStatusLine().getStatusCode() != 200)
		{
			Assert.fail(String.format("HTTP STATUS CODE:expect=%s, actual=%s", 200, response.getStatusLine().getStatusCode()));
		}
		
		String tmp = String.format("\"%s_%s_%s_%s_%s_%s_%s_%s\"",  
				param1, param2, param3, Float.parseFloat(param4), Double.parseDouble(param5), param6, dt.parse(param7).getTime(), dt2.parse(param8).getTime());
		if(!response.getBody().equals(tmp))
		{
			Assert.fail(String.format("HTTP RESPONSE:expect=%s, actual=%s", tmp, response.getBody()));
		}
		
		//测试错误参数输入
		param1 = "324ddsdfsd";
		 try {
		        postUrl = String.format("%s/paramtest?s1=%s&i1=%s&l1=%s&f1=%s&d1=%s&b1=%s&dt1=%s&dt2=%s",
		        		URL, param1, param2, param3, param4, param5, param6, URLEncoder.encode(param7, "utf-8"), URLEncoder.encode(param8, "utf-8"));
	        }
	        catch (UnsupportedEncodingException e) {
		         e.printStackTrace();
	        }
			
			response = RequestNoBody(postUrl, appId, accessToken, false);
			if(response == null || response.getStatusLine().getStatusCode() != 400)
			{
				Assert.fail(String.format("HTTP STATUS CODE:expect=%s, actual=%s", 400, response.getStatusLine().getStatusCode()));
			}
	}
		
	@Test
	public void testException()
	{
		System.out.println("Test HttpWorkerHandler.Exception");
		
		String[] ary= new String[]{"201","202","304","400","401","403","404","405","406","409","410","412","415","417","422","500","501","503"};
		String appId = "1";
		String accessToken = "testtesttest";
		
		for(String code : ary)
		{
			int iCode = Integer.parseInt(code);
			String postUrl = String.format("%s/exception/%s", URL, iCode);
			TestResponseEntity response = RequestNoBody(postUrl, appId, accessToken, false);
			if(response == null || response.getStatusLine().getStatusCode() != iCode)
			{
				int rtnCode = -1;
				if(response != null)
				{
					rtnCode = response.getStatusLine().getStatusCode();
				}
				Assert.fail(String.format("HTTP STATUS CODE:expect=%s, actual=%s", iCode, rtnCode));
			}
		}
	}

	@After
	public void tearDown() throws Exception
	{
		// 在每个测试方法后运行
		//System.out.println("Tear down");
	}

	@AfterClass
	public static void tearDownAfterClass()
	{
		System.out.println("Tear down After class");
		httpRun.interrupt();
		httpRun.stop();
	}
}
