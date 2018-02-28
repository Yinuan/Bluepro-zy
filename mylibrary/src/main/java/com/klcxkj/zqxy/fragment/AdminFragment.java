package com.klcxkj.zqxy.fragment;

import android.bluetooth.BluetoothAdapter;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.klcxkj.mylibrary.R;
import com.klcxkj.zqxy.common.Common;
import com.klcxkj.zqxy.databean.UserInfo;
import com.klcxkj.zqxy.ui.MainAdminActivity;
import com.klcxkj.zqxy.ui.SearchAdminDeviceActivity;
import com.klcxkj.zqxy.widget.Effectstype;
import com.klcxkj.zqxy.zxing.zxing.activity.CaptureActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class AdminFragment extends BaseFragment implements View.OnClickListener {
	private BluetoothAdapter bluetoothAdapter;

	private LinearLayout bluetooth_disconnect_layout;
	private LinearLayout device_register_layout;
	private TextView admin_prjname_txt;

	private RelativeLayout scan_device_qrcode;

	private SharedPreferences sp;
	private UserInfo mUserInfo;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();



		IntentFilter statusFilter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
		getActivity().registerReceiver(mStatusReceive, statusFilter);
		EventBus.getDefault().register(this);
	}

	private View rootView;// 缓存Fragment view

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (rootView == null) {
			rootView = inflater.inflate(R.layout.fragment_admin, container, false);
			initView(rootView);
			updateView();
		}
		ViewGroup parent = (ViewGroup) rootView.getParent();
		if (parent != null) {
			parent.removeView(rootView);
		}
		return rootView;
	}
	private TextView title;
	private void initView(View view) {
		//返回
		LinearLayout backLayout = (LinearLayout)rootView. findViewById(R.id.top_btn_back);
		backLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				getActivity().finish();
			}
		});
		 title = (TextView)rootView. findViewById(R.id.menu_title);


		//
		admin_prjname_txt = (TextView) view.findViewById(R.id.admin_prjname_txt);

		bluetooth_disconnect_layout = (LinearLayout) view.findViewById(R.id.bluetooth_disconnect_layout);
		device_register_layout = (LinearLayout) view.findViewById(R.id.device_register_layout);

		scan_device_qrcode = (RelativeLayout) view.findViewById(R.id.scan_device_qrcode);



	}

	@Subscribe
	public void onEvent(String msg){
		if (msg.equals("login_success")){
			if (MainAdminActivity.pName !=null){
				title.setText(MainAdminActivity.pName);
			}
			sp = getActivity().getSharedPreferences("adminInfo", Context.MODE_PRIVATE);
			mUserInfo = Common.getUserInfo(sp);

			if (mUserInfo != null && !TextUtils.isEmpty(mUserInfo.PrjName)) {
				admin_prjname_txt.setText(mUserInfo.PrjName);
			}
			scan_device_qrcode.setOnClickListener(this);
			device_register_layout.setOnClickListener(this);
			bluetooth_disconnect_layout.setOnClickListener(this);

		}
	}


	private void updateView() {
		if (bluetooth_disconnect_layout != null) {

			if (!isBluetoothEnable()) {
				bluetooth_disconnect_layout.setVisibility(View.VISIBLE);
				device_register_layout.setVisibility(View.GONE);
			} else {
				bluetooth_disconnect_layout.setVisibility(View.GONE);
				device_register_layout.setVisibility(View.VISIBLE);

			}

		}
	};

	private void showNoBluetoothDailog() {
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

	private boolean isBluetoothEnable() {
		return bluetoothAdapter.isEnabled();
	}

	private void openSetting() {
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

	private BroadcastReceiver mStatusReceive = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {

			if (intent.getAction().equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
				int blueState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
				switch (blueState) {
				case BluetoothAdapter.STATE_TURNING_ON:
					break;
				case BluetoothAdapter.STATE_ON:
					updateView();
					break;
				case BluetoothAdapter.STATE_TURNING_OFF:
					break;
				case BluetoothAdapter.STATE_OFF:
					updateView();
					break;
				}
			}

		}
	};

	@Override
	public void onDestroy() {
		super.onDestroy();
		getActivity().unregisterReceiver(mStatusReceive);
		EventBus.getDefault().unregister(this);
	}

	@Override
	public void onClick(View view) {
		int i = view.getId();
		if (i == R.id.scan_device_qrcode) {
			if (!isBluetoothEnable()) {
				showNoBluetoothDailog();
			} else {
				Intent intent = new Intent();
				intent.setClass(getActivity(), CaptureActivity.class);
				intent.putExtra("capture_type", CaptureActivity.CAPTURE_ADMIN);
				startActivity(intent);
			}

		} else if (i == R.id.bluetooth_disconnect_layout) {
			showNoBluetoothDailog();

		} else if (i == R.id.device_register_layout) {
			Intent intent = new Intent();
			intent.setClass(getActivity(), SearchAdminDeviceActivity.class);
			startActivity(intent);

		}
	}
}
