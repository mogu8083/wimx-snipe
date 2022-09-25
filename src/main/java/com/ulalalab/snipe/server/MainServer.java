package com.ulalalab.snipe.server;

<<<<<<< HEAD
import com.ulalalab.snipe.infra.handler.*;
=======
import com.ulalalab.snipe.infra.handler.ChoiceProtocolHandler;
>>>>>>> e9804688e6756cd7b5dabf9696af8a119a5a5914
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
<<<<<<< HEAD
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
=======
import lombok.extern.slf4j.Slf4j;
>>>>>>> e9804688e6756cd7b5dabf9696af8a119a5a5914
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.NumberUtils;
import org.springframework.util.StringUtils;

@Component
<<<<<<< HEAD
@Slf4j(topic = "TCP.MainServer")
public class MainServer {

	private static final ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

=======
@Slf4j
public class MainServer {

>>>>>>> e9804688e6756cd7b5dabf9696af8a119a5a5914
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
<<<<<<< HEAD
		log.info("Main Server 실행");
=======
		log.info("Tcp Server 실행");
>>>>>>> e9804688e6756cd7b5dabf9696af8a119a5a5914

		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();

		try {
			ServerBootstrap bootstrap = new ServerBootstrap();
			bootstrap.group(bossGroup, workerGroup)
					.channel(NioServerSocketChannel.class)
					.handler(new LoggingHandler(LogLevel.INFO))
					.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
					.childOption(ChannelOption.SO_LINGER, 0)
					.childOption(ChannelOption.TCP_NODELAY, true)
					.childOption(ChannelOption.SO_KEEPALIVE, true)
					.childHandler(new ChannelInitializer<SocketChannel>() {

						@Override
						public void initChannel(SocketChannel ch) {
							ChannelPipeline p = ch.pipeline();
<<<<<<< HEAD

							// 프로토콜 선택 핸들러
=======
>>>>>>> e9804688e6756cd7b5dabf9696af8a119a5a5914
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