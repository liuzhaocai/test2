package com.hopen.darts.networks.netty;

import android.os.Looper;
import android.util.Log;

import com.hopen.darts.base.C;
import com.hopen.darts.networks.commons.netty.BaseNettyRequest;
import com.hopen.darts.networks.interfaces.OnNettyReceiveCallback;

import java.util.LinkedList;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.CharsetUtil;

/**
 * 长连接操作入口工具类
 */

public class NettyUtil {
    public static final String TAG = "NettyLog";
    public static final ByteBuf ping = Unpooled.unreleasableBuffer(Unpooled.copiedBuffer("ping\n",
            CharsetUtil.UTF_8));
    public static final String pong = "pong";
    private static LinkedList<OnNettyReceiveCallback> onNettyReceiveCallbacks;
    private static NettyClient nettyClient;

    /**
     * 初始化长连接
     */
    public static void initClient() {
        nettyClient = new NettyClient();
        onNettyReceiveCallbacks = new LinkedList<>();
    }

    /**
     * 打开连接
     */
    public static void open() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                nettyClient.connect();
            }
        }).start();
    }

    /**
     * 断开连接
     */
    public static void close() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                NettyHandler.sendEmptyMessage(C.handler.netty_close);
                nettyClient.disconnect();
            }
        }).start();
    }

    /**
     * @return 长连接是否已经登录到服务器
     */
    public static boolean isNettyLogin() {
        return NettyHandler.isNettyLogin();
    }

    /**
     * 发送请求
     *
     * @param request 要发送的请求
     */
    public static void sendRequest(BaseNettyRequest request) {
        nettyClient.sendRequest(request);
    }

    /**
     * 延迟发送请求
     *
     * @param request 要发送的请求
     * @param delay   延迟时长，单位：毫秒
     */
    public static void sendRequest(BaseNettyRequest request, long delay) {
        nettyClient.sendRequest(request, delay);
    }

    /**
     * 添加长连接消息接收监听
     * 自动回调到主线程
     *
     * @param call 监听回调对象
     */
    public static void addReceiveMsgCall(OnNettyReceiveCallback call) {
        if (mainCheck(false)) return;
        onNettyReceiveCallbacks.addFirst(call);
    }

    /**
     * 移除长连接消息接收监听
     *
     * @param call 要移除的监听回调对象
     */
    public static void removeReceiveMsgCall(OnNettyReceiveCallback call) {
        if (mainCheck(false)) return;
        onNettyReceiveCallbacks.remove(call);
    }

    /**
     * 关闭重连
     */
    static void reopen() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                nettyClient.disconnect();
                nettyClient.connect();
            }
        }).start();
    }

    /**
     * 检查是否在主线程
     *
     * @param check 根据需要传入，传入true检查是否在主线程，传入false检查是否不在主线程
     * @return check为true时：在主线程返回true，否则返回false
     * check为false时：不在主线程返回true，否则返回false
     */
    static boolean mainCheck(boolean check) {
        if (check) {
            if (Looper.myLooper() == Looper.getMainLooper()) {
                Log.e(TAG, "Cannot be used in main looper. 不能在主线程使用");
                return true;
            }
            return false;
        } else {
            if (Looper.myLooper() != Looper.getMainLooper()) {
                Log.e(TAG, "Can only be used in main looper. 只能在主线程使用");
                return true;
            }
            return false;
        }
    }

    /**
     * 通知所有已注册的监听回调
     *
     * @param response 收到的数据
     */
    static void callListeners(String response) {
        for (OnNettyReceiveCallback item : onNettyReceiveCallbacks) {
            try {
                item.receiveMsg(response);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

}
