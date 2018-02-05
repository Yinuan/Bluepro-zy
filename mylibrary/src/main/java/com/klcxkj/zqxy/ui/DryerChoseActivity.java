package com.klcxkj.zqxy.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.klcxkj.mylibrary.R;
import com.klcxkj.zqxy.common.Common;
import com.klcxkj.zqxy.databean.DeviceInfo;
import com.klcxkj.zqxy.zxing.zxing.activity.CaptureActivity;

public class DryerChoseActivity extends BaseActivity {


    private LinearLayout serachBath ,scanBath,scanBtan2;
    private ImageView device_state_img; //洗澡图标
    private LinearLayout layout_unbind,layout_bind,layout_root;
    private TextView project_name;
    private TextView project_address;
    private TextView scanName;

    private TextView unbind_scan,unbind_search;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drink_chose);
        initview();
        initdata();
        bindclick();
    }

    private void initview() {
        showMenu("吹风机");
        layout_bind = (LinearLayout) findViewById(R.id.layout_binded);
        layout_unbind = (LinearLayout) findViewById(R.id.layout_unbind);
        layout_root = (LinearLayout) findViewById(R.id.layout_root);
        serachBath = (LinearLayout) findViewById(R.id.bath_chose_search);
        scanBath = (LinearLayout) findViewById(R.id.bath_chose_scan);
        scanBtan2 = (LinearLayout) findViewById(R.id.bath_chose_scan2);
        device_state_img = (ImageView) findViewById(R.id.device_state_img);
        project_address = (TextView) findViewById(R.id.project_address);
        project_name = (TextView) findViewById(R.id.project_name);
        scanName = (TextView) findViewById(R.id.chose_scan_name);
        unbind_scan = (TextView) findViewById(R.id.unbind_scan);
        unbind_search = (TextView) findViewById(R.id.unbind_search);
        scanName.setText("扫码吹风");
        unbind_search.setText("搜索吹风机");
        unbind_scan.setText("扫描吹风机");
    }

    private void initdata() {
        SharedPreferences sp = getSharedPreferences("adminInfo", Context.MODE_PRIVATE);
        DeviceInfo deviceInfo = Common.getSaveWaterDeviceInfo(sp);
        if (deviceInfo !=null){
            //绑定了洗澡水表
            layout_bind.setVisibility(View.VISIBLE);
            layout_unbind.setVisibility(View.GONE);
            project_name.setText(deviceInfo.FJName);
            project_address.setText(deviceInfo.DevDescript);
        }else {
            //未绑定洗澡水表
            layout_bind.setVisibility(View.GONE);
            layout_unbind.setVisibility(View.VISIBLE);
            layout_root.setBackgroundResource(R.mipmap.chenjing_bg);
        }


    }

    private void bindclick() {
        scanBath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in =new Intent(DryerChoseActivity.this,CaptureActivity.class);
                in.putExtra("capture_type", CaptureActivity.CAPTURE_WATER);
                startActivity(in);



            }
        });
        scanBtan2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in =new Intent(DryerChoseActivity.this,CaptureActivity.class);
                in.putExtra("capture_type", CaptureActivity.CAPTURE_WATER);
                startActivity(in);



            }
        });
        serachBath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in =new Intent(DryerChoseActivity.this,SearchBratheDeviceActivity.class);
                in.putExtra("type","4");
                startActivity(in);
            }
        });
        device_state_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(DryerChoseActivity.this,DryerActivity.class);
                intent.putExtra("type","4");
                startActivity(intent);
                finish();
            }
        });
    }
}
