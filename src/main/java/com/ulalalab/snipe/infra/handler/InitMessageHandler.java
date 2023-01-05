package com.ulalalab.snipe.infra.handler;

import com.ulalalab.snipe.infra.util.ByteUtils;
import com.ulalalab.snipe.infra.util.CRC16ModubusUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
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
		ByteBuf clientBuf = Unpooled.buffer(60);

		// 초기 데이터 인지 확인
		// TODO : 장비 시리얼이 맞는지 확인, 인증이 맞는지 확인
		boolean isDevieAuth = false;

		if(buf.getByte(4)==0x00) {
			if(buf.getShort(0) == 0x1616) {
				int receiveCRC = buf.getUnsignedShort(22);
				int checkCRC = CRC16ModubusUtils.calc(ByteBufUtil.getBytes(buf, 0, 22));

				if(receiveCRC != checkCRC) {
					log.warn("CRC 일치 하지 않음.");
					log.warn(ByteBufUtil.prettyHexDump(buf));
				} else {
					clientBuf.writeByte(0x16);
					clientBuf.writeByte(0x16);
					clientBuf.writeShort(0x0000);
					clientBuf.writeByte(0x00);

					clientBuf.writeInt((int) Instant.now().getEpochSecond());
					clientBuf.writeByte(0x00);

					clientBuf.writeShort(CRC16ModubusUtils.calc(ByteBufUtil.getBytes(clientBuf, 0, clientBuf.writerIndex())));
					clientBuf.writeByte(0xF5);

					ctx.writeAndFlush(clientBuf);
					clientBuf.unwrap();

					isDevieAuth = true;
				}
			}
		}

		if(isDevieAuth) {
			ctx.channel().pipeline().remove(this);
			log.info("{} -> 장비 인증 성공11", ctx.channel().id());

			ctx.fireChannelRead(buf);
		} else {
			log.warn("{} -> 장비 인증 실패, 연결 해제", ctx.channel().id());
			ctx.close();
			buf.release();
		}
	}
}