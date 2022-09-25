package com.ulalalab.snipe.infra.handler;

import com.ulalalab.snipe.infra.codec.PacketDecoder;
import com.ulalalab.snipe.infra.constant.ProtocolEnum;
import com.ulalalab.snipe.infra.manage.ChannelManager;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import lombok.extern.slf4j.Slf4j;
<<<<<<< HEAD
=======
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
>>>>>>> e9804688e6756cd7b5dabf9696af8a119a5a5914
import java.util.Set;

@Slf4j
public class ChoiceProtocolHandler extends ChannelInboundHandlerAdapter {

    private static Set<Channel> channelGroup = ChannelManager.getInstance();

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object packet) throws Exception {
        ByteBuf in = (ByteBuf) packet;

        final int first = in.getUnsignedByte(in.readerIndex());
        final int second = in.getUnsignedByte(in.readerIndex() + 1);

        if(this.isHttp(first, second)) {
            httpHandler(ctx);

<<<<<<< HEAD
            log.info("{} Http 연결 !!", ctx.channel().remoteAddress());
=======
            log.info(ctx.channel().remoteAddress() + " Http 연결 !!");
>>>>>>> e9804688e6756cd7b5dabf9696af8a119a5a5914
        } else {
            tcpHandler(ctx);

            channelGroup.add(ctx.channel());
<<<<<<< HEAD
            log.info("{} 연결 !! / 연결 갯수 : {}", ctx.channel().remoteAddress(), channelGroup.size());
=======
            log.info(ctx.channel().remoteAddress() + " 연결 !! / 연결 갯수 : " + channelGroup.size());
>>>>>>> e9804688e6756cd7b5dabf9696af8a119a5a5914
        }
        ctx.fireChannelRead(packet);
    }

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
     */
    private void httpHandler(ChannelHandlerContext ctx) {
        ChannelPipeline p = ctx.pipeline();

        p.addLast("HTTP.DefaultHandler", new DefaultHandler(ProtocolEnum.HTTP));
        p.addLast("HTTP.HttpRequestDecoder", new HttpRequestDecoder());
        p.addLast("HTTP.HttpResponseEncoder", new HttpResponseEncoder());
        p.addLast("HTTP.HttpResponseHandler", new HttpResponseHandler());
        p.remove(this);
    }

    /**
     * TCP 핸들러 파이프라인
     */
    private void tcpHandler(ChannelHandlerContext ctx) {
        ChannelPipeline p = ctx.pipeline();

        // 기본 ( 연결 관련 )
        p.addLast("TCP.DefaultHandler", new DefaultHandler(ProtocolEnum.TCP));

        // Packet 디코더 - 패킷 처리
        p.addLast("TCP.PacketDecoder", new PacketDecoder());

        // 계산식 핸들러
        p.addLast("TCP.CaculateHandler", new CaculateHandler());

        // 데이터 가공 처리
        p.addLast("TCP.ProcessHandler", new ProcessHandler());

        p.remove(this);
    }

    /**
     * 관련 없는 파이프라인 삭제
     */
//    private void removePipeline(ChannelPipeline channelPipeline, String prefixName) {
//        channelPipeline.forEach(c -> {
//            if(!c.getKey().contains(prefixName)) {
//                channelPipeline.remove(c.getKey());
//                logger.info(c.getKey() + " 파이프라인 삭제 !!");
//            }
//        });
//    }
}