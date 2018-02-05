package com.klcxkj.zqxy.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jaeger.library.StatusBarUtil;
import com.klcxkj.zqxy.MyApp;
import com.klcxkj.mylibrary.R;
import com.klcxkj.zqxy.adapter.BillAdapter;
import com.klcxkj.zqxy.common.Common;
import com.klcxkj.zqxy.databean.BillInfo;
import com.klcxkj.zqxy.databean.UserInfo;
import com.klcxkj.zqxy.response.PublicArrayData;
import com.klcxkj.zqxy.utils.GlobalTools;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import net.android.tools.afinal.FinalHttp;
import net.android.tools.afinal.http.AjaxCallBack;
import net.android.tools.afinal.http.AjaxParams;

import java.lang.reflect.Type;
import java.util.ArrayList;


public class MyBillActivity extends BaseActivity {

	private TextView back_img;

	private BillAdapter billAdapter;
	private ArrayList<BillInfo> consumebillInfos = new ArrayList<BillInfo>();

	private UserInfo mUserInfo;
	private SharedPreferences sp;

	private ListView listView;
	private SmartRefreshLayout smartRefreshLayout;
	private int count=1;//分页


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mybill);
		StatusBarUtil.setColor(this,getResources().getColor(R.color.base_color),0);
		sp = getSharedPreferences("adminInfo", Context.MODE_PRIVATE);
		mUserInfo = Common.getUserInfo(sp);
		initView();
		getMyBill(mUserInfo);
	}

	private void initView() {
		back_img = (TextView) findViewById(R.id.back_img);
		listView = (ListView) findViewById(R.id.message_list);
		smartRefreshLayout = (SmartRefreshLayout) findViewById(R.id.message_refreshLayout);
		back_img.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				finish();
			}
		});

		billAdapter = new BillAdapter(MyBillActivity.this, consumebillInfos);
		listView.setAdapter(billAdapter);
		smartRefreshLayout.setEnableLoadmore(true);

		smartRefreshLayout.setOnLoadmoreListener(new OnLoadmoreListener() {
			@Override
			public void onLoadmore(RefreshLayout refreshlayout) {
				count++;
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						getMyBill(mUserInfo);
					}
				},1600);
				refreshlayout.finishLoadmore(2000);
			}
		});
		smartRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh(RefreshLayout refreshlayout) {
				count=1;
				consumebillInfos.clear();
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						getMyBill(mUserInfo);
					}
				},1600);
				smartRefreshLayout.setEnableLoadmore(true);
				refreshlayout.finishRefresh(2000);
			}
		});
		

	}




	private void getMyBill(UserInfo mInfo) {


		if (Common.isNetWorkConnected(MyBillActivity.this)) {

			if (TextUtils.isEmpty(mInfo.TelPhone + "")) {
				Common.showToast(MyBillActivity.this, R.string.phonenum_null, Gravity.CENTER);

				return;
			}
			if (!Common.isPhoneNum(mInfo.TelPhone + "")) {
				Common.showToast(MyBillActivity.this, R.string.phonenum_not_irregular, Gravity.CENTER);

				return;
			}

			loadingDialogProgress = GlobalTools.getInstance().showDailog(this,"加载中.");

			AjaxParams ajaxParams = new AjaxParams();
			ajaxParams.put("TelPhone", mInfo.TelPhone + "");
			ajaxParams.put("PrjID", mInfo.PrjID + "");
			ajaxParams.put("GroupID", "2");
			ajaxParams.put("BeginDT", "2017-01-01 12:00");
			ajaxParams.put("EndDT", Common.timeconvertHHmm(System.currentTimeMillis()));
			ajaxParams.put("CurNum", ""+ count);
			ajaxParams.put("RecTypeID", "" + 2);
			ajaxParams.put("loginCode", mInfo.TelPhone+","+mInfo.loginCode);
			ajaxParams.put("phoneSystem", "Android");
			ajaxParams.put("version", MyApp.versionCode);
			new FinalHttp().get(Common.BASE_URL + "personBillList", ajaxParams,
					new AjaxCallBack<Object>() {

						@Override
						public void onSuccess(Object t) {
							super.onSuccess(t);
							String result = t.toString();
							if (loadingDialogProgress !=null){
								loadingDialogProgress.dismiss();
							}
							PublicArrayData publicArrayData = new Gson().fromJson(result, PublicArrayData.class);
							if (publicArrayData.error_code.equals("0")) {

								Type listType = new TypeToken<ArrayList<BillInfo>>() {}.getType();
								ArrayList<BillInfo> billInfos =  new Gson().fromJson(publicArrayData.data, listType);
								consumebillInfos.addAll(billInfos);
								billAdapter.changeData(consumebillInfos);

							}else if (publicArrayData.error_code.equals("7")){
								Common.logout(MyBillActivity.this, sp,dialogBuilder);
							}

						}

						@Override
						public void onFailure(Throwable t, int errorNo, String strMsg) {
							super.onFailure(t, errorNo, strMsg);

							if (loadingDialogProgress !=null){
								loadingDialogProgress.dismiss();
							}
						}
					});

		} else {

			Common.showNoNetworkDailog(dialogBuilder, MyBillActivity.this);
		}

	}

}
