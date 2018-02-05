package com.klcxkj.zqxy.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {


	//商户PID
	public static final String PARTNER = "2088711300455398";
	//商户收款账号
	public static final String SELLER = "szklcxkj@163.com";
	//商户私钥，pkcs8格式
	public static final String RSA_PRIVATE = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAMRZx90XlL9BjN2jPbFHdIejY51uZHoWpJuBOas9H03lo7U826b5WFTyIA2HGnJUvtjYywPUOs6Yb5E8t7pAn9IlQwX+I8x7F/mc7mL4QykjY6A4vM4uuAAwMrg12lV+Rfq7Hj3Aqox08X1gwvCtfrpcOa5Aqv5H0AdH/i151s/DAgMBAAECgYEApTgOz2GKTqzvxYgY31WLHp9DsTM05O6ssBU623ZhIwD6kgSVp5jk1QWwJvHqNQ5OUNWLbrtkiHcIWiPHTbgXQhm2EzYRxyFFLeG+msjnJE9b1ADy94WA21CE9BmPxYhQvTA3+AOtoiTQgoIllqh/sUU0aKEzN+UVdaHNRjW0IxECQQDi3Wl2evcPkX+DeyC6gwOTqWzYXeLwaoKoQdCsAxVtJ61+QSoGNh3yXJvm9tNLtnUppJ+YPYsSNbFgwGl7ijzvAkEA3ZEpFiJOR7H4H0dSgoVS+oaSplo4A6aupgybOimNQeEG1FC7eNfg0W5rH7gcwm6HJm4vHj9zJvudWbubXo8CbQJAW/DfjOTIu8Z7pw1/dhcim/VN52IlVsnTqd0CdLPLOzGZXlGKdnn4KQDfLftaJpULZpURGOHYOskH2e4g0bB0gQJBALKqLlZx3WvE4A7g5XDob6wjPjg0gfy3uRefDMEO5qBcA7TLURE2C0z2XB2cylwi4oqjyQz8HzAtL4o/VR8f9NUCQAE2PuBEg7hYHQEcrl+X+nk5PS20TPl/xQFodNbzgebRXvbBdHkaCNClvR07AIw+DoxF1pBV7dw1uPwD5jsE4n0=";
	//支付宝公钥
	public static final String RSA_PUBLIC = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCnxj/9qwVfgoUh/y2W89L6BkRAFljhNhgPdyPuBV64bfQNN1PjbCzkIM6qRdKBoLPXmKKMiFYnkd6rAoprih3/PrQEB/VsW8OoM8fxn67UDYuyBTqA23MML9q1+ilIZwBC2AQ2UBVOrFXfFl75p6/B5KsiNG9zpgmLCUYuLkxpLQIDAQAB";

	public static boolean isEmpty(String s) {
		return (s == null) || "".endsWith(s) || "NULL".equalsIgnoreCase(s);
	}
	/**
	 * 根据用户名的不同长度，来进行替换 ，达到保密效果
	 *
	 * @param userName 用户名
	 * @return 替换后的用户名
	 */
	public static String userNameReplaceWithStar(String userName) {
		String userNameAfterReplaced = "";

		if (userName == null){
			userName = "";
		}

		int nameLength = userName.length();

		if (nameLength <= 1) {
			userNameAfterReplaced = "*";
		} else if (nameLength == 2) {
			userNameAfterReplaced = replaceAction(userName, "(?<=\\d{0})\\d(?=\\d{1})");
		} else if (nameLength >= 3 && nameLength <= 6) {
			userNameAfterReplaced = replaceAction(userName, "(?<=\\d{1})\\d(?=\\d{1})");
		} else if (nameLength == 7) {
			userNameAfterReplaced = replaceAction(userName, "(?<=\\d{1})\\d(?=\\d{2})");
		} else if (nameLength == 8) {
			userNameAfterReplaced = replaceAction(userName, "(?<=\\d{2})\\d(?=\\d{2})");
		} else if (nameLength == 9) {
			userNameAfterReplaced = replaceAction(userName, "(?<=\\d{2})\\d(?=\\d{3})");
		} else if (nameLength == 10) {
			userNameAfterReplaced = replaceAction(userName, "(?<=\\d{3})\\d(?=\\d{3})");
		} else if (nameLength >= 11) {
			userNameAfterReplaced = replaceAction(userName, "(?<=\\d{3})\\d(?=\\d{4})");
		}

		return userNameAfterReplaced;

	}

	/**
	 * 实际替换动作
	 *
	 * @param username username
	 * @param regular  正则
	 * @return
	 */
	private static String replaceAction(String username, String regular) {
		return username.replaceAll(regular, "*");
	}

	/**
	 * 实际替换动作
	 *
	 * @param username username
	 * @param regular  正则
	 * @return
	 */
	private static String replaceAction2(String username, String regular) {
		return username.replaceAll(regular, "x");
	}
	/**
	 * 身份证号替换，保留前四位和后四位
	 *
	 * 如果身份证号为空 或者 null ,返回null ；否则，返回替换后的字符串；
	 *
	 * @param idCard 身份证号
	 * @return
	 */
	public static String idCardReplaceWithStar(String idCard) {

		if (idCard.isEmpty() || idCard == null) {
			return "";
		} else {
			return replaceAction2(idCard, "(?<=\\d{4})\\d(?=\\d{4})");
		}
	}

	/**
	 * 手机号码替换
	 *
	 * 如果手机号码为空 或者 null ,返回null ；否则，返回替换后的字符串；
	 *
	 * @param bankCard 手机号码
	 * @return
	 */
	public static String bankPhoneReplaceWithStar(String bankCard) {

		if (bankCard.isEmpty() || bankCard == null) {
			return null;
		} else {
			return replaceAction(bankCard, "(?<=\\d{3})\\d(?=\\d{3})");
		}
	}

	// 校验Tag Alias 只能是数字,英文字母和中文
	public static boolean isValidTagAndAlias(String s) {
		Pattern p = Pattern.compile("^[\u4E00-\u9FA50-9a-zA-Z_!@#$&*+=.|]+$");
		Matcher m = p.matcher(s);
		return m.matches();
	}
}