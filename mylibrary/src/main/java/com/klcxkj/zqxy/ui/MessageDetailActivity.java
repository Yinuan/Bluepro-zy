package com.klcxkj.zqxy.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.klcxkj.mylibrary.R;
import com.klcxkj.zqxy.databean.MsgPush;
import com.zzhoujay.richtext.CacheType;
import com.zzhoujay.richtext.RichText;

public class MessageDetailActivity extends BaseActivity {

    private TextView html_txt;
    private MsgPush msgPush;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_detail);
      //  StatusBarUtil.setColor(this,getResources().getColor(R.color.base_color),0);
        RichText.initCacheDir(this);
        RichText.debugMode = true;
        initdata();
        initview();
    }

    private void initdata() {
        Intent intent =getIntent();
        msgPush = (MsgPush) intent.getExtras().getSerializable("MsgPush");
    }

    private void initview() {
        showMenu("消息详情");
        html_txt =findViewById(R.id.html_text);
        if (msgPush !=null){
            if (msgPush.getPushcontent() !=null){
                RichText.from(msgPush.getPushcontent())
                        .showBorder(false)
                        .cache(CacheType.all)
                        .into(html_txt);
            }
        }




    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RichText.recycle();
    }
}
