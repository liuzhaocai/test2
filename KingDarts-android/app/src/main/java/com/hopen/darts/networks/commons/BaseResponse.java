package com.hopen.darts.networks.commons;

import com.hopen.darts.base.C;

/**
 * Created by weitong on 16/7/4.
 */
public class BaseResponse {

    private String code;
    private String msg;
    private Object data;

    public BaseResponse() {
    }

    public BaseResponse(String msg, String code) {
        this.msg = msg;
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public boolean isSuccess() {
        return C.network.code_success.equals(code);
    }

}
