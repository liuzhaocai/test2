package com.hopen.darts.networks.netty;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.hopen.darts.base.BaseApplication;
import com.hopen.darts.base.BaseDialog;
import com.hopen.darts.base.C;
import com.hopen.darts.manager.KDManager;
import com.hopen.darts.networks.commons.netty.N;
import com.hopen.darts.networks.request.LoginRequest;
import com.hopen.darts.networks.response.LoginResponse;
import com.hopen.darts.utils.AlertMessage.PushMessageDialog;
import com.hopen.darts.utils.IPUtil;
import com.hopen.darts.utils.PhoneUtil;

/**
 * 长连接主线程处理器
 */
public class NettyHandler {

    /**
     * Initialization on Demand Holder.
     * 这种方法使用内部类来做到延迟加载对象，在初始化这个内部类的时候，
     * JLS(Java Language Sepcification)会保证这个类的线程安全。
     * 这种写法最大的优点在于，完全使用了Java虚拟机的机制进行同步保证，没有使用同步的关键字。
     */
    private static class SingletonHolder {
        private static final NettyHandler instance = new NettyHandler();
    }

    /**
     * 私有构造方法
     */
    private NettyHandler() {
        nettyHandler = new Handler(Looper.getMainLooper(), new NettyHandlerCallback());
        loginFlag = FLAG_LOGIN_NONE;
        lastResponseTime = -1;
    }

    /**
     * @return 获取单例
     */
    static NettyHandler getInstance() {
        return SingletonHolder.instance;
    }

    /**
     * 发送一个仅有what标识的空消息到主线程
     *
     * @param what what标识
     * @return 发送是否成功
     */
    static boolean sendEmptyMessage(int what) {
        return getInstance().nettyHandler.sendEmptyMessage(what);
    }

    /**
     * 发送一个消息对象到主线程
     *
     * @param msg 消息对象
     * @return 发送是否成功
     */
    static boolean sendMessage(Message msg) {
        return getInstance().nettyHandler.sendMessage(msg);
    }

    /**
     * @return 长连接是否已经登录到服务器
     */
    static boolean isNettyLogin() {
        return getInstance().loginFlag == FLAG_LOGGED_IN;
    }

    /**
     * 登录标识：已登录
     */
    private static final int FLAG_LOGGED_IN = 1;
    /**
     * 登录标识：登录中
     */
    private static final int FLAG_LOGGING_IN = 0;
    /**
     * 登录标识：登录失败
     */
    private static final int FLAG_LOGIN_FAILED = -1;
    /**
     * 登录标识：还未登录
     */
    private static final int FLAG_LOGIN_NONE = -2;
    /**
     * 登录标识：长连接已关闭，登录请求和重连等机制也关闭
     */
    private static final int FLAG_LOGIN_CLOSE = -3;
    /**
     * 回调主线程的Handler
     */
    private Handler nettyHandler;
    /**
     * 登录状态标识
     */
    private volatile int loginFlag;
    /**
     * 最近一次收到服务器消息的时间
     */
    private long lastResponseTime;
    /**
     * 长连接相关信息提示窗
     */
    private PushMessageDialog infoDialog;
    /**
     * 轮循守护线程
     */
    private Thread nettyDaemonThread;

    /**
     * 当长连接断开时回调
     */
    private void onNettyDisconnect() {
        if (loginFlag != FLAG_LOGIN_CLOSE)
            loginFlag = FLAG_LOGIN_NONE;
        try {
            if (infoDialog != null) {
                if (loginFlag == FLAG_LOGIN_CLOSE) {
                    infoDialog.dismiss();
                    infoDialog = null;
                } else {
                    if (IPUtil.isNetworkAvailable(BaseApplication.getApplication())) {
                        infoDialog.refresh("正在重新连接\n请稍后");
                    } else {
                        infoDialog.refresh("未检测到互联网\n请检查");
                    }
                }
                return;
            }
            if (BaseApplication.getBaseActivity() == null || loginFlag == FLAG_LOGIN_CLOSE) return;
            infoDialog = PushMessageDialog.get()
                    .text("正在重新连接\n请稍后")
                    .autoDismiss(false);
            infoDialog.show(BaseApplication.getBaseActivity());
        } catch (Throwable e) {
            e.printStackTrace();
            infoDialog = null;
        }
    }

    /**
     * 与服务器失去连接的提示，在提示后几秒（提示的持续时间）后自动重启
     */
    private void nettyRestart() {
        PushMessageDialog.get()
                .text("与服务器失去连接\n正在重启")
                .autoDismiss(true)
                .dismissListener(new BaseDialog.OnDismissWithAnimEndListener() {
                    @Override
                    public void onDismissWithAnimEnd() {
                        BaseApplication.restart();
                    }
                })
                .show(BaseApplication.getBaseActivity());
    }

    /**
     * 当长连接收到消息时回调
     *
     * @param response 收到的消息
     */
    private void onNettyRead(String response) {
        lastResponseTime = System.currentTimeMillis();
        if (NettyUtil.pong.equals(response)) return; //收到心跳
        if (response.contains("\"type\":\"" + N.active + "\"")) {//通道在服务器端已处于活跃状态
            //请求登陆接口
            login2Server(0);
        } else if (response.contains("\"type\":\"" + N.Login + "\"")) {//登录接口回文
            LoginResponse loginResponse = JSON.parseObject(response, LoginResponse.class);
            switch (loginResponse.getCode()) {
                case C.netty.code_success:
                    loginFlag = FLAG_LOGGED_IN;
                    KDManager.getInstance().setToken(loginResponse.getData().getToken());
                    try {
                        if (infoDialog != null) {
                            infoDialog.dismiss();
                            infoDialog = null;
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                        infoDialog = null;
                    }
                    break;
                case "401"://登录失败
                    NettyUtil.close();
                    break;
                case "403"://服务器重启中
                    loginFlag = FLAG_LOGIN_FAILED;
                    login2Server(10000);
                    break;
                case "402"://设备已登录
                default://其他未知情况
                    loginFlag = FLAG_LOGIN_FAILED;
                    login2Server(6000);
                    break;
            }
            NettyUtil.callListeners(response);
        } else NettyUtil.callListeners(response);
    }

    /**
     * 调用服务器自定义登录接口登录到服务器
     */
    private synchronized void login2Server(final long delay) {
        if (loginFlag >= FLAG_LOGGING_IN) return;
        loginFlag = FLAG_LOGGING_IN;
        NettyUtil.sendRequest(new LoginRequest(PhoneUtil.readSNCode()), delay);
    }

    /**
     * 开启长连接状态监视守护线程
     */
    private void startNettyDemonThread() {
        if (nettyDaemonThread == null) {
            nettyDaemonThread = new Thread(new NettyDemonRunnable());
            nettyDaemonThread.setName("netty_daemon");
            nettyDaemonThread.setDaemon(true);//守护线程
            nettyDaemonThread.start();
        }
    }

    /**
     * 长连接状态监视守护线程
     */
    private class NettyDemonRunnable implements Runnable {

        @Override
        public void run() {
            try {
                long enter_time = System.currentTimeMillis();
                while (loginFlag == FLAG_LOGIN_NONE) {
                    Thread.sleep(1000);
                    if (loginFlag == FLAG_LOGIN_CLOSE) {
                        nettyDaemonThread = null;
                        return;
                    }
                    if (System.currentTimeMillis() - enter_time > 20000) {
                        //长期没收到服务器推送的连接成功消息，重新打开通道
                        Log.e(NettyUtil.TAG, "超过20秒没有收到连接成功消息，开始执行重连操作");
                        nettyDaemonThread = null;
                        if (loginFlag != FLAG_LOGIN_CLOSE)
                            NettyUtil.reopen();
                        return;
                    }
                }
                long active_time = System.currentTimeMillis();
                while (true) {
                    Thread.sleep(1000);
                    if (loginFlag == FLAG_LOGIN_CLOSE) {
                        nettyDaemonThread = null;
                        return;
                    }
                    if (loginFlag == FLAG_LOGGED_IN) break;
                    if (System.currentTimeMillis() - active_time > 30000) {
                        //长期没收到服务器推送的连接成功消息，重新打开通道
                        Log.e(NettyUtil.TAG, "超过30秒没有登录成功，开始执行重连操作");
                        nettyDaemonThread = null;
                        if (loginFlag != FLAG_LOGIN_CLOSE)
                            NettyUtil.reopen();
                        return;
                    }
                }
                while (true) {
                    Thread.sleep(4000);
                    if (loginFlag == FLAG_LOGIN_CLOSE) {
                        nettyDaemonThread = null;
                        return;
                    }
                    if (lastResponseTime == -1) continue;
                    if (System.currentTimeMillis() - lastResponseTime > 12000) {
                        //长期没收到服务器推送数据，重新连接服务器
                        Log.e(NettyUtil.TAG, "超过12秒没有收到pong心跳，开始执行重连操作");
                        nettyDaemonThread = null;
                        if (loginFlag != FLAG_LOGIN_CLOSE)
                            NettyUtil.reopen();
                        return;
                    }
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 回调主线程消息接收处理器
     */
    private class NettyHandlerCallback implements Handler.Callback {

        /**
         * 主线程回调处理
         *
         * @param msg 消息对象
         */
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case C.handler.netty_connect:
                    startNettyDemonThread();
                    break;
                case C.handler.netty_disconnect:
                    onNettyDisconnect();
                    break;
                case C.handler.netty_restart:
                    nettyRestart();
                    break;
                case C.handler.netty_send_error:
                    NettyUtil.callListeners((String) msg.obj);
                    break;
                case C.handler.netty_read:
                    onNettyRead((String) msg.obj);
                    break;
                case C.handler.netty_close:
                    loginFlag = FLAG_LOGIN_CLOSE;
                    break;
            }
            return false;
        }
    }
}
