package com.hopen.darts.networks.netty;

import com.hopen.darts.base.C;

import java.util.concurrent.TimeUnit;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;

public class CustomChannelInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel ch) {
        ChannelPipeline pipeline = ch.pipeline();
        ByteBuf buf = Unpooled.copiedBuffer("\n".getBytes());
        pipeline.addLast("user_event", new IdleStateHandler(0, C.netty.ping_time, 0, TimeUnit.SECONDS));
        pipeline.addLast("user_decoder", new DelimiterBasedFrameDecoder(1024, buf));
        pipeline.addLast("decoder", new StringDecoder());
        pipeline.addLast("encoder", new StringEncoder());
        pipeline.addLast("user_handler", new CustomChannelHandler());
    }
}
