package com.ulalalab.snipe;

import com.ulalalab.snipe.infra.util.LocalDateUtils;
import com.ulalalab.snipe.infra.util.RandomUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.util.NumberUtils;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Profile("wimx-client")
@SpringBootApplication
public class WimxPacketApplication {

	public static void main(String[] args) throws Exception {

		String TCP_IP = System.getProperty("tcp.ip");
		String TCP_PORT = System.getProperty("tcp.port");
		ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
		scheduler.initialize();

		AtomicInteger i = new AtomicInteger();
		scheduler.scheduleAtFixedRate(()-> {
			try {
				EventLoopGroup eventLoopGroup = new NioEventLoopGroup(1);
				Bootstrap bootstrap = new Bootstrap();

				i.getAndIncrement();
				int finalI = i.get();
				bootstrap.group(eventLoopGroup)
						.channel(NioSocketChannel.class)
						.handler(new LoggingHandler(LogLevel.DEBUG))
						.handler(new ChannelInitializer<SocketChannel>() {
							@Override
							protected void initChannel(SocketChannel ch) throws Exception {
								ch.pipeline().addLast(new WimxPacketHandler(finalI %2));
							}
						});
				bootstrap.remoteAddress(TCP_IP, NumberUtils.parseNumber(TCP_PORT, Integer.class));
				bootstrap.connect().sync().channel();
			} catch(Exception e) {
				log.info(e.getMessage());
			}
		}, 30000);
	}
}

@Slf4j
class WimxPacketHandler extends ChannelInboundHandlerAdapter {
	int index = 0;

	WimxPacketHandler(int index) {
		this.index = index;
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
		scheduler.initialize();

		if(this.index==0) {
			ByteBuf buf = Unpooled.buffer();
			String time = LocalDateUtils.getLocalDateTimeString(LocalDateUtils.getNowUTCLocalDateTime(), "yyMMddHHmmss");
			StringBuffer sb = new StringBuffer();

			sb.append("UP-1729,");
			sb.append(time);
			sb.append(",v0.1,1");
			sb.append(","+ RandomUtils.getFloatRandom());
			sb.append(","+ RandomUtils.getFloatRandom());
			sb.append(","+ RandomUtils.getFloatRandom());
			sb.append(","+ RandomUtils.getFloatRandom());
			sb.append("#"+ RandomUtils.getFloatRandom());

			buf.writeBytes(sb.toString().getBytes(StandardCharsets.UTF_8));
			ctx.writeAndFlush(buf);
			log.info(sb + " -> 전송!!");
			buf.clear();
			ctx.close();
		} else if(this.index==1) {
			ByteBuf buf = Unpooled.buffer();
			String time = LocalDateUtils.getLocalDateTimeString(LocalDateUtils.getNowUTCLocalDateTime(), "yyMMddHHmmss");
			StringBuffer sb = new StringBuffer();

			sb.append("UD-1653,");
			sb.append(time);
			sb.append(",v0.1,1");
			sb.append(","+ RandomUtils.getFloatRandom());
			sb.append(","+ RandomUtils.getFloatRandom());
			sb.append(","+ RandomUtils.getFloatRandom());
			sb.append(","+ RandomUtils.getFloatRandom());
			sb.append(","+ RandomUtils.getFloatRandom());

			buf.writeBytes(sb.toString().getBytes(StandardCharsets.UTF_8));
			ctx.writeAndFlush(buf);
			log.info(sb + " -> 전송!!");
			buf.clear();
			ctx.close();
		}
	}
}