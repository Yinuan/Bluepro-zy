package com.klcxkj.zqxy.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.klcxkj.mylibrary.R;
import com.klcxkj.zqxy.adapter.OneNewsListAdapter;
import com.klcxkj.zqxy.databean.MsgQuerySpread;
import com.klcxkj.zqxy.ui.BathChoseActivity;
import com.klcxkj.zqxy.ui.MessageCenterActivity;
import com.klcxkj.zqxy.ui.MyBillActivity;
import com.klcxkj.zqxy.ui.PushMessageCenter;
import com.klcxkj.zqxy.ui.WashingChosActivity;
import com.klcxkj.zqxy.widget.MyListView;
import com.klcxkj.zqxy.zxing.zxing.activity.CaptureActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

/**
 * autor:OFFICE-ADMIN
 * time:2017/11/1
 * email:yinjuan@klcxkj.com
 * description: 用户登录默认第一碎片
 */

public class OneFragment extends BaseFragment implements View.OnClickListener{

    private View rootView;// 缓存Fragment view

    //


    private MyListView listview;
    private OneNewsListAdapter newsAdapter;
    private List<MsgQuerySpread> data =new ArrayList<>();

    //顶部两大菜单
    private LinearLayout one_rechange;
    private LinearLayout one_scan;
    //中间6大菜单
    private RelativeLayout one_xizao;
    private RelativeLayout one_xiyi;
    private RelativeLayout one_yinshui;
    private RelativeLayout one_chuifengji;
    private RelativeLayout one_chongdian;
    private RelativeLayout one_other;

    private RelativeLayout one_bill;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //极光注册
        EventBus.getDefault().register(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootView ==null){
            rootView =inflater.inflate(R.layout.fragment_one,container,false);
            initview(rootView);

        }

        return rootView;
    }


    private void initview(View rootView) {
        //

       LinearLayout backLayout = (LinearLayout)rootView. findViewById(R.id.top_btn_back);
        backLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               getActivity().finish();
            }
        });
        TextView title = (TextView)rootView. findViewById(R.id.menu_title);
        title.setText("蓝牙水表");

        //two menu
        one_rechange = (LinearLayout) rootView.findViewById(R.id.one_rechange);
        one_scan = (LinearLayout) rootView.findViewById(R.id.one_scan);

        //six menu
        one_xizao = (RelativeLayout) rootView.findViewById(R.id.one_xizao);
        one_xiyi = (RelativeLayout) rootView.findViewById(R.id.one_xiyi);
        one_yinshui = (RelativeLayout) rootView.findViewById(R.id.one_yinshui);
        one_chuifengji = (RelativeLayout) rootView.findViewById(R.id.one_chuifengji);
        one_chongdian = (RelativeLayout) rootView.findViewById(R.id.one_chongdian);
        one_other = (RelativeLayout) rootView.findViewById(R.id.one_other);


        one_bill =rootView.findViewById(R.id.my_bill_layout);








    }
    @Subscribe
   public void onEvent(String msg){
       if (msg.equals("login_success")){
           one_xizao.setOnClickListener(this);
           one_xiyi.setOnClickListener(this);
           one_yinshui.setOnClickListener(this);
           one_chuifengji.setOnClickListener(this);
           one_chongdian.setOnClickListener(this);
           one_other.setOnClickListener(this);
           one_bill.setOnClickListener(this);
       }
   }


    @Override
    public void onClick(View view) {
        String code;
        Intent intent ;
        int i = view.getId();
        if (i == R.id.one_xizao) {
            intent = new Intent(getActivity(), BathChoseActivity.class);
            startActivity(intent);

        } else if (i == R.id.one_xiyi) {
            startActivity(new Intent(getActivity(), WashingChosActivity.class));

        } else if (i == R.id.one_yinshui) {
            intent = new Intent(getActivity(), CaptureActivity.class);
            intent.putExtra("capture_type", CaptureActivity.CAPTURE_DRINK);
            startActivity(intent);

        } else if (i == R.id.one_chuifengji) {
            intent = new Intent(getActivity(), CaptureActivity.class);
            intent.putExtra("capture_type", CaptureActivity.CAPTURE_DRYER);
            startActivity(intent);

        } else if (i == R.id.one_chongdian) {
            intent = new Intent(getActivity(), CaptureActivity.class);
            intent.putExtra("capture_type", CaptureActivity.CAPTURE_ELE);
            startActivity(intent);

        } else if (i == R.id.one_other) {
            intent = new Intent(getActivity(), CaptureActivity.class);
            intent.putExtra("capture_type", CaptureActivity.CAPTURE_OTHER);
            startActivity(intent);

        } else if (i == R.id.one_rechange) {


        } else if (i == R.id.one_scan) {
            intent = new Intent(getActivity(), CaptureActivity.class);
            intent.putExtra("capture_type", CaptureActivity.CAPTURE_OTHER);
            startActivity(intent);

        } else if (i == R.id.one_more_message) {
            startActivity(new Intent(getActivity(), MessageCenterActivity.class));

        } else if (i == R.id.query_push_message) {
            startActivity(new Intent(getActivity(), PushMessageCenter.class));

        }else if (i==R.id.my_bill_layout){
            startActivity(new Intent(getActivity(), MyBillActivity.class));
        }
    }




    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }


}
