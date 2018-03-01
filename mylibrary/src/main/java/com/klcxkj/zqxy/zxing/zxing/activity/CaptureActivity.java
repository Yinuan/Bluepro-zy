/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.klcxkj.zqxy.zxing.zxing.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.zxing.Result;
import com.google.zxing.client.result.ParsedResult;
import com.klcxkj.jxing.OnScannerCompletionListener;
import com.klcxkj.jxing.ScannerView;
import com.klcxkj.jxing.common.Scanner;
import com.klcxkj.mylibrary.R;
import com.klcxkj.zqxy.MyApp;
import com.klcxkj.zqxy.common.Common;
import com.klcxkj.zqxy.databean.DeviceInfo;
import com.klcxkj.zqxy.databean.UserInfo;
import com.klcxkj.zqxy.response.PublicArrayData;
import com.klcxkj.zqxy.response.PublicGetData;
import com.klcxkj.zqxy.ui.BaseActivity;
import com.klcxkj.zqxy.ui.Bath2Activity;
import com.klcxkj.zqxy.ui.DeviceRegisterActivity;
import com.klcxkj.zqxy.ui.WashingActivity;
import com.klcxkj.zqxy.ui.WaterDeviceListActivity;
import com.klcxkj.zqxy.widget.Effectstype;
import com.klcxkj.zqxy.widget.LoadingDialogProgress;
import com.klcxkj.zqxy.widget.NiftyDialogBuilder;

import net.android.tools.afinal.FinalHttp;
import net.android.tools.afinal.http.AjaxCallBack;
import net.android.tools.afinal.http.AjaxParams;

import java.lang.reflect.Type;
import java.util.ArrayList;


public  class CaptureActivity extends BaseActivity implements OnScannerCompletionListener {

    private static final String TAG = CaptureActivity.class.getSimpleName();
    public static final int CAPTURE_WASHING = 6;//洗衣机
    public static final int CAPTURE_WATER = 4;//洗澡
    public static final int CAPTURE_ADMIN = 254; //管理员的操作
    public static final int CAPTURE_DRINK =5;  //饮水
    public static final int CAPTURE_DRYER =7;//吹风机
    public static final int CAPTURE_ELE =8;//冲电
    public static final int CAPTURE_AIR =9;//空调
    public static final int CAPTURE_OTHER =255;//其他



    private TextView device_list_txt;
    private ImageView flash_img;
   private int ifOpenLight = 0; // 判断是否开启闪光灯

    private SharedPreferences sp;
    private UserInfo mUserInfo;

    private LoadingDialogProgress progress;

   private int capture_type;
    private ScannerView mScannerView;
    private RelativeLayout proshow;

    protected NiftyDialogBuilder dialogBuilder;
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_capture);
        dialogBuilder = NiftyDialogBuilder.getInstance(this);
        //二维码设置
        setdecode();
        device_list_txt = (TextView) findViewById(R.id.device_list_txt);
        proshow =findViewById(R.id.decode_show_deal);
        flash_img = (ImageView) findViewById(R.id.flash_img);
        openLight();
        flash_img.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                ifOpenLight++;
                openLight();
            }
        });
        //隐藏设备列表
         initdata();
        sp=getSharedPreferences("adminInfo", Context.MODE_PRIVATE);
        mUserInfo = Common.getUserInfo(sp);
        findViewById(R.id.back_img).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                finish();

            }
        });


    }
    // 是否开启闪光灯
    private void openLight() {
        switch (ifOpenLight % 2) {
            case 0:
                // 关闭
                flash_img.setSelected(false);
                mScannerView.toggleLight(false);
                break;

            case 1:
                // 打开
                flash_img.setSelected(true);
                mScannerView.toggleLight(true); // 开闪光灯
                break;
            default:
                break;
        }

    }

    private void showpro(){
        proshow.setVisibility(View.VISIBLE);
    }
    private void hidepro(){
        proshow.setVisibility(View.GONE);
    }

    private void setdecode() {
        mScannerView = (ScannerView) findViewById(R.id.capture_preview);
        mScannerView.setOnScannerCompletionListener(this);


        int laserMode = 0;
        int scanMode = 0;

        mScannerView.setMediaResId(R.raw.beep);//设置扫描成功的声音

       // mScannerView.setLaserFrameBoundColor(R.color.base_color);
        mScannerView.setLaserFrameSize(240,240);
        if (scanMode == 1) {
            //二维码
            mScannerView.setScanMode(Scanner.ScanMode.QR_CODE_MODE);
        } else if (scanMode == 2) {
            //一维码
            mScannerView.setScanMode(Scanner.ScanMode.PRODUCT_MODE);
        }
        //显示扫描成功后的缩略图
        mScannerView.isShowResThumbnail(true);
        //全屏识别
        mScannerView.isScanFullScreen(false);
        //隐藏扫描框
        mScannerView.isHideLaserFrame(false);
//        mScannerView.isScanInvert(true);//扫描反色二维码
//        mScannerView.setCameraFacing(CameraFacing.FRONT);
//        mScannerView.setLaserMoveSpeed(1);//速度

        mScannerView.setLaserFrameTopMargin(120);//扫描框与屏幕上方距离
//        mScannerView.setLaserFrameSize(400, 400);//扫描框大小
        mScannerView.setLaserFrameCornerLength(25);//设置4角长度
//        mScannerView.setLaserLineHeight(5);//设置扫描线高度
//        mScannerView.setLaserFrameCornerWidth(5);

        mScannerView.setLaserLineResId(R.drawable.scan_line);//线图
    }

    private void initdata() {
        capture_type = getIntent().getExtras().getInt("capture_type");
        switch (capture_type){
            case 4: //热水
                device_list_txt.setVisibility(View.VISIBLE);
                break;
            case 5: //饮水
                device_list_txt.setVisibility(View.VISIBLE);
                break;
            case 6://室外洗衣机
                device_list_txt.setVisibility(View.VISIBLE);
                break;
            case 7://吹风机
                device_list_txt.setVisibility(View.VISIBLE);
                break;
            case 8://充电
                device_list_txt.setVisibility(View.VISIBLE);
                break;
            case 9://空调
                device_list_txt.setVisibility(View.VISIBLE);
                break;
            case 254: //管理员
                device_list_txt.setVisibility(View.GONE);
                break;
            case 255: //其他
                device_list_txt.setVisibility(View.GONE);
                break;
        }
        //点击查看设备类表
        device_list_txt.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(CaptureActivity.this, WaterDeviceListActivity.class);
                intent.putExtra("capture_type",capture_type);
                startActivity(intent);
                finish();
            }
        });
    }

    private void restartPreviewAfterDelay(long delayMS) {
        mScannerView.restartPreviewAfterDelay(delayMS);
        resetStatusView();
    }
    private Result mLastResult;
    private void resetStatusView() {
        mLastResult = null;
    }
    @Override
    protected void onResume() {
        mScannerView.onResume();
        resetStatusView();
        super.onResume();
    }

    @Override
    protected void onPause() {
        mScannerView.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                if (mLastResult != null) {
                    restartPreviewAfterDelay(0L);
                    return true;
                }
                break;
        }
        return super.onKeyDown(keyCode, event);
    }
    @Override
    public void onScannerCompletion(Result rawResult, ParsedResult parsedResult, Bitmap barcode) {
        if (rawResult ==null){
            showErrorQR("无效的二维码");
            return;
        }
        showpro(); //显示处理
        String result = rawResult.getText();
        Log.d(TAG, result);
        try {
            String[] okString = Common.getSubString(result, ",");
            if (okString != null && okString.length == 3) {
                if (!okString[0].equals("KLCXKJ-Water")) {
                    showErrorQR(getString(R.string.error_qr));
                    hidepro();
                    return;
                }

                String number =okString[1];
              /*  if (capture_type==255 ||capture_type==254){
                    int num =Integer.parseInt(number);
                   Integer[] items ={4,5,6,7,8,9};
                    if (!Arrays.asList(items).contains(num)) {
                        showErrorQR(getString(R.string.error_qr));
                        return;
                    }
                }else {
                    if (capture_type !=Integer.parseInt(number)){
                        showErrorQR(getString(R.string.error_qr));
                        return;

                    }
                }*/

                if (!okString[2].contains(":")) {
                    getMACInfo(okString[1], Common.getMacMode(okString[2]));
                } else {
                    getMACInfo(okString[1], okString[2]);
                }

            } else {
                showErrorQR(getString(R.string.error_qr));
                hidepro();
            }

        } catch (Exception e) {
            showErrorQR(getString(R.string.error_qr));
            hidepro();
        }
    }



    @Override
    public void onBackPressed() {
        // super.onBackPressed();
        finish();

    }

    private void getMACInfo(final String device_type, final String address) {

        if (Common.isNetWorkConnected(CaptureActivity.this)) {
            AjaxParams ajaxParams = new AjaxParams();
            ajaxParams.put("PrjID", mUserInfo.PrjID + "");
            ajaxParams.put("deviceID_List", "0");
            ajaxParams.put("deviceMac_List", address);
            ajaxParams.put("loginCode", mUserInfo.TelPhone + "," + mUserInfo.loginCode);
            ajaxParams.put("phoneSystem", "Android");
            ajaxParams.put("version", MyApp.versionCode);
            Log.d(TAG, "ajaxParams:" + ajaxParams);
            new FinalHttp().get(Common.BASE_URL + "deviceInfo", ajaxParams,
                    new AjaxCallBack<Object>() {

                        @Override
                        public void onSuccess(Object t) {
                            super.onSuccess(t);
                            String result = t.toString();
                            hidepro();
                            Log.d(TAG, result);
                            PublicArrayData publicArrayData = new Gson().fromJson(result, PublicArrayData.class);
                            if (publicArrayData.error_code.equals("0")) {

                                Type listType = new TypeToken<ArrayList<DeviceInfo>>() {}.getType();
                                ArrayList<DeviceInfo> deviceInfos = new Gson().fromJson(publicArrayData.data, listType);

                                if (deviceInfos != null && deviceInfos.size() > 0) {
                                    DeviceInfo serverDeviceInfo = deviceInfos.get(0);
                                    if (serverDeviceInfo ==null){
                                        restartPreviewAfterDelay(1000);
                                        return;
                                    }
                                    if (serverDeviceInfo.PrjID==0){ //只有一个MAC地址的时候
                                        if (capture_type!=254){
                                            showErrorQR("设备未登记!");
                                            return;
                                        }
                                    }
                                    if (serverDeviceInfo.Dsbtypeid !=capture_type){ //当前扫描的大类与传过来的大类不相等
                                        if (capture_type!=255 && capture_type!=254){
                                            showErrorQR("无效的二维码");
                                            return;
                                        }
                                    }
                                    if (mUserInfo.PrjID !=0){
                                        if (serverDeviceInfo.PrjID !=mUserInfo.PrjID){
                                            if (capture_type!=254){ //普通用户
                                                showErrorQR("此设备的项目与您的项目不一致");
                                            }else {  //操作员管理多个项目
                                                //跳转到使用
                                                skipDecive(serverDeviceInfo,address);
                                            }

                                        }else {
                                            //跳转到使用
                                            skipDecive(serverDeviceInfo,address);
                                        }
                                    }else {
                                        //去绑定项目
                                        bindDecive(serverDeviceInfo,address);
                                    }


                                } else {
                                    //这台设备还没数据（未登记，gson解析不出数据）
                                    if (capture_type == CAPTURE_ADMIN){
                                        DeviceInfo deviceInfo = new DeviceInfo();
                                        deviceInfo.DevName = getString(R.string.new_device);
                                        deviceInfo.devMac = address;
                                        Intent intent = new Intent();
                                        intent.setClass(CaptureActivity.this, DeviceRegisterActivity.class);
                                        intent.putExtra("device_data", deviceInfo);
                                        startActivity(intent);
                                    }else {
                                        restartPreviewAfterDelay(1000);
                                    }

                                }
                            } else if (publicArrayData.error_code.equals("7")) {
                                //登陆失效

                                Common.logout2(CaptureActivity.this, getSharedPreferences("adminInfo", Context.MODE_PRIVATE), dialogBuilder,publicArrayData.message);
                            } else {
                                //这台设备还没数据（未登记，查询失败）
                                if (capture_type == CAPTURE_ADMIN){
                                    DeviceInfo deviceInfo = new DeviceInfo();
                                    deviceInfo.DevName = getString(R.string.new_device);
                                    deviceInfo.devMac = address;
                                    Intent intent = new Intent();
                                    intent.setClass(CaptureActivity.this, DeviceRegisterActivity.class);
                                    intent.putExtra("device_data", deviceInfo);
                                    startActivity(intent);
                                }else {
                                    restartPreviewAfterDelay(1000);
                                }
                            }

                        }

                        @Override
                        public void onFailure(Throwable t, int errorNo, String strMsg) {
                            super.onFailure(t, errorNo, strMsg);
                            restartPreviewAfterDelay(1000);
                            hidepro();
                        }
                    });

        } else {
            Common.showNoNetworkDailog(dialogBuilder, CaptureActivity.this);
            restartPreviewAfterDelay(1000);
        }

    }

    private void skipDecive(DeviceInfo serverDeviceInfo, String address){
        Intent intent = new Intent();
        Bundle bundle =new Bundle();
        bundle.putSerializable("DeviceInfo",serverDeviceInfo);
        switch (capture_type){

            case 4://热水
                //绑定
                skipUI(intent,bundle);
                break;
            case 5://饮水
                skipUI(intent,bundle);
                break;
            case 6://室外洗衣机
                //绑定
                skipWashingUi(intent,bundle);
                break;

            case 7: //吹风机
                skipUI(intent,bundle);
                break;
            case 8: //充电器
                skipUI(intent,bundle);
                break;
            case 9: //空调
                break;
            case 254: //管理员
                if (!TextUtils.isEmpty(serverDeviceInfo.devMac)){
                    intent.setClass(CaptureActivity.this, DeviceRegisterActivity.class);
                    intent.putExtra("device_data", serverDeviceInfo);
                    startActivity(intent);
                } else {
                    DeviceInfo deviceInfo = new DeviceInfo();
                    deviceInfo.DevName = getString(R.string.new_device);
                    deviceInfo.devMac = address;
                    intent.setClass(CaptureActivity.this, DeviceRegisterActivity.class);
                    intent.putExtra("device_data", deviceInfo);
                    startActivity(intent);
                }
                break;
            case 255:  //其他
                if (serverDeviceInfo.Dsbtypeid==6){
                    skipWashingUi(intent,bundle);
                }else {
                    skipUI(intent,bundle);
                }
                break;
        }
    }

    private void skipWashingUi(Intent intent,Bundle bundle) {
        intent.setClass(CaptureActivity.this, WashingActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
    }

    private void skipUI(Intent intent,Bundle bundle){
        intent.setClass(CaptureActivity.this, Bath2Activity.class);
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
    }
    /**
     * 用于用户没有项目时的绑定
     */
    private void bindDecive(final DeviceInfo deviceInfo, final String address){
        AjaxParams ajaxParams = new AjaxParams();
        ajaxParams.put("TelPhone", sp.getString(Common.USER_PHONE_NUM, ""));
        ajaxParams.put("PrjID", "" + deviceInfo.PrjID);
        ajaxParams.put("WXID", "0");
        ajaxParams.put("loginCode", mUserInfo.TelPhone + "," + mUserInfo.loginCode);
        ajaxParams.put("phoneSystem", "Android");
        ajaxParams.put("version", MyApp.versionCode);
        new FinalHttp().get(Common.BASE_URL + "bingding", ajaxParams, new AjaxCallBack<Object>() {

            @Override
            public void onSuccess(Object t) {
                super.onSuccess(t);
                String result = t.toString();

                PublicGetData publicGetData = new Gson().fromJson(result, PublicGetData.class);
                if (publicGetData.error_code.equals("0")) {
                    UserInfo mInfo = new Gson().fromJson(publicGetData.data, UserInfo.class);
                    mInfo.loginCode = mUserInfo.loginCode;
                    SharedPreferences.Editor editor = sp.edit();
                    //更新用户信息
                    editor.putString(Common.USER_INFO, new Gson().toJson(mInfo));
                    editor.commit();
                    skipDecive(deviceInfo,address);

                } else if (publicGetData.error_code.equals("7")) {
                    Common.logout(CaptureActivity.this, sp, dialogBuilder);
                } else {
                    Common.showToast(CaptureActivity.this, R.string.bind_fail, Gravity.CENTER);
                }
            }
            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);

                Common.showToast(CaptureActivity.this, R.string.bind_fail, Gravity.CENTER);
            }
        });
    }

    private void showErrorQR(String string) {
        dialogBuilder.withTitle(getString(R.string.tips)).withMessage(string)
                .withEffect(Effectstype.Fadein).isCancelable(false)
                .withButton2Text(getString(R.string.sure))
                .setButton2Click(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogBuilder.dismiss();
                        restartPreviewAfterDelay(1000);
                    }
                }).show();
    }



}