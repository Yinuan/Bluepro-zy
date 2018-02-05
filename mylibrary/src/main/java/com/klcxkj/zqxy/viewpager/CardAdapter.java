package com.klcxkj.zqxy.viewpager;


import android.support.v7.widget.CardView;

public interface CardAdapter {

    int MAX_ELEVATION_FACTOR = 6;

    float getBaseElevation();

    CardView getCardViewAt(int position);

    int getCount();
}
