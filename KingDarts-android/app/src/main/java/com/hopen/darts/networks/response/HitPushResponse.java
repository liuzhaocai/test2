package com.hopen.darts.networks.response;

import com.hopen.darts.networks.commons.netty.BaseNettyResponse;
import com.hopen.darts.networks.mapper.Step;

public class HitPushResponse extends BaseNettyResponse {

    private String code;
    private String msg;
    private DataBean data;

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String getMsg() {
        return msg;
    }

    @Override
    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public class DataBean{
        private Step step;

        public Step getStep() {
            return step;
        }

        public void setStep(Step step) {
            this.step = step;
        }
    }
}
