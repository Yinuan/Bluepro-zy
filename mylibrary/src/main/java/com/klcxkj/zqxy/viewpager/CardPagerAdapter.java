package com.klcxkj.zqxy.viewpager;

import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.klcxkj.mylibrary.R;
import com.klcxkj.zqxy.databean.CardpakageMine;

import java.util.ArrayList;
import java.util.List;

public class CardPagerAdapter extends PagerAdapter implements CardAdapter {

    private List<CardView> mViews;
    private List<CardpakageMine> mData;
    private float mBaseElevation;
    public CardPagerAdapter() {
        mData = new ArrayList<>();
        mViews = new ArrayList<>();
    }

    public void addCardItem(CardpakageMine item) {
        mViews.add(null);
        mData.add(item);

    }

    public void clearCardItem(CardpakageMine item){
        mData = new ArrayList<>();
        mViews = new ArrayList<>();
        mViews.add(null);
        mData.add(item);
    }
    public void setCardItem(List<CardpakageMine> data){
        if (data ==null || data.size()==0){
            mViews.add(null);
            mData =new ArrayList<>();
            mData.add(new CardpakageMine());
            return;
        }
        for (int i = 0; i <mData.size() ; i++) {
            mViews.add(null);
        }
        mData =data;

    }

    public float getBaseElevation() {
        return mBaseElevation;
    }

    @Override
    public CardView getCardViewAt(int position) {
        return mViews.get(position);
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(final ViewGroup container, int position) {
        View  view;
        if (mData.get(position).getYKname()!=null && mData.get(position).getYKname().length()>0){

            view = LayoutInflater.from(container.getContext()).inflate(R.layout.item_viewpager_card, container, false);
            bind(mData.get(position), view);
        }else {
            view = LayoutInflater.from(container.getContext()).inflate(R.layout.item_viewpager, container, false);
            Button card_buy_btn = (Button) view.findViewById(R.id.card_buy_btn);
            card_buy_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onback !=null){
                        onback.onSubscibeCallBack();
                    }
                }
            });
        }

        container.addView(view);

        CardView cardView = (CardView) view.findViewById(R.id.cardView);

        if (mBaseElevation == 0) {
            mBaseElevation = cardView.getCardElevation();
        }

        cardView.setMaxCardElevation(mBaseElevation * MAX_ELEVATION_FACTOR);
        mViews.set(position, cardView);


        return view;
    }


    @Override
    public void startUpdate(ViewGroup container) {
        super.startUpdate(container);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
        mViews.set(position, null);
    }

    private void bind(CardpakageMine item, View view) {

        TextView titleTextView = (TextView) view.findViewById(R.id.viewpager_txt_11);
        TextView contentTextView = (TextView) view.findViewById(R.id.viewpager_txt_22);
        TextView textView2 = (TextView) view.findViewById(R.id.viewpager_txt_33);
       // contentTextView.setText(item.getMonthbuyday()+"/"+item.getMonthTimes());
        textView2.setText(item.getYKname());
        RelativeLayout layout =view.findViewById(R.id.card_show_bg);
        if (item.getYKname().contains("洗衣机")){
            layout.setBackgroundResource(R.mipmap.card_washing_deafult1);
        }else {
            layout.setBackgroundResource(R.mipmap.card_decive_deafult1);
        }




    }
    //预约的回调接口
    public void setOnback(onSubscibe onback) {
        this.onback = onback;
    }

    public  onSubscibe  onback;
    public interface  onSubscibe
    {
        void onSubscibeCallBack();
    };
}
