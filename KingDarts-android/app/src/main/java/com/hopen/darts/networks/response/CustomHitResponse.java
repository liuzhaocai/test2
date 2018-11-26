package com.hopen.darts.networks.response;

import com.hopen.darts.networks.commons.netty.BaseNettyResponse;

/**
 * Created by zhangyanxue on 2018/7/30.
 */

public class CustomHitResponse extends BaseNettyResponse {

    /**
     * data : {"playerInfo":{"target_equno":"111111"}}
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
         * playerInfo : {"target_equno":"111111"}
         */

        private PlayerInfoBean playerInfo;

        public PlayerInfoBean getPlayerInfo() {
            return playerInfo;
        }

        public void setPlayerInfo(PlayerInfoBean playerInfo) {
            this.playerInfo = playerInfo;
        }

        public static class PlayerInfoBean {
            /**
             * playIcon : 111111
             * equno : 111111
             * isCamera : 111111
             */

            private boolean isCamera;
            private String playIcon;
            private String equno;

            public String getPlayIcon() {
                return playIcon;
            }

            public void setPlayIcon(String playIcon) {
                this.playIcon = playIcon;
            }

            public String getEquno() {
                return equno;
            }

            public void setEquno(String equno) {
                this.equno = equno;
            }

            public boolean isCamera() {
                return isCamera;
            }

            public void setCamera(boolean camera) {
                isCamera = camera;
            }
        }
    }
}
