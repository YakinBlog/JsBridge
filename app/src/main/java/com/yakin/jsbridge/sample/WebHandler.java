package com.yakin.jsbridge.sample;

import com.yakin.jsbridge.JsCallback;
import com.yakin.jsbridge.LogUtil;

public class WebHandler extends JsCallback {

    public void testJava(String id, String param) {
        LogUtil.d("test was called,[%s]%s", id, param);
        invoke(id, "来自Native的回复");
    }
}
