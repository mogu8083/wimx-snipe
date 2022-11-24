package com.ulalalab.snipe.infra.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SelectHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object packet) throws Exception {
        ChannelPipeline p = ctx.pipeline();

        // 기본 ( 연결 관련 )
        p.addLast("TCP.DefaultHandler", new DefaultHandler());

        // Packet 디코더 - 패킷 처리
        //p.addLast("TCP.PacketDecoder", new PacketDecoder());

        // 계산식 핸들러
        //p.addLast("TCP.CalculateHandler", new CalculateHandler());
        p.addLast("TCP.PacketHandler", new PacketHandler());

        // 설정 핸들러
        p.addLast("TCP.SettingHandler", new SettingHandler());
        // 데이터 가공 처리
        //p.addLast("TCP.ProcessHandler", new ProcessHandler());
        p.addLast("TCP.ResultHandler", new ResultHandler());

        p.remove(this);

        ctx.fireChannelActive();
        ctx.fireChannelRead(packet);
    }
}