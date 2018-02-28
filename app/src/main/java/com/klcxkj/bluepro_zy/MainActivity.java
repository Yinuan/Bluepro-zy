package com.klcxkj.bluepro_zy;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.klcxkj.zqxy.ui.MainAdminActivity;
import com.klcxkj.zqxy.ui.MainUserActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //用户
        findViewById(R.id.button4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent();
                intent.setClass(MainActivity.this, MainUserActivity.class);
                intent.putExtra("tellPhone","24");
                intent.putExtra("PrjID","0");
                intent.putExtra("app_url","http://106.75.164.143/appI/api/");
                intent.putExtra("prijName","蓝牙项目");
                startActivity(intent);
            }
        });
        //操作员
        findViewById(R.id.button5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent();
                intent.setClass(MainActivity.this, MainAdminActivity.class);
                intent.putExtra("tellPhone","18565651403");
                intent.putExtra("PrjID","0");
                intent.putExtra("app_url","http://106.75.164.143/appI/api/");
                intent.putExtra("prijName","蓝牙项目");
                startActivity(intent);
            }
        });
    }
}
