package com.klcxkj.zqxy.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.klcxkj.mylibrary.R;
import com.klcxkj.zqxy.adapter.FinancialAdapter;
import com.klcxkj.zqxy.databean.AdminProductInfo;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class FinancialManagementActivity extends BaseActivity {


	private ListView finanic_listview;

	private FinancialAdapter financialAdapter;

	private ArrayList<AdminProductInfo> adminProductInfos;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_financial_management);

		String adminproduct = getIntent().getExtras().getString("adminproduct_infos");

		if (TextUtils.isEmpty(adminproduct)) {
			return;
		}
		Type listType = new TypeToken<ArrayList<AdminProductInfo>>() {}.getType();
		adminProductInfos = new Gson().fromJson(adminproduct, listType);

		if (adminProductInfos == null) {
			return;
		}

		initView();

		getData();
	}

	private void initView() {
		showMenu("财务管理");
		finanic_listview = (ListView) findViewById(R.id.finanic_listview);


	}

	private void getData() {

		financialAdapter = new FinancialAdapter(
				FinancialManagementActivity.this, adminProductInfos);
		finanic_listview.setAdapter(financialAdapter);
	}
}
