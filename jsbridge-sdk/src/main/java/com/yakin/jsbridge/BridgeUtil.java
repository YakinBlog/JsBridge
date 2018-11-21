package com.yakin.jsbridge;

import java.lang.reflect.Method;

public class BridgeUtil {

    final static String SCHEMA = "jsbridge://";
    final static String CALL_METHOD = SCHEMA + "call/";	// 格式为 jsbridge://call/[function]/[data]
    final static String CALLBACK_DATA = SCHEMA + "callback/";	//格式为 jsbridge://callback/[function]/[data]

    final static String METHOD_RESULT = "result";
    final static String NO_SUCH_METHOD = "NoSuchMethod";

    final static String DISPATCH_JS_CALLBACK = "javascript:JsBridge._dispatchCallBack('%s', '%s');";
    final static String CALL_JS_METHOD = "javascript:JsBridge._callJsMethod('%s', '%s', '%s');";

    static BridgeMessage getMessageFromUrl(String url) {
        LogUtil.d("getMessageFromUrl was called:[%s]", url);
        String filter = "";
        if(url.startsWith(CALL_METHOD)) {
            filter = url.replace(CALL_METHOD, "");
        } else if(url.startsWith(CALLBACK_DATA)) {
            filter = url.replace(CALLBACK_DATA, "");
        }
        int index = filter.indexOf("/");
        if(index > 0) {
            BridgeMessage message = new BridgeMessage();
            message.functionName = filter.substring(0, index);
            if(filter.length() > index) {
                message.parseJson(filter.substring(index + 1));
            }
            return message;
        }
        LogUtil.e("Not found function name:[%s]", url);
        return null;
    }

    static boolean callJavaMethod(Object object, BridgeMessage message) {
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
            return true;
        } catch (Exception e) {
            LogUtil.e(e, "%s.%s(String, String) call failed", clazz.getName(), message.functionName);
            return false;
        }
    }
/*
(function() {
    var SCHEME = 'jsbridge://';
    var CALL_METHOD = 'call/';
    var CALLBACK_METHOD = 'callback/';

    var METHOD_RESULT = 'result/';
    var NO_SUCH_METHOD = "NoSuchMethod";

    var execIframe;

    var callbacks = {};
    var uniqueId = 1;

    // 创建消息发送器iframe
    function _createExecIframe(doc) {
        console.log('_createExecIframe was called.');
        execIframe = doc.createElement('iframe');
        execIframe.style.display = 'none';
        doc.documentElement.appendChild(execIframe);
    }

    // 调用Native方法
    function _callNativeMethod(method, param, callback) {
        console.log('_callNativeMethod was called.');
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
        console.log('_dispatchNativeCallBack was called.');
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
        console.log('_callJsMethodFromNative was called.');
        if(method) {
            try {
                eval(method + '("' + id + '","' + param + '")');
            } catch(e) {
                console.log('Not found ' + method + ' method.');
                var params = {
                    id: id,
                    param: NO_SUCH_METHOD
                }
                execIframe.src = SCHEME + CALLBACK_METHOD + METHOD_RESULT + JSON.stringify(params);
            }
        }
    }

    // 处理Js回调
    function _dispatchCallBackToNative(id, param) {
        console.log('_dispatchCallBackToNative was called.');
        var params = {
            id: id,
            param: param
        }
        execIframe.src = SCHEME + CALLBACK_METHOD + METHOD_RESULT + JSON.stringify(params);
    }

    // JsBridge加载成功的回调
    function _initJavascriptBridge() {
        if(JsBridge) {
            console.log("_initJavascriptBridge was called.");
            try {
                eval('JsBridgeReady()');
            } catch(e) {
                console.log("Not found JsBridgeReady mathod.");
            }
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
        return "javascript:" +
                "(function(){" +
                    "var SCHEME=\"jsbridge://\";" +
                    "var CALL_METHOD=\"call/\";" +
                    "var CALLBACK_METHOD=\"callback/\";" +
                    "var METHOD_RESULT=\"result/\";" +
                    "var NO_SUCH_METHOD=\"NoSuchMethod\";" +
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
                        "if(method){" +
                            "try{" +
                                "eval(method+'(\"'+id+'\",\"'+param+'\")')" +
                            "}catch(e){" +
                                "var params={id:id,param:NO_SUCH_METHOD};" +
                                "execIframe.src=SCHEME+CALLBACK_METHOD+METHOD_RESULT+JSON.stringify(params)" +
                            "}" +
                        "}" +
                    "}" +
                    "function _dispatchCallBackToNative(id,param){" +
                        "var params={id:id,param:param};" +
                        "execIframe.src=SCHEME+CALLBACK_METHOD+METHOD_RESULT+JSON.stringify(params)" +
                    "}" +
                    "function _initJavascriptBridge(){" +
                        "if(JsBridge){try{eval(\"JsBridgeReady()\")}catch(e){}}" +
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
