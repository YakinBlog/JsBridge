package com.yakin.jsbridge;

import android.text.TextUtils;

import java.lang.reflect.Method;

public class BridgeUtil {

    final static String SCHEMA = "jsbridge://";
    final static String CALL_METHOD = SCHEMA + "call/";	// 格式为 jsbridge://call/[function]/[data]
    final static String CALLBACK_DATA = SCHEMA + "callback/";	//格式为 jsbridge://callback/[function]/[data]

    final static String DISPATCH_JS_CALLBACK = "javascript:JsBridge._dispatchCallBack('%s', '%s');";
    final static String CALL_JS_METHOD = "javascript:JsBridge._callJsMethod('%s', '%s', '%s');";

    static BridgeMessage getCallMessageFromUrl(String url) {
        LogUtil.d("getMessageFromUrl was called:[%s]", url);
        String filter = url.replace(CALL_METHOD, "");
        String[] functionAndData = filter.split("/");
        if(functionAndData.length >= 1 && !TextUtils.isEmpty(functionAndData[0])) {
            BridgeMessage message = new BridgeMessage();
            message.functionName = functionAndData[0];
            if(functionAndData.length > 1) {
                message.parseJson(functionAndData[1]);
            }
            return message;
        }
        LogUtil.e("Not found function name:[%s]", url);
        return null;
    }

    static BridgeMessage getCallbackMessageFromUrl(String url) {
        LogUtil.d("getMessageFromUrl was called:[%s]", url);
        String data = url.replace(CALLBACK_DATA, "");
        if(!TextUtils.isEmpty(data)) {
            BridgeMessage message = new BridgeMessage();
            message.parseJson(data);
            return message;
        }
        LogUtil.e("Not found callback data:[%s]", url);
        return null;
    }

    static void callJavaMethod(Object object, BridgeMessage message) {
        Class clazz = object.getClass();
        try {
            Method function = clazz.getMethod(message.functionName, String.class, String.class);
            String id = message.id;
            if(id == null) {
                id = "";
            }
            String param = message.param;
            if(param == null) {
                param = "";
            }
            function.invoke(object, id, param);
        } catch (Exception e) {
            LogUtil.e(e, "%s.%s(String, String) call failed", clazz.getName(), message.functionName);
        }
    }
/*
(function() {
    var SCHEME = 'jsbridge://';
    var CALL_METHOD = 'call/';
    var CALLBACK_METHOD = 'callback/';

    var execIframe;

    var callbacks = {};
    var uniqueId = 1;

    // 创建消息发送器iframe
    function _createExecIframe(doc) {
        console.log('_createExecIframe was called');
        execIframe = doc.createElement('iframe');
        execIframe.style.display = 'none';
        doc.documentElement.appendChild(execIframe);
    }

    // 调用Native方法
    function _callNativeMethod(method, param, callback) {
        console.log('_callNativeMethod was called');
        var callbackId = method + '_' + (uniqueId++) + '_' + new Date().getTime();
        var params = {
            id: callbackId,
            param: param
        }
        if (callback) {
            callbacks[callbackId] = callback;
        }
        if(method) {
            execIframe.src = SCHEME + CALL_METHOD + method + '/' + JSON.stringify(params);
        }
    }

    // 处理Native回调
    function _dispatchCallBackFromNative(callbackId, result) {
        console.log('_dispatchNativeCallBack was called');
        if(callbackId) {
            var callback = callbacks[callbackId];
            if (callback) {
                callback(result);
                delete callbacks[callbackId];
            }
        }
    }

    // 调用Js方法
    function _callJsMethodFromNative(method, id, param) {
        console.log('_callJsMethodFromNative was called');
        if(method) {
            eval(method + '("' + id + '","' + param + '")');
        }
    }

    // 处理Js回调
    function _dispatchCallBackToNative(id, param) {
        console.log('_dispatchCallBackToNative was called');
        var params = {
            id: id,
            param: param
        }
        execIframe.src = SCHEME + CALLBACK_METHOD + JSON.stringify(params);
    }

    // JsBridge加载成功的回调
    function _initJavascriptBridge() {
        if(JsBridge) {
            console.log("_initJavascriptBridge was called.");
            eval('JsBridgeReady()');
        }
    }

    window.JsBridge = {
        call: _callNativeMethod,
        invoke: _dispatchCallBackToNative,
        _callJsMethod: _callJsMethodFromNative,
        _dispatchCallBack: _dispatchCallBackFromNative
    };

    _createExecIframe(document);
    _initJavascriptBridge();
})();
*/
    static String getJsBridgeScript() {
        // 初始化变量
        return "javascript:" +
                "(function(){" +
                    "var SCHEME=\"jsbridge://\";" +
                    "var CALL_METHOD=\"call/\";" +
                    "var CALLBACK_METHOD=\"callback/\";" +
                    "var execIframe;" +
                    "var callbacks={};" +
                    "var uniqueId=1;" +
                    "function _createExecIframe(doc){" +
                        "execIframe=doc.createElement(\"iframe\");" +
                        "execIframe.style.display=\"none\";" +
                        "doc.documentElement.appendChild(execIframe)" +
                    "}" +
                    "function _callNativeMethod(method,param,callback){" +
                        "var callbackId=method+\"_\"+(uniqueId++)+\"_\"+new Date().getTime();" +
                        "var params={id:callbackId,param:param};" +
                        "if(callback){callbacks[callbackId]=callback}" +
                        "if(method){execIframe.src=SCHEME+CALL_METHOD+method+\"/\"+JSON.stringify(params)}" +
                    "}" +
                    "function _dispatchCallBackFromNative(callbackId,result){" +
                        "if(callbackId){" +
                            "var callback=callbacks[callbackId];" +
                            "if(callback){" +
                                "callback(result);" +
                                "delete callbacks[callbackId]" +
                            "}" +
                        "}" +
                    "}" +
                    "function _callJsMethodFromNative(method,id,param){" +
                        "if(method){eval(method+'(\"'+id+'\",\"'+param+'\")')}" +
                    "}" +
                    "function _dispatchCallBackToNative(id,param){" +
                        "var params={id:id,param:param};" +
                        "execIframe.src=SCHEME+CALLBACK_METHOD+JSON.stringify(params)" +
                    "}" +
                    "function _initJavascriptBridge(){" +
                        "if(JsBridge){eval(\"JsBridgeReady()\")}" +
                    "}" +
                    "window.JsBridge={" +
                        "call:_callNativeMethod," +
                        "invoke:_dispatchCallBackToNative," +
                        "_callJsMethod:_callJsMethodFromNative," +
                        "_dispatchCallBack:_dispatchCallBackFromNative" +
                    "};" +
                    "_createExecIframe(document);" +
                    "_initJavascriptBridge()" +
                "})();";
    }
}
