package com.xrk.uiac.bll.common;

/**
 * 
 * 用户模块相关常量
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：yexx<yexiaoxiao@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年5月15日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public class UserConstants
{
	/**
	 * 用户账号类型，以手机号为主账号
	 */
	public static final int USERCODE_TYPE_MOBILE = 1;
	
	/**
	 * 用户账号类型，以邮箱未主账号
	 */
	public static final int USERCODE_TYPE_EMAIL = 2;
	
	/**
	 * 用户账号类型，以普通的字符串为主账号
	 */
	public static final int USERCODE_TYPE_NORMAL = 3;
	
	/**
	 * 密码最大长度
	 */
	public static final int PASSWORD_MAX_LEN = 16;
	
	/**
	 * 密码最小长度
	 */
	public static final int PASSWORD_MIN_LEN = 6;
	
	/**
	 * MD5加密后密码的长度
	 */
	public static final int PASSWORD_MD5_LEN = 32;
	
	/**
	 * 账号已被删除
	 */
	public static final int ACCOUNT_DEL_STATUS_DELETE = 1;
	
	/**
	 * 账号未被删除
	 */
	public static final int ACCOUNT_DEL_STATUS_NORMAL = 0;
	
	/**
	 * 账号已被禁用
	 */
	public static final int ACCOUNT_STATUS_DISABLED = 2;
	
	/**
	 * 账号未被禁用
	 */
	public static final int ACCOUNT_STATUS_ENABLED = 1;
	
	/**
	 * 性别, 未设置
	 */
	public static final int ACCOUNT_GENDER_NOT_SET = 0;
	
	/**
	 * 性别, 男
	 */
	public static final int ACCOUNT_GENDER_MALE = 1;
	
	/**
	 * 性别, 女
	 */
	public static final int ACCOUNT_GENDER_FEMALE = 2;
	
	/**
	 * 验证码类型, 注册
	 */
	public static final int CAPTCHA_CHECKTYPE_REGISTER = 1;
	
	/**
	 * 验证码类型, 重置密码
	 */
	public static final int CAPTCHA_CHECKTYPE_RESET_PASSWORD = 2;
	
	/**
	 * 验证码类型, 绑定
	 */
	public static final int CAPTCHA_CHECKTYPE_BINDING = 3;
	
	/**
	 * 验证码类型，登录
	 */
	public static final int CAPTCHA_CHECKTYPE_LOGIN = 4;
	
	/**
	 * 验证码校验结果, 验证码过期
	 */
	public static final int CAPTCHA_VALIDATION_RESULT_EXPIRE = 3;
	
	/**
	 * 验证码校验结果, 验证码未验证
	 */
	public static final int CAPTCHA_VALIDATION_RESULT_UNVERIFIED = 2;
	
	/**
	 * 验证码校验结果, 验证成功
	 */
	public static final int CAPTCHA_VALIDATION_RESULT_SUCCESS = 1;
	
	/**
	 * token校验结果, 过期
	 */
	public static final int ACCESSTOKEN_VALIDATION_RESULT_EXPIRE = 3;
	
	/**
	 * token校验结果, 无效
	 */
	public static final int ACCESSTOKEN_VALIDATION_RESULT_INVALID = 2;
	
	/**
	 * token校验结果, 成功
	 */
	public static final int ACCESSTOKEN_VALIDATION_RESULT_SUCCESS = 1;
	
	/**
	 * 手机号已绑定
	 */
	public static final int MOBILE_IS_VERIFIED = 1;
	
	/**
	 * 手机号未绑定
	 */
	public static final int MOBILE_IS_NOT_VERIFIED = 0;
	
	/**
	 * 邮箱已绑定
	 */
	public static final int EMAIL_IS_VERIFIED = 1;
	
	/**
	 * 邮箱未绑定
	 */
	public static final int EMAIL_IS_NOT_VERIFIED = 0;
	
	/**
	 * app是第三方应用
	 */
	public static final int APP_IS_THIRDPARTY = 1;
	
	/**
	 * app不是第三方应用
	 */
	public static final int APP_IS_NOT_THIRDPARTY = 0;
	
	/**
	 * app未被删除
	 */
	public static final int APP_IS_DEL = 1;
	
	/**
	 * app已被删除
	 */
	public static final int APP_IS_NOT_DEL = 0;
	
	/**
	 * 验证码验证状态, 已成功
	 */
	public static final int VERIFY_STATUS_SUCCESS = 1;
	
	/**
	 * 验证码验证状态, 未验证
	 */
	public static final int VERIFY_STATUS_NOT_SUCCESS = 0;
	
	/**
	 * bool值的字符串形式, true
	 */
	public static final String BOOLEAN_TRUE = "true";
	
	/**
	 * bool值的字符串形式, false
	 */
	public static final String BOOLEAN_FALSE = "false";
	
	/**
	 * 验证码的默认过期时间
	 */
	public static final int DEFAULT_CAPTCHA_EXPIRE_TIME = 120;
	
	/**
	 * 验证结果的默认过期时间
	 */
	public static final int DEFAULT_VALIDATION_EXPIRE_TIME = 1800;
	
	/**
	 * 用户基本信息校验类型，注册用户
	 */
	public static final int USERINFO_VALIDATION_TYPE_CREATE_USER = 1;
	
	/**
	 * 用户基本信息校验类型，更新用户基本信息
	 */
	public static final int USERINFO_VALIDATION_TYPE_UPDATE = 2;
	
	/**
	 * 用户未经过验证
	 */
	public static final int USER_IS_UNVERIFIED = 1;
	
	/**
	 * 用户已经过验证
	 */
	public static final int USER_IS_VERIFIED = 0;
	
	/**
	 * 绑定手机号后，账号改变
	 */
	public static final String REMARK_BINDING_CHANGE_ACCOUNT = "account changes after binding";
}
