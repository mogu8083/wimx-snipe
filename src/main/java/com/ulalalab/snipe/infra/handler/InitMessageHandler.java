package com.ulalalab.snipe.infra.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.time.Instant;

@Component
@ChannelHandler.Sharable
@Slf4j(topic = "TCP.InitMessageHandler")
public class InitMessageHandler extends ChannelInboundHandlerAdapter {

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object packet) {
		ByteBuf buf = (ByteBuf) packet;
		ByteBuf clientBuf = Unpooled.buffer(13);

		// 초기 데이터 인지 확인
		// TODO : 장비 시리얼이 맞는지 확인, 인증이 맞는지 확인
		boolean isDevieAuth = false;

		if(buf.getByte(4)==0x00) {
			if(buf.getShort(0) == 0x1616) {
				clientBuf.writeByte(0x16);
				clientBuf.writeByte(0x16);
				clientBuf.writeShort(0x0000);
				clientBuf.writeByte(0x00);

				clientBuf.writeInt((int) Instant.now().getEpochSecond());
				clientBuf.writeByte(0x00);
				clientBuf.writeByte(0x00);
				clientBuf.writeByte(0x00);
				clientBuf.writeByte(0xF5);

				ctx.writeAndFlush(clientBuf);
				clientBuf.unwrap();

				isDevieAuth = true;
			}
		}

		if(isDevieAuth) {
			ctx.channel().pipeline().remove(this);
			ctx.fireChannelRead(buf);
		} else {
			ctx.close();
		}
	}
}