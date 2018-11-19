package com.yakin.jsbridge;

import android.content.Context;
import android.os.Build;
import android.os.Looper;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BridgeWebView extends WebView implements IBridge {

    private JsCallback mHandler;
    private Map<String, IBridgeCallback> mCallbackMap = new HashMap<>();
    private long uniqueId = 1;

    // 缓存事件
    List<CallQueue> mCacheCallQueue = new ArrayList<>();
    boolean isLoading;

    class CallQueue {
        String id;
        String method;
        String param;
        IBridgeCallback callback;
    }

    public BridgeWebView(Context context) {
        super(context);
    }

    public BridgeWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BridgeWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        LogUtil.d("JsBridge:onFinishInflate was called");

        setVerticalScrollBarEnabled(false);
        setHorizontalScrollBarEnabled(false);
        setWebViewClient(new BridgeWebViewClient(this));
        WebSettings webSettings = getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSupportMultipleWindows(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
    }

    @Override
    public void setWebViewClient(WebViewClient client) {
        if(!(client instanceof BridgeWebViewClient)) {
            LogUtil.e("Not support JsBridge, pls setWebViewClient(BridgeWebViewClient)");
        }
        super.setWebViewClient(client);
    }

    public void setHandler(JsCallback handler) {
        handler.setWebView(this);
        this.mHandler = handler;
    }

    @Override
    public void call(String method, String param) {
        call(method, param, null);
    }

    @Override
    public void call(String method, String param, IBridgeCallback callback) {
        String callbackId = method + "_" + (uniqueId++) + "_" + SystemClock.currentThreadTimeMillis();
        if(callback != null) {
            mCallbackMap.put(callbackId, callback);
        }
        final CallQueue queue = new CallQueue();
        queue.id = callbackId;
        queue.method = method;
        queue.param = param;
        queue.callback = callback;
        handleCallQueue(queue);
    }

    private void handleCallQueue(CallQueue queue) {
        if(isLoading) {
            mCacheCallQueue.add(queue);
        } else {
            callJsMethod(queue.method, queue.id, queue.param);
        }
    }

    void callJavaMethod(String url) {
        if(mHandler == null) {
            LogUtil.e("Handler not found, pls call setHandler()");
            return;
        }
        BridgeMessage message = BridgeUtil.getCallMessageFromUrl(url);
        if(message != null) {
            BridgeUtil.callJavaMethod(mHandler, message);
        }
    }

    void callbackToJs(String id, String result) {
        String jsCommand = String.format(BridgeUtil.DISPATCH_JS_CALLBACK, id, result);
        if (Thread.currentThread() == Looper.getMainLooper().getThread()) {
            super.loadUrl(jsCommand);
        }
    }

    void callJsMethod(String method, String id, String param) {
        String jsCommand = String.format(BridgeUtil.CALL_JS_METHOD, method, id, param);
        if (Thread.currentThread() == Looper.getMainLooper().getThread()) {
            super.loadUrl(jsCommand);
        }
    }

    void callbackToJava(String url) {
        BridgeMessage message = BridgeUtil.getCallbackMessageFromUrl(url);
        if(message != null) {
            IBridgeCallback callback = mCallbackMap.remove(message.id);
            if(callback != null) {
                callback.onJsCallback(message.param);
            }
        }
    }

    void loadJsbridge() {
        super.loadUrl(BridgeUtil.getJsBridgeScript());
    }
}
