package com.klcxkj.zqxy.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.klcxkj.zqxy.MyApp;
import com.klcxkj.mylibrary.R;
import com.klcxkj.zqxy.databean.WashingModelInfo;

import org.greenrobot.eventbus.EventBus;

public class WashingOrderActivity extends BaseActivity {

    private TextView txt1,txt2,txt3,txt4;
    private Button order_btn;
    private   WashingModelInfo washingModelInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_washing_order);
        initview();
        initdata();
        bindclick();
    }

    private void bindclick() {
        order_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventBus.getDefault().postSticky("order_ok");
                Intent in=new Intent(WashingOrderActivity.this,WashingActivity.class);
                Bundle bundle =new Bundle();
                bundle.putSerializable("model_chose",washingModelInfo);
                in.putExtras(bundle);
                startActivity(in);
                finish();
            }
        });
    }

    private void initdata() {
        Intent intent =getIntent();
        washingModelInfo = (WashingModelInfo) intent.getExtras().getSerializable("model_chose");
        if (washingModelInfo ==null){
            return;
        }
        txt1.setText(MyApp.washingDecivename);
        txt2.setText(washingModelInfo.getTypename());
        txt3.setText(Integer.parseInt(washingModelInfo.getMoney())/1000+"元");
        txt4.setText(Integer.parseInt(washingModelInfo.getTimes())/60+"分钟");
    }

    private void initview() {
       showMenu("订单确认");
        txt1 =findViewById(R.id.order_txt_1);
        txt2 =findViewById(R.id.order_txt_2);
        txt3 =findViewById(R.id.order_txt_3);
        txt4 =findViewById(R.id.order_txt_4);
        order_btn =findViewById(R.id.order_btn);
    }
}
