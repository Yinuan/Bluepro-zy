package com.klcxkj.zqxy.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.klcxkj.mylibrary.R;
import com.klcxkj.zqxy.common.Common;
import com.klcxkj.zqxy.databean.DeviceInfo;
import com.klcxkj.zqxy.databean.UserInfo;
import com.klcxkj.zqxy.ui.Bath2Activity;
import com.klcxkj.zqxy.ui.SearchAdminDeviceActivity;
import com.klcxkj.zqxy.ui.WashingActivity;

import java.util.ArrayList;
import java.util.List;

public class AdminDeviceAdapter extends BaseAdapter {

	private LayoutInflater mInflator;
	private List<DeviceInfo> devices;
	private Context mContext;

	private Handler mHandler;
	private UserInfo mUserInfo;
	private SharedPreferences sp;

	public AdminDeviceAdapter(Context context, List<DeviceInfo> list,
			Handler handler, SharedPreferences sp) {
		super();
		mContext = context;
		mInflator = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		devices = list;
		mHandler = handler;
		sp=mContext.getSharedPreferences("adminInfo", Context.MODE_PRIVATE);
		mUserInfo = Common.getUserInfo(sp);
	}

	@Override
	public int getCount() {
		return devices.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return devices.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub

		return position;
	}

	@Override
	public View getView(int i, View view, ViewGroup viewGroup) {

		ViewHolder viewHolder;
		if (view == null) {
			view = mInflator.inflate(R.layout.scan_admin_device_item, viewGroup, false);
			viewHolder = new ViewHolder();

			viewHolder.name_txt = (TextView) view.findViewById(R.id.name_txt);

			viewHolder.mac_txt = (TextView) view.findViewById(R.id.mac_txt);

			viewHolder.bind_btn = (Button) view.findViewById(R.id.bind_btn);
			viewHolder.water_btn = (Button) view.findViewById(R.id.water_btn);
			viewHolder.layout =view.findViewById(R.id.admin_decive_layout);

			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}
		DeviceInfo deviceInfo = devices.get(i);
		//Log.d("AdminDeviceAdapter", "设备ID："+deviceInfo.PrjID + "--操作员的：" + mUserInfo.PrjID);
		/*if (deviceInfo.PrjID!=0){  //登记过的设备
			if (deviceInfo.PrjID !=mUserInfo.PrjID){
				viewHolder.layout.setBackgroundResource(R.color.help_button_view);
				viewHolder.bind_btn.setEnabled(false);
				viewHolder.water_btn.setEnabled(false);
			}else {
				viewHolder.layout.setBackgroundResource(R.color.white);
				viewHolder.bind_btn.setEnabled(true);
				viewHolder.water_btn.setEnabled(true);
			}
		}else { //新设备
			viewHolder.layout.setBackgroundResource(R.color.white);
			viewHolder.bind_btn.setEnabled(true);
			viewHolder.water_btn.setEnabled(true);
		}*/


		viewHolder.name_txt.setText(deviceInfo.DevName);

		viewHolder.mac_txt.setText("MAC：" + deviceInfo.devMac);

		if (deviceInfo.DevName.equals(mContext.getString(R.string.new_device))) {
			viewHolder.bind_btn.setBackgroundResource(R.drawable.dialog_btn4_selecter);
			viewHolder.bind_btn.setText(R.string.register);
			viewHolder.water_btn.setVisibility(View.GONE);
		} else {
			viewHolder.bind_btn.setBackgroundResource(R.drawable.dialog_btn3_selecter);
			viewHolder.bind_btn.setText(R.string.update);
			viewHolder.water_btn.setVisibility(View.VISIBLE);
		}

		viewHolder.bind_btn.setTag(deviceInfo);

		viewHolder.bind_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				DeviceInfo mDeviceInfo = (DeviceInfo) v.getTag();
				Message msg = new Message();
				msg.what = SearchAdminDeviceActivity.DVICE_REGISTER;
				msg.obj = mDeviceInfo;
				mHandler.sendMessage(msg);

			}
		});
		
		
		viewHolder.water_btn.setTag(deviceInfo);

		viewHolder.water_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				DeviceInfo mDeviceInfo = (DeviceInfo) v.getTag();
				Intent intent = new Intent();
				Bundle bundle =new Bundle();
				switch (mDeviceInfo.Dsbtypeid){
					case 4:
						intent.setClass(mContext, Bath2Activity.class);
						break;
					case 5:
						intent.setClass(mContext, Bath2Activity.class);
						break;
					case 6:
						intent.setClass(mContext, WashingActivity.class);
						break;
					case 7:
						intent.setClass(mContext, Bath2Activity.class);
						break;
					case 8:
						intent.setClass(mContext, Bath2Activity.class);
						break;
					case 9:
						break;
				}
				bundle.putSerializable("DeviceInfo",mDeviceInfo);
				intent.putExtras(bundle);
				mContext.startActivity(intent);
			}
		});
		
		return view;
	}

	static class ViewHolder {
		TextView name_txt;
		TextView mac_txt;
		Button water_btn;
		Button bind_btn;
		LinearLayout layout;
	}

	public void changeData(ArrayList<DeviceInfo> lists) {
		devices = lists;
		notifyDataSetChanged();

	}

}
