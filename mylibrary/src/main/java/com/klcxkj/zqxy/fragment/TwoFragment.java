package com.klcxkj.zqxy.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.klcxkj.zqxy.MyApp;
import com.klcxkj.mylibrary.R;
import com.klcxkj.zqxy.common.Common;
import com.klcxkj.zqxy.databean.CardPackageResult;
import com.klcxkj.zqxy.databean.CardpakageMine;
import com.klcxkj.zqxy.databean.DespoitBean;
import com.klcxkj.zqxy.databean.DespoitResult;
import com.klcxkj.zqxy.databean.UserInfo;
import com.klcxkj.zqxy.response.PublicGetData;
import com.klcxkj.zqxy.ui.CardPackageActivity;
import com.klcxkj.zqxy.ui.MyDespoitActivity;
import com.klcxkj.zqxy.ui.SearchBratheDeviceActivity;
import com.klcxkj.zqxy.viewpager.CardPagerAdapter;
import com.klcxkj.zqxy.viewpager.ShadowTransformer;
import com.klcxkj.zqxy.widget.Effectstype;
import com.klcxkj.zqxy.zxing.zxing.activity.CaptureActivity;

import net.android.tools.afinal.FinalHttp;
import net.android.tools.afinal.http.AjaxCallBack;
import net.android.tools.afinal.http.AjaxParams;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

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
 * time:2017/11/2
 * email:yinjuan@klcxkj.com
 * description:
 */

public class TwoFragment extends BaseFragment {
    
    private View rootView;
    //卡片
    private ViewPager mViewPager;
    private CardPagerAdapter cAdapter;
    public static int myCardNumber =0;

    private ShadowTransformer mCardShadowTransformer;
    //卡片信息
    private TextView cUser;  //剩余使用次数/天数
    private  TextView cCount,cPermonney;//卡片购买，卡片使用次数，卡片预充值
    private Button cBuy,cRechange;
    //我的月卡集合
    private List<CardpakageMine> myCardData ;
    //我的余额
    private TextView my_monney;
    //选择中的卡片的类型ID
    private String devType;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EventBus.getDefault().register(this);

    }

    private void initdata() {

        //获取押金缴纳的情况
        getMyDespoit();

    }



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootView ==null){
            rootView =inflater.inflate(R.layout.fragment_two,container,false);
            initview(rootView);
            bindclick();
            initdata();
        }
        mUserInfo =Common.getUserInfo(sp);
        //钱
        updateUserInfo(mUserInfo);
        //获取自己的卡片套餐
        getMyCardPackage();
        return rootView;
    }



    private void initview(View rootView) {
        cBuy = (Button) rootView.findViewById(R.id.card_use_buy);
        cCount = (TextView) rootView.findViewById(R.id.card_use_count);
        cPermonney = (TextView) rootView.findViewById(R.id.card_use_permonney);
        cUser = (TextView) rootView.findViewById(R.id.card_use_info);
        cRechange = (Button) rootView.findViewById(R.id.card_use_rechange);
        my_monney = (TextView) rootView.findViewById(R.id.my_monney);
        mViewPager = (ViewPager) rootView.findViewById(R.id.viewPager);

        cAdapter =new CardPagerAdapter();
        cAdapter.addCardItem(new CardpakageMine());
        mCardShadowTransformer = new ShadowTransformer(mViewPager, cAdapter);
        mCardShadowTransformer.enableScaling(false);
        mViewPager.setAdapter(cAdapter);
        mViewPager.setPageTransformer(false, mCardShadowTransformer);
        mViewPager.setOffscreenPageLimit(3);

//        my_monney.setText(Common.getShowMonty(mUserInfo.AccMoney + mUserInfo.GivenAccMoney, getString(R.string.yuan1)));




    }

    private void bindclick() {
        cBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean is_bind_account = Common.isBindAccount(sp);
                if (is_bind_account){
                    Intent intent =new Intent(getActivity(), CardPackageActivity.class);
                    if (cBuy.getText().toString().equals("续费")){
                        intent.putExtra("DevType",devType);
                    }
                    startActivity(intent);
                }else {
                    showBindDialog();
                }


            }
        });
        cPermonney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                boolean is_bind_account = Common.isBindAccount(sp);
                if (is_bind_account){
                    startActivity(new Intent(getActivity(), MyDespoitActivity.class));
                }else {
                    showBindDialog();
                }
                //押金



            }
        });

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                //更新试图
                Log.d("TwoFragment", "position:" + position);
                if (position !=0){
                    cUser.setText("剩余使用次数/天数");
                    cCount.setText(myCardData.get(position-1).getMonthHadTimes()+"次/"+myCardData.get(position-1).getMonthbuyday()+"天");
                    cCount.setVisibility(View.VISIBLE);
                    cBuy.setText("续费");
                    devType =myCardData.get(position-1).getDepositxtype()+"";
                }else if (position==0){
                    cUser.setText("购买月卡");
                    cCount.setVisibility(View.GONE);
                    cBuy.setText("购卡");
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        cRechange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        //卡片上的购买按钮回掉
        cAdapter.setOnback(new CardPagerAdapter.onSubscibe() {
            @Override
            public void onSubscibeCallBack() {
                boolean is_bind_account = Common.isBindAccount(sp);
                if (is_bind_account){
                    Intent intent =new Intent(getActivity(), CardPackageActivity.class);
                    startActivity(intent);
                }else {
                    showBindDialog();
                }


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
                Log.d("TwoFragment", "我的月卡:" + result);
                if (result==null ){
                    return;
                }
                CardPackageResult packageResult =null;
                try {
                     packageResult =new Gson().fromJson(result.toString(),CardPackageResult.class);
                    if (packageResult ==null ){
                        Toast.makeText(getActivity(), "服务器错误,json数据格式不正确", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }catch (JsonSyntaxException e){
                    Toast.makeText(getActivity(), "服务器错误,json数据格式不正确", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (packageResult.getError_code().equals("0")){
                    if (packageResult.getData() !=null && packageResult.getData().size()>0){
                        myCardNumber =packageResult.getData().size();
                        if (cAdapter ==null){
                            return;
                        }
                        cAdapter.clearCardItem(new CardpakageMine());
                        myCardData =new ArrayList<>();
                        for (int i = 0; i <packageResult.getData().size() ; i++) {
                          CardpakageMine info =packageResult.getData().get(i);
                            myCardData.add(info);
                           cAdapter.addCardItem(info);
                        }

                        cAdapter.notifyDataSetChanged();
                        mViewPager.setCurrentItem(1);

                    }
                }

            }

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
            }
        });
    }

    /**
     * 获取我的押金缴纳情况
     */
    private int statu;
    private void getMyDespoit() {
        mUserInfo =Common.getUserInfo(sp);
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
                if (getActivity() ==null && result ==null){
                    return;
                }
                DespoitResult despoitResult =null;
               try {
                   despoitResult =new Gson().fromJson(result.toString(),DespoitResult.class);
                   if (despoitResult ==null ){
                       Toast.makeText(getActivity(), "服务器错误,json数据格式不正确", Toast.LENGTH_SHORT).show();
                       return;
                   }
               }catch (JsonSyntaxException e){
                   Toast.makeText(getActivity(), "服务器错误,json数据格式不正确", Toast.LENGTH_SHORT).show();
                   return;
               }

                if (despoitResult.getError_code().equals("0")){

                    if (despoitResult.getData()!=null && despoitResult.getData().size()>0) {
                        List<DespoitBean> list =despoitResult.getData();
                        DespoitBean bean =list.get(0);
                       /* statu =bean.getStatus();
                       switch (statu){
                           case 0: //未使用
                               cPermonney.setText("未交纳");
                               break;
                           case 1: //正在使用
                               cPermonney.setText("已交纳");
                               break;
                           case 2://申请退还
                               cPermonney.setText("申请退还中");
                               break;
                           case 3: //退还成功
                               cPermonney.setText("未交纳");
                               break;
                           case 4://其他
                               cPermonney.setText("未交纳");
                               break;
                       }*/
                    }else {
                      //  cPermonney.setText("未交纳");
                    }
                }else {
                    Toast.makeText(getActivity(), despoitResult.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
            }
        });
    }

    @Subscribe
    public void onGetEvent(String message){
        if (message.equals("MyDespoitActivity_sucess")){
            getMyDespoit();

        }else if (message.equals("cardPackage_scuess")){
            getMyCardPackage();
        }
    }
    /**
     * 更新用户信息
     * @param mInfo
     */
    private void updateUserInfo(final UserInfo mInfo) {

        if (!Common.isNetWorkConnected(getActivity())){
            Common.showNoNetworkDailog(dialogBuilder,getActivity());
            return;
        }
        OkHttpClient client =new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        RequestBody requestBody=new FormBody.Builder()
                .add("TelPhone",""+mInfo.TelPhone)
                .add("PrjID",""+mInfo.PrjID)
                .add("WXID","0")
                .add("loginCode",mInfo.TelPhone + "," + mInfo.loginCode)
                .add("isOpUser","0")
                .add("phoneSystem", "Android")
                .add("version",MyApp.versionCode)

                .build();
        Request request =new Request.Builder()
                .url(Common.BASE_URL + "accountInfo")
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String result =response.body().string();
                Log.d("TwoFragment", result);
                if (result ==null){
                    return;
                }
                if (getActivity() !=null){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            PublicGetData publicGetData =null;
                                try {
                                    publicGetData = new Gson().fromJson(result, PublicGetData.class);
                                    if (publicGetData ==null){
                                        Toast.makeText(getActivity(), "服务器错误,json数据格式不正确", Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                }catch (JsonSyntaxException e){
                                    Toast.makeText(getActivity(), "服务器错误,json数据格式不正确", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                if (publicGetData.error_code.equals("0")) {

                                     UserInfo info = new Gson().fromJson(publicGetData.data, UserInfo.class);
                                    info.loginCode = mInfo.loginCode;
                                    if (info != null) {
                                        SharedPreferences.Editor editor = sp.edit();
                                        editor.putString(Common.USER_PHONE_NUM, info.TelPhone + "");
                                        editor.putString(Common.USER_INFO, new Gson().toJson(info));
                                        editor.putInt(Common.ACCOUNT_IS_USER, info.GroupID);
                                        editor.commit();
                                        my_monney.setText(Common.getShowMonty(info.AccMoney+info.GivenAccMoney, getString(R.string.yuan1)));
                                    }

                                }else if (publicGetData.error_code.equals("7")){
                                    Common.logout2(getActivity(), sp, dialogBuilder,publicGetData.message);

                                }



                        }
                    });
                }

            }
        });

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);

    }

    private void showBindDialog() {
        dialogBuilder.withTitle(getString(R.string.tips))
                .withMessage(getString(R.string.bind_tips2))
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
                        Intent intent = new Intent();
                        intent.setClass(getActivity(), SearchBratheDeviceActivity.class);
                        intent.putExtra("capture_type", CaptureActivity.CAPTURE_WATER);
                        startActivity(intent);

                    }
                }).show();
    }
}
