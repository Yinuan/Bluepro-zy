package com.klcxkj.zqxy.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.klcxkj.mylibrary.R;
import com.klcxkj.zqxy.databean.AdminProductInfo;
import com.klcxkj.zqxy.databean.ExceptionData;

public class ExceptionAdapter extends BaseAdapter {

	private LayoutInflater mInflator;
	private ArrayList<ExceptionData> deviceExceptionDatas;
	private Context mContext;

	public ExceptionAdapter(Context context, ArrayList<ExceptionData> exceptionDatas) {
		super();
		mContext = context;
		mInflator = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		deviceExceptionDatas = exceptionDatas;

	}

	@Override
	public int getCount() {
		return deviceExceptionDatas.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return deviceExceptionDatas.get(position);
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
			view = mInflator.inflate(R.layout.exception_item, viewGroup, false);
			viewHolder = new ViewHolder();

			viewHolder.exception_project_name_txt = (TextView) view
					.findViewById(R.id.exception_project_name_txt);
			
			viewHolder.exception_count_txt = (TextView) view
					.findViewById(R.id.exception_count_txt);

			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}
		ExceptionData exceptionData = deviceExceptionDatas.get(i);
		
		viewHolder.exception_project_name_txt.setText(exceptionData.PrjName);
		viewHolder.exception_count_txt.setText(exceptionData.DevErrCount +"");

		return view;
	}

	static class ViewHolder {
		TextView exception_project_name_txt;
		
		TextView exception_count_txt;
		
	}

	public void changeData(ArrayList<ExceptionData> lists) {
		deviceExceptionDatas = lists;
		notifyDataSetChanged();
	}

}
