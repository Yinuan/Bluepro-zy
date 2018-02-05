package com.klcxkj.zqxy.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;

public class ViewHolder extends RecyclerView.ViewHolder {

	public ViewHolder(View itemView) {
		super(itemView);
	}

	@SuppressWarnings("unchecked")
	public static <T> T get(View view, int id) {
		SparseArray viewHolder = (SparseArray) view.getTag();
		if (viewHolder == null) {
			viewHolder = new SparseArray();
			view.setTag(viewHolder);
		}
		View childView = (View) viewHolder.get(id);
		if (childView == null) {
			childView =

			view.findViewById(id);
			viewHolder.put(id,

			childView);
		}
		return (T) childView;
	}
}