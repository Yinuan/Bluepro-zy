package com.klcxkj.zqxy.response;

import java.io.Serializable;

import com.google.gson.JsonObject;

public class PublicGetData implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String error_code;
	public String message;
	public String error_message;
	public JsonObject data;
}
