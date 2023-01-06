package com.ulalalab.snipe.infra.handler;

import com.ulalalab.snipe.infra.util.CRC16ModubusUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.time.Instant;

@Component
@ChannelHandler.Sharable
@Slf4j(topic = "TCP.ResponseWriteHandler")
public class ResponseWriteHandler extends ChannelInboundHandlerAdapter {

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		ByteBuf clientBuf = Unpooled.buffer(14);
		short transactionId = (short) evt;

		clientBuf.writeByte(0x16);
		clientBuf.writeByte(0x16);
		clientBuf.writeShort(transactionId);
		clientBuf.writeByte(0x01);
		clientBuf.writeInt((int) Instant.now().getEpochSecond());
		clientBuf.writeByte(0x00);
		clientBuf.writeByte(0x00);
		clientBuf.writeShort(CRC16ModubusUtils.calc(ByteBufUtil.getBytes(clientBuf, 0, clientBuf.writerIndex())));
		clientBuf.writeByte(0xF5);

		ctx.channel().writeAndFlush(clientBuf);

		clientBuf.clear();
		clientBuf.unwrap();
	}
}