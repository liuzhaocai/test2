package com.hopen.darts.networks.response;

import com.hopen.darts.networks.commons.BaseResponse;

/**
 * Created by zhangyanxue on 2018/7/20.
 */

public class GetAppVersionResponse extends BaseResponse {


    @Override
    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    /**
     * data : {"appUrl":"2018/7/others/13c11d679fbc46429b71eaa440550b85.apk","appVersion":2,"createTime":1531969260000}
     */

    private DataBean data;

    public static class DataBean {
        /**
         * appUrl : 2018/7/others/13c11d679fbc46429b71eaa440550b85.apk
         * appVersion : 2
         * createTime : 1531969260000
         */

        private String appUrl;
        private int appVersion;
        private long createTime;

        public String getAppUrl() {
            return appUrl;
        }

        public void setAppUrl(String appUrl) {
            this.appUrl = appUrl;
        }

        public int getAppVersion() {
            return appVersion;
        }

        public void setAppVersion(int appVersion) {
            this.appVersion = appVersion;
        }

        public long getCreateTime() {
            return createTime;
        }

        public void setCreateTime(long createTime) {
            this.createTime = createTime;
        }
    }
}
