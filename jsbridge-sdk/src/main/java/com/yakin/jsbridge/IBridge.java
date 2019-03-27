package com.yakin.jsbridge;

public interface IBridge {

    void call(String method, String param);
    void call(String method, String param, IBridgeCallback callback);
}
