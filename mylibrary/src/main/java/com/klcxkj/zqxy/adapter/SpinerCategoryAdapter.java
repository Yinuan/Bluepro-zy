package com.klcxkj.zqxy.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.klcxkj.mylibrary.R;
import com.klcxkj.zqxy.databean.DeciveType;

import java.util.List;

public class SpinerCategoryAdapter extends BaseAdapter {

	private LayoutInflater mInflator;
	private List<DeciveType> spinerList;
	private Context mContext;

	public SpinerCategoryAdapter(Context context, List<DeciveType> list) {
		super();
		mContext = context;
		mInflator = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
	public View getView(final int i, View view, ViewGroup viewGroup) {

		ViewHolder viewHolder;
		if (view == null) {
			view = mInflator.inflate(R.layout.spiner_item, viewGroup, false);
			viewHolder = new ViewHolder();

			viewHolder.spiner_txt = (TextView) view.findViewById(R.id.spiner_txt);
			viewHolder.imageView = (ImageView) view.findViewById(R.id.spiner_image);
			viewHolder.relativeLayout = (RelativeLayout) view.findViewById(R.id.spiner_layout);

			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}
		String spiner = spinerList.get(i).getDevname();
		viewHolder.spiner_txt.setText(spiner);



		return view;
	}

	static class ViewHolder {
		TextView spiner_txt;
		ImageView imageView;
		RelativeLayout relativeLayout;
	}

	public void changeData(List<DeciveType> lists) {
		spinerList = lists;
		notifyDataSetChanged();

	}



	//预约的回调接口
	public void setOnback(ongetDeciveType onback) {
		this.onback = onback;
	}

	public  ongetDeciveType  onback;
	public interface  ongetDeciveType
	{
		void onTypeCallBack(int numt);
	};

}
