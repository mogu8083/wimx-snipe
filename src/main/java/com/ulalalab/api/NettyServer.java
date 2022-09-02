package com.ulalalab.api;

import com.ulalalab.api.common.handler.DataHandler;
import com.ulalalab.api.common.handler.DefaultHandler;
import com.ulalalab.api.common.repository.DeviceRepository;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;

@Component
public class NettyServer {

	@Autowired
	private DeviceRepository deviceRepository;

	@PostConstruct
	public void init() throws InterruptedException {
		deviceRepository.findByDeviceId("123123");

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
							p.addLast(new DefaultHandler());
							p.addLast(new DataHandler());
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