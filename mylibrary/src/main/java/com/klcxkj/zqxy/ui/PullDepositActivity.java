package com.klcxkj.zqxy.ui;

import android.content.Context;
import android.content.Intent;
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
import com.klcxkj.zqxy.databean.DespoitBean;
import com.klcxkj.zqxy.databean.DespoitResult;
import com.klcxkj.zqxy.utils.GlobalTools;

import net.android.tools.afinal.FinalHttp;
import net.android.tools.afinal.http.AjaxCallBack;
import net.android.tools.afinal.http.AjaxParams;

import java.util.ArrayList;
import java.util.List;

/**
 * autor:OFFICE-ADMIN
 * time:2017/11/8
 * email:yinjuan@klcxkj.com
 * description:押金退还
 */
public class PullDepositActivity extends BaseActivity implements View.OnClickListener{

    private TextView dType;//设备类型
    private Button btn_ok,btn_cancle;
    private RelativeLayout layout1;
    private PopupWindow popupWindow;

    private   List<DespoitBean> data;//数据
    private EditText return_txt_acount; //返款账号

    private TextView pull_despoit_monney_count; //押金数额
    private String devtypeid; //押金ID
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pull_deposit);
        initdata();
        initview();
        bindclick();
        //获取项目押金
        getRentMonney();
    }

    private void initdata() {
        sp = getSharedPreferences("adminInfo", Context.MODE_PRIVATE);
        mUserInfo=Common.getUserInfo(sp);
    }


    private void initview() {
        showMenu("退押金");
        dType = (TextView) findViewById(R.id.deposit_device_type);
        layout1 = (RelativeLayout) findViewById(R.id.deposit_layout);
        btn_cancle = (Button) findViewById(R.id.deposit_pull_cancle);
        btn_ok = (Button) findViewById(R.id.deposit_pull_btn);
        return_txt_acount = (EditText) findViewById(R.id.return_txt_acount);
        pull_despoit_monney_count = (TextView) findViewById(R.id.pull_despoit_monney_count);
        btn_ok.setOnClickListener(this);
        btn_cancle.setOnClickListener(this);
        return_txt_acount.setText(mUserInfo.TelPhone+"");
        btn_ok.setEnabled(false);
    }

    private void bindclick() {
        layout1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (popupWindow !=null&& popupWindow.isShowing()){
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

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.deposit_pull_cancle) {
            finish();

        } else if (i == R.id.deposit_pull_btn) {
            submitDespoitReturn();

        }
    }

    /**
     * 退押金
     */
    private void submitDespoitReturn() {
        loadingDialogProgress =GlobalTools.getInstance().showDailog(PullDepositActivity.this,"准备中.");
        AjaxParams params =new AjaxParams();
        params.put("PrjID",""+mUserInfo.PrjID);
        params.put("AccID",""+mUserInfo.AccID);
        params.put("tkAccID",""+return_txt_acount.getText().toString());
        params.put("markDescript","1");
        params.put("devtypeid",devtypeid);

        params.put("loginCode",mUserInfo.TelPhone+","+mUserInfo.loginCode);
        params.put("phoneSystem", "Android");
        params.put("version", MyApp.versionCode);
        new FinalHttp().get(Common.BASE_URL + "deposit", params, new AjaxCallBack<Object>() {
            @Override
            public void onSuccess(Object result) {
                super.onSuccess(result);
                if (loadingDialogProgress !=null){
                    loadingDialogProgress.dismiss();
                }
                Log.d("PullDepositActivity", result.toString());
                CardPackageResult cardResult =new Gson().fromJson(result.toString(),CardPackageResult.class);
                if (cardResult.getError_code().equals("0")){
                    Intent intent =new Intent(PullDepositActivity.this,ReturnCardResultActivity.class);
                    intent.putExtra("card_do",ReturnCardResultActivity.DESPOIT_RETURN);
                    intent.putExtra("monney","");
                    startActivity(intent);
                    finish();
                }else {
                    Intent intent =new Intent(PullDepositActivity.this,ReturnCardResultActivity.class);
                    intent.putExtra("card_do",ReturnCardResultActivity.DESPOIT_RETURN_FAILED);
                    intent.putExtra("monney","");
                    startActivity(intent);
                    finish();
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


    private void getRentMonney() {
        sp = getSharedPreferences("adminInfo", Context.MODE_PRIVATE);
        mUserInfo = Common.getUserInfo(sp);
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
                Log.d("PullDepositActivity", "result:" + result);
                DespoitResult despoitResult =new Gson().fromJson(result.toString(),DespoitResult.class);
                if (despoitResult.getError_code().equals("0")){

                    if (despoitResult.getData()!=null && despoitResult.getData().size()>0) {
                            data =new ArrayList<DespoitBean>();
                        for (int i = 0; i <despoitResult.getData().size() ; i++) {
                            data.add(despoitResult.getData().get(i));
                        }

                    }else {  //没有押金的时候
                        btn_ok.setEnabled(false);
                        btn_cancle.setEnabled(false);
                        btn_cancle.setBackgroundResource(R.drawable.btn_gray_bg);
                        btn_ok.setBackgroundResource(R.drawable.btn_gray_bg);
                    }
                }else {
                    Toast.makeText(PullDepositActivity.this, despoitResult.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
            }
        });
    }

    private void showPop1() {
        if (data ==null || data.size() ==0){
            Toast.makeText(PullDepositActivity.this, "未获取到押金信息", Toast.LENGTH_SHORT).show();
            return;
        }
        List<String> listStr =new ArrayList<>();
        for (int i = 0; i <data.size() ; i++) {
            listStr.add(data.get(i).getRemark());
        }
        View view1 = LayoutInflater.from(PullDepositActivity.this).inflate(R.layout.pop_repair_style, null);
        ListView listView = (ListView) view1.findViewById(R.id.pop_list);

        RepairPopAdapter adapter =new RepairPopAdapter(PullDepositActivity.this);
        adapter.setList(listStr);
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

                dType.setCompoundDrawables(null, null, null, null);
                dType.setText(data.get(position).getRemark());//类型
                dType.setTextColor(getResources().getColor(R.color.text_color));
                //钱
                float a =Float.parseFloat(data.get(position).getSavedeposit());
                float b =a/1000;
                pull_despoit_monney_count.setText(""+b);
                //ID
                devtypeid =data.get(position).getDeposittype()+"";
                btn_ok.setEnabled(true);

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
