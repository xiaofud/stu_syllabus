package com.hjsmallfly.syllabus.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.hjsmallfly.syllabus.helpers.ClipBoardHelper;
import com.hjsmallfly.syllabus.syllabus.R;
import com.umeng.analytics.MobclickAgent;

public class OAWebViewActivity extends AppCompatActivity implements View.OnClickListener {

    private WebView webView;

    private TextView url_text_view;
    private Button copy_button;

    private void find_views(){

        webView = (WebView) findViewById(R.id.oa_web_view);
        url_text_view = (TextView) findViewById(R.id.oa_title_view);
        copy_button = (Button) findViewById(R.id.copy_the_url_button);

    }

    private void setup_views(){
        url_text_view.setText(OAActivity.CUR_OA_OBJECT.title);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(true);
        // fit width
        webView.setInitialScale(100);
        webView.loadUrl(OAActivity.CUR_OA_OBJECT.url);

        // 监听器
        copy_button.setOnClickListener(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oaweb_view);

        find_views();
        setup_views();
    }

    // 友盟的统计功能
    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.copy_the_url_button:
                ClipBoardHelper.setContent(this, OAActivity.CUR_OA_OBJECT.url);
                Toast.makeText(OAWebViewActivity.this, "链接已复制到剪贴板", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }
}
