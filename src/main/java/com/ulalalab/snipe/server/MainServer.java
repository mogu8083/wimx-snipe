package com.ulalalab.snipe.server;

import com.ulalalab.snipe.infra.handler.ChoiceProtocolHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MainServer {

//	@Autowired
//	private DefaultHandler defaultHandler;

	//@Autowired
	//private ProcessHandler processHandler;

	@Value("${netty.tcp-port}")
	private int tcpPort;

	@Value("${netty.http-port}")
	private int httpPort;

	@Value("${netty.boss-count}")
	private int bossCount;

	@Value("${netty.worker-count}")
	private int workerCount;


	public void start() throws InterruptedException {
		log.info("Tcp Server 실행");

		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();

		try {

			// 1. EventServer
			ServerBootstrap bootstrap = new ServerBootstrap();
			bootstrap.group(bossGroup, workerGroup)
					.channel(NioServerSocketChannel.class)
					.handler(new LoggingHandler(LogLevel.INFO))
					.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
					.childHandler(new ChannelInitializer<SocketChannel>() {

						@Override
						public void initChannel(SocketChannel ch) {
							ChannelPipeline p = ch.pipeline();
							p.addLast(new ChoiceProtocolHandler());
						}
					});
			bootstrap.bind(tcpPort).sync().channel();
		} catch(Exception e) {
			log.error(e.getMessage());

			workerGroup.shutdownGracefully().sync();
			bossGroup.shutdownGracefully().sync();

			Thread.sleep(5000);
			this.start();
		}
	}
}