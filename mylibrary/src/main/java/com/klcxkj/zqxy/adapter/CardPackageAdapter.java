package com.klcxkj.zqxy.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.klcxkj.mylibrary.R;
import com.klcxkj.zqxy.databean.CardPackageAll;
import com.klcxkj.zqxy.databean.CardPackageData;

/**
 * autor:OFFICE-ADMIN
 * time:2017/11/8
 * email:yinjuan@klcxkj.com
 * description:卡片套餐办理选择适配器
 */

public class CardPackageAdapter extends MyAdapter<CardPackageData>{
    /**
     * 构造方法描述:基类构造方法
     *
     * @param mContext
     */
    public CardPackageAdapter(Context mContext) {
        super(mContext);
    }


    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        if (view ==null){
            view =View.inflate(mContext, R.layout.item_card_pakeage,null);
        }
        CardPackageData cardPackageData =getItem(position);
        CardPackageAll cardInfo =cardPackageData.getCardPackageAll();
        LinearLayout layout =ViewHolder.get(view,R.id.item_card_bg);
        TextView txt1 =ViewHolder.get(view,R.id.item_card_text1);
        TextView txt2 =ViewHolder.get(view,R.id.item_card_text2);
        txt1.setText(cardInfo.getTypevalue());
        txt2.setText(cardInfo.getDescname());
        if (cardPackageData.getChosed() ==1){  //选中

            layout.setBackgroundResource(R.mipmap.card_package_chosed);
        }else {
             layout.setBackgroundResource(R.drawable.card_pakeage_unchoased);
        }
        return view;
    }
}
