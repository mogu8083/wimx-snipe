package com.ulalalab.snipe.server;

import com.ulalalab.snipe.infra.listener.ConnectionListener;
import com.ulalalab.snipe.infra.util.ByteUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
<<<<<<< HEAD
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;
=======
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
>>>>>>> e9804688e6756cd7b5dabf9696af8a119a5a5914
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.util.NumberUtils;
import javax.annotation.PostConstruct;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
@Profile("client")
public class ClientServer {

<<<<<<< HEAD
	@Value("#{systemProperties['tcp.ip']}")
	private String TCP_IP;

	@Value("#{systemProperties['tcp.port']}")
	private String TCP_PORT;

	@Value("#{systemProperties['thread.count']}")
	private String THREAD_COUNT;

	private int threadCnt;

	EventLoopGroup eventLoopGroup;

=======
	@Value("${netty.tcp-port}")
	private int tcpPort;
>>>>>>> e9804688e6756cd7b5dabf9696af8a119a5a5914

	@PostConstruct
	public void run() throws Exception {
		try {
<<<<<<< HEAD
			this.threadCnt = NumberUtils.parseNumber(THREAD_COUNT, Integer.class);
			this.eventLoopGroup = new NioEventLoopGroup(threadCnt);

			log.info("ClientServer 실행 / Thread : {} 실행", threadCnt);

			for(int i=0; i<threadCnt; i++) {
				this.createBootstrap(new Bootstrap(), eventLoopGroup);
			}
		} catch(Exception e) {
			log.error("{} 연결 실패 => {}", this.getClass(), e.getMessage());
=======

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
>>>>>>> e9804688e6756cd7b5dabf9696af8a119a5a5914
		}
	}

	public Bootstrap createBootstrap(Bootstrap bootstrap, EventLoopGroup eventLoop) throws InterruptedException {
		ChannelFuture channelFuture;
		Channel channel;

		bootstrap.group(eventLoopGroup)
				.channel(NioSocketChannel.class)
				.handler(new LoggingHandler(LogLevel.INFO))
				.option(ChannelOption.SO_KEEPALIVE, true)
				.handler(new ClientHandler(this, 1));
//				.handler(new ChannelInitializer<SocketChannel>() {
//					@Override
//					public void initChannel(SocketChannel ch) throws Exception {
//						ch.pipeline().addLast(new ClientHandler(this, 1));
//					}
//				});
		bootstrap.remoteAddress(TCP_IP, NumberUtils.parseNumber(TCP_PORT, Integer.class));
		bootstrap.connect().addListener(new ConnectionListener(this));

		return bootstrap;
	}
}

@Slf4j(topic = "CLIENT.clientHandler")
class ClientHandler extends ChannelInboundHandlerAdapter {

	private int deviceId;

	private ClientServer clientServer;

	public ClientHandler(ClientServer clientServer, int deviceId) {
		this.clientServer = clientServer;
		this.deviceId = deviceId;
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		String deviceSuffix = System.getProperty("device.suffix");

		int index = 0;
		int end = 100000000;

		do {
			if(ctx.channel().isWritable()) {
				ByteBuf buf = PooledByteBufAllocator.DEFAULT.heapBuffer(65);

				Thread.sleep(1000);

				Random random = new Random();
				int s = random.nextInt();

				buf.writeByte(0x02);

				String device = ("WX-") + deviceId + deviceSuffix;
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

<<<<<<< HEAD
				StringBuffer sb = new StringBuffer();

				for (int i = 0; i < buf.readableBytes(); i++) {
					sb.append(ByteUtils.byteToHexString(buf.getByte(i)) + " ");
				}
				log.info("HEX : {}", sb.toString());
=======
//				StringBuffer sb = new StringBuffer();
//
//				for (int i = 0; i < buf.readableBytes(); i++) {
//					sb.append(ByteUtils.byteToHexString(buf.getByte(i)) + " ");
//				}
				//logger.info("HEX : " + sb.toString());
>>>>>>> e9804688e6756cd7b5dabf9696af8a119a5a5914
				ctx.writeAndFlush(buf);
				buf.clear();
			}
		} while (index++ < end);
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {

		log.error("{} 연결해제 !!!", ctx.channel().remoteAddress());

		final EventLoop eventLoop = ctx.channel().eventLoop();
		eventLoop.schedule(new Runnable() {

			@Override
			public void run() {
				try {
					clientServer.createBootstrap(new Bootstrap(), eventLoop);
				} catch (InterruptedException e) {
					log.error(e.getMessage());
					throw new RuntimeException(e);
				}
			}
		}, 1L, TimeUnit.SECONDS);
		super.channelInactive(ctx);
	}
}