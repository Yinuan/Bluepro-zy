package com.klcxkj.zqxy.ui;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.google.gson.Gson;
import com.klcxkj.zqxy.MyApp;
import com.klcxkj.mylibrary.R;
import com.klcxkj.zqxy.adapter.RentRecrodingAdapter;
import com.klcxkj.zqxy.common.Common;
import com.klcxkj.zqxy.databean.RentRecordingBean;
import com.klcxkj.zqxy.databean.RentRecordingResult;
import com.klcxkj.zqxy.utils.GlobalTools;
import com.klcxkj.zqxy.utils.StatusBarUtil;

import net.android.tools.afinal.FinalHttp;
import net.android.tools.afinal.http.AjaxCallBack;
import net.android.tools.afinal.http.AjaxParams;

import java.util.List;
/**
 * autor:OFFICE-ADMIN
 * time:2017/11/17
 * email:yinjuan@klcxkj.com
 * description:租赁申请的历史纪录
 */

public class MyRentRecrodingActivity extends BaseActivity {

    private ListView lv;
    private RentRecrodingAdapter adpater;
    private List<RentRecordingBean> data;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_rent_recroding);
        StatusBarUtil.setColor(this,getResources().getColor(R.color.base_color),0);
        loadingDialogProgress = GlobalTools.getInstance().showDailog(this,"加载.");
        initdata();
        initview();
    }

    private void initdata() {
        sp = getSharedPreferences("adminInfo", Context.MODE_PRIVATE);
        mUserInfo=Common.getUserInfo(sp);
        AjaxParams params =new AjaxParams();
        params.put("PrjID",mUserInfo.PrjID+"");
        params.put("phone",mUserInfo.TelPhone+"");
        params.put("loginCode",mUserInfo.TelPhone+","+mUserInfo.loginCode);
        params.put("phoneSystem", "Android");
        params.put("version", MyApp.versionCode);
        new FinalHttp().get(Common.BASE_URL + "paq", params, new AjaxCallBack<Object>() {
            @Override
            public void onSuccess(Object result) {
                super.onSuccess(result);
                Log.d("----------", "reslt:" + result);
                if (loadingDialogProgress !=null){
                    loadingDialogProgress.dismiss();
                }

                RentRecordingResult recordingResult =new Gson().fromJson(result.toString(),RentRecordingResult.class);
                if (recordingResult.getError_code().equals("0")){
                    if (recordingResult.getData() !=null && recordingResult.getData().size()>0){
                         data =recordingResult.getData();
                    }
                    adpater.setList(data);
                    adpater.notifyDataSetChanged();

                }else {
                    toast(recordingResult.getMessage());
                }
            }

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                    toast(strMsg);
                if (loadingDialogProgress !=null){
                    loadingDialogProgress.dismiss();
                }

            }
        });
    }

    private void initview() {
        showMenu("申请记录");

        lv = (ListView) findViewById(R.id.rent_recroding_list);
        adpater =new RentRecrodingAdapter(this);

        lv.setAdapter(adpater);
    }
}
