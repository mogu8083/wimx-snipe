package com.ulalalab.snipe.server;

import com.ulalalab.snipe.common.codec.PacketDecoder;
import com.ulalalab.snipe.common.handler.*;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class TcpServer {

	private static final Logger logger = LoggerFactory.getLogger(TcpServer.class);

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
		logger.info("Tcp Server 실행");

		bossGroup = new NioEventLoopGroup(bossCount);
		workerGroup = new NioEventLoopGroup(workerCount);

		try {

			// 1. EventServer
			ServerBootstrap bootstrap = new ServerBootstrap();
			bootstrap.group(bossGroup, workerGroup)
					.channel(NioServerSocketChannel.class)
					.handler(new LoggingHandler(LogLevel.INFO))
					.childHandler(new ChannelInitializer<SocketChannel>() {

						@Override
						public void initChannel(SocketChannel ch) {
							ChannelPipeline p = ch.pipeline();

							// 기본 ( 연결 관련 )
							p.addLast(new DefaultHandler());

							// Packet 디코더
							p.addLast(new PacketDecoder());

							// 데이터 가공 처리
							p.addLast(processHandler);
						}
					});
			bootstrap.bind(tcpPort).sync().channel();
		} catch(Exception e) {
			logger.error(e.getMessage());

			workerGroup.shutdownGracefully().sync();
			bossGroup.shutdownGracefully().sync();

			Thread.sleep(5000);
			this.start();
		}
	}
}