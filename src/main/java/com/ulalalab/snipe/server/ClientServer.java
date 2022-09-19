package com.ulalalab.snipe.server;

import com.ulalalab.snipe.infra.handler.ClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;

@Component
@Profile("client")
public class ClientServer {

	private static final Logger logger = LoggerFactory.getLogger(ClientServer.class);

	@Value("${netty.tcp-port}")
	private int tcpPort;

	@PostConstruct
	public void start() throws InterruptedException {
		try {
			logger.info("ClientServer 실행");

			EventLoopGroup group = new NioEventLoopGroup(50);
			ChannelFuture channelFuture;
			Channel channel;
			ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

			for(int x=1; x<50; x++) {
				Bootstrap bootstrap = new Bootstrap();
				bootstrap.group(group)
						.channel(NioSocketChannel.class)
						.handler(new ClientHandler(x));

				channel = bootstrap.connect("127.0.0.1", tcpPort).sync().channel();
				//channelFuture.channel().close().sync();
				//channelFuture.channel();
				channelGroup.add(channel);
			}
		} catch(Exception e) {
			logger.error(this.getClass() + " 연결 실패 => " + e.getMessage());

			Thread.sleep(5000);
			this.start();
		}
	}
}