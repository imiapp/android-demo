package com.bo.mysdkdemo;

import android.content.Context;

import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import cz.msebera.android.httpclient.entity.ByteArrayEntity;

public class NetWorkRequest {

    private final static String TAG = "NetWorkRequest";

    public static final String SCHEME = "http://";
    //测试环境
//    public static String HOST = "172.16.192.101:9100/";

    //正式环境
    public static String HOST = "172.16.192.101:9110/";

    public final static String BASE_URL = SCHEME + HOST;
    public static final String WEB_SERVER = BASE_URL + "imi/";

    public static void createChannel(Context context, String version, String scope, AsyncHttpResponseHandler handler) {
        Map<String, Object> payloadParams = new HashMap<>();
        payloadParams.put("version", version);
        payloadParams.put("scope", scope);

        JSONObject jsonObject = new JSONObject(payloadParams);
        ByteArrayEntity entity = null;
        try {
            entity = new ByteArrayEntity(jsonObject.toString().getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        AsyncHttpHelp.post(context, WEB_SERVER + "createChannel", entity, "application/json;charset=utf-8", handler);
    }

    public static void getAuthorizationInfo(Context context, String topicId, String scope, AsyncHttpResponseHandler handler) {
        Map<String, Object> payloadParams = new HashMap<>();
        payloadParams.put("topicId", topicId);
        payloadParams.put("scope", scope);

        JSONObject jsonObject = new JSONObject(payloadParams);
        ByteArrayEntity entity = null;
        try {
            entity = new ByteArrayEntity(jsonObject.toString().getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        AsyncHttpHelp.post(context, WEB_SERVER + "getAuthorizationInfo", entity, "application/json;charset=utf-8", handler);
    }

}
