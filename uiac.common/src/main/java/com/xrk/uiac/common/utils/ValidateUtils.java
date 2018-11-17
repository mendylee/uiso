package com.xrk.uiac.common.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * 用于验证参数正确性的工具类
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：yexx<yexiaoxiao@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年5月14日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public class ValidateUtils
{
	/**
	 * 验证手机号正则表达式
	 */
	private static final String V_MOBILE = "^((13[0-9])|(15[^4,\\D])|(18[0-9])|(17[0,6-8]))\\d{8}$";
	
	/**
	 * 验证手机号的宽松规则正则表达式
	 */
	private static final String V_MOBILE_EASY = "^(1[3-9])\\d{9}$";
	
	/**
	 * 验证邮箱地址的正则表达式
	 */
	private static final String V_EMAIL = "^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$";
	//^([a-zA-Z0-9]*[-_]?[a-zA-Z0-9]+)*@([a-zA-Z0-9]*[-_]?[a-zA-Z0-9]+)+[\\.][A-Za-z]{2,3}([\\.][A-Za-z]{2})?$ 这个很慢
	//^\\s*\\w+(?:\\.{0,1}[\\w-]+)*@[a-zA-Z0-9]+(?:[-.][a-zA-Z0-9]+)*\\.[a-zA-Z]+\\s*$ 相对好
	//^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$ 快，但是有漏判
	
	/**
	 * 验证QQ号的正则表达式
	 */
	private static final String V_QQ = "^[1-9]{1}[0-9]{4,9}$";
	
	/**
	 * 验证url地址的正则表达式^http://[\\w-\\.]+(?:/|(?:/[\\w\\.\\-]+)*)?$
	 */
	private static final String V_URL = "^(([^:/?#]+):)?(//([^/?#]*))?([^?#]*)(\\?([^#]*))?(#(.*))?";
	
	/**
	 * 验证邮政编码的正则表达式
	 */
	private static final String V_POSTCODE = "^[1-9]{1}[0-9]{5}$";
											
	/**
	 * 
	 * 验证字符串是否为正确的手机号
	 *    
	 * @param value
	 * @return
	 */
	public static boolean isMobile(String value)
	{
		return match(V_MOBILE_EASY, value);
	}
	/**
	 * 
	 * 验证url是否为正确的地址
	 *    
	 * @param url
	 * @return
	 */
	public static boolean isUrl(String value){
		return match(V_URL, value);
	}
	
	/**
	 * 
	 * 验证字符串是否为正确的邮箱地址
	 *    
	 * @param value
	 * @return
	 */
	public static boolean isEmail(String value)
	{
		return match(V_EMAIL, value);
	}
	
	/**
	 * 
	 * 验证字符串是否为正确的qq
	 *    
	 * @param value
	 * @return
	 */
	public static boolean isQQ(String value)
	{
		return match(V_QQ, value);
	}
	
	/**
	 * 
	 * 验证字符串是否为正确的邮政编码  
	 *    
	 * @param value
	 * @return
	 */
	public static boolean isPostcode(String value)
	{
		return match(V_POSTCODE, value);
	}
	
	/**
	 * 
	 * 统一正则匹配方法  
	 * 输入为Null时,统一返回false
	 *    
	 * @param regex	用于验证的正则表达式
	 * @param str	需要验证的字符串
	 * @return
	 */
	private static boolean match(String regex, String str)
    {
		if (str == null)
		{
			return false;
		}
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }
}
