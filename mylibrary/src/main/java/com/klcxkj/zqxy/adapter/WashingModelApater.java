package com.klcxkj.zqxy.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.klcxkj.mylibrary.R;
import com.klcxkj.zqxy.databean.WashingModelInfo;

/**
 * autor:OFFICE-ADMIN
 * time:2017/12/10
 * email:yinjuan@klcxkj.com
 * description:
 */

public class WashingModelApater extends MyAdapter<WashingModelInfo> {

    /**
     * 构造方法描述:基类构造方法
     *
     * @param mContext
     */
    public WashingModelApater(Context mContext) {
        super(mContext);
    }



    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        if (view ==null){
            view=View.inflate(mContext, R.layout.item_washing_model,null);
        }
        WashingModelInfo washingModelInfo =getItem(position);
        ImageView icon =ViewHolder.get(view,R.id.model_icon);
        TextView name =ViewHolder.get(view,R.id.model_name);
        TextView count =ViewHolder.get(view,R.id.model_count);
        TextView ps =ViewHolder.get(view,R.id.model_ps);
        if (washingModelInfo !=null){
            name.setText(washingModelInfo.getTypename());
            float b =Float.parseFloat(washingModelInfo.getMoney())/1000; //厘为单位
            int d=Integer.parseInt(washingModelInfo.getTimes())/60; //秒为单位
            count.setText(b+"元/"+d+"分钟");
            ps.setText(washingModelInfo.getDescname());
            switch (washingModelInfo.getTypeid()){
                case 33:
                    icon.setImageResource(R.mipmap.model_standard);
                    break;
                case 34:
                    icon.setImageResource(R.mipmap.model_quick);
                    break;
                case 35:
                    icon.setImageResource(R.mipmap.model_clear);
                    break;
                case 36:
                    icon.setImageResource(R.mipmap.model_big);
                    break;
            }
        }
        return view;
    }
}
