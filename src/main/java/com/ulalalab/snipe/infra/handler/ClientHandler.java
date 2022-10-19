package com.ulalalab.snipe.infra.handler;

import com.ulalalab.snipe.infra.util.ByteUtils;
import com.ulalalab.snipe.infra.util.LocalDateUtils;
import com.ulalalab.snipe.server.ClientServer;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.EventLoop;
import lombok.extern.slf4j.Slf4j;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

@Slf4j(topic = "CLIENT.ClientHandler")
public
class ClientHandler extends ChannelInboundHandlerAdapter {
	private int deviceId;
	private String deviceSuffix;
	private String deviceName;
	private ClientServer clientServer;

	public ClientHandler(int deviceId, ClientServer clientServer) {
		this.clientServer = clientServer;
		this.deviceId = deviceId;
		this.deviceSuffix = System.getProperty("device.suffix");
		this.deviceName = "WX-" + deviceId + deviceSuffix;
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		log.info("{} -> 클라이언트 연결", deviceName);

		while (true) {
			if(!ctx.channel().isOpen()) {
				break;
			}

			if(ctx.channel().isWritable()) {
				Thread.sleep(1000);

				ByteBuf buf = PooledByteBufAllocator.DEFAULT.heapBuffer(65);

				Random random = new Random();
				int s = random.nextInt();

				buf.writeByte(0x02);

				String device = deviceName;
				buf.writeBytes(ByteUtils.convertIntToByteArray(device.getBytes(StandardCharsets.UTF_8).length));
				buf.writeBytes(device.getBytes(Charset.defaultCharset()));

				long ss = System.currentTimeMillis();

				buf.writeBytes(ByteUtils.convertLongToByteArray(ss));

				double d = Math.round(Math.random() * 100 * 10) / 10.0;
				buf.writeBytes(ByteUtils.convertDoubleToByteArray(d));

				d = Math.round(Math.random() * 100 * 10) / 10.0;
				buf.writeBytes(ByteUtils.convertDoubleToByteArray(d));

				d = Math.round(Math.random() * 100 * 10) / 10.0;
				buf.writeBytes(ByteUtils.convertDoubleToByteArray(d));

				d = Math.round(Math.random() * 100 * 10) / 10.0;
				buf.writeBytes(ByteUtils.convertDoubleToByteArray(d));

				d = Math.round(Math.random() * 100 * 10) / 10.0;
				buf.writeBytes(ByteUtils.convertDoubleToByteArray(d));
				buf.writeByte(0x03);

				StringBuffer sb = new StringBuffer();

//				for (int i = 0; i < buf.readableBytes(); i++) {
//					sb.append(ByteUtils.byteToHexString(buf.getByte(i)) + " ");
//				}
//				log.info("HEX : {}", sb.toString());

				LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(ss), TimeZone.getDefault().toZoneId());
				String time = LocalDateUtils.getLocalDateTimeString(localDateTime, LocalDateUtils.DATE_TIME_FORMAT);

				log.info("{} / time : {}", device, time);
				ctx.writeAndFlush(buf);
				buf.clear();
			}
		}
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		log.warn("channelInactive");
		log.warn("deviceName : {} -> 연결해제 !!!", deviceName);

		final EventLoop eventLoop = ctx.channel().eventLoop();

		eventLoop.scheduleAtFixedRate(() -> {
			try {
				ChannelFuture future = clientServer.bootstrapConnect(deviceId);

				if (future != null) {
					eventLoop.shutdownGracefully();
					eventLoop.terminationFuture();
				}
			} catch (Exception e) {
				log.warn(e.getMessage());
			}
		}, 3L, 5L, TimeUnit.SECONDS);
	}

	@Override
	public void channelUnregistered(final ChannelHandlerContext ctx) throws Exception {

	}
}