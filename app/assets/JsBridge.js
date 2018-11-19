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

    window.JsBridge = {
        call: _callNativeMethod,
        invoke: _dispatchCallBackToNative,
        _callJsMethod: _callJsMethodFromNative,
        _dispatchCallBack: _dispatchCallBackFromNative
    };

     _createExecIframe(document);
})();