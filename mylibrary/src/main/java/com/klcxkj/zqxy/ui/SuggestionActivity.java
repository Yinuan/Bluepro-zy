package com.klcxkj.zqxy.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.klcxkj.zqxy.MyApp;
import com.klcxkj.mylibrary.R;
import com.klcxkj.zqxy.common.Common;
import com.klcxkj.zqxy.databean.UserInfo;
import com.klcxkj.zqxy.response.PublicGetData;

import net.android.tools.afinal.FinalHttp;
import net.android.tools.afinal.http.AjaxCallBack;
import net.android.tools.afinal.http.AjaxParams;

public class SuggestionActivity extends BaseActivity {

	private TextView back_img;
	private EditText feedback_edit;
	private Button ok_btn;

	private UserInfo mUserInfo;
	private SharedPreferences sp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_suggestion);

		sp = getSharedPreferences("adminInfo", Context.MODE_PRIVATE);
		mUserInfo = Common.getUserInfo(sp);

		if (mUserInfo == null || mUserInfo.PrjID == 0) {
			return;
		}

		initView();
	}

	private void initView() {
		showMenu("意见反馈");
		back_img = (TextView) findViewById(R.id.back_img);
		feedback_edit = (EditText) findViewById(R.id.feedback_edit);
		ok_btn = (Button) findViewById(R.id.ok_btn);

		back_img.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

		ok_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String feedback = feedback_edit.getText().toString();
				if (TextUtils.isEmpty(feedback)) {
					Common.showToast(SuggestionActivity.this,
							R.string.suggestion_hint, Gravity.BOTTOM);
					return;
				}

				postFeedback(mUserInfo, feedback);
			}
		});
	}

	private void postFeedback(UserInfo mInfo, String feedback) {

		if (Common.isNetWorkConnected(SuggestionActivity.this)) {
			startProgressDialog(SuggestionActivity.this);
			AjaxParams ajaxParams = new AjaxParams();
			ajaxParams.put("PrjID", mInfo.PrjID + "");
			ajaxParams.put("AccID", mInfo.AccID + "");
			ajaxParams.put("RepTitle", mInfo.TelPhone + "");
			ajaxParams.put("RepContent", feedback);
			ajaxParams.put("loginCode", mUserInfo.TelPhone + "," + mUserInfo.loginCode);
			ajaxParams.put("phoneSystem", "Android");
			ajaxParams.put("version", MyApp.versionCode);
			new FinalHttp().get(Common.BASE_URL + "/tsAddInfo", ajaxParams,
					new AjaxCallBack<Object>() {

						@Override
						public void onSuccess(Object t) {
							super.onSuccess(t);
							String result = t.toString();
							stopProgressDialog();
							PublicGetData publicGetData = new Gson().fromJson(
									result, PublicGetData.class);
							if (publicGetData.error_code.equals("0")) {
								Common.showToast(SuggestionActivity.this,
										R.string.suggestion_seccess,
										Gravity.CENTER);
								new Handler().postDelayed(new Runnable() {

									@Override
									public void run() {
										finish();
									}
								}, 2000);
							} else if (publicGetData.error_code.equals("7")) {
								Common.logout(SuggestionActivity.this, sp,dialogBuilder);
							}

						}

						@Override
						public void onFailure(Throwable t, int errorNo,
								String strMsg) {
							super.onFailure(t, errorNo, strMsg);
							stopProgressDialog();
						}
					});

		} else {
			Common.showNoNetworkDailog(dialogBuilder, SuggestionActivity.this);
		}

	}
}
