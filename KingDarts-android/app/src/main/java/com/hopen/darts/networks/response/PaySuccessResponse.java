package com.hopen.darts.networks.response;

import com.hopen.darts.networks.commons.netty.BaseNettyResponse;

public class PaySuccessResponse extends BaseNettyResponse {

    private DataBean data;

    @Override
    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public class DataBean {

        private String order_no;
        private String order_type;
        private int userPoints;//用户积分
        private int vipLevel;//用户vip等级 1~9
        private double lifeRate;//剩余生命百分比
        private int remainTime;//剩余时间，单位秒

        public String getOrder_no() {
            return order_no;
        }

        public void setOrder_no(String order_no) {
            this.order_no = order_no;
        }

        public String getOrder_type() {
            return order_type;
        }

        public void setOrder_type(String order_type) {
            this.order_type = order_type;
        }

        public int getUserPoints() {
            return userPoints;
        }

        public void setUserPoints(int userPoints) {
            this.userPoints = userPoints;
        }

        public int getVipLevel() {
            return vipLevel;
        }

        public void setVipLevel(int vipLevel) {
            this.vipLevel = vipLevel;
        }

        public double getLifeRate() {
            return lifeRate;
        }

        public void setLifeRate(double lifeRate) {
            this.lifeRate = lifeRate;
        }

        public int getRemainTime() {
            return remainTime;
        }

        public void setRemainTime(int remainTime) {
            this.remainTime = remainTime;
        }
    }

}
