package com.ulalalab.snipe.server;

import com.ulalalab.snipe.infra.handler.InitTcpHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Slf4j(topic = "TCP.MainServer")
@Profile({"local-server", "dev-server"})
public class TcpServer {

	@Value("${server.tcp-port}")
	private int TCP_PORT;

	@Value("${server.boss-count}")
	private int BOSS_COUNT;

	@Value("${server.worker-count}")
	private int WORKER_COUNT;

	private EventLoopGroup bossGroup = null;
	private EventLoopGroup workerGroup = null;
	private Class serverSocketChannel = null;

	private InitTcpHandler initTcpHandler;

	public TcpServer(InitTcpHandler initTcpHandler) {
		this.initTcpHandler = initTcpHandler;
	}

	public void start() throws InterruptedException {
		this.setInit();

		log.info("Main Server 실행");

		try {
			ServerBootstrap bootstrap = new ServerBootstrap();
			bootstrap.group(bossGroup, workerGroup)
					.channel(serverSocketChannel)
					.handler(new LoggingHandler(LogLevel.DEBUG))
					.option(ChannelOption.SO_REUSEADDR, true)
					.option(ChannelOption.SO_BACKLOG, 50000)
					.childOption(ChannelOption.SO_LINGER, 0)
					.childHandler(new ChannelInitializer<SocketChannel>() {

						@Override
						public void initChannel(SocketChannel ch) {
							ChannelPipeline p = ch.pipeline();
							p.addLast(initTcpHandler);
						}
					});
			bootstrap.bind(TCP_PORT).sync().channel();
		} catch(Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();

			workerGroup.shutdownGracefully().sync();
			bossGroup.shutdownGracefully().sync();

			Thread.sleep(5000);
			this.start();
		}
	}

	private void setInit() {
		final boolean isLinux = System.getProperty("os.name").contains("Linux");

		if(isLinux) {
			bossGroup = new EpollEventLoopGroup(BOSS_COUNT);
			workerGroup = new EpollEventLoopGroup(WORKER_COUNT);

			serverSocketChannel = EpollServerSocketChannel.class;
		} else {
			bossGroup = new NioEventLoopGroup(BOSS_COUNT);
			workerGroup = new NioEventLoopGroup(WORKER_COUNT);

			serverSocketChannel = NioServerSocketChannel.class;
		}
	}
}