package com.klcxkj.zqxy.response;

import java.io.Serializable;

import com.google.gson.JsonObject;

public class PublicPDData implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String error_code;
	public String message;
	public String code;
	public JsonObject pd;
}
