package com.klcxkj.zqxy.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.klcxkj.mylibrary.R;

/**
 * autor:OFFICE-ADMIN
 * time:2017/10/31
 * email:yinjuan@klcxkj.com
 * description:
 */

public class RepairPopAdapter extends MyAdapter<String> {
    /**
     * 构造方法描述:基类构造方法
     *
     * @param mContext
     */
    public RepairPopAdapter(Context mContext) {
        super(mContext);
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        if (view ==null){
            view =View.inflate(mContext, R.layout.pop_listview_item,null);
        }
        TextView name =ViewHolder.get(view,R.id.pop_list_item_name);
       String str =getItem(position);
        name.setText(str);
        return view;
    }
}
