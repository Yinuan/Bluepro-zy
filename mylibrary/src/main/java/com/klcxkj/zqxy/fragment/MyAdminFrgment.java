package com.klcxkj.zqxy.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.klcxkj.zqxy.MyApp;
import com.klcxkj.mylibrary.R;
import com.klcxkj.zqxy.common.Common;
import com.klcxkj.zqxy.databean.AdminProductInfo;
import com.klcxkj.zqxy.databean.PowerData;
import com.klcxkj.zqxy.databean.PrjInfo;
import com.klcxkj.zqxy.databean.UserInfo;
import com.klcxkj.zqxy.response.PublicArrayData;
import com.klcxkj.zqxy.response.PublicGetData;
import com.klcxkj.zqxy.ui.AboutActivity;
import com.klcxkj.zqxy.ui.DeviceExceptionActivity;
import com.klcxkj.zqxy.ui.FinancialManagementActivity;
import com.klcxkj.zqxy.ui.LoginActivity;
import com.klcxkj.zqxy.ui.MyInfoActivity;
import com.klcxkj.zqxy.ui.SearchBratheDeviceActivity;
import com.klcxkj.zqxy.ui.SuggestionActivity;
import com.klcxkj.zqxy.widget.CircleImageView;
import com.klcxkj.zqxy.widget.Effectstype;
import com.klcxkj.zqxy.zxing.zxing.activity.CaptureActivity;

import net.android.tools.afinal.FinalHttp;
import net.android.tools.afinal.http.AjaxCallBack;
import net.android.tools.afinal.http.AjaxParams;

import java.lang.reflect.Type;
import java.util.ArrayList;

import me.iwf.photopicker.PhotoPicker;

import static android.app.Activity.RESULT_OK;

/**
 * autor:OFFICE-ADMIN
 * time:2017/11/20
 * email:yinjuan@klcxkj.com
 * description:管理员中心
 */

public class MyAdminFrgment extends BaseFragment implements View.OnClickListener{

    private View rootView;
    private SharedPreferences sp;
    private UserInfo mUserInfo;
    private ArrayList<AdminProductInfo> adminProductInfos = new ArrayList<AdminProductInfo>();

    //菜单
    private RelativeLayout admin_bill; //菜单
    private RelativeLayout admin_project;//项目
    private RelativeLayout admin_device;//设备
    private RelativeLayout admin_yijian;//意见反馈
    private RelativeLayout admin_bout;//关于
    private RelativeLayout admin_call;//电话
    private RelativeLayout admin_monney;//财务管理
    //编辑资料
    private TextView admin_quzhi_layout;
    private Button loginOut_btn;
    private TextView admin_quzhi_account_txt;
    private TextView admin_quzhi_address_txt;

    //我的项目
    private TextView my_project_num_txt;
    //头像
    private CircleImageView icon;
    private TextView my_version_txt;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sp = getActivity().getSharedPreferences("adminInfo", Context.MODE_PRIVATE);
        if (Common.getUserInfo(sp) !=null){
            mUserInfo = Common.getUserInfo(sp);
            getAdmin(mUserInfo);
        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.item_admin_center, container, false);
            initView(rootView);
            if (mUserInfo !=null){
                if (Common.isBindAccount(sp)) {
                    updatePrjInfo(mUserInfo);
                }
                initData();
            }

        }

        return rootView;
    }


    private void initView(View rootView) {



        //
        admin_bill = (RelativeLayout) rootView.findViewById(R.id.admin_bill);
        admin_project = (RelativeLayout) rootView.findViewById(R.id.my_project_layout);
        admin_device = (RelativeLayout) rootView.findViewById(R.id.device_exception_layout);
        admin_yijian = (RelativeLayout) rootView.findViewById(R.id.my_suggestion_layout);
        admin_bout = (RelativeLayout) rootView.findViewById(R.id.my_about_layout);
        admin_call = (RelativeLayout) rootView.findViewById(R.id.my_version_layout);
        admin_quzhi_layout = (TextView) rootView.findViewById(R.id.admin_quzhi_layout);
        admin_monney =rootView.findViewById(R.id.financial_management_layout);

        loginOut_btn = (Button) rootView.findViewById(R.id.loginOut_btn);
        my_project_num_txt = (TextView) rootView.findViewById(R.id.my_project_num_txt);

        icon = (CircleImageView) rootView.findViewById(R.id.admin_quzhi_img);
        admin_quzhi_account_txt =rootView.findViewById(R.id.admin_quzhi_account_txt);
        admin_quzhi_address_txt =rootView.findViewById(R.id.admin_quzhi_address_txt);
        my_version_txt =rootView.findViewById(R.id.my_version_txt);


        if (mUserInfo !=null){
            admin_bill.setOnClickListener(this);
            admin_project.setOnClickListener(this);
            admin_device.setOnClickListener(this);
            admin_yijian.setOnClickListener(this);
            admin_bout.setOnClickListener(this);
            admin_call.setOnClickListener(this);
            admin_quzhi_layout.setOnClickListener(this);
            loginOut_btn.setOnClickListener(this);
            icon.setOnClickListener(this);
            admin_monney.setOnClickListener(this);
        }


    }

    private void initAdminData() {
        if (adminProductInfos!=null && adminProductInfos.size()>0){
            my_project_num_txt.setText("我的项目 ("+adminProductInfos.size()+"个)");
        }

    }
    private void initData() {

        admin_quzhi_account_txt.setText(mUserInfo.TelPhone+"");
        admin_quzhi_address_txt.setText(mUserInfo.PrjName);
       // getAdmin(mUserInfo);
        updateAdmin(mUserInfo);
    }


    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.admin_bill) {
        } else if (i == R.id.my_project_layout) {/*  if (adminProductInfos.size() >0) {
                    Intent intent3 = new Intent();
                    intent3.setClass(getActivity(), FinancialManagementActivity.class);
                    intent3.putExtra("adminproduct_infos", new Gson().toJson(adminProductInfos));
                    startActivity(intent3);
                }*/

        } else if (i == R.id.device_exception_layout) {
            Intent intent4 = new Intent();
            intent4.setClass(getActivity(), DeviceExceptionActivity.class);
            startActivity(intent4);

        } else if (i == R.id.my_suggestion_layout) {
            boolean is_bind_account2 = Common.isBindAccount(sp);
            if (is_bind_account2) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), SuggestionActivity.class);
                startActivity(intent);
            } else {
                showBindDialog();
            }


        } else if (i == R.id.my_about_layout) {
            Intent intent = new Intent();
            intent.setClass(getActivity(), AboutActivity.class);
            startActivity(intent);

        } else if (i == R.id.my_version_layout) {
            if (tellPhone == null) {
                return;
            }
            showPhoneDailog(tellPhone);

        } else if (i == R.id.admin_quzhi_layout) {
            Intent intent1 = new Intent();
            intent1.setClass(getActivity(), MyInfoActivity.class);
            intent1.putExtra("is_admin", true);
            startActivity(intent1);

        } else if (i == R.id.loginOut_btn) {
            SharedPreferences.Editor editor = sp.edit();
            editor.remove(Common.USER_PHONE_NUM);
            editor.remove(Common.USER_INFO);
            editor.remove(Common.ACCOUNT_IS_USER);
            editor.commit();

            Intent intent5 = new Intent(getActivity(), LoginActivity.class);
            intent5.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent5);
            getActivity().finish();

        } else if (i == R.id.admin_quzhi_img) {/*  PhotoPicker.builder()
                        .setPhotoCount(1)
                        .setShowCamera(true)
                        .setShowGif(true)
                        .setPreviewEnabled(false)
                        .start(getActivity(),MyAdminFrgment.this, PhotoPicker.REQUEST_CODE);*/


        } else if (i == R.id.financial_management_layout) {
            if (adminProductInfos.size() > 0) {
                Intent in = new Intent();
                in.setClass(getActivity(), FinancialManagementActivity.class);
                in.putExtra("adminproduct_infos", new Gson().toJson(adminProductInfos));
                startActivity(in);
            }

        }
    }



    ArrayList<String> photos;
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PhotoPicker.REQUEST_CODE) {
            Log.d("MyAdminFrgment", "data:" + data);
            if (data != null) {

                photos = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
                Log.d("MyAdminFrgment", photos.get(0));
            }
        }
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

    private void showPhoneDailog(final String phone) {
        String tip = getString(R.string.phone_tips1) + phone + getString(R.string.phone_tips2);
        dialogBuilder.withTitle(getString(R.string.tips))
                .withMessage(tip)
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
                        intent.setAction(Intent.ACTION_CALL);
                        String url = "tel:"+phone;
                        intent.setData(Uri.parse(url));
                        startActivity(intent);
                        dialogBuilder.dismiss();
                    }
                }).show();
    }


    /**
     * 获取项目的财务信息
     * @param mInfo
     */
    private void updateAdmin(UserInfo mInfo) {

        if (Common.isNetWorkConnected(getActivity())) {
            AjaxParams ajaxParams = new AjaxParams();
            ajaxParams.put("TelPhone", mInfo.TelPhone + "");
            ajaxParams.put("loginCode", mInfo.TelPhone+","+mInfo.loginCode);
            ajaxParams.put("phoneSystem", "Android");
            ajaxParams.put("version", MyApp.versionCode);
            Log.d("MyAdminFrgment", "ajaxParams:" + ajaxParams);
            new FinalHttp().get(Common.BASE_URL + "userManage", ajaxParams,
                    new AjaxCallBack<Object>() {

                        @Override
                        public void onSuccess(Object t) {
                            super.onSuccess(t);
                            String result = t.toString();
                           if (result==null && result.length()==0){
                               Toast.makeText(getActivity(), "服务器错误,json数据格式不正确", Toast.LENGTH_SHORT).show();

                               return;
                           }
                            PublicArrayData publicArrayData =null;
                           try {
                               publicArrayData = new Gson().fromJson(result, PublicArrayData.class);
                               if (publicArrayData.error_code==null){
                                   Toast.makeText(getActivity(), "服务器错误,json数据格式不正确", Toast.LENGTH_SHORT).show();
                                   return;
                               }
                           }catch (JsonSyntaxException e){
                               Toast.makeText(getActivity(), "服务器错误,json数据格式不正确", Toast.LENGTH_SHORT).show();
                               return;
                           }

                            if (publicArrayData.error_code.equals("0")) {

                                Type listType = new TypeToken<ArrayList<AdminProductInfo>>() {}.getType();
                                adminProductInfos = new Gson().fromJson(publicArrayData.data, listType);
                                initAdminData();

                            }else if (publicArrayData.error_code.equals("7")){
                                Common.logout(getActivity(), sp,dialogBuilder);
                            }

                        }

                        @Override
                        public void onFailure(Throwable t, int errorNo, String strMsg) {
                            super.onFailure(t, errorNo, strMsg);
                        }
                    });

        } else {
            Common.showNoNetworkDailog(dialogBuilder, getActivity());
        }

    }

    private void getAdmin(final UserInfo mInfo) {

        if (Common.isNetWorkConnected(getActivity())) {

            AjaxParams ajaxParams = new AjaxParams();
            ajaxParams.put("OPID", mInfo.AccID + "");
            ajaxParams.put("loginCode", mInfo.TelPhone+","+mInfo.loginCode);
            ajaxParams.put("phoneSystem", "Android");
            ajaxParams.put("version", MyApp.versionCode);
            new FinalHttp().get(Common.BASE_URL + "OpOrder", ajaxParams,
                    new AjaxCallBack<Object>() {

                        @Override
                        public void onSuccess(Object t) {
                            super.onSuccess(t);
                            String result = t.toString();
                            if (result==null && result.length()==0){
                                Toast.makeText(getActivity(), "服务器错误,json数据格式不正确", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            PublicGetData publicGetData =null;
                            try {
                                publicGetData = new Gson().fromJson(result, PublicGetData.class);
                            }catch (JsonSyntaxException e){
                                Toast.makeText(getActivity(), "服务器错误,json数据格式不正确", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            if (publicGetData.error_code.equals("0")) {

                                PowerData powerData = new Gson().fromJson(publicGetData.data, PowerData.class);

                                updateAdmin(mUserInfo);

                            } else if (publicGetData.error_code.equals("7")){
                                Common.logout2(getActivity(), sp,dialogBuilder,publicGetData.message);
                            }

                        }

                        @Override
                        public void onFailure(Throwable t, int errorNo, String strMsg) {
                            super.onFailure(t, errorNo, strMsg);
                        }
                    });

        } else {
            Common.showNoNetworkDailog(dialogBuilder, getActivity());
        }

    }
    //服务电话
    private String tellPhone;
    private void updatePrjInfo(final UserInfo mInfo) {

        if (Common.isNetWorkConnected(getActivity())) {
            AjaxParams ajaxParams = new AjaxParams();
            ajaxParams.put("PrjID", mInfo.PrjID + "");
            ajaxParams.put("AccID", mInfo.AccID+"");
            ajaxParams.put("loginCode", mInfo.TelPhone+","+mInfo.loginCode);
            ajaxParams.put("phoneSystem", "Android");
            ajaxParams.put("version", MyApp.versionCode);
            new FinalHttp().get(Common.BASE_URL + "priinfo", ajaxParams,
                    new AjaxCallBack<Object>() {

                        @Override
                        public void onSuccess(Object t) {
                            super.onSuccess(t);
                            String result = t.toString();
                          if (result==null && result.length()==0){
                              Toast.makeText(getActivity(), "服务器错误,json数据格式不正确", Toast.LENGTH_SHORT).show();
                              return;
                          }
                            PublicGetData publicGetData =null;
                            try {
                                publicGetData = new Gson().fromJson(result, PublicGetData.class);
                                if (publicGetData.error_code==null){
                                    Toast.makeText(getActivity(), "服务器错误,json数据格式不正确", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                          }catch (JsonSyntaxException e){
                                Toast.makeText(getActivity(), "服务器错误,json数据格式不正确", Toast.LENGTH_SHORT).show();
                                return;
                          }

                            if (publicGetData.error_code.equals("0")) {

                                final PrjInfo info = new Gson().fromJson(publicGetData.data, PrjInfo.class);
                                if (info != null) {

                                    if (info.ServerTel !=null && info.ServerTel.length()>0){
                                       // admin_call.setVisibility(View.VISIBLE);
                                        tellPhone =info.ServerTel;
                                        my_version_txt.setText(tellPhone);
                                    }
                                }

                            } else if (publicGetData.error_code.equals("7")){
                                Common.logout(getActivity(), sp,dialogBuilder);
                            }

                        }

                        @Override
                        public void onFailure(Throwable t, int errorNo, String strMsg) {
                            super.onFailure(t, errorNo, strMsg);
                        }
                    });

        } else {
            Common.showNoNetworkDailog(dialogBuilder, getActivity());
        }

    }

}
