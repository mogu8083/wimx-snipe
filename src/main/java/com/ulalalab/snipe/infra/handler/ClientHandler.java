package com.ulalalab.snipe.infra.handler;

import com.ulalalab.snipe.device.model.DeviceCode;
import com.ulalalab.snipe.infra.util.ByteUtils;
import com.ulalalab.snipe.infra.util.DevUtils;
import com.ulalalab.snipe.infra.util.LocalDateUtils;
import com.ulalalab.snipe.infra.util.RandomUtils;
import com.ulalalab.snipe.server.ClientServer;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.buffer.UnpooledHeapByteBuf;
import io.netty.channel.*;
import lombok.extern.slf4j.Slf4j;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.TimeZone;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Slf4j(topic = "CLIENT.ClientHandler")
public class ClientHandler extends ChannelInboundHandlerAdapter {
	private int deviceId;
	private String deviceSuffix;
	private String deviceName;
	private ClientServer clientServer;
	//private ByteBuf buffer;
	private short transactionId = 1;

	public ClientHandler(int deviceId, ClientServer clientServer) {
		this.clientServer = clientServer;
		this.deviceId = deviceId;
		//this.deviceSuffix = System.getProperty("device.suffix");
		//this.deviceName = "WX-" + deviceId + deviceSuffix;
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		log.info("클라이언트 연결 Deivce Index : {}", deviceId);

		this.sendInit(ctx);

		ctx.executor().scheduleAtFixedRate(() -> {
			//CompletableFuture.runAsync(() -> {
				//sendPacket(ctx);
			//if(ctx.channel().isOpen()) {
			if(ctx.channel().isWritable()) {
				sendPackData(ctx);
			}
			//}
			//});
		}, 1000, 1000, TimeUnit.MILLISECONDS);
		//channelRead(ctx, null);
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		ByteBuf buf = (ByteBuf) msg;
		//buffer.writeBytes(buf);
		//buf.clear();
		//buf.release();

		log.info("Receive -> " + ByteBufUtil.prettyHexDump(buf));

		if(msg !=null) {
			if(DevUtils.isPrint2(deviceId)) {
				if(buf.getByte(4) == 0x04) {
					log.info("재부팅 Response -> " + ByteBufUtil.hexDump(buf));
				} else {
					log.info("Response -> " + ByteBufUtil.hexDump(buf));
				}
			}
			buf.clear();
		}
		//buffer.clear();

		//buf.release();
		//sendPacket(ctx);
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		log.warn("클라이언트 연결 해제 deivce Index : {}", deviceId);

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

	private void sendPacket(ChannelHandlerContext ctx) {
		//ByteBuf buf = ctx.alloc().heapBuffer();
		//if (ctx.channel().isWritable()) {

		ByteBuf buffer = Unpooled.buffer();
			Random random = new Random();
			int s = random.nextInt();

			buffer.writeByte(0x02);

			String device = deviceName;

			buffer.writeBytes(ByteUtils.convertIntToByteArray(device.getBytes(StandardCharsets.UTF_8).length));
			buffer.writeBytes(device.getBytes(Charset.defaultCharset()));

			long ss = System.currentTimeMillis();

			buffer.writeBytes(ByteUtils.convertLongToByteArray(ss));

			double d = Math.round(Math.random() * 100 * 10) / 10.0;
			buffer.writeBytes(ByteUtils.convertDoubleToByteArray(d));

			d = Math.round(Math.random() * 100 * 10) / 10.0;
			buffer.writeBytes(ByteUtils.convertDoubleToByteArray(d));

			d = Math.round(Math.random() * 100 * 10) / 10.0;
			buffer.writeBytes(ByteUtils.convertDoubleToByteArray(d));

			d = Math.round(Math.random() * 100 * 10) / 10.0;
			buffer.writeBytes(ByteUtils.convertDoubleToByteArray(d));

			d = Math.round(Math.random() * 100 * 10) / 10.0;
			buffer.writeBytes(ByteUtils.convertDoubleToByteArray(d));
			buffer.writeByte(0x03);

			ChannelFuture future = ctx.writeAndFlush(buffer);
			buffer.clear();
//			if(future.isDone()) {
//				buffer.clear();
//				//buffer.release();
//			}
		//}
	}

	private void sendInit(ChannelHandlerContext ctx) {
		ByteBuf buf = Unpooled.buffer();

		buf.writeByte(0x16);
		buf.writeByte(0x16);

		buf.writeShort(0x0000);
		buf.writeByte(0x00);

		buf.writeInt(0);

		buf.writeShort(0);
		buf.writeShort(0);
		buf.writeShort(0);

		buf.writeInt(0);

		buf.writeByte(0);
		buf.writeShort(0);

		buf.writeShort(0);
		buf.writeByte(0xF5);

		if(DevUtils.isPrint2(deviceId)) {
			log.info("Init Message : {}", ByteBufUtil.hexDump(buf));
		}

		ctx.writeAndFlush(buf);
		buf.clear();
	}

	private void sendPackData(ChannelHandlerContext ctx) {
		ByteBuf buf = Unpooled.buffer();
		Random random = new Random();

		//if (ctx.channel().isWritable()) {
			try {
				Thread.sleep(1000);

				int channelCount = 4;

				// STX 0x16 0x16
				buf.writeByte(0x16);
				buf.writeByte(0x16);

				// Transaction ID (2 Byte)
				buf.writeShort(transactionId++);

				// Cmd (1 Byte)
				/* 0x01 : Data
				 * 0x02 : Write
				 * 0x03 : Update
				 * 0x04 : Reboot
				 * 0x05 : Interval
				 */
				buf.writeByte(0x01);

				// Timestamp
				int timestamp = (int) Instant.now().getEpochSecond();
				buf.writeInt(timestamp);

				// Device Code (2 Byte)
				buf.writeShort(DeviceCode.WICON_L.getCode());

				// TODO : Device 등록년월 (2 Byte)
				buf.writeByte(0x00);
				buf.writeByte(0x00);

				// Device Index (2 Byte)
				buf.writeShort(deviceId);

				// Version (4 Byte)
				buf.writeFloat(1.16f);

				// RSSI (1 Byte)
				buf.writeByte(0x00);

				// Data Length (2 Byte)
				int dataLength = Integer.BYTES * channelCount;
				buf.writeShort(dataLength);

				//////////채널 데이터//////////////////////////////////////
				for(int i = 0; i < channelCount; i++) {
					buf.writeFloat(RandomUtils.getFloatRandom(100, 5));
				}
				/////////////////////////////////////////////////////////

				// CRC (2 Byte)
				//buf.writeByte(ByteUtils.getCRC(buf.getBytes(2, buf, 20 + dataLength).array()));
				buf.writeByte(0x00);
				buf.writeByte(0x00);

				// ETC (1 Byte)
				buf.writeByte(0xF5);



				Thread.sleep(1000);

				channelCount = 4;

				// STX 0x16 0x16
				buf.writeByte(0x16);
				buf.writeByte(0x16);

				// Transaction ID (2 Byte)
				buf.writeShort(transactionId++);

				// Cmd (1 Byte)
				/* 0x01 : Data
				 * 0x02 : Write
				 * 0x03 : Update
				 * 0x04 : Reboot
				 * 0x05 : Interval
				 */
				buf.writeByte(0x01);

				// Timestamp
				timestamp = (int) Instant.now().getEpochSecond();
				buf.writeInt(timestamp);

				// Device Code (2 Byte)
				buf.writeShort(DeviceCode.WICON_L.getCode());

				// TODO : Device 등록년월 (2 Byte)
				buf.writeByte(0x00);
				buf.writeByte(0x00);

				// Device Index (2 Byte)
				buf.writeShort(deviceId);

				// Version (4 Byte)
				buf.writeFloat(1.17f);

				// RSSI (1 Byte)
				buf.writeByte(0x00);

				// Data Length (2 Byte)
				dataLength = Integer.BYTES * channelCount;
				buf.writeShort(dataLength);

				//////////채널 데이터//////////////////////////////////////
				for(int i = 0; i < channelCount; i++) {
					buf.writeFloat(RandomUtils.getFloatRandom(100, 5));
				}
				/////////////////////////////////////////////////////////

				// CRC (2 Byte)
				//buf.writeByte(ByteUtils.getCRC(buf.getBytes(2, buf, 20 + dataLength).array()));
				buf.writeByte(0x00);
				buf.writeByte(0x00);

				// ETC (1 Byte)
				buf.writeByte(0xF5);

				if(DevUtils.isPrint2(deviceId)) {
					log.info(ByteBufUtil.hexDump(buf));
				}
				ctx.writeAndFlush(buf);
				buf.clear();

//				if(future.isDone()) {
//					buf.clear();
//					//buf.release();
//					buf = null;
//				}
			} catch(Exception e) {
				e.printStackTrace();
				log.error(e.getMessage());
			}
		}
//	}
}