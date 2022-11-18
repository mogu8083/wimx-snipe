package com.ulalalab.snipe.server;

import com.ulalalab.snipe.infra.handler.ClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.NumberUtils;

@Slf4j
public class ClientServer {

	private String TCP_IP = System.getProperty("tcp.ip");
	private String TCP_PORT = System.getProperty("tcp.port");
	private String THREAD_COUNT = System.getProperty("thread.count");
	private int threadCnt;
	private EventLoopGroup eventLoopGroup;

	public static void main(String[] args) throws Exception {
		ClientServer clientServer = new ClientServer();
		clientServer.run();
	}

	public void run() throws Exception {
		try {
			this.threadCnt = NumberUtils.parseNumber(THREAD_COUNT, Integer.class);
			log.info("ClientServer 실행 / Thread : " + THREAD_COUNT + " 실행");

			for(int i = 1; i < this.threadCnt + 1; i++) {
				eventLoopGroup = new NioEventLoopGroup(1);
				Bootstrap bootstrap = new Bootstrap();

				bootstrap.group(eventLoopGroup)
						.channel(NioSocketChannel.class)
						.handler(new LoggingHandler(LogLevel.DEBUG))
						.handler(new ClientHandler(i, this));
				bootstrap.remoteAddress(TCP_IP, NumberUtils.parseNumber(TCP_PORT, Integer.class));
				Channel channel = bootstrap.connect().sync().channel();

				eventLoopGroup.register(channel);
			}
		} catch(Exception e) {
			log.info(this.getClass() + "{} 연결 실패 => {}" + e.getMessage());
		}
	}
}