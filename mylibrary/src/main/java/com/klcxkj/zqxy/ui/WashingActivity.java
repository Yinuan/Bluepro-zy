package com.klcxkj.zqxy.ui;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jooronjar.BluetoothService;
import com.example.jooronjar.DealRateInfo;
import com.example.jooronjar.DownRateInfo;
import com.example.jooronjar.utils.AnalyTools;
import com.example.jooronjar.utils.CMDUtils;
import com.example.jooronjar.utils.DigitalTrans;
import com.example.jooronjar.utils.WaterCodeListener;
import com.google.gson.Gson;
import com.klcxkj.zqxy.MyApp;
import com.klcxkj.mylibrary.R;
import com.klcxkj.zqxy.common.Common;
import com.klcxkj.zqxy.databean.DeviceInfo;
import com.klcxkj.zqxy.databean.PostConsumeData;
import com.klcxkj.zqxy.databean.UserInfo;
import com.klcxkj.zqxy.databean.WashingModelInfo;
import com.klcxkj.zqxy.databean.WashingPerMonneyBean;
import com.klcxkj.zqxy.databean.WashingPerMonneyResult;
import com.klcxkj.zqxy.response.PublicGetData;
import com.klcxkj.zqxy.response.PublicPostConsumeData;
import com.klcxkj.zqxy.widget.Effectstype;
import com.klcxkj.zqxy.widget.TimeView;
import com.klcxkj.zqxy.zxing.zxing.activity.CaptureActivity;

import net.android.tools.afinal.FinalHttp;
import net.android.tools.afinal.http.AjaxCallBack;
import net.android.tools.afinal.http.AjaxParams;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class WashingActivity extends BaseActivity implements WaterCodeListener{

    private static final int DELAY_TIME = 60000;
    //设备
    private TextView pName ;//项目名字
    private ImageView deciveIcon;
    private ProgressBar progressBar;
    private TextView proState; //状态
    //订单信息
    private RelativeLayout model_chose; //西医模式选择
    private TextView deciveName; //洗衣机地址
    private TextView deciveModel;//模式
    private TextView decivePerMonney;//账单金额
    private TextView remainMonney; //余额
    //very import   state for buletooth connet
    private  int bState =0;
    //lanya
    private BluetoothService mbtService = null;
    private BluetoothAdapter bluetoothAdapter;
    //decive
    private DeviceInfo mDeviceInfo;
    private  WashingModelInfo washingModelInfo; //洗涤
    private TimeView washing_time;//倒计时
    private int times=10000;//剩余时间
    //
    private  int connectFailed =0; //失败连接次数
    private int matchFailed =0; //配对失败重配次数

    //
    private String DevTypeID; //设备的小类

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      /*  getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);*/
        setContentView(R.layout.activity_washing);
        sp =getSharedPreferences("adminInfo", Context.MODE_PRIVATE);
        mUserInfo = Common.getUserInfo(sp);
        mDeviceInfo= (DeviceInfo) getIntent().getExtras().getSerializable("DeviceInfo");
        initview();
        initdata();
        bindclick();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.getExtras() !=null){ //更新了设备，相应更新洗衣模式
            if (intent.getExtras().getSerializable("DeviceInfo") !=null){
                mDeviceInfo = (DeviceInfo) intent.getExtras().getSerializable("DeviceInfo");
                washingModelInfo =null;
                initview();
                initdata();
                bindclick();
            }else if (intent.getExtras().getSerializable("model_chose")!=null){
                washingModelInfo= (WashingModelInfo) intent.getExtras().getSerializable("model_chose");
                if (washingModelInfo !=null){
                   // AppPreference.getInstance().saveWashingModelInfo(washingModelInfo);
                    //  xaifafeilv(wproductid,wdeviceid,wmacBuffer,wtac_timeBuffer);
                    //g更新UI
                    deciveModel.setText(washingModelInfo.getTypename());

                    int a =0;
                    if (washingModelInfo.getMoney()!=null){
                        a =Integer.parseInt(washingModelInfo.getMoney());
                    }
                    decivePerMonney.setText(Common.getShowMonty(a, getString(R.string.yuan1))); //预扣费
                    if (mbtService.getState() ==BluetoothService.STATE_CONNECTED){
                        showProgressbar();
                        CMDUtils.chaxueshebei(mbtService,true);

                    }else {
                        //蓝牙连接
                        mbtService.connect(bluetoothAdapter.getRemoteDevice(mDeviceInfo.devMac));
                        showProgressbar();
                    }

                }
            }
        }

    }



    private void initview() {
        showMenu("洗衣");
        if (mUserInfo.GroupID==2){
            rightTxt.setVisibility(View.VISIBLE);
            rightTxt.setText("更换洗衣机");
            rightTxt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent in =new Intent(WashingActivity.this,SearchBratheDeviceActivity.class);
                    in.putExtra("capture_type", CaptureActivity.CAPTURE_WASHING);
                    startActivity(in);

                }
            });
        }

        //1
        pName = (TextView) findViewById(R.id.project_name_txt);
        deciveIcon = (ImageView) findViewById(R.id.device_state_img);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        proState = (TextView) findViewById(R.id.device_connect_state_txt);
        //2
        model_chose = (RelativeLayout) findViewById(R.id.washing_model_chose);
        deciveName = (TextView) findViewById(R.id.device_name_txt);
        deciveModel = (TextView) findViewById(R.id.device_model_txt);
        decivePerMonney = (TextView) findViewById(R.id.monney_bill);
        remainMonney = (TextView) findViewById(R.id.monney_remain);
        //倒计时
        washing_time = (TimeView) findViewById(R.id.washing_time);
        //初始化
        deciveIcon.setImageResource(R.mipmap.washing_unconnect);
        proState.setText("未连接");
        //设备连接状态
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mbtService = BluetoothService.sharedManager();
        mbtService.setHandlerContext(mHandler);

        IntentFilter statusFilter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        statusFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);// 状态改变
        registerReceiver(mStatusReceive, statusFilter);
        //蓝牙连接
        AnalyTools.setWaterCodeLisnter(this);

    }
    private void initdata() {


        if (mDeviceInfo !=null){
         //   Log.d("WashingActivity", mDeviceInfo.DevName);
           // Log.d("WashingActivity", mDeviceInfo.devMac);
            DevTypeID =mDeviceInfo.DevTypeID+"";
          //  Log.d("WashingActivity","s设备的小类："+ DevTypeID);
            pName.setText(mDeviceInfo.PrjName);//项目名字
            deciveName.setText(mDeviceInfo.DevName); //设备名字
            remainMonney.setText(Common.getShowMonty(mUserInfo.AccMoney + mUserInfo.GivenAccMoney, getString(R.string.yuan1)));//我的余额
        }
        //初始化账单金额和余额。用以显示用户关掉APP后重新打开时 对象washingModelInfo没了的情况
      /*  WashingModelInfo washingModelInfo1 =AppPreference.getInstance().getWashingModelInfo();
        if (washingModelInfo1 !=null){
            deciveModel.setText(washingModelInfo1.getTypename());
            int a =0;
            if (washingModelInfo1.getMoney()!=null){
                a =Integer.parseInt(washingModelInfo1.getMoney());
            }
            decivePerMonney.setText(Common.getShowMonty(a, getString(R.string.yuan1))); //账单金额
        }else {

        }*/
        decivePerMonney.setText(Common.getShowMonty(0, getString(R.string.yuan1))); //账单金额
        deciveModel.setText("--");
        //检查蓝牙打开没
        if (!bluetoothAdapter.isEnabled()){
            //UI
            bState =31;
             updateUI(bState);
            //弹提醒
            showNoBluetoothDailog();
        }else {
            //蓝牙连接
            mbtService.connect(bluetoothAdapter.getRemoteDevice(mDeviceInfo.devMac));
            showProgressbar();
           // updateUI(37);
        }


    }
    /**
     * 结束蓝牙服务
     */
    private void stopBuletoothServer(){
        unregisterReceiver(mStatusReceive);
        mHandler.removeMessages(BluetoothService.MESSAGE_STATE_NOTHING);
        if (mbtService != null) {
            if (mbtService.getState() == BluetoothService.STATE_CONNECTED) {
                mbtService.stop();
            }
        }
        washing_time.stop();
        connectFailed=0;
        matchFailed=0;
    }


    @Subscribe
    public void onevetMsg(String mes){
        if (mes.equals("washingtimeover")){
            CMDUtils.chaxueshebei(mbtService,true);
        }else if (mes.equals("bind_new_decive")){ //绑定了设备
            stopBuletoothServer();
        }else if (mes.equals("monney_is_change")){ //充值了钱，刷新
            mUserInfo=Common.getUserInfo(sp);
            remainMonney.setText(Common.getShowMonty(mUserInfo.AccMoney + mUserInfo.GivenAccMoney, getString(R.string.yuan1)));

        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        mbtService.setHandlerContext(mHandler);
    }


    @Override
    protected void onRestart() {
        super.onRestart();
       // Log.d("WashingActivity", "onRestart");
    }

    @Override
    protected void onStop() {
        super.onStop();
       // Log.d("WashingActivity", "onStop");
        //手机熄了

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mStatusReceive);
        mHandler.removeMessages(BluetoothService.MESSAGE_STATE_NOTHING);
        if (mbtService != null) {
            if (mbtService.getState() == BluetoothService.STATE_CONNECTED) {
                mbtService.stop();
            }
        }
       // Log.d("WashingActivity", "onDestroy");
        washing_time.stop();
        EventBus.getDefault().unregister(this);
        dialogBuilder=null;

    }

    private void bindclick() {
        //按钮
        deciveIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                switch (bState){
                    case 0: //原始状态
                        break;
                    case 31: //蓝牙未开启
                        //弹出提示框
                        showNoBluetoothDailog();
                        break;
                    case 32: //蓝牙未连接
                        //点击连接蓝牙
                        if (mbtService!=null){
                            mbtService.connect(bluetoothAdapter.getRemoteDevice(mDeviceInfo.devMac));
                        }
                        break;
                    case 33://连接失败
                        //点击重新连接
                        if (mbtService!=null){
                            showProgressbar();
                            mbtService.connect(bluetoothAdapter.getRemoteDevice(mDeviceInfo.devMac));
                        }
                        break;
                    case 40://开始洗衣
                        if (mDeviceInfo ==null){
                            toast("设备信息:NULL");
                            return;
                        }
                        Intent intent =new Intent(WashingActivity.this,WashingModelActivity.class);
                     Bundle bundle =new Bundle();
                        bundle.putSerializable("mDeviceInfo",mDeviceInfo);
                       intent.putExtras(bundle);
                        startActivity(intent);

                        break;
                    case 35://洗衣中
                        //结束交易

                        break;
                    case 36://结束洗衣
                      //  CMDUtils.caijishuju(mbtService,true);
                        break;
                    case 37://连接中

                        break;
                    case 38://蓝牙60秒后失去连接
                        //点击重试
                        if (mbtService!=null){
                            mbtService.connect(bluetoothAdapter.getRemoteDevice(mDeviceInfo.devMac));
                            showProgressbar();
                        }
                        break;
                    case 39://清除记录后
                      //  CMDUtils.chaxueshebei(mbtService,true);
                        if (mDeviceInfo ==null){
                            toast("设备信息:NULL");
                            return;
                        }
                        Intent intent2 =new Intent(WashingActivity.this,WashingModelActivity.class);
                        Bundle bundle2 =new Bundle();
                        bundle2.putSerializable("mDeviceInfo",mDeviceInfo);
                        intent2.putExtras(bundle2);
                        startActivity(intent2);

                        break;
                    case 42:
                        if (mbtService.getState() == BluetoothService.STATE_CONNECTED){
                            CMDUtils.chaxueshebei(mbtService,true);
                        }else {
                            mbtService.connect(bluetoothAdapter.getRemoteDevice(mDeviceInfo.devMac));
                            showProgressbar();
                        }
                        break;

                }
            }
        });

        //未连接到设备
        deciveName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (deciveName.getText().toString().equals("未连接到洗衣机?")){
                    Intent intent = new Intent();
                    intent.setClass(WashingActivity.this, H5Activity.class);
                    intent.putExtra("h5_tag", H5Activity.LBSSB);
                    startActivity(intent);
                }
            }
        });
    }

    /**
     * 更新界面
     */

    private void updateUI(int state) {
        ColorStateList csl = (ColorStateList) getResources().getColorStateList(R.color.red);
        ColorStateList cs2 = (ColorStateList) getResources().getColorStateList(R.color.txt_one);

        pName.setTextColor(cs2);  //项目名字
        switch (state){
            case 31: //未开启
                proState.setText("蓝牙未开启"); //
                deciveIcon.setImageResource(R.drawable.bluetooth_disconnect);
                break;
            case 32: //未连接
                proState.setText("未连接"); //
                deciveIcon.setImageResource(R.mipmap.washing_unconnect);
                break;
            case 33: //链接失败
                hideProgressbar();
                proState.setText("连接失败"); //
                deciveIcon.setImageResource(R.mipmap.washing_unconnect);
                deciveName.setText("未连接到洗衣机?");
                deciveName.setTextColor(csl);
                break;
            case 34: //连接成功
                hideProgressbar();
                connectFailed=0;
                matchFailed=0;
                deciveName.setText(mDeviceInfo.DevName);
                deciveName.setTextColor(cs2);
                proState.setText("开始洗衣"); //
                deciveIcon.setImageResource(R.mipmap.washing);
                //查询设备状态
                CMDUtils.chaxueshebei(mbtService,true);
              //  Log.d("WashingActivity", "查询设备");
                break;
            case 35://工作中 60秒后断开蓝牙
                hideProgressbar();
                proState.setText("洗衣中.."); //
                washing_time.setVisibility(View.VISIBLE);
                deciveIcon.setImageResource(R.drawable.animation_washing);//图标
                AnimationDrawable animationDrawable = (AnimationDrawable) deciveIcon.getDrawable();
                animationDrawable.start();
                break;
            case 36://结束洗衣（设备已经结算，数据未采集）
                proState.setText("正在生成账单,请稍候"); //
                deciveIcon.setImageResource(R.mipmap.washing);
                hideProgressbar();
              //  showStopDailog();
                //去新界面生成订单
                break;
            case 37://连接中
                proState.setText("连接中");
                deciveIcon.setImageResource(R.mipmap.washing_unconnect);//图标
                showProgressbar();
                break;
            case 38: //失去连接
                proState.setText("重新连接"); //状态变更
                deciveIcon.setImageResource(R.drawable.bluetooth_disconnect);
                washing_time.setVisibility(View.GONE);
                washing_time.stop();
                hideProgressbar();
                deciveIcon.setEnabled(true);
                break;
            case 39://清除缓存成功
                proState.setText("开始洗衣");
                deciveIcon.setImageResource(R.mipmap.washing);
                //清除
                if (washingModelInfo!=null){
                    washingModelInfo =null;
                }
                hideProgressbar();
                break;
            case 40:
                proState.setText("开始洗衣");
                deciveIcon.setImageResource(R.mipmap.washing);
                hideProgressbar();
                break;
        }


    }


    private BroadcastReceiver mStatusReceive = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                int blueState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
                switch (blueState) {
                    case BluetoothAdapter.STATE_TURNING_ON:
                        break;
                    case BluetoothAdapter.STATE_ON:
                      //  Log.d("WashingActivity", "蓝牙开启状态");
                        if (dialogBuilder != null && dialogBuilder.isShowing()) {
                            dialogBuilder.dismiss();
                        }
                        bState =32;
                        updateUI(bState);
                        showProgressbar();
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        break;
                    case BluetoothAdapter.STATE_OFF:
                       // Log.d("WashingActivity", "蓝牙关闭了");
                        showNoBluetoothDailog();
                        try {
                            if (mbtService != null) {
                                if (mbtService.getState() == BluetoothService.STATE_CONNECTED) {
                                    mbtService.stop();
                                }
                            }
                        } catch (Exception e) {
                            // TODO: handle exception
                        }
                        bState=31;
                        updateUI(bState);
                        break;

                }
            } else if (intent.getAction().equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                switch (device.getBondState()) {
                    case BluetoothDevice.BOND_BONDING:// 正在配对
                      //  Log.d("WashingActivity", "正在配对");
                        break;
                    case BluetoothDevice.BOND_BONDED:// 配对结束
                      //  Log.d("WashingActivity", "配对完成，开始连接设备");
                        mbtService.connect(device);

                        break;
                    case BluetoothDevice.BOND_NONE:// 取消配对/未配对
                        matchFailed++;
                        if (matchFailed>=3){
                            showConnectFail2();
                          //  Log.d("WashingActivity", "配对失败");
                            bState =33;
                            updateUI(bState);
                        }else {
                            mbtService.connect(bluetoothAdapter.getRemoteDevice(mDeviceInfo.devMac));
                        }

                        break;
                    default:
                        break;
                }

            }

        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (  resultCode ==RESULT_OK){
            if (requestCode==101){//选择的洗衣模式
                washingModelInfo= (WashingModelInfo) data.getExtras().getSerializable("model_chose");

                if (washingModelInfo !=null){
                 //   AppPreference.getInstance().saveWashingModelInfo(washingModelInfo);
                    //  xaifafeilv(wproductid,wdeviceid,wmacBuffer,wtac_timeBuffer);
                    //g更新UI
                    deciveModel.setText(washingModelInfo.getTypename());

                    int a =0;
                    if (washingModelInfo.getMoney()!=null){
                        a =Integer.parseInt(washingModelInfo.getMoney());
                    }
                    decivePerMonney.setText(Common.getShowMonty(a, getString(R.string.yuan1))); //预扣费
                    bState=40;

            }
            /*else if (requestCode==102){ //选择的洗衣机

                    mDeviceInfo = (DeviceInfo) data.getExtras().getSerializable("DeviceInfo");
                    if (mDeviceInfo !=null){
                        if (mbtService !=null){
                            if (mbtService.getState()!=BluetoothService.STATE_CONNECTED){
                                mbtService.connect(bluetoothAdapter.getRemoteDevice(mDeviceInfo.devMac));
                                showProgressbar();
                            }
                        }else {
                            //偷了个懒
                            toast("蓝牙服务已失效，请重启此页面");
                        }

                    }
                }*/

            }
        }
    }

    /**
     * 连接失败
     */
    private void showConnectFail() {
        hideProgressbar();
        if (dialogBuilder ==null){
            return;
        }
        dialogBuilder.withTitle(getString(R.string.tips))
                .withMessage(getString(R.string.connect_fail))
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
     * 蓝牙配对失败
     */
    private void showConnectFail2() {
        hideProgressbar();
        if (dialogBuilder==null){
            return;
        }
        dialogBuilder.withTitle(getString(R.string.tips))
                .withMessage(getString(R.string.connect_fail2))
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
     * 重置蓝牙断开的时间
     * time=60s
     */
    private void resetHandler(){
        mHandler.removeMessages(BluetoothService.MESSAGE_STATE_NOTHING);
        Message message = new Message();
        message.what = BluetoothService.MESSAGE_STATE_NOTHING;
        mHandler.sendMessageDelayed(message, DELAY_TIME);
    }

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case BluetoothService.MESSAGE_NO_XIAFACALLBACK:  //下发
                    showStartFail();
                    //将钱取消
                    if (downRateInfo != null) {
                        PostConsumeData postConsumeData = new PostConsumeData();
                        postConsumeData.downRateInfo = downRateInfo;
                        postConsumeData.productid = wproductid;
                        postConsumeData.devid = mDeviceInfo.DevID;
                        postEmptyDownRate(postConsumeData);
                    }
                    if (washingModelInfo !=null) {
                        washingModelInfo=null;
                    }
                    if (mbtService.getState()==BluetoothService.STATE_CONNECTED){
                        mbtService.stop();

                    }
                    break;

                case BluetoothService.MESSAGE_NO_JIESHUCALLBACK: //结束费率

                    break;

                case BluetoothService.MESSAGE_STATE_NOTHING: //状态通知
                  //  Log.d("WashingActivity", "mbtService.getState():" + mbtService.getState());

                    if (mbtService != null) {
                        if (mbtService.getState() == BluetoothService.STATE_CONNECTED) {
                         //   Log.d("WashingActivity", "stop");
                            mbtService.stop();
                            bState =0;
                        }
                    }
                    break;

                case BluetoothService.MESSAGE_STATE_CHANGE: //状态变更

                    switch (msg.arg1) {
                        case BluetoothService.STATE_CONNECTED: //连上
                            hideProgressbar();
                            bState =34;
                             resetHandler();//重置断开时间
                            break;
                        case BluetoothService.STATE_CONNECTING: //连接中
                            bState=37;
                            break;
                        case BluetoothService.STATE_LISTEN:
                            break;
                        case BluetoothService.STATE_CONNECTION_LOST:  //失连
                            bState=38;
                             break;
                        case BluetoothService.STATE_CONNECTION_FAIL:  //连接失败
                            hideProgressbar();
                            //走3次连接
                            connectFailed++;
                            if (connectFailed >=3){
                                showConnectFail();//连接失败的弹出提示
                                bState =33;
                            }else {
                                mbtService.connect(bluetoothAdapter.getRemoteDevice(mDeviceInfo.devMac));
                            }

                             break;
                        case BluetoothService.STATE_NONE:
                            break;
                    }
                   // Log.d("WashingActivity", "bState:" + bState);
                    updateUI(bState);
                    break;
                case BluetoothService.MESSAGE_WRITE:
                    break;
                case BluetoothService.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    String result = DigitalTrans.bytesToHexString(readBuf);
                    //	processBluetoothDada(result);
                    //从蓝牙水表返回的数据
                    AnalyTools.analyWaterDatas(readBuf);
                    StringBuffer sb =new StringBuffer();
                    for (int i = 0; i < readBuf.length; i++) {
                       sb.append(readBuf[i]);
                    }
                   // Log.d("WashingActivity","sb:"+ sb.toString());
                    break;
            }
        }
    };


    private void showProgressbar() {
        progressBar.setVisibility(View.VISIBLE);
        deciveIcon.setEnabled(false);
        proState.setEnabled(false);
    }

    private void hideProgressbar() {
        progressBar.setVisibility(View.GONE);
        deciveIcon.setEnabled(true);
        proState.setEnabled(true);
    }


    private void startDeal(int mproductid, int mdeviceid, byte[] macBuffer, byte[] tac_timeBuffer) {
        DealRateInfo dealRateInfo =new DealRateInfo();
        dealRateInfo.timeId =DigitalTrans.getTimeID();
        dealRateInfo.usecount =100;
        dealRateInfo.MacType =wtype;
        dealRateInfo.Constype =2; // 扣费方式
        dealRateInfo.ykMoney =5112; //钱
        dealRateInfo.YkTimes =2100; //时间
        dealRateInfo.WRate1 =0;
        dealRateInfo.ChargeMethod =33; //计费方式
        dealRateInfo.MinChargeUnit =1; //计量单位
        dealRateInfo.AutoDisConTime =10000;
        dealRateInfo.ERate1 =0;
        dealRateInfo.GRate1 =0;
        dealRateInfo.ERate2 =0;
        dealRateInfo.Grate2 =0;
        dealRateInfo.ERate3 =0;
        dealRateInfo.Grate3 =0;
        dealRateInfo.fullw =0;
        dealRateInfo.fullTime =0;

        try {
            CMDUtils.dealStart(mbtService,dealRateInfo,mproductid,170,2,macBuffer,tac_timeBuffer,true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 余额不足
     */
    private void showYueBuzu() {
        hideProgressbar();
        if (dialogBuilder ==null){
            return;
        }
        if (mUserInfo.GroupID==1){
            dialogBuilder.withTitle(getString(R.string.tips))
                    .withMessage(getString(R.string.yuebuzu_tip2))
                    .withEffect(Effectstype.Fadein).isCancelable(false)
                    .withButton2Text("我知道了")
                    .setButton2Click(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialogBuilder.dismiss();
                        }
                    }).show();
        }else if (mUserInfo.GroupID==2){
            dialogBuilder.withTitle(getString(R.string.tips))
                    .withMessage(getString(R.string.yuebuzu_tips))
                    .withEffect(Effectstype.Fadein).isCancelable(false)
                  .withButton2Text("我知道了")
                    .setButton2Click(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            dialogBuilder.dismiss();
                        }
                    }).show();
        }

    }

    /**
     * 别人在用洗衣机
     */
    private void showOtherWashing(int time) {
        hideProgressbar();
        if (dialogBuilder ==null){
            return;
        }
        long second =time;
        long days = second / 86400;            //转换天数
        second = second % 86400;            //剩余秒数
        long hours = second / 3600;            //转换小时
        second = second % 3600;                //剩余秒数
        long minutes = second /60;            //转换分钟
        second = second % 60;                //剩余秒数
        System.out.println();
        dialogBuilder.withTitle(getString(R.string.tips))
                .withMessage("此洗衣机已被使用。预计"+minutes+"分钟"+second+"秒后,可以正常使用.")
                .withEffect(Effectstype.Fadein).isCancelable(false)
                .withButton1Text("取消")
                .setButton1Click(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogBuilder.dismiss();
                        finish();
                    }
                }).withButton2Text("好的")
                .setButton2Click(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                       dialogBuilder.dismiss();

                    }
                }).show();
    }
    /**
     * 下发费率失败
     * @param message
     */
    private void showErrorDownRate(String message) {

        hideProgressbar();
        if (dialogBuilder ==null){
            return;
        }
        dialogBuilder.withTitle(getString(R.string.tips)).withMessage(message)
                .withEffect(Effectstype.Fadein).isCancelable(false)
                .withButton2Text(getString(R.string.i_known))
                .setButton2Click(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (mbtService != null) {
                            if (mbtService.getState() == BluetoothService.STATE_CONNECTED) {
                                mbtService.stop();
                            }
                        }
                        dialogBuilder.dismiss();
                    }
                }).show();
    }

    /**
     * 需要交纳押金的设备未交纳
     * @param message
     */
    private void showErrorDeposit(String message) {

        hideProgressbar();
        if (dialogBuilder ==null){
            return;
        }
        dialogBuilder.withTitle(getString(R.string.tips)).withMessage("设备需要交押金。当前用户没交押金，确定交纳押金?")
                .withEffect(Effectstype.Fadein).isCancelable(false)
                .withButton1Text("取消")
                .withButton2Text("确定")
                .setButton1Click(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialogBuilder.dismiss();
                    }
                })
                .setButton2Click(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogBuilder.dismiss();
                        Intent intent =new Intent(WashingActivity.this, MyDespoitActivity.class);
                        intent.putExtra("DevType",mDeviceInfo.DevTypeID+"");
                        startActivity(intent);
                    }
                }).show();
    }

    /**
     * 下发费率
     * @param mproductid
     * @param mdeviceid
     * @param macBuffer
     * @param tac_timeBuffer
     */
    private DownRateInfo downRateInfo;
    private void xaifafeilv(final int mproductid, final int mdeviceid, final byte[] macBuffer, final byte[] tac_timeBuffer) {

        if (Common.isNetWorkConnected(WashingActivity.this)) {
            AjaxParams ajaxParams = new AjaxParams();

            if (mUserInfo == null) {
                return;
            }
            deciveIcon.setEnabled(false);
            ajaxParams.put("PrjID", mproductid + "");
            ajaxParams.put("AccID", mUserInfo.AccID + "");
            ajaxParams.put("GroupID", mUserInfo.GroupID + "");
            ajaxParams.put("DevID", mdeviceid + "");
            ajaxParams.put("consumeMothe", "1");
            ajaxParams.put("xfMothe", washingModelInfo.getTypeid()+"");
            ajaxParams.put("loginCode",mUserInfo.TelPhone+","+mUserInfo.loginCode);
            ajaxParams.put("phoneSystem", "Android");
            ajaxParams.put("version", MyApp.versionCode);
          //  Log.d("WashingActivity", "ajaxParams:" + ajaxParams);
            new FinalHttp().get(Common.BASE_URL + "downRate", ajaxParams,
                    new AjaxCallBack<Object>() {

                        @Override
                        public void onSuccess(Object t) {
                            super.onSuccess(t);
                            String result = t.toString();
                            //Log.d("WashingActivity","下发费率：="+ result);
                            PublicGetData publicGetData = new Gson().fromJson(result, PublicGetData.class);
                            if (publicGetData.error_code.equals("0")) {


                                 downRateInfo = new Gson().fromJson(publicGetData.data, DownRateInfo.class);
                                if (downRateInfo != null) {
                                    int a =washingModelInfo.getTypeid();
                                    if (a>36 || a<33){
                                        toast("洗衣模式错误!");
                                        return;
                                    }
                                    DealRateInfo dealRateInfo =new DealRateInfo();
                                    String time =downRateInfo.ConsumeDT;
                                    dealRateInfo.timeId = time.substring(2,time.length());
                                    dealRateInfo.usecount =downRateInfo.UseCount;
                                    dealRateInfo.MacType =wtype;
                                    dealRateInfo.Constype =2;
                                    dealRateInfo.ykMoney =Integer.parseInt(washingModelInfo.getMoney()); //钱
                                    dealRateInfo.YkTimes =Integer.parseInt(washingModelInfo.getTimes()); //时间
                                    dealRateInfo.WRate1 =0;
                                    dealRateInfo.ChargeMethod =washingModelInfo.getTypeid(); //计费方式
                                    dealRateInfo.MinChargeUnit =1; //计量单位
                                    dealRateInfo.AutoDisConTime =10000;
                                    dealRateInfo.ERate1 =0;
                                    dealRateInfo.GRate1 =0;
                                    dealRateInfo.ERate2 =0;
                                    dealRateInfo.Grate2 =0;
                                    dealRateInfo.ERate3 =0;
                                    dealRateInfo.Grate3 =0;
                                    dealRateInfo.fullw =0;
                                    dealRateInfo.fullTime =0;
                                  //  Log.d("WashingActivity", "mUserInfo.AccID:" + mUserInfo.AccID);
                                  //  Log.d("WashingActivity", DigitalTrans.getTimeID());
                                    if (mUserInfo.GroupID ==2){ //用户
                                        try {
                                            CMDUtils.dealStart(mbtService,dealRateInfo,mproductid,mUserInfo.AccID,2,macBuffer,tac_timeBuffer,true);
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }else {  //管理员
                                        try {
                                            CMDUtils.dealStart(mbtService,dealRateInfo,mproductid,mUserInfo.AccID,1,macBuffer,tac_timeBuffer,true);
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    //丢包的一个处理
                                    Message message = new Message();
                                    message.what = BluetoothService.MESSAGE_NO_XIAFACALLBACK;
                                    mHandler.sendMessageDelayed(message, 4000);
                                } else {
                                    toast("费率参数:null");
                                    deciveIcon.setEnabled(true);
                                }
                            } else if (publicGetData.error_code.equals("7")) {
                                Common.logout2(WashingActivity.this, sp, dialogBuilder,publicGetData.message);
                                hideProgressbar();
                            } else if(publicGetData.error_code.equals("2")){//设备需要缴纳押金
                                showErrorDeposit(publicGetData.message);
                                hideProgressbar();
                            }else if (publicGetData.error_code.equals("4")){//设备被预约
                                showErrorDownRate(publicGetData.message);
                                hideProgressbar();
                            }else if (publicGetData.error_code.equals("6")){//余额不足
                                showYueBuzu();
                                hideProgressbar();
                            }else {
                                deciveIcon.setEnabled(true);
                                if (publicGetData.message.contains("余额不足")) {
                                    showYueBuzu();
                                    updateUserInfo(mUserInfo);
                                } else {
                                    showErrorDownRate(publicGetData.message);

                                }
                                hideProgressbar();
                            }


                        }

                        @Override
                        public void onFailure(Throwable t, int errorNo, String strMsg) {
                            super.onFailure(t, errorNo, strMsg);
                           // Log.d("WashingActivity", strMsg);
                            hideProgressbar();
                            deciveIcon.setEnabled(true);
                        }
                    });
        } else {
            hideProgressbar();
            Common.showNoNetworkDailog(dialogBuilder,WashingActivity.this);
        }
    }
    private void postEmpty(){

        if (downRateInfo != null) {
            PostConsumeData postConsumeData = new PostConsumeData();
            postConsumeData.downRateInfo = downRateInfo;
            postConsumeData.productid = wproductid;
            postConsumeData.devid = mDeviceInfo.DevID;
            postEmptyDownRate(postConsumeData);
        }
    }

    /**
     * 上传消费数据
     * @param postConsumeData
     */
    private void postEmptyDownRate(PostConsumeData postConsumeData) {

        OkHttpClient client =new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        RequestBody requestBody=new FormBody.Builder()
                .add("PrjID",""+ postConsumeData.productid )
                .add("AccID",""+mUserInfo.AccID )
                .add("DevID",""+ postConsumeData.devid )
                .add("GroupID",""+ mUserInfo.GroupID )
                .add("UpMoney",""+0)
                .add("PerMoney",""+ postConsumeData.downRateInfo.PerMoney)
                .add("ConsumeDT",downRateInfo.ConsumeDT)
                .add("devType",mDeviceInfo.Dsbtypeid+"")
                .add("loginCode",mUserInfo.TelPhone + "," + mUserInfo.loginCode)
                .add("phoneSystem", "Android")
                .add("version",MyApp.versionCode)
                .build();
        Request request =new Request.Builder()
                .url(Common.BASE_URL + "savexf")
                .post(requestBody)
                .build();

       client.newCall(request).enqueue(new Callback() {
           @Override
           public void onFailure(Call call, IOException e) {

           }

           @Override
           public void onResponse(Call call, Response response) throws IOException {

           }
       });
    }
    /**
     * 更新用户信息
     * @param mInfo
     */
    private void updateUserInfo(final UserInfo mInfo) {

        if (Common.isNetWorkConnected(WashingActivity.this)) {

            if (TextUtils.isEmpty(mInfo.TelPhone + "")) {
                Common.showToast(WashingActivity.this, R.string.phonenum_null, Gravity.CENTER);
                return;
            }
            if (!Common.isPhoneNum(mInfo.TelPhone + "")) {
                Common.showToast(WashingActivity.this, R.string.phonenum_not_irregular, Gravity.CENTER);
                return;
            }

            AjaxParams ajaxParams = new AjaxParams();
            ajaxParams.put("TelPhone", mInfo.TelPhone + "");
            ajaxParams.put("PrjID", mInfo.PrjID + "");
            ajaxParams.put("WXID", "0");
            ajaxParams.put("loginCode", mInfo.TelPhone + "," + mInfo.loginCode);
            ajaxParams.put("phoneSystem", "Android");
            ajaxParams.put("version", MyApp.versionCode);
            if (mUserInfo.GroupID==1){
                ajaxParams.put("isOpUser","1");
            }else {
                ajaxParams.put("isOpUser","0");
            }
            new FinalHttp().get(Common.BASE_URL + "accountInfo", ajaxParams,
                    new AjaxCallBack<Object>() {

                        @Override
                        public void onSuccess(Object t) {
                            super.onSuccess(t);
                            String result = t.toString();
                          //  Log.d("WashingActivity", "更新个人信息：="+result);
                            PublicGetData publicGetData = new Gson().fromJson(result, PublicGetData.class);
                            if (publicGetData.error_code.equals("0")) {

                                UserInfo info = new Gson().fromJson(publicGetData.data, UserInfo.class);
                                info.loginCode = mInfo.loginCode;
                                if (info != null) {
                                    SharedPreferences.Editor editor = sp.edit();
                                    editor.putString(Common.USER_PHONE_NUM, info.TelPhone + "");
                                    editor.putString(Common.USER_INFO, new Gson().toJson(info));
                                    editor.putInt(Common.ACCOUNT_IS_USER, info.GroupID);
                                    editor.commit();
                                    try {
                                        remainMonney.setText(Common.getShowMonty(info.AccMoney + info.GivenAccMoney, getString(R.string.yuan1)));

                                    } catch (Exception e) {
                                        // TODO: handle exception
                                    }

                                }

                            }

                        }

                        @Override
                        public void onFailure(Throwable t, int errorNo, String strMsg) {
                            super.onFailure(t, errorNo, strMsg);
                        }
                    });

        }

    }

    /**
     * 上传消费数据
     * @param ismine
     * @param prjid
     * @param accoutid
     * @param deviceid
     * @param groupid
     * @param consumeMoney
     * @param preMoney
     * @param consumetime
     * @param usercount
     */
    private int submitNum=0;//提交次数

    private void postXiaoFei(final boolean ismine, final int prjid, final int accoutid, final int deviceid, final int groupid,
                             int consumeMoney, final int preMoney, final String consumetime, final int usercount) {

        if (!Common.isNetWorkConnected(WashingActivity.this)){
            Common.showNoNetworkDailog(dialogBuilder,WashingActivity.this);
            return;
        }
        OkHttpClient client =new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        RequestBody requestBody=new FormBody.Builder()
                .add("PrjID",""+prjid)
                .add("AccID",""+accoutid)
                .add("DevID",""+mDeviceInfo.DevID)
                .add("GroupID",""+groupid)
                .add("UpMoney",""+consumeMoney)
                .add("PerMoney",""+ preMoney)
                .add("ConsumeDT","20" + consumetime)
                .add("devType",mDeviceInfo.Dsbtypeid+"")
                .add("loginCode", mUserInfo.TelPhone + "," + mUserInfo.loginCode)
                .add("phoneSystem", "Android")
                .add("version",MyApp.versionCode)
                .build();
        Request request =new Request.Builder()
                .url(Common.BASE_URL + "savexf")
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result =response.body().string();
               // Log.d("WashingActivity","上传消费数据：="+ result);
                final PublicPostConsumeData publicPostConsumeData = new Gson().fromJson(result, PublicPostConsumeData.class);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (publicPostConsumeData.error_code.equals("0")) {
                            try {
                                CMDUtils.fanhuicunchu(mbtService,true,consumetime,prjid,deviceid,accoutid,usercount);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            //  {"message":"存储过程运行异常","error_code":"2"}
                        } else if (publicPostConsumeData.error_code.equals("7")) {

                            Common.logout2(WashingActivity.this, sp, dialogBuilder,publicPostConsumeData.message);
                        } else {
                            hideProgressbar();
                            //提交三次失败，清除掉这条数据
                            // 提示
                            submitNum++;

                            if (submitNum>=3){
                                //三次采集都失败，将空数据传给后台，补扣费
                                //下发费率，传空数据给后台。返回预扣费

                                try {
                                    CMDUtils.fanhuicunchu(mbtService,true,consumetime,prjid,deviceid,accoutid,usercount);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }else {
                                //showInterruptDeviceDailog();
                                CMDUtils.caijishuju(mbtService,true);
                            }

                        }
                    }
                });

            }
        });


    }

    /**
     * 根据mAC地址查询洗衣机最近消费
     */
    private void queryMdelByMac(){

        AjaxParams params =new AjaxParams();
        params.put("DevMac",mDeviceInfo.devMac);
        params.put("PrjID",""+mDeviceInfo.PrjID);
        params.put("loginCode",mUserInfo.TelPhone+","+mUserInfo.loginCode);
        params.put("phoneSystem", "Android");
        params.put("version", MyApp.versionCode);
       // Log.d("WashingActivity", "params:" + params);
        new FinalHttp().get(Common.BASE_URL + "getOrderByMac", params, new AjaxCallBack<Object>() {
            @Override
            public void onSuccess(Object result) {
                super.onSuccess(result);
              //  Log.d("----------", "result:" + result);

                WashingPerMonneyResult perMonneyResult =new Gson().fromJson(result.toString(),WashingPerMonneyResult.class);
                if (perMonneyResult.getError_code().equals("0")){
                    WashingPerMonneyBean monneyBean =perMonneyResult.getData();
                    if (monneyBean!=null){
                        decivePerMonney.setText(Common.getShowMonty(Integer.parseInt(monneyBean.getPerMoney()), getString(R.string.yuan1))); //预扣费
                    }
                }
            }

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                toast(strMsg);
            }
        });
    }

    private String consumeTimeId;
    private String consumeYk;
    @Override
    public void caijishujuOnback(boolean b, String timeid, int mproductid, int mdeviceid,
                                 int maccountid,String accounttypeString, int usercount, String ykmoneyString,
                                 String consumeMoneString, String rateString, String macString) {

        if (b){
            consumeTimeId=timeid;
            consumeYk=ykmoneyString;



            if (dAccountId ==mUserInfo.AccID){
                postXiaoFei(true, mproductid, maccountid, mdeviceid, Integer.parseInt(accounttypeString), Integer.parseInt(consumeMoneString),
                        Integer.parseInt(ykmoneyString), timeid, usercount);

            }else {
                postXiaoFei(false, mproductid, maccountid, mdeviceid, Integer.parseInt(accounttypeString), Integer.parseInt(consumeMoneString),
                        Integer.parseInt(ykmoneyString), timeid, usercount);
            }


        }

    }



    @Override
    public void jieshufeilvOnback(boolean b ,int accountid) {



    }

    @Override
    public void xiafafeilvOnback(boolean b) {

    }

    @Override
    public void fanhuicunchuOnback(boolean b) {

        if (b){
           // Log.d("WashingActivity", "清除记录");
            if (dAccountId==mUserInfo.AccID){
                if (mUserInfo.GroupID==2){
                    Intent intent = new Intent();
                    intent.setClass(WashingActivity.this, WashingConsumeActivity.class);
                    intent.putExtra("time",consumeTimeId);
                    intent.putExtra("ykmonney",consumeYk);
                    intent.putExtra("model",mConstype+"");
                    startActivity(intent);
                    finish();
                }
            }
            //初始化
            bState =39;
            updateUI(bState);

        }
    }

    private int mType;
    private int littleType;
    private  int dAccountId =0;  //设备查询到正在进行中的订单的账号
    private int userId =1002;  //用户的ID
    private int wproductid;
    private byte[] wmacBuffer;
    private byte[] wtac_timeBuffer;
    private String wtype;
    private int mConstype;//洗衣模式
    @Override
    public void chaxueNewshebeiOnback(boolean b, int charge, int mdeviceid, int mproductid, int maccountid,
                                      byte[] macBuffer, byte[] tac_timeBuffer, int macType,int lType, int constype, int macTime) {


       // Log.d("WashingActivity", "mdeviceid:" + mdeviceid);
       // Log.d("WashingActivity", "mproductid:" + mproductid);
      //  Log.d("-------", "maccountid:" + maccountid);
       // Log.d("WashingActivity", "macType:" + macType);
       // Log.d("WashingActivity", "lType:" + lType);
       // Log.d("WashingActivity", "charge:" + charge);
       // Log.d("WashingActivity", "constype:" + constype);
        resetHandler();
        mConstype =constype; //洗衣模式
        mType =macType; //设备类型  大类
        littleType =lType;
        dAccountId =maccountid;
        wproductid =mproductid;
        wmacBuffer =macBuffer;
        wtac_timeBuffer =tac_timeBuffer;
        wtype=macType+"&"+lType;

        if (!b){
            Toast.makeText(this, "查询设备失败,请稍后重试", Toast.LENGTH_SHORT).show();
        }
        if (mdeviceid !=mDeviceInfo.DevID){
            if (mbtService.getState()==BluetoothService.STATE_CONNECTED){
                mbtService.stop();
            }
          switch (mUserInfo.GroupID){
              case 1:
                  showStartFail6();
                  break;
              case 2:
                  showStartFail5();
                  break;
          }
            return;
        }
        if (mUserInfo.GroupID==2){
            if (mproductid==0){
                if (mbtService.getState()==BluetoothService.STATE_CONNECTED){
                    mbtService.stop();
                }
                showStartFail3();
                return;
            }else {
                if (mDeviceInfo.DevTypeID !=lType ||mUserInfo.PrjID!=mproductid){ //当洗衣机信息被篡改后，提示
                    if (mbtService.getState()==BluetoothService.STATE_CONNECTED){
                        mbtService.stop();
                    }
                    showStartFail2();
                    return;
                }
            }

        }else if (mUserInfo.GroupID==1){
            if (mDeviceInfo.DevTypeID !=lType){
                if (mbtService.getState()==BluetoothService.STATE_CONNECTED){
                    mbtService.stop();
                }
                showStartFail4();
                return;
            }
        }

        switch (macType){


            case 2://洗衣机
                switch (charge){
                    case 0:
                        bState =40;
                        updateUI(bState);
                        if (washingModelInfo !=null){
                            //查询设备直接，下发费率
                            bState=40;
                            showProgressbar();
                            xaifafeilv(wproductid,mDeviceInfo.DevID,wmacBuffer,wtac_timeBuffer);
                        }
                        break;
                    case 1://订单进行中
                        //判断一下是不是自己再用洗衣机，是的话就显示剩余时间
                        if (mUserInfo.AccID ==maccountid){
                            //倒计时启动
                            times =macTime;
                            //Log.d("WashingActivity", "times:" + times);
                            washing_time.setTime(times);

                            bState =35;
                            updateUI(bState);
                            String modelString="";

                            switch (constype){
                                case 33:
                                    modelString ="标准洗";

                                    break;
                                case 34:
                                    modelString ="快速洗";

                                    break;
                                case 35:
                                    modelString ="单脱水";

                                    break;
                                case 36:
                                    modelString ="大件洗";

                                    break;
                            }
                            deciveModel.setText(modelString);
                            queryMdelByMac();//根据MAC地址查询洗衣机最近的消费情况

                        }else {
                            showOtherWashing(macTime);
                            bState=42;
                        }

                        break;
                    case 2:
                        break;
                    case 3:
                        //数据未采集
                        bState =36;
                        updateUI(bState);
                        CMDUtils.caijishuju(mbtService,true);
                        break;
                }
                break;


        }

    }



    @Override
    public void startDeal(boolean b) {
        mHandler.removeMessages(BluetoothService.MESSAGE_NO_XIAFACALLBACK);
        hideProgressbar();
        if (b){
           // Log.d("WashingActivity", "开始交易");

            bState =35;
            updateUI(bState);
             times =Integer.parseInt( washingModelInfo.getTimes());
            washing_time.setTime(times);
            if (washingModelInfo !=null) {
                washingModelInfo=null;
            }
            updateUserInfo(mUserInfo); //更新我的钱
            dAccountId=mUserInfo.AccID;

        }else {
            showStartFail();
            //将钱取消
            if (downRateInfo != null) {
                PostConsumeData postConsumeData = new PostConsumeData();
                postConsumeData.downRateInfo = downRateInfo;
                postConsumeData.productid = wproductid;
                postConsumeData.devid = mDeviceInfo.DevID;
                postEmptyDownRate(postConsumeData);
            }
            if (washingModelInfo !=null) {
                washingModelInfo=null;
            }
           if (mbtService.getState()==BluetoothService.STATE_CONNECTED){
               mbtService.stop();

           }
        }
    }

    @Override
    public void stopDeal(boolean b) {
        if (b){
           // Log.d("WashingActivity", "结束交易");
            CMDUtils.caijishuju(mbtService,true);
        }else {
          //  Log.d("WashingActivity", "结束交易失败");
            CMDUtils.chaxueshebei(mbtService,true);
        }
    }


    /**
     * 连接失败
     */
    private void showStartFail() {
        hideProgressbar();
        if (dialogBuilder ==null){
            return;
        }
        dialogBuilder.withTitle(getString(R.string.tips))
                .withMessage("对不起,洗衣开始失败了.请尝试其他设备!")
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
     * 设备更改后
     */
    private void showStartFail2() {
        hideProgressbar();
        if (dialogBuilder ==null){
            return;
        }
        dialogBuilder.withTitle(getString(R.string.tips))
                .withMessage("洗衣机信息已修改,请重新绑定")
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
     * 设备更改后
     */
    private void showStartFail3() {
        hideProgressbar();
        if (dialogBuilder ==null){
            return;
        }
        dialogBuilder.withTitle(getString(R.string.tips))
                .withMessage("该设备信息已修改,请联系管理员")
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
     * 设备更改后
     */
    private void showStartFail4() {
        hideProgressbar();
        if (dialogBuilder ==null){
            return;
        }
        dialogBuilder.withTitle(getString(R.string.tips))
                .withMessage("该设备信息已修改,请重新登记")
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
     * 设备更改后
     */
    private void showStartFail5() {
        hideProgressbar();
        if (dialogBuilder ==null){
            return;
        }
        dialogBuilder.withTitle(getString(R.string.tips))
                .withMessage("该设备信息已修改,请联系管理员")
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
     * 设备更改后
     */
    private void showStartFail6() {
        hideProgressbar();
        if (dialogBuilder ==null){
            return;
        }
        dialogBuilder.withTitle(getString(R.string.tips))
                .withMessage("该设备编号不一致,请重新登记")
                .withEffect(Effectstype.Fadein).isCancelable(false)
                .withButton2Text(getString(R.string.i_known))
                .setButton2Click(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogBuilder.dismiss();
                    }
                }).show();
    }
}
