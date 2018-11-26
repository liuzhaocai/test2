package com.hopen.darts.base;

import android.os.Environment;

/**
 * Created by thomas on 2018/5/16.
 */

public class C {
    public static final class app {
        private static final String base_name = "Darts";
        public static final String no_sn = "未读取到SN码";
        public static final String file_path = fun.getBasePath() + "/" + base_name;
        public static final String file_path_tools = file_path + "/tools";
        public static final String file_path_img = file_path + "/images";
        public static final String file_path_temp = file_path + "/temp";
        public static final String file_path_img_compress = file_path_img + "/compress";
        public static final String file_path_audio = file_path + "/audio";
        public static final String sp_name = base_name + "_sp";
        public static final boolean debug = true;//是否开启debug模式，正式打包时需要设为false
        public static final int MAX_BYTES = 500 * 1024; // 单个log文件最大储存
        public static final int MAX_LOGGER_FILE_NUMBER = 10; //最多生成log文件个数
    }

    public static final class db {
        public static final String file_path_db = app.file_path + "/database";
        public static final String recordName = "/kingDarts_BackUp.db";
        public static final String dbName = "/kingDarts.db";
    }

    public static final class player {
        public static final String common_player = "common_player_";
        public static final String BasePlayerImagePath = C.app.file_path_img + "/Player/";
        public static final String common_name = "player";
        public static final String default_head = network.img_url + "app_resource/default_head.png";
    }

    public static final class netty {
        public static final String host_product = "ab.lovedarts.cn";
        public static final String host_develop = "kingdarts.lovedarts.cn";
        public static final String host = app.debug ? host_develop : host_product;
        public static final int port = 8000;
        public static final int ping_time = 4;
        public static final String code_success = "1";
        public static final String code_fail = "0";
        public static final String code_offline = "1000";
        public static final String ConnectErrorMsg = "无法连接服务器请检查网络连接";
    }

    public static final class msg {

    }

    public static final class network {
        public static final String base_url_product = "http://ab.lovedarts.cn";
        public static final String base_url_develop = "http://kingdarts.lovedarts.cn";
        public static final String base_url = app.debug ? base_url_develop : base_url_product;
        public static final String request_url = base_url + "/api/";
        public static final String img_url = "http://resource.lovedarts.cn/";
        public static final String file_url_product = "http://resource.lovedarts.cn/";
        public static final String file_url_develop = "https://lovedarts.oss-cn-beijing.aliyuncs.com/";
        public static final String file_url = app.debug ? file_url_develop : file_url_product;
        public static final String code_success = "0";
        public static final String code_fail = "1";
        public static final String code_invalid = "2000";
        public static final String code_handle_error = "235369";
    }

    public static final class key {
        public static final String key = "key";
        public static final String msg = "msg";
        public static final String position = "position";
        public static final String model = "model";
        public static final String id = "id";
        public static final String type = "type";
        public static final String item = "item";
        public static final String name = "name";
        public static final String list = "list";
        public static final String array = "array";
        public static final String user = "user";
        public static final String index = "index";
        public static final String rl = "rl";
        public static final String last_order_request_id = "last_order_request_id";
        public static final String last_order_start_request_id = "last_order_start_request_id";
        public static final String last_take_photo_hit_request_id = "last_take_photo_hit_request_id";
    }

    public static final class handler {
        public static final int netty_read = 90;
        public static final int netty_send_error = 91;
        public static final int netty_connect = 92;
        public static final int netty_disconnect = 93;
        public static final int netty_restart = 94;
        public static final int netty_close = 95;
        public static final int daemon = 96;
    }

    public static final class code {

        public static final class request {
            public static final int game_setting_local = 1;
            public static final int game_setting_online = 2;
        }

        public static final class result {
            public static final int game_setting_retreat = 1;
            public static final int game_setting_over = 2;
        }
    }

    public static final class fun {

        public static String getBasePath() {
            if (Environment.getExternalStorageState().equals(
                    Environment.MEDIA_MOUNTED)) {
                return Environment
                        .getExternalStorageDirectory().getPath();
            } else {
                return BaseApplication.getApplication().getApplicationContext().getFilesDir()
                        .getAbsolutePath();
            }
        }
    }
}
