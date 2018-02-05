package com.klcxkj.zqxy.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.gson.Gson;
import com.klcxkj.zqxy.MyApp;
import com.klcxkj.mylibrary.R;
import com.klcxkj.zqxy.adapter.MessageCenterApater;
import com.klcxkj.zqxy.common.Common;
import com.klcxkj.zqxy.databean.MsgPush;
import com.klcxkj.zqxy.databean.MsgPushResult;
import com.klcxkj.zqxy.utils.GlobalTools;
import com.klcxkj.zqxy.utils.StatusBarUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import net.android.tools.afinal.FinalHttp;
import net.android.tools.afinal.http.AjaxCallBack;
import net.android.tools.afinal.http.AjaxParams;

import java.util.ArrayList;
import java.util.List;

public class  MessageCenterActivity extends BaseActivity {

    private ListView listView;
    private MessageCenterApater apater;
    private List<MsgPush> mdata =new ArrayList<>();

    private SmartRefreshLayout smartRefreshLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_center);
        StatusBarUtil.setColor(this,getResources().getColor(R.color.base_color),0);
        initdata();
        initview();
        bindclick();
    }



    private void initdata() {
        sp = getSharedPreferences("adminInfo", Context.MODE_PRIVATE);
        mUserInfo= Common.getUserInfo(sp);
        loadingDialogProgress = GlobalTools.getInstance().showDailog(this,"加载.");
        getMSgList();
    }



    private void initview() {
        showMenu("消息中心");
        listView = (ListView) findViewById(R.id.message_list);

        apater =new MessageCenterApater(this);

        listView.setAdapter(apater);

        smartRefreshLayout =findViewById(R.id.message_refreshLayout);
        smartRefreshLayout.setEnableLoadmore(true);
        smartRefreshLayout.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                num++;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getMSgList();
                    }
                },1600);
                refreshlayout.finishLoadmore(2000);
            }
        });
        smartRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                mdata.clear();
                num=1;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getMSgList();
                    }
                },1600);
                smartRefreshLayout.setEnableLoadmore(true);
                refreshlayout.finishRefresh(2000);
            }
        });




    }






    private void bindclick() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Intent intent =new Intent(MessageCenterActivity.this,MessageDetailActivity.class);
                Bundle bundle =new Bundle();
                bundle.putSerializable("MsgPush",mdata.get(position));
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }
    private int num =1;
    private void getMSgList() {
        AjaxParams params =new AjaxParams();
        params.put("PrjID",""+mUserInfo.PrjID);
        params.put("CurNum",""+num);
        params.put("loginCode",mUserInfo.TelPhone+","+mUserInfo.loginCode);
        params.put("phoneSystem", "Android");
        params.put("version", MyApp.versionCode);

        new FinalHttp().get(Common.BASE_URL + "queryPushMsg", params, new AjaxCallBack<Object>() {
            @Override
            public void onSuccess(Object result) {
                super.onSuccess(result);
                Log.d("----------", "result:" + result);
                if (loadingDialogProgress !=null){
                    loadingDialogProgress.dismiss();
                }
                MsgPushResult pushResult =new Gson().fromJson(result.toString(),MsgPushResult.class);
                if (pushResult.getData() !=null && pushResult.getData().size() >0){
                    for (int i = 0; i < pushResult.getData().size(); i++) {
                        mdata.add(pushResult.getData().get(i));
                    }

                    apater.setList(mdata);
                    apater.notifyDataSetChanged();
                }else {
                    smartRefreshLayout.setEnableLoadmore(false);
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
}
