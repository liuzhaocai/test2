package com.hopen.darts.networks.netty;

import android.os.Message;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.hopen.darts.base.C;
import com.hopen.darts.networks.commons.netty.BaseNettyRequest;
import com.hopen.darts.networks.commons.netty.BaseNettyResponse;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;


class NettyClient {
    private static final int FLAG_CONNECTING = 1;
    private static final int FLAG_DISCONNECTING = 2;
    private static final int FLAG_CONNECTED = 3;
    private static final int FLAG_DISCONNECTED = 4;
    private static final int FLAG_CONNECT_FAILED = 5;

    private NioEventLoopGroup group;
    private Bootstrap bootstrap;
    private Channel channel;
    private volatile int connectFlag;
    private int reconnectNum = 0;

    /**
     * 初始化
     */
    NettyClient() {
    }

    /**
     * @return 是否还没有连接状态
     */
    private synchronized boolean isConnectNone() {
        return connectFlag == 0;
    }

    /**
     * @return 是否正在连接
     */
    private synchronized boolean isConnecting() {
        return connectFlag == FLAG_CONNECTING;
    }

    /**
     * @return 是否正在断开连接
     */
    private synchronized boolean isDisconnecting() {
        return connectFlag == FLAG_DISCONNECTING;
    }

    /**
     * @return 是否已经连接
     */
    private synchronized boolean isConnected() {
        return connectFlag == FLAG_CONNECTED;
    }

    /**
     * @return 是否打开长连接失败
     */
    private synchronized boolean isConnectFailed() {
        return connectFlag == FLAG_CONNECT_FAILED;
    }

    /**
     * @return 是否已经断开连接
     */
    private synchronized boolean isDisconnected() {
        return connectFlag == FLAG_DISCONNECTED;
    }

    /**
     * 打开长连接
     */
    synchronized void connect() {
        if (NettyUtil.mainCheck(true)) return;
        if (isConnectNone() || isDisconnected()) {
            connectFlag = FLAG_CONNECTING;
            Log.i(NettyUtil.TAG, "正在打开长连接...");
            try {
                group = new NioEventLoopGroup();
                bootstrap = new Bootstrap().group(group)
                        .channel(NioSocketChannel.class)
                        .option(ChannelOption.SO_KEEPALIVE, true)
                        .handler(new CustomChannelInitializer());
                ChannelFuture future = bootstrap.connect(C.netty.host, C.netty.port).sync();
                if (future != null && future.isSuccess()) {
                    Log.i(NettyUtil.TAG, "长连接打开成功");
                    channel = future.channel();
                    reconnectNum = 0;
                    connectFlag = FLAG_CONNECTED;
                    NettyHandler.sendEmptyMessage(C.handler.netty_connect);
                } else throw new IOException();
            } catch (Throwable e) {
                e.printStackTrace();
                connectFlag = FLAG_CONNECT_FAILED;
                Log.i(NettyUtil.TAG, "长连接打开失败");
                reconnect();
            }
        }
    }

    /**
     * 关闭长连接
     */
    synchronized void disconnect() {
        if (NettyUtil.mainCheck(true)) return;
        if (isConnected() || isConnectFailed()) {
            connectFlag = FLAG_DISCONNECTING;
            NettyHandler.sendEmptyMessage(C.handler.netty_disconnect);
            if (group != null) {
                Log.i(NettyUtil.TAG, "正在关闭长连接...");
                try {
                    group.shutdownGracefully(0, 0, TimeUnit.MILLISECONDS).sync();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
            group = null;
            channel = null;
            bootstrap = null;
            connectFlag = FLAG_DISCONNECTED;
            Log.i(NettyUtil.TAG, "长连接已关闭。");
        }
    }

    /**
     * 重新创建长连接
     */
    private void reconnect() {
        if (NettyUtil.mainCheck(true)) return;
        if (reconnectNum < 20) {//最多179次，当嵌套调用超过179次时，会报StackOverflowError异常
            try {
                Thread.sleep(6000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            reconnectNum++;
            Log.i(NettyUtil.TAG, "正在执行第" + reconnectNum + "次重连，首先关闭已经创建的长连接");
            disconnect();
            connect();
        } else {
            NettyHandler.sendEmptyMessage(C.handler.netty_restart);
        }
    }

    /**
     * 向长连接中发送数据
     *
     * @param msg 要发送的数据
     * @return 发送是否成功
     */
    synchronized boolean writeAndFlush(String msg) {
        if (NettyUtil.mainCheck(true)) return false;
        try {
            if (!isConnected()) throw new IOException("长连接还未建立");
            Log.i(NettyUtil.TAG, "发送数据：" + msg);
            ChannelFuture future = channel.writeAndFlush(msg + "\n").sync();
            if (future != null && future.isSuccess()) {
                Log.i(NettyUtil.TAG, "发送数据成功");
                return true;
            } else throw new IOException("通道发送失败");
        } catch (Throwable e) {
            e.printStackTrace();
            Log.i(NettyUtil.TAG, "发送数据失败");
            return false;
        }
    }

    /**
     * 发送消息
     *
     * @param request 要发送的请求
     */
    synchronized void sendRequest(BaseNettyRequest request) {
        sendRequest(request, 0);
    }

    /**
     * 发送消息
     *
     * @param request 要发送的请求
     * @param delay   延迟时长，单位：毫秒
     */
    synchronized void sendRequest(BaseNettyRequest request, long delay) {
        new Sender(request, delay).send();
    }

    /**
     * 请求发送员
     */
    class Sender implements Runnable {
        BaseNettyRequest request;
        long delay;

        Sender(BaseNettyRequest request, long delay) {
            this.request = request;
            this.delay = delay;
        }

        void send() {
            new Thread(this).start();
        }

        @Override
        public void run() {
            if (delay > 0) {
                try {
                    Thread.sleep(delay);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
            if (request != null && !writeAndFlush(request.getParams())) {
                BaseNettyResponse response = new BaseNettyResponse();
                response.setType(request.getType());
                response.setRequestId(request.getRequestId());
                response.setMsg("发送失败");
                response.setCode(C.netty.code_fail);
                Message msg = Message.obtain();
                msg.what = C.handler.netty_send_error;
                msg.obj = JSON.toJSONString(response);
                NettyHandler.sendMessage(msg);
            }
        }
    }
}