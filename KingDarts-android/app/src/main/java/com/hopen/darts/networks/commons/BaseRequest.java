package com.hopen.darts.networks.commons;

import android.text.TextUtils;
import android.util.Log;

import com.hopen.darts.base.C;

import java.util.HashMap;

import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by weitong on 16/7/4.
 */
public class BaseRequest {
    private String baseURLString = C.network.request_url;
    private MethodType methodType;
    private String methodName;
    protected HashMap<String, Object> queryMap;
    private HashMap<String, Object> headerMap;

    public BaseRequest() {
        init(MethodType.POST);
    }

    public BaseRequest(MethodType method_type) {
        init(method_type);
    }

    protected void init(MethodType method_type) {
        this.baseURLString = C.network.request_url;
        this.methodType = method_type;
        this.queryMap = new HashMap<>();
        this.headerMap = new HashMap<>();
    }

    private Request.Builder buildBuilder() {
        Request.Builder builder = new Request.Builder();
        try {
            Log.d("开始请求 请求参数 header -> ", "key = value");
            for (String key : headerMap.keySet()) {
                Log.d("          header -> ", key + " = " + headerMap.get(key));
                builder.addHeader(key, headerMap.get(key) + "");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return builder;
    }

    private String stringOfAPI() {
        if (MethodType.POST == this.methodType) {
            return this.baseURLString + methodName;
        }
        Log.d("开始请求 请求参数 query -> ", "key = value");
        StringBuffer query = new StringBuffer("?");
        for (String key : queryMap.keySet()) {
            Log.d("          query -> ", key + " = " + queryMap.get(key));
            String val = queryMap.get(key) == null ? "" : queryMap.get(key) + "";
            query.append(key).append("=").append(val).append("&");
        }
        query.deleteCharAt(query.length() - 1);
        return this.baseURLString + methodName + query.toString();
    }

    protected RequestBody buildFormBody() {
        try {
            Log.d("开始请求 请求参数 query -> ", "key = value");
            FormBody.Builder builder = new FormBody.Builder();
            for (String key : queryMap.keySet()) {
                Log.d("          query -> ", key + " = " + queryMap.get(key));
                builder.add(key, queryMap.get(key) + "");
            }
            return builder.build();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //  生成请求用Request
    Request buildRequest() {
        Request.Builder builder = buildBuilder();
        builder.url(stringOfAPI());
        Request request = null;
        if (this.methodType == MethodType.POST) {
            request = builder.post(buildFormBody()).build();
        } else if (this.methodType == MethodType.GET) {
            request = builder.get().build();
        } else {
            request = builder.delete().build();
        }
        return request;
    }

    public void addQuery(String key, Object val) {
        if (key == null || val == null || TextUtils.isEmpty(key)) {
            return;
        }
        this.queryMap.put(key, val);
    }

    public void addHeader(String key, Object val) {
        if (key == null || val == null || TextUtils.isEmpty(key)) {
            return;
        }
        this.headerMap.put(key, val);
    }

    public void setBaseURLString(String baseURLString) {
        this.baseURLString = baseURLString;
    }

    public MethodType getMethodType() {
        return methodType;
    }

    public void post() {
        this.methodType = MethodType.POST;
    }

    public void get() {
        this.methodType = MethodType.GET;
    }

    public void delete() {
        this.methodType = MethodType.DELETE;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getMethodName() {
        return methodName;
    }
}
