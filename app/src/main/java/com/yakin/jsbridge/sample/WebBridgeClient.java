package com.yakin.jsbridge.sample;

import android.webkit.WebView;

import com.yakin.jsbridge.BridgeWebView;
import com.yakin.jsbridge.BridgeWebViewClient;

public class WebBridgeClient extends BridgeWebViewClient {

    public WebBridgeClient(BridgeWebView webView) {
        super(webView);
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        // 自定义黑白名单控制等操作
        return super.shouldOverrideUrlLoading(view, url);
    }
}
