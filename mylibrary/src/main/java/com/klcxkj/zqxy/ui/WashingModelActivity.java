package com.klcxkj.zqxy.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.klcxkj.zqxy.MyApp;
import com.klcxkj.mylibrary.R;
import com.klcxkj.zqxy.adapter.WashingModelApater;
import com.klcxkj.zqxy.common.Common;
import com.klcxkj.zqxy.databean.DeviceInfo;
import com.klcxkj.zqxy.databean.WashingModelInfo;
import com.klcxkj.zqxy.databean.WashingModelResult;
import com.klcxkj.zqxy.utils.GlobalTools;
import com.klcxkj.zqxy.utils.StatusBarUtil;
import com.klcxkj.zqxy.widget.MyGridView2;

import net.android.tools.afinal.FinalHttp;
import net.android.tools.afinal.http.AjaxCallBack;
import net.android.tools.afinal.http.AjaxParams;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

public class WashingModelActivity extends BaseActivity  {

    private LinearLayout layout1,layout2,layout3,layout4;
    private List<WashingModelInfo> mData;
    private TextView my_monney;
    private WashingModelApater washingModelApater;
    private MyGridView2 gridView;
    private TextView monney;
    private ScrollView scrollView;

    private DeviceInfo mDeviceInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_washing_model);
        StatusBarUtil.setColor(this,getResources().getColor(R.color.base_color),0);
        initdata();
        initview();
        bindclick();
        EventBus.getDefault().register(this);
        //获取项目洗涤模式
        getWashingModel();
    }

    private void initdata() {
        mDeviceInfo = (DeviceInfo) getIntent().getExtras().getSerializable("mDeviceInfo");

        sp =getSharedPreferences("adminInfo", Context.MODE_PRIVATE);
        mUserInfo = Common.getUserInfo(sp);
    }

    private void bindclick() {
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
               WashingModelInfo washingModelInfo = mData.get(position);
                Intent intent =new Intent(WashingModelActivity.this,WashingOrderActivity.class);
                Bundle bundle =new Bundle();
                bundle.putSerializable("model_chose",washingModelInfo);
                intent.putExtras(bundle);
                startActivity(intent);
             //   setResult(RESULT_OK,intent);
              //  finish();
            }
        });
        monney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
            }
        });
    }


    private void initview() {
        showMenu("洗衣模式");
        washingModelApater =new WashingModelApater(this);

        my_monney = (TextView) findViewById(R.id.model_monney);
        gridView = (MyGridView2) findViewById(R.id.card_gridview);
        monney= (TextView) findViewById(R.id.model_monney_no);
        my_monney.setText(Common.getShowMonty(mUserInfo.AccMoney + mUserInfo.GivenAccMoney, getString(R.string.yuan1)));

        scrollView =findViewById(R.id.scrollView_model);

    }


    /**
     *获取洗衣模式
     */
    private void getWashingModel() {
        if (mDeviceInfo ==null ){
            toast("设备信息:NULL");
            return;
        }
        loadingDialogProgress = GlobalTools.getInstance().showDailog(this,"加载..");
        AjaxParams params =new AjaxParams();
        params.put("PrjID",""+mDeviceInfo.PrjID);
        params.put("devType",mDeviceInfo.DevTypeID+"");
        params.put("loginCode",mUserInfo.TelPhone+","+mUserInfo.loginCode);
        params.put("phoneSystem", "Android");
        params.put("version", MyApp.versionCode);
        new FinalHttp().get(Common.BASE_URL + "xyms", params, new AjaxCallBack<Object>() {
            @Override
            public void onSuccess(Object result) {
                super.onSuccess(result);
                if (loadingDialogProgress !=null){
                    loadingDialogProgress.dismiss();
                }
                WashingModelResult modelResult =new Gson().fromJson(result.toString(),WashingModelResult.class);
                if (modelResult.getError_code().equals("0")){
                    if (modelResult.getData() !=null && modelResult.getData().size()>0){
                        scrollView.setVisibility(View.VISIBLE);
                        mData =modelResult.getData();
                        washingModelApater.setList(mData);
                        gridView.setAdapter(washingModelApater);
                    }else {
                        toast("洗衣模式未设置!");
                    }
                }else {
                    toast(modelResult.getMessage());
                }

            }

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                if (loadingDialogProgress !=null){
                    loadingDialogProgress.dismiss();
                }
            }
        });
    }

    @Subscribe
    public void onEventMsg(String mes){
        if (mes.equals("order_ok")){
            finish();
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
