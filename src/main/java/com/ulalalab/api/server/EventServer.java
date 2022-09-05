package com.ulalalab.api.server;

import com.ulalalab.api.common.handler.DataHandler;
import com.ulalalab.api.common.handler.DefaultHandler;
import com.ulalalab.api.instance.GlobalInstance;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EventServer {

	private static final Logger logger = LoggerFactory.getLogger(EventServer.class);

	@Autowired
	private DefaultHandler defaultHandler;

	@Autowired
	private DataHandler dataHandler;

	public void start() throws InterruptedException {
		logger.info("Event Server 실행");

		EventLoopGroup bossGroup = new NioEventLoopGroup(1);
		EventLoopGroup workerGroup = new NioEventLoopGroup();

		try {
			GlobalInstance.eventServerFlag = true;
			ServerBootstrap bootstrap = new ServerBootstrap();
			bootstrap.group(bossGroup, workerGroup)
					.channel(NioServerSocketChannel.class)
					.handler(new LoggingHandler(LogLevel.INFO))
					.childHandler(new ChannelInitializer<SocketChannel>() {

						@Override
						public void initChannel(SocketChannel ch) {
							ChannelPipeline p = ch.pipeline();
							p.addLast(defaultHandler);
							p.addLast(dataHandler);
						}
					});
			bootstrap.bind(38080);
			bootstrap.bind(38081);
			bootstrap.bind(38082);
			bootstrap.bind(38083);
			bootstrap.bind(38084);
			bootstrap.bind(38085);
			bootstrap.bind(38086);
			bootstrap.bind(38087);
			bootstrap.bind(38088);
			bootstrap.bind(38089).sync().channel().closeFuture().sync();
		} catch(Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} finally {
			workerGroup.shutdownGracefully().sync();
			bossGroup.shutdownGracefully().sync();
			GlobalInstance.eventServerFlag = false;
		}
	}
}