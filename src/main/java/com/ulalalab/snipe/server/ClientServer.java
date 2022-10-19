package com.ulalalab.snipe.server;

import com.ulalalab.snipe.infra.handler.ClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.util.NumberUtils;
import javax.annotation.PostConstruct;

@Component
@Slf4j(topic = "CLIENT.ClientServer")
@Profile({"local-client", "dev-client"})
public class ClientServer {

	@Value("#{systemProperties['tcp.ip']}")
	private String TCP_IP;

	@Value("#{systemProperties['tcp.port']}")
	private String TCP_PORT;

	@Value("#{systemProperties['thread.count']}")
	private String THREAD_COUNT;

	private int threadCnt;

	public EventLoopGroup eventLoopGroup;


	@PostConstruct
	public void run() throws Exception {
		try {
			this.threadCnt = NumberUtils.parseNumber(THREAD_COUNT, Integer.class);
			EventLoopGroup eventLoopGroup = new NioEventLoopGroup(1);

			log.info("ClientServer 실행 / Thread : {} 실행", threadCnt);

			for(int i = 1; i < threadCnt + 1; i++) {
				this.bootstrapConnect(i);
			}
		} catch(Exception e) {
			e.printStackTrace();
			log.error("{} 연결 실패 => {}", this.getClass(), e.getMessage());
		}
	}

	public ChannelFuture bootstrapConnect(int deviceId) throws Exception {
		ChannelFuture channelFuture = null;

		try {
			Bootstrap bootstrap = new Bootstrap();
			EventLoopGroup eventLoopGroup = new NioEventLoopGroup(1);

			bootstrap.group(eventLoopGroup)
					.channel(NioSocketChannel.class)
					.handler(new LoggingHandler(LogLevel.DEBUG))
					//.option(ChannelOption.SO_KEEPALIVE, true)
					//.option(ChannelOption.TCP_NODELAY, true)
					.handler(new ClientHandler(deviceId, this));
//					.handler(new ChannelInitializer<SocketChannel>() {
//					@Override
//					public void initChannel(SocketChannel ch) throws Exception {
//						ch.pipeline().addLast(new ClientHandler(this, 1));
//					}
//				});
			bootstrap.remoteAddress(TCP_IP, NumberUtils.parseNumber(TCP_PORT, Integer.class));
			channelFuture = bootstrap.connect().sync().channel().closeFuture();
		} catch(Exception e) {
			log.error("{} 연결 실패 => {}", this.getClass(), e.getMessage());
		}
		return channelFuture;
	}
}