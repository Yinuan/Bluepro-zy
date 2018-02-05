package com.klcxkj.zqxy.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.klcxkj.mylibrary.R;
import com.klcxkj.zqxy.databean.MessageData;

public class MessageAdapter extends BaseAdapter {

	private LayoutInflater mInflator;
	private ArrayList<MessageData> messageDatas;
	private Context mContext;

	public MessageAdapter(Context context, ArrayList<MessageData> list) {
		super();
		mContext = context;
		mInflator = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		messageDatas = list;

	}

	@Override
	public int getCount() {
		return messageDatas.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return messageDatas.get(position);
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
			view = mInflator.inflate(R.layout.message_item, viewGroup, false);
			viewHolder = new ViewHolder();

			viewHolder.message_content_txt = (TextView) view
					.findViewById(R.id.message_content_txt);

			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}
		MessageData messageData = messageDatas.get(i);

		viewHolder.message_content_txt.setText(messageData.message_content);

		return view;
	}

	static class ViewHolder {
		TextView message_content_txt;
	}

	public void changeData(ArrayList<MessageData> lists) {
		messageDatas = lists;
		notifyDataSetChanged();
	}

}
