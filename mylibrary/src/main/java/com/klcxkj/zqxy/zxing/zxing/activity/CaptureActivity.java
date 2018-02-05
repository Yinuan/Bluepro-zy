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

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.klcxkj.zqxy.MyApp;
import com.klcxkj.mylibrary.R;
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
import com.klcxkj.zqxy.zxing.zxing.camera.CameraManager;
import com.klcxkj.zqxy.zxing.zxing.decode.DecodeThread;
import com.klcxkj.zqxy.zxing.zxing.utils.BeepManager;
import com.klcxkj.zqxy.zxing.zxing.utils.CaptureActivityHandler;
import com.klcxkj.zqxy.zxing.zxing.utils.InactivityTimer;

import net.android.tools.afinal.FinalHttp;
import net.android.tools.afinal.http.AjaxCallBack;
import net.android.tools.afinal.http.AjaxParams;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Hashtable;

/**
 * This activity opens the camera and does the actual scanning on a background
 * thread. It draws a viewfinder to help the user place the barcode correctly,
 * shows feedback as the image processing is happening, and then overlays the
 * results when a scan is successful.
 *
 * @author dswitkin@google.com (Daniel Switkin)
 * @author Sean Owen
 */
public final class CaptureActivity extends BaseActivity implements
        SurfaceHolder.Callback {

    private static final String TAG = CaptureActivity.class.getSimpleName();
    public static final int CAPTURE_WASHING = 6;//洗衣机
    public static final int CAPTURE_WATER = 4;//洗澡
    public static final int CAPTURE_ADMIN = 254; //管理员的操作
    public static final int CAPTURE_DRINK =5;  //饮水
    public static final int CAPTURE_DRYER =7;//吹风机
    public static final int CAPTURE_ELE =8;//冲电
    public static final int CAPTURE_AIR =9;//空调
    public static final int CAPTURE_OTHER =255;//其他
    private CameraManager cameraManager;
    private CaptureActivityHandler handler;
    private InactivityTimer inactivityTimer;
    private BeepManager beepManager;

    private SurfaceView scanPreview = null;
    private RelativeLayout scanContainer;
    private RelativeLayout scanCropView;
    private ImageView scanLine;

    private Rect mCropRect = null;
    private boolean isHasSurface = false;

    public Handler getHandler() {
        return handler;
    }

    public CameraManager getCameraManager() {
        return cameraManager;
    }
    private TextView device_list_txt;
    private ImageView flash_img;
   private int ifOpenLight = 0; // 判断是否开启闪光灯

   private int capture_type;



    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_capture);
        cameraManager = new CameraManager(getApplication());
        scanPreview = (SurfaceView) findViewById(R.id.capture_preview);
        scanContainer = (RelativeLayout) findViewById(R.id.capture_container);
        scanCropView = (RelativeLayout) findViewById(R.id.capture_crop_view);
        scanLine = (ImageView) findViewById(R.id.capture_scan_line);
         device_list_txt = (TextView) findViewById(R.id.device_list_txt);
        inactivityTimer = new InactivityTimer(this);
        beepManager = new BeepManager(this);

        TranslateAnimation animation = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.9f);
        animation.setDuration(4500);
        animation.setRepeatCount(-1);
        animation.setRepeatMode(Animation.RESTART);
        scanLine.startAnimation(animation);

        flash_img = (ImageView) findViewById(R.id.flash_img);
        openLight();
        flash_img.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                ifOpenLight++;
                openLight();
            }
        });
        //隐藏设备列表
         initdata();

        findViewById(R.id.back_img).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                finish();

            }
        });
        sp=getSharedPreferences("adminInfo", Context.MODE_PRIVATE);
        mUserInfo =Common.getUserInfo(sp);

    }

    private void initdata() {
        capture_type = getIntent().getExtras().getInt("capture_type");
        Log.d(TAG, "capture_type:" + capture_type);
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

    // 是否开启闪光灯
    private void openLight() {
        switch (ifOpenLight % 2) {
            case 0:
                // 关闭
                flash_img.setSelected(false);
                cameraManager.closeLight();
                break;

            case 1:
                // 打开
                flash_img.setSelected(true);
                cameraManager.openLight(); // 开闪光灯
                break;
            default:
                break;
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        handler = null;

        if (isHasSurface) {
            initCamera(scanPreview.getHolder());
        } else {
            scanPreview.getHolder().addCallback(this);
        }

        inactivityTimer.onResume();
    }

    @Override
    protected void onPause() {
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        inactivityTimer.onPause();
        beepManager.close();
        cameraManager.closeDriver();
        if (!isHasSurface) {
            scanPreview.getHolder().removeCallback(this);
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        inactivityTimer.shutdown();
        super.onDestroy();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (holder == null) {
            Log.e(TAG,
                    "*** WARNING *** surfaceCreated() gave us a null surface!");
        }
        if (!isHasSurface) {
            isHasSurface = true;
            initCamera(holder);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        isHasSurface = false;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {

    }

    /**
     * A valid barcode has been found, so give an indication of success and show
     * the results.
     *
     * @param rawResult The contents of the barcode.
     * @param bundle    The extras
     */

    public void handleDecode(Result rawResult, Bundle bundle) {
        inactivityTimer.onActivity();
        beepManager.playBeepSoundAndVibrate();

        // Intent resultIntent = new Intent();
        // bundle.putInt("width", mCropRect.width());
        // bundle.putInt("height", mCropRect.height());
        // bundle.putString("result", rawResult.getText());
        // resultIntent.putExtras(bundle);

        // 07-18 20:09:12.205: E/water(18412): result =
        // KLCXKJ-Water,1,001583112233
        String result = rawResult.getText();
        Log.d(TAG, result);
        try {
            String[] okString = Common.getSubString(result, ",");
            if (okString != null && okString.length == 3) {
                if (!okString[0].equals("KLCXKJ-Water")) {
                    showErrorQR(getString(R.string.error_qr));
                    return;
                }

                String number =okString[1];
                Log.d(TAG, "capture_type:" + capture_type);
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
            }

        } catch (Exception e) {
            showErrorQR(getString(R.string.error_qr));
        }

    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        if (surfaceHolder == null) {
            throw new IllegalStateException("No SurfaceHolder provided");
        }
        if (cameraManager.isOpen()) {
            Log.w(TAG, "initCamera() while already open -- late SurfaceView callback?");
            return;
        }
        try {
            cameraManager.openDriver(surfaceHolder);
            // Creating the handler starts the preview, which can also throw a
            // RuntimeException.
            if (handler == null) {
                handler = new CaptureActivityHandler(this, cameraManager, DecodeThread.ALL_MODE);
            }

            initCrop();
        } catch (IOException ioe) {
            Log.w(TAG, ioe);
            displayFrameworkBugMessageAndExit();
        } catch (RuntimeException e) {
            // Barcode Scanner has seen crashes in the wild of this variety:
            // java.?lang.?RuntimeException: Fail to connect to camera service
            Log.w(TAG, "Unexpected error initializing camera", e);
            displayFrameworkBugMessageAndExit();
        }
    }

    private void displayFrameworkBugMessageAndExit() {
        // camera error
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.app_name));
        builder.setMessage("请到设置-权限管理打开该应用的相机权限");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                setResult(Activity.RESULT_CANCELED);

                finish();
            }

        });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
                setResult(Activity.RESULT_CANCELED);
                finish();
            }
        });
        builder.show();
    }

    public void restartPreviewAfterDelay(long delayMS) {
        if (handler != null) {
            handler.sendEmptyMessageDelayed(R.id.restart_preview, delayMS);
        }
    }

    public Rect getCropRect() {
        return mCropRect;
    }

    /**
     * 初始化截取的矩形区域
     */
    private void initCrop() {
        int cameraWidth = cameraManager.getCameraResolution().y;
        int cameraHeight = cameraManager.getCameraResolution().x;

        /** 获取布局中扫描框的位置信息 */
        int[] location = new int[2];
        scanCropView.getLocationInWindow(location);

        int cropLeft = location[0];
        int cropTop = location[1] - getStatusBarHeight();

        int cropWidth = scanCropView.getWidth();
        int cropHeight = scanCropView.getHeight();

        /** 获取布局容器的宽高 */
        int containerWidth = scanContainer.getWidth();
        int containerHeight = scanContainer.getHeight();

        /** 计算最终截取的矩形的左上角顶点x坐标 */
        int x = cropLeft * cameraWidth / containerWidth;
        /** 计算最终截取的矩形的左上角顶点y坐标 */
        int y = cropTop * cameraHeight / containerHeight;

        /** 计算最终截取的矩形的宽度 */
        int width = cropWidth * cameraWidth / containerWidth;
        /** 计算最终截取的矩形的高度 */
        int height = cropHeight * cameraHeight / containerHeight;

        /** 生成最终的截取的矩形 */
        mCropRect = new Rect(x, y, width + x, height + y);
    }

    private int getStatusBarHeight() {
        try {
            Class<?> c = Class.forName("com.android.internal.R$dimen");
            Object obj = c.newInstance();
            Field field = c.getField("status_bar_height");
            int x = Integer.parseInt(field.get(obj).toString());
            return getResources().getDimensionPixelSize(x);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    // ===================================

    private Bitmap scanBitmap;

    /**
     * 扫描二维码图片的方法
     *
     * @param path
     * @return
     */
    public Result scanningImage(String path) {
        if (TextUtils.isEmpty(path)) {
            return null;
        }
        Hashtable<DecodeHintType, String> hints = new Hashtable<DecodeHintType, String>();
        hints.put(DecodeHintType.CHARACTER_SET, "UTF8"); // 设置二维码内容的编码

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true; // 先获取原大小
        scanBitmap = BitmapFactory.decodeFile(path, options);
        options.inJustDecodeBounds = false; // 获取新的大小
        int sampleSize = (int) (options.outHeight / (float) 200);
        if (sampleSize <= 0)
            sampleSize = 1;
        options.inSampleSize = sampleSize;
        scanBitmap = BitmapFactory.decodeFile(path, options);
        int width = scanBitmap.getWidth();
        int height = scanBitmap.getHeight();
        int[] pixels = new int[width * height];
        scanBitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        /**
         * 第三个参数是图片的像素
         */
        RGBLuminanceSource source = new RGBLuminanceSource(width, height,
                pixels);
        BinaryBitmap bitmap1 = new BinaryBitmap(new HybridBinarizer(source));
        QRCodeReader reader = new QRCodeReader();
        try {
            return reader.decode(bitmap1, hints);

        } catch (NotFoundException e) {
            e.printStackTrace();
        } catch (ChecksumException e) {
            e.printStackTrace();
        } catch (FormatException e) {
            e.printStackTrace();
        }
        return null;
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
                        }
                    });

        } else {
            Common.showNoNetworkDailog(dialogBuilder, CaptureActivity.this);
            restartPreviewAfterDelay(1000);
        }

    }

    private void skipDecive( DeviceInfo serverDeviceInfo,String address){
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
                .setButton2Click(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogBuilder.dismiss();
                        restartPreviewAfterDelay(1000);
                    }
                }).show();
    }



}