package com.klcxkj.zqxy.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.klcxkj.mylibrary.R;
import com.klcxkj.zqxy.databean.WashingQueryBean;
import com.klcxkj.zqxy.widget.NiftyDialogBuilder;

/**
 * autor:OFFICE-ADMIN
 * time:2017/11/28
 * email:yinjuan@klcxkj.com
 * description:查询预约洗衣机状态的适配器
 */

public class WashingQueryAdapter extends MyAdapter<WashingQueryBean>{

    protected NiftyDialogBuilder dialogBuilder;
    /**
     * 构造方法描述:基类构造方法
     *
     * @param mContext
     */
    public WashingQueryAdapter(Context mContext) {
        super(mContext);
        dialogBuilder = NiftyDialogBuilder.getInstance(mContext);
    }

    @Override
    public View getView(final int position, View view, ViewGroup viewGroup) {
        if (view ==null){
            view =View.inflate(mContext, R.layout.item_washing_query,null);
        }
        WashingQueryBean str =getItem(position);
        TextView address =ViewHolder.get(view,R.id.washing_decive_adresss);
        ImageView icon =ViewHolder.get(view,R.id.washing_state_icon);
        TextView state =ViewHolder.get(view,R.id.washing_state_txt);
        LinearLayout btn =ViewHolder.get(view,R.id.washing_yuyue_btn);
        View line =ViewHolder.get(view,R.id.washing_top_line);
        address.setText(str.getDevName());
        switch (str.getIsDevUsed()){
            case 0: //空闲
                state.setText("空闲");
                state.setTextColor(mContext.getResources().getColor(R.color.base_color));
                icon.setImageResource(R.mipmap.washing_decive_null);
                btn.setVisibility(View.VISIBLE);
                line.setBackgroundColor(ContextCompat.getColor(mContext, R.color.base_color));//使用colors.xml文件中的颜色
                break;
            case 1: //使用
                state.setText("使用中");
                state.setTextColor(mContext.getResources().getColor(R.color.shuangse));
                icon.setImageResource(R.mipmap.washing_decive_doing);
                btn.setVisibility(View.GONE);
                line.setBackgroundColor(ContextCompat.getColor(mContext, R.color.shuangse));//使用colors.xml文件中的颜色
                break;
            case 2:
                state.setText("预约中");
                state.setTextColor(mContext.getResources().getColor(R.color.shuangse));
                icon.setImageResource(R.mipmap.washing_decive_doing);
                btn.setVisibility(View.GONE);
                line.setBackgroundColor(ContextCompat.getColor(mContext, R.color.shuangse));//使用colors.xml文件中的颜色
                break;
            case 3:
                state.setText("其他");
                state.setTextColor(mContext.getResources().getColor(R.color.shuangse));
                icon.setImageResource(R.mipmap.washing_decive_doing);
                btn.setVisibility(View.GONE);
                line.setBackgroundColor(ContextCompat.getColor(mContext, R.color.shuangse));//使用colors.xml文件中的颜色
                break;
        }

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onback !=null){
                    onback.onSubscibeCallBack(position);
                }

            }
        });
        return view;
    }

    //预约的回调接口
    public void setOnback(onSubscibe onback) {
        this.onback = onback;
    }

    public  onSubscibe  onback;
    public interface  onSubscibe
    {
        void onSubscibeCallBack(int numt);
    };
}
