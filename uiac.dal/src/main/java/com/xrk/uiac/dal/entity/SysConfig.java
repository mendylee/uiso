package com.xrk.uiac.dal.entity;

import com.xrk.hws.dal.annotations.Column;
import com.xrk.hws.dal.annotations.Table;
import com.xrk.hws.dal.common.DbType;

/**
 * 
 * SysConfig: 系统设置字典mongo实体类
 *
 * <br>=
 * ========================= <br>
 * 公司：广州向日葵信息科技有限公司 <br>
 * 开发：zhub<zhubin@xiangrikui.com> <br>
 * 版本：1.0 <br>
 * 创建时间：2015年5月13日 <br>
 * JDK版本：1.7 <br>=
 * =========================
 */
@Table(name = "uiac_app_setting")
public class SysConfig
{
	@Column(name = "param_key", type = DbType.VARCHAR)
	private String item;
	@Column(name = "param_value", type = DbType.VARCHAR)
	private String value;

	public String getItem()
	{
		return item;
	}

	public void setItem(String item)
	{
		this.item = item;
	}

	public String getValue()
	{
		return value;
	}

	public void setValue(String value)
	{
		this.value = value;
	}

}
