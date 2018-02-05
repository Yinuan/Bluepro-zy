package com.klcxkj.zqxy.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.klcxkj.mylibrary.R;
import com.klcxkj.zqxy.databean.BillInfo;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class BillAdapter extends BaseAdapter {

	private LayoutInflater mInflator;
	private List<BillInfo> billInfos;

	private Context mContext;

	public BillAdapter(Context context, List<BillInfo> list) {
		super();
		mInflator = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		billInfos = list;
		mContext= context;

	}

	@Override
	public int getCount() {
		return billInfos.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return billInfos.get(position);
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
			view = mInflator.inflate(R.layout.bill_item,
					viewGroup, false);
			viewHolder = new ViewHolder();

			viewHolder.bill_icon = (ImageView) view.findViewById(R.id.bill_icon);

			viewHolder.dealmark_txt = (TextView) view.findViewById(R.id.dealmark_txt);

			viewHolder.deal_time_txt = (TextView) view.findViewById(R.id.deal_time_txt);
			viewHolder.money_txt = (TextView) view.findViewById(R.id.money_txt);

			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}
		BillInfo billInfo = billInfos.get(i);

		if (billInfo.DealMark !=null){
			if (billInfo.DealMark.equals(mContext.getString(R.string.consume_bill))) {
				viewHolder.bill_icon.setImageResource(R.drawable.consume_bill_normal);

				DecimalFormat decimalFormat=new DecimalFormat("##0.00");//构造方法的字符格式这里如果小数不足2位,会以0补足.
				String string = decimalFormat.format(billInfo.UpMoney);//format 返回的是字符串

				viewHolder.money_txt.setText("-"+string+mContext.getString(R.string.yuan1));
				viewHolder.money_txt.setTextColor(mContext.getResources().getColor(R.color.red));
			}else {
				viewHolder.bill_icon.setImageResource(R.drawable.recharge_bill_normal);

				DecimalFormat decimalFormat=new DecimalFormat("##0.00");//构造方法的字符格式这里如果小数不足2位,会以0补足.
				String string = decimalFormat.format(billInfo.DealMoney);//format 返回的是字符串

				viewHolder.money_txt.setText("+"+string+mContext.getString(R.string.yuan1));
				viewHolder.money_txt.setTextColor(mContext.getResources().getColor(R.color.bill_recharge));
			}

			viewHolder.dealmark_txt.setText(billInfo.ConsumeType);
			viewHolder.deal_time_txt.setText(billInfo.DealDT);
		}

		


		return view;
	}

	static class ViewHolder {
		ImageView bill_icon;
		TextView dealmark_txt;
		TextView deal_time_txt;
		TextView money_txt;
	}

	public void changeData(ArrayList<BillInfo> lists) {
		billInfos = lists;
		notifyDataSetChanged();

	}

}
