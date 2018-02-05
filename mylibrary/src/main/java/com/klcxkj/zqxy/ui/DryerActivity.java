package com.klcxkj.zqxy.ui;

import android.os.Bundle;

import com.klcxkj.mylibrary.R;

public class DryerActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dryer);
        initview();
    }

    private void initview() {
        showMenu("吹风机");

    }
}
