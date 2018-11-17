package com.xrk.uiac.common.utils.http;

/**
*
* HTTP Cookie
*
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：lijp<lijingping@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015-4-12
 * <br>==========================
*/
public class Cookie {

    private com.ning.http.client.cookie.Cookie cookie;
    private boolean secure;
    
    public com.ning.http.client.cookie.Cookie getClientCookie() {
    	return cookie;
    }
    
    public Cookie(com.ning.http.client.cookie.Cookie cookie) {
    	this.cookie = cookie;
    }
    
    public Cookie(String domain, String name, String value, boolean wrap, String path, int maxAge, boolean secure,boolean httpOnly) 
    {
    	cookie = new com.ning.http.client.cookie.Cookie(name, value, wrap, domain, path, maxAge, secure, httpOnly);
    	this.secure = secure;
    }
    
    @Override
    public String toString() {
    	if (cookie==null) {
    		return "Cookie: null";
    	}
        return String.format("Cookie: domain=%s, name=%s, value=%s, path=%s, maxAge=%d, secure=%s",
        		cookie.getDomain(),
        		cookie.getName(),
        		cookie.getValue(),
        		cookie.getPath(),
        		cookie.getMaxAge(),
        		secure);
    }
}
