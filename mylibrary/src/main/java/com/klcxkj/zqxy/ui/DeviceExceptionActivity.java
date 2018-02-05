package com.klcxkj.zqxy.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.klcxkj.zqxy.MyApp;
import com.klcxkj.mylibrary.R;
import com.klcxkj.zqxy.adapter.ExceptionAdapter;
import com.klcxkj.zqxy.common.Common;
import com.klcxkj.zqxy.databean.ExceptionData;
import com.klcxkj.zqxy.databean.UserInfo;
import com.klcxkj.zqxy.response.PublicArrayData;

import net.android.tools.afinal.FinalHttp;
import net.android.tools.afinal.http.AjaxCallBack;
import net.android.tools.afinal.http.AjaxParams;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class DeviceExceptionActivity extends BaseActivity {


	private ListView exception_listview;

	private ExceptionAdapter exceptionAdapter;
	
	private ArrayList<ExceptionData> exceptionDatas;
	
	private UserInfo mUserInfo;
	private SharedPreferences sp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_device_exception);
		sp = getSharedPreferences("adminInfo", Context.MODE_PRIVATE);
		mUserInfo = Common.getUserInfo(sp);
		initView();

		updateException(mUserInfo);
		
	}

	private void initView() {
		showMenu("设备异常");
		exception_listview = (ListView)findViewById(R.id.exception_listview);

		
		exception_listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				ExceptionData data  = (ExceptionData) exceptionAdapter.getItem(arg2);
				Intent intent = new Intent();
				intent.setClass(DeviceExceptionActivity.this, ExceptionDettailActivity.class);
				intent.putExtra("exception_prjid", data.PrjID);
				startActivity(intent);
				
			}
		});
		
	}
	
	
	
	private void updateException(UserInfo mInfo) {

		if (Common.isNetWorkConnected(DeviceExceptionActivity.this)) {

			if (TextUtils.isEmpty(mInfo.TelPhone + "")) {
				Common.showToast(DeviceExceptionActivity.this, R.string.phonenum_null,
						Gravity.CENTER);
				return;
			}
			if (!Common.isPhoneNum(mInfo.TelPhone + "")) {
				Common.showToast(DeviceExceptionActivity.this,
						R.string.phonenum_not_irregular, Gravity.CENTER);
				return;
			}

			AjaxParams ajaxParams = new AjaxParams();
			ajaxParams.put("TelPhone", mInfo.TelPhone + "");
			ajaxParams.put("loginCode", mInfo.TelPhone+","+mInfo.loginCode);
			ajaxParams.put("phoneSystem", "Android");
			ajaxParams.put("version", MyApp.versionCode);

			new FinalHttp().get(Common.BASE_URL + "errPrjlist", ajaxParams,
					new AjaxCallBack<Object>() {

						@Override
						public void onSuccess(Object t) {
							super.onSuccess(t);
							String result = t.toString();
							PublicArrayData publicArrayData = new Gson()
									.fromJson(result, PublicArrayData.class);
							if (publicArrayData.error_code.equals("0")) {

								Type listType = new TypeToken<ArrayList<ExceptionData>>() {
								}.getType();
								exceptionDatas = new Gson().fromJson(
										publicArrayData.data, listType);
								
								exceptionAdapter = new ExceptionAdapter(DeviceExceptionActivity.this, exceptionDatas);
								exception_listview.setAdapter(exceptionAdapter);

							}  else if (publicArrayData.error_code.equals("7")){
								Common.logout(DeviceExceptionActivity.this, sp,dialogBuilder);
							}

						}

						@Override
						public void onFailure(Throwable t, int errorNo,
								String strMsg) {
							super.onFailure(t, errorNo, strMsg);
						}
					});

		} else {
			Common.showNoNetworkDailog(dialogBuilder, DeviceExceptionActivity.this);
		}

	}
	
}
