package com.klcxkj.zqxy.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.klcxkj.mylibrary.R;

import java.text.DecimalFormat;

public class WashingConsumeActivity extends BaseActivity {

    private TextView time,monney,model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_washing_consume);
        initview();
        initdata();
    }

    private void initdata() {
        Intent intent =getIntent();
       String timestr = intent.getStringExtra("time");
       String mode = intent.getStringExtra("model");
       String ykMonney = intent.getStringExtra("ykmonney");
        String modelString="";
        switch (Integer.parseInt(mode)){
            case 33:
                modelString ="标准洗";
                break;
            case 34:
                modelString ="快速洗";
                break;
            case 35:
                modelString ="单脱水";
                break;
            case 36:
                modelString ="大件洗";
                break;
        }
        model.setText(modelString);
        monney.setText(Float.parseFloat(ykMonney)/1000+"(元)");
        if (timestr.length()==12){
            String str = timestr.substring(0,6);
            System.out.println(str);
            DecimalFormat df = new DecimalFormat("##,##");
            String ret=df.format(Double.parseDouble(str));
            ret=ret.replaceAll(",","/");
            String str2 = timestr.substring(6,12);
            DecimalFormat df2 = new DecimalFormat("##,##");
            String ret2=df2.format(Double.parseDouble(str2));
            ret2=ret2.replaceAll(",",":");
            time.setText("20"+ret+" "+ret2);
        }else {
            time.setText("20"+timestr);
        }

    }

    private void initview() {
        showMenu("消费详情");
        time =findViewById(R.id.finish_time_txt);
        model =findViewById(R.id.withhold_amount_txt);
        monney =findViewById(R.id.consume_amount_txt);
    }
}
