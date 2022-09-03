package com.ulalalab.api;

import com.ulalalab.api.common.handler.DataHandler;
import com.ulalalab.api.common.handler.DefaultHandler;
import com.ulalalab.api.common.repository.DeviceRepository;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;

@Component
public class NettyServer {

	@Autowired
	private DefaultHandler defaultHandler;

	@Autowired
	private DataHandler dataHandler;

	@PostConstruct
	public void init() throws InterruptedException {
		System.out.println("##@@ " + "init");

		EventLoopGroup bossGroup = new NioEventLoopGroup(1);
		EventLoopGroup workerGroup = new NioEventLoopGroup();

		try {
			ServerBootstrap bootstrap = new ServerBootstrap();
			bootstrap.group(bossGroup, workerGroup)
					.channel(NioServerSocketChannel.class)
					.handler(new LoggingHandler(LogLevel.DEBUG))
					.childHandler(new ChannelInitializer<SocketChannel>() {
						@Override
						public void initChannel(SocketChannel ch) {
							ChannelPipeline p = ch.pipeline();
							p.addLast(defaultHandler);
							p.addLast(dataHandler);
						}
					});
			bootstrap.bind(38080).sync().channel().closeFuture().sync();

		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			workerGroup.shutdownGracefully().sync();
			bossGroup.shutdownGracefully().sync();
		}
	}
}