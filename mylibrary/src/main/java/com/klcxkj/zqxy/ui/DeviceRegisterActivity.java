package com.klcxkj.zqxy.ui;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jooronjar.BluetoothService;
import com.example.jooronjar.utils.AnalyAdminTools;
import com.example.jooronjar.utils.CMDUtils;
import com.example.jooronjar.utils.OnWaterAdminListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.klcxkj.zqxy.MyApp;
import com.klcxkj.mylibrary.R;
import com.klcxkj.zqxy.adapter.SpinerAreaAdapter;
import com.klcxkj.zqxy.adapter.SpinerCategory2Adapter;
import com.klcxkj.zqxy.adapter.SpinerCategoryAdapter;
import com.klcxkj.zqxy.adapter.SpinerProjectAdapter;
import com.klcxkj.zqxy.common.Common;
import com.klcxkj.zqxy.databean.AreaInfo;
import com.klcxkj.zqxy.databean.DeciveInfo2;
import com.klcxkj.zqxy.databean.DeciveType;
import com.klcxkj.zqxy.databean.DeciveTypeResult;
import com.klcxkj.zqxy.databean.DeciveTypeResult2;
import com.klcxkj.zqxy.databean.DeviceInfo;
import com.klcxkj.zqxy.databean.ProjectInfo;
import com.klcxkj.zqxy.databean.UserInfo;
import com.klcxkj.zqxy.databean.WashingOrderBean;
import com.klcxkj.zqxy.response.PublicAreaData;
import com.klcxkj.zqxy.response.PublicArrayData;
import com.klcxkj.zqxy.response.PublicGetDataDev;
import com.klcxkj.zqxy.response.PublicGetDataDev2;
import com.klcxkj.zqxy.widget.Effectstype;

import net.android.tools.afinal.FinalHttp;
import net.android.tools.afinal.http.AjaxCallBack;
import net.android.tools.afinal.http.AjaxParams;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * author : yinjuan
 * time： 2017/10/17 13:54
 * email：yin.juan2016@outlook.com
 * Description:设备登记
 */
public class DeviceRegisterActivity extends BaseActivity implements OnWaterAdminListener{



	private LinearLayout content_layout;
	private LinearLayout spiner_layout;
	private TextView spiner_title_txt;
	private ListView spiner_listview;

	private TextView my_mac_txt, project_name_txt, device_category_txt,
			select_area_txt, select_building_txt, select_floor_txt,
			select_room_txt;

	private RelativeLayout project_name_layout, device_category_layout,
			select_area_layout, select_building_layout, select_floor_layout,
			select_room_layout;

	private View empty_view;
	private Button device_register_btn;

	private SpinerProjectAdapter spinerProjectAdapter;
	private SpinerAreaAdapter spinerAreaAdapter;
	private SpinerCategoryAdapter spinerCategoryAdapter;
	private SpinerCategory2Adapter spinerCategoryAdapter2;

	//设备类型的数据
	private List<DeciveType> projectcategoryArrayList ; //大类
	private List<DeciveInfo2> projectcategoryList ;//小类
	private String category = "";//设备小类的ID

	private DeviceInfo deviceInfo;

	private SharedPreferences sp;
	private UserInfo userInfo;

	private BluetoothService mbtService = null;
	private BluetoothAdapter bluetoothAdapter;

	private int fail_content_count = 0;

	private int type_big;  //设备选择的大类
	private int type_li;  //设备的小类

	private int devFailed =1 ;//设备后台登记失败返回-1
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_deviceregister);
		AnalyAdminTools.setWaterAdminListener(this);
		deviceInfo = (DeviceInfo) getIntent().getExtras().get("device_data");
		if (deviceInfo == null) {
			return;
		}
		bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		mbtService = BluetoothService.sharedManager();
		mbtService.setHandlerContext(mHandler);
		sp = getSharedPreferences("adminInfo", Context.MODE_PRIVATE);
		userInfo = Common.getUserInfo(sp);
		initView();
		IntentFilter statusFilter = new IntentFilter();
		statusFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);// 状态改变
		registerReceiver(mStatusReceive, statusFilter);
		if (!TextUtils.isEmpty(deviceInfo.DevName)){
			if (!deviceInfo.DevName.equals("新设备")){
				//更改设备的操作

			}
		}

	}

	@Override
	protected void onDestroy() {
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
		dialogBuilder=null;
	}

	private void initView() {
		showMenu("设备登记");
		my_mac_txt = (TextView) findViewById(R.id.my_mac_txt);
		project_name_txt = (TextView) findViewById(R.id.project_name_txt);
		device_category_txt = (TextView) findViewById(R.id.device_category_txt);
		select_area_txt = (TextView) findViewById(R.id.select_area_txt);
		select_building_txt = (TextView) findViewById(R.id.select_building_txt);
		select_floor_txt = (TextView) findViewById(R.id.select_floor_txt);
		select_room_txt = (TextView) findViewById(R.id.select_room_txt);

		device_register_btn = (Button) findViewById(R.id.device_register_btn);
		empty_view = (View) findViewById(R.id.empty_view);

		project_name_layout = (RelativeLayout) findViewById(R.id.project_name_layout);
		device_category_layout = (RelativeLayout) findViewById(R.id.device_category_layout);
		select_area_layout = (RelativeLayout) findViewById(R.id.select_area_layout);
		select_building_layout = (RelativeLayout) findViewById(R.id.select_building_layout);
		select_floor_layout = (RelativeLayout) findViewById(R.id.select_floor_layout);
		select_room_layout = (RelativeLayout) findViewById(R.id.select_room_layout);

		project_name_layout.setOnClickListener(onClickListener);
		device_category_layout.setOnClickListener(onClickListener);
		select_area_layout.setOnClickListener(onClickListener);
		select_building_layout.setOnClickListener(onClickListener);
		select_floor_layout.setOnClickListener(onClickListener);
		select_room_layout.setOnClickListener(onClickListener);

		empty_view.setOnClickListener(onClickListener);

		// my_mac_txt.setOnClickListener(onClickListener);
		device_register_btn.setOnClickListener(onClickListener);

		content_layout = (LinearLayout) findViewById(R.id.content_layout);
		spiner_layout = (LinearLayout) findViewById(R.id.spiner_layout);
		spiner_listview = (ListView) findViewById(R.id.spiner_listview);
		spiner_title_txt = (TextView) findViewById(R.id.spiner_title_txt);

		content_layout.setVisibility(View.VISIBLE);
		spiner_layout.setVisibility(View.GONE);

		if (deviceInfo.DevTypeID!=0){
			category =deviceInfo.DevTypeID+"";
			type_li=deviceInfo.DevTypeID;
		}
		if (deviceInfo.Dsbtypeid !=0){
			type_big =deviceInfo.Dsbtypeid;
		}
		if (!TextUtils.isEmpty(deviceInfo.devMac)) {
			my_mac_txt.setText(deviceInfo.devMac);
		}

	if (!TextUtils.isEmpty(deviceInfo.DevTypeName)) {
		device_category_txt.setText(deviceInfo.DevTypeName);
		}

		if (!TextUtils.isEmpty(deviceInfo.PrjName)) {
			project_name_txt.setTag(deviceInfo.PrjID);
			project_name_txt.setText(deviceInfo.PrjName);
		}

		if (!TextUtils.isEmpty(deviceInfo.QUName)) {
			select_area_txt.setTag(deviceInfo.QUID);
			select_area_txt.setText(deviceInfo.QUName);
		}

		if (!TextUtils.isEmpty(deviceInfo.LDName)) {
			select_building_txt.setText(deviceInfo.LDName);
			select_building_txt.setTag(deviceInfo.LDID);
		}

		if (!TextUtils.isEmpty(deviceInfo.LCName)) {
			select_floor_txt.setText(deviceInfo.LCName);
			select_floor_txt.setTag(deviceInfo.LCID);
		}

		if (!TextUtils.isEmpty(deviceInfo.FJName)) {
			select_room_txt.setTag(deviceInfo.FJID);
			select_room_txt.setText(deviceInfo.FJName);
		}

		spiner_listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {

				if (arg0.getAdapter() instanceof SpinerProjectAdapter) {
					SpinerProjectAdapter spinerProjectAdapter = (SpinerProjectAdapter) arg0.getAdapter();
					ProjectInfo projectInfo = (ProjectInfo) spinerProjectAdapter.getItem(arg2);
					String old = project_name_txt.getText().toString();
					if (!old.equals(projectInfo.PrjName)) {
						project_name_txt.setTag(projectInfo.PrjID);
						project_name_txt.setText(projectInfo.PrjName);

						select_area_txt.setTag(0);
						select_area_txt.setText("");

						select_building_txt.setTag(0);
						select_building_txt.setText("");

						select_floor_txt.setTag(0);
						select_floor_txt.setText("");

						select_room_txt.setTag(0);
						select_room_txt.setText("");
					}

					closeList();
				} else if (arg0.getAdapter() instanceof SpinerAreaAdapter) {

					SpinerAreaAdapter spinerAreaAdapter = (SpinerAreaAdapter) arg0.getAdapter();
					AreaInfo areaInfo = (AreaInfo) spinerAreaAdapter.getItem(arg2);

					if (areaInfo.AreaMark == 1) {

						String old = select_area_txt.getText().toString();
						if (!old.equals(areaInfo.AreaName)) {
							select_area_txt.setTag(areaInfo.AreaID);
							select_area_txt.setText(areaInfo.AreaName);

							select_building_txt.setTag(0);
							select_building_txt.setText("");

							select_floor_txt.setTag(0);
							select_floor_txt.setText("");

							select_room_txt.setTag(0);
							select_room_txt.setText("");
						}

					} else if (areaInfo.AreaMark == 2) {

						String old = select_building_txt.getText().toString();
						if (!old.equals(areaInfo.AreaName)) {
							select_building_txt.setTag(areaInfo.AreaID);
							select_building_txt.setText(areaInfo.AreaName);

							select_floor_txt.setTag(0);
							select_floor_txt.setText("");

							select_room_txt.setTag(0);
							select_room_txt.setText("");
						}

					} else if (areaInfo.AreaMark == 3) {

						String old = select_floor_txt.getText().toString();
						if (!old.equals(areaInfo.AreaName)) {
							select_floor_txt.setTag(areaInfo.AreaID);
							select_floor_txt.setText(areaInfo.AreaName);

							select_room_txt.setTag(0);
							select_room_txt.setText("");
						}

					} else if (areaInfo.AreaMark == 4) {

						String old = select_room_txt.getText().toString();
						if (!old.equals(areaInfo.AreaName)) {
							select_room_txt.setTag(areaInfo.AreaID);
							select_room_txt.setText(areaInfo.AreaName);
						}

					}

					closeList();

				} else if (arg0.getAdapter() instanceof SpinerCategoryAdapter) {  //选择大类
					closeList();
					getTypeByType(arg2);
					type_big =projectcategoryArrayList.get(arg2).getTypeid();
				//	Log.d("DeviceRegisterActivity", "type_big:" + type_big);

				}else if (arg0.getAdapter() instanceof SpinerCategory2Adapter){ //选择小类
					SpinerCategory2Adapter spinerCategoryAdapter = (SpinerCategory2Adapter) arg0.getAdapter();
					DeciveInfo2 info2 = (DeciveInfo2) spinerCategoryAdapter.getItem(arg2);
					String categoryStr = info2.getTypename2();
					category =info2.getDevtypeid()+"";
					//Log.d("DeviceRegisterActivity","type_little:"+ category);
					device_category_txt.setText(categoryStr);
					closeList();

				}
			}
		});
	}

	private OnClickListener onClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			int i = v.getId();
			if (i == R.id.empty_view) {
				closeList();


			} else if (i == R.id.project_name_layout) {
				project_name_layout.setEnabled(false);
				getproject();

			} else if (i == R.id.device_category_layout) {
				if (project_name_txt.getTag() == null) {
					Common.showToast(DeviceRegisterActivity.this,
							R.string.select_project_first, Gravity.CENTER);
					return;
				}
				getprojectcategory();
				device_category_layout.setEnabled(false);

			} else if (i == R.id.select_area_layout) {
				if (project_name_txt.getTag() == null) {
					Common.showToast(DeviceRegisterActivity.this,
							R.string.select_project_first, Gravity.CENTER);
					return;
				}
				final int prjid = (Integer) project_name_txt.getTag();
				if (prjid == 0) {
					Common.showToast(DeviceRegisterActivity.this,
							R.string.select_project_first, Gravity.CENTER);
					return;
				}
				if (TextUtils.isEmpty(project_name_txt.getText().toString())) {
					Common.showToast(DeviceRegisterActivity.this,
							R.string.select_project_first, Gravity.CENTER);
					return;
				}
				select_area_layout.setEnabled(false);
				getAreaInfo(0);

			} else if (i == R.id.select_building_layout) {
				if (project_name_txt.getTag() == null) {
					Common.showToast(DeviceRegisterActivity.this,
							R.string.select_project_first, Gravity.CENTER);
					return;
				}
				final int prjid = (Integer) project_name_txt.getTag();
				if (prjid == 0) {
					Common.showToast(DeviceRegisterActivity.this,
							R.string.select_project_first, Gravity.CENTER);
					return;
				}
				if (TextUtils.isEmpty(project_name_txt.getText().toString())) {
					Common.showToast(DeviceRegisterActivity.this,
							R.string.select_project_first, Gravity.CENTER);
					return;
				}
				if (TextUtils.isEmpty(select_area_txt.getText().toString())) {
					Common.showToast(DeviceRegisterActivity.this,
							R.string.select_area_first, Gravity.CENTER);
					return;
				}
				select_building_layout.setEnabled(false);
				int areaid = (Integer) select_area_txt.getTag();
				getAreaInfo(areaid);

			} else if (i == R.id.select_floor_layout) {
				if (project_name_txt.getTag() == null) {
					Common.showToast(DeviceRegisterActivity.this,
							R.string.select_project_first, Gravity.CENTER);
					return;
				}
				final int prjid = (Integer) project_name_txt.getTag();
				if (prjid == 0) {
					Common.showToast(DeviceRegisterActivity.this,
							R.string.select_project_first, Gravity.CENTER);
					return;
				}
				if (TextUtils.isEmpty(project_name_txt.getText().toString())) {
					Common.showToast(DeviceRegisterActivity.this,
							R.string.select_project_first, Gravity.CENTER);
					return;
				}
				if (TextUtils.isEmpty(select_area_txt.getText().toString())) {
					Common.showToast(DeviceRegisterActivity.this,
							R.string.select_area_first, Gravity.CENTER);
					return;
				}
				if (TextUtils.isEmpty(select_building_txt.getText().toString())) {
					Common.showToast(DeviceRegisterActivity.this,
							R.string.select_ld_first, Gravity.CENTER);
					return;
				}
				int areaid = (Integer) select_building_txt.getTag();
				getAreaInfo(areaid);

			} else if (i == R.id.select_room_layout) {
				if (project_name_txt.getTag() == null) {
					Common.showToast(DeviceRegisterActivity.this,
							R.string.select_project_first, Gravity.CENTER);
					return;
				}
				final int prjid = (Integer) project_name_txt.getTag();
				if (prjid == 0) {
					Common.showToast(DeviceRegisterActivity.this,
							R.string.select_project_first, Gravity.CENTER);
					return;
				}
				if (TextUtils.isEmpty(project_name_txt.getText().toString())) {
					Common.showToast(DeviceRegisterActivity.this,
							R.string.select_project_first, Gravity.CENTER);
					return;
				}
				if (TextUtils.isEmpty(select_area_txt.getText().toString())) {
					Common.showToast(DeviceRegisterActivity.this,
							R.string.select_area_first, Gravity.CENTER);
					return;
				}
				if (TextUtils.isEmpty(select_building_txt.getText().toString())) {
					Common.showToast(DeviceRegisterActivity.this,
							R.string.select_ld_first, Gravity.CENTER);
					return;
				}
				if (TextUtils.isEmpty(select_floor_txt.getText().toString())) {
					Common.showToast(DeviceRegisterActivity.this,
							R.string.select_lc_first, Gravity.CENTER);
					return;
				}
				int areaid = (Integer) select_floor_txt.getTag();
				select_room_layout.setEnabled(false);
				getAreaInfo(areaid);

			} else if (i == R.id.device_register_btn) {
				fail_content_count = 0;
				deviceregister();
				devFailed = 1;

			} else {
			}
		}
	};

	/**
	 * 获取项目区域
	 * @param areaid
	 */
	protected void getAreaInfo(final int areaid) {

		if (Common.isNetWorkConnected(DeviceRegisterActivity.this)) {
			int prjid = (Integer) project_name_txt.getTag();
			if (prjid == 0) {
				Common.showToast(this, R.string.select_project_first, Gravity.CENTER);
				return;
			}

			startProgressDialog(this);
			AjaxParams ajaxParams = new AjaxParams();
			ajaxParams.put("PrjID", "" + prjid);
			ajaxParams.put("AreaID", "" + areaid);
			ajaxParams.put("loginCode", userInfo.TelPhone + "," + userInfo.loginCode);
			ajaxParams.put("phoneSystem", "Android");
			ajaxParams.put("version", MyApp.versionCode);
			//Log.d("DeviceRegisterActivity", "ajaxParams:" + ajaxParams);
			new FinalHttp().get(Common.BASE_URL + "areainfo", ajaxParams,
					new AjaxCallBack<Object>() {

						@Override
						public void onSuccess(Object t) {
							super.onSuccess(t);
							stopProgressDialog();
							select_area_layout.setEnabled(true);
							select_building_layout.setEnabled(true);
							select_room_layout.setEnabled(true);
							String result = t.toString();
							PublicAreaData publicGetData = new Gson().fromJson(result, PublicAreaData.class);
							if (publicGetData.error_code.equals("0")) {

								Type listType = new TypeToken<ArrayList<AreaInfo>>() {}.getType();
								ArrayList<AreaInfo> areaInfos = new Gson().fromJson(publicGetData.arlist, listType);

								if (spinerAreaAdapter == null) {
									spinerAreaAdapter = new SpinerAreaAdapter(DeviceRegisterActivity.this, areaInfos);

								} else {
									spinerAreaAdapter.changeData(areaInfos);
								}
								spiner_listview.setAdapter(spinerAreaAdapter);
								switch (areaid) {
								case 0:
									spiner_title_txt.setText(R.string.select_area2);
									break;
								case 1:
									spiner_title_txt.setText(R.string.select_building2);
									break;
								case 2:
									spiner_title_txt.setText(R.string.select_floor2);
									break;
								case 3:
									spiner_title_txt.setText(R.string.select_room2);
									break;

								default:
									break;
								}

								spiner_layout.setVisibility(View.VISIBLE);
								Animation inAnimation = AnimationUtils.loadAnimation(DeviceRegisterActivity.this, R.anim.slide_in_from_bottom);
								spiner_layout.setAnimation(inAnimation);
								inAnimation.start();

							} else if (publicGetData.error_code.equals("7")) {
								Common.logout(DeviceRegisterActivity.this, sp,dialogBuilder);
							} else {
								Common.showToast(DeviceRegisterActivity.this, R.string.get_data_fail2, Gravity.CENTER);
							}

						}

						@Override
						public void onFailure(Throwable t, int errorNo, String strMsg) {
							stopProgressDialog();
							select_area_layout.setEnabled(true);
							select_building_layout.setEnabled(true);
							select_room_layout.setEnabled(true);
							Common.showToast(DeviceRegisterActivity.this, R.string.get_data_fail, Gravity.CENTER);
							super.onFailure(t, errorNo, strMsg);
						}
					});
		} else {
			Common.showNoNetworkDailog(dialogBuilder, DeviceRegisterActivity.this);
		}

	}

	/**
	 * 设备注册
	 */
	private String DevName;
	private int prjid;
	protected void deviceregister() {
		if (project_name_txt.getTag() == null) {
			Common.showToast(this, R.string.select_project_first, Gravity.CENTER);
			return;
		}
		 prjid = (Integer) project_name_txt.getTag();
		if (prjid == 0) {
			Common.showToast(this, R.string.select_project_first, Gravity.CENTER);
			return;
		}

		if (TextUtils.isEmpty(project_name_txt.getText().toString())) {
			Common.showToast(this, R.string.select_project_first,
					Gravity.CENTER);
			return;
		}

		if (TextUtils.isEmpty(device_category_txt.getText().toString())) {
			Common.showToast(this, R.string.select_category_first, Gravity.CENTER);
			return;
		}

		if (TextUtils.isEmpty(select_area_txt.getText().toString())) {
			Common.showToast(this, R.string.select_area_first,
					Gravity.CENTER);
			return;
		}

		if (TextUtils.isEmpty(select_building_txt.getText().toString())) {
			Common.showToast(this, R.string.select_ld_first, Gravity.CENTER);
			return;
		}

		if (TextUtils.isEmpty(select_floor_txt.getText().toString())) {
			Common.showToast(this, R.string.select_lc_first, Gravity.CENTER);
			return;
		}

		if (TextUtils.isEmpty(select_room_txt.getText().toString())) {
			Common.showToast(this, R.string.select_fj_first, Gravity.CENTER);
			return;
		}

		 DevName =device_category_txt.getText().toString()+"-"+
				select_building_txt.getText().toString() + "-"
				+ select_floor_txt.getText().toString() + "-"
				+ select_room_txt.getText().toString();



		deciveExit=-1;
	//	Log.d("DeviceRegisterActivity", "category:" + category);
		if (category.equals("1")){  //是水表
			deciveIsExit();//判断设备是否存在
		}else {
			if (type_big==4){ //新增的设备小类!=1的时候
				deciveIsExit();//判断设备是否存在
			}else {
				buletoothConnect();//蓝牙操作
			}

		}

	}

	private void buletoothConnect(){

		startProgressDialog(this);
		//调用sdk设置设备的类型
		if (mbtService.getState() == BluetoothService.STATE_CONNECTED){
			CMDUtils.chaxueshebei(mbtService,true);
		}else {
			//1 蓝牙连接
			//Log.d("DeviceRegisterActivity", "蓝牙未链接");
			if (!TextUtils.isEmpty(deviceInfo.devMac)) {
				if (deviceInfo.devMac.contains(":")) {
					mbtService.connect(bluetoothAdapter.getRemoteDevice(deviceInfo.devMac));
				} else {
					mbtService.connect(bluetoothAdapter.getRemoteDevice(Common.getMacMode(deviceInfo.devMac)));
				}
				//mbtService.connect(bluetoothAdapter.getRemoteDevice(deviceInfo.devMac));
			}
		}
	}

	/**
	 * 注册新增的设备
	 */
	private void deciveRegister() {

		if (Common.isNetWorkConnected(DeviceRegisterActivity.this)) {
			//新增设备
				AjaxParams ajaxParams = new AjaxParams();
				ajaxParams.put("PrjID", "" + prjid);
				ajaxParams.put("OPID", "" + userInfo.AccID);
				if (deviceInfo.DevID > 0) { //是否有设备ID
					if (deviceInfo.PrjID != prjid) {  //项目不一致
						// 表明是新增
						ajaxParams.put("DeviceID", "" + 0);
					} else {
						// 表明是修改
						ajaxParams.put("DeviceID", "" + deviceInfo.DevID);
						ajaxParams.put("DevID", "" + deviceInfo.DevID);
					}
				} else {
					// 表明是新增
					ajaxParams.put("DeviceID", "" + 0);
				}
				ajaxParams.put("DeviceMac", deviceInfo.devMac);
				ajaxParams.put("DevName", DevName);
				ajaxParams.put("DevDescript", DevName);
				ajaxParams.put("DevTypeID",  category);
				int areaid = (Integer) select_room_txt.getTag();
				ajaxParams.put("AreaID", "" + areaid);
				ajaxParams.put("IsUse", "" + 1);
				ajaxParams.put("loginCode", userInfo.TelPhone + "," + userInfo.loginCode);
				ajaxParams.put("phoneSystem", "Android");
				ajaxParams.put("version", MyApp.versionCode);
			//	Log.d("DeviceRegisterActivity", "ajaxParams:" + ajaxParams);
				new FinalHttp().get(Common.BASE_URL + "addMeter", ajaxParams,
						new AjaxCallBack<Object>() {
							@Override
							public void onSuccess(Object t) {
								super.onSuccess(t);

								String result = t.toString();
							//	Log.d("--------", "登记设备："+result);
								PublicGetDataDev publicGetData = new Gson().fromJson(result, PublicGetDataDev.class);
								if (publicGetData.error_code.equals("2") || publicGetData.error_code.equals("0")) {
									//成功
									if (publicGetData.data !=null){
										DevId=	publicGetData.data.DevID;
									}else {
										DevId=	deviceInfo.DevID;
									}

									//清除设备
									qingchushebei();
								} else if (publicGetData.error_code.equals("-1")) {
									stopProgressDialog();
									Common.showToast(DeviceRegisterActivity.this, R.string.device_regeister_fail, Gravity.CENTER);


								} else if (publicGetData.error_code.equals("7")) {
									stopProgressDialog();
									Common.logout2(DeviceRegisterActivity.this, sp,dialogBuilder,publicGetData.message);
								} else {
									stopProgressDialog();
									Common.showToast(DeviceRegisterActivity.this,
											publicGetData.message, Gravity.CENTER);
								}
							}
							@Override
							public void onFailure(Throwable t, int errorNo, String strMsg) {
								super.onFailure(t, errorNo, strMsg);
								stopProgressDialog();
								Common.showToast(DeviceRegisterActivity.this, R.string.device_regeister_fail, Gravity.CENTER);

							}
						});


	} else {
		Common.showNoNetworkDailog(dialogBuilder, DeviceRegisterActivity.this);
	}

}

	/**
	 * 替换设备
	 */
	private void deciveUpdate(){
		AjaxParams params =new AjaxParams();
		int areaid = (Integer) select_room_txt.getTag();
		params.put("AreaID",""+areaid);
	//	params.put("DevID",""+deviceInfo.DevID);
		params.put("DevID",category);
		params.put("DevName",DevName);
		params.put("PrjID",""+prjid);
		params.put("newMac",""+deviceInfo.devMac);
		params.put("loginCode",userInfo.TelPhone+","+userInfo.loginCode);
		params.put("phoneSystem", "Android");
		params.put("version", MyApp.versionCode);

		//Log.d("DeviceRegisterActivity", "params:" + params);
		new FinalHttp().get(Common.BASE_URL + "devupdate", params, new AjaxCallBack<Object>() {
			@Override
			public void onSuccess(Object result) {
				super.onSuccess(result);
				//Log.d("DeviceRegisterActivity", result.toString());
				PublicGetDataDev2 publicGetData = new Gson().fromJson(result.toString(), PublicGetDataDev2.class);

				if (publicGetData.error_code.equals("2") || publicGetData.error_code.equals("0")) {
					//成功
					Common.showToast(DeviceRegisterActivity.this, R.string.device_regeister_success2, Gravity.CENTER);
					//
					DevId =publicGetData.DevID;
					qingchushebei();
				} else if (publicGetData.error_code.equals("-1")) {

					Common.showToast(DeviceRegisterActivity.this, "设备更改失败", Gravity.CENTER);
					stopProgressDialog();

				} else if (publicGetData.error_code.equals("7")) {
					Common.logout2(DeviceRegisterActivity.this, sp,dialogBuilder,publicGetData.message);
					stopProgressDialog();
				} else {
					Common.showToast(DeviceRegisterActivity.this, publicGetData.message, Gravity.CENTER);
					stopProgressDialog();
				}

			}

			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				super.onFailure(t, errorNo, strMsg);
				Common.showToast(DeviceRegisterActivity.this, "设备更改失败,设备已经初始化", Gravity.CENTER);
				stopProgressDialog();

			}
		});
	}

	/**
	 * 判断房间是否存在水表
	 */
	private int deciveExit =-1;
	private void deciveIsExit(){
		 //是水表，查询房间内是否存在水表
			AjaxParams params =new AjaxParams();
			int areaid = (Integer) select_room_txt.getTag();
			params.put("AreaId",""+areaid);
			params.put("PrjID",""+prjid);
			params.put("DevType",category);
			params.put("loginCode",userInfo.TelPhone+","+userInfo.loginCode);
			params.put("phoneSystem", "Android");
			params.put("version", MyApp.versionCode);
			Log.d("DeviceRegisterActivity", "params:" + params);
			new FinalHttp().get(Common.BASE_URL + "isDevbyAreaId", params, new AjaxCallBack<Object>() {
				@Override
				public void onSuccess(Object result) {
					super.onSuccess(result);
					//Log.d("DeviceRegisterActivity", "result:" + result);
					WashingOrderBean orderBean =new Gson().fromJson(result.toString(),WashingOrderBean.class);
					if (orderBean !=null){
						int code =Integer.parseInt(orderBean.getError_code());
						switch (code){
							case 0: //存在
								//提示是否更换
								showupdateDecive();
								break;
							case 1: //不存在
								//蓝牙操作
								buletoothConnect();
								break;
							case 2: //异常
								toast(orderBean.getMessage());
								stopProgressDialog();
								break;

						}
					}
				}

				@Override
				public void onFailure(Throwable t, int errorNo, String strMsg) {
					super.onFailure(t, errorNo, strMsg);
					toast(strMsg);
					stopProgressDialog();
				}
			});

	}

	/**
	 * 获取设备的大类
	 */
	protected void getprojectcategory() {
		AjaxParams params =new AjaxParams();
		String prjIds =project_name_txt.getTag().toString();
		Log.d("DeviceRegisterActivity", "项目id:"+prjIds);
		params.put("PrjID",prjIds);
		params.put("loginCode",userInfo.TelPhone+","+userInfo.loginCode);
		params.put("phoneSystem", "Android");
		params.put("version", MyApp.versionCode);
		startProgressDialog(this);

		new FinalHttp().get(Common.BASE_URL + "typeQryinfo", params, new AjaxCallBack<Object>() {
			@Override
			public void onSuccess(Object result) {
				super.onSuccess(result);
				device_category_layout.setEnabled(true);
				stopProgressDialog();
				//Log.d("DeviceRegisterActivity", "result:" + result);
				DeciveTypeResult typeResult =new Gson().fromJson(result.toString(),DeciveTypeResult.class);
				if (typeResult.getError_code().equals("0")){
					projectcategoryArrayList =typeResult.getData();
					if (projectcategoryArrayList !=null && projectcategoryArrayList.size()>0){
						if (spinerCategoryAdapter == null) {
							spinerCategoryAdapter = new SpinerCategoryAdapter(
									DeviceRegisterActivity.this, projectcategoryArrayList);
						} else {
							spinerCategoryAdapter.changeData(projectcategoryArrayList);
						}
					}


					spiner_listview.setAdapter(spinerCategoryAdapter);
					spiner_title_txt.setText(R.string.select_device_category);

					spiner_layout.setVisibility(View.VISIBLE);
					Animation inAnimation = AnimationUtils.loadAnimation(
							DeviceRegisterActivity.this, R.anim.slide_in_from_bottom);
					spiner_layout.setAnimation(inAnimation);
					inAnimation.start();

				}else {
					//查询失败了

					Toast.makeText(DeviceRegisterActivity.this, typeResult.getMessage(), Toast.LENGTH_SHORT).show();
				}
			}

			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				super.onFailure(t, errorNo, strMsg);
				device_category_layout.setEnabled(true);
				stopProgressDialog();
			}
		});


	}

	/**
	 * 获取设备的小类
	 * @param numt
	 */
	private void getTypeByType(int numt) {
		startProgressDialog(this);
		AjaxParams params =new AjaxParams();
		String prjIds =project_name_txt.getTag().toString();
		params.put("PrjID",prjIds);
		params.put("typeID",projectcategoryArrayList.get(numt).getTypeid()+"");
		params.put("loginCode",userInfo.TelPhone+","+userInfo.loginCode);
		params.put("phoneSystem", "Android");
		params.put("version", MyApp.versionCode);
		//Log.d("DeviceRegisterActivity", "params:" + params);
		new FinalHttp().get(Common.BASE_URL + "stypeQryinfo", params, new AjaxCallBack<Object>() {
			@Override
			public void onSuccess(Object result) {
				super.onSuccess(result);
			//	Log.d("----------", "result:" + result);
				stopProgressDialog();
				DeciveTypeResult2 typeResult =new Gson().fromJson(result.toString(),DeciveTypeResult2.class);
				projectcategoryList =typeResult.getData();
				if (spinerCategoryAdapter2 ==null){
					spinerCategoryAdapter2 =new SpinerCategory2Adapter(DeviceRegisterActivity.this,projectcategoryList);
				}else {
					spinerCategoryAdapter2.changeData(projectcategoryList);
				}
				spiner_listview.setAdapter(spinerCategoryAdapter2);
				spiner_title_txt.setText(R.string.select_device_category);

				spiner_layout.setVisibility(View.VISIBLE);
				Animation inAnimation = AnimationUtils.loadAnimation(
						DeviceRegisterActivity.this, R.anim.slide_in_from_bottom);
				spiner_layout.setAnimation(inAnimation);
				inAnimation.start();

			}

			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				super.onFailure(t, errorNo, strMsg);
				stopProgressDialog();
			}
		});
	}

	/**
	 * 获取项目名称
	 */
	protected void getproject() {

		if (Common.isNetWorkConnected(DeviceRegisterActivity.this)) {

			UserInfo userInfo = Common.getUserInfo(sp);

			if (userInfo == null) {
				return;
			}
			startProgressDialog(this);

			AjaxParams ajaxParams = new AjaxParams();
			ajaxParams.put("OPID", "" + userInfo.AccID);
			ajaxParams.put("loginCode", userInfo.TelPhone + "," + userInfo.loginCode);
			ajaxParams.put("phoneSystem", "Android");
			ajaxParams.put("version", MyApp.versionCode);
			//Log.d("DeviceRegisterActivity", "ajaxParams:" + ajaxParams);
			new FinalHttp().get(Common.BASE_URL + "prjlist", ajaxParams,
					new AjaxCallBack<Object>() {

						@Override
						public void onSuccess(Object t) {
							super.onSuccess(t);
							stopProgressDialog();
							project_name_layout.setEnabled(true);
							String result = t.toString();
							//Log.d("DeviceRegisterActivity", result);
							PublicArrayData publicGetData = new Gson().fromJson(result, PublicArrayData.class);
							if (publicGetData.error_code.equals("0")) {

								Type listType = new TypeToken<ArrayList<ProjectInfo>>() {}.getType();
								ArrayList<ProjectInfo> projectInfos = new Gson().fromJson(publicGetData.data, listType);

								if (spinerProjectAdapter == null) {
									spinerProjectAdapter = new SpinerProjectAdapter(
											DeviceRegisterActivity.this, projectInfos);

								} else {
									spinerProjectAdapter.changeData(projectInfos);
								}
								spiner_listview.setAdapter(spinerProjectAdapter);
								spiner_title_txt.setText(R.string.select_project_name);

								spiner_layout.setVisibility(View.VISIBLE);
								Animation inAnimation = AnimationUtils.loadAnimation(
										DeviceRegisterActivity.this, R.anim.slide_in_from_bottom);
								spiner_layout.setAnimation(inAnimation);
								inAnimation.start();

							} else if (publicGetData.error_code.equals("7")) {
								Common.logout(DeviceRegisterActivity.this, sp,dialogBuilder);
							} else {
								Common.showToast(DeviceRegisterActivity.this,
										R.string.get_data_fail2, Gravity.CENTER);
							}

						}

						@Override
						public void onFailure(Throwable t, int errorNo, String strMsg) {
							stopProgressDialog();
							project_name_layout.setEnabled(true);
							Common.showToast(DeviceRegisterActivity.this, R.string.get_data_fail, Gravity.CENTER);
							super.onFailure(t, errorNo, strMsg);
						}
					});
		} else {
			Common.showNoNetworkDailog(dialogBuilder,
					DeviceRegisterActivity.this);
		}

	}

	@Override
	public void onBackPressed() {

		if (spiner_layout.getVisibility() != View.GONE) {
			closeList();
		} else {
			super.onBackPressed();
		}

	}

	private void closeList() {
		Animation outAnimation = AnimationUtils.loadAnimation(DeviceRegisterActivity.this, R.anim.slide_out_to_bottom);
		// 使用ImageView显示动画
		// spaceshipImage.startAnimation(hyperspaceJumpAnimation);
		spiner_layout.setAnimation(outAnimation);
		outAnimation.start();
		spiner_layout.setVisibility(View.GONE);
	}

	private final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {

			case BluetoothService.MESSAGE_STATE_CHANGE:
				switch (msg.arg1) {
				case BluetoothService.STATE_CONNECTED: //连接成功
					fail_content_count = 0;
					CMDUtils.chaxueshebei(mbtService,true);
					break;

				case BluetoothService.STATE_CONNECTING:
					
					break;
				case BluetoothService.STATE_LISTEN:
					break;
				case BluetoothService.STATE_CONNECTION_LOST:
					break;
				case BluetoothService.STATE_CONNECTION_FAIL:
					//蓝牙连接两次操作
					stopProgressDialog();
					Common.showToast(DeviceRegisterActivity.this, R.string.xiafa_fail, Gravity.CENTER);

					
					break;
				case BluetoothService.STATE_NONE:

					break;

				}
				break;
			case BluetoothService.MESSAGE_WRITE:
				break;
			case BluetoothService.MESSAGE_READ:
				byte[] readBuf = (byte[]) msg.obj;
			//	String result = DigitalTrans.bytesToHexString(readBuf);
				//	processBluetoothDada(result);
				//从蓝牙水表返回的数据
				if (readBuf !=null){
					AnalyAdminTools.analyWaterDatas(readBuf);
				}
				break;
			}

		}
	};


	
	
	private BroadcastReceiver mStatusReceive = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {

			if (intent.getAction().equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)) {
				BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				switch (device.getBondState()) {
				case BluetoothDevice.BOND_BONDING:// 正在配对
					Log.e("water", "正在配对......");
					break;
				case BluetoothDevice.BOND_BONDED:// 配对结束
					Log.e("water", "完成配对");

					mbtService.connect(device);

					break;
				case BluetoothDevice.BOND_NONE:// 取消配对/未配对
					Log.e("water", "取消配对");

					stopProgressDialog();
					Common.showToast(DeviceRegisterActivity.this, R.string.xiafa_fail, Gravity.CENTER);

					break;
				default:
					break;
				}

			}

		}
	};

	@Override
	public void settingDeciveType(boolean b) {
		if (b){
			//Log.d("DeviceRegisterActivity", "设置类型成功");
			stopProgressDialog();
			Common.showToast(DeviceRegisterActivity.this, R.string.device_regeister_success, Gravity.CENTER);
			mHandler.postDelayed(new Runnable() {

				@Override
				public void run() {
					setResult(RESULT_OK);
					finish();
				}
			}, 1000);
		}else {
			//Log.d("DeviceRegisterActivity", "设备登记失败");
			Common.showToast(DeviceRegisterActivity.this, R.string.device_regeister_fail, Gravity.CENTER);
			stopProgressDialog();
		}
	}

	@Override
	public void qingchushebeiOnback(boolean b, int i, int i1) {
		if (devFailed ==1){
			if (b){
			//	Log.d("DeviceRegisterActivity", "清除项目成功");
				xiafaxiangmu();
			}else {
				stopProgressDialog();
				Common.showToast(DeviceRegisterActivity.this, R.string.device_regeister_fail, Gravity.CENTER);
			}
		}


	}

	@Override
	public void xiafaxiangmuOnback(boolean b, int i, int i1) {
		if (b){
			//Log.d("DeviceRegisterActivity", "下发项目成功");
            switch (typeSize){
                case 0:
                    toast("查询设备失败");
                    stopProgressDialog();
                    break;
                case 23:
					//设备登记成功
					Common.showToast(DeviceRegisterActivity.this, R.string.device_regeister_success, Gravity.CENTER);
					stopProgressDialog();
					mHandler.postDelayed(new Runnable() {

						@Override
						public void run() {
							setResult(RESULT_OK);
							finish();
						}
					}, 1000);
                    break;
                case 28:
                    if (TextUtils.isEmpty(category) || typeDecive==-1){
                        toast("请设置设备的类型");
                        return;
                    }
                    try {
					//	Log.d("DeviceRegisterActivity", "typeDecive:" + typeDecive);
					//	Log.d("DeviceRegisterActivity", category);
						CMDUtils.settingDecive(mbtService,1,typeDecive,Integer.parseInt(category),true);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
            }

		}else {
			stopProgressDialog();
			Common.showToast(DeviceRegisterActivity.this, R.string.device_regeister_fail, Gravity.CENTER);
		}
	}

	private int typeSize;  //查询的数据长度
    private int typeDecive=-1;//设备的类型
	@Override
	public void chaxueshebeiOnback(boolean b, int size,int macType) {

		if (b){
         //   Log.d("DeviceRegisterActivity", "size:" + size);
            typeSize=size;
			String str="";
			switch (type_big){ //大类转一下，小类可直接用
				case 4://老设备  热水表
                    typeDecive=0;
					break;
				case 5: //老设备 饮水机
                    typeDecive=1;
					break;
				case 6:
					str="洗衣机";
                    typeDecive=2;
					break;
				case 7:
					str="吹风机";
                    typeDecive=3;
					break;
				case 8:
					str="充电器";
                    typeDecive=4;
					break;
				case 9:
					str="空调";
                    typeDecive=5;
					break;
			}
          //  Log.d("DeviceRegisterActivity", "type:" + typeDecive);
          //  Log.d("DeviceRegisterActivity", "macType:" + macType);
          //  Log.d("DeviceRegisterActivity", str);
         if (typeSize ==28){

                if (macType==2){//洗衣机类
                    if (typeDecive==2){
                        //清除，下发项目
                       // CMDUtils.qingchushebei(mbtService,true);
						if (deciveExit==101){ //进入水表替换接口
							deciveUpdate();
						}else {
							deciveRegister();
						}
                    }else {
                        toast("此设备不能登记为非洗衣机类型!");
                        stopProgressDialog();
                        return;
                    }
                }else {  //非洗衣机类
                    if (typeDecive==2){
                        toast("此设备不能登记为洗衣机类型!");
                        stopProgressDialog();
                        return;
                    }else {
                      //  CMDUtils.qingchushebei(mbtService,true);
						if (deciveExit==101){ //进入水表替换接口
							deciveUpdate();
						}else {
							deciveRegister();
						}
                    }

                }

			}else if (typeSize==23){ //旧的

				if (typeDecive ==0  || typeDecive==1|| typeDecive==3){
					//清除，下发
                   // CMDUtils.qingchushebei(mbtService,true);
					if (deciveExit==101){ //进入水表替换接口
						deciveUpdate();
					}else {
						deciveRegister();
					}
				}else {
					toast("登记失败,此设备不能登记为"+str+",请重新登记!");
                    stopProgressDialog();
				}

			}
		}else {
			stopProgressDialog();
			Common.showToast(DeviceRegisterActivity.this, R.string.device_regeister_fail, Gravity.CENTER);
		}
	}

	private void qingchushebei() {
		CMDUtils.qingchushebei(mbtService, false);
	}

	private int DevId;
	private void xiafaxiangmu() {

		int prjid = (Integer) project_name_txt.getTag();
		if (prjid == 0) {
			Common.showToast(this, R.string.select_project_first, Gravity.CENTER);
			return;
		}
		if (DevId ==0){
			Common.showToast(DeviceRegisterActivity.this, "设备编号:NULL", Gravity.CENTER);
			return;
		}
		try {
			String retureString = CMDUtils.xiafaxiangmu(mbtService, DevId, prjid, true);
		} catch (IOException e1) {
			e1.printStackTrace();
			Common.showToast(DeviceRegisterActivity.this, "下发项目失败,请检查参数是否完整", Gravity.CENTER);
			stopProgressDialog();
		}

	}

	/**
	 * 提示是否更换设备
	 */
	private void showupdateDecive() {
		stopProgressDialog();
		if (dialogBuilder==null){
			return;
		}
		dialogBuilder.withTitle(getString(R.string.tips))
				.withMessage("该房间已登记,是否进行替换?")
				.withEffect(Effectstype.Fadein).isCancelable(false)
				.withButton1Text(getString(R.string.cancel))
				.setButton1Click(new OnClickListener() {
					@Override
					public void onClick(View v) {
						dialogBuilder.dismiss();
					}
				})
				.withButton2Text("替换")
				.setButton2Click(new OnClickListener() {
					@Override
					public void onClick(View v) {
						dialogBuilder.dismiss();
						startProgressDialog(DeviceRegisterActivity.this);
						deciveExit=101;
						buletoothConnect();
					}
				}).show();
	}


}
