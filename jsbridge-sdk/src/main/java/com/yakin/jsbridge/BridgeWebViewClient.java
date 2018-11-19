package com.yakin.jsbridge;

import android.graphics.Bitmap;
import android.os.Build;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

public class BridgeWebViewClient extends WebViewClient {

    private BridgeWebView mWebView;

    public BridgeWebViewClient(BridgeWebView webView) {
        mWebView = webView;
    }

    private boolean shouldOverrideUrlLoading(String url) {
        LogUtil.d("shouldOverrideUrlLoading was called:%s", url);
        try {
            url = URLDecoder.decode(url, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            LogUtil.e(e, "url decode failed");
        }
        if (url.startsWith(BridgeUtil.CALLBACK_DATA)) {
            mWebView.callbackToJava(url);
            return true;
        } else if (url.startsWith(BridgeUtil.CALL_METHOD)) {
            mWebView.callJavaMethod(url);
            return true;
        }
        return false;
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if(shouldOverrideUrlLoading(request.getUrl().toString())) {
                return true;
            }
        }
        return super.shouldOverrideUrlLoading(view, request);
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        if(shouldOverrideUrlLoading(url)) {
            return true;
        }
        return super.shouldOverrideUrlLoading(view, url);
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
        LogUtil.d("onPageStarted was called");
        mWebView.isLoading = true;
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        LogUtil.d("onPageFinished was called");
        mWebView.loadJsbridge();

        // 处理缓存事件
        mWebView.isLoading = false;
        List<BridgeWebView.CallQueue> list = new ArrayList<>(mWebView.mCacheCallQueue);
        mWebView.mCacheCallQueue.clear();
        for (BridgeWebView.CallQueue queue: list) {
            mWebView.callJsMethod(queue.method, queue.id, queue.param);
        }
    }
}
