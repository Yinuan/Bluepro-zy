package com.klcxkj.zqxy.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.klcxkj.mylibrary.R;
/**
 * author : yinjuan
 * time： 2017/10/17 13:54
 * email：yin.juan2016@outlook.com
 * Description:操作指引
 */

public class OperationGuideActivity extends BaseActivity implements View.OnClickListener{

private RelativeLayout line1,line2,line3,line4,line5,line6;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_operation_guide);
        initview();
    }

    private void initview() {
        showMenu("操作指引");
        line1 = (RelativeLayout) findViewById(R.id.line1);
        line2 = (RelativeLayout) findViewById(R.id.line2);
        line3 = (RelativeLayout) findViewById(R.id.line3);
        line4 = (RelativeLayout) findViewById(R.id.line4);
        line5 = (RelativeLayout) findViewById(R.id.line5);
        line6 = (RelativeLayout) findViewById(R.id.line6);

        line1.setOnClickListener(this);
        line2.setOnClickListener(this);
        line3.setOnClickListener(this);
        line4.setOnClickListener(this);
        line5.setOnClickListener(this);
        line6.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.line1) {
            Intent intent = new Intent();
            intent.setClass(OperationGuideActivity.this, H5Activity.class);
            intent.putExtra("h5_tag", H5Activity.YSLC);
            startActivity(intent);

        } else if (i == R.id.line2) {
        } else if (i == R.id.line3) {
        } else if (i == R.id.line4) {
        } else if (i == R.id.line5) {
        } else if (i == R.id.line6) {
        }
    }
}
