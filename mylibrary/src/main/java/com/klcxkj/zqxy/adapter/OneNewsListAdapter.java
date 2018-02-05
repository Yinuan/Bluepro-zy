package com.klcxkj.zqxy.adapter;

import android.content.Context;
import android.util.Patterns;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.klcxkj.mylibrary.R;
import com.klcxkj.zqxy.databean.MsgQuerySpread;

/**
 * autor:OFFICE-ADMIN
 * time:2017/11/1
 * email:yinjuan@klcxkj.com
 * description:
 */

public class OneNewsListAdapter extends MyAdapter<MsgQuerySpread> {
    /**
     * 构造方法描述:基类构造方法
     *
     * @param mContext
     */
    public OneNewsListAdapter(Context mContext) {
        super(mContext);
    }

    @Override
    public View getView(int position, View mView, ViewGroup viewGroup) {
        MsgQuerySpread tStr  =getItem(position);
        if (mView ==null){
            mView =View.inflate(mContext,R.layout.fragment_one_news_list,null);
        }
        LinearLayout layout1 =ViewHolder.get(mView,R.id.one_news_layout1);
        LinearLayout layout2 =ViewHolder.get(mView,R.id.one_news_layout2);
        LinearLayout layout3 =ViewHolder.get(mView,R.id.one_news_layout3);
        TextView title =ViewHolder.get(mView,R.id.one_news_title);
        TextView content =ViewHolder.get(mView,R.id.one_news_content);
        TextView time =ViewHolder.get(mView,R.id.one_news_time);
        ImageView icon =ViewHolder.get(mView,R.id.one_news_icon);
        TextView title2 =ViewHolder.get(mView,R.id.one_news_title2);
        ImageView imageView =ViewHolder.get(mView,R.id.one_news_image);
        TextView title3 =ViewHolder.get(mView,R.id.one_news_title3);
        TextView content3 =ViewHolder.get(mView,R.id.one_news_content3);
        TextView time3 =ViewHolder.get(mView,R.id.one_news_time3);
        if (tStr !=null){
            if (tStr.getSpreadContent() !=null && tStr.getSpreadContent().length()>0){
                String webUrl =tStr.getSpreadPIC();
                if (Patterns.WEB_URL.matcher(webUrl).matches()){
                    layout1.setVisibility(View.VISIBLE);
                    layout2.setVisibility(View.GONE);
                    layout3.setVisibility(View.GONE);
                    title.setText(tStr.getSpreadTitle());
                    content.setText(tStr.getSpreadContent());
                    time.setText(tStr.getCreaterDT());

                    Glide.with(mContext)
                            .load(tStr.getSpreadPIC())
                            .error(R.mipmap.msg_deafult)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .centerCrop()
                            .into(icon);
                }else {
                    layout1.setVisibility(View.GONE);
                    layout2.setVisibility(View.GONE);
                    layout3.setVisibility(View.VISIBLE);
                    title3.setText(tStr.getSpreadTitle());
                    content3.setText(tStr.getSpreadContent());
                    time3.setText(tStr.getCreaterDT());
                }


            }else {
                layout1.setVisibility(View.GONE);
                layout2.setVisibility(View.VISIBLE);
                layout3.setVisibility(View.GONE);
                title2.setText(tStr.getSpreadTitle());
                Glide.with(mContext)
                        .load(tStr.getSpreadPIC())
                        .error(R.mipmap.msg_big_deafult)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .centerCrop()
                        .into(imageView);


            }
        }

        return mView;
    }
}
