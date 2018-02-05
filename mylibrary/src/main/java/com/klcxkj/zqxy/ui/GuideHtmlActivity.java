package com.klcxkj.zqxy.ui;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import com.klcxkj.mylibrary.R;
import com.klcxkj.zqxy.common.Common;

public class GuideHtmlActivity extends BaseActivity {

    private LinearLayout backLayout;
    private WebView webView;
    private String weburl = Common.URL+"ap/OperationGuide/OperationGuide.html";;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide_html);
        initview();
        bindclick();
    }

    private void bindclick() {
        backLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (webView.canGoBack()){
                    webView.goBack();
                }else {
                    finish();
                }
            }
        });
    }

    private void initview() {
        backLayout = (LinearLayout) findViewById(R.id.top_back_op);
        webView = (WebView) findViewById(R.id.webview_html);

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


    @Override
    // 设置回退
    // 5、覆盖Activity类的onKeyDown(int keyCoder,KeyEvent event)方法
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //按下返回键并且webview界面可以返回
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {

            webView.goBack(); // goBack()表示返回WebView的上一页面
            return true;
        }
        return super.onKeyDown(keyCode,event);
    }

}
