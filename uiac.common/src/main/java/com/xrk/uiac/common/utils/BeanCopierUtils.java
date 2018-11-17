package com.xrk.uiac.common.utils;

import java.util.HashMap;
import java.util.Map;

import net.sf.cglib.beans.BeanCopier;

/**
 * 基于BeanCopier cglib动态字节码实现对象之间属性的复制
 * 注：默认只实现属性名称、类型都相同拷贝
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：zhub<zhubin@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年4月29日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public class BeanCopierUtils
{
	
	public static final Map<String, BeanCopier> beanCopiers = new HashMap<String, BeanCopier>();
	
	public static void copy(Object sourceObj,Object destObj){
		String key = genKey(sourceObj.getClass(),destObj.getClass());
		BeanCopier copier = null;
		if(!beanCopiers.containsKey(key)){
			copier = BeanCopier.create(sourceObj.getClass(), destObj.getClass(), false);
			beanCopiers.put(key, copier);
		}else{
			copier = beanCopiers.get(key);
		}
		copier.copy(sourceObj, destObj, null);  
	}
	
	private static String genKey(Class<?> srcClazz,Class<?> destClazz){
		return srcClazz.getName()+destClazz.getName();
	}

}
