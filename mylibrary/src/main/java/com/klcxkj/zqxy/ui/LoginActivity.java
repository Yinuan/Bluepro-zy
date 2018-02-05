package com.klcxkj.zqxy.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.klcxkj.zqxy.MyApp;
import com.klcxkj.mylibrary.R;
import com.klcxkj.zqxy.common.Common;
import com.klcxkj.zqxy.databean.UserInfo;
import com.klcxkj.zqxy.response.PublicGetData;
import com.klcxkj.zqxy.response.PublicPDData;
import com.klcxkj.zqxy.utils.AppPreference;
import com.klcxkj.zqxy.utils.GlobalTools;
import com.klcxkj.zqxy.utils.MD5Util;
import com.klcxkj.zqxy.widget.SecurityCodeView;
import com.klcxkj.zqxy.widget.SecurityCodeView.InputCompleteListener;
import com.klcxkj.zqxy.widget.TimeButton;

import net.android.tools.afinal.FinalHttp;
import net.android.tools.afinal.http.AjaxCallBack;
import net.android.tools.afinal.http.AjaxParams;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;


public class LoginActivity extends BaseActivity {

	public static final int PROCESS_GETCODE = 1;


	private EditText user_phone_num_edit;
	private EditText check_code_edit;
	private TimeButton send_phone_identifying_code_btn;
	private Button login_btn;

	private LinearLayout content_layout;
	private RelativeLayout security_code_layout;
	private ImageView securitycode_img, fresh_img;

	private SecurityCodeView scv_edittext;

	private SharedPreferences sp;

	private PublicPDData publicPDData;
	//登录用户选择
	private LinearLayout login_type_chose;
	private RelativeLayout login_stu,login_admin;
	private TextView login_type_stu_txt,login_type_admin_txt;
	private View login_type_stu_line,login_type_admin_line;

	private String isOpUser ="0";//学生与管理员的判断 0 为学生 1 为 管理员
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		sp = getSharedPreferences("adminInfo", Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putBoolean(Common.USER_IS_FIRST, false);
		editor.commit();
		AppPreference.getInstance().saveWashingTime("0");
		initView();
	}

	private void initView() {
		//顶部
		showMenu("登录");
		backLayout.setVisibility(View.GONE);
		//选择用户
		login_type_chose = (LinearLayout) findViewById(R.id.login_type_chose);
		login_stu = (RelativeLayout) findViewById(R.id.login_stu);
		login_admin = (RelativeLayout) findViewById(R.id.login_admin);
		login_type_stu_txt = (TextView) findViewById(R.id.login_type_stu_txt);
		login_type_admin_txt = (TextView) findViewById(R.id.login_type_admin_txt);
		login_type_stu_line =findViewById(R.id.login_type_stu_line);
		login_type_admin_line =findViewById(R.id.login_type_admin_line);

		user_phone_num_edit = (EditText) findViewById(R.id.user_phone_num_edit);
		check_code_edit = (EditText) findViewById(R.id.check_code_edit);
		send_phone_identifying_code_btn = (TimeButton) findViewById(R.id.send_phone_identifying_code_btn);
		send_phone_identifying_code_btn.setEditText(user_phone_num_edit);

		content_layout = (LinearLayout) findViewById(R.id.content_layout);
		security_code_layout = (RelativeLayout) findViewById(R.id.security_code_layout);
		securitycode_img = (ImageView) findViewById(R.id.securitycode_img);
		fresh_img = (ImageView) findViewById(R.id.fresh_img);
		scv_edittext = (SecurityCodeView) findViewById(R.id.scv_edittext);
		scv_edittext.setInputCompleteListener(inputCompleteListener);

		content_layout.setVisibility(View.VISIBLE);
		security_code_layout.setVisibility(View.GONE);

		login_btn = (Button) findViewById(R.id.login_btn);

		login_btn.setOnClickListener(onClickListener);
		send_phone_identifying_code_btn.setOnClickListener(onClickListener);

		fresh_img.setOnClickListener(onClickListener);

		login_admin.setOnClickListener(onClickListener);
		login_stu.setOnClickListener(onClickListener);
	}

	private OnClickListener onClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {

			int i = v.getId();
			if (i == R.id.fresh_img) {
				getSC();


			} else if (i == R.id.send_phone_identifying_code_btn) {
				hidenInputMethod(user_phone_num_edit, LoginActivity.this);
				showInputMethod(LoginActivity.this);
				getSC();

			} else if (i == R.id.login_btn) {//login(user_phone_num_edit.getText().toString(), "0", "1");
				String checkcode = check_code_edit.getText().toString();
				if (TextUtils.isEmpty(checkcode)) {
					Common.showToast(LoginActivity.this, R.string.hint_check_code, Gravity.CENTER);
					return;
				}
				login(user_phone_num_edit.getText().toString(), "0", checkcode);

				/*if (publicPDData != null && !TextUtils.isEmpty(publicPDData.code)) {
					if (!publicPDData.code.equals(checkcode)) {
						Common.showToast(LoginActivity.this, R.string.checkcode_error, Gravity.CENTER);
					} else {
						login(user_phone_num_edit.getText().toString(), "0", checkcode);
					}
				} else {
					Common.showToast(LoginActivity.this, R.string.checkcode_error, Gravity.CENTER);
				}*/


			} else if (i == R.id.login_stu) {
				login_type_stu_line.setVisibility(View.VISIBLE);
				login_type_stu_txt.setTextColor(getResources().getColor(R.color.base_color));

				login_type_admin_line.setVisibility(View.GONE);
				login_type_admin_txt.setTextColor(getResources().getColor(R.color.txt_two));
				isOpUser = "0";

			} else if (i == R.id.login_admin) {
				login_type_stu_line.setVisibility(View.GONE);
				login_type_stu_txt.setTextColor(getResources().getColor(R.color.txt_two));

				login_type_admin_line.setVisibility(View.VISIBLE);
				login_type_admin_txt.setTextColor(getResources().getColor(R.color.base_color));
				isOpUser = "1";

			} else {
			}

		}

	};

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				Bitmap bmp = (Bitmap) msg.obj;
				securitycode_img.setImageBitmap(bmp);
				login_type_chose.setVisibility(View.GONE);
				break;
			case PROCESS_GETCODE:
				
				TimeHandlerData timeHandlerData = (TimeHandlerData) msg.obj;
				String phonenum = user_phone_num_edit.getText().toString();
				
				String s = timeHandlerData.timeString.substring(0, 11) + "telPhoneStr" + phonenum;
				String sk2 = MD5Util.getMd5(s);

				AjaxParams ajaxParams = new AjaxParams();
				ajaxParams.put("Code", timeHandlerData.codeString);
				ajaxParams.put("token", sk2);
				ajaxParams.put("TelPhone", phonenum);
				ajaxParams.put("phoneSystem", "Android");
				ajaxParams.put("version", MyApp.versionCode);

				new FinalHttp().get(Common.BASE_URL + "getCode", ajaxParams,
						new AjaxCallBack<Object>() {

							@Override
							public void onSuccess(Object t) {
								super.onSuccess(t);
								String result = t.toString();
								Log.e("water", "checkcode resutl = " + result);
								publicPDData = new Gson().fromJson(result, PublicPDData.class);
								if (publicPDData.error_code.equals("0")) {
									if (!TextUtils.isEmpty(publicPDData.code)) {
										Common.showToast(LoginActivity.this, R.string.check_code_sended, Gravity.CENTER);
										send_phone_identifying_code_btn.startTime(send_phone_identifying_code_btn);
									}
								} else {
									Common.showToast(LoginActivity.this, publicPDData.message, Gravity.CENTER);
								}

							}

							@Override
							public void onFailure(Throwable t, int errorNo, String strMsg) {
								super.onFailure(t, errorNo, strMsg);
							}
						});
				
				break;
			}
		}

	};

	private InputCompleteListener inputCompleteListener = new InputCompleteListener() {

		@Override
		public void inputComplete() {
			scv_edittext.clearFocus();
			hidenInputMethod(scv_edittext, LoginActivity.this);
			new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {
					String code = scv_edittext.getEditContent();
					scv_edittext.clearEditText();
					content_layout.setVisibility(View.VISIBLE);
					security_code_layout.setVisibility(View.GONE);
					getcheckcode(user_phone_num_edit.getText().toString(), code);
					login_type_chose.setVisibility(View.VISIBLE);
				}
			}, 1000);

		}

		@Override
		public void deleteContent(boolean isDelete) {
			// TODO Auto-generated method stub

		}
	};

	private void getcheckcode(String phonenum, final String code) {

		if (Common.isNetWorkConnected(LoginActivity.this)) {
			if (TextUtils.isEmpty(phonenum)) {
				Common.showToast(LoginActivity.this, R.string.phonenum_null, Gravity.CENTER);
				return;
			}
			if (!Common.isPhoneNum(phonenum)) {
				Common.showToast(LoginActivity.this, R.string.phonenum_not_irregular, Gravity.CENTER);
				return;
			}

			publicPDData = null;

			new Thread(new Runnable() {

				@Override
				public void run() {
					String timeString;
					URL url = null;// 取得资源对象
					try {
						url = new URL("https://www.baidu.com/");
						URLConnection uc = url.openConnection();// 生成连接对象
						uc.connect(); // 发出连接
						long time = uc.getDate(); // 取得网站日期时间

						if (time != 0) {
							SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmm");
							timeString = df.format(new Date(time));
							
							TimeHandlerData timeHandlerData= new TimeHandlerData();
							timeHandlerData.timeString = timeString;
							timeHandlerData.codeString = code;
							Message message = new Message();
							message.what = PROCESS_GETCODE;
							message.obj = timeHandlerData;
							mHandler.sendMessage(message);
						} else {
							SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmm");
							timeString = df.format(new Date());
							
							TimeHandlerData timeHandlerData= new TimeHandlerData();
							timeHandlerData.timeString = timeString;
							timeHandlerData.codeString = code;
							Message message = new Message();
							message.what = PROCESS_GETCODE;
							message.obj = timeHandlerData;
							mHandler.sendMessage(message);

						}
					} catch (Exception e) {
						e.printStackTrace();

						SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmm");
						timeString = df.format(new Date());
						
						TimeHandlerData timeHandlerData= new TimeHandlerData();
						timeHandlerData.timeString = timeString;
						timeHandlerData.codeString = code;
						Message message = new Message();
						message.what = PROCESS_GETCODE;
						message.obj = timeHandlerData;
						mHandler.sendMessage(message);
					}
				}
			}).start();

		} else {
			Common.showNoNetworkDailog(dialogBuilder, LoginActivity.this);
		}

	}

	protected void getSC() {
		content_layout.setVisibility(View.GONE);
		security_code_layout.setVisibility(View.VISIBLE);
		user_phone_num_edit.clearFocus();
		scv_edittext.requestFocus();
		final String phonenum = user_phone_num_edit.getText().toString();
		if (TextUtils.isEmpty(phonenum)) {
			Common.showToast(LoginActivity.this, R.string.phonenum_null, Gravity.CENTER);
			return;
		}
		if (!Common.isPhoneNum(phonenum)) {
			Common.showToast(LoginActivity.this, R.string.phonenum_not_irregular, Gravity.CENTER);
			return;
		}

		// 新建线程加载图片信息，发送到消息队列中
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Bitmap bmp = getURLimage(Common.URL + "api/authImage?mobile=" + phonenum);
				Message msg = new Message();
				msg.what = 0;
				msg.obj = bmp;
				System.out.println("000");
				mHandler.sendMessage(msg);
			}
		}).start();
	}

	protected void login(final String phonenum, String prjid, String checkcode) {
		if (Common.isNetWorkConnected(LoginActivity.this)) {
			if (TextUtils.isEmpty(phonenum)) {
				Common.showToast(LoginActivity.this, R.string.phonenum_null, Gravity.CENTER);
				return;
			}
			if (!Common.isPhoneNum(phonenum)) {
				Common.showToast(LoginActivity.this, R.string.phonenum_not_irregular, Gravity.CENTER);
				return;
			}
			login_btn.setEnabled(false);
			loadingDialogProgress =GlobalTools.getInstance().showDailog(LoginActivity.this,"登录中.");
			AjaxParams ajaxParams = new AjaxParams();
			ajaxParams.put("TelPhone", phonenum);
			ajaxParams.put("PrjID", "0");
			ajaxParams.put("Code",checkcode);//
			ajaxParams.put("isOpUser",isOpUser);
			ajaxParams.put("phoneSystem", "Android");
			ajaxParams.put("version", MyApp.versionCode);

			Log.d("LoginActivity", "ajaxParams:" + ajaxParams);
			new FinalHttp().get(Common.BASE_URL + "login", ajaxParams,
					new AjaxCallBack<Object>() {

						@Override
						public void onSuccess(Object t) {
							super.onSuccess(t);
							login_btn.setEnabled(true);
							if (loadingDialogProgress !=null){
								loadingDialogProgress.dismiss();
							}
							String result = t.toString();
							Log.e("LoginActivity", "login result = " + result);
							PublicGetData publicGetData = new Gson().fromJson(result, PublicGetData.class);
							if (publicGetData.error_code.equals("0") || publicGetData.error_code.equals("5")) {

								UserInfo userInfo = new Gson().fromJson(publicGetData.data, UserInfo.class);
								if (isOpUser.equals("1")){

									if (userInfo.GroupID!=1){
										toast("登录异常,没有该管理员");
										return;
									}
								}
								//登陆成功 注册极光推送服务
								if (userInfo.tags !=null && userInfo.tags.length()>0){
									Set<String> set =new HashSet<String>();
									String[] tags =userInfo.tags.split(",");
									for (String item: tags) {
										if (!TextUtils.isEmpty(item)){
											if (GlobalTools.isValidTagAndAlias(item)){
												set.add(item);
											}
										}
									}
									Log.d("LoginActivity", "注册极光");
								}
								Intent intent = new Intent();
								Editor editor = sp.edit();
								editor.putString(Common.USER_PHONE_NUM, phonenum);

								if (userInfo == null) {

									editor.putInt(Common.ACCOUNT_IS_USER, 2);//设定为学生用户
									AppPreference.getInstance().saveWashingTime("2");
									intent.setClass(LoginActivity.this, MainUserActivity.class);
									editor.putString(Common.USER_INFO, "");
								} else {

									userInfo.TelPhone = Long.valueOf(phonenum);

									if (userInfo.GroupID == 1) {
										editor.putInt(Common.ACCOUNT_IS_USER, 1);
										AppPreference.getInstance().saveWashingTime("1");
										intent.setClass(LoginActivity.this, MainAdminActivity.class);
									} else {
										userInfo.GroupID = 2;
										editor.putInt(Common.ACCOUNT_IS_USER, 2);
										AppPreference.getInstance().saveWashingTime("2");
										intent.setClass(LoginActivity.this, MainUserActivity.class);

									}

								}
								editor.putString(Common.USER_INFO, new Gson().toJson(userInfo));
								editor.commit();
								startActivity(intent);
								finish();

							} else if (publicGetData.error_code.equals("3")) {
								Common.showToast(LoginActivity.this, R.string.yanzhengma_error, Gravity.CENTER);
							} else {
								Common.showToast(LoginActivity.this, publicGetData.message, Gravity.CENTER);
							}

						}

						@Override
						public void onFailure(Throwable t, int errorNo, String strMsg) {
							super.onFailure(t, errorNo, strMsg);
							login_btn.setEnabled(true);
							toast("登录失败,请稍后重试");
							if (loadingDialogProgress !=null){
								loadingDialogProgress.dismiss();
							}
						}
					});

		} else {
			Common.showNoNetworkDailog(dialogBuilder, LoginActivity.this);
		}

	}

	// 加载图片
	private Bitmap getURLimage(String url) {
		Bitmap bmp = null;
		try {
			URL myurl = new URL(url);
			// 获得连接
			HttpURLConnection conn = (HttpURLConnection) myurl.openConnection();
			conn.setConnectTimeout(6000);// 设置超时
			conn.setDoInput(true);
			conn.setUseCaches(false);// 不缓存
			conn.connect();
			InputStream is = conn.getInputStream();// 获得图片的数据流
			bmp = BitmapFactory.decodeStream(is);
			is.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bmp;
	}

	public static void hidenInputMethod(View view, Context context) {
		InputMethodManager inputMethodManager = (InputMethodManager) context
				.getApplicationContext().getSystemService(
						Context.INPUT_METHOD_SERVICE);
		inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(),
				InputMethodManager.HIDE_NOT_ALWAYS);
	}

	public static void showInputMethod(Context context) {
		InputMethodManager inputMethodManager = (InputMethodManager) context
				.getApplicationContext().getSystemService(
						Context.INPUT_METHOD_SERVICE);
		inputMethodManager.toggleSoftInput(0,
				InputMethodManager.HIDE_NOT_ALWAYS);
	}

	@Override
	public void onBackPressed() {
		if (content_layout.getVisibility() != View.VISIBLE) {
			scv_edittext.clearFocus();
			hidenInputMethod(scv_edittext, LoginActivity.this);
			scv_edittext.clearEditText();
			content_layout.setVisibility(View.VISIBLE);
			security_code_layout.setVisibility(View.GONE);
		} else {
			super.onBackPressed();
		}

	}

	
	private class TimeHandlerData{
		public String timeString;
		public String codeString;
	}


}
