package com.klcxkj.zqxy.ui;

import java.util.ArrayList;

import com.klcxkj.mylibrary.R;
import com.klcxkj.zqxy.adapter.MessageAdapter;
import com.klcxkj.zqxy.databean.MessageData;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.TextView;

public class MessageActivity extends Activity{

	private ListView message_list;
	private TextView back_img;
	
	private ArrayList<MessageData> messageDatas = new ArrayList<MessageData>();
	private MessageAdapter messageAdapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_message);
		initView();
	}
	private void initView() {
		// TODO Auto-generated method stub
		back_img = (TextView)findViewById(R.id.back_img);
		message_list = (ListView)findViewById(R.id.message_list);
		
		back_img.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		getMessagaData();
		
	}
	private void getMessagaData() {
		messageDatas.clear();
		for (int i = 0; i < 10; i++) {
			MessageData messageData = new MessageData();
			messageData.message_content ="这是第"+i+"条数据";
			
			messageDatas.add(messageData);
		}
		
		if (messageAdapter == null) {
			messageAdapter = new MessageAdapter(MessageActivity.this, messageDatas);
			message_list.setAdapter(messageAdapter);
		}else {
			messageAdapter.changeData(messageDatas);
		}
		
		
	}

}
