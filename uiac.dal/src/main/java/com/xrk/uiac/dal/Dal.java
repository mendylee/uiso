package com.xrk.uiac.dal;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import com.xrk.hws.dal.DalManager;
import com.xrk.hws.dal.core.DataSet;
import com.xrk.hws.dal.exception.DalException;

/**
 * 
 * 数据访问层
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：yexx<yexiaoxiao@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年8月21日 上午10:42:31
 * <br> JDK版本：1.8
 * <br>==========================
 */
public class Dal
{
	private static DalManager dalManager = DalManager.getInstance();
	private static Dal _instance = null;
	private boolean bInit = false;

	public static Dal getInstance()
	{
		if (_instance == null)
		{
			synchronized (Dal.class) {
				if (_instance == null) {
					_instance = new Dal();
					//_instance._init();
				}
			}
		}

		return _instance;
	}

	private Dal() 
	{

	}

	/**
	 * 
	 * 初始化DAL  
	 *    
	 * @param basePath
	 * @param apIn
	 */
	private void _init(String basePath, InputStream apIn)
	{
		if (bInit) 
		{
			return;
		}
		
		//未提供输入流，则根据路径获取输入流
		//否则使用传入的输入流，初始化DalSetting
		if (apIn == null)
		{
			InputStream in = null;
	        try
	        {
	        	String filename = basePath + "/app.properties";
		        in = new FileInputStream(filename);	        
	        }
	        catch (FileNotFoundException e1)
	        {
		        e1.printStackTrace();
		        return;
	        }
	        
	        //初始化DalSetting，从app.properties读取配置项
	        DalSetting.init(in);
		}
		else
		{
			DalSetting.init(apIn);
		}
		
		String configFilePath = String.format("/%s/configs.xml", DalSetting.getDbTypeName());
		boolean isAbsolutePath = false;
		Class<?> loader = null;
		if(basePath == null || basePath.isEmpty())
		{
			//configFilePath = Dal.class.getResource("/configs.xml").getPath();
			loader = Dal.class;
		}
		else
		{
			configFilePath = String.format("%s%s", basePath, configFilePath);
			isAbsolutePath = true;
		}
		
		if (dalManager == null)
		{
			throw new DalException("Get dal instance error");
		}
		System.out.println(String.format("config file path:%s", configFilePath));
		
		if (DalManager.init(configFilePath, isAbsolutePath, basePath, loader) != 0) 
		{
			throw new DalException("Load config error");
		}
		bInit = true;
	}

	@SuppressWarnings("unused")
	private void _dispose()
	{
		if (dalManager == null) {
			throw new DalException("Get dal instance error");
		}

		if (dalManager.close() != 0) {
			throw new DalException("Load config error");
		}

		bInit = false;
	}

	/**
	 * 
	 * 提供输入流，初始化DAL
	 * 一般用在测试方法内 
	 *    
	 * @param apIn
	 */
	public static void init(InputStream apIn)
	{		
		getInstance()._init("", apIn);
	}
	
	/**
	 * 
	 * 提供配置文件根路径，初始化DAL
	 *
	 * @param basePath
	 */
	public static void init(String basePath)
	{
		getInstance()._init(basePath, null);
	}

	public static void dispose()
	{
	}

	public static <T> DataSet getDataSet(Class<T> clazz) throws DalException
	{
		DataSet dataSet = dalManager.getDataSet(clazz);
		if (dataSet == null) {
			throw new DalException("Dal disabled");
		}
		return dataSet;
	}
}
