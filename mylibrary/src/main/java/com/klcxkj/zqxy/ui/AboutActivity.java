package com.klcxkj.zqxy.ui;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.klcxkj.mylibrary.R;


/**
 * author : yinjuan
 * time： 2017/10/17 13:54
 * email：yin.juan2016@outlook.com
 * Description:关于我们
 */
public class AboutActivity extends BaseActivity {


	private TextView my_app_name_txt;

	private RelativeLayout copyright_layout, disclaimer_layout;



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);


		initView();
	}

	private void initView() {
		showMenu("关于我们");
		my_app_name_txt = (TextView) findViewById(R.id.my_app_name_txt);
		copyright_layout = (RelativeLayout) findViewById(R.id.copyright_layout);
		disclaimer_layout = (RelativeLayout) findViewById(R.id.disclaimer_layout);


		my_app_name_txt.setText(getAppName());
		

		
		copyright_layout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(AboutActivity.this, H5Activity.class);
				intent.putExtra("h5_tag", H5Activity.BQSM);
				startActivity(intent);
			}
		});
		
		disclaimer_layout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(AboutActivity.this, H5Activity.class);
				intent.putExtra("h5_tag", H5Activity.MZSM);
				startActivity(intent);
			}
		});
	}


	
	/**
	 * 获取应用程序名称
	 */
	private String getAppName() {
		try {
			PackageManager packageManager = getPackageManager();
			PackageInfo packageInfo = packageManager.getPackageInfo(
					getPackageName(), 0);
			int labelRes = packageInfo.applicationInfo.labelRes;

			String version = packageInfo.versionName;

			return getResources().getString(labelRes) + " " + version;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

}
