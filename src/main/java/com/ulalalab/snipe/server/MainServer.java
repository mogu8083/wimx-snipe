package com.ulalalab.snipe.server;

import com.ulalalab.snipe.infra.handler.ChoiceProtocolHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.NumberUtils;

@Component
@Slf4j(topic = "TCP.MainServer")
public class MainServer {

//	@Autowired
//	private DefaultHandler defaultHandler;

	//@Autowired
	//private ProcessHandler processHandler;

	@Value("${netty.boss-count}")
	private int bossCount;

	@Value("${netty.worker-count}")
	private int workerCount;

	@Value("#{systemProperties['tcp.port']}")
	private String TCP_PORT;


	public void start() throws InterruptedException {
		log.info("Main Server 실행");

		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();

		try {
			ServerBootstrap bootstrap = new ServerBootstrap();
			bootstrap.group(bossGroup, workerGroup)
					.channel(NioServerSocketChannel.class)
					//.handler(new LoggingHandler(LogLevel.DEBUG))
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
					.childOption(ChannelOption.SO_LINGER, 0)
					//.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
					//.childOption(NioChannelOption.RCVBUF_ALLOCATOR, new FixedRecvByteBufAllocator(256 * 1024))
					//.childOption(ChannelOption.TCP_NODELAY, true)
					//.childOption(ChannelOption.TCP_FASTOPEN, 0)
					//.childOption(ChannelOption.SO_LINGER, 0)
					//ChannelOption.SO_RCVBUF, 256 * 1024);
					//ChannelOption.SO_BACKLOG, 1024);
					.childHandler(new ChannelInitializer<NioSocketChannel>() {

						@Override
						public void initChannel(NioSocketChannel ch) {
							ChannelPipeline p = ch.pipeline();

							// 프로토콜 선택 핸들러
							p.addLast(new ChoiceProtocolHandler());
						}
					});
			bootstrap.bind(NumberUtils.parseNumber(TCP_PORT, Integer.class)).sync().channel();
		} catch(Exception e) {
			log.error(e.getMessage());

			workerGroup.shutdownGracefully().sync();
			bossGroup.shutdownGracefully().sync();

			Thread.sleep(5000);
			this.start();
		}
	}
}