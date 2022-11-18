package com.ulalalab.snipe.server;

import com.ulalalab.snipe.infra.handler.ChoiceProtocolHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.util.NumberUtils;

@Component
@Slf4j(topic = "TCP.MainServer")
@Profile({"local-server", "dev-server"})
public class MainServer
		<SC extends SocketChannel, SSC extends ServerSocketChannel> {

	@Value("#{systemProperties['tcp.port']}")
	private String TCP_PORT;

	private Class<SSC> serverSocketChannelClass;

	private final boolean isLinux = System.getProperty("os.name").contains("Linux");

	public MainServer(Class<SSC> classSSC) {
		serverSocketChannelClass = classSSC;
	}

	public void start() throws InterruptedException {
		log.info("Main Server 실행");

		EventLoopGroup bossGroup;
		EventLoopGroup workerGroup;

		if(isLinux) {
			bossGroup = new EpollEventLoopGroup(1);
			workerGroup = new EpollEventLoopGroup();
		} else {
			bossGroup = new NioEventLoopGroup(1);
			workerGroup = new NioEventLoopGroup();
		}

		try {
			ServerBootstrap bootstrap = new ServerBootstrap();
			Object cc;
			bootstrap.group(bossGroup, workerGroup)
					.channel(serverSocketChannelClass)
					.handler(new LoggingHandler(LogLevel.DEBUG))
					//.option(ChannelOption.SO_RCVBUF, 10485760)
					.option(ChannelOption.SO_REUSEADDR, true)
					.option(ChannelOption.SO_BACKLOG, 50000)
					//.option(NioChannelOption.RCVBUF_ALLOCATOR, new FixedRecvByteBufAllocator(256 * 1024))
					//.option(ChannelOption.TCP_NODELAY, true)
					//.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
					//.option(ChannelOption.TCP_FASTOPEN, 0)
					//.option(ChannelOption.SO_RCVBUF, 512)
					.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
					.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
					.childOption(ChannelOption.TCP_NODELAY, true)
					//.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
					//.childOption(ChannelOption.RCVBUF_ALLOCATOR, new FixedRecvByteBufAllocator(1024))
					//.childOption(ChannelOption.TCP_NODELAY, true)
					//.childOption(ChannelOption.TCP_FASTOPEN, 0)
					//.childOption(ChannelOption.SO_LINGER, 0)
					//ChannelOption.SO_RCVBUF, 256 * 1024);
					//ChannelOption.SO_BACKLOG, 1024);
					.childOption(ChannelOption.SO_LINGER, 0)
					.childHandler(new ChannelInitializer<SC>() {

						@Override
						public void initChannel(SC ch) {
							ChannelPipeline p = ch.pipeline();

							// 프로토콜 선택 핸들러
							p.addLast(new ChoiceProtocolHandler());
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