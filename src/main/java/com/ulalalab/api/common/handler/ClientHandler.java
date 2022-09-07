package com.ulalalab.api.common.handler;

import com.ulalalab.api.common.util.ByteUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.nio.charset.Charset;
import java.util.Random;

public class ClientHandler extends ChannelInboundHandlerAdapter {

	private static final Logger logger = LoggerFactory.getLogger(ClientHandler.class);
	private int deviceId;

	public ClientHandler(int deviceId) {
		this.deviceId = deviceId;
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		// 1초마다 전송
//		scheduledFuture = ctx.executor().schedule(new Runnable() {
//
//			@Override
//			public void run() {
//				if(scheduledFuture!=null) {
//					Random random = new Random();
//					int s = random.nextInt();
//
//					ByteBuf buf = PooledByteBufAllocator.DEFAULT.buffer(65);
//
//					String device = ("WX-") + deviceId;
//					buf.writeBytes(ByteUtil.convertIntToByteArray(device.getBytes(Charset.defaultCharset()).length));
//					buf.writeBytes(device.getBytes(Charset.defaultCharset()));
//
//					double d = Math.round(Math.random() * 100 * 10) / 10.0;
//					buf.writeBytes(ByteUtil.convertDoubleToByteArray(d));
//
//					d = Math.round(Math.random() * 100 * 10) / 10.0;
//					buf.writeBytes(ByteUtil.convertDoubleToByteArray(d));
//
//					d = Math.round(Math.random() * 100 * 10) / 10.0;
//					buf.writeBytes(ByteUtil.convertDoubleToByteArray(d));
//
//					d = Math.round(Math.random() * 100 * 10) / 10.0;
//					buf.writeBytes(ByteUtil.convertDoubleToByteArray(d));
//
//					d = Math.round(Math.random() * 100 * 10) / 10.0;
//					buf.writeBytes(ByteUtil.convertDoubleToByteArray(d));
//
//					ctx.writeAndFlush(buf);
//					logger.info("##");
//					ctx.close();
//				}
//			}
//		}, 1000, TimeUnit.MILLISECONDS);

		while(true) {
			if(ctx.channel().isWritable()) {
				ByteBuf buf = PooledByteBufAllocator.DEFAULT.heapBuffer(50);

				Thread.sleep(500);

				Random random = new Random();
				int s = random.nextInt();

				String device = ("WX-") + deviceId;
				buf.writeBytes(ByteUtil.convertIntToByteArray(device.getBytes(Charset.defaultCharset()).length));
				buf.writeBytes(device.getBytes(Charset.defaultCharset()));

				double d = Math.round(Math.random() * 100 * 10) / 10.0;
				buf.writeBytes(ByteUtil.convertDoubleToByteArray(d));

				d = Math.round(Math.random() * 100 * 10) / 10.0;
				buf.writeBytes(ByteUtil.convertDoubleToByteArray(d));

				d = Math.round(Math.random() * 100 * 10) / 10.0;
				buf.writeBytes(ByteUtil.convertDoubleToByteArray(d));

				d = Math.round(Math.random() * 100 * 10) / 10.0;
				buf.writeBytes(ByteUtil.convertDoubleToByteArray(d));

				d = Math.round(Math.random() * 100 * 10) / 10.0;
				buf.writeBytes(ByteUtil.convertDoubleToByteArray(d));

				StringBuffer sb = new StringBuffer();

				for (int i = 0; i < buf.readableBytes(); i++) {
					sb.append(ByteUtil.byteToHexString(buf.getByte(i)) + " ");
				}

				logger.info("HEX : " + sb.toString());
				ctx.writeAndFlush(buf);
				buf.clear();
			} else {
				break;
			}
		}
	}
}