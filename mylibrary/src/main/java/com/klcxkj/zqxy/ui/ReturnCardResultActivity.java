package com.klcxkj.zqxy.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.klcxkj.mylibrary.R;
import com.klcxkj.zqxy.databean.CardPackageAll;
import com.klcxkj.zqxy.databean.CardpakageMine;

import org.greenrobot.eventbus.EventBus;

/**
 * autor:OFFICE-ADMIN
 * time:2017/11/17
 * email:yinjuan@klcxkj.com
 * description:申请退卡结果
 */
public class ReturnCardResultActivity extends BaseActivity {

    private LinearLayout layout_ok,layout_error;
    private TextView ok1,ok2;
    private TextView error1,error2;
    private TextView title;
    private Button btn;
    //月卡信息
    private TextView card_name,card_address,card_monney,card_count_time;

    private LinearLayout linerLayout_hint; //洗衣月卡的详细
    public static final String CARD_BUY="card_buy"; //月卡购买成功
    public static final String CARD_RETURN="card_return"; //月卡退还成功
    public static final String CARD_RETURN_FAILE="card_return_error"; //月卡退还失败
    public static final String DESPOIT_RECHANGE="despoit_rechange";//押金充值
    public static final String DESPOIT_RETURN="despoit_return";//押金退还
    public static final String DESPOIT_RETURN_FAILED="despoit_return_error";//押金退还失败

    private CardPackageAll cardInfo;
    private CardpakageMine cardInfoMine;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_return_card_result);
        initview();
        initdata();

    }

    private String card_do;
    private String monney;
    private void initdata() {
        Intent intent =getIntent();
        if (intent.getStringExtra("card_do")!=null){
            card_do =intent.getStringExtra("card_do");
        }
        if (intent.getStringExtra("monney")!=null){
             monney =intent.getStringExtra("monney");

        }
        if (card_do.equals(CARD_BUY)){  //月卡购买成功
            showMenu("购买成功");
            layout_ok.setVisibility(View.VISIBLE);
            layout_error.setVisibility(View.GONE);
            ok2.setText("提醒：洗衣机月卡仅限房间内的洗衣机上使用");
            title.setText("购买成功");
            ok1.setText("");
          cardInfo= (CardPackageAll) intent.getExtras().getSerializable("CardInfo");
        }else if (card_do.equals(CARD_RETURN)){ //月卡退还
            showMenu("退卡申请");
            layout_ok.setVisibility(View.VISIBLE);
            layout_error.setVisibility(View.GONE);
            cardInfoMine= (CardpakageMine) intent.getExtras().getSerializable("CardInfoMine");
        }else if (card_do.equals(DESPOIT_RECHANGE)){ //押金冲值成功
            showMenu("押金充值");
            title.setText("充值成功");
            ok1.setText("¥"+monney);
            ok2.setText("缴纳押金成功，即可使用设备");
            layout_ok.setVisibility(View.VISIBLE);
            layout_error.setVisibility(View.GONE);
            linerLayout_hint.setVisibility(View.GONE);
        }else if (card_do.equals(DESPOIT_RETURN)){ //押金退还
            showMenu("退押金申请");
            title.setText("申请成功");
            layout_ok.setVisibility(View.VISIBLE);
            layout_error.setVisibility(View.GONE);
            linerLayout_hint.setVisibility(View.GONE);
        }else if (card_do.equals(CARD_RETURN_FAILE)){
            showMenu("退卡申请");
            layout_ok.setVisibility(View.GONE);
            layout_error.setVisibility(View.VISIBLE);
            cardInfoMine= (CardpakageMine) intent.getExtras().getSerializable("CardInfoMine");
        }else if (card_do.equals(DESPOIT_RETURN_FAILED)){
            showMenu("退押金申请");
            layout_ok.setVisibility(View.GONE);
            layout_error.setVisibility(View.VISIBLE);
            linerLayout_hint.setVisibility(View.GONE);
        }

        //显示月卡信息
        if (cardInfo !=null){
            card_name.setText(cardInfo.getTypename());
            card_address.setText("仅限房间内使用");
            String str =cardInfo.getTypevalue();

            float a =Float.parseFloat(str.substring(0,str.length()-1));
            float b =a;
            card_monney.setText(b+"元");
            card_count_time.setText(cardInfo.getDescname());
        }
        if (cardInfoMine !=null){
            card_name.setText(cardInfoMine.getYKname());
            card_address.setText("仅限房间内使用");
            float a =Float.parseFloat(cardInfoMine.getYKmoney());
            float b =a;
            card_monney.setText(b+"元");
            card_count_time.setText(cardInfoMine.getMonthHadTimes()+"/"+cardInfoMine.getMonthTimes());
        }
    }

    private void initview() {
        showMenu("退卡申请");
        layout_error = (LinearLayout) findViewById(R.id.return_card_error);
        layout_ok = (LinearLayout) findViewById(R.id.return_card_ok);
        ok1 = (TextView) findViewById(R.id.card_result_ok_1);
        ok2 = (TextView) findViewById(R.id.card_result_ok_2);
        error1 = (TextView) findViewById(R.id.card_result_error_1);
        error2 = (TextView) findViewById(R.id.card_result_error_2);
        title = (TextView) findViewById(R.id.card_result_ok_title);
         linerLayout_hint = (LinearLayout) findViewById(R.id.linerLayout_hint);
        btn = (Button) findViewById(R.id.deposit_pull_cancle);
        //月卡信息
        card_name = (TextView) findViewById(R.id.card_type);
        card_address = (TextView) findViewById(R.id.card_address);
        card_monney = (TextView) findViewById(R.id.card_monney);
        card_count_time = (TextView) findViewById(R.id.card_count_time);

        //按钮点击
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (card_do.equals(DESPOIT_RECHANGE)||card_do.equals(DESPOIT_RETURN)){
                    EventBus.getDefault().postSticky("MyDespoitActivity_sucess");
                }else if (card_do.equals(CARD_RETURN) || card_do.equals(CARD_BUY)){
                    EventBus.getDefault().postSticky("cardPackage_scuess");
                }
                finish();
            }
        });
    }
}
