package com.hopen.darts.networks.commons.netty;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;

import java.util.HashMap;
import java.util.Map;

public class BaseNettyRequest {

    private Map<String, Object> baseMap;
    private String type;
    private String requestId;

    protected BaseNettyRequest() {
        baseMap = new HashMap<>();
        requestId = System.currentTimeMillis() + "";
    }

    protected void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    protected void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getRequestId() {
        return requestId;
    }

    protected void setDataValue(String key, Object value) {
        if (key == null || value == null || TextUtils.isEmpty(key)) {
            return;
        }
        baseMap.put(key, value);
    }

    public String getParams() {
        Map<String, Object> params = new HashMap<>();
        params.put("type", this.type);
        params.put("requestId", this.requestId);
        if (baseMap.size() > 0) {
            params.put("data", baseMap);
        }
        return JSON.toJSONString(params);
    }

}
