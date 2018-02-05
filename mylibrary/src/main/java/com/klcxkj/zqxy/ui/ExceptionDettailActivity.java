package com.klcxkj.zqxy.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.klcxkj.zqxy.MyApp;
import com.klcxkj.mylibrary.R;
import com.klcxkj.zqxy.adapter.ExceptionDetailAdapter;
import com.klcxkj.zqxy.common.Common;
import com.klcxkj.zqxy.databean.ExceptionDetailData;
import com.klcxkj.zqxy.databean.UserInfo;
import com.klcxkj.zqxy.response.PublicArrayData;

import net.android.tools.afinal.FinalHttp;
import net.android.tools.afinal.http.AjaxCallBack;
import net.android.tools.afinal.http.AjaxParams;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class ExceptionDettailActivity extends BaseActivity{


	private ListView exception_detail_listview;

	private ArrayList<ExceptionDetailData> exceptionDetailDatas = new ArrayList<ExceptionDetailData>();
	private ExceptionDetailAdapter exceptionDetailAdapter;
	
	private int exception_prjid;

	private SharedPreferences sp;
	private UserInfo userInfo;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_exception_detail);
		
		sp = getSharedPreferences("adminInfo", Context.MODE_PRIVATE);

		userInfo = Common.getUserInfo(sp);
		
		exception_prjid = getIntent().getExtras().getInt("exception_prjid");
		if (exception_prjid == 0) {
			return;
		}
		
		initView();

		getData();
	}

	private void initView() {
		showMenu("故障详情");
		exception_detail_listview = (ListView)findViewById(R.id.exception_detail_listview);

		
		
	}
	
	private void getData() {

		if (Common.isNetWorkConnected(ExceptionDettailActivity.this)) {

			AjaxParams ajaxParams = new AjaxParams();
			ajaxParams.put("PrjID", exception_prjid + "");
			ajaxParams.put("AreaID", 0 + "");
			ajaxParams.put("MarkID", 0 + "");
			ajaxParams.put("CurNum", 1 + "");
			ajaxParams.put("loginCode", userInfo.TelPhone+","+userInfo.loginCode);
			ajaxParams.put("phoneSystem", "Android");
			ajaxParams.put("version", MyApp.versionCode);

			new FinalHttp().get(Common.BASE_URL + "errDeviceList", ajaxParams,
					new AjaxCallBack<Object>() {

						@Override
						public void onSuccess(Object t) {
							super.onSuccess(t);
							String result = t.toString();
							PublicArrayData publicArrayData = new Gson()
									.fromJson(result, PublicArrayData.class);
							if (publicArrayData.error_code.equals("0")) {

								Type listType = new TypeToken<ArrayList<ExceptionDetailData>>() {
								}.getType();
								exceptionDetailDatas = new Gson().fromJson(
										publicArrayData.data, listType);
								if (exceptionDetailDatas != null) {
									exceptionDetailAdapter = new ExceptionDetailAdapter(ExceptionDettailActivity.this, exceptionDetailDatas);
									exception_detail_listview.setAdapter(exceptionDetailAdapter);
								}
								

							}else if (publicArrayData.error_code.equals("7")){
								Common.logout(ExceptionDettailActivity.this, sp,dialogBuilder);
							}

						}

						@Override
						public void onFailure(Throwable t, int errorNo,
								String strMsg) {
							super.onFailure(t, errorNo, strMsg);
						}
					});

		} else {
			Common.showNoNetworkDailog(dialogBuilder, ExceptionDettailActivity.this);
		}

	}
}
