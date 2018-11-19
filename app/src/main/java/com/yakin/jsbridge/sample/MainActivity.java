package com.yakin.jsbridge.sample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.yakin.jsbridge.BridgeWebView;
import com.yakin.jsbridge.IBridgeCallback;
import com.yakin.jsbridge.LogUtil;

public class MainActivity extends AppCompatActivity {

    BridgeWebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LogUtil.isDebug = true;

        mWebView = (BridgeWebView) findViewById(R.id.web_view);

        mWebView.loadUrl("file:///android_asset/demo.html");
        mWebView.setHandler(new WebHandler());

        findViewById(R.id.click).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWebView.call("testJs", "来自Native的问候", new IBridgeCallback() {
                    @Override
                    public void onJsCallback(String result) {
                        LogUtil.d("onJsCallback was called:%s", result);
                    }
                });
            }
        });
    }
}
