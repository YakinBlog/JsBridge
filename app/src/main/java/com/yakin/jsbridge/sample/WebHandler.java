package com.yakin.jsbridge.sample;

import android.webkit.JavascriptInterface;

import com.yakin.jsbridge.JsCallback;
import com.yakin.jsbridge.LogUtil;

public class WebHandler extends JsCallback {

    @JavascriptInterface
    public void testJava(String id, String param) {
        LogUtil.d("test was called,[%s]%s", id, param);
        invoke(id, param);
    }
}
