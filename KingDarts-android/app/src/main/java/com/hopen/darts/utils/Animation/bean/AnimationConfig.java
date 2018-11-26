package com.hopen.darts.utils.Animation.bean;

import java.util.List;

public class AnimationConfig {


    /**
     * config :
     * images : [{"count":1,"name":"10001.png"},{"count":1,"name":"10002.png"},{"count":1,"name":"10003.png"},{"count":1,"name":"10004.png"},{"count":1,"name":"10005.png"},{"count":1,"name":"10006.png"},{"count":1,"name":"10007.png"},{"count":1,"name":"10008.png"},{"count":1,"name":"10009.png"},{"count":1,"name":"10010.png"},{"count":1,"name":"10011.png"},{"count":1,"name":"10012.png"},{"count":1,"name":"10013.png"},{"count":1,"name":"10014.png"},{"count":1,"name":"10015.png"},{"count":1,"name":"10016.png"},{"count":1,"name":"10017.png"},{"count":1,"name":"10018.png"},{"count":1,"name":"10019.png"},{"count":1,"name":"10020.png"},{"count":1,"name":"10021.png"},{"count":1,"name":"10022.png"},{"count":1,"name":"10023.png"},{"count":1,"name":"10024.png"},{"count":1,"name":"10025.png"},{"count":1,"name":"10026.png"},{"count":1,"name":"10027.png"},{"count":1,"name":"10028.png"},{"count":1,"name":"10029.png"},{"count":1,"name":"10030.png"},{"count":1,"name":"10031.png"},{"count":1,"name":"10032.png"},{"count":1,"name":"10033.png"},{"count":1,"name":"10034.png"},{"count":1,"name":"10035.png"},{"count":1,"name":"10036.png"},{"count":1,"name":"10037.png"},{"count":1,"name":"10038.png"},{"count":1,"name":"10039.png"},{"count":1,"name":"10040.png"},{"count":1,"name":"10041.png"},{"count":1,"name":"10042.png"},{"count":1,"name":"10043.png"},{"count":1,"name":"10044.png"},{"count":1,"name":"10045.png"},{"count":1,"name":"10046.png"},{"count":1,"name":"10047.png"},{"count":1,"name":"10048.png"},{"count":1,"name":"10049.png"},{"count":1,"name":"10050.png"},{"count":1,"name":"10051.png"},{"count":1,"name":"10052.png"},{"count":1,"name":"10053.png"},{"count":1,"name":"10054.png"},{"count":1,"name":"10055.png"},{"count":1,"name":"10056.png"},{"count":1,"name":"10057.png"},{"count":1,"name":"10058.png"},{"count":1,"name":"10059.png"},{"count":1,"name":"10060.png"},{"count":1,"name":"10061.png"},{"count":1,"name":"10062.png"},{"count":1,"name":"10063.png"},{"count":1,"name":"10064.png"},{"count":1,"name":"10065.png"}]
     * path : /blood
     * style :
     */

    private String config;
    private String path;
    private String style;
    private List<ImagesBean> images;

    public String getConfig() {
        return config;
    }

    public void setConfig(String config) {
        this.config = config;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public List<ImagesBean> getImages() {
        return images;
    }

    public void setImages(List<ImagesBean> images) {
        this.images = images;
    }

    public static class ImagesBean {
        /**
         * count : 1
         * name : 10001.png
         */

        private int count;
        private String name;

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}