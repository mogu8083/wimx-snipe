package com.ulalalab.api;

import com.ulalalab.api.common.handler.EchoServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;
import java.net.InetSocketAddress;

@Component
public class NettyServer {

	@PostConstruct
	public void init() {
		System.out.println("##@@ " + " Init!!");

		EventLoopGroup bossGroup = new NioEventLoopGroup(1);
		EventLoopGroup workerGroup = new NioEventLoopGroup();

		try {
			ServerBootstrap bootstrap = new ServerBootstrap();
			bootstrap.group(bossGroup, workerGroup) // 이벤트 루프 등록
					.channel(NioServerSocketChannel.class)
					.localAddress(38080)
					.handler(new LoggingHandler(LogLevel.INFO))
					.childHandler( // child 에 대한 핸들러 추가
						new ChannelInitializer<SocketChannel>() {
							@Override
							public void initChannel(SocketChannel ch) throws Exception {
								ChannelPipeline p = ch.pipeline();
								p.addLast(new EchoServerHandler());
							}
						});
			ChannelFuture f = bootstrap.bind();

		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}