package com.ulalalab.snipe.server;

import com.ulalalab.snipe.infra.codec.PacketDecoder;
import com.ulalalab.snipe.infra.handler.CaculateHandler;
import com.ulalalab.snipe.infra.handler.ChoiceProtocolHandler;
import com.ulalalab.snipe.infra.handler.DefaultHandler;
import com.ulalalab.snipe.infra.handler.ProcessHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MainServer {

	private static final Logger logger = LoggerFactory.getLogger(MainServer.class);
	private static final ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

//	@Autowired
//	private DefaultHandler defaultHandler;

	//@Autowired
	//private ProcessHandler processHandler;

	@Value("${netty.tcp-port}")
	private int tcpPort;

	@Value("${netty.http-port}")
	private int httpPort;

	@Value("${netty.boss-count}")
	private int bossCount;

	@Value("${netty.worker-count}")
	private int workerCount;


	public void start() throws InterruptedException {
		logger.info("Tcp Server 실행");

		EventLoopGroup bossGroup = new NioEventLoopGroup(bossCount);
		EventLoopGroup workerGroup = new NioEventLoopGroup();

		try {

			// 1. EventServer
			ServerBootstrap bootstrap = new ServerBootstrap();
			bootstrap.group(bossGroup, workerGroup)
					.channel(NioServerSocketChannel.class)
					.handler(new LoggingHandler(LogLevel.INFO))
					.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
					.childHandler(new ChannelInitializer<SocketChannel>() {

						@Override
						public void initChannel(SocketChannel ch) {
							ChannelPipeline p = ch.pipeline();

							// 프로토콜 선택
							p.addLast(new ChoiceProtocolHandler());

							// 기본 ( 연결 관련 )
							p.addLast("TCP.defaultHandler", new DefaultHandler());

							// Packet 디코더 - 패킷 처리
							p.addLast("TCP.packetDecoder", new PacketDecoder());

							// 계산식 핸들러
							p.addLast("TCP.caculateHandler", new CaculateHandler());

							// 데이터 가공 처리
							p.addLast("TCP.processHandler", new ProcessHandler());
						}
					});
			bootstrap.bind(tcpPort).sync().channel();
		} catch(Exception e) {
			logger.error(e.getMessage());

			workerGroup.shutdownGracefully().sync();
			bossGroup.shutdownGracefully().sync();

			Thread.sleep(5000);
			this.start();
		}
	}
}