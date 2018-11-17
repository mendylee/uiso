package com.xrk.uiac.service;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.security.CodeSource;
import java.util.Set;

import com.xiangrikui.netflix.eureka.RegistryEurekaClient;
import com.xrk.hws.common.logger.Logger;
import com.xrk.hws.http.HttpServer;
import com.xrk.hws.http.monitor.MonitorClient;
import com.xrk.uiac.bll.cache.CacheService;
import com.xrk.uiac.bll.cache.SysConfigCache;
import com.xrk.uiac.common.utils.ScanClass;
import com.xrk.uiac.dal.Dal;
import com.xrk.uiac.service.annotation.HttpRouterInfo;
import com.xrk.uiac.service.handler.AbstractHttpWorkerHandler;

public class App
{
	public static void main(String[] args)
	{
		String appBasePath = "";
		try {
			CodeSource codeSource = App.class.getProtectionDomain().getCodeSource();
	        appBasePath = URLDecoder.decode(codeSource.getLocation().toURI().getPath(), "UTF-8");
    		File jarFile = new File(appBasePath);
        	appBasePath = jarFile.getParentFile().getPath();
        }
        catch (URISyntaxException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
		
		String configPath = String.format("%s/config", appBasePath);
		System.out.println("configPath="+configPath);
		
		try
		{
			Logger.init(configPath);
		}
		catch(Throwable ex)
		{
			System.out.println("Init logger ERRMSG:"+ex.getMessage());
			System.out.println("STACK:");
			ex.printStackTrace();
			return;
		}    
        
		//加载DAL
		Dal.init(configPath);
		//Dal.init();
		
		//加载系统配置项cache
		SysConfigCache sysConfig = SysConfigCache.getInstance();
				
		//应用程序初始化操作，如：配置加载、环境初始化等 
		CacheService.Init();
		
		//已移除自动化监控
		//监控初始化
		//MonitoringClient.getInstance().start();
		
		//质量日志监控初始化
		try
		{
			MonitorClient.init(configPath);
		}
		catch (Exception e)
		{
			Logger.error(e, "fail to init MonitorClient");
		}
				
		HttpServer server = new HttpServer();
		// 线程数默认为处理器数目
		int processNum = Runtime.getRuntime().availableProcessors();
		server.init(processNum, processNum * 2, processNum * 2, null);
		server.addListen(new InetSocketAddress(sysConfig.getHttpPort()));

		// 自动加载指定包下的所有处理器
		Set<Class<?>> set = ScanClass.getClasses("com.xrk.uiac.service.handler");
		for (Class<?> classes : set) {
			if (classes.isAnnotationPresent(HttpRouterInfo.class)) {
				try {
					AbstractHttpWorkerHandler handler = (AbstractHttpWorkerHandler) classes
					        .newInstance();
					handler.register(server);
				}
				catch (InstantiationException e) {
					Logger.error(e, e.getMessage());
				}
				catch (IllegalAccessException e) {
					Logger.error(e, e.getMessage());
				}
			}
		}		
		Logger.info("start http server, port:%s", sysConfig.getHttpPort());
		RegistryEurekaClient.getSingleton().registryEureka();
		server.run();
	}
}
