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

public class FinancialAdapter extends BaseAdapter {

	private LayoutInflater mInflator;
	private ArrayList<AdminProductInfo> financialDatas;
	private Context mContext;

	public FinancialAdapter(Context context, ArrayList<AdminProductInfo> list) {
		super();
		mContext = context;
		mInflator = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		financialDatas = list;

	}

	@Override
	public int getCount() {
		return financialDatas.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return financialDatas.get(position);
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
			view = mInflator.inflate(R.layout.financial_item, viewGroup, false);
			viewHolder = new ViewHolder();

			viewHolder.project_name_txt = (TextView) view
					.findViewById(R.id.project_name_txt);
			
			viewHolder.device_count_txt = (TextView) view
					.findViewById(R.id.device_count_txt);
			
			viewHolder.account_count_txt = (TextView) view
					.findViewById(R.id.account_count_txt);
			
			viewHolder.recharge_count_txt = (TextView) view
					.findViewById(R.id.recharge_count_txt);
			
			viewHolder.consume_count_txt = (TextView) view
					.findViewById(R.id.consume_count_txt);

			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}
		AdminProductInfo adminProductInfo = financialDatas.get(i);
		
		viewHolder.project_name_txt.setText(adminProductInfo.PrjName);
		viewHolder.device_count_txt.setText(adminProductInfo.DevAll +"");
		viewHolder.account_count_txt.setText(adminProductInfo.UserCount +"");
		viewHolder.recharge_count_txt.setText(adminProductInfo.SaveMoney +"");
		viewHolder.consume_count_txt.setText(adminProductInfo.XFMoney +"");

		return view;
	}

	static class ViewHolder {
		TextView project_name_txt;
		
		TextView device_count_txt;
		TextView account_count_txt;
		TextView recharge_count_txt;
		TextView consume_count_txt;
		
	}

	public void changeData(ArrayList<AdminProductInfo> lists) {
		financialDatas = lists;
		notifyDataSetChanged();
	}

}
