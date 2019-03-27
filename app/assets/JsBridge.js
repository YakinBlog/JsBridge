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
                eval(method + '(\'' + id + '\',\'' + param + '\')');
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
