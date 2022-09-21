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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.script.Invocable;
import javax.script.ScriptException;

@Component
//@ChannelHandler.Sharable
public class ProcessHandler extends ChannelInboundHandlerAdapter {

	private static final Logger logger = LoggerFactory.getLogger(ProcessHandler.class);
	private static Long receive = 0L;
	private static ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

	@Autowired
	private RedisTemplate<String, Object> redisTemplate;

	@Autowired
	private JdbcTemplate jdbcTemplate;
	private Invocable invocable;

//	public ProcessHandler() {
//		this.redisTemplate = (RedisTemplate<String, Object>) BeansUtils.getBean("redisTemplate");
//		this.jdbcTemplate = (JdbcTemplate) BeansUtils.getBean("jdbcTemplate");
//	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object packet) {
		try {
			System.out.println("##@@ 222 " + packet);

			boolean suffix = false;
			Device device = (Device) packet;

			if (StringUtils.hasText(device.getSource())) {
				this.invocable = ScriptUtils.getInvocable(device.getSource());
			}

			Double cvCh1 = device.getCh1();

			if (invocable != null) {
				cvCh1 = (Double) invocable.invokeFunction("add", cvCh1);
				device.setCh1(cvCh1);

				if(cvCh1.isNaN()) {
					throw new ScriptException("isNaN");
				}
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
		} catch(ScriptException e) {
			logger.error(e.getMessage());
			e.printStackTrace();

			invocable = null;
			ctx.pipeline().remove("caculateHandler");
			logger.error(this.getClass() + " -> caculateHandler 제거!");
		} catch(Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} finally {
			// 참조 해체
			ReferenceCountUtil.release(packet);
		}
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