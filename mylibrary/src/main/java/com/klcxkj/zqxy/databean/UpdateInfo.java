package com.klcxkj.zqxy.databean;

public class UpdateInfo {

	private String message;
	private String error_code;
	private String url;
	public UpdateInfo(String message, String error_code, String url) {
		super();
		this.message = message;
		this.error_code = error_code;
		this.url = url;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getError_code() {
		return error_code;
	}
	public void setError_code(String error_code) {
		this.error_code = error_code;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	
	
}
