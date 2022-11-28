package com.ulalalab.snipe.infra.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class InitTcpHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object packet) throws Exception {
        ChannelPipeline p = ctx.pipeline();

        p.addLast("TCP.DefaultHandler", new DefaultHandler());
        p.addLast("TCP.PacketHandler", new PacketHandler());
        //p.addLast("TCP.SettingHandler", new SettingHandler());
        p.addLast("TCP.ResultHandler", new ResultHandler());
        p.remove(this);

        ctx.fireChannelActive();
        ctx.fireChannelRead(packet);
    }
}