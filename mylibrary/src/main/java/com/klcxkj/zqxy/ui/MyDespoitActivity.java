package com.klcxkj.zqxy.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
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
import com.klcxkj.zqxy.databean.DespoitBean;
import com.klcxkj.zqxy.databean.DespoitResult;
import com.klcxkj.zqxy.databean.PayResult;
import com.klcxkj.zqxy.databean.RentDecive;
import com.klcxkj.zqxy.databean.RentDeciveInfo;
import com.klcxkj.zqxy.utils.GlobalTools;
import com.klcxkj.zqxy.utils.StatusBarUtil;
import com.klcxkj.zqxy.widget.Effectstype;

import net.android.tools.afinal.FinalHttp;
import net.android.tools.afinal.http.AjaxCallBack;
import net.android.tools.afinal.http.AjaxParams;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


/**
 * autor:OFFICE-ADMIN
 * time:2017/11/8
 * email:yinjuan@klcxkj.com
 * description:押金充值
 */
public class MyDespoitActivity extends BaseActivity {


    private TextView dType;//设备类型
    private Button deposit_btn;
    private RelativeLayout layout1;
    private PopupWindow popupWindow;
    private List<RentDeciveInfo> rentData;
    private int position1;
    private TextView deposit_count; //押金数额
    private String type;
    private LinearLayout deposit_root_layout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_despoit);
        StatusBarUtil.setColor(this,getResources().getColor(R.color.base_color),0);
        initview();
        initdata();

        bindclick();
    }

    private void initdata() {
        Intent intent =getIntent();
        if (intent.getStringExtra("DevType") !=null){
            type =intent.getStringExtra("DevType");
            Log.d("MyDespoitActivity", "押金类型："+type);
        }
        sp = getSharedPreferences("adminInfo", Context.MODE_PRIVATE);
        mUserInfo = Common.getUserInfo(sp);
        loadingDialogProgress = GlobalTools.getInstance().showDailog(this,"加载.");

        getMyDespoit(); //获取我的押金
    }

    private void bindclick() {
        layout1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (popupWindow !=null){
                    if (popupWindow.isShowing()){
                        popupWindow.dismiss();
                        Drawable rightDrawable = getResources().getDrawable(R.drawable.pull_down);
                        rightDrawable.setBounds(0, 0, rightDrawable.getMinimumWidth(), rightDrawable.getMinimumHeight());
                        dType.setCompoundDrawables(null, null, rightDrawable, null);
                    }else {
                        showPop1();
                    }
                }else {
                    showPop1();
                }

            }
        });
        deposit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              //  showDialog_style1("此功能暂不开放,请在退押金时间段内办理");
               // getCardPackageOrderInfo();
            }
        });
    }



    private void initview() {
        showMenu("押金充值");

        dType = (TextView) findViewById(R.id.deposit_device_type);
        layout1 = (RelativeLayout) findViewById(R.id.deposit_layout);
        deposit_btn = (Button) findViewById(R.id.deposit_btn);
        deposit_count = (TextView) findViewById(R.id.deposit_count);
        deposit_root_layout =findViewById(R.id.deposit_root_layout);
        deposit_root_layout.setVisibility(View.GONE);
        deposit_btn.setEnabled(false);

        rightTxt.setText("申请退押金");
        rightTxt.setVisibility(View.VISIBLE);
        rightTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(MyDespoitActivity.this, PullDepositActivity.class));
            }
        });
    }

    /**
     * 获取可以交押金的设备id
     */
    private void getDesDeciveType(){

        OkHttpClient client =new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        RequestBody requestBody=new FormBody.Builder()
                .add("PrjID",""+mUserInfo.PrjID)
                .add("loginCode",mUserInfo.TelPhone+","+mUserInfo.loginCode)
                .add("phoneSystem", "Android")
                .add("version", MyApp.versionCode)
                .build();
        Request request =new Request.Builder()
                .url(Common.BASE_URL + "yjsb")
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
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        RentDecive rentDecive =new Gson().fromJson(result,RentDecive.class);
                        if (rentDecive.getError_code().equals("0")){
                            if (rentDecive.getData() !=null && rentDecive.getData().size()>0){
                                rentData =rentDecive.getData();
                                if (type !=null && type.length()>0){
                                    for (int i = 0; i <rentData.size() ; i++) {
                                        if (type.equals(rentData.get(i).getTypeid()+"")){//两个小类相等时
                                            Drawable rightDrawable = getResources().getDrawable(R.drawable.pull_down);
                                            rightDrawable.setBounds(0, 0, rightDrawable.getMinimumWidth(), rightDrawable.getMinimumHeight());
                                            dType.setCompoundDrawables(null, null, rightDrawable, null);
                                            dType.setText(rentData.get(i).getTypename());
                                            float monney =Float.parseFloat(rentData.get(i).getDictnum());
                                            float a = monney/1000;
                                            deposit_count.setText(a+""); //钱
                                            position1 =i;
                                            deposit_btn.setEnabled(true);
                                        }
                                    }
                                }else {
                                    //设备名 默认值第一个
                                    Drawable rightDrawable = getResources().getDrawable(R.drawable.pull_down);
                                    rightDrawable.setBounds(0, 0, rightDrawable.getMinimumWidth(), rightDrawable.getMinimumHeight());
                                    dType.setCompoundDrawables(null, null, rightDrawable, null);
                                    dType.setText(rentData.get(0).getTypename());
                                    float monney =Float.parseFloat(rentData.get(0).getDictnum());
                                    float a = monney/1000;
                                    deposit_count.setText(a+""); //钱
                                    position1 =0;
                                    deposit_btn.setEnabled(true);
                                    for (int i = 0; i <list.size() ; i++) {
                                        if (rentData.get(0).getTypeid()==list.get(i).getDeposittype()) {
                                            deposit_btn.setEnabled(false);
                                            deposit_btn.setBackgroundResource(R.drawable.btn_gray_bg);
                                        }
                                    }
                                }
                                deposit_root_layout.setVisibility(View.VISIBLE);

                            }else {
                                //  Toast.makeText(MyDespoitActivity.this, "暂无押金", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
            }
        });

    }


    private void showPop1() {
        if (rentData ==null || rentData.size()==0){
            Toast.makeText(this, "未获取到设备信息", Toast.LENGTH_SHORT).show();
           return;
        }
        List<String> strList=new ArrayList<>();
        for (int i = 0; i <rentData.size() ; i++) {
            strList.add(rentData.get(i).getTypename());
        }
        View view1 = LayoutInflater.from(MyDespoitActivity.this).inflate(R.layout.pop_repair_style, null);
        ListView listView = (ListView) view1.findViewById(R.id.pop_list);

        RepairPopAdapter adapter =new RepairPopAdapter(MyDespoitActivity.this);
        adapter.setList(strList);
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
        popupWindow.showAsDropDown(layout1);
        Drawable rightDrawable = getResources().getDrawable(R.drawable.pull_up);
        rightDrawable.setBounds(0, 0, rightDrawable.getMinimumWidth(), rightDrawable.getMinimumHeight());
        dType.setCompoundDrawables(null, null, rightDrawable, null);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                popupWindow.dismiss();
                //设备名与钱和位置
                Drawable rightDrawable = getResources().getDrawable(R.drawable.pull_down);
                rightDrawable.setBounds(0, 0, rightDrawable.getMinimumWidth(), rightDrawable.getMinimumHeight());
                dType.setCompoundDrawables(null, null, rightDrawable, null);
                dType.setText(rentData.get(position).getTypename());
                float monney =Float.parseFloat(rentData.get(position).getDictnum());
                float a = monney/1000;
                deposit_count.setText(a+""); //钱
                position1 =position;
                //置灰
                int type =rentData.get(position).getTypeid();
                if (list !=null && list.size()>0){
                    for (int i = 0; i <list.size() ; i++) {
                        if (type ==list.get(i).getDeposittype()){
                            deposit_btn.setEnabled(false);
                            deposit_btn.setBackgroundResource(R.drawable.btn_gray_bg);
                            return;
                        }
                    }
                }
                deposit_btn.setEnabled(true);
                deposit_btn.setBackgroundResource(R.drawable.login_btn_selecter);

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



    private static final int SDK_PAY_FLAG = 1;

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {

                    PayResult payResult = (PayResult) msg.obj;

                    // 支付宝返回此次支付结果及加签，建议对支付宝签名信息拿签约时支付宝提供的公钥做验签
                    // String resultInfo = payResult.getResult();

                    String resultStatus = payResult.getResultStatus();

                    // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                    if (TextUtils.equals(resultStatus, "9000")) {
                        Common.showToast(MyDespoitActivity.this, R.string.zhifubao_pay_seccess, Gravity.BOTTOM);

                        Log.d("MyDespoitActivity", "购买成功");
                        EventBus.getDefault().postSticky("MyDespoitActivity_sucess");
                        Intent intent =new Intent(MyDespoitActivity.this,ReturnCardResultActivity.class);
                        intent.putExtra("card_do",ReturnCardResultActivity.DESPOIT_RECHANGE);
                        intent.putExtra("monney",deposit_count.getText().toString());
                        startActivity(intent);
                        finish();


                    } else {
                        // 判断resultStatus 为非“9000”则代表可能支付失败
                        // “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                        if (TextUtils.equals(resultStatus, "8000")) {
                            Common.showToast(MyDespoitActivity.this, R.string.zhifubao_pay_process, Gravity.BOTTOM);

                        } else {
                            // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                            Common.showToast(MyDespoitActivity.this, R.string.zhifubao_pay_failed, Gravity.BOTTOM);

                        }
                    }
                    break;
                }
                default:
                    break;
            }
        }

    };

    private void showSaveDialog1() {
        dialogBuilder.withTitle(getString(R.string.tips))
                .withMessage(getString(R.string.save_idcard))
                .withEffect(Effectstype.Fadein).isCancelable(false)
                .withButton1Text(getString(R.string.cancel))
                .setButton1Click(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogBuilder.dismiss();

                    }
                }).withButton2Text(getString(R.string.sure))
                .setButton2Click(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent();
                        intent.setClass(MyDespoitActivity.this, MyInfoActivity.class);
                        intent.putExtra("is_admin", false);
                        startActivity(intent);
                        dialogBuilder.dismiss();

                    }
                }).show();
    }
    private void showSaveDialog2(String msg) {
        dialogBuilder.withTitle(getString(R.string.tips)).withMessage(msg)
                .withEffect(Effectstype.Fadein).isCancelable(false)
                .withButton1Text(getString(R.string.cancel))
                .withButton2Text(getString(R.string.i_known))
                .setButton2Click(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogBuilder.dismiss();

                    }
                }).show();
    }


    private List<DespoitBean> list =new ArrayList<>();
    private void getMyDespoit() {
        if (!Common.isNetWorkConnected(MyDespoitActivity.this)){
            if (loadingDialogProgress !=null){
                loadingDialogProgress.dismiss();
            }
            Common.showNoNetworkDailog(dialogBuilder, MyDespoitActivity.this);
            return;
        }
        AjaxParams params =new AjaxParams();
        params.put("PrjID",mUserInfo.PrjID+"");
        params.put("AccID",""+mUserInfo.AccID);
        params.put("loginCode",mUserInfo.TelPhone+","+mUserInfo.loginCode);
        params.put("phoneSystem", "Android");
        params.put("version", MyApp.versionCode);
        new FinalHttp().get(Common.BASE_URL + "pDeposit", params, new AjaxCallBack<Object>() {
            @Override
            public void onSuccess(Object result) {
                super.onSuccess(result);

                Log.d("MyDespoitActivity", result.toString());
                DespoitResult despoitResult =new Gson().fromJson(result.toString(),DespoitResult.class);
                if (despoitResult.getError_code().equals("0")){

                    if (despoitResult.getData()!=null && despoitResult.getData().size()>0) {

                        for (int j = 0; j <despoitResult.getData().size() ; j++) {
                            list.add(despoitResult.getData().get(j));
                        }
                    }
                }else if (despoitResult.getError_code().equals("7")){
                    Common.logout2(MyDespoitActivity.this, sp,dialogBuilder,despoitResult.getMessage());
                }
                getDesDeciveType(); //获取押金设备类型

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
