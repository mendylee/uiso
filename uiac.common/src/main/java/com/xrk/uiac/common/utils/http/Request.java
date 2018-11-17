package com.xrk.uiac.common.utils.http;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
*
* HTTP请求
*
* <br>==========================
* <br> 公司：广州向日葵信息科技有限公司
* <br> 开发：lijp<lijingping@xiangrikui.com>
* <br> 版本：1.0
* <br> 创建时间：2015-4-12
* <br>==========================
*/
public class Request {
	
	public String url;
	private Map<String, String> headers;
	private List<com.ning.http.client.cookie.Cookie> cookies;
	private Map<String, String> params;
	private byte[] body;
	
	public Request(String url) {
		this.url = url;
	}
	
	public Map<String, String> getHeaders() {
		return headers;
	}
	
	public List<com.ning.http.client.cookie.Cookie> getClientCookies() {
		return cookies;
	}
	
	public Map<String, String> getParams() {
		return params;
	}
	
	public byte[] getBody() {
		return body;
	}
	
	/** 设置Header */
	public void addHeader(String name, String value) {
		if (headers==null) {
			headers = new HashMap<String, String>();
		}
		headers.put(name, value);
	}
	
	/** 设置Cookie */
	public void addCookie(Cookie cookie) {
		if (cookies==null) {
			cookies = new ArrayList<com.ning.http.client.cookie.Cookie>();
		}
		cookies.add(cookie.getClientCookie());
	}
	
	/** 设置POST参数 */
	public void addParam(String name, String value) {
		if (params == null) {
			params = new HashMap<String, String>();
		}
		params.put(name, value);
	}
	
	/**
	 * 替换Header容器
	 * @param headers
	 */
	public void setHeaders(Map<String, String> headers) {
		this.headers = headers;
	}
	
	/**
	 * 替换Cookie容器
	 * @param cookies
	 */
	public void setCookies(List<com.ning.http.client.cookie.Cookie> cookies) {
		this.cookies = cookies;
	}
	
	/**
	 * 替换Params容器
	 * @param headers
	 */
	public void setParams(Map<String, String> params) {
		this.params = params;
	}
	
	/**
	 * 设置字节流byte[]
	 * <br>只在POST下有效
	 * <br> 必须设置 addHeader("Content-Length", String.valueOf(body.length));
	 * <br>使用setBody(), addParam() 和 setParams()将失效
	 * @param body
	 */
	public void setBody(byte[] body) {
		this.body = body;
	}
}