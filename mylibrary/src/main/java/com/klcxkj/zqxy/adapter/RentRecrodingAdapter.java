package com.klcxkj.zqxy.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.klcxkj.mylibrary.R;
import com.klcxkj.zqxy.databean.RentRecordingBean;
import com.klcxkj.zqxy.ui.RentReturnActivity;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * autor:OFFICE-ADMIN
 * time:2017/10/31
 * email:yinjuan@klcxkj.com
 * description:
 */

public class RentRecrodingAdapter extends MyAdapter<RentRecordingBean> {
    /**
     * 构造方法描述:基类构造方法
     *
     * @param mContext
     */
    public RentRecrodingAdapter(Context mContext) {
        super(mContext);
    }

    @Override
    public View getView(final int position, View view, ViewGroup viewGroup) {
        if (view ==null){
            view =View.inflate(mContext, R.layout.item_rent_recording_list,null);
        }
        final RentRecordingBean str =getItem(position);
        ImageView imageView =ViewHolder.get(view,R.id.rent_icon);
        TextView title =ViewHolder.get(view,R.id.rent_recording_title);
        TextView time =ViewHolder.get(view,R.id.rent_recording_time);
        TextView content =ViewHolder.get(view,R.id.rent_recording_content);
        TextView btn =ViewHolder.get(view,R.id.rent_recroding_btn);

        SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy-MM-dd");
        Long recordingtime =Long.parseLong(str.getApplicateDT());
        Date date=new Date(recordingtime);
        time.setText(dateFormater.format(date));
        switch (str.getStatus()){
            case 0: //处理中
                title.setText(str.getDevname()+"申请处理中");
                btn.setText("取消申请");
                btn.setTextColor(mContext.getResources().getColor(R.color.base_color));
                btn.setBackgroundResource(R.drawable.rent_waiting);
                btn.setEnabled(true);
                imageView.setImageResource(R.mipmap.rent_waiting);
                break;
            case 1: //申请成功
                title.setText(str.getDevname()+"申请成功");
                btn.setText("已完成");
                btn.setEnabled(false);
                imageView.setImageResource(R.mipmap.rent_waiting);
                break;
            case 2: //取消申请
                title.setText(str.getDevname()+"申请已取消");
                btn.setText("已取消");
                btn.setEnabled(false);
                imageView.setImageResource(R.mipmap.rent_cancle);
                break;
            case 3://其他
                title.setText(str.getDevname()+"申请审核失败");
                btn.setText("申请失败");
                btn.setEnabled(false);
                imageView.setImageResource(R.mipmap.rent_waiting);
                break;
            case 4:
                title.setText(str.getDevname()+"申请退款中");
                btn.setText("取消申请");
                btn.setEnabled(false);
                imageView.setImageResource(R.mipmap.rent_cancle);
                break;
            case 5:
                title.setText(str.getDevname()+"申请退款成功");
                btn.setText("已完成");
                btn.setEnabled(false);
                imageView.setImageResource(R.mipmap.rent_waiting);
                break;

        }
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("-----", "position:" + position);
               Intent intent=new Intent(mContext, RentReturnActivity.class);
                Bundle bundle =new Bundle();
                bundle.putSerializable("cardRecroding",str);
                intent.putExtras(bundle);
                mContext.startActivity(intent);

            }
        });
        return view;
    }
}
