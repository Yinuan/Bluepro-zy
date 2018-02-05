package com.klcxkj.zqxy.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.klcxkj.mylibrary.R;
import com.klcxkj.zqxy.databean.PackageData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CurrentPackageAdapter extends BaseAdapter {
	private ArrayList<PackageData> mDatas;
	private Context mContext;
	private Map<PackageData, Boolean> isCheckMap = new HashMap<PackageData, Boolean>();

	public CurrentPackageAdapter(Context context, ArrayList<PackageData> datas) {
		super();
		mContext = context;
		mDatas = datas;
	}

	@Override
	public int getCount() {

		return mDatas.size();
	}

	@Override
	public PackageData getItem(int position) {

		return mDatas.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		ViewHolder viewHolder;
		if (view == null) {
			view = LayoutInflater.from(mContext).inflate(R.layout.package_item, null);
			viewHolder = new ViewHolder();
			viewHolder.package_acount = (TextView) view.findViewById(R.id.package_count_txt);
			viewHolder.current_package_layout = (LinearLayout) view.findViewById(R.id.current_package_layout);
			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}
		PackageData data = mDatas.get(position);
		if (!isCheckMap.containsKey(data)) {
			isCheckMap.put(data, false);
		}
		if (isCheckMap.get(data)) {
			viewHolder.current_package_layout.setSelected(true);
		} else {
			viewHolder.current_package_layout.setSelected(false);
		}
		if (data.package_id == -1) {
			viewHolder.package_acount.setText(R.string.other);
		}else {
			viewHolder.package_acount.setText(data.package_account + mContext.getString(R.string.yuan1));
		}

		return view;
	}

	public Map<PackageData, Boolean> getCheckMap() {
		return isCheckMap;
	}

	public boolean getSelectedData(PackageData data) {
		return isCheckMap.get(data);
	}

	public void setSelectedData(PackageData data, boolean result) {
		isCheckMap.clear();
		isCheckMap.put(data, result);
		notifyDataSetChanged();
	}

	public void clearCheckMap() {
		isCheckMap.clear();
		notifyDataSetChanged();
	}

	static class ViewHolder {
		TextView package_acount;
		LinearLayout current_package_layout;
	}

	public void changeData(ArrayList<PackageData> datas) {
		mDatas = datas;
		isCheckMap.clear();
		notifyDataSetChanged();
	}
}
