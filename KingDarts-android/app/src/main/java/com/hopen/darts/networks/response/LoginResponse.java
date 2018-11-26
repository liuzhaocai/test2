package com.hopen.darts.networks.response;

import com.hopen.darts.networks.commons.netty.BaseNettyResponse;

/**
 * Created by zhangyanxue on 2018/7/11.
 */

public class LoginResponse extends BaseNettyResponse {

    /**
     * data : {"token":"wweeerrtrtt"}
     */
    private DataBean data;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * token : wweeerrtrtt
         */

        private String token;

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }
    }
}
