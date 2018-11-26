package com.hopen.darts.networks.response;

import com.hopen.darts.networks.commons.netty.BaseNettyResponse;

/**
 * Created by zhangyanxue on 2018/7/12.
 */

public class StartPushResponse extends BaseNettyResponse {

    /**
     * code : 1
     * msg : 匹配成功
     * data : {"order_no":"1RK0I384","playerName":"player1"}
     */

    private String code;
    private String msg;
    private DataBean data;

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

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * order_no : 1RK0I384
         * playerName : player1
         */

        private String order_no;
        private String playerName;
        private long time;

        public String getOrder_no() {
            return order_no;
        }

        public void setOrder_no(String order_no) {
            this.order_no = order_no;
        }

        public String getPlayerName() {
            return playerName;
        }

        public void setPlayerName(String playerName) {
            this.playerName = playerName;
        }

        public long getTime() {
            return time;
        }

        public void setTime(long time) {
            this.time = time;
        }
    }
}
