package com.hopen.darts.networks.commons.netty;

import android.text.TextUtils;

import com.hopen.darts.base.C;
import com.hopen.darts.networks.commons.BaseResponse;

public class BaseNettyResponse extends BaseResponse {

    private String type;
    private String requestId;

    public N.Result isSuccess(String type) {
        if (this.type == null) {
            return N.Result.ignore;
        }
        if (this.type.equals(type)) {
            if (isSuccess()) {
                return N.Result.success;
            } else if (TextUtils.equals(this.getCode(),C.netty.code_offline)) {
                return N.Result.offline;
            } else {
                return N.Result.fail;
            }
        } else {
            return N.Result.ignore;
        }
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public boolean isSuccess() {
        return C.netty.code_success.equals(this.getCode());
    }

}
