package com.klcxkj.zqxy.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.klcxkj.zqxy.MyApp;
import com.klcxkj.mylibrary.R;
import com.klcxkj.zqxy.adapter.RepairPopAdapter;
import com.klcxkj.zqxy.common.Common;
import com.klcxkj.zqxy.databean.DeciveType;
import com.klcxkj.zqxy.databean.DeciveTypeResult;
import com.klcxkj.zqxy.databean.UserInfo;
import com.klcxkj.zqxy.response.PublicGetData;
import com.klcxkj.zqxy.response.PublicLastRoomData;

import net.android.tools.afinal.FinalHttp;
import net.android.tools.afinal.http.AjaxCallBack;
import net.android.tools.afinal.http.AjaxParams;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RepairActivity extends BaseActivity {

	private TextView ctv1,ctv2;
	private ImageView chose1,chose2; //选择设备/故障现象文字
	private RadioGroup repair_reason_radiagroup;
	private EditText room_number_edit, repair_edit;
	private Button ok_btn;
	private RelativeLayout layout1,layout2; //设备/故障现在布局
	private UserInfo mUserInfo;
	private SharedPreferences sp;
	private String[] city ={"蓝牙连不上","设备损坏"};
	private PopupWindow popupWindow;
	private PopupWindow popupWindow2;
	private String[] item1 ={"水表不出水","水表连接不上","水表损坏"};
	private String[] item2 ={"水管没水","洗衣机连接不上","洗衣机不工作"};
	private String[] item3 ={"吹风机损坏","吹风机连接不上"};
	private String[] item4 ={"饮水机不出水","饮水机连接不上","饮水机损坏"};
	private String[] item5 ={"充电桩不通电","充电桩连接不上","充电桩损坏"};
	private String[] item6 ={"空调连接不上","空调不工作"};
	private List<DeciveType> mData;
	private int position1;
	private int position2;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_repair);

		sp = getSharedPreferences("adminInfo", Context.MODE_PRIVATE);
		mUserInfo = Common.getUserInfo(sp);

		if (mUserInfo == null || mUserInfo.PrjID == 0) {
			return;
		}
		initView();

		getLastRoom(mUserInfo);
		loadTypeBig();//获取设备大类
	}

	private void initView() {
		showMenu("故障报修");

		repair_reason_radiagroup = (RadioGroup) findViewById(R.id.repair_reason_radiagroup);

		room_number_edit = (EditText) findViewById(R.id.room_number_edit);
		repair_edit = (EditText) findViewById(R.id.repair_edit);
		chose1 = (ImageView) findViewById(R.id.repair_chose1_img);
		chose2 = (ImageView) findViewById(R.id.repair_chose2_img);
		layout1 = (RelativeLayout) findViewById(R.id.repair_device_chose);
		layout2 = (RelativeLayout) findViewById(R.id.repair_style_chose);
		ctv1 = (TextView) findViewById(R.id.repair_chose1);
		ctv2 = (TextView) findViewById(R.id.repair_chose2);
		repair_edit.requestFocus();

		ok_btn = (Button) findViewById(R.id.ok_btn);

		layout1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {

				if (popupWindow2 !=null && popupWindow2.isShowing()){
					chose2.setImageResource(R.drawable.pull_down);
					popupWindow2.dismiss();
				}
				if (popupWindow !=null){
					if (popupWindow.isShowing()){
						popupWindow.dismiss();
						chose1.setImageResource(R.drawable.pull_down);
					}else {
						showPop1();
					}
				}else {
					showPop1();
				}

			}
		});
		layout2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				if (ctv1.getText()!=null && ctv1.getText().toString().length()>0){
					if (popupWindow2 !=null && popupWindow2.isShowing()){
						chose2.setImageResource(R.drawable.pull_down);
						popupWindow2.dismiss();
					}else {
						showPop2();
					}
				}else {
					toast("请选择设备类型");
					return;
				}


			}
		});

		ok_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				String reasonString = null;
				reasonString =ctv2.getText().toString();
				if (TextUtils.isEmpty(reasonString)) {
					Common.showToast(RepairActivity.this, R.string.repair_reason_empty, Gravity.CENTER);
					return;
				}
				
				
				String repaircontent = repair_edit.getText().toString();
				if (TextUtils.isEmpty(repaircontent)) {
					Common.showToast(RepairActivity.this, R.string.repair_empty, Gravity.CENTER);
					return;
				}

				String repairroom = room_number_edit.getText().toString();
				
				if (TextUtils.isEmpty(repairroom)) {
					Common.showToast(RepairActivity.this, R.string.repair_room_empty, Gravity.CENTER);
					return;
				}

				postRepair(mUserInfo,reasonString,repaircontent,repairroom);
			}
		});
	}

	private void postRepair(UserInfo mInfo, String reasonString, String repaircontent, String repairroom) {

		if (Common.isNetWorkConnected(RepairActivity.this)) {
			
			startProgressDialog(RepairActivity.this);

			AjaxParams ajaxParams = new AjaxParams();
			ajaxParams.put("PrjID", mInfo.PrjID + "");
			ajaxParams.put("AccID", mInfo.AccID + "");
			ajaxParams.put("RepTitle", reasonString);
			ajaxParams.put("RepContent", repaircontent);
			ajaxParams.put("AreaName", repairroom);
			ajaxParams.put("DevName", mData.get(position1).getDevname());
			ajaxParams.put("DevID", ""+mData.get(position1).getTypeid());
			ajaxParams.put("loginCode",mInfo.TelPhone+","+mInfo.loginCode);
			ajaxParams.put("phoneSystem", "Android");
			ajaxParams.put("version", MyApp.versionCode);
			Log.d("RepairActivity", "ajaxParams:" + ajaxParams);
			new FinalHttp().get(Common.BASE_URL + "errdevreportAddInfo", ajaxParams,
					new AjaxCallBack<Object>() {

						@Override
						public void onSuccess(Object t) {
							super.onSuccess(t);
							String result = t.toString();
							stopProgressDialog();
							PublicGetData publicGetData = new Gson()
									.fromJson(result, PublicGetData.class);
							if (publicGetData.error_code.equals("0")) {
								Common.showToast(RepairActivity.this, R.string.repair_seccess, Gravity.BOTTOM);
								new Handler().postDelayed(new Runnable() {
									
									@Override
									public void run() {
										finish();
									}
								}, 2000);
								
							}else if (publicGetData.error_code.equals("7")){
								Common.logout(RepairActivity.this, sp,dialogBuilder);
							}

						}

						@Override
						public void onFailure(Throwable t, int errorNo,
								String strMsg) {
							super.onFailure(t, errorNo, strMsg);
							stopProgressDialog();
						}
					});

		} else {
			Common.showNoNetworkDailog(dialogBuilder, RepairActivity.this);
		}

	}

	private void getLastRoom(UserInfo mInfo) {

		if (Common.isNetWorkConnected(RepairActivity.this)) {

			AjaxParams ajaxParams = new AjaxParams();
			ajaxParams.put("PrjID", mInfo.PrjID + "");
			ajaxParams.put("AccID", mInfo.AccID + "");
			ajaxParams.put("loginCode", mInfo.TelPhone+","+mInfo.loginCode);
			ajaxParams.put("phoneSystem", "Android");
			ajaxParams.put("version", MyApp.versionCode);
			new FinalHttp().get(Common.BASE_URL + "lastXFAreaInfo", ajaxParams,
					new AjaxCallBack<Object>() {

						@Override
						public void onSuccess(Object t) {
							super.onSuccess(t);
							String result = t.toString();
							stopProgressDialog();
							PublicLastRoomData publicLastRoomData = new Gson().fromJson(result, PublicLastRoomData.class);
							if (publicLastRoomData.error_code.equals("0")) {
								room_number_edit.setText(publicLastRoomData.AreaName);
							}else if (publicLastRoomData.error_code.equals("7")){
								Common.logout(RepairActivity.this, sp,dialogBuilder);
							}

						}

						@Override
						public void onFailure(Throwable t, int errorNo,
								String strMsg) {
							super.onFailure(t, errorNo, strMsg);
							stopProgressDialog();
						}
					});

		} else {
			Common.showNoNetworkDailog(dialogBuilder, RepairActivity.this);
		}

	}
	/**
	 * 获取设备的大类信息
	 *
	 */
	private void loadTypeBig(){
		AjaxParams params =new AjaxParams();
		params.put("PrjID",""+mUserInfo.PrjID);
		params.put("loginCode",mUserInfo.TelPhone+","+mUserInfo.loginCode);
		params.put("phoneSystem", "Android");
		params.put("version", MyApp.versionCode);
		new FinalHttp().get(Common.BASE_URL + "typeQryinfo", params, new AjaxCallBack<Object>() {
			@Override
			public void onSuccess(Object result) {
				super.onSuccess(result);
				DeciveTypeResult typeResult =new Gson().fromJson(result.toString(),DeciveTypeResult.class);
				if (typeResult.getData() !=null && typeResult.getData().size()>0){
					mData =typeResult.getData();
				}

			}

			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				super.onFailure(t, errorNo, strMsg);
				toast(strMsg);
			}
		});
	}

	/**
	 * 获取屏幕宽度
	 * @return
	 */
	private int getWidth(){
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		int widthPixels = metrics.widthPixels;
		int width =(widthPixels*3)/4;
		return  width;
	}


	private void showPop1() {
		final List<String> strList =new ArrayList<>();
		for (int i = 0; i <mData.size() ; i++) {
			strList.add(mData.get(i).getDevname());
		}
		View view1 = LayoutInflater.from(RepairActivity.this).inflate(R.layout.pop_repair_style, null);
		ListView listView = (ListView) view1.findViewById(R.id.pop_list);

		RepairPopAdapter adapter =new RepairPopAdapter(RepairActivity.this);
		adapter.setList(strList);
		listView.setAdapter(adapter);
		if (popupWindow ==null){
			popupWindow = new PopupWindow(view1, ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
		}
		ColorDrawable cd = new ColorDrawable(0x7f0d0071);
		popupWindow.setBackgroundDrawable(cd);
		WindowManager.LayoutParams lp=getWindow().getAttributes();
		lp.alpha = Common.COLORSET;
		getWindow().setAttributes(lp);
		popupWindow.setFocusable(false);// 取得焦点
		//注意  要是点击外部空白处弹框消息  那么必须给弹框设置一个背景色  不然是不起作用的
		// 设置允许在外点击消失
		popupWindow.setOutsideTouchable(false);
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		//点击外部消失
		//  popupWindow.setOutsideTouchable(true);
		//设置可以点击
		popupWindow.setTouchable(true);
		// 设置背景，这个是为了点击“返回Back”也能使其消失，并且并不会影响你的背景
		// 软键盘不会挡着popupwindow
		popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
		//popupWindow.showAtLocation(view1, Gravity.CENTER, 0, 0);
		popupWindow.showAsDropDown(layout1);
		chose1.setImageResource(R.drawable.pull_up);
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
				position1=position;
				ctv1.setText(strList.get(position));
				chose1.setImageResource(R.drawable.pull_down);
				ctv1.setTextColor(getResources().getColor(R.color.text_color));
				ctv2.setText("");
				popupWindow.dismiss();

			}
		});
		// 监听菜单的关闭事件
		popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
			@Override
			public void onDismiss() {

			}
		});
		// 监听触屏事件
		popupWindow.setTouchInterceptor(new View.OnTouchListener() {
			public boolean onTouch(View view, MotionEvent event) {
				return false;
			}
		});
		popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {

			//在dismiss中恢复透明度
			public void onDismiss() {
				WindowManager.LayoutParams lp = getWindow().getAttributes();
				lp.alpha = 1f;
				getWindow().setAttributes(lp);
			}
		});
	}

	private void showPop2() {
		View view1 = LayoutInflater.from(RepairActivity.this).inflate(R.layout.pop_repair_style, null);
		ListView listView = (ListView) view1.findViewById(R.id.pop_list);

		final RepairPopAdapter adapter =new RepairPopAdapter(RepairActivity.this);
		Log.d("RepairActivity", ctv1.getText().toString());
		if (ctv1.getText().toString().equals("热水表")){
			adapter.setList(Arrays.asList(item1));
		}else if (ctv1.getText().toString().equals("开水器")){
			adapter.setList(Arrays.asList(item4));
		}else if (ctv1.getText().toString().equals("洗衣机")){
			adapter.setList(Arrays.asList(item2));
		}else if (ctv1.getText().toString().equals("吹风机")){
			adapter.setList(Arrays.asList(item3));
		}else if (ctv1.getText().toString().equals("充电")){
			adapter.setList(Arrays.asList(item5));
		}else if (ctv1.getText().toString().equals("空调")){
			adapter.setList(Arrays.asList(item6));
		}else {
			adapter.setList(Arrays.asList(city));
		}

		listView.setAdapter(adapter);
		popupWindow2 = new PopupWindow(view1, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

		ColorDrawable cd = new ColorDrawable(0x7f0d0071);
		popupWindow2.setBackgroundDrawable(cd);
		WindowManager.LayoutParams lp=getWindow().getAttributes();
		lp.alpha = Common.COLORSET;
		getWindow().setAttributes(lp);
		popupWindow2.setFocusable(false);// 取得焦点
		//注意  要是点击外部空白处弹框消息  那么必须给弹框设置一个背景色  不然是不起作用的
		// 设置允许在外点击消失
		popupWindow2.setOutsideTouchable(false);
		popupWindow2.setBackgroundDrawable(new BitmapDrawable());
		//点击外部消失
		//  popupWindow.setOutsideTouchable(true);
		//设置可以点击
		popupWindow2.setTouchable(true);
		// 设置背景，这个是为了点击“返回Back”也能使其消失，并且并不会影响你的背景
		// 软键盘不会挡着popupwindow
		popupWindow2.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
		//popupWindow.showAtLocation(view1, Gravity.CENTER, 0, 0);
		popupWindow2.showAsDropDown(layout2);
		chose2.setImageResource(R.drawable.pull_up);
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
				position2=position;
				String str =adapter.getItem(position);

				Log.d("RepairActivity", str);
				chose2.setImageResource(R.drawable.pull_down);
				ctv2.setText(str);
				ctv2.setTextColor(getResources().getColor(R.color.text_color));
				popupWindow2.dismiss();

			}
		});
		// 监听菜单的关闭事件
		popupWindow2.setOnDismissListener(new PopupWindow.OnDismissListener() {
			@Override
			public void onDismiss() {

			}
		});
		// 监听触屏事件
		popupWindow2.setTouchInterceptor(new View.OnTouchListener() {
			public boolean onTouch(View view, MotionEvent event) {
				return false;
			}
		});
		popupWindow2.setOnDismissListener(new PopupWindow.OnDismissListener() {

			//在dismiss中恢复透明度
			public void onDismiss() {
				WindowManager.LayoutParams lp = getWindow().getAttributes();
				lp.alpha = 1f;
				getWindow().setAttributes(lp);
			}
		});
	}
}
