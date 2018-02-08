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
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jooronjar.BluetoothService;
import com.example.jooronjar.DownRateInfo;
import com.example.jooronjar.utils.AnalyTools;
import com.example.jooronjar.utils.CMDUtils;
import com.example.jooronjar.utils.DigitalTrans;
import com.example.jooronjar.utils.WaterCodeListener;
import com.google.gson.Gson;
import com.klcxkj.mylibrary.R;
import com.klcxkj.zqxy.MyApp;
import com.klcxkj.zqxy.common.Common;
import com.klcxkj.zqxy.databean.DeviceInfo;
import com.klcxkj.zqxy.databean.PostConsumeData;
import com.klcxkj.zqxy.databean.UserInfo;
import com.klcxkj.zqxy.databean.WashingPerMonneyBean;
import com.klcxkj.zqxy.databean.WashingPerMonneyResult;
import com.klcxkj.zqxy.response.PublicGetData;
import com.klcxkj.zqxy.response.PublicPostConsumeData;
import com.klcxkj.zqxy.widget.Effectstype;
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


/**
 * autor:OFFICE-ADMIN
 * time:2017/11/29
 * email:yinjuan@klcxkj.com
 * description:洗澡2/自己绘制的
 */
public class Bath2Activity extends BaseActivity implements WaterCodeListener {


    private static final int DELAY_TIME = 60000;
    //设备
    private TextView pName ;//项目名字
    private ImageView deciveIcon;
    private ProgressBar progressBar;
    private TextView proState; //状态
    //订单信息
    private TextView deciveName;
    private TextView decivePerMonney;//预扣金额
    private TextView remainMonney; //余额
    //very import   state for buletooth connet
    private  int bState =0;
    //lanya
    private BluetoothService mbtService = null;
    private BluetoothAdapter bluetoothAdapter;

    private SharedPreferences sp;
    private UserInfo userInfo;
    private DeviceInfo mDeviceInfo; //设备
    private  int mipMapId_unConnected ;//图标 未连上/连接失败
    private  int mipMapId_connected ;//连接上的图标
    private  int mipMapId_anmi=0 ;//连接上动画的图标

    private int isUser =0;  //用户是否点击了开始
    private int connectFailed =0; //失败连接次数
    private int matchFailed =0; //配对失败重配次数
    //设备类型
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bath2);
        sp =getSharedPreferences("adminInfo", Context.MODE_PRIVATE);
        userInfo = Common.getUserInfo(sp);
        mDeviceInfo = (DeviceInfo) getIntent().getExtras().getSerializable("DeviceInfo");
        initview();
        showUI();
        initdata();
        bindclick();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        mDeviceInfo = (DeviceInfo) intent.getExtras().getSerializable("DeviceInfo");
        initview();
        showUI();
        initdata();
        bindclick();
    }



    String str ="";
    private void showUI() {

      if (mDeviceInfo !=null){
          switch (mDeviceInfo.Dsbtypeid){
              case 4: //热水
                  mipMapId_unConnected=R.drawable.start_brathe_normal;
                  mipMapId_connected=R.drawable.start_brathe_select;
                 mipMapId_anmi=R.drawable.animation_brathe;
                  str ="洗澡";
                  break;
              case 5: //饮水
                  mipMapId_unConnected=R.drawable.start_water_normal;
                  mipMapId_connected=R.drawable.start_water_select;
                  mipMapId_anmi=R.drawable.animation_water;
                  str ="饮水";
                  break;
              case 7://吹风机
                  mipMapId_unConnected=R.mipmap.dryer_unconnected;
                  mipMapId_connected=R.mipmap.dryer_unconnected;
                  str ="吹风机";
                  break;
              case 8://充电
                  mipMapId_unConnected=R.mipmap.dryer_unconnected;
                  mipMapId_connected=R.mipmap.dryer_unconnected;
                  str ="充电器";
                  break;
              case 9://空调
                  mipMapId_unConnected=0;
                  mipMapId_connected=0;
                  mipMapId_anmi=0;
                  str ="空调";
                  break;

              case 255: //其他
                  mipMapId_unConnected=0;
                  mipMapId_connected=0;
                  mipMapId_anmi=0;
                  str ="设备";
                  break;
          }
          showMenu(str);
          //初始化
          deciveIcon.setImageResource(mipMapId_unConnected);  //图标
          proState.setText("未连接"); //蓝牙连接状态
          pName.setText(mDeviceInfo.PrjName);  //项目名称

      }
    }

    @Subscribe
    public void eventMsg(String msg){
        if (msg.equals("bind_new_decive")){
            stopBuletoothServer();//停止此蓝牙服务
        }else if (msg.equals("monney_is_change")){ //充值回来啦
            userInfo =Common.getUserInfo(sp);
            remainMonney.setText(Common.getShowMonty(userInfo.AccMoney + userInfo.GivenAccMoney, getString(R.string.yuan1)));//我的余额

        }
    }
    @Override
    protected void onDestroy() {
        unregisterReceiver(mStatusReceive);
        mHandler.removeMessages(BluetoothService.MESSAGE_STATE_NOTHING);
        if (mbtService != null) {
            if (mbtService.getState() == BluetoothService.STATE_CONNECTED) {
                mbtService.stop();
            }
        }
        super.onDestroy();
        dialogBuilder=null;
        EventBus.getDefault().unregister(this);
    }

    private void initview() {

        //1
        pName = (TextView) findViewById(R.id.project_name_txt);
        deciveIcon = (ImageView) findViewById(R.id.device_state_img);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        proState = (TextView) findViewById(R.id.device_connect_state_txt);
        //2
        deciveName = (TextView) findViewById(R.id.device_name_txt);
        decivePerMonney = (TextView) findViewById(R.id.monney_bill);
        remainMonney = (TextView) findViewById(R.id.monney_remain);
        //设备连接状态
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        //服务注册handler进来
        mbtService = BluetoothService.sharedManager();
        mbtService.setHandlerContext(mHandler);


        IntentFilter statusFilter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        statusFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);// 状态改变
        registerReceiver(mStatusReceive, statusFilter);
        //蓝牙连接
        AnalyTools.setWaterCodeLisnter(this);

    }
    private void initdata() {

        //下面三个
        deciveName.setText(mDeviceInfo.DevName); //设备名字
        decivePerMonney.setText(Common.getShowMonty(0, getString(R.string.yuan1))); //预扣费
        remainMonney.setText(Common.getShowMonty(userInfo.AccMoney + userInfo.GivenAccMoney, getString(R.string.yuan1)));//我的余额


        //检查蓝牙打开没
        if (!bluetoothAdapter.isEnabled()){
            //UI
            bState =31;
            updateUI(bState);
            //弹提醒
            showNoBluetoothDailog();
        }else {
            //蓝牙连接
            //bState =41;
           // updateUI(bState);
            mbtService.connect(bluetoothAdapter.getRemoteDevice(mDeviceInfo.devMac));

        }

        isUser=0;
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
        connectFailed=0;
        matchFailed=0;
    }
    private void bindclick() {
        //按钮
        deciveIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              //  Log.d("Bath2Activity", "bState:" + bState);
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
                    case 40://开始
                        //下发费率
                      //  Log.d("Bath2Activity", "开始");
                        isUser=1;
                        CMDUtils.chaxueshebei(mbtService,true);
                        showProgressbar();
                        break;
                    case 35://中ing
                        //结束交易

                        if (userInfo.AccID ==dAccountId){//自己的

                            showStopDailog();
                        }else {

                        }
                        break;
                    case 36://结束
                        CMDUtils.caijishuju(mbtService,true);
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
                        if (mbtService !=null){
                            if (mbtService.getState() ==BluetoothService.STATE_CONNECTED){
                                isUser=1;
                                CMDUtils.chaxueshebei(mbtService,true);
                            }else {
                                mbtService.connect(bluetoothAdapter.getRemoteDevice(mDeviceInfo.devMac));
                                showProgressbar();
                            }
                        }
                        break;
                    case 42:
                        if (mbtService!=null){
                            mbtService.connect(bluetoothAdapter.getRemoteDevice(mDeviceInfo.devMac));
                            showProgressbar();
                        }
                        break;

                }
            }
        });
        deciveName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (deciveName.getText().toString().equals("未连接到设备?")){
                    Intent intent = new Intent();
                    intent.setClass(Bath2Activity.this, H5Activity.class);
                    intent.putExtra("h5_tag", H5Activity.LBSSB);
                    startActivity(intent);
                }
            }
        });

        if ( mDeviceInfo.Dsbtypeid ==4 &&userInfo.GroupID==2){   //热水表
            rightTxt.setVisibility(View.VISIBLE);
            rightTxt.setText("更换水表");
            rightTxt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent in =new Intent(Bath2Activity.this,SearchBratheDeviceActivity.class);
                    in.putExtra("capture_type", CaptureActivity.CAPTURE_WATER);
                    startActivity(in);
                }
            });
        }

        if ( mDeviceInfo.Dsbtypeid ==5 &&userInfo.GroupID==2){   //饮水机
            rightTxt.setVisibility(View.VISIBLE);
            rightTxt.setText("更换开水器");
          rightTxt.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View view) {
                  Intent intent = new Intent();
                  intent.setClass(Bath2Activity.this, WaterDeviceListActivity.class);
                  intent.putExtra("capture_type",5);
                  startActivity(intent);
              }
          });

        }
        if ( mDeviceInfo.Dsbtypeid ==7 &&userInfo.GroupID==2){   //饮水机
            rightTxt.setVisibility(View.VISIBLE);
            rightTxt.setText("更换吹风机");
            rightTxt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent();
                    intent.setClass(Bath2Activity.this, WaterDeviceListActivity.class);
                    intent.putExtra("capture_type",7);
                    startActivity(intent);
                }
            });

        }


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
                deciveIcon.setImageResource(mipMapId_unConnected);
                break;
            case 33: //链接失败
                hideProgressbar();
                proState.setText("连接失败"); //
                deciveIcon.setImageResource(mipMapId_unConnected);
                deciveName.setText("未连接到设备?");
                deciveName.setTextColor(csl);
                break;
            case 34: //连接成功
                connectFailed=0;
                matchFailed=0;
                hideProgressbar();
                deciveName.setText(mDeviceInfo.DevName);
                deciveName.setTextColor(cs2);
                //查询设备状态
                CMDUtils.chaxueshebei(mbtService,true);
                proState.setText("连接成功"); //
                deciveIcon.setImageResource(mipMapId_connected);
                break;
            case 35://工作中 60秒后断开蓝牙
                proState.setText("结束"+str);
                if (mipMapId_anmi !=0){
                    deciveIcon.setImageResource(mipMapId_anmi);//图标R.drawable.animation_washing
                    AnimationDrawable animationDrawable = (AnimationDrawable) deciveIcon.getDrawable();
                    animationDrawable.start();
                }else {
                    deciveIcon.setImageResource(R.mipmap.dryer_connected);//图标R.drawable.animation_washing
                }

                break;
            case 36://结束洗衣（设备已经结算，数据未采集）
                proState.setText("结束订单"); //
                deciveIcon.setImageResource(mipMapId_connected);//R.mipmap.washing
                //  showStopDailog();
                //去新界面生成订单
                break;
            case 37://连接中
                //Log.d("Bath2Activity", "你想干嘛？--806577071@qq.com");
                proState.setText("连接中.");
                deciveIcon.setImageResource(mipMapId_unConnected);//图标
                showProgressbar();
                break;
            case 38: //失去连接
                hideProgressbar();
                proState.setText("点击重试"); //状态变更
                deciveIcon.setImageResource(R.drawable.bluetooth_disconnect);
                //t弹窗消失
                if (dialogBuilder !=null && dialogBuilder.isShowing()){
                    dialogBuilder.dismiss();
                }
                deciveIcon.setEnabled(true);
                break;
            case 39://清除缓存成功
                switch (mDeviceInfo.Dsbtypeid){
                    case 4: //洗澡
                        proState.setText("开始洗澡");
                        break;
                    case 5://用水
                        proState.setText("开始用水");
                        break;
                    case 7: //吹风
                        proState.setText("开始使用");
                        break;
                    case 8: //充电
                        proState.setText("开始使用");
                        break;
                    case 9://空调
                        proState.setText("开始使用");
                        break;
                    case 255: //其他
                        proState.setText("开始使用");
                        break;
                }

                deciveIcon.setImageResource(mipMapId_connected);
                break;
            case 40:
                switch (mDeviceInfo.Dsbtypeid){
                    case 4: //洗澡
                        proState.setText("开始洗澡");
                        break;
                    case 5://用水
                        proState.setText("开始用水");
                        break;
                    case 7: //吹风
                        proState.setText("开始使用");
                        break;
                    case 8: //充电
                        proState.setText("开始使用");
                        break;
                    case 9://空调
                        proState.setText("开始使用");
                        break;
                    case 255: //其他
                        proState.setText("开始使用");
                        break;
                }
                deciveIcon.setImageResource(mipMapId_connected);
                break;
            case 41:
                showProgressbar();
                deciveIcon.setImageResource(R.drawable.bluetooth_disconnect);
                proState.setText("蓝牙配对中."); //状态变更
                break;
            case 42:
                hideProgressbar();
                proState.setText("点击重试"); //状态变更
                deciveIcon.setImageResource(R.drawable.bluetooth_disconnect);
                break;
        }


    }
    /**
     *                    .::::.
     *                  .::::::::.
     *                 :::::::::::
     *             ..:::::::::::'
     *           '::::::::::::'
     *             .::::::::::
     *        '::::::::::::::..
     *             ..::::::::::::.
     *           ``::::::::::::::::
     *            ::::``:::::::::'        .:::.
     *           ::::'   ':::::'       .::::::::.
     *         .::::'      ::::     .:::::::'::::.
     *        .:::'       :::::  .:::::::::' ':::::.
     *       .::'        :::::.:::::::::'      ':::::.
     *      .::'         ::::::::::::::'         ``::::.
     *  ...:::           ::::::::::::'              ``::.
     * ```` ':.          ':::::::::'                  ::::..
     *                    '.:::::'                    ':'````..
     */
    private BroadcastReceiver mStatusReceive = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                int blueState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
                switch (blueState) {
                    case BluetoothAdapter.STATE_TURNING_ON:
                        break;
                    case BluetoothAdapter.STATE_ON:
                      //  Log.d("Bath2Activity", "蓝牙开启状态");
                        if (dialogBuilder != null && dialogBuilder.isShowing()) {
                            dialogBuilder.dismiss();
                        }
                        bState =32;
                        updateUI(bState);
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        break;
                    case BluetoothAdapter.STATE_OFF:
                       // Log.d("WashngActivity", "蓝牙关闭了");
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
                        break;
                    case BluetoothDevice.BOND_BONDED:// 配对结束

                        mbtService.connect(device);

                        break;
                    case BluetoothDevice.BOND_NONE:// 取消配对/未配对
                        matchFailed++;
                        if (matchFailed>=3){
                            showConnectFail2();
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

    /**
     * 连接失败
     */
    private void showConnectFail() {
        hideProgressbar();
        if (dialogBuilder==null){
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
                    postEmpty();
                    showStartFail();
                    break;

                case BluetoothService.MESSAGE_NO_JIESHUCALLBACK: //结束费率

                    break;

                case BluetoothService.MESSAGE_STATE_NOTHING: //状态通知
                   // Log.d("Bath2Activity", "mbtService.getState():" + mbtService.getState());

                    if (mbtService != null) {
                        if (mbtService.getState() == BluetoothService.STATE_CONNECTED) {
                          //  Log.d("Bath2Activity", "stop");
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
                            connectFailed ++;
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
                  //  Log.d("Bath2Activity", "bState:" + bState);
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
                   // Log.d("Bath2Activity","sb:"+ sb.toString());
                    break;
            }
        }
    };

    /**
     * 中断用水(自己的)
     */
    private void showStopDailog() {
        if (dialogBuilder==null){
            return;
        }
        hideProgressbar();
        dialogBuilder.withTitle(getString(R.string.tips))
                .withMessage(getString(R.string.stop_tips))
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
                        //判断设备是否是自己在使用
                        //结束使用
                        showProgressbar();
                        CMDUtils.jieshufeilv(mbtService,true);
                    }
                }).show();

    }
    /**
     * 中断用水(打断别人的)
     */
    private int stopDecive =0;
    private void showInterruptDeviceDailog() {
        hideProgressbar();
        if (dialogBuilder==null){
            return;
        }
        dialogBuilder.withTitle(getString(R.string.tips))
                .withMessage(getString(R.string.interrupt_tips))
                .withEffect(Effectstype.Fadein).isCancelable(false)
                .withButton1Text(getString(R.string.cancel))
                .setButton1Click(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        dialogBuilder.dismiss();
                        if (mbtService.getState() == BluetoothService.STATE_CONNECTED) {
                            mbtService.stop();
                        }
                        bState=38;
                        updateUI(bState);
                       // finish();
                    }
                }).withButton2Text(getString(R.string.sure))
                .setButton2Click(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CMDUtils.jieshufeilv(mbtService,true);
                     //   stopDecive=1;
                        dialogBuilder.dismiss();

                    }
                }).show();
    }

    private void showMenu() {
        hideProgressbar();
        if (dialogBuilder==null){
            return;
        }
        dialogBuilder.withTitle(getString(R.string.tips))
                .withMessage(getResources().getString(R.string.consume_menu))
                .withEffect(Effectstype.Fadein).isCancelable(false)
              //  .withButton1Text(getString(R.string.cancel))
                .withButton2Text(getString(R.string.sure))
              /*  .setButton1Click(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        isUser=0;
                        dialogBuilder.dismiss();
                    }
                })*/
                .setButton2Click(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogBuilder.dismiss();
                        isUser=0;
                        if (mbtService.getState()==BluetoothService.STATE_CONNECTED){
                            mbtService.stop();

                        }


                    }
                }).show();
    }

    private void showNoInterruptDeviceDailog() {
        hideProgressbar();
        if (dialogBuilder==null){
            return;
        }
        dialogBuilder.withTitle(getString(R.string.tips))
                .withMessage(getString(R.string.no_interrupt_tips))
                .withEffect(Effectstype.Fadein).isCancelable(false)
                .withButton1Text(getString(R.string.cancel))
                .setButton1Click(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        dialogBuilder.dismiss();

                        // finish();
                    }
                }).withButton2Text(getString(R.string.sure))
                .setButton2Click(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //   stopDecive=1;
                        dialogBuilder.dismiss();
                        finish();

                    }
                }).show();
    }

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

    private void submitFailed(){
        hideProgressbar();
        dialogBuilder.withTitle(getString(R.string.tips))
                .withMessage("上传消费数据失败,请重试")
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
                        CMDUtils.chaxueshebei(mbtService,true);
                    }
                }).show();
    }

    /**
     * 余额不足
     */
    private void showYueBuzu() {
        hideProgressbar();
        if (dialogBuilder==null){
            return;
        }
        if (userInfo.GroupID==1){
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
        }else if (userInfo.GroupID==2){
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
     * 下发费率失败
     * @param message
     */
    private void showErrorDownRate(String message) {
        hideProgressbar();
        if (dialogBuilder==null){
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
     * 下发费率
     * @param mproductid
     * @param mdeviceid
     * @param macBuffer
     * @param tac_timeBuffer
     */
    private int perMonneys;
    private  DownRateInfo downRateInfo;

    private void xaifafeilv(final int mproductid, final int mdeviceid, final byte[] macBuffer, final byte[] tac_timeBuffer) {



        if (!Common.isNetWorkConnected(Bath2Activity.this)){
            Common.showNoNetworkDailog(dialogBuilder,Bath2Activity.this);
            return;
        }
        //不可点击
        deciveIcon.setEnabled(false);
        OkHttpClient client =new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        RequestBody requestBody=new FormBody.Builder()
                .add("PrjID",""+mproductid)
                .add("AccID",""+ userInfo.AccID )
                .add("GroupID",""+userInfo.GroupID)
                .add("DevID",mdeviceid+"")
                .add("consumeMothe","0")
                .add("xfMothe","0")
                .add("loginCode",userInfo.TelPhone+","+userInfo.loginCode)
                .add("phoneSystem", "Android")
                .add("version", MyApp.versionCode)
                .build();
        Request request =new Request.Builder()
                .url(Common.BASE_URL + "downRate")
                .post(requestBody)
                .build();
        //Log.d("-----", "下发userInfo.AccID:" + userInfo.AccID);
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
               hideProgressbar();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result =response.body().string();
                final PublicGetData publicGetData = new Gson().fromJson(result, PublicGetData.class);
                if (publicGetData==null){
                    return;
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (publicGetData.error_code.equals("0")) {

                             downRateInfo = new Gson().fromJson(publicGetData.data, DownRateInfo.class);
                            if (downRateInfo != null) {

                                if (userInfo.GroupID==2){  //普通用户
                                    //下发费率
                                    try {
                                        CMDUtils.xiafafeilv(mbtService, true,
                                                downRateInfo, mproductid, userInfo.AccID, 2, macBuffer, tac_timeBuffer);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }else {  //操作员
                                    //下发费率
                                    try {
                                        CMDUtils.xiafafeilv(mbtService, true,
                                                downRateInfo, mproductid, userInfo.AccID, 1, macBuffer, tac_timeBuffer);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                                //更新预扣金额
                                perMonneys =downRateInfo.PerMoney;
                                decivePerMonney.setText(Common.getShowMonty(perMonneys, getString(R.string.yuan1))); //预扣费
                                //丢包的一个处理
                                Message message = new Message();
                                message.what = BluetoothService.MESSAGE_NO_XIAFACALLBACK;
                                mHandler.sendMessageDelayed(message, 4000);
                            } else {
                                hideProgressbar();
                                deciveIcon.setEnabled(true);
                            }
                        } else if (publicGetData.error_code.equals("7")) {
                            hideProgressbar();
                            Common.logout2(Bath2Activity.this, sp, dialogBuilder,publicGetData.message);
                        } else if (publicGetData.error_code.equals("2")){
                            showErrorDeposit(publicGetData.message);
                            //可点击
                            deciveIcon.setEnabled(true);
                            hideProgressbar();
                        } else {
                            hideProgressbar();
                            deciveIcon.setEnabled(true);
                            if (publicGetData.message.contains("余额不足")) {
                                showYueBuzu();
                                updateUserInfo(userInfo);
                            } else {
                                showErrorDownRate(publicGetData.message);
                            }
                        }
                    }
                });

            }
        });
    }

    /**
     * 需要交纳押金的设备未交纳
     * @param message
     */
    private void showErrorDeposit(String message) {

        hideProgressbar();
        if (dialogBuilder==null){
            return;
        }
        dialogBuilder.withTitle(getString(R.string.tips)).withMessage("设备需要交押金当前用户没交押金，确定交纳押金?")
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
                        Intent intent =new Intent(Bath2Activity.this, MyDespoitActivity.class);
                        intent.putExtra("DevType",mDeviceInfo.DevTypeID+"");
                        startActivity(intent);
                    }
                }).show();
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
    private  int mGruo; //用户类型
    private  int mAcc; //用户账号
    private PublicPostConsumeData publicPostConsumeData;
    private void postXiaoFei(final boolean ismine, final int prjid, final int accoutid, final int deviceid, final int groupid,
                             int consumeMoney, int preMoney, final String timeid, final int usercount) {


        if (!Common.isNetWorkConnected(Bath2Activity.this)){
            Common.showNoNetworkDailog(dialogBuilder,Bath2Activity.this);
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
                .add("GroupID",""+ groupid )
                .add("UpMoney",""+consumeMoney)
                .add("PerMoney",""+preMoney)
                .add("ConsumeDT","20" + timeid)
                .add("devType",mDeviceInfo.Dsbtypeid+"")
                .add("loginCode",userInfo.TelPhone + "," + userInfo.loginCode)
                .add("phoneSystem", "Android")
                .add("version",MyApp.versionCode)
                .build();
        //Log.d("------", "上传accoutid:" + accoutid);
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
              //  Log.d("-------", "上传消费记录：="+result);
                publicPostConsumeData = new Gson().fromJson(result, PublicPostConsumeData.class);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (publicPostConsumeData.error_code.equals("0")) {
                            try {
                                CMDUtils.fanhuicunchu(mbtService, true, timeid, prjid, deviceid, accoutid, usercount);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else if (publicPostConsumeData.error_code.equals("7")) {

                            Common.logout2(Bath2Activity.this, sp, dialogBuilder,publicPostConsumeData.message);
                        } else {
                            submitNum++;
                            mGruo =groupid;
                            mAcc =accoutid;
                            if (submitNum>=3){
                                //postEmpty();//上传空数据
                                //直接丢弃。上传三次失败了还上传个屁的空数据，又不是钱导致上传失败
                                try {
                                    CMDUtils.fanhuicunchu(mbtService,true,timeid,prjid,deviceid,accoutid,usercount);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }else {
                                CMDUtils.caijishuju(mbtService,true);
                            }
                        }
                    }
                });

            }
        });
    }
    private void postEmpty(){


        if (downRateInfo != null) { //下发费率成功后设备使用失败
            PostConsumeData postConsumeData = new PostConsumeData();
            postConsumeData.downRateInfo = downRateInfo;
            postConsumeData.productid = wproductid;
            postConsumeData.devid = mDeviceInfo.DevID;
            postEmptyDownRate(postConsumeData);
        }else { //采集数据后上传三次

        }


    }
    /**
     * 连接失败
     */
    private void showStartFail() {
        hideProgressbar();
        isUser=0;
        if (dialogBuilder ==null){
            return;
        }
        dialogBuilder.withTitle(getString(R.string.tips))
                .withMessage("设备出错.再试试吧!")
                .withEffect(Effectstype.Fadein).isCancelable(false)
                .withButton2Text(getString(R.string.i_known))
                .setButton2Click(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogBuilder.dismiss();
                        if (mbtService.getState()==BluetoothService.STATE_CONNECTED){
                            mbtService.stop();

                        }
                    }
                }).show();
    }
    /**
     * 上传消费数据 空数据
     * @param postConsumeData
     */
    private void postEmptyDownRate(PostConsumeData postConsumeData) {
        int gro =0;
        int acc =0;
        if (mGruo ==0){ //下发费率后设备使用错误
            gro =userInfo.GroupID;
            acc =userInfo.AccID;
        }else { //采集数据三次不成功后
            gro =mGruo;
            acc =mAcc;
        }
      OkHttpClient client =new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        RequestBody requestBody=new FormBody.Builder()
                .add("PrjID",""+ postConsumeData.productid )
                .add("AccID",""+acc )
                .add("DevID",""+ postConsumeData.devid )
                .add("GroupID",""+gro)
                .add("UpMoney",""+0)
                .add("PerMoney",""+ postConsumeData.downRateInfo.PerMoney)
                .add("ConsumeDT",downRateInfo.ConsumeDT)
                .add("devType",mDeviceInfo.Dsbtypeid+"")
                .add("loginCode",userInfo.TelPhone + "," + userInfo.loginCode)
                .add("phoneSystem", "Android")
                .add("version",MyApp.versionCode)
                .build();
       // Log.d("Bath2Activity", postConsumeData.downRateInfo.ConsumeDT);
      //  Log.d("Bath2Activity", downRateInfo.ConsumeDT);
      //  Log.d("-------", "上传acc:" + acc);
        Request request =new Request.Builder()
                .url(Common.BASE_URL + "savexf")
                .post(requestBody)
                .build();
       client.newCall(request).enqueue(new Callback() {
           @Override
           public void onFailure(Call call, IOException e) {
             //  Log.d("----", "e:" + e);
           }

           @Override
           public void onResponse(Call call, Response response) throws IOException {
                String str =response.body().string();
            //   Log.d("-------", "上传空数据：="+str);
           }
       });

/*        AjaxParams ajaxParams = new AjaxParams();
        ajaxParams.put("PrjID", ""+postConsumeData.productid);
        ajaxParams.put("AccID", ""+acc);
        ajaxParams.put("DevID", ""+postConsumeData.devid);
        ajaxParams.put("GroupID", ""+gro);
        ajaxParams.put("UpMoney", "0");
        ajaxParams.put("PerMoney", ""+postConsumeData.downRateInfo.PerMoney);
        ajaxParams.put("ConsumeDT", downRateInfo.ConsumeDT);
        ajaxParams.put("devType", ""+mDeviceInfo.Dsbtypeid);
        ajaxParams.put("loginCode", userInfo.TelPhone + "," + userInfo.loginCode);
        ajaxParams.put("phoneSystem", "Android");
        ajaxParams.put("version", MyApp.versionCode);
        Log.d("----", "上传空ajaxParams:" + ajaxParams);
        new FinalHttp().get(Common.BASE_URL + "savexf", new AjaxCallBack<Object>() {
            @Override
            public void onSuccess(Object o) {
                super.onSuccess(o);
                Log.d("---", o.toString());
            }

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
            }
        });*/
    }

    /**
     * 更新用户信息
     * @param mInfo
     */
    private void updateUserInfo(final UserInfo mInfo) {

        if (Common.isNetWorkConnected(Bath2Activity.this)) {

           /* if (TextUtils.isEmpty(mInfo.TelPhone + "")) {
                Common.showToast(Bath2Activity.this, R.string.phonenum_null, Gravity.CENTER);
                return;
            }
            if (!Common.isPhoneNum(mInfo.TelPhone + "")) {
                Common.showToast(Bath2Activity.this, R.string.phonenum_not_irregular, Gravity.CENTER);
                return;
            }*/

            AjaxParams ajaxParams = new AjaxParams();
            ajaxParams.put("TelPhone", mInfo.TelPhone + "");
            ajaxParams.put("PrjID", mInfo.PrjID + "");
            ajaxParams.put("WXID", "0");
            ajaxParams.put("loginCode", mInfo.TelPhone + "," + mInfo.loginCode);
            ajaxParams.put("phoneSystem", "Android");
            ajaxParams.put("version", MyApp.versionCode);

            if (userInfo.GroupID==1){
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
                          //  Log.d("Bath2Activity", "更新个人信息：="+result);
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
                          //  Log.d("Bath2Activity", strMsg);
                        }
                    });

        }

    }


    /**
     * 根据mAC地址查最近消费
     */
    private void queryMdelByMac(){

        AjaxParams params =new AjaxParams();
        params.put("DevMac",mDeviceInfo.devMac);
        params.put("PrjID",""+mDeviceInfo.PrjID);
        params.put("loginCode",userInfo.TelPhone+","+userInfo.loginCode);
        params.put("phoneSystem", "Android");
        params.put("version", MyApp.versionCode);
      //  Log.d("WashingActivity", "params:" + params);
        new FinalHttp().get(Common.BASE_URL + "getOrderByMac", params, new AjaxCallBack<Object>() {
            @Override
            public void onSuccess(Object result) {
                super.onSuccess(result);
             //   Log.d("----------", "result:" + result);

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

    private int maccid; //采集即刻数据的账号
    @Override
    public void caijishujuOnback(boolean b, String timeid, int mproductid, int mdeviceid,
                                 int maccountid,String accounttypeString, int usercount, String ykmoneyString,
                                 String consumeMoneString, String rateString, String macString) {

        if (b){
          //  Log.d("Bath2Activity", "消费金额："+consumeMoneString);
                maccid =maccountid;
            if (maccountid ==userInfo.AccID){
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

        if (b){
          //  Log.d("Bath2Activity", "结束费率成功");
            CMDUtils.caijishuju(mbtService,true);
        }else {
           // Log.d("Bath2Activity", "结束费率失败");
            CMDUtils.chaxueshebei(mbtService,true);
        }

    }

    @Override
    public void xiafafeilvOnback(boolean b) {
        hideProgressbar();
        deciveIcon.setEnabled(true);
        mHandler.removeMessages(BluetoothService.MESSAGE_NO_XIAFACALLBACK);
        if (b){
            bState =35;
            updateUI(bState);
            dAccountId =userInfo.AccID;
           //扣钱
            updateUserInfo(userInfo);
            /*int monneyTotail =userInfo.AccMoney + userInfo.GivenAccMoney;
            if (monneyTotail >perMonneys){
                remainMonney.setText(Common.getShowMonty(monneyTotail-perMonneys, getString(R.string.yuan1)));//我的余额

            }*/
        }else {
          //  Log.d("Bath2Activity", "下发费率失败");
            postEmpty();
            showStartFail();
        }
    }

    @Override
    public void fanhuicunchuOnback(boolean b) {

        hideProgressbar();
        if (b){
           // Log.d("Bath2Activity", "清除记录");
            if (maccid ==userInfo.AccID) {
                if (userInfo.GroupID==2){ //普通用户显示账单
                    if (publicPostConsumeData.PerMoney!=0){//如果上传消费数据失败，即账单为0不显示账单
                        Intent intent = new Intent();
                        intent.setClass(Bath2Activity.this, ConsumeActivity.class);
                        intent.putExtra("consume_type",mDeviceInfo.Dsbtypeid);
                        intent.putExtra("consumedata", publicPostConsumeData);
                        startActivity(intent);
                        finish();
                    }
                }else {
                    updateUserInfo(userInfo); //操作员更新钱
                }
            }
            //初始化
            bState =39;
            updateUI(bState);
        }else {
            //Log.d("Bath2Activity", "清除记录失败");
        }
    }

    private int mType;
    private int littleType;
    private  int dAccountId =0;  //设备查询到正在进行中的订单的账号
    private int wproductid;
    private byte[] wmacBuffer;
    private byte[] wtac_timeBuffer;
    @Override
    public void chaxueNewshebeiOnback(boolean b, int charge, int mdeviceid, int mproductid, int maccountid,
                                      byte[] macBuffer, byte[] tac_timeBuffer, int macType,int lType, int constype, int macTime) {


       // Log.d("Bath2Activity", "mdeviceid:" + mdeviceid);
        //Log.d("Bath2Activity", "mproductid:" + mproductid);
        //Log.d("-------", "maccountid:" + maccountid);
        //Log.d("Bath2Activity", "macType:" + macType);//012345
       // Log.d("Bath2Activity", "lType:" + lType);
       // Log.d("Bath2Activity", "charge:" + charge);
       // Log.d("Bath2Activity", "constype:" + constype);
        if (!b){
            Toast.makeText(this, "查询设备失败，请稍后重试", Toast.LENGTH_SHORT).show();
        }
        resetHandler();
        mType =macType;
        littleType =lType;
        dAccountId =maccountid;
        wproductid =mproductid;
        wmacBuffer =macBuffer;
        wtac_timeBuffer =tac_timeBuffer;
        String type=macType+""+lType;
       //校验

        //使用
        switch (macType){
            case 0: //水表
                showProError(mdeviceid);
               // Log.d("Bath2Activity", "是水表");
                switch (charge){
                    case 0:
                        bState =40;
                        updateUI(bState);
                        //Log.d("Bath2Activity", "isUser:" + isUser);
                        if (isUser==1){

                            xaifafeilv(wproductid,mDeviceInfo.DevID,wmacBuffer,wtac_timeBuffer);

                        }
                        break;
                    case 1://订单进行中
                        if (maccountid !=userInfo.AccID){
                            if (isUser==1){
                                showMenu();
                            }else {
                                showInterruptDeviceDailog();
                            }

                        }else {
                            bState =35;
                            updateUI(bState);
                            queryMdelByMac();
                        }
                        break;
                    case 2://刷卡消费中，不能结束用水，提示
                      showNoInterruptDeviceDailog();
                        break;
                    case 3:
                        //数据未采集
                        bState =36;
                        updateUI(bState);
                        CMDUtils.caijishuju(mbtService,true);
                        break;
                }
                break;
            case 1://饮水机
                showProError(mdeviceid);
                //Log.d("Bath2Activity", "是饮水机");
                switch (charge){
                    case 0:
                        bState =40;
                        updateUI(bState);
                        if (isUser==1){
                            xaifafeilv(wproductid,mDeviceInfo.DevID,wmacBuffer,wtac_timeBuffer);

                        }
                        break;
                    case 1://订单进行中
                        if (maccountid !=userInfo.AccID){

                            if (isUser==1){
                                showMenu();
                            }else {
                                showInterruptDeviceDailog();
                            }
                        }else {
                            bState =35;
                            updateUI(bState);
                            queryMdelByMac();
                        }
                        break;
                    case 2:
                        showNoInterruptDeviceDailog();
                        break;
                    case 3:
                        //数据未采集
                        bState =36;
                        updateUI(bState);
                        showProgressbar();
                        CMDUtils.caijishuju(mbtService,true);
                        break;
                }
                break;
            case 2://洗衣机

                break;
            case 3: //吹风机
                showProError2(mdeviceid);
                switch (charge){
                    case 0:
                        bState =40;
                        updateUI(bState);
                        if (isUser==1){
                            xaifafeilv(wproductid,mDeviceInfo.DevID,wmacBuffer,wtac_timeBuffer);

                        }
                        break;
                    case 1://订单进行中
                        if (maccountid !=userInfo.AccID){
                            if (isUser==1){
                                showMenu();
                            }else {
                                showInterruptDeviceDailog();
                            }
                        }else {
                            bState =35;
                            updateUI(bState);
                            queryMdelByMac();
                        }
                        break;
                    case 2:
                        showNoInterruptDeviceDailog();
                        break;
                    case 3:
                        //数据未采集
                        bState =36;
                        updateUI(bState);
                        CMDUtils.caijishuju(mbtService,true);
                        break;
                }
                break;
            case 4://冲电器
                showProError2(mdeviceid);
                switch (charge){
                    case 0:
                        bState =40;
                        updateUI(bState);
                        if (isUser==1){
                            xaifafeilv(wproductid,mDeviceInfo.DevID,wmacBuffer,wtac_timeBuffer);

                        }
                        break;
                    case 1://订单进行中
                        if (maccountid !=userInfo.AccID){
                            if (isUser==1){
                                showMenu();
                            }else {
                                showInterruptDeviceDailog();
                            }
                        }else {
                            bState =35;
                            updateUI(bState);
                            queryMdelByMac();
                        }
                        break;
                    case 2:
                        showNoInterruptDeviceDailog();
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
        if (b){
            //Log.d("Bath2Activity", "开始交易");
        }else {
           // Log.d("Bath2Activity", "交易开始失败");
        }
    }

    @Override
    public void stopDeal(boolean b) {
        if (b){
          //  Log.d("Bath2Activity", "结束交易");
            CMDUtils.caijishuju(mbtService,true);
        }
    }

    private void showProError(int mdeviceid){
        if (userInfo.GroupID==2){ //用户
          /*  if (mDeviceInfo.DevID !=mdeviceid){ //设备ID错误
                showStartFail4();
                return;
            }*/
            if (wproductid!=0){
                if (userInfo.PrjID !=wproductid){ //项目编号
                    showStartFail4();
                    return;
                }
            }else { //设备无项目编号的情况
                showStartFail4();
                return;
            }
            if (littleType>0){
                if (littleType !=mDeviceInfo.DevTypeID){ //设备小类
                    showStartFail4();
                    return;
                }

            }else { //老设备无设备小类，不做处理

            }

        }else if (userInfo.GroupID==1){ //操作员
            /*if (mDeviceInfo.DevID !=mdeviceid){ //设备ID错误
                showStartFail3();
                return;
            }*/
            if (wproductid==0){ //设备无项目编号的情况
                showStartFail3();
                return;
            }
            if (littleType>0){
                if (littleType !=mDeviceInfo.DevTypeID){
                    showStartFail3();
                    return;
                }

            }else { //老设备无设备小类，不做处理

            }
        }else { //未知错误
            toast("用户类型错误");
            return;
        }


    }
    private void showProError2(int mdeviceid){
        if (userInfo.GroupID==2){ //用户
            if (mDeviceInfo.DevID !=mdeviceid){ //设备ID错误
                showStartFail4();
                return;
            }
            if (wproductid!=0){
                if (userInfo.PrjID !=wproductid){ //项目编号
                    showStartFail4();
                    return;
                }
            }else { //设备无项目编号的情况
                showStartFail4();
                return;
            }
            if (littleType>0){
                if (littleType !=mDeviceInfo.DevTypeID){ //设备小类
                    showStartFail4();
                    return;
                }

            }else { //老设备无设备小类，不做处理

            }

        }else if (userInfo.GroupID==1){ //操作员
            if (mDeviceInfo.DevID !=mdeviceid){ //设备ID错误
                showStartFail3();
                return;
            }
            if (wproductid==0){ //设备无项目编号的情况
                showStartFail3();
                return;
            }
            if (littleType>0){
                if (littleType !=mDeviceInfo.DevTypeID){
                    showStartFail3();
                    return;
                }

            }else { //老设备无设备小类，不做处理

            }
        }else { //未知错误
            toast("用户类型错误");
            return;
        }


    }


    /**
     * 连接失败
     */
    private void showStartFail3() {
        hideProgressbar();
        if (dialogBuilder ==null){
            return;
        }
        dialogBuilder.withTitle(getString(R.string.tips))
                .withMessage("该设备信息已修改，请重新登记")
                .withEffect(Effectstype.Fadein).isCancelable(false)
                .withButton2Text(getString(R.string.i_known))
                .setButton2Click(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogBuilder.dismiss();
                        if (mbtService.getState()==BluetoothService.STATE_CONNECTED){
                            mbtService.stop();

                        }
                    }
                }).show();
    }
    /**
     * 连接失败
     */
    private void showStartFail2() {
        hideProgressbar();
        if (dialogBuilder ==null){
            return;
        }
        dialogBuilder.withTitle(getString(R.string.tips))
                .withMessage("该设备信息已修改，请重新绑定")
                .withEffect(Effectstype.Fadein).isCancelable(false)
                .withButton2Text(getString(R.string.i_known))
                .setButton2Click(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogBuilder.dismiss();
                        if (mbtService.getState()==BluetoothService.STATE_CONNECTED){
                            mbtService.stop();

                        }
                    }
                }).show();
    }
    /**
     * 连接失败
     */
    private void showStartFail4() {
        hideProgressbar();
        if (dialogBuilder ==null){
            return;
        }
        dialogBuilder.withTitle(getString(R.string.tips))
                .withMessage("该设备信息已修改，请联系管理员")
                .withEffect(Effectstype.Fadein).isCancelable(false)
                .withButton2Text(getString(R.string.i_known))
                .setButton2Click(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogBuilder.dismiss();
                        if (mbtService.getState()==BluetoothService.STATE_CONNECTED){
                            mbtService.stop();

                        }
                    }
                }).show();
    }
    /**
     * 连接失败
     */
    private void showStartFail5() {
        hideProgressbar();
        if (dialogBuilder ==null){
            return;
        }
        dialogBuilder.withTitle(getString(R.string.tips))
                .withMessage("该设备信息已修改，请联系管理员")
                .withEffect(Effectstype.Fadein).isCancelable(false)
                .withButton2Text(getString(R.string.i_known))
                .setButton2Click(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogBuilder.dismiss();
                        if (mbtService.getState()==BluetoothService.STATE_CONNECTED){
                            mbtService.stop();

                        }
                    }
                }).show();
    }
    /**
     * 连接失败
     */
    private void showStartFail6() {
        hideProgressbar();
        if (dialogBuilder ==null){
            return;
        }
        dialogBuilder.withTitle(getString(R.string.tips))
                .withMessage("该设备编号不一致，请重新登记")
                .withEffect(Effectstype.Fadein).isCancelable(false)
                .withButton2Text(getString(R.string.i_known))
                .setButton2Click(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogBuilder.dismiss();
                        if (mbtService.getState()==BluetoothService.STATE_CONNECTED){
                            mbtService.stop();

                        }
                    }
                }).show();
    }
}
