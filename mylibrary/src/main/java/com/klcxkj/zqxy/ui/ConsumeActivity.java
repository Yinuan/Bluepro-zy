package com.klcxkj.zqxy.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import com.google.gson.Gson;
import com.klcxkj.mylibrary.R;
import com.klcxkj.zqxy.MyApp;
import com.klcxkj.zqxy.common.Common;
import com.klcxkj.zqxy.databean.UserInfo;
import com.klcxkj.zqxy.response.PublicGetData;
import com.klcxkj.zqxy.response.PublicPostConsumeData;

import net.android.tools.afinal.FinalHttp;
import net.android.tools.afinal.http.AjaxCallBack;
import net.android.tools.afinal.http.AjaxParams;

public class ConsumeActivity extends BaseActivity {


	private TextView xizao_finish;
	private TextView finish_time_txt, withhold_amount_txt, consume_amount_txt,
			return_amount_txt;

	private PublicPostConsumeData consumeData;



	private int type;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_consume);
		sp = getSharedPreferences("adminInfo", Context.MODE_PRIVATE);
		mUserInfo = Common.getUserInfo(sp);
		showMenu("消费详情");
		consumeData = (PublicPostConsumeData) getIntent().getExtras().getSerializable("consumedata");
		type = getIntent().getExtras().getInt("consume_type");
		initView();
		updateUserInfo(mUserInfo);
	}

	private void initView() {

		xizao_finish = (TextView) findViewById(R.id.xizao_finish);
		switch (type){
			case 4:
				xizao_finish.setText("洗澡完成");
				break;
			case 5:
				xizao_finish.setText("用水完成");
				break;
			case 7:
				xizao_finish.setText("吹风完成");
				break;
			case 8:
				xizao_finish.setText("充电完成");
				break;
			case 9:
				xizao_finish.setText("吹风完成");
				break;
		}




		finish_time_txt = (TextView) findViewById(R.id.finish_time_txt);
		withhold_amount_txt = (TextView) findViewById(R.id.withhold_amount_txt);
		consume_amount_txt = (TextView) findViewById(R.id.consume_amount_txt);
		return_amount_txt = (TextView) findViewById(R.id.return_amount_txt);

		finish_time_txt.setText(consumeData.FishTime);
		withhold_amount_txt.setText(Common.getShowMonty(consumeData.PerMoney,
				getString(R.string.yuan)));
		consume_amount_txt.setText(Common.getShowMonty(consumeData.UpMoney,
				getString(R.string.yuan)));
		return_amount_txt.setText(Common.getShowMonty(consumeData.UpLeadMoney,
				getString(R.string.yuan)));
	}

	/**
	 * 更新用户信息
	 * @param mInfo
	 */
	private void updateUserInfo(final UserInfo mInfo) {

		if (Common.isNetWorkConnected(ConsumeActivity.this)) {

			/*if (TextUtils.isEmpty(mInfo.TelPhone + "")) {
				Common.showToast(ConsumeActivity.this, R.string.phonenum_null, Gravity.CENTER);
				return;
			}
			if (!Common.isPhoneNum(mInfo.TelPhone + "")) {
				Common.showToast(ConsumeActivity.this, R.string.phonenum_not_irregular, Gravity.CENTER);
				return;
			}
*/
			AjaxParams ajaxParams = new AjaxParams();
			ajaxParams.put("TelPhone", mInfo.TelPhone + "");
			ajaxParams.put("PrjID", mInfo.PrjID + "");
			ajaxParams.put("WXID", "0");
			ajaxParams.put("loginCode", mInfo.TelPhone + "," + mInfo.loginCode);
			ajaxParams.put("isOpUser", "0");
			ajaxParams.put("phoneSystem", "Android");
			ajaxParams.put("version", MyApp.versionCode);

			new FinalHttp().get(Common.BASE_URL + "accountInfo", ajaxParams,
					new AjaxCallBack<Object>() {

						@Override
						public void onSuccess(Object t) {
							super.onSuccess(t);
							String result = t.toString();
						//	Log.d("ConsumeActivity", "更新个人信息：=" + result);
							PublicGetData publicGetData = new Gson().fromJson(result, PublicGetData.class);
							if (publicGetData.error_code.equals("0")) {

								UserInfo info = new Gson().fromJson(publicGetData.data, UserInfo.class);
								info.loginCode = mInfo.loginCode;
								if (info != null) {
									SharedPreferences.Editor editor = sp.edit();
									editor.putString(Common.USER_PHONE_NUM, info.TelPhone + "");
									editor.putString(Common.USER_INFO, new Gson().toJson(info));
									editor.putInt(Common.ACCOUNT_IS_USER, info.GroupID);
									editor.commit();


								}

							}

						}

						@Override
						public void onFailure(Throwable t, int errorNo, String strMsg) {
							super.onFailure(t, errorNo, strMsg);
						//	Log.d("ConsumeActivity", strMsg);
						}
					});

		}
	}
}
