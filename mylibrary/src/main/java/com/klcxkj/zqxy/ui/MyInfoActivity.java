package com.klcxkj.zqxy.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.klcxkj.zqxy.MyApp;
import com.klcxkj.mylibrary.R;
import com.klcxkj.zqxy.common.Common;
import com.klcxkj.zqxy.databean.DeviceInfo;
import com.klcxkj.zqxy.databean.IDCardData;
import com.klcxkj.zqxy.databean.UserInfo;
import com.klcxkj.zqxy.response.PublicGetData;
import com.klcxkj.zqxy.utils.AppPreference;
import com.klcxkj.zqxy.widget.Effectstype;
import com.klcxkj.zqxy.zxing.zxing.activity.CaptureActivity;

import net.android.tools.afinal.FinalHttp;
import net.android.tools.afinal.http.AjaxCallBack;
import net.android.tools.afinal.http.AjaxParams;

public class MyInfoActivity extends BaseActivity {

	private TextView back_img;

	private TextView logout_btn;

	private TextView my_account_txt;
	private TextView my_school_txt;
	private TextView my_room_number_txt;

	private RelativeLayout room_layout;


	private SharedPreferences sp;

	private boolean is_admin;

	private LinearLayout save_userinfo_layout;
	private EditText my_name_txt, my_idcard_txt;
	private RadioButton man_Button, woman_Button;
	private Button save_btn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_myinfo);
		sp = getSharedPreferences("adminInfo", Context.MODE_PRIVATE);

		is_admin = getIntent().getExtras().getBoolean("is_admin");
		initView();
	}

	private void initView() {
		showMenu("个人中心");
		back_img = (TextView) findViewById(R.id.back_img);
		logout_btn = (TextView) findViewById(R.id.logout_btn);

		back_img.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

		logout_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Editor editor = sp.edit();
				editor.remove(Common.USER_PHONE_NUM);
				editor.remove(Common.USER_INFO);
				editor.remove(Common.ACCOUNT_IS_USER);
				editor.commit();

				Intent intent = new Intent(MyInfoActivity.this, LoginActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
				finish();

			}
		});

		my_account_txt = (TextView) findViewById(R.id.my_account_txt);
		my_school_txt = (TextView) findViewById(R.id.my_school_txt);
		my_room_number_txt = (TextView) findViewById(R.id.my_room_number_txt);
		room_layout = (RelativeLayout) findViewById(R.id.room_layout);

		DeviceInfo deviceInfo = getDev();

		final UserInfo userInfo = Common.getUserInfo(sp);

		if (userInfo != null) {
			my_account_txt.setText("" + userInfo.TelPhone);

			if (TextUtils.isEmpty(userInfo.PrjName)) {
				my_school_txt.setText(R.string.no_room);
			} else {
				my_school_txt.setText(userInfo.PrjName);
			}

		}

		if (is_admin) {
			room_layout.setVisibility(View.GONE);
		} else {
			room_layout.setVisibility(View.VISIBLE);
			if (deviceInfo != null) {
				if (TextUtils.isEmpty(deviceInfo.DevName)) {
					my_room_number_txt.setText(R.string.no_room);
				} else {
					my_room_number_txt.setText(deviceInfo.DevName);
				}

			} else {
				my_room_number_txt.setText(R.string.no_room);
			}
		}

		save_userinfo_layout = (LinearLayout) findViewById(R.id.save_userinfo_layout);
		my_name_txt = (EditText) findViewById(R.id.my_name_txt);
		my_idcard_txt = (EditText) findViewById(R.id.my_idcard_txt);
		save_btn = (Button) findViewById(R.id.save_btn);

		man_Button = (RadioButton) findViewById(R.id.man_Button);
		woman_Button = (RadioButton) findViewById(R.id.woman_Button);

		if (is_admin) {
			save_userinfo_layout.setVisibility(View.GONE);
		} else {

			save_userinfo_layout.setVisibility(View.VISIBLE);

			save_userinfo_layout.setVisibility(View.VISIBLE);

			getInfo(userInfo);

		}

		save_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (!Common.isBindAccount(sp)) {

					showNoBindAccount();

				} else {

					String name = my_name_txt.getText().toString();
					String sex = null;
					if (man_Button.isChecked()) {
						sex = "男";
					} else if (woman_Button.isChecked()) {
						sex = "女";
					}
					String idcard = my_idcard_txt.getText().toString();

					if (TextUtils.isEmpty(name)) {
						Common.showToast(MyInfoActivity.this, getString(R.string.hint_name), Gravity.CENTER);
						return;
					}

					if (TextUtils.isEmpty(sex)) {
						Common.showToast(MyInfoActivity.this, getString(R.string.hint_sex), Gravity.CENTER);
						return;
					}

					if (TextUtils.isEmpty(idcard)) {
						Common.showToast(MyInfoActivity.this, getString(R.string.hint_IDcard), Gravity.CENTER);
						return;
					}

//					updateInfo(userInfo, name, sex, idcard);

					// String result = IDCard.IDCardValidate(idcard);
					// if (TextUtils.isEmpty(result)) {
					// updateInfo(userInfo, name, sex, idcard);
					// }else {
					// Common.showToast(MyInfoActivity.this,
					// R.string.user_peopleid_error, Gravity.CENTER);
					// }

					if (!Common.isLegalId(idcard)) {
						Common.showToast(MyInfoActivity.this, R.string.user_peopleid_error, Gravity.CENTER);
						return;
					}
					updateInfo(userInfo, name, sex, idcard);

				}

			}
		});

	}

	private DeviceInfo getDev(){
		DeviceInfo deviceInfo1 =null;
		if (Common.getBindBratheDeviceInfo(sp) !=null){
			deviceInfo1 =Common.getBindBratheDeviceInfo(sp);
		}else {
			deviceInfo1 =Common.getBindWashingDeviceInfo(sp);
		}
		return deviceInfo1;
	}

	private void updateInfo(final UserInfo mInfo, final String name, final String sex, final String idcard) {

		if (Common.isNetWorkConnected(this)) {

			AjaxParams ajaxParams = new AjaxParams();
			ajaxParams.put("PrjID", mInfo.PrjID + "");
			ajaxParams.put("TelPhone", mInfo.TelPhone + "");
			ajaxParams.put("Identifier", idcard);
			ajaxParams.put("Name", name);
			ajaxParams.put("Sex", sex);
			ajaxParams.put("loginCode",mInfo.TelPhone+","+mInfo.loginCode);
			ajaxParams.put("phoneSystem", "Android");
			ajaxParams.put("version", MyApp.versionCode);
			new FinalHttp().get(Common.BASE_URL +"infoUp",
					ajaxParams, new AjaxCallBack<Object>() {

						@Override
						public void onSuccess(Object t) {
							super.onSuccess(t);
							String result = t.toString();
							Log.e("water", "result = " + result);
							PublicGetData publicGetData = new Gson().fromJson(result, PublicGetData.class);
							if (publicGetData.error_code.equals("0")) {

								Common.showToast(MyInfoActivity.this, "保存成功", Gravity.CENTER);
								IDCardData idCardData =new IDCardData();
								idCardData.AccMoney =mInfo.AccMoney;
								idCardData.Identifier=idcard;
								idCardData.Name=name;
								idCardData.Sex =sex;
								idCardData.PrjID=mInfo.PrjID;
								AppPreference.getInstance().saveMyNameSexIdCard(idCardData);
								finish();

							} else if (publicGetData.error_code.equals("7")){
								Common.logout2(MyInfoActivity.this, sp,dialogBuilder,publicGetData.message);
							}

						}

						@Override
						public void onFailure(Throwable t, int errorNo, String strMsg) {
							super.onFailure(t, errorNo, strMsg);
						}
					});

		} else {
			Common.showNoNetworkDailog(dialogBuilder, this);
		}

	}

	private void getInfo(final UserInfo mInfo) {

		if (Common.isNetWorkConnected(this)) {

			AjaxParams ajaxParams = new AjaxParams();
			ajaxParams.put("PrjID", mInfo.PrjID + "");
			ajaxParams.put("AccID", mInfo.AccID + "");
			ajaxParams.put("loginCode",mInfo.TelPhone+","+mInfo.loginCode);
			ajaxParams.put("phoneSystem", "Android");
			ajaxParams.put("version", MyApp.versionCode);

			new FinalHttp().get(Common.BASE_URL + "infoSel",
					ajaxParams, new AjaxCallBack<Object>() {

						@Override
						public void onSuccess(Object t) {
							super.onSuccess(t);
							String result = t.toString();
							Log.e("water", "result = " + result);
							PublicGetData publicGetData = new Gson().fromJson(result, PublicGetData.class);
							if (publicGetData.error_code.equals("0")) {

								IDCardData idCardData = new Gson().fromJson(publicGetData.data, IDCardData.class);
								if (idCardData != null) {

									if (!TextUtils.isEmpty(idCardData.Name)) {
										my_name_txt.setText(idCardData.Name);
									}

									if (!TextUtils.isEmpty(idCardData.Sex)) {
										if (idCardData.Sex.equals("男")) {
											man_Button.setChecked(true);
										} else if (idCardData.Sex.equals("女")) {
											woman_Button.setChecked(true);
										}
									}

									if (!TextUtils.isEmpty(idCardData.Identifier)) {
										my_idcard_txt.setText(idCardData.Identifier);
									}

								}

							} else if (publicGetData.error_code.equals("7")) {
								Common.logout(MyInfoActivity.this, sp, dialogBuilder);
							}

						}

						@Override
						public void onFailure(Throwable t, int errorNo, String strMsg) {
							super.onFailure(t, errorNo, strMsg);
						}
					});

		} else {
			Common.showNoNetworkDailog(dialogBuilder, this);
		}

	}

	private void showNoBindAccount() {
		dialogBuilder.withTitle(getString(R.string.tips))
				.withMessage(getString(R.string.no_bind_accout))
				.withEffect(Effectstype.Fadein).isCancelable(false)
				.withButton1Text(getString(R.string.cancel))
				.setButton1Click(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						dialogBuilder.dismiss();
					}
				}).withButton2Text(getString(R.string.bind))
				.setButton2Click(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						dialogBuilder.dismiss();

						Intent intent = new Intent(MyInfoActivity.this, SearchBratheDeviceActivity.class);
						intent.putExtra("capture_type", CaptureActivity.CAPTURE_WATER);
						startActivity(intent);

					}
				}).show();
	}
}
