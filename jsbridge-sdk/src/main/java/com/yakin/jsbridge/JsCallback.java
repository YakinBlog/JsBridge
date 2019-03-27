package com.yakin.jsbridge;

import android.content.Context;

public abstract class JsCallback {

    private BridgeWebView mWebView;

    void setWebView(BridgeWebView webView) {
        mWebView = webView;
    }

    protected Context getContext() {
        return mWebView.getContext();
    }

    protected void invoke(String id, String result) {
        mWebView.callbackToJs(id, result);
    }
}
