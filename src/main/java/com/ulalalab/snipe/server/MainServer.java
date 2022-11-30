package com.ulalalab.snipe.server;

import com.ulalalab.snipe.infra.channel.SpChannelGroup;
import com.ulalalab.snipe.infra.handler.InitTcpHandler;
import com.ulalalab.snipe.infra.manage.EventManager;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.DefaultEventExecutor;
import io.netty.util.concurrent.EventExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;
import org.springframework.util.NumberUtils;

import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

@Component
@Slf4j(topic = "TCP.MainServer")
@Profile({"local-server", "dev-server"})
public class MainServer
		<SC extends SocketChannel, SSC extends ServerSocketChannel> {

	@Value("${server.tcp.port}")
	private String TCP_PORT;

	public static StatefulRedisConnection<String, String> redisConnection;

	@Autowired
	private ThreadPoolTaskScheduler taskScheduler;

	@Autowired
	private RedisTemplate<String, Object> redisTemplate;

	private Class<SSC> serverSocketChannelClass;

	private final boolean isLinux = System.getProperty("os.name").contains("Linux");
	//private ThreadPoolTaskExecutor taskExecutor = EventManager.getInstance().getTaskExecutor();

	public MainServer(Class<SSC> classSSC) {
		RedisClient redisClient = RedisClient.create(
				RedisURI.builder()
						.withHost("10.10.0.90")
						.withPort(6379)
						.withPassword("ulalalab12!@")
						.build());

		redisConnection = redisClient.connect();

		serverSocketChannelClass = classSSC;
	}

	public void start() throws InterruptedException {
		log.info("Main Server 실행");

		//					//connection.set(keySerializer.serialize(deviceId), keySerializer.serialize(redisObject.toString()));
		//					connection.set(keySerializer.serialize(deviceId), valueSerializer.serialize(redisObject.toString()));
		//
		//					if (DevUtils.isPrint(deviceId)) {
		//						log.info("Redis Execute -> " + deviceId);
		//					}
		//					return null;
		//				});
		//taskScheduler.scheduleAtFixedRate(this::redisSaveAll, 1000);

		EventLoopGroup bossGroup;
		EventLoopGroup workerGroup;
		//Executor executor = new SimpleAsyncTaskExecutor("TaskExecutor");

		if(isLinux) {
			bossGroup = new EpollEventLoopGroup(1);
			workerGroup = new EpollEventLoopGroup(8);
		} else {
			bossGroup = new NioEventLoopGroup(1);
			workerGroup = new NioEventLoopGroup(8);
		}

		try {
			ServerBootstrap bootstrap = new ServerBootstrap();
			bootstrap.group(bossGroup, workerGroup)
					.channel(serverSocketChannelClass)
					.handler(new LoggingHandler(LogLevel.DEBUG))
					//.option(ChannelOption.SO_RCVBUF, 1048576)
					.option(ChannelOption.SO_REUSEADDR, true)
					.option(ChannelOption.SO_BACKLOG, 50000)
					//.childOption(ChannelOption.SO_RCVBUF, 256 * 1024)
					//.option(ChannelOption.ALLOCATOR, new PooledByteBufAllocator())
					//.option(ChannelOption.ALLOCATOR, new FixedRecvByteBufAllocator())
					//.option(ChannelOption.TCP_NODELAY, true)
					//.option(ChannelOption.ALLOCATOR, new UnpooledUnsafeHeapByteBuf(256, 1024))
					//.option(ChannelOption.TCP_FASTOPEN, 0)
					//.option(ChannelOption.SO_RCVBUF, 512)
					//.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
					//.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
					//.childOption(ChannelOption.TCP_NODELAY, true)
					//.childOption(ChannelOption.RCVBUF_ALLOCATOR, new FixedRecvByteBufAllocator(1024))
					//.childOption(ChannelOption.TCP_NODELAY, true)
					//.childOption(ChannelOption.TCP_FASTOPEN, 0)
					//.childOption(ChannelOption.SO_LINGER, 0)
					//ChannelOption.SO_RCVBUF, 256 * 1024);
					.childOption(ChannelOption.SO_LINGER, 0)
					.childHandler(new ChannelInitializer<SC>() {

						@Override
						public void initChannel(SC ch) {
							ChannelPipeline p = ch.pipeline();
							p.addLast(new InitTcpHandler());
						}
					});
			bootstrap.bind(NumberUtils.parseNumber(TCP_PORT, Integer.class)).sync().channel();
		} catch(Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();

			workerGroup.shutdownGracefully().sync();
			bossGroup.shutdownGracefully().sync();

			Thread.sleep(5000);
			this.start();
		}
	}
}