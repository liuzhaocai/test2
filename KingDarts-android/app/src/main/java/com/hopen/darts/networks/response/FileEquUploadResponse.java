package com.hopen.darts.networks.response;

import com.hopen.darts.networks.commons.BaseResponse;

import java.util.List;

/**
 * Created by zhangyanxue on 2018/7/30.
 */

public class FileEquUploadResponse extends BaseResponse{


    /**
     * code : 0
     * msg :
     * data : {"files":[{"original":"11.jpg","type":".jpg","url":"2018/7/jpg/92be8008753c4ea0804bbaa93b7d5374.jpg"}]}
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
        private List<FilesBean> files;

        public List<FilesBean> getFiles() {
            return files;
        }

        public void setFiles(List<FilesBean> files) {
            this.files = files;
        }

        public static class FilesBean {
            /**
             * original : 11.jpg
             * type : .jpg
             * url : 2018/7/jpg/92be8008753c4ea0804bbaa93b7d5374.jpg
             */

            private String original;
            private String type;
            private String url;

            public String getOriginal() {
                return original;
            }

            public void setOriginal(String original) {
                this.original = original;
            }

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            public String getUrl() {
                return url;
            }

            public void setUrl(String url) {
                this.url = url;
            }
        }
    }
}
