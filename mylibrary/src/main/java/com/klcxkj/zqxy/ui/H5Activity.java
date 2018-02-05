package com.klcxkj.zqxy.ui;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.klcxkj.mylibrary.R;
import com.klcxkj.zqxy.common.Common;

public class H5Activity extends BaseActivity {

	public static final String YSLC = "yslc";//用水说明
	public static final String BQSM = "bqsm";//版权申明
	public static final String LBSSB = "lbssb";//连不上设备
	public static final String MZSM = "mzsm";//免责声明
	public static final String SMSYK = "smsykf";//什么是预扣
	public static final String SBDSB = "sbdsb";//搜不到设备
	public static final String TKSM = "tksm";//退款说明
	public static final String WLJKSQ = "wljksq";//未连接开水器

	public static final String MESS="messageDetail";//消息详情

	public static final String QUERYSPREAD="querySpread";//推广详情
	public static final String PUSHMES="pushmsg";//消息详情

	public static final String OGuide="operationGuide";//操作指引
	private WebView webView;
	
	private String tag;
	private String url;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_h5);
		Intent intent =getIntent();
		if (intent.getStringExtra("h5_tag") !=null){
			tag =intent.getStringExtra("h5_tag");
		}
		if (intent.getStringExtra("h5_url")!=null){
			url =intent.getStringExtra("h5_url");
		}

		
		initView();
	}

	private void initView() {
		String weburl =Common.URL+ "ap/" + tag  + ".html";
		if (tag.equals(YSLC)) {
			showMenu("操作指引");
			weburl =Common.URL+"ap/OperationGuide/OperationGuide.html";
		}else if (tag.equals(BQSM)){
			showMenu("版权申明");
		}else if (tag.equals(LBSSB)){
			showMenu("连不上设备");
		}else if (tag.equals(MZSM)){
			showMenu("免责声明");
		}else if (tag.equals(MESS)){
			showMenu("消息详情");
			weburl ="http://bns.qq.com/main.shtml";
		}else if (tag.equals(QUERYSPREAD)){
			showMenu("推荐");
			weburl=url;
		}else if (tag.equals(PUSHMES)){
			showMenu("消息详情");
			weburl =url;
		}


		webView = (WebView) findViewById(R.id.webview);
		webView.loadUrl(weburl);
		webView.setWebViewClient(new MyWebViewClient());
		webView.getSettings().setJavaScriptEnabled(true);
		WebSettings webSettings = webView.getSettings();
		webSettings.setDomStorageEnabled(true);
	}
	// Web视图
	private class MyWebViewClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			view.loadUrl(url);
			return true;
		}
	}



}
