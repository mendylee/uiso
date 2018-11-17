package com.xrk.uiac.service.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * HTTP接口操作方法注解类，标注接口使用的HTTP方法，默认返回码及URI的匹配规则
 * HttpMethod: HttpMethod.java.
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：shunchiguo<shunchiguo@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年5月13日
 * <br> JDK版本：1.7
 * <br>==========================
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface HttpMethod
{
	public enum METHOD {PUT, GET, DELETE, POST, PATCH}
	public enum STATUS_CODE{OK,  CREATED, ACCEPTED, NO_CONTENT}
	/**
	 * 
	 * 每个处理方法可匹配的URI路径，使用部份匹配操作，匹配URI中的部份内容
	 *    
	 * @return
	 */
	String uri() default "";
	/**
	 * 
	 * 允许使用的HTTP请求方法，目前暂时支持一种操作
	 *    
	 * @return
	 */
	METHOD method() default METHOD.GET;	
	/**
	 * 
	 * 正常操作后返回的HTTP状态码  
	 *    
	 * @return
	 */
	STATUS_CODE code() default STATUS_CODE.OK;
	
	/**
	 * 
	 * 方法排序优先级，数值越小优先级越高  
	 *    
	 * @return
	 */
	int priority() default 10;
}
