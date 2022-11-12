package com.ulalalab.snipe.infra.handler;

import com.ulalalab.snipe.infra.constant.ProtocolEnum;
import com.ulalalab.snipe.infra.manage.ChannelManager;
import com.ulalalab.snipe.infra.util.ByteUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;

@Slf4j
public class TestHandler extends ChannelInboundHandlerAdapter {

	private ByteBuf buffer;

	@Override
	public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
		buffer = ctx.alloc().buffer(1024);
	}

	@Override
	public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
		buffer.release();
		buffer = null;
	}

	@Override
	public void channelRead(@NotNull ChannelHandlerContext ctx, @NotNull Object msg) throws Exception {

		ByteBuf in = (ByteBuf) msg;
		buffer.writeBytes(in);
		in.release();

		while(true) {
			if(buffer.readableBytes() > 0 && buffer.getByte(0)==0x02) {

				//if(buffer.getByte(buffer.readableBytes()-1)==0x03) {
				if(buffer.indexOf(buffer.readerIndex(), buffer.readerIndex() + buffer.readableBytes(), (byte) 0x03) > -1) {
					try {
						int readerIndex = buffer.readerIndex();
						int readableBytes = buffer.readableBytes();

						buffer.readByte();
						int deviceSize = buffer.readInt();

						String deviceId = buffer.toString(buffer.readerIndex(), deviceSize, StandardCharsets.UTF_8);
						buffer.readBytes(deviceSize);

						Long time = buffer.readLong();
						//LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(time), TimeZone.getDefault().toZoneId());

						log.info("Receive HEX : {}", ByteUtils.byteBufToHexString(buffer, readerIndex, readableBytes + readerIndex));

						Double ch1 = buffer.readDouble();
						Double ch2 = buffer.readDouble();
						Double ch3 = buffer.readDouble();
						Double ch4 = buffer.readDouble();
						Double ch5 = buffer.readDouble();

						log.info(deviceId);

						// 0x03
						buffer.readByte();

						buffer.slice();
						//buffer.clear();
					} catch (Exception e) {
						e.printStackTrace();
						log.error(e.getMessage());
						int index = buffer.indexOf(buffer.readerIndex(), buffer.readerIndex() + buffer.readableBytes(), (byte) 0x02);

						if(index > -1) {
							//buffer.slice(index, buffer.readerIndex() + buffer.readableBytes());
							buffer.readerIndex(index);
						} else {
							buffer.clear();
						}
					}
				} else {
					break;
				}
			} else {
				int index = buffer.indexOf(buffer.readerIndex(), buffer.readerIndex() + buffer.readableBytes(), (byte) 0x02);

				if(index > -1) {
					buffer.slice(index, buffer.readerIndex() + buffer.readableBytes());
				} else {
					buffer.clear();
				}
				break;
			}
		}
		log.info("남은 HEX : {}", ByteUtils.byteBufToHexString(buffer, buffer.readerIndex(), buffer.readableBytes() + buffer.readerIndex()));
	}
}