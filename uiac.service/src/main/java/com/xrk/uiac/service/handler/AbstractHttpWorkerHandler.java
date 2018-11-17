package com.xrk.uiac.service.handler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.time.StopWatch;

import com.google.gson.Gson;
import com.xrk.hws.common.logger.Logger;
import com.xrk.hws.http.HttpServer;
import com.xrk.hws.http.HttpWorkerHandler;
import com.xrk.hws.http.context.HttpContext;
import com.xrk.hws.http.monitor.MonitorClient;
import com.xrk.hws.http.monitor.MonitorContext;
import com.xrk.uiac.bll.exception.AbstractRedirectException;
import com.xrk.uiac.bll.exception.BUSINESS_CODE;
import com.xrk.uiac.bll.exception.BadRequestException;
import com.xrk.uiac.bll.exception.BusinessException;
import com.xrk.uiac.bll.exception.HTTP_CODE;
import com.xrk.uiac.bll.exception.InternalServerException;
import com.xrk.uiac.bll.exception.MethodNotAllowedException;
import com.xrk.uiac.common.utils.SortEntity;
import com.xrk.uiac.common.utils.ThreadSafeSortList;
import com.xrk.uiac.service.annotation.HttpMethod;
import com.xrk.uiac.service.annotation.HttpRouterInfo;
import com.xrk.uiac.service.entity.ErrorResponseEntity;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.Modifier;
import javassist.NotFoundException;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.MethodInfo;

/**
 * 对业务层处理器的抽像类，主要完成
 * 1.处理方法的加载、注册
 * 2.自定义请求参数的获取
 * 3.控制方法的二次封装，简化前端控制层编写
 * 4.统一处理业务层的异常，返回给客户端
 * AbstractHttpWorkerHandler: AbstractHttpWorkerHandler.java.
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：shunchiguo<shunchiguo@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年5月13日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public abstract class AbstractHttpWorkerHandler extends HttpWorkerHandler
{	
	//自定义HTTP常量头
	private final String CLIENT_VERSION = "XRK-CLIENT-VERSION";
	private final String APPID = "XRK-APPID";
	private final String ACCESS_TOKEN = "XRK-ACCESS-TOKEN";
	
	private Map<String, HttpMethod> hsAnnotation = new HashMap<String, HttpMethod>();
	private Map<String, String[]> hsMethodParams = new HashMap<String, String[]>(); 
	
	public AbstractHttpWorkerHandler()
	{		
	}
	
	private void InitMethod()
	{
		Method[] mt = this.getClass().getMethods();
		List<SortEntity<Method>> lsMethod = new ThreadSafeSortList<SortEntity<Method>>();
		for(Method m : mt)
		{
			//如果注解了HttpMethod类,则默认为映射方法
			if(m.isAnnotationPresent(HttpMethod.class))
			{
				HttpMethod hm = m.getAnnotation(HttpMethod.class);
				lsMethod.add(new SortEntity<Method>(hm.priority(), m));
			}
		}
		
		for(SortEntity<Method> method : lsMethod)
		{
			Method m = method.getValue();
			HttpMethod hm = m.getAnnotation(HttpMethod.class);
			//请求函数绑定HTTP方法
			this.addFunction(hm.method().name(), hm.uri(), m);
			Logger.info("Add Route Function: method=%s, uri=%s, status code=%s, priority=%s, class=%s, function=%s", 
					hm.method().name(), hm.uri(), hm.code(), hm.priority(), this.getClass().getName(), m.getName());
			//根据映射的方法名缓存注解信息
			hsAnnotation.put(m.getName(), hm);
			String methodKey = String.format("%s_%s", m.getName(), m.getParameterCount());
			hsMethodParams.put(methodKey, getMethodParamNames(m));
		}		
	}
	
	public void register(HttpServer server)
    {
		//将当前类注册到路由表中
		HttpRouterInfo router = this.getClass().getAnnotation(HttpRouterInfo.class);
		String strRouter = String.format("^/%s$|^/%s[?/]{1}.*", router.router(), router.router());
		String strMethod = router.method();
		if (server.registerRequestHandler(strMethod, strRouter, this) != 0)
		{
			Logger.error("Register handler failed! class=%s, method=%s, route=%s", this.getClass().getName(), strMethod, strRouter);
		}
		else
		{
			Logger.info("Register handler success! class=%s, method=%s, route=%s", this.getClass().getName(), strMethod, strRouter);
		}
		
		InitMethod();
    }  
	
	/**
	 * 
	 * 获取方法的参数名称  
	 *    
	 * @param m
	 * @return
	 */
	private String[] getMethodParamNames(Method m)
	{  
		 try {
	          ClassPool pool = ClassPool.getDefault();  
	          CtClass cc = pool.get(m.getDeclaringClass().getName());
	          
	          
	          CtMethod[] aryM = cc.getDeclaredMethods(m.getName());
	          CtMethod cm = aryM[0];
	          //检测重载方法
	          if(aryM.length > 1)
	          {
		          for(CtMethod temp : aryM)
		          {
		        	  if(temp.getName().equals(m.getName()) 
		        			  && temp.getParameterTypes().length == m.getParameterTypes().length)
		        	  {
		        		  cm = temp;
		        		  break;
		        	  }
		          }
	          }
	          
	          //使用javaassist的反射方法获取方法的参数名
	          MethodInfo methodInfo = cm.getMethodInfo();  
	          CodeAttribute codeAttribute = methodInfo.getCodeAttribute();  
	          LocalVariableAttribute attr = (LocalVariableAttribute) codeAttribute.getAttribute(LocalVariableAttribute.tag);  
	          if (attr == null)  {
	              //exception
	          }
	          String[] paramNames = new String[cm.getParameterTypes().length];  
	          int pos = Modifier.isStatic(cm.getModifiers()) ? 0 : 1;  
	          for (int i = 0; i < paramNames.length; i++)  
	              paramNames[i] = attr.variableName(i + pos);
	          return paramNames;
	      } catch (NotFoundException e) {
	          Logger.error(e, e.getMessage());
	      } 
		 return null;
    }  
	
	private static ThreadLocal<DateFormat> dtFormat = new ThreadLocal<DateFormat>() {
        @Override
        protected DateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd");
        }
    };
    
    private static ThreadLocal<DateFormat> dtTimeFormat = new ThreadLocal<DateFormat>() {
        @Override
        protected DateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        }
    };
	/**
	 * 
	 * 转换参数  
	 *    
	 * @param paramType
	 * @param val
	 * @param ctx
	 * @return
	 * @throws ParseException 
	 */
	private Object parseVal(String paramType, String val, HttpContext ctx) throws ParseException
	{
		Object objVal = null;
		Double d = null;
		switch(paramType)
		{
			case "short":
			case "java.lang.Short":
				d = Double.parseDouble(val);
				objVal = d.shortValue();
				break;
			case "int":
			case "java.lang.Integer":
				d = Double.parseDouble(val);				
				objVal = d.intValue();
				break;
			case "long":
			case "java.lang.Long":
				d = Double.parseDouble(val);
				objVal = d.longValue();
				break;
			case "float":
			case "java.lang.Float":
				objVal = Float.parseFloat(val);
				break;
			case "double":
			case "java.lang.Double":
				objVal = Double.parseDouble(val);
				break;
			case "boolean":
			case "java.lang.Boolean":
				objVal = Boolean.parseBoolean(val);
				break;
			case "java.util.Date":
				if(val.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}"))
				{
					objVal = dtTimeFormat.get().parse(val);
				}
				else
				{
					objVal = dtFormat.get().parse(val);
				}				
				break;
			case "com.xrk.uiac.service.handler.CustomParameter":
				objVal = getCustomParameter(ctx);
				break;
			case "com.xrk.hws.http.context.HttpContext":
				objVal = ctx;
				break;
			default:
				objVal = val;
				break;
		}
		return objVal;
	}
	
	@Override 
	protected void callFunction(Method func, HttpContext ctx) throws InvocationTargetException,IllegalAccessException
    {
		StopWatch sw = new StopWatch();
		MonitorContext mctx = null;
		//用以监控的参数
		String clientIP = ctx.request.headers().get("X-Forwarded-For");
		try {
			if(func == null)
			{
				Logger.warn("invalid request! method=%s, uri=%s", ctx.request.getMethod(), ctx.request.getUri());
				MethodNotAllowedException ex = new MethodNotAllowedException(BUSINESS_CODE.HTTP_METHOD_INVALID, "HTTP请求方法不正确");
				InvocationTargetException exp = new InvocationTargetException(ex);
				throw exp;
			}
			  
			sw.start();

			//初始化质量日志监控context，并启动监控
			mctx = MonitorClient.getAccessContext();
			MonitorClient.start(mctx);
			
             if(clientIP == null || clientIP.isEmpty()){
             	InetSocketAddress insocket = (InetSocketAddress) ctx.ctx.channel().remoteAddress();
             	clientIP =  insocket.getAddress().getHostAddress();
             }
             
			Logger.debug("incoming request! process function=%s, method=%s, uri=%s, remote Addr=%s, userAgent=%s", 
					func.getName(), ctx.request.getMethod(), ctx.request.getUri(), 
					clientIP, ctx.request.headers().get("User-Agent"));
			//分析方法的参数
			String methodKey = String.format("%s_%s", func.getName(), func.getParameterCount());
			String[] params = hsMethodParams.get(methodKey);
			List<Object> lsParam = new ArrayList<Object>();
			if(params != null)
			{
				//从请求中获取参数
				int i =0;
				for(Class<?> param : func.getParameterTypes())
				{
					String paramName = params[i++];
					
					List<String> lsVal = ctx.getUriAttribute(paramName);
					String val = lsVal != null ? (lsVal.size() > 0 ? lsVal.get(0) : null) : null;
					if(val == null)
					{
						val = ctx.getPostAttrValue(paramName);
					}
					
					try
					{
						Object objVal = parseVal(param.getName(), val, ctx);
						Logger.debug("get params: paramName=%s, paramType=%s, inputVal=%s, outVal=%s", paramName, param.getName(), val, objVal);						
						lsParam.add(objVal);
					}
					catch(NumberFormatException | ParseException ex)
					{
						//参数转换异常
						Logger.warn("request parameter invalid! method=%s, uri=%s, parameter info:name=%s, val=%s, type=%s, uiacRunTime=%s",
								ctx.request.getMethod(), ctx.request.getUri(),paramName, val, param.getName(), sw.toString());
						
						BadRequestException exFail = new BadRequestException(BUSINESS_CODE.PARAMER_INVAILD, 
								String.format("参数转换异常:请求参数=%s,送入参数=%s,参数类型=%s", paramName, val, param.getName()));
						InvocationTargetException exp = new InvocationTargetException(exFail);
						throw exp;
					}
				}
			}

			//调用方法
			Object rtn = func.invoke(this, lsParam.toArray());
			
			//调用成功时的返回值
			int httpCode = HTTP_CODE.OK;
	        //处理返回事件 
			switch(hsAnnotation.get(func.getName()).code())
			{
				case CREATED:
					httpCode = HTTP_CODE.CREATED;
					break;
				case ACCEPTED:
					httpCode = HTTP_CODE.ACCEPTED;
					break;
				case NO_CONTENT:
					httpCode = HTTP_CODE.NO_CONTENT;
					break;
				default:
					httpCode = HTTP_CODE.OK;
					break;
			}
			
			this.renderJSON(ctx, rtn, httpCode);
			MonitorClient.stop(mctx, ctx, new Gson().toJson(rtn), func.getName(), String.valueOf(httpCode));
        }
        catch (InvocationTargetException e) {
        	//处理自定义异常
        	Throwable exception = e.getTargetException();
        	
        	BusinessException ex = null;
        	if(exception instanceof AbstractRedirectException)
        	{
        		//重定向异常处理
        		ex = (AbstractRedirectException)exception;
        	}
        	else if(exception instanceof BusinessException)
        	{
        		//其它业务异常
        		ex = (BusinessException)exception;
        	}
        	else
        	{
        		Logger.error(e,  "call method error!");
        		//否则当成服务器内部错误抛出
        		ex = new InternalServerException(BUSINESS_CODE.INTERNAL_SERVER_ERROR, 
        				String.format("ex=%s, traget ex=%s", e.getMessage(), exception.getMessage()));
        	}
        	
        	ErrorResponseEntity errObj = new ErrorResponseEntity(ex.getErrCode(), ex.getMessage());
			this.renderJSON(ctx, errObj, ex.getHttpCode());
			
			MonitorClient.stop(mctx, ctx, new Gson().toJson(errObj), func.getName(), String.valueOf(ex.getHttpCode()));
        }
		
		sw.stop();
		Logger.debug("end request! uiacRunTime=%s, process function=%s, method=%s, uri=%s, remote Addr=%s, userAgent=%s", 
				sw.toString(), func.getName(), ctx.request.getMethod(), ctx.request.getUri(), 
				clientIP, ctx.request.headers().get("User-Agent"));
    }
	
	/**
	 * 
	 * 获取当前请求中的自定义头参数  
	 *    
	 * @param ctx
	 * @return
	 */
	protected CustomParameter getCustomParameter(HttpContext ctx)
	{
		CustomParameter head = new CustomParameter();
		head.setClientVersion(ctx.request.headers().get(CLIENT_VERSION));
		head.setAppId(ctx.request.headers().get(APPID));
		head.setAccessToken(ctx.request.headers().get(ACCESS_TOKEN));
		head.setUri(ctx.request.getUri());
		//获取URI中匹配的分组信息
		head.setUriGroup(getMatcheGroup(ctx));
		return head;
	}	
}
