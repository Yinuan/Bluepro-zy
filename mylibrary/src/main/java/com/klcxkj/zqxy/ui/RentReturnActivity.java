package com.klcxkj.zqxy.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.klcxkj.zqxy.MyApp;
import com.klcxkj.mylibrary.R;
import com.klcxkj.zqxy.common.Common;
import com.klcxkj.zqxy.databean.CardPackageResult;
import com.klcxkj.zqxy.databean.RentRecordingBean;
import com.klcxkj.zqxy.widget.Effectstype;

import net.android.tools.afinal.FinalHttp;
import net.android.tools.afinal.http.AjaxCallBack;
import net.android.tools.afinal.http.AjaxParams;

import java.text.SimpleDateFormat;
import java.util.Date;

public class RentReturnActivity extends BaseActivity {

    private Button btn;  //提交按钮
    private RelativeLayout rent_recording_stuta; //取消申请那行布局
    private RentRecordingBean recordingBean;  //传过来的对象
    private TextView title,time; //租赁订单的标题，时间
    private EditText reason;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rent_return);
        initdata();
        initview();
        bindclick();
    }

    private void initdata() {
        sp = getSharedPreferences("adminInfo", Context.MODE_PRIVATE);
        mUserInfo=Common.getUserInfo(sp);
        Intent intent =getIntent();
        recordingBean= (RentRecordingBean) intent.getExtras().getSerializable("cardRecroding");

    }


    private void initview() {
        showMenu("取消申请");
        btn = (Button) findViewById(R.id.rent_return_btn);
        rent_recording_stuta = (RelativeLayout) findViewById(R.id.rent_recording_stuta);
        title = (TextView) findViewById(R.id.rent_recording_title);
        time = (TextView) findViewById(R.id.rent_recording_time);
        reason = (EditText) findViewById(R.id.rent_recording_edittext);

        title.setText(recordingBean.getDevname()+"申请审核中");
        SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy-MM-dd");
        Long recordingtime =Long.parseLong(recordingBean.getApplicateDT());
        Date date=new Date(recordingtime);
        time.setText(dateFormater.format(date));
        rent_recording_stuta.setVisibility(View.GONE);
    }
    private void bindclick() {
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDia();
            }
        });
    }

    private void showDia() {
        dialogBuilder.withTitle(getString(R.string.tips))
                .withMessage(getString(R.string.rent_return_hint1))
                .withEffect(Effectstype.Fadein).isCancelable(false)
                .withButton1Text(getString(R.string.cancel))
                .setButton1Click(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogBuilder.dismiss();
                    }
                }).withButton2Text(getString(R.string.ok))
                .setButton2Click(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogBuilder.dismiss();
                       submitCancleRent();
                    }
                }).show();
    }

    private void submitCancleRent() {
        AjaxParams params =new AjaxParams();
        params.put("PrjID",mUserInfo.PrjID+"");
        params.put("applicateID",recordingBean.getApplicateID()+"");
        params.put("reason",reason.getText().toString());

        params.put("loginCode",mUserInfo.TelPhone+","+mUserInfo.loginCode);
        params.put("phoneSystem", "Android");
        params.put("version", MyApp.versionCode);
        Log.d("RentReturnActivity", "params:" + params);

        new FinalHttp().get(Common.BASE_URL + "quzu", params, new AjaxCallBack<Object>() {
            @Override
            public void onSuccess(Object result) {
                super.onSuccess(result);
                Log.d("----------", "result:" + result);
                CardPackageResult cardPackageResult =new Gson().fromJson(result.toString(),CardPackageResult.class);
                if (cardPackageResult.getError_code().equals("0")){
                    finish();
                }else {
                    toast(cardPackageResult.getMessage());

                }
            }

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                toast(strMsg);
            }
        });
    }
}
