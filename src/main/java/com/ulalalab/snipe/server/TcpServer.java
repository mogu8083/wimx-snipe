package com.ulalalab.snipe.server;

import com.ulalalab.snipe.infra.handler.InitTcpHandler;
import com.ulalalab.snipe.infra.manage.RedisManager;
import io.lettuce.core.FlushMode;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.async.RedisAclAsyncCommands;
import io.lettuce.core.api.async.RedisAsyncCommands;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.Executor;

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
	private ThreadPoolTaskScheduler threadPoolTaskScheduler;
	private Executor threadPoolTaskExecutor;
	private RedisManager redisManager;

	public TcpServer(InitTcpHandler initTcpHandler, ThreadPoolTaskScheduler threadPoolTaskScheduler, ThreadPoolTaskExecutor threadPoolTaskExecutor, RedisManager redisManager) {
		this.initTcpHandler = initTcpHandler;
		this.threadPoolTaskScheduler = threadPoolTaskScheduler;
		this.threadPoolTaskExecutor = threadPoolTaskExecutor;
		this.redisManager = redisManager;
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
			workerGroup = new EpollEventLoopGroup(WORKER_COUNT, threadPoolTaskExecutor);

			serverSocketChannel = EpollServerSocketChannel.class;
		} else {
			bossGroup = new NioEventLoopGroup(BOSS_COUNT);
			workerGroup = new NioEventLoopGroup(WORKER_COUNT, threadPoolTaskExecutor);

			serverSocketChannel = NioServerSocketChannel.class;
		}

//		threadPoolTaskScheduler.scheduleAtFixedRate(() -> {
//			Map<StatefulRedisConnection<String, String>, Integer> map = redisManager.getRedisConnectionMap();
//			log.info("Redis Flush!");
//
//			map.entrySet().forEach(entry -> {
//				entry.getKey().async().save();
//			});
//		}, 1000);

//		threadPoolTaskScheduler.scheduleAtFixedRate(() -> {
//			Map<RedisAsyncCommands<String, String>, Integer> map = redisManager.getRedisCommandsMap();
//			log.info("Redis Flush!");
//
//			map.entrySet().forEach(entry -> {
//				entry.getKey().flushCommands();
//			});
//		}, 1000);

	}


}