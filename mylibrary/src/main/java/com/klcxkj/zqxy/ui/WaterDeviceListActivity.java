package com.klcxkj.zqxy.ui;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.klcxkj.zqxy.MyApp;
import com.klcxkj.mylibrary.R;
import com.klcxkj.zqxy.adapter.WaterDeviceAdapter;
import com.klcxkj.zqxy.common.Common;
import com.klcxkj.zqxy.databean.DeviceInfo;
import com.klcxkj.zqxy.databean.UserInfo;
import com.klcxkj.zqxy.response.PublicArrayData;
import com.klcxkj.zqxy.response.PublicGetData;
import com.klcxkj.zqxy.widget.Effectstype;

import net.android.tools.afinal.FinalHttp;
import net.android.tools.afinal.http.AjaxCallBack;
import net.android.tools.afinal.http.AjaxParams;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Dispatcher;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 热水表设备搜索
 */
public class WaterDeviceListActivity extends BaseActivity {


	private ListView device_listview;
	private LinearLayout progressbar_layout;

	private BluetoothAdapter mBtAdapter;

	private WaterDeviceAdapter waterDeviceAdapter;
	private ArrayList<DeviceInfo> mBluetoothDevices;
	private ArrayList<String> mBluetoothName;

	public static final int LINK_WATER_DEVICE = 999;
	private static final int LINK_OUT_TIME = 998;

	private UserInfo mUserInfo;
	private SharedPreferences sp;
	private int capture_type;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_devicelist);
		initView();
		initdata();
		sp = getSharedPreferences("adminInfo", Context.MODE_PRIVATE);
		mUserInfo = Common.getUserInfo(sp);
		mBtAdapter = BluetoothAdapter.getDefaultAdapter();

		mBluetoothDevices = new ArrayList<DeviceInfo>();
		mBluetoothName = new ArrayList<String>();

		waterDeviceAdapter = new WaterDeviceAdapter(this, mBluetoothDevices, mHandler);
		device_listview.setAdapter(waterDeviceAdapter);

		// 设置广播信息过滤
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
		intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
		intentFilter.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
		intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);

		this.registerReceiver(receiver, intentFilter);
		if (mBtAdapter.isEnabled()){
			startSearch();
		}else {
			showNoBluetoothDailog();
		}

	}

	private void initdata() {

		capture_type =getIntent().getExtras().getInt("capture_type");
		//Log.d("WaterDeviceListActivity", "capture_type:" + capture_type);
		//queryBigDeciveType();
	}

	private void initView() {
		showMenu("设备列表");
		device_listview = (ListView) findViewById(R.id.device_listview);
		progressbar_layout = (LinearLayout) findViewById(R.id.progressbar_layout);
		progressbar_layout.setVisibility(View.GONE);

	}

	private void startSearch() {

		mBluetoothDevices.clear();
		waterDeviceAdapter.changeData(mBluetoothDevices);
		mBluetoothName.clear();
		progressbar_layout.setVisibility(View.VISIBLE);
		doDiscovery();

		mHandler.removeMessages(LINK_OUT_TIME);
		mHandler.sendEmptyMessageDelayed(LINK_OUT_TIME, 60000);

	}

	private void doDiscovery() {
		if (mBtAdapter.isDiscovering()) {
			mBtAdapter.cancelDiscovery();
		}
		mBtAdapter.startDiscovery();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mHandler.removeMessages(LINK_OUT_TIME);
		// Make sure we're not doing discovery anymore
		if (mBtAdapter != null) {
			mBtAdapter.cancelDiscovery();
		}
		//注销蓝牙广播
		unregisterReceiver(receiver);
		cancleAll();
	}

	@Override
	public void onBackPressed() {
		finish();
	}

	private BroadcastReceiver receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (BluetoothDevice.ACTION_FOUND.equals(action)) {
				// 获取查找到的蓝牙设备
				// device_layout.setVisibility(View.VISIBLE);
				BluetoothDevice bluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				BluetoothDevice mDevice = mBtAdapter.getRemoteDevice(bluetoothDevice.getAddress());
				if (mDevice != null && mDevice.getAddress() != null) {
					try {
						String address = mDevice.getAddress();
						String name = mDevice.getName();
						if (name!=null && name.startsWith("KLCXKJ")){
							if (!mBluetoothName.contains(address)){
								getMACInfo(address);
							}
						}

					} catch (Exception e) {
						// TODO: handle exception
					}
				}

			} else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
				// 状态改变的广播
				// BluetoothDevice bluetoothDevice = intent
				// .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				// BluetoothDevice mDevice = mBtAdapter
				// .getRemoteDevice(bluetoothDevice.getAddress());
			}
		}
	};

	private final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == LINK_WATER_DEVICE) {
				DeviceInfo deviceInfo = (DeviceInfo) msg.obj;
				if (deviceInfo != null && !TextUtils.isEmpty(deviceInfo.devMac)) {
					if (mUserInfo.PrjID ==0){
						//用户没有项目的时候去绑定项目。即第一次绑定
						bindProtect(deviceInfo);
					}else {
						if (deviceInfo.PrjID !=mUserInfo.PrjID){
							toast("此设备的项目与您的项目不一致");
						}else {
							skipDecive(deviceInfo);
						}
					}

				}
			} else if (msg.what == LINK_OUT_TIME) {
				if (mBluetoothDevices == null || mBluetoothDevices.size() == 0) {
					progressbar_layout.setVisibility(View.GONE);
					showNoDevice();
					mBtAdapter.cancelDiscovery();
				} else {
					progressbar_layout.setVisibility(View.GONE);
				}

			}

		}
	};

	/**
	 * 开始使用
	 */
	private void skipDecive(DeviceInfo deviceInfo){
		Intent intent =new Intent();
		Bundle bundle =new Bundle();
		bundle.putSerializable("DeviceInfo",deviceInfo);
		Editor editor = sp.edit();
		switch (capture_type){
			case 4: //洗澡
				intent.setClass(WaterDeviceListActivity.this,Bath2Activity.class);
				intent.putExtras(bundle);
				editor.putString(Common.USER_BRATHE + Common.getUserPhone(sp), new Gson().toJson(deviceInfo));
				editor.commit();
				startActivity(intent);
				break;
			case 5: //开水器
				editor.putString(Common.USER_WATER + Common.getUserPhone(sp), new Gson().toJson(deviceInfo));
				editor.commit();
				intent.setClass(WaterDeviceListActivity.this,Bath2Activity.class);
				intent.putExtras(bundle);
				startActivity(intent);
				break;
			case 6://洗衣机
				intent.setClass(WaterDeviceListActivity.this, WashingActivity.class);
				intent.putExtras(bundle);
				editor.putString(Common.USER_WASHING + Common.getUserPhone(sp), new Gson().toJson(deviceInfo));
				editor.commit();
				startActivity(intent);
				break;

			case 7://吹风机
				intent.setClass(WaterDeviceListActivity.this,Bath2Activity.class);
				intent.putExtras(bundle);
				startActivity(intent);
				break;
			case 8://充电
				intent.setClass(WaterDeviceListActivity.this,Bath2Activity.class);
				intent.putExtras(bundle);
				startActivity(intent);
				break;
			case 9://空调
				intent.setClass(WaterDeviceListActivity.this,Bath2Activity.class);
				intent.putExtras(bundle);
				startActivity(intent);
				break;
			case 255:
				break;

		}
		if (isFrist==-1){ //新用户第一绑定的时候
			finish();
		}
	}

	/**
	 * 绑定项目
	 */
	private int isFrist =0;
	private void bindProtect(final DeviceInfo deviceInfo){
		AjaxParams params =new AjaxParams();
		params.put("TelPhone",""+mUserInfo.TelPhone);
		params.put("PrjID",""+deviceInfo.PrjID);
		params.put("WXID","0");
		params.put("loginCode",mUserInfo.TelPhone + "," + mUserInfo.loginCode);
		params.put("phoneSystem", "Android");
		params.put("version", MyApp.versionCode);
		//Log.d("WaterDeviceListActivity", "params:" + params);
		new FinalHttp().get(Common.BASE_URL + "bingding", params, new AjaxCallBack<Object>() {
			@Override
			public void onSuccess(Object result) {
				super.onSuccess(result);
				isFrist =-1;
				PublicGetData publicGetData = new Gson().fromJson(result.toString(), PublicGetData.class);
				if (publicGetData.error_code.equals("0")) {
					UserInfo mInfo = new Gson().fromJson(publicGetData.data, UserInfo.class);
					mInfo.loginCode = mUserInfo.loginCode;
					SharedPreferences.Editor editor = sp.edit();
					switch (capture_type){
						case 4:
							editor.putString(Common.USER_BRATHE + Common.getUserPhone(sp), new Gson().toJson(deviceInfo));
							editor.putString(Common.USER_PHONE_NUM, mInfo.TelPhone + "");
							editor.putString(Common.USER_INFO, new Gson().toJson(mInfo));
							editor.commit();
							break;
						case 6:
							editor.putString(Common.USER_WASHING + Common.getUserPhone(sp), new Gson().toJson(deviceInfo));
							editor.putString(Common.USER_PHONE_NUM, mInfo.TelPhone + "");
							editor.putString(Common.USER_INFO, new Gson().toJson(mInfo));
							editor.commit();
							break;
						default:
							editor.putString(Common.USER_PHONE_NUM, mInfo.TelPhone + "");
							editor.putString(Common.USER_INFO, new Gson().toJson(mInfo));
							editor.commit();
							break;
					}
					//成功绑定后，去开始使用
					skipDecive(deviceInfo);
				}else {
					Common.showToast(WaterDeviceListActivity.this, R.string.bind_fail, Gravity.CENTER);
				}

			}

			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				super.onFailure(t, errorNo, strMsg);
				toast(strMsg);
			}
		});
	}
	private void showNoDevice() {
		dialogBuilder.withTitle(getString(R.string.tips))
				.withMessage(getString(R.string.no_search_device))
				.withEffect(Effectstype.Fadein).isCancelable(false)
				.withButton1Text(getString(R.string.cancel))
				.setButton1Click(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						dialogBuilder.dismiss();
						finish();
					}
				}).withButton2Text(getString(R.string.search))
				.setButton2Click(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						startSearch();
						dialogBuilder.dismiss();
					}
				}).show();
	}


	private String tag ="water";
	OkHttpClient client =new OkHttpClient.Builder()
			.connectTimeout(10, TimeUnit.SECONDS)
			.writeTimeout(10, TimeUnit.SECONDS)
			.readTimeout(30, TimeUnit.SECONDS)
			.build();
	protected void getMACInfo(final String address) {

		if (!Common.isNetWorkConnected(WaterDeviceListActivity.this)){
			Common.showNoNetworkDailog(dialogBuilder,WaterDeviceListActivity.this);
			return;
		}


		RequestBody requestBody=new FormBody.Builder()
				.add("PrjID",""+ mUserInfo.PrjID)
				.add("deviceID_List","0")
				.add("deviceMac_List",address)
				.add("loginCode",mUserInfo.TelPhone+","+mUserInfo.loginCode)
				.add("phoneSystem", "Android")
				.add("version",MyApp.versionCode)
				.build();
		Request request =new Request.Builder()
				.url(Common.BASE_URL + "deviceInfo")
				.post(requestBody)
				.tag(tag)
				.build();
		client.newCall(request).enqueue(new Callback() {
			@Override
			public void onFailure(Call call, IOException e) {
				call.cancel();
			}

			@Override
			public void onResponse(Call call, Response response) throws IOException {
				String result = response.body().string();
				call.cancel();
				final PublicArrayData publicArrayData = new Gson().fromJson(result, PublicArrayData.class);
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if (publicArrayData.error_code.equals("0")) {

							Type listType = new TypeToken<ArrayList<DeviceInfo>>() {}.getType();
							ArrayList<DeviceInfo> deviceInfos = new Gson().fromJson(publicArrayData.data, listType);

							if (deviceInfos != null && deviceInfos.size() > 0) {

								// 这里表明 在后台获取到该mac地址的设备信息。所以这是一台已经登记过的设备。
								DeviceInfo deviceInfo = deviceInfos.get(0);
								if (capture_type ==254 ){ //管理员
									if (deviceInfo !=null ){
										if (!mBluetoothName.contains(address)) {
											mHandler.removeMessages(LINK_OUT_TIME);
											mBluetoothDevices.add(deviceInfo);
											mBluetoothName.add(address);
											waterDeviceAdapter.changeData(mBluetoothDevices);
											progressbar_layout.setVisibility(View.GONE);


										}
									}
								}else { //非管理员

									if (deviceInfo != null && deviceInfo.Dsbtypeid == capture_type) {
										if (mUserInfo.PrjID>0){
											if (deviceInfo.PrjID==mUserInfo.PrjID){
												if (!mBluetoothName.contains(address)) {
													mHandler.removeMessages(LINK_OUT_TIME);
													mBluetoothDevices.add(deviceInfo);
													mBluetoothName.add(address);
													waterDeviceAdapter.changeData(mBluetoothDevices);
													progressbar_layout.setVisibility(View.GONE);


												}
											}
										}else {
											if (!mBluetoothName.contains(address)) {
												mHandler.removeMessages(LINK_OUT_TIME);
												mBluetoothDevices.add(deviceInfo);
												mBluetoothName.add(address);
												waterDeviceAdapter.changeData(mBluetoothDevices);
												progressbar_layout.setVisibility(View.GONE);


											}
										}


									}
								}
							}

						} else if (publicArrayData.error_code.equals("7")) {
							mHandler.removeMessages(LINK_OUT_TIME);
							Common.logout2(WaterDeviceListActivity.this, sp,dialogBuilder,publicArrayData.message);
						}
					}
				});

			}
		});

	}
	/**
	 * 用于用户没有项目时的绑定
	 */
	private void bindDecive(final DeviceInfo deviceInfo, final String address){
		AjaxParams ajaxParams = new AjaxParams();
		ajaxParams.put("TelPhone", sp.getString(Common.USER_PHONE_NUM, ""));
		ajaxParams.put("PrjID", "" + deviceInfo.PrjID);
		ajaxParams.put("WXID", "0");
		ajaxParams.put("loginCode", mUserInfo.TelPhone + "," + mUserInfo.loginCode);
		ajaxParams.put("phoneSystem", "Android");
		ajaxParams.put("version", MyApp.versionCode);

		new FinalHttp().get(Common.BASE_URL + "bingding", ajaxParams, new AjaxCallBack<Object>() {

			@Override
			public void onSuccess(Object t) {
				super.onSuccess(t);
				String result = t.toString();

				PublicGetData publicGetData = new Gson().fromJson(result, PublicGetData.class);
				if (publicGetData.error_code.equals("0")) {
					UserInfo mInfo = new Gson().fromJson(publicGetData.data, UserInfo.class);
					mInfo.loginCode = mUserInfo.loginCode;
					SharedPreferences.Editor editor = sp.edit();
					//更新用户信息
					editor.putString(Common.USER_INFO, new Gson().toJson(mInfo));
					editor.commit();
				

				} else if (publicGetData.error_code.equals("7")) {
					Common.logout(WaterDeviceListActivity.this, sp, dialogBuilder);
				} else {
					Common.showToast(WaterDeviceListActivity.this, R.string.bind_fail, Gravity.CENTER);
				}
			}
			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				super.onFailure(t, errorNo, strMsg);

				Common.showToast(WaterDeviceListActivity.this, R.string.bind_fail, Gravity.CENTER);
			}
		});
	}

	/**
	 * 取消所有的网络请求
	 */
	private void cancleAll(){
		Dispatcher dispatcher = client.dispatcher();
		synchronized (dispatcher){
			for (Call call : dispatcher.queuedCalls()) {
				if (tag.equals(call.request().tag())) {
					call.cancel();
				}
			}
			for (Call call : dispatcher.runningCalls()) {
				if (tag.equals(call.request().tag())) {
					call.cancel();
				}
			}
		}


	}
	
}
