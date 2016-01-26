package com.alan.sphare.presentation.activityUI;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.alan.sphare.R;

/**
 * Created by Alan on 2016/1/25.
 */
public class WebViewActivity extends Activity {

    final String host = "http://139.129.40.103:5678/SPhare";
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview_main);

        webView = (WebView) findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);//保证跳转停留在本webview内，不调用系统浏览器
                return true;
            }
        });
        webView.loadUrl(host);
    }
}
