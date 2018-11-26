package com.hopen.darts.networks.response;

import com.hopen.darts.networks.commons.netty.BaseNettyResponse;

public class OrderResponse extends BaseNettyResponse {

    @Override
    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    private DataBean data;

    public class DataBean {

        /**
         * order_no : I8E83032
         * order_type : 2
         * points : 140
         */

        private String url;
        private String order_no;
        private String order_type;
        private int points;
        private String balance;

        public String getBalance() {
            return balance;
        }

        public void setBalance(String balance) {
            this.balance = balance;
        }
        public String getUrl() {
            return url;
        }


        public void setUrl(String url) {
            this.url = url;
        }

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

        public int getPoints() {
            return points;
        }

        public void setPoints(int points) {
            this.points = points;
        }
    }

}
