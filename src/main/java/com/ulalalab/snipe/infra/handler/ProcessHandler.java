package com.ulalalab.snipe.infra.handler;

import com.ulalalab.snipe.device.model.Device;
import com.ulalalab.snipe.infra.util.BeansUtils;
import com.ulalalab.snipe.infra.util.RandomUtils;
import com.ulalalab.snipe.infra.util.ScriptUtils;
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
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.StringUtils;

import javax.script.Invocable;

@ChannelHandler.Sharable
public class ProcessHandler extends ChannelInboundHandlerAdapter {

	private static final Logger logger = LoggerFactory.getLogger(ProcessHandler.class);
	private static Long receive = 0L;
	private static ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

	private RedisTemplate<String, Object> redisTemplate;
	private JdbcTemplate jdbcTemplate;
	private Invocable invocable;

//	public ProcessHandler(String javascriptSource) {
//		this.redisTemplate = (RedisTemplate<String, Object>) BeansUtils.getBean("redisTemplate");
//		this.jdbcTemplate = (JdbcTemplate) BeansUtils.getBean("jdbcTemplate");
//
//		if(javascriptSource!=null) {
//			this.invocable = ScriptUtils.getInvocable(javascriptSource);
//		}
//	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object packet) {
		try {
			boolean suffix = false;
			Device device = (Device) packet;

			if(StringUtils.hasText(device.getSource())) {
				this.invocable = ScriptUtils.getInvocable(device.getSource());
			}

			Double cvCh1 = device.getCh1();

			if(invocable!=null) {
				cvCh1 = (Double) invocable.invokeFunction("add", cvCh1);
				device.setCh1(cvCh1);
			}
			logger.info("받은 데이터 [" + (receive++) + ", 클라이언트 Count : " + channelGroup.size() + "] => " + device.toString());

			// 1. TimscaleDB Update
			jdbcTemplate.update("insert into ulalalab_c(time, device_id, ch1, ch2, ch3, ch4, ch5) values(now(), ?, ?, ?, ?, ?, ?)"
					, device.getDeviceId() + (suffix ? "0" : "")
					, cvCh1
					, device.getCh2()
					, device.getCh3()
					, device.getCh4()
					, device.getCh5()
			);
		} catch(Exception e) {
			logger.error(e.getMessage());
		} finally {
			// 참조 해체
			ReferenceCountUtil.release(packet);
		}
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		logger.info(ctx.channel().toString() + " 연결 / 연결 갯수 : " + channelGroup.size());

		Channel channel = ctx.channel();
		channelGroup.add(channel);
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		logger.info(ctx.channel().toString() + " 연결 해제 / 연결 갯수 : " + channelGroup.size());

		Channel channel = ctx.channel();
		channelGroup.remove(channel);
		ctx.close();
	}



	// 수신 데이터 처리 완료
	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) {
		//ctx.writeAndFlush(Unpooled.EMPTY_BUFFER) // 대기중인 메시지를 플러시하고 채널을 닫음
			//.addListener(ChannelFutureListener.CLOSE);
		//logger.info(this.getClass() + " => channelReadComplete");
		//logger.info(this.getClass() + " / channelReadComplete!!");
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		logger.error(this.getClass() + " / " +  cause.getCause());
		cause.printStackTrace();
		ctx.close();
	}
}