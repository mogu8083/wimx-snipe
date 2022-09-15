package com.ulalalab.snipe.server;

import com.ulalalab.snipe.common.codec.PacketDecoder;
import com.ulalalab.snipe.common.handler.DefaultHandler;
import com.ulalalab.snipe.common.handler.ProcessHandler;
import com.ulalalab.snipe.common.instance.GlobalInstance;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
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

import javax.annotation.PostConstruct;

//@Component
public class HttpServer {

	private static final Logger logger = LoggerFactory.getLogger(HttpServer.class);

	@Autowired
	private ProcessHandler processHandler;

	@Value("${netty.http-port}")
	private int httpPort;

	@Value("${netty.boss-count}")
	private int bossCount;

	@Value("${netty.worker-count}")
	private int workerCount;

	public void start() throws InterruptedException {
		logger.info("Http Server 실행");

		EventLoopGroup bossGroup = new NioEventLoopGroup(bossCount);
		EventLoopGroup workerGroup = new NioEventLoopGroup(workerCount);

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
							p.addLast(new HttpRequestDecoder());
							p.addLast(new HttpResponseEncoder());
						}
					});
			bootstrap.bind(httpPort).sync().channel().closeFuture().sync();
		} catch(Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} finally {
			workerGroup.shutdownGracefully().sync();
			bossGroup.shutdownGracefully().sync();

			Thread.sleep(5000);
			this.start();
		}
	}
}