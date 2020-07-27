package com.lh.sms.client.ui.view;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;

import com.lh.sms.client.R;
import com.lh.sms.client.framing.ObjectFactory;

import androidx.appcompat.app.AppCompatActivity;

public class ShowWebView extends AppCompatActivity {
    private static final String TAG = "Agreement";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        //绑定事件
        bindEvent();
    }
    /**
     * @do 绑定事件
     * @author liuhua
     * @date 2020/4/16 8:34 PM
     */
    private void bindEvent() {
        //退出
        TextView viewById = findViewById(R.id.close_intent);
        viewById.setOnClickListener(v->{
            finish();
        });
        String title = getIntent().getStringExtra("title");
        viewById.setText(title);
        //显示协议内容
        WebView webView = findViewById(R.id.webView);
        webView.loadUrl(getIntent().getStringExtra("url"));
        WebSettings settings = webView.getSettings();
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
//支持js
        settings.setJavaScriptEnabled(true);
        settings.setUserAgentString("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/43.0.2357.134 Safari/537.36");
//自适应屏幕
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
//自动缩放
        settings.setBuiltInZoomControls(true);
        settings.setSupportZoom(true);
//支持获取手势焦点
        webView.requestFocusFromTouch();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        ObjectFactory.remove(this.getClass());
    }
}
