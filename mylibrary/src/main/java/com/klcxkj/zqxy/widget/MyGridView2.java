package com.klcxkj.zqxy.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * autor:OFFICE-ADMIN
 * time:2017/12/8
 * email:yinjuan@klcxkj.com
 * description:自定义的
 */

public class MyGridView2 extends GridView{
    public MyGridView2(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyGridView2(Context context) {
        super(context);
    }

    public MyGridView2(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int expandSpec = MeasureSpec.makeMeasureSpec(
                Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
