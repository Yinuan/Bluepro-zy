package com.klcxkj.zqxy.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.klcxkj.mylibrary.R;
import com.klcxkj.zqxy.databean.MsgPush;

/**
 * autor:OFFICE-ADMIN
 * time:2017/11/16
 * email:yinjuan@klcxkj.com
 * description:
 */

public class MessageCenterApater extends MyAdapter<MsgPush> {
    /**
     * 构造方法描述:基类构造方法
     *
     * @param mContext
     */
    public MessageCenterApater(Context mContext) {
        super(mContext);
    }

    Handler handler =new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };

    @Override
    public View getView(int position, View mView, ViewGroup viewGroup) {
        if (mView ==null){
            mView =View.inflate(mContext, R.layout.item_message_center,null);
        }
        final MsgPush str =getItem(position);
        TextView time =ViewHolder.get(mView,R.id.push_msg_time);
        //1
        RelativeLayout relativeLayout = ViewHolder.get(mView,R.id.message_type_1);
        ImageView icon =ViewHolder.get(mView,R.id.message_icon);
        TextView message_title =ViewHolder.get(mView,R.id.message_title);
        final TextView message_content =ViewHolder.get(mView,R.id.message_content);


        time.setText(str.getPushDT());
        message_title.setText(str.getPushtitle());
        message_content.setText(str.getContentTxt());

        return mView;
    }



}
