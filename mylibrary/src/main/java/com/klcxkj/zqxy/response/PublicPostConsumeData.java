package com.klcxkj.zqxy.response;

import java.io.Serializable;

import com.google.gson.JsonObject;

public class PublicPostConsumeData implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String error_code;
	public String message;
	public int PerMoney;//预扣金额
	public int UpMoney;//消费金额
	public int UpLeadMoney;	//返回金额
	public String FishTime;//结束时间
}
