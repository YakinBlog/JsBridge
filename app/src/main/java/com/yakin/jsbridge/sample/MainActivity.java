package com.yakin.jsbridge.sample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.yakin.jsbridge.BridgeWebView;
import com.yakin.jsbridge.IBridgeCallback;
import com.yakin.jsbridge.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

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
        mWebView.setWebViewClient(new WebBridgeClient(mWebView));

        findViewById(R.id.click).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject json = new JSONObject();
                try {
                    json.put("key1", "value1");
                    json.put("key2", "value2");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                mWebView.call("testJs", json.toString(), new IBridgeCallback() {
                    @Override
                    public void onJsCallback(String result) {
                        LogUtil.d("onJsCallback was called:%s", result);
                    }
                });
            }
        });
    }
}
