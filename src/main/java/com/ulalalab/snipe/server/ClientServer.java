package com.ulalalab.snipe.server;

import com.ulalalab.snipe.infra.listener.ConnectionListener;
import com.ulalalab.snipe.infra.util.ByteUtils;
import com.ulalalab.snipe.infra.util.LocalDateUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.util.NumberUtils;
import javax.annotation.PostConstruct;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
@Profile({"local-client", "dev-client"})
public class ClientServer {

	@Value("#{systemProperties['tcp.ip']}")
	private String TCP_IP;

	@Value("#{systemProperties['tcp.port']}")
	private String TCP_PORT;

	@Value("#{systemProperties['thread.count']}")
	private String THREAD_COUNT;

	private int threadCnt;

	EventLoopGroup eventLoopGroup;


	@PostConstruct
	public void run() throws Exception {
		try {
			this.threadCnt = NumberUtils.parseNumber(THREAD_COUNT, Integer.class);
			this.eventLoopGroup = new NioEventLoopGroup(threadCnt);

			log.info("ClientServer 실행 / Thread : {} 실행", threadCnt);

			for(int i=1; i<threadCnt+1; i++) {
				this.createBootstrap(new Bootstrap(), eventLoopGroup, i);
			}
		} catch(Exception e) {
			e.printStackTrace();
			log.error("{} 연결 실패 => {}", this.getClass(), e.getMessage());
		}
	}

	public Bootstrap createBootstrap(Bootstrap bootstrap, EventLoopGroup eventLoop, Integer deviceId) throws InterruptedException {
		ChannelFuture channelFuture;
		Channel channel;

		bootstrap.group(eventLoopGroup)
				.channel(NioSocketChannel.class)
				.handler(new LoggingHandler(LogLevel.INFO))
				.option(ChannelOption.SO_KEEPALIVE, true)
				.option(ChannelOption.TCP_NODELAY, true)
				.handler(new ClientHandler(this, deviceId));
//				.handler(new ChannelInitializer<SocketChannel>() {
//					@Override
//					public void initChannel(SocketChannel ch) throws Exception {
//						ch.pipeline().addLast(new ClientHandler(this, 1));
//					}
//				});
		bootstrap.remoteAddress(TCP_IP, NumberUtils.parseNumber(TCP_PORT, Integer.class));
		bootstrap.connect().addListener(new ConnectionListener(this, deviceId));

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
		int end = 1000000000;

		while (index++ < end) {
			if(ctx.channel().isWritable()) {
				Thread.sleep(1000);

				ByteBuf buf = PooledByteBufAllocator.DEFAULT.heapBuffer(65);

				Random random = new Random();
				int s = random.nextInt();

				buf.writeByte(0x02);

				String device = ("WX-") + deviceId + deviceSuffix;
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

//				s = random.nextInt();

//				StringBuffer sb = new StringBuffer();

//				for (int i = 0; i < buf.readableBytes(); i++) {
//					sb.append(ByteUtils.byteToHexString(buf.getByte(i)) + " ");
//				}
				//log.info("HEX : {}", sb.toString());

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

//		log.error("{} 연결해제 !!!", ctx.channel().remoteAddress());

//		final EventLoop eventLoop = ctx.channel().eventLoop();
//		eventLoop.schedule(new Runnable() {
//
//			@Override
//			public void run() {
//				try {
//					clientServer.createBootstrap(new Bootstrap(), eventLoop, deviceId);
//				} catch (InterruptedException e) {
//					log.error(e.getMessage());
//					throw new RuntimeException(e);
//				}
//			}
//		}, 1L, TimeUnit.SECONDS);
//		super.channelInactive(ctx);
	}
}
