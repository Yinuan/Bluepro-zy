package com.klcxkj.zqxy.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.AlphaAnimation;
import android.widget.RelativeLayout;

import com.klcxkj.mylibrary.R;
import com.klcxkj.zqxy.common.Common;
import com.klcxkj.zqxy.utils.AppPreference;

public class SplashActivity extends Activity {
	private RelativeLayout rootLayout;
	// private TextView versionText;

	private static final int sleepTime = 2000;
	private SharedPreferences sp;

	private boolean isFirstUse;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		/*if((getIntent().getFlags()&Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT)!=0){

			finish();
			return;
		}*/
		setContentView(R.layout.activity_splash);
		
		sp = getSharedPreferences("adminInfo", Context.MODE_PRIVATE);

		isFirstUse = sp.getBoolean(Common.USER_IS_FIRST, true);

		if (isFirstUse) {

			startActivity(new Intent(SplashActivity.this, SwitchActivity.class));

			finish();

		} else {
			rootLayout = (RelativeLayout) findViewById(R.id.splash_root);
			AlphaAnimation animation = new AlphaAnimation(0.3f, 1.0f);
			animation.setDuration(1500);
			rootLayout.startAnimation(animation);
		}

	}

	private static final int TIME = 1500;
	private static final int GO_HOME = 1000;

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.what == GO_HOME) {
				
				
				if (Common.isLogin(sp)) {
					if (AppPreference.getInstance().getWashingTime() !=null){
						String isusertr = AppPreference.getInstance().getWashingTime();
						int isuser =Integer.parseInt(isusertr);
						if (isuser== 1) {
							startActivity(new Intent(SplashActivity.this, MainAdminActivity.class));
						}else if (isuser==2){
							startActivity(new Intent(SplashActivity.this, MainUserActivity.class));
						}else {
							startActivity(new Intent(SplashActivity.this, LoginActivity.class));
						}
					}else {
						startActivity(new Intent(SplashActivity.this, SwitchActivity.class));

					}


				}else {
					startActivity(new Intent(SplashActivity.this, LoginActivity.class));
				}
				
					finish();
			}

		};
	};

	@Override
	protected void onStart() {
		super.onStart();

		mHandler.sendEmptyMessageDelayed(GO_HOME, TIME);

	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		mHandler.removeMessages(GO_HOME);
		super.onPause();
	}

}
