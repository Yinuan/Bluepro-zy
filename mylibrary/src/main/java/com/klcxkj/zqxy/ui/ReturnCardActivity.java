package com.klcxkj.zqxy.ui;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.klcxkj.zqxy.MyApp;
import com.klcxkj.mylibrary.R;
import com.klcxkj.zqxy.adapter.RepairPopAdapter;
import com.klcxkj.zqxy.common.Common;
import com.klcxkj.zqxy.databean.CardPackageResult;
import com.klcxkj.zqxy.databean.CardpakageMine;

import net.android.tools.afinal.FinalHttp;
import net.android.tools.afinal.http.AjaxCallBack;
import net.android.tools.afinal.http.AjaxParams;

import java.util.ArrayList;
import java.util.List;

/**
 * autor:OFFICE-ADMIN
 * time:2017/11/17
 * email:yinjuan@klcxkj.com
 * description:申请退卡
 */
public class ReturnCardActivity extends BaseActivity {

    private EditText return_txt_acount; //返款账号

    private TextView deposit_device_type ;//选择的卡类型
    private TextView return_txt_monney; //月卡中的金额
    private Button btn_cancle,btn_ok; //

    //设备类型的选择
    private TextView dType;
    private RelativeLayout popLayout;
    private PopupWindow popupWindow;
    //

    //shuju
    private List<CardpakageMine> data;
    private CardpakageMine cardpakageMine;
    //月卡编号
    private String monthcardID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_return_card);

        initdata();
        initview();
        bindclick();
    }

    private void initdata() {
        sp = getSharedPreferences("adminInfo", Context.MODE_PRIVATE);
        mUserInfo=Common.getUserInfo(sp);
        getMyCardPackage();
    }


    private void initview() {
        showMenu("申请退卡");
        return_txt_acount = (EditText) findViewById(R.id.return_txt_acount);

        deposit_device_type = (TextView) findViewById(R.id.deposit_device_type);
        return_txt_monney = (TextView) findViewById(R.id.return_txt_monney);
        btn_cancle = (Button) findViewById(R.id.deposit_pull_cancle);
        btn_ok = (Button) findViewById(R.id.deposit_pull_btn);

        dType = (TextView) findViewById(R.id.deposit_device_type);
        popLayout = (RelativeLayout) findViewById(R.id.deposit_layout);

        return_txt_acount.setText(mUserInfo.TelPhone+"");
    }
    private void bindclick() {
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               /* Intent intent =new Intent(ReturnCardActivity.this,ReturnCardResultActivity.class);
                intent.putExtra("card_do",ReturnCardResultActivity.CARD_RETURN);
                intent.putExtra("monney","");
                startActivity(intent);
                finish();*/
               //提交退卡申请
                submitCardReturn();
            }
        });
        btn_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        dType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (popupWindow !=null && popupWindow.isShowing()){
                    popupWindow.dismiss();
                    Drawable rightDrawable = getResources().getDrawable(R.drawable.pull_down);
                    rightDrawable.setBounds(0, 0, rightDrawable.getMinimumWidth(), rightDrawable.getMinimumHeight());
                    dType.setCompoundDrawables(null, null, rightDrawable, null);
                }else {
                    showPop1();
                }
            }
        });
    }

    /**
     * 退还月卡
     */
    private void submitCardReturn() {
        AjaxParams params =new AjaxParams();
        params.put("PrjID",mUserInfo.PrjID+"");
        params.put("AccID",""+mUserInfo.AccID);
        params.put("tkAccID",""+mUserInfo.TelPhone);//退支付账号
        params.put("monthcardID",monthcardID);
        params.put("markDescript","1");
        params.put("loginCode",mUserInfo.TelPhone+","+mUserInfo.loginCode);
        params.put("phoneSystem", "Android");
        params.put("version", MyApp.versionCode);
        Log.d("ReturnCardActivity", "params:" + params);
        new FinalHttp().get(Common.BASE_URL + "tyk", params, new AjaxCallBack<Object>() {
            @Override
            public void onSuccess(Object result) {
                super.onSuccess(result);


            }

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                toast(strMsg);
            }
        });
    }

    /**
     * 获取我的月卡
     */
    private void getMyCardPackage() {

        AjaxParams params =new AjaxParams();
        params.put("PrjID",mUserInfo.PrjID+"");
        params.put("AccID",""+mUserInfo.AccID);
        params.put("loginCode",mUserInfo.TelPhone+","+mUserInfo.loginCode);
        params.put("phoneSystem", "Android");
        params.put("version", MyApp.versionCode);
        new FinalHttp().get(Common.BASE_URL + "yetc", params, new AjaxCallBack<Object>() {
            @Override
            public void onSuccess(Object result) {
                super.onSuccess(result);
                Log.d("----------", "result:" + result);
                CardPackageResult packageResult =new Gson().fromJson(result.toString(),CardPackageResult.class);
                if (packageResult.getError_code().equals("0")){
                    if (packageResult.getData() !=null && packageResult.getData().size()>0){

                        data =packageResult.getData();
                    }
                }

            }

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
            }
        });
    }
    private void showPop1() {
        if (data ==null ||data.size()==0){
            Toast.makeText(ReturnCardActivity.this, "暂无卡片可以办理退卡", Toast.LENGTH_SHORT).show();
            return;
        }
        final List<String> listData =new ArrayList<>();
        for (int i = 0; i <data.size() ; i++) {
            listData.add(data.get(i).getYKname());
        }
        View view1 = LayoutInflater.from(ReturnCardActivity.this).inflate(R.layout.pop_repair_style, null);
        ListView listView = (ListView) view1.findViewById(R.id.pop_list);
        RepairPopAdapter adapter =new RepairPopAdapter(ReturnCardActivity.this);
        adapter.setList(listData);
        listView.setAdapter(adapter);
        if (popupWindow ==null){
            popupWindow = new PopupWindow(view1, ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        }

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
        dType.setCompoundDrawables(null, null, rightDrawable, null);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                dType.setCompoundDrawables(null, null, null, null);
                dType.setText(data.get(position).getYKname());//月卡金额
                float a =Float.parseFloat(data.get(position).getYKmoney());
                float b =a/1000;
                return_txt_monney.setText(b+"");//钱
                monthcardID =data.get(position).getDeposittype()+"";
                cardpakageMine =data.get(position);
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
