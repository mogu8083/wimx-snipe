package com.ulalalab.snipe.infra.handler;

import com.ulalalab.snipe.device.model.Device;
import com.ulalalab.snipe.infra.manage.ChannelManager;
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
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.script.Invocable;
import javax.script.ScriptException;
import java.util.Set;

@Slf4j(topic = "TCP.ProcessHandler")
public class ProcessHandler extends ChannelInboundHandlerAdapter {

	private static Long receive = 1L;
	private static Set<Channel> channelGroup = ChannelManager.getInstance();

//	@Autowired
	private RedisTemplate<String, Object> redisTemplate;
//
//	@Autowired
	private JdbcTemplate jdbcTemplate;

	private Invocable invocable;

	public ProcessHandler() {
		this.redisTemplate = (RedisTemplate<String, Object>) BeansUtils.getBean("redisTemplate");
		this.jdbcTemplate = (JdbcTemplate) BeansUtils.getBean("jdbcTemplate");
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object packet) {
		try {
			boolean suffix = false;
			Device device = (Device) packet;

			if (StringUtils.hasText(device.getSource())) {
				this.invocable = ScriptUtils.getInvocable(device.getSource());
			}

			Double cvCh1 = device.getCh1();
			Double cvCh2 = device.getCh2();
			Double cvCh3 = device.getCh3();
			Double cvCh4 = device.getCh4();
			Double cvCh5 = device.getCh5();

			if (invocable != null) {
				cvCh1 = (Double) invocable.invokeFunction("add", cvCh1);
				cvCh2 = (Double) invocable.invokeFunction("add", cvCh2);
				cvCh3 = (Double) invocable.invokeFunction("add", cvCh3);
				cvCh4 = (Double) invocable.invokeFunction("add", cvCh4);
				cvCh5 = (Double) invocable.invokeFunction("add", cvCh5);

				device.setCh1(cvCh1);
				device.setCh2(cvCh2);
				device.setCh3(cvCh3);
				device.setCh4(cvCh4);
				device.setCh5(cvCh5);

				if(cvCh1.isNaN()) {
					throw new ScriptException("isNaN");
				}
			}
			log.info("받은 데이터 [" + (receive++) + ", 클라이언트 Count : " + channelGroup.size() + "] => " + device.toString());

			// 1. TimscaleDB Update
			jdbcTemplate.update("insert into ulalalab_c(time, device_id, ch1, ch2, ch3, ch4, ch5) values(now(), ?, ?, ?, ?, ?, ?)"
					, device.getDeviceId()
					, cvCh1
					, cvCh2
					, cvCh3
					, cvCh4
					, cvCh5
			);

			// 2. Redis
			//
		} catch(ScriptException e) {
			log.error(e.getMessage());
			e.printStackTrace();

			invocable = null;
			ctx.pipeline().remove("caculateHandler");
			log.error(this.getClass() + " -> caculateHandler 제거!");
		} catch(Exception e) {
			log.error(e.getMessage());
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
		log.error(this.getClass() + " / " +  cause.getCause());
		cause.printStackTrace();
		ctx.close();
	}
}