package com.klcxkj.zqxy.ui;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.klcxkj.zqxy.MyApp;
import com.klcxkj.mylibrary.R;
import com.klcxkj.zqxy.adapter.RepairPopAdapter;
import com.klcxkj.zqxy.adapter.WashingQueryAdapter;
import com.klcxkj.zqxy.common.Common;
import com.klcxkj.zqxy.databean.AreaInfo;
import com.klcxkj.zqxy.databean.DeviceInfo;
import com.klcxkj.zqxy.databean.ResultMsg;
import com.klcxkj.zqxy.databean.WashingQueryBean;
import com.klcxkj.zqxy.databean.WashingQueryResult;
import com.klcxkj.zqxy.response.PublicAreaData;
import com.klcxkj.zqxy.utils.GlobalTools;
import com.klcxkj.zqxy.utils.StatusBarUtil;
import com.klcxkj.zqxy.widget.Effectstype;

import net.android.tools.afinal.FinalHttp;
import net.android.tools.afinal.http.AjaxCallBack;
import net.android.tools.afinal.http.AjaxParams;

import org.greenrobot.eventbus.EventBus;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class WashingQueryActivity extends BaseActivity implements View.OnClickListener {

    private WashingQueryAdapter adapter;
    private ListView listView;
    private TextView name1,name2,name3;

    private DeviceInfo deviceInfo;
    private List<WashingQueryBean> mData;

    private LinearLayout popLayout;
    private  List<AreaInfo>  areaInfos1;
    private  List<AreaInfo>  areaInfos2;
    private  List<AreaInfo>  areaInfos3;

    private int areaID1; //区域ID
    private int areaID2; //楼栋ID
    private int areaID3;  //楼层ID
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_washing_query);
        StatusBarUtil.setColor(this,getResources().getColor(R.color.base_color),0);
        initdata();
        initview();
        bindclick();
    }

    private DeviceInfo getDeviceInfo(){
        DeviceInfo deviceInfo1=null;
        if (Common.getBindWashingDeviceInfo(sp)!=null){
            deviceInfo1=  Common.getBindWashingDeviceInfo(sp);
        }
        return deviceInfo1;
    }

    private void initdata() {
        sp = getSharedPreferences("adminInfo", Context.MODE_PRIVATE);
        deviceInfo =getDeviceInfo();
        mUserInfo =Common.getUserInfo(sp);
        loadingDialogProgress = GlobalTools.getInstance().showDailog(this,"加载.");
        //初始化 区域ID
        if (deviceInfo !=null){ //有绑定洗衣机的时候
            areaID1=deviceInfo.QUID;
            areaID2=deviceInfo.LDID;
            areaID3=deviceInfo.LCID;
            loadAreaInfo(0,1);//加载区域信息
            loadAreaInfo(areaID1,2);//加载楼栋信息
            loadAreaInfo(areaID2,3);//加载楼层信息
        }else { //没有绑定洗衣机的时候
            loadAreaInfo(0,1);//加载区域信息
        }



    }

    private void initview() {
        showMenu("预约洗衣机");
        listView = (ListView) findViewById(R.id.washing_query_listview);
        name1 = (TextView) findViewById(R.id.query_address1_name);
        name2 = (TextView) findViewById(R.id.query_address2_name);
        name3 = (TextView) findViewById(R.id.query_address3_name);
        popLayout =findViewById(R.id.pop_layout);

        adapter =new WashingQueryAdapter(this);
        listView.setAdapter(adapter);

        if (deviceInfo !=null){
            name1.setText(deviceInfo.QUName);
            name2.setText(deviceInfo.LDName);
            name3.setText(deviceInfo.LCName);
            //根据位置查询预约信息
            loadDevByAddress();
        }

        name1.setOnClickListener(this);
        name2.setOnClickListener(this);
        name3.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.query_address1_name) {
            if (popupWindow != null && popupWindow.isShowing()) {
                popupWindow.dismiss();
                Drawable rightDrawable = getResources().getDrawable(R.drawable.pull_down);
                rightDrawable.setBounds(0, 0, rightDrawable.getMinimumWidth(), rightDrawable.getMinimumHeight());
                name1.setCompoundDrawables(null, null, rightDrawable, null);
            } else {
                showPop1(1);
            }

        } else if (i == R.id.query_address2_name) {
            if (popupWindow != null && popupWindow.isShowing()) {
                popupWindow.dismiss();
                Drawable rightDrawable = getResources().getDrawable(R.drawable.pull_down);
                rightDrawable.setBounds(0, 0, rightDrawable.getMinimumWidth(), rightDrawable.getMinimumHeight());
                name2.setCompoundDrawables(null, null, rightDrawable, null);
            } else {
                showPop1(2);

            }

        } else if (i == R.id.query_address3_name) {
            if (popupWindow != null && popupWindow.isShowing()) {
                popupWindow.dismiss();
                Drawable rightDrawable = getResources().getDrawable(R.drawable.pull_down);
                rightDrawable.setBounds(0, 0, rightDrawable.getMinimumWidth(), rightDrawable.getMinimumHeight());
                name3.setCompoundDrawables(null, null, rightDrawable, null);

            } else {
                showPop1(3);

            }

        }
    }
    private void bindclick() {

      adapter.setOnback(new WashingQueryAdapter.onSubscibe() {
          @Override
          public void onSubscibeCallBack(int numt) {
             //点击进行预约
              loadingDialogProgress = GlobalTools.getInstance().showDailog(WashingQueryActivity.this,"加载.");
              subscibeDecive(numt);

          }
      });
        
    }

    /**
     * 根据楼层查询预约消息
     */
    private void loadDevByAddress(){

        AjaxParams params =new AjaxParams();
        params.put("PrjID",mUserInfo.PrjID+"");
        params.put("DevArea",areaID1+"");
        params.put("DevBuild",areaID2+"");
        params.put("DevFloor",areaID3+"");
        params.put("loginCode",mUserInfo.TelPhone+","+mUserInfo.loginCode);
        params.put("phoneSystem", "Android");
        params.put("version", MyApp.versionCode);
        //Log.d("WashingQueryActivity", params.toString());
        new FinalHttp().get(Common.BASE_URL + "devquery", params, new AjaxCallBack<Object>() {
            @Override
            public void onSuccess(Object result) {
                super.onSuccess(result);
                if (loadingDialogProgress !=null){
                    loadingDialogProgress.dismiss();
                }
               // Log.d("WashingQueryActivity", "根据楼层查询:" + result);
                WashingQueryResult queryResult =new Gson().fromJson(result.toString(),WashingQueryResult.class);

                if (queryResult.getError_code().equals("0")) {
                    mData=new ArrayList<WashingQueryBean>();
                    if (queryResult.getData() !=null && queryResult.getData().size()>0){
                        for (int i = 0; i <queryResult.getData().size() ; i++) {
                            mData.add(queryResult.getData().get(i));

                        }
                    }
                    adapter.setList(mData);
                    adapter.notifyDataSetChanged();

                }else if (queryResult.getError_code().equals("7")){
                    Common.logout2(WashingQueryActivity.this, sp, dialogBuilder,queryResult.getMessage());
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


    /**
     * 提交预约
     * @param numt
     */
    private void subscibeDecive(int numt) {
        //没有绑定项目的时候
        AjaxParams params =new AjaxParams();
        params.put("PrjID",mUserInfo.PrjID+"");
        params.put("DevID",mData.get(numt).getDevID()+"");
        params.put("phone",mUserInfo.TelPhone+"");
        params.put("Devstartime","");
        params.put("loginCode",mUserInfo.TelPhone+","+mUserInfo.loginCode);
        params.put("phoneSystem", "Android");
        params.put("version", MyApp.versionCode);
        new FinalHttp().post(Common.BASE_URL + "devOrderadd", params, new AjaxCallBack<Object>() {
            @Override
            public void onSuccess(Object result) {
                super.onSuccess(result);
               // Log.d("WashingQueryActivity", "预约结果result:" + result);
                if (loadingDialogProgress !=null){
                    loadingDialogProgress.dismiss();
                }
                ResultMsg msg =new Gson().fromJson(result.toString(),ResultMsg.class);
                if (msg ==null || msg.getError_code() ==null){
                    return;
                }
              int code =Integer.parseInt(msg.getError_code());
                switch (code){
                    case 0: //正常
                        dialogBuilder.withTitle("提示")
                                .withMessage(msg.getMessage())
                                .withEffect(Effectstype.Fadein).isCancelable(false)
                                .withButton2Text("确定")
                                .setButton2Click(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dialogBuilder.dismiss();
                                        EventBus.getDefault().postSticky("washing_order_ok");
                                        finish();
                                    }
                                }).show();
                        break;
                    case 1:  //异常
                        showDialog(msg.getMessage());
                        break;
                    case 2:  //预约设备不是洗衣机
                        showDialog(msg.getMessage());
                        break;
                    case 3: //今天已经预约洗衣机了
                        showDialog(msg.getMessage());
                        break;
                    case 4://设备已经被预约
                        showDialog(msg.getMessage());
                        break;
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

    private void showDialog(String msg){
        dialogBuilder.withTitle("提示")
                .withMessage(msg)
                .withEffect(Effectstype.Fadein).isCancelable(false)
                .withButton2Text("确定")
                .setButton2Click(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogBuilder.dismiss();
                    }
                }).show();
    }

    /**
     * 加载区域信息
     * @param id
     * @param num
     */

    private void loadAreaInfo(int id, final int num){

        AjaxParams params =new AjaxParams();
        params.put("PrjID",""+mUserInfo.PrjID);
        params.put("AreaID",""+id);
        params.put("loginCode",mUserInfo.TelPhone+","+mUserInfo.loginCode);
        params.put("phoneSystem", "Android");
        params.put("version", MyApp.versionCode);
     //   Log.d("WashingQueryActivity", "params:" + params);
        new FinalHttp().get(Common.BASE_URL + "areainfo", params, new AjaxCallBack<Object>() {
            @Override
            public void onSuccess(Object result) {
                super.onSuccess(result);
               // Log.d("WashingQueryActivity", "区域信息:" + result);
                if (loadingDialogProgress !=null){
                    loadingDialogProgress.dismiss();
                }
                PublicAreaData publicGetData = new Gson().fromJson(result.toString(), PublicAreaData.class);
                if (publicGetData.error_code.equals("0")) {

                    Type listType = new TypeToken<ArrayList<AreaInfo>>() {}.getType();
                    switch (num){
                        case 1: //区域信息
                            areaInfos1 = new Gson().fromJson(publicGetData.arlist, listType);
                            //加载楼栋信息
                            if (deviceInfo==null){
                                loadAreaInfo(areaInfos1.get(0).AreaID,2);
                                name1.setText(areaInfos1.get(0).AreaName);
                            }

                            break;
                        case 2:
                            areaInfos2= new Gson().fromJson(publicGetData.arlist, listType);
                            //加载楼层信息
                            if (deviceInfo==null){
                                loadAreaInfo(areaInfos2.get(0).AreaID,3);
                                name2.setText(areaInfos2.get(0).AreaName);
                            }

                            break;
                        case 3:
                            areaInfos3 = new Gson().fromJson(publicGetData.arlist, listType);
                            break;
                    }


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

    private PopupWindow popupWindow =null;;
    private void showPop1(final int num) {

        final List<String> strList=new ArrayList<>();
        switch (num){
            case 1:
                if (areaInfos1 ==null || areaInfos1.size()==0){
                    toast("暂无数据");
                    return;
                }
                for (int i = 0; i <areaInfos1.size() ; i++) {
                    strList.add(areaInfos1.get(i).AreaName);
                }
                break;
            case 2:
                if (areaInfos2 ==null || areaInfos2.size()==0){
                    toast("暂无数据");
                    return;
                }
                for (int i = 0; i <areaInfos2.size() ; i++) {
                    strList.add(areaInfos2.get(i).AreaName);
                }
                break;
            case 3:
                if (areaInfos3 ==null || areaInfos3.size()==0){
                    toast("暂无数据");
                    return;
                }
                for (int i = 0; i <areaInfos3.size() ; i++) {
                    strList.add(areaInfos3.get(i).AreaName);
                }
                break;
        }

        View view1 = LayoutInflater.from(WashingQueryActivity.this).inflate(R.layout.pop_repair_style, null);
        ListView listView = (ListView) view1.findViewById(R.id.pop_list);

        RepairPopAdapter adapter =new RepairPopAdapter(WashingQueryActivity.this);
        adapter.setList(strList);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        popupWindow = new PopupWindow(view1, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        ColorDrawable cd = new ColorDrawable(0x7f0d0071);
        popupWindow.setBackgroundDrawable(cd);
        WindowManager.LayoutParams lp=getWindow().getAttributes();
        lp.alpha = Common.COLORSET;
        getWindow().setAttributes(lp);
        popupWindow.setFocusable(false);// 取得焦点
        //注意  要是点击外部空白处弹框消息  那么必须给弹框设置一个背景色  不然是不起作用的
        // 设置允许在外点击消失
        popupWindow.setOutsideTouchable(false);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        //点击外部消失
        //  popupWindow.setOutsideTouchable(true);
        //设置可以点击
        popupWindow.setTouchable(true);
        // 设置背景，这个是为了点击“返回Back”也能使其消失，并且并不会影响你的背景
        // 软键盘不会挡着popupwindow
        popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        //popupWindow.showAtLocation(view1, Gravity.CENTER, 0, 0);
        popupWindow.showAsDropDown(popLayout);
        Drawable rightDrawable = getResources().getDrawable(R.drawable.pull_up);
        rightDrawable.setBounds(0, 0, rightDrawable.getMinimumWidth(), rightDrawable.getMinimumHeight());
        Drawable rightDrawable2 = getResources().getDrawable(R.drawable.pull_down);
        rightDrawable2.setBounds(0, 0, rightDrawable.getMinimumWidth(), rightDrawable.getMinimumHeight());
        switch (num){
            case 1:
                name1.setCompoundDrawables(null, null, rightDrawable, null);
                name2.setCompoundDrawables(null, null, rightDrawable2, null);
                name3.setCompoundDrawables(null, null, rightDrawable2, null);
                break;
            case 2:
                name2.setCompoundDrawables(null, null, rightDrawable, null);
                name1.setCompoundDrawables(null, null, rightDrawable2, null);
                name3.setCompoundDrawables(null, null, rightDrawable2, null);
                break;
            case 3:
                name3.setCompoundDrawables(null, null, rightDrawable, null);
                name1.setCompoundDrawables(null, null, rightDrawable2, null);
                name2.setCompoundDrawables(null, null, rightDrawable2, null);
                break;
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Drawable rightDrawable = getResources().getDrawable(R.drawable.pull_down);
                rightDrawable.setBounds(0, 0, rightDrawable.getMinimumWidth(), rightDrawable.getMinimumHeight());
                switch (num){
                    case 1:
                        name1.setCompoundDrawables(null, null, rightDrawable, null);
                        name2.setCompoundDrawables(null, null, rightDrawable, null);
                        name3.setCompoundDrawables(null, null, rightDrawable, null);
                        name1.setText(strList.get(position));
                        name2.setText("");
                        name3.setText("");
                        areaID1 =areaInfos1.get(position).AreaID;
                        loadAreaInfo(areaID1,2);
                        break;
                    case 2:
                        name2.setCompoundDrawables(null, null, rightDrawable, null);
                        name3.setCompoundDrawables(null, null, rightDrawable, null);
                        name2.setText(strList.get(position));
                        name3.setText("");
                        areaID2 =areaInfos2.get(position).AreaID;
                        loadAreaInfo(areaID2,3);
                        break;
                    case 3:
                        name3.setCompoundDrawables(null, null, rightDrawable, null);
                        name3.setText(strList.get(position));
                        areaID3 =areaInfos3.get(position).AreaID;
                        loadingDialogProgress = GlobalTools.getInstance().showDailog(WashingQueryActivity.this,"加载.");
                        loadDevByAddress();
                        break;
                }

                popupWindow.dismiss();


            }
        });
        // 监听菜单的关闭事件
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {

            }
        });
        // 监听触屏事件
        popupWindow.setTouchInterceptor(new View.OnTouchListener() {
            public boolean onTouch(View view, MotionEvent event) {
                return false;
            }
        });
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {

            //在dismiss中恢复透明度
            public void onDismiss() {
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1f;
                getWindow().setAttributes(lp);
            }
        });
    }




}
