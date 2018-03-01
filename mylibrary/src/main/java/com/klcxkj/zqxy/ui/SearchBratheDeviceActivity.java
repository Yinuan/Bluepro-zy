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
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.klcxkj.zqxy.MyApp;
import com.klcxkj.mylibrary.R;
import com.klcxkj.zqxy.adapter.BratheDeviceAdapter;
import com.klcxkj.zqxy.common.Common;
import com.klcxkj.zqxy.databean.DeviceInfo;
import com.klcxkj.zqxy.databean.UserInfo;
import com.klcxkj.zqxy.response.PublicArrayData;
import com.klcxkj.zqxy.response.PublicGetData;
import com.klcxkj.zqxy.utils.GlobalTools;
import com.klcxkj.zqxy.widget.DiffuseView;
import com.klcxkj.zqxy.widget.Effectstype;

import net.android.tools.afinal.FinalHttp;
import net.android.tools.afinal.http.AjaxCallBack;
import net.android.tools.afinal.http.AjaxParams;

import org.greenrobot.eventbus.EventBus;

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
 * author : yinjuan
 * time： 2017/10/17 13:54
 * email：yin.juan2016@outlook.com
 * Description:用户的 搜索水表和洗衣机
 */
public class SearchBratheDeviceActivity extends BaseActivity {

	public static final int BIND_USER_START = 998;
	// public static final int BIND_USER_FINISH = 999;

	private TextView search_state_txt;
	private TextView searching_device_number_txt;

	private LinearLayout device_layout;
	private ListView device_listview;
	private TextView close_txt;

	private BluetoothAdapter mBtAdapter;

	private BratheDeviceAdapter bratheDeviceAdapter;
	private ArrayList<DeviceInfo> mBluetoothDevices;
	private ArrayList<String> mBluetoothName;

	private DiffuseView mDiffuseView;
	private View click_view;
	private TextView look_device_txt;

	private SharedPreferences sp;


	private int prjid = 0;
	private UserInfo userInfo;

	private TextView bluetooth_school_txt;

	private static final int LINK_OUT_TIME = 997;
    private int capture_type; //区分洗衣机和热水表



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_searchdevice);

		sp = getSharedPreferences("adminInfo", Context.MODE_PRIVATE);
		userInfo = Common.getUserInfo(sp);

		initView();

		mBtAdapter = BluetoothAdapter.getDefaultAdapter();
		if (mBtAdapter ==null){
			toast("此设备不支持蓝牙");

		}else {
			if (!mBtAdapter.isEnabled()){ //蓝牙未开启，默认给他打开
				mBtAdapter.enable();
			}
		}
		mBluetoothDevices = new ArrayList<DeviceInfo>();
		mBluetoothName = new ArrayList<String>();

		bratheDeviceAdapter = new BratheDeviceAdapter(this, mBluetoothDevices, myHandler, sp);
		searching_device_number_txt.setText(String.format(getResources()
				.getString(R.string.search_number_device2), mBluetoothDevices.size()));
		device_listview.setAdapter(bratheDeviceAdapter);

		// 设置广播信息过滤
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
		intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
		intentFilter.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
		intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);

		this.registerReceiver(receiver, intentFilter);

		startSearch();


	}

	private Handler myHandler = new Handler() {

		@Override
		public void handleMessage(final Message msg) {
			super.handleMessage(msg);
			if (msg.what == BIND_USER_START) {
				final DeviceInfo deviceInfo = (DeviceInfo) msg.obj;
				dialogBuilder.withTitle(getString(R.string.tips))
						.withMessage(getString(R.string.bind_tips))
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
								bindSchool(deviceInfo);

								dialogBuilder.dismiss();
							}
						}).show();

			}
		}

	};

	private void initView() {

		showMenu("搜索设备");
		bluetooth_school_txt = (TextView) findViewById(R.id.bluetooth_school_txt);
		if (userInfo != null && !TextUtils.isEmpty(userInfo.PrjName)) {
			bluetooth_school_txt.setText(userInfo.PrjName);
		} else {
			bluetooth_school_txt.setVisibility(View.GONE);
		}

		mDiffuseView = (DiffuseView) findViewById(R.id.diffuseView);
		click_view = (View) findViewById(R.id.click_empty_view);
		search_state_txt = (TextView) findViewById(R.id.search_state_txt);
		searching_device_number_txt = (TextView) findViewById(R.id.searching_device_number_txt);
		device_layout = (LinearLayout) findViewById(R.id.device_layout);
		device_listview = (ListView) findViewById(R.id.device_listview);
		close_txt = (TextView) findViewById(R.id.close_txt);

		device_layout.setVisibility(View.GONE);

		look_device_txt = (TextView) findViewById(R.id.look_device_txt);
		look_device_txt.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (look_device_txt.getText().toString().equals(getString(R.string.look_device))) {
					showListView();

				}

			}
		});

		close_txt.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				closeListView();

			}
		});

		click_view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (search_state_txt.getText().toString().equals(getString(R.string.start_searching))) {
					startSearch();
				} else if (search_state_txt.getText().toString().equals(getString(R.string.stop_searching))) {
					stopSearch();
				}else if (search_state_txt.getText().toString().equals(getString(R.string.bluetooth_search))){
					showNoBluetoothDailog();
				}

			}
		});

		Intent intent =getIntent();
        capture_type = intent.getExtras().getInt("capture_type");

	}

	private void bindSchool(final DeviceInfo deviceInfo) {
		if (Common.isNetWorkConnected(SearchBratheDeviceActivity.this)) {
			if (userInfo != null && userInfo.PrjID != 0) { //绑定了项目
                Editor editor = sp.edit();
                Intent intent =new Intent();
                Bundle bundle =new Bundle();
				EventBus.getDefault().postSticky("bind_new_decive");
                switch (capture_type){	//更改绑定的设备信息
                    case 4:
                        editor.putString(Common.USER_BRATHE + Common.getUserPhone(sp), new Gson().toJson(deviceInfo));
                        editor.commit();
                       	intent.setClass(SearchBratheDeviceActivity.this,Bath2Activity.class);
                        bundle.putSerializable("DeviceInfo",deviceInfo);
                        intent.putExtras(bundle);
                        startActivity(intent);
                        break;
                    case 6:
                        editor.putString(Common.USER_WASHING + Common.getUserPhone(sp), new Gson().toJson(deviceInfo));
                        editor.commit();
                       	intent.setClass(SearchBratheDeviceActivity.this,WashingActivity.class);
                        bundle.putSerializable("DeviceInfo",deviceInfo);
                        intent.putExtras(bundle);
                        startActivity(intent);
                        break;
                }
				finish();
			} else {
				AjaxParams ajaxParams = new AjaxParams();
				ajaxParams.put("TelPhone", sp.getString(Common.USER_PHONE_NUM, ""));
				ajaxParams.put("PrjID", "" + deviceInfo.PrjID);
				ajaxParams.put("WXID", "0");
				ajaxParams.put("loginCode", userInfo.TelPhone + "," + userInfo.loginCode);
				ajaxParams.put("phoneSystem", "Android");
				ajaxParams.put("version", MyApp.versionCode);
				Log.d("SearchBratheDeviceActiv", "ajaxParams:" + ajaxParams);
				loadingDialogProgress= GlobalTools.getInstance().showDailog(SearchBratheDeviceActivity.this,"绑定中.");
				new FinalHttp().get(Common.BASE_URL + "bingding", ajaxParams, new AjaxCallBack<Object>() {
					@Override
					public void onSuccess(Object t) {
						super.onSuccess(t);
						String result = t.toString();
						if (loadingDialogProgress !=null){
							loadingDialogProgress.dismiss();
						}
						PublicGetData publicGetData = new Gson().fromJson(result, PublicGetData.class);
						if (publicGetData.error_code.equals("0")) {
                            UserInfo mInfo = new Gson().fromJson(publicGetData.data, UserInfo.class);
                            mInfo.loginCode = userInfo.loginCode;
                            Editor editor = sp.edit();
                            Intent intent = new Intent();
                            Bundle bundle =new Bundle();
							EventBus.getDefault().postSticky("bind_new_decive");
                            //将设备信息保存起来
                            //跳转
                            switch (capture_type){
                                case 4:
                                    editor.putString(Common.USER_BRATHE + Common.getUserPhone(sp), new Gson().toJson(deviceInfo));
                                    editor.putString(Common.USER_PHONE_NUM, mInfo.TelPhone + "");
                                    editor.putString(Common.USER_INFO, new Gson().toJson(mInfo));
                                    editor.commit();
                                    intent.setClass(SearchBratheDeviceActivity.this, Bath2Activity.class);
                                    bundle.putSerializable("DeviceInfo",deviceInfo);
                                    intent.putExtras(bundle);
                                    startActivity(intent);
                                    break;
                                case 6:
                                    editor.putString(Common.USER_WASHING + Common.getUserPhone(sp), new Gson().toJson(deviceInfo));
                                    editor.putString(Common.USER_PHONE_NUM, mInfo.TelPhone + "");
                                    editor.putString(Common.USER_INFO, new Gson().toJson(mInfo));
                                    editor.commit();
                                    intent.setClass(SearchBratheDeviceActivity.this, WashingActivity.class);
                                    bundle.putSerializable("DeviceInfo",deviceInfo);
                                    intent.putExtras(bundle);
                                    startActivity(intent);
                                    break;
                            }



							finish();

						} else if (publicGetData.error_code.equals("7")) {
							Common.logout(SearchBratheDeviceActivity.this, sp, dialogBuilder);
						} else {
							Common.showToast(SearchBratheDeviceActivity.this, R.string.bind_fail, Gravity.CENTER);
						}
					}
					@Override
					public void onFailure(Throwable t, int errorNo, String strMsg) {
						super.onFailure(t, errorNo, strMsg);
						if (loadingDialogProgress !=null){
							loadingDialogProgress.dismiss();
						}
						Common.showToast(SearchBratheDeviceActivity.this, R.string.bind_fail, Gravity.CENTER);
					}
				});
			}
			} else{
				Common.showNoNetworkDailog(dialogBuilder, SearchBratheDeviceActivity.this);
		}
	}

	private final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == LINK_OUT_TIME) {
				if (mBluetoothDevices == null || mBluetoothDevices.size() == 0) {
					stopSearch();
					showNoDevice();
				}
			}
		}
	};

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

	private void startSearch() {

		search_state_txt.setText(R.string.stop_searching);
		look_device_txt.setText(R.string.look_device);
		mDiffuseView.start();
		mBluetoothDevices.clear();
		bratheDeviceAdapter.changeData(mBluetoothDevices);
		mBluetoothName.clear();
		searching_device_number_txt.setText(String.format(getResources()
				.getString(R.string.search_number_device2), mBluetoothDevices.size()));
		doDiscovery();
		mHandler.removeMessages(LINK_OUT_TIME);
		mHandler.sendEmptyMessageDelayed(LINK_OUT_TIME, 60000);

	}

	private void stopSearch() {
		search_state_txt.setText(R.string.start_searching);
		mDiffuseView.stop();
		notDiscovery();
	}

	private void doDiscovery() {
		if (mBtAdapter.isDiscovering()) {
			mBtAdapter.cancelDiscovery();
		}
		mBtAdapter.startDiscovery();
	}

	private void notDiscovery() {
		mBtAdapter.cancelDiscovery();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mHandler.removeMessages(LINK_OUT_TIME);
		search_state_txt.setText(R.string.start_searching);
		mDiffuseView.stop();
		if (mBtAdapter != null) {
			mBtAdapter.cancelDiscovery();
		}
		// Unregister broadcast listeners
		this.unregisterReceiver(receiver);
		cancleAll();
	}

	private void showListView() {
		if (device_layout.getVisibility() != View.VISIBLE) {
			device_layout.setVisibility(View.VISIBLE);
			Animation inAnimation = AnimationUtils.loadAnimation(
					SearchBratheDeviceActivity.this,
					R.anim.slide_in_from_bottom);
			// 使用ImageView显示动画
			// spaceshipImage.startAnimation(hyperspaceJumpAnimation);
			device_layout.setAnimation(inAnimation);

			inAnimation.start();
		}
	}

	private void closeListView() {
		Animation outAnimation = AnimationUtils.loadAnimation(
				SearchBratheDeviceActivity.this, R.anim.slide_out_to_bottom);
		// 使用ImageView显示动画
		// spaceshipImage.startAnimation(hyperspaceJumpAnimation);
		device_layout.setAnimation(outAnimation);

		outAnimation.start();
		device_layout.setVisibility(View.GONE);
	}

	private BroadcastReceiver receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (BluetoothDevice.ACTION_FOUND.equals(action)) {
				// 获取查找到的蓝牙设备
				// device_layout.setVisibility(View.VISIBLE);
				BluetoothDevice bluetoothDevice = intent
						.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

				BluetoothDevice mDevice = mBtAdapter
						.getRemoteDevice(bluetoothDevice.getAddress());

				if (mDevice != null && mDevice.getAddress() != null) {

					// mBtAdapter.cancelDiscovery();
					try {
						String address = mDevice.getAddress();

						String name = mDevice.getName();


						if (name!=null && name.startsWith("KLCXKJ")){

							if (!mBluetoothName.contains(address) ) {
								//getMACInfo(address);
								queryDeciveByMac(address);
							}
						}

					} catch (Exception e) {
						// TODO: handle exception
					}

				}

			} else if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
				int blueState = intent.getIntExtra(
						BluetoothAdapter.EXTRA_STATE, 0);
				switch (blueState) {
					case BluetoothAdapter.STATE_TURNING_ON:
						break;
					case BluetoothAdapter.STATE_ON:
						startSearch();
						break;
					case BluetoothAdapter.STATE_TURNING_OFF:
						break;
					case BluetoothAdapter.STATE_OFF:
						stopSearch();
						search_state_txt.setText(R.string.bluetooth_search);
						searching_device_number_txt.setText(R.string.bluetooth_state_disconnect);
						look_device_txt.setText(R.string.no_shuibiao1);
						break;
				}
			}
		}
	};

	protected void getMACInfo(final String address) {

		if (Common.isNetWorkConnected(SearchBratheDeviceActivity.this)) {
			AjaxParams ajaxParams = new AjaxParams();
			ajaxParams.put("PrjID", "" + userInfo.PrjID);
			ajaxParams.put("deviceID_List", "0");
			ajaxParams.put("deviceMac_List", address);
			ajaxParams.put("loginCode", userInfo.TelPhone + "," + userInfo.loginCode);
			ajaxParams.put("phoneSystem", "Android");
			ajaxParams.put("version", MyApp.versionCode);
			new FinalHttp().get(Common.BASE_URL + "deviceInfo", ajaxParams,
					new AjaxCallBack<Object>() {

						@Override
						public void onSuccess(Object t) {
							super.onSuccess(t);
							String result = t.toString();
							PublicArrayData publicArrayData = new Gson().fromJson(result, PublicArrayData.class);
							if (publicArrayData.error_code.equals("0")) {
								Type listType = new TypeToken<ArrayList<DeviceInfo>>() {}.getType();
								ArrayList<DeviceInfo> deviceInfos = new Gson().fromJson(publicArrayData.data, listType);
								if (deviceInfos != null && deviceInfos.size() > 0) {
									// 这里表明 在后台获取到该mac地址的设备信息。所以这是一台已经登记过的设备。
									DeviceInfo deviceInfo = deviceInfos.get(0);
                                    if (deviceInfo !=null && deviceInfo.PrjID !=0){
										if (deviceInfo.Dsbtypeid ==capture_type){

											if (!mBluetoothName.contains(address)) {
												mHandler.removeMessages(LINK_OUT_TIME);
												mBluetoothDevices.add(deviceInfo);
												mBluetoothName.add(address);
												switch (capture_type){
													case 4:
														searching_device_number_txt.setText(String.format(getResources()
																.getString(R.string.search_number_device), mBluetoothDevices.size()));

														break;
													case 6:
														searching_device_number_txt.setText(String.format(getResources()
																.getString(R.string.search_number_device3), mBluetoothDevices.size()));

														break;
												}
												bratheDeviceAdapter.changeData(mBluetoothDevices);
												showListView();

											}
										}

									}

								} else {
									// 这里表明 在后台没有获取到该mac地址的设备信息。所以这个是一台新设备。
								}

							} else if (publicArrayData.error_code.equals("7")) {
								Common.logout(SearchBratheDeviceActivity.this, sp,dialogBuilder);
							}

						}

						@Override
						public void onFailure(Throwable t, int errorNo, String strMsg) {
							super.onFailure(t, errorNo, strMsg);
						}
					});
		} else {
			// Common.showNoNetworkDailog(dialogBuilder,SearchBratheDeviceActivity.this);
		}

	}

	private String tag ="water_user";
	OkHttpClient client =new OkHttpClient.Builder()
			.connectTimeout(10, TimeUnit.SECONDS)
			.writeTimeout(10, TimeUnit.SECONDS)
			.readTimeout(30, TimeUnit.SECONDS)
			.build();
	private void queryDeciveByMac(final String address){
		if (!Common.isNetWorkConnected(SearchBratheDeviceActivity.this)){
			Common.showNoNetworkDailog(dialogBuilder,SearchBratheDeviceActivity.this);
			return;
		}

		RequestBody requestBody =new FormBody.Builder()
				.add("PrjID",""+ userInfo.PrjID)
				.add("deviceID_List","0")
				.add("deviceMac_List",address)
				.add("loginCode",userInfo.TelPhone + "," + userInfo.loginCode)
				.add("phoneSystem", "Android")
				.add("version",MyApp.versionCode)
				.build();
		Request request =new Request.Builder()
				.url(Common.BASE_URL + "deviceInfo")
				.post(requestBody)
				.tag(tag)
				.build();
		Log.d("SearchBratheDeviceActiv", "userInfo.PrjID:" + userInfo.PrjID);
		client.newCall(request).enqueue(new Callback() {
			@Override
			public void onFailure(Call call, IOException e) {
				call.cancel();
			}

			@Override
			public void onResponse(Call call, Response response) throws IOException {
				final String result =response.body().string();
				call.cancel();
				Log.d("SearchBratheDeviceActiv", result);
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						PublicArrayData publicArrayData =null;
						try {
							publicArrayData = new Gson().fromJson(result, PublicArrayData.class);
							if (publicArrayData==null){
								toast("服务器错误,json数据格式不对");
								return;
							}
						}catch (JsonSyntaxException e){
							toast("服务器错误,json数据格式不对");
							return;
						}

						if (publicArrayData.error_code.equals("0")) {
							Type listType = new TypeToken<ArrayList<DeviceInfo>>() {}.getType();
							ArrayList<DeviceInfo> deviceInfos = new Gson().fromJson(publicArrayData.data, listType);
							if (deviceInfos != null && deviceInfos.size() > 0) {
								// 这里表明 在后台获取到该mac地址的设备信息。所以这是一台已经登记过的设备。
								DeviceInfo deviceInfo = deviceInfos.get(0);
								if (deviceInfo !=null && deviceInfo.PrjID !=0){
									if (deviceInfo.Dsbtypeid ==capture_type){
										if (userInfo.PrjID >0){
											if (deviceInfo.PrjID !=userInfo.PrjID){
												return;
											}
										}
										if (!mBluetoothName.contains(address)) {
											mHandler.removeMessages(LINK_OUT_TIME);
											mBluetoothDevices.add(deviceInfo);
											mBluetoothName.add(address);
											bratheDeviceAdapter.changeData(mBluetoothDevices);
											switch (capture_type){
												case 4:
													searching_device_number_txt.setText(String.format(getResources()
															.getString(R.string.search_number_device), mBluetoothDevices.size()));

													break;
												case 6:
													searching_device_number_txt.setText(String.format(getResources()
															.getString(R.string.search_number_device3), mBluetoothDevices.size()));

													break;

											}
											showListView();




										}
									}

								}

							} else {
								// 这里表明 在后台没有获取到该mac地址的设备信息。所以这个是一台新设备。
							}

						} else if (publicArrayData.error_code.equals("7")) {
							Common.logout2(SearchBratheDeviceActivity.this, sp,dialogBuilder,publicArrayData.message);
						}
					}
				});

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
