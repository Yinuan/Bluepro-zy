package com.klcxkj.zqxy.common;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.klcxkj.mylibrary.R;
import com.klcxkj.zqxy.databean.DeviceInfo;
import com.klcxkj.zqxy.databean.UserInfo;
import com.klcxkj.zqxy.ui.LoginActivity;
import com.klcxkj.zqxy.widget.Effectstype;
import com.klcxkj.zqxy.widget.NiftyDialogBuilder;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Common {
	
//	public static final String URL = "http://106.75.164.179/";
	
	public static final String URL = "http://60.191.37.212:36080/";//
			//"http://106.75.164.143/";
//	

//	public static final String URL = "http://china-qzxy.cn/";
	// public static final String URL = "http://klcxkj-qzxy.cn/";

	// public static final String BASE_URL = "http://106.75.147.104/appI/api/";

	public static  String BASE_URL ="";///http://106.75.166.86/appI/api/
	//"http://192.168.1.20:8081/klcxkj/appI/api/";
	;

	public static final String USER_IS_FIRST = "user_is_first";

	public static final String ACCOUNT_IS_USER = "is_user"; // 1 表明是管理员 2表明是用户
	public static final String USER_PHONE_NUM = "user_phone_num";
	public static final String USER_INFO = "user_info";

	public static final String PRJ_INFO = "prj_info";

	// public static final String USER_BIND_BRATHE = "user_bind_brathe";

	public static final String USER_BRATHE = "user_brathe";
	public static final String USER_WATER = "user_water";

	public static final String USER_WASHING = "user_washing";//洗衣机

	public static final float COLORSET=0.86f;

	// public static final String USER_BIND_WATER = "user_bind_water";

	/**
	 * 获取用户手机号码
	 */
	public static String getUserPhone(SharedPreferences sharedPreferences) {
		String phoneString = sharedPreferences.getString(Common.USER_PHONE_NUM, "");

		return phoneString;
	}

	/**
	 * 获取用户信息
	 */
	public static UserInfo getUserInfo(SharedPreferences sharedPreferences) {
		UserInfo userInfo = null;
		String userinfoString = sharedPreferences.getString(Common.USER_INFO, "");
		if (!TextUtils.isEmpty(userinfoString)) {
			userInfo = new Gson().fromJson(userinfoString, UserInfo.class);
		}

		return userInfo;
	}

	/**
	 * 判断用户是否是管理员
	 */
	public static boolean isAdmin(SharedPreferences sharedPreferences) {
		boolean isadmin = false;
		String userinfoString = sharedPreferences.getString(Common.USER_INFO, "");
		if (!TextUtils.isEmpty(userinfoString)) {
			UserInfo userInfo = new Gson().fromJson(userinfoString, UserInfo.class);
			if (userInfo != null && userInfo.GroupID == 1) {
				isadmin = true;
			}
		}
		return isadmin;
	}

	/**
	 * 获取保存设备信息
	 * 开水器2（用水）
	 */
	public static DeviceInfo getSaveWaterDeviceInfo(
			SharedPreferences sharedPreferences) {

		DeviceInfo bindDeviceInfo = null;
		String deviceinfoString = sharedPreferences.getString(Common.USER_WATER
				+ Common.getUserPhone(sharedPreferences), "");

		if (!TextUtils.isEmpty(deviceinfoString)) {
			bindDeviceInfo = new Gson().fromJson(deviceinfoString, DeviceInfo.class);
		}
		return bindDeviceInfo;
	}

	/**
	 * 获取绑定的设备信息
	 * 热水器1（用途：洗澡等）
	 */
	public static DeviceInfo getBindBratheDeviceInfo(
			SharedPreferences sharedPreferences) {

		DeviceInfo bindDeviceInfo = null;
		String deviceinfoString = sharedPreferences.getString(
						Common.USER_BRATHE + Common.getUserPhone(sharedPreferences), "");

		if (!TextUtils.isEmpty(deviceinfoString)) {
			bindDeviceInfo = new Gson().fromJson(deviceinfoString, DeviceInfo.class);
		}
		return bindDeviceInfo;
	}

	/**
	 * 获取绑定的设备信息
	 * 洗衣机
	 */
	public static DeviceInfo getBindWashingDeviceInfo(
			SharedPreferences sharedPreferences) {

		DeviceInfo bindDeviceInfo = null;
		String deviceinfoString = sharedPreferences.getString(
				Common.USER_WASHING + Common.getUserPhone(sharedPreferences), "");

		if (!TextUtils.isEmpty(deviceinfoString)) {
			bindDeviceInfo = new Gson().fromJson(deviceinfoString, DeviceInfo.class);
		}
		return bindDeviceInfo;
	}

	/**
	 * 判断是否登录
	 */
	public static boolean isLogin(SharedPreferences sharedPreferences) {
		boolean islogin = true;
		String user_account = sharedPreferences.getString(Common.USER_PHONE_NUM, "");

		if (TextUtils.isEmpty(user_account)) {
			islogin = false;
		}
		return islogin;
	}

	/**
	 * 判断是否绑定过账号
	 */
	public static boolean isBindAccount(SharedPreferences sharedPreferences) {
		boolean is_bind_school = false;
		UserInfo userInfo = getUserInfo(sharedPreferences);
		Log.d("Common", "userInfo.PrjID:" + userInfo.PrjID);
		if (userInfo != null && userInfo.PrjID != 0) {
			// 表明已经绑定账号
			is_bind_school = true;
		}
		return is_bind_school;
	}

	/**
	 * 判断是否绑定了设备
	 */
	public static boolean isBindDevice(SharedPreferences sharedPreferences) {
		boolean is_bind_device = false;

		DeviceInfo bindDeviceInfo = getBindBratheDeviceInfo(sharedPreferences);
		if (bindDeviceInfo != null) {
			is_bind_device = true;
		}
		return is_bind_device;
	}

	/**
	 * 检测网络是否可用
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isNetWorkConnected(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mNetworkInfo = mConnectivityManager
					.getActiveNetworkInfo();
			if (mNetworkInfo != null) {
				return mNetworkInfo.isAvailable();
			}
		}

		return false;
	}

	/**
	 * 显示toast
	 */

	private static Toast mToast;

	public static void showToast(Context context, int resid, int position) {

		Toast.makeText(context,resid,Toast.LENGTH_SHORT).show();
		/*if (mToast == null) {
			LayoutInflater inflater = LayoutInflater.from(context);
			View layout = inflater.inflate(R.layout.toast_layout, null);
			TextView title = (TextView) layout.findViewById(R.id.toast_text);
			title.setText(resid);
			mToast = new Toast(context);
			// toast.setGravity(Gravity.RIGHT | Gravity.TOP, 0, 0);
			mToast.setView(layout);

		} else {
			View view = mToast.getView();
			TextView textView = (TextView) view.findViewById(R.id.toast_text);
			textView.setText(resid);
		}
		if (position == Gravity.BOTTOM) {
			mToast.setGravity(position, 0, 100);
		} else {
			mToast.setGravity(position, 0, 0);
		}

		mToast.setDuration(Toast.LENGTH_SHORT);
		mToast.show();*/

	}

	public static void showToast(Context context, String string, int position) {
		Toast.makeText(context, string,Toast.LENGTH_SHORT).show();
	/*	if (mToast == null) {
			LayoutInflater inflater = LayoutInflater.from(context);
			View layout = inflater.inflate(R.layout.toast_layout, null);
			TextView title = (TextView) layout.findViewById(R.id.toast_text);
			title.setText(string);
			mToast = new Toast(context);
			// toast.setGravity(Gravity.RIGHT | Gravity.TOP, 0, 0);
			mToast.setView(layout);

		} else {
			View view = mToast.getView();
			TextView textView = (TextView) view.findViewById(R.id.toast_text);
			textView.setText(string);
		}
		mToast.setGravity(position, 0, 0);
		mToast.setDuration(Toast.LENGTH_SHORT);
		mToast.show();*/

	}

	/**
	 * 判断是不是手机号码
	 */
	public static boolean isPhoneNum(String phone) {
		// boolean result = false;
		// if (phone != null && phone.length() == 11 && phone.startsWith("1")) {
		// result = true;
		// } else {
		// result = false;
		// }
		// return result;

		boolean isValid = false;

		String expression = "((^(13|14|15|17|18)[0-9]{9}$)|(^0[1,2]{1}\\d{1}-?\\d{8}$)|(^0[3-9] {1}\\d{2}-?\\d{7,8}$)|(^0[1,2]{1}\\d{1}-?\\d{8}-(\\d{1,4})$)|(^0[3-9]{1}\\d{2}-? \\d{7,8}-(\\d{1,4})$))";
		CharSequence inputStr = phone;

		Pattern pattern = Pattern.compile(expression);

		Matcher matcher = pattern.matcher(inputStr);

		if (matcher.matches()) {
			isValid = true;
		}

		return isValid;

	}

	/*
	 * 验证号码 手机号 固话均可
	 */
	public static boolean isPhoneNumberValid(String phoneNumber) {
		boolean isValid = false;

		String expression = "((^(13|14|15|17|18)[0-9]{9}$)|(^0[1,2]{1}\\d{1}-?\\d{8}$)|(^0[3-9] {1}\\d{2}-?\\d{7,8}$)|(^0[1,2]{1}\\d{1}-?\\d{8}-(\\d{1,4})$)|(^0[3-9]{1}\\d{2}-? \\d{7,8}-(\\d{1,4})$))";
		CharSequence inputStr = phoneNumber;

		Pattern pattern = Pattern.compile(expression);

		Matcher matcher = pattern.matcher(inputStr);

		if (matcher.matches()) {
			isValid = true;
		}

		return isValid;

	}

	/*
	 * s获取可以显示的金额
	 */
	public static String getShowMonty(int money, String houzhui) {
		DecimalFormat df = new DecimalFormat("0.00");
		String result = df.format((float) money / 1000) + houzhui;

		return result;

	}

	public static void showNoNetworkDailog(
			final NiftyDialogBuilder dialogBuilder, Context context) {
		try {
			dialogBuilder.withTitle(context.getString(R.string.tips))
			.withMessage(context.getString(R.string.no_network))
			.withEffect(Effectstype.Fadein).isCancelable(false)
			.withButton2Text(context.getString(R.string.sure))
			.setButton2Click(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					dialogBuilder.dismiss();
				}
			}).show();
		} catch (Exception e) {
			// TODO: handle exception
		}
		
	}

	public static String[] getSubString(String string, String index) {
		String[] tagsStrings = string.split(index);
		return tagsStrings;
	}

	public static String getMacMode(String mac) {
		String regex = "(.{2})";
		String input = mac.toUpperCase().replaceAll(regex, "$1:");
		String resultString = input.substring(0, input.length() - 1);
		return resultString;
	}

	/**
	 * 将时间戳转化为时间串
	 */
	public static String timeconvertHHmm(long mill) {
		Date date = new Date(mill);
		String strs = "";
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			strs = sdf.format(date);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return strs;
	}

	public static void logout(final Context context,
			final SharedPreferences sp, final NiftyDialogBuilder dialogBuilder) {
		dialogBuilder.withTitle(context.getString(R.string.tips))
		.withMessage(context.getString(R.string.login_shixiao))
		.withEffect(Effectstype.Fadein).isCancelable(false)
		.withButton2Text(context.getString(R.string.sure))
		.setButton2Click(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Editor editor = sp.edit();
				editor.remove(Common.USER_PHONE_NUM);
				editor.remove(Common.USER_INFO);
				editor.remove(Common.ACCOUNT_IS_USER);
				editor.commit();
				Intent intent = new Intent(context, LoginActivity.class);
				if (!isTop(context, intent)) {
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
							| Intent.FLAG_ACTIVITY_NEW_TASK);
					context.startActivity(intent);
					Activity activity = (Activity) context;
					activity.finish();
				}
				
				dialogBuilder.dismiss();
			}
		}).show();




	}
	public static void logout2(final Context context, final SharedPreferences sp, final NiftyDialogBuilder dialogBuilder,String str) {
		dialogBuilder.withTitle(context.getString(R.string.tips))
				.withMessage(str)
				.withEffect(Effectstype.Fadein).isCancelable(false)
				.withButton2Text(context.getString(R.string.sure))
				.setButton2Click(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						Editor editor = sp.edit();
						editor.remove(Common.USER_PHONE_NUM);
						editor.remove(Common.USER_INFO);
						editor.remove(Common.ACCOUNT_IS_USER);
						editor.commit();
						Intent intent = new Intent(context, LoginActivity.class);
						if (!isTop(context, intent)) {
							intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
									| Intent.FLAG_ACTIVITY_NEW_TASK);
							context.startActivity(intent);
							Activity activity = (Activity) context;
							activity.finish();
						}

						dialogBuilder.dismiss();
					}
				}).show();
	}



		/**
         *
         * @Description: TODO 判断activity是否在应用的最顶层
         * @param context
         *            上下文
         * @param intent
         *            intent携带activity
         * @return boolean true为在最顶层，false为否
         * @author Sunday
         * @date 2016年3月15日
         */
	public static boolean isTop(Context context, Intent intent) {
		ActivityManager am = (ActivityManager) context
				.getSystemService(context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> appTask = am.getRunningTasks(1);
		if (appTask.size() > 0
				&& appTask.get(0).topActivity.equals(intent.getComponent())) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean compairMacSame(String mac1, String mac2) {
		boolean result = false;
		if (TextUtils.isEmpty(mac2)) {
			result = true;
		} else if (mac1.contains(":") && mac2.contains(":")) {
			if (mac1.toUpperCase().equals(mac2.toUpperCase())) {
				result = true;
			}
		} else if (mac1.contains(":") && !mac2.contains(":")) {
			if (mac1.toUpperCase().equals(getMacMode(mac2))) {
				result = true;
			}
		} else if (!mac1.contains(":") && mac2.contains(":")) {
			if (mac2.toUpperCase().equals(getMacMode(mac1))) {
				result = true;
			}
		} else if (!mac1.contains(":") && !mac2.contains(":")) {
			if (getMacMode(mac1).equals(getMacMode(mac2))) {
				result = true;
			}
		}
		return result;

	}

	
	/** 
     * 功能：身份证的有效验证 
     *  
     * @param IDStr 
     *            身份证号 
     * @return 有效：返回"" 无效：返回String信息 
     * @throws ParseException 
     */  
    public static boolean IDCardValidate(String IDStr) throws ParseException {  
        String errorInfo = "";// 记录错误信息  
        String[] ValCodeArr = { "1", "0", "x", "9", "8", "7", "6", "5", "4",  
                "3", "2" };  
        String[] Wi = { "7", "9", "10", "5", "8", "4", "2", "1", "6", "3", "7",  
                "9", "10", "5", "8", "4", "2" };  
        String Ai = "";  
        // ================ 号码的长度 15位或18位 ================  
        if (IDStr.length() != 15 && IDStr.length() != 18) {  
            errorInfo = "身份证号码长度应该为15位或18位。";  
            return false;  
        }  
        // =======================(end)========================  
  
        // ================ 数字 除最后以为都为数字 ================  
        if (IDStr.length() == 18) {  
            Ai = IDStr.substring(0, 17);  
        } else if (IDStr.length() == 15) {  
            Ai = IDStr.substring(0, 6) + "19" + IDStr.substring(6, 15);  
        }  
        if (isNumeric(Ai) == false) {  
            errorInfo = "身份证15位号码都应为数字 ; 18位号码除最后一位外，都应为数字。";  
            return false;  
        }  
        // =======================(end)========================  
  
        // ================ 出生年月是否有效 ================  
        String strYear = Ai.substring(6, 10);// 年份  
        String strMonth = Ai.substring(10, 12);// 月份  
        String strDay = Ai.substring(12, 14);// 月份  
        if (isDataFormat(strYear + "-" + strMonth + "-" + strDay) == false) {  
            errorInfo = "身份证生日无效。";  
            return false;  
        }  
        GregorianCalendar gc = new GregorianCalendar();  
        SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd");  
        try {  
            if ((gc.get(Calendar.YEAR) - Integer.parseInt(strYear)) > 150  
                    || (gc.getTime().getTime() - s.parse(  
                            strYear + "-" + strMonth + "-" + strDay).getTime()) < 0) {  
                errorInfo = "身份证生日不在有效范围。";  
                return false;  
            }  
        } catch (NumberFormatException e) {  
            // TODO Auto-generated catch block  
            e.printStackTrace();  
        } catch (java.text.ParseException e) {  
            // TODO Auto-generated catch block  
            e.printStackTrace();  
        }  
        if (Integer.parseInt(strMonth) > 12 || Integer.parseInt(strMonth) == 0) {  
            errorInfo = "身份证月份无效";  
            return false;  
        }  
        if (Integer.parseInt(strDay) > 31 || Integer.parseInt(strDay) == 0) {  
            errorInfo = "身份证日期无效";  
            return false;  
        }  
        // =====================(end)=====================  
  
        // ================ 地区码时候有效 ================  
        Hashtable h = GetAreaCode();  
        if (h.get(Ai.substring(0, 2)) == null) {  
            errorInfo = "身份证地区编码错误。";  
            return false;  
        }  
        // ==============================================  
  
        // ================ 判断最后一位的值 ================  
        int TotalmulAiWi = 0;  
        for (int i = 0; i < 17; i++) {  
            TotalmulAiWi = TotalmulAiWi  
                    + Integer.parseInt(String.valueOf(Ai.charAt(i)))  
                    * Integer.parseInt(Wi[i]);  
        }  
        int modValue = TotalmulAiWi % 11;  
        String strVerifyCode = ValCodeArr[modValue];  
        Ai = Ai + strVerifyCode;  
  
        if (IDStr.length() == 18) {  
            if (Ai.equals(IDStr) == false) {  
                errorInfo = "身份证无效，不是合法的身份证号码";  
                return false;  
            }  
        } else {  
            return true;  
        }  
        // =====================(end)=====================  
        return true;  
    }
    
    /** 
     * 功能：设置地区编码 
     *  
     * @return Hashtable 对象 
     */  
    @SuppressWarnings({ "rawtypes", "unchecked" })  
    private static Hashtable GetAreaCode() {  
        Hashtable hashtable = new Hashtable();  
        hashtable.put("11", "北京");  
        hashtable.put("12", "天津");  
        hashtable.put("13", "河北");  
        hashtable.put("14", "山西");  
        hashtable.put("15", "内蒙古");  
        hashtable.put("21", "辽宁");  
        hashtable.put("22", "吉林");  
        hashtable.put("23", "黑龙江");  
        hashtable.put("31", "上海");  
        hashtable.put("32", "江苏");  
        hashtable.put("33", "浙江");  
        hashtable.put("34", "安徽");  
        hashtable.put("35", "福建");  
        hashtable.put("36", "江西");  
        hashtable.put("37", "山东");  
        hashtable.put("41", "河南");  
        hashtable.put("42", "湖北");  
        hashtable.put("43", "湖南");  
        hashtable.put("44", "广东");  
        hashtable.put("45", "广西");  
        hashtable.put("46", "海南");  
        hashtable.put("50", "重庆");  
        hashtable.put("51", "四川");  
        hashtable.put("52", "贵州");  
        hashtable.put("53", "云南");  
        hashtable.put("54", "西藏");  
        hashtable.put("61", "陕西");  
        hashtable.put("62", "甘肃");  
        hashtable.put("63", "青海");  
        hashtable.put("64", "宁夏");  
        hashtable.put("65", "新疆");  
        hashtable.put("71", "台湾");  
        hashtable.put("81", "香港");  
        hashtable.put("82", "澳门");  
        hashtable.put("91", "国外");  
        return hashtable;  
    }  
    
    
    public static boolean isNumeric(String str) {
		Pattern pattern = Pattern.compile("[0-9]*");
		Matcher isNum = pattern.matcher(str);
		if (!isNum.matches()) {
			return false;
		}
		return true;
	}
    
    /** 
     * 验证日期字符串是否是YYYY-MM-DD格式 
     *  
     * @param str 
     * @return 
     */  
    private static boolean isDataFormat(String str) {  
        boolean flag = false;  
        // String  
        // regxStr="[1-9][0-9]{3}-[0-1][0-2]-((0[1-9])|([12][0-9])|(3[01]))";  
        String regxStr = "^((\\d{2}(([02468][048])|([13579][26]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))(\\s(((0?[0-9])|([1-2][0-3]))\\:([0-5]?[0-9])((\\s)|(\\:([0-5]?[0-9])))))?$";  
        Pattern pattern1 = Pattern.compile(regxStr);  
        Matcher isNo = pattern1.matcher(str);  
        if (isNo.matches()) {  
            flag = true;  
        }  
        return flag;  
    } 
    
    
    /**
     * 验证输入的身份证号是否合法
     */
    public static boolean isLegalId(String id){
     if (id.toUpperCase().matches("(^\\d{15}$)|(^\\d{17}([0-9]|X)$)")){
      return true;
     }else {
      return false;
     }
    }

}
