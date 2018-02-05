package com.klcxkj.zqxy.ui;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.klcxkj.mylibrary.R;
import com.klcxkj.zqxy.databean.UserInfo;
import com.klcxkj.zqxy.widget.CustomProgressDialog;
import com.klcxkj.zqxy.widget.Effectstype;
import com.klcxkj.zqxy.widget.LoadingDialogProgress;
import com.klcxkj.zqxy.widget.NiftyDialogBuilder;

public class BaseActivity extends Activity {

	protected Context mContext;
	private CustomProgressDialog progressDialog;

	protected NiftyDialogBuilder dialogBuilder;

	protected LoadingDialogProgress loadingDialogProgress;

	public static final String NET_CHANGE = "net_change";
	// 标记当前网络状态，0为无可用网络状态，1表示有。
	public static final String NET_TYPE = "net_type";
	protected UserInfo mUserInfo;
	protected SharedPreferences sp;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		dialogBuilder = NiftyDialogBuilder.getInstance(this);
		IntentFilter filter = new IntentFilter();
		filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
		registerReceiver(mNetworkChangeListener, filter);

	}

	protected  TextView rightTxt;
	protected LinearLayout backLayout;
	protected void showMenu(String str){
		 backLayout = (LinearLayout) findViewById(R.id.top_btn_back);
		backLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				finish();
			}
		});
		TextView title = (TextView) findViewById(R.id.menu_title);
		title.setText(str);
		rightTxt = (TextView) findViewById(R.id.logout_btn);


	}
	protected void toast(String message){
		Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
	}
	protected void startProgressDialog(Context context, String content) {
		if (progressDialog == null) {
			progressDialog = CustomProgressDialog.createDialog(context);
			progressDialog.setMessage(content);
			progressDialog.setCanceledOnTouchOutside(false);
			progressDialog.show();
		} else if (!progressDialog.isShowing()) {
			progressDialog.show();
		}

	}

	protected void showNoBluetoothDailog() {
		dialogBuilder.withTitle(getString(R.string.tips))
				.withMessage(getString(R.string.tips_bluetooth_disconnect))
				.withEffect(Effectstype.Fadein).isCancelable(false)
				.withButton1Text(getString(R.string.settings))
				.setButton1Click(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						openSetting();
						dialogBuilder.dismiss();
					}
				}).withButton2Text(getString(R.string.ok))
				.setButton2Click(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						dialogBuilder.dismiss();
					}
				}).show();
	}
	protected void openSetting() {
		Intent intent = new Intent();
		intent.setAction(Settings.ACTION_BLUETOOTH_SETTINGS);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		try {
			startActivity(intent);
		} catch (ActivityNotFoundException ex) {
			ex.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void startProgressDialog(Context context) {
		startProgressDialog(context, getString(R.string.loading));
	}

	protected void stopProgressDialog() {
		if (progressDialog != null && progressDialog.isShowing()) {
			progressDialog.dismiss();
			progressDialog = null;
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		try {
			if (dialogBuilder != null && dialogBuilder.isShowing()) {
				dialogBuilder.dismiss();
			}

			unregisterReceiver(mNetworkChangeListener);
		} catch (Exception e) {
			// TODO: handle exception
		}

		super.onDestroy();
	}

	private BroadcastReceiver mNetworkChangeListener = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mNetworkInfo = mConnectivityManager
					.getActiveNetworkInfo();
			if (mNetworkInfo != null) {
				if (mNetworkInfo.isAvailable()) {
					
				}else {
					showNoNetwork();
				}
			}else {
				showNoNetwork();
			}
		}
	};

	
	private void showNoNetwork() {
		if (dialogBuilder != null && dialogBuilder.isShowing()) {
			dialogBuilder.dismiss();
		}
		dialogBuilder.withTitle(getString(R.string.tips))
				.withMessage(getString(R.string.no_network))
				.withEffect(Effectstype.Fadein).isCancelable(false)
				.withButton2Text(getString(R.string.i_known))
				.setButton2Click(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						dialogBuilder.dismiss();
					}
				}).show();
	}

	/**
	 * 右边蓝色，左边白色
	 * @param str
	 */
	protected void showDialog_style1(String str){
		dialogBuilder.withTitle(getString(R.string.tips))
				.withMessage(str)
				.withEffect(Effectstype.Fadein).isCancelable(false)
				.withButton1Text(getString(R.string.cancel))
				.setButton1Click(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						dialogBuilder.dismiss();
					}
				}).withButton2Text(getString(R.string.ok))
				.setButton2Click(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						dialogBuilder.dismiss();
					}
				}).show();
	}
	/**
	 * 右边白色，左边蓝色
	 * @param str
	 */
	protected void showDialog_style2(String str){
		dialogBuilder.withTitle(getString(R.string.tips))
				.withMessage(str)
				.withEffect(Effectstype.Fadein).isCancelable(false)
				.withButton2Text(getString(R.string.ok))
				.setButton2Click(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						dialogBuilder.dismiss();
					}
				})
				.withButton1Text(getString(R.string.cancel))
				.setButton1Click(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						dialogBuilder.dismiss();
					}
				}).show();
	}
}
