package com.klcxkj.zqxy.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.klcxkj.mylibrary.R;
import com.klcxkj.zqxy.common.Common;
import com.klcxkj.zqxy.databean.ExceptionData;
import com.klcxkj.zqxy.databean.ExceptionDetailData;

public class ExceptionDetailAdapter extends BaseAdapter {

	private LayoutInflater mInflator;
	private ArrayList<ExceptionDetailData> exceptionDatas;
	private Context mContext;

	public ExceptionDetailAdapter(Context context, ArrayList<ExceptionDetailData> exceptionDetailDatas) {
		super();
		mContext = context;
		mInflator = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		exceptionDatas = exceptionDetailDatas;

	}

	@Override
	public int getCount() {
		return exceptionDatas.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return exceptionDatas.get(position);
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
			view = mInflator.inflate(R.layout.exception__detail_item, viewGroup, false);
			viewHolder = new ViewHolder();
			
			viewHolder.exception_device_address_txt = (TextView) view
					.findViewById(R.id.exception_device_address_txt);
			
			viewHolder.exception_time_txt = (TextView) view
					.findViewById(R.id.exception_time_txt);
			
			viewHolder.exception_reason_txt = (TextView) view
					.findViewById(R.id.exception_reason_txt);

			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}
		ExceptionDetailData exceptionDetailData = exceptionDatas.get(i);
		
		viewHolder.exception_device_address_txt.setText(exceptionDetailData.DevName);
		viewHolder.exception_time_txt.setText(Common.timeconvertHHmm(exceptionDetailData.UpStatusDT));
		viewHolder.exception_reason_txt.setText(exceptionDetailData.StatusName);

		return view;
	}

	static class ViewHolder {
		
		TextView exception_device_address_txt;
		TextView exception_time_txt;
		TextView exception_reason_txt;
		
	}

	public void changeData(ArrayList<ExceptionDetailData> lists) {
		exceptionDatas = lists;
		notifyDataSetChanged();
	}

}
