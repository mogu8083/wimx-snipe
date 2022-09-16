package com.ulalalab.snipe.common.handler;

import com.ulalalab.snipe.device.model.Device;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
//@Scope(value = "prototype")
@ChannelHandler.Sharable
public class ProcessHandler extends ChannelInboundHandlerAdapter {

	private static final Logger logger = LoggerFactory.getLogger(ProcessHandler.class);
	private static Long receive = 0L;
	private static ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
	//private int DATA_LENGTH = 1024;
	//private ByteBuf buff;

	@Autowired
	private RedisTemplate<String, Object> redisTemplate;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		Channel channel = ctx.channel();
		channelGroup.add(channel);
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object packet) {
		try {
			Device device = (Device) packet;
			logger.info("받은 데이터 [" + (receive++) + ", 클라이언트 Count : " + channelGroup.size() + "] => " + device.toString());

			// 1. TimscaleDB Update
			jdbcTemplate.update("insert into ulalalab_a(time, device_id, ch1, ch2, ch3, ch4, ch5) values(?, ?, ?, ?, ?, ?, ?)"
					, device.getTime()
					, device.getDeviceId()
					, device.getCh1()
					, device.getCh2()
					, device.getCh3()
					, device.getCh4()
					, device.getCh5()
			);
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			ReferenceCountUtil.safeRelease(packet);
		}
	}

	// 수신 데이터 처리 완료
	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) {
		//ctx.writeAndFlush(Unpooled.EMPTY_BUFFER) // 대기중인 메시지를 플러시하고 채널을 닫음
			//.addListener(ChannelFutureListener.CLOSE);
		//logger.info(this.getClass() + " => channelReadComplete");\
		//logger.info(this.getClass() + " / channelReadComplete!!");
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		logger.error(this.getClass() + " / " +  cause.getCause());
		cause.printStackTrace();
		ctx.close();
	}
}