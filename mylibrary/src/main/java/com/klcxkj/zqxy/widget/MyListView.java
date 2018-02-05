package com.klcxkj.zqxy.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * autor:OFFICE-ADMIN
 * time:2017/12/12
 * email:yinjuan@klcxkj.com
 * description:自定义
 */

public class MyListView extends ListView{

    public MyListView(Context context) {
        super(context);
    }

    public MyListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
