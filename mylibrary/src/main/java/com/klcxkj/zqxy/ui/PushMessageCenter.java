package com.klcxkj.zqxy.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.klcxkj.zqxy.MyApp;
import com.klcxkj.mylibrary.R;
import com.klcxkj.zqxy.adapter.OneNewsListAdapter;
import com.klcxkj.zqxy.common.Common;
import com.klcxkj.zqxy.databean.MsgQueryList;
import com.klcxkj.zqxy.databean.MsgQuerySpread;
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

public class PushMessageCenter extends BaseActivity {

    private List<MsgQuerySpread> data =new ArrayList<>();
    private ListView listView;
    private OneNewsListAdapter newsAdapter;
    //下拉控件
    private SmartRefreshLayout smartRefreshLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_push_message_center);
        StatusBarUtil.setColor(this,getResources().getColor(R.color.base_color),0);
        initdata();
        initview();
    }

    private void initdata() {
        sp=getSharedPreferences("adminInfo", Context.MODE_PRIVATE);
        mUserInfo =Common.getUserInfo(sp);
        loadingDialogProgress=GlobalTools.getInstance().showDailog(this,"加载.");
        getMsgRecommed();
    }

    private void initview() {

        showMenu("推荐");
        smartRefreshLayout =findViewById(R.id.pull_refreshLayout);
        listView =findViewById(R.id.push_message_list);
        newsAdapter =new OneNewsListAdapter(this);
        listView.setAdapter(newsAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                if (Patterns.WEB_URL.matcher(data.get(position).getSpreadURL()).matches()) {
                    Log.d("OneFragment", data.get(position).getSpreadURL());
                    Intent intent = new Intent();
                    intent.setClass(PushMessageCenter.this, H5Activity.class);
                    intent.putExtra("h5_tag", H5Activity.QUERYSPREAD);
                    intent.putExtra("h5_url",data.get(position).getSpreadURL());
                    startActivity(intent);
                }


            }
        });

        smartRefreshLayout.setEnableLoadmore(true);
        smartRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                //获取推荐的消息
                num=1;
                data.clear();
                new  Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        //推广消息
                        getMsgRecommed();

                    }
                },1600);
                //推广消息
                refreshlayout.finishRefresh(2000);
                smartRefreshLayout.setEnableLoadmore(true);

            }
        });

        smartRefreshLayout.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                num++;
                new  Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        //推广消息
                        getMsgRecommed();

                    }
                },1600);
                refreshlayout.finishLoadmore(2000);
            }
        });
    }

    private int num =1;
    private void getMsgRecommed() {
        if (Common.isNetWorkConnected(PushMessageCenter.this)){
            AjaxParams params =new AjaxParams();
            params.put("PrjID",mUserInfo.PrjID+"");
            params.put("CurNum",""+num);

            params.put("loginCode",mUserInfo.TelPhone+","+mUserInfo.loginCode);
            params.put("phoneSystem", "Android");
            params.put("version", MyApp.versionCode);
            Log.d("PushMessageCenter", "params:" + params);
            new FinalHttp().get(Common.BASE_URL + "querySpread", params, new AjaxCallBack<Object>() {
                @Override
                public void onSuccess(Object result) {
                    super.onSuccess(result);
                    if (loadingDialogProgress !=null){
                        loadingDialogProgress.dismiss();
                    }
                    Log.d("PushMessageCenter", "result:" + result);
                    MsgQueryList queryList =new Gson().fromJson(result.toString(),MsgQueryList.class);
                    if (queryList.getError_code().equals("0")){
                        if (queryList.getData()!=null && queryList.getData().size()>0){
                            //数据节目刷新
                            for (int i = 0; i <queryList.getData().size() ; i++) {
                                data.add(queryList.getData().get(i));
                            }
                            if (newsAdapter !=null){
                                newsAdapter.setList(data);
                                newsAdapter.notifyDataSetChanged();
                            }

                            //将消息缓存起来以备网络不顺畅的时候显示。避免首页空白

                        }else {
                            Log.d("PushMessageCenter", "空推广");
                            smartRefreshLayout.setEnableLoadmore(false);
                        }
                    }else {
                        Toast.makeText(PushMessageCenter.this, queryList.getMessage(), Toast.LENGTH_SHORT).show();
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
        }else {
            Toast.makeText(PushMessageCenter.this, "网络连接失败，请检查网络", Toast.LENGTH_SHORT).show();
            if (loadingDialogProgress !=null){
                loadingDialogProgress.dismiss();
            }
        }
    }

}
