package com.ulalalab.api;

import com.ulalalab.api.common.handler.ClientHandler;
import com.ulalalab.api.common.service.InitService;
import com.ulalalab.api.common.util.ByteUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Component
@Profile("client")
public class ClientServer {

	private static final Logger logger = LoggerFactory.getLogger(InitService.class);

	@Value("${netty.tcp-port}")
	private int tcpPort;

	@PostConstruct
	public void start() throws InterruptedException {
		try {
			logger.info("ClientServer 실행");

			EventLoopGroup group = new NioEventLoopGroup();
			ChannelFuture channelFuture;
			Channel channel;
			ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

			for(int x=1; x<1000; x++) {
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