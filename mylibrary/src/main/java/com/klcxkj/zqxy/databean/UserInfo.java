package com.klcxkj.zqxy.databean;

import java.io.Serializable;

public class UserInfo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
/*	public int AccMoney;  // 账户余额
	public int AccStatusID; // 账户状态目前未知
	public int PrjID;  // 项目id（一个学校一个项目），绑定账号后才有
	public String PrjName;
	public int AccID;  // 账户id 管理员的opid用此参数
	public int GroupID;  // 1 管理员  2 普通用户
	public long TelPhone;
	public String loginCode;
	public int GivenAccMoney;  // 账户赠送金额*/

	/**
	 * PrjName : 凯路创新
	 * TelPhone : 18565651403
	 * loginCode : 873076
	 * AccMoney : 77800
	 * alias : 18565651403
	 * GivenAccMoney : -5982
	 * AccStatusID : 0
	 * AccID : 157
	 * tags : test,1
	 * PrjID : 1
	 * GroupID : 2
	 */
	public String PrjName;
	public long TelPhone;
	public String loginCode;
	public int AccMoney;
	public String alias;
	public int GivenAccMoney;
	public int AccStatusID;
	public int AccID;
	public String tags;
	public int PrjID;
	public int GroupID;


	@Override
	public String toString() {
		return "UserInfo{" +
				"PrjName='" + PrjName + '\'' +
				", TelPhone=" + TelPhone +
				", loginCode='" + loginCode + '\'' +
				", AccMoney=" + AccMoney +
				", alias='" + alias + '\'' +
				", GivenAccMoney=" + GivenAccMoney +
				", AccStatusID=" + AccStatusID +
				", AccID=" + AccID +
				", tags='" + tags + '\'' +
				", PrjID=" + PrjID +
				", GroupID=" + GroupID +
				'}';
	}
}
