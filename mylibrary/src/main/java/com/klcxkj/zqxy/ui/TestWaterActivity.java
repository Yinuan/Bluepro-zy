package com.klcxkj.zqxy.ui;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.ColorStateList;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.jooronjar.BluetoothService;
import com.example.jooronjar.DownRateInfo;
import com.example.jooronjar.utils.CMDUtils;
import com.example.jooronjar.utils.DigitalTrans;
import com.google.gson.Gson;
import com.klcxkj.mylibrary.R;
import com.klcxkj.zqxy.common.Common;
import com.klcxkj.zqxy.databean.DeviceInfo;
import com.klcxkj.zqxy.databean.PostConsumeData;
import com.klcxkj.zqxy.databean.UserInfo;
import com.klcxkj.zqxy.response.PublicGetData;
import com.klcxkj.zqxy.response.PublicPostConsumeData;
import com.klcxkj.zqxy.utils.Toolsutils;
import com.klcxkj.zqxy.widget.Effectstype;

import net.android.tools.afinal.FinalHttp;
import net.android.tools.afinal.http.AjaxCallBack;
import net.android.tools.afinal.http.AjaxParams;

import java.io.IOException;
import java.text.DecimalFormat;

public class TestWaterActivity extends BaseActivity {
	private static final int DELAY_TIME = 60000;
	private BluetoothAdapter bluetoothAdapter;

	private TextView project_name_txt, device_connect_state_txt;
	private ImageView device_state_img;
	private ProgressBar progressbar;

	private TextView device_txt, device_name_txt, yukou_txt, yue_txt;

	private SharedPreferences sp;

	private BluetoothService mbtService = null;

	public static final int OPERATION_NOTHING = 0;
	public static final int OPERATION_OPEN = 1;
	public static final int OPERATION_CLOSE = 2;
	public static final int OPERATION_AUTO_LINK = 3;

	private int operation = OPERATION_NOTHING;
	private boolean caiji_updateview;
	private boolean try_connect;

	private DeviceInfo mDeviceInfo;
	private UserInfo userInfo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test_water);

		sp = getSharedPreferences("adminInfo", Context.MODE_PRIVATE);
		bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

		mbtService = BluetoothService.sharedManager();
		mbtService.setHandlerContext(mHandler);

		if (!isBluetoothEnable()) {
			showNoBluetoothDailog();
		}

		mDeviceInfo = (DeviceInfo) getIntent().getExtras().getSerializable("deviceinfo");
		userInfo = Common.getUserInfo(sp);
		if (mDeviceInfo == null) {
			return;
		}

		if (!Common.isBindAccount(sp)) {
			bindSchool(mDeviceInfo);
		} else {
			initView();
			updateView();
			IntentFilter statusFilter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
			statusFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);// 状态改变
			registerReceiver(mStatusReceive, statusFilter);

			connectDevice();
		}

	}

	private void initView() {

		showMenu("测试用水");

		project_name_txt = (TextView) findViewById(R.id.project_name_txt);
		device_state_img = (ImageView) findViewById(R.id.device_state_img);
		device_connect_state_txt = (TextView) findViewById(R.id.device_connect_state_txt);
		device_state_img.setOnClickListener(onClickListener);
		device_connect_state_txt.setOnClickListener(onClickListener);
		progressbar = (ProgressBar) findViewById(R.id.progressbar);
		progressbar.setVisibility(View.GONE);

		device_name_txt = (TextView) findViewById(R.id.device_name_txt);
		device_txt = (TextView) findViewById(R.id.device_txt);
		yukou_txt = (TextView) findViewById(R.id.yukou_txt);
		yue_txt = (TextView) findViewById(R.id.yue_txt);
		device_name_txt.setOnClickListener(onClickListener);

	}

	private void updateView() {
		try {
			hideProgressbar();

			if (userInfo != null) {
				if (!TextUtils.isEmpty(userInfo.PrjName)) {
					project_name_txt.setText(userInfo.PrjName);
				}
				yukou_txt.setText(Common.getShowMonty(0,
						getString(R.string.yuan1)));
				yue_txt.setText(Common.getShowMonty(userInfo.AccMoney
						+ userInfo.GivenAccMoney, getString(R.string.yuan1)));
			}
			device_state_img.setSelected(false);

			device_txt.setText(mDeviceInfo.DevTypeName);

			if (!isBluetoothEnable()) {
				// 表示蓝牙没有打开
				showNoBluetoothDailog();

				ColorStateList csl = (ColorStateList) getResources().getColorStateList(R.color.red);
				device_name_txt.setTextColor(csl);
				if (mDeviceInfo.DevTypeID == 1) {
					device_name_txt.setText(R.string.no_content_bluetooth);
				} else if (mDeviceInfo.DevTypeID == 2) {
					device_name_txt.setText(R.string.no_content_bluetooth2);
				}

				device_connect_state_txt.setText(R.string.bluetooth_state_disconnect);
				device_state_img.setImageResource(R.drawable.bluetooth_disconnect);

			} else {
				ColorStateList csl = (ColorStateList) getResources().getColorStateList(R.color.grgray);
				device_name_txt.setTextColor(csl);

				if (!TextUtils.isEmpty(mDeviceInfo.DevName)) {
					device_name_txt.setText(mDeviceInfo.DevName);
				} else {
					device_name_txt.setText("");
				}

				if (mDeviceInfo.DevTypeID == 1) {
					// 表明已经绑定账号并且绑定了设备
					device_connect_state_txt.setText(R.string.start_brathe);
					device_state_img.setImageResource(R.drawable.start_brathe_btn_selecter);
				} else if (mDeviceInfo.DevTypeID == 2) {

					device_connect_state_txt.setText(R.string.start_water);
					device_state_img.setImageResource(R.drawable.start_water_btn_selecter);

				}

				if (mbtService.getState() != BluetoothService.STATE_CONNECTED) {

					ColorStateList csl2 = (ColorStateList) getResources().getColorStateList(R.color.red);
					device_name_txt.setTextColor(csl2);

					device_state_img.setSelected(true);
					device_connect_state_txt.setText(R.string.retry);
					if (mDeviceInfo.DevTypeID == 1) {
						device_name_txt.setText(R.string.no_content_bluetooth);
					} else if (mDeviceInfo.DevTypeID == 2) {
						device_name_txt.setText(R.string.no_content_bluetooth2);
					}
				} else if (mbtService.getState() == BluetoothService.STATE_CONNECTED) {
					device_state_img.setSelected(false);
				}

			}
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	private void updateUseingView() {
		if (mDeviceInfo.DevTypeID == 1) {

			device_connect_state_txt.setText(R.string.stop_brathe);
			device_state_img.setImageResource(R.drawable.animation_brathe);

		} else if (mDeviceInfo.DevTypeID == 2) {

			device_connect_state_txt.setText(R.string.stop_water);
			device_state_img.setImageResource(R.drawable.animation_water);

		}
		try {
			AnimationDrawable animationDrawable = (AnimationDrawable) device_state_img.getDrawable();
			animationDrawable.start();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	private void updateFinishView() {
		yukou_txt.setText(Common.getShowMonty(0, getString(R.string.yuan1)));
		if (mDeviceInfo.DevTypeID == 1) {
			device_connect_state_txt.setText(R.string.start_brathe);
			device_state_img.setImageResource(R.drawable.start_brathe_btn_selecter);

		} else if (mDeviceInfo.DevTypeID == 2) {
			device_connect_state_txt.setText(R.string.start_water);
			device_state_img.setImageResource(R.drawable.start_water_btn_selecter);
		}

	}

	private OnClickListener onClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			int i = v.getId();
			if (i == R.id.device_connect_state_txt || i == R.id.device_state_img) {
				String string = device_connect_state_txt.getText().toString();
				if (!TextUtils.isEmpty(string)) {
					if (string.equals(getString(R.string.start_brathe))) {
						startUse();
						mHandler.removeMessages(BluetoothService.MESSAGE_STATE_NOTHING);
						Message message = new Message();
						message.what = BluetoothService.MESSAGE_STATE_NOTHING;
						mHandler.sendMessageDelayed(message, DELAY_TIME);
					} else if (string.equals(getString(R.string.stop_brathe))) {
						showStopDailog();
					} else if (string.equals(getString(R.string.bluetooth_state_disconnect))) {
						showNoBluetoothDailog();
					} else if (string.equals(getString(R.string.start_water))) {
						startUse();

						mHandler.removeMessages(BluetoothService.MESSAGE_STATE_NOTHING);
						Message message = new Message();
						message.what = BluetoothService.MESSAGE_STATE_NOTHING;
						mHandler.sendMessageDelayed(message, DELAY_TIME);
					} else if (string.equals(getString(R.string.stop_water))) {
						showStopDailog();
					} else if (string.equals(getString(R.string.retry))) {
						try_connect = true;
						connectDevice();
						mHandler.removeMessages(BluetoothService.MESSAGE_NO_XIAFACALLBACK);
						Message message = new Message();
						message.what = BluetoothService.MESSAGE_STATE_NOTHING;
						mHandler.sendMessageDelayed(message, DELAY_TIME);
					}
				}

			} else if (i == R.id.device_name_txt) {
				String contentString = device_name_txt.getText().toString();
				if (contentString.equals(getString(R.string.no_content_bluetooth))) {
					// 未连接到设备
					Intent intent = new Intent();
					intent.setClass(TestWaterActivity.this, H5Activity.class);
					intent.putExtra("h5_tag", H5Activity.LBSSB);
					startActivity(intent);

				} else if (contentString.equals(getString(R.string.no_content_bluetooth2))) {
					// 未连接到设备
					Intent intent = new Intent();
					intent.setClass(TestWaterActivity.this, H5Activity.class);
					intent.putExtra("h5_tag", H5Activity.LBSSB);
					startActivity(intent);

				}

			} else {
			}

		}
	};



	private boolean isBluetoothEnable() {
		return bluetoothAdapter.isEnabled();
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
					if (dialogBuilder != null && dialogBuilder.isShowing()) {
						dialogBuilder.dismiss();
					}
					updateView();
					break;
				case BluetoothAdapter.STATE_TURNING_OFF:
					break;
				case BluetoothAdapter.STATE_OFF:
					showNoBluetoothDailog();
					try {
						if (mbtService != null) {
							if (mbtService.getState() == BluetoothService.STATE_CONNECTED) {
								mbtService.stop();
							}
						}
					} catch (Exception e) {
						// TODO: handle exception
					}
					updateView();
					break;
				}
			} else if (intent.getAction().equals(
					BluetoothDevice.ACTION_BOND_STATE_CHANGED)) {
				BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				switch (device.getBondState()) {
				case BluetoothDevice.BOND_BONDING:// 正在配对
					Log.e("water", "正在配对......");
					showProgressbar();
					break;
				case BluetoothDevice.BOND_BONDED:// 配对结束
					Log.e("water", "完成配对");

					mbtService.connect(device);

					break;
				case BluetoothDevice.BOND_NONE:// 取消配对/未配对
					Log.e("water", "取消配对");
					if (try_connect) {
						showConnectFail();
					}
					try_connect = false;
					updateView();

					break;
				default:
					break;
				}

			}

		}
	};

	@Override
	public void onDestroy() {
		Log.e("water", "home onDestroy");
		try {
			unregisterReceiver(mStatusReceive);
			mHandler.removeMessages(BluetoothService.MESSAGE_STATE_NOTHING);
			if (mbtService != null) {
				if (mbtService.getState() == BluetoothService.STATE_CONNECTED) {
					mbtService.stop();
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		super.onDestroy();
	}

	private void showProgressbar() {
		progressbar.setVisibility(View.VISIBLE);
		device_state_img.setEnabled(false);
		device_connect_state_txt.setEnabled(false);
	}

	private void hideProgressbar() {
		progressbar.setVisibility(View.GONE);
		device_state_img.setEnabled(true);
		device_connect_state_txt.setEnabled(true);
	}

	private void startUse() {
		operation = OPERATION_OPEN;
		showProgressbar();

		if (mbtService.getState() != BluetoothService.STATE_CONNECTED) {
			connectDevice();
		} else {

			if (Common.compairMacSame(mDeviceInfo.devMac,
					mbtService.getConnectAddress())) {
				chaxueshebei();
			} else {
				connectDevice();
			}

		}
	}

	private void stopUse() {

		showProgressbar();
		operation = OPERATION_CLOSE;

		if (mbtService.getState() != BluetoothService.STATE_CONNECTED) {
			connectDevice();
		} else {

			if (Common.compairMacSame(mDeviceInfo.devMac,
					mbtService.getConnectAddress())) {
				chaxueshebei();
			} else {
				connectDevice();
			}

		}

	}

	private void connectDevice() {
		if (mDeviceInfo.devMac.contains(":")) {
			mbtService.connect(bluetoothAdapter.getRemoteDevice(mDeviceInfo.devMac));
		} else {
			mbtService.connect(bluetoothAdapter.getRemoteDevice(Common.getMacMode(mDeviceInfo.devMac)));
		}
	}

	private void bindSchool(final DeviceInfo deviceInfo) {

		if (Common.isNetWorkConnected(TestWaterActivity.this)) {
			AjaxParams ajaxParams = new AjaxParams();
			ajaxParams.put("TelPhone", sp.getString(Common.USER_PHONE_NUM, ""));
			ajaxParams.put("PrjID", "" + deviceInfo.PrjID);
			ajaxParams.put("WXID", "0");
			ajaxParams.put("loginCode", userInfo.TelPhone + ","
					+ userInfo.loginCode);

			new FinalHttp().get(Common.BASE_URL + "bingding", ajaxParams,
					new AjaxCallBack<Object>() {

						@Override
						public void onSuccess(Object t) {
							super.onSuccess(t);
							String result = t.toString();
							Log.d("TestWaterActivity", result);
							PublicGetData publicGetData = new Gson().fromJson(result, PublicGetData.class);
							if (publicGetData.error_code.equals("0")) {

								UserInfo mInfo = new Gson().fromJson(publicGetData.data, UserInfo.class);
								mInfo.loginCode = userInfo.loginCode;
								Editor editor = sp.edit();
								editor.putString(Common.USER_INFO, new Gson().toJson(mInfo));
								editor.commit();

								userInfo = mInfo;

								initView();
								updateView();
								IntentFilter statusFilter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
								statusFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);// 状态改变
								registerReceiver(mStatusReceive, statusFilter);

								connectDevice();

							} else if (publicGetData.error_code.equals("7")) {
								Common.logout(TestWaterActivity.this, sp, dialogBuilder);
							} else {
								Common.showToast(TestWaterActivity.this, R.string.bind_fail, Gravity.CENTER);

							}

						}

						@Override
						public void onFailure(Throwable t, int errorNo, String strMsg) {
							super.onFailure(t, errorNo, strMsg);
						}
					});

		} else {
			Common.showNoNetworkDailog(dialogBuilder, TestWaterActivity.this);
		}

	}

	private void getDownRate(final int mproductid,final int mdeviceid,
			final byte[] macBuffer, final byte[] tac_timeBuffer) {

		if (Common.isNetWorkConnected(TestWaterActivity.this)) {
			AjaxParams ajaxParams = new AjaxParams();

			if (userInfo == null ) {
				return;
			}
			ajaxParams.put("PrjID", mproductid + "");
			ajaxParams.put("AccID", userInfo.AccID + "");

			ajaxParams.put("GroupID", userInfo.GroupID + "");
			ajaxParams.put("DevID", mdeviceid + "");
			ajaxParams.put("loginCode", userInfo.TelPhone + "," + userInfo.loginCode);

			new FinalHttp().get(Common.BASE_URL + "downRate", ajaxParams,
					new AjaxCallBack<Object>() {

						@Override
						public void onSuccess(Object t) {
							super.onSuccess(t);
							String result = t.toString();
							PublicGetData publicGetData = new Gson().fromJson(result, PublicGetData.class);
							if (publicGetData.error_code.equals("0")) {

								DownRateInfo downRateInfo = new Gson().fromJson(publicGetData.data, DownRateInfo.class);
								if (downRateInfo != null) {

									yukou_txt.setText(Common.getShowMonty(downRateInfo.PerMoney, getString(R.string.yuan1)));

									xiafafeilv(downRateInfo, mproductid,mdeviceid, userInfo.AccID, macBuffer,
											tac_timeBuffer);

								} else {
									hideProgressbar();
								}
							} else if (publicGetData.error_code.equals("7")) {
								hideProgressbar();
								Common.logout(TestWaterActivity.this, sp, dialogBuilder);
							} else {
								hideProgressbar();
								if (publicGetData.message.contains("余额不足")) {
									showYueBuzu();
								} else {
									showErrorDownRate(getString(R.string.error_downrate1));

									DownRateInfo downRateInfo = new Gson()
											.fromJson(publicGetData.data, DownRateInfo.class);

									if (downRateInfo != null) {
										
										PostConsumeData postConsumeData = new PostConsumeData();
										postConsumeData.downRateInfo = downRateInfo;
										postConsumeData.productid = mproductid;
										postConsumeData.devid = mdeviceid;
										
										postEmptyDownRate(postConsumeData);

									}
								}

							}

						}

						@Override
						public void onFailure(Throwable t, int errorNo, String strMsg) {
							hideProgressbar();
							super.onFailure(t, errorNo, strMsg);
						}
					});
		} else {
			hideProgressbar();
			Common.showNoNetworkDailog(dialogBuilder, TestWaterActivity.this);
		}

	}

	private void postEmptyDownRate(PostConsumeData postConsumeData) {
		AjaxParams ajaxParams = new AjaxParams();
		ajaxParams.put("PrjID", postConsumeData.productid + "");
		ajaxParams.put("AccID", userInfo.AccID + "");
		ajaxParams.put("DevID", postConsumeData.devid + "");
		ajaxParams.put("GroupID", userInfo.GroupID + "");

		ajaxParams.put("UpMoney", 0 + "");
		ajaxParams.put("PerMoney", postConsumeData.downRateInfo.PerMoney + "");
		ajaxParams.put("ConsumeDT",  postConsumeData.downRateInfo.ConsumeDT);
		ajaxParams.put("loginCode", userInfo.TelPhone + ","
				+ userInfo.loginCode);
		new FinalHttp().get(Common.BASE_URL + "savexf", ajaxParams,
				new AjaxCallBack<Object>() {

					@Override
					public void onSuccess(Object t) {
						super.onSuccess(t);

					}

					@Override
					public void onFailure(Throwable t, int errorNo, String strMsg) {
						super.onFailure(t, errorNo, strMsg);
					}
				});

	}

	private void postXiaoFei(final boolean ismine, final int prjid,
			final int accoutid, final int deviceid, int groupid,
			int consumeMoney, int preMoney, final String consumetime,
			final int usercount) {
		if (Common.isNetWorkConnected(TestWaterActivity.this)) {
			AjaxParams ajaxParams = new AjaxParams();
			ajaxParams.put("PrjID", prjid + "");
			ajaxParams.put("AccID", accoutid + "");
			ajaxParams.put("DevID", deviceid + "");
			ajaxParams.put("GroupID", groupid + "");

			ajaxParams.put("UpMoney", consumeMoney + "");
			ajaxParams.put("PerMoney", preMoney + "");
			ajaxParams.put("ConsumeDT", "20" + consumetime);
			ajaxParams.put("loginCode", userInfo.TelPhone + ","
					+ userInfo.loginCode);
			new FinalHttp().get(Common.BASE_URL + "savexf", ajaxParams,
					new AjaxCallBack<Object>() {

						@Override
						public void onSuccess(Object t) {
							super.onSuccess(t);
							String result = t.toString();
							PublicPostConsumeData publicPostConsumeData = new Gson()
									.fromJson(result,
											PublicPostConsumeData.class);

							if (publicPostConsumeData.error_code.equals("0")) {

								fanhuicunchu(consumetime, prjid, deviceid,
										accoutid, usercount);

								if (ismine && operation == OPERATION_CLOSE) {
									// 这里表明当前结束的是自己的消费清单，并且此时用户是想自己结束

									updateFinishView();

									updateUserInfo(userInfo);

									operation = OPERATION_NOTHING;
									mHandler.removeMessages(BluetoothService.MESSAGE_STATE_NOTHING);
									Intent intent = new Intent();
									intent.setClass(TestWaterActivity.this,
											ConsumeActivity.class);
									intent.putExtra("consume_type", 3);
									intent.putExtra("consumedata",
											publicPostConsumeData);
									startActivity(intent);
									finish();
								} else {
									hideProgressbar();
								}

							} else if (publicPostConsumeData.error_code
									.equals("7")) {
								hideProgressbar();
								Common.logout(TestWaterActivity.this, sp,
										dialogBuilder);
							} else if (publicPostConsumeData.error_code
									.equals("-3")) {
								hideProgressbar();
								fanhuicunchu(consumetime, prjid, deviceid,
										accoutid, usercount);
							}else if (publicPostConsumeData.error_code
									.equals("-2")) {
								hideProgressbar();
								fanhuicunchu(consumetime, prjid, deviceid,
										accoutid, usercount);
							} else {
								hideProgressbar();
							}

						}

						@Override
						public void onFailure(Throwable t, int errorNo,
								String strMsg) {
							hideProgressbar();
							super.onFailure(t, errorNo, strMsg);
						}
					});
		} else {
			hideProgressbar();
			Common.showNoNetworkDailog(dialogBuilder, TestWaterActivity.this);
		}
	}

	private final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case BluetoothService.MESSAGE_NO_XIAFACALLBACK:
				PostConsumeData postConsumeData = (PostConsumeData) msg.obj;
				postEmptyDownRate(postConsumeData);
				showErrorDownRate(getString(R.string.error_downrate1));
				break;

			case BluetoothService.MESSAGE_NO_JIESHUCALLBACK:
				showErrorDownRate(getString(R.string.error_downrate1));
				break;

			case BluetoothService.MESSAGE_STATE_NOTHING:
				try_connect = false;
				operation = OPERATION_NOTHING;
				if (mbtService != null) {
					if (mbtService.getState() == BluetoothService.STATE_CONNECTED) {
						mbtService.stop();
					}
				}
				mHandler.removeMessages(BluetoothService.MESSAGE_STATE_NOTHING);
				updateView();
				break;

			case BluetoothService.MESSAGE_STATE_CHANGE:
				switch (msg.arg1) {
				case BluetoothService.STATE_CONNECTED:
					if (dialogBuilder.isShowing()) {
						dialogBuilder.dismiss();
					}
					Log.e("water", "STATE_CONNECTED");
					try_connect = false;
					updateView();
					if (operation == OPERATION_CLOSE) {
						jieshufeilv();
					} else {
						showProgressbar();
						chaxueshebei();
					}
					mHandler.removeMessages(BluetoothService.MESSAGE_STATE_NOTHING);
					Message message = new Message();
					message.what = BluetoothService.MESSAGE_STATE_NOTHING;
					mHandler.sendMessageDelayed(message, DELAY_TIME);
					break;
				case BluetoothService.STATE_CONNECTING:
					showProgressbar();
					break;
				case BluetoothService.STATE_LISTEN:
					break;
				case BluetoothService.STATE_CONNECTION_LOST:
					// break;
				case BluetoothService.STATE_CONNECTION_FAIL: {
					Log.e("water", "STATE_CONNECTION_FAIL");
					if (try_connect) {
						showConnectFail();
					}
					try_connect = false;
					hideProgressbar();
					updateView();

				}
					break;
				case BluetoothService.STATE_NONE:
					break;
				}
				break;
			case BluetoothService.MESSAGE_WRITE:
				break;
			case BluetoothService.MESSAGE_READ:
				byte[] readBuf = (byte[]) msg.obj;
				String result = DigitalTrans.bytesToHexString(readBuf);

				processBluetoothDada(result);

				break;
			}
		}
	};

	private void processBluetoothDada(String result) {
		if (result.startsWith("23") && result.endsWith("0a")) {
			String finalString = result.substring(2, result.length() - 2);

			String callbackString = DigitalTrans
					.AsciiStringToString(finalString);

			byte[] databyte = Toolsutils.handleResult(TestWaterActivity.this,
					callbackString);

			byte[] allbyte = DigitalTrans.hexStringToByte(DigitalTrans
					.AsciiStringToString(finalString));

			Log.e("water", "finalString = " + finalString
					+ "  callbackString = " + callbackString
					+ "  allbyte[4]  = " + allbyte[4] + "  databyte[0] = "
					+ databyte[0]);

			if (databyte != null && databyte[0] == -128) {

				if (allbyte[4] == 25) {
					// 清除设备
					xiafaxiangmu();
				} else if (allbyte[4] == 32) {
					// 下发项目编号
					chaxueshebei();
				} else if (allbyte[4] == 33) {
					// 下发费率
					mHandler.removeMessages(BluetoothService.MESSAGE_NO_XIAFACALLBACK);
					updateUseingView();
					hideProgressbar();
				} else if (allbyte[4] == 34) {
					// 结束消费
					mHandler.removeMessages(BluetoothService.MESSAGE_NO_JIESHUCALLBACK);
					caijishuju();

				} else if (allbyte[4] == 35) {
					// 查询设备状态
					if (databyte != null && databyte.length == 23) {

						byte[] productidBuffer = new byte[4];
						for (int i = 0; i < productidBuffer.length; i++) {
							productidBuffer[i] = databyte[i + 1];
						}
						int mproductid = DigitalTrans
								.hexStringToAlgorism(DigitalTrans
										.bytesToHexString(productidBuffer));

						byte[] deviceidBuffer = new byte[4];
						for (int i = 0; i < deviceidBuffer.length; i++) {
							deviceidBuffer[i] = databyte[i + 5];
						}
						int mdeviceid = DigitalTrans
								.hexStringToAlgorism(DigitalTrans
										.bytesToHexString(deviceidBuffer));

						byte[] accoutidBuffer = new byte[4];
						for (int i = 0; i < accoutidBuffer.length; i++) {
							accoutidBuffer[i] = databyte[i + 9];
						}
						int maccountid = DigitalTrans
								.hexStringToAlgorism(DigitalTrans
										.bytesToHexString(accoutidBuffer));

						byte[] onchargeBuffer = new byte[1];
						for (int i = 0; i < onchargeBuffer.length; i++) {
							onchargeBuffer[i] = databyte[i + 19];
						}
						int charge = DigitalTrans
								.hexStringToAlgorism(DigitalTrans
										.bytesToHexString(onchargeBuffer));
						if (charge == 0) {
							// 空闲状态
							if (operation == OPERATION_OPEN) {

								if (mDeviceInfo != null) {
									if (mDeviceInfo.PrjID == mproductid
											&& mDeviceInfo.DevID == mdeviceid) {
										// 表明当前deviceid和productid一致。开始下发费率
										byte[] tac_timeBuffer = new byte[2];
										for (int i = 0; i < tac_timeBuffer.length; i++) {
											tac_timeBuffer[i] = databyte[i + 21];
										}

										byte[] macBuffer = new byte[6];
										for (int i = 0; i < macBuffer.length; i++) {
											macBuffer[i] = databyte[i + 13];
										}
										getDownRate( mproductid,mdeviceid,
												macBuffer, tac_timeBuffer);

									} else {
										// deviceid 和 productid 不一致。
										qingchushebei();
									}
								} else {
									hideProgressbar();
								}

							} else if (operation == OPERATION_CLOSE) {

								if (userInfo != null
										&& userInfo.AccID == maccountid) {
									// 表明是自己想要关闭用水的时候，发现设备已经被自己关闭了。
									updateFinishView();
								} else {
									// // 表明是自己想要关闭用水的时候，发现设备已经被别人关闭了。
									showOtherFinish();

									mHandler.postDelayed(new Runnable() {

										@Override
										public void run() {
											if (dialogBuilder != null
													&& dialogBuilder
															.isShowing()) {
												updateFinishView();
												dialogBuilder.dismiss();
											}
										}
									}, 5000);
								}

							} else {
								hideProgressbar();
							}

						} else if (charge == 1) {
							// oncharge_edit.setText("有订单在进行");
							if (userInfo != null
									&& userInfo.AccID == maccountid) {
								// 表明是自己正在使用水表

								if (operation == OPERATION_OPEN) {
									// 这个时候需要采集数据，采集数据的目的是为了显示ui，然后显示ui
									caiji_updateview = true;
									caijishuju();

								} else if (operation == OPERATION_CLOSE) {
									// 自己想关水的时候发现自己正在使用水，那么直接结算消费
									jieshufeilv();
								} else {
									// 自动连接上了设备，然后发现设备是自己正在用水。这个时候也是更新一下ui，不需要下发费率
									// 这个时候需要采集数据，采集数据的目的是为了显示ui，然后显示ui
									caiji_updateview = true;
									caijishuju();
								}

							} else {
								// 表明是别人正在使用水表
								if (operation == OPERATION_OPEN) {
									// 自己想开水的时候发现别人在使用这个设备了，就需要提示是不是需要打断别人。
									showInterruptDeviceDailog();
									// jieshufeilv();
								} else if (operation == OPERATION_CLOSE) {
									// 自己想关水的时候发现别人在使用这个设备了，就不打扰别人用水了。

									showOtherFinish();
									mHandler.postDelayed(new Runnable() {

										@Override
										public void run() {
											if (dialogBuilder != null
													&& dialogBuilder
															.isShowing()) {
												updateFinishView();
												dialogBuilder.dismiss();
											}
										}
									}, 5000);
								} else {
									hideProgressbar();
								}

							}

						} else if (charge == 2) {
							// oncharge_edit.setText("刷卡消费");
							showErrorDownRate(getString(R.string.error_downrate3));
						} else if (charge == 3) {
							// oncharge_edit.setText("消费完成，数据没有采集");
							// 当查询到设备没有采集数据的话，不管是谁在用，在做什么操作，都应该采集数据。
							caijishuju();

						}

					} else {
						hideProgressbar();
					}

				} else if (allbyte[4] == -5) {
					// 采集消费数据

					if (databyte != null && databyte.length == 42) {

						byte[] timeidBuffer = new byte[6];
						for (int i = 0; i < timeidBuffer.length; i++) {
							timeidBuffer[i] = databyte[i + 1];
						}
						String timeid = DigitalTrans
								.bytesToHexString(timeidBuffer);

						byte[] productidBuffer = new byte[4];
						for (int i = 0; i < productidBuffer.length; i++) {
							productidBuffer[i] = databyte[i + 7];
						}
						int productid = DigitalTrans
								.hexStringToAlgorism(DigitalTrans
										.bytesToHexString(productidBuffer));

						byte[] deviceidBuffer = new byte[4];
						for (int i = 0; i < deviceidBuffer.length; i++) {
							deviceidBuffer[i] = databyte[i + 11];
						}
						int deviceid = DigitalTrans
								.hexStringToAlgorism(DigitalTrans
										.bytesToHexString(deviceidBuffer));

						byte[] accoutidBuffer = new byte[4];
						for (int i = 0; i < accoutidBuffer.length; i++) {
							accoutidBuffer[i] = databyte[i + 15];
						}
						int accountid = DigitalTrans
								.hexStringToAlgorism(DigitalTrans
										.bytesToHexString(accoutidBuffer));

						byte[] accounttypeBuffer = new byte[1];
						for (int i = 0; i < accounttypeBuffer.length; i++) {
							accounttypeBuffer[i] = databyte[i + 19];
						}
						int accounttype = DigitalTrans
								.hexStringToAlgorism(DigitalTrans
										.bytesToHexString(accounttypeBuffer));

						byte[] usercountBuffer = new byte[4];
						for (int i = 0; i < usercountBuffer.length; i++) {
							usercountBuffer[i] = databyte[i + 20];
						}
						int usercount = DigitalTrans
								.hexStringToAlgorism(DigitalTrans
										.bytesToHexString(usercountBuffer));

						byte[] ykmoneyBuffer = new byte[4];
						for (int i = 0; i < ykmoneyBuffer.length; i++) {
							ykmoneyBuffer[i] = databyte[i + 24];
						}
						int ykmoney = DigitalTrans
								.hexStringToAlgorism(DigitalTrans
										.bytesToHexString(ykmoneyBuffer));

						byte[] consumeMoneBuffer = new byte[4];
						for (int i = 0; i < consumeMoneBuffer.length; i++) {
							consumeMoneBuffer[i] = databyte[i + 28];
						}
						int consumeMone = DigitalTrans
								.hexStringToAlgorism(DigitalTrans
										.bytesToHexString(consumeMoneBuffer));

						if (!caiji_updateview) {

							if (userInfo != null && userInfo.AccID == accountid) {
								// 表明是自己使用水表
								postXiaoFei(true, productid, accountid,
										deviceid, accounttype, consumeMone,
										ykmoney, timeid, usercount);

							} else {
								// 表明是别人使用水表
								postXiaoFei(false, productid, accountid,
										deviceid, accounttype, consumeMone,
										ykmoney, timeid, usercount);
							}
						} else {
							
							// 这里采集数据的目的是当发现是自己正在用水，更新一下正在用水的UI的。
							caiji_updateview = false;
							hideProgressbar();

							float size = (float) ykmoney / 1000;
							DecimalFormat df = new DecimalFormat("0.00");// 格式化小数，不足的补0
							String filesize = df.format(size);

							yukou_txt.setText(filesize
									+ getString(R.string.yuan1));

							updateUseingView();
							
						}

					} else {
						hideProgressbar();
						Common.showToast(TestWaterActivity.this,
								"采集消费数据返回的数据域出错", Gravity.CENTER);
					}

				} else if (allbyte[4] == -6) {
					// 返回记录
					if (operation == OPERATION_OPEN) {
						// 表明这里是打开的时候，中断别人洗澡的时候

						if (mDeviceInfo != null) {
							chaxueshebei();
						} else {
							hideProgressbar();
						}
					} else {
						hideProgressbar();
						if (mbtService != null) {
							if (mbtService.getState() == BluetoothService.STATE_CONNECTED) {
								mbtService.stop();
							}
						}
					}

				}
			} else {
				// processDialog.dismiss();
				hideProgressbar();
				if (allbyte[4] == 25) {
					// 清除设备
					Common.showToast(TestWaterActivity.this, "清除设备信息返回数据错误",
							Gravity.CENTER);
				} else if (allbyte[4] == 32) {
					// 下发项目编号
					Common.showToast(TestWaterActivity.this, "下发项目编号返回数据错误",
							Gravity.CENTER);
				} else if (allbyte[4] == 33) {
					// 下发费率
					chaxueshebei();
					// Common.showToast(YongShuiActivity.this,
					// "下发费率返回数据错误，正在检查设备状态，请稍候再试", Gravity.CENTER);
				} else if (allbyte[4] == 34) {
					// 结束消费
					caijishuju();
				} else if (allbyte[4] == 35) {
					// 查询设备状态
					Common.showToast(TestWaterActivity.this, "查询设备状态返回数据错误",
							Gravity.CENTER);
				} else if (allbyte[4] == -5) {
					// 采集消费数据
					Common.showToast(TestWaterActivity.this, "采集数据返回数据错误",
							Gravity.CENTER);
				} else if (allbyte[4] == -6) {
					// 返回记录
					Common.showToast(TestWaterActivity.this,
							"返回记录存储返回数据错误，请重试一遍", Gravity.CENTER);
				}

			}

		} else {
			hideProgressbar();
		}
	}

	private void qingchushebei() {
		CMDUtils.qingchushebei(mbtService, false);
	}

	private void xiafaxiangmu() {

		try {
			String retureString = CMDUtils.xiafaxiangmu(mbtService,
					mDeviceInfo.DevID, mDeviceInfo.PrjID, true);
		} catch (IOException e1) {
			e1.printStackTrace();
			operation = OPERATION_NOTHING;
			hideProgressbar();
			Common.showToast(TestWaterActivity.this, "请检查参数是否完整",
					Gravity.CENTER);
		}

	}

	private void chaxueshebei() {
		String retureString = CMDUtils.chaxueshebei(mbtService, true);
	}

	private void xiafafeilv(DownRateInfo downRateInfo, int mproductid,int mdeviceid,
			int maccountid, byte[] macBuffer, byte[] tac_timeBuffer) {

		try {

			String retureString = CMDUtils.xiafafeilv(mbtService, true,
					downRateInfo, mproductid, maccountid, 1, macBuffer,
					tac_timeBuffer);
			mHandler.removeMessages(BluetoothService.MESSAGE_NO_XIAFACALLBACK);
			PostConsumeData postConsumeData = new PostConsumeData();
			postConsumeData.downRateInfo = downRateInfo;
			postConsumeData.productid = mproductid;
			postConsumeData.devid = mdeviceid;
			
			Message message = new Message();
			message.what = BluetoothService.MESSAGE_NO_XIAFACALLBACK;
			message.obj = postConsumeData;
			mHandler.sendMessageDelayed(message, 3000);

		} catch (IOException e1) {
			e1.printStackTrace();
			operation = OPERATION_NOTHING;
			hideProgressbar();
			Common.showToast(TestWaterActivity.this, "请检查参数是否完整",
					Gravity.CENTER);
		}
	}

	private void jieshufeilv() {
		String retureString = CMDUtils.jieshufeilv(mbtService, true);
		mHandler.removeMessages(BluetoothService.MESSAGE_NO_XIAFACALLBACK);
		Message message = new Message();
		message.what = BluetoothService.MESSAGE_NO_JIESHUCALLBACK;
		mHandler.sendMessageDelayed(message, 3000);
	}

	private void caijishuju() {

		String retureString = CMDUtils.caijishuju(mbtService, true);

	}

	private void fanhuicunchu(String timeid, int productid, int deviceid,
			int accountid, int usercount) {
		try {
			String retureString;
			retureString = CMDUtils.fanhuicunchu(mbtService, true, timeid,
					productid, deviceid, accountid, usercount);
		} catch (IOException e1) {
			e1.printStackTrace();
			Common.showToast(TestWaterActivity.this, "请检查参数是否完整",
					Gravity.CENTER);
		}

	}

	private void showInterruptDeviceDailog() {
		hideProgressbar();
		dialogBuilder.withTitle(getString(R.string.tips))
				.withMessage(getString(R.string.interrupt_tips))
				.withEffect(Effectstype.Fadein).isCancelable(false)
				.withButton1Text(getString(R.string.cancel))
				.setButton1Click(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						operation = OPERATION_NOTHING;
						dialogBuilder.dismiss();
					}
				}).withButton2Text(getString(R.string.sure))
				.setButton2Click(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						jieshufeilv();
						dialogBuilder.dismiss();
					}
				}).show();
	}

	private void showStopDailog() {

		if (Common.isNetWorkConnected(TestWaterActivity.this)) {
			hideProgressbar();
			dialogBuilder.withTitle(getString(R.string.tips))
					.withMessage(getString(R.string.stop_tips))
					.withEffect(Effectstype.Fadein).isCancelable(false)
					.withButton1Text(getString(R.string.cancel))
					.setButton1Click(new View.OnClickListener() {
						@Override
						public void onClick(View v) {

							dialogBuilder.dismiss();
						}
					}).withButton2Text(getString(R.string.sure))
					.setButton2Click(new View.OnClickListener() {
						@Override
						public void onClick(View v) {

							stopUse();

							mHandler.removeMessages(BluetoothService.MESSAGE_STATE_NOTHING);
							Message message = new Message();
							message.what = BluetoothService.MESSAGE_STATE_NOTHING;
							mHandler.sendMessageDelayed(message, DELAY_TIME);

							dialogBuilder.dismiss();
						}
					}).show();

		} else {
			hideProgressbar();
			Common.showNoNetworkDailog(dialogBuilder, TestWaterActivity.this);
		}

	}

	private void showYueBuzu() {
		operation = OPERATION_NOTHING;
		hideProgressbar();
		dialogBuilder.withTitle(getString(R.string.tips))
				.withMessage(getString(R.string.yuebuzu_tips))
				.withEffect(Effectstype.Fadein).isCancelable(false)
				.withButton2Text(getString(R.string.i_known))
				.setButton2Click(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						dialogBuilder.dismiss();
					}
				}).show();
	}

	private void showOtherFinish() {
		operation = OPERATION_NOTHING;
		hideProgressbar();
		dialogBuilder.withTitle(getString(R.string.tips))
				.withMessage(getString(R.string.other_finish1))
				.withEffect(Effectstype.Fadein).isCancelable(false)
				.withButton2Text(getString(R.string.i_known))
				.setButton2Click(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						updateFinishView();
						updateView();
						dialogBuilder.dismiss();
					}
				}).show();
	}

	private void showErrorDownRate(String message) {
		operation = OPERATION_NOTHING;
		hideProgressbar();
		dialogBuilder.withTitle(getString(R.string.tips)).withMessage(message)
				.withEffect(Effectstype.Fadein).isCancelable(false)
				.withButton2Text(getString(R.string.i_known))
				.setButton2Click(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						dialogBuilder.dismiss();
					}
				}).show();
	}

	private void showConnectFail() {
		operation = OPERATION_NOTHING;
		hideProgressbar();
		dialogBuilder.withTitle(getString(R.string.tips))
				.withMessage(getString(R.string.connect_fail))
				.withEffect(Effectstype.Fadein).isCancelable(false)
				.withButton2Text(getString(R.string.i_known))
				.setButton2Click(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						dialogBuilder.dismiss();
					}
				}).show();
	}

	private void updateUserInfo(final UserInfo mInfo) {

		if (Common.isNetWorkConnected(TestWaterActivity.this)) {

			if (TextUtils.isEmpty(mInfo.TelPhone + "")) {
				Common.showToast(TestWaterActivity.this,
						R.string.phonenum_null, Gravity.CENTER);
				return;
			}
			if (!Common.isPhoneNum(mInfo.TelPhone + "")) {
				Common.showToast(TestWaterActivity.this,
						R.string.phonenum_not_irregular, Gravity.CENTER);
				return;
			}

			AjaxParams ajaxParams = new AjaxParams();
			ajaxParams.put("TelPhone", mInfo.TelPhone + "");
			ajaxParams.put("PrjID", mInfo.PrjID + "");
			ajaxParams.put("WXID", "0");
			ajaxParams.put("loginCode", mInfo.TelPhone + "," + mInfo.loginCode);
			new FinalHttp().get(Common.BASE_URL + "accountInfo", ajaxParams,
					new AjaxCallBack<Object>() {

						@Override
						public void onSuccess(Object t) {
							super.onSuccess(t);
							String result = t.toString();
							PublicGetData publicGetData = new Gson().fromJson(
									result, PublicGetData.class);
							if (publicGetData.error_code.equals("0")) {

								UserInfo info = new Gson().fromJson(
										publicGetData.data, UserInfo.class);
								info.loginCode = mInfo.loginCode;
								if (info != null) {
									userInfo = info;
									Editor editor = sp.edit();
									editor.putString(Common.USER_PHONE_NUM,
											info.TelPhone + "");
									editor.putString(Common.USER_INFO,
											new Gson().toJson(info));
									editor.putInt(Common.ACCOUNT_IS_USER,
											info.GroupID);
									editor.commit();
									try {
										yue_txt.setText(Common.getShowMonty(
												info.AccMoney
														+ info.GivenAccMoney,
												getString(R.string.yuan1)));
									} catch (Exception e) {
										// TODO: handle exception
									}

								}

							}

						}

						@Override
						public void onFailure(Throwable t, int errorNo,
								String strMsg) {
							super.onFailure(t, errorNo, strMsg);
						}
					});

		}

	}

}