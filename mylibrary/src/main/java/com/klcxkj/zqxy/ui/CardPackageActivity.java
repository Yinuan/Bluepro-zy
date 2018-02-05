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
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.klcxkj.zqxy.MyApp;
import com.klcxkj.mylibrary.R;
import com.klcxkj.zqxy.adapter.CardPackageAdapter;
import com.klcxkj.zqxy.adapter.RepairPopAdapter;
import com.klcxkj.zqxy.common.Common;
import com.klcxkj.zqxy.databean.CardPackageAll;
import com.klcxkj.zqxy.databean.CardPackageAllResult;
import com.klcxkj.zqxy.databean.CardPackageData;
import com.klcxkj.zqxy.databean.CardPackageResult2;
import com.klcxkj.zqxy.databean.CardPackageType;
import com.klcxkj.zqxy.databean.PayResult;
import com.klcxkj.zqxy.fragment.TwoFragment;
import com.klcxkj.zqxy.response.PublicAliPayInfoData;
import com.klcxkj.zqxy.utils.GlobalTools;
import com.klcxkj.zqxy.utils.StatusBarUtil;
import com.klcxkj.zqxy.widget.Effectstype;
import com.klcxkj.zqxy.widget.MyGridView2;
import com.zzhoujay.richtext.CacheType;
import com.zzhoujay.richtext.RichText;

import net.android.tools.afinal.FinalHttp;
import net.android.tools.afinal.http.AjaxCallBack;
import net.android.tools.afinal.http.AjaxParams;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
 * description:卡片套餐
 */

public class CardPackageActivity extends  BaseActivity implements View.OnClickListener{

    private MyGridView2 gridView;
    private CardPackageAdapter packageAdapter;
    //默认布局
    private LinearLayout card_package_deafult;


    //套餐按钮。girdview有个动态高度问题理解不深，故没使用
    private LinearLayout layout1,layout2,layout3,layout4;
    private TextView monneyNum1,monneyNum2,monneyNum3,monneyNum4;
    private TextView count1,count2,count3,count4;

    //设备类型的选择
    private TextView dType;
    private RelativeLayout popLayout;
    private PopupWindow popupWindow;
    private List<CardPackageType> mdata;//设备
    private List<CardPackageAll> data;//月卡套餐信
    private List<CardPackageData> cardData ; //gridview的数据
    private int position1,position2;//选中的设备ID和月卡ID
    private Button submitBtn; //cardpackage_btn
    private CardPackageAll submitCardInfo; //选中的卡片信息

    //月卡套餐根布局
    private ScrollView rootLayout;

    //卡片套餐的两个说明
    private TextView cardTitle,cardContent;
    //卡片的默认选择类型
    private String devType;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_package);
        StatusBarUtil.setColor(this,getResources().getColor(R.color.base_color),0);
        initdata();
        initview();


    }



    private void initdata() {
        sp = getSharedPreferences("adminInfo", Context.MODE_PRIVATE);
       mUserInfo=Common.getUserInfo(sp);
        Intent intent =getIntent();
        if (intent.getStringExtra("DevType") !=null){
            devType =intent.getStringExtra("DevType");
            Log.d("CardPackageActivity","传过"+devType);
        }
        //获取有卡片套餐的设备ID（小类）
       loadCardPack(); //加载卡片套餐信息
        //loadCardInfo();

    }



    private void initview() {
        showMenu("月卡套餐");
        rightTxt.setText("申请退卡");
        if (TwoFragment.myCardNumber >0){
            rightTxt.setVisibility(View.GONE);
        }
        rightTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CardPackageActivity.this,ReturnCardActivity.class));
            }
        });
        packageAdapter =new CardPackageAdapter(this);
        gridView = (MyGridView2) findViewById(R.id.card_gridview);
        gridView.setAdapter(packageAdapter);
        dType = (TextView) findViewById(R.id.card_pack_decive_type);
        card_package_deafult = (LinearLayout) findViewById(R.id.card_package_deafult);
        popLayout = (RelativeLayout) findViewById(R.id.card_package_decive_type_layout);

        layout1 = (LinearLayout) findViewById(R.id.xiyi_package_1);
        layout2 = (LinearLayout) findViewById(R.id.xiyi_package_2);
        layout3 = (LinearLayout) findViewById(R.id.xiyi_package_3);
        layout4 = (LinearLayout) findViewById(R.id.xiyi_package_4);

        monneyNum1 = (TextView) findViewById(R.id.item_card_monney_1);
        monneyNum2 = (TextView) findViewById(R.id.item_card_monney_2);
        monneyNum3 = (TextView) findViewById(R.id.item_card_monney_3);
        monneyNum4 = (TextView) findViewById(R.id.item_card_monney_4);

        count1 = (TextView) findViewById(R.id.item_card_count_1);
        count2 = (TextView) findViewById(R.id.item_card_count_2);
        count3 = (TextView) findViewById(R.id.item_card_count_3);
        count4 = (TextView) findViewById(R.id.item_card_count_4);

        submitBtn = (Button) findViewById(R.id.cardpackage_btn);

        cardTitle =findViewById(R.id.cardpackage_title);
        cardContent =findViewById(R.id.cardpackage_content);

        rootLayout =findViewById(R.id.card_package_layout);

        layout1.setOnClickListener(this);
        layout2.setOnClickListener(this);
        layout3.setOnClickListener(this);
        layout4.setOnClickListener(this);
        dType.setOnClickListener(this);
        submitBtn.setOnClickListener(this);
        submitBtn.setEnabled(false);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

               if (cardData.get(position).getChosed() ==1){
                   cardData.get(position).setChosed(0);
                   submitCardInfo =null;
               }else {
                   for (int j = 0; j <cardData.size() ; j++) {
                       if (cardData.get(j).getChosed()==1){
                           cardData.get(j).setChosed(0);
                       }
                   }
                   cardData.get(position).setChosed(1);
                   submitCardInfo =cardData.get(position).getCardPackageAll();
               }
            //   packageAdapter.setList(cardData);
                packageAdapter.notifyDataSetChanged();

            }
        });
    }


    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.xiyi_package_1) {
            position2 = 0;
            layout1.setBackgroundResource(R.mipmap.card_package_chosed);
            layout2.setBackgroundResource(R.drawable.card_pakeage_unchoased);
            layout3.setBackgroundResource(R.drawable.card_pakeage_unchoased);
            layout4.setBackgroundResource(R.drawable.card_pakeage_unchoased);

        } else if (i == R.id.xiyi_package_2) {
            position2 = 1;
            layout2.setBackgroundResource(R.mipmap.card_package_chosed);
            layout1.setBackgroundResource(R.drawable.card_pakeage_unchoased);
            layout3.setBackgroundResource(R.drawable.card_pakeage_unchoased);
            layout4.setBackgroundResource(R.drawable.card_pakeage_unchoased);

        } else if (i == R.id.xiyi_package_3) {
            position2 = 2;
            layout3.setBackgroundResource(R.mipmap.card_package_chosed);
            layout1.setBackgroundResource(R.drawable.card_pakeage_unchoased);
            layout2.setBackgroundResource(R.drawable.card_pakeage_unchoased);
            layout4.setBackgroundResource(R.drawable.card_pakeage_unchoased);

        } else if (i == R.id.xiyi_package_4) {
            position2 = 3;
            layout4.setBackgroundResource(R.mipmap.card_package_chosed);
            layout1.setBackgroundResource(R.drawable.card_pakeage_unchoased);
            layout2.setBackgroundResource(R.drawable.card_pakeage_unchoased);
            layout3.setBackgroundResource(R.drawable.card_pakeage_unchoased);

        } else if (i == R.id.card_pack_decive_type) {
            if (popupWindow != null && popupWindow.isShowing()) {
                popupWindow.dismiss();
                Drawable rightDrawable = getResources().getDrawable(R.drawable.pull_down);
                rightDrawable.setBounds(0, 0, rightDrawable.getMinimumWidth(), rightDrawable.getMinimumHeight());
                dType.setCompoundDrawables(null, null, rightDrawable, null);
            } else {
                showPop1();
            }

        } else if (i == R.id.cardpackage_btn) {//购买月卡的操作在此
            //弹提示
            showDialogHint();


        }
    }

    private void showDialogHint() {
        if (dialogBuilder != null && dialogBuilder.isShowing()) {
            dialogBuilder.dismiss();
        }
        dialogBuilder.withTitle(getString(R.string.tips))
                .withMessage("购买完成后不可退卡,确定继续购买月卡?")
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
                        dialogBuilder.dismiss();
                        //购买卡片
                        getCardPackageOrderInfo();
                    }
                }).show();
    }


    private void updataUi() {
        int size =data.size();
        if (size >=4){
          count1.setText(data.get(0).getDescname());
            count2.setText(data.get(1).getDescname());
            count3.setText(data.get(2).getDescname());
            count4.setText(data.get(3).getDescname());

            monneyNum1.setText(data.get(0).getTypevalue());
            monneyNum2.setText(data.get(1).getTypevalue());
            monneyNum3.setText(data.get(2).getTypevalue());
            monneyNum4.setText(data.get(3).getTypevalue());
        }else {
            Toast.makeText(CardPackageActivity.this, "该设备的月卡套餐数量少于4个。--by·李·芬", Toast.LENGTH_SHORT).show();
        }
    }
    private void showPop1() {
        if (mdata ==null || mdata.size()==0){
            Toast.makeText(this, "未获取到该有月卡套餐的设备", Toast.LENGTH_SHORT).show();
            return;
        }
        final List<String> strList=new ArrayList<>();
        for (int i = 0; i <mdata.size() ; i++) {
            strList.add(mdata.get(i).getTypename());
        }
        View view1 = LayoutInflater.from(CardPackageActivity.this).inflate(R.layout.pop_repair_style, null);
        ListView listView = (ListView) view1.findViewById(R.id.pop_list);

        RepairPopAdapter adapter =new RepairPopAdapter(CardPackageActivity.this);
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
        popupWindow.showAsDropDown(popLayout);
        Drawable rightDrawable = getResources().getDrawable(R.drawable.pull_up);
        rightDrawable.setBounds(0, 0, rightDrawable.getMinimumWidth(), rightDrawable.getMinimumHeight());
        dType.setCompoundDrawables(null, null, rightDrawable, null);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                dType.setCompoundDrawables(null, null, null, null);
                dType.setText(strList.get(position));
                popupWindow.dismiss();
                //获取套餐数据
                position1 =position;
                //

                getCardPackageInfo(mdata.get(position).getTypeid());

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
    private void showNoNetwork() {
        if (dialogBuilder != null && dialogBuilder.isShowing()) {
            dialogBuilder.dismiss();
        }
        dialogBuilder.withTitle(getString(R.string.tips))
                .withMessage(getString(R.string.no_network))
                .withEffect(Effectstype.Fadein).isCancelable(false)
                .withButton2Text(getString(R.string.i_known))
                .setButton2Click(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogBuilder.dismiss();
                    }
                }).show();
    }
    /**
     * 获取有月卡套餐的设备类型
     *
     */
    private void loadCardPack() {
        loadingDialogProgress = GlobalTools.getInstance().showDailog(this,"加载.");
        AjaxParams params =new AjaxParams();
        params.put("PrjID",mUserInfo.PrjID+" ");//AccID
        params.put("AccID",mUserInfo.AccID+"");
        params.put("loginCode",mUserInfo.TelPhone+","+mUserInfo.loginCode);
        params.put("phoneSystem", "Android");
        params.put("version", MyApp.versionCode);
        Log.d("CardPackageActivity", "params:" + params);
        new FinalHttp().get(Common.BASE_URL + "yksb", params, new AjaxCallBack<Object>() {
            @Override
            public void onSuccess(Object result) {
                super.onSuccess(result);

                Log.d("CardPackageActivity", "result:11111" + result);
                CardPackageResult2 typeResult =new Gson().fromJson(result.toString(),CardPackageResult2.class);
                if (typeResult.getError_code().equals("0")) {
                    if (typeResult.getData()!=null && typeResult.getData().size()>0) {
                        mdata =typeResult.getData();
                        if (devType !=null && devType.length()>0){
                            for (int i = 0; i <mdata.size() ; i++) {
                                if (devType.equals(mdata.get(i).getTypeid()+"")){
                                    Drawable rightDrawable = getResources().getDrawable(R.drawable.pull_down);
                                    rightDrawable.setBounds(0, 0, rightDrawable.getMinimumWidth(), rightDrawable.getMinimumHeight());
                                    dType.setCompoundDrawables(null, null, rightDrawable, null);
                                    dType.setText(mdata.get(i).getTypename()); //设置默认值
                                    getCardPackageInfo(mdata.get(i).getTypeid());
                                }
                            }
                        }else {
                            //m默认显示第一个
                            Drawable rightDrawable = getResources().getDrawable(R.drawable.pull_down);
                            rightDrawable.setBounds(0, 0, rightDrawable.getMinimumWidth(), rightDrawable.getMinimumHeight());
                            dType.setCompoundDrawables(null, null, rightDrawable, null);
                            dType.setText(mdata.get(0).getTypename()); //设置默认值
                            getCardPackageInfo(mdata.get(0).getTypeid());
                        }

                    }else {
                        Log.d("CardPackageActivity", "1111111---没数据");
                        if (loadingDialogProgress !=null){
                            loadingDialogProgress.dismiss();
                        }
                    }
                }else if (typeResult.getError_code().equals("7")){
                    if (loadingDialogProgress !=null){
                        loadingDialogProgress.dismiss();
                    }
                    Common.logout2(CardPackageActivity.this, sp,dialogBuilder,typeResult.getMessage());
                }
                else {
                    if (loadingDialogProgress !=null){
                        loadingDialogProgress.dismiss();
                    }
                    Toast.makeText(CardPackageActivity.this, typeResult.getMessage(), Toast.LENGTH_SHORT).show();
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
     * 根据设备类型获取相关的卡片套餐
     * @param typeid
     */
    private void getCardPackageInfo(final int typeid) {
        AjaxParams params =new AjaxParams();
        params.put("PrjID",""+mUserInfo.PrjID);
        params.put("devtypeid",""+typeid);
        params.put("AccID",mUserInfo.AccID+"");
        params.put("loginCode",mUserInfo.TelPhone+","+mUserInfo.loginCode);
        params.put("phoneSystem", "Android");
        params.put("version", MyApp.versionCode);
        Log.d("CardPackageActivity", "params:" + params);
        new FinalHttp().get(Common.BASE_URL + "yktc", params, new AjaxCallBack<Object>() {
            @Override
            public void onSuccess(Object result) {
                super.onSuccess(result);
                Log.d("CardPackageActivity", "afinall数据=:" + result);

                CardPackageAllResult allResult =new Gson().fromJson(result.toString(),CardPackageAllResult.class);
                if (allResult.getError_code().equals("0")){
                    if (allResult.getData() !=null && allResult.getData().size()>0){

                        data =allResult.getData();
                        card_package_deafult.setVisibility(View.GONE);
                        gridView.setVisibility(View.VISIBLE);
                        //updataUi();
                      cardData =new ArrayList<CardPackageData>();
                        for (int i = 0; i <data.size() ; i++) {
                         CardPackageData cardpack =new CardPackageData(data.get(i),0);
                            cardData.add(cardpack);
                        }
                        cardData.get(0).setChosed(1);
                        submitCardInfo =cardData.get(0).getCardPackageAll();
                        packageAdapter.setList(cardData);
                        packageAdapter.notifyDataSetChanged();
                        submitBtn.setEnabled(true);
                        //刷新界面信息
                        cardTitle.setText(cardData.get(0).getCardPackageAll().getTitle());
                        String str=data.get(0).getRemark();
                        if (str !=null){
                            String msg =str.replaceAll("\t","");
                            RichText.from(msg)
                                    .showBorder(true)
                                    .cache(CacheType.all)
                                    .into(cardContent);
                        }
                        rootLayout.setVisibility(View.VISIBLE);
                    }else {
                        gridView.setVisibility(View.GONE);
                    }
                }else {
                    Toast.makeText(CardPackageActivity.this, allResult.getMessage(), Toast.LENGTH_SHORT).show();
                }
                if (loadingDialogProgress !=null){
                    loadingDialogProgress.dismiss();
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
     * 提交选择好的卡片订单
     */
    private void getCardPackageOrderInfo() {
        if (submitCardInfo ==null){
            toast("请选择月卡!");
            return;
        }
        if (Common.isNetWorkConnected(CardPackageActivity.this)){
          //  String str=data.get(position2).getTypevalue();
          //  String totalFee =str.substring(0,str.length()-1);
            String str =submitCardInfo.getTypevalue();
            String totalFee =str.substring(0,str.length()-1);
            AjaxParams params =new AjaxParams();
            params.put("wxid","0");
             params.put("totalFee",totalFee);
            params.put("PrjId",""+mUserInfo.PrjID);
            params.put("AccID",""+mUserInfo.AccID);
            params.put("zfAccID",""+mUserInfo.TelPhone);
            params.put("devtypeid",mdata.get(position1).getTypeid()+"");
            params.put("yktcmode",submitCardInfo.getTypeid()+"");
            params.put("markDescript","vghg");
            params.put("loginCode",mUserInfo.TelPhone+","+mUserInfo.loginCode);
            params.put("phoneSystem", "Android");
            params.put("version", MyApp.versionCode);
            Log.d("CardPackageActivity", "params:" + params);
            new FinalHttp().get(Common.BASE_URL + "getPayInforMonth", params, new AjaxCallBack<Object>() {
                @Override
                public void onSuccess(Object result) {
                    super.onSuccess(result);
                    Log.d("CardPackageActivity", result.toString());
                    final PublicAliPayInfoData aliPayInfoData =new Gson().fromJson(result.toString(),PublicAliPayInfoData.class);

                }

                @Override
                public void onFailure(Throwable t, int errorNo, String strMsg) {
                    super.onFailure(t, errorNo, strMsg);
                }
            });

        }else {
            Toast.makeText(CardPackageActivity.this, "网络连接失败，请检查你的网络", Toast.LENGTH_SHORT).show();
        }
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
                        Common.showToast(CardPackageActivity.this, R.string.zhifubao_pay_seccess, Gravity.BOTTOM);

                        Log.d("CardPackageActivity", "购买成功");
                       Intent intent =new Intent(CardPackageActivity.this,ReturnCardResultActivity.class);
                        intent.putExtra("card_do",ReturnCardResultActivity.CARD_BUY);
                        intent.putExtra("monney","");
                        Bundle bundle =new Bundle();
                        bundle.putSerializable("CardInfo",submitCardInfo);
                        intent.putExtras(bundle);
                        startActivity(intent);
                        finish();

                    } else {
                        // 判断resultStatus 为非“9000”则代表可能支付失败
                        // “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                        if (TextUtils.equals(resultStatus, "8000")) {
                            Common.showToast(CardPackageActivity.this, R.string.zhifubao_pay_process, Gravity.BOTTOM);

                        } else {
                            // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                            Common.showToast(CardPackageActivity.this, R.string.zhifubao_pay_failed, Gravity.BOTTOM);

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
                        intent.setClass(CardPackageActivity.this, MyInfoActivity.class);
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


    private void loadCardInfo(){
        OkHttpClient okHttpClient =new OkHttpClient();
        RequestBody requestBody =new FormBody.Builder()
                .add("PrjID",mUserInfo.PrjID+"")
                .add("AccID",""+mUserInfo.AccID)
                .add("loginCode",mUserInfo.TelPhone+","+mUserInfo.loginCode)
                .add("phoneSystem", "Android")
                .add("version",MyApp.versionCode)
                .build();
        Request request =new Request.Builder().url(Common.BASE_URL + "yksb").post(requestBody).build();
        Call call =okHttpClient.newCall(request);
        call.enqueue(new Callback() {

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d("CardPackageActivity", "okhttp:="+call.toString());
                Log.d("CardPackageActivity",  "okhttp:="+response.toString());
            }
            @Override
            public void onFailure(Call call, IOException e) {

            }


        });



    }
}
