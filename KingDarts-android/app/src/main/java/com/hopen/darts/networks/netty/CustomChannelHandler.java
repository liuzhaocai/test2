package com.hopen.darts.networks.netty;

import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.hopen.darts.base.C;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;

@Sharable
public class CustomChannelHandler extends ChannelInboundHandlerAdapter {

    /**
     * 通道接收到数据时触发此回调
     *
     * @param ctx      通道处理环境，主要用来在通道中向远端发送数据
     * @param response 收到的数据对象
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object response) {
        String s = (String) response;
        Log.d(NettyUtil.TAG, "收到数据：" + s);
        if (TextUtils.isEmpty(s)) return;
        ReferenceCountUtil.release(s);
        Message msg = Message.obtain();
        msg.what = C.handler.netty_read;
        msg.obj = response;
        NettyHandler.sendMessage(msg);
    }

    /**
     * 用户自定义事件触发时回调，当客户端的所有ChannelHandler中：
     * {@link C.netty#ping_time}秒内没有write事件，则会触发userEventTriggered方法，发送心跳
     *
     * @param ctx 通道处理环境，主要用来在通道中向远端发送数据
     * @param evt 事件对象
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            if (state == IdleState.WRITER_IDLE) {
                //发送心跳包
                Log.i(NettyUtil.TAG, "发送数据：ping");
                ctx.writeAndFlush(NettyUtil.ping.duplicate());
            }
        }
    }
}
