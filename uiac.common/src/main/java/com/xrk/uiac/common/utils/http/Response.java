package com.xrk.uiac.common.utils.http;


import java.io.IOException;
import java.util.List;

/**
*
* HTTP响应
*
* <br>==========================
* <br> 公司：广州向日葵信息科技有限公司
* <br> 开发：lijp<lijingping@xiangrikui.com>
* <br> 版本：1.0
* <br> 创建时间：2015-4-12
* <br>==========================
*/
public class Response {
	private com.ning.http.client.Response response;
	
	public Response(com.ning.http.client.Response response) {
		this.response = response;
	}
	
	/** 响应状态码 */
	public int getStatusCode() {
		return response.getStatusCode();
	}
	
	/**
	 * 获取内容, 采用 UTF-8 编码
	 * @return
	 * @throws IOException
	 */
	public String getContent() throws IOException {
		return getContent("UTF-8");
	}
	
	/** 
	 * 获取内容
	 * @Param charset 编码
	 *  */
	public String getContent(String charset) throws IOException {
		return response.getResponseBody(charset);
	}
	
	/** 获取Header信息 */
	public String getHeader(String name) {
		return response.getHeader(name);
	}
	
	/** 获取Header列表信息 */
	public List<String> getHeaders(String name) {
		return response.getHeaders(name);
	}
	
	/** 获取Cookie信息 */
	public Cookie getCookie(String name) {
		List<com.ning.http.client.cookie.Cookie> cookies = response.getCookies();
		for (com.ning.http.client.cookie.Cookie cookie : cookies) {
			if (cookie.getName().equals(name)) {
				return new Cookie(cookie);
			}
		}
		return null;
	}
}