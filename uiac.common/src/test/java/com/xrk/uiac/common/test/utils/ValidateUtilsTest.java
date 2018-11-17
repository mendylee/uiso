package com.xrk.uiac.common.test.utils;

import static org.junit.Assert.*;

import java.util.Date;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.xrk.uiac.common.utils.ValidateUtils;

public class ValidateUtilsTest
{

	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception
	{
	}

	@Before
	public void setUp() throws Exception
	{
	}

	@After
	public void tearDown() throws Exception
	{
	}
	
	@Test
	public void testIsPostcode()
	{
		String postcode1 = null;
		String postcode2 = "";
		String postcode3 = "1234";
		String postcode4 = "123456";
		String postcode5 = "923456";
		String postcode6 = "12345x";
		String postcode7 = "023456";
		String postcode8 = "1234567";
		
		//空字符串
		assertFalse(ValidateUtils.isPostcode(postcode1));
		
		//""字符串
		assertFalse(ValidateUtils.isPostcode(postcode2));
		
		//太短
		assertFalse(ValidateUtils.isPostcode(postcode3));
		
		//1开头的邮编，正确
		assertTrue(ValidateUtils.isPostcode(postcode4));
		
		//9开头的邮编，正确
		assertTrue(ValidateUtils.isPostcode(postcode5));
		
		//非法字符
		assertFalse(ValidateUtils.isPostcode(postcode6));
		
		//0开头的邮编，错误
		assertFalse(ValidateUtils.isPostcode(postcode7));
		
		//太长
		assertFalse(ValidateUtils.isPostcode(postcode8));
	}
	
	@Test
	public void testIsQQ()
	{
		String qq1 = null;
		String qq2 = "";
		String qq3 = "1234";
		String qq4 = "12345";
		String qq5 = "92345";
		String qq6 = "1234567890";
		String qq7 = "9234567890";
		String qq8 = "1234567-";
		String qq9 = "01234567";
		String qq10 = "12345678901";
		
		//空字符串
		assertFalse(ValidateUtils.isQQ(qq1));
		
		//""字符串
		assertFalse(ValidateUtils.isQQ(qq2));
		
		//字符串太短
		assertFalse(ValidateUtils.isQQ(qq3));
		
		//1开头5位qq号
		assertTrue(ValidateUtils.isQQ(qq4));
		
		//9开头5位qq号
		assertTrue(ValidateUtils.isQQ(qq5));
		
		//1开头10位qq号
		assertTrue(ValidateUtils.isQQ(qq6));
		
		//9开头10位qq号
		assertTrue(ValidateUtils.isQQ(qq7));
		
		//出现非法字符
		assertFalse(ValidateUtils.isQQ(qq8));
		
		//0不能出现在第一位
		assertFalse(ValidateUtils.isQQ(qq9));
		
		//太长
		assertFalse(ValidateUtils.isQQ(qq10));
	}

	@Test
	public void testIsMobile()
	{
		String mobile1 = null;
		String mobile2 = "";
		String mobile3 = "1234";
		String mobile4 = "中文";
		String mobile5 = "14700000000";
		String mobile6 = "15400000000";
		String mobile7 = "17200000000";
		String mobile8 = "1380000000";
		String mobile9 = "188000000000";
		String mobilea = "13599966688";
		String mobileb = "18600000000";
		
		//null
		assertFalse(ValidateUtils.isMobile(mobile1));
		
		//空字符串
		assertFalse(ValidateUtils.isMobile(mobile2));
		
		//短的乱字符
		assertFalse(ValidateUtils.isMobile(mobile3));
		
		//中文
		assertFalse(ValidateUtils.isMobile(mobile4));
		
		//长度正确,但是号码段不正确
		//宽松模式，长度正确，号码段也正确
		assertTrue(ValidateUtils.isMobile(mobile5));
		
		//长度正确,但是号码段不正确
		//宽松模式，长度正确，号码段也正确
		assertTrue(ValidateUtils.isMobile(mobile6));
		
		//长度正确,但是号码段不正确
		//宽松模式，长度正确，号码段也正确
		assertTrue(ValidateUtils.isMobile(mobile7));
		
		//号码段正确,但是长度太短
		assertFalse(ValidateUtils.isMobile(mobile8));
		
		//号码段正确,但是长度太长
		assertFalse(ValidateUtils.isMobile(mobile9));
		
		//正确
		assertTrue(ValidateUtils.isMobile(mobilea));
		
		//正确
		assertTrue(ValidateUtils.isMobile(mobileb));
	}
	
	@Test
	public void testUrl(){
		String url = null;
		String url2= "http://www.sina.com";
		String url3="http://xrk.com/call.do";
		String url4="http://127.0.0.1:8081/call.do";
		//url为null 不合法
		assertFalse(ValidateUtils.isUrl(url));
		//正确
		assertTrue(ValidateUtils.isUrl(url2));
		//正确
		assertTrue(ValidateUtils.isUrl(url3));
		assertTrue(ValidateUtils.isUrl(url4));
	}

	@Test
	public void testIsEmail()
	{
		String email1 = null;
		String email2 = "";
		String email3 = "yexiaoxiao@xiangrikui.com";
		String email4 = "yexiao_xiao@xiangrikui.com";
		String email5 = "yexiao-xiao@xiangrikui.com";
		String email6 = ".yexiaoxiao@xiangrikui.com";
		String email7 = "yexiaoxiao@xiangrikui.cn";
		String email8 = "yexiaoxiao@xiangrikui.com.cn";
		String email9 = "yexiaoxiao@xiangrikui.c-m";
		String email10 = "02000051010680002015003668";
		String email11 = "fsdklfjklasklgaklsdjklfalsdlfkasjkdfjklas";
		String email12 = "lfaklsdfjkl23kjlfaslkdf92jlkfasdlkflk3r53432klasdklfjaljs@324523kl42jkljkfgj2kl3";
		String email13 = "fsdfsda@123423@fkskdjf@12342134";
		String email14 = "8935jkasfaklfdkl.fweljrkwle23423ifjksala09324023094klkfasd@90234ji2342jfjfvasdfal324fsdfsdfsdfsdfsdfsdfsdf42342342323423kavd.com.cn";
		
		//null字符串
		assertFalse(ValidateUtils.isEmail(email1));
		
		//""字符串
		assertFalse(ValidateUtils.isEmail(email2));
		
		//正确
		assertTrue(ValidateUtils.isEmail(email3));
		
		//合法字符
		assertTrue(ValidateUtils.isEmail(email4));
		
		//合法字符
		assertTrue(ValidateUtils.isEmail(email5));
		
		//非法字符
		assertFalse(ValidateUtils.isEmail(email6));
		
		//正确
		assertTrue(ValidateUtils.isEmail(email7));
		
		//正确
		assertTrue(ValidateUtils.isEmail(email8));
		
		//非法字符
		System.out.println("非法字符匹配");
		System.out.println("start at: " + new Date());
		//assertFalse(ValidateUtils.isEmail(email9));
		System.out.println("end at: " + new Date());
		
		//超长非法字符
		System.out.println("超长非法字符匹配");
		System.out.println("start at: " + new Date());
		assertFalse(ValidateUtils.isEmail(email10));
		System.out.println("end at: " + new Date());
		
		//超超长非法字符
		System.out.println("超超长非法字符匹配");
		System.out.println("start at: " + new Date());
		assertFalse(ValidateUtils.isEmail(email11));
		System.out.println("end at: " + new Date());
		
		//超超超长非法字符
		System.out.println("超超超长非法字符匹配");
		System.out.println("start at: " + new Date());
		assertFalse(ValidateUtils.isEmail(email12));
		System.out.println("end at: " + new Date());
		
		//很多@混淆
		System.out.println("很多@混淆的非法字符匹配");
		System.out.println("start at: " + new Date());
		assertFalse(ValidateUtils.isEmail(email13));
		System.out.println("end at: " + new Date());
		
		//超长合法字符
		System.out.println("超长合法字符匹配");
		System.out.println("start at: " + new Date());
		assertTrue(ValidateUtils.isEmail(email14));
		System.out.println("end at: " + new Date());
	}

}
