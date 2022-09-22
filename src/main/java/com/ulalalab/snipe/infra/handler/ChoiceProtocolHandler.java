package com.ulalalab.snipe.infra.handler;

import com.ulalalab.snipe.infra.codec.PacketDecoder;
import com.ulalalab.snipe.infra.util.BeansUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

public class ChoiceProtocolHandler extends ChannelInboundHandlerAdapter {

    private final static Logger logger = LoggerFactory.getLogger(ChoiceProtocolHandler.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object packet) {
        ByteBuf in = (ByteBuf) packet;

        final int first = in.getUnsignedByte(in.readerIndex());
        final int second = in.getUnsignedByte(in.readerIndex() + 1);

        if(this.isHttp(first, second)) {
            httpHandler(ctx);
        } else {
            tcpHandler(ctx);
        }
        ctx.fireChannelRead(packet);
    }

//    @Override
//    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
//        final int first = in.getUnsignedByte(in.readerIndex());
//        final int second = in.getUnsignedByte(in.readerIndex() + 1);
//
//        //logger.info("first : "  + first + " / second : " + second);
//
//        if(this.isHttp(first, second)) {
//            httpHandler(ctx);
//        } else {
//            tcpHandler(ctx);
//        }
//    }
//
    /**
     * Http 프로토콜 Check
     * @param first 1 Index Byte
     * @param second 2 Index Byte
     * @return True / False
     */
    private boolean isHttp(int first, int second) {
        return
                first == 'G' && second == 'E' ||    // GET
                first == 'P' && second == 'O' ||    // POST
                first == 'P' && second == 'U' ||    // PUT
                first == 'P' && second == 'A' ||    // PATCH
                first == 'D' && second == 'E';      // DELETE
    }

    /**
     * Http 핸들러 파이프라인
     * @param ctx
     */
    private void httpHandler(ChannelHandlerContext ctx) {
//        ChannelPipeline p = ctx.pipeline();
//        p.addLast(new HttpRequestDecoder());
//        p.addLast(new HttpResponseEncoder());
//        p.addLast(new HttpResponseHandler());
//        p.remove(this);

        ChannelPipeline p = ctx.pipeline();
        this.removePipeline(p, ProtocolType.HTTP.name());
    }

    private void tcpHandler(ChannelHandlerContext ctx) {
        ChannelPipeline p = ctx.pipeline();
        this.removePipeline(p, ProtocolType.TCP.name());
    }

    /**
     * 관련 없는 파이프라인 삭제
     */
    private void removePipeline(ChannelPipeline channelPipeline, String prefixName) {
        channelPipeline.forEach(c -> {
            if(!c.getKey().contains(prefixName)) {
                channelPipeline.remove(c.getKey());
                logger.info(c.getKey() + " 파이프라인 삭제 !!");
            }
        });
    }

    private enum ProtocolType {
        TCP("TCP"), HTTP("HTTP"), MQTT("MQTT");

        private String protocol;

        ProtocolType(String protocol) {
            this.protocol = protocol;
        }
    }
}