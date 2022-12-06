package com.ulalalab.snipe.infra.handler;

import com.ulalalab.snipe.infra.util.ByteUtils;
import com.ulalalab.snipe.infra.util.LocalDateUtils;
import com.ulalalab.snipe.infra.util.RandomUtils;
import com.ulalalab.snipe.server.ClientServer;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.Unpooled;
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
			//ByteBuf buf = PooledByteBufAllocator.DEFAULT.heapBuffer(65);
			ByteBuf buf = Unpooled.buffer(65);

			if(!ctx.channel().isOpen()) {
				break;
			}

			if(ctx.channel().isWritable()) {
				Thread.sleep(1000);

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

				int channelCount = 5;
				int channelTotalSize = Integer.BYTES * channelCount;

				// NEW

				// STX
				buf.writeByte(0x02);

				long sss = System.currentTimeMillis();
				buf.writeBytes(ByteUtils.convertLongToByteArray(sss));

				// Command
				buf.writeByte(0x11);

				// 채널 가변 ( 1 ~ * ) 총합 사이즈
				buf.writeBytes(ByteUtils.convertIntToByteArray(channelTotalSize));

				// 채널 가변 ( 1 ~ * )
				float f = 0.0F;
				for(int i=0; i > channelCount; i++) {
					buf.writeBytes(ByteUtils.convertFloatToByteArray(RandomUtils.getFloatRandom()));
				}
//				// STX
				buf.writeByte(0x03);

				LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(sss), TimeZone.getDefault().toZoneId());
				String time = LocalDateUtils.getLocalDateTimeString(localDateTime, LocalDateUtils.DATE_TIME_FORMAT);

				if(device.equals("WX-1A") || device.equals("WX-1Z")) {
					log.info("{} / time : {}", device, time);
				}
				ctx.writeAndFlush(buf);
				buf.clear();
			}
		}
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		log.warn("channelInactive");
		log.warn("deviceName : {} -> 연결해제 !!!", deviceName);

//		final EventLoop eventLoop = ctx.channel().eventLoop();
//
//		eventLoop.scheduleAtFixedRate(() -> {
//			try {
//				ChannelFuture future = clientServer.bootstrapConnect(deviceId);
//
//				if (future != null) {
//					eventLoop.shutdownGracefully();
//					eventLoop.terminationFuture();
//				}
//			} catch (Exception e) {
//				log.warn(e.getMessage());
//			}
//		}, 3L, 5L, TimeUnit.SECONDS);
	}

	@Override
	public void channelUnregistered(final ChannelHandlerContext ctx) throws Exception {

	}
}