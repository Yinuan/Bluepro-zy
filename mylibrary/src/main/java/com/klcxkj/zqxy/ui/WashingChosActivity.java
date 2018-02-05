package com.klcxkj.zqxy.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.klcxkj.zqxy.MyApp;
import com.klcxkj.mylibrary.R;
import com.klcxkj.zqxy.common.Common;
import com.klcxkj.zqxy.databean.DeviceInfo;
import com.klcxkj.zqxy.databean.WashingOrderBean;
import com.klcxkj.zqxy.zxing.zxing.activity.CaptureActivity;

import net.android.tools.afinal.FinalHttp;
import net.android.tools.afinal.http.AjaxCallBack;
import net.android.tools.afinal.http.AjaxParams;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class WashingChosActivity extends BaseActivity {

    private LinearLayout serachBath ,scanBath,scanBtan2,queryBtn,queryBtn2;
    private RelativeLayout queryBtn3;
    private ImageView device_state_img; //洗澡图标
    private LinearLayout layout_unbind,layout_bind,layout_root;
    private TextView project_name;
    private TextView project_address;

    //显示预约信息
    private TextView washing_yuyue_address,washing_yuyue_address2;
    private TextView order_time,order_time2;
    private LinearLayout washing_order_item,washing_order_item2;

    private int diff;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_washing_chose);
        EventBus.getDefault().register(this);
        initview();
        initdata();
        bindclick();

        loadDevOrd();
    }


    private Handler mHandle =new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    diff =diff-1;
                    Log.d("WashingChosActivity", "diff1:" + diff);
                    //更新UI

                    if (diff%60==0){
                        order_time.setText(diff/60+"分钟");
                        order_time2.setText(diff/60+"分钟");
                    }
                    if (diff > 0) {
                        Message message = mHandle.obtainMessage(1);
                       mHandle.sendMessageDelayed(message,1000);
                    } else {
                        Message message = mHandle.obtainMessage(2);
                        mHandle.sendMessage(message);
                        mHandle.removeMessages(1);
                    }
                    break;

                case 2:
                    loadDevOrd();
                    Log.d("WashingChosActivity", "diff2:" + diff);
                    mHandle.removeMessages(2);
                    break;
            }
        }
    };

    private void initview() {
        showMenu("洗衣");
        //外部大布局
        layout_bind = (LinearLayout) findViewById(R.id.layout_binded);
        layout_root = (LinearLayout) findViewById(R.id.layout_root);
        layout_unbind = (LinearLayout) findViewById(R.id.layout_unbind);
        //未绑定的三个
        serachBath = (LinearLayout) findViewById(R.id.washing_chose_search);
        scanBath = (LinearLayout) findViewById(R.id.washing_chose_scan);
        queryBtn = (LinearLayout) findViewById(R.id.washing_chose_query);
        //绑定的
        project_name = (TextView) findViewById(R.id.project_name);
        device_state_img = (ImageView) findViewById(R.id.device_state_img);
        project_address = (TextView) findViewById(R.id.project_address);
        scanBtan2 = (LinearLayout) findViewById(R.id.bath_chose_scan2);
        queryBtn2 = (LinearLayout) findViewById(R.id.bath_chose_query2);
        queryBtn3 = (RelativeLayout) findViewById(R.id.bath_chose_query3);

        //预约的
        //1
        washing_yuyue_address =findViewById(R.id.washing_yuyue_address);
        order_time =findViewById(R.id.order_time);
        washing_order_item =findViewById(R.id.washing_order_item);
        //2
        washing_yuyue_address2 =findViewById(R.id.washing_yuyue_address2);
        order_time2 =findViewById(R.id.order_time2);
        washing_order_item2 =findViewById(R.id.washing_order_item2);
    }

    DeviceInfo deviceInfo;
    private void initdata() {
         sp = getSharedPreferences("adminInfo", Context.MODE_PRIVATE);
        mUserInfo=Common.getUserInfo(sp);
         deviceInfo = Common.getBindWashingDeviceInfo(sp);
        if (deviceInfo !=null){
            //绑定了洗澡水表
            layout_bind.setVisibility(View.VISIBLE);
            layout_unbind.setVisibility(View.GONE);
            project_name.setText(deviceInfo.PrjName);
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
                Intent in =new Intent(WashingChosActivity.this,CaptureActivity.class);
                in.putExtra("capture_type", CaptureActivity.CAPTURE_WASHING);
                startActivity(in);



            }
        });
        scanBtan2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in =new Intent(WashingChosActivity.this,CaptureActivity.class);
                in.putExtra("capture_type", CaptureActivity.CAPTURE_WASHING);
                startActivity(in);



            }
        });
        serachBath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in =new Intent(WashingChosActivity.this,SearchBratheDeviceActivity.class);
                in.putExtra("capture_type",CaptureActivity.CAPTURE_WASHING);
                startActivity(in);
            }
        });
        device_state_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(WashingChosActivity.this,WashingActivity.class);

                Bundle bundle =new Bundle();
                bundle.putSerializable("DeviceInfo",deviceInfo);
                intent.putExtras(bundle);
                startActivity(intent);
                finish();
            }
        });
        queryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(WashingChosActivity.this,WashingQueryActivity.class));
            }
        });
        queryBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (washingOrderBean !=null && washingOrderBean.getError_code().equals("0")){
                    return;
                }
                startActivity(new Intent(WashingChosActivity.this,WashingQueryActivity.class));
            }
        });
    }



    @Subscribe
    public void onEventMsg(String msg){
        if (msg.equals("washing_order_ok")){
            loadDevOrd();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
       mHandle.removeMessages(1);
        mHandle.removeMessages(2);
    }

    /**
     * 根据账号项目取设备预约信息
     */
    private  WashingOrderBean washingOrderBean;
    private void loadDevOrd(){
        AjaxParams params =new AjaxParams();
        params.put("AccID",""+mUserInfo.AccID);
        params.put("PrjID",""+mUserInfo.PrjID);
        params.put("loginCode",mUserInfo.TelPhone+","+mUserInfo.loginCode);
        params.put("phoneSystem", "Android");
        params.put("version", MyApp.versionCode);
        Log.d("WashingChosActivity", "params:" + params);
        new FinalHttp().get(Common.BASE_URL + "getDevOrder", params, new AjaxCallBack<Object>() {
            @Override
            public void onSuccess(Object result) {
                super.onSuccess(result);
                 washingOrderBean =new Gson().fromJson(result.toString(),WashingOrderBean.class);
                if (washingOrderBean!=null){
                    if (washingOrderBean.getError_code().equals("0")){
                        washing_order_item.setVisibility(View.VISIBLE);
                        washing_order_item2.setVisibility(View.VISIBLE);
                        //1;
                        washing_yuyue_address.setText(washingOrderBean.getDevtypeName());
                        order_time.setText(washingOrderBean.getHhastime()/60+"分钟");
                        //2
                        washing_yuyue_address2.setText(washingOrderBean.getDevtypeName());
                        order_time2.setText(washingOrderBean.getHhastime()/60+"分钟");
                        //开启倒计时

                        diff =washingOrderBean.getHhastime();
                        if (diff>0){
                            Message message = mHandle.obtainMessage(1);
                            mHandle.sendMessage(message);
                        }else {
                            washing_order_item.setVisibility(View.GONE);
                            washing_order_item2.setVisibility(View.GONE);
                        }

                    }else if (washingOrderBean.getError_code().equals("7")){
                        Common.logout2(WashingChosActivity.this, sp, dialogBuilder,washingOrderBean.getMessage());

                    }
                    else {
                        washing_order_item.setVisibility(View.GONE);
                        washing_order_item2.setVisibility(View.GONE);

                    }

                }

            }

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
            }
        });
    }

}
