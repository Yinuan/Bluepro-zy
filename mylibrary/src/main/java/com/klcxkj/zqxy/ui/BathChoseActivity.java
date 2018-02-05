package com.klcxkj.zqxy.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.klcxkj.zqxy.MyApp;
import com.klcxkj.mylibrary.R;
import com.klcxkj.zqxy.common.Common;
import com.klcxkj.zqxy.databean.DeviceInfo;
import com.klcxkj.zqxy.response.PublicArrayData;
import com.klcxkj.zqxy.utils.GlobalTools;
import com.klcxkj.zqxy.zxing.zxing.activity.CaptureActivity;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class BathChoseActivity extends BaseActivity {

    private LinearLayout serachBath ,scanBath,scanBtan2;
    private ImageView device_state_img; //洗澡图标
    private LinearLayout layout_unbind,layout_bind,layout_root;
    private TextView project_name;
    private TextView project_address;
    private DeviceInfo deviceInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bath_chose);
        initview();
        initdata();
        bindclick();
    }



    private void initview() {
        showMenu("洗澡");
        layout_bind = (LinearLayout) findViewById(R.id.layout_binded);
        layout_unbind = (LinearLayout) findViewById(R.id.layout_unbind);
        layout_root = (LinearLayout) findViewById(R.id.layout_root);
        serachBath = (LinearLayout) findViewById(R.id.bath_chose_search);
        scanBath = (LinearLayout) findViewById(R.id.bath_chose_scan);
        scanBtan2 = (LinearLayout) findViewById(R.id.bath_chose_scan2);
        device_state_img = (ImageView) findViewById(R.id.device_state_img);
        project_address = (TextView) findViewById(R.id.project_address);
        project_name = (TextView) findViewById(R.id.project_name);

    }

    private void initdata() {
         sp = getSharedPreferences("adminInfo", Context.MODE_PRIVATE);
         deviceInfo = Common.getBindBratheDeviceInfo(sp);
        mUserInfo =Common.getUserInfo(sp);
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
                Intent in =new Intent(BathChoseActivity.this,CaptureActivity.class);

                in.putExtra("capture_type", CaptureActivity.CAPTURE_WATER);
                startActivity(in);



            }
        });
        scanBtan2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in =new Intent(BathChoseActivity.this,CaptureActivity.class);

                in.putExtra("capture_type", CaptureActivity.CAPTURE_WATER);
                startActivity(in);




            }
        });
        serachBath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in =new Intent(BathChoseActivity.this,SearchBratheDeviceActivity.class);
                in.putExtra("capture_type",CaptureActivity.CAPTURE_WATER);
                startActivity(in);

            }
        });
        device_state_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //根据mac地址获取最新的后台数据
                getMacAddress(deviceInfo.devMac);

            }
        });
    }

    private void getMacAddress(String address) {
        if (!Common.isNetWorkConnected(BathChoseActivity.this)){
            Common.showNoNetworkDailog(dialogBuilder,BathChoseActivity.this);
            return;
        }
        loadingDialogProgress = GlobalTools.getInstance().showDailog(BathChoseActivity.this,"读取中.");
        OkHttpClient client =new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        RequestBody requestBody=new FormBody.Builder()
                .add("PrjID",""+mUserInfo.PrjID)
                .add("deviceID_List","0")
                .add("deviceMac_List",address)
                .add("loginCode",mUserInfo.TelPhone+","+mUserInfo.loginCode)
                .add("phoneSystem", "Android")
                .add("version", MyApp.versionCode)
                .build();
        Request request =new Request.Builder()
                .url(Common.BASE_URL + "deviceInfo")
                .post(requestBody)
                .build();
       client.newCall(request).enqueue(new Callback() {
           @Override
           public void onFailure(Call call, IOException e) {
                if (loadingDialogProgress !=null){
                    loadingDialogProgress.dismiss();
                }
           }

           @Override
           public void onResponse(Call call, Response response) throws IOException {
               if (loadingDialogProgress !=null){
                   loadingDialogProgress.dismiss();
               }
                final String result =response.body().string();
           //    Log.d("BathChoseActivity", result);
               runOnUiThread(new Runnable() {
                   @Override
                   public void run() {
                       PublicArrayData publicArrayData = new Gson().fromJson(result, PublicArrayData.class);
                       if (publicArrayData.error_code.equals("0")){
                           Type listType = new TypeToken<ArrayList<DeviceInfo>>() {}.getType();
                           ArrayList<DeviceInfo> deviceInfos = new Gson().fromJson(publicArrayData.data, listType);
                           if (deviceInfos !=null && deviceInfos.size()>0){
                               DeviceInfo deviceInfo =deviceInfos.get(0);
                               SharedPreferences.Editor editor = sp.edit();
                               editor.putString(Common.USER_BRATHE + Common.getUserPhone(sp), new Gson().toJson(deviceInfo));
                               editor.commit();
                               Intent intent =new Intent(BathChoseActivity.this,Bath2Activity.class);
                               Bundle bundle =new Bundle();
                               bundle.putSerializable("DeviceInfo",deviceInfo);
                               intent.putExtras(bundle);
                               startActivity(intent);
                               finish();
                           }else {
                               toast("设备信息已失效，请扫码使用");
                           }
                       }else if (publicArrayData.error_code.equals("7")){
                           Common.logout2(BathChoseActivity.this, sp, dialogBuilder,publicArrayData.message);
                       }
                   }
               });
           }
       });
    }
}
