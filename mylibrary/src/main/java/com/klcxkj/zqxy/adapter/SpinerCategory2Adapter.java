package com.klcxkj.zqxy.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.klcxkj.mylibrary.R;
import com.klcxkj.zqxy.databean.DeciveInfo2;

import java.util.List;

public class SpinerCategory2Adapter extends BaseAdapter {

	private LayoutInflater mInflator;
	private List<DeciveInfo2> spinerList;
	private Context mContext;

	public SpinerCategory2Adapter(Context context, List<DeciveInfo2> list) {
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


			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}
		viewHolder.imageView.setVisibility(View.GONE);
		String spiner = spinerList.get(i).getTypename2();
		viewHolder.spiner_txt.setText(spiner);



		return view;
	}

	static class ViewHolder {
		TextView spiner_txt;
		ImageView imageView;

	}

	public void changeData(List<DeciveInfo2> lists) {
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
