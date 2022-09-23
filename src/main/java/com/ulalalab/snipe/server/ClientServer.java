package com.ulalalab.snipe.server;

import com.ulalalab.snipe.infra.util.ByteUtils;
import com.ulalalab.snipe.infra.util.RandomUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Random;

@Component
@Slf4j
@Profile("client")
public class ClientServer {

	@Value("${netty.tcp-port}")
	private int tcpPort;

	@PostConstruct
	public void start() throws InterruptedException {
		try {

			log.info("ClientServer 실행");

			int threadCnt = 500;

			EventLoopGroup group = new NioEventLoopGroup(threadCnt);
			ChannelFuture channelFuture;
			Channel channel;
			ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

			for(int x=1; x<(threadCnt+1); x++) {
				Bootstrap bootstrap = new Bootstrap();
				bootstrap.group(group)
						.channel(NioSocketChannel.class)
						.handler(new ClientHandler(x));

				channel = bootstrap.connect("127.0.0.1", tcpPort).sync().channel();
				channelGroup.add(channel);
				log.info("channelGroup.size() : " + channelGroup.size());
			}
		} catch(Exception e) {
			log.error(this.getClass() + " 연결 실패 => " + e.getMessage());

			Thread.sleep(5000);
			this.start();
		}
	}
}


class ClientHandler extends ChannelInboundHandlerAdapter {

	private static final Logger logger = LoggerFactory.getLogger(ClientHandler.class);
	private int deviceId;

	public ClientHandler(int deviceId) {
		this.deviceId = deviceId;
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		int index = 0;
		int end = 100000000;

		do {
			if(ctx.channel().isWritable()) {
				ByteBuf buf = PooledByteBufAllocator.DEFAULT.heapBuffer(65);

				Thread.sleep(1000);

				Random random = new Random();
				int s = random.nextInt();

				boolean suffix = false;

				buf.writeByte(0x02);

				String device = ("WX-") + deviceId + (suffix ? + RandomUtils.getNumberRandom(99) : "");
				buf.writeBytes(ByteUtils.convertIntToByteArray(device.getBytes(StandardCharsets.UTF_8).length));
				buf.writeBytes(device.getBytes(Charset.defaultCharset()));

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

//				s = random.nextInt();
//
//				buf.writeByte(0x02);
//				buf.writeBytes(ByteUtils.convertIntToByteArray(device.getBytes(StandardCharsets.UTF_8).length));
//				buf.writeBytes(device.getBytes(Charset.defaultCharset()));
//
//				d = Math.round(Math.random() * 100 * 10) / 10.0;
//				buf.writeBytes(ByteUtils.convertDoubleToByteArray(d));
//
//				d = Math.round(Math.random() * 100 * 10) / 10.0;
//				buf.writeBytes(ByteUtils.convertDoubleToByteArray(d));
//
//				d = Math.round(Math.random() * 100 * 10) / 10.0;
//				buf.writeBytes(ByteUtils.convertDoubleToByteArray(d));
//
//				d = Math.round(Math.random() * 100 * 10) / 10.0;
//				buf.writeBytes(ByteUtils.convertDoubleToByteArray(d));
//
//				d = Math.round(Math.random() * 100 * 10) / 10.0;
//				buf.writeBytes(ByteUtils.convertDoubleToByteArray(d));
//				buf.writeByte(0x03);
//
//				s = random.nextInt();
//
//				buf.writeByte(0x02);
//				buf.writeBytes(ByteUtils.convertIntToByteArray(device.getBytes(StandardCharsets.UTF_8).length));
//				buf.writeBytes(device.getBytes(Charset.defaultCharset()));
//
//				d = Math.round(Math.random() * 100 * 10) / 10.0;
//				buf.writeBytes(ByteUtils.convertDoubleToByteArray(d));
//
//				d = Math.round(Math.random() * 100 * 10) / 10.0;
//				buf.writeBytes(ByteUtils.convertDoubleToByteArray(d));
//
//				d = Math.round(Math.random() * 100 * 10) / 10.0;
//				buf.writeBytes(ByteUtils.convertDoubleToByteArray(d));
//
//				d = Math.round(Math.random() * 100 * 10) / 10.0;
//				buf.writeBytes(ByteUtils.convertDoubleToByteArray(d));
//
//				d = Math.round(Math.random() * 100 * 10) / 10.0;
//				buf.writeBytes(ByteUtils.convertDoubleToByteArray(d));
//				buf.writeByte(0x03);

//				StringBuffer sb = new StringBuffer();
//
//				for (int i = 0; i < buf.readableBytes(); i++) {
//					sb.append(ByteUtils.byteToHexString(buf.getByte(i)) + " ");
//				}
				//logger.info("HEX : " + sb.toString());
				ctx.writeAndFlush(buf);
				buf.clear();
			}
		} while (index++ < end);
	}
}