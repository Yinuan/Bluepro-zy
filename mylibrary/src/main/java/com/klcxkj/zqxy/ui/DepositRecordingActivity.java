package com.klcxkj.zqxy.ui;

import android.os.Bundle;

import com.klcxkj.mylibrary.R;

public class DepositRecordingActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deposit_recording);
        initview();
    }

    private void initview() {
        showMenu("押金记录");
    }
}
