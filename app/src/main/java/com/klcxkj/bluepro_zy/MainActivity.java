package com.klcxkj.bluepro_zy;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.klcxkj.zqxy.common.Common;
import com.klcxkj.zqxy.ui.MainAdminActivity;
import com.klcxkj.zqxy.ui.MainUserActivity;
import com.klcxkj.zqxy.utils.AppPreference;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.button4)
    Button button4;
    @BindView(R.id.zy_ip)
    EditText zyIp;
    @BindView(R.id.zy_user)
    EditText zyUser;
    @BindView(R.id.zy_pass)
    EditText zyPass;
    @BindView(R.id.button5)
    Button button5;
    @BindView(R.id.button6)
    Button button6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        initData();

    }

    private void initData() {
        if (AppPreference.getInstance().getZyIp() != null) {
            zyIp.setText(AppPreference.getInstance().getZyIp());
        }
    }


    @OnClick({R.id.button5, R.id.button6})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.button5:
                //管理员调用方式
                if (zyIp.getText().toString() != null && zyIp.getText().length() > 0) {
                    AppPreference.getInstance().saveZyIp(zyIp.getText().toString());

                    Intent intent = new Intent();
                    intent.setClass(MainActivity.this, MainAdminActivity.class);
                    intent.putExtra("tellPhone", "18565651403"); //管理员账户
                    intent.putExtra("PrjID", "0");
                    intent.putExtra("prijName", "正元智慧");
                    intent.putExtra("app_url", "http://60.191.37.212:36080/appI/api/");//zyIp.getText().toString()
                    startActivity(intent);
                }
                break;
            case R.id.button6:
                //用户调用方式
                if (zyIp.getText().toString() != null && zyIp.getText().length() > 0) {
                    AppPreference.getInstance().saveZyIp(zyIp.getText().toString());

                    Intent intent = new Intent();
                    intent.setClass(MainActivity.this, MainUserActivity.class);
                    intent.putExtra("tellPhone", "18565651403"); //用户账号
                    intent.putExtra("PrjID", "0");
                    intent.putExtra("prijName", "正元智慧");
                    intent.putExtra("accNum","18302"); //一卡通账号
                    intent.putExtra("app_url", "http://60.191.37.212:36080/appI/api/");//zyIp.getText().toString()
                    startActivity(intent);
                }
                break;
        }
    }
}
