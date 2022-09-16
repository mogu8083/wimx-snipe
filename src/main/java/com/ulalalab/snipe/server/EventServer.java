package com.ulalalab.snipe.server;

import com.ulalalab.snipe.common.codec.PacketDecoder;
import com.ulalalab.snipe.common.handler.*;
import com.ulalalab.snipe.common.instance.GlobalInstance;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class EventServer {

	private static final Logger logger = LoggerFactory.getLogger(EventServer.class);

	@Autowired
	private ProcessHandler processHandler;

	@Value("${netty.tcp-port}")
	private int tcpPort;

	@Value("${netty.http-port}")
	private int httpPort;

	@Value("${netty.boss-count}")
	private int bossCount;

	@Value("${netty.worker-count}")
	private int workerCount;

	EventLoopGroup bossGroup;
	EventLoopGroup workerGroup;

	public void start() throws InterruptedException {
		logger.info("Event Server / Http Server 실행");

		bossGroup = new NioEventLoopGroup(bossCount);
		workerGroup = new NioEventLoopGroup(workerCount);

		try {
			GlobalInstance.eventServerFlag = true;

			// 1. EventServer
			ServerBootstrap eventBootstrap = new ServerBootstrap();
			eventBootstrap.group(bossGroup, workerGroup)
					.channel(NioServerSocketChannel.class)
					.handler(new LoggingHandler(LogLevel.INFO))
					.childHandler(new ChannelInitializer<SocketChannel>() {

						@Override
						public void initChannel(SocketChannel ch) {
							ChannelPipeline p = ch.pipeline();

							// Packet 디코더
							p.addLast(new PacketDecoder());

							// 기본 ( 연결 관련 )
							p.addLast(new DefaultHandler());

							// 데이터 가공 처리
							p.addLast(processHandler);
						}
					});
			eventBootstrap.bind(tcpPort).sync().channel();

			// 2. HttpServer
			ServerBootstrap httpBootstrap = new ServerBootstrap();

			bossGroup = new NioEventLoopGroup(bossCount);
			workerGroup = new NioEventLoopGroup(workerCount);

			httpBootstrap.group(bossGroup, workerGroup)
					.channel(NioServerSocketChannel.class)
					.handler(new LoggingHandler(LogLevel.INFO))
					.childHandler(new ChannelInitializer<SocketChannel>() {

						@Override
						public void initChannel(SocketChannel ch) {
							ChannelPipeline p = ch.pipeline();
							p.addLast(new HttpRequestDecoder());
							p.addLast(new HttpResponseEncoder());
							p.addLast(new HttpResponseHandler());
						}
					});
			httpBootstrap.bind(httpPort).sync().channel().closeFuture().sync();

		} catch(Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} finally {
			workerGroup.shutdownGracefully().sync();
			bossGroup.shutdownGracefully().sync();
			GlobalInstance.eventServerFlag = false;

			Thread.sleep(5000);
			this.start();
		}
	}
}