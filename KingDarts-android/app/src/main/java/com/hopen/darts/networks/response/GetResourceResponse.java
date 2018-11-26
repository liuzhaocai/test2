package com.hopen.darts.networks.response;

import com.hopen.darts.networks.commons.BaseResponse;

import java.util.List;

public class GetResourceResponse extends BaseResponse {


    private List<DataBean> data;

    @Override
    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public class DataBean{


        /**
         * resourceUrl : app_resource/video_animation.zip
         * resourceName : video_animation
         * fileName : 视频
         * resourceVersion : 1
         * type : 1 视频压缩文件 2 video  3 animation压缩文件
         */

        private String resourceUrl;
        private String resourceName;
        private String fileName;
        private int resourceVersion;
        private int type;

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getResourceUrl() {
            return resourceUrl;
        }

        public void setResourceUrl(String resourceUrl) {
            this.resourceUrl = resourceUrl;
        }

        public String getResourceName() {
            return resourceName;
        }

        public void setResourceName(String resourceName) {
            this.resourceName = resourceName;
        }

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public int getResourceVersion() {
            return resourceVersion;
        }

        public void setResourceVersion(int resourceVersion) {
            this.resourceVersion = resourceVersion;
        }

    }

}
