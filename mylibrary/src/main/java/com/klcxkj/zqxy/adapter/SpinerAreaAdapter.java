package com.klcxkj.zqxy.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.klcxkj.mylibrary.R;
import com.klcxkj.zqxy.databean.AreaInfo;
import com.klcxkj.zqxy.databean.ProjectInfo;

public class SpinerAreaAdapter extends BaseAdapter {

	private LayoutInflater mInflator;
	private ArrayList<AreaInfo> spinerList;
	private Context mContext;

	public SpinerAreaAdapter(Context context,
			ArrayList<AreaInfo> list) {
		super();
		mContext = context;
		mInflator = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		spinerList = list;

	}

	@Override
	public int getCount() {
		return spinerList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return spinerList.get(position);
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
			view = mInflator.inflate(R.layout.spiner_item,
					viewGroup, false);
			viewHolder = new ViewHolder();

			viewHolder.spiner_txt = (TextView) view
					.findViewById(R.id.spiner_txt);


			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}
		AreaInfo spiner = spinerList.get(i);

		viewHolder.spiner_txt.setText(spiner.AreaName);

		return view;
	}

	static class ViewHolder {
		TextView spiner_txt;
	}

	public void changeData(ArrayList<AreaInfo> lists) {
		spinerList = lists;
		notifyDataSetChanged();

	}

}
