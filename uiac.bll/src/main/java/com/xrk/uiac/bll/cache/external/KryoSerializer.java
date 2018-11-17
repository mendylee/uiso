package com.xrk.uiac.bll.cache.external;

import java.io.ByteArrayOutputStream;
import java.util.HashSet;
import java.util.Set;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.xrk.uiac.common.utils.ScanClass;

/**
 * Kryo序列化辅助类
 * KryoSerializer: KryoSerializer.java.
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：shunchiguo<shunchiguo@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年5月27日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public class KryoSerializer
{
	private static ThreadLocal<Kryo> threadLocalKryo = new ThreadLocal<Kryo>(); 
	private static Set<Class<?>> hsClass = new HashSet<Class<?>>();
		
	private static  Kryo getKryo()
    {
		Kryo kryo = threadLocalKryo.get();
	    if(kryo == null)
	    {
	       return buildKryo();
	    }
	    
	    return kryo;
    }

	private static Kryo buildKryo()
	{
		Kryo kryo = new Kryo();
		int i = 1;
		for(Class<?> loadClass : hsClass)
		{
			kryo.register(loadClass, i++);
		}		
		threadLocalKryo.set(kryo);
		return kryo;
	}
	
	public static void init()
	{
		Set<Class<?>> set = ScanClass.getClasses("com.xrk.uiac.bll.entity");
		for (Class<?> classes : set) {
			//忽略基本类型
			if(classes.isInterface() 
					|| classes.isAnnotation() 
					|| classes.isAnonymousClass()
					|| classes.isEnum()
					|| classes.isPrimitive()){
				continue;
			}
			addClass(classes);
		}
	}
	
	public static void addClass(Class<?> classType)
	{
		hsClass.add(classType);		
	}
	
	public static byte[] Serializer(Object obj)
	{
		Output output = new Output(new ByteArrayOutputStream()); 
		getKryo().writeObject(output, obj);
		return output.toBytes();
	}
	
	public static <T> T Deserializer(Class<T> clazz, byte[] bt)
	{
		Input input = new Input(bt);
		T outObj = getKryo().readObject(input, clazz);		
		return outObj;
	}
}
