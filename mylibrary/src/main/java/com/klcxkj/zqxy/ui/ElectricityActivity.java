package com.klcxkj.zqxy.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.klcxkj.mylibrary.R;

public class ElectricityActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_electricity);
        showMenu("充电");
        findViewById(R.id.button3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =getIntent();
                intent.putExtra("ceshi","nihap");
                setResult(RESULT_OK,intent);
                finish();
            }
        });
    }
}
