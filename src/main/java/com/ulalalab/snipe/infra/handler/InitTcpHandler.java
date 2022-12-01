package com.ulalalab.snipe.infra.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Slf4j
//@Scope("prototype")
@Component
@RequiredArgsConstructor
@ChannelHandler.Sharable
public class InitTcpHandler extends ChannelInboundHandlerAdapter {

    private final DefaultHandler defaultHandler;
    private final SettingHandler settingHandler;
    private final PacketHandler packetHandler;
    private final ResultHandler resultHandler;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object packet) throws Exception {
        ChannelPipeline p = ctx.pipeline();

        p.addLast("TCP.DefaultHandler", defaultHandler);
        p.addLast("TCP.PacketHandler", packetHandler);
        p.addLast("TCP.SettingHandler", settingHandler);
        p.addLast("TCP.ResultHandler", resultHandler);
        p.remove(this);

        ctx.fireChannelActive();
        ctx.fireChannelRead(packet);
    }
}