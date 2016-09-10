package com.hjsmallfly.syllabus.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.hjsmallfly.syllabus.syllabus.R;

public class InternetLoginWebActivity extends AppCompatActivity {

    private WebView webView;
    public static final String URL = "http://1.1.1.2";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_internet_login_web);

        webView = (WebView) findViewById(R.id.internetWebview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(true);
        // webView 自动重定向
        webView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // do your handling codes here, which url is the requested url
                // probably you need to open that url rather than redirect:
                view.loadUrl(url);
                return true; // then it is not handled by default action
            }
        });
        // 读取页面内容
        webView.loadUrl(URL);

    }
}
