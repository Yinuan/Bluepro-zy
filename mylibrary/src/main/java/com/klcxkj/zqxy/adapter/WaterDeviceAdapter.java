package com.klcxkj.zqxy.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.klcxkj.mylibrary.R;
import com.klcxkj.zqxy.common.Common;
import com.klcxkj.zqxy.databean.DeviceInfo;
import com.klcxkj.zqxy.databean.UserInfo;
import com.klcxkj.zqxy.ui.WaterDeviceListActivity;

import java.util.ArrayList;
import java.util.List;

public class WaterDeviceAdapter extends BaseAdapter {

	private LayoutInflater mInflator;
	private List<DeviceInfo> devices;
	private UserInfo mUserInfo;
	private SharedPreferences sp;

	private Handler mHandler;

	public WaterDeviceAdapter(Context context, List<DeviceInfo> list, Handler handler) {
		super();
		mInflator = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		devices = list;

		mHandler = handler;
		sp=context.getSharedPreferences("adminInfo", Context.MODE_PRIVATE);
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
			view = mInflator.inflate(R.layout.scan_water_device_item,
					viewGroup, false);
			viewHolder = new ViewHolder();

			viewHolder.name_txt = (TextView) view.findViewById(R.id.name_txt);

			viewHolder.mac_txt = (TextView) view.findViewById(R.id.mac_txt);

			viewHolder.link_btn = (Button) view.findViewById(R.id.link_btn);
			viewHolder.layout =view.findViewById(R.id.user_scan_list_layout);

			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}
		DeviceInfo deviceInfo = devices.get(i);
		if (deviceInfo.PrjID!=0){  //登记过的设备
			if (deviceInfo.PrjID !=mUserInfo.PrjID){
				if (mUserInfo.PrjID==0){
					viewHolder.layout.setBackgroundResource(R.color.white);
					viewHolder.link_btn.setEnabled(true);

				}else {
					viewHolder.layout.setBackgroundResource(R.color.text_hint);
					viewHolder.link_btn.setEnabled(false);

				}


			}else {
				viewHolder.layout.setBackgroundResource(R.color.white);
				viewHolder.link_btn.setEnabled(true);

			}
		}
		viewHolder.name_txt.setText(deviceInfo.DevName);

		viewHolder.mac_txt.setText("MAC：" + deviceInfo.devMac);
		viewHolder.link_btn.setTag(deviceInfo);

		viewHolder.link_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				DeviceInfo mDeviceInfo = (DeviceInfo) v.getTag();

				Message message = new Message();
				message.obj = mDeviceInfo;
				message.what = WaterDeviceListActivity.LINK_WATER_DEVICE;
				mHandler.sendMessage(message);

			}
		});
		return view;
	}

	static class ViewHolder {
		TextView name_txt;
		TextView mac_txt;
		Button link_btn;
		RelativeLayout layout;
	}

	public void changeData(ArrayList<DeviceInfo> lists) {
		devices = lists;
		notifyDataSetChanged();

	}

}
