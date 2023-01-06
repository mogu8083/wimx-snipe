package com.ulalalab.snipe.server;

import com.ulalalab.snipe.infra.handler.ClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
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
	private String THREAD_START = System.getProperty("thread.start");
	private String THREAD_COUNT = System.getProperty("thread.count");

	private int threadCnt;
	private int threadStart;

	private EventLoopGroup eventLoopGroup;

	public void run() throws Exception {
		try {
			this.threadCnt = NumberUtils.parseNumber(THREAD_COUNT, Integer.class);
			this.threadStart = NumberUtils.parseNumber(THREAD_START, Integer.class);

			log.info("ClientServer 실행 / Thread : " + THREAD_COUNT + " 실행");
			//this.eventLoopGroup = new NioEventLoopGroup(50);

			PooledByteBufAllocator.DEFAULT.heapBuffer(41209202);

			for(int i = this.threadStart; i < this.threadCnt + this.threadStart; i++) {

				//this.eventLoopGroup = new EpollEventLoopGroup(1);
				this.eventLoopGroup = new NioEventLoopGroup(1);

				try {
					Bootstrap bootstrap = new Bootstrap();

					bootstrap.group(eventLoopGroup)
							//.channel(NioSocketChannel.class)
							.channel(NioSocketChannel.class)
							.handler(new LoggingHandler(LogLevel.DEBUG))
							.handler(new ClientHandler(i, this));
					bootstrap.remoteAddress(TCP_IP, NumberUtils.parseNumber(TCP_PORT, Integer.class));

					if(bootstrap.connect().isSuccess()) {
						eventLoopGroup.register(bootstrap.connect().channel());
					}
				} catch(Exception e) {
					log.error(this.getClass() + "{} 연결 실패 => {}" + e.getMessage());
				}
			}
		} catch(Exception e) {
			log.info(this.getClass() + "{} 연결 실패 => {}" + e.getMessage());

			Thread.sleep(5000);
			this.run();
		}
	}
}