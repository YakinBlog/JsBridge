package com.yakin.jsbridge;

import org.json.JSONException;
import org.json.JSONObject;

public class BridgeMessage {

    private final static String ID = "id";
    private final static String PARAM = "param";

    public String functionName;
    public String id;
    public String param;

    public void parseJson(String data) {
        try {
            JSONObject object = new JSONObject(data);
            if(object.has(ID)) {
                id = object.getString(ID);
            }
            if(object.has(PARAM)) {
                param = object.getString(PARAM);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
